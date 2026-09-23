<template>
	<view class="bg-gray-100 min-h-[100vh]" :style="themeColor()">
		<view class="fixed top-0 inset-x-0 z-10">
			<view class='p-[20rpx] bg-white border-solid border-0 border-b-[2rpx] border-gray-200'>
				<u-search :placeholder="t('searchPlaceholder')" actionText :actionStyle="{'width':0,'margin':0}" :inputStyle="{'font-size': '28rpx'}" height="68rpx" v-model="articleTitle" @clickIcon="searchFn"></u-search>
			</view>
			<scroll-view :scroll-x="true" :enable-flex="true"
				class="nav-list bg-white align-center px-[20rpx] box-border">
				<view class="flex scroll-view-wrap">
					<view
						:class="['nav-item text-[28rpx] mx-[10rpx] h-[60rpx] leading-[60rpx] my-[10rpx] border-t-0 border-l-0 border-r-0',{'border-solid border-b-[4rpx] active': currCategoryId==item.category_id}]"
						@click="loadCategory(item.category_id)" v-for="(item,index) in categoryList"
						:key="item.category_id">
						{{item.name}}
					</view>
				</view>
			</scroll-view>
		</view>

		<mescroll-body ref="mescrollRef" @init="mescrollInit" top="210rpx" @down="downCallback" @up="getArticleListFn">
			<view v-for="(item,index) in articleList" :key="item.id"
				:class="['bg-white flex align-center p-[20rpx] rounded-[8rpx] mx-[20rpx]',{'mb-[20rpx]': articleList.length-1 !== index}] "
				@click="toLink(item.id)">
				<up-image width="174rpx" height="174rpx" :src="img(item.image)" model="aspectFill">
					<template #error>
						<u-icon name="photo" color="#999" size="50"></u-icon>
					</template>
				</up-image>
				<view class="flex-1 flex flex-col ml-[20rpx]">
					<view class="text-[32rpx] leading-[1.3] multi-hidden mt-[4rpx]">{{item.title}}</view>
					<view class="text-[28rpx] max-w-[480rpx] truncate mt-[10rpx] text-gray-500">{{item.summary}}</view>
					<view class="text-[24rpx] text-gray-400 mt-auto flex justify-between">
						{{item.create_time}}
					</view>
				</view>
			</view>
			<mescroll-empty v-if="!articleList.length && loading"></mescroll-empty>
		</mescroll-body>
		<tabbar />
	</view>
</template>

<script setup lang="ts">
	import { reactive, ref, onMounted } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import { t } from '@/locale'
	import { redirect, img } from '@/utils/common';
	import { getArticleList, getArticleCategory } from '@/app/api/article'
	import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue'
	import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue'
	import useMescroll from '@/components/mescroll/hooks/useMescroll.js'
	import { onPageScroll, onReachBottom } from '@dcloudio/uni-app'
	import { useShare } from '@/hooks/useShare'

    const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
	const { setShare, onShareAppMessage, onShareTimeline } = useShare()
	setShare()
	onShareAppMessage()
	onShareTimeline()

	let categoryList = ref<Array<Object>>([]);
	let articleList = ref<Array<any>>([]);
	let currCategoryId = ref<number | string>('');
	let articleTitle = ref<string>('');
	let mescrollRef = ref(null);
	let loading = ref<boolean>(false);

	interface acceptingDataStructure {
		data : acceptingDataItemStructure,
		msg : string,
		code : number
	}
	interface acceptingDataItemStructure {
		data : object,
		[propName : string] : number | string | object
	}
	onLoad(async () => {
		await getArticleCategory().then((res : acceptingDataStructure) => {
			const initData = { name: t("all"), category_id: '' };
			categoryList.value.push(initData);
			categoryList.value = categoryList.value.concat(res.data);
		});
	})

	interface mescrollStructure {
		num : number,
		size : number,
		endSuccess : Function,
		[propName : string] : any
	}
	const getArticleListFn = (mescroll : mescrollStructure) => {
		loading.value = false;
		let data : object = {
			category_id: currCategoryId.value,
			title: articleTitle.value,
			page: mescroll.num,
			limit: mescroll.size
		};

		getArticleList(data).then((res : acceptingDataStructure) => {
			let newArr = (res.data.data as Array<Object>);
			//设置列表数据
			if (mescroll.num == 1) {
				articleList.value = []; //如果是第一页需手动制空列表
			}
			articleList.value = articleList.value.concat(newArr);
			mescroll.endSuccess(newArr.length);
			loading.value = true;
		}).catch(() => {
			loading.value = true;
			mescroll.endErr(); // 请求失败, 结束加载
		})
	}

	const loadCategory = (id : string) => {
		currCategoryId.value = id;
		getMescroll().resetUpScroll();
	}

	const searchFn = () => {
		getMescroll().resetUpScroll();
	}

	const toLink = (id : string) => {
		redirect({ url: '/app/pages/article/detail', param: { id } })
	}

	onMounted(() => {
		setTimeout(() => {
			getMescroll().optUp.textNoMore = t("end");
		}, 500)
	});
</script>

<style lang="scss" scoped>
	.nav-item.active {
		color: $u-primary;
	}

	.scroll-view-wrap {
		word-break: keep-all;
	}
</style>