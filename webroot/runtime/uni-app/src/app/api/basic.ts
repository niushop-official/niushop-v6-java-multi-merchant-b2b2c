import request from '@/utils/request'
/**
 * 获取商品分类模板配置
 */
export function getGoodsCategoryConfig() {
    return request.get(`basic/goods/category/config`)
}

/**
 * 获取商品分类树结构
 */
export function getGoodsCategoryTree(params: Record<string, any>) {
    return request.get(`basic/goods/category/tree`, params)
}

/**
 * 获取商品分类列表
 */
export function getGoodsCategoryList(params: Record<string, any>) {
    return request.get(`basic/goods/category/list`, params)
}


/**
 * 查询同城配送预约配置
 */
export function getLocal(params: Record<string, any>) {
    return request.get('basic/delivery/local', params)
}

/**
 * 查询同城配送配送员轨迹
 */
export function getDeliveryTrack(params: Record<string, any>) {
    return request.get('basic/delivery/track', params)
}

/**
 * 查询配送方式
 */
export function getDeliveryType() {
    return request.get('basic/delivery/delivery_type')
}

/**
 * 查询距离
 */
export function getDeliveryConfig() {
    return request.get('basic/delivery/config')
}

/**
 * 查询物流信息
 */
export function getDeliveryPackage(params: Record<string, any>) {
    return request.get(`delivery/delivery_package/${params.trade_type}`, params)
}

/**
 * 查询自提点
 */
export function getDeliveryStoreList(params: Record<string, any>) {
    return request.get('delivery/delivery_store/list', params)
}