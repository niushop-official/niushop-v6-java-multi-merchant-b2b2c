import request from '@/utils/request'


/**
 * 获取买单列表
 * @param params
 * @returns
 */
export function getCashSiteList(params: Record<string, any>) {
    return request.get(`cash/site/order/list`, {params})
}

/**
 * 获取买单详情
 * @param id id
 * @returns
 */
export function getCashSiteStatus() {
    return request.get(`cash/site/order/status`);
}

/**
 * 获取买单详情
 * @param id id
 * @returns
 */
export function getCashSiteDetail(id: number) {
    return request.get(`cash/site/order/detail/${id}`);
}