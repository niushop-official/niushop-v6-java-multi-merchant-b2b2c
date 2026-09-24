<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10" :closeable="true">
		<view @touchmove.prevent.stop class="popup-common">
            <view class="title">赠品</view>
			<scroll-view scroll-y="true" class="h-[35vh]">
                <view class="px-[20rpx]">
                    <view v-if="giftData && giftData.length" class=" mb-[10rpx]">
                        <view v-for="(cItem,index) in giftData" :key="index" @click="toLink(cItem)" class="flex p-[var(--pad-sidebar-m)] bg-[#f9f9f9] mb-[20rpx] rounded-[24rpx]">
                            <up-image radius="var(--goods-rounded-big)" width="120rpx" height="120rpx" :src="img(cItem.sku_image)" model="aspectFill">
                                <template #error>
                                    <image class="w-[120rpx] h-[120rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"/>
                                </template>
                            </up-image>
                            <view class="ml-[16rpx] py-[8rpx] flex flex-1 flex-col justify-between">
                                <view class="flex items-center">
                                    <view class="text-[26rpx] max-w-[400rpx] truncate leading-[40rpx] text-[#333]">{{ cItem.goods.goods_name }}</view>
                                    <view class="ml-[auto] font-400 text-[26rpx] text-[#303133]">
                                        <text>x</text>
                                        <text>{{ cItem.num }}</text>
                                    </view>
                                </view>
                                <view class="flex items-center">
                                    <view v-if="cItem.sku_name" class="text-[22rpx] text-[var(--text-color-light9)] truncate max-w-[400rpx] leading-[28rpx]">{{ cItem.sku_name }}</view>
                                    
                                </view>
                            </view>
                        </view>
                    </view>
                </view>
				
			</scroll-view>
			<view class="btn-wrap">
                <button class="primary-btn-bg btn" @click="confirm">确认</button>
			</view>
		</view>
    </u-popup>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { orderCoupon } from '@/addon/mall/api/order'
import { img,redirect } from '@/utils/common';

const show = ref(false)
const giftData = ref<any>(null)
const open = (data:any)=> {
    show.value = true
    giftData.value = data
}

const confirm = ()=> {
    show.value = false
}
const toLink = (data: any) => {
  redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id } })
}
defineExpose({
	open,
})
</script>

<style lang="scss" scoped></style>