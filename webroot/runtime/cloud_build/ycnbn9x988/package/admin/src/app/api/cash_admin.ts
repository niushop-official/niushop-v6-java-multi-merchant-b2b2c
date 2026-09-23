import request from '@/utils/request'


/**
 * 获取买单列表
 * @param params
 * @returns
 */
export function getCashAdminList(params: Record<string, any>) {
    return request.get(`cash/admin/order/list`, {params})
}

/**
 * 获取买单详情
 * @param id id
 * @returns
 */
export function getCashAdminStatus() {
    return request.get(`cash/admin/order/status`);
}

/**
 * 获取买单详情
 * @param id id
 * @returns
 */
export function getCashAdminDetail(id: number) {
    return request.get(`cash/admin/order/detail/${id}`);
}