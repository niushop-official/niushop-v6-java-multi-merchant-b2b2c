package com.niu.mall.service.core.shop.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.site.Site;
import com.niu.core.entity.site.SiteGroup;
import com.niu.core.entity.stat.SysStat;
import com.niu.core.mapper.site.SiteGroupMapper;
import com.niu.core.mapper.site.SiteMapper;
import com.niu.core.mapper.stat.SysStatMapper;
import com.niu.core.service.admin.stat.ISiteStatService;
import com.niu.core.service.admin.stat.vo.StartChartVo;
import com.niu.core.service.core.sys.ICoreSysStatService;
import com.niu.core.service.core.sys.vo.SysStatVo;
import com.niu.core.entity.goods.MallGoods;
import com.niu.mall.entity.order.MallOrder;
import com.niu.core.enums.goods.GoodsStatusEnum;
import com.niu.core.mapper.goods.MallGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.vo.MallGoodsStatVo;
import com.niu.mall.service.core.shop.ICoreStatService;
import com.niu.mall.service.core.shop.param.AddStatParam;
import com.niu.core.common.utils.StringUtils;
import com.niu.core.service.core.sys.param.SysDataDecParam;
import com.niu.mall.service.core.shop.param.DelStatParam;
import com.niu.mall.service.core.shop.vo.StatDataVo;
import com.niu.mall.service.core.shop.vo.StatVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CoreStatServiceImpl implements ICoreStatService {

    @Resource
    MallGoodsMapper mallGoodsMapper;
    @Resource
    MallOrderMapper mallOrderMapper;
    @Resource
    ICoreSysStatService coreSysStatService;
    @Resource
    ICoreGoodsStatService coreGoodsStatService;
    @Resource
    SysStatMapper sysStatMapper;
    @Resource
    SiteMapper siteMapper;
    @Resource
    SiteGroupMapper siteGroupMapper;
    @Resource
    ISiteStatService siteStatService;


    /**
     * 添加统计数据
     *
     * @param siteId
     * @param data
     */
    @Override
    public void addStat(Integer siteId, AddStatParam data) {
        SysStat model = sysStatMapper.selectOne(new QueryWrapper<SysStat>().eq("date", DateUtil.today()).eq("site_id", siteId));
        if (model == null) {
            SysStat stat = new SysStat();
            BeanUtil.copyProperties(data, stat);
            stat.setSiteId(siteId);
            stat.setDate(DateUtil.today());
            stat.setMonth(DateUtil.format(new DateTime(stat.getDate()), "yyyy-MM"));
            stat.setDateTime(new DateTime(stat.getDate()).getTime() / 1000);
            sysStatMapper.insert(stat);
        } else {
            if (ObjectUtil.isNotEmpty(data.getOrderNum())) model.setOrderNum(model.getOrderNum() + data.getOrderNum());
            if (ObjectUtil.isNotEmpty(data.getSaleMoney()))
                model.setSaleMoney(model.getSaleMoney().add(data.getSaleMoney()));
            if (ObjectUtil.isNotEmpty(data.getRefundMoney()))
                model.setRefundMoney(model.getRefundMoney().add(data.getRefundMoney()));
            if (ObjectUtil.isNotEmpty(data.getAccessSum()))
                model.setAccessSum(model.getAccessSum() + data.getAccessSum());
            sysStatMapper.updateById(model);
        }
    }

    @Override
    public void delStat(Integer siteId, DelStatParam data) {
        if (ObjectUtil.isEmpty(data) || StringUtils.isEmpty(data.getDate())) {
            return;
        }
        SysDataDecParam decParam = new SysDataDecParam();
        decParam.setSiteId(siteId);
        decParam.setTime(DateUtils.StringToTimestamp(data.getDate()));
        decParam.setOrderNum(data.getOrderNum());
        coreSysStatService.decStat(decParam);
    }

    /**
     * 查询统计总和
     *
     * @param siteId
     * @return
     */
    @Override
    public StatVo getStat(Integer siteId) {
        StatVo vo = new StatVo();
        Long orderCount = mallOrderMapper.selectCount(new QueryWrapper<MallOrder>().eq("site_id", siteId).eq("delete_time", 0));
        vo.setOrderNum(orderCount.intValue());
        vo.setAddonNum(getAddonCount());//应用总数
        LambdaQueryWrapper<MallGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MallGoods::getStatus, GoodsStatusEnum.ONLINE.getStatus(), GoodsStatusEnum.OFFLINE.getStatus());
        queryWrapper.eq(MallGoods::getSiteId, siteId);
        Long goodsCount = mallGoodsMapper.selectCount(queryWrapper);
        vo.setGoodsNum(goodsCount.intValue());//商品总数
        return vo;
    }

    /**
     * 获取时间区间内天统计数据
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatVo getStatDate(Integer siteId, Long startTime, Long endtTime) {

        String start = DateUtils.timestampToString(startTime);
        String end = DateUtils.timestampToString(endtTime);
        SysStatVo sysStatVo = coreSysStatService.getStat(siteId, start, end);
        MallGoodsStatVo goodsStatVo = coreGoodsStatService.getGoodsStat(siteId, start, end);
        StatVo vo = new StatVo();
        vo.setSaleMoney(sysStatVo.getSaleMoney());
        vo.setRefundMoney(sysStatVo.getRefundMoney());
        vo.setOrderNum(sysStatVo.getOrderNum());
        vo.setRefundNum(goodsStatVo.getRefundNum());
        vo.setCartNum(goodsStatVo.getCartNum());
        vo.setSaleNum(goodsStatVo.getSaleNum());
        vo.setPayNum(goodsStatVo.getPayNum());
        vo.setAccessNum(goodsStatVo.getAccessNum());
        return vo;

    }

    /**
     * 获取时间区间内天统计数据
     *
     * @param siteId
     * @param startTime
     * @param endtTime
     * @return
     */
    @Override
    public StatDataVo getStatData(Integer siteId, String startTime, String endtTime) {
        StatDataVo vo = new StatDataVo();
        StartChartVo sysDataVo = siteStatService.getStatData(siteId, startTime, endtTime);
        BeanUtils.copyProperties(sysDataVo, vo);
        return vo;
    }

    public Integer getAddonCount() {
        int addonCount = 0;
        Site siteInfo = siteMapper.selectById(RequestUtils.siteId());
        if (siteInfo != null) {
            //查询分组
            SiteGroup siteGroup = siteGroupMapper.selectById(siteInfo.getGroupId());
            if (siteGroup != null && siteGroup.getAddon() != null) {
                try {
                    JSONArray addonArray = JSONUtil.parseArray(siteGroup.getAddon());
                    addonCount = addonArray.size();
                } catch (Exception e) {
                    log.error("站点获取插件数量失败：{}", e.getMessage(), e);
                }
            }
        }
        return addonCount;
    }

}
