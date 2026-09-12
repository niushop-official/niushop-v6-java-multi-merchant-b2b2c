import request from '@/utils/request'

//包含店铺管理，店铺用户管理，店铺操作日志

/***************************************************** 店铺管理 ****************************************************/

/**
 * 获取店铺列表
 * @param params
 * @returns
 */
export function getShopSelect(params: Record<string, any>) {
    return request.get(`mall/admin/shop/select`, { params })
}

/**
 * 删除店铺
 * @param siteId
 */
export function deleteShop(siteId: number) {
    return request.delete(`shop/admin/shop/${siteId}`)
}

/**
 * 关闭店铺
 * @param params
 */
export function closeShop(params: Record<string, any>) {
    return request.put(`shop/admin/shop/close/${params.site_id}`, params, { showSuccessMessage: true })
}

/**
 * 打开店铺
 * @param params
 */
export function openShop(params: Record<string, any>) {
    return request.put(`shop/admin/shop/open/${params.site_id}`, params, { showSuccessMessage: true })
}

/**
 * 获取全部店铺状态
 */
export function getStatusList() {
    return request.get(`shop/admin/shop/statuslist`)
}

/**
 * 获取店铺账户信息
 */
export function getShopAccount(id: number) {
    return request.get(`shop/admin/shop/account/${id}`)
}
/**
 * 获取店铺账户收支信息
 */
export function getShopAccountLog(params: Record<string, any>) {
    return request.get(`shop/admin/shop/accountLog/${params.id}`, { params })
}
/**
 * 获取店铺提现记录信息
 */
export function getShopWithdrawal(params: Record<string, any>) {
    return request.get(`shop/admin/shop/cashout/${params.id}`, { params })
}
/**
 * 设置店铺账户信息
 */
