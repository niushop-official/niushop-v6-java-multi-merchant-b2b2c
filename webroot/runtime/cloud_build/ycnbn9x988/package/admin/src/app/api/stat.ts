import request from '@/utils/request'

/***************************************************** 统计信息 **************************************************/
/**
 * 获取统计信息
 */
export function getStatInfo() {
    return request.get(`stat/index`)
}

/**
 * 获取统计图表
 */
export function getStatChart() {
    return request.get(`stat/index/chart`)
}

/**
 * 获取最新动态
 */
export function getStatNewsList(params: Record<string, any>) {
    return request.get(`stat/index/news`, { params })
}

/**
 * 获取站点统计信息
 */
export function getSiteStatInfo() {
    return request.get(`stat/siteindex`)
}

/**
 * 获取财务收款统计数据
 */
export function getFinancePayStatInfo() {
    return request.get(`stat/finance/pay`)
}

/**
 * 获取财务结算统计数据
 */
export function getFinanceSettlementStatInfo() {
    return request.get(`stat/finance/settlement`)
}

/**
 * 获取财务会员统计数据
 */
export function getFinanceMemberStatInfo() {
    return request.get(`stat/finance/member`)
}

/**
 * 获取平台账单统计
 */
export function getFinanceAccountsTotal(params: Record<string, any>) {
    return request.get(`stat/finance/accounts/total`, { params })
}

/**
 * 获取平台账单统计
 */
export function getFinanceAccountsList(params: Record<string, any>) {
    return request.get(`stat/finance/accounts/list`, { params })
}