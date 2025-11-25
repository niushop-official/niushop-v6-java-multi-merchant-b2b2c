import request from '@/utils/request'

//包含站点用户管理，站点操作日志, 账单列表

/***************************************************** 当前站点用户 *************************************************/

/**
 * 获取站点用户列表
 * @param params
 * @returns
 */
export function getUserList(params: Record<string, any>) {
    return request.get(`site/user`, { params })
}

/**
 * 获取站点用户详情
 * @param uid
 */
export function getUserInfo(uid: number) {
    return request.get(`site/user/${uid}`);
}

/**
 * 添加用户
 * @param params
 * @returns
 */
export function addUser(params: Record<string, any>) {
    return request.post('site/user', params, { showSuccessMessage: true })
}

/**
 * 编辑用户
 * @param params
 */
export function editUser(params: Record<string, any>) {
    return request.put(`site/user/${params.uid}`, params, { showSuccessMessage: true })
}

/**
 * 锁定用户
 * @param uid
 * @returns
 */
export function lockUser(uid: number) {
    return request.put(`site/user/lock/${uid}`)
}


/**
 * 解锁用户
 * @param uid
 * @returns
 */
export function unlockUser(uid: number) {
    return request.put(`site/user/unlock/${uid}`)
}


/***************************************************** 操作日志 **************************************************/

/**
 * 获取操作日志列表
 * @param params
 * @returns
 */
export function getLogList(params: Record<string, any>) {
    return request.get(`site/log`, { params })
}

/**
 * 获取操作日志详情
 * @param id
 */
export function getLogInfo(id: number) {
    return request.get(`site/log/${id}`)
}

/**
 * 清空操作日志列表
 * @returns
 */
export function logDestroy() {
    return request.delete(`site/log/destroy`)
}

/***************************************************** 账单列表 **************************************************/

/**
 * 获取账单列表
 * @param params
 * @returns
 */
export function getAccountList(params: Record<string, any>) {
    return request.get(`site/account`, { params })
}

/**
 * 获取账单列表
 * @param params
 * @returns
 */
export function getFinanceList(params: Record<string, any>) {
    return request.get(`site/finance`, { params })
}
/**
 * 获取账单统计
 * @returns
 */
export function getAccountStat() {
    return request.get(`site/account/stat`)
}

/**
 * 获取账单类型
 * @returns
 */
export function getAccountType() {
    return request.get(`site/account/type`)
}

/**
 * 获取支付列表
 * @param params
 * @returns
 */
export function getPayList(params: Record<string, any>) {
    return request.get(`pay/list`, { params })
}

/**
 * 获取按支付方式统计数据
 * @returns
 */
export function getPayTypeStat() {
    return request.get(`pay/stat/type`)
}

/**
 * 获取按支付渠道统计数据
 * @returns
 */
export function getChannelStat() {
    return request.get(`pay/stat/channel`)
}

/**
 * 获取支付方式列表
 * @returns
 */
export function getPayTypeList() {
    return request.get(`pay/type/list`)
}

/**
 * 获取支付方式端口
 * @returns
 */
export function getChannelList() {
    return request.get(`pay/channel/list`)
}

/**
 * 获取店铺端应用
 * @returns
 */
export function getSiteApp() {
    return request.get('site/siteapp')
}

/**
 * 获取平台端应用
 * @returns
 */
export function getAdminApp() {
    return request.get('site/adminapp')
}
