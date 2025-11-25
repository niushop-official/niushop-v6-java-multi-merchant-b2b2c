import request from '@/utils/request'

/**
 * 获取PC导航管理列表
 * @param params
 * @returns
 */
export function getNavList(params: Record<string, any>) {
    return request.get(`web/nav`, {params})
}

/**
 * 获取PC导航管理详情
 * @param id PC导航管理id
 * @returns
 */
export function getNavInfo(id: number) {
    return request.get(`web/nav/${id}`);
}

/**
 * 添加PC导航管理
 * @param params
 * @returns
 */
export function addNav(params: Record<string, any>) {
    return request.post('web/nav', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑PC导航管理
 * @param params
 * @returns
 */
export function editNav(params: Record<string, any>) {
    return request.put(`web/nav/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除PC导航管理
 * @param id
 * @returns
 */
export function deleteNav(id: number) {
    return request.delete(`web/nav/${id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取PC端自定义链接列表
 * @param params
 */
export function getLink(params: Record<string, any>) {
    return request.get(`web/link`, { params })
}

/**
 * 获取友情链接列表
 * @param params
 * @returns
 */
export function getFriendlyLinkList(params: Record<string, any>) {
    return request.get(`web/friendly_link`, {params})
}

/**
 * 获取友情链接详情
 * @param id 友情链接id
 * @returns
 */
export function getFriendlyLinkInfo(id: number) {
    return request.get(`web/friendly_link/${id}`);
}

/**
 * 添加友情链接
 * @param params
 * @returns
 */
export function addFriendlyLink(params: Record<string, any>) {
    return request.post('web/friendly_link', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑友情链接
 * @param params
 * @returns
 */
export function editFriendlyLink(params: Record<string, any>) {
    return request.put(`web/friendly_link/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除友情链接
 * @param id
 * @returns
 */
export function deleteFriendlyLink(id: number) {
    return request.delete(`web/friendly_link/${id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取web配置
 * @returns
 */
export function getWebConfig() {
    return request.get(`web/config`);
}

/**
 * 添加web配置
 * @param params
 * @returns
 */
export function setWebConfig(params: Record<string, any>) {
    return request.post('web/config', params, { showErrorMessage: true, showSuccessMessage: true })
}