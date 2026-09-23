import request from '@/utils/request'

/**
 * 付费会员卡详情（会员中心）
 */
export function getVipcardInfo() {
    return request.get('vip_card/info', {}, { showErrorMessage: true })
}

/**
 * 创建付费会员卡订单
 */
export function createVipcardOrder(data: AnyObject) {
    return request.post('vip_card/order/create', data, { showErrorMessage: true })
}

/**
 * 付费会员卡列表
 */
export function getVipcardList(data: AnyObject) {
    return request.get('vip_card/order/lists', data, { showErrorMessage: true })
}

/**
 * 付费会员卡状态列表
 */
export function getVipcardStatusList() {
    return request.get('vip_card/order/status')
}



/**
 * 付费会员卡详情
 */
export function getVipcardDetail(id: number) {
    return request.get(`vip_card/order/detail/${ id }`, {}, { showErrorMessage: true })
}

/**
 * 付费会员卡权益
 */
export function getVipcardBenefitsGroup(data: AnyObject) {
    return request.get('vip_card/benefits/group', data, { showErrorMessage: true })
}

/**
 * 付费会员卡专属商品
 */
export function getVipcardGoods(data: AnyObject) {
    return request.get('vip_card/goods', data, { showErrorMessage: true })
}


/**
 * 付费会员卡专属商品
 */
export function getVipcardComponents() {
    return request.get('vip_card/components')
}