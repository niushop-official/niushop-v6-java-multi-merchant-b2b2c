<template>
  <el-container :class="['h-full px-[10px]',{'layout-header border-b border-color': !dark}]" >
    <el-row class="w-100 h-full w-full">
        <el-col :span="12">
            <div class="left-panel h-full flex items-center">
            <el-header class="logo-wrap flex items-center justify-center ">
                <div class="logo flex items-center m-auto " v-if="!systemStore.menuIsCollapse">
                    <el-image style="width: 30px; height: 30px" class="rounded-[6px]" :src="img(logoUrl ? logoUrl : '' )" fit="contain">
                        <template #error>
                            <div class="flex justify-center items-center w-full h-[30px] rounded-[6px]"><img class="max-w-[30px]" src="@/app/assets/images/icon-addon.png" alt=""  object-fit="contain"></div>
                        </template>
                    </el-image>
                </div>
                <div class="logo flex items-center justify-center" v-else>
                    <i class="text-3xl iconfont iconyunkongjian"></i>
                </div>
                <!-- 店铺名称 -->
                <div class="ml-[10px]">{{siteInfo.site_name}}</div>
            </el-header>
            <!-- 刷新当前页 -->
            <!-- <div class="navbar-item flex items-center h-full cursor-pointer" @click="refreshRouter">
                <icon name="element Refresh" />
            </div> -->
            <!-- 面包屑导航 -->
            <!-- <div class="flex items-center h-full pl-[10px] hidden-xs-only">
                <el-breadcrumb separator="/">
                <el-breadcrumb-item v-for="(route, index) in breadcrumb" :key="index">{{route.meta.title }}</el-breadcrumb-item>
                </el-breadcrumb>
            </div> -->
            </div>
        </el-col>
        <el-col :span="12">
            <div class="right-panel h-full flex items-center justify-end">
            <!-- 店铺套餐 -->
            <div class="mx-[10px] text-[14px] bg-[#E6F1FE] rounded-[8px] px-[6px] py-[4px] leading-normal flex items-center cursor-pointer" v-if="appType == 'site'" @click="changeBusinessStatus()">
                <span class="nc-iconfont nc-icon-dianpu text-[#2878FF] text-[15px] mr-[5px]"></span>
                <span class="text-[#2878FF]" v-if="siteInfo.business_status">营业中</span>
                <span class="text-[#2878FF]" v-else>暂停营业</span>
            </div>
            <!-- 商家手机管理端 -->
            <i class="nc-iconfont nc-icon-shoujiV6xx cursor-pointer px-[8px]" :title="t('商家助手')" @click="toSiteApp"></i>
            <!-- 切换语言 -->
            <!-- <div class="navbar-item flex items-center h-full cursor-pointer">
                <switch-lang />
            </div> -->
            <!-- 切换全屏 -->
            <!-- <div class="navbar-item flex items-center h-full cursor-pointer" @click="toggleFullscreen">
                <icon name="iconfont-icontuichuquanping" v-if="isFullscreen" />
                <icon name="iconfont-iconquanping" v-else />
            </div> -->
            <!-- 布局设置 -->
            <div class="navbar-item flex items-center h-full cursor-pointer">
                <layout-setting />
            </div>
            <!-- 预览 只有站点时展示-->
            <i class="iconfont iconicon_huojian1 cursor-pointer px-[8px]" :title="t('visitWap')" @click="toPreview"></i>
            <i class="iconfont iconlingdang-xianxing cursor-pointer px-[8px]" :title="t('newInfo')" v-if="appType == 'site'"></i>
            <!-- 用户信息 -->
            <div class="navbar-item flex items-center h-full cursor-pointer">
                <user-info />
            </div>
            </div>
        </el-col>
    </el-row>
    <input type="hidden" v-model="comparisonToken">
    <input type="hidden" v-model="comparisonSiteId">

    <el-dialog v-model="detectionLoginDialog" :title="t('layout.detectionLoginTip')" width="30%" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
      <span>{{ t('layout.detectionLoginContent') }}</span>
      <template #footer>
                <span class="dialog-footer">
                    <el-button @click="detectionLoginFn">{{ t('layout.detectionLoginOperation') }}</el-button>
                </span>
      </template>
    </el-dialog>
  </el-container>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted } from 'vue'
