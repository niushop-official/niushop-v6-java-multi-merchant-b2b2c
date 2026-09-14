<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()">
        <view v-if="latest.length" class="sidebar-margin card-template my-[var(--top-m)] flex items-center justify-between"  @click="redirect({ url: '/app/pages/account/account_record'})">
            <text class="text-[28rpx] font-500">历史账单</text>
            <view class="flex-1 flex overflow-hidden box-border mx-[20rpx] items-center">
                <swiper :vertical="true" :duration="500" :autoplay="true" circular="true" class="flex-1">
                    <swiper-item v-for="(item, index) in latest" :key="index" @touchmove.prevent.stop>
                        <text class="beyond-hiding truncate text-[26rpx]">{{ item.memo }}</text>
                    </swiper-item>
                </swiper>
            </view>
            <view class="text-[#999]">
                <teXt class="text-[26rpx]">全部</teXt>
                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx]"></text>
            </view>
        </view>
        <view class="sidebar-margin card-template my-[var(--top-m)] !py-[40rpx]">
            <view class="flex flex-col  items-center mb-[60rpx]">
                <text class="text-[28rpx] mb-[20rpx]">可提现（元）</text>
                <view class="flex items-center">
                    <text class="text-primary text-[60rpx] mr-[6rpx]">{{ parseFloat(accountInfo.money).toFixed(2) || '0.00' }}</text>
                </view>
            </view>
            <view class="grid grid-cols-2 gap-y-[60rpx]">
                <view class="flex flex-col  items-center">
                    <text class="text-[28rpx] text-[#666] mb-[20rpx]">已提现</text>
                    <text class="text-[36rpx] font-500">{{ parseFloat(accountInfo.withdrawal_money).toFixed(2) || '0.00' }}</text>
                </view>
                <view class="flex flex-col  items-center">
                    <text class="text-[28rpx] text-[#666] mb-[20rpx]">提现中(元)</text>
                    <text class="text-[36rpx] font-500">{{ parseFloat(accountInfo.money_cash_outing).toFixed(2) || '0.00' }}</text>
                </view>
                <view class="flex flex-col  items-center">
                    <text class="text-[28rpx] text-[#666] mb-[20rpx]">近30天累计收入(元)</text>
                    <text class="text-[36rpx] font-500">{{ parseFloat(lastmonth).toFixed(2) || '0.00' }}</text>
                </view>
                <view class="flex flex-col  items-center">
                    <text class="text-[28rpx] text-[#666] mb-[20rpx]">累计收入(元)</text>
                    <text class="text-[36rpx] font-500">{{ parseFloat(accountInfo.money_get).toFixed(2) || '0.00' }}</text>
                </view>
            </view>
            <view class="mt-[60rpx] flex justify-center items-center">
               <text class="flex-1 h-[80rpx] bg-primary rounded-full text-center leading-[80rpx] text-[30rpx] text-[#fff]" @click="transferClick">提现</text> 
            </view>
        </view>
        <view class="sidebar-margin card-template flex items-center justify-between">
            <view class="flex-1 flex items-center box-border" @click="redirect({ url: '/app/pages/account/account_record'})">
                <view class="w-[50rpx] h-[50rpx] mr-[16rpx]">
                    <image :src="img('static/resource/images/site_uniapp/account_icon.png')" class="w-[50rpx] h-[50rpx]" />
                </view>
                <view class="flex-1">
                    <view class="text-[28rpx] mb-[20rpx]">资金流水</view>
                    <view class="text-[24rpx] text-[#666]">查看资金流水</view>
                </view>
                <view class="nc-iconfont nc-icon-youV6xx text-[28rpx]"></view>
            </view>
            <view class="w-[2rpx] h-[50rpx] bg-[#ddd] mx-[40rpx]"></view>
            <view class="flex-1 flex items-center box-border" @click="redirect({ url: '/app/pages/account/cash_out_record'})">
                <view class="w-[50rpx] h-[50rpx] mr-[16rpx]">
                    <image :src="img('static/resource/images/site_uniapp/cash_out_icon.png')" class="w-[50rpx] h-[50rpx]" />
                </view>
                <view class="flex-1">
                    <view class="text-[28rpx] mb-[20rpx]">提现记录</view>
                    <view class="text-[24rpx] text-[#666]">查看提现记录</view>
                </view>
                <view class="nc-iconfont nc-icon-youV6xx text-[28rpx]"></view>
            </view>
        </view>
        <!-- 提示 -->
        <tips-popup ref="tipsRef" />
        <loading-page  :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { redirect, img } from '@/utils/common'
import { getShopAccountInfo, getAccountCheck, getFinanceLatest, getShopinComeOfRange } from '@/app/api/account'


const loading = ref(true)
const accountInfo = ref<any>({
    money: 0,
    withdrawal_money: 0,
    money_cash_outing: 0,
    money_get: 0
})
const lastmonth = ref(0)
const latest = ref([])
const getFinanceLatestFn = () => {
    getFinanceLatest().then((res: any) => {
        latest.value = res.data
    })
}
getFinanceLatestFn()
const getShopAccountInfoFn = () => {
    loading.value = true
    getShopAccountInfo().then((res: any) => {
        accountInfo.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
getShopAccountInfoFn()

const getShopinComeOfRangeFn = () => {
    getShopinComeOfRange('lastmonth').then((res: any) => {
        lastmonth.value = res.data
    })
}
getShopinComeOfRangeFn()

const tipsRef = ref()
const  transferClick = () => {
    redirect({ url: '/app/pages/account/cash_out' })
}
</script>

<style lang-scss scoped>
swiper {
    height: 40rpx;
}

.beyond-hiding {
    display: inline-block;
    width: 100%;
    white-space: nowrap;
    line-height: 40rpx;
}
</style>