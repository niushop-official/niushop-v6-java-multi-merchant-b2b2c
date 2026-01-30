import request from '@/utils/request'
/**
 * 获取商品统计基本信息
 * @returns
 */
export function getGoodsStatisticsBasic(params: Record<string, any>) {
    return request.get('supply/site/goods/statistics/basic', { params })
}

/**
 * 获取商品排行图表统计信息
 * @returns
 */
export function getGoodsStatisticsTrend(params: Record<string, any>) {
    return request.get('supply/site/goods/statistics/trend', { params })
}
/**
 * 获取商品排行榜统计类型
 */
export function getGoodsStatisticsType() {
    return request.get(`supply/site/goods/statistics/type`)
}

/**
 * 获取商品排行信息
 * @returns
 */
export function getGoodsStatisticsRank(params: Record<string, any>) {
    return request.get('supply/site/goods/statistics/rank', { params })
}

/**
 * 获取资讯列表
 * @returns
 */
export function getArticleList(params: Record<string, any>) {
    return request.get('supply/site/stat/article', { params })
}

/**
 * 获取优化建议列表
 * @returns
 */
export function getSuggestionList(params: Record<string, any>) {
    return request.get('supply/site/stat/suggestion', { params })
}

/**
 * 获取应用列表
 * @returns
 */
export function getAddonList(params: Record<string, any>) {
    return request.get('supply/site/stat/addon', { params })
}

/******************************************** 账单 ********************************************/

/**
 * 获取供货商账单统计
 */
export function getFinanceAccountsTotal(params: Record<string, any>) {
    return request.get(`supply/site/account/total`, { params })
}

/**
 * 获取供货商账单列表
 */
export function getFinanceAccountsList(params: Record<string, any>) {
    return request.get(`supply/site/account/bill`, { params })
}