import request from '@/utils/request'

/**
 * 获取分销统计
 */
export function getFenxiaoAccountStat() {
    return request.get('mall_fenxiao/site/stat_account')
}

/**
 * 获取用户分销统计
 */
export function getFenxiaoMemberStat(params: Record<string, any>) {
    return request.get('mall_fenxiao/site/stat_member',{ params })
}