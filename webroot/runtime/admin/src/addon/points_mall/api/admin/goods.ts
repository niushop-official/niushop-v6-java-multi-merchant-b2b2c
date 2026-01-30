import request from '@/utils/request'

/**
 * 获取商品列表
 * @param params
 * @returns
 */
export function getGoodsPageList(params: Record<string, any>) {
    return request.get(`points_mall/admin/goods`, { params })
}

/**
 * 获取商品添加/编辑初始化数据
 * @param params
 */
export function getGoodsInit(params: Record<string, any>) {
    return request.get(`points_mall/admin/goods/init`, { params });
}


/**
 * 获取会员限制规则
 * @returns
 */
export function getMemberRule() {
    return request.get(`points_mall/admin/goods/member/rule`)
}

/**
 * 添加商品
 * @param params
 * @returns
 */
export function addGoods(params: Record<string, any>) {
    return request.post('points_mall/admin/goods', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商品
 * @param params
 */
export function editGoods(params: Record<string, any>) {
    return request.put(`points_mall/admin/goods/${params.goods_id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商品状态
 * @returns
 */
export function getGoodsStatus() {
    return request.get(`points_mall/admin/goods/status`);
}


/**
 * 修改商品排序号
 * @param params
 */
export function editGoodsSort(params: Record<string, any>) {
    return request.put(`points_mall/admin/goods/sort`, params, { showSuccessMessage: true })
}


/**
 * 获取快速添加商品列表
 * @param params
 */
export function getAdminGoodsSelect(params: Record<string, any>) {
    return request.get(`points_mall/admin/goods/fast_add/select`, { params });
}

/**
 * 获取商品添加/编辑初始化数据
 * @param params
 */
export function getGoodsFastInit(params: Record<string, any>) {
    return request.get(`points_mall/admin/goods/fast_add/detail`, { params });
}


/**
 * 获取商品类型
 * @returns
 */
export function getGoodsType() {
    return request.get(`points_mall/admin/goods/fast_add/type`);
}

/**
 * 修改商品上下架状态
 * @param params
 */
export function editGoodsStatus(params: Record<string, any>) {
    return request.put(`points_mall/admin/goods/status`, params, { showSuccessMessage: true })
}

/**
 * 删除商品
 * @param params
 * @returns
 */
export function deleteGoods(params: Record<string, any>) {
    return request.put(`points_mall/admin/goods/delete`, params, { showErrorMessage: true, showSuccessMessage: true })
}
