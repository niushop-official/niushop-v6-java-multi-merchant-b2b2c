import request from '@/utils/request'

/**
 * 获取订单列表
 * @returns
 */
export function getOrderList(params: Record<string, any>) {
    return request.get('mall/site/order/list', { params })
}

/**
 * 获取交易配置
 * @returns
 */
export function getConfig() {
    return request.get('mall/site/order/config')
}

/**
 * 编辑交易配置
 * @returns
 */
export function setConfig(param: any) {
    return request.post('mall/site/order/config', param, { showSuccessMessage: true })
}

/**
 * 获取订单列表
 * @returns
 */
export function getOrderDetail(order_id: number) {
    return request.get(`mall/site/order/detail/${order_id}`)
}

/**
 * 获取订单状态
 * @return
 */
export function getOrderStatus() {
    return request.get(`mall/site/order/status`)
}

/**
 * 获取订单类型
 * @return
 */
export function getOrderType() {
    return request.get(`mall/site/order/type`)
}

/**
 * 订单关闭
 * @return
 */
export function orderClose(order_id: number) {
    return request.put(`mall/site/order/close/${order_id}`)
}

/**
 * 订单删除
 * @return
 */
export function orderDelete(params: Record<string, any>) {
    return request.post(`mall/site/order/delete`, params, { showSuccessMessage: true })
}


/**
 * 获取订单配送方式
 * @return
 */
export function getOrderDeliveryType(params: Record<string, any>) {
    return request.get(`mall/site/order/delivery_type`, { params })
}

/**
 * 订单发货
 * @return
 */
export function orderDelivery(params: Record<string, any>) {
    return request.put(`mall/site/order/delivery`, params)
}

/**
 * 商家留言
 * @return
 */
export function setShopRemark(params: Record<string, any>) {
    return request.put(`mall/site/order/shop_remark`, params)
}

/**
 * 订单完成
 * @return
 */
export function orderFinish(order_id: number) {
    return request.put(`mall/site/order/finish/${order_id}`)
}

/**
 * 物流包裹信息（物流跟踪）
 * @return
 */
export function deliveryPackage(params: Record<string, any>) {
    return request.get(`mall/site/order/delivery/package`, { params })
}

/**
 * 物流包裹列表
 * @return
 */
export function deliveryPackageList(params: Record<string, any>) {
    return request.get(`mall/site/order/delivery/package/list`, { params })
}

/**
 * 退款维权列表
 * @param {Record<string, any>} params
 * @return
 */
export function orderRefund(params: Record<string, any>) {
    return request.get(`mall/site/order/refund`, { params })
}

/**
 * 退款维权详情
 */
export function orderRefundDetail(refund_id: number) {
    return request.get(`mall/site/order/refund/${refund_id}`)
}

/**
 * 退款审核
 * @return
 */
export function auditRefund(params: Record<string, any>) {
    return request.put(`mall/site/order/refund/audit/${params.order_refund_no}`, params)
}

/**
 * 退款收货审核
 * @return
 */
export function refundDelivery(params: Record<string, any>) {
    return request.put(`mall/site/order/refund/delivery/${params.order_refund_no}`, params)
}

/**
 * 商家主动退款
 */
export function shopActiveRefund(params: Record<string, any>) {
    return request.post(`mall/site/order/refund/active`, params, { showSuccessMessage: true })
}


/**
 * 获取支付类型
 */
export function getOrderPayType() {
    return request.get(`mall/site/order/pay/type`)
}

/**
 * 获取订单来源
 */
export function getOrderFrom() {
    return request.get(`mall/site/order/from`)
}
/**
 * 订单调价
 * @return
 */
export function orderEditPrice(params: Record<string, any>) {
    return request.put(`mall/site/order/edit_price`, params, { showSuccessMessage: true })
}

/**
 * 获取订单地址信息
 * @return
 */
export function getOrderEditAddress(params: Record<string, any>) {
    return request.get(`mall/site/order/edit_delivery`, { params })
}

/**
 * 修改地址
 * @return
 */
export function orderEditAddress(params: Record<string, any>) {
    return request.put(`mall/site/order/edit_delivery`, params)
}

/**
 * 批量发货列表
 * @return
 */
export function getOrderBatchDeliveryList(params: Record<string, any>) {
    return request.get(`mall/site/order_batch_delivery`, { params })
}

/**
 * 批量发货导入
 * @return
 */
export function addBatchOrderDelivery(params: Record<string, any>) {
    return request.put(`mall/site/order_batch_delivery/add_batch_order_delivery`, params)
}

/**
 * 获取批量发货状态
 * @return
 */
export function getOrderBatchDeliveryState() {
    return request.get(`mall/site/order_batch_delivery/get_status`)
}

/**
 * 获取批量发货操作类型
 * @return
 */
export function getOrderBatchDeliveryType() {
    return request.get(`mall/site/order_batch_delivery/get_type`)
}

/**
 * 获取已选订单项总重量
 * @return
 */
export function getSelectOrderGoodsWeight(params: Record<string, any>) {
    return request.get(`mall/site/order/select/weight`, { params })
}

/**
 * 获取已选订单项配送费用
 * @return
 */
export function getDeliveryFee(params: Record<string, any>) {
    return request.get(`mall/site/order/delivery/fee`, { params })
}
