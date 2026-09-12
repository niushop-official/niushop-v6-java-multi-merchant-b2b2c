import request from '@/utils/request'

/***************************************************** 店铺管理 ****************************************************/

/**
 * 获取店铺分页列表
 * @param params
 * @returns
 */
export function getShopPageList(params: Record<string, any>) {
    return request.get(`mall/admin/shop`, { params })
}

/**
 * 获取店铺列表
 * @param params
 * @returns
 */
export function getShopSelect(params: Record<string, any>) {
    return request.get(`mall/admin/shop/select`, { params })
}

/**
 * 获取店铺列表
 * @param params
 * @returns
 */
export function getShopList(params: Record<string, any>) {
    return request.get(`mall/admin/shop/list`, { params })
}

/**
 * 获取店铺详情
 * @param site_id
 */
export function getShopInfo(site_id: number) {
    return request.get(`mall/admin/shop/${site_id}`);
}

/**
 * 添加店铺
 * @param params
 * @returns
 */
export function addShop(params: Record<string, any>) {
    return request.post('mall/admin/shop', params, { showSuccessMessage: true })
}

/**
 * 更新店铺
 * @param params
 */
export function editShop(params: Record<string, any>) {
    return request.put(`mall/admin/shop/${params.site_id}`, params, { showSuccessMessage: true })
}

/***************************************************** 店铺申请管理 ****************************************************/

/**
 * 获取店铺申请列表
 * @param params
 * @returns
 */
export function getShopApplyList(params: Record<string, any>) {
    return request.get(`mall/admin/shop_apply`, { params })
}

/**
 * 获取店铺申请详情
 * @param apply_id
 */
export function getShopApplyInfo(apply_id: number) {
    return request.get(`mall/admin/shop_apply/${apply_id}`)
}

/**
 * 审核店铺申请
 * @param params
 */
export function verifyShopApply(params: Record<string, any>) {
    return request.put(`mall/admin/shop_apply/verify/${params.apply_id}`, params, { showSuccessMessage: false })
}

/**
 * 删除店铺申请
 * @param apply_id
 */
export function deleteShopApply(apply_id: number) {
    return request.delete(`mall/admin/shop_apply/${apply_id}`, { showSuccessMessage: true });
}

/**
 * 获取全部店铺申请状态
 */
export function getApplyStatusList() {
    return request.get(`mall/admin/shop_apply/statuslist`)
}

/**
 * 获取表单配置
 * @returns
 */
export function getApplyConfig() {
    return request.get('mall/admin/shop_apply/config')
}

/**
 * 编辑表单配置
 * @returns
 */
export function setApplyConfig(param: any) {
    return request.post('mall/admin/shop_apply/config', param, { showSuccessMessage: true })
}