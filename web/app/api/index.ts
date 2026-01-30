/**
 * 获取广告位
 */
export function getAdvInfo(params: Record<string, any>) {
    return request.get(`diy/adv`, params)
}

/**
 * 获取web端配置
 */
export function getWebConfig() {
    return request.get(`web/config`)
}
/**
 * 获取导航列表
 */
export function getNavList() {
    return request.get(`web/nav`)
}
/**
 * 获取友情链接
 */
export function getFriendlyLink() {
    return request.get(`web/friendly_link`)
}
/**
 * 获取首页楼层
 */
export function getFloor(params: Record<string, any>) {
    return request.get(`mall/web/floor`, params)
}
/**
 * 获取商品分类
 */
export function getCategoryTree() {
    return request.get(`basic/goods/category/tree`)
}

/**
 * 获取商品服务
 */
export function getGoodsService() {
    return request.get(`basic/goods/service`)
}

/**
 * 获取店铺
 */
export function getWebShopList() {
    return request.get(`shop/web/select_shop_list`)
}


/**
 * 查看申请权限
 */
export function checkShopStatus() {
    return request.get(`shop/web/shop_apply/check`)
}


/**
 * 查看申请权限
 */
export function getQrcode(params: Record<string, any>) {
    return request.get(`qrcode/index`, params)
}
