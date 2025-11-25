

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
export function getGoodsSku(sku_id: number) {
    return request.get(`mall/goods/sku/${ sku_id }`)
}

/**
 *  收藏
 */
export function collect(goods_id: number) {
    return request.post(`mall/goods/collect/${ goods_id }`)
}

/**
 *  取消收藏
 */
export function cancelCollect(params: Record<string, any>) {
    return request.put(`mall/goods/collect`, params)
}

/**
 * 获取收藏列表
 */
export function getCollectList(params: Record<string, any>) {
    return request.get(`mall/goods/collect`, params)
}

/***************************************************** 评价 ****************************************************/
/**
 *  获取评价设置
 */
export function getEvaluateConfig() {
    return request.get(`mall/goods/evaluate/config`)
}

/**
 * 获取评价列表
 */
export function getEvaluateList(params: Record<string, any>) {
    return request.get(`mall/goods/evaluate`, params)
}

/**
 *  添加评论
 */
export function setEvaluate(params: Record<string, any>) {
    return request.post(`mall/goods/evaluate`, params)
}

/**
 * 获取评价统计
 */
export function getEvaluateCount(params: Record<string, any>) {
    return request.get(`mall/goods/evaluate/count`, params)
}

/**
 * 获取商品列表供组件调用
 */
export function getGoodsComponents(params: Record<string, any>) {
    return request.get(`mall/goods/components`, params)
}

/**
 * 获取推荐商品
 * @returns
 */
export function getRecommendGoods(params: Record<string, any>) {
    return request.get(`mall/goods/recommend/${ params.site_id }`, params)
}



/**
 * 获取商品详情二维码
 */
export function getGoodsQrcode(params: Record<string, any>) {
    return request.get(`mall/goods/detail/qrcode`, params)
}