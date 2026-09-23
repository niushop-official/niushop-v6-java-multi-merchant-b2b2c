import { defineStore } from 'pinia'
import { getToken, setToken, removeToken, getAppType } from '@/utils/common'
import { login,mobileLogin, logout, getAdminAuthMenus, getSiteAuthMenus, getSiteInfo,getAddons } from '@/app/api/auth'
import { ElMessage } from 'element-plus'
import storage from '@/utils/storage'
import router from '@/router'
import { formatRouters, findFirstValidRoute } from '@/router/routers'
import useTabbarStore from './tabbar'
import Test from '@/utils/test'

interface User {
    token: string,
    userInfo: object,
    siteInfo: null | Record<string, any>,
    routers: any[],
    rules: any[],
    addonIndexRoute: Record<string, symbol>
    addons: string[]
}

const userStore = defineStore('user', {
    state: (): User => {
        return {
            token: getToken() || '',
            userInfo: storage.get('userinfo') || {},
            siteInfo: null,
            routers: [],
            rules: [],
            addonIndexRoute: {},
            addons: [],
        }
    },
    actions: {
        async getSiteInfo() {
            await getSiteInfo().then(({ data }) => {
                this.siteInfo = data
                storage.set({ key: 'siteId', data: data.site_id || 0 })
                storage.set({ key: 'siteInfo', data: data })
                storage.set({ key: 'comparisonSiteIdStorage', data: data.site_id || 0 })
            }).catch((res) => {
            })
        },
        login(form: object, app_type: any) {
            return new Promise((resolve, reject) => {
                login(form, app_type)
                    .then((res) => {
                        // if (app_type == 'admin' && Test.empty(res.data.userrole)) {
                        //     storage.setPrefix('site')
                        // }
                        this.token = res.data.token
                        this.userInfo = res.data.userinfo
                        this.siteInfo = res.data.site_info || {}
                        setToken(res.data.token)
                        storage.set({ key: 'userinfo', data: res.data.userinfo })
                        storage.set({ key: 'siteId', data: res.data.site_id || 0 })
                        storage.set({ key: 'siteInfo', data: res.data.site_info || {} })
                        storage.set({ key: 'comparisonSiteIdStorage', data: res.data.site_id || 0 })
                        storage.set({ key: 'comparisonTokenStorage', data: res.data.token })
                        resolve(res)
                    })
                    .catch((error) => {
                        reject(error)
                    })
            })
        },
        mobileLoginFn(form: object) {
            return new Promise((resolve, reject) => {
                mobileLogin(form).then((res) => {
                        this.token = res.data.token
                        this.userInfo = res.data.userinfo
                        this.siteInfo = res.data.site_info || {}
                        setToken(res.data.token)
                        storage.set({ key: 'userinfo', data: res.data.userinfo })
                        storage.set({ key: 'siteId', data: res.data.site_id || 0 })
                        storage.set({ key: 'siteInfo', data: res.data.site_info || {} })
                        storage.set({ key: 'comparisonSiteIdStorage', data: res.data.site_id || 0 })
                        storage.set({ key: 'comparisonTokenStorage', data: res.data.token })
                        resolve(res)
                    })
                    .catch((error) => {
                        reject(error)
                    })
            })
        },
        clearRouters() {
            this.routers = []
        },
        logout() {
            if (!this.token) return
            this.token = ''
            this.userInfo = {}
            this.siteInfo = {}
            removeToken()
            storage.remove(['userinfo', 'siteInfo'])
            this.routers = []
            logout()
            // 清除tabbar
            useTabbarStore().clearTab()
            //todo 商圈
            // const login = getAppType() == 'admin' ? '/admin/login' : '/site/login'
            const login = '/' + getAppType() + '/login'
            // router.push(login)
            location.href = `${location.origin}${login}`
        },
        getAuthMenus() {
            //todo 商圈
            const getAuthMenusFun = (getAppType() == 'admin' || getAppType() == 'circle') ? getAdminAuthMenus : getSiteAuthMenus
            return new Promise((resolve, reject) => {
                getAuthMenusFun({})
                    .then((res) => {
                        this.routers = formatRouters(res.data)
                        // 获取插件的首个菜单
                        this.routers.forEach((item, index) => {
                            if (item.meta.addon !== '') {
                                if (item.children && item.children.length) {
                                    this.addonIndexRoute[item.meta.addon] = findFirstValidRoute(item.children)
                                } else {
                                    this.addonIndexRoute[item.meta.addon] = item.name
                                }
                            }
                        })
                        resolve(res)
                    })
                    .catch((error) => {
                        reject(error)
						if(error.code == 403){
							ElMessage({ message: error.msg, type: 'error' })
							if (!this.token) return
							this.token = ''
							this.userInfo = {}
							this.siteInfo = {}
							removeToken()
							storage.remove(['userinfo', 'siteInfo'])
							this.routers = []
							// 清除tabbar
							useTabbarStore().clearTab()
							// const login = getAppType() == 'admin' ? '/admin/login' : '/site/login'
                            const login = '/' + getAppType() + '/login'
							location.href = `${location.origin}${login}`
						}
                    })
            })
        },
        setUserInfo(data: any) {
            this.userInfo = data
            storage.set({ key: 'userinfo', data: data })
        },
        checkAddon(addon: string) {
            return this.addons.includes(addon)
        },
        async getAddons() {
            await getAddons().then(({ data }) => {
                this.addons = data
               
            }).catch(() => {

            })
        },
    }
})

export default userStore
