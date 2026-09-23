import request from '@/utils/request'
/**
 * 获取店铺提现记录详情
 */
export function getTransferDetail(id: number) {
    return request.get(`shop/web/cash_out/${ id }`)
}

/**
 * 获取店铺提现转账
 */
export function getTransferShop(data: AnyObject) {
    return request.post(`shop/web/cash_out/transfer/${ data.id }`, data)
}

/**
 * 获取店铺提现记录详情
 */
export function getTransferCheck(data: AnyObject) {
    return request.get(`shop/web/cash_out/transfer/check`, data, { showErrorMessage: true })
}

/**
 * 获取店铺小程序二维码
 */
export function getQrcode(data: AnyObject) {
    return request.get(`qrcode/index`, data)
}

/**
 * 收藏取消店铺
 * @param params
 */
export function editShopCollect(params: Record<string, any>) {
    return request.put(`shop/web/shop/${ params.site_id }/${ params.is_follow }/${ params.addon }`)
}

/**
 * 获取店铺收藏列表
 */
export function getShopFollowList(params: Record<string, any>) {
    return request.get(`shop/web/follow`, params)
}