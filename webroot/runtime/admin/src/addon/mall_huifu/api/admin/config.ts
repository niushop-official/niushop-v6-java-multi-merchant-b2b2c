import request from '@/utils/request'

/**
 * 获取汇付信息
 * @param 
 * @returns
 */
export function getConfigHuifu() {
    return request.get(`mall_huifu/admin/config/huifu`);
}

/**
 * 设置汇付信息
 * @param 
 * @returns
 */
export function setConfigHuifu(params: Record<string, any>) {
    return request.put(`mall_huifu/admin/config/huifu`, params, { showErrorMessage: true, showSuccessMessage: true });
}