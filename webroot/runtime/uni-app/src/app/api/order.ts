import request from '@/utils/request'


/**
 * 充值订单列表
 */
export function getRechargeList(params: Record<string, any>) {
    return request.get(`order/recharge/list`, params)
}

/**
 * 充值记录详情
 */
export function getRechargeDetail(id: number) {
    return request.get(`order/recharge/${id}`, {}, {showErrorMessage: true})
}