import request from '@/utils/request'

/**
 * 获取商品列表
 * @param params
 * @returns
 */
export function getGoodsPageList(params: Record<string, any>) {
    return request.get(`mall/admin/goods`, { params })
}

/**
 * 获取商品详情
 * @param goods_id 商品goods_id
 * @returns
 */
export function getGoodsInfo(goods_id: number) {
    return request.get(`mall/admin/goods/${goods_id}`);
}

/**
 * 修改商品排序号
 * @param params
 */
export function editGoodsSort(params: Record<string, any>) {
    return request.put(`mall/admin/goods/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改商品上下架状态
 * @param params
 */
export function editGoodsStatus(params: Record<string, any>) {
    return request.put(`mall/admin/goods/status`, params, { showSuccessMessage: true })
}

/**
 * 获取商品选择分页列表
 * @param params
 * @returns
 */
export function getGoodsSelectPageList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/select`, { params })
}

/**
 * 获取商品SKU规格列表
 * @param params
 * @returns
 */
export function getGoodsSkuList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/sku`, { params })
}

/**
 * 获取商品SKU规格不分页列表
 * @param params
 * @returns
 */
export function getGoodsSkuNoPageList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/selectgoodssku`, { params })
}

/**
 * 获取商品类型
 * @returns
 */
export function getGoodsType() {
    return request.get(`mall/admin/goods/type`);
}

/**
 * 获取商品类型
 * @returns
 */
export function getGoodsStatusType() {
    return request.get(`mall/admin/goods/status_type`);
}

/**
 * 获取商品状态
 * @returns
 */
export function getGoodsStatus() {
    return request.get(`mall/admin/goods/status`);
}

/**
 * 设置商品分类配置
 * @param params
 * @returns
 */
export function setCategoryConfig(params: Record<string, any>) {
    return request.post(`mall/admin/goods/category/config`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品分类配置
 * @returns
 */
export function getCategoryConfig() {
    return request.get(`mall/admin/goods/category/config`);
}






/**
 * 获取供货商列表
 * @param params
 * @returns
 */
export function getSupplierList(params: Record<string, any>) {
    return request.get(`mall_supplier/supplier/list`, { params })
}

/**
 * 获取商品评价列表
 * @param params
 * @returns
 */
export function getEvaluateList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/evaluate`, { params })
}

/**
 * 添加商品评价
 * @param params
 * @returns
 */
export function addEvaluate(params: Record<string, any>) {
    return request.post('mall/admin/goods/evaluate', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除商品评价
 * @param evaluate_id
 * @returns
 */
export function deleteEvaluate(evaluate_id: number) {
    return request.delete(`mall/admin/goods/evaluate/${evaluate_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 商品评价 通过
 * @param evaluate_id
 * @returns
 */
export function adoptEvaluate(evaluate_id: number) {
    return request.put(`mall/admin/goods/evaluate/adopt/${evaluate_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 商品评价 拒绝
 * @param evaluate_id
 * @returns
 */
export function refuseEvaluate(evaluate_id: number) {
    return request.put(`mall/admin/goods/evaluate/refuse/${evaluate_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 商品评价 回复
 * @param params
 * @returns
 */
export function replyEvaluate(params: Record<string, any>) {
    return request.put(`mall/admin/goods/evaluate/reply/${params.evaluate_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 商品评价 置顶
 * @param evaluate_id
 * @returns
 */
export function toppingEvaluate(evaluate_id: number) {
    return request.put(`mall/admin/goods/evaluate/topping/${evaluate_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 商品评价 取消置顶
 * @param evaluate_id
 * @returns
 */
export function cancelToppingEvaluate(evaluate_id: number) {
    return request.put(`mall/admin/goods/evaluate/cancel_topping/${evaluate_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 商品审核通过
 * @param params
 */
export function audit(params: Record<string, any>) {
    return request.put(`mall/admin/goods/audit`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品审核拒绝
 * @param params
 */
export function refuse(params: Record<string, any>) {
    return request.put(`mall/admin/goods/refuse`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品强制下架
 * @param params
 */
export function offline(params: Record<string, any>) {
    return request.put(`mall/admin/goods/offline`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品配置
 * @returns
 */
export function getConfig() {
    return request.get('mall/admin/goods/config')
}

/**
 * 编辑商品配置
 * @returns
 */
export function setConfig(param: any) {
    return request.post('mall/admin/goods/config', param, { showSuccessMessage: true })
}

/**
 * 获取商品来源
 * @returns
 */
export function getGoodsSource() {
    return request.get(`mall/admin/goods/source`);
}

/*********************************  商品标签  ***************************************/

/**
 * 获取商品标签分页列表
 * @param params
 * @returns
 */
export function getLabelPageList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/label`, { params })
}

/**
 * 获取商品标签详情
 * @param label_id 商品标签label_id
 * @returns
 */
export function getLabelInfo(label_id: number) {
    return request.get(`basic/admin/goods/label/${label_id}`);
}

/**
 * 添加商品标签
 * @param params
 * @returns
 */
export function addLabel(params: Record<string, any>) {
    return request.post('basic/admin/goods/label', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商品标签
 * @param params
 * @returns
 */
export function editLabel(params: Record<string, any>) {
    return request.put(`basic/admin/goods/label/${params.label_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 修改商品标签状态
 * @param params
 */
export function modifyLabelStatus(params: Record<string, any>) {
    return request.put(`basic/admin/goods/label/status`, params, { showSuccessMessage: true })
}

/**
 * 删除商品标签
 * @param label_id
 * @returns
 */
export function deleteLabel(label_id: number) {
    return request.delete(`basic/admin/goods/label/${label_id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 修改商品标签排序号
 * @param params
 */
export function modifyLabelSort(params: Record<string, any>) {
    return request.put(`basic/admin/goods/label/sort`, params, { showSuccessMessage: true })
}

/**
 * 复制标签
 * @param params
 */
export function copyLabel(params: Record<string, any>) {
    return request.post(`basic/admin/goods/label/copy/${ params.label_id }`, params, { showSuccessMessage: true })
}
