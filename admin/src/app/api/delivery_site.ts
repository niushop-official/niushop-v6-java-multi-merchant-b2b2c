import request from '@/utils/request'

/*********************************  物流公司  ***************************************/
/**
 * 获取物流公司列表
 * @param params
 * @returns
 */
export function getCompanyList(params: Record<string, any>) {
    return request.get(`delivery/site/delivery/company`, {params})
}

/**
 * 获取物流公司详情
 * @param company_id 物流公司company_id
 * @returns
 */
export function getCompanyInfo(company_id: number) {
    return request.get(`delivery/site/delivery/company/${company_id}`);
}

/*********************************  运费模版  ***************************************/
/**
 * 获取运费模版分页列表
 * @param params
 * @returns
 */
export function getShippingTemplatePageList(params: Record<string, any>) {
    return request.get(`delivery/site/shipping/template`, {params})
}

/**
 * 获取运费模版列表
 * @param params
 * @returns
 */
export function getShippingTemplateList(params: Record<string, any>) {
    return request.get(`delivery/site/shipping/template/list`, {params})
}

/**
 * 获取物运费模版详情
 * @param template_id 运费模版template_id
 * @returns
 */
export function getShippingTemplateInfo(template_id: number) {
    return request.get(`delivery/site/shipping/template/${template_id}`);
}

/**
 * 添加运费模版
 * @param params
 * @returns
 */
export function addShippingTemplate(params: Record<string, any>) {
    return request.post('delivery/site/shipping/template', params, {showErrorMessage: true, showSuccessMessage: true})
}

/**
 * 编辑运费模版
 * @param params
 * @returns
 */