export function setShopAccount(params: Record<string, any>) {
    return request.put(`shop/admin/shop/account/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}
/**
 * 设置店铺信息
 */
export function setShopInfo(params: Record<string, any>) {
    return request.put(`shop/admin/shop/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}


/***************************************************** 店铺分组管理 ****************************************************/

/**
 * 获取店铺分组列表
 * @param params
 * @returns
 */
export function getShopGroupList(params: Record<string, any>) {
    return request.get(`shop/admin/shop_group`, { params })
}

/**
 * 获取店铺分组详情
 * @param groupId
 */
export function getShopGroupInfo(groupId: number) {
    return request.get(`shop/admin/shop_group/${groupId}`);
}

/**
 * 添加店铺分组
 * @param params
 * @returns
 */
export function addShopGroup(params: Record<string, any>) {
    return request.post('shop/admin/shop_group', params, { showSuccessMessage: true })
}

/**
 * 更新店铺分组
 * @param params
 */
export function editShopGroup(params: Record<string, any>) {
    return request.put(`shop/admin/shop_group/${params.group_id}`, params, { showSuccessMessage: true })
}

/**
 * 删除店铺分组
 * @param group_id
 */
export function deleteShopGroup(group_id: number) {
    return request.delete(`shop/admin/shop_group/${group_id}`, { showSuccessMessage: true })
}

/**
 * 获取全部店铺分组
 * @param params
 * @returns
 */
export function getShopGroupAll(params: Record<string, any> = {}) {
    return request.get(`shop/admin/shop_group/all`, { params })
}

/***************************************************** 店铺申请管理 ****************************************************/

/**
 * 获取店铺申请列表
 * @param params
 * @returns
 */
export function getShopApplyList(params: Record<string, any>) {
    return request.get(`shop/admin/shop_apply`, { params })
}

/**
 * 获取店铺申请详情
 * @param apply_id
 */
export function getShopApplyInfo(apply_id: number) {
    return request.get(`shop/admin/shop_apply/${apply_id}`)
}

/**
 * 审核店铺申请
 * @param params
 */
export function verifyShopApply(params: Record<string, any>) {
    return request.put(`shop/admin/shop_apply/verify/${params.apply_id}`, params, { showSuccessMessage: false })
}

/**
 * 删除店铺申请
 * @param apply_id
 */
export function deleteShopApply(apply_id: number) {
    return request.delete(`shop/admin/shop_apply/${apply_id}`, { showSuccessMessage: true });
}

/**
 * 获取全部店铺申请状态
 */
export function getApplyStatusList() {
    return request.get(`shop/admin/shop_apply/statuslist`)
}


/***************************************************** 店铺提现 ****************************************************/
/**
 * 获取店铺转账方式
 */
export function getShopTransferType() {
    return request.get(`/shop/site/shop/cash_out/transfer_type`)
}
/**
 * 获取店铺提现统计
 */
export function getWithdrawinfo() {
    return request.get(`shop/admin/shop/cashout/withdrawinfo`)
}

/**
 * 获取店铺提现记录
 */
export function getShopCashOutList(params: Record<string, any>) {
    return request.get(`shop/admin/shop/cashout`, { params })
}

/**
 * 获取店铺提现详情
 */
export function getShopCashOutInfo(id: number) {
    return request.get(`shop/admin/shop/cashout/info/${id}`)
}

/**
 * 获取店铺账单类型列表
 */
export function getShopCashOutStatus() {
    return request.get(`shop/admin/shop/cashout/status`)
}

/**
 * 店铺提现审核
 */
export function auditShopCashOut(params: Record<string, any>) {
    return request.put(`shop/admin/shop/cashout/audit/${params.id}`, params, { showSuccessMessage: false })
}

/**
 * 店铺提现转账
 */
export function transferShopCashOutList(params: Record<string, any>) {
    return request.put(`shop/admin/shop/cashout/transfer/${params.id}`, params, { showSuccessMessage: false })
}

/**
 * 获取提现设置
 */
export function getShopCashOutConfig() {
    return request.get(`shop/admin/shop/config/cash_out`)
}

/**
 * 设置提现设置
 * @param params
 * @returns
 */
export function setShopCashOutConfig(params: Record<string, any>) {
    return request.post(`shop/admin/shop/config/cash_out`, params, { showSuccessMessage: true })
}

/**
 * 获取分账账户
 * 
 */
export function getAdminPayment() {
    return request.get(`shop/admin/site_payment`);
}


/**
 * 获取店铺应用列表
 * 
 */
export function getAppList() {
    return request.get(`addon/app/select`);
}

/**
 * 获取店铺插件列表
 * 
 */
export function getAddonList(params: Record<string, any>) {
    return request.get(`addon/addon/select`,{params});
}

/**
 * 修改店铺密码
 * 
 */
export function resetPassword(params: Record<string, any>) {
    return request.put(`auth/site/password/${params.id}`,params,{ showSuccessMessage: true });
}


/**
 * 获取账户入驻申请列表
 * 
 */
export function getAccountApply(params: Record<string, any>) {
    return request.get(`shop/admin/spear_user_apply`,{params});
}

/**
 * 获取账户入驻平台类型
 * 
 */
export function getAccountApplyType() {
    return request.get(`shop/admin/spear_user_apply/type`);
}

/**
 * 获取账户入驻平台详情
 * @param apply_id
 */
export function getAccountApplyInfo(id: number) {
    return request.get(`shop/admin/spear_user_apply/${id}`)
}

/**
 * 获取账户管理列表
 * 
 */
export function getAccount(params: Record<string, any>) {
    return request.get(`shop/admin/spear_user`,{params});
}

/**
 * 获取账户入驻平台详情
 * @param id
 */
export function getAccountInfo(id: number) {
    return request.get(`shop/admin/spear_user/${id}`)
}

/**
 * 获取账户管理状态
 * 
 */
export function getAccountStatus() {
    return request.get(`shop/admin/spear_user/status`);
}

/**
 * 获取账户管理平台类型
 * 
 */
export function getAccountType() {
    return request.get(`shop/admin/spear_user/type`);
}

/**
 * 获取账户管理平台类型
 * 
 */

export function getAccountSettle() {
    return request.get(`shop/admin/spear_user/settle`);
}

/**
 * 绑定账户
 */
export function addByHandle(params: Record<string, any>) {
    return request.post(`shop/admin/spear_user`, params, { showSuccessMessage: true });
}

/**
 * 编辑账户
 */
export function editAccount(params: Record<string, any>) {
    return request.put(`shop/admin/spear_user`, params, { showSuccessMessage: true });
}
