import request from '@/utils/request'

/**
 * 获取内容分页列表
 * @param params
 * @returns
 */
export function getContentList(params: Record<string, any>) {
    return request.get(`sow_community/site/content`, { params })
}

/**
 * 获取内容状态
 * @returns
 */
export function getContentStatus() {
    return request.get(`sow_community/site/content/status`)
}

/**
 * 获取内容详情
 * @param content_id 社区分类content_id
 * @returns
 */
export function getContentInfo(content_id: number) {
    return request.get(`sow_community/site/content/${ content_id }`);
}

/**
 * 添加内容
 * @param params
 * @returns
 */
export function addContent(params: Record<string, any>) {
    return request.post('sow_community/site/content', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑内容
 * @param params
 * @returns
 */
export function editContent(params: Record<string, any>) {
    return request.put(`sow_community/site/content/${ params.content_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除社区
 * @param params
 */
export function deleteContent(params: Record<string, any>) {
    return request.put(`sow_community/site/content/delete`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 强制下架
 * @param params
 */
export function offContent(params: Record<string, any>) {
    return request.put(`sow_community/site/content/off`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 审核内容
 * @param params
 * @returns
 */
export function auditContent(params: Record<string, any>) {
    return request.put(`sow_community/site/content/audit/${ params.content_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取内容选择分页列表
 * @param params
 * @returns
 */
export function getContentSelectList(params: Record<string, any>) {
    return request.get(`sow_community/site/content/select`, { params })
}

/**
 * 获取是否允许评论（社区配置）
 * @param params
 * @returns
 */
export function getSowCommunityConfig(params: Record<string, any>) {
    return request.get(`sow_community/site/config`, { params })
}