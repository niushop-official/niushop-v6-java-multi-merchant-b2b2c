import request from '@/utils/request'

/**
 * 获取商品列表
 * @param params
 * @returns
 */
export function getGoodsPageList(params: Record<string, any>) {
    return request.get(`mall/site/goods`, { params })
}

/**
 * 获取商品详情
 * @param goods_id 商品goods_id
 * @returns
 */
export function getGoodsInfo(goods_id: number) {
    return request.get(`mall/site/goods/${goods_id}`);
}

/**
 * 添加实物商品
 * @param params
 * @returns
 */
export function addGoods(params: Record<string, any>) {
    return request.post('mall/site/goods', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑实物商品
 * @param params
 */
export function editGoods(params: Record<string, any>) {
    return request.put(`mall/site/goods/${params.goods_id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品添加/编辑初始化数据
 * @param params
 */
export function getGoodsInit(params: Record<string, any>) {
    return request.get(`mall/site/goods/init`, { params });
}

/**
 * 获取商品添加/编辑初始化数据
 * @param params
 */
export function getGoodsSync(params: Record<string, any>) {
    return request.get(`mall/site/goods/sync`, { params });
}

/**
 * 添加虚拟商品
 * @param params
 * @returns
 */
export function addVirtualGoods(params: Record<string, any>) {
    return request.post('mall/site/goods/virtual', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑虚拟商品
 * @param params
 */
export function editVirtualGoods(params: Record<string, any>) {
    return request.put(`mall/site/goods/virtual/${params.goods_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取虚拟商品添加/编辑初始化数据
 * @param params
 */
export function getVirtualGoodsInit(params: Record<string, any>) {
    return request.get(`mall/site/goods/virtual/init`, { params });
}

/**
 * 删除商品
 * @param params
 * @returns
 */
export function deleteGoods(params: Record<string, any>) {
    return request.put(`mall/site/goods/delete`, params, { showErrorMessage: true, showSuccessMessage: true })
}


/**
 * 设置商品分类配置
 * @param params
 * @returns
 */
export function setCategoryConfig(params: Record<string, any>) {
    return request.post(`mall/site/goods/category/config`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品分类配置
 * @returns
 */
export function getCategoryConfig() {
    return request.get(`mall/site/goods/category/config`);
}

/**
 * 获取回收站商品分页列表
 * @param params
 * @returns
 */
export function getRecycleGoodsPageList(params: Record<string, any>) {
    return request.get(`mall/site/goods/recycle`, { params })
}

/**
 * 恢复商品
 * @param params
 * @returns
 */
export function recycleGoods(params: Record<string, any>) {
    return request.put(`mall/site/goods/recycle`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 修改商品排序号
 * @param params
 */
export function editGoodsSort(params: Record<string, any>) {
    return request.put(`mall/site/goods/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改商品上下架状态
 * @param params
 */
export function editGoodsStatus(params: Record<string, any>) {
    return request.put(`mall/site/goods/status`, params, { showSuccessMessage: true })
}

/**
 * 复制商品
 * @param params
 */
export function copyGoods(params: Record<string, any>) {
    return request.put(`mall/site/goods/copy/${params.goods_id}`, params, { showSuccessMessage: true })
}

/**
 * 获取商品选择分页列表
 * @param params
 * @returns
 */
export function getGoodsSelectPageList(params: Record<string, any>) {
    return request.get(`mall/site/goods/select`, { params })
}

/**
 * 获取商品SKU规格列表
 * @param params
 * @returns
 */
export function getGoodsSkuList(params: Record<string, any>) {
    return request.get(`mall/site/goods/sku`, { params })
}

/**
 * 获取商品SKU规格不分页列表
 * @param params
 * @returns
 */
export function getGoodsSkuNoPageList(params: Record<string, any>) {
    return request.get(`mall/site/goods/selectgoodssku`, { params })
}

/** 查询商品参与营销活动的数量
 * @param params
 * @returns
 */
export function getActiveGoodsCount(params: Record<string, any>) {
    return request.get(`mall/site/goods/active/count`, { params })
}
/**
 * 编辑商品SKU规格库存
 * @param params
 * @returns
 */
export function editGoodsListStock(params: Record<string, any>) {
    return request.put(`mall/site/goods/sku/stock`, params, { showSuccessMessage: true })
}

/**
 * 编辑商品SKU规格价格
 * @param params
 * @returns
 */
export function editGoodsListPrice(params: Record<string, any>) {
    return request.put(`mall/site/goods/sku/price`, params, { showSuccessMessage: true })
}

/**
 * 编辑商品SKU规格会员价格
 * @param params
 * @returns
 */
export function editGoodsListMemberPrice(params: Record<string, any>) {
    return request.put(`mall/site/goods/sku/member_price`, params, { showSuccessMessage: true })
}

/**
 * 获取商品类型
 * @returns
 */
export function getGoodsType() {
    return request.get(`mall/site/goods/type`);
}

/**
 * 获取商品状态
 * @returns
 */
export function getGoodsStatus() {
    return request.get(`mall/site/goods/status`);
}

/**
 * 获取商品状态
 * @returns
 */
export function getGoodsStatusType() {
    return request.get(`mall/site/goods/status_type`);
}
/**
 * 获取商品标签列表
 * @param params
 * @returns
 */
export function getLabelList(params: Record<string, any>) {
    return request.get(`mall/site/goods/label/list`, { params })
}

/**
 * 获取支付方式列表
 * @param params
 * @returns
 */
export function getPayTypeList(params: Record<string, any>) {
    return request.get(`mall/site/goods/payTypeList`, { params })
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
    return request.get(`mall/site/goods/evaluate`, { params })
}

/**
 * 添加商品评价
 * @param params
 * @returns
 */
export function addEvaluate(params: Record<string, any>) {
    return request.post('mall/site/goods/evaluate', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除商品评价
 * @param evaluate_id
 * @returns
 */
export function deleteEvaluate(evaluate_id: number) {
    return request.delete(`mall/site/goods/evaluate/${evaluate_id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品评价 通过
 * @param evaluate_id
 * @returns
 */
export function adoptEvaluate(evaluate_id: number) {
    return request.put(`mall/site/goods/evaluate/adopt/${evaluate_id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品评价 拒绝
 * @param evaluate_id
 * @returns
 */
export function refuseEvaluate(evaluate_id: number) {
    return request.put(`mall/site/goods/evaluate/refuse/${evaluate_id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品评价 回复
 * @param params
 * @returns
 */
export function replyEvaluate(params: Record<string, any>) {
    return request.put(`mall/site/goods/evaluate/reply/${params.evaluate_id}`, params, {
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
    return request.put(`mall/site/goods/evaluate/topping/${evaluate_id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 商品评价 取消置顶
 * @param evaluate_id
 * @returns
 */
export function cancelToppingEvaluate(evaluate_id: number) {
    return request.put(`mall/site/goods/evaluate/cancel_topping/${evaluate_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取商品来源
 * @returns
 */
export function getGoodsSource() {
    return request.get(`mall/site/goods/source`);
}

/**
 * 获取批量设置字典列表
 * @returns
 */
export function getGoodsBatchSetDict() {
    return request.get(`mall/site/goods/batchSet/dict`)
}

/**
 * 商品批量设置
 * @param params
 */
export function goodsBatchSet(params: Record<string, any>) {
    return request.put(`mall/site/goods/batchSet`, params, { showSuccessMessage: true })
}