import request from '@/utils/request'

/**
 * 获取用户信息
 * @returns
 */
export function getUserInfo() {
    return request.get(`auth/get`)
}

/**
 * 设置用户信息
 * @returns
 */
export function setUserInfo(params: Record<string, any>) {
    return request.put(`auth/edit`, params, {showSuccessMessage: true});
}

