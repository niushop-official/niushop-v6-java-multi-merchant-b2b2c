/**
 * 店铺分类列表
 */
export function getShopCategory() {
    return request.get(`shop/web/shop_apply/shop_category`)
}

/**
 * 店铺套餐列表
 */
export function getSiteGroup() {
    return request.get(`shop/web/shop_apply/site_group`)
}

/**
 * 添加店铺申请
 */
export function setShopApply(params: Record<string, any>) {
    return request.post(`shop/web/shop_apply`, params, { showSuccessMessage: true })
}

/**
 * 编辑店铺申请
 */
export function editShopApply(params: Record<string, any>) {
    return request.put(`shop/web/shop_apply/${ params.apply_id }`, params, { showSuccessMessage: true })
}

/**
 * 店铺申请列表
 */
export function getShopApplyList() {
    return request.get(`shop/web/shop_apply`)
}

/**
 * 店铺申请列表详情
 */
export function getShopApply(apply_id: number) {
    return request.get(`shop/web/shop_apply/${ apply_id }`)
}
/**
 * 店铺申请协议
 */
export function getShopApplyAgreement() {
    return request.get(`shop/web/shop_apply/agreement`)
}

/**
 * 查看申请权限
 */
export function checkhaveAccount(params: number) {
    return request.get(`shop/web/shop_apply/account_check`,params)
}
