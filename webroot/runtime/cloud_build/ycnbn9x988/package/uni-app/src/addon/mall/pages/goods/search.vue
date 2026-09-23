<template>
	<view class="bg-[var(--page-bg-color)] min-h-[100vh]" :style="themeColor()">
		<view class="flex items-center px-[20rpx] h-[120rpx]">
			<view class="h-[68rpx] bg-[#fff] px-[30rpx] flex items-center rounded-[100rpx] flex-1">
				<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 text-[var(--text-color-light9)] text-[26rpx] mr-[18rpx]"></text>
				<input class="text-[28rpx] flex-1" maxlength="50" type="text" v-model="inputValue"  placeholder="请搜索您想要的商品" confirm-type="search" placeholderClass="text-[var(--text-color-light9)] text-[28rpx]" @confirm="search">
				<text v-if="inputValue" class="nc-iconfont nc-icon-cuohaoV6xx text-[24rpx] text-[var(--text-color-light9)]" @click="inputValue=''"></text>
			</view>
			<text @click.stop="search()" class="text-[28rpx] ml-[32rpx] -mb-[2rpx]">搜索</text>
		</view>

		<view class="search-content">
			<!-- 历史搜索 -->
			<view class="history" v-if="historyList.length">
				<view class="history-box">
					<view class="history-top">
						<view class="title font-500">历史搜索</view>
						<view class="icon nc-iconfont nc-icon-shanchu-yuangaizhiV6xx !text-[24rpx] text-[var(--text-color-light9)]" @click="deleteHistoryList"></view>
					</view>
					<view class="history-bottom " id="history-list" :style="{ maxHeight: !isAllHistory ? '100%' : '168rpx' }">
						<view class="history-li" v-for="(item, index) in historyList" :key="index" @click="otherSearch(item)">
							<view>{{ item }}</view>
						</view>
						<view class="history-li history_more" v-if="isAllHistory" @click="isAllHistory = false">
							<view>
								<text class="text-[30rpx] nc-iconfont nc-icon-xiaV6xx"></text>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="sidebar-margin">
			<scroll-view :scroll-x="true" class="w-full whitespace-nowrap" v-if="rankConfig.search_style_type == 1">
				<view class="flex gap-x-[20rpx]">
					<view class="w-[458rpx] flex-shrink-0 rounded-[20rpx] px-[10rpx] pt-[20rpx] pb-[10rpx] box-border flex flex-col" :style="bgStyle(item)" v-for="(item,index) in rankList" :key="index">
						<view class="text-[30rpx] font-500 text-[#FF0000] leading-[42rpx] mb-[16rpx]" :style="{color: item.config.text_color }">{{ item.name }}</view>
						<view class="bg-[#fff] rounded-[16rpx] p-[20rpx] box-border flex-1">
							<template v-for="(subItem, subIndex) in item.goods_list" :key="subIndex">
								<view class="flex mb-[20rpx]" v-if="subIndex < 3" @click="toLink(subItem)">
									<view class="relative w-[100rpx] h-[100rpx] rounded-[8rpx] overflow-hidden flex-shrink-0 mr-[16rpx]">
										<image class="absolute top-0 left-0 w-[28rpx] h-[30rpx] z-10" :src="getRankBadge(subIndex + 1)" mode="aspectFill"/>
										<view class="absolute top-0 left-0 flex items-center justify-center w-[28rpx] h-[30rpx] z-20">
											<text class="text-[18rpx] font-500 text-[#fff]"> {{ subIndex + 1}}</text>
										</view>
										<up-image radius="8rpx" width="100rpx" height="100rpx" :src="img(subItem.goods_cover_thumb_mid)" model="aspectFill">
											<template #error>
												<image class="w-[100rpx] h-[100rpx] rounded-[8rpx] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
											</template>
										</up-image>
									</view>
									<view class="flex flex-col">
										<view class="leading-[36rpx] using-hidden text-[26rpx] font-500 whitespace-normal">{{ subItem.goods_name }}</view>
										<view class="text-[var(--price-text-color)] price-font flex items-baseline mt-[auto]">
											<text class="text-[20rpx] font-500">￥</text>
											<text class="text-[32rpx] font-500">{{ diyGoods.goodsPrice(subItem).toFixed(2) }}</text>
										</view>
									</view>
								</view>
								<view class="flex" :class="{'mb-[36rpx]': subIndex + 1 != item.goods_list.length }" v-else  @click="toLink(subItem)">
									<view class="relative w-[28rpx] h-[30rpx] flex items-center justify-center mr-[20rpx] flex-shrink-0">
										<image class="absolute top-0 left-0 w-[28rpx] h-[30rpx] z-10" :src="getRankBadge(subIndex + 1)" mode="aspectFill"/>
										<text class="relative text-[20rpx] font-500 text-[#fff] z-20"> {{ subIndex + 1}}</text>
									</view>
									<view class="flex-1 leading-[36rpx] using-hidden text-[26rpx] font-500 whitespace-normal">{{ subItem.goods_name  }}</view>
								</view>
							</template>
						</view>
					</view>
				</view>
			</scroll-view>
			<scroll-view :scroll-x="true" class="w-full whitespace-nowrap" v-else-if="rankConfig.search_style_type == 2">
				<view class="flex gap-x-[20rpx]">
					<view class="w-[458rpx] flex-shrink-0 rounded-[20rpx] px-[10rpx] pt-[20rpx] pb-[10rpx] box-border flex flex-col" :style="bgStyle(item)" v-for="(item,index) in rankList" :key="index">
						<view class="text-[30rpx] font-500 text-[#FF0000] leading-[42rpx] mb-[16rpx]" :style="{color: item.config.text_color }">{{ item.name }}</view>
						<view class="bg-[#fff] rounded-[16rpx] p-[20rpx] flex-1">
							<view class="flex" :class="{'mb-[20rpx]': subIndex + 1 != item.goods_list.length }" v-for="(subItem, subIndex) in item.goods_list" :key="subIndex"  @click="toLink(subItem)">
								<view class="relative w-[70rpx] h-[70rpx] rounded-[6rpx] overflow-hidden flex-shrink-0 mr-[10rpx]">
									<image class="absolute top-0 left-0 w-[24rpx] h-[30rpx] z-10" :src="getRankBadge(subIndex + 1)" mode="aspectFill"/>
									<view class="absolute top-0 left-0 flex items-center justify-center w-[24rpx] h-[30rpx] z-20">
										<text class="text-[18rpx] font-500 text-[#fff]"> {{ subIndex + 1}}</text>
									</view>
									<up-image radius="6rpx" width="70rpx" height="70rpx" :src="img(subItem.goods_cover_thumb_mid)" model="aspectFill">
										<template #error>
											<image class="w-[70rpx] h-[70rpx] rounded-[6rpx] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
										</template>
									</up-image>
								</view>
								<view class="flex flex-col">
									<view class="leading-[36rpx] using-hidden text-[26rpx] font-500 whitespace-normal">{{ subItem.goods_name }}</view>
									<view class="text-[var(--price-text-color)] price-font flex items-baseline mt-[auto]">
										<text class="text-[14rpx] font-500">￥</text>
										<text class="text-[26rpx] font-500">{{ diyGoods.goodsPrice(subItem).toFixed(2) }}</text>
									</view>
								</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
			<scroll-view :scroll-x="true" class="w-full whitespace-nowrap" v-else>
				<view class="flex gap-x-[20rpx]">
					<view class="w-[458rpx] flex-shrink-0 rounded-[20rpx] px-[10rpx] pt-[20rpx] pb-[10rpx] box-border flex flex-col" :style="bgStyle(item)" v-for="(item,index) in rankList" :key="index">
						<view class="text-[30rpx] font-500 text-[#FF0000] leading-[42rpx] mb-[16rpx]" :style="{color: item.config.text_color }">{{ item.name }}</view>
						<view class="bg-[#fff] rounded-[16rpx] p-[20rpx] box-border flex-1">
							<view class="flex" :class="{'mb-[36rpx]': subIndex + 1 != item.goods_list.length }" v-for="(subItem, subIndex) in item.goods_list" :key="subIndex"  @click="toLink(subItem)">
								<view class="relative w-[28rpx] h-[30rpx] flex justify-center mr-[20rpx] flex-shrink-0">
									<image class="absolute top-0 left-0 w-[28rpx] h-[30rpx] z-10" :src="getRankBadge(subIndex + 1)" mode="aspectFill"/>
									<text class="relative text-[20rpx] font-500 text-[#fff] leading-[22rpx] z-20"> {{ subIndex + 1}}</text>
								</view>
								<view class="flex-1 leading-[36rpx] using-hidden text-[26rpx] font-500 whitespace-normal">{{ subItem.goods_name  }}</view>
							</view>
						</view>
					</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>
<script setup lang="ts">
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref, nextTick } from 'vue';
import { redirect, img } from '@/utils/common';
import useConfigStore from "@/stores/config";
import { getRankSearch, getRankConfig } from '@/addon/mall/api/rank';
import { useGoods } from '@/addon/mall/hooks/useGoods'

const diyGoods = useGoods();

const inputValue = ref('') //搜索框的值
const historyList = ref([]) //历史搜索记录
const isAllHistory = ref(false)
const rankList = ref<any>([])
const rankConfig = ref<any>({})

onLoad((options:any) => {
    uni.getStorageSync('goodsSearchHistory') ? '' : uni.setStorageSync('goodsSearchHistory', []);
});

onShow(() => {
    findHistoryList()
    nextTick(()=> {
        getHistoryHeight();
    });
})

//获取历史搜索记录
const findHistoryList =()=> {
    historyList.value = uni.getStorageSync('goodsSearchHistory').reverse();
}

// 删除所有历史记录
const deleteHistoryList =()=> {
    uni.showModal({
        title: '提示',
        content: '确认删除全部历史记录？',
		confirmColor: useConfigStore().themeColor['--primary-color'],
        success: res => {
            if (res.confirm) {
                uni.setStorageSync('goodsSearchHistory', []);
                findHistoryList();
            }
        }
    });
}

//搜索
const search = ()=> {
    // 对历史搜索处理,判断有无,最近搜索显示在最前
	let historyList = uni.getStorageSync('goodsSearchHistory');
	let array = [];
	if (historyList.length) {
		array = historyList.filter(v => {
			return v != inputValue.value.trim();
		});
		array.push(inputValue.value.trim());
	} else {
		array.push(inputValue.value.trim());
	}
	uni.setStorageSync('goodsSearchHistory', array);

	redirect({ url: '/addon/mall/pages/goods/list', param: { goods_name: inputValue.value.trim() }, mode: 'navigateTo' })
}

// 点击历史搜索
const otherSearch = (e:any)=> {
    inputValue.value = e;
    search();
}

// 获取元素高度
const getHistoryHeight =()=> {
    const query = uni.createSelectorQuery().in(this);
    query.select('#history-list').boundingClientRect((data:any) => {
        if (data && data.height > uni.upx2px(70) * 2 + uni.upx2px(35) * 2) {
            isAllHistory.value = true;
        }
    }).exec();
}


function getRankBadge(sort: any) {
	if (rankConfig.value.search_style_type == 1) {
		switch (sort) {
			case 1:
				return img('addon/mall/rank/rank-style1-1.png');
			case 2:
				return img('addon/mall/rank/rank-style1-2.png');
			case 3:
				return img('addon/mall/rank/rank-style1-3.png');
			default:
				return img('addon/mall/rank/rank-style1-4.png');
		}
	} else if (rankConfig.value.search_style_type == 2) {
		switch (sort) {
			case 1:
				return img('addon/mall/rank/rank-style2-1.png');
			case 2:
				return img('addon/mall/rank/rank-style2-2.png');
			case 3:
				return img('addon/mall/rank/rank-style2-3.png');
			default:
				return img('addon/mall/rank/rank-style2-4.png');
		}
	} else {
		switch (sort) {
			case 1:
				return img('addon/mall/rank/rank-style3-1.png');
			case 2:
				return img('addon/mall/rank/rank-style3-2.png');
			case 3:
				return img('addon/mall/rank/rank-style3-3.png');
			default:
				return img('addon/mall/rank/rank-style3-4.png');
		}
	}
    
}

