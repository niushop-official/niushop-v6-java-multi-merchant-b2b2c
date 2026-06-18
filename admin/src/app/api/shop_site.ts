import request from '@/utils/request'
/**
 * 获取收款方式配置校验
 */
export function getAccountCheck() {
    return request.get(`shop/site/shop/account/check`)
}
/**
 * 获取店铺转账方式
 */
export function getTransfertype() {
    return request.get(`/shop/site/shop/cash_out/transfer_type`)
}
/**
 * 申请提现
 */
export function getCashApply(params: Record<string, any>) {
    return request.post(`/shop/site/shop/cash_apply`, params)
}
/**
 * 获取店铺提现记录
 */
export function getCashOutList(params: Record<string, any>) {
    return request.get(`shop/site/shop/cash_out`, { params })
}
/**
 * 获取店铺提现详情
 */
export function getShopCashOutInfo(id: number) {
    return request.get(`shop/site/shop/cash_out/${id}`)
}

/**
 * 获取店铺信息
 */
export function getShopAccountInfo() {
    return request.get(`shop/site/shop/account/info`)
}

/**
 * 获取店铺账单记录
 */
export function getShopAccountLog(params: Record<string, any>) {
    return request.get(`shop/site/shop/account/log`, { params })
}

/**
 * 获取店铺账单类型列表
 */
export function getShopAccountTypeList() {
    return request.get(`shop/site/shop/account/typelist`)
}

/**
 * 设置店铺收款方式
 */
export function setShopAccount(params: Record<string, any>) {
    return request.put(`shop/site/shop/account/set`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取店铺账单类型列表
 */
export function getShopCashOutStatus() {
    return request.get(`shop/site/shop/cash_out/status`)
}

/**
 * 获取店铺提现
 */
export function getShopCashOutTransfer(id: number) {
    return request.get(`shop/site/shop/cash_out/transfer/${id}`)
}
/**
 * 店铺提现取消
 */
export function transferShopCashCancel(id: number) {
    return request.put(`/shop/site/shop/cash_cancel/${id}`, {}, { showSuccessMessage: true })
}

/**
 * 获取店铺信息
 */
export function getShopInfo() {
    return request.get(`shop/site/setting/get_shop`)
}

/**
 * 获取店铺基础信息
 */
export function getShopBaseInfo() {
    return request.get(`shop/site/setting`)
}
/**
 * 设置店铺收款方式
 */
export function setShopBaseInfo(params: Record<string, any>) {
    return request.put(`shop/site/setting/set`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/***************************************************** 会员列表 ****************************************************/
/**
 * 获取店铺会员列表
 * @param params
 * @returns
 */
export function getShopMember(params: Record<string, any>) {
    return request.get(`shop/site/member`, { params })
}
/**
 * 获取平台会员选择列表
 * @param params
 * @returns
 */
export function getPlatMemberSelectList(params: Record<string, any>) {
    return request.get(`shop/site/member/plat_select`, {params})
}
/**
 * 获取店铺会员编码
 * @returns
 */
export function gethopMemberNo() {
    return request.get(`shop/site/member/memberno`);
}
/**
 * 添加店铺会员
 * @param params
 * @returns
 */
export function addShopMember(params: Record<string, any>) {
    return request.post(`shop/site/member`, params, { showSuccessMessage: true })
}
/**
 * 获取店铺会员注册来源
 * @param params
 * @returns
 */
export function getShopRegisterChannel(params: Record<string, any>) {
    return request.get(`shop/site/member/register_channel`, params)
}
/**
 * 获取店铺会员关注状态
 * @param params
 * @returns
 */
export function getShopIsFollow(params: Record<string, any>) {
    return request.get(`shop/site/member/is_follow`, params)
}
/**
 * 获取店铺会员详情
 * @param member_id 会员id
 * @returns
 */
export function getShopMemberInfo(member_id: number) {
    return request.get(`shop/site/member/${member_id}`);
}
/**
 * 店铺会员状态变更
 * @param params
 */
export function editShopMemberStatus(params: Record<string, any>) {
    return request.put(`shop/site/member/setstatus/${params.status}`, params, { showSuccessMessage: true })
}
/**
 * 编辑会员详情
 * @param params
 */
export function editShopMemberDetail(params: Record<string, any>) {
    return request.put(`shop/site/member/modify/${params.member_id}/${params.field}`, params, { showSuccessMessage: true })
}
/**
 * 获取店铺会员优惠券
 * @param member_id 会员id
 * @returns
 */
export function getShopMemberCoupon(member_id: number,params: Record<string, any>) {
    return request.get(`mall/site/member/coupon/list/${member_id}`,{params});
}

/**
 * 批量编辑会员详情
 * @param params
 */
export function batchModifyMemberDetail(params: Record<string, any>) {
    return request.put(`shop/site/member/batch_modify`, params, { showSuccessMessage: true })
}

/**
 * 店铺优惠券批量发放
 * @param params
 */
export function sendCoupon(params: Record<string, any>) {
    return request.post(`mall/site/goods/coupon/send`, params, { showSuccessMessage: true })
}

/**
 * 获取店铺会员详情类型
 * @param member_id 会员id
 * @returns
 */
export function getMemberContentType() {
    return request.get(`member/member/content/type`);
}

/**
 * 获取店铺会员消费记录
 * @param member_id 会员id
 * @returns
 */
export function getShopMemberConsumeList(params: Record<string, any>) {
    return request.get(`mall/site/order/consume/list`,{params});
}
/***************************************************** 会员标签 ****************************************************/
/**
 * 获取店铺全部会员标签
 */
export function getShopMemberLabelAll() {
    return request.get(`shop/site/member/label/all`);
}
/**
 * 获取店铺会员标签列表
 * @param params
 * @returns
 */
export function getShopMemberLabelList(params: Record<string, any>) {
    return request.get(`shop/site/member/label`, { params })
}
/**
 * 获取店铺会员标签详情
 * @param label_id 会员标签label_id
 * @returns
 */
export function getShopMemberLabelInfo(label_id: number) {
    return request.get(`shop/site/member/label/${label_id}`);
}
/**
 * 删除店铺会员标签
 * @param label_id
 * @returns
 */
export function deleteShopMemberLabel(label_id: number) {
    return request.delete(`shop/site/member/label/${label_id}`, { showSuccessMessage: true })
}
/**
 * 添加店铺会员标签
 * @param params
 * @returns
 */
export function addShopMemberLabel(params: Record<string, any>) {
    return request.post('shop/site/member/label', params, { showSuccessMessage: true })
}
/**
 * 编辑店铺会员标签
 * @param params
 */
export function updateShopMemberLabel(params: Record<string, any>) {
    return request.put(`shop/site/member/label/${params.label_id}`, params, { showSuccessMessage: true })
}

/**
 * 获取会员设置
 */
export function getShopMemberConfig() {
    return request.get(`shop/site/member/config`)
}

/**
 * 会员设置
 * @param params
 * @returns
 */
export function setShopMemberConfig(params: Record<string, any>) {
    return request.post(`shop/site/member/config`, params, {showSuccessMessage: true})
}

/**
 * 获取分账账户
 * 
 */
export function getSitePayment() {
    return request.get(`shop/site/site_payment`);
}

/**
 * 分账账户状态改变
 */
export function setSitePaymentStatus(params: Record<string, any>) {
    return request.put(`shop/site/site_payment/status/${params.id}`, params, { showSuccessMessage: true })
}

/**
 * 切换营业状态
 */
export function updateBusinessStatus(params: Record<string, any>) {
    return request.put(`shop/site/shop/business_status`, params, { showSuccessMessage: true })
}
