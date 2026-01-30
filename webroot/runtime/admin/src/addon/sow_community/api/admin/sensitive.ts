import request from '@/utils/request'

/**
 * 获取敏感词设置
 * @returns
 */
export function getSensitive() {
    return request.get('sow_community/admin/sensitive')
}

/**
 * 编辑取敏感词设置
 * @returns
 */
export function setSensitive(param: any) {
    return request.post('sow_community/admin/sensitive', param, { showSuccessMessage: true })
}