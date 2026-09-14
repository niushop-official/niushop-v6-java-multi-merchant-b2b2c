import request from '@/utils/request'

/***************************************************** 付费会员 ****************************************************/

/**
 * 获取全部会员等级
 */
export function getVipcardAll() {
    return request.get(`vip_card/lists`);
}

/**
 * 获取付费会员卡列表
 * @param params
 * @returns
 */
export function getVipcardList(params: Record<string, any>) {
    return request.get(`vip_card/pages`, {params})
}

/**
 * 获取付费会员卡详情
 * @param id 会员卡id
 * @returns
 */
export function getVipcardInfo(id: number) {
    return request.get(`vip_card/detail/${id}`);
}

/**
 * 添加付费会员卡
 * @param params
 * @returns
 */
export function addVipcard(params: Record<string, any>) {
    return request.post(`vip_card/add`, params, {showSuccessMessage: true})
}


/**
 * 编辑付费会员卡
 * @param params
 */
export function editVipcard(params: Record<string, any>) {
    return request.put(`vip_card/edit/${params.card_id}`, params, {showSuccessMessage: true})
}

/**
 * 删除付费会员卡
 * @param id
 * @returns
 */
export function deleteVipcard(id: number) {
    return request.delete(`vip_card/del/${id}`, { showSuccessMessage: true })
}

/**
 * 修改会员卡状态
 * @param params
 */
export function updateVipcardStatus(params: Record<string, any>) {
    return request.put(`vip_card/status`, params, { showSuccessMessage: true })
}

/***************************************************** 会员权益 ****************************************************/

/**
 * 获取会员卡权益列表
 * @returns
 */
export function getBenefitsList() {
    return request.get(`vip_card/benefits/list`)
}


/**
 * 获取全部权益
 */
export function getBenefitsAll() {
    return request.get(`vip_card/benefits/all`);
}

/**
 * 获取会员卡权益字典
 * @returns
 */
export function getBenefits() {
    return request.get(`vip_card/benefits/dict`)
}

/**
 * 获取会员卡权益配置
 * @param type 权益配置类型
 * @returns
 */
export function getBenefitsConfig(type: string) {
    return request.get(`vip_card/benefits/config/${type}`);
}

/**
 * 会员卡权益规则设置
 * @param params
 * @returns
 */
export function setBenefitsConfig(params: Record<string, any>) {
    return request.put(`vip_card/benefits/config/${params.type}`, params, { showSuccessMessage: true })
}

/**
 * 修改会员卡权益状态
 * @param params
 */
export function updateBenefitsStatus(params: Record<string, any>) {
    return request.put(`vip_card/benefits/status`, params, { showSuccessMessage: true })
}

/**
 * 修改会员卡权益排序号
 * @param params
 */
export function modifyBenefitsSort(params: Record<string, any>) {
    return request.put(`vip_card/benefits/sort`, params, { showSuccessMessage: true })
}

/***************************************************** 购卡会员 ****************************************************/

/**
 * 获取购卡会员列表
 * @param params
 * @returns
 */
export function getBuycardList(params: Record<string, any>) {
    return request.get(`vip_card/buy/pages`, {params})
}

/**
 * 获取购卡会员详情
 * @param id id
 * @returns
 */
export function getBuycardInfo(id: number) {
    return request.get(`vip_card/buy/detail/${id}`);
}

/***************************************************** 购卡订单 ****************************************************/

/**
 * 获取购卡会员列表
 * @param params
 * @returns
 */
export function getOrderList(params: Record<string, any>) {
    return request.get(`vip_card/order/pages`, {params})
}

/**
 * 获取购卡会员详情
 * @param id id
 * @returns
 */
export function getOrderDetail(id: number) {
    return request.get(`vip_card/order/detail/${id}`);
}


/**
 * 设置会员卡配置
 * @param params
 */
export function setVipcardConfig(params: Record<string, any>) {
    return request.put(`vip_card/config`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取会员卡配置
 * @returns
 */
export function getVipcardConfig() {
    return request.get(`vip_card/config`)
}