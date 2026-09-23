import request from '@/utils/request'


/**
 * 获取数据统计
 */
export function getstat() {
    return request.get(`adminapp/site/stat`)
}

/**
 * 设置数据统计
 */
export function setstat(params: Record<string, any>) {
    return request.post(`adminapp/site/stat`, params)
}

/**
 * 获取全部数据统计
 */
export function getAllstat() {
    return request.get(`adminapp/site/stat_of_all`)
}

/**
 * 获取代办事项统计
 */
export function getTodo() {
    return request.get(`adminapp/site/todo`)
}

/**
 * 设置代办事项统计
 */
export function setTodo(params: Record<string, any>) {
    return request.post(`adminapp/site/todo`, params)
}


/**
 * 获取代办事项统计
 */
export function getAllTodo() {
    return request.get(`adminapp/site/todo_of_all`)
}

/**
 * 获取统计值
 */
export function geTotal(params: Record<string, any>) {
    return request.get(`${params.url}`)
}


/**
 * 首页应用
 */
export function getAppOfIndex() {
    return request.get(`adminapp/site/apps_of_index`)
}

/**
 * 设置首页应用
 */
export function setAppOfIndex(params: Record<string, any>) {
    return request.post(`adminapp/site/apps_of_index`, params)
}

/**
 * 应用
 */
export function getApp() {
    return request.get(`adminapp/site/apps`)
}