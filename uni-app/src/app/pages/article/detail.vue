<template>
	<view class="bg-white" :style="themeColor()">
		<block v-if="!loading">
			<view class="border-solid border-t-0 border-l-0 border-r-0 border-b-[1px] border-gray-200 p-[10px]">
				<view class="text-[16px]">
					{{articleDetail.title}}
				</view>
				<view class="flex align-center justify-between text-[12px] text-gray-400 mt-[15px]">
					<text>{{articleDetail.create_time}}</text>
				</view>
			</view>
			<view class="mx-[10px] my-[10px] bg-gray-100 p-[8px] text-[14px] rounded-[5px] leading-[1.3]">
				{{t('abstract')}}：{{articleDetail.summary}}
			</view>
			<view class="px-[10px] pd-[10px]">
				<u-parse :content="articleDetail.content" :tagStyle="style"></u-parse>
			</view>
		</block>
		<u-loading-page bg-color="rgb(248,248,248)" :loading="loading" loadingText=""></u-loading-page>
	</view>
</template>

<script setup lang="ts">
	import { reactive, ref } from 'vue'
	import { onLoad } from '@dcloudio/uni-app'
	import { t } from '@/locale'
	import { getArticleInfo,incVisit } from '@/app/api/article';
	import { useShare } from '@/hooks/useShare'

	const { setShare, onShareAppMessage, onShareTimeline } = useShare()
	onShareAppMessage()
	onShareTimeline()

	let articleDetail = ref<Array<any>>([]);
	let loading = ref<boolean>(true);
	let style = {
		h2: 'margin-bottom: 15px;',
		p: 'margin-bottom: 10px;line-height: 1.5;',
		img: 'margin: 10px 0;vertical-align: top;',
	};
	onLoad((option) => {
		loading.value = true;
		getArticleInfo(option.id).then((res:any) => {
			articleDetail.value = res.data;
			loading.value = false;
			let share = {
				title: articleDetail.value.title,
				desc: articleDetail.value.intro,
				url: articleDetail.value.image
			}
            uni.setNavigationBarTitle({
                title: articleDetail.value.title
            })
			setShare({
				wechat: {
					...share
				},
				weapp: {
					...share
				}
			});

            incVisit(option.id).then(res=>{

            })
		});
	})
</script>
<style lang="scss" scoped></style>
