import request from '@/utils/request'

/***************************************************** 发票管理 ****************************************************/

/**
 * 获取发票列表
 */
export function getMallInvoice(params: Record<string, any>) {
    return request.get(`basic/invoice`, params)
}
/**
 * 获取订单详情
 */
export function getMallInvoiceDetail(id :number) {
    return request.get(`basic/invoice/${id}`)
}