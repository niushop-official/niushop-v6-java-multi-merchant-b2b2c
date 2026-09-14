import request from '@/utils/request'


/**
 * 获取店铺详情
 */
export function getSiteInfo(params: Record<string, any>) {
    return request.get(`site/info/${ params.site_id }`, params, { showErrorMessage: true })
}

/**
 * 订单创建计算
 */
export function orderCreateCalculate(params: Record<string, any>) {
    return request.get('cash/order/calculate', params)
}

/**
 * 订单创建
 */
export function orderCreate(params: Record<string, any>) {
    return request.post('cash/order/create', params)
}


/**
 * 付费会员卡列表
 */
export function getCashList(data: AnyObject) {
    return request.get('cash/order/lists', data, { showErrorMessage: true })
}

/**
 * 付费会员卡状态列表
 */
export function getCashStatusList() {
    return request.get('cash/order/status')
}



/**
 * 付费会员卡详情
 */
export function getCashDetail(id: number) {
    return request.get(`cash/order/detail/${ id }`, {}, { showErrorMessage: true })
}