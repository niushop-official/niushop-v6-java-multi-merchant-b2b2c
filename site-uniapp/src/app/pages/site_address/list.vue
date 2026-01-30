<template>
    <view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden order-list" :style="themeColor()">
        <view class="fixed left-0 top-0 right-0 z-10 px-[20rpx] py-[14rpx] bg-[#fff] ">
            <view class="h-[60rpx] bg-[#f6f6f6] rounded-[30rpx] flex items-center">
                <view class="search-input !px-[20rpx]">
                    <input class="input" maxlength="50" type="text" v-model="keyword" placeholder="请输入联系方式" placeholderClass="text-[var(--text-color-light9)] text-[24rpx]" confirm-type="search" @confirm="searchFn()">
                    <text v-if="keyword" class="nc-iconfont nc-icon-cuohaoV6xx1 clear mt-[4rpx] mr-[10rpx]" @click="keyword=''"></text>
                    <text class="nc-iconfont nc-icon-sousuo-duanV6xx1 btn !mr-0 w-[40rpx] text-center" @click="searchFn()"></text>
                </view>
            </view>
        </view>
        <mescroll-body ref="mescrollRef" top="88rpx" bottom="50px" @init="mescrollInit" :down="{ use: false }" @up="getShopAddressListFn">
            <view class="sidebar-margin pt-[var(--top-m)]" v-if="list.length">
                <view class="mb-[var(--top-m)] rounded-[var(--rounded-big)] overflow-hidden" v-for="(item, index) in list" :key="index">
                    <view class="flex flex-col card-template">
                        <view class="flex-1 line-feed flex items-start justify-between">
                            <view class="mr-[30rpx]">
                                <view class="flex items-center">
                                    <view class="text-[#333] text-[30rpx] leading-[34rpx] font-500">{{ item.contact_name }}</view>
                                    <text class="text-[#333] text-[30rpx] ml-[10rpx]">{{ item.mobile }}</text>
                                </view>
                                <view class="mt-[16rpx] text-[26rpx] line-feed leading-[1.4]">{{ item.full_address }}</view>
                            </view>
                            <text class="nc-iconfont nc-icon-xiugaiV6xx flex-shrink-0 text-[32rpx]" @click.stop="redirect({url: '/app/pages/site_address/edit',param: { id: item.id }})"></text>
                        </view>
                        <view class="flex justify-between pt-[26rpx]">
                            <view class="flex items-center">
                                <view class="flex items-center text-[26rpx] leading-none text-[var(--text-color-light6)] mr-[20rpx]">
                                    <text class="iconfont !text-[26rpx] mr-[10rpx] text-[var(--text-color-light6)] " :class="{ 'iconduigou !text-primary': item.is_default_delivery, 'iconcheckbox_nol': !item.is_default_delivery }"></text>
                                    默认发货地址
                                </view>
                                <view class="flex items-center text-[26rpx] leading-none text-[var(--text-color-light6)]">
                                    <text class="iconfont !text-[26rpx] mr-[10rpx] text-[var(--text-color-light6)]" :class="{ 'iconduigou !text-primary': item.is_default_refund, 'iconcheckbox_nol': !item.is_default_refund }"></text>
                                    默认收货地址
                                </view>
                            </view>
                            <view @click.stop="deleteEvent(item)" class="ml-[40rpx] text-[26rpx] text-[var(--text-color-light6)]">删除</view>
                        </view>
                    </view>
                </view>
            </view>
            <mescroll-empty v-if="!list.length && loading" :option="{tip : '暂无地址'}"></mescroll-empty>
        </mescroll-body>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] footer w-full fixed bottom-0 left-0 right-0 box-border bg-[#fff]">
                <button hover-class="none" class="primary-btn-bg text-[#fff] h-[80rpx] leading-[80rpx] rounded-[16rpx] text-[26rpx] font-500" @click="addEvent">添加新地址</button>
            </view>
        </view>
        <!-- 提示 -->
        <tips-popup ref="tipsRef" />
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { redirect } from '@/utils/common'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom, onShow } from '@dcloudio/uni-app';
import { getShopAddressList, deleteShopAddress } from '@/app/api/delivery_site'
const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const list = ref<any>([]);
const loading = ref<boolean>(false);
const keyword = ref<string>('');

const getShopAddressListFn = (mescroll: any) => {
	loading.value = false;
	let data = {
        page: mescroll.num,
		limit: mescroll.size,
        mobile: keyword.value
    }
	getShopAddressList(data).then((res: any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (Number(mescroll.num) === 1) {
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

const searchFn = () => {
	getMescroll().resetUpScroll();
}

const addEvent = () => {
	redirect({url: '/app/pages/site_address/edit'})
}

const tipsRef = ref()
const repeatLoading = ref(false)
const  deleteEvent = (item: any) => {
    tipsRef.value.open('您确定要删除吗？', () => {
        if (repeatLoading.value) return;
        repeatLoading.value = true;
        deleteShopAddress(item.id).then(() => {
            getMescroll().resetUpScroll();
            repeatLoading.value = false;
        }).catch(() => {
            repeatLoading.value = false;
        })
    })
}
</script>

<style lang="scss" scoped>
.footer {
    height: calc(80rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(80rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
.line-feed {
    word-wrap: break-word;
    word-break: break-all;
}

</style>