package com.niu.core.service.api.sys.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.CollectUtils;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.object.CollectBuildUtils;
import com.niu.core.common.utils.object.ObjectGroupUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.verify.Verifier;
import com.niu.core.entity.verify.Verify;
import com.niu.core.enums.sys.VerifyTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.verify.VerifierMapper;
import com.niu.core.mapper.verify.VerifyMapper;
import com.niu.core.service.admin.member.vo.MemberBriefInfoVo;
import com.niu.core.service.api.sys.ISysVerifyService;
import com.niu.core.service.api.sys.param.*;
import com.niu.core.service.api.sys.vo.SysVerifyDetailVo;
import com.niu.core.service.api.sys.vo.SysVerifyGetCodeVo;
import com.niu.core.service.api.sys.vo.SysVerifyRecordsVo;
import com.niu.core.service.core.verify.ICoreVerifyService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysVerifyServiceImpl implements ISysVerifyService {

    @Autowired
    private VerifyMapper verifyMapper;

    @Autowired
    private VerifierMapper verifierMapper;

    @Autowired
    private MemberMapper memberMapper;

    @Resource
    private ICoreVerifyService coreVerifyService;

    private static final String CACHE_VERIFY = "verify";

    @Override
    public List<SysVerifyGetCodeVo> getVerifyCode(SysVerifyGetCodeParam param) {
        Map<String, Object> data = param.getData();
        Integer siteId = (data != null && data.get("site_id") != null) ?
                ((data.get("site_id") instanceof Integer) ? (Integer) data.get("site_id") : Integer.valueOf(data.get("site_id").toString())) : 0;

        // 生成核销码(对应业务调用)
        List<String> verifyCodeList = coreVerifyService.create(siteId, param.memberId(), param.getType(), param.getData());
        List<SysVerifyGetCodeVo> sysVerifyGetCodeVoList = new ArrayList<>();
        for (String verifyCode : verifyCodeList) {
            SysVerifyGetCodeVo sysVerifyGetCodeVo = new SysVerifyGetCodeVo();
            sysVerifyGetCodeVo.setCode(verifyCode);
            sysVerifyGetCodeVo.setQrcode(QrcodeUtils.qrcode(siteId, "h5", verifyCode, "", null));
            sysVerifyGetCodeVoList.add(sysVerifyGetCodeVo);
        }
        return sysVerifyGetCodeVoList;
    }

        /**
         * 生成二维码
         *
         * @param siteId
         * @param verifyCode
         * @return
         */
        private String generateQrcode (Integer siteId, String verifyCode){

            return "";
        }





    /**
     * 校验是否是核销员
     *
     * @param param
     * @return
     */
    @Override
    public Boolean checkVerifier(SysVerifyCheckVerifierParam param) {
        QueryWrapper<Verifier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id", param.memberId());
        return verifierMapper.exists(queryWrapper);
    }

    /**
     * 获取核销员核销记录
     *
     * @param param
     * @return
     */
    @Override
    public PageResult getRecordsPageByVerifier(PageParam pageParam, SysVerifyRecordsParam param) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        QueryWrapper<Verify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("verifier_member_id", param.memberId());

        if (StrUtil.isNotEmpty(param.getCode())) {
            queryWrapper.eq("code", param.getCode());
        }
        if (StrUtil.isNotEmpty(param.getType())) {
            queryWrapper.eq("type", param.getType());
        }
        if (StrUtil.isNotEmpty(param.getCreateTime())) {
            queryWrapper.eq("create_time", param.getCreateTime());
        }
        if (StrUtil.isNotEmpty(param.getRelateTag())) {
            queryWrapper.eq("relate_tag", param.getRelateTag());
        }
        if (StrUtil.isNotEmpty(param.getKeyword())) {
            queryWrapper.like("body", param.getKeyword());
        }
        queryWrapper.orderByDesc("create_time");

        IPage<Verify> iPage = this.verifyMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<SysVerifyRecordsVo> dataList = CollectUtils.convert(iPage.getRecords(), SysVerifyRecordsVo.class);

        if (!dataList.isEmpty()) {
            // 填充会员信息
            QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
            memberQueryWrapper.select("member_id", "nickname", "mobile", "headimg");
            List<Integer> memberIdList = CollectBuildUtils.getKeyValueList(dataList, "verifierMemberId");
            memberQueryWrapper.in("member_id", memberIdList);
            List<Member> memberList = memberMapper.selectList(memberQueryWrapper);
            Map<Integer, Member> memberMap = ObjectGroupUtils.group(memberList, "memberId");
            
            dataList.forEach(bean -> {
                // 设置会员信息
                Member member = memberMap.get(bean.getVerifierMemberId());
                if (member != null) {
                    MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
                    BeanUtils.copyProperties(member, memberVo);
                    bean.setMember(memberVo);
                }
                // 设置类型名称
                String typeName = VerifyTypeEnum.getTypeByName(bean.getType());
                bean.setTypeName(typeName);
            });
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(dataList);
    }

    @Override
    public SysVerifyDetailVo getRecordsDetailByVerifier(SysVerifyDetailParam param) {
        QueryWrapper<Verify> verifyQueryWrapper = new QueryWrapper<>();
        verifyQueryWrapper.eq("verifier_member_id", param.memberId());
        verifyQueryWrapper.eq("code", param.getCode());
        Verify verify = this.verifyMapper.selectOne(verifyQueryWrapper);
        SysVerifyDetailVo sysVerifyDetailVo = new SysVerifyDetailVo();
        if (verify != null) {
            BeanUtils.copyProperties(verify, sysVerifyDetailVo);
            // 查询会员信息
            QueryWrapper<Member> memberQueryWrapper = new QueryWrapper<>();
            memberQueryWrapper.select("member_id", "nickname", "mobile", "headimg");
            memberQueryWrapper.eq("member_id", verify.getVerifierMemberId());
            Member member = this.memberMapper.selectOne(memberQueryWrapper);
            if (member != null) {
                MemberBriefInfoVo memberVo = new MemberBriefInfoVo();
                BeanUtils.copyProperties(member, memberVo);
                sysVerifyDetailVo.setMember(memberVo);
            }
        }
        return sysVerifyDetailVo;
    }

    /**
     * 获取核销码信息
     *
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> getInfoByCode(SysVerifyByCodeParam param) {

        return coreVerifyService.getInfoByCode(param.getCode(), param.memberId());
    }

    /**
     * 核销(核销api调用)
     *
     * @param param
     * @return
     */
    @Override
    public Boolean verifyCode(SysVerifyCodeParam param) {
        return coreVerifyService.verify( param.memberId(), param.getCode());
    }

}
