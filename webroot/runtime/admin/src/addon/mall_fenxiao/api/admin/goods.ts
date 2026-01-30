import request from '@/utils/request'

/**
 * 获取商品列表
 * @param params
 * @returns
 */
export function getFenxiaoGoodsPageList(params: Record<string, any>) {
    return request.get(`mall_fenxiao/admin/fenxiao_goods`, {params})
}


/**
 * 获取分销商品详情
 * @param id 商品id
 * @returns
 */
export function getFenxiaoGoodsInfo(id: number) {
    return request.get(`mall_fenxiao/admin/fenxiao_goods_config/${id}`);
}

