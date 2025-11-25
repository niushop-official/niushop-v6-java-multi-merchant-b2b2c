import request from '@/utils/request'

/**
 * 获取获取分账列表
 * @param params
 * @returns
 */
export function getSpearList(params: Record<string, any>) {
    return request.get(`spear/admin`, { params })
}

/**
 * 获取分账详情
 */
export function getSpearInfo(id: number) {
    return request.get(`spear/admin/${id}`)
}

/**
 * 获取分账明细
 */
export function getSpearDetail(id: number) {
    return request.get(`spear/admin/detail/${id}`)
}

/**
 * 获取分账状态
 */
export function getSpearStatus() {
    return request.get(`spear/admin/status`)
}

/**
 * 分账
 */
export function setSpear(params: Record<string, any>) {
    return request.post(`spear/admin/separ/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取获取分账列表
 * @param params
 * @returns
 */
export function getSiteSpearList(params: Record<string, any>) {
    return request.get(`spear/site`, { params })
}

/**
 * 获取分账明细
 */
export function getSiteSpearDetail(id: number) {
    return request.get(`spear/site/detail/${id}`)
}