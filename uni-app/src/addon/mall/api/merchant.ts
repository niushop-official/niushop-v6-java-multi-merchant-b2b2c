import request from '@/utils/request'

/**
 * 添加店铺申请
 */
export function setShopApply(params: Record<string, any>) {
    return request.post(`mall/shop_apply`, params, { showSuccessMessage: true })
}

/**
 * 编辑店铺申请
 */
export function editShopApply(params: Record<string, any>) {
    return request.put(`mall/shop_apply/${ params.apply_id }`, params, { showSuccessMessage: true })
}

/**
 * 店铺申请状态
 */
export function getShopApplyStatus() {
    return request.get(`mall/shop_apply/status`)
}
/**
 * 店铺申请列表
 */
export function getShopApplyList(params: Record<string, any>) {
    return request.get(`mall/shop_apply`,params)
}

/**
 * 店铺申请列表详情
 */
export function getShopApply(apply_id: number) {
    return request.get(`mall/shop_apply/${ apply_id }`)
}

/**
 * 查看申请权限
 */
export function checkhaveAccount(params: number) {
    return request.get(`mall/shop_apply/account_check`,params)
}

/**
 * 店铺申请状态
 */
export function getShopConfig() {
    return request.get(`mall/shop_apply/config`)
}