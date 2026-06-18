import request from '@/utils/request'

/***************************************************** 平台优惠券 ****************************************************/

/**
 * 获取商品分类列表
 * @param params
 * @returns
 */
export function getGoodsCategoryList() {
    return request.get(`mall/admin/goods/platform_coupon/init`)
}

/**
 * 获取初始化店铺列表
 * @param params
 * @returns
 */
export function getSiteList() {
    return request.get(`mall/admin/goods/platform_coupon/site`)
}

/**
 * 添加优惠券
 * @param params
 * @returns
 */
export function addPlatformCoupon(params: Record<string, any>) {
    return request.post(`mall/admin/goods/platform_coupon`, params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getPlatformCouponList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/platform_coupon`, {params})
}
/**
 * 获取会员优惠券列表
 * @param params
 * @returns
 */
export function getMemberPlatformCouponList(member_id: number,params: Record<string, any>) {
    return request.get(`mall/admin/member/platform_coupon/list/${member_id}`,{ params})
}
/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getPlatformCouponRecords(params: Record<string, any>) {
    return request.get(`mall/admin/goods/platform_coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getPlatformCouponInfo(id: number) {
    return request.get(`mall/admin/goods/platform_coupon/detail/${id}`);
}

/**
 * 优惠券状态变更
 * @param params
 * @returns
 */
export function editPlatformCouponStatus(params: Record<string, any>) {
    return request.put(`mall/admin/goods/platform_coupon/setstatus/${params.status}`, params, {showSuccessMessage: true})
}

/**
 * 编辑优惠券
 * @param params
 * @returns
 */
export function editPlatformCoupon(params: Record<string, any>) {
    return request.put(`mall/admin/goods/platform_coupon/edit/${params.id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除优惠券
 * @param id
 * @returns
 */
export function deletePlatformCoupon(id: number) {
    return request.delete(`mall/admin/goods/platform_coupon/${id}`, {showSuccessMessage: true})
}

/**
 * 关闭优惠券
 * @param id
 * @returns
 */
export function closePlatformCoupon(id: number) {
    return request.put(`mall/admin/goods/platform_coupon/invalid/${ id }`, { showSuccessMessage: true })
}

/**
 * 获取选中优惠券列表
 * @param params
 * @returns
 */
export function getSelectedCouponList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/platform_coupon/selected`, { params })
}

/**
 * 获取优惠券状态
 * @param id
 * @returns
 */
export function getStatusList() {
    return request.get(`mall/admin/goods/platform_coupon/status`);
}

/***************************************************** 店铺优惠券 ****************************************************/

/**
 * 获取优惠券列表
 * @param params
 * @returns
 */
export function getCouponList(params: Record<string, any>) {
    return request.get(`mall/admin/goods/coupon`, {params})
}

/**
 * 获取优惠券领取记录
 * @param params
 * @returns
 */
export function getCouponRecords(params: Record<string, any>) {
    return request.get(`mall/admin/goods/coupon/records`, {params});
}

/**
 * 获取优惠券详情
 * @param id
 * @returns
 */
export function getCouponInfo(id: number) {
    return request.get(`mall/admin/goods/coupon/detail/${id}`);
}

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

/***************************************************** 满减 ****************************************************/

/**
 * 获取满减列表
 * @param params
 * @returns
 */
export function getFullReductionList(params: Record<string, any>) {
    return request.get(`mall/admin/full_reduction/list`,{ params })
}

/**
 * 获取满减详情
 * @param params
 * @returns
 */
export function getFullReductionInfo(id: number) {
    return request.get(`mall/admin/full_reduction/info/${id}`)
}

/**
 * 获取满减初始化
 * @param params
 * @returns
 */
export function getFullReductionInit() {
    return request.get(`mall/admin/full_reduction/init`)
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