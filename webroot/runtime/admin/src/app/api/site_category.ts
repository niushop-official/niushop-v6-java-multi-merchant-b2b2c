import request from '@/utils/request'

/***************************************************** 行业类目管理 ****************************************************/

/**
 * 获取行业类目列表
 * @param params
 * @returns
 */
export function getSiteCategoryList(params: Record<string, any>) {
    return request.get(`site/category/tree`, { params })
}

/**
 * 获取行业类目列表
 * @param params
 * @returns
 */
export function getSiteCategoryTopList() {
    return request.get(`site/category/top`)
}

/**
 * 获取行业类目详情
 * @param category_id
 */
export function getSiteCategoryInfo(category_id: number) {
    return request.get(`site/category/${category_id}`)
}

/**
 * 添加行业类目
 * @param params
 * @returns
 */
export function addSiteCategory(params: Record<string, any>) {
    return request.post('site/category', params, { showSuccessMessage: true })
}

/**
 * 更新行业类目
 * @param params
 */
export function editSiteCategory(params: Record<string, any>) {
    return request.put(`site/category/${params.category_id}`, params, { showSuccessMessage: true })
}

/**
 * 删除行业类目
 * @param category_id
 */
export function deleteSiteCategory(category_id: number) {
    return request.delete(`site/category/${category_id}`, { showSuccessMessage: true });
}

/**
 * 获取全部行业类目
 * @param params
 * @returns
 */
export function getSiteCategoryAll(params: Record<string, any>) {
    return request.get(`site/category/tree`, { params })
}

    // 设置行业类目轮播图
export function setSiteCategoryBanner(params: Record<string, any>) {
    return request.put(`site/category/${params.category_id}/banner`, params, { showSuccessMessage: true })
}
