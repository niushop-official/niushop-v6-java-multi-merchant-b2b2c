package com.niu.core.service.admin.marketing.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.mapper.QueryMapperUtils;
import com.niu.core.entity.marketing.NewcomerGiftRecords;
import com.niu.core.mapper.marketing.NewcomerGiftRecordsMapper;
import com.niu.core.service.admin.marketing.INewcomerGiftService;
import com.niu.core.service.admin.marketing.param.NewcomerGiftConfigParam;
import com.niu.core.service.admin.marketing.param.NewcomerGiftRecordsSearchParam;
import com.niu.core.service.admin.marketing.vo.NewcomerGiftConfigVo;
import com.niu.core.service.admin.marketing.vo.NewcomerGiftRecordsListVo;
import com.niu.core.service.core.member.ICoreMemberService;
import com.niu.core.service.core.sys.ICoreConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 新人礼后台服务
 */
@Service
public class NewcomerGiftServiceImpl implements INewcomerGiftService {

    private static final String CONFIG_KEY = "NEWCOMER_GIFT";

    @Resource
    private ICoreConfigService coreConfigService;

    @Resource
    private ICoreMemberService coreMemberService;

    @Resource
    private NewcomerGiftRecordsMapper newcomerGiftRecordsMapper;

    @Override
    public NewcomerGiftConfigVo getConfig() {
        JSONObject config = coreConfigService.getConfigValue(RequestUtils.defaultSiteId(), CONFIG_KEY);
        if (ObjectUtil.isEmpty(config) || config.isEmpty()) {
            return defaultConfig();
        }
        NewcomerGiftConfigVo vo = JSONUtil.toBean(config, NewcomerGiftConfigVo.class);
        if (vo.getGifts() == null) {
            vo.setGifts(new JSONObject());
        }
        if (vo.getIsOpen() == null) {
            vo.setIsOpen(0);
        }
        return vo;
    }

    @Override
    public void setConfig(NewcomerGiftConfigParam param) {
        JSONObject data = new JSONObject();
        data.set("is_open", ObjectUtil.defaultIfNull(param.getIsOpen(), 0));
        data.set("gifts", ObjectUtil.defaultIfNull(param.getGifts(), new JSONObject()));
        coreConfigService.setConfig(RequestUtils.defaultSiteId(), CONFIG_KEY, data);
    }

    @Override
    public PageResult<NewcomerGiftRecordsListVo> records(PageParam pageParam, NewcomerGiftRecordsSearchParam searchParam) {
        int page = pageParam.getPage();
        int limit = pageParam.getLimit();

        MPJQueryWrapper<NewcomerGiftRecords> queryWrapper = new MPJQueryWrapper<>();
        queryWrapper.setAlias("ngr")
                .innerJoin("?_member nm ON ngr.member_id = nm.member_id".replace("?_", GlobalConfig.tablePrefix))
                .select("ngr.record_id, ngr.member_id, ngr.gifts, ngr.create_time, nm.member_no, nm.nickname, nm.headimg")
                .orderByDesc("ngr.record_id");

        if (ObjectUtil.isNotEmpty(searchParam.getKeywords())) {
            String keyword = searchParam.getKeywords();
            queryWrapper.and(i -> i.like("nm.nickname", keyword)
                    .or().like("nm.mobile", keyword)
                    .or().like("nm.member_no", keyword));
        }
        if (ObjectUtil.isNotEmpty(searchParam.getCreateTime())) {
            QueryMapperUtils.buildByTime(queryWrapper, "ngr.create_time", searchParam.getCreateTime());
        }

        IPage<NewcomerGiftRecordsListVo> iPage = newcomerGiftRecordsMapper.selectJoinPage(
                new Page<>(page, limit),
                NewcomerGiftRecordsListVo.class,
                queryWrapper
        );

        List<NewcomerGiftRecordsListVo> list = new ArrayList<>();
        for (NewcomerGiftRecordsListVo item : iPage.getRecords()) {
            if (ObjectUtil.isNotEmpty(item.getGifts())) {
                item.setGiftsContent(coreMemberService.getGiftContent(0, JSONUtil.parseObj(item.getGifts()), "admin"));
            } else {
                item.setGiftsContent(new JSONObject());
            }
            list.add(item);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);
    }

    @Override
    public Integer count() {
        return Math.toIntExact(newcomerGiftRecordsMapper.selectCount(new QueryWrapper<>()));
    }

    private NewcomerGiftConfigVo defaultConfig() {
        NewcomerGiftConfigVo vo = new NewcomerGiftConfigVo();
        vo.setIsOpen(0);
        vo.setGifts(new JSONObject());
        return vo;
    }
}
