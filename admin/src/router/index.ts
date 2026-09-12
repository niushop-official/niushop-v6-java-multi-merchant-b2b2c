import { createRouter, createWebHistory, RouteLocationRaw, RouteLocationNormalizedLoaded } from 'vue-router'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { STATIC_ROUTES, NO_LOGIN_ROUTES, ROOT_ROUTER, ADMIN_ROUTE, SITE_ROUTE, CIRCLE_ROUTE, findFirstValidRoute } from './routers'
import { language } from '@/lang'
import useSystemStore from '@/stores/modules/system'
import useUserStore from '@/stores/modules/user'
import { setWindowTitle, getAppType, urlToRouteRaw } from '@/utils/common'
import storage from '@/utils/storage'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [ADMIN_ROUTE, SITE_ROUTE, CIRCLE_ROUTE, ...STATIC_ROUTES]
})

/**
 * 重写push方法
 */
const originPush = router.push
router.push = (to: RouteLocationRaw) => {
    const route = typeof to == 'string' ? urlToRouteRaw(to) : to
    if (route.path) {
        const paths = route.path.split('/').filter((item: string) => { return item })
        const app_type = getAppType()
        if (['admin', 'site', 'home'].indexOf(paths[0]) == -1 && paths[0] != app_type) {
            route.path = `/${app_type}${route.path}`
        }
    }
    return originPush(route)
}

/**
 * 重写resolve方法
 */
const originResolve = router.resolve
router.resolve = (to: RouteLocationRaw, currentLocation?: RouteLocationNormalizedLoaded) => {
    const route = typeof to == 'string' ? urlToRouteRaw(to) : to
    if (route.path) {
        const paths = route.path.split('/').filter((item: string) => { return item })
        const app_type = getAppType()
        if (['admin', 'site', 'home'].indexOf(paths[0]) == -1 && paths[0] != app_type) {
            route.path = `/${app_type}${route.path}`
        }
    }
    return originResolve(route, currentLocation)
}

// 全局前置守卫
router.beforeEach(async (to: any, from, next) => {
    NProgress.configure({ showSpinner: false })
    NProgress.start()

    to.redirectedFrom && (to.query = to.redirectedFrom.query)

    const userStore = useUserStore()
    const systemStore = useSystemStore()
    const appType = getAppType()
    const title = []

    to.meta.title && title.push(to.meta.title)

    if (!userStore.siteInfo && appType != 'home') {
        await userStore.getSiteInfo()
        await userStore.getAddons()
    }

    // 加载语言包
    await language.loadLocaleMessages(to.meta.addon || '', (to.meta.view || to.path), systemStore.lang);

    let matched: any = to.matched;
    if (matched && matched.length && matched[0].path != '/:pathMatch(.*)*') {
        matched = matched[0].path;
    } else {
        matched = '/' + appType
    }

    let tempLoginPath = '';
    if(to.path == '/'){
        tempLoginPath = '/admin/login' 
    }else{
        let temp_matched = matched.replace(/\//g, '');
        tempLoginPath = `/${temp_matched}/login`
    }
    const loginPath = tempLoginPath;
    if (appType != 'site' || to.path === loginPath) {
        systemStore.website.site_name && title.push(systemStore.website.site_name)
    } else {
        userStore.siteInfo && userStore.siteInfo.site_name && title.push(userStore.siteInfo.site_name)
    }

    // 设置网站标题
    setWindowTitle(title.join('-'))

    // 判断是否需登录
    if (NO_LOGIN_ROUTES.includes(to.path)) {
        next()
    } else if (userStore.token) {
        // 如果已加载路由
        if (userStore.routers.length) {
            if (to.path === loginPath) {
                next(`/${getAppType()}`)
            } else {
                next()
            }
        } else {
            try {
                await userStore.getAuthMenus()

                // 设置首页路由
                const firstRoute: symbol | string | undefined = findFirstValidRoute(userStore.routers)

                ROOT_ROUTER.redirect = { name: firstRoute }
                router.addRoute(ROOT_ROUTER)

                // 设置应用首页路由
                if (getAppType() == 'admin') {
                    ADMIN_ROUTE.children[0].redirect = { name: firstRoute }
                    router.addRoute(ADMIN_ROUTE)
                } else if (getAppType() == 'circle') {
                    CIRCLE_ROUTE.children[0].redirect = { name: firstRoute }
                    router.addRoute(CIRCLE_ROUTE)
                } else {
                    SITE_ROUTE.children[0].redirect = { name: firstRoute }
                    router.addRoute(SITE_ROUTE)
                }

                // 添加动态路由
                userStore.routers.forEach(route => {
                    if (!route.children) {
                        if (route.meta.app == 'admin') {
                            router.addRoute(ADMIN_ROUTE.children[0].name, route)
                        } else if (route.meta.app == 'site'){
                            router.addRoute(SITE_ROUTE.children[0].name, route)
                        }else if (route.meta.app == 'circle'){
                            router.addRoute(CIRCLE_ROUTE.children[0].name, route)
                        }
                        return
                    }

                    // 动态添加可访问路由表
                    if (route.meta.app == 'admin') {
                        router.addRoute(ADMIN_ROUTE.name, route)
                    }else if (route.meta.app == 'site'){
                        router.addRoute(SITE_ROUTE.name, route)
                    }else if (route.meta.app == 'circle'){
                        router.addRoute(CIRCLE_ROUTE.name, route)
                    }
                })
                next(to)
            } catch (err) {
                next({ path: loginPath })
            }
        }
    } else {
        if (to.path === loginPath) {
            next()
        } else {
            next({ path: loginPath })
        }
    }
})

// 全局后置钩子
router.afterEach(() => {
    NProgress.done()
})

export default router
