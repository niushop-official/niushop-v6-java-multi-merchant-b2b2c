import request from '@/utils/request'

/***************************************************** 订单列表 ****************************************************/
/**
 * 获取订单设置
 */
export function getMallOrderConfig() {
    return request.get(`mall/order/config`)
}

/**
 * 获取订单状态列表
 */
export function getMallOrderStatus() {
    return request.get(`mall/order/status`)
}
/**
 * 获取订单列表
 */
export function getMallOrder(params: Record<string, any>) {
    return request.get(`mall/order`, params)
}

/**
 * 获取订单角标数据
 */
export function getShopOrderNum() {
    return request.get(`mall/order/num`)
}

/**
 * 获取订单详情
 */
export function getMallOrderDetail(order_id :number) {
    return request.get(`mall/order/${order_id}`)
}

/**
 * 订单列表,订单详情 调支付接口
 */
export function getMallOrderPay(order_id: number) {
    return request.get(`mall/order_group/get_group_by_order/${order_id}`)
}
/**
 * 订单列表,订单详情 调关闭接口
 */
export function getMallOrderClose(group_id: number) {
    return request.put(`mall/order_group/close/${group_id}`)
}
/**
 * 关闭订单
 */
export function orderClose(order_id: number) {
    return request.put(`mall/order/close/${order_id}`)
}

/**
 * 订单完成
 */
export function orderFinish(order_id: number) {
    return request.put(`mall/order/finish/${order_id}`)
}

/**
 * 订单创建计算
 */
export function orderCreateCalculate(params: Record<string, any>) {
    return request.post('mall/order_create/calculate', params, { showSuccessMessage: false })
}

/**
 * 订单创建
 */
export function orderCreate(params: Record<string, any>) {
    return request.post('mall/order_create/create', params)
}

/**
 * 查询订单可用优惠券
 */
export function orderCoupon(params: Record<string, any>) {
    return request.post('mall/order_create/coupon', params)
}
/**
 * 查询订单可用平台优惠券
 */
export function orderPlatformCoupon(params: Record<string, any>) {
    return request.post('mall/order_create/platform_coupon', params)
}
/**
 * 查询自提点
 */
export function getStoreList(params: Record<string, any>) {
    return request.get('mall/order_create/store', params)
}
/**
 * 查询物流信息
 */
export function getMaterialflowList(params: Record<string, any>) {
    return request.get('mall/order/logistics', params)
}
