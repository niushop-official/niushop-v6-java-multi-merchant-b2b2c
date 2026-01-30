<template>
    <view class="min-h-[100vh] bg-[#fff] overflow-hidden" :style="themeColor()">
        <view class="card-template  my-[20rpx]">
            <view class="flex items-center justify-between mb-[40rpx]">
                <view class="text-[30rpx] font-500">数据概览</view>
                <view class="text-[26rpx] text-primary" @click="toStat">{{  Object.keys(statList).length ? '设置' : '去添加' }}</view>
            </view>
            <view class="grid grid-cols-3 gap-[30rpx]">
                <view class="border-[2rpx] border-dashed border-[#eee] rounded-[16rpx] p-[20rpx] box-border flex flex-col items-center" v-for="(item,index) in statList" :key="index">
                    <text class="price-font text-[42rpx] fnt-500 mb-[18rpx]">{{ item.num || 0 }}</text>
                    <view class="text-[26rpx] text-[#666]">{{ item.name }}</view>
                </view>
            </view>
        </view>
        <view class="mx-[24rpx] my-[20rpx] h-[2rpx] bg-[#f6f6f6]"></view>
        <view class="card-template  mb-[20rpx]">
            <view class="flex items-center justify-between mb-[40rpx]">
                <view class="text-[30rpx] font-500">代办事项</view>
                <view class="text-[26rpx] text-primary" @click="tolink">{{ Object.keys(statTodo).length ? '设置' : '去添加' }}</view>
            </view>
            <view class="grid grid-cols-4 gap-x-[20rpx]">
                <view class="border-[2rpx] border-dashed border-[#eee] rounded-[16rpx] p-[16rpx] box-border flex flex-col items-center" v-for="(item,index) in statTodo" :key="index">
                    <text class="price-font text-[42rpx] fnt-500 mb-[18rpx]">{{ item.num || 0 }}</text>
                    <view class="text-[24rpx] text-[#666]">{{ item.name }}</view>
                </view>
            </view>
        </view>
        <view class="mx-[24rpx] my-[20rpx] h-[2rpx] bg-[#f6f6f6]"></view>
        <view class="card-template  mb-[20rpx]">
            <view class="flex items-center justify-between mb-[40rpx]">
                <view class="text-[30rpx] font-500">常用功能</view>
                <view class="text-[26rpx] text-primary" @click="toApp">{{ appList.length ? '设置': '去添加' }}  </view>
            </view>
            <view class="grid grid-cols-5 gap-x-[10rpx] gap-y-[60rpx]">
                <view class="flex flex-col items-center" v-for="(item,index) in appList" :key="index">
                    <image class="w-[48rpx] h-[48rpx] overflow-hidden" :src="img(item.icon)" mode="aspectFill"></image>
                    <view class="text-[24rpx] mt-[22rpx]">{{ item.name }}</view>
                </view>
            </view>
        </view>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] fixed bottom-0 left-0 right-0 border-box bg-[#fff] flex items-center justify-between">
                <button hover-class="none" class="flex-1 primary-btn-bg text-[#fff] h-[70rpx] leading-[70rpx] rounded-[16rpx] text-[26rpx] font-500 border-box" @click="save">确定</button>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { redirect, img } from '@/utils/common';
import { onShow } from '@dcloudio/uni-app';
import { getAppOfIndex } from '@/app/api/index';
import { useCommon } from '@/app/pages/index/public/js/common';

const statList = ref<any>({}) 
const statTodo = ref<any>({}) 
const appList = ref<any>([])

const getCommon = useCommon()

onShow(() => {
    getstatFn()
    getTodoFn()
    getAppOfIndexFn()
})

const getstatFn = async () => {
   statList.value = await getCommon.getStatItemList()
}

const  getTodoFn = async () =>{
    statTodo.value = await getCommon.getTodoItemList()
}

const getAppOfIndexFn = () => {
    getAppOfIndex().then((res: any) => {
        appList.value = res.data
    })
}


// 去添加
const toStat = () => {
    redirect({ url: '/app/pages/setting/stat_edit' })
}
const  tolink = () => {
    redirect({ url: '/app/pages/setting/todo_edit' })
}
const toApp = () => {
    redirect({ url: '/app/pages/setting/app_edit' })
}

const save = () => {
    redirect({ url: '/app/pages/index/index', mode:'redirectTo' })
}
</script>

<style lang="scss" scoped>
.footer {
    height: calc(70rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(70rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>