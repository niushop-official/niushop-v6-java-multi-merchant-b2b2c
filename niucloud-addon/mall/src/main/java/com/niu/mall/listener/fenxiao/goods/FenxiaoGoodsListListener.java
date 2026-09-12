package com.niu.mall.listener.fenxiao.goods;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.annotation.EventCallback;
import com.niu.core.enums.fenxiao.FenxiaoTradeTypeEnum;
import com.niu.core.event.fenxiao.FenxiaoGoodsListEventDefiner;
import com.niu.core.event.fenxiao.param.FenxiaoGoodsListParam;
import com.niu.mall.service.core.fenxiao.IMallCoreFenxiaoGoodsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * 提供商城分销可选商品列表
 */
@Component
@EventCallback("mall")
public class FenxiaoGoodsListListener extends FenxiaoGoodsListEventDefiner {

    @Resource
    IMallCoreFenxiaoGoodsService mallCoreFenxiaoGoodsService;

    /**
     * 按 trade_type 返回商城商品列表或详情
     *
     * @param event 商品列表事件
     * @return FenxiaoGoodsListEventResult
     */
    @Override
    public FenxiaoGoodsListEventResult handleCallback(FenxiaoGoodsListEvent event) {
        FenxiaoGoodsListEventResult result = new FenxiaoGoodsListEventResult();
        // 非商城业务类型，不处理
        if (!FenxiaoTradeTypeEnum.MALL.getType().equals(event.getTradeType())) {
            return result;
        }
        FenxiaoGoodsListParam param = buildParam(event);
        // 详情模式：返回规格列表
        if (ObjectUtil.isNotEmpty(event.getIsInfo()) && event.getIsInfo() == 1) {
            result.setGoodsInfo(mallCoreFenxiaoGoodsService.getGoodsInfo(param));
        } else {
            // 列表模式：返回分页可选商品
            result.setPageResult(mallCoreFenxiaoGoodsService.getGoodsList(param));
        }
        return result;
    }

    /**
     * 转换事件参数为商品查询参数
     *
     * @param event 事件参数
     * @return FenxiaoGoodsListParam
     */
    private FenxiaoGoodsListParam buildParam(FenxiaoGoodsListEvent event) {
        FenxiaoGoodsListParam param = new FenxiaoGoodsListParam();
        param.setSiteId(event.getSiteId());
        param.setGoodsName(event.getGoodsName());
        param.setGoodsIds(event.getGoodsIds());
        param.setExcludeGoodsIds(event.getExcludeGoodsIds());
        param.setGoodsId(event.getGoodsId());
        param.setPage(event.getPage());
        param.setLimit(event.getLimit());
        return param;
    }
}
