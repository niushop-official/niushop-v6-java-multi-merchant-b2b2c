import request from '@/utils/request'

/**
 * 拼团列表
 * @param params
 * @returns
 */
export function getPintuanAdminGroupList(params: Record<string, any>) {
    return request.get(`pintuan/admin/group/pages`, { params })
}

/**
 * 拼团详情
 * @param id
 * @returns
 */
export function getPintuanAdminGroupDetail(id: number) {
    return request.get(`pintuan/admin/group/detail/${ id }`)
}