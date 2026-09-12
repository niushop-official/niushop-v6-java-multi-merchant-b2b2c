import request from '@/utils/request'

/***************************************************** 平台优惠券 ****************************************************/

/**
 * 获取商品分类列表
 * @param params
 * @returns
 */
export function getGoodsCategoryList() {
    return request.get(`marketing/admin/goods/platform_coupon/init`)
}

/**
 * 获取初始化店铺列表
 * @param params
 * @returns
 */
export function getSiteList(params: Record<string, any>) {
    return request.get(`marketing/admin/goods/platform_coupon/site`, {params})
}

/**
 * 添加优惠券
 * @param params
 * @returns
 */
export function addPlatformCoupon(params: Record<string, any>) {
    return request.post(`marketing/admin/goods/platform_coupon`, params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getPlatformCouponList(params: Record<string, any>) {
    return request.get(`marketing/admin/goods/platform_coupon`, {params})
}
/**
 * 获取会员优惠券列表
 * @param params
 * @returns
 */
export function getMemberPlatformCouponList(member_id: number,params: Record<string, any>) {
    return request.get(`marketing/admin/member/platform_coupon/list/${member_id}`,{ params})
}
/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getPlatformCouponRecords(params: Record<string, any>) {
    return request.get(`marketing/admin/goods/platform_coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getPlatformCouponInfo(id: number) {
    return request.get(`marketing/admin/goods/platform_coupon/detail/${id}`);
}

/**
 * 优惠券状态变更
 * @param params
 * @returns
 */
export function editPlatformCouponStatus(params: Record<string, any>) {
    return request.put(`marketing/admin/goods/platform_coupon/setstatus/${params.status}`, params, {showSuccessMessage: true})
}

/**
 * 编辑优惠券
 * @param params
 * @returns
 */
export function editPlatformCoupon(params: Record<string, any>) {
    return request.put(`marketing/admin/goods/platform_coupon/edit/${params.id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除优惠券
 * @param id
 * @returns
 */
export function deletePlatformCoupon(id: number) {
    return request.delete(`marketing/admin/goods/platform_coupon/${id}`, {showSuccessMessage: true})
}

/**
 * 关闭优惠券
 * @param id
 * @returns
 */
export function closePlatformCoupon(id: number) {
    return request.put(`marketing/admin/goods/platform_coupon/invalid/${ id }`, { showSuccessMessage: true })
}

/**
 * 赠送优惠券
 * @param id 会员id
 * @returns
 */
export function sendCoupon(params: Record<string, any>) {
    return request.post(`marketing/admin/goods/platform_coupon/send`, params, {showSuccessMessage: true});
}

/**
 * 获取选中优惠券列表
 * @param params
 * @returns
 */
export function getSelectedCouponList(params: Record<string, any>) {
    return request.get(`marketing/admin/goods/platform_coupon/selected`, { params })
}

/**
 * 获取优惠券状态
 * @param id
 * @returns
 */
export function getStatusList() {
    return request.get(`marketing/admin/goods/platform_coupon/status`);
}

/**
 * 获取优惠券状态
 * @param id
 * @returns
 */
export function getTradeTypeList() {
    return request.get(`marketing/admin/goods/platform_coupon/trade_type`);
}

/***************************************************** 店铺优惠券 ****************************************************/

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getCouponList(params: Record<string, any>) {
    return request.get(`marketing/admin/goods/coupon`, {params})
}

/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getCouponRecords(params: Record<string, any>) {
    return request.get(`marketing/admin/goods/coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponInfo(id: number) {
    return request.get(`marketing/admin/goods/coupon/detail/${id}`);
}

/***************************************************** 新人礼配置 ****************************************************/

/**
 * 获取新人礼配置
 * @returns
 */
export function getNewcomerGiftConfig() {
    return request.get(`marketing/admin/newcomer_gift/config`)
}

/**
 * 设置新人礼配置
 * @param params
 * @returns
 */
export function setNewcomerGiftConfig(params: Record<string, any>) {
    return request.post(`marketing/admin/newcomer_gift/config`, params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 获取新人礼领取记录
 * @param params
 * @returns
 */
export function getNewcomerGiftRecords(params: Record<string, any>) {
    return request.get(`marketing/admin/newcomer_gift/records`, { params })
}

/**
 * 获取新人礼领取记录数量
 * @returns
 */
export function getNewcomerGiftRecordsCount() {
    return request.get(`marketing/admin/newcomer_gift/records/count`)
}

/***************************************************** 满减 ****************************************************/

/**
 * 获取满减列表
 * @param params
 * @returns
 */
export function getFullReductionList(params: Record<string, any>) {
    return request.get(`marketing/admin/full_reduction/list`,{ params })
}

/**
 * 获取满减详情
 * @param params
 * @returns
 */
export function getFullReductionInfo(id: number) {
    return request.get(`marketing/admin/full_reduction/info/${id}`)
}

/**
 * 获取满减初始化
 * @param params
 * @returns
 */
export function getFullReductionInit() {
    return request.get(`marketing/admin/full_reduction/init`)
}