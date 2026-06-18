<template>
  <el-container :class="['h-full bg-[var(--layout-header-bg)]',{'layout-header border-b border-color': !dark}]" >
    <div class="flex items-center h-full px-[10px] w-full box-border">
        <div class="left-panel h-full flex items-center w-[226px]">
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
                <div class="ml-[10px] text-[#fff] max-w-[150px] using-hidden">{{siteInfo.site_name}}</div>
            </el-header>
        </div>
        <div class="flex items-center gap-[8px] h-[var(--layout-header-height)] text-[var(--layout-header-text-color)]">
            <template v-for="(item, index) in oneMenuData" :key="index">
                <div v-if="item.meta.show" class="flex items-center cursor-pointer"
                    @click="handleTabClick(item.name)">
                    <span class="px-[14px] truncate text-[14px] text-[#d7dee8] h-[32px] leading-[32px] rounded-[4px] hover:bg-[var(--layout-header-text-hover)]"
                        :class="{ '!text-[#fff] font-bold': oneMenuActive === item.original_name }">
                        {{item.meta.short_title || item.meta.title }}</span>
                </div>
            </template>
        </div>
        <div class="ml-auto right-panel h-full flex items-center justify-end">
            <!-- 店铺套餐 -->
            <div class="mx-[10px] text-[14px] bg-[#E6F1FE] rounded-[8px] px-[6px] py-[8px] leading-[1] flex items-center cursor-pointer" v-if="appType == 'site'" @click="changeBusinessStatus()">
                <span class="nc-iconfont nc-icon-dianpu text-[#2878FF] text-[15px] mr-[5px]"></span>
                <span class="text-[#2878FF]" v-if="siteInfo.business_status">营业中</span>
                <span class="text-[#2878FF]" v-else>暂停营业</span>
            </div>
            <!-- 商家手机管理端 -->
            <el-popover placement="bottom" width="130px" trigger="hover" opper-class="!min-w-[130px]">
                <template #reference>
                    <i class="navbar-item nc-iconfont nc-icon-shoujiV6xxmm cursor-pointer text-[#fff]" :title="t('商家助手')"></i>
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
            <!-- 布局设置 -->
            <div class="navbar-item flex items-center h-full cursor-pointer">
                <layout-setting />
            </div>
            <!-- 预览 只有站点时展示-->
            <i class="navbar-item iconfont iconicon_huojian1 cursor-pointer text-[#fff]" :title="t('visitWap')" @click="toPreview"></i>
            <i class="navbar-item iconfont iconlingdang-xianxing cursor-pointer text-[#fff]" :title="t('newInfo')" v-if="appType == 'site'"></i>
            <!-- 用户信息 -->
            <div class="navbar-item flex items-center h-full cursor-pointer">
                <user-info />
            </div>
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
import useUserStore from '@/stores/modules/user'
import { useRoute, useRouter } from 'vue-router'
import { findFirstValidRoute } from '@/router/routers'
import { updateBusinessStatus } from '@/app/api/shop_site'
import { t } from '@/lang'
import { img, copy } from '@/utils/common'
import storage from '@/utils/storage'
import QRCode from 'qrcode'

const appType = storage.get('app_type')
const { toggle: toggleFullscreen } = useFullscreen()
const systemStore = useSystemStore()
const appStore = useAppStore()
const route = useRoute()
const router = useRouter()

const userStore = useUserStore()
const routers = userStore.routers

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

/* 一级菜单 */
const oneMenuData = ref<Record<string, any>[]>([])
routers.forEach(item => {
    item.original_name = item.name
    if (item.children && item.children.length) {
        item.name = findFirstValidRoute(item.children)
    }
    oneMenuData.value.push(item)
})
const oneMenuActive = ref(oneMenuData.value[0].name)

watch(route, () => {
    oneMenuActive.value = route.matched[1].name
}, { immediate: true })

const handleTabClick = (name:any) => {
    router.push({ name: name })
}
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

const wapPreview = ref('')
const wapImage = ref('')
const loadQrcode = () => {
    wapPreview.value = `${location.origin}/adminapp/`
    // errorCorrectionLevel：密度容错率L（低）H(高)
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}
loadQrcode()
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
<style>
:root,
body {
    --layout-header-bg: var(--el-color-primary);
    --layout-header-height: 44px;
    --layout-header-text-color: hsla(0, 0%, 100%, .8);
    --layout-header-text-hover: var(--el-color-primary-light-3);
    --layout-header-text-selected: rgba(0, 0, 0, .2);
}
</style>

<style lang="scss" scoped>
.layout-header{
  position: relative;
  z-index: 5;
  box-shadow: 0px 0px 4px 0px rgba(0,145,255,0.1);
}
.navbar-item {
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 32px;
    height: 32px;
    padding: 0 5px;
    border-radius: 4px;
    box-sizing: border-box;
    &:hover {
        background-color: var(--layout-header-text-hover);
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
