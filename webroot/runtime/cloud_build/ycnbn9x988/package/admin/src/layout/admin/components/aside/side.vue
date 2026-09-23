<template>
    <el-container class="h-full bg-[var(--el-bg-color-overlay)]" v-if="twoMenuData.length">
        <el-main class="p-0 flex">
            <el-scrollbar class="two-menu w-[200px]">
                <div class="w-[180px] h-[76px] mx-[10px] flex items-center justify-center text-[26px] border-0 border-b-[1px] border-solid border-[#EBEEF5]">{{ route.matched[1].meta.short_title }}</div>
               <el-menu :default-active="route.name" :router="true" class="aside-menu" :collapse="systemStore.menuIsCollapse">
                    <menu-item v-for="(route, index) in twoMenuData" :routes="route" :key="index" />
                </el-menu>
                <div class="h-[48px]"></div>
            </el-scrollbar>
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import { nextTick, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import menuItem from './menu-item.vue'
import useSystemStore from '@/stores/modules/system'
const systemStore = useSystemStore()
const route = useRoute()
const twoMenuData = ref<Record<string, any>[]>([])

// routers.forEach(item => {
//     item.original_name = item.name
//     if (item.children && item.children.length) {
//         item.name = findFirstValidRoute(item.children)
//     }
//     oneMenuData.value.push(item)
// })

// const oneMenuActive = ref(route.matched[1].name)

watch(route, () => {
    // oneMenuActive.value = route.matched[1].name == ADMIN_ROUTE.children[0].name ? route.matched[2].name : route.matched[1].name
    nextTick(() => {
        twoMenuData.value = route.matched[1].children ?? []
    })

}, { immediate: true })
</script>

<style lang="scss">
.one-menu{
    .aside-menu:not(.el-menu--collapse) {
        background-color: transparent;
        .el-menu-item{
            margin-bottom: 4px;
            height: 40px;
            padding-left: 12px !important;
            color: rgba(255,255,255,.7);
            font-size: 14px;
            border-radius: 2px;
            &:hover{
                background-color: var(--el-color-primary);
                color: #fff;
            }
            &.is-active{
                background-color: var(--el-color-primary) !important;
                color: #fff;
            }
            span{
                font-size: 14px;
                margin-left: 8px;
            }
        }
    }
    .el-menu{
        border: 0;
    }
    .el-scrollbar{
        height: calc(100vh - 65px);
    }
}
.two-menu{
    .aside-menu:not(.el-menu--collapse) {
        width: 100%;
        border: 0;
        padding-top: 14px;
        .el-menu-item{
            height: 36px;
            margin: 0 0 4px;
            padding: 0 8px !important;
            border-radius: 0;
            span{
                margin-left: 8px;
                font-size: 14px;
            }
            &.is-active{
                background-color: var(--el-color-primary-light-9) !important;
            }
            &:hover{
                background-color: #f7f7f7;
                color: var(--el-color-primary);
            }
        }
        .el-sub-menu{
            margin-bottom: 8px;
            .el-sub-menu__title{
                margin: 0 0 4px;
                height: 36px;
                padding-left: 8px;
                border-radius: 2px;
                span{
                    height: 36px;
                    display: flex;
                    align-items: center;
                    font-size: 14px;
                }
                &:hover{
                    background-color: #f7f7f7;
                    color: var(--el-color-primary);
                }
                .el-icon.el-sub-menu__icon-arrow{
                    right: 15px;
                    top:12px;
                }
            }
            .el-menu-item{
                padding-left: 20px !important;
            }
        }
    }
}


</style>
