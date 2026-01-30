import request from '@/utils/request'

/**
 * 获取汇付类型
 * @param type 类型
 * @returns
 */
export function getHuifuType(type: string) {
    return request.get(`mall_huifu/site/paramtype/${type}`);
}
/**
 * 获取汇付银行
 * @param type 类型
 * @returns
 */
export function getHuifuGetparam() {
    return request.get(`mall_huifu/site/getparam`);
}

/**
 * 获取汇付银行
 * @param type 类型
 * @returns
 */
export function getHuifuBank(params: Record<string, any> = {}) {
    return request.get(`mall_huifu/site/bank`, { params });
}

/**
 * 获取汇付银行
 * @param type 类型
 * @returns
 */
export function getHuifuSubbank(params: Record<string, any>) {
    return request.get(`mall_huifu/site/subbank`, { params });
}

/**
 * 获取汇付省市
 * @param type 类型
 * @returns
 */
export function getHuifuProvincecity() {
    return request.get(`mall_huifu/site/provincecity`);
}
/**
 * 汇付申请
 * @param params
 * @returns
 */
export function addHuifu(params: Record<string, any>) {
    return request.post('mall_huifu/site/apply', params, { showErrorMessage: true, showSuccessMessage: true })
}
/**
 * 汇付开通产品
 * @param params
 * @returns
 */
export function addBusinessentry(params: Record<string, any>) {
    return request.post('mall_huifu/site/businessentry', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 获取汇付详情
 * @param type 类型
 * @returns
 */
export function getHuifuInfo() {
    return request.get(`mall_huifu/site/info`);
}
