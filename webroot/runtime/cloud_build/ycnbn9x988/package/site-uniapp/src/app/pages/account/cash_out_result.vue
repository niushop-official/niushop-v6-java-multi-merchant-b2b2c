<template>
    <view class="w-screen h-screen flex flex-col items-center" v-if="cashOutInfo">
            <view class="flex-1 flex flex-col items-center w-full pt-[140rpx]">
                <text class="text-[36rpx] ml-[16rpx] font-500">{{ cashOutInfo.status_name }}</text>
                <view class="text-[56rpx] font-500 mt-[40rpx] mb-[60rpx]">
                    <text class="text-[36rpx] mr-[6rpx]">￥</text>
                    <text>{{ moneyFormat(cashOutInfo.apply_money) }}</text>
                </view>
                <view class="w-full px-[var(--sidebar-m)] flex items-center justify-between box-border mb-[20rpx]">
                    <text>到账方式</text>
                    <text>{{ cashOutInfo.transfer_type_name }}</text>
                </view>
                <view class="w-full px-[var(--sidebar-m)] flex items-center justify-between box-border">
                    <text>申请时间</text>
                    <text>{{ cashOutInfo.create_time }}</text>
                </view>
            </view>
            <view class="pb-[260rpx]">
                <button class="w-[340rpx] !border-0 h-[80rpx] text-[28rpx] text-[#333] !bg-[#f2f2f2] flex-center font-500 rounded-[20rpx]" :plain="true" @click="complete">完成</button>
            </view>
        </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app';
import { moneyFormat, redirect } from '@/utils/common';
import { getShopCashOutInfo } from '@/app/api/account'

const id = ref('')
const cashOutInfo = ref<any>(null)
onLoad((option: any) => {
    id.value = option.id;
    getShopCashOutInfoFn(id.value)
})

const  getShopCashOutInfoFn  = (id: any) => {
    getShopCashOutInfo(id).then((res: any) => {
        cashOutInfo.value = res.data
    })
}
const complete = () => {
    redirect({ url: '/app/pages/index/index' })
}
</script>

<style scoped>

</style>