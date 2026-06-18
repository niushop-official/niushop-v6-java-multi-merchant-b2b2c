import request from '@/utils/request'

/**
 * 获取万能表单数据详情
 * @param id
 * @returns
 */
export function getFormRecordsInfo(id: number) {
    return request.get(`diy/site/form/records/${ id }`);
}