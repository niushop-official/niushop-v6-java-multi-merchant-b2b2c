package com.niu.core.service.site.shop.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.niu.core.entity.shop.MemberLabel;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.member.Member;
import com.niu.core.mapper.shop.SiteShopMemberLabelMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.api.shop.vo.SiteShopFollowVo;
import com.niu.core.service.site.shop.ISiteShopMemberService;
import com.niu.core.service.site.shop.param.SiteShopMemberModifyParam;
import com.niu.core.service.site.shop.param.SiteShopMemberParam;
import com.niu.core.service.site.shop.param.SiteShopMemberSearchParam;
import com.niu.core.service.site.shop.param.PlatMemberSelectParam;
import com.niu.core.service.site.shop.vo.SiteShopMemberInfoVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberLabelAllListVo;
import com.niu.core.service.site.shop.vo.SiteShopMemberListVo;
import com.niu.core.service.site.shop.vo.PlatMemberSelectVo;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


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


    @Override
    public PageResult<SiteShopMemberListVo> page(PageParam pageParam, SiteShopMemberSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();


        MPJQueryWrapper<SiteShopMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ssm");
        queryWrapper.select("ssm.id,ssm.site_id,ssm.member_id, ssm.is_follow, ssm.first_consum_time, ssm.last_consum_time, ssm.shop_member_label, m.member_no, m.mobile, m.nickname, m.username, m.headimg, m.register_channel,ssm.comsum_money,ssm.comsum_num");
        queryWrapper.leftJoin("?_member m ON m.`member_id` = ssm.member_id".replace("?_", "ns_"));

        queryWrapper.eq("ssm.site_id", RequestUtils.siteId());

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
        if (ObjectUtil.isNotEmpty(searchParam.getRegisterChannel())) {
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

        List<SiteShopMemberListVo> list = new LinkedList<>();
        for (SiteShopMemberListVo item : iPage.getRecords()) {
            SiteShopMemberListVo vo = new SiteShopMemberListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);

            if (!item.getShopMemberLabel().isEmpty()) {
                JSONArray memberLabelArrays = JSONUtil.parseArray(vo.getShopMemberLabel());
                if (memberLabelArrays != null && memberLabelArrays.size() > 0) {
                    List<SiteShopMemberLabelAllListVo> memberLabelArray = new LinkedList<>();
                    List<MemberLabel> labelList = siteShopMemberLabelMapper.selectList(new QueryWrapper<MemberLabel>().select("label_name").in("label_id", memberLabelArrays));
                    for (MemberLabel labelItem : labelList) {
                        SiteShopMemberLabelAllListVo labelVo = new SiteShopMemberLabelAllListVo();
                        BeanUtils.copyProperties(labelItem, labelVo);
                        memberLabelArray.add(labelVo);
                    }
                    vo.setShopMemberLabelArray(memberLabelArray);
                }
            }
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public SiteShopMemberInfoVo info(Integer id) {

        Integer siteId = RequestUtils.defaultSiteId();
        SiteShopMemberInfoVo model = siteShopMemberMapper.selectJoinOne(SiteShopMemberInfoVo.class,
                new MPJQueryWrapper<SiteShopMember>()
                        .setAlias("ssm")
                        .leftJoin("?_member m ON m.`member_id` = ssm.member_id".replace("?_", "ns_"))
                        .leftJoin("?_member_level ml ON ml.level_id = m.member_level".replace("?_", "ns_"))
                        .select("ssm.*,m.*, ml.level_name as member_level_name")
                        .eq("m.member_id", id)
                        .eq("ssm.site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");


        if (StrUtil.isNotEmpty(model.getShopMemberLabel()) && !Arrays.asList(model.getShopMemberLabel()).isEmpty()) {
            JSONArray memberLabelArrays = JSONUtil.parseArray(model.getShopMemberLabel());
            if (memberLabelArrays != null && memberLabelArrays.size() > 0) {
                List<SiteShopMemberLabelAllListVo> memberLabelArray = new LinkedList<>();
                List<MemberLabel> labelList = siteShopMemberLabelMapper.selectList(new QueryWrapper<MemberLabel>().select("label_name").in("label_id", memberLabelArrays));
                for (MemberLabel labelItem : labelList) {
                    SiteShopMemberLabelAllListVo labelVo = new SiteShopMemberLabelAllListVo();
                    BeanUtils.copyProperties(labelItem, labelVo);
                    memberLabelArray.add(labelVo);
                }
                model.setShopMemberLabelArray(memberLabelArray);
            }
        }
        return model;
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
        siteShopMemberMapper.update(model, updateWrapper);
    }

    @Override
    public PageResult<SiteShopFollowVo> followList(PageParam pageParam) {
        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();
        MPJQueryWrapper<SiteShopMember> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.select("ssm.id,ssm.site_id,site.logo,site.icon,site.site_name,site.front_end_logo,ssm.member_id")
                .setAlias("ssm")
                .leftJoin("?_site site ON site.site_id = ssm.site_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.eq("ssm.is_follow",1);
        queryWrapper.eq("ssm.member_id",RequestUtils.memberId());
        queryWrapper.orderByDesc("site.create_time");
        IPage<SiteShopFollowVo> iPage = siteShopMemberMapper.selectJoinPage(new Page<>(page, limit), SiteShopFollowVo.class, queryWrapper);
        List<SiteShopFollowVo> records = iPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)){
            for (SiteShopFollowVo record : records) {
                // null避免图片裂开
                if ("".equals(record.getLogo())) record.setLogo(null);
                if ("".equals(record.getIcon())) record.setIcon(null);
                if ("".equals(record.getFrontEndLogo())) record.setFrontEndLogo(null);
                
                Site site = new Site();
                site.setSiteId(record.getSiteId());
                site.setSiteName(record.getSiteName());
                site.setLogo(record.getLogo());
                site.setIcon(record.getIcon());
                // 如果front_end_logo为空，使用logo作为默认值
                String frontEndLogo = record.getFrontEndLogo();
                if (ObjectUtil.isEmpty(frontEndLogo)) {
                    frontEndLogo = record.getLogo();
                }
                site.setFrontEndLogo(frontEndLogo);
                record.setSite(site);
            }
        }
        return PageResult.build(iPage);
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
