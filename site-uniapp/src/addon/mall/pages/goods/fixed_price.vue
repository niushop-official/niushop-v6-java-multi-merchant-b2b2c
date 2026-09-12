<template>
    <view class="bg-[#fff] min-h-[100vh] " :style="themeColor()">
        <view class="p-[20rpx]">
            <view v-show="Object.keys(goodsSkuData).length">
                <view class="flex items-center justify-between mb-[20rpx]">
                    <text>指定会员价</text>
                    <view @click="selectData.isShow = !selectData.isShow">
                        <text class="nc-iconfont nc-icon-fuzhiV6xx1" v-if="!selectData.isShow"></text>
                        <text>{{ selectData.isShow ? '取消批量' : '批量' }}</text>
                    </view>
                </view>
                <view>
                    <view class="flex items-center mb-[20rpx]" v-for="(item, key, index) in goodsSkuData" :key="key" @click="handleSingle(item,key)"> 
                        <text v-if="selectData.isShow" class="self-center iconfont text-color text-[34rpx] mr-[32rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0" :class="{ 'iconxuanze1 text-primary': item.checked,'bg-[#F5F5F5]':!item.checked}" @click.stop="handleCheckedCitiesChange(item)"></text>
                       <view class="bg-[var(--page-bg-color)] p-[20rpx] box-border h-[100rpx] flex-1 rounded-[20rpx] flex items-center">
                            <view class="flex-1 flex">
                                <view class="flex-1 ml-[20rpx] text-[28rpx]">{{ item.spec_name_show }}</view>
                                <view class="flex flex-col justify-between items-center mr-[16rpx]">
                                    <text class="price-font text-[28rpx]" v-if="item.price">￥{{ moneyFormat(item.price) }}</text>
                                </view>
                            </view>
                            <text class="nc-iconfont nc-icon-xiaV6xx"></text>
                       </view> 
                    </view>
                </view>
            </view>
        </view>
        <view class="w-full footer">
            <view class="bg-[#fff] py-[var(--top-m)] px-[var(--sidebar-m)] flex justify-between items-center fixed bottom-0 left-0 right-0 box-border" v-if="selectData.isShow">
                <view class="mr-[20rpx] flex items-center">
                    <text class="iconfont text-color text-[34rpx] mr-[20rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0" :class="{ 'iconxuanze1 text-primary': selectData.skuCheckAll,'bg-[#F5F5F5]': !selectData.skuCheckAll }" @click="skuHandleCheckAllChange()"></text>
                    <text>全选</text>
                </view>
                <view class="flex items-center flex-1">
                    <button class="btn flex-1 !leading-[76rpx] mr-[20rpx] !text-[var(--primary-color)] border-[2rpx] border-solid border-[var(--primary-color)] rounded-[16rpx] bg-transparent box-border" hover-class="none" @click="handleBatch">指定会员价</button>
                    <button class="btn flex-1 border-[0] rounded-[16rpx] primary-btn-bg !text-[#fff]" shape="circle" hover-class="none" @click="selectData.isShow = false">完成设置</button>
                </view>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { debounce, deepClone, img, moneyFormat, redirect } from '@/utils/common'
import { onLoad } from '@dcloudio/uni-app'

const selectData: any = reactive({
    isShow: false, // 是否显示
    skuCheckAll :false,// 是否全选
    skuCheckedCities: [],// 选中的规格
})

const goodsSkuData: any = reactive({}) // 商品SKU规格数据

onLoad((option: any) => {
    // 多规格数据
    let skuData =  uni.getStorageSync('editGoodsSkuData') ? JSON.parse(uni.getStorageSync('editGoodsSkuData')) : {};
    for (let key in goodsSkuData){
        delete goodsSkuData[key];
    }
    Object.assign(goodsSkuData, skuData)
})


// 规格全选
const skuHandleCheckAllChange = () => {
    selectData.skuCheckAll = !selectData.skuCheckAll
    if(selectData.skuCheckAll){
        selectData.skuCheckedCities = Object.keys(goodsSkuData)
        for (const key in goodsSkuData) {
            goodsSkuData[key].checked = true
        }
    } else {
        for (const key in goodsSkuData) {
            goodsSkuData[key].checked = false
        }
        selectData.skuCheckedCities = []
    }
}
</script>

<style lang="scss" scoped>
.footer {
    height: calc(80rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(80rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
.btn{
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 260rpx;
    height: 66rpx;
    font-size: 26rpx;
    color: var(--primary-color);
    border: 2rpx solid var(--primary-color);
    border-radius: 16rpx;
}
</style>