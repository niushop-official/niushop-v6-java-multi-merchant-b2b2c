import request from '@/utils/request'

/**
 * 获取收款方式配置校验
 */
export function getAccountCheck() {
    return request.get(`shop/site/shop/account/check`)
}

/**
 * 获取店铺转账方式
 */
export function getTransfertype() {
    return request.get(`shop/site/shop/cash_out/transfer_type`)
}

/**
 * 申请提现
 */
export function getCashApply(params: Record<string, any>) {
    return request.post(`shop/site/shop/cash_apply`, params)
}

/**
 * 获取店铺提现记录
 */
export function getCashOutList(params: Record<string, any>) {
    return request.get(`shop/site/shop/cash_out`, params)
}

/**
 * 获取店铺提现详情
 */
export function getShopCashOutInfo(id: number) {
    return request.get(`shop/site/shop/cash_out/${id}`)
}

/**
 * 获取店铺结算账户列表
 */
export function getShopSettAccountList() {
    return request.get(`site/sett_account`)
}

/**
 * 获取店铺结算账户详情
 * @param id 结算账户id
 */
export function getShopSettAccountInfo(id: number) {
    return request.get(`site/sett_account/${id}`)
}

/**
 * 添加店铺结算账户
 */
export function addShopSettAccount(params: Record<string, any>) {
    return request.post(`site/sett_account`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 修改店铺结算账户
 */
export function editShopSettAccount(params: Record<string, any>) {
    return request.put(`site/sett_account/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除店铺结算账户
 */
export function delShopSettAccount(id: number) {
    return request.delete(`site/sett_account/${id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取店铺信息
 */
export function getShopAccountInfo() {
    return request.get(`shop/site/shop/account/info`)
}

/**
 * 获取店铺账单记录
 */
export function getShopAccountLog(params: Record<string, any>) {
    return request.get(`shop/site/shop/account/log`, params)
}

/**
 * 获取店铺账单类型列表
 */
export function getShopAccountTypeList() {
    return request.get(`shop/site/shop/account/typelist`)
}

/**
 * 设置店铺收款方式
 */
export function setShopAccount(params: Record<string, any>) {
    return request.put(`shop/site/shop/account/set`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取店铺账单类型列表
 */
export function getShopCashOutStatus() {
    return request.get(`shop/site/shop/cash_out/status`)
}

/**
 * 获取店铺提现
 */
export function getShopCashOutTransfer(id: number) {
    return request.get(`shop/site/shop/cash_out/transfer/${id}`)
}
/**
 * 店铺提现取消
 */
export function transferShopCashCancel(id: number) {
    return request.put(`/shop/site/shop/cash_cancel/${id}`, {}, { showSuccessMessage: true })
}

/**
 * 获取账单最后一条
 */
export function getFinanceLatest() {
    return request.get(`site/finance/latest`)
}

/**
 * 获取最近三十天收入
 */
export function getShopinComeOfRange(range: string) {
    return request.get(`shop/site/shop/income_of_range/${range}`)
}
