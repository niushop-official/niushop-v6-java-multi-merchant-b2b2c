<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()">
        <view class="bg-[#fff] py-[var(--pad-top-m)] px-[var(--pad-sidebar-m)] border-box mb-[20rpx]">
            <view class="flex items-center mb-[40rpx]">
                <text class="text-[30rpx] font-500 mr-[20rpx]">已添加</text>
                <text class="text-[24rpx] text-[#999]">最多可添加6项</text>
            </view>
            <dragsort-list ref="dragSortRef" v-model:list.sync="statList" :cols="3" :padding="10"  :itemHeight="80" :isAWait="isAWait">
                <template #default="{ item, index }">
                    <view class="h-full flex items-center">
                        <view class="w-full relative  border-[2rpx] border-dashed border-[#eee] rounded-[16rpx] p-[20rpx] box-border flex flex-col items-center">
                            <text class="price-font text-[42rpx] fnt-500 mb-[18rpx]">{{ item.num || 0 }}</text>
                            <view class="text-[26rpx] text-[#666]">{{ item.name }}</view>
                            <text class="nc-iconfont nc-icon-jianshaoV6mm text-[28rpx] text-[#b9b8c1] absolute top-[-18rpx] right-[-16rpx]" @click="delStat(item,index)" @touchend.stop="nothing()"  @mousedown.stop="nothing()" @mouseup.stop="nothing()"></text>
                        </view>
                    </view>
                </template>
            </dragsort-list>
        </view>
        <view class="bg-[#fff] py-[var(--pad-top-m)] px-[var(--pad-sidebar-m)] border-box mb-[20rpx]">
            <view class="grid grid-cols-3 gap-[30rpx]">
                <view class="w-full relative  border-[2rpx] border-dashed border-[#eee] rounded-[16rpx] p-[20rpx] box-border flex flex-col items-center" :class="{'bg-[#f6f7fb]': item.is_add }" v-for="(item, index) in statAll" :key="index">
                    <text class="price-font text-[42rpx] fnt-500 mb-[18rpx]">{{ item.num || 0 }}</text>
                    <view class="text-[26rpx] text-[#666]">{{ item.name }}</view>
                    <text class="nc-iconfont nc-icon-duihaoV6mm text-[28rpx] text-[#ebeaf1] absolute top-[-14rpx] right-[-14rpx] leading-none" v-if="item.is_add"></text>
                    <text class="nc-iconfont nc-icon-tianjiaV6mm text-[28rpx] text-primary absolute top-[-14rpx] right-[-14rpx] leading-none" v-else @click="addStat(item)"></text>
                </view>
            </view>
        </view>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] fixed bottom-0 left-0 right-0 border-box bg-[#fff] flex items-center justify-between">
                <button hover-class="none" class="flex-1 mr-[20rpx] bg-[var(--primary-color-light)] text-primary h-[70rpx] leading-[70rpx] rounded-[16rpx] text-[26rpx] font-500 border-box" @click="cancel">取消</button>
                <button hover-class="none" class="flex-1 primary-btn-bg text-[#fff] h-[70rpx] leading-[70rpx] rounded-[16rpx] text-[26rpx] font-500 border-box" @click="save">保存</button>
            </view>
        </view>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import {  ref } from 'vue';
import { redirect } from '@/utils/common';
import { onShow } from '@dcloudio/uni-app';
import dragsortList from '@/components/dragsort-list/dragsort-list.vue';
import { getAllstat, setstat } from '@/app/api/index';
import { useCommon } from '@/app/pages/index/public/js/common';

const getCommon = useCommon()

const statList = ref<any>([]) 
const statAll = ref<any>([])
const isAWait = ref(false)
const dragSortRef = ref<any>(null)
const loading = ref(true)

onShow(() => {
    isAWait.value = true
    getstatFn()
    getAllstatFn()
})
const getstatFn = async () => {
    let data = await getCommon.getStatItemList()
    statList.value = Object.values(data) || []
}
const  getAllstatFn =  () => {
    loading.value = true
    getAllstat().then((res: any) => {
        statAll.value = res.data
        statAll.value.forEach((item: any) => {
            let index = statList.value.findIndex((val: any) => val.key == item.key)
            if(index != -1){
                item.is_add = true
            }else{
                item.is_add = false
            }
        })
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
const delStat = (data: any, index: any) => {
    statList.value.splice(index, 1)
    statAll.value.forEach((item: any) => {
        if (item.key === data.key) {
            item.is_add = false
        }
    })
    // 调用组件的refresh方法刷新数据
    if (dragSortRef.value) {
        dragSortRef.value.refresh();
    }
}

const addStat = (data: any) => {
    if(statList.value.length >= 6){
        uni.showToast({ title: '最多添加6项', icon: 'none' })
        return false
    }
    statList.value.push(data)
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
    if(statList.value.length == 0){
        uni.showToast({ title: '请添加统计项', icon: 'none' })
        return false
    }
    let data = statList.value.map((item: any) => {
        return item.key
    })
    setstat({ value: data }).then(() => {
        let cacheList: any = {};
        statList.value.forEach((item: any) => {
            cacheList[item.key] = item;
        })
        uni.setStorageSync('statItemList', cacheList)
        redirect({ url: '/app/pages/setting/index', mode: 'redirectTo' })
    })
}
</script>

<style lang="scss" scoped>
.footer {
    height: calc(70rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(70rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>