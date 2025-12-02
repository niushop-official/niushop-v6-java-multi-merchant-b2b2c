<template>
	<view class="bg-[#fff] overflow-hidden category-content">
		<view class="mescroll-box h-full relative" v-if="tabsData.length">
			<view class="tabs-box z-2 absolute left-0 bg-[var(--temp-bg)] bottom-0 top-0" >
				<scroll-view :scroll-y="true" class="scroll-height">
					<view class="bg-[#fff]">
						<view class="tab-item" :class="{ 'tab-item-active': index == tabActive,'rounded-br-[12rpx]': tabActive - 1===index,'rounded-tr-[12rpx]':tabActive + 1 === index  }" v-for="(item, index) in tabsData" :key="index" @click="firstLevelClick(index, item)">
							<view class="text-box text-left leading-[1.3] break-words px-[16rpx]">
								{{ item.category_name }}
							</view>
						</view>
					</view>
				</scroll-view>
			</view>
			<scroll-view  :scroll-y="true">
				<view class="pl-[180rpx] scroll-ios">
					<view class="bg-[#fff] py-[30rpx] px-[20rpx] rounded-[var(--rounded-big)]" v-if="tabsData[tabActive]?.child_list && !loading">
                        <view class="text-[30rpx] font-bold mb-[20rpx]">{{ tabsData[tabActive].category_name }}</view>
                        <view class=" grid grid-cols-3 gap-x-[25rpx] gap-y-[30rpx]">
                            <template v-for="(item, index) in tabsData[tabActive]?.child_list" :key="item.category_id">
                                <view @click="toLink(item.category_id)" class=" flex items-center justify-center flex-col">
                                    <up-image radius="var(--goods-rounded-big)" width="160rpx" height="160rpx" :src="img(item.image ? item.image : '')" model="aspectFill">
                                        <template #error>
                                            <image class="rounded-[var(--goods-rounded-big)] overflow-hidden w-[160rpx] h-[160rpx] align-middle" :src="img('addon/mall/category_default.png')" mode="aspectFill"></image>
                                        </template>
                                    </up-image>
                                    <view class="text-[26rpx] text-center mt-[16rpx] leading-[36rpx]">{{ item.category_name }}</view>
                                </view>
                            </template>
                        </view>
					</view>
					<mescroll-empty class="part" v-if="!tabsData[tabActive]?.child_list && !loading" :option="{tip : '暂无商品分类'}"></mescroll-empty>
				</view>
			</scroll-view>
		</view>
		<tabbar />
		<mescroll-empty v-if="!tabsData.length && !loading" :option="{tip : '暂无商品分类'}"></mescroll-empty>
		<!-- <loading-page :loading="loading"></loading-page> -->
	</view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { img, redirect } from '@/utils/common';
import { getGoodsCategoryTree } from '@/app/api/basic';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useDiyStore from '@/app/stores/diy';

const diyStore = useDiyStore();
const loading = ref<boolean>(true);

onMounted(() => {
	getCategoryData()
})
/**
 * @description 获取分类数据
 * */
const tabsData: any = ref<Array<Object>>([])
const getCategoryData = () => {
    loading.value = true;
    getGoodsCategoryTree({site_id: diyStore.siteId}).then((res: any) => {
        tabsData.value = res.data;
        loading.value = false;
    }).catch(() => {
        loading.value = false;
    });
}

// 一级菜单样式控制
const tabActive = ref<number>(0)

// 一级菜单点击事件
const firstLevelClick = (index: number, data: Object) => {
	tabActive.value = index;
}
const toLink = (curr_goods_category: string) => {
	redirect({ url: '/addon/mall/pages/shop/goods_list', param: {site_id: diyStore.siteId, curr_goods_category } })
}

</script>

<style lang="scss" scoped>
.category-content{
	height: calc(100vh - 112px);
}

.class-select {
	position: relative;
	font-weight: bold;

	&::before {
		content: "";
		position: absolute;
		bottom: 0;
		height: 6rpx;
		background-color: $u-primary;
		width: 90%;
		left: 50%;
		transform: translateX(-50%);
	}
}

.list-select {
	position: relative;
	margin-right: 28rpx;

	&::before {
		content: "";
		position: absolute;
		background-color: #999;
		width: 2rpx;
		height: 70%;
		top: 50%;
		right: -14rpx;
		transform: translatey(-50%);
	}
}

.transform-rotate {
	transform: rotate(180deg);
}


.tabs-box {
	width: 180rpx;
	font-size: 28rpx;
}

.tabs-box .tab-item {
    min-height: 64rpx;
    padding: 18rpx 0;
    text-align: center;
    background-color: var(--temp-bg);
    display: flex;
    align-items: center;
    justify-content: center;
}

.tabs-box .tab-item-active {
    position: relative;
    color: var(--primary-color);
    background-color: #fff;

	&::before {
		display: inline-block;
		position: absolute;
		left: 0rpx;
		top: 50%;
		transform: translateY(-50%);
		content: '';
		width: 6rpx;
		height: 48rpx;
		background-color: var(--primary-color);
	}

	&::after {
		display: inline-block;
		position: absolute;
		left: 0rpx;
		top: 50%;
		transform: translateY(-50%);
		content: '';
		width: 6rpx;
		height: 48rpx;
		background-color: var(--primary-color);
	}
}


:deep(.mescroll-upwarp) {
	box-sizing: border-box;
	padding-left: 182rpx;
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}
/*  #ifdef  H5  */
.scroll-ios{
	padding-bottom: calc(50px + 20rpx + constant(safe-area-inset-bottom)) !important;
	padding-bottom: calc(50px  + 20rpx  + env(safe-area-inset-bottom)) !important;
}
/*  #endif  */
/*  #ifndef  H5  */
.scroll-ios{
	padding-bottom: calc(120rpx  + constant(safe-area-inset-bottom)) !important;
	padding-bottom: calc(120rpx  + env(safe-area-inset-bottom)) !important;
}
/*  #endif  */
.scroll-height{
	height: 100%;
}

/*  #ifdef  H5  */
.mescroll-empty.empty-page.part{
	width: 542rpx;
	height: 542rpx;
	margin-top: 0;
	margin-left: 0;
	padding-top: 50rpx;
	.img{
		width: 160rpx !important;
		height: 120rpx !important;
	}
}
/*  #endif  */

</style>
