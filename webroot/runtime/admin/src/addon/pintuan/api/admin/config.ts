import request from '@/utils/request'

/**
 * 获取拼团配置
 * @returns
 */
export function getPintuanAdminConfig() {
    return request.get(`pintuan/admin/config`)
}

/**
 * 编辑拼团配置
 * @param params
 * @returns
 */
export function setPintuanAdminConfig(params: Record<string, any>) {
    return request.post(`pintuan/admin/config`, params, { showSuccessMessage: true })
}

/**
 * 获取分享关键字
 * @returns
 */
export function getPintuanAdminShareKeyword() {
    return request.get(`pintuan/admin/share`)
}

/**
 * 获取分享默认数据
 * @returns
 */
export function getPintuanAdminShareInit() {
    return request.get(`pintuan/admin/share_init`)
}
