package com.niu.mall.service.api.goods.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.entity.member.Member;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.mapper.member.MemberMapper;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.goods.MallGoodsEvaluate;
import com.niu.mall.entity.order.MallOrder;
import com.niu.mall.entity.order.MallOrderGoods;
import com.niu.mall.entity.order.config.MallOrderEvaluate;
import com.niu.mall.enums.goods.GoodsEvaluateEnum;
import com.niu.mall.mapper.goods.MallGoodsEvaluateMapper;
import com.niu.mall.mapper.order.MallOrderGoodsMapper;
import com.niu.mall.mapper.order.MallOrderMapper;
import com.niu.mall.service.api.goods.IGoodsEvaluateService;
import com.niu.mall.service.api.goods.param.MallGoodsEvaluateParam;
import com.niu.mall.service.api.goods.param.MallGoodsEvaluateSearchParam;
import com.niu.mall.service.api.goods.vo.*;
import com.niu.mall.service.core.goods.ICoreGoodsEvaluateService;
import com.niu.mall.service.core.order.ICoreOrderConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.*;

@Service
public class GoodsEvaluateServiceImpl implements IGoodsEvaluateService {

    @Resource
    MallGoodsEvaluateMapper mallGoodsEvaluateMapper;

    @Resource
    MallOrderGoodsMapper mallOrderGoodsMapper;

    @Resource
    ICoreGoodsEvaluateService coreGoodsEvaluateService;

    @Resource
    MemberMapper memberMapper;

    @Resource
    MallOrderMapper mallOrderMapper;

    @Resource
    ICoreOrderConfigService coreOrderConfigService;

    private ICoreUploadService coreUploadService() {
        return SpringContext.bean(ICoreUploadService.class);
    }


    @Override
    public PageResult<MallGoodsEvaluateListVo> getPages(PageParam pageParam, MallGoodsEvaluateSearchParam searchParam) {
        Integer page  = pageParam.getPage();
        Integer limit =pageParam.getLimit();

        QueryWrapper<MallGoodsEvaluate> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus()));
        if(ObjectUtil.isNotEmpty(searchParam.getGoodsId())) queryWrapper.eq("goods_id", searchParam.getGoodsId());
        if(ObjectUtil.isNotEmpty(searchParam.getScores())) queryWrapper.in("scores", searchParam.getScores());
        queryWrapper.orderByDesc("topping");
        queryWrapper.orderByDesc("update_time");
        queryWrapper.orderByDesc("create_time");

        IPage<MallGoodsEvaluate> iPage = mallGoodsEvaluateMapper.selectPage(new Page<>(page, limit), queryWrapper);
        List<MallGoodsEvaluateListVo> list = new LinkedList<>();

        for (MallGoodsEvaluate item : iPage.getRecords()) {
            MallGoodsEvaluateListVo vo = new MallGoodsEvaluateListVo();
            BeanUtils.copyProperties(item, vo);
            String[] images = JSONUtil.parseArray(item.getImages()).stream()
                    .map(Object::toString)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            vo.setImages(images);
            if(ObjectUtil.isNotEmpty(vo.getImages()) && vo.getImages().length > 0)
            {
                List<String> listImageMid = new ArrayList<>();
                for (String image : vo.getImages()) {
                    listImageMid.add(coreUploadService().thumb(item.getSiteId(), image, UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
                }
                vo.setImageMid(listImageMid);
            }

            list.add(vo);
        }
        return PageResult.build(page, limit, iPage.getTotal()).setData(list);

    }

    @Override
    public MallGoodsEvaluateInfoVo getInfo(Integer id) {
        MallGoodsEvaluate model = mallGoodsEvaluateMapper.selectOne(
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallGoodsEvaluateInfoVo vo = new MallGoodsEvaluateInfoVo();
        BeanUtils.copyProperties(model, vo);
        // 过滤掉空字符串，避免前端显示图片加载失败
        String[] images = JSONUtil.parseArray(model.getImages()).stream()
                .map(Object::toString)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        vo.setImages(images);
        if(ObjectUtil.isNotEmpty(vo.getImages()) && vo.getImages().length > 0)
        {
            List<String> listImageMid = new ArrayList<>();
            List<String> listImageBig = new ArrayList<>();
            for (String image : vo.getImages()) {
                listImageMid.add(coreUploadService().thumb(vo.getSiteId(), image, UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
                listImageBig.add(coreUploadService().thumb(vo.getSiteId(), image, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType()));
            }
            vo.setImageMid(listImageMid);
            vo.setImageBig(listImageBig);
        }

        return vo;
    }

    @Override
    public void add(MallGoodsEvaluateParam mallGoodsEvaluateParam) {
        Member member = memberMapper.selectOne(new QueryWrapper<Member>().eq("member_id", RequestUtils.memberId()).select("nickname, headimg"));

        MallOrderEvaluate config = coreOrderConfigService.getEvaluateConfig();

        for (MallGoodsEvaluateParam.Evaluate item : mallGoodsEvaluateParam.getEvaluateArray()) {
            MallGoodsEvaluate evaluate = new MallGoodsEvaluate();
            BeanUtils.copyProperties(item, evaluate);

            //查询订单站点id
            MallOrder order = mallOrderMapper.selectOne(new QueryWrapper<MallOrder>().eq("order_id", item.getOrderId()).select("site_id"));
            evaluate.setSiteId(order.getSiteId());
            evaluate.setMemberId(RequestUtils.memberId());
            evaluate.setMemberHead(member.getHeadimg());
            evaluate.setMemberName(member.getNickname());
            evaluate.setIsAudit(config.getEvaluateIsToExamine());
            evaluate.setImages(JSONUtil.parseArray(item.getImages()).toString());
            evaluate.setCreateTime(System.currentTimeMillis() / 1000);
            coreGoodsEvaluateService.addEvaluate(evaluate);
        }
    }

    @Override
    public Map<String, Long> getCount(Integer goodsId) {

        Long goodsEvaluate = mallGoodsEvaluateMapper.selectCount(new QueryWrapper<MallGoodsEvaluate>().eq("goods_id", goodsId).in("scores", Arrays.asList(4, 5)).in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus())));
        Long centreEvaluate = mallGoodsEvaluateMapper.selectCount(new QueryWrapper<MallGoodsEvaluate>().eq("goods_id", goodsId).in("scores", Arrays.asList(2, 3)).in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus())));
        Long wantingCentreEvaluate = mallGoodsEvaluateMapper.selectCount(new QueryWrapper<MallGoodsEvaluate>().eq("goods_id", goodsId).in("scores", Arrays.asList(0, 1)).in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus())));
        Map<String, Long> map = new HashMap<>();
        map.put("good_evaluate", goodsEvaluate);
        map.put("centre_evaluate", centreEvaluate);
        map.put("wanting_centre_evaluate", wantingCentreEvaluate);
        return map;
    }

    @Override
    public MallGoodsEvaluateInGoodsVo getList(Integer goodsId) {

        QueryWrapper<MallGoodsEvaluate> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("is_audit", Arrays.asList(GoodsEvaluateEnum.AUDIT_NO.getStatus(), GoodsEvaluateEnum.AUDIT_ADOPT.getStatus()));
        queryWrapper.eq("goods_id", goodsId);

        Long count = mallGoodsEvaluateMapper.selectCount(queryWrapper);

        queryWrapper.orderByDesc("topping");
        queryWrapper.orderByDesc("update_time");
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 3");

        List<MallGoodsEvaluate> list = mallGoodsEvaluateMapper.selectList(queryWrapper);

        MallGoodsEvaluateInGoodsVo listVo = new MallGoodsEvaluateInGoodsVo();
        List<MallGoodsEvaluateListVo> voList = new ArrayList<>();
        for (MallGoodsEvaluate item : list) {

            MallGoodsEvaluateListVo vo = new MallGoodsEvaluateListVo();
            BeanUtils.copyProperties(item, vo);
            String[] images = JSONUtil.parseArray(item.getImages()).stream()
                    .map(Object::toString)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            vo.setImages(images);
            if(ObjectUtil.isNotEmpty(vo.getImages()) && vo.getImages().length > 0)
            {
                List<String> listImageMid = new ArrayList<>();
                for (String image : vo.getImages()) {
                    listImageMid.add(coreUploadService().thumb(item.getSiteId(), image, UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
                }
                vo.setImageMid(listImageMid);
            }
            voList.add(vo);
        }

        listVo.setList(voList);
        listVo.setCount(count);
        return listVo;
    }

    @Override
    public List<MallGoodsEvaluateDetailVo> getDetail(Integer orderId) {

        QueryWrapper<MallGoodsEvaluate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id", orderId);

        List<MallGoodsEvaluate> list = mallGoodsEvaluateMapper.selectList(queryWrapper);

        List<MallGoodsEvaluateDetailVo> voList = new ArrayList<>();
        for (MallGoodsEvaluate item : list) {

            MallGoodsEvaluateDetailVo vo = new MallGoodsEvaluateDetailVo();
            BeanUtils.copyProperties(item, vo);
            // 过滤掉空字符串，避免前端显示图片加载失败
            String[] images = JSONUtil.parseArray(item.getImages()).stream()
                    .map(Object::toString)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
            vo.setImages(images);
            if(ObjectUtil.isNotEmpty(vo.getImages()) && vo.getImages().length > 0)
            {
                List<String> listImageMid = new ArrayList<>();
                List<String> listImageBig = new ArrayList<>();
                for (String image : vo.getImages()) {
                    listImageMid.add(coreUploadService().thumb(vo.getSiteId(), image, UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
                    listImageBig.add(coreUploadService().thumb(vo.getSiteId(), image, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType()));
                }
                vo.setImageMid(listImageMid);
                vo.setImageBig(listImageBig);
            }
            MallOrderGoods mallOrderGoods = mallOrderGoodsMapper.selectOne(new QueryWrapper<MallOrderGoods>().eq("order_goods_id", item.getOrderGoodsId()));
            if(ObjectUtil.isNotEmpty(mallOrderGoods)) {
                MallGoodsEvaluateDetailVo.OrderGoods orderGoods = new MallGoodsEvaluateDetailVo.OrderGoods();
                BeanUtils.copyProperties(mallOrderGoods, orderGoods);
                vo.setOrderGoods(orderGoods);
            }
            voList.add(vo);
        }

        return voList;
    }
}
