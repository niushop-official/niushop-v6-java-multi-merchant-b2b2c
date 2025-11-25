/***************************************************** 地址管理 ****************************************************/
/**
 * 获取下级地址列表
 * @param pid
 */
export function getAreaListByPid(pid: number = 0) {
    return request.get(`area/list_by_pid/${ pid }`)
}

/**
 * 获取地址树列表
 * @param level
 */
export function getAreatree(level: number = 1) {
    return request.get(`area/tree/${ level }`)
}

/**
 * 获取会员收货地址列表
 * @param params
 * @returns
 */
export function getAddressList(params: Record<string, any>) {
    return request.get(`member/address`, { params })
}

/**
 * 获取会员收货地址详情
 * @param id 会员收货地址id
 * @returns
 */
export function getAddressInfo(id: number) {
    return request.get(`member/address/${ id }`);
}

/**
 * 添加会员收货地址
 * @param params
 * @returns
 */
export function addAddress(params: Record<string, any>) {
    return request.post('member/address', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑会员收货地址
 * @param params
 * @returns
 */
export function editAddress(params: Record<string, any>) {
    return request.put(`member/address/${ params.id }`, params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 删除会员收货地址
 * @param id
 * @returns
 */
export function deleteAddress(id: number) {
    return request.delete(`member/address/${ id }`, { showErrorMessage: true, showSuccessMessage: true })
}
