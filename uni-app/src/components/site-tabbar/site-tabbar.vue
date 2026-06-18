<template>
    <template v-if="tabbar && Object.keys(tabbar).length">
        <u-tabbar :value="value" zIndex="9999" :fixed="true" :placeholder="true" :safeAreaInsetBottom="true" :inactive-color="tabbar.value.textColor" :active-color="tabbar.value.textHoverColor" :border="props.border" class="custom-tabbar">
            <template v-for="item in tabbar.value.list">
                <u-tabbar-item class="py-[5rpx]" :custom-style="{'background-color': tabbar.value.backgroundColor}" :text="item.text" :icon="img(value == item.link.url ? item.iconSelectPath : item.iconPath)" :name="item.link.url" v-if="tabbar.value.type == 1" @click="itemBtn(item.link.url)"></u-tabbar-item>
                <u-tabbar-item class="py-[5rpx]" :custom-style="{'background-color': tabbar.value.backgroundColor}" :icon="img(value == item.link.url ? item.iconSelectPath : item.iconPath)" :name="item.link.url" v-if="tabbar.value.type == 2" @click="itemBtn(item.link.url)"></u-tabbar-item>
                <u-tabbar-item class="py-[5rpx]" :custom-style="{'background-color': tabbar.value.backgroundColor}" :text="item.text" :name="item.link.url" v-if="tabbar.value.type == 3" @click="itemBtn(item.link.url)"></u-tabbar-item>
            </template>
        </u-tabbar>
        <view class="tab-bar-placeholder"></view>
    </template>
</template>

<script setup lang="ts">
import { reactive, computed, watch, nextTick, getCurrentInstance, ref } from 'vue'
import { redirect, currRoute, currShareRoute, img } from '@/utils/common'
import useSystemStore from '@/stores/system'

const props = defineProps({
    siteId: {
        type: String || Number,
        default: ''
    },
    color: {
        type: Object,
        default: () => {
            return {
                backgroundColor: '', // 背景色
                textColor: '', // 文字颜色
                textHoverColor: '', // 文字选中颜色
            };
        }
    },
    border: {
        type: Boolean,
        default: true
    }
})


const tabbar: any = reactive({})
const systemStore = useSystemStore()

const instance = getCurrentInstance();
const setTabbarHeight = () => {
    nextTick(() => {
        const query = uni.createSelectorQuery().in(instance);
        query.select('.tab-bar-placeholder').boundingClientRect((data: any) => {
            let height = data ? data.height : 0;
            let tabbarInfo = {
                height: height
            };
            uni.setStorageSync('tabbarInfo', tabbarInfo);
        }).exec();
    }) 
}
const setTabbar = () => {
    if(props.siteId){
        if(systemStore.siteTabbarInfo[props.siteId]){
            Object.assign(tabbar, systemStore.siteTabbarInfo[props.siteId]);
            setTabbarHeight()
        } else {
            systemStore.getTabbarFn(props.siteId, (data: any)=>{
                if(data){
                    Object.assign(tabbar, data);
                }
                setTabbarHeight()
            })
        }
        
    }
    if (props.color) {
        for (let key in props.color) {
            if (props.color[key] && tabbar.value[key]) {
                tabbar.value[key] = props.color[key];
            }
        }
    }
}

setTabbar()

watch(
    () => props.color,
    (newValue, oldValue) => {
        if (newValue && oldValue && newValue != oldValue) {
            setTabbar()
        }
    }
    , { immediate: true, deep: true }
)


const value = computed(() => {
    let query: any = currShareRoute().params;
    let str = [];
    for (let key in query) {
        str.push(key + '=' + query[key]);
    }
    return '/' + currRoute() + (str.length > 0 ? '?' + str.join('&') : '')
})


const itemBtn = (url: any) => {
    if (url.indexOf('http') != -1 || url.indexOf('http') != -1) {

        // #ifdef H5
        window.location.href = url;
        // #endif

        // #ifdef MP
        redirect({
            url: '/app/pages/webview/index',
            param: { src: encodeURIComponent(url) }
        });
        // #endif
    } else {
        let query: any = currShareRoute().params;
        let str = [];
        for (let key in query) {
            str.push(key + '=' + query[key]);
        }
        if (url == ('/' + currRoute()) && !str.length) return
        redirect({ url, mode: 'reLaunch' })
    }
    
}


</script>

<style lang="scss" scoped>
.tab-bar-placeholder {
    padding-bottom: calc(constant(safe-area-inset-bottom) + 50px);
    padding-bottom: calc(env(safe-area-inset-bottom) + 50px);
}
</style>

<style lang="scss">
.custom-tabbar {
    .u-tabbar-item__icon .u-icon__img {
        width: 19px !important;
        height: 19px !important;
    }

    :deep(.u-tabbar-item__text) {
        font-size: 20rpx !important;
    }
}

/* #ifdef MP */
.u-tabbar {
    ::v-deep .u-tabbar-item__icon .u-icon .u-icon__img {
        width: 19px !important;
        height: 19px !important;
    }

    ::v-deep .u-tabbar-item__text {
        font-size: 20rpx !important;
    }
}

/* #endif */
</style>
