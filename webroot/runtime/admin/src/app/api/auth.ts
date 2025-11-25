import request from '@/utils/request'

/**
 * 登录
 * @param params
 * @param app_type
 */
export function login(params: Record<string, any>, app_type: string) {
    return request.get(`login/${app_type}`, {params})
}

/**
 * 退出登录
 */
export function logout() {
    return request.put('auth/logout', {}, { showErrorMessage: false })
}

/**
 * 获取登录用户权限（平台端）
 * @returns
 */
export function getAdminAuthMenus(params: Record<string, any>) {
    return request.get('auth/admin/authmenu', {params})
}

/**
 * 获取登录用户权限（店铺端）
 * @returns
 */
export function getSiteAuthMenus(params: Record<string, any>) {
    return request.get('auth/site/authmenu', {params})
}

/**
 * 获取登录用户权限
 * @returns
 */
export function getSiteInfo() {
    const timestamp = new Date().getTime();
    return request.get(`auth/site?timestamp=${timestamp}`)
}

/**
 * 获取登录配置信息
 * @returns
 */
export function getLoginConfig() {
    return request.get('login/config')
}

/**
 * 获取当前版本信息
 */
export function getVersions() {
    return request.get(`sys/info`)
}

/**
 * 获取当前版本信息
 */
export function getAddons() {
    return request.get(`addon/cache`)
}

