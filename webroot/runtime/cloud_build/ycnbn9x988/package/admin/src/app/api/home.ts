import request from '@/utils/request'

export function getHomeSite(params: Record<string, any>) {
    return request.get(`home/site`, { params })
}


export function getHomeSiteList() {
    return request.get(`home/site/list`)
}

// 查询应用
export function getAddon() {
    return request.get(`addon/app/with_site_num`)
}