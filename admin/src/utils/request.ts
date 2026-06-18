import axios, { HttpStatusCode } from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse, AxiosRequestConfig } from 'axios'
import { getToken, isUrl } from './common';
import { ElMessage } from 'element-plus'
import type { MessageParams } from 'element-plus'
import { t } from '@/lang'
import useUserStore from '@/stores/modules/user'
import storage from '@/utils/storage'
import { isAddonInstallRestarting } from '@/utils/addon-install-state'

interface RequestConfig extends AxiosRequestConfig {
    showErrorMessage?: boolean
    showSuccessMessage?: boolean
}

interface InternalRequestConfig extends InternalAxiosRequestConfig {
    showErrorMessage?: boolean
    showSuccessMessage?: boolean
}

interface requestResponse extends AxiosResponse {
    config: InternalRequestConfig
}

class Request {
    private instance: AxiosInstance;

    constructor() {
        this.instance = axios.create({
            baseURL: import.meta.env.VITE_APP_BASE_URL.substr(-1) == '/' ? import.meta.env.VITE_APP_BASE_URL : `${import.meta.env.VITE_APP_BASE_URL}/`,
            headers: {
                'Content-Type': 'application/json',
                'lang': storage.get('lang') ?? 'zh-cn'
            }
        });

        // 全局请求拦截器
        this.instance.interceptors.request.use(
            (config: InternalRequestConfig) => {
                // 携带token site-id
                if (getToken()) {
                    config.headers[import.meta.env.VITE_REQUEST_HEADER_TOKEN_KEY] = getToken()
                }
                config.headers[import.meta.env.VITE_REQUEST_HEADER_SITEID_KEY] = storage.get('siteId') || 0
                return config
            },
            (err: any) => {
                return Promise.reject(err)
            }
        )

        // 全局响应拦截器
        this.instance.interceptors.response.use(
			(response : requestResponse) => {
				if (response.request.responseType != 'blob') {
					const res = response.data
					if (res.code != 1) { // 业务错误（code != 1）
						this.handleAuthError(res.code) // 保留原有权限处理
						// 关键：针对/auth/site接口且code=403的情况，返回完整原始信息
						if ((response.config.url?.includes('auth/site') || response.config.url?.includes('auth/site/authmenu')) && res.code === 403) {
							return Promise.reject({
								...res, // 返回后端原始数据（包含code、msg、data等）
								originalResponse: response // 附加完整响应对象
							})
						}

						// 其他情况保持原有逻辑
						if (res.code != 401 && this.shouldShowError(response.config)) {
							this.showElMessage({
								message: res.msg,
								type: 'error',
								dangerouslyUseHTMLString: true,
								duration: 5000
							})
						}

						// 普通业务错误，保持原有结构
						const error : ExtendedError = new Error(res.msg || 'Error')
						error.code = res.code
						error.originalData = res
						error.originalResponse = response
						return Promise.reject(error)
					} else {
						// 成功响应保持不变
						if (response.config.showSuccessMessage) {
							ElMessage({ message: res.msg, type: 'success' })
						}
						return res
					}
				}
				return response.data
			},
			(err : any) => {
				// 网络错误保持原有处理
				this.handleNetworkError(err)
				return Promise.reject(err)
			}
		)
    }

    /**
     * 发送get请求
     * @param url
     * @param config
     * @returns
     */
    public get<T = any, R = AxiosResponse<T>>(url: string, config?: RequestConfig): Promise<R> {
        return this.instance.get(url, config)
    }

    /**
     * 发送get请求
     * @param url
     * @param config
     * @returns
     */
    public post<T = any, R = AxiosResponse<T>, D = any>(url: string, data?: D, config?: RequestConfig): Promise<R> {
        return this.instance.post(url, data, config)
    }

    /**
     * 发送get请求
     * @param url
     * @param config
     * @returns
     */
    public put<T = any, R = AxiosResponse<T>, D = any>(url: string, data?: D, config?: RequestConfig): Promise<R> {
        return this.instance.put(url, data, config)
    }

    /**
     * 发送get请求
     * @param url
     * @param config
     * @returns
     */
    public delete<T = any, R = AxiosResponse<T>>(url: string, config?: RequestConfig): Promise<R> {
        return this.instance.delete(url, config)
    }

    /**
     * 处理网络请求错误
     * @param err
     */
    private handleNetworkError(err: any) {
        let errMessage = ''

        if (err.response && err.response.status) {
            const errStatus = err.response.status
            switch (errStatus) {
                case 400:
                    errMessage = t('axios.400')
                    break
                case 401:
                    errMessage = t('axios.401')
                    break
                case 403:
                    errMessage = t('axios.403')
                    break
                case 404:
                    const baseURL = isUrl(err.response.config.baseURL) ? err.response.config.baseURL : `${location.origin}${err.response.config.baseURL}`
                    errMessage = baseURL + t('axios.baseUrlError')
                    break
                case 405:
                    errMessage = t('axios.405')
                    break
                case 408:
                    errMessage = t('axios.408')
                    break
                case 409:
                    errMessage = t('axios.409')
                    break
                case 500:
                    errMessage = t('axios.500')
                    break
                case 501:
                    errMessage = t('axios.501')
                    break
                case 502:
                    errMessage = t('axios.502')
                    break
                case 503:
                    errMessage = t('axios.503')
                    break
                case 504:
                    errMessage = t('axios.504')
                    break
                case 505:
                    errMessage = t('axios.505')
                    break
            }
        }
        err.message.includes('timeout') && (errMessage = t('axios.timeout'))
        if (err.code == 'ERR_NETWORK') {
            const baseURL = isUrl(err.config.baseURL) ? err.config.baseURL : `${location.origin}${err.config.baseURL}`
            errMessage = baseURL + t('axios.baseUrlError')
        }
        err.config && this.shouldShowError(err.config) && errMessage && this.showElMessage({ dangerouslyUseHTMLString: true, duration: 5000, message: errMessage, type: 'error' })
    }

    private shouldShowError(config: InternalRequestConfig): boolean {
        if (isAddonInstallRestarting()) return false
        return config.showErrorMessage !== false
    }

    private handleAuthError(code: number) {
        switch (code) {
            case 401:
                useUserStore().logout()
                break;
        }
    }

    private messageCache = new Map();

    private showElMessage(options: MessageParams) {
        const cacheKey = options.message
        const cachedMessage = this.messageCache.get(cacheKey);

        if (!cachedMessage || Date.now() - cachedMessage.timestamp > 5000) { // 5秒内重复内容不再弹出，可自定义过期时间
            this.messageCache.set(cacheKey, { timestamp: Date.now() });
            ElMessage(options)
        }
    }
}

export default new Request()
