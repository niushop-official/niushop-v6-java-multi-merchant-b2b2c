import request from '@/utils/request'

/**
 * 获取会员统计基础数据
 */
export function getMemberStatBasic() {
    return request.get('mall/admin/member/stat/basic')
}

/**
 * 获取会员数据概览
 */
export function getMemberData(params: Record<string, any>) {
    return request.get('mall/admin/member/stat/data', { params })
}

/**
 * 获取趋势分析数据
 */
export function getTrendData(params: Record<string, any>) {
    return request.get('mall/admin/member/stat/trend', { params })
}

/**
 * 获取来源分布
 */
export function getSourceData() {
    return request.get('mall/admin/member/stat/source')
}

/**
 * 获取时长分布
 */
export function getDurationData() {
    return request.get('mall/admin/member/stat/duration')
}

/**
 * 获取会员排行
 */
export function getRankingData(params: Record<string, any>) {
    return request.get('mall/admin/member/stat/ranking', { params })
}

/**
 * 获取交易统计基础数据
 */
export function getTradeStatBasic(params: Record<string, any>) {
    return request.get('mall/admin/trade/stat/basic', { params })
}

/**
 * 获取交易统计趋势数据
 */
export function getTradeTrend(params: Record<string, any>) {
    return request.get('mall/admin/trade/stat/trend', { params })
}

/**
 * 获取交易转化统计数据
 */
export function getTradeConversion(params: Record<string, any>) {
    return request.get('mall/admin/trade/stat/conversion', { params })
}

/**
 * 获取交易渠道统计数据
 */
export function getTradeChannel(params: Record<string, any>) {
    return request.get('mall/admin/trade/stat/channel', { params })
}

/**
 * 获取成交客户统计数据
 */
export function getTradeDealMember(params: Record<string, any>) {
    return request.get('mall/admin/trade/stat/dealMember', { params })
}
