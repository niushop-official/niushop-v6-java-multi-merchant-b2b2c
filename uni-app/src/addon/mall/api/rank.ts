import request from '@/utils/request'

// 榜单分类列表
export function getRankList() {
    return request.get(`mall/rank`)
}

// 榜单商品列表
export function getRankGoodsList(params: Record<string, any>) {
    return request.get(`mall/rank/goods`, params)
}

// 获取排行榜配置
export function getRankConfig() {
    return request.get(`mall/rank/getRankConfig`)
}

// 榜单组件商品列表
export function getRankComponentsGoodsList(params: Record<string, any>) {
    return request.get(`mall/rank/components`, params)
}


// 获取排行榜配置
export function getRankSearch() {
    return request.get(`mall/rank/search`)
}



 



