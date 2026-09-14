import { toRaw } from 'vue'
import { RouteRecordRaw, RouterView } from 'vue-router'
import Default from '@/layout/index.vue'
import Decorate from '@/layout/decorate/index.vue'

// 静态路由
export const STATIC_ROUTES: Array<RouteRecordRaw> = [
    {
        path: '/:pathMatch(.*)*',
        component: () => import('@/app/views/error/404.vue')
    }
]

// 免登录路由
export const NO_LOGIN_ROUTES: string[] = [
    '/404'
]

// 根路由
export const ROOT_ROUTER: RouteRecordRaw = {
    path: '/',
    component: Default,
    name: Symbol()
}

// 平台端根路由
export const ADMIN_ROUTE: RouteRecordRaw = {
    path: '/admin',
    name: Symbol('admin'),
    children: [
        {
            path: '',
            name: Symbol('adminRoot'),
            component: Default
        },
        {
            path: 'login',
            component: () => import('@/app/views/login/index.vue')
        }
    ]
}

// 站点端根路由
export const SITE_ROUTE: RouteRecordRaw = {
    path: '/site',
    name: Symbol('site'),
    children: [
        {
            path: '',
            name: Symbol('siteRoot'),
            component: Default
        },
        {
            path: 'login',
            component: () => import('@/app/views/login/index.vue')
        }
    ]
}


export const CIRCLE_ROUTE: RouteRecordRaw = {
    path: '/circle',
    name: Symbol('circle'),
    children: [
        {
            path: '',
            name: Symbol('circleRoot'),
            component: Default
        },
        {
            path: 'login',
            component: () => import('@/app/views/login/index.vue'),
            meta: {
                addon: '',
                view: '/login'
            }
        }
    ]
}

export const DECORATE_ROUTER: RouteRecordRaw = {
    path: '/decorate',
    component: Decorate,
    name: Symbol('decorate'),
    children: []
}

const modules = import.meta.glob('@/app/views/**/*.vue')
const addonModules = import.meta.glob('@/addon/**/views/**/*.vue')

interface Route {
    menu_name: string,
    menu_short_name: string,
    router_path: string,
    view_path: string
    menu_type: number,
    menu_key: string,
    icon?: {
        name: string,
        type: string
    },
    children?: [],
    is_show: boolean,
    app_type: string,
    addon: string,
    menu_attr ?:  String
}

/**
 * 创建路由
 * @param data
 * @returns
 */
const createRoute = function (route: Route, parentRoute: RouteRecordRaw | null = null): RouteRecordRaw {
    const record: RouteRecordRaw = {
        path: `/${route.app_type}/${route.router_path}`,
        name: route.menu_key,
        meta: {
            title: route.menu_name,
            short_title: route.menu_short_name,
            icon: route.icon,
            type: route.menu_type,
            show: route.is_show,
            app: route.app_type,
            view: route.view_path,
            addon: route.addon,
            attr: route.menu_attr,
            parent_route: parentRoute ? parentRoute.meta : parentRoute
        }
    }
    if (route.menu_type == 0) {
        record.component = parentRoute ? RouterView : () => Promise.resolve(Default)
        if (!route.children) record.component = RouterView
    } else {
        if (route.view_path.indexOf('://') != -1) {
            route.addon = route.view_path.split('://')[0]
            route.view_path = route.view_path.split('://')[1]
        }
        // if(route.addon == 'o2o'){
        //     record.component = addonModules[`/src/addon/views/${route.view_path}.vue`]
        // }else{
            record.component = route.addon ? addonModules[`/src/addon/${route.addon}/views/${route.view_path}.vue`] : modules[`/src/app/views/${route.view_path}.vue`]
        // }   
    }
    return record
}

/**
 * 格式化路由
 * @param routes
 * @returns
 */
export function formatRouters(routes: Route[], parentRoute: RouteRecordRaw | null = null) {
    return routes.map((route) => {
        const routeRecord = createRoute(route, parentRoute)
        if (route.children != null && route.children && route.children.length) {
            routeRecord.children = formatRouters(route.children, routeRecord)
        }
        return routeRecord
    })
}

/**
 * 获取首条有效路由
 * @param routes
 * @returns
 */
export function findFirstValidRoute(routes: RouteRecordRaw[]): string | undefined {
    for (const route of routes) {
        if (route.meta?.type == 1 && route.meta?.show) {
            return route.name as string
        }
        if (route.children) {
            const name = findFirstValidRoute(route.children)
            if (name) {
                return name
            }
        }
    }
}
