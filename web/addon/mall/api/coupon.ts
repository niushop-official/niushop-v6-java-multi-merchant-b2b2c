/**
 * 优惠券列表
 */
export function getMallCouponList(params: Record<string, any>) {
    return request.get(`mall/coupon`, params)
}
/**
 * 商品详情优惠券
 */
export function getMallGoodsCoupon(params: Record<string, any>) {
    return request.get(`mall/goods_coupon`, params)
}
/**
 * 优惠券详情
 */
export function getMallCouponInfo(id: number) {
    return request.get(`mall/coupon/${ id }`)
}

/**
 * 领取优惠券
 */
export function getCoupon(params: Record<string, any>) {
    return request.post(`mall/coupon`, params, { showSuccessMessage: true })
}

/**
 * 获取我的优惠券
 */
export function getMyCouponList(params: Record<string, any>) {
    return request.get(`mall/member/coupon`, params)
}

/**
 * 获取我的优惠券数量
 * status 1：待使用，2：已使用，3：已过期，4：已失效
 */
export function getMyCouponCount(params: Record<string, any>) {
    return request.get(`mall/member/coupon/count`, params)
}