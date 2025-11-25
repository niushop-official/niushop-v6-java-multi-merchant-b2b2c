package com.niu.mall.service.site.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.niu.core.common.config.GlobalConfig;
import com.niu.core.common.domain.PageParam;
import com.niu.core.common.domain.PageResult;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.enums.upload.UploadThumbTypeEnum;
import com.niu.core.service.core.sys.ICoreUploadService;
import com.niu.mall.entity.goods.MallGoodsEvaluate;
import com.niu.mall.enums.goods.GoodsEvaluateEnum;
import com.niu.mall.mapper.goods.MallGoodsEvaluateMapper;
import com.niu.mall.service.site.goods.IMallGoodsEvaluateService;
import com.niu.mall.service.site.goods.param.MallGoodsEvaluateParam;
import com.niu.mall.service.site.goods.param.MallGoodsEvaluateSearchParam;
import com.niu.mall.service.site.goods.vo.MallGoodsEvaluateInfoVo;
import com.niu.mall.service.site.goods.vo.MallGoodsEvaluateListVo;
import com.niu.mall.service.core.goods.ICoreGoodsStatService;
import com.niu.mall.service.core.goods.param.GoodsAddStatParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 商品评价实现
 */
@Service
public class MallGoodsEvaluateServiceImpl implements IMallGoodsEvaluateService {

    @Resource
    ICoreGoodsStatService coreGoodsStatService;

    @Resource
    MallGoodsEvaluateMapper mallGoodsEvaluateMapper;

    @Resource
    ICoreUploadService coreUploadService;

