import request from '@/utils/request'

/*********************************  商品  ***************************************/
/**
 * 获取商品分类树结构
 * @returns
 */
export function getCategoryTree() {
    return request.get(`basic/admin/goods/tree`)
}


/**
 * 获取商品分类列表
 * @param params
 * @returns
 */
export function getCategoryList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/category`, { params })
}

/**
 * 获取商品分类详情
 * @param category_id 商品分类category_id
 * @returns
 */
export function getCategoryInfo(category_id: number) {
    return request.get(`basic/admin/goods/category/${category_id}`);
}

/**
 * 添加商品分类
 * @param params
 * @returns
 */
export function addCategory(params: Record<string, any>) {
    return request.post('basic/admin/goods/category', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商品分类
 * @param params
 * @returns
 */
export function editCategory(params: Record<string, any>) {
    return request.put(`basic/admin/goods/category/${params.category_id}`, params, { showErrorMessage: true, showSuccessMessage: true })
}


/**
 * 删除商品分类
 * @param category_id
 * @returns
 */
export function deleteCategory(category_id: number) {
    return request.delete(`basic/admin/goods/category/${category_id}`)
}

/**
 * 拖拽编辑商品分类
 * @param params
 * @returns
 */
export function updateCategory(params: Record<string, any>) {
    return request.post(`basic/admin/goods/category/update`, params, { showErrorMessage: true, showSuccessMessage: true })
}


/**
 * 获取商品分类树结构供弹框调用
 * @returns
 */
export function getCategoryTreeComponents() {
    return request.get(`basic/admin/goods/category/components`)
}

/**
 * 获取商品服务分页列表
 * @param params
 * @returns
 */
export function getServePageList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/service`, { params })
}

/**
 * 获取商品服务列表
 * @param params
 * @returns
 */
export function getServeList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/service/list`, { params })
}

/**
 * 获取商品服务详情
 * @param service_id 商品服务service_id
 * @returns
 */
export function getServeInfo(service_id: number) {
    return request.get(`basic/admin/goods/service/${service_id}`);
}

/**
 * 添加商品服务
 * @param params
 * @returns
 */
export function addServe(params: Record<string, any>) {
    return request.post('basic/admin/goods/service', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商品服务
 * @param params
 * @returns
 */
export function editServe(params: Record<string, any>) {
    return request.put(`basic/admin/goods/service/${params.service_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除商品服务
 * @param service_id
 * @returns
 */
export function deleteServe(service_id: number) {
    return request.delete(`basic/admin/goods/service/${service_id}`, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}


/**
 * 获取商品品牌分页列表
 * @param params
 * @returns
 */
export function getBrandPageList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/brand`, { params })
}

/**
 * 获取商品品牌列表
 * @param params
 * @returns
 */
export function getBrandList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/brand/list`, { params })
}

/**
 * 获取商品品牌详情
 * @param brand_id 商品品牌brand_id
 * @returns
 */
export function getBrandInfo(brand_id: number) {
    return request.get(`basic/admin/goods/brand/${brand_id}`);
}

/**
 * 添加商品品牌
 * @param params
 * @returns
 */
export function addBrand(params: Record<string, any>) {
    return request.post('basic/admin/goods/brand', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商品品牌
 * @param params
 * @returns
 */
export function editBrand(params: Record<string, any>) {
    return request.put(`basic/admin/goods/brand/${params.brand_id}`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 修改商品品牌排序号
 * @param params
 */
export function modifyBrandSort(params: Record<string, any>) {
    return request.put(`basic/admin/goods/brand/sort`, params, { showSuccessMessage: true })
}

/**
 * 删除商品品牌
 * @param brand_id
 * @returns
 */
export function deleteBrand(brand_id: number) {
    return request.delete(`basic/admin/goods/brand/${brand_id}`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 店铺商品品牌转入平台
 * @param params
 */
export function moveBrand(params: Record<string, any>) {
    return request.put(`basic/admin/goods/brand/move`, params, { showSuccessMessage: true })
}

/**
 * 获取商品参数分页列表
 * @param params
 * @returns
 */
export function getAttrPageList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/attr`, { params })
}

/**
 * 获取商品参数列表
 * @param params
 * @returns
 */
export function getAttrList(params: Record<string, any>) {
    return request.get(`basic/admin/goods/attr_list`, { params })
}

/**
 * 获取商品参数详情
 * @param attr_id 商品参数attr_id
 * @returns
 */
export function getAttrInfo(attr_id: number) {
    return request.get(`basic/admin/goods/attr/${ attr_id }`);
}

/**
 * 添加商品参数
 * @param params
 * @returns
 */
export function addAttr(params: Record<string, any>) {
    return request.post('basic/admin/goods/attr', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑商品参数
 * @param params
 * @returns
 */
export function editAttr(params: Record<string, any>) {
    return request.put(`basic/admin/goods/attr/${ params.attr_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除商品参数
 * @param attr_id
 * @returns
 */
export function deleteAttr(attr_id: number) {
    return request.delete(`basic/admin/goods/attr/${ attr_id }`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 修改商品参数排序号
 * @param params
 */
export function modifyAttrSort(params: Record<string, any>) {
    return request.put(`basic/admin/goods/attr/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改商品参数名称
 * @param params
 */
export function modifyAttrName(params: Record<string, any>) {
    return request.put(`basic/admin/goods/attr/attr_name`, params, { showSuccessMessage: true })
}

/**
 * 修改商品参数值
 * @param params
 */
export function modifyAttrValue(params: Record<string, any>) {
    return request.put(`basic/admin/goods/attr/attr_value`, params, { showSuccessMessage: true })
}

/*********************************  发票  ***************************************/

/**
 * 获取发票列表
 */
export function getInvoiceList(params: Record<string, any>) {
    return request.get(`basic/admin/invoice`, { params })
}

/**
 * 获取发票列表
 */
export function getInvoiceDetail(id: number) {
    return request.get(`basic/admin/invoice/${id}`)
}