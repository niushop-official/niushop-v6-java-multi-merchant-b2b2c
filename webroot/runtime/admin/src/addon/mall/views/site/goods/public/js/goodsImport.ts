import request from '@/utils/request'

/**
 * 商品类型
 */
export function getGoodsTypeAll() {
    return request.get(`mall/site/goods/import/goodsTypeAll`)
}

/**
 * 导入记录
 * @param params
 */
export function getImportRecordPage(params: Record<string, any>) {
    return request.get(`mall/site/goods/import/page`, { params })
}

/**
 * 添加记录
 * @param params
 */
export function addRecord(params: Record<string, any>) {
    return request.post(`mall/site/goods/import/add`, params, { showErrorMessage: true })
}

/**
 * 执行导入
 * @param params
 */
export function importExec(params: Record<string, any>) {
    return request.post(`mall/site/goods/import/exec`, params, { showErrorMessage: true, showSuccessMessage: true } )
}
