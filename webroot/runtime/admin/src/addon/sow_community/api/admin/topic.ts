import request from '@/utils/request'

/**
 * 获取社区话题分页列表
 * @param params
 * @returns
 */
export function getTopicList(params: Record<string, any>) {
    return request.get(`sow_community/admin/topic`, { params })
}

/**
 * 获取社区话题列表(不分页)
 * @returns
 */
export function getTopic() {
    return request.get(`sow_community/admin/topic/list`)
}

/**
 * 获取社区话题详情
 * @param topic_id 社区分类topic_id
 * @returns
 */
export function getTopicInfo(topic_id: number) {
    return request.get(`sow_community/admin/topic/${ topic_id }`);
}

/**
 * 添加社区话题
 * @param params
 * @returns
 */
export function addTopic(params: Record<string, any>) {
    return request.post('sow_community/admin/topic', params, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 编辑社区话题
 * @param params
 * @returns
 */
export function editTopic(params: Record<string, any>) {
    return request.put(`sow_community/admin/topic/${ params.topic_id }`, params, {
        showErrorMessage: true,
        showSuccessMessage: true
    })
}

/**
 * 删除社区话题
 * @param topic_id
 * @returns
 */
export function deleteTopic(topic_id: number) {
    return request.delete(`sow_community/admin/topic/${ topic_id }`, { showErrorMessage: true, showSuccessMessage: true })
}

/**
 * 修改社区话题排序号
 * @param params
 */
export function modifyTopicSort(params: Record<string, any>) {
    return request.put(`sow_community/admin/topic/sort`, params, { showSuccessMessage: true })
}

/**
 * 修改社区话题状态
 * @param params
 */
export function modifyTopicStatus(params: Record<string, any>) {
    return request.put(`sow_community/admin/topic/status`, params, { showSuccessMessage: true })
}

/**
 * 修改社区话题状态
 * @param params
 */
export function modifyTopicRecommend(params: Record<string, any>) {
    return request.put(`sow_community/admin/topic/recommend`, params, { showSuccessMessage: true })
}
