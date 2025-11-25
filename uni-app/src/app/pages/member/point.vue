<template>
    <view class="bg-[var(--page-bg-color)] min-h-[100vh]" :style="themeColor()">
        <template v-if="!loading">
            <view class="w-full bg-[var(--page-bg-color)]">
                <view class="pb-[210rpx] relative" :style="headerStyle">
                    <!-- #ifdef MP-WEIXIN -->
                    <top-tabbar :data="param" :scrollBool="topTabarObj.getScrollBool()" class="top-header" />
                    <!-- #endif -->
                    <view class="text-[70rpx] leading-[90rpx] text-[#fff] pl-[60rpx] font-500 pt-[77rpx] price-font">{{ pointInfo.point || 0 }}</view>
                    <view class="flex items-center pl-[60rpx]">
                        <image class="h-[36rpx] w-[36rpx] -mb-[4rpx]" :src="img('static/resource/images/member/point/icon.png')" mode="heightFix" />
                        <view class="text-[26rpx] leading-[36rpx] text-[#fff] ml-[10rpx]">我的积分</view>
                    </view>
                    <view class="side-tab" :style="{top: topStyle}" @click="toLink('/app/pages/member/point_detail')">
                        <text class="nc-iconfont nc-icon-jifenduihuanV6xx1 icon"></text>
                        <text class="desc">积分明细</text>
                    </view>
                </view>
                <view class="sidebar-margin flex flex-col mt-[-178rpx] relative">
                    <view class="w-[322rpx] h-[80rpx] text-[30rpx] text-[#333] font-500 box-border pl-[30rpx] pt-[var(--pad-top-m)] rounded-tl-[var(--rounded-big)] overflow-hidden mb-[-2rpx]"
                    :style="{backgroundImage: 'url(' + img('static/resource/images/member/point/top_bg.png') + ') ',backgroundSize: '100% 100%',backgroundRepeat: 'no-repeat'}">
                        <text class="leading-[42rpx]">积分详情</text>
                    </view>
                    <view class="flex items-center px-[30rpx] rounded-[var(--rounded-big)] !rounded-tl-none bg-[#fff] h-[173rpx] box-border">
                        <view class="w-[196rpx] flex-shrink-0 text-center">
                            <view class="text-[#333] text-[42rpx] leading-[54rpx] price-font">{{ pointInfo.point_get || 0 }}</view>
                            <view class="mt-[8rpx] text-[var(--text-color-light6)] text-[26rpx] leading-[36rpx] font-400">累计积分</view>
                        </view>
                        <view class="w-[1rpx] h-[50rpx] flex-shrink-0 bg-[var(--temp-bg)] mx-[10rpx]"></view>
                        <view class="w-[196rpx] flex-shrink-0 text-center">
                            <view class="text-[#333] text-[42rpx] leading-[54rpx] price-font">{{ pointInfo.use || 0 }}</view>
                            <view class="mt-[8rpx] text-[var(--text-color-light6)] text-[26rpx] leading-[36rpx] font-400">累计消费</view>
                        </view>
                        <view class="w-[1rpx] h-[50rpx] flex-shrink-0 bg-[var(--temp-bg)] mx-[10rpx]"></view>
                        <view class="w-[196rpx] min-w-[209.33rpx] flex-shrink-0 text-center">
                            <view class="text-[#333] text-[42rpx] leading-[54rpx] price-font">{{ pointInfo.point || 0 }}</view>
                            <view class="mt-[8rpx] text-[var(--text-color-light6)] text-[26rpx] leading-[36rpx] font-400">可用积分</view>
                        </view>
                    </view>
                </view>
            </view>
            <view class="mt-[var(--top-m)] sidebar-margin card-template">
                <view class="title">热门活动</view>
                <view class="mt-[60rpx] flex">
                    <view class="w-[200rpx] h-[253rpx] box-border pt-[69rpx] relative text-center mr-[16rpx]"
                    :style="{backgroundImage: 'url(' + img('static/resource/images/member/point/activity_1.png') + ') ', backgroundSize: '100% 100%', backgroundRepeat: 'no-repeat'}">
                        <image class="h-[78rpx] w-[78rpx] absolute left-[65rpx] top-[-21rpx]" :src="img('static/resource/images/member/point/activity_icon_1.png')" mode="heightFix" />
                        <view class="text-[28rpx] leading-[39rpx] text-[#333]">每日赚积分</view>
                        <view class="mt-[10rpx] text-[24rpx] leading-[34rpx] text-[var(--text-color-light6)]">每日签到</view>
                        <view class="w-full flex justify-center mt-[20rpx]">
                            <button class="h-[54rpx] !m-0 rounded-[100rpx] text-[24rpx] flex-center !text-[#fff]" shape="circle" :style="{background: 'linear-gradient( 94deg, #FB7939 0%, #FE120E 99%), #EF000C'}" @click="toLink('/app/pages/member/sign_in')">去签到</button>
                        </view>
                    </view>
                    <view class="w-[200rpx] h-[253rpx] box-border pt-[69rpx] relative text-center" :style="{backgroundImage: 'url(' + img('static/resource/images/member/point/activity_3.png') + ') ',backgroundSize: '100% 100%', backgroundRepeat: 'no-repeat'}">
                        <image class="h-[78rpx] w-[78rpx] absolute left-[65rpx] top-[-21rpx]" :src="img('static/resource/images/member/point/icon.png')" mode="heightFix" />
                        <view class="text-[28rpx] leading-[39rpx] text-[#333]">购物返积分</view>
                        <view class="mt-[10rpx] text-[24rpx] leading-[34rpx] text-[var(--text-color-light6)]">下单得积分</view>
                        <view class="w-full flex justify-center mt-[20rpx]">
                            <button class="h-[54rpx] !m-0 rounded-[100rpx] text-[24rpx] flex-center !text-[#fff]" shape="circle" 
                            :style="{background: 'linear-gradient( 94deg, #FB7939 0%, #FE120E 99%), #EF000C'}" @click="toLink('/addon/mall/pages/goods/list')">去逛逛</button>
                        </view>
                    </view>
                </view>
            </view>	
		</template>
		<loading-page :loading="loading"></loading-page>
	</view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { t } from '@/locale'
