import request from '@/utils/request'

/**
 * 获取商品列表
 * @param params
 * @returns
 */
export function getGoodsPageList(params: Record<string, any>) {
    return request.get(`supply/admin/goods`, { params })
}

/**
 * 获取商品详情
 * @param goods_id 商品goods_id
 * @returns
 */
export function getGoodsInfo(goods_id: number) {
    return request.get(`supply/admin/goods/${goods_id}`);
}

/**
 * 修改商品排序号
 * @param params
 */
export function editGoodsSort(params: Record<string, any>) {
    return request.put(`supply/admin/goods/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改商品上下架状态
 * @param params
 */
export function editGoodsStatus(params: Record<string, any>) {
    return request.put(`supply/admin/goods/status`, params, { showSuccessMessage: true })
}

/**
 * 获取商品选择分页列表
 * @param params
 * @returns
 */
export function getGoodsSelectPageList(params: Record<string, any>) {
    return request.get(`supply/admin/goods/select`, { params })
}

/**
 * 获取商品SKU规格列表
 * @param params
 * @returns
 */
export function getGoodsSkuList(params: Record<string, any>) {
    return request.get(`supply/admin/goods/sku`, { params })
}

/**
 * 获取商品SKU规格不分页列表
 * @param params
 * @returns
 */
export function getGoodsSkuNoPageList(params: Record<string, any>) {
    return request.get(`supply/admin/goods/selectgoodssku`, { params })
}

/**
 * 获取商品类型
 * @returns
 */
export function getGoodsType() {
    return request.get(`supply/admin/goods/type`);
}

/**
 * 获取商品状态
 * @returns
 */
export function getGoodsStatus() {
    return request.get(`supply/admin/goods/status`);
}

/**
 * 商品审核通过
 * @param params
 */
export function audit(params: Record<string, any>) {
    return request.put(`supply/admin/goods/audit`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品审核拒绝
 * @param params
 */
export function refuse(params: Record<string, any>) {
    return request.put(`supply/admin/goods/refuse`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品强制下架
 * @param params
 */
export function offline(params: Record<string, any>) {
    return request.put(`supply/admin/goods/offline`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品配置
 * @returns
 */
export function getConfig() {
    return request.get('supply/admin/goods/config')
}

/**
 * 编辑商品配置
 * @returns
 */
export function setConfig(param: any) {
    return request.post('supply/admin/goods/config', param, { showSuccessMessage: true })
}
