import request from '@/utils/request'

/**
 * 获取拼团配置
 * @returns
 */
export function getPintuanConfig() {
    return request.get(`pintuan/site/config`)
}

/**
 * 编辑拼团配置
 * @param params
 * @returns
 */
export function setPintuanConfig(params: Record<string, any>) {
    return request.post(`pintuan/site/config`, params, { showSuccessMessage: true })
}

/**
 * 获取分享关键字
 * @returns
 */
export function getPintuanShareKeyword() {
    return request.get(`pintuan/site/share`)
}

/**
 * 获取分享默认数据
 * @returns
 */
export function getPintuanShareInit() {
    return request.get(`pintuan/site/share_init`)
}