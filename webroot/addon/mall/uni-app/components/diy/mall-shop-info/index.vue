<template>
	<view :style="warpCss" class="shop-info-search-wrap">
        <view class="relative pb-[20rpx]">
            <view class="bg-img-one" :class="{'!-bottom-[100rpx]': diyComponent.bgGradient == true}">
				<image v-if="diyComponent.bgImg" :src="img(diyComponent.bgImg)" mode="scaleToFill" class="w-full h-[390rpx]" :show-menu-by-longpress="true"/>
				<view v-else class="w-full h-full bg-[#ccc]"></view>
				<view class="absolute left-0 right-0 bottom-0 top-0" :style="bgImgBoxStyle"></view>
			</view>
            <view class="fixed-wrap" :style="fixedStyle">
				<!-- 搜索 -->
				<view class="diy-search-wrap relative z-10" @click.stop="toLink" :style="navbarInnerStyle">
					<view class="search-left flex-center h-[60rpx] rounded-[30rpx] box-border arrow-left pl-[12rpx] pr-[20rpx] leading-[1]" :style="navbarInnerArrowStyle">
						<text class="nc-iconfont text-[#fff] nc-icon-zuoV6xx text-[18px]" :style="{color: diyComponent.search.color }" @click.stop="backToPrevious()"></text>
						<text class="w-[2rpx] h-[26rpx] bg-[#fff] ml-[8rpx] mr-[16rpx]" :style="{backgroundColor: diyComponent.search.color }"></text>
						<text class="iconfont iconshouyev6mm text-[#fff] text-[16px]" @click.stop="redirect({ url: '/app/pages/index/index', mode: 'reLaunch' })" :style="{color: diyComponent.search.color }"></text>
					</view>

					<view class="search-content" @click.stop="toLink" :style="{backgroundColor: diyComponent.search.bgColor }">
						<text class="input-content text-[#fff] text-[24rpx] leading-[68rpx]" :style="{color: diyComponent.search.color }">{{isShowSearchPlaceholder ? diyComponent.search.text : ''}}</text>
						<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 w-[80rpx] h-[52rpx] flex items-center justify-center rounded-[50rpx] text-[28rpx] text-[#fff]" :style="{backgroundColor: diyComponent.search.btnBgColor, color: diyComponent.search.btnColor }"></text>
						<swiper class="swiper-wrap" :interval="diyComponent.search.hotWord.interval * 1000" autoplay="true" vertical="true" circular="true" v-if="!isShowSearchPlaceholder">
							<swiper-item class="swiper-item" v-for="(item) in diyComponent.search.hotWord.list" :key="item.id">
								<view class="leading-[64rpx] text-[24rpx]" :style="{color: diyComponent.search.color }">{{ item.text }}</view>
							</swiper-item>
						</swiper>
					</view>

				</view>

				<shop-tab v-if="!shopInfoTabIsShow" :tab="diyComponent.tab"  :select-index="currTabIndex" :is-show="currentBottomTab == 'shop_index' || currentBottomTab == ''"  @confirm="tabConfirm"></shop-tab>

				<view class="bg-img" v-if="fixedStyleBg">
					<view class="absolute left-0 right-0 bottom-0 top-0  z-100" :style="{'background-color': `rgba(0,0,0,${(diyComponent.bgOpacity / 100)})`}"></view>
					<image v-if="diyComponent.bgImg" :src="img(diyComponent.bgImg)" mode="widthFix" class="w-full h-full" :show-menu-by-longpress="true"/>
					<view v-else class="w-full h-full bg-[#ccc]"></view>
				</view>
			</view>
            <!-- 解决fixed定位后导航栏塌陷的问题 -->
			<template v-if="diyStore.mode != 'decorate'">
				<view v-if="diyComponent.positionWay == 'fixed' && props.scrollBool != -1" class="u-navbar-placeholder" :style="{ width: '100%', paddingTop: moduleHeight }"></view>
			</template>

             <!-- 店铺信息 -->
             <view class="diy-mall-shop-info relative px-[24rpx] py-[20rpx] box-border min-h-[120rpx]">
                <view class="flex items-center" @click="toDetail" v-if="Object.keys(shopInfo).length">
                    <view class="flex-center">
						<up-image radius="20rpx" width="80rpx" height="80rpx" :src="img(shopInfo.icon ? shopInfo.icon :'')" mode="aspectFill" :show-menu-by-longpress="true">
							<template #error>
								<image :src="img('addon/mall/shop/shop_default.png')" class="w-[80rpx] h-[80rpx] rounded-[20rpx] align-middle" :show-menu-by-longpress="true"></image>
							</template>
						</up-image>
                    </view>
                    <view class="flex flex-col ml-[20rpx]">
                        <view class="flex items-center">
							<view class="bg-[#EF000C] flex-center py-[6rpx] px-[6rpx]  text-[#FFFFFF] text-[20rpx] rounded-[4rpx] mr-[8rpx]">{{ shopInfo.typeName }}</view>
                            <view class="text-[28rpx] truncate max-w-[230rpx]" :style="{ color : diyComponent.shop.textColor }">{{ shopInfo.siteName }}</view>
                            <view class="nc-iconfont nc-icon-youV6xx text-[28rpx] ml-[10rpx] mt-[4rpx]" :style="{ color : diyComponent.shop.textColor }"></view>
                        </view>
                        <view class="flex items-center mt-[14rpx]">
                            <text class="text-[#fff] text-[23rpx] font-bold leading-[1]">{{shopInfo.follow_number}}</text>
							<text class="text-[#fff] text-[22rpx]">人关注</text>
							<text v-if="shopInfo.distance" class="text-[22rpx] text-[#fff] ml-[16rpx]">{{ distance(shopInfo.distance) }}</text>
                        </view>
                    </view>
                    <view class="flex-1 flex justify-end">
                        <view @click.stop="followShop" class="w-[120rpx] h-[50rpx] rounded-[50rpx] flex-center box-border" :class="{'primary-btn-bg':  isFollow == 0,'border-[2rpx] border-solid border-[#fff]': isFollow == 1}">
                            <text class="nc-iconfont text-[22rpx]  mt-[2rpx] text-[#fff] nc-icon-guanzhuV6xx" v-if="isFollow  == 0"></text>
                            <text class="text-[22rpx] text-[#fff]" :class="{'ml-[8rpx]': isFollow == 1 }">{{ isFollow  == 0 ? t('unFollowed') : t('followed') }}</text>
                        </view>
                    </view>
                </view>
            </view>
			<!-- tab页切换 -->
			<shop-tab v-if="shopInfoTabIsShow" :tab="diyComponent.tab" :select-index="currTabIndex" :is-show="currentBottomTab == 'shop_index' || currentBottomTab == ''" @confirm="tabConfirm"></shop-tab>
        </view>
		<!-- 展示微页面数据 -->
		<template v-if="currentSource == 'diy_page'">
			<view class="child-diy-template-wrap bg-index" :style="nextComponentMarginBottom">
				<diy-group :data="diyPageData" v-if="diyPageData.value && diyPageData.value.length"></diy-group>
				<diy-mall-empty v-else />
			</view>
		</template>
		<!-- 店铺信息tab页切换 -->
		<template v-if="currentSource == 'shop_tab'">
			<view class="child-diy-template-wrap bg-index" :style="nextComponentMarginBottom">
				<diy-mall-shop-coupon-list :component="couponData" v-if="currentBottomTab == 'shop_coupon_list'" />
				<view v-else-if="currentBottomTab == 'shop_goods_list'" class="p-[var(--top-m)]">
					<diy-mall-goods-list :component="goodsData"  />
				</view>
				<view v-else-if="currentBottomTab == 'shop_goods_category'">
					<diy-mall-goods-category :show-tab="diyComponent.tab.control"/>
				</view>
			</view>
		</template>
	</view>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch,onMounted, nextTick,getCurrentInstance } from 'vue'
