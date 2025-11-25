import request from '@/utils/request'

/**
 * 获取文章栏目
 */
export function getArticleCategory() {
    return request.get(`article/category`)
}

/**
 * 文章列表
 */
export function getArticleList(params: Record<string, any>) {
    return request.get('article/list', params)
}

/**
 * 获取文章热门推荐
 */
export function getArticleHot() {
    return request.get(`article/hot`)
}

/**
 * 获取文章详情
 */
export function getArticleInfo(id: number) {
    return request.get(`article/${ id }`)
}

/**
 * 文章列表
 */
export function getArticleAll(params: Record<string, any>) {
    return request.get('article/all', params)
}

/**
 * 增加文章访问量
 */
export function incVisit(id: any) {
    return request.put(`article/incVisit/${ id }`, {}, { showErrorMessage: false, showSuccessMessage: false })
}