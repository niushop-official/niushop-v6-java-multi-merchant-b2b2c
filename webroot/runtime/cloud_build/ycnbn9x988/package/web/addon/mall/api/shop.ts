/***************************************************** 店铺管理 ****************************************************/
/**
 * 获取店铺列表
 */
export function getShopList(params: Record<string, any>) {
    return request.get(`shop/web/shop`, params)
}

/**
 * 获取店铺分类
 */
export function getShopCategory() {
    return request.get(`shop/web/shop_category`)
}

/**
 * 获取店铺详情
 */
export function getShopInfo(id: number) {
    return request.get(`shop/web/shop/${ id }`)
}

/**
 * 收藏取消店铺
 * @param params
 */
export function editShopCollect(params: Record<string, any>) {
    return request.put(`shop/web/shop/${ params.site_id }/${ params.is_follow }`)
}

/**
 * 获取店铺收藏列表
 */
export function getShopFollowList(params: Record<string, any>) {
    return request.get(`shop/web/follow`, params)
}

