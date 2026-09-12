import request from '@/utils/request'

/***************************************************** 店铺优惠券 ****************************************************/
/**
 * 获取商品分类列表
 * @param params
 * @returns
 */
export function getGoodsCategoryList(params: Record<string, any>) {
    return request.get(`marketing/site/goods/coupon/init`, params)
}

/**
 * 添加优惠券
 * @param params
 * @returns
 */
export function addCoupon(params: Record<string, any>) {
    return request.post(`marketing/site/goods/coupon`, params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getCouponList(params: Record<string, any>) {
    return request.get(`marketing/site/goods/coupon`, {params})
}

/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getCouponRecords(params: Record<string, any>) {
    return request.get(`marketing/site/goods/coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponInfo(id: number) {
    return request.get(`marketing/site/goods/coupon/detail/${id}`);
}
/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponStat(params: Record<string, any>) {
    return request.get(`marketing/site/goods/coupon/stat`, {params});
}
/**
 * 优惠券状态变更
 * @param params
 * @returns
 */
export function editCouponStatus(params: Record<string, any>) {
    return request.put(`marketing/site/goods/coupon/setstatus/${params.status}`, params, {showSuccessMessage: true})
}

/**
 * 编辑优惠券
 * @param params
 * @returns
 */
export function editCoupon(params: Record<string, any>) {
    return request.put(`marketing/site/goods/coupon/edit/${params.id}`, params, {
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
    return request.delete(`marketing/site/goods/coupon/${id}`, {showSuccessMessage: true})
}

/**
 * 关闭优惠券
 * @param id
 * @returns
 */
export function closeCoupon(id: number) {
    return request.put(`marketing/site/goods/coupon/invalid/${ id }`, { showSuccessMessage: true })
}

/**
 * 店铺优惠券批量发放
 * @param params
 */
export function sendCoupon(params: Record<string, any>) {
    return request.post(`marketing/site/goods/coupon/send`, params, { showSuccessMessage: true })
}

/**
 * 获取店铺会员优惠券
 * @param member_id 会员id
 * @returns
 */
export function getShopMemberCoupon(member_id: number,params: Record<string, any>) {
    return request.get(`marketing/site/member/coupon/list/${member_id}`,{params});
}

/**
 * 获取优惠券状态
 * @param id
 * @returns
 */
export function getStatusList() {
    return request.get(`marketing/site/goods/coupon/status`);
}

/**
 * 获取选中优惠券列表
 * @param params
 * @returns
 */
export function getCouponSelectPageList(params: Record<string, any>) {
    return request.get(`marketing/site/goods/coupon/select`, { params })
}

/**
 * 获取商品选择组件
 * @param params
 * @returns
 */
export function getGoodsSelectComponent() {
    return request.get(`marketing/site/goods_select/component`)
}

/***************************************************** 满减 ****************************************************/
/**
 * 获取满减列表
 */
export function getFullReductionList(params: Record<string, any>) {
    return request.get(`marketing/site/full_reduction/list`, {params})
}

/**
 * 获取满减详情
 * @param id
 * @returns
 */
export function getFullReductionInfo(params: Record<string, any>) {
    return request.get(`marketing/site/full_reduction/init`,{params});
}

/**
 * 添加满减
 * @param params
 * @returns
 */
export function addFullReduction(params: Record<string, any>) {
    return request.post(`marketing/site/full_reduction/add`, params, {
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
    return request.post(`marketing/site/full_reduction/edit/${params.id}`, params, {
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
    return request.delete(`marketing/site/full_reduction/delete/${ id }`, { showSuccessMessage: true })
}

/**
 * 关闭满减
 * @param id
 * @returns
 */
export function closeFullReduction(id: number,params: Record<string, any>) {
    return request.put(`marketing/site/full_reduction/setStatus/${ id }`, params,{ showSuccessMessage: true })
}

/**
 * 检测满减商品
 * @param id
 * @returns
 */
export function checkFullReduction(params: Record<string, any>) {
    return request.post(`marketing/site/full_reduction/check`, params)
}

/**
 * 获取商品选择组件
 * @param params
 * @returns
 */
export function getFullReductionGoodsSelectComponent() {
    return request.get(`marketing/site/full_reduction/goods_select/component`)
}