import { redirect, img, pxToRpx } from '@/utils/common';
import { getMemberAccountPointcount } from '@/app/api/member';
import { topTabar } from '@/utils/topTabbar'
import useSystemStore from "@/stores/system";

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let param = topTabarObj.setTopTabbarParam({ title: '我的积分' })
/********* 自定义头部 - end ***********/

const systemStore = useSystemStore()
const headerStyle = computed(() => {
    return {
        backgroundImage: 'url(' + img('static/resource/images/member/point/point_bg.png') + ') ',
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'bottom',
        // paddingTop:Object.keys(menuButtonInfo).length?(Number(menuButtonInfo.height) * 2 + menuButtonInfo.top * 2 + 77)+'rpx':'77rpx',
    }
})
const topStyle = computed(() => {
    let style = Object.keys(systemStore.menuButtonInfo).length ? (pxToRpx(Number(systemStore.menuButtonInfo.height)) + pxToRpx(systemStore.menuButtonInfo.top) + 50) + 'rpx;' : '50rpx'
    return style
})

//个人积分信息
const pointInfo = ref({});
//积分任务
const loading = ref(true)
onLoad(async() => {
    let pointInfoRes: any = await getMemberAccountPointcount()
    pointInfo.value = pointInfoRes.data
    loading.value = false
})
const toLink = (url = '', param = {}) => {
    redirect({ url, param })
}
</script>

<style lang="scss" scoped>
.bg-color {
    background-color: rgba(102, 102, 102, 0.4);
}
</style>