import { img,  redirect,getToken,currRoute, distance } from '@/utils/common'
import { t } from '@/locale'
import useDiyStore from '@/app/stores/diy'
import { useLogin } from '@/hooks/useLogin'
import { getShopInfo,editShopCollect } from '@/addon/mall/api/shop';
import { getDiyInfo } from '@/app/api/diy';
import shopTab from './shop-tab.vue';
import useConfigStore from '@/stores/config'
import useSystemStore from '@/stores/system'
import { onPageScroll, onHide, onShow } from '@dcloudio/uni-app';
import diyMallShopCouponList from '@/addon/mall/components/diy/mall-shop-coupon-list/index.vue';  
import diyMallGoodsList from '@/addon/mall/components/diy/mall-goods-list/index.vue';
import diyMallGoodsCategory from '@/addon/mall/components/ns-shop-category/category.vue'
import diyMallEmpty from '@/addon/mall/components/diy/mall-empty/index.vue'

const instance = getCurrentInstance();
const props = defineProps(['component', 'index', 'pullDownRefreshCount', 'global', 'scrollBool','overallData']);

const systemStore = useSystemStore()

// 获取系统状态栏的高度
let systemInfo = uni.getSystemInfoSync();
let platform = systemInfo.platform;

const diyStore = useDiyStore();
const configStore = useConfigStore()

const diyComponent = computed(() => {
	if (diyStore.mode == 'decorate') {
		return diyStore.value[props.index];
	} else {
		return props.component;
	}
})

// 获取下个组件的外边距
const nextComponentMarginBottom = computed(() => {
	var style = '';
	if(diyComponent.value.bgGradient == true) {
		style += 'margin-top:' + ((props.overallData[props.index + 1].margin.top * 2) - 20) + 'rpx;';
	}
	return style;
})


// 导航栏内部盒子的样式
const navbarInnerArrowStyle = computed(() => {
	let style = '';
	// 导航栏宽度，如果在小程序下，导航栏宽度为胶囊的左边到屏幕左边的距离
	// #ifdef MP
	if (platform === 'ios') {
		// 苹果(iOS)设备
		style += 'font-weight: 700;';
	} else if (platform === 'android') {
	  // 安卓(Android)设备
	  
	}
	// #endif
	style += `background-color: ${diyComponent.value.search.bgColor}`
	return style;
})

// 返回上一页
const backToPrevious = () => {
	if(getCurrentPages().length > 1){
		uni.navigateBack({
			delta: 1
		});
	}else{
        redirect({
			url: '/app/pages/index/index',
            mode: 'reLaunch'
		});
	}
}

const warpCss = computed(() => {
	var style = '';
	if(diyComponent.value.componentStartBgColor) {
		if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${diyComponent.value.componentGradientAngle},${diyComponent.value.componentStartBgColor},${diyComponent.value.componentEndBgColor});`;
		else style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
	}

	if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
	if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
	if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
	if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';

	return style;
})

watch(
	() => props.pullDownRefreshCount,
	(newValue, oldValue) => {
		// 处理下拉刷新业务
	}
)

const moduleHeight:any = ref('')

const setModuleLocation = ()=> {
	nextTick(() => {
		setTimeout(()=>{
			const query = uni.createSelectorQuery().in(instance);
			query.select('.fixed-wrap').boundingClientRect((data:any) => {
				moduleHeight.value = (data.height || 0) + 'px';
			}).exec();
		})
	})
}

const fixedStyleBg = ref(false);
const fixedStyle = computed(()=>{
	if (diyStore.mode == 'decorate') return '';
	var style = '';
	// #ifdef H5
	if(props.global.topStatusBar.isShow && props.global.topStatusBar.style == 'style-4') {
		style += 'top:' + diyStore.topTabarHeight + 'px;';
	}
	// #endif
	
	if(diyComponent.value.positionWay == 'fixed') {
		if (props.scrollBool != -1) {
			style += 'position: fixed;z-index: 10;left: 0;right: 0;';
		}

		// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
		menuButtonInfo = uni.getMenuButtonBoundingClientRect();
		if(props.global.topStatusBar.isShow) {
			style += 'top:' + diyStore.topTabarHeight + 'px;';
		}
		// #endif
		
		fixedStyleBg.value = false;
		if (props.scrollBool == 1) {
			let str = diyComponent.value.fixedBgColor || '';
			let arr = str.split(',');
			let num = diyComponent.value.fixedBgColor ? parseInt(arr[arr.length-1]) : 0;
			if (!diyComponent.value.fixedBgColor || num == 0) {
				fixedStyleBg.value = true;
			} else {
				fixedStyleBg.value = false;
				style += 'background-color:' + diyComponent.value.fixedBgColor + ';';
			}
		}
	}
	return style;
})

const isShowSearchPlaceholder = computed(()=> {
	let flag = true;
	for (let i = 0; i < diyComponent.value.search.hotWord.list.length; i++) {
		let item = diyComponent.value.search.hotWord.list[i];
		if (item.text) {
			flag = false;
			break;
		}
	}
	return flag;
})

// 背景渐变
const bgImgBoxStyle = computed(()=>{
	var style = '';
	let str = props.global.pageStartBgColor ? props.global.pageStartBgColor : 'rgba(255,255,255,1)';
	if (diyComponent.value.bgGradient == true) {
		style += `background: linear-gradient(180deg, rgba(0, 0, 0, ${(diyComponent.value.bgOpacity / 100)}) 0%, rgba(0, 0, 0, 0.5) 70%, ${str} 100%)`;
	} else {
		style += `background: rgba(0, 0, 0, ${diyComponent.value.bgOpacity / 100})`;
	}
	return style;
});


let menuButtonInfo: any = {};
const navbarInnerStyle = ref('')

onMounted(() => {
	refresh();
	// 装修模式下刷新
	if (diyStore.mode == 'decorate') {
		watch(
			() => diyComponent.value,
			(newValue, oldValue) => {
				if (newValue && newValue.componentName == 'MallShopInfo') {
					refresh();
				}
			}
		)
	}

		// 如果是小程序，获取右上角胶囊的尺寸信息，避免导航栏右侧内容与胶囊重叠(支付宝小程序非本API，尚未兼容)
	// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
	if(diyComponent.value.positionWay == 'fixed') {
		menuButtonInfo = uni.getMenuButtonBoundingClientRect();
		// 导航栏内部盒子的样式
		// 导航栏宽度，如果在小程序下，导航栏宽度为胶囊的左边到屏幕左边的距离
		// 如果是各家小程序，导航栏内部的宽度需要减少右边胶囊的宽度
		if(props.global.topStatusBar.isShow == false) {
			let rightButtonWidth = menuButtonInfo.width ? menuButtonInfo.width * 2 + 'rpx' : '70rpx';
			navbarInnerStyle.value += 'padding-right:calc(' + rightButtonWidth + ' + 30rpx);';
			navbarInnerStyle.value += 'padding-top:' + menuButtonInfo.top + 'px;';
		}
		
	}
	// #endif
});

const shopInfo:any = ref({})
const refresh = () => {
	setModuleLocation();
	tabConfirm({ source: 'home' },-1)
	// 装修模式下设置默认图
	if (diyStore.mode == 'decorate') {
		shopInfo.value = {
			siteId: 0,
			icon: '',
			siteName: '周六福珠宝旗舰店',
			typeName: '直营店铺',
			rate: 5,
			isFollow: 0,
			follow_number: 0
		};
	} else {
		getShopInfoFn();
	}
}

const getShopInfoFn = ()=> {
	if (diyStore.siteId) {
		getShopInfo(diyStore.siteId,{ position: systemStore.diyAddressInfo }).then((res: any) => {
			if (res.data) {
				shopInfo.value = {
					siteId: res.data.site_id,
					icon: res.data.front_end_logo ? res.data.front_end_logo : res.data.icon,
					siteName: res.data.site_name,
					typeName: res.data.is_self == 1 ? '自营店铺' : '直营店铺',
					follow_number: res.data.follow_number,
					rate: 5,
					isFollow: res.data.member_info?.is_follow,
					distance: res.data.distance
				}
				isFollow.value = shopInfo.value.isFollow
			}
		})
	}
}

const toDetail = ()=>{
	redirect({ url: '/addon/mall/pages/shop/detail?site_id=' + shopInfo.value.siteId })
}

let isFollow:any = ref(0);

// 关注店铺
const followShop = ()=> {
	if (diyStore.mode == 'decorate') return;

	// 检测是否登录
	if (!getToken()) {
		// TODO 获取当前路由
		useLogin().setLoginBack({ url: '/' + currRoute() + '?site_id=' + diyStore.siteId })
		return;
	}

	editShopCollect({
		site_id: shopInfo.value.siteId,
		is_follow: isFollow.value == 0 ? 1 : 0
	}).then(res => {
		if (isFollow.value) {
			uni.showToast({
				title: '取消关注',
				icon: 'none'
			});
			shopInfo.value.follow_number--
		} else {
			uni.showToast({
				title: '关注成功',
				icon: 'none'
			});
			shopInfo.value.follow_number++
		}
		isFollow.value = isFollow.value == 0 ? 1 : 0
	})
}
// 跳转查询商品
const toLink = () =>{
	if(diyStore.siteId){
		redirect({url:`/addon/mall/pages/shop/search?site_id=${diyStore.siteId}`})
	}
}

// 轮播指示器
let isShowDots = ref(true)
// #ifdef H5
isShowDots.value = true;
// #endif

// #ifdef MP-WEIXIN
isShowDots.value = false;
// #endif

/******************************* 存储滚动值-start ***********************/
// 键名和组件名一致即可
let componentsScrollVal = uni.getStorageSync('componentsScrollValGroup')
if(componentsScrollVal && (typeof componentsScrollVal == "object")){
	componentsScrollVal.MallShopInfo = 20
	uni.setStorageSync('componentsScrollValGroup', componentsScrollVal);
}else{
	let obj = {
		MallShopInfo: 20
	}
	uni.setStorageSync('componentsScrollValGroup', obj);
}
/******************************* 存储滚动值-end ***********************/ 

/* tab - start */
const shopInfoTabIsShow = ref(true)
const currentSource = ref('')
const currTabIndex = ref(-1)
const tabConfirm = (data:any, index:any)=> {
    currTabIndex.value = index;
	currentSource.value = data.source;
	if (data.source == 'home') {
		// 首页
		diyStore.topFixedStatus = 'home'
		configStore.shop_source = 'home'
	} else if (data.source == 'diy_page') {
		// 查询微页面数据
		diyStore.topFixedStatus = 'diy'
		configStore.shop_source = 'diy_page'
		getDiyInfoFn(data.diy_id);
	}
}


const diyPageData = reactive({
    pageMode: 'diy',
    title: '',
    global: {},
    value: []
});

const getDiyInfoFn = (id: any) => {
    if (!id) {
        diyPageData.pageMode = 'diy';
        diyPageData.title = '';
        diyPageData.global = {};
        diyPageData.value = [];
        return;
    }
    getDiyInfo({
        id,
		site_id: diyStore.siteId
    }).then((res: any) => {
        if (res.data.value) {
            let data = res.data;
            diyPageData.pageMode = data.mode;
            diyPageData.title = data.title;

            let sources = JSON.parse(data.value);
            diyPageData.global = sources.global;
            diyPageData.global.topStatusBar.isShow = false; // 子页面不需要展示顶部导航栏
            diyPageData.global.bottomTabBar.isShow = false; // 子页面不需要展示底部导航
            diyPageData.value = sources.value;

            diyPageData.value.forEach((item: any, index) => {
                item.pageStyle = '';
                if (item.pageStartBgColor) {
                    if (item.pageStartBgColor && item.pageEndBgColor) item.pageStyle += `background:linear-gradient(${ item.pageGradientAngle },${ item.pageStartBgColor },${ item.pageEndBgColor });`;
                    else item.pageStyle += 'background-color:' + item.pageStartBgColor + ';';
                }

                if (item.margin) {
                    if (item.margin.top > 0) {
                        item.pageStyle += 'padding-top:' + item.margin.top * 2 + 'rpx' + ';';
                    }
                    item.pageStyle += 'padding-bottom:' + item.margin.bottom * 2 + 'rpx' + ';';
                    item.pageStyle += 'padding-right:' + item.margin.both * 2 + 'rpx' + ';';
                    item.pageStyle += 'padding-left:' + item.margin.both * 2 + 'rpx' + ';';
                }
            });
            uni.setNavigationBarTitle({
                title: diyPageData.title
            })
        }
    });
}
onPageScroll((e) => {
	if(diyComponent.value && diyComponent.value.positionWay == 'fixed') {
		if(e.scrollTop >= 76){
			shopInfoTabIsShow.value = false
		}else{
			shopInfoTabIsShow.value = true
		}
	}
})
/* tab - end */

/******************************* 底部导航切换-start ***********************/ 
const currentBottomTab = ref('')
watch(() => configStore.shop_tabbar, (newValue, oldValue) => {
	if(newValue){
		currentSource.value = 'shop_tab'
	}

	if(newValue == 'shop_index'){
		currentBottomTab.value = 'shop_index'
		tabConfirm({ source: 'home' }, -1)
	} else if(newValue == 'shop_goods_category') {
		diyStore.topFixedStatus = 'diy'
		currentBottomTab.value ='shop_goods_category'
	} else if(newValue == 'shop_goods_list')  {
		diyStore.topFixedStatus = 'diy'
		currentBottomTab.value ='shop_goods_list'
	} else if(newValue =='shop_coupon_list')  {
		diyStore.topFixedStatus = 'diy'
		currentBottomTab.value ='shop_coupon_list'
	}
})
const goodsData = reactive({
	style: 'style-1',
	source: 'all',
	num: 30,
	topElementRounded: 12,
    bottomElementRounded: 12,
	imgElementRounded: 10,
    margin: {
        both: 10,
        bottom: 0,
        top: 0
    },
    priceStyle: {
        mainColor: "#FF4142",
        control: true
    },
    goodsNameStyle: {
        color: "#303133",
        control: true,
        fontWeight: "normal"
    },
    saleStyle: {
        color: "#999",
        control: true
    },
    labelStyle: {
        isShow: true,
        control: true
    },
	distanceStyle: {
        control: false
    },
    position:{
        control: false
    },
    localDelivery:{
        control: false
    },
    btnStyle: {
        fontWeight: false,
        padding: 0,
        aroundRadius: 25,
        textColor: "#fff",
        startBgColor: '#FF4142',
        endBgColor: '#FF4142',
        style: 'nc-icon-gouwuche1',
        control: true
    }
})

const couponData = reactive({
	num: 30
})
/******************************* 底部导航切换-end ***********************/  
</script>

<style lang="scss" scoped>
	.shop-info-search-wrap{
		.bg-img{
			position: absolute;
			width: 100%;
			top: 0;
			bottom: 0;
			z-index: 0;
			-webkit-filter: blur(0);
			filter: blur(0);
			overflow: hidden;
		}
		.bg-img-one{
			position: absolute;
			width: 100%;
			top: 0;
			bottom: 0;
			z-index: 0;
			-webkit-filter: blur(0);
			filter: blur(0);
			overflow: hidden;
			uni-image, image{
				-webkit-filter: blur(0px);
				filter: blur(0px);
				-webkit-transform: scale(1);
				transform: scale(1);
			}
			.bg-img-box{
				position: absolute;
				top: 0;
				right: 0;
				bottom: 0;
				left: 0;
			}
		}
	}
	
	.fixed-wrap {
		&.fixed {
			position: fixed;
			left: 0;
			right: 0;
			top: 0;
			z-index: 991;
			transition: background .3s;
		}
	}

	.diy-search-wrap{
		display: flex;
		position: relative;
		align-items: center;
		padding: 20rpx;
		.img-wrap{
			display: flex;
			align-items: center;
			justify-content: center;
			width: 140rpx;
			height: 60rpx;
			margin-right: 30rpx;
			image{
				width: 100%;
				height:100%;
			}
		}
		.search-left{
			border-radius: 50rpx;
			background-color: rgba(255,255,255,.2);
			margin-right: 20rpx;
		}
		.search-content {
			display: flex;
			align-items: center;
			padding-left: 32rpx;
			padding-right: 6rpx;
			border-radius: 50rpx;
			background-color: rgba(255, 255, 255, .2);
			flex: 1;
			position: relative;

			.input-content, .uni-input {
				box-sizing: border-box;
				display: block;
				height: 64rpx;
				line-height: 64rpx;
				width: 100%;
				padding-right: 20rpx;
				color: #fff;
				background: none;
			}
			.iconfont {
				font-size: 30rpx;
				font-weight: bold;
				color: #fff;
			}
			.swiper-wrap{
				position: absolute;
				width:80%;
				height: 64rpx;
				line-height: 64rpx;
				color:#fff;
			}
		}

	}
    .swiper-animation{
		transform: scale(0.96, 0.96);
		transition-duration: 0.3s;
		transition-timing-function: ease;
	}
	// 轮播指示器
	.swiper-right :deep(.uni-swiper-dots-horizontal) {
		right: 80rpx;
		display: flex;
		justify-content: flex-end;
		transform: translate(0);
	}
	.swiper-left :deep(.uni-swiper-dots-horizontal) {
		left: 80rpx;
		transform: translate(0);
	}
	.swiper :deep(.uni-swiper-dot) {
		width: 12rpx;
		height: 12rpx;
	}
	.swiper.ns-indicator-dots :deep(.uni-swiper-dot) {
		width: 18rpx;
		height: 6rpx;
		border-radius: 4rpx;
	}
	.swiper.ns-indicator-dots :deep(.uni-swiper-dot-active) {
		width: 36rpx;
	}
	.swiper-dot-box {
		position: absolute;
		bottom: 20rpx;
		width: 100%;
		display: flex;
		align-items: center;
		justify-content: center;
		padding: 0 80rpx 8rpx;
		box-sizing: border-box;
	
		&.swiper-left {
			justify-content: flex-start;
		}
	
		&.swiper-right {
			justify-content: flex-end;
		}
	
		.swiper-dot {
			background-color: #b2b2b2;
			width: 12rpx;
			border-radius: 50%;
			height: 12rpx;
			margin: 8rpx;
		}
	
		&.straightLine {
			.swiper-dot {
				width: 18rpx;
				height: 6rpx;
				border-radius: 4rpx;
	
				&.active {
					width: 36rpx;
				}
			}
		}
	}
    .shop-type{
        background: linear-gradient( 90deg, #FFD6B5 0%, #FFBA8C 100%);
    }
	.search-box {
		&::after {
			content: '';
			width: 20rpx;
			height: 20rpx;
			background-color: #fff;
			position: absolute;
			left: 50%;
			top: -10rpx;
			transform: translateX(-50%) rotate(45deg);
		}
	}
	primary-btn-bg {
		background: linear-gradient(94deg, #FB7939 0%, #FF4142 99%), #FF4142;
	}
</style>
