import request from '@/utils/request'

/**
 * 获取社区宝贝库分页列表
 * @param params
 * @returns
 */
export function getTreasureList(params: Record<string, any>) {
    return request.get(`sow_community/site/treasure`, { params })
}

/**
 * 获取社区宝贝库类型列表
 * @returns
 */
export function getTreasureType() {
    return request.get(`sow_community/site/treasure/type`)
}

/**
 * 修改宝贝是否参与种草
 * @param params
 * @returns
 */
export function treasureJoin(params: Record<string, any>) {
    return request.put(`sow_community/site/treasure/join`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取社区宝贝库选择宝贝列表
 * @param params
 * @returns
 */
export function getTreasureSelect(params: Record<string, any>) {
    return request.get(`sow_community/site/treasure/select`, { params })
}

/**
 *社区宝贝库已选宝贝列表
 * @param params
 * @returns
 */
export function getTreasureSelected(params: Record<string, any>) {
    return request.get(`sow_community/site/treasure/selected`, { params })
}

/**
 *社区宝贝库-提交审核
 * @param params
 * @returns
 */
 export function setTreasureApply(params: Record<string, any>) {
    return request.post(`sow_community/site/treasure/apply`, params, { showErrorMessage: true, showSuccessMessage: true })
}
