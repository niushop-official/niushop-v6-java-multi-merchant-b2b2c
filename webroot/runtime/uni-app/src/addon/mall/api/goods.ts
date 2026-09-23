import request from '@/utils/request'



/**
 * 获取商品列表
 */
export function getGoodsPages(params: Record<string, any>) {
    return request.get(`mall/goods/pages`, params)
}
/**
 * 获取商品优惠劵可用列表
 */
export function getGoodsCouponPages(params: Record<string, any>) {
    return request.get(`mall/goods_coupon/pages`, params)
}

/**
 * 获取商品列表
 */
export function getShopGoodsPages(params: Record<string, any>) {
    return request.get(`mall/goods_shop/pages`, params)
}

/**
 * 获取商品详情
 */
export function getGoodsDetail(params: Record<string, any>) {
    return request.get(`mall/goods/detail`, params)
}

/**
 * 获取商品规格
 */
export function getGoodsSku(sku_id: any) {
    return request.get(`mall/goods/sku/${ sku_id }`)
}

/**
 *  收藏
 */
export function collect(goods_id: any) {
    return request.post(`mall/goods/collect/${ goods_id }`)
}

/**
 *  取消收藏
 */
export function cancelCollect(params: Record<string, any>) {
    return request.put(`mall/goods/collect`, params, { showSuccessMessage: true })
}

/**
 * 获取收藏列表
 */
export function getCollectList(params: Record<string, any>) {
    return request.get(`mall/goods/collect`, params)
}

/**
 * 获取评价
 */
export function getEvaluateList(goods_id: any) {
    return request.get(`mall/goods/evaluate/list`, { goods_id })
}

/**
 * 获取商品列表供组件调用
 */
export function getGoodsComponents(params: Record<string, any>) {
    return request.get(`mall/goods/components`, params)
}

/**
 * 添加浏览记录
 */
export function addGoodsBrowse(params: Record<string, any>) {
    return request.post(`mall/goods/browse`, params)
}
/**
 * 商品足迹列表
 */
export function getBrowse(params: Record<string, any>) {
    return request.get('mall/goods/browse', params)
}
/**
 * 商品足迹删除
 */
export function delBrowse(params: Record<string, any>) {
    return request.delete(`mall/goods/browse`, params)
}

/**
 *  获取商品搜索配置
 */
export function getGoodsConfigSearch() {
    return request.get(`mall/goods/config/search`)
}

/**
 *  获取装修推荐商品
 */
export function getShopRecommendComponents(params: Record<string, any>) {
    return request.get(`mall/goods/shop_recommend/components`, params)
}

/**
 *  获取商品满减送信息
 */
export function getShopMarketingList(params: Record<string, any>) {
    return request.post(`mall/marketing/list`, params)
}

/**
 *  获取商品满减送信息详情
 */
export function getShopMarketingInfo(params: Record<string, any>) {
    return request.get(`mall/marketing/detail`, params)
}

export function getMarketingGoodsList(type: string, id: number,params: Record<string, any>) {
    return request.get(`mall/marketing/${type}/${id}/goods`, params)
}

/**
 *  查询优惠信息列表
 */
export function getGoodsSkuPromotionList(params: Record<string, any>) {
    return request.get(`mall/goods/sku_promotion/list`, params)
}

/**
 *  商品优惠信息详情
 */
export function getGoodsSkuPromotionDetail(params: Record<string, any>) {
    return request.get(`mall/goods/sku_promotion/detail`, params)
}

/**
 *  商品优惠信息详情
 */
export function getGoodsRank(params: Record<string, any>) {
    return request.get(`mall/rank/goods_rank`, params)
}