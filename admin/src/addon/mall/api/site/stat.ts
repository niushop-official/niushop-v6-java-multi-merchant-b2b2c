import request from '@/utils/request'
/**
 * 获取商品统计基本信息
 * @returns
 */
export function getGoodsStatisticsBasic(params: Record<string, any>) {
    return request.get('mall/site/goods/statistics/basic', { params })
}

/**
 * 获取商品排行图表统计信息
 * @returns
 */
export function getGoodsStatisticsTrend(params: Record<string, any>) {
    return request.get('mall/site/goods/statistics/trend', { params })
}
/**
 * 获取商品排行榜统计类型
 */
export function getGoodsStatisticsType() {
    return request.get(`mall/site/goods/statistics/type`)
}

/**
 * 获取商品排行信息
 * @returns
 */
export function getGoodsStatisticsRank(params: Record<string, any>) {
    return request.get('mall/site/goods/statistics/rank', { params })
}

/**
 * 获取资讯列表
 * @returns
 */
export function getArticleList(params: Record<string, any>) {
    return request.get('mall/site/stat/article', { params })
}

/**
 * 获取优化建议列表
 * @returns
 */
export function getSuggestionList(params: Record<string, any>) {
    return request.get('mall/site/stat/suggestion', { params })
}

/**
 * 获取应用列表
 * @returns
 */
export function getAddonList(params: Record<string, any>) {
    return request.get('mall/site/stat/addon', { params })
}


/**
 * 获取交易统计基础数据
 */
export function getTradeStatBasic(params: Record<string, any>) {
    return request.get('mall/site/trade/stat/basic', { params })
}

/**
 * 获取交易统计趋势数据
 */
export function getTradeTrend(params: Record<string, any>) {
    return request.get('mall/site/trade/stat/trend', { params })
}

/**
 * 获取交易转化统计数据
 */
export function getTradeConversion(params: Record<string, any>) {
    return request.get('mall/site/trade/stat/conversion', { params })
}

/**
 * 获取交易渠道统计数据
 */
export function getTradeChannel(params: Record<string, any>) {
    return request.get('mall/site/trade/stat/channel', { params })
}

/**
 * 获取成交客户统计数据
 */
export function getTradeDealMember(params: Record<string, any>) {
    return request.get('mall/site/trade/stat/dealMember', { params })
}
