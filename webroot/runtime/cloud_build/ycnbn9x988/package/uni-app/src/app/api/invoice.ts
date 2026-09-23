import request from '@/utils/request'

/***************************************************** 发票管理 ****************************************************/

/**
 * 获取发票配置
 */
export function getInvoiceConfig() {
	return request.get(`basic/invoice/content/list`)
}

/**
 * 获取发票列表
 */
export function getMallInvoice(params: Record<string, any>) {
    return request.get(`basic/invoice`, params)
}
/**
 * 获取发票详情
 */
export function getMallInvoiceDetail(id :number) {
    return request.get(`basic/invoice/${id}`)
}


// 添加发票
export function addInvoice(params: Record<string, any>) {
    return request.post(`basic/invoice/apply`, params)
}

// 编辑发票
export function editInvoice(id:number, params: Record<string, any>) {
    return request.post(`basic/invoice/edit/${id}`, params)
}

// 取消申请
export function cancelInvoice(id: number) {
    return request.delete(`basic/invoice/cancel/${id}`)
}