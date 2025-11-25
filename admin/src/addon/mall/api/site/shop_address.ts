import request from '@/utils/request'

/**
 * 获取商家收货地址
 * @returns
 */
export function getOrderRefundAddress() {
    return request.get('basic/site/order/refund/address')
}