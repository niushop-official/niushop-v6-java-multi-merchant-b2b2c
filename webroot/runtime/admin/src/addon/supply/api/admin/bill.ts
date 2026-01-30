import request from '@/utils/request'
/**
 * 获取供货商账单统计
 */
export function getSupplyAccountsTotal(params: Record<string, any>) {
    return request.get(`supply/admin/bill/total`, { params })
}

/**
 * 获取供货商账单统计
 */
export function getSupplyAccountsList(params: Record<string, any>) {
    return request.get(`supply/admin/bill/list`, { params })
}