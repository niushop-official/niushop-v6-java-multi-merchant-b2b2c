import request from '@/utils/request'

/*********************************  物流查询  ***************************************/
/**
 * 设置 物流查询配置
 * @param params
 * @returns
 */
export function setDeliverySearch(params: Record<string, any>) {
    return request.post('delivery/admin/delivery/search', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取 物流查询配置
 * @returns
 */
export function getDeliverySearch() {
    return request.get('delivery/admin/delivery/search')
}

/*********************************  物流公司  ***************************************/
/**
 * 获取物流公司列表
 * @param params
 * @returns
 */
export function getCompanyList(params: Record<string, any>) {
    return request.get(`delivery/admin/delivery/company`, { params })
}

/**
 * 获取物流公司详情
 * @param company_id 物流公司company_id
 * @returns
 */
export function getCompanyInfo(company_id: number) {
    return request.get(`delivery/admin/delivery/company/${ company_id }`);
}

/**
 * 添加物流公司
 * @param params
 * @returns
 */
export function addCompany(params: Record<string, any>) {
    return request.post('delivery/admin/delivery/company', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑物流公司
 * @param params
 * @returns
 */
export function editCompany(params: Record<string, any>) {
    return request.put(`delivery/admin/delivery/company/${ params.company_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除物流公司
 * @param company_id
 * @returns
 */
export function deleteCompany(company_id: number) {
    return request.delete(`delivery/admin/delivery/company/${ company_id }`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/*********************************  同城配送  ***************************************/


/**
 * 获取配送记录分页列表
 * @param params
 * @returns
 */
export function getDeliveryRecordList(params: Record<string, any>) {
    return request.get(`delivery/admin/local_delivery/order`, {params})
}

/**
 * 获取同城配送订单详情
 * @param id
 * @returns
 */
export function getDeliveryRecordInfo(id: number) {
    return request.get(`delivery/admin/local_delivery/order/${ id }`);
}


/**
 * 获取配送方式
 * @param id
 * @returns
 */
export function getAdminDeliveryType() {
    return request.get(`delivery/admin/delivery/type`);
}
/***************************************************** 距离显示 ****************************************************/

/**
 * 获取距离配置
 * @returns
 */
export function getDeliveryConfig() {
    return request.get('delivery/admin/delivery/config')
}

/**
 * 编辑距离配置
 * @param params
 * @returns
 */
export function setDeliveryConfig(params: Record<string, any>) {
    return request.put('delivery/admin/delivery/config', params, {showSuccessMessage: true})
}

/**
 * 物流包裹信息（物流跟踪）
 * @return
 */
export function deliveryPackage(params: Record<string, any>) {
    return request.get(`delivery/admin/delivery_package/trade_package/${params.trade_type}`, {params})
}