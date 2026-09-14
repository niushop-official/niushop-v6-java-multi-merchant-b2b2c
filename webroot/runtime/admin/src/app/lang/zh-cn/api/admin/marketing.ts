import request from '@/utils/request'

/************ 商品榜单 ****************/

/**
 * 设置排行榜配置
 * @param params
 * @returns
 */
export function setRankConfig(params: Record<string, any>) {
    return request.post('mall/admin/good/rank/config', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取排行榜配置
 * @returns
 */
export function getRankConfig() {
    return request.get(`mall/admin/good/rank/config`)
}

/**
 * 获取排行榜列表
 * @param params
 * @returns
 */
export function getRankPageList(params: Record<string, any>) {
    return request.get(`mall/admin/good/rank`, { params })
}

/**
 * 获取排行榜选项列表
 * @returns
 */
export function optionData() {
    return request.get(`mall/admin/good/rank/dict`)
}

/**
 * 获取排行榜详情
 * @param rank_id
 * @returns
 */
export function getRankInfo(rank_id: number) {
    return request.get(`mall/admin/good/rank/${ rank_id }`);
}

/**
 * 添加排行榜
 * @param params
 * @returns
 */
export function addGoodRank(params: Record<string, any>) {
    return request.post('mall/admin/good/rank', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑排行榜
 * @param params
 * @returns
 */
export function editGoodRank(params: Record<string, any>) {
    return request.put(`mall/admin/good/rank/${ params.id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 排行榜状态
 * @param params
 * @returns
 */
export function editRankStatus(params: Record<string, any>) {
    return request.put(`mall/admin/goods/rank/status`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除排行榜
 * @param id
 * @returns
 */
export function deleteGoodRank(id: number) {
    return request.delete(`mall/admin/good/rank/${ id }`, { showSuccessMessage: true })
}

/**
 * 批量删除排行榜
 * @param params
 * @returns
 */
export function batchDelete(params: Record<string, any>) {
    return request.put(`mall/admin/good/rank/batchDelete`, params, { showSuccessMessage: true })
}

/**
 * 排行榜修改排序
 * @param params
 * @returns
 */
export function modifyGoodsRankSort(params: Record<string, any>) {
    return request.put(`mall/admin/good/rank/sort`, params, { showSuccessMessage: true })
}

/**
 * 商品排行榜列表
 * @param params
 * @returns
 */
export function getSelectRankPageList(params: Record<string, any>) {
    return request.get(`mall/admin/good/rank/select`, { params })
}

/**
 * 获取悬浮按钮
 */
export function getPointDeductConfig() {
    return request.get(`mall/admin/point_deduct/config`)
}

/**
 * 获取悬浮按钮
 */
export function setPointDeductConfig(params: Record<string, any>) {
    return request.post(`mall/admin/point_deduct/config`, params, { showSuccessMessage: true })
}

/***************************************************** 满赠 ****************************************************/

/**
 * 获取满赠列表
 * @param params
 * @returns
 */
export function getFullGiftList(params: Record<string, any>) {
    return request.get(`mall/admin/full_gift/list`,{ params })
}

/**
 * 获取满赠详情
 * @param params
 * @returns
 */
export function getFullGiftInfo(id: number) {
    return request.get(`mall/admin/full_gift/info/${id}`)
}

/**
 * 获取满减初始化
 * @param params
 * @returns
 */
export function getFullGiftInit() {
    return request.get(`mall/admin/full_gift/init`)
}