
/**
 * 获取足迹
 */
export function getBrowseList(params: Record<string, any>) {
    return request.get('mall/goods/browse', params)
}
/**
 * 添加足迹
 */
export function addBrowse(params: Record<string, any>) {
    return request.post('mall/goods/browse', params)
}
