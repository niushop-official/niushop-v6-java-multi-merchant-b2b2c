<template>
    <el-container class="w-[200px] h-[calc(100vh-64px)] layout-aside flex flex-col">
        <el-main class="menu-wrap">
            <el-scrollbar>
                <el-menu :default-active="route.name" :router="true" class="aside-menu h-full" :unique-opened="true" :collapse="systemStore.menuIsCollapse">
                    <menu-item v-for="(route, index) in menuData" :routes="route" :key="index" />
                </el-menu>
                <div class="h-[48px]"></div>
            </el-scrollbar>
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { useRoute } from 'vue-router'
import useSystemStore from '@/stores/modules/system'
import useUserStore from '@/stores/modules/user'
import menuItem from './menu-item.vue'
import { findFirstValidRoute } from '@/router/routers'

const systemStore = useSystemStore()
const userStore = useUserStore()
const route = useRoute()
const siteInfo = userStore.siteInfo
const routers = userStore.routers
const addonIndexRoute = userStore.addonIndexRoute
const menuData = ref<Record<string, any>[]>([])
const addonRouters: Record<string, any> = {}
const addonOrder = ref<string[]>([])

routers.forEach(item => {
    item.original_name = item.name
    if (item.meta.addon == '') {
        if (item.meta.attr == '') {
            if (item.children && item.children.length) {
                item.name = findFirstValidRoute(item.children)
            }
            menuData.value.push(item)
        }
    } else if (item.meta.addon != '' && siteInfo?.apps.length == 1 && siteInfo?.apps[0].key == item.meta.addon) {
        if (item.children) {
            item.children.forEach((citem: Record<string, any>) => {
                citem.original_name = citem.name
                if (citem.children && citem.children.length) {
                    citem.name = findFirstValidRoute(citem.children)
                }
            })
            menuData.value.unshift(...item.children)
        } else {
            menuData.value.unshift(item)
        }
    } else {
        const addonKey = item.meta.addon
        // 仅在第一次出现时记录顺序，避免重复
        if (!addonRouters[addonKey]) {
            addonOrder.value.push(addonKey)
            addonRouters[addonKey] = item
        }
    }
})

// 多应用时将应用插入菜单
if (siteInfo?.apps.length > 1) {
    const routers:Record<string, any>[] = []
    // 先按addonOrder的顺序遍历，保证原始顺序
    addonOrder.value.forEach(addonKey => {
        // 再判断是否在siteInfo.apps中存在
        const isInApps = siteInfo?.apps.some(app => app.key === addonKey)
        if (isInApps) {
            addonRouters[addonKey].name = addonIndexRoute[addonKey]
            routers.push(addonRouters[addonKey])
        }
    })
    menuData.value.unshift(...routers)
}
</script>

<style lang="scss">
.menu-wrap {
    padding: 0!important;

    .el-menu {
        border-right: 0!important;

        .el-menu-item, .el-sub-menu__title {
            --el-menu-item-height: 40px;
        }

        .el-sub-menu .el-menu-item {
            --el-menu-sub-item-height: 40px;
        }
    }
}
</style>
