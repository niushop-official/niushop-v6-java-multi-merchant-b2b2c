import request from '@/utils/request'

/**
 * 获取交易配置
 * @returns
 */
export function getConfig() {
    return request.get('points_mall/admin/order/config')
}

/**
 * 编辑交易配置
 * @returns
 */
export function setConfig(param: any) {
    return request.post('points_mall/admin/order/config', param, { showSuccessMessage: true })
}


/**
 * 获取订单列表
 * @returns
 */
export function getOrderList(params: Record<string, any>) {
    return request.get('points_mall/admin/order/list', { params })
}

/**
 * 获取订单详情
 * @returns
 */
export function getOrderDetail(order_id: number) {
    return request.get(`points_mall/admin/order/detail/${order_id}`)
}

/**
 * 获取订单状态
 * @return
 */
export function getOrderStatus() {
    return request.get(`points_mall/admin/order/status`)
}

/**
 * 获取订单类型
 * @return
 */
export function getOrderType() {
    return request.get(`oints_mall/admin/order/type`)
}

/**
 * 获取支付类型
 */
export function getOrderPayType() {
    return request.get(`points_mall/admin/order/pay/type`)
}

/**
 * 获取订单来源
 */
export function getOrderFrom() {
    return request.get(`points_mall/admin/order/from`)
}

/**
 * 订单关闭
 * @return
 */
export function orderClose(order_id: number) {
    return request.put(`points_mall/admin/order/close/${order_id}`)
}

/**
 * 订单完成
 * @return
 */
export function orderFinish(order_id: number) {
    return request.put(`points_mall/admin/order/finish/${order_id}`)
}

/**
 * 获取订单地址信息
 * @return
 */
export function getOrderEditAddress(params: Record<string, any>) {
    return request.get(`points_mall/admin/order/edit_delivery`, { params })
}

/**
 * 修改地址
 * @return
 */
export function orderEditAddress(params: Record<string, any>) {
    return request.put(`points_mall/admin/order/edit_delivery`, params)
}


/**
 * 获取订单配送方式
 * @return
 */
export function getOrderDeliveryType(params: Record<string, any>) {
    return request.get(`points_mall/admin/order/delivery_type`, {params})
}

/**
 * 订单发货
 * @return
 */
export function orderDelivery(params: Record<string, any>) {
    return request.put(`points_mall/admin/order/delivery`, params)
}


/**
 * 商家留言
 * @return
 */
export function setRemark(params: Record<string, any>) {
    return request.put(`points_mall/admin/order/shop_remark`, params)
}