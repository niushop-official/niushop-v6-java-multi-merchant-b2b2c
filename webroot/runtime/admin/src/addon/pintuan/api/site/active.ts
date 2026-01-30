import request from '@/utils/request'

/**
 * 活动列表
 * @param params
 * @returns
 */
export function getPintuanList(params: Record<string, any>) {
    return request.get(`pintuan/site/pages`, { params })
}

/**
 * 初始化字典
 * @param params
 * @returns
 */
export function getPintuanInit() {
    return request.get(`pintuan/site/init`)
}

/**
 * 获取商品详情模板
 * @param goods_id 商品goods_id
 * @returns
 */
export function getGoodsInfoTemplate(params: any) {
    return request.get(`diy/list`, { params });
}

/**
 * 批量设置商品详情模板
 * @param
 * @returns
 */
export function goodsBatchSet(params: any) {
    return request.post(`pintuan/site/batch_set`, params, { showSuccessMessage: true });
}

/**
 * 添加活动
 * @param params
 * @returns
 */
export function addPintuan(params: Record<string, any>) {
    return request.post(`pintuan/site/add`,params,{showSuccessMessage:true})
}

/**
 * 编辑活动
 * @returns
 * @param params
 */
export function editPintuan(params: Record<string, any>) {
    return request.put(`pintuan/site/edit/${ params.id }`, params, { showSuccessMessage: true })
}

/**
 * 活动详情
 * @param id
 * @returns
 */
export function getPintuanDetail(id: number) {
    return request.get(`pintuan/site/detail/${ id }`)
}

/**
 * 分享字典
 * @param id
 * @returns
 */
export function getPintuanShare() {
    return request.get(`pintuan/site/share`)
}

/**
 * 关闭活动
 * @param id
 * @returns
 */
export function closePintuan(id: number) {
    return request.put(`pintuan/site/close/${id}`,{showSuccessMessage:true})
}

/**
 * 删除活动
 * @returns
 * @param params
 */
export function deletePintuan(id: number) {
    return request.delete(`pintuan/site/delete/${id}`,{showSuccessMessage:true})
}



/**
 * 商品选择 列表
 * @returns
 * @param params
 */
export function getPintuanGoodsSelect(params: Record<string, any>) {
    return request.get(`pintuan/site/goods_of_select`,{params})
}

/**
 * 获取活动维度统计
 * @returns
 */
export function getPintuanStatTotal(params: Record<string, any>) {
    return request.get(`pintuan/site/stat/total`,{ params})
}

/**
 * 获取统计天
 * @returns
 */
export function getPintuanStatDay(params: Record<string, any>) {
    return request.get(`pintuan/site/stat/day`,{ params})
}

/**
 * 获取统计小时
 * @returns
 */
export function getPintuanStatHour(params: Record<string, any>) {
    return request.get(`pintuan/site/stat/hour`,{ params})
}
 
/**
 * 获取渠道数据统计
 * @returns
 */
export function getPintuanStatChannel(params: Record<string, any>) {
    return request.get(`pintuan/site/stat/channel`,{ params})
}

/**
 * 获取商品数据[组件]
 */
export function getPintuanDiyGoods(params: Record<string, any>) {
    return request.get(`pintuan/site/diy/goods`,{ params})
}

/**
 * 兑换码列表
 * @returns
 */
export function getPintuanReceiveList(params: Record<string, any>) {
    return request.get(`pintuan/site/receive_list`,{ params})
}

/**
 * 导入兑换码
 * @returns
 */
export function pintuanCodeImport(params: Record<string, any>) {
    return request.post(`pintuan/site/code/import`,params,{ showSuccessMessage: true })
}

/**
 * 批量删除兑换码
 * @returns
 */
export function pintuanCodeDelete(params: Record<string, any>) {
    return request.post(`pintuan/site/code/delete`, params,{ showSuccessMessage: true })
}

/**
 * 清除所有兑换码
 * @returns
 */
export function pintuanCodeClear(params: Record<string, any>) {
    return request.post(`pintuan/site/code/clear`,params,{ showSuccessMessage: true })
}