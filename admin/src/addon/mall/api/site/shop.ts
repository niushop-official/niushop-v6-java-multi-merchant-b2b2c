import request from '@/utils/request'

/**
 * 获取统计图数据
 */
export function getShopStat() {
    return request.get(`mall/site/stat`)
}

/**
 * 获取店铺信息
 */
export function getShopInfo() {
    return request.get(`mall/site/stat/shop`)
}

/**
 * 获取代办事项统计
 */
export function getShopStatTodo() {
    return request.get(`mall/site/stat/todo`)
}
/**
 * 获取数据统计
 */
export function getShopStatTotal(params: Record<string, any>) {
    return request.get(`mall/site/stat/total`, { params })
}

/**
 * 获取商品排行榜
 */
export function getShopStatRank(params: Record<string, any>) {
    return request.get(`mall/site/stat/rank`, { params })
}

/**
 * 获取统计图数据
 */
export function getShopStatSale(params: Record<string, any>) {
    return request.get(`mall/site/stat/sale`, { params })
}