<template>
    <view class="bg-[var(--page-bg-color)] min-h-[100vh] overflow-hidden" :style="themeColor()">
        <view>
            <shop-head></shop-head>
        </view>
        <view>
            <view class="my-[var(--top-m)] sidebar-margin relative" v-if="tabsData.length">
                <view class="p-[20rpx] bg-[#fff] rounded-[24rpx] mb-[var(--top-m)]" v-for="(item, index) in tabsData" :key="index" @click="toLink(item.category_id)">
                    <view class="pl-[14rpx] text-[30rpx] font-bold leading-[42rpx] class-select">{{ item.category_name}}</view>
                    <view class="mt-[20rpx] flex flex-wrap justify-between" v-if="item.child_list && item.child_list.length">
                        <view class="w-[325rpx] h-[80rpx] bg-[#F6F6F6] rounded-[12rpx] px-[20rpx] mb-[20rpx] leading-[80rpx] truncate box-border text-[26rpx] font-500" v-for="(subItem,subIndex) in item.child_list" :key="subIndex" @click.stop="toLink(subItem.category_id)">{{ subItem.category_name }}</view>
                    </view>
                </view>
            </view>
            <mescroll-empty v-if="!tabsData.length && !loading" :option="{tip : '暂无商品分类'}"></mescroll-empty>
        </view>
        <site-tabbar :site-id="siteId" v-if="siteId" />
    </view>
</template>
<script setup lang="ts">
import { ref } from 'vue';
import { onLoad, onPageScroll } from '@dcloudio/uni-app';
import { img, redirect } from '@/utils/common';
import { getGoodsCategoryTree } from '@/app/api/basic';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import shopHead from '@/addon/mall/pages/home/components/shop-head/shop-head.vue'

const loading = ref<boolean>(true);
const siteId = ref(0);
const tabsData: any = ref<Array<Object>>([])
onLoad((option: any) => {
    siteId.value = option.site_id;
    getCategoryData()
})
const getCategoryData = () => {
    loading.value = true;
    getGoodsCategoryTree({site_id: siteId.value }).then((res: any) => {
        tabsData.value = res.data;
        loading.value = false;
    }).catch(() => {
        loading.value = false;
    });
}


onPageScroll((e) => {})
const toLink = (curr_goods_category: string) => {
	redirect({ url: '/addon/mall/pages/shop/goods_list', param: {site_id:siteId.value, curr_goods_category } })
}
</script>
<style lang="scss" scoped>
.class-select {
	position: relative;

	&::before {
		content: "";
		position: absolute;
		left: 0;
        width: 6rpx;
		height: 30rpx;
		background-color: $u-primary;
        border-radius: 1rpx 5rpx 5rpx 1rpx;
        top: 50%;
        transform: translateY(-50%);	
	}
}
</style>