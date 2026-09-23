import request from '@/utils/request'

/**
 * 获取全部行业类目
 * @param params
 * @returns
 */
export function getSiteCategoryAll(params: Record<string, any>) {
    return request.get(`site/category/tree`, params)
}

/**
 * 获取圈子
 * @param params
 * @returns
 */
export function getCircleList(params: Record<string, any>) {
    return request.get(`site/circle/list`, params)
}