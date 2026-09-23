import request from '@/utils/request'

/**
 * 获取商品分类树结构
 * @returns
 */
export function getCategoryTree() {
    return request.get(`basic/site/goods/tree`)
}

/**
 * 获取商品品牌列表
 * @param params
 * @returns
 */
export function getBrandList(params: Record<string, any>) {
    return request.get(`basic/site/goods/brand/list`, params)
}

/**
 * 获取商品服务列表
 * @param params
 * @returns
 */
export function getServeList(params: Record<string, any>) {
    return request.get(`basic/site/goods/service/list`, params)
}


/**
 * 切换营业状态
 */
export function updateBusinessStatus(params: Record<string, any>) {
    return request.put(`site/business_status`, params, { showSuccessMessage: true })
}


/**
 * 获取保障说明列表
 * @param params
 * @returns
 */
export function getGuaranteeList(params: Record<string, any>) {
    // 使用新的接口获取与 O2O 相关的商品服务
    return request.get('basic/site/goods/service/addon/o2o')
}