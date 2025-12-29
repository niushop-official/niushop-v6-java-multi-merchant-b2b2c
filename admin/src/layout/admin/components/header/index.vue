<template>
    <el-container class="h-[120px] px-[20px] bg-[#2B303B] block">
        <div class="h-[60rpx] flex items-center justify-between">
            <!-- logo -->
            <div class="logo-wrap">
                <div class="logo flex items-center m-auto h-[60px] max-w-[190px] box-border pr-[10px]">
                    <el-image style="width: 100%; height: 100%" :src="img(logoUrl)" fit="contain">
                        <template #error>
                            <div class="flex justify-center items-center w-full h-full"><img class="max-h-[60px] max-w-[100%]" src="@/app/assets/images/site_login_logo.png" alt=""  object-fit="contain"></div>
                        </template>
                    </el-image>
                </div>
            </div>
            <div class="text-[#fff] text-[16px] font-500">{{ siteInfo?.site_name }}</div>
            <div class="right-panel h-full flex items-center justify-end">
                <!-- 预览 只有站点时展示-->
                <i class="iconfont iconlingdang-xianxing cursor-pointer px-[8px]" :title="t('newInfo')" v-if="appType == 'site'"></i>
                <!-- 切换语言 -->
                <!-- <div class="navbar-item flex items-center h-full cursor-pointer">
                    <switch-lang />
                </div> -->
                <!-- 切换全屏 -->
                <!-- <div class="navbar-item flex items-center h-full cursor-pointer" @click="toggleFullscreen">
                    <icon name="iconfont-icontuichuquanping" v-if="isFullscreen" />
                    <icon name="iconfont-iconquanping" v-else />
                </div> -->
                <!-- 面包屑导航 -->
                <!-- <div class="flex items-center h-full pl-[10px] hidden-xs-only">
                    <el-breadcrumb separator="/">
                        <el-breadcrumb-item v-for="(route, index) in breadcrumb" :key="index">{{route.meta.title }}</el-breadcrumb-item>
                    </el-breadcrumb>
                </div> -->
                <!-- 刷新当前页 -->
                <div class="navbar-item flex items-center h-full cursor-pointer" @click="refreshRouter">
                    <icon name="element Refresh" color="#fff" />
                </div>
                <!-- 预览-->
                <el-popover placement="bottom" :width="400" trigger="hover">
                    <template #reference>
                        <i class="iconfont iconicon_huojian1 cursor-pointer text-[#fff] px-[8px]" :title="t('visitAdmin')"></i>
                    </template>
                    <div class="px-[8px]">
                        <div class="pt-[8px] pb-[20px] border-0 border-b-[1px] border-solid border-[#E5E5E5] cursor-pointer" @click="toWeb">
                            <div class="flex items-center text-[#333] mb-[14px]">
                                <span class="nc-iconfont nc-icon-gongzuotai mr-[10px]"></span>
                                <span class="text-[14px] font-500">PC端</span>
                            </div>
                            <div class="text-[14px] text-[#333]">
                                <span>链接：</span>
                                <span class="hover:text-primary">{{ webUrl  }}</span>
                            </div>
                        </div>
                        <div class="py-[20px] cursor-pointer" @click="toPreview">
                            <div class="flex items-center text-[#333] mb-[14px]">
                                <span class="nc-iconfont nc-icon-shoujiV6xx1 mr-[10px]"></span>
                                <span class="text-[14px] font-500">H5端</span>
                            </div>
                            <div class="text-[14px] text-[#333]">
                                <span>链接：</span>
                                <span class="hover:text-primary">{{ wapUrl  }}</span>
                            </div>
                        </div>
                    </div>
                </el-popover>
                <!-- 布局设置 -->
                <div class="navbar-item flex items-center h-full cursor-pointer">
                    <layout-setting />
                </div>
                <!-- 用户信息 -->
                <div class="navbar-item flex items-center h-full cursor-pointer">
                    <user-info />
                </div>
            </div>
        </div>
        <div class="flex items-center">
            <div class="left-panel h-[60px] flex items-center flex-1">
                <div class="flex items-center h-[60px] leading-[60px] text-[#fff]">
                    <template v-for="(item, index) in oneMenuData" :key="index">
                        <div v-if="item.meta.show" class="px-[12px] flex items-center cursor-pointer" :class="{'bg-[var(--el-color-primary)]':oneMenuActive===item.original_name}" @click="handleTabClick(item.name)">
                            <div class="w-[20px] h-full flex items-center mr-[8px]">
                                <template v-if="item.meta.icon">
                                    <el-image class="w-[16px] h-[16px] overflow-hidden" :src="item.meta.icon" fit="fill" v-if="isUrl(item.meta.icon)"/>
                                    <icon :name="item.meta.icon" v-else />
                                </template>
                                <icon v-else :name="'iconfont iconshezhi1'" />
                            </div>
                            <span class="w-full truncate text-[14px]">{{ item.meta.short_title || item.meta.title }}</span>
                        </div>
                    </template>
                </div>
            </div>
            <div class="flex items-center flex-shrink-0">
                <el-popover placement="bottom" width="130px" trigger="hover" opper-class="!min-w-[130px]">
                    <template #reference>
                        <div class="w-[90px] h-[28px] bg-[#FF7D00] text-[14px] text-[#fff] flex items-center justify-center cursor-pointer">商家手机端</div>
                    </template>
                    <div class="flex justify-center">
                        <div>
                            <div class="flex justify-center items-center w-[110px] h-[110px] rounded-[4px] border-solid border-[1px] border-[#eee] mb-[10px]">
                                <el-image :src="wapImage" class=" w-[100px] h-[100px]" />
                            </div>
                            <div class="text-center  flex items-center justify-center">
                                <span class="text-[12px] text-primary cursor-pointer" @click="copy(wapPreview)">复制链接</span>
                            </div>
                        </div>
                    </div>
                </el-popover>
                <div class="w-[90px] h-[28px] bg-[#F53F3F] text-[14px] text-[#fff] flex items-center justify-center cursor-pointer ml-[10px]" @click="toArticle">最新动态</div>
                <div class="w-[100px] h-[28px] bg-[#00B42A] text-[14px] text-[#fff] flex items-center justify-center cursor-pointer ml-[10px]" @click="toVersion">版本号V{{ systemStore.website?.version?.version }}</div>
            </div>
        </div>
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
import { computed, ref, onMounted, watch } from 'vue'
import layoutSetting from './layout-setting.vue'
import userInfo from './user-info.vue'
import { useFullscreen } from '@vueuse/core'
import useSystemStore from '@/stores/modules/system'
import useAppStore from '@/stores/modules/app'
import { useRoute, useRouter } from 'vue-router'
import { t } from '@/lang'
import storage from '@/utils/storage'
import { img, isUrl, copy } from '@/utils/common'
import useUserStore from '@/stores/modules/user'
import { ADMIN_ROUTE, findFirstValidRoute } from '@/router/routers'
import QRCode from 'qrcode'

