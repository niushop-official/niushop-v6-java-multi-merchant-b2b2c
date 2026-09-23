import request from '@/utils/request'

/**
 * 获取全部行业类目
 * @param params
 * @returns
 */
export function getSiteCategoryAll(params: Record<string, any>) {
    return request.get(`site/category/tree`, params)
}