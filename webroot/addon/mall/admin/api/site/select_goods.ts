import request from '@/utils/request'

/**
 * 供货商分页列表
 * @returns
 */
export function getSupplyList(params: Record<string, any>) {
    return request.get('mall/site/select_goods/list', {params})
}

/**
 * 供货商分页列表
 * @returns
 */
export function getGoodsList(params: Record<string, any>) {
    return request.get('mall/site/select_goods/goods_list', {params})
}

/**
 * 供货商信息
 * @param site_id 商品site_id
 * @returns
 */
export function getShopInfo(site_id: number) {
    return request.get(`shop/site/shop/info/${site_id}`);
}


/**
 * 选品商品详情
 * @param params
 */
export function getGoodsDetail(params: Record<string, any>) {
    return request.get(`mall/site/select_goods/goods_detail`, { params });
}

/**
 * 挑货
 * @param params
 * @returns
 */
export function pickGoods(params: Record<string, any>) {
    return request.post('mall/site/select_goods/pick', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 批量挑货已选商品列表
 * @param params
 */
export function getBatchPick(params: Record<string, any>) {
    return request.get(`mall/site/select_goods/batch_pick/goods_list`, { params },);
}
/**
 * 批量挑货
 * @param params
 * @returns
 */
export function batchPickGoods(params: Record<string, any>) {
    return request.post('mall/site/select_goods/batch_pick', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 供货商分页列表
 * @returns
 */
export function getMysupplyList(params: Record<string, any>) {
    return request.get('mall/site/select_goods/my_supply', {params})
}

/**
 * 合作店铺分页列表
 * @returns
 */
export function getShopList(params: Record<string, any>) {
    return request.get('mall/site/select_goods/my_shop', {params})
}

/**
 * 供货商分页列表
 * @returns
 */
export function getSupplyGoods(goodsId: number) {
    return request.get(`mall/site/supply_goods/${goodsId}`)
}