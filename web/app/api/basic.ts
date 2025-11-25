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