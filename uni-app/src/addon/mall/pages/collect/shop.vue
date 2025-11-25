<template>
	<view class="bg-[#F6F6F6] min-h-screen overflow-hidden order-list" :style="themeColor()">
		<mescroll-body ref="mescrollRef" top="0rpx" @init="mescrollInit" :down="{ use: false }" @up="getCollectFn">
			<view class="px-[var(--sidebar-m)] top-mar" v-if="list.length">
				<u-swipe-action ref="swipeActive">
					<template v-for="(item, index) in list" :key="index">
						<view class="mb-[20rpx]  rounded-[var(--rounded-big)] overflow-hidden">
							<u-swipe-action-item :options="cartOptions" @click="swipeClick(item.site.site_id,0)">
								<view class="card-template flex items-center box-border" @click="toShopDetail(item.site.site_id)">
									<view class="flex items-center">
										<up-image class="overflow-hidden" radius="50%" width="100rpx" height="100rpx" :src="img(item.site.icon ? item.site.icon : '')" model="aspectFill">
											<template #error>
												<image :src="img('addon/mall/shop/shop_default.png')" class="w-[100rpx] h-[100rpx] rounded-full"></image>
											</template>
										</up-image>
										<view class="ml-[20rpx]">{{item.site.site_name}}</view>
									</view>
								</view>
							</u-swipe-action-item>
						</view>
					</template>
				</u-swipe-action>
			</view>
			<mescroll-empty :option="{tip : '暂无数据'}" v-if="!list.length && loading"></mescroll-empty>
		</mescroll-body>
		
	</view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { t } from '@/locale'
import { img, redirect } from '@/utils/common';
import { getShopFollowList , editShopCollect} from '@/addon/mall/api/shop';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
let list = ref<Array<Object>>([]);
let loading = ref<boolean>(false);
const getCollectFn = (mescroll) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size
	};

	getShopFollowList(data).then((res) => {
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



//进入店铺
const toShopDetail = (id: any) => {
	redirect({ url: '/addon/mall/pages/index', param: { site_id: id } })
}
const cartOptions = ref([
    {
        text: '取消关注',
        style: {
            backgroundColor: '#EF000C'
        }
    }
])

// 取消店铺关注
const swipeClick = (id:any,is_follow:any) => {
	editShopCollect({
		site_id: id,
		is_follow: is_follow
	}).then(res => {
        list.value = [];
	    getMescroll().resetUpScroll();
    })
}
</script>
<style>
.order-list .mescroll-body {
	padding-bottom: constant(safe-area-inset-bottom) !important;
	padding-bottom: env(safe-area-inset-bottom) !important;
}
</style>
<style lang="scss" scoped>

:deep(.u-swipe-action-item__right__button__wrapper){
    padding:0 10rpx !important;
}
:deep(.u-swipe-action-item__right__button__wrapper__text){
    font-size:24rpx !important;
}
</style>
