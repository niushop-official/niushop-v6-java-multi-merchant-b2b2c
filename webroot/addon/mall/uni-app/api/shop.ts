import request from '@/utils/request'

/**
 * 获取评论设置
 */
export function getEvaluateConfig() {
    return request.get(`mall/goods/evaluate/config`)
}

/**
 * 获取店铺列表
 */
export function getShopList(params: Record<string, any>) {
    return request.get(`shop/web/shop`, params)
}

/**
 * 获取店铺列表(有商品)
 */
export function getShopWebList(params: Record<string, any>) {
    return request.get(`shop/web/shop_list`, params)
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
export function getShopInfo(id: number, params: any = null) {
    return request.get(`shop/web/shop/${ id }`, params)
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

/**
 * 获取店铺组件
 */
export function getShopComponents(params: Record<string, any>) {
    return request.get(`shop/web/shop/component`, params)
}