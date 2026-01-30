import request from '@/utils/request'

/**
 * 获取订单列表
 * @param params
 * @returns
 */
export function getPintuanAdminOrderList(params: Record<string, any>) {
    return request.get(`pintuan/admin/order/list`, { params })
}

/**
 * 获取订单字典
 * @param params
 * @returns
 */
export function getPintuanAdminOrderInit() {
    return request.get(`pintuan/admin/order/init`)
}

/**
 * 获取订单详情
 * @param id 订单id
 * @returns
 */
export function getPintuanAdminOrderInfo(order_no: string) {
    return request.get(`pintuan/admin/order/detail/${order_no}`);
}

/**
 * 订单日志
 */
export function pintuanAdminOrderLog(order_no: string) {
    return request.get(`pintuan/admin/order/log/${order_no}`)
}