import layoutSetting from './layout-setting.vue'
import userInfo from './user-info.vue'
import { useFullscreen } from '@vueuse/core'
import useSystemStore from '@/stores/modules/system'
import useAppStore from '@/stores/modules/app'
import useUserStore from '@/stores/modules/user'
import { useRoute, useRouter } from 'vue-router'
import { updateBusinessStatus } from '@/app/api/shop_site'
import { t } from '@/lang'
import { img } from '@/utils/common'
import storage from '@/utils/storage'

const appType = storage.get('app_type')
const { toggle: toggleFullscreen } = useFullscreen()
const systemStore = useSystemStore()
const appStore = useAppStore()
const route = useRoute()
const router = useRouter()

const userStore = useUserStore()
const siteInfo = computed(() => {
    return userStore.siteInfo
})

const logoUrl = computed(() => {
    return userStore.siteInfo.front_end_logo ? userStore.siteInfo.front_end_logo : systemStore.website.front_end_logo
})

const screenWidth = ref(window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth)

// 检测登录 start
const detectionLoginDialog = ref(false)
const comparisonToken = ref('')
const comparisonSiteId = ref('')
if (storage.get('comparisonTokenStorage')) {
    comparisonToken.value = storage.get('comparisonTokenStorage')
    // storage.remove(['comparisonTokenStorage']);
}
if (storage.get('comparisonSiteIdStorage')) {
    comparisonSiteId.value = storage.get('comparisonSiteIdStorage')
    // storage.remove(['comparisonSiteIdStorage']);
}
// 监听标签页面切换
document.addEventListener('visibilitychange', e => {
    if (document.visibilityState === 'visible' && (comparisonSiteId.value != storage.get('siteId') || comparisonToken.value != storage.get('token'))) {
        detectionLoginDialog.value = true
    }
})

const detectionLoginFn = () => {
    detectionLoginDialog.value = false
    location.href = `${location.origin}/site/`
}
// 检测登录 end

onMounted(() => {
    // 监听窗体宽度变化
    window.onresize = () => {
        return (() => {
            screenWidth.value = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth
        })()
    }
})

// watch(screenWidth, () => {
//     if (screenWidth.value < 992) {
//         if (!systemStore.menuIsCollapse) systemStore.toggleMenuCollapse(true)
//     } else {
//         if (systemStore.menuIsCollapse) systemStore.toggleMenuCollapse(false)
//     }
// })

// 菜单栏展开折叠
// const toggleMenuCollapse = () => {
//     systemStore.$patch((state) => {
//         if (screenWidth.value < 768) {
//             state.menuDrawer = true
//             state.menuIsCollapse = false
//         } else {
//             systemStore.toggleMenuCollapse(!systemStore.menuIsCollapse)
//         }
//     })
// }

// 刷新路由
const refreshRouter = () => {
    if (!appStore.routeRefreshTag) return
    appStore.refreshRouterView()
}

// 面包屑导航
const breadcrumb = computed(() => {
    const matched = route.matched.filter(item => { return item.meta.title })
    if (matched[0] && matched[0].path == '/') matched.splice(0, 1)
    return matched
})

// 跳转去预览
const toPreview = () => {
    const url = router.resolve({
        path: '/preview/wap',
        query: {
            page: `/addon/mall/pages/index?site_id=${siteInfo.value.site_id}`
        }
    })
    window.open(url.href)
}

// 商家手机管理端
const toSiteApp = () => {
    window.open(location.origin + '/adminapp')
}
const changeBusinessStatus = () => {
    const data = {
        business_status: siteInfo.value.business_status ? 0 : 1
    }
    updateBusinessStatus(data).then((res: any) => {
        useUserStore().getSiteInfo()
        useSystemStore().getWebsiteInfo()
    })
}

// 返回上一页
// const backFn = () => {
//     router.go(-1)
// }
</script>

<style lang="scss" scoped>
.layout-header{
  position: relative;
  z-index: 5;
  box-shadow: 0px 0px 4px 0px rgba(0,145,255,0.1);
}
.navbar-item {
  padding: 0 8px;
  &:hover {
    background-color: var(--el-bg-color-page);
  }
}
.index-item {
  border: 1px solid;
  border-color: var(--el-color-primary);
  &:hover {
    color: #fff;
    background-color: var(--el-color-primary);
  }
}

</style>
