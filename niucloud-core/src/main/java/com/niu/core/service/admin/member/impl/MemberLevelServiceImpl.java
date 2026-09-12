package com.niu.core.service.admin.member.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.entity.member.MemberLevel;
import com.niu.core.mapper.member.MemberLevelMapper;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.admin.member.param.MemberLevelParam;
import com.niu.core.service.admin.member.param.MemberLevelSearchParam;
import com.niu.core.service.admin.member.IMemberLevelService;
import com.niu.core.service.admin.member.vo.MemberLevelAllListVo;
import com.niu.core.service.admin.member.vo.MemberLevelInfoVo;
import com.niu.core.service.admin.member.vo.MemberLevelListVo;
import com.niu.core.service.core.member.ICoreMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import jakarta.annotation.Resource;
import java.util.LinkedList;
import java.util.List;


/**
 * 会员等级实现
 */
@Service
public class MemberLevelServiceImpl implements IMemberLevelService {
        
    @Resource
    MemberLevelMapper memberLevelMapper;

    @Resource
    MemberMapper memberMapper;

    @Resource
    ICoreMemberService coreMemberService;

    /**
     * 会员等级列表
     * @param pageParam 分页参数
     * @param searchParam 搜索参数
     * @return PageResult<MemberLevelListVo>
     */
    @Override
    public PageResult<MemberLevelListVo> page(PageParam pageParam, MemberLevelSearchParam searchParam) {
        Integer siteId = RequestUtils.defaultSiteId();
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByAsc("growth");
        if (ObjectUtil.isNotEmpty(searchParam.getLevelName())) queryWrapper.like("level_name", searchParam.getLevelName());

        IPage<MemberLevel> iPage = memberLevelMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MemberLevelListVo> list = new LinkedList<>();
        for (MemberLevel item : iPage.getRecords()) {
            MemberLevelListVo vo = new MemberLevelListVo();
            BeanUtils.copyProperties(item, vo);
            vo.setMemberNum(memberMapper.selectCount(new QueryWrapper<Member>().eq("member_level", vo.getLevelId())));
            if (ObjectUtil.isNotEmpty(item.getLevelBenefits())) vo.setLevelBenefits(coreMemberService.getBenefitsContent(item.getSiteId(), JSONUtil.parseObj(item.getLevelBenefits()), "admin"));
            if (ObjectUtil.isNotEmpty(item.getLevelGifts())) vo.setLevelGifts(coreMemberService.getGiftContent(item.getSiteId(), JSONUtil.parseObj(item.getLevelGifts()), "admin"));
            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public List<MemberLevelListVo> list(MemberLevelSearchParam searchParam) {
        Integer siteId = RequestUtils.defaultSiteId();
        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("site_id", siteId);
        queryWrapper.orderByDesc("level_id");
        if (ObjectUtil.isNotEmpty(searchParam.getLevelName())) queryWrapper.like("level_name", searchParam.getLevelName());

        List<MemberLevel> list = memberLevelMapper.selectList(queryWrapper);
        List<MemberLevelListVo> listVo = new LinkedList<>();
        for (MemberLevel item : list) {
            MemberLevelListVo vo = new MemberLevelListVo();
            BeanUtils.copyProperties(item, vo);
            listVo.add(vo);
        }
        return listVo;
    }

    /**
     * 会员等级详情
     * @param id 主键
     * @return MemberLevelInfoVo
     */
    @Override
    public MemberLevelInfoVo info(Integer id) {
        Integer siteId = RequestUtils.defaultSiteId();

        MemberLevel model = memberLevelMapper.selectOne(
                new QueryWrapper<MemberLevel>()
                    .eq("site_id", siteId)
                    .eq("level_id", id)
                    .last("limit 1"));

        Assert.notNull(model, "等级不存在");

        MemberLevelInfoVo vo = new MemberLevelInfoVo();
        BeanUtils.copyProperties(model, vo);
        // 对齐前端 gift-coupon：coupon_id 必须为数组，否则不会调 platform_coupon/selected，礼包优惠券不展示
        vo.setLevelGifts(normalizeLevelGifts(model.getLevelGifts()));
        return vo;
    }

    /**
     * 规范化等级礼包 JSON：shop_coupon.coupon_id 统一为数组，is_use 统一为 0/1。
     */
    private String normalizeLevelGifts(String raw) {
        if (StrUtil.isBlank(raw)) {
            return "{}";
        }
        String trim = raw.trim();
        if (!trim.startsWith("{")) {
            return "{}";
        }
        try {
            JSONObject gifts = JSONUtil.parseObj(trim);
            JSONObject shopCoupon = gifts.getJSONObject("shop_coupon");
            if (shopCoupon != null) {
                shopCoupon.set("is_use", shopCoupon.getInt("is_use", 0));
                shopCoupon.set("coupon_id", toCouponIdArray(shopCoupon.get("coupon_id")));
                if (shopCoupon.get("coupon_list") == null) {
                    shopCoupon.set("coupon_list", new JSONObject());
                }
                gifts.set("shop_coupon", shopCoupon);
            }
            return gifts.toString();
        } catch (Exception e) {
            return trim;
        }
    }

    private JSONArray toCouponIdArray(Object couponId) {
        JSONArray arr = new JSONArray();
        if (couponId == null) {
            return arr;
        }
        if (couponId instanceof JSONArray) {
            for (Object item : (JSONArray) couponId) {
                Integer id = toPositiveInt(item);
                if (id != null) {
                    arr.add(id);
                }
            }
            return arr;
        }
        if (couponId instanceof JSONObject) {
            for (Object item : ((JSONObject) couponId).values()) {
                Integer id = toPositiveInt(item);
                if (id != null) {
                    arr.add(id);
                }
            }
            return arr;
        }
        if (couponId instanceof CharSequence) {
            String text = couponId.toString().trim();
            if (StrUtil.isBlank(text)) {
                return arr;
            }
            if (JSONUtil.isTypeJSONArray(text)) {
                return toCouponIdArray(JSONUtil.parseArray(text));
            }
            for (String part : StrUtil.splitTrim(text, ',')) {
                Integer id = toPositiveInt(part);
                if (id != null) {
                    arr.add(id);
                }
            }
            return arr;
        }
        Integer id = toPositiveInt(couponId);
        if (id != null) {
            arr.add(id);
        }
        return arr;
    }

    private Integer toPositiveInt(Object value) {
        if (value == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(value.toString().trim());
            return id > 0 ? id : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 会员等级添加
     * @param addParam 参数
     */
    @Override
    public void add(MemberLevelParam addParam) {
        Integer siteId = RequestUtils.defaultSiteId();

        MemberLevel model = new MemberLevel();

        model.setSiteId(siteId);
        model.setLevelName(addParam.getLevelName());
        model.setGrowth(addParam.getGrowth());
        model.setRemark(addParam.getRemark());
        model.setCreateTime(System.currentTimeMillis() / 1000);
        model.setLevelBenefits(addParam.getLevelBenefits().toString());
        model.setLevelGifts(addParam.getLevelGifts().toString());

        memberLevelMapper.insert(model);
    }

    /**
     * 会员等级编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MemberLevelParam editParam) {
        Integer siteId = RequestUtils.defaultSiteId();

        UpdateWrapper<MemberLevel> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("site_id", siteId)
                .eq("level_id",  id);

        MemberLevel model = new MemberLevel();
        model.setLevelName(editParam.getLevelName());
        model.setGrowth(editParam.getGrowth());
        model.setRemark(editParam.getRemark());
        model.setUpdateTime(System.currentTimeMillis() / 1000);
        model.setLevelBenefits(editParam.getLevelBenefits().toString());
        model.setLevelGifts(editParam.getLevelGifts().toString());

        memberLevelMapper.update(model, updateWrapper);
    }

    /**
     * 会员等级删除
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        Integer siteId = RequestUtils.defaultSiteId();

        Long memberNum = memberMapper.selectCount(new QueryWrapper<Member>()
                .eq("site_id", siteId)
                .eq("member_level", id));
        if (memberNum > 0) throw new CommonException("该等级下存在会员不允许删除");

        QueryWrapper<MemberLevel> queryWrapper = new QueryWrapper<MemberLevel>()
                .eq("site_id", siteId)
                .eq("level_id", id);

        memberLevelMapper.delete(queryWrapper);
    }

    /**
     * 全部会员等级
     * @return
     */
    public List<MemberLevelAllListVo> all() {
        Integer siteId = RequestUtils.defaultSiteId();

        MPJQueryWrapper<MemberLevel> queryWrapper = new MPJQueryWrapper<MemberLevel>();
        queryWrapper.select("level_id,level_name, growth,site_id,level_benefits,level_gifts").eq("site_id", siteId);

        List<MemberLevel> labels = memberLevelMapper.selectList(queryWrapper); // 调用 selectList 方法

        List<MemberLevelAllListVo> list = new LinkedList<>();
        for (MemberLevel item : labels) {
            MemberLevelAllListVo vo = new MemberLevelAllListVo();
            BeanUtils.copyProperties(item, vo);
            list.add(vo);
        }
        return list;
    }
}
