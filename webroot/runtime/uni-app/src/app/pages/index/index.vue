<template>
    <view :style="themeColor()">

        <loading-page :loading="diy.getLoading()"></loading-page>

        <view v-show="!diy.getLoading()">

            <!-- 自定义模板渲染 -->
            <view class="diy-template-wrap bg-index" :style="diy.pageStyle()">

                <diy-group ref="diyGroupRef" :data="diy.data" />

            </view>

        </view>

        <!-- 小程序隐私协议 -->
        <wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
      

    </view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { useDiy } from '@/hooks/useDiy'
import { redirect } from '@/utils/common';
import { useShare } from '@/hooks/useShare'
import diyGroup from '@/addon/components/diy/group/index.vue'
import { onShow } from '@dcloudio/uni-app';
import useDiyStore from '@/app/stores/diy';

const { setShare } = useShare()

uni.hideTabBar() // 隐藏tabbar

const diy = useDiy({
    name: 'DIY_INDEX'
})

const diyGroupRef = ref(null)

const wxPrivacyPopupRef: any = ref(null)
const collectTipRef: any = ref(null)
// 监听页面加载
diy.onLoad();

onShow(()=>{
    const diyStore = useDiyStore();
    diyStore.siteId = 0
})

// 监听页面显示
diy.onShow((data: any) => {
    if (data.value) {
        // uni.setNavigationBarTitle({
        // 	title: diyData.title
        // })
    } else if (data.page) {
        // 跳转到设置的启动页
        redirect({ url: data.page, mode: 'reLaunch' })
    }
    let share = data.share ? JSON.parse(data.share) : null;
    setShare(share);
    diyGroupRef.value?.refresh();

    // #ifdef MP
    nextTick(() => {
        if (wxPrivacyPopupRef.value) wxPrivacyPopupRef.value.proactive();
        if (collectTipRef.value) collectTipRef.value.show();
    })
    // #endif
});

// 监听页面隐藏
diy.onHide();

// 监听页面卸载
diy.onUnload();

// 监听滚动事件
diy.onPageScroll()
</script>
<style lang="scss" scoped>
@import '@/styles/diy.scss';
</style>
<style lang="scss">
.diy-template-wrap {
    /* #ifdef MP */
    .child-diy-template-wrap {
        ::v-deep .diy-group {
            > .draggable-element.top-fixed-diy {
                display: block !important;
            }
        }
    }

    /* #endif */
}
</style>
