import request from '@/utils/request'
/**
 * 获取 物流管理配置
 * @returns
 */
export function getShopDeliveryList() {
    return request.get('delivery/site/delivery/deliveryList')
}

/**
 * 获取运费模版列表
 * @param params
 * @returns
 */
export function getShippingTemplateList(params: Record<string, any>) {
    return request.get(`delivery/site/shipping/template/list`, params)
}

/**
 * 获取物流公司列表
 * @param params
 * @returns
 */
export function getCompanyList(params: Record<string, any>) {
    return request.get(`delivery/site/delivery/company`, params)
}


/*********************************  商家地址库  ***************************************/
/**
 * 获取商家地址库列表
 * @param params
 * @returns
 */
export function getShopAddressList(params: Record<string, any>) {
    return request.get(`delivery/site/site_address`, params)
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

/*********************************  配送员  ***************************************/

/**
 * 获取 配送员列表
 * @param params
 * @returns
 */
export function getShopDeliverList(params: Record<string, any>) {
    return request.get('delivery/site/shop_delivery/staff/list', params)
}
/*********************************  同城配送设置  ***************************************/

/**
 * 获取三方配送列表
 * @param params
 * @returns
 */
export function getInUseLocalDeliveryList(params: Record<string, any>) {
    return request.get('delivery/site/local_delivery/list', params)
}


/*********************************  门店自提  ***************************************/

/**
 * 获取门店选择列表
 * @param params
 * @returns
 */
export function getDeliveryStoreListAll(params: Record<string, any>) {
    return request.get(`delivery/site/delivery/store/list`, params)
}

