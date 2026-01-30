import request from '@/utils/request'

/**
 * 退款维权列表
 * @param {Record<string, any>} params
 * @return
 */
export function orderRefund(params: Record<string, any>) {
    return request.get(`points_mall/admin/order/refund`, {params})
}

/**
 * 退款维权详情
 */
export function orderRefundDetail(refund_id: number) {
    return request.get(`points_mall/admin/order/refund/${refund_id}`)
}

/**
 * 退款审核
 * @return
 */
export function auditRefund(params: Record<string, any>) {
    return request.put(`points_mall/admin/order/refund/audit/${params.order_refund_no}`, params)
}

/**
 * 退款收货审核
 * @return
 */
export function refundDelivery(params: Record<string, any>) {
    return request.put(`points_mall/admin/order/refund/delivery/${params.order_refund_no}`, params)
}