import request from '@/utils/request'

/**
 * 获取社区评论分页列表
 * @param params
 * @returns
 */
export function getCommentList(params: Record<string, any>) {
    return request.get(`sow_community/admin/comment`, { params })
}

/**
 * 获取社区评论详情
 * @param comment_id 社区分类comment_id
 * @returns
 */
export function getCommentInfo(comment_id: number) {
    return request.get(`sow_community/admin/comment/${ comment_id }`);
}

/**
 * 添加社区评论
 * @param params
 * @returns
 */
export function addComment(params: Record<string, any>) {
    return request.post('sow_community/admin/comment', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除社区评论
 * @param comment_id
 * @returns
 */
export function deleteComment(comment_id: number) {
    return request.delete(`sow_community/admin/comment/${ comment_id }`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 批量删除社区评论
 * @param params
 * @returns
 */
export function batchDelteComment(params: Record<string, any>) {
    return request.post(`sow_community/admin/comment/batch_del`, params ,{ showSuccessMessage: true })
}

/**
 * 批量审核社区评论
 * @param params
 * @returns
 */
export function batchAuditComment(params: Record<string, any>) {
    return request.put(`sow_community/admin/comment/batch_audit`, params ,{ showSuccessMessage: true })
}

/**
 * 审核社区评论
 * @param params
 * @returns
 */
export function auditComment(params: Record<string, any>) {
    return request.put(`sow_community/admin/comment/audit/${ params.comment_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取社区评论状态列表
 */
export function getCommentStatus() {
    return request.get(`sow_community/admin/comment/status`)
}