    /**
     * 商品评价列表
     *
     * @param pageParam   分页参数
     * @param searchParam 搜索参数
     * @return PageResult<ShopGoodsEvaluateListVo>
     */
    @Override
    public PageResult<MallGoodsEvaluateListVo> list(PageParam pageParam, MallGoodsEvaluateSearchParam searchParam) {

        Integer page = pageParam.getPage();
        Integer limit = pageParam.getLimit();

        MPJQueryWrapper<MallGoodsEvaluate> queryWrapper = new MPJQueryWrapper<>();

        queryWrapper.setAlias("ge")
                .leftJoin("?_mall_goods ng ON ge.goods_id = ng.goods_id".replace("?_", GlobalConfig.tablePrefix));
        queryWrapper.select("ge.evaluate_id,ge.site_id,ge.order_id,ge.order_goods_id,ge.goods_id,ge.member_id,ge.content,ge.images,ge.is_anonymous,ge.scores,ge.is_audit,ge.explain_first,ge.create_time,ge.topping,ge.quality_score,ge.service_score,ge.logistics_score,ng.goods_name, ng.goods_cover");
        if (ObjectUtil.isNotEmpty(searchParam.getGoodsName()))
            queryWrapper.like("ng.goods_name", searchParam.getGoodsName());
        queryWrapper.eq("ge.site_id", RequestUtils.siteId());
        queryWrapper.orderByDesc("ge.create_time");

        IPage<MallGoodsEvaluateListVo> iPage = mallGoodsEvaluateMapper.selectJoinPage(new Page<>(page, limit), MallGoodsEvaluateListVo.class, queryWrapper);

        List<Integer> evaluateIds = new ArrayList<>();
        for (MallGoodsEvaluateListVo vo : iPage.getRecords()) {
            evaluateIds.add(vo.getEvaluateId());
        }
        Map<Integer, String> imagesMap = new HashMap<>();
        if (!evaluateIds.isEmpty()) {
            List<MallGoodsEvaluate> evaluateList = mallGoodsEvaluateMapper.selectList(
                    new QueryWrapper<MallGoodsEvaluate>()
                            .in("evaluate_id", evaluateIds)
                            .select("evaluate_id", "images"));
            for (MallGoodsEvaluate evaluate : evaluateList) {
                imagesMap.put(evaluate.getEvaluateId(), evaluate.getImages());
            }
        }

        for (MallGoodsEvaluateListVo item : iPage.getRecords()) {
            String imagesStr = imagesMap.get(item.getEvaluateId());
            if (ObjectUtil.isNotEmpty(imagesStr)) {
                List<String> imageList = JSONUtil.parseArray(imagesStr).toList(String.class);
                List<String> listImageSmall = new ArrayList<>();
                List<String> listImageBig = new ArrayList<>();

                for (String image : imageList) {
                    listImageSmall.add(coreUploadService.thumb(item.getSiteId(), image, UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
                    listImageBig.add(coreUploadService.thumb(item.getSiteId(), image, UploadThumbTypeEnum.BIG.getType()).getDataMap().get(UploadThumbTypeEnum.BIG.getType()));
                }
                item.setImageSmall(listImageSmall);
                item.setImageBig(listImageBig);
                item.setImages(imageList);
            } else {
                item.setImages(new ArrayList<>());
            }

            if (ObjectUtil.isNotEmpty(item.getGoodsCover())) {
                item.setGoodsCoverThumbSmall(coreUploadService.thumb(item.getSiteId(), item.getGoodsCover(), UploadThumbTypeEnum.SMALL.getType()).getDataMap().get(UploadThumbTypeEnum.SMALL.getType()));
                item.setGoodsCoverThumbMid(coreUploadService.thumb(item.getSiteId(), item.getGoodsCover(), UploadThumbTypeEnum.MID.getType()).getDataMap().get(UploadThumbTypeEnum.MID.getType()));
            }

            MallGoodsEvaluateListVo.Goods goods = new MallGoodsEvaluateListVo.Goods();
            BeanUtil.copyProperties(item, goods);
            item.setGoods(goods);
        }
        return PageResult.build(iPage);
    }


    /**
     * 商品评价详情
     *
     * @param id 主键
     * @return ShopGoodsEvaluateInfoVo
     */
    @Override
    public MallGoodsEvaluateInfoVo info(Integer id) {

        MallGoodsEvaluate model = mallGoodsEvaluateMapper.selectOne(
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在");

        MallGoodsEvaluateInfoVo vo = new MallGoodsEvaluateInfoVo();
        BeanUtils.copyProperties(model, vo);
        return vo;
    }

    /**
     * 商品评价添加
     *
     * @param addParam 参数
     */
    @Override
    public void add(MallGoodsEvaluateParam addParam) {

        MallGoodsEvaluate model = new MallGoodsEvaluate();

        model.setSiteId(RequestUtils.siteId());
        model.setGoodsId(addParam.getGoodsId());
        model.setMemberHead(addParam.getMemberHead());
        model.setMemberName(addParam.getMemberName());
        model.setContent(addParam.getContent());
        model.setImages(JSONUtil.parseArray(addParam.getImages()).toString());
        model.setIsAnonymous(addParam.getIsAnonymous());
        model.setScores(addParam.getScores());
        model.setIsAudit(GoodsEvaluateEnum.AUDIT_NO.getStatus());
        model.setCreateTime(DateUtils.StringToTimestamp(addParam.getCreateTime()));
        mallGoodsEvaluateMapper.insert(model);
    }

    /**
     * 商品评价编辑
     *
     * @param editParam 参数
     */
    @Override
    public void edit(Integer id, MallGoodsEvaluateParam editParam) {

        MallGoodsEvaluate model = mallGoodsEvaluateMapper.selectOne(
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");
        model.setEvaluateId(id);
        mallGoodsEvaluateMapper.updateById(model);
    }

    /**
     * 商品评价删除
     *
     * @param id 主键ID
     */
    @Override
    public void del(Integer id) {
        MallGoodsEvaluate model = mallGoodsEvaluateMapper.selectOne(
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId())
                        .last("limit 1"));

        Assert.notNull(model, "数据不存在!");

        mallGoodsEvaluateMapper.delete(new QueryWrapper<MallGoodsEvaluate>().eq("evaluate_id", id));

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(model.getSiteId());
        param.setGoodsId(model.getGoodsId());
        param.setEvaluateNum(-1);
        coreGoodsStatService.addStat(param);
    }

    @Override
    public void auditAdopt(Integer id) {
        MallGoodsEvaluate mallGoodsEvaluate = new MallGoodsEvaluate();
        mallGoodsEvaluate.setIsAudit(GoodsEvaluateEnum.AUDIT_ADOPT.getStatus());
        mallGoodsEvaluateMapper.update(mallGoodsEvaluate,
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));

        MallGoodsEvaluate record = mallGoodsEvaluateMapper.selectById(id);

        GoodsAddStatParam param = new GoodsAddStatParam();
        param.setSiteId(record.getSiteId());
        param.setGoodsId(record.getGoodsId());
        param.setEvaluateNum(1);
        coreGoodsStatService.addStat(param);
    }

    @Override
    public void auditRefuse(Integer id) {

        MallGoodsEvaluate mallGoodsEvaluate = new MallGoodsEvaluate();
        mallGoodsEvaluate.setIsAudit(GoodsEvaluateEnum.AUDIT_REFUSE.getStatus());
        mallGoodsEvaluateMapper.update(mallGoodsEvaluate,
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void reply(Integer id, String explainFirst) {

        MallGoodsEvaluate mallGoodsEvaluate = new MallGoodsEvaluate();
        mallGoodsEvaluate.setExplainFirst(explainFirst);
        mallGoodsEvaluateMapper.update(mallGoodsEvaluate,
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void topping(Integer id) {

        MallGoodsEvaluate mallGoodsEvaluate = new MallGoodsEvaluate();
        mallGoodsEvaluate.setTopping(1);
        mallGoodsEvaluate.setUpdateTime(System.currentTimeMillis() / 1000);
        mallGoodsEvaluateMapper.update(mallGoodsEvaluate,
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

    @Override
    public void cancelTopping(Integer id) {

        MallGoodsEvaluate mallGoodsEvaluate = new MallGoodsEvaluate();
        mallGoodsEvaluate.setTopping(0);
        mallGoodsEvaluate.setUpdateTime(System.currentTimeMillis() / 1000);
        mallGoodsEvaluateMapper.update(mallGoodsEvaluate,
                new QueryWrapper<MallGoodsEvaluate>()
                        .eq("evaluate_id", id)
                        .eq("site_id", RequestUtils.siteId()));
    }

}
