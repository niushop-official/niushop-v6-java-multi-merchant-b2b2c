<template>
	<view class="bg-[var(--page-bg-color)] min-h-[100vh]" :style="themeColor()">
		<view class="fixed left-0 right-0 top-0 product-warp bg-[var(--page-bg-color)]">
			<view class="py-[14rpx] flex items-center justify-between px-[20rpx]">
				<view class="flex-1 search-input bg-[#fff] mr-[20rpx]">
					<text @click.stop="searchTypeFn('all')" class="nc-iconfont nc-icon-sousuo-duanV6xx1 btn"></text>
					<input class="input" maxlength="50" type="text" v-model="goods_name"  placeholder="请搜索您想要的商品" placeholderClass="text-[var(--text-color-light9)] text-[24rpx]" confirm-type="search" @confirm="searchTypeFn('all')">
					<text v-if="goods_name" class="nc-iconfont nc-icon-cuohaoV6xx1 clear" @click="goods_name=''"></text>
				</view>
				<view :class="['list-icon iconfont list-icon', listType ? 'icona-yingyongzhongxinV6xx-32' : 'icona-yingyongliebiaoV6xx-32']" @click="listIconBtn"></view>
			</view>
			<view class="flex justify-between items-center h-[88rpx] px-[30rpx]">
				<view class="flex items-center justify-between text-[26rpx] flex-1">
					<text class="text-light3"  :class="{ 'text-color font-500': searchType == 'all' }" @click="searchTypeFn('all')">综合排序</text>
					<view class="flex items-center text-light3" :class="{ 'text-color font-500': searchType == 'sale_num' }" @click="searchTypeFn('sale_num')">
						<text class="mr-[4rpx]">销量</text>
						<text v-if="sale_num == 'asc'" class="font16 nc-iconfont nc-icon-a-xiangshangV6xx1" :class="{'text-primary': searchType == 'sale_num' }"></text>
						<text  v-else class="font16 nc-iconfont nc-icon-a-xiangxiaV6xx1" :class="{'text-primary': searchType == 'sale_num' }"></text>
					</view>
					<view class="flex items-center text-light3" :class="{'text-color font-500': searchType == 'price' }" @click="searchTypeFn('price')">
						<text class=" mr-[4rpx]">价格</text>
						<text v-if="price == 'asc'" class="font16 nc-iconfont nc-icon-a-xiangshangV6xx1" :class="{'text-primary': searchType == 'price' }"></text>
						<text v-else class="font16 nc-iconfont nc-icon-a-xiangxiaV6xx1" :class="{'text-primary': searchType == 'price' }"></text>
					</view>
					<view class="flex items-center" :class="{'text-color font-500': searchType == 'label', 'text-light3': searchType != 'label' }" @click="searchTypeFn('label')">
						<text class="mr-[8rpx]">筛选</text>
						<text class="iconfont font-500 icona-shaixuanV6xx-34 -mb-[4rpx] !text-[26rpx]"></text>
					</view>
				</view>
			</view>
		</view>
		<u-popup :show="labelPopup" mode="top" @close="labelPopup = false">
			<view @touchmove.prevent.stop>
				<view class="text-[28rpx] px-[30rpx] mt-[40rpx]">全部分类</view>
				<view class="flex flex-wrap pl-[30rpx] pt-[30rpx]">
					<text @click="loadCategory(item.category_id)" v-for="(item, index) in categoryList" :key="item.category_id"
						:class="{ 'label-select': currGoodsCategory == item.category_id }" class="category-wrap truncate  flex items-center justify-center  box-border ">
						{{ item.category_name }}
					</text>
				</view>
			</view>
		</u-popup>

		<mescroll-body ref="mescrollRef" top="176rpx" bottom="50px" @init="mescrollInit" :down="{ use: false }"  @up="getAllAppListFn">
			<view v-if="goodsList.length" :class="['sidebar-margin', !listType ? 'biserial-goods-list' : '']">
				<template v-if="listType">
					<view v-for="(item, index) in goodsList" :key="index" class="goods-item-wrap bg-white flex  overflow-hidden" :class="{ 'mb20': (index+1) != goodsList.length}" @click="toDetail(item.goods_id)">
						<image v-if="item.goods_cover_thumb_mid" class="w-[270rpx] h-[270rpx] rounded-[var(--rounded-mid)]" :src="img(item.goods_cover_thumb_mid)" :mode="'aspectFill'" @error="item.goods_cover_thumb_mid='static/resource/images/diy/shop_default.jpg'"></image>
						<image v-else class="w-[270rpx] h-[270rpx] rounded-[var(--rounded-mid)]" :src="img('static/resource/images/diy/shop_default.jpg')" :mode="'aspectFill'"></image>
						<view class="flex-1 flex flex-col  ml-[20rpx] py-[6rpx]">
							<view class="text-[28rpx] text-[#333] leading-[40rpx] multi-hidden mb-[10rpx]">
								<view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                {{ item.goods_name }}
							</view>
							<view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden mb-[8rpx]" v-if="item.sub_title">
							    {{ item.sub_title }}
							</view>
							<view class="flex mb-[10rpx]" v-if="item.site?.distance">
								<view class="bg-[#f6f6f6] px-[10rpx] h-[42rpx] rounded-[6rpx] flex items-center">
									<view class="flex items-center">
										<view class="w-[28rpx] h-[28rpx] rounded-[2rpx] overflow-hidden mr-[8rpx] flex-center">
											<up-image radius="2rpx" width="28rpx" height="28rpx" :src="diyShop.siteLogo(item.site)" mode="aspectFill">
												<template #error>
													<image :src="img('addon/mall/shop/shop_default.png')" class="w-[28rpx] h-[28rpx] rounded-[2rpx] align-middle"></image>
												</template>
											</up-image>
										</view>
										<view class="text-[24rpx] max-w-[240rpx] truncate">{{ item.site?.site_name }}</view>
									</view>
									<view class="w-[2rpx] h-[18rpx] bg-[#bbb] mx-[10rpx]"></view>
									<view class="text-[24rpx] flex-shrink-0">{{ distance(item.site.distance) }}</view>
								</view>
							</view>
							<view v-if="item.goods_label_name && item.goods_label_name.length" class="flex flex-wrap">
                                <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                    <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')"/>
                                    <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">
                                        {{ tagItem.label_name }}
                                    </view>
                                </template>
                            </view>
							<view class="mt-auto flex justify-between items-end">
								<view class="flex items-baseline mt-[20rpx]">
									<view class="text-[var(--price-text-color)] price-font flex items-baseline">
										<text class="text-[24rpx] font-500 mr-[4rpx]">￥</text>
										<text class="text-[40rpx] font-500">{{ diyGoods.goodsPrice(item).toFixed(2).split('.')[0] }}</text>
										<text class="text-[24rpx] font-500">.{{ diyGoods.goodsPrice(item).toFixed(2).split('.')[1] }}</text>
									</view>
									<image v-if="diyGoods.priceType(item) == 'svip_price'" class="max-w-[64rpx] h-[22rpx] ml-[6rpx]" :src="img('addon/mall/SVIP.png')" mode="heightFix" />
									<image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
									<image class="h-[24rpx] w-[72rpx] ml-[6rpx]" v-if="diyGoods.priceType(item) == 'discount_price'" :src="img('addon/mall/discount.png')" mode="heightFix" />
								</view>
                                
                                <text class="text-[22rpx] mt-[20rpx] text-[var(--text-color-light9)]">
                                    已售{{ item.sale_num }}{{ item.unit }}
                                </text>
                            </view>
						</view>
					</view>
				</template>
				<template v-else>
					<view>
						<template v-for="(item, index) in goodsList">
							<view v-if="(index % 2) == 0" class="flex flex-col bg-[#fff] box-border rounded-[var(--rounded-mid)] overflow-hidden mt-[var(--top-m)]"
                                  @click="toDetail(item.goods_id)">
                                <image v-if="item.goods_cover_thumb_mid"
                                       class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]"
                                       :src="img(item.goods_cover_thumb_mid)" :mode="'aspectFill'"
                                       @error="item.goods_cover_thumb_mid='static/resource/images/diy/shop_default.jpg'"/>
                                <image v-else
                                       class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]"
                                       :src="img('static/resource/images/diy/shop_default.jpg')"
                                       :mode="'aspectFill'"/>
                                <view class="px-[20rpx] flex-1 pt-[16rpx] pb-[24rpx] flex flex-col justify-between">
									<view class="flex mb-[16rpx]" v-if="item.site?.distance">
										<view class="bg-[#f6f6f6] px-[8rpx] h-[40rpx] rounded-[6rpx] flex items-center">
											<view class="flex items-center">
												<view class="w-[28rpx] h-[28rpx] rounded-[2rpx] overflow-hidden mr-[8rpx] flex-center flex-shrink-0">
													<up-image radius="2rpx" width="28rpx" height="28rpx" :src="diyShop.siteLogo(item.site)" mode="aspectFill">
														<template #error>
															<image :src="img('addon/mall/shop/shop_default.png')" class="w-[28rpx] h-[28rpx] rounded-[2rpx] align-middle"></image>
														</template>
													</up-image>
												</view>
												<view class="text-[22rpx] using-hidden">{{ item.site?.site_name }}</view>
											</view>
											<view class="w-[2rpx] h-[18rpx] bg-[#bbb] mx-[10rpx]"></view>
											<view class="text-[22rpx] flex-shrink-0">{{ distance(item.site.distance) }}</view>
										</view>
									</view>
                                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden">
                                        <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                        {{ item.goods_name }}
                                    </view>
									<view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sub_title">
									    {{ item.sub_title }}
									</view>
                                    <view v-if="item.goods_label_name && item.goods_label_name.length" class="flex flex-wrap">
                                        <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                            <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')"/>
                                            <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                        </template>
                                    </view>
                                    <view class="flex justify-between flex-wrap items-end">
                                        <view class="flex items-baseline mt-[20rpx]">
                                            <view class="text-[var(--price-text-color)] price-font flex items-baseline">
                                                <text class="text-[24rpx] font-500">￥</text>
                                                <text class="text-[40rpx] font-500">{{ diyGoods.goodsPrice(item).toFixed(2).split('.')[0] }}</text>
                                                <text class="text-[24rpx] font-500">.{{ diyGoods.goodsPrice(item).toFixed(2).split('.')[1] }}</text>
                                            </view>
											<image v-if="diyGoods.priceType(item) == 'svip_price'" class="max-w-[64rpx] h-[22rpx] ml-[6rpx]" :src="img('addon/mall/SVIP.png')" mode="heightFix" />
                                            <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
											<image class="h-[24rpx] w-[72rpx] ml-[6rpx]" v-if="diyGoods.priceType(item) == 'discount_price'" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                        </view>
                                        <text class="text-[22rpx] text-[var(--text-color-light9)] mt-[20rpx]">已售{{ item.sale_num }}{{ item.unit }}</text>
                                    </view>
                                </view>
                            </view>
						</template>
					</view>
					<view>
                        <template v-for="(item, index) in goodsList">
                            <view v-if="(index%2) == 1" class="flex flex-col bg-[#fff] box-border rounded-[var(--rounded-mid)] overflow-hidden mt-[var(--top-m)]" @click="toDetail(item.goods_id)">
                                <image v-if="item.goods_cover_thumb_mid"
                                       class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]"
                                       :src="img(item.goods_cover_thumb_mid)" :mode="'aspectFill'"
                                       @error="item.goods_cover_thumb_mid='static/resource/images/diy/shop_default.jpg'"></image>
                                <image v-else class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]"
                                       :src="img('static/resource/images/diy/shop_default.jpg')"
                                       :mode="'aspectFill'"></image>
                                <view class="px-[20rpx] flex-1 pt-[16rpx] pb-[24rpx] flex flex-col justify-between">
									<view class="flex mb-[16rpx]" v-if="item.site?.distance">
										<view class="bg-[#f6f6f6] px-[8rpx] h-[40rpx] rounded-[6rpx] flex items-center">
											<view class="flex items-center">
												<view class="w-[28rpx] h-[28rpx] rounded-[2rpx] overflow-hidden mr-[8rpx] flex-center flex-shrink-0">
													<up-image radius="2rpx" width="28rpx" height="28rpx" :src="diyShop.siteLogo(item.site)" mode="aspectFill">
														<template #error>
															<image :src="img('addon/mall/shop/shop_default.png')" class="w-[28rpx] h-[28rpx] rounded-[2rpx] align-middle"></image>
														</template>
													</up-image>
												</view>
												<view class="text-[22rpx] using-hidden">{{ item.site?.site_name }}</view>
											</view>
											<view class="w-[2rpx] h-[18rpx] bg-[#bbb] mx-[10rpx]"></view>
											<view class="text-[22rpx] flex-shrink-0">{{ distance(item.site.distance) }}</view>
										</view>
									</view>
                                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden">
                                        <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">
                                            {{ item.goods_brand.brand_name }}
                                        </view>
                                        {{ item.goods_name }}
                                    </view>
									<view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sub_title">
									    {{ item.sub_title }}
									</view>
                                    <view v-if="item.goods_label_name && item.goods_label_name.length" class="flex flex-wrap">
                                        <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                            <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')" />
                                            <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">
                                                {{ tagItem.label_name }}
                                            </view>
                                        </template>
                                    </view>
                                    <view class="flex justify-between flex-wrap items-baseline">
                                        <view class="flex items-baseline mt-[20rpx]">
                                            <view class="text-[var(--price-text-color)] price-font flex items-baseline">
                                                <text class="text-[24rpx] font-500">￥</text>
                                                <text class="text-[40rpx] font-500">{{ diyGoods.goodsPrice(item).toFixed(2).split('.')[0] }}</text>
                                                <text class="text-[24rpx] font-500">.{{ diyGoods.goodsPrice(item).toFixed(2).split('.')[1] }}</text>
                                            </view>
											<image v-if="diyGoods.priceType(item) == 'svip_price'" class="max-w-[64rpx] h-[22rpx] ml-[6rpx]" :src="img('addon/mall/SVIP.png')" mode="heightFix" />
                                            <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
											<image class="h-[24rpx] w-[72rpx] ml-[6rpx]" v-if="diyGoods.priceType(item) == 'discount_price'" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                        </view>
                                        <text class="mt-[20rpx] text-[22rpx] text-[var(--text-color-light9)]">已售{{ item.sale_num }}{{ item.unit }}</text>
                                    </view>
                                </view>
                            </view>
                        </template>
                    </view>
				</template>

			</view>
			<mescroll-empty v-if="!goodsList.length && loading" :option="{tip : '暂无商品'}"></mescroll-empty>
		</mescroll-body>

		<tabbar />
	</view>
</template>

<script setup lang="ts">
import { watch, ref, onMounted } from 'vue'
import { t } from '@/locale'
import { redirect, img, getToken, distance } from '@/utils/common';
import { getGoodsCategoryTree } from '@/app/api/basic'
import { getGoodsPages, getGoodsCouponPages } from '@/addon/mall/api/goods';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import { useGoods } from '@/addon/mall/hooks/useGoods'
import { useShop } from '@/addon/mall/hooks/useShop'
import useSystemStore from '@/stores/system'
import { useLocation } from '@/hooks/useLocation'

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const diyGoods = useGoods();
const diyShop = useShop();
let siteId = ref(0)
let categoryList = ref<Array<Object>>([]);
let goodsList = ref<Array<any>>([]);
let coupon_id = ref<number | string>('');
let currGoodsCategory = ref<number | string>('');
let mescrollRef = ref(null);
let loading = ref<boolean>(false);
// 标签
let labelPopup = ref(false);
let goods_name = ref("");
let price = ref("");
let sale_num = ref("");
let searchType = ref('all');
let is_vip_card = ref('');
// 刷新定位
/************** 定位-start ****************/
const locationVal = useLocation(true);
locationVal.refresh();
/************** 定位-end ****************/
const systemStore = useSystemStore();


//列表类型
let listType = ref(true)
onLoad(async (option:any) => {
	currGoodsCategory.value = option.curr_goods_category || ''
	goods_name.value = option.goods_name ? decodeURIComponent(option.goods_name) : ''
	coupon_id.value = option.coupon_id || ''
    siteId.value = option.site_id || 0;
	is_vip_card.value = option.is_vip_card || '';
	await getGoodsCategoryTree({}).then((res: any) => {
		const initData = { category_name: "全部", category_id: '' };
		categoryList.value.push(initData);
		categoryList.value = categoryList.value.concat(res.data);
	});
})

interface mescrollStructure {
	num: number,
	size: number,
	endSuccess: Function,
	[propName: string]: any
}

const getAllAppListFn = (mescroll: mescrollStructure) => {
	loading.value = false;
	let data: object = {
		goods_mall_category: currGoodsCategory.value,
		page: mescroll.num,
		limit: mescroll.size,
		keyword: goods_name.value,
		coupon_id: coupon_id.value,
        site_id:siteId.value,
		order: searchType.value === 'all' ? '' : searchType.value,
		sort: searchType.value == 'price' ? price.value : sale_num.value,
		position: systemStore.diyAddressInfo,
		is_vip_card: is_vip_card.value
	};
	let api = coupon_id.value ? getGoodsCouponPages : getGoodsPages;
	api(data).then((res: any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (Number(mescroll.num) === 1) {
			goodsList.value = []; //如果是第一页需手动制空列表
		}
		goodsList.value = goodsList.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}

const loadCategory = (id: string) => {
	currGoodsCategory.value = id;
	goodsList.value = [];
	getMescroll().resetUpScroll();
	labelPopup.value = false;
}
// 搜索
const searchTypeFn = (type:any) => {
	searchType.value = type;
	if (type == 'all') {
		sale_num.value = '';
		price.value = '';
	}
	if (type == 'price') {
		sale_num.value = '';
		if(price.value){
			price.value = price.value == 'asc' ? 'desc' : 'asc';
		}else{
			price.value = 'asc';
		}
	}
	if (type == 'sale_num') {
		price.value = '';
		if(sale_num.value){
			sale_num.value = sale_num.value == 'asc' ? 'desc' : 'asc';
		}else{
			sale_num.value = 'asc';
		}
	}
	if (type == 'label') {
		sale_num.value = 'asc';
		price.value = 'asc';
		labelPopup.value = true;
	} else {
		labelPopup.value = false;
		goodsList.value = [];

		getMescroll().resetUpScroll();
	}
}
//列表样式切换
const listIconBtn = () => {
	listType.value = !listType.value
}
const toDetail = (id: string | number) => {
	redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: id }, mode: 'navigateTo' })
}
onMounted(() => {
	setTimeout(() => {
		getMescroll().optUp.textNoMore = t("end");
	}, 500)
});

</script>

<style lang="scss" scoped>
@import '@/addon/mall/styles/common.scss';
.scroll-view-wrap {
	word-break: keep-all;
}
.list-icon {
	font-size: 32rpx;
	color: #333;
	margin-bottom: -2rpx;
}
.font16 {
	font-size: 16px;
}
.text-light3 {
	color:#333;
}
.text-color {
	color: var(--primary-color) !important;
}

.label-select {
	color: var(--primary-color);
	border-color: var(--primary-color);
	background-color: var(--primary-color-light);
}
.category-wrap{
	color: #333;
	padding-left: 10px;
	padding-right: 10px;
	border: 2rpx solid transparent;
	width: 184rpx;
	height: 56rpx;
	margin-right: 30px;
	margin-bottom: 30px;
	background-color: var(--temp-bg);
	border-radius: 50rpx;
	font-size: 24rpx;
}
.goods-item-wrap {
	padding: 24rpx 20rpx;
	border-radius: var(--rounded-small);
	&.mb20 {
		margin-bottom: 20rpx;
	}
}
:deep(.u-popup .u-transition) {
	top: 156rpx !important;
}

.product-warp {
	z-index: 99999;
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}
:deep(.u-input__content__clear){
	width: 28rpx;
	height: 28rpx;
	font-size: 28rpx;
	background-color: var(--text-color-light9);
}
.biserial-goods-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-gap: 10px;
}
</style>