import request from '@/utils/request' 

/**
 * 获取小程序发货列表
 * @param params
 * @returns
 */
export function getShippingList(params: Record<string, any>) {
    return request.get(`delivery/site/delivery_weapp/shipping`, {params})
}

/**
 * 重新录入
 * @param params
 * @returns
 */
export function updateShippingRetry(params: Record<string, any>) {
    return request.put(`delivery/site/delivery_weapp/shipping/retry`, params, { showErrorMessage: true, showSuccessMessage: true })
}