const appType = storage.get('app_type')
const { toggle: toggleFullscreen } = useFullscreen()
const systemStore = useSystemStore()
const appStore = useAppStore()
const route = useRoute()
const screenWidth = ref(window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth)

// 菜单数据,logo
const userStore = useUserStore()
const siteInfo = userStore.siteInfo

const logoUrl = computed(() => {
    return userStore.siteInfo.icon ? userStore.siteInfo.icon : systemStore.website.icon
})
const router = useRouter()
const routers = userStore.routers

const oneMenuData = ref<Record<string, any>[]>([])
routers.forEach(item => {
    item.original_name = item.name
    if (item.meta.addon == '') {
        if (item.meta.attr == '') {
            if (item.children && item.children.length) {
                item.name = findFirstValidRoute(item.children)
            }
            oneMenuData.value.push(item)
        }
    } else if (item.meta.addon != '' && siteInfo?.apps.length <= 1 && siteInfo?.apps[0].key == item.meta.addon) {
        if (item.children && item.children.length) {
            item.name = findFirstValidRoute(item.children)
        }
        oneMenuData.value.push(item)
    }
})
const oneMenuActive = ref(oneMenuData.value[0].name)

const twoMenuData = ref<Record<string, any>[]>([])
watch(route, () => {
    if (route.meta.attr != '') {
        oneMenuActive.value = route.matched[1].name
        twoMenuData.value = route.matched[2].children ?? []
    } else {
        oneMenuActive.value = route.matched[1].name == ADMIN_ROUTE.children[0].name ? route.matched[2].name : route.matched[1].name
        twoMenuData.value = route.matched[1].children ?? []
    }
}, { immediate: true })
const handleTabClick = (name:any) => {
    router.push({ name: name })
}
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
    location.href = `${location.origin}/admin/`
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

// 跳转去预览
const webUrl = location.origin + '/web/'
const wapUrl = location.origin + '/wap/'

const toWeb = () => {
    window.open(webUrl)
}
const toPreview = () => {
    const url = router.resolve({
        path: '/preview/wap',
        query: {
            page: '/app/pages/index/index'
        }
    })
    window.open(url.href)
}

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
// const breadcrumb = computed(() => {
//     const matched = route.matched.filter(item => { return item.meta.title })
//     if (matched[0] && matched[0].path == '/') matched.splice(0, 1)
//     return matched
// })

// 返回上一页
// const backFn = () => {
//     router.go(-1)
// }

// 商家手机管理端
const wapPreview = ref('')
const wapImage = ref('')
const loadQrcode = () => {
    wapPreview.value = `${location.origin}/adminapp/`
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}
loadQrcode()
const toArticle = () => {
    router.push({ path: '/admin/article/list' })
}
const toVersion = () => {
    router.push({ path: '/admin/tools/authorize' })
}
</script>

<style lang="scss" scoped>
.layout-header{
  position: relative;
  z-index: 5;
  box-shadow: 0px 0px 4px 0px rgba(0,145,255,0.1);
}
.navbar-item {
  padding: 0 8px;
  // &:hover {
  //     background-color: var(--el-bg-color-page);
  // }
}
.index-item {
  border: 1px solid;
  border-color: var(--el-color-primary);
  &:hover {
    color: #fff;
    background-color: var(--el-color-primary);
  }
}
.logo-wrap {
  padding: 0;
  display: flex;
  white-space: nowrap;
  align-items: center;

  .logo {
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