export function editShippingTemplate(params: Record<string, any>) {
    return request.put(`delivery/site/shipping/template/${params.template_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除运费模版
 * @param template_id
 * @returns
 */
export function deleteShippingTemplate(template_id: number) {
    return request.delete(`delivery/site/shipping/template/${template_id}`, {showErrorMessage: true, showSuccessMessage: true})
}

/*********************************  电子面单  ***************************************/
/**
 * 获取电子面单分页列表
 * @param params
 * @returns
 */
export function getElectronicSheetPageList(params: Record<string, any>) {
    return request.get(`delivery/site/electronic_sheet`, { params })
}

/**
 * 获取电子面单列表
 * @param params
 * @returns
 */
export function getElectronicSheetList(params: Record<string, any>) {
    return request.get(`delivery/site/electronic_sheet/list`, { params })
}

/**
 * 获取电子面单详情
 * @param id 电子面单id
 * @returns
 */
export function getElectronicSheetInfo(id: number) {
    return request.get(`delivery/site/electronic_sheet/${ id }`);
}

/**
 * 添加电子面单
 * @param params
 * @returns
 */
export function addElectronicSheet(params: Record<string, any>) {
    return request.post('delivery/site/electronic_sheet', params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 编辑电子面单
 * @param params
 * @returns
 */
export function editElectronicSheet(params: Record<string, any>) {
    return request.put(`delivery/site/electronic_sheet/${ params.id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除电子面单
 * @param id
 * @returns
 */
export function deleteElectronicSheet(id: number) {
    return request.delete(`delivery/site/electronic_sheet/${ id }`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 设置默认电子面单模板
 * @param params
 * @returns
 */
export function setDefaultElectronicSheet(params: Record<string, any>) {
    return request.put(`delivery/site/electronic_sheet/setDefault/${ params.id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 设置电子面单 设置
 * @param params
 * @returns
 */
export function setElectronicSheetConfig(params: Record<string, any>) {
    return request.post('delivery/site/electronic_sheet/config', params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 获取电子面单配置
 * @returns
 */
export function getElectronicSheetConfig() {
    return request.get(`delivery/site/electronic_sheet/config`)
}

/**
 * 获取邮费支付方式类型
 * @returns
 */
export function getElectronicSheetPayType() {
    return request.get(`delivery/site/electronic_sheet/paytype`)
}

/**
 * 打印电子面单
 * @param params
 * @returns
 */
export function printElectronicSheet(params: Record<string, any>) {
    return request.post('delivery/site/electronic_sheet/print', params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/*********************************  商家地址库  ***************************************/
/**
 * 获取商家地址库列表
 * @param params
 * @returns
 */
export function getShopAddressList(params: Record<string, any>) {
    return request.get(`delivery/site/site_address`, { params })
}

/**
 * 获取商家地址库详情
 * @param id 商家地址库id
 * @returns
 */
export function getShopAddressInfo(id: number) {
    return request.get(`delivery/site/site_address/${id}`);
}

/**
 * 添加商家地址库
 * @param params
 * @returns
 */
export function addShopAddress(params: Record<string, any>) {
    return request.post('delivery/site/site_address', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商家地址库
 * @param params
 * @returns
 */
export function editShopAddress(params: Record<string, any>) {
    return request.put(`delivery/site/site_address/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除商家地址库
 * @param id
 * @returns
 */
export function deleteShopAddress(id: number) {
    return request.delete(`delivery/site/site_address/${id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取商家默认发货地址
 * @returns
 */
export function getShopDefaultDeliveryAddressInfo() {
    return request.get('delivery/site/site_address/default/delivery')
}

/**
 * 获取商家收货地址
 * @returns
 */
export function getOrderRefundAddress() {
    return request.get('delivery/site/order/refund/address')
}

/*********************************  配送服务商  ***************************************/

/**
 * 获取配送服务商列表
 * @param params
 * @returns
 */
export function getDeliveryServiceList(id: number) {
    return request.get(`delivery/site/delivery_store/delivery_service_list/${id}`)
}

/**
 * 服务商开通门店
 * @param params
 * @returns
 */
export function deliveryShopOpen(params: Record<string, any>) {
    return request.put(`delivery/site/delivery_store/delivery_shop_open/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 服务商门店品类修改
 * @param params
 * @returns
 */
export function deliveryShopEdit(params: Record<string, any>) {
    return request.put(`delivery/site/delivery_store/delivery_shop_edit/${params.id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/*********************************  配送设置  ***************************************/
/**
 * 获取 物流管理配置
 * @returns
 */
export function getShopDeliveryList() {
    return request.get('delivery/site/delivery/deliveryList')
}

/**
 * 物流管理设置
 * @param params
 * @returns
 */
export function setShopDeliveryConfig(params: Record<string, any>) {
    return request.put(`delivery/site/delivery/setConfig`, params, { showSuccessMessage: true })
}

/*********************************  配送员  ***************************************/

/**
 * 获取 配送员分页列表
 * @param params
 * @returns
 */
export function getShopDelivery(params: Record<string, any>) {
    return request.get('delivery/site/shop_delivery/staff', { params })
}

/**
 * 获取 配送员列表
 * @param params
 * @returns
 */
export function getShopDeliverList(params: Record<string, any>) {
    return request.get('delivery/site/shop_delivery/staff/list', { params })
}

/**
 * 获取配送员信息
 * @param staff_id
 * @returns
 */
export function getShopDeliverInfo(staff_id: number) {
    return request.get(`delivery/site/shop_delivery/staff/${ staff_id }`);
}

/**
 * 添加配送员
 * @param params
 * @returns
 */
export function addShopDeliver(params: Record<string, any>) {
    return request.post('delivery/site/shop_delivery/staff', params, { showSuccessMessage: true })
}

/**
 * 编辑配送员
 * @param params
 * @returns
 */
export function editShopDeliver(params: Record<string, any>) {
    return request.put(`delivery/site/shop_delivery/staff/${ params.deliver_id }`, params, { showSuccessMessage: true })
}

/**
 * 删除配送员
 * @param staff_id
 * @returns
 */
export function deleteShopDeliver(staff_id: number) {
    return request.delete(`delivery/site/shop_delivery/staff/${ staff_id }`)
}

/*********************************  同城配送设置  ***************************************/

/**
 * 获取同城配送设置
 * @returns
 */
export function getLocal() {
    return request.get('delivery/site/local');
}

/**
 * 设置同城配送
 * @param params
 * @returns
 */
export function setLocal(params: Record<string, any>) {
    return request.put('delivery/site/local', params, { showSuccessMessage: true })
}

/**
 * 获取三方配送列表
 * @param params
 * @returns
 */
export function getInUseLocalDeliveryList() {
    return request.get('delivery/site/local_delivery/list')
}

/**
 * 获取同城配送平台列表
 */
export function getLocalDeliveryServiceList() {
    return request.get(`delivery/site/local_delivery/service`)
}

/**
 * 同城配送平台设置
 * @param params
 * @returns
 */
export function setLocalDeliveryService(params: Record<string, any>) {
    return request.put(`delivery/site/local_delivery/service/${params.delivery_type}`, params, {showSuccessMessage: true})
}

/**
 * 同城配送平台详情
 * @param delivery_type
 * @returns
 */
export function getLocalDeliveryServiceInfo(delivery_type: string) {
    return request.get(`delivery/site/local_delivery/service/${delivery_type}`,)
}

/*********************************  同城配送记录  ***************************************/

/**
 * 获取配送记录状态
 * @param params
 * @returns
 */
export function getLocalDeliveryStatus() {
    return request.get('delivery/site/local_delivery/order/status')
}

/**
 * 获取配送记录分页列表
 * @param params
 * @returns
 */
export function getLocalDeliveryList(params: Record<string, any>) {
    return request.get(`delivery/site/local_delivery/order`, {params})
}

/**
 * 获取同城配送订单详情
 * @param id
 * @returns
 */
export function getLocalDeliveryInfo(id: number) {
    return request.get(`delivery/site/local_delivery/order/${ id }`);
}

/**
 * 获取同城配送订单取消原因
 * @param params
 * @returns
 */
export function getLocalDeliveryCancelReason(params: Record<string, any>) {
    return request.get('delivery/site/local_delivery/order/cancel_reason', {params})
}

/**
 * 获取同城配送订单同步
 * @param staff_id
 * @returns
 */
export function getLocalDeliverySync(id: number) {
    return request.put(`delivery/site/local_delivery/order/sync/${ id }`);
}

/**
 * 同城配送订单取消
 * @param params
 * @returns
 */
export function setLocalDeliveryCancel(params: Record<string, any>) {
   return request.put(`delivery/site/local_delivery/order/cancel/${ params.id }`, params, { showSuccessMessage: true })
}

/*********************************  商家配送记录  ***************************************/
/**
 * 获取配送记录状态
 * @param params
 * @returns
 */
export function getShopDeliveryStatus() {
    return request.get('delivery/site/shop_delivery/order/status')
}

/**
 * 获取商家配送记录分页列表
 * @param params
 * @returns
 */
export function getShopDeliveryOrderList(params: Record<string, any>) {
    return request.get(`delivery/site/shop_delivery/order`, {params})
}

/**
 * 获取同城配送订单详情
 * @param id
 * @returns
 */
export function getShopDeliveryInfo(id: number) {
    return request.get(`delivery/site/shop_delivery/order/${ id }`);
}

/**
 * 获取商家配送记录订单完成
 * @param staff_id
 * @returns
 */
export function getShopDeliveryFinish(id: number) {
    return request.put(`delivery/site/shop_delivery/order/finish/${ id }`);
}

/**
 * 获取商家配送记录订单完成
 * @param staff_id
 * @returns
 */
export function getDeliveryFinish(id: number) {
    return request.put(`delivery/site/local_delivery/order/finish/${ id }`);
}

/**
 * 商家配送记录订单取消
 * @param params
 * @returns
 */
export function setShopDeliveryCancel(params: Record<string, any>) {
   return request.put(`delivery/site/shop_delivery/order/cancel/${ params.id }`, params, { showSuccessMessage: true })
}

/**
 * 获取配送方式
 * @param id
 * @returns
 */
export function getShopDeliveryType() {
    return request.get(`delivery/site/delivery/type`);
}