/**
 * 获取商品分类模板配置
 */
export function getGoodsCategoryConfig() {
    return request.get(`basic/goods/category/config`)
}


/**
 * 获取店铺商品分类
 */
export function getStoreCategory(params: Record<string, any>) {
    return request.get(`basic/goods/category/tree`, params)
}


/**
 * 收藏取消店铺
 * @param params
 */
export function editShopCollect(params: Record<string, any>) {
    return request.put(`shop/web/shop/${ params.site_id }/${ params.is_follow }/${ params.addon }`)
}

/**
 * 获取店铺收藏列表
 */
export function getShopFollowList(params: Record<string, any>) {
    return request.get(`shop/web/follow`, params)
}