const bgStyle = (data: any) => {
    let style = '';
	if(data.config?.bg_type == 'bg_image') {
		style += `background-image:url(${ img(data.config.bg_image) });`;
		style += `background-size:100% 100%;`;
		style += `background-position:top;`;
		style += `background-repeat:no-repeat;`;
	} else {
		if (data.config?.bg_color_start) {
			if (data.config.bg_color_start && data.config.bg_color_end) {
				style += `background:linear-gradient(156deg, ${ data.config.bg_color_start } 50%, ${ data.config.bg_color_end } 80%);`;
			} else {
				style += 'background-color:' + data.config.bg_color_start + ';';
			}
		}
	}
    return style;
};

const getRankConfigFn = () => {
	getRankConfig().then((res:any) => {
		rankConfig.value = res.data;
		getRankSearchFn()
	}).catch(() => {
		getRankSearchFn()
	})
}
getRankConfigFn()
const getRankSearchFn = () => {
	getRankSearch().then((res:any) => {
		rankList.value = res.data;
	});
}

const toLink = (data: any) => {
	redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id }})
}

</script>
<style lang="scss" scoped>
	.content {
		width: 100vw;
		/* #ifdef MP */
		height: 100vh;
		/* #endif */
		/* #ifdef H5 */
		height: calc(100vh - env(safe-area-inset-bottom) - var(--status-bar-height));
		height: calc(100vh - constant(safe-area-inset-bottom) - var(--status-bar-height));
		/* #endif */
		/* #ifdef APP-PLUS */
		height: calc(100vh - 44px - env(safe-area-inset-bottom));
		height: calc(100vh - 44px - constant(safe-area-inset-bottom));
		/* #endif */
		background: #ffffff;
	}

	.search-content {
		box-sizing: border-box;
		background: var(--page-bg-color);
	}

	.history {
		width: 100%;
		box-sizing: border-box;

		.history-box {
			width: 100%;
			height: 100%;
			background: var(--page-bg-color);
			padding: 16rpx 20rpx 0rpx 20rpx;

			box-sizing: border-box;
			overflow: hidden;

			.history-top {
				width: 100%;
				display: flex;
				justify-content: space-between;
				align-items: center;

				.title {
					font-size: 28rpx;
				}

				.iconfont {
					color: var(--text-color-light9);
					font-size: 28rpx;
				}
			}

			.history-bottom {
				width: 100%;
				padding-top: 20rpx;
				position: relative;

				.history-li {
					display: inline-block;
					margin-right: 20rpx;
					margin-bottom: 15rpx;
					max-width: 100%;

					view {
						line-height: 56rpx;
						background: var(--temp-bg) !important;
						height: 56rpx;
						color: #333 !important;
						margin: 0 0rpx 4rpx 0 !important;
						padding: 0 24rpx;
						overflow: hidden;
						white-space: nowrap;
						text-overflow: ellipsis;
						border-radius: 100rpx;
						font-size: 24rpx;
					}

					&.history_more {
						margin-right: 0;
						position: absolute;
						bottom: 0;
						right: 0;
					}
				}
			}
		}
	}

</style>
