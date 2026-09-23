/**
 * 申请退款
 */
export function applyRefund(params: Record<string, any>) {
    return request.post(`mall/refund/apply`, params, { showSuccessMessage: true })
}

/**
 * 修改退款申请
 */
export function editRefund(params: Record<string, any>) {
    return request.put(`mall/refund/${ params.order_refund_no }`, params, { showSuccessMessage: true })
}

/**
 * 申请退款退货物流
 */
export function refundDelivery(params: Record<string, any>) {
    return request.post(`mall/refund/delivery/${ params.order_refund_no }`, params, { showSuccessMessage: true })
}

/**
 * 修改退款申请物流
 */
export function editRefundDelivery(params: Record<string, any>) {
    return request.put(`mall/refund/delivery/${ params.order_refund_no }`, params, { showSuccessMessage: true })
}

/**
 * 获取退款原因
 */
export function getRefundReason() {
    return request.get('mall/refund/reason')
}

/**
 * 获取退款列表
 */
export function getRefundList(params: Record<string, any>) {
    return request.get('mall/order/refund', params)
}

/**
 * 获取退款详情
 */
export function getRefundDetail(orderRefundNo: string) {
    return request.get(`mall/order/refund/${ orderRefundNo }`)
}
/**
 * 获取退款金额
 */
export function getRefundMoney(params: Record<string, any>) {
    return request.get(`mall/refund/refund_data`,params)
}

/**
 * 编辑退款金额
 */
export function getRefundMoneyAgain(params: Record<string, any>) {
    return request.get(`mall/refund/refund_data_by_no`,params)
}
/**
 * 取消维权
 */
export function closeRefund(orderRefundNo: string) {
    return request.put(`mall/refund/close/${ orderRefundNo }`, {}, { showSuccessMessage: true })
}
