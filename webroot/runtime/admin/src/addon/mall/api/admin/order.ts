import request from '@/utils/request'

/**
 * 获取交易配置
 * @returns
 */
export function getConfig() {
    return request.get('mall/admin/order/config')
}

/**
 * 编辑交易配置
 * @returns
 */
export function setConfig(param: any) {
    return request.post('mall/admin/order/config', param, { showSuccessMessage: true })
}

/**
 * 获取订单列表
 * @returns
 */
export function getOrderList(params: Record<string, any>) {
    return request.get('mall/admin/order/list', { params })
}

/**
 * 获取订单列表
 * @returns
 */
export function getOrderDetail(order_id: number) {
    return request.get(`mall/admin/order/detail/${order_id}`)
}

/**
 * 获取订单状态
 * @return
 */
export function getOrderStatus() {
    return request.get(`mall/admin/order/status`)
}

/**
 * 获取订单类型
 * @return
 */
export function getOrderType() {
    return request.get(`mall/admin/order/type`)
}

/**
 * 商家留言
 * @return
 */
export function setMallRemark(params: Record<string, any>) {
    return request.put(`mall/admin/order/mall_remark`, params)
}

/**
 * 退款维权列表
 * @param {Record<string, any>} params
 * @return
 */
export function orderRefund(params: Record<string, any>) {
    return request.get(`mall/admin/order/refund`, { params })
}

/**
 * 退款维权详情
 */
export function orderRefundDetail(refund_id: number) {
    return request.get(`mall/admin/order/refund/${refund_id}`)
}

/**
 * 开具发票
 */
export function setInvoice(id: number, params: Record<string, any>) {
    return request.put(`basic/admin/invoice/${id}`, params, { showSuccessMessage: true })
}

/**
 * 获取支付类型
 */
export function getOrderPayType() {
    return request.get(`mall/admin/order/pay/type`)
}

/**
 * 获取订单来源
 */
export function getOrderFrom() {
    return request.get(`mall/admin/order/from`)
}

/***************************************************** 结算订单 ****************************************************/
/**
 * 获取结算订单列表
 * @param params
 * @returns
 */
export function getSettlementOrderList(params: Record<string, any>) {
    return request.get(`mall/admin/order/settlement`, { params })
}
/**
 * 获取结算订单统计
 * @returns
 */
export function getSettlementInfo() {
    return request.get(`mall/admin/order/settlement/info`)
}
