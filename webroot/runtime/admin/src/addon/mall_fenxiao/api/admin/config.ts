import request from '@/utils/request'

/**
 * 获取分销设置
 */
export function getFenxiaoConfig() {
    return request.get('mall_fenxiao/admin/config/fenxiao');
}

/**
 * 编辑分销设置
 * @param params
 * @returns
 */
export function setFenxiaoConfig(params: Record<string, any>) {
    return request.put('mall_fenxiao/admin/config/fenxiao', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取分销海报设置
 */
export function getFenxiaoPosterConfig() {
    return request.get('mall_fenxiao/admin/config/fenxiao_poster');
}

/**
 * 编辑分销海报设置
 * @param params
 * @returns
 */
export function setFenxiaoPosterConfig(params: Record<string, any>) {
    return request.put('mall_fenxiao/admin/config/fenxiao_poster', params, { showErrorMessage: true, showSuccessMessage: true })
}
