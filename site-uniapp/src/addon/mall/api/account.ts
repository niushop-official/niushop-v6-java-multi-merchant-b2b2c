import request from '@/utils/request'

/**
 * 获取平台账单统计
 */
export function getFinanceAccountsTotal(params: Record<string, any>) {
    return request.get(`stat/finance/accounts/total`, params)
}

/**
 * 获取平台账单统计
 */
export function getFinanceAccountsList(params: Record<string, any>) {
    return request.get(`stat/finance/accounts/list`, params)
}

/**
 * 获取平台账单详情
 */
export function getFinanceAccountsDetail(params: Record<string, any>) {
    return request.get(`stat/finance/accounts/detail`, params)
}