<template>
	<view class="bg-[var(--page-bg-color)] min-h-[100vh]" :style="themeColor()">
        <view class="fixed left-0 right-0 top-0 z-100 bg-[var(--page-bg-color)] product-warp">
			<!-- #ifdef MP-WEIXIN -->
			<top-tabbar :data="topTabbarData" scrollBool="1" :isBack="false" />
			<!-- #endif -->
			<view class="px-[var(--sidebar-m)] py-[14rpx] flex items-center">
				<template v-if="isShow">
					<view @click.stop="goToChooseLocation" v-if="systemStore.diyAddressInfo"  class="mr-[30rpx]">
						<view class="flex items-baseline font-500">
							<text class="text-[24rpx] mr-[2rpx]">{{ systemStore.diyAddressInfo.city }}</text>
							<text class="iconfont iconxiaV6xx !text-[24rpx]"></text>
						</view>
						<view class="text-[18rpx] mt-[10rpx] truncate max-w-[160rpx]" v-if="systemStore.diyAddressInfo.community">{{ systemStore.diyAddressInfo.community }}</view>
					</view>
					<view @click.stop="goToChooseLocation" class="text-[24rpx] mr-[30rpx] truncate max-w-[160rpx]" v-else>{{ systemStore.defaultPositionAddress }}</view>
				</template>
				<view class="search-input bg-[#fff]">
					<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 btn" @click="searchNameFn()"></text>
					<input class="input" maxlength="50" type="text" v-model="shopName"  placeholder="请搜索店铺" placeholderClass="text-[var(--text-color-light9)]" confirm-type="search"  @confirm="searchNameFn()"/>
					<text v-if="shopName" class="nc-iconfont nc-icon-cuohaoV6xx clear" @click="reset"></text>
				</view>
			</view>
			<view class="h-[88rpx] px-[50rpx]">
				<view class="flex items-center justify-between text-[28rpx] h-[88rpx]">
					<text class="flex items-center justify-center" :class="{ 'class-select': searchType == 'all' }" @click="searchTypeFn('all')">默认</text>
					<text class="flex items-center justify-center" :class="{ 'class-select': searchType == 'mall_sale_num' }" @click="searchTypeFn('mall_sale_num')">销量</text>
					<text class="flex items-center justify-center" :class="{'class-select': searchType == 'distance' }" @click="searchTypeFn('distance')" v-if="isShow">距离</text>
					<!-- <text class="flex-1 flex items-center justify-center" :class="{'class-select': searchType == 'area', 'text-[#333]': searchType != 'area' }" @click="searchTypeFn('area')">
						<text class="max-w-[138rpx] truncate inline-block">{{ region.district_name  || '区域' }}</text>
					</text> -->
					<view class="flex items-center justify-center text-[#333]" @click="searchTypeFn('label')">
						<text class="mr-[8rpx]">筛选</text>
						<text class="iconfont font-500 icona-shaixuanV6xx-34 -mb-[2rpx] !text-[24rpx]"></text>
					</view>
				</view>
			</view>
		</view>
		<view class="lable-warp">
			<u-popup :show="labelPopup" mode="top" @close="labelPopup = false">
				<view :style="labelPopupCss"  @touchmove.prevent.stop >
					<view class="text-[28rpx] px-[30rpx] mt-[40rpx]">店铺分类</view>
					<view class="flex flex-wrap pl-[30rpx] pt-[30rpx]">
						<text @click="loadCategory(item.category_id)" v-for="(item, index) in categoryList" :key="index" :class="{ 'label-select': currCategory.indexOf(item.category_id) != -1 }" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent w-[184rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[50rpx] text-[24rpx]">
							{{ item.name }}
						</text>
					</view>
					<view class="text-[28rpx] px-[30rpx] mt-[40rpx]">配送方式</view>
					<view class="flex flex-wrap pl-[30rpx] pt-[30rpx]">
						<text @click="loadDelivery(key)" v-for="(item, key) in deliveryTypeList" :key="key" :class="{ 'label-select': currDelivery.indexOf(key) != -1 }" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent w-[184rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[50rpx] text-[24rpx]">
							{{ item }}
						</text>
					</view>
					<view class="text-[28rpx] px-[30rpx] mt-[40rpx]">是否自营</view>
					<view class="flex flex-wrap pl-[30rpx] pt-[30rpx]">
						<text @click="loadSelf(item.value)" v-for="(item, index) in selfList" :key="index" :class="{ 'label-select': is_self == item.value }" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent w-[184rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[50rpx] text-[24rpx]">
							{{ item.name }}
						</text>
					</view>
					<view class="flex items-center justify-between px-[20rpx] py-[30rpx]">
						<text class="w-[320rpx] h-[68rpx] rounded-full text-center leading-[68rpx] text-[26rpx] mr-[20rpx] text-primary border-primary border-solid border-[2rpx]" @click="reset">重置</text>
						<text class="w-[320rpx] h-[68rpx] bg-primary rounded-full text-center leading-[68rpx] text-[26rpx] text-[#fff]" @click="confirm">确认</text>
					</view>
				</view>
			</u-popup>
		</view>

		<mescroll-body ref="mescrollRef" :top="mescrollTop"  @init="mescrollInit" :down="{ use: false }" height="auto" @up="getShopListFn">
			<view class="px-[var(--sidebar-m)] pt-[var(--top-m)]">
				<block v-for="(item,index) in shopList" :key="index">
					<view class="mb-[var(--top-m)] card-template p-[20rpx]">
						<view class="flex  items-center justify-between" @click="shopClick(item.site_id)">
							<view class="flex">
								<view class="w-[70rpx] h-[70rpx] border-[1rpx] border-[#F1F1F1] border-solid rounded-[12rpx] flex items-center justify-center">
									<up-image radius="6" width="70rpx" height="70rpx" :src="diyShop.siteLogo(item)" mode="aspectFill">
										<template #error>
											<image :src="img('addon/mall/shop/shop_default.png')" class="w-[70rpx] h-[70rpx] rounded-[12rpx] align-middle"></image>
										</template>
									</up-image>
								</view>
								<view class="ml-[20rpx] flex flex-col justify-between py-[4rpx]">
									<view class="text-[26rpx] leading-[32rpx] font-500 truncate max-w-[350rpx]">{{ diyShop.siteName(item) }}</view>
									<view class="flex items-center">
										<text class="text-[22rpx] text-[#999] ">{{item.category_name}}</text>
										<template v-if="item.distance">
											<text class="text-[22rpx] text-[#999]" @click.stop="getAddress(item)">
												<text class="inline-block w-[2rpx] h-[18rpx] bg-[#ccc] mx-[12rpx]"></text>
												<text>{{ distance(item.distance) }}</text>
												<text class="inline-block nc-iconfont nc-icon-youV6xx text-[22rpx] relative top-[2rpx]"></text>
											</text>
										</template>
									</view>
									<!-- <view class="text-[22rpx] text-primary ml-[4rpx]">{{item.follow_number}}人关注</view> -->
								</view>
							</view>
							<button class="w-[130rpx]  h-[50rpx] font-500 text-[22rpx] leading-[50rpx] !text-[#fff] m-0  p-0 rounded-full  primary-btn-bg remove-border" hover-class="none">进店看看</button>
						</view>
						<view class="goods-list mt-[20rpx]" v-if="item.goods.length">
							<view class="goods-item" v-for="(subItem,suIndex) in item.goods" :key="suIndex" @click="toGoodsDetail(subItem.goods_id)">
								<view class="bg-[#fff] w-[210rpx]">
									<up-image radius="var(--goods-rounded-big)" width="212rpx" height="212rpx" :src="img(subItem.goods_cover_thumb_mid ? subItem.goods_cover_thumb_mid : '')" mode="aspectFill">
										<template #error>
											<image :src="img('static/resource/images/diy/shop_default.jpg')" class="w-[212rpx] h-[212rpx] rounded-[var(--goods-rounded-big)]"></image>
										</template>
									</up-image>
									<view class="relative min-h-[40rpx] p-[10rpx] pb-[20rpx]">
										<view class="text-[26rpx] text-[#333] truncate mb-[10rpx] ">{{ subItem.goods_name }}</view>
										<view class="text-[var(--price-text-color)] font-bold price-font block truncate max-w-[160rpx] leading-[1] overflow-hidden">
											<text class="text-[20rpx] font-400 mr-[2rpx]">￥</text>
											<text class="text-[32rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(subItem)).toFixed(2) }}</text>
										</view>
									</view>
								</view>
							</view>
						</view>
					</view>
				</block>
			</view>
			<mescroll-empty v-if="!shopList.length && loading"></mescroll-empty>
		</mescroll-body>
		<tabbar />
		<area-select ref="areaRef" @complete="areaSelectComplete" :area-id="region.district_id || region.city_id" />
	</view>
</template>

<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import { img, redirect, pxToRpx, isWeixinBrowser, distance } from '@/utils/common'
import { getShopWebList } from '@/addon/mall/api/shop';
import { getSiteCategoryAll } from '@/app/api/site'
import { getDeliveryType, getDeliveryConfig } from '@/app/api/basic'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom, onShow } from '@dcloudio/uni-app';
import { useGoods } from '@/addon/mall/hooks/useGoods'
import { useShop } from '@/addon/mall/hooks/useShop'
import { useLocation } from '@/hooks/useLocation'
import useSystemStore from '@/stores/system';
import { topTabar } from '@/utils/topTabbar';
import wechat from '@/utils/wechat'

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let topTabbarData = topTabarObj.setTopTabbarParam({ title: '店铺街', topStatusBar: { textColor: '#333' }})
/********* 自定义头部 - end ***********/

/************** 定位-start ****************/
const locationVal = useLocation(true);

/************** 定位-end ****************/

const systemStore = useSystemStore();

onLoad(() => { 
	locationVal.onLoad();
	locationVal.init();
	getDeliveryConfigFn()
	getShopCategoryFn()
	getDeliveryTypeFn()
})
onShow(() => { 
    // 刷新定位
	locationVal.refresh();
})

watch(() => systemStore.diyAddressInfo, (newVal, oldVal) => {
    if (newVal && newVal != oldVal) {
        nextTick(() => {
           getMescroll().resetUpScroll();
        })
    }
}, { deep: true});

const mescrollTop = computed(()=>{
	return `${ (pxToRpx(systemStore.topTabbarInfo.height)) + 176 }rpx`
})

const labelPopupCss = computed(() => {
    let style = ''
    // #ifdef MP-WEIXIN
    style = `padding-top: calc(${ systemStore.topTabbarInfo.height || 0 }px + 20rpx) !important;`
    // #endif
    // #ifdef H5
    style = `padding-top: 20rpx !important;`
    // #endif
    return style
})

const diyGoods = useGoods();
const diyShop = useShop();

const shopName = ref('')
const loading = ref<boolean>(false)

const shopList = ref<Array<any>>([])//店铺列表
const categoryList = ref<any>([])
const deliveryTypeList = ref<any>([])
const selfList = ref<any>([{ name: '是', value: 1 }, { name: '否', value: 0}])

const searchType = ref<string>('all')//搜索类型
const labelPopup = ref(false);
const areaRef = ref()
const currCategory = ref<any>([]);//当前分类
const currDelivery = ref<any>([]);
const is_self = ref();//是否自营
const region = ref({
	province_id: 0,
	city_id: 0,
	district_id: 0,
	district_name: ''
})
const isShow = ref<any>(null);

const  getDeliveryConfigFn = () => {
	getDeliveryConfig().then((res: any) => {
		isShow.value = res.data.is_show;
	})
}

const  getShopCategoryFn = () => { 
	getSiteCategoryAll({addon: 'mall'}).then((res: any) => {
		categoryList.value = res.data;
	})
}
const getDeliveryTypeFn = () => { 
	getDeliveryType().then((res: any) => {
		deliveryTypeList.value = res.data;
	})
}
// 加载列表数据
const getShopListFn = (mescroll: any) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size,
		keyword: shopName.value,
		position: systemStore.diyAddressInfo,
		region: region.value,
		order: searchType.value === 'all' ? '' : searchType.value,
		category_id: currCategory.value,
		delivery_type: currDelivery.value,
		is_self: is_self.value
	};
	getShopWebList(data).then((res: any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (Number(mescroll.num) === 1) {
			shopList.value = []; //如果是第一页需手动制空列表
		}
		shopList.value = shopList.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}
// 搜索店铺
const searchNameFn = () => {
	getMescroll().resetUpScroll()
}

const areaSelectComplete = (event: any) => {
	console.log(event)
    region.value.province_id = event.province?.id || 0
    region.value.city_id = event.city?.id || 0
    region.value.district_id = event.district?.id || 0,
    region.value.district_name = event.district?.name || ''
	getMescroll().resetUpScroll()
}
// 搜索
const searchTypeFn = (type:any) => {
	searchType.value = type;
	if (type == 'all') {
		currCategory.value = [];
		currDelivery.value = [];
		is_self.value = ''
	}
	if (type == 'label') {
		labelPopup.value = true;
	} else if(type == 'area'){
		areaRef.value.open()
	} else {
		labelPopup.value = false;
		shopList.value = [];

		getMescroll().resetUpScroll();
	}
}

const loadCategory = (id: string) => {
	if(currCategory.value.indexOf(id) == -1){
		currCategory.value.push(id);
	}else{
		currCategory.value.splice(currCategory.value.indexOf(id), 1);
	}
}

const loadDelivery = (key: string) => {
	if(currDelivery.value.indexOf(key) == -1){
		currDelivery.value.push(key);
	}else{
		currDelivery.value.splice(currDelivery.value.indexOf(key), 1);
	}
}

const loadSelf = (val: string) => {
	is_self.value = val;
}
const reset = () => {
	labelPopup.value = false;
	currCategory.value = [];
	currDelivery.value = [];
	region.value = {
		province_id: 0, 
		city_id: 0, 
		district_id: 0,
		district_name: ''
	};
	searchType.value = 'all';
	shopName.value = ''
	is_self.value = '';
	shopList.value = [];
	getMescroll().resetUpScroll();
}
const confirm = () => { 
	labelPopup.value = false;
	shopList.value = [];
	getMescroll().resetUpScroll();
}
//店铺详情
const shopClick = (site_id:number) =>{
	redirect({url: '/addon/mall/pages/site/index', param: {site_id}})
}
// 跳转商品详情
const toGoodsDetail = (goods_id: number) => {
	redirect({url: '/addon/mall/pages/goods/detail', param: {goods_id}})
}

// 跳转到选择地址页面
const goToChooseLocation = () => {
	const pages = getCurrentPages()
	const currentPage = pages[pages.length - 1]
	const currentRoute = '/' + (currentPage ? currentPage.route : '')
	const backurl = encodeURIComponent(currentRoute)
	redirect({url: '/app/pages/location/choose_location', param: {backurl}})
}

// #ifdef H5
if (isWeixinBrowser()) {
    wechat.init();
}
// #endif
const getAddress = (data:any) => {
    // #ifdef H5
    if (isWeixinBrowser()) {
        wechat.openLocation({
            latitude: Number(data.latitude),
            longitude: Number(data.longitude),
            name: data.full_address,
            address: data.full_address
        })
    }
    // #endif
    // #ifdef MP
	uni.openLocation({
		latitude: Number(data.latitude),
		longitude: Number(data.longitude),
        name: data.full_address,
        address: data.full_address,
		success: function () { }
	});
    // #endif
}
</script>

<style lang="scss" scoped>
.class-select{
	position: relative;
	font-weight: 500;
	color: var(--primary-color);
	&::before {
		content: "";
		position: absolute;
		bottom: -14rpx;
		height: 2rpx;
		border-radius: 2rpx;
		background-color: var(--primary-color);
		width: 54rpx;
		left: 50%;
		transform: translateX(-50%);
	}
}
.label-select {
	color: var(--primary-color);
	border-color: var(--primary-color);
	background-color: var(--primary-color-light);
}

:deep(.lable-warp .u-popup .u-transition) {
	top: 156rpx !important;
}

.product-warp {
	z-index: 99999;
}

.goods-list{
	display: grid;
    grid-template-rows: auto;
    grid-template-columns: repeat(3,1fr);
    grid-gap: 12rpx;
}
.tag{
	background: linear-gradient( 90deg, #FFD6B5 0%, #FFBA8C 100%)
}
.follow{
	&::before {
		content: "";
		width: 10rpx;
		height: 10rpx;
		position: absolute;
		background-color: #fff;
		border: 1rpx solid transparent;
		border-top-color: var(--primary-color);
		border-right-color: var(--primary-color);
		left: -6rpx;
		top: 50%;
		transform: translateY(-50%) rotate(45deg);
		border-radius: 50%;
	}
	&::after {
		content: "";
		width: 10rpx;
		height: 10rpx;
		position: absolute;
		background-color: #fff;
		border: 1rpx solid transparent;
		border-left-color: var(--primary-color);
		border-bottom-color: var(--primary-color);
		right: -6rpx;
		top: 50%;
		transform: translateY(-50%) rotate(45deg);
		border-radius: 50%;
	}
}
</style>

