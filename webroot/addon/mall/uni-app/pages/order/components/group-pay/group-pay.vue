<template>
     <view>
        <u-popup :show="show" @close="show = false" mode="bottom" :round="10" :closeable="true">
            <view @touchmove.prevent.stop>
                <view class="text-center p-[30rpx]">以下订单需一起支付</view>
                <scroll-view scroll-y="true" class="h-[50vh] bg-[var(--page-bg-color)]">
                    <view class="p-[30rpx]">
                        <view class="mb-[30rpx] card-template" v-for="(item,index) in currentData.order_list" :key="index">
                            <view class="text-[#303133] text-[26rpx] font-400 leading-[36rpx] flex items-center">
                                <text class="iconfont iconVector-25 text-[28rpx]"></text>
                                <text class="text-[26rpx] font-400 ml-[12rpx] max-w-[480rpx] truncate">{{ item.site ? item.site.site_name : '' }}</text>
                            </view>
                            <view>
                                <view class="flex box-border mt-[20rpx]" v-for="(subitem, index) in item.order_goods" :key="index">
                                    <up-image width="100rpx" height="100rpx" :radius="'var(--goods-rounded-big)'" :src="img(subitem.goods_image_thumb_small ? subitem.goods_image_thumb_small : '')" mode="aspectFill">
                                        <template #error>
                                            <image class="w-[100rpx] h-[100rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
                                        </template>
                                    </up-image>
                                    <view class="ml-[20rpx] flex flex-1 flex-col box-border">
                                        <view class="flex justify-between items-baseline">
                                            <view class="max-w-[322rpx] text-[28rpx] leading-[40rpx] font-400 truncate text-[#303133]">{{ subitem.goods_name }}</view>
                                            <view class="text-right leading-[42rpx] ml-[10rpx] price-font">
                                                <text class="text-[22rpx]">￥</text>
                                                <text class="text-[32rpx] font-500">{{parseFloat(subitem.price).toFixed(2).split('.')[0] }}</text>
                                                <text class="text-[22rpx] font-500">.{{parseFloat(subitem.price).toFixed(2).split('.')[1] }}</text>
                                            </view>
                                        </view>
                                        <view class="flex  justify-between items-baseline text-[#303133] mt-[14rpx]">
                                            <view>
                                                <view class="text-[24rpx] text-[var(--text-color-light6)] font-400 truncate leading-[34rpx] max-w-[369rpx] mb-[10rpx]" v-if="subitem.sku_name">{{ subitem.sku_name }}</view>
                                            </view>
                                            <text class="text-right text-[26rpx] font-400 w-[90rpx] leading-[36rpx]">x{{ subitem.num }}</text>
                                        </view>
                                    </view>
                                </view>
                            </view>
                            <view class="flex justify-end items-center mt-[20rpx]">
                                <view class="text-[var(--price-text-color)] price-font">
                                    <text class="text-[26rpx]">￥</text>
                                    <text class="text-[36rpx] font-500">{{ parseFloat(item.order_money).toFixed(2).split('.')[0]  }}</text>
                                    <text class="text-[24rpx] font-500">.{{ parseFloat(item.order_money).toFixed(2).split('.')[1]  }}</text>
                                </view>
                            </view>
                        </view>
                    </view>
                </scroll-view>
                <view class="p-[30rpx] flex justify-center">
                    <u-button shape="circle" @click="show = false" :customStyle="{width:'300rpx !important',boxSizing:'border-box',margin:'0rpx'}">取消</u-button>
                    <u-button type="primary" shape="circle" :customStyle="{width:'300rpx !important',boxSizing:'border-box',margin:'0rpx',marginLeft:'20rpx'}" @click="confirm">付款</u-button>
                </view>
            </view>
        </u-popup>
        <pay ref="payRef" @close="payClose"></pay>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { img } from '@/utils/common';
const show = ref(false)
const currentData = ref({}) // 组合支付数据
const orderData = ref({})  //当前订单数据
const open = (data,row) => {
    currentData.value = uni.$u.deepClone(data)
    orderData.value = uni.$u.deepClone(row) 
    currentData.value.order_list =  currentData.value.order_list.map(el =>{
        let total = 0
        el.order_goods.forEach(item => {
            total +=  item.num
        })
        el.total = total
        return el
    })
    show.value = true
}
const payRef = ref(null)
const confirm = ()=>{
    payRef.value?.open('mall', currentData.value.group_id, `/addon/mall/pages/order/detail?order_id=${orderData.value.order_id}`);
}
 defineExpose({
    open
})
</script>

<style lang="scss" scoped>

</style>