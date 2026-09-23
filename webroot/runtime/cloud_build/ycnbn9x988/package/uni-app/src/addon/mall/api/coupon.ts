import request from '@/utils/request'

/**
 * 优惠券列表
 */
export function getMallCouponList(params: Record<string, any>) {
    return request.get(`marketing/coupon`, params)
}

/**
 * 商品详情优惠券
 */
export function getMallGoodsCoupon(params: Record<string, any>) {
    return request.get(`marketing/goods_coupon`, params)
}
/**
 * 优惠券详情
 */
export function getMallCouponInfo(id: number) {
    return request.get(`marketing/coupon/${ id }`)
}

/**
 * 优惠券二维码
 */
export function getMallCouponQrocde(id: number) {
    return request.get(`marketing/coupon/qrcode/${ id }`)
}

/**
 * 领取优惠券
 */
export function getCoupon(params: Record<string, any>) {
    return request.post(`marketing/coupon`, params, { showSuccessMessage: true })
}

/**
 * 获取我的优惠券
 */
export function getMyCouponList(params: Record<string, any>) {
    return request.get(`marketing/member/coupon`, params)
}

/**
 * 获取优惠券列表供组件调用（店铺）
 */
export function getMallCouponComponents(params: Record<string, any>) {
    return request.get(`marketing/coupon/components`, params)
}

/**
 * 获取优惠券列表供组件调用（平台）
 */
export function getPlatformMallCouponComponents(params: Record<string, any>) {
    return request.get(`marketing/coupon/components_of_mall`, params)
}

/**
 * 获取我的优惠券数量
 * status 1：待使用，2：已使用，3：已过期，4：已失效
 */
export function getMyCouponCount(params: Record<string, any>) {
    return request.get(`marketing/member/coupon/count`, params)
}

/**
 * 获取我的优惠券类型
 */
export function getMyCouponType() {
    return request.get(`marketing/coupon_type`)
}

/**
 * 获取我的优惠数量
 */
export function getMyCouponStatusCount() {
    return request.get(`marketing/member/coupon/status_count`)
}