import request from '@/utils/request'

/***************************************************** 消息管理 ****************************************************/

/**
 * 消息列表
 * @returns
 */
export function getNoticeList(params: any) {
    return request.get('notice/notice', {params})
}

/**
 * 消息详情
 * @param key
 * @returns
 */
export function getNoticeInfo(key: string) {
    return request.get(`notice/notice/${key}`)
}

/**
 * 消息发送记录
 * @param params
 * @returns
 */
export function getNoticeLog(params: any) {
    return request.get(`notice/log`, {params})
}

/**
 * 消息启动与关闭
 * @param params
 * @returns
 */
export function editNoticeStatus(params: Record<string, any>) {
    return request.post(`notice/notice/editstatus`, params, {showSuccessMessage: true})
}

/**
 * 消息修改
 * @param params
 * @returns
 */
export function editNotice(params: Record<string, any>) {
    return request.post(`notice/notice/edit`, params, {showSuccessMessage: true})
}

/**
 * 短信配置列表
 * @returns
 */
export function getSmsList() {
    return request.get('notice/notice/sms')
}

/**
 * 短信配置详情
 * @param sms_type
 * @returns
 */
export function getSmsInfo(sms_type: string) {
    return request.get(`notice/notice/sms/${sms_type}`,)
}

/**
 * 短信配置修改
 * @param params
 */
export function editSms(params: Record<string, any>) {
    return request.put(`notice/notice/sms/${params.sms_type}`, params, {showSuccessMessage: true})
}

/**
 * 短信发送记录
 * @param params
 */
export function getSmsLog(params: Record<string, any>) {
    return request.get(`notice/sms/log`, {params})
}

/**
 * 获取当前登录子账号
 */
export function getAccountIsLogin() {
    return request.get(`sms/niusms/config`)
}

/**
 * 登录子账号
 * @param params
 */
export function loginAccount(params: Record<string, any>) {
    return request.post(`sms/niusms/account/login`,params,{ showSuccessMessage: true })
}

/**
 * 注册子账号
 * @param params
 */
export function registerAccount(params: Record<string, any>) {
    return request.post(`sms/niusms/account/register`,params,{ showSuccessMessage: true })
}

/**
 * 获取当前登录子账号信息
 * @param username
 */
export function getAccountInfo(username: string) {
    return request.get(`sms/niusms/account/info/${username}`)
}

/**
 * 获取模版列表
 * @param params
 */
export function getTemplateList(params: Record<string, any>) {
    return request.get(`sms/niusms/template/list/${params.sms_type}/${params.username}`,{})
}

/**
 * 获取签名列表
 * @param username
 * @param params
 */
export function getSignList(username: string, params: Record<string, any>) {
    return request.get(`sms/niusms/sign/list/${username}`,{params})
}

/**
 * 添加签名
 * @param username
 * @param params
 */
export function addSign(username: string, params: Record<string, any>) {
    return request.post(`sms/niusms/sign/report/${username}`, params, { showSuccessMessage: true });
}

/**
 * 删除签名
 * @param username
 * @param params
 */
export function deleteSign(username: string, params: Record<string, any>) {
    return request.post(`sms/niusms/sign/delete/${username}`, params, { showSuccessMessage: true });
}

/**
 * 更新子账号信息
 * @param username
 * @param params
 */
export function editAccount(username: string,params: Record<string, any>) {
  return request.post(`sms/niusms/account/edit/${username}`, params, { showSuccessMessage: true });
}

/**
 * 获取短信发送记录
 * @param username
 * @param params
 */
export function getSmsSendList(username: string, params: Record<string, any>) {
    return request.get(`sms/niusms/account/send_list/${username}`,{params})
}

/**
 * 获取充值列表
 * @param username
 * @param params
 */
export function getSmsOrdersList(username: string, params: Record<string, any>) {
    return request.get(`sms/niusms/order/list/${username}`,{params})
}

/**
 * 获取套餐列表
 */
export function getSmsPackagesList() {
    return request.get(`sms/niusms/packages`)
}

/**
 * 获取图像验证码
 */
export function getSmsCaptcha() {
    return request.get(`sms/niusms/captcha`)
}

/**
 * 获取图像验证码
 */
export function getsiteCaptcha() {
    return request.get(`site/captcha/create`)
}

/**
 * 获取图像验证码类型
 */
export function getCaptchaType() {
    return request.get(`captcha/type`)
}
/**
 * 发送验证码
 * @param params
 */
export function getSmsSend(params: Record<string, any>) {
    return request.post(`sms/niusms/send`,params,{ showSuccessMessage: true })
}

/**
 * 添加签名配置项
 */
export function getSmsSignConfig() {
    return request.get(`sms/niusms/sign/report/config`)
}


/**
 * 消息接收人设置
 * @param params
 */
export function setNoticeRecipient(params: Record<string, any>) {
    return request.put(`sys/config/notice_recipient`, params,{showSuccessMessage: true})
}

/**
 * 获取消息接收人
 * @param params
 */
export function getNoticeRecipient(params: Record<string, any>) {
    return request.get(`sys/config/notice_recipient`)
}

/**
 * 模版报备配置项
 */
export function getTemplateReportConfig() {
    return request.get(`sms/niusms/template/report/config`)
}


/**
 * 模版报备
 * @param sms_type
 * @param username
 * @param sms_type
 * @param username
 * @param params
 */
export function reportTemplate(sms_type: string, username: string, params: Record<string, any>) {
    return request.post(`sms/niusms/template/report/${sms_type}/${username}`,params,{ showSuccessMessage: true })
}

/**
 * 模版详情
 * @param sms_type
 * @param username
 * @param sms_type
 * @param username
 * @param params
 */
export function getreportTemplateInfo(sms_type: string, username: string,params: Record<string, any>) {
    return request.get(`sms/niusms/template/info/${sms_type}/${username}`,{params})
}


/**
 * 充值下单
 * @param username
 * @param params
 */
export function smsOrderCreate(username: string, params: Record<string, any>) {
    return request.post(`sms/niusms/order/create/${username}`, params)
}

/**
 * 获取支付信息
 * @param username
 * @param params
 */
export function getOrderPayInfo(username: string, params: Record<string, any>) {
    return request.get(`sms/niusms/order/pay/${username}`, {params})
}

/**
 * 获取订单详情
 * @param username
 * @param params
 */
export function getOrderInfo(username: string, params: Record<string, any>) {
    return request.get(`sms/niusms/order/info/${username}`, {params})
}

/**
 * 获取支付状态
 * @param username
 * @param params
 */
export function getOrderPayStatus(username: string, params: Record<string, any>) {
    return request.get(`sms/niusms/order/status/${username}`, {params})
}

/**
 * 计算金额
 * @param username
 * @param params
 */
export function calculateOrderPay(username: string, params: Record<string, any>) {
    return request.post(`sms/niusms/order/calculate/${username}`, params)
}

/**
 * 启用牛云短信
 * @param params
 */
export function enableNiusms(params: Record<string, any>) {
    return request.put(`sms/niusms/enable`,params,{ showSuccessMessage: true })
}

/**
 * 同步模版状态
 * @param username
 * @param sms_type
 * @param username
 */
export function templateSync(sms_type: string, username: string) {
    return request.get(`sms/niusms/template/sync/${sms_type}/${username}`)
}

/**
 * 重置密码
 * @param username
 * @param params
 */
export function resetPassword(username: string,params: Record<string, any>) {
    return request.post(`sms/niusms/account/reset/password/${username}`,params,{ showSuccessMessage: true})
}

/**
 * 清除模版报备
 * @param template_id
 * @param username
 * @param template_id
 */
export function clearTemplate(username: string,template_id: string) {
    return request.delete(`sms/niusms/template/${username}/${template_id}`)
}
