<template>
    <el-container class="w-100 h-[100%]">
        <el-main class="p-0 flex">
            <el-scrollbar v-if="twoMenuData.length" class="two-menu box-border" :class="{'w-[236px] px-[10px]': !isCollapse}">
                <div class="h-[44px] flex items-center border-0 border-b-[1px] border-solid border-[#e6e6e6] mb-[10px]" v-if="!isCollapse">
                    <span class="text-[14px] font-bold">{{ route.matched[1].meta.title }}</span>
                </div>
                <el-menu :key="route.name" :default-active="route.name" :router="true" class="aside-menu" :collapse="isCollapse" :default-openeds="defaultOpeneds">
                    <menu-item v-for="(route, index) in twoMenuData" :routes="route" :key="index" :collapse="isCollapse" />
                </el-menu>
                <div class="h-[48px]"></div>
            </el-scrollbar>
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import menuItem from './menu-item.vue'

const route = useRoute()
const isCollapse = ref(false)

const twoMenuData = ref<Record<string, any>[]>([])
const defaultOpeneds = ref<string[]>([])

watch(route, () => {
    twoMenuData.value = route.matched[1].children ?? []
    defaultOpeneds.value = []
    twoMenuData.value.forEach((route, index) => {
        defaultOpeneds.value.push(String(route.name))
    })
}, { immediate: true })

// 控制是否展开/合并
const isCollapseFn = () => {
    isCollapse.value = !isCollapse.value
    if (!isCollapse.value) {
        defaultOpeneds.value = []
        setTimeout(() => {
            twoMenuData.value.forEach((route, index) => {
                defaultOpeneds.value.push(String(route.name))
            })
        }, 100)
    }
}
</script>

<style>
:root,
body {
    --layout-side-hover-bg: #f7f8fa;
    --layout-side-active-bg: var(--el-color-primary-light-9);
    --layout-side-active-text: var(--el-color-primary);
}
</style>
<style lang="scss">
.two-menu{
    .aside-menu:not(.el-menu--collapse) {
        border: 0;
        .el-menu-item{
            height:35px !important;
            line-height: 1;
            &.is-active{
                color: var(--layout-side-active-text) !important;
                font-weight: 700;
            }
        }
    }
    .el-sub-menu{
        .el-sub-menu__title{
            height: 40px;
            padding: 0 !important;
            line-height: 1;
            color: #999;
            &:hover{
                color: var(--layout-side-active-text);
                background-color: transparent !important;
            }
            .el-icon.el-sub-menu__icon-arrow{
                right: 5px;
                font-size: 14px;
                color: #bdbfc7;
            }
        }
        .el-menu.el-menu--inline{
            display: flex;
            flex-wrap: wrap;
            gap: 0px 10px;
            margin-left: 15px;
            .el-menu-item{
                height:35px !important;
                width: 95px;
                text-indent: 10px;
                line-height: 1;
                color: #333;
                white-space: break-spaces;
                &:hover{
                    background-color: transparent;
                    color: var(--layout-side-active-text);
                }
                &.is-active{
                    color: var(--layout-side-active-text) !important;
                    font-weight: 700;
                }
            }
        }
    }

    .el-menu-item{
        height: 40px;
        padding: 0 !important;
        color: #999;
        &:hover{
            background-color: var(--layout-side-hover-bg);
        }
    }

    // 二级菜单折叠后的样式
    .el-menu.el-menu--vertical.el-menu--collapse.aside-menu{
        width: auto;
        border: 0;
        .el-menu-item{
            margin: 5px !important;
            min-height: 40px;
            &:hover{
                background-color: var(--layout-side-hover-bg);
            }
        }
        .el-sub-menu{
            min-height: auto;
            overflow: hidden;
            .el-menu-tooltip__trigger, .el-tooltip__trigger{
                padding: 0;
                margin: 5px !important;
                width: 40px;
                height: 40px;
                display: flex;
                justify-content: center;
                align-items: center;
            }
        }
    }
}
// 二级菜单折叠之后，弹窗样式
.el-menu--vertical.el-menu--popup-container{
    .el-menu--popup{
        padding: 8px;
        min-width: auto;
    }
    .el-menu-item, .el-sub-menu, .el-sub-menu{
        height: auto!important;
        padding: 10px;
        line-height: 1;
        color: #999;
        &.is-active{
            color: #333;
            background-color: transparent;
        }
        &:hover{
            background-color: var(--layout-side-hover-bg);
        }
    }
}

.logo-wrap {
    padding: 0;
    display: flex;
    white-space: nowrap;
    align-items: center;

    .logo {
        height: 100%;
        box-sizing: border-box;
    }

    .logo-title {
        flex: 1;
        width: 0;
        text-overflow: ellipsis;
        overflow: hidden;
        font-size: var(--el-font-size-base);
    }
}
</style>
