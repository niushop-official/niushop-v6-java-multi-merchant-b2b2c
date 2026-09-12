package com.niu.core.event.fenxiao;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.common.component.context.event.EventResult;
import com.niu.core.common.component.context.listener.CallbackListener;
import com.niu.core.common.domain.PageResult;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsDetailVo;
import com.niu.core.event.fenxiao.vo.FenxiaoGoodsSelectVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 获取分销商品列表
 * 对应 PHP: FenxiaoGoodsList
 */
public abstract class FenxiaoGoodsListEventDefiner extends CallbackListener<FenxiaoGoodsListEventDefiner.FenxiaoGoodsListEvent> {

    @Override
    public abstract FenxiaoGoodsListEventResult handleCallback(FenxiaoGoodsListEvent event);

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoGoodsListEvent extends Event {

        /**
         * 业务类型
         */
        private String tradeType;

        /**
         * 商品名称
         */
        private String goodsName;

        /**
         * 指定商品ID列表
         */
        private List<Integer> goodsIds;

        /**
         * 排除的商品ID列表
         */
        private List<Integer> excludeGoodsIds;

        /**
         * 商品ID（详情查询用）
         */
        private Integer goodsId;

        /**
         * 1=获取商品详情(sku_list)
         */
        private Integer isInfo;

        /**
         * 分页页码
         */
        private Integer page;

        /**
         * 每页条数
         */
        private Integer limit;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FenxiaoGoodsListEventResult extends EventResult {

        /**
         * 商品分页列表
         */
        private PageResult<FenxiaoGoodsSelectVo> pageResult;

        /**
         * 商品详情
         */
        private FenxiaoGoodsDetailVo goodsInfo;
    }
}
