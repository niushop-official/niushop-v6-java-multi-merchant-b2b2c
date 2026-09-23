/**
 * 添加购物车
 */
export function addCart(data: AnyObject) {
    return request.post(`mall/cart`, data)
}

/**
 * 编辑购物车数量
 */
export function editCart(data: AnyObject) {
    return request.put(`mall/cart`, data)
}

/**
 * 删除购物车
 */
export function deleteCart(data: AnyObject) {
    return request.put(`mall/cart/delete`, data)
}

/**
 * 清空购物车
 */
export function clearCart() {
    return request.delete(`mall/cart/clear`)
}

/**
 * 获取购物车列表
 */
export function getCartList(params: Record<string, any>) {
    return request.get(`mall/cart`, params)
}

/**
 * 获取购物车商品列表
 */
export function getCartGoodsList(params: Record<string, any>) {
    return request.get(`mall/cart/goods`, params)
}

/**
 * 获取购物车数量
 */
export function getCartSum(params: Record<string, any>) {
    return request.get(`mall/cart/sum`, params)
}