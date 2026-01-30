import request from '@/utils/request'

/**
 * 获取商品列表
 * @param params
 * @returns
 */
export function getGoodsPageList(params: Record<string, any>) {
    return request.get(`supply/site/goods`, { params })
}

/**
 * 获取商品详情
 * @param goods_id 商品goods_id
 * @returns
 */
export function getGoodsInfo(goods_id: number) {
    return request.get(`supply/site/goods/${goods_id}`);
}

/**
 * 添加实物商品
 * @param params
 * @returns
 */
export function addGoods(params: Record<string, any>) {
    return request.post('supply/site/goods', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑实物商品
 * @param params
 */
export function editGoods(params: Record<string, any>) {
    return request.put(`supply/site/goods/${params.goods_id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品添加/编辑初始化数据
 * @param params
 */
export function getGoodsInit(params: Record<string, any>) {
    return request.get(`supply/site/goods/init`, { params });
}

/**
 * 添加虚拟商品
 * @param params
 * @returns
 */
export function addVirtualGoods(params: Record<string, any>) {
    return request.post('supply/site/goods/virtual', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑虚拟商品
 * @param params
 */
export function editVirtualGoods(params: Record<string, any>) {
    return request.put(`supply/site/goods/virtual/${params.goods_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取虚拟商品添加/编辑初始化数据
 * @param params
 */
export function getVirtualGoodsInit(params: Record<string, any>) {
    return request.get(`supply/site/goods/virtual/init`, { params });
}

/**
 * 删除商品
 * @param params
 * @returns
 */
export function deleteGoods(params: Record<string, any>) {
    return request.put(`supply/site/goods/delete`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取回收站商品分页列表
 * @param params
 * @returns
 */
export function getRecycleGoodsPageList(params: Record<string, any>) {
    return request.get(`supply/site/goods/recycle`, { params })
}

/**
 * 恢复商品
 * @param params
 * @returns
 */
export function recycleGoods(params: Record<string, any>) {
    return request.put(`supply/site/goods/recycle`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 修改商品排序号
 * @param params
 */
export function editGoodsSort(params: Record<string, any>) {
    return request.put(`supply/site/goods/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改商品上下架状态
 * @param params
 */
export function editGoodsStatus(params: Record<string, any>) {
    return request.put(`supply/site/goods/status`, params, { showSuccessMessage: true })
}

/**
 * 复制商品
 * @param params
 */
export function copyGoods(params: Record<string, any>) {
    return request.put(`supply/site/goods/copy/${params.goods_id}`, params, { showSuccessMessage: true })
}

/**
 * 获取商品选择分页列表
 * @param params
 * @returns
 */
export function getGoodsSelectPageList(params: Record<string, any>) {
    return request.get(`supply/site/goods/select`, { params })
}

/**
 * 获取商品SKU规格列表
 * @param params
 * @returns
 */
export function getGoodsSkuList(params: Record<string, any>) {
    return request.get(`supply/site/goods/sku`, { params })
}

/**
 * 获取商品SKU规格不分页列表
 * @param params
 * @returns
 */
export function getGoodsSkuNoPageList(params: Record<string, any>) {
    return request.get(`supply/site/goods/selectgoodssku`, { params })
}

/**
 * 编辑商品SKU规格库存
 * @param params
 * @returns
 */
export function editGoodsListStock(params: Record<string, any>) {
    return request.put(`supply/site/goods/sku/stock`, params, { showSuccessMessage: true })
}

/**
 * 编辑商品SKU规格价格
 * @param params
 * @returns
 */
export function editGoodsListPrice(params: Record<string, any>) {
    return request.put(`supply/site/goods/sku/price`, params, { showSuccessMessage: true })
}

/**
 * 获取商品类型
 * @returns
 */
export function getGoodsType() {
    return request.get(`supply/site/goods/type`);
}

/**
 * 获取商品状态
 * @returns
 */
export function getGoodsStatus() {
    return request.get(`supply/site/goods/status`);
}

/**
 * 获取批量设置字典列表
 * @returns
 */
export function getGoodsBatchSetDict() {
    return request.get(`supply/site/goods/batchSet/dict`)
}

/**
 * 商品批量设置
 * @param params
 */
export function goodsBatchSet(params: Record<string, any>) {
    return request.put(`supply/site/goods/batchSet`, params, { showSuccessMessage: true })
}