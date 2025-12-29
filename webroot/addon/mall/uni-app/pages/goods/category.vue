<template>
	<view :style="themeColor()">
		<!-- #ifdef MP-WEIXIN || APP-PLUS -->
        <top-tabbar :data="topTabbarData" scrollBool="1" :isBack="false" />
        <!-- #endif -->
		<view class="relative">
			<category-template-one-one class="category" v-if="config.level===1&&config.template === 'style-1'" :categoryId="categoryId" :config="config" />
			<category-template-two-one v-if="config.level===2&&config.template === 'style-1'" :categoryId="categoryId" :config="config" />
			<category-template-two-two class="category" v-if="config.level===2&&config.template === 'style-2'" :categoryId="categoryId" :config="config" />
			<category-template-three-one v-if="config.level===3&&config.template === 'style-1'" :categoryId="categoryId" :config="config" />
		</view>
	</view>
</template>
<script setup lang="ts">
import { onLoad, onShow } from '@dcloudio/uni-app'
import { ref, computed } from 'vue';
import categoryTemplateTwoOne from '@/addon/mall/pages/goods/components/category-template-two-one.vue';
import categoryTemplateOneOne from '@/addon/mall/pages/goods/components/category-template-one-one.vue';
import categoryTemplateTwoTwo from '@/addon/mall/pages/goods/components/category-template-two-two.vue';
import categoryTemplateThreeOne from '@/addon/mall/pages/goods/components/category-template-three-one.vue';
import { getGoodsCategoryConfig } from '@/app/api/basic';
import useCartStore from '@/addon/mall/stores/cart'
import { topTabar } from '@/utils/topTabbar';

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let topTabbarData = topTabarObj.setTopTabbarParam({ title: '商品分类', topStatusBar: { textColor: '#333' }})
/********* 自定义头部 - end ***********/

const cartStore = useCartStore();
const config = ref({})
const categoryId = ref(0)

const getGoodsCategoryConfigFn = () => {
	getGoodsCategoryConfig().then(res => {
		config.value = res.data
        uni.setNavigationBarTitle({
            title: config.value.page_title
        });
		topTabbarData = topTabarObj.setTopTabbarParam({ title: config.value.page_title, topStatusBar: { textColor: '#333' }})
	})
}

onLoad((options:any) => {
    categoryId.value = options.category_id || 0;
    getGoodsCategoryConfigFn()
});

onShow(() => {
	// 查询购物车列表
	cartStore.getList();
})

</script>
<style>

/*  #ifdef  H5  */
:deep(.category .detail .mescroll-body) {
	padding-bottom: calc(50px  + constant(safe-area-inset-bottom)) !important;
	padding-bottom: calc(50px  + env(safe-area-inset-bottom)) !important;
}

:deep(.category .cart .mescroll-body){
	padding-bottom: calc(100rpx + 50px  + constant(safe-area-inset-bottom)) !important;
	padding-bottom: calc(100rpx + 50px  + env(safe-area-inset-bottom)) !important;
}
/*  #endif  */

.category .labelPopup :deep(.u-transition) {
	top: 96rpx !important;
	left: 170rpx !important;
	z-index: 8 !important;
	&:nth-child(2){
		left: 168rpx !important;
	}
}
.category .labelPopup.active :deep(.u-transition) {
	top: 190rpx !important;
}

</style>
<style lang="scss" scoped>
:deep(.mescroll-upwarp) {
	box-sizing: border-box;
	padding-left: 182rpx;
}

// :deep(.tab-bar-placeholder) {
// 	display: none !important;
// }

:deep(.u-tabbar__placeholder) {
	display: none !important;
}
:deep(.category-img .u-image__error){
	background-color: #f1f1f1;
}
button::after{
	border: 0 !important;
}
:deep(.part .mescroll-empty){
	width: 542rpx;
	height: 542rpx;
	margin-top: 0;
	margin-left: 0;
	margin-right: 0;
	padding-top: 50rpx;
}
</style>
