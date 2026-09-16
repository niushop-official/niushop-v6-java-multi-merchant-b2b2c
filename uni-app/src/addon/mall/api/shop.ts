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
    return request.post(`mall/shop_list`, params)
}

/**
 * 获取店铺详情
 */
export function getShopInfo(id: number, params: any = null) {
    return request.get(`mall/shop/${ id }`, params)
}

/**
 * 获取店铺组件
 */
export function getShopComponents(params: Record<string, any>) {
    return request.get(`mall/shop/component`, params)
}