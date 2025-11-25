import request from '@/utils/request'

/**
 * 获取首页楼层列表
 * @param params
 * @returns
 */
export function getFloorList(params: Record<string, any>) {
    return request.get(`mall/admin/web/floor`, {params})
}

/**
 * 获取首页楼层详情
 * @param id 首页楼层id
 * @returns
 */
export function getFloorInfo(id: number) {
    return request.get(`mall/admin/web/floor/${id}`);
}

/**
 * 添加首页楼层
 * @param params
 * @returns
 */
export function addFloor(params: Record<string, any>) {
    return request.post('mall/admin/web/floor', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑首页楼层
 * @param params
 */
export function editFloor(params: Record<string, any>) {
    return request.put(`mall/admin/web/floor/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除首页楼层
 * @param id
 * @returns
 */
export function deleteFloor(id: number) {
    return request.delete(`mall/admin/web/floor/${id}`, { showErrorMessage: true, showSuccessMessage: true })
}