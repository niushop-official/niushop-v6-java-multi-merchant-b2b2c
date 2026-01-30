import request from '@/utils/request'

/**
 * 获取订单列表
 * @param params
 * @returns
 */
export function getPintuanOrderList(params: Record<string, any>) {
    return request.get(`pintuan/site/order/list`, { params })
}

/**
 * 获取订单字典
 * @param params
 * @returns
 */
export function getPintuanOrderInit() {
    return request.get(`pintuan/site/order/init`)
}

/**
 * 获取订单详情
 * @param id 订单id
 * @returns
 */
export function getPintuanOrderInfo(order_no: string) {
    return request.get(`pintuan/site/order/detail/${order_no}`);
}

/**
 * 主动退款
 * @param params
 * @returns
 */
export function pintuanOrderActiveRefund(order_id: string, params: Record<string, any>) {
    return request.post(`pintuan/site/order/refund/active/${order_id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 订单备注
 * @param params
 * @returns
 */
export function pintuanOrderRremark(order_no:string,params: Record<string, any>) {
    return request.post(`pintuan/site/order/remark/${order_no}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 关闭订单
 * @param params
 * @returns
 */
export function pintuanOrderClose(order_no: string,params: Record<string, any>) {
    return request.put(`pintuan/site/order/close/${order_no}`, params, { showErrorMessage: true, showSuccessMessage: true })
}


/**
 * 订单日志
 */
export function pintuanOrderLog(order_no: string) {
    return request.get(`pintuan/site/order/log/${order_no}`)
}

/**
 * 售后日志
 */
export function pintuanOrderRefundLog(order_refund_no: string) {
    return request.get(`pintuan/site/order/refund/log/${order_refund_no}`)
}





/**
 * 获取售后订单详情
 * @param id 订单id
 * @returns
 */
export function getPintuanOrderRefundInfo(order_refund_no: string) {
    return request.get(`pintuan/site/order/refund/detail/${order_refund_no}`);
}

/**
 * 退款审核
 */
export function refundAudit(order_refund_no:string, params: Record<string, any>) {
    return request.post(`pintuan/site/order/refund/audit/${order_refund_no}`, params)
}

/**
 * 重新拉取三方订单
 */
export function retryPintuanOrder(order_no: string) {
    return request.post(`pintuan/site/order/retry/${order_no}`)
}
