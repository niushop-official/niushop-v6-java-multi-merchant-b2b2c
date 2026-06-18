<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10">
        <view class="popup-common">
			<view class="title">商品内容</view>
			<scroll-view :scroll-y="true" class="px-[30rpx] box-border h-[460rpx] overflow-auto">
				<view class="text-[28rpx] leading-[52rpx]">
					<text>{{ message }}</text>
				</view>
				
			</scroll-view>
			<view class="btn-wrap">
				<button class="primary-btn-bg btn" @click="copyContent">复制</button>
			</view>
		</view>
    </u-popup>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { copy } from '@/utils/common';

const show = ref(false)
const message = ref('')

const open = (data: any) => {
	message.value = data
	show.value = true
}

const copyContent = () => {
	copy(message.value)
	show.value = false
}

defineExpose({
	open
})
</script>


