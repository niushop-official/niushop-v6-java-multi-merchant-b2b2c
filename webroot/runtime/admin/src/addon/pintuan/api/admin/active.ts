import request from '@/utils/request'

/**
 * 活动列表
 * @param params
 * @returns
 */
export function getPintuanAdminList(params: Record<string, any>) {
    return request.get(`pintuan/admin/pages`, { params })
}

/**
 * 初始化字典
 * @param params
 * @returns
 */
export function getPintuanAdminInit() {
    return request.get(`pintuan/admin/init`)
}

/**
 * 活动详情
 * @param id
 * @returns
 */
export function getPintuanAdminDetail(id: number) {
    return request.get(`pintuan/admin/detail/${ id }`)
}

/**
 * 获取活动维度统计
 * @returns
 */
export function getPintuanAdminStatTotal(params: Record<string, any>) {
    return request.get(`pintuan/admin/stat/total`,{ params})
}

/**
 * 获取统计天
 * @returns
 */
export function getPintuanAdminStatDay(params: Record<string, any>) {
    return request.get(`pintuan/admin/stat/day`,{ params})
}

/**
 * 获取统计小时
 * @returns
 */
export function getPintuanAdminStatHour(params: Record<string, any>) {
    return request.get(`pintuan/admin/stat/hour`,{ params})
}
 
/**
 * 获取渠道数据统计
 * @returns
 */
export function getPintuanAdminStatChannel(params: Record<string, any>) {
    return request.get(`pintuan/admin/stat/channel`,{ params})
}

/**
 * 修改排序号
 * @param params
 */
export function editPintuanAdminSort(params: Record<string, any>) {
    return request.put(`pintuan/admin/edit_sort/${params.id}`, params, { showSuccessMessage: true })
}

/**
 * 审核
 * @param params
 */
export function editPintuanAdminAudit(params: Record<string, any>) {
    return request.put(`pintuan/admin/audit/${params.id}`, params, { showSuccessMessage: true })
}

/**
 * 获取商品数据[组件]
 */
export function getPintuanDiyGoods(params: Record<string, any>) {
    return request.get(`pintuan/admin/diy/goods`,{ params})
}