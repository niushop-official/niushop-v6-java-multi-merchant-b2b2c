import request from '@/utils/request'

// 分享专区列表
export function getNewcomerGoodsList(params: Record<string, any>) {
    return request.get(`mall/newcomer/goods`, params)
}

export function getNewcomersConfig() {
    return request.get(`mall/newcomer/config`)
}

// 首页新人专享列表
export function getNewcomersComponentsList(params: Record<string, any>) {
    return request.get(`mall/newcomer/goods/components`, params)
}

