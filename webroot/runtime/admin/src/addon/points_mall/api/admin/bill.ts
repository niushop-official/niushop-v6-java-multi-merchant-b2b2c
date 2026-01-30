import request from '@/utils/request'
/**
 * 获取积分商城账单统计
 */
export function getPointsMallAccountsTotal(params: Record<string, any>) {
    return request.get(`points_mall/admin/bill/total`, { params })
}

/**
 * 获取积分商城账单统计
 */
export function getPointsMallAccountsList(params: Record<string, any>) {
    return request.get(`points_mall/admin/bill/list`, { params })
}