<template>
	<view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden order-list" :style="themeColor()">
		<view class="fixed left-0 top-0 right-0 z-10">
			<scroll-view :scroll-x="true" class="tab-style-2">
                <view class="tab-content">
                    <view class="tab-items flex-1 text-center" :class="{ 'class-select': current == item.key.toString() }" @click="switchTab(item.key)" v-for="(item, index) in tabs">{{ item.name }}</view>
                </view>
            </scroll-view>
        </view>
		<mescroll-body ref="mescrollRef" top="90rpx" @init="mescrollInit" @down="downCallback" @up="getMallInvoiceFn">
			<view class="sidebar-margin" v-if="list.length">
				<template v-for="(item, index) in list" :key="index">
					<view class="mb-[var(--top-m)] card-template">
                        <block v-for="(subitem,subIndex) in item.orderData.orderGoods" :key="subIndex">
							<view class="flex" :class="{'mt-[var(--top-m)]' : subIndex}">
								<image :src="img(subitem.sku_image)" class="w-[80rpx] h-[80rpx] flex-shrink-0" mode="aspectFit"></image>
								<view class="flex-1 ml-[20rpx]">
									<view class=" text-[26rpx] text-[#333] leading-[34rpx] truncate max-w-[530rpx]">{{ subitem.goods_name }}</view>
									<view class="text-[22rpx] text-[#999] leading-[30rpx] truncate mt-[6rpx] max-w-[530rpx]">{{ subitem.sku_name }}</view>
								</view>
							</view>
						</block>
                        <view class="mt-[36rpx] bg-[#f5f6f7] rounded-[20rpx] px-[30rpx] py-[24rpx] flex items-center justify-between">
                            <view class="text-[26rpx] leading-[34rpx]">
                                <view class="text-[#282828] font-700">{{ item.type_name }}</view>
                                <view class="text-[#818181] mt-[8rpx] ">{{ t('applyTime') }} {{ item.create_time }}</view>
                            </view>
                            <view class="leading-[42rpx]">
                                <text class="text-[24rpx] font-400">￥</text>
                                <text class="text-[32rpx] font-700">{{ item.money }}</text>
                            </view>
                        </view>
                        <view class="mt-[38rpx] flex items-center justify-between">
                            <view class="text-[28rpx] font-700 text-[#282828]">{{ item.is_invoice_name }}</view>
                            <view class="order-grey-hollow-btn" @click="toLink(item.id)">{{ t('detail') }}</view>
                        </view>
					</view>
				</template>
			</view>
			<mescroll-empty :option="{tip : '暂无发票记录'}" v-if="!list.length && loading"></mescroll-empty>
		</mescroll-body>
		<fab-float />
	</view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { t } from '@/locale'
import { img, redirect } from '@/utils/common'
import { getMallInvoice } from '@/app/api/invoice';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import FabFloat from '@/components/fab-float/fab-float.vue'
const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
let current = ref('all')
const tabs = ref([
	{ name: '全部', key: 'all' },
    { name: '已开票', key: 1 },
    { name: '未开票', key: 0 }
])
let list = ref<Array<Object>>([]);
let loading = ref<boolean>(false);


const getMallInvoiceFn = (mescroll) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size,
		is_invoice: current.value
	};

	getMallInvoice(data).then((res) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (mescroll.num == 1) {
			list.value = []; //如果是第一页需手动制空列表
		}
		list.value = list.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}
const switchTab = (event:any)=> {
    current.value = event
	list.value = [];
	getMescroll().resetUpScroll();
}
// 去详情
const toLink = (id:number) => {
	redirect({ url: '/app/pages/invoice/detail', param: { id: id } })
}
</script>
<style>
.order-list .mescroll-body {
	padding-bottom: constant(safe-area-inset-bottom) !important;
	padding-bottom: env(safe-area-inset-bottom) !important;
}
</style>
<style lang="scss" scoped>
</style>
