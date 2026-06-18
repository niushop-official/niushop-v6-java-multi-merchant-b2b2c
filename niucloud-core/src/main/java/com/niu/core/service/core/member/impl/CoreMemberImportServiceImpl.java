package com.niu.core.service.core.member.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.niu.core.common.component.context.WebAppEnvs;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberImport;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.enums.member.AccountTypeEnum;
import com.niu.core.enums.member.MemberImportStatusEnum;
import com.niu.core.enums.member.MemberRegisterChannelEnum;
import com.niu.core.enums.member.MemberRegisterTypeEnum;
import com.niu.core.mapper.member.MemberImportMapper;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.param.MemberImportAddParam;
import com.niu.core.service.core.member.ICoreMemberAccountService;
import com.niu.core.service.core.member.ICoreMemberImportService;
import com.niu.core.service.core.member.ICoreMemberService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class CoreMemberImportServiceImpl implements ICoreMemberImportService {

    @Resource
    private MemberImportMapper memberImportMapper;

    @Resource
    private MemberMapper memberMapper;

    @Resource
    private MemberLevelMapper memberLevelMapper;

    @Resource
    private ICoreMemberAccountService coreMemberAccountService;
    @Resource
    ICoreMemberService coreMemberService;

    @Resource
    @Lazy
    private ICoreMemberImportService coreMemberImportService;

    private static final String[] IMPORT_HEADERS = {
            "手机号", "会员用户名", "会员昵称", "会员等级",
            "微信用户openid", "微信小程序openid", "微信unionid",
            "性别", "出生日期", "可用积分", "可用余额", "状态"
    };

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer add(MemberImportAddParam.MemberImportData addParam) {
        MemberImport memberImport = new MemberImport();
        memberImport.setSiteId((Integer) addParam.getMyMemberLabel().get("site_id"));
        memberImport.setMainId((Integer) addParam.getMyMemberLabel().get("main_id"));
        memberImport.setStatus(MemberImportStatusEnum.PROCESSING.getValue());
        memberImport.setTotalNum(0);
        memberImport.setSuccessNum(0);
        memberImport.setFailNum(0);
        memberImport.setRepeatNum(0);
        memberImport.setData(JSONUtil.toJsonStr(addParam));
        memberImport.setOutput("");
        memberImport.setFailOutput("");
        memberImport.setFailRemark("");
        memberImport.setDuration(0);
        memberImport.setCreateTime(System.currentTimeMillis() / 1000);
        memberImport.setUpdateTime(System.currentTimeMillis() / 1000);

        memberImportMapper.insert(memberImport);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                coreMemberImportService.execute(memberImport.getId());
            }
        });

        return memberImport.getId();
    }

    @Async
    @Override
    public void execute(Integer id) {
        log.info("开始执行会员导入任务: ID {}", id);
        long startTime = System.currentTimeMillis();

        MemberImport memberImport = memberImportMapper.selectById(id);
        if (ObjectUtil.isEmpty(memberImport)) {
            log.error("会员导入任务不存在: ID {}", id);
            return;
        }

        if (!memberImport.getStatus().equals(MemberImportStatusEnum.PROCESSING.getValue())) {
            log.warn("会员导入任务已完成，跳过: ID {}", id);
            return;
        }

        try {
            JSONObject config = JSONUtil.parseObj(memberImport.getData());
            String filePath = config.getStr("path");

            Map<String, Object> result = importMembers(memberImport, filePath, config, memberImport.getSiteId());

            memberImport.setTotalNum((Integer) result.get("totalNum"));
            memberImport.setSuccessNum((Integer) result.get("successNum"));
            memberImport.setFailNum((Integer) result.get("failNum"));
            memberImport.setRepeatNum((Integer) result.get("repeatNum"));
            memberImport.setOutput((String) result.get("output"));
            memberImport.setFailOutput((String) result.get("failOutput"));
            memberImport.setFailRemark((String) result.get("failRemark"));
            memberImport.setStatus(MemberImportStatusEnum.FINISH.getValue());

            long endTime = System.currentTimeMillis();
            memberImport.setDuration((int) ((endTime - startTime) / 1000));
            memberImport.setUpdateTime(System.currentTimeMillis() / 1000);

            memberImportMapper.updateById(memberImport);

        } catch (Exception e) {
            log.error("会员导入失败", e);

            long endTime = System.currentTimeMillis();
            memberImport.setStatus(MemberImportStatusEnum.FAIL.getValue());
            memberImport.setFailRemark(e.getMessage());
            memberImport.setDuration((int) ((endTime - startTime) / 1000));
            memberImport.setUpdateTime(System.currentTimeMillis() / 1000);

            memberImportMapper.updateById(memberImport);
        }
    }

    private Map<String, Object> importMembers(MemberImport memberImport, String filePath, JSONObject config, Integer siteId) {
        Map<String, Object> result = new HashMap<>();
        int totalNum = 0;
        int successNum = 0;
        int failNum = 0;
        int repeatNum = 0;

        List<String> successRecords = new ArrayList<>();
        List<String> failRecords = new ArrayList<>();
        List<String> failRemarks = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(WebAppEnvs.get().webRootDownResource + filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();

            for (int i = 2; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                totalNum++;
                String mobile = getCellValue(row.getCell(0));
                String username = getCellValue(row.getCell(1));
                String nickname = getCellValue(row.getCell(2));
                String levelName = getCellValue(row.getCell(3));
                String wxOpenid = getCellValue(row.getCell(4));
                String weappOpenid = getCellValue(row.getCell(5));
                String wxUnionid = getCellValue(row.getCell(6));
                String sex = getCellValue(row.getCell(7));
                String birthday = getCellValue(row.getCell(8));
                String point = getCellValue(row.getCell(9));
                String balance = getCellValue(row.getCell(10));
                String status = getCellValue(row.getCell(11));

                try {
                    validateMemberData(mobile, username, nickname, levelName, wxOpenid, weappOpenid, wxUnionid, sex, birthday, point, balance, status, siteId);

                    Member member = createOrUpdateMember(mobile, username, nickname, levelName, wxOpenid, weappOpenid, wxUnionid,
                            sex, birthday, point, balance, status, siteId, config, memberImport.getId());

                    successNum++;
                    successRecords.add(mobile);

                } catch (Exception e) {
                    failNum++;
                    failRecords.add(mobile);
                    failRemarks.add(e.getMessage());
                }
            }

            String outputPath = generateSuccessFile(successRecords, siteId);
            String failOutputPath = generateFailFile(failRecords, failRemarks, siteId);

            result.put("totalNum", totalNum);
            result.put("successNum", successNum);
            result.put("failNum", failNum);
            result.put("repeatNum", repeatNum);
            result.put("output", outputPath);
            result.put("failOutput", failOutputPath);
            result.put("failRemark", "");

        } catch (Exception e) {
            result.put("failRemark", e.getMessage());
            throw new CommonException("导入失败: " + e.getMessage());
        }

        return result;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private void validateMemberData(String mobile, String username, String nickname, String levelName, String wxOpenid,
                                    String weappOpenid, String wxUnionid, String sex, String birthday, String point,
                                    String balance, String status, Integer siteId) {
        if (StrUtil.isEmpty(mobile)) {
            throw new CommonException("手机号不能为空");
        }

        if (!mobile.matches("^1[3-9]\\d{9}$")) {
            throw new CommonException("手机号格式不正确");
        }

        if (StrUtil.isNotEmpty(username)) {
            Member existingMember = memberMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Member>()
                            .eq("site_id", siteId)
                            .eq("username", username)
                            .ne("mobile", mobile)
                            .last("limit 1")
            );
            if (existingMember != null) {
                throw new CommonException("会员用户名已存在");
            }
        }

        if (StrUtil.isNotEmpty(levelName)) {
            MemberLevel memberLevel = memberLevelMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MemberLevel>()
                            .eq("site_id", siteId)
                            .eq("level_name", levelName)
                            .last("limit 1")
            );
            if (memberLevel == null) {
                throw new CommonException("会员等级不存在");
            }
        }

        if (StrUtil.isNotEmpty(wxOpenid)) {
            Member existingMember = memberMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Member>()
                            .eq("site_id", siteId)
                            .eq("wx_openid", wxOpenid)
                            .ne("mobile", mobile)
                            .last("limit 1")
            );
            if (existingMember != null) {
                throw new CommonException("微信用户openid已存在");
            }
        }

        if (StrUtil.isNotEmpty(weappOpenid)) {
            Member existingMember = memberMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Member>()
                            .eq("site_id", siteId)
                            .eq("weapp_openid", weappOpenid)
                            .ne("mobile", mobile)
                            .last("limit 1")
            );
            if (existingMember != null) {
                throw new CommonException("微信小程序openid已存在");
            }
        }

        if (StrUtil.isNotEmpty(wxUnionid)) {
            Member existingMember = memberMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Member>()
                            .eq("site_id", siteId)
                            .eq("wx_unionid", wxUnionid)
                            .ne("mobile", mobile)
                            .last("limit 1")
            );
            if (existingMember != null) {
                throw new CommonException("微信unionid已存在");
            }
        }

        if (StrUtil.isNotEmpty(sex) && !Arrays.asList("男", "女", "未知").contains(sex)) {
            throw new CommonException("性别格式不正确");
        }

        if (StrUtil.isNotEmpty(birthday) && !birthday.matches("\\d{4}[-/]\\d{2}[-/]\\d{2}")) {
            throw new CommonException("出生日期格式不正确");
        }
    }

    private Member createOrUpdateMember(String mobile, String username, String nickname, String levelName, String wxOpenid,
                                        String weappOpenid, String wxUnionid, String sex, String birthday, String point,
                                        String balance, String status, Integer siteId, JSONObject config, Integer importId) {
        Member member = memberMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Member>()
                        .eq("site_id", siteId)
                        .eq("mobile", mobile)
                        .last("limit 1")
        );

        BigDecimal importPoint = getBigDecimal(point);
        BigDecimal importBalance = getBigDecimal(balance);

        if (member == null)
            member = addMember(mobile, username, nickname, levelName, wxOpenid, weappOpenid, wxUnionid, sex, birthday,
                    importPoint, importBalance, siteId, importId);
        else
            updateMember(member, username, nickname, wxOpenid, weappOpenid, wxUnionid, sex, birthday,
                    importPoint, importBalance, config.getJSONObject("mobileRepeatRule"), importId);

        return member;
    }

    private String generateSuccessFile(List<String> successRecords, Integer siteId) throws IOException {
        if (successRecords.isEmpty()) {
            return "";
        }

        String outputDir = WebAppEnvs.get().webRootDownResource + "memberImport/";
        if (!FileUtil.isDirectory(outputDir) && !FileUtil.mkdir(outputDir).isDirectory()) {
            throw new IOException("无法创建会员导入输出目录");
        }

        String fileName = "member_import_success_" + siteId + "_" + System.currentTimeMillis() + ".csv";
        String filePath = outputDir + fileName;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write("手机号\n");
            for (String mobile : successRecords) {
                writer.write(mobile + "\n");
            }
        }

        return filePath.replace(WebAppEnvs.get().webRootDownResource, "");
    }

    private String generateFailFile(List<String> failRecords, List<String> failRemarks, Integer siteId) throws IOException {
        if (failRecords.isEmpty()) {
            return "";
        }

        String outputDir = WebAppEnvs.get().webRootDownResource + "memberImport/";
        if (!FileUtil.isDirectory(outputDir) && !FileUtil.mkdir(outputDir).isDirectory()) {
            throw new IOException("无法创建会员导入输出目录");
        }

        String fileName = "member_import_fail_" + siteId + "_" + System.currentTimeMillis() + ".csv";
        String filePath = outputDir + fileName;

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            writer.write("手机号,失败原因\n");
            for (int i = 0; i < failRecords.size(); i++) {
                writer.write(failRecords.get(i) + "," + failRemarks.get(i) + "\n");
            }
        }

        return filePath.replace(WebAppEnvs.get().webRootDownResource, "");
    }

    // ========== 辅助方法 ==========

    private Member addMember(String mobile, String username, String nickname, String levelName, String wxOpenid,
                             String weappOpenid, String wxUnionid, String sex, String birthday,
                             BigDecimal point, BigDecimal balance, Integer siteId, Integer importId) {
        Member member = new Member();
        member.setSiteId(siteId);
        member.setMobile(mobile);
        member.setCreateTime(System.currentTimeMillis() / 1000);
        member.setMemberNo(coreMemberService.createMemberNo(siteId));
        member.setRegisterChannel(MemberRegisterChannelEnum.IMPORT.getType());
        member.setRegisterType(MemberRegisterTypeEnum.IMPORT.getType());


        if (StrUtil.isNotEmpty(username)) {
            member.setUsername(username);
        }

        if (StrUtil.isNotEmpty(nickname)) {
            member.setNickname(nickname);
        }

        if (StrUtil.isNotEmpty(levelName)) {
            MemberLevel memberLevel = memberLevelMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<MemberLevel>()
                            .eq("site_id", siteId)
                            .eq("level_name", levelName)
                            .last("limit 1")
            );
            if (memberLevel != null) {
                member.setMemberLevel(memberLevel.getLevelId());
            }
        }

        if (StrUtil.isNotEmpty(wxOpenid)) {
            member.setWxOpenid(wxOpenid);
        }

        if (StrUtil.isNotEmpty(weappOpenid)) {
            member.setWeappOpenid(weappOpenid);
        }

        if (StrUtil.isNotEmpty(wxUnionid)) {
            member.setWxUnionid(wxUnionid);
        }

        if (StrUtil.isNotEmpty(sex)) {
            if ("男".equals(sex)) {
                member.setSex(1);
            } else if ("女".equals(sex)) {
                member.setSex(2);
            } else {
                member.setSex(0);
            }
        }

        if (StrUtil.isNotEmpty(birthday)) {
            member.setBirthday(birthday);
        }

        member.setUpdateTime(System.currentTimeMillis() / 1000);

        member.setPassword(PasswordEncipher.encode("123456"));
        memberMapper.insert(member);

        if (point != null && point.compareTo(BigDecimal.ZERO) > 0) {
            coreMemberAccountService.addLog(siteId, member.getMemberId(), AccountTypeEnum.POINT.getType(),
                    point.doubleValue(), "member_import", "会员导入", String.valueOf(importId));
        }
        if (balance != null && balance.compareTo(BigDecimal.ZERO) > 0) {
            coreMemberAccountService.addLog(siteId, member.getMemberId(), AccountTypeEnum.BALANCE.getType(),
                    balance.doubleValue(), "member_import", "会员导入", String.valueOf(importId));
        }

        return member;
    }

    private void updateMember(Member existing, String username, String nickname, String wxOpenid, String weappOpenid, String wxUnionid,
                              String sex, String birthday, BigDecimal point, BigDecimal balance, JSONObject rule, Integer importId) {
        if (rule == null) {
            rule = new JSONObject();
        }

        // 校验关键字段是否冲突
        if (StringUtils.isNotBlank(username) && !username.equals(existing.getUsername())) {
            throw new CommonException("该手机号的用户名与导入用户名不一致");
        }
        if (StringUtils.isNotBlank(wxOpenid) && !wxOpenid.equals(existing.getWxOpenid())) {
            throw new CommonException("该手机号的微信用户openid与导入微信用户openid不一致");
        }
        if (StringUtils.isNotBlank(weappOpenid) && !weappOpenid.equals(existing.getWeappOpenid())) {
            throw new CommonException("该手机号的微信小程序openid与导入微信小程序openid不一致");
        }
        if (StringUtils.isNotBlank(wxUnionid) && !wxUnionid.equals(existing.getWxUnionid())) {
            throw new CommonException("该手机号的微信unionid与导入微信unionid不一致");
        }

        Member update = new Member();
        update.setMemberId(existing.getMemberId());

        // basic不为跳过时更新基础信息
        if (!"skip".equals(rule.getStr("basic"))) {
            if (StrUtil.isNotEmpty(nickname)) {
                update.setNickname(nickname);
            }
            if (StrUtil.isNotEmpty(sex)) {
                if ("男".equals(sex)) {
                    update.setSex(1);
                } else if ("女".equals(sex)) {
                    update.setSex(2);
                } else {
                    update.setSex(0);
                }
            }

            if (StrUtil.isNotEmpty(birthday)) {
                update.setBirthday(birthday);
            }

            update.setUpdateTime(System.currentTimeMillis() / 1000);
            memberMapper.updateById(update);
        }

        // 处理积分
        BigDecimal oldPoint = existing.getPoint() != null ? new BigDecimal(existing.getPoint()) : BigDecimal.ZERO;
        applyAccountRule(existing.getSiteId(), existing.getMemberId(), AccountTypeEnum.POINT.getType(),
                oldPoint, point, rule.getStr("point"), String.valueOf(importId));
        // 处理余额
        applyAccountRule(existing.getSiteId(), existing.getMemberId(), AccountTypeEnum.BALANCE.getType(),
                existing.getBalance(), balance, rule.getStr("balance"), String.valueOf(importId));

    }

    /**
     * 处理规则
     *
     * @param siteId      站点id
     * @param memberId    会员id
     * @param accountType 变更类型
     * @param oldVal      旧值
     * @param newVal      新值
     * @param rule        规则 跳过/覆盖/增加
     * @param relatedId   相关ID
     */
    private void applyAccountRule(Integer siteId, Integer memberId, String accountType, BigDecimal oldVal, BigDecimal newVal,
                                  String rule, String relatedId) {
        //跳过时不处理
        if (StringUtils.isBlank(rule) || "skip".equals(rule)) return;
        if (newVal == null) return;

        BigDecimal change = null;
        if ("overwrite".equals(rule)) {
            //覆盖时计算差值进行变更
            change = newVal.subtract(oldVal);
        } else if ("add".equals(rule)) {
            change = newVal;
        }
        if (change != null && change.compareTo(BigDecimal.ZERO) != 0) {
            coreMemberAccountService.addLog(siteId, memberId, accountType, change.doubleValue(), "member_import", "会员导入", relatedId);
        }
    }

    /**
     * 获取正在进行的任务列表
     */
    public List<MemberImport> getProcessingList() {
        LambdaQueryWrapper<MemberImport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MemberImport::getStatus, MemberImportStatusEnum.PROCESSING.getValue());
        return memberImportMapper.selectList(wrapper);
    }

    /**
     * 获取BigDecimal类型的数字
     *
     * @param str 待转换的数字
     * @return 转换失败时返回0
     */
    private BigDecimal getBigDecimal(String str) {
        if (str == null || str.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
