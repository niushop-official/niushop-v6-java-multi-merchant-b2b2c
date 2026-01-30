import request from '@/utils/request'

/**
 * 拼团列表
 * @param params
 * @returns
 */
export function getPintuanGroupList(params: Record<string, any>) {
    return request.get(`pintuan/site/group/pages`, { params })
}

/**
 * 拼团详情
 * @param id
 * @returns
 */
export function getPintuanGroupDetail(id: number) {
    return request.get(`pintuan/site/group/detail/${ id }`)
}