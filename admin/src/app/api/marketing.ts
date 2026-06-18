import request from '@/utils/request'


// 获取营销概况商家常用
export function getAdminpromotion() {
    return request.get(`site/adminpromotion`)
}