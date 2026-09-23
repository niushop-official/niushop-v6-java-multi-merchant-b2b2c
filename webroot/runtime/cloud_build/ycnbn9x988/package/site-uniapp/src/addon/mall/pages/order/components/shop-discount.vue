<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10">
        <view @touchmove.prevent.stop class="popup-common">
            <view class="title">店铺优惠</view>
            <view class="card-template">
                <view class="card-template-item justify-between px-[16rpx]">
                    <view class="text-[30rpx] w-[150rpx] font-500 text-[#303133]">优惠金额</view>
                    <text class="text-right price-font text-[34rpx]">-￥{{ parseFloat(shopDiscountInfo.discount_details.shop_discount.discount_total.discount_money).toFixed(2)}}</text>
                </view>
                <view class="card-template !bg-[#f2f3f5] !px-[16rpx]">
                    <view class="card-template-item justify-between" v-for="(item, index) in shopDiscountInfo.discount_details.shop_discount.discount_list" :key="index">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133]">{{ item.discount_type_name }}</view>
                        <text class="text-right price-font text-[28rpx]">-￥{{ parseFloat(item.discount_money).toFixed(2)}}</text>
                    </view>
                </view>
            </view>
        </view>
    </u-popup>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { deepClone } from '@/utils/common';

const show = ref(false)
const shopDiscountInfo = ref<any>({})

const open = (data: any) => {
    show.value = true;
    shopDiscountInfo.value = deepClone(data);
}


defineExpose({
    open
})
</script>

<style scoped>

</style>