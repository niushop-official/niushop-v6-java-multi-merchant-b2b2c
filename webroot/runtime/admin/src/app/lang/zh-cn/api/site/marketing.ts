import request from '@/utils/request'

/**
 * 营销中心
 * @param params
 * @returns
 */
export function getMarketingIndex(params: Record<string, any>) {
    return request.get(`mall/site/marketing`, {params})
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