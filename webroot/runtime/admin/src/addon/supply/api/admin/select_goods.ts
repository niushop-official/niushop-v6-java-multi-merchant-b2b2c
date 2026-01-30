import request from '@/utils/request'

/**
 * 查看挑货
 * @returns
 */
export function getShopSupply(params: Record<string, any>) {
    return request.get('supply/site/mall_goods_by_supply', {params})
}