import request from '@/utils/request'

/**
 * 获取社区分类分页列表
 * @param params
 * @returns
 */
export function getCategoryList(params: Record<string, any>) {
    return request.get(`sow_community/admin/category`, { params })
}

/**
 * 获取社区分类列表
 * @returns
 */
export function getCategory() {
    return request.get(`sow_community/admin/category/list`)
}

/**
 * 获取社区分类详情
 * @param category_id 社区分类category_id
 * @returns
 */
export function getCategoryInfo(category_id: number) {
    return request.get(`sow_community/admin/category/${ category_id }`);
}

/**
 * 添加社区分类
 * @param params
 * @returns
 */
export function addCategory(params: Record<string, any>) {
    return request.post('sow_community/admin/category', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑社区分类
 * @param params
 * @returns
 */
export function editCategory(params: Record<string, any>) {
    return request.put(`sow_community/admin/category/${ params.category_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除社区分类
 * @param category_id
 * @returns
 */
export function deleteCategory(category_id: number) {
    return request.delete(`sow_community/admin/category/${ category_id }`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 修改社区分类排序号
 * @param params
 */
export function modifyCategorySort(params: Record<string, any>) {
    return request.put(`sow_community/admin/category/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改社区分类状态
 * @param params
 */
export function modifyCategoryStatus(params: Record<string, any>) {
    return request.put(`sow_community/admin/category/status`, params, { showSuccessMessage: true })
}
