<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()">
        <view class="bg-[#fff] py-[var(--pad-top-m)] px-[var(--pad-sidebar-m)] border-box mb-[20rpx]">
            <view class="flex items-center mb-[40rpx]">
                <text class="text-[30rpx] font-500 mr-[20rpx]">已添加</text>
                <text class="text-[24rpx] text-[#999]">最多可添加9项</text>
            </view>
            <dragsort-list ref="dragSortRef" v-model:list.sync="appList" :cols="5" :itemHeight="70" :isAWait="isAWait">
                <template #default="{ item, index }">
                    <view class="h-full flex items-center">
                        <view class="w-full flex flex-col items-center">
                            <view class="relative leading-none">
                                <image class="w-[48rpx] h-[48rpx] overflow-hidden" :src="img(item.icon)" mode="aspectFill"></image>
                                <text class="nc-iconfont nc-icon-jianshaoV6mm text-[28rpx] text-[#ccc] absolute top-[-18rpx] right-[-18rpx] leading-none" @click="delApp(item,index)" @touchend.stop="nothing()"  @mousedown.stop="nothing()" @mouseup.stop="nothing()"></text>
                            </view>
                            <view class="text-[24rpx] mt-[22rpx]">{{ item.name }}</view>
                        </view>
                    </view>
                    
                </template>
            </dragsort-list>
        </view>
        <view class="bg-[#fff] mb-[20rpx]">
            <template v-for="(item,index) in menuList" :key="index">
                <view class="py-[var(--pad-top-m)] mx-[var(--pad-sidebar-m)] border-box mb-[20rpx]" :class="{'border-bottom': (index + 1) != menuList.length }"  v-if="item.childs && item.childs.length">
                    <view class="text-[30rpx] font-500 mb-[40rpx]">{{ item.name }}</view>
                    <view class="grid grid-cols-5 gap-x-[10rpx] gap-y-[40rpx]">
                        <view class="flex flex-col items-center" v-for="(subItem,subIndex) in item.childs" :key="subIndex" >
                            <view class="relative">
                                <image class="w-[48rpx] h-[48rpx] overflow-hidden" :src="img(subItem.icon)" mode="aspectFill"></image>
                                <text class="nc-iconfont nc-icon-duihaoV6mm text-[28rpx] text-[#eee] absolute top-[-18rpx] right-[-18rpx] leading-none" v-if="subItem.is_add"></text>
                                <text class="nc-iconfont nc-icon-tianjiaV6mm text-[28rpx] text-primary absolute top-[-18rpx] right-[-18rpx] leading-none" v-else @click="addApp(subItem)"></text>
                            </view>
                            <view class="text-[24rpx] mt-[22rpx]">{{ subItem.name }}</view>
                        </view>
                    </view>
                </view>
            </template>
        </view>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] fixed bottom-0 left-0 right-0 border-box bg-[#fff] flex items-center justify-between">
                <button hover-class="none" class="flex-1 mr-[20rpx] bg-[var(--primary-color-light)] text-primary h-[70rpx] leading-[70rpx] rounded-[16rpx] text-[26rpx] font-500 border-box" @click="cancel">取消</button>
                <button hover-class="none" class="flex-1 primary-btn-bg text-[#fff] h-[70rpx] leading-[70rpx] rounded-[16rpx] text-[26rpx] font-500 border-box" @click="save">保存</button>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import {  ref } from 'vue';
import { redirect, img } from '@/utils/common';
import { onShow } from '@dcloudio/uni-app';
import { getApp, getAppOfIndex, setAppOfIndex } from '@/app/api/index';
import dragsortList from '@/components/dragsort-list/dragsort-list.vue';

const appList = ref<any>([])
const menuList = ref<any>({})
const isAWait = ref(false)
const dragSortRef = ref<any>(null)
onShow(async () => {
    isAWait.value = true
    await getAppOfIndexFn()
    getAppFn()
})
const getAppOfIndexFn = async () => {
    appList.value = await (await getAppOfIndex()).data
}
const getAppFn = () => {
    getApp().then((res: any) => {
        menuList.value = res.data
        menuList.value.forEach((item: any) => {
            if (item.childs) {
                item.childs.forEach((subItem: any) => {
                    let index = appList.value.findIndex((appItem: any) => appItem.key == subItem.key)
                    if(index != -1){
                        subItem.is_add = true
                    }else{
                        subItem.is_add = false
                    }
                })
            }
        })
        
    })
}
const delApp = (data: any, index: number) => {
    appList.value.splice(index, 1)
    menuList.value.forEach((item: any) => {
        if(item.childs) {
            item.childs.forEach((subItem: any) => {
                if (subItem.key === data.key) {
                    subItem.is_add = false
                }
            })
        }
        
    })
    // 调用组件的refresh方法刷新数据
    if (dragSortRef.value) {
        dragSortRef.value.refresh();
    }
}
const addApp = (data: any) => {
    if(appList.value.length >= 9){
        uni.showToast({ title: '最多添加9项', icon: 'none' })
        return false
    }
    appList.value.push(data)
    data.is_add = true
    // 调用组件的refresh方法刷新数据
    if (dragSortRef.value) {
        dragSortRef.value.refresh();
    }
}
const nothing = () => {}

const cancel = () => {
    redirect({ url: '/app/pages/setting/index', mode: 'redirectTo' })
}

const save = () => {
    if(appList.value.length == 0){
        uni.showToast({ title: '请添加应用', icon: 'none' })
        return false
    }
    console.log(appList.value)
    let data = appList.value.map((item: any) => {
        return item.key
    })

    setAppOfIndex({ value: data }).then(() => {
        redirect({ url: '/app/pages/setting/index', mode: 'redirectTo' })
    })
}
</script>

<style lang="scss" scoped>
.border-bottom{
    border-bottom: 1rpx solid #ededed;
}
.footer {
    height: calc(70rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(70rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}


</style>