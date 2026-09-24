import request from '@/utils/request'

/**
 * 获取店铺基础信息
 */
export function getShopBaseInfo() {
    return request.get(`mall/site/shop/setting`)
}

/**
 * 设置店铺收款方式
 */
export function setShopBaseInfo(params: Record<string, any>) {
    return request.put(`mall/site/shop/setting`, params, { showErrorMessage: true, showSuccessMessage: true })
}