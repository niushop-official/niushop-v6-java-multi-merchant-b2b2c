<template>
    <view @touchmove.prevent.stop class="share-popup">
        <u-popup :show="adsPopShow" mode="center" @close="adsPopupClose" overlayOpacity="0.3" bgColor="transparent">
            <view @touchmove.prevent.stop class="flex flex-col items-center" v-if="data && data.imgUrl">
                <image :src="img(data.imgUrl)" :style="popWindowStyle()"  @click="diyStore.toRedirect(data.link)"/>
                <text @click="adsPopupClose" class="mt-[50rpx] nc-iconfont nc-icon-cuohaoV6xx1 !text-[50rpx] text-[#fff]"></text>  
            </view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue';
import { redirect, img } from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
const diyStore = useDiyStore();

const props = defineProps({
    data: {
        type: Object,
        default: {}
    }
})

const data = computed(() => {
    return props.data.popWindow;
})

const adsPopShow = ref(false);
const pageTriggered = ref(false); // 标记当前页面是否已触发过弹窗（避免同一页面重复触发）

const getCurrentPagePath = () => {
    const pages = getCurrentPages();
    if (pages.length === 0) return '';
    const currentPage = pages[pages.length - 1];
    // 返回当前页面的路径（如/pages/index/index）
    return currentPage.route || '';
}

const getStorageKey = () => {
    if (!data.value.id) return '';
    const pagePath = getCurrentPagePath();
    return `${data.value.id}_${pagePath}_pop_window_count`;
};


onMounted(() => {
    nextTick(() => {
        if (pageTriggered.value) return;

        const storageKey = getStorageKey();
        if (!storageKey) return;

        const cacheValue = uni.getStorageSync(storageKey);
        // 如果缓存值和当前配置不一致，清除缓存（解决配置切换后状态残留）
        if (cacheValue && cacheValue !== data.value.count) {
            uni.removeStorageSync(storageKey);
        }

        if (data.value.count === 'always') {
            uni.removeStorageSync(storageKey);
        }

        const isOnLoadTrigger = uni.getStorageSync('isOnLoad');
        const isPageEnterTrigger = true; // 进入每个页面都触发（核心修改）
        if (isOnLoadTrigger || isPageEnterTrigger) {
            adsPopupShow();
            pageTriggered.value = true; // 标记当前页面已触发
            if (isOnLoadTrigger) uni.removeStorageSync('isOnLoad');
        }
    })
})

const popWindowStyle =() => {
    // 做大展示宽高
    const MAX_WIDTH = 290;
    const MAX_HEIGHT = 410;
    // 参照宽高
    const REFER_WIDTH = 290;
    const REFER_HEIGHT = 290;

    const scale = data.value.imgHeight / data.value.imgWidth;
    let width, height;
    if (scale <= REFER_HEIGHT / REFER_WIDTH) {
        width = MAX_WIDTH;
        height = width * scale;
    } else {
        height = MAX_HEIGHT;
        width = height / scale;
    }

    return `height:${height * 2}rpx;width:${width * 2}rpx;`;
}

const adsPopupShow = () => {
    // 装修模式下刷新
    if (diyStore.mode === 'decorate' || !data.value.show || !getStorageKey()) return false
    const storageKey = getStorageKey();
    const popWindowCount = uni.getStorageSync(storageKey);
    // always模式：清除存储并显示；once模式：仅当前页面未显示过才显示
    if (data.value.count === 'always') {
        adsPopShow.value = true;
    } else if (data.value.count === 'once' && !popWindowCount) {
        adsPopShow.value = true;
    }
}

const adsPopupClose = () => {
    adsPopShow.value = false;
    const storageKey = getStorageKey();
    if (storageKey) {
        uni.setStorageSync(storageKey, data.value.count);
    }
}

// onBeforeUnmount(()=>{
//     uni.removeStorageSync(data.value.id + '_pop_window_count');
// })
</script>

<style scoped lang="scss">

</style>
