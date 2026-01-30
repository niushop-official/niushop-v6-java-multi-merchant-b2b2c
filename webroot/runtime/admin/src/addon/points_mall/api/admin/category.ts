import request from '@/utils/request'

/**
 * 获取分类分页列表
 * @param params
 * @returns
 */
export function getCategoryList(params: Record<string, any>) {
    return request.get(`points_mall/admin/category`, { params })
}

/**
 * 获取分类列表
 * @returns
 */
export function getCategory() {
    return request.get(`points_mall/admin/category/list`)
}

/**
 * 获取分类详情
 * @param category_id 分类category_id
 * @returns
 */
export function getCategoryInfo(category_id: number) {
    return request.get(`points_mall/admin/category/${ category_id }`);
}

/**
 * 添加分类
 * @param params
 * @returns
 */
export function addCategory(params: Record<string, any>) {
    return request.post('points_mall/admin/category', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑分类
 * @param params
 * @returns
 */
export function editCategory(params: Record<string, any>) {
    return request.put(`points_mall/admin/category/${ params.category_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除分类
 * @param category_id
 * @returns
 */
export function deleteCategory(category_id: number) {
    return request.delete(`points_mall/admin/category/${ category_id }`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 修改分类排序号
 * @param params
 */
export function modifyCategorySort(params: Record<string, any>) {
    return request.put(`points_mall/admin/category/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改分类状态
 * @param params
 */
export function modifyCategoryStatus(params: Record<string, any>) {
    return request.put(`points_mall/admin/category/status`, params, { showSuccessMessage: true })
}