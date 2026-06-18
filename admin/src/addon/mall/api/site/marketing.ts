import request from '@/utils/request'

/**
 * 营销中心
 * @param params
 * @returns
 */
export function getMarketingIndex(params: Record<string, any>) {
    return request.get(`mall/site/marketing`, {params})
}

/**
 * 获取商品分类列表
 * @param params
 * @returns
 */
export function getGoodsCategoryList(params: Record<string, any>) {
    return request.get(`mall/site/goods/coupon/init`, params)
}

/**
 * 添加优惠券
 * @param params
 * @returns
 */
export function addCoupon(params: Record<string, any>) {
    return request.post(`mall/site/goods/coupon`, params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getCouponList(params: Record<string, any>) {
    return request.get(`mall/site/goods/coupon`, {params})
}

/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getCouponRecords(params: Record<string, any>) {
    return request.get(`mall/site/goods/coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponInfo(id: number) {
    return request.get(`mall/site/goods/coupon/detail/${id}`);
}
/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponStat(params: Record<string, any>) {
    return request.get(`mall/site/goods/coupon/stat`, {params});
}
/**
 * 优惠券状态变更
 * @param params
 * @returns
 */
export function editCouponStatus(params: Record<string, any>) {
    return request.put(`mall/site/goods/coupon/setstatus/${params.status}`, params, {showSuccessMessage: true})
}

/**
 * 编辑优惠券
 * @param params
 * @returns
 */
export function editCoupon(params: Record<string, any>) {
    return request.put(`mall/site/goods/coupon/edit/${params.id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除优惠券
 * @param id
 * @returns
 */
export function deleteCoupon(id: number) {
    return request.delete(`mall/site/goods/coupon/${id}`, {showSuccessMessage: true})
}

/**
 * 关闭优惠券
 * @param id
 * @returns
 */
export function closeCoupon(id: number) {
    return request.put(`mall/site/goods/coupon/invalid/${ id }`, { showSuccessMessage: true })
}

/**
 * 获取优惠券状态
 * @param id
 * @returns
 */
export function getStatusList() {
    return request.get(`mall/site/goods/coupon/status`);
}

/**
 * 获取选中优惠券列表
 * @param params
 * @returns
 */
export function getCouponSelectPageList(params: Record<string, any>) {
    return request.get(`mall/site/goods/coupon/select`, { params })
}

/***************************************************** 限时折扣 ****************************************************/

/**
 * 获取限时折扣列表
 * @param params
 * @returns
 */
export function getActiveDiscountPageList(params: Record<string, any>) {
    return request.get(`mall/site/active/discount`, { params })
}

/**
 * 获取限时折扣状态列表
 * @returns
 */
export function getActiveDiscountStatusList() {
    return request.get(`mall/site/active/status`)
}

/**
 * 获取限时折扣详情
 * @param active_id active_id
 * @returns
 */
export function getActiveDiscountInfo(active_id: number) {
    return request.get(`mall/site/active/discount/${ active_id }`);
}

/**
 * 添加限时折扣
 * @param params
 * @returns
 */
export function addActiveDiscount(params: Record<string, any>) {
    return request.post('mall/site/active/discount', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑限时折扣
 * @param params
 * @returns
 */
export function editActiveDiscount(params: Record<string, any>) {
    return request.put(`mall/site/active/discount/${ params.active_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 关闭限时折扣
 * @param active_id
 * @returns
 */
export function closeActiveDiscount(active_id: number) {
    return request.put(`mall/site/active/discount/close/${ active_id }`, {}, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除限时折扣
 * @param active_id
 * @returns
 */
export function deleteActiveDiscount(active_id: number) {
    return request.delete(`mall/site/active/discount/${ active_id }`, { showSuccessMessage: true })
}

/**
 * 获取参与限时折扣商品列表
 * @param params
 * @returns
 */
export function getActiveDiscountGoodsPageList(params: Record<string, any>) {
    return request.get(`mall/site/active/discount/goods/${ params.active_id }`, { params })
}

/**
 * 获取参与限时折扣订单列表
 * @param params
 * @returns
 */
export function getActiveDiscountOrderPageList(params: Record<string, any>) {
    return request.get(`mall/site/active/discount/order/${ params.active_id }`, { params })
}

/**
 * 获取参与限时折扣会员列表
 * @param params
 * @returns
 */
export function getActiveDiscountMemberPageList(params: Record<string, any>) {
    return request.get(`mall/site/active/discount/member/${ params.active_id }`, { params })
}

/**
 * 获取悬浮按钮
 */
export function getShopPointConfig() {
    return request.get(`mall/site/point_deduct/config`)
}

/**
 * 获取悬浮按钮
 */
export function setShopPointConfig(params: Record<string, any>) {
    return request.post(`mall/site/point_deduct/config`, params, { showSuccessMessage: true })
}

/***************************************************** 满减 ****************************************************/
/**
 * 获取满减列表
 */
export function getFullReductionList(params: Record<string, any>) {
    return request.get(`mall/site/full_reduction/list`, {params})
}

/**
 * 获取满减详情
 * @param id
 * @returns
 */
export function getFullReductionInfo(params: Record<string, any>) {
    return request.get(`mall/site/full_reduction/init`,{params});
}

/**
 * 添加满减
 * @param params
 * @returns
 */
export function addFullReduction(params: Record<string, any>) {
    return request.post(`mall/site/full_reduction/add`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 编辑满减
 * @param params
 * @returns
 */
export function editFullReduction(params: Record<string, any>) {
    return request.post(`mall/site/full_reduction/edit/${params.id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除满减
 * @param id
 * @returns
 */
export function deleteFullReduction(id: number) {
    return request.delete(`mall/site/full_reduction/delete/${ id }`, { showSuccessMessage: true })
}

/**
 * 关闭满减
 * @param id
 * @returns
 */
export function closeFullReduction(id: number,params: Record<string, any>) {
    return request.put(`mall/site/full_reduction/setStatus/${ id }`, params,{ showSuccessMessage: true })
}

/**
 * 检测满减商品
 * @param id
 * @returns
 */
export function checkFullReduction(params: Record<string, any>) {
    return request.post(`mall/site/full_reduction/check`, params)
}


/***************************************************** 满赠 ****************************************************/
/**
 * 获取满赠列表
 */
export function getFullGiftList(params: Record<string, any>) {
    return request.get(`mall/site/full_gift/list`, {params})
}

/**
 * 获取满赠详情
 * @param id
 * @returns
 */
export function getFullGiftInfo(params: Record<string, any>) {
    return request.get(`mall/site/full_gift/init`,{params});
}

/**
 * 添加满赠
 * @param params
 * @returns
 */
export function addFullGift(params: Record<string, any>) {
    return request.post(`mall/site/full_gift/add`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 编辑满赠
 * @param params
 * @returns
 */
export function editFullGift(params: Record<string, any>) {
    return request.post(`mall/site/full_gift/edit/${params.id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除满赠
 * @param id
 * @returns
 */
export function deleteFullGift(id: number) {
    return request.delete(`mall/site/full_gift/delete/${ id }`, { showSuccessMessage: true })
}

/**
 * 关闭满赠
 * @param id
 * @returns
 */
export function closeFullGift(id: number,params: Record<string, any>) {
    return request.put(`mall/site/full_gift/setStatus/${ id }`, params,{ showSuccessMessage: true })
}

/**
 * 检测满赠商品
 * @param id
 * @returns
 */
export function checkFullGift(params: Record<string, any>) {
    return request.post(`mall/site/full_gift/check`, params)
}