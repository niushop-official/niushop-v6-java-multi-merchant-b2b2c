package com.niu.core.service.site.shop.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.shop.MemberLabel;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.mapper.shop.SiteShopMemberLabelMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.service.core.shop.ICoreShopMemberService;
import com.niu.core.service.site.shop.ISiteShopMemberService;
import com.niu.core.service.site.shop.param.*;
import com.niu.core.service.site.shop.vo.PlatMemberSelectVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelAllListVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberListVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 店铺会员实现
 */
@Service
public class SiteShopMemberServiceImpl implements ISiteShopMemberService {

    @Resource
    SiteShopMemberMapper siteShopMemberMapper;

    @Resource
    SiteShopMemberLabelMapper siteShopMemberLabelMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    ICoreShopMemberService coreShopMemberService;


    @Override
    public PageResult<SiteShopMemberListVo> page(PageParam pageParam, SiteShopMemberSearchParam searchParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();


        MPJQueryWrapper<SiteShopMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ssm");
        queryWrapper.select("ssm.id,ssm.site_id,ssm.member_id, ssm.is_follow, ssm.first_consum_time, ssm.last_consum_time, ssm.shop_member_label, m.member_no, m.mobile, m.nickname, m.username, m.headimg, m.register_channel,ssm.comsum_money,ssm.comsum_num");
        queryWrapper.leftJoin("?_member m ON m.`member_id` = ssm.member_id".replace("?_", GlobalConfig.tablePrefix));

        queryWrapper.eq("ssm.site_id", RequestUtils.siteId());

        if (ObjectUtil.isNotEmpty(searchParam.getAddon())) {
            queryWrapper.eq("ssm.addon", searchParam.getAddon());
        }

        // 查询条件
        if (ObjectUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("m.member_no", searchParam.getKeyword()).or()
                    .like("m.username", searchParam.getKeyword()).or()
                    .like("m.nickname", searchParam.getKeyword()).or()
                    .like("m.mobile", searchParam.getKeyword()));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getComsumMoney())) {
            queryWrapper.eq("ssm.comsum_money", searchParam.getComsumMoney());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getRegisterChannel()) && !"all".equals(searchParam.getRegisterChannel())) {
            queryWrapper.eq("m.register_channel", searchParam.getRegisterChannel());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getComsumMoney())) {
        queryWrapper.eq("ssm.comsum_money", searchParam.getComsumMoney());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getComsumNum())) {
        queryWrapper.eq("ssm.comsum_num", searchParam.getComsumNum());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getFirstConsumTime())) {
        queryWrapper.eq("ssm.first_consum_time", searchParam.getFirstConsumTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getId())) {
        queryWrapper.eq("ssm.id", searchParam.getId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getIsFollow())) {
        queryWrapper.eq("ssm.is_follow", searchParam.getIsFollow());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getLastConsumTime())) {
        queryWrapper.eq("ssm.last_consum_time", searchParam.getLastConsumTime());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getMemberId())) {
        queryWrapper.eq("ssm.member_id", searchParam.getMemberId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getShopMemberLabel())) {
        queryWrapper.like("ssm.shop_member_label", searchParam.getShopMemberLabel());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getSiteId())) {
        queryWrapper.eq("ssm.site_id", searchParam.getSiteId());
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "m.create_time", searchParam.getCreateTime());
        }
        queryWrapper.orderByDesc("ssm.id");

        IPage<SiteShopMemberListVo> iPage = siteShopMemberMapper.selectJoinPage(new Page<>(page, limit), SiteShopMemberListVo.class,  queryWrapper);

        Set<Integer> allLabelIds = new HashSet<>();
        for (SiteShopMemberListVo item : iPage.getRecords()) {
            allLabelIds.addAll(parseShopMemberLabelIds(item.getShopMemberLabel()));
        }
        Map<Integer, SiteShopMemberLabelAllListVo> labelMap = getShopMemberLabelMap(RequestUtils.siteId(), allLabelIds);

        List<SiteShopMemberListVo> list = new LinkedList<>();
        for (SiteShopMemberListVo item : iPage.getRecords()) {
            SiteShopMemberListVo vo = new SiteShopMemberListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setShopMemberLabelArray(buildShopMemberLabelArray(item.getShopMemberLabel(), labelMap));
            list.add(vo);
        }
        fillMallFirstConsumTime(list, searchParam.getAddon());
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SiteShopMemberInfoVo info(Integer memberId, String addon) {

        MPJQueryWrapper<SiteShopMember> queryWrapper = new MPJQueryWrapper<SiteShopMember>()
                .setAlias("ssm")
                .leftJoin("?_member m ON m.`member_id` = ssm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .leftJoin("?_member_level ml ON ml.level_id = m.member_level".replace("?_", GlobalConfig.tablePrefix))
                .select("ssm.*,m.*, ml.level_name as member_level_name")
                .eq("m.member_id", memberId)
                .eq("ssm.site_id", RequestUtils.siteId());
        if (ObjectUtil.isNotEmpty(addon)) {
            queryWrapper.eq("ssm.addon", addon);
        }
        queryWrapper.last("limit 1");

        SiteShopMemberInfoVo model = siteShopMemberMapper.selectJoinOne(SiteShopMemberInfoVo.class, queryWrapper);

        Assert.notNull(model, "数据不存在");


        List<Integer> labelIds = parseShopMemberLabelIds(model.getShopMemberLabel());
        model.setShopMemberLabelArray(buildShopMemberLabelArray(model.getShopMemberLabel(),
                getShopMemberLabelMap(RequestUtils.siteId(), new HashSet<>(labelIds))));
        fillMallFirstConsumTime(model, addon);
        return model;
    }

    private void fillMallFirstConsumTime(List<SiteShopMemberListVo> list, String addon) {
        if (!"mall".equals(addon) || list.isEmpty()) {
            return;
        }
        Set<Integer> memberIds = new HashSet<>();
        for (SiteShopMemberListVo item : list) {
            if (isEmptyTime(item.getFirstConsumTime()) && item.getMemberId() != null) {
                memberIds.add(item.getMemberId());
            }
        }
        Map<Integer, Long> firstConsumTimeMap = queryMallFirstConsumTimes(RequestUtils.siteId(), memberIds);
        for (SiteShopMemberListVo item : list) {
            Long firstConsumTime = firstConsumTimeMap.get(item.getMemberId());
            if (firstConsumTime != null) {
                item.setFirstConsumTime(firstConsumTime);
                updateFirstConsumTime(item.getId(), firstConsumTime);
            }
        }
    }

    private void fillMallFirstConsumTime(SiteShopMemberInfoVo model, String addon) {
        if (!"mall".equals(addon) || model == null || !isEmptyTime(model.getFirstConsumTime()) || model.getMemberId() == null) {
            return;
        }
        Map<Integer, Long> firstConsumTimeMap = queryMallFirstConsumTimes(RequestUtils.siteId(), Set.of(model.getMemberId()));
        Long firstConsumTime = firstConsumTimeMap.get(model.getMemberId());
        if (firstConsumTime != null) {
            model.setFirstConsumTime(firstConsumTime);
            updateFirstConsumTime(model.getId(), firstConsumTime);
        }
    }

    private Map<Integer, Long> queryMallFirstConsumTimes(Integer siteId, Set<Integer> memberIds) {
        if (siteId == null || memberIds == null || memberIds.isEmpty()) {
            return Collections.emptyMap();
        }
        String placeholders = String.join(",", Collections.nCopies(memberIds.size(), "?"));
        String sql = "SELECT member_id, MIN(CASE WHEN pay_time > 0 THEN pay_time ELSE create_time END) AS first_consum_time FROM `" + GlobalConfig.tablePrefix + "mall_order` "
                + "WHERE site_id = ? AND member_id IN (" + placeholders + ") "
                + "AND (pay_time > 0 OR create_time > 0) AND delete_time = 0 AND status IN ('2','3','5') "
                + "GROUP BY member_id";
        List<Object> args = new ArrayList<>();
        args.add(siteId);
        args.addAll(memberIds);
        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, args.toArray());
            Map<Integer, Long> result = new HashMap<>();
            for (Map<String, Object> row : rows) {
                Number memberId = (Number) row.get("member_id");
                Number firstConsumTime = (Number) row.get("first_consum_time");
                if (memberId != null && firstConsumTime != null) {
                    result.put(memberId.intValue(), firstConsumTime.longValue());
                }
            }
            return result;
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    private void updateFirstConsumTime(Integer id, Long firstConsumTime) {
        if (id == null || firstConsumTime == null) {
            return;
        }
        SiteShopMember update = new SiteShopMember();
        update.setId(id);
        update.setFirstConsumTime(firstConsumTime);
        siteShopMemberMapper.updateById(update);
    }

    private boolean isEmptyTime(Long time) {
        return time == null || time <= 0;
    }

    /**
     * Resolves the label JSON stored on shop members without querying once per member.
     */
    private Map<Integer, SiteShopMemberLabelAllListVo> getShopMemberLabelMap(Integer siteId, Set<Integer> labelIds) {
        if (labelIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<MemberLabel> labels = siteShopMemberLabelMapper.selectList(new QueryWrapper<MemberLabel>()
                .select("label_id", "label_name")
                .eq("site_id", siteId)
                .in("label_id", labelIds));
        Map<Integer, SiteShopMemberLabelAllListVo> result = new HashMap<>(labels.size());
        for (MemberLabel label : labels) {
            SiteShopMemberLabelAllListVo labelVo = new SiteShopMemberLabelAllListVo();
            BeanUtils.copyProperties(label, labelVo);
            result.put(labelVo.getLabelId(), labelVo);
        }
        return result;
    }

    private List<SiteShopMemberLabelAllListVo> buildShopMemberLabelArray(String shopMemberLabel,
                                                                            Map<Integer, SiteShopMemberLabelAllListVo> labelMap) {
        List<SiteShopMemberLabelAllListVo> result = new ArrayList<>();
        for (Integer labelId : parseShopMemberLabelIds(shopMemberLabel)) {
            SiteShopMemberLabelAllListVo label = labelMap.get(labelId);
            if (label != null) {
                result.add(label);
            }
        }
        return result;
    }

    private List<Integer> parseShopMemberLabelIds(String shopMemberLabel) {
        if (StrUtil.isBlank(shopMemberLabel)) {
            return Collections.emptyList();
        }
        try {
            JSONArray labelArray = JSONUtil.parseArray(shopMemberLabel);
            List<Integer> labelIds = new ArrayList<>(labelArray.size());
            for (Object labelId : labelArray) {
                Integer value = cn.hutool.core.convert.Convert.toInt(labelId, null);
                if (value != null) {
                    labelIds.add(value);
                }
            }
            return labelIds;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    @Override
    public void add(SiteShopMemberParam addParam) {

        SiteShopMember model = new SiteShopMember();

        model.setComsumMoney(addParam.getComsumMoney());
        model.setComsumNum(addParam.getComsumNum());
        model.setFirstConsumTime(addParam.getFirstConsumTime());
        model.setIsFollow(addParam.getIsFollow());
        model.setLastConsumTime(addParam.getLastConsumTime());
        model.setMemberId(addParam.getMemberId());
        model.setShopMemberLabel(addParam.getShopMemberLabel());

        model.setSiteId(RequestUtils.siteId());

        siteShopMemberMapper.insert(model);
    }

    @Override
    public void edit(Integer id, SiteShopMemberParam editParam) {

        SiteShopMember model = siteShopMemberMapper.selectOne(
                new QueryWrapper<SiteShopMember>()
                        .eq("id", id)
                        .eq("site_id", RequestUtils.siteId()));

        Assert.notNull(model, "店铺会员不存在");
        model.setComsumMoney(editParam.getComsumMoney());
        model.setComsumNum(editParam.getComsumNum());
        model.setFirstConsumTime(editParam.getFirstConsumTime());
        model.setIsFollow(editParam.getIsFollow());
        model.setLastConsumTime(editParam.getLastConsumTime());
        model.setMemberId(editParam.getMemberId());
        model.setShopMemberLabel(editParam.getShopMemberLabel());
        siteShopMemberMapper.updateById(model);
    }

    @Override
    public void del(Integer id) {
        siteShopMemberMapper.delete(new QueryWrapper<SiteShopMember>().eq("id", id).eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void modify(SiteShopMemberModifyParam editParam) {
        Integer siteId = RequestUtils.siteId();
        SiteShopMember model = new SiteShopMember();
        model.setShopMemberLabel(String.valueOf(editParam.getValue()));
        UpdateWrapper<SiteShopMember> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("member_id", editParam.getMemberId());
        if (ObjectUtil.isNotEmpty(editParam.getAddon())) {
            updateWrapper.eq("addon", editParam.getAddon());
        }
        siteShopMemberMapper.update(model, updateWrapper);
    }

    @Override
    public void batchModify(SiteShopMemberBatchModifyParam editParam) {
        coreShopMemberService.batchModify(RequestUtils.siteId(), editParam.getMemberIds(), editParam.getAddon(), editParam.getField(), editParam.getValue());
    }


    @Override
    public List<PlatMemberSelectVo> platMemberSelect(PlatMemberSelectParam searchParam) {
        QueryWrapper<Member> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("member_id, nickname, member_no");
        queryWrapper.eq("site_id", RequestUtils.defaultSiteId());

        if (StrUtil.isNotEmpty(searchParam.getKeyword())) {
            queryWrapper.and(i -> i.like("member_no", searchParam.getKeyword())
                    .or().like("nickname", searchParam.getKeyword())
                    .or().like("mobile", searchParam.getKeyword()));
        }

        queryWrapper.orderByDesc("member_id");

        List<Member> memberList = memberMapper.selectList(queryWrapper);
        List<PlatMemberSelectVo> result = new LinkedList<>();

        for (Member member : memberList) {
            PlatMemberSelectVo vo = new PlatMemberSelectVo();
            vo.setMemberId(member.getMemberId());
            vo.setNickname(member.getNickname());
            vo.setMemberNo(member.getMemberNo());
            result.add(vo);
        }

        return result;
    }

}
