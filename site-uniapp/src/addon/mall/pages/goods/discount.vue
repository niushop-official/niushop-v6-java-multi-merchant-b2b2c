<template>
    <view class="bg-[#fff] min-h-[100vh] " :style="themeColor()">
        <view class="p-[20rpx]">
            <view class="flex items-center justify-between mb-[20rpx]">
                <text>会员等级</text>
                <text>会员享受折扣</text>
            </view>
            <view v-for="(item,index) in memberLevel" :key="index" class="bg-[var(--page-bg-color)] p-[20rpx] box-border rounded-[20rpx] flex items-center justify-between mb-[20rpx]">
                <text>{{ item.level_name }}</text>
                <text>{{item.level_benefits.discount.discount}}</text>
            </view>
        </view>
        <view class="w-full footer">
            <view class="bg-[#fff] py-[var(--top-m)] px-[var(--sidebar-m)] footer w-full fixed bottom-0 left-0 right-0 box-border">
                <button hover-class="none"  class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx] rounded-[16rpx] text-[26rpx] font-500" @click="back">保存</button>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getMemberLevelAll } from '@/app/api/member'

// 会员等级
const memberLevel = ref<any>([])
const getMemberLevelAllFn = () => {
    getMemberLevelAll().then((res: any) => {
        memberLevel.value = res.data
        memberLevel.value.forEach((item: any, index: any, Array: any) => {
            if (!item.level_benefits || item.level_benefits == null) {
                Array[index].level_benefits = {
                    discount: {
                        discount: '原价'
                    }
                }
            } else if (item.level_benefits && item.level_benefits != null && !item.level_benefits.discount) {
                Array[index].level_benefits.discount = {
                    discount: '原价'
                }
            } else if (item.level_benefits && item.level_benefits != null && item.level_benefits.discount && !item.level_benefits.discount.discount) {
                Array[index].level_benefits.discount.discount = '原价'
            } else {
                Array[index].level_benefits.discount.discount += '折'
            }
        })
    })
}
getMemberLevelAllFn()


const back = () => {
    uni.navigateBack({
        delta: 1
    });
}
</script>

<style scoped>

</style>