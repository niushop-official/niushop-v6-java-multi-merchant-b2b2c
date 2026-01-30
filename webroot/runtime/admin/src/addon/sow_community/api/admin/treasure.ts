import request from '@/utils/request'

/**
 * 获取社区宝贝库分页列表
 * @param params
 * @returns
 */
export function getTreasureList(params: Record<string, any>) {
    return request.get(`sow_community/admin/treasure`, { params })
}

/**
 * 获取社区宝贝库类型列表
 * @returns
 */
export function getTreasureType() {
    return request.get(`sow_community/admin/treasure/type`)
}

/**
 * 修改宝贝是否参与种草
 * @param params
 * @returns
 */
export function treasureJoin(params: Record<string, any>) {
    return request.put(`sow_community/admin/treasure/join`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取社区宝贝库选择宝贝列表
 * @param params
 * @returns
 */
export function getTreasureSelect(params: Record<string, any>) {
    return request.get(`sow_community/admin/treasure/select`, { params })
}

/**
 *社区宝贝库已选宝贝列表
 * @param params
 * @returns
 */
export function getTreasureSelected(params: Record<string, any>) {
    return request.get(`sow_community/admin/treasure/selected`, { params })
}

/**
 *社区宝贝库-宝贝状态
 * @param params
 * @returns
 */
 export function getTreasureStatus() {
    return request.get(`sow_community/admin/treasure/status`)
}

/**
 *社区宝贝库-宝贝审核通过
 * @param params
 * @returns
 */
 export function setTreasureAgree(params: Record<string, any>) {
    return request.put(`sow_community/admin/treasure/agree`, params)
}

/**
 *社区宝贝库-宝贝审核拒绝
 * @param params
 * @returns
 */
 export function setTreasureRefuse(params: Record<string, any>) {
    return request.put(`sow_community/admin/treasure/refuse`, params)
}