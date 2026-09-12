<template>
	<view :style="warpCss" class="shop-info-search-wrap" :class="[diyComponent.style]" v-if="Object.keys(shopInfo).length">
		<view class="relative" v-if="diyComponent.style == 'style-1'">
            <view class="bg-img" :class="{'bottom200': diyComponent.bgGradient == true}">
				<video v-if="diyComponent.bgVideo && diyComponent.curBgImg == diyComponent.bgVideo" id="bgVideo" class="w-full h-full" :src="img(diyComponent.bgVideo)" objectFit="cover" :muted="true" :autoplay="true" :loop="true" :controls="false" :show-center-play-btn="false" ref="videoRef"></video>
				<image v-else-if="diyComponent.curBgImg" :src="img(diyComponent.curBgImg)" mode="aspectFill" class="w-full h-full"/>
				<view v-else class="w-full h-full bg-[#ccc]"></view>
				<view class="bg-img-box" :style="bgImgBoxStyle"></view>
			</view>
			
            <view class="fixed-wrap" :style="fixedStyle">
				<!-- 搜索 -->
				<view class="diy-search-wrap relative z-10"  :style="!props.global.topStatusBar.isShow ? navbarInnerStyle : ''">
					<view class="search-left flex-center h-[60rpx] rounded-[30rpx] box-border arrow-left pl-[12rpx] pr-[20rpx] leading-[1]" :style="navbarInnerArrowStyle">
						<text class="nc-iconfont text-[#fff] nc-icon-zuoV6xx text-[18px]" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }" @click.stop="backToPrevious()"></text>
						<text class="w-[2rpx] h-[26rpx] bg-[#fff] ml-[8rpx] mr-[16rpx]" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }"></text>
						<text class="iconfont iconshouyev6mm text-[#fff] text-[16px]" @click.stop="redirect({ url: '/app/pages/index/index', mode: 'reLaunch' })" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }"></text>
					</view>

					<view class="search-content" @click.stop="toLink" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ?  diyComponent.fixedSearch.bgColor : diyComponent.search.bgColor }">
						<text class="input-content text-[#fff] text-[24rpx] leading-[68rpx]" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }">{{isShowSearchPlaceholder ? diyComponent.search.text : ''}}</text>
						<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 w-[80rpx] h-[52rpx] flex items-center justify-center rounded-[50rpx] text-[28rpx] text-[#fff]" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.btnBgColor : diyComponent.search.btnBgColor, color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.btnColor  : diyComponent.search.btnColor }"></text>
						<swiper class="swiper-wrap" :interval="diyComponent.search.hotWord.interval * 1000" autoplay="true" vertical="true" circular="true" v-if="!isShowSearchPlaceholder">
							<swiper-item class="swiper-item" v-for="(item) in diyComponent.search.hotWord.list" :key="item.id">
								<view class="leading-[64rpx] text-[24rpx]" :style="{color:  props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }">{{ item.text }}</view>
							</swiper-item>
						</swiper>
					</view>
					<view class="w-[56rpx] h-[56rpx] flex items-center justify-center rounded-[50%] ml-[16rpx]" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ?  diyComponent.fixedSearch.bgColor : diyComponent.search.bgColor }" v-if="shopInfo.qrcode_shop_config.is_show" @click="openShareFn">
						<text class="nc-iconfont nc-icon-fukuanmaV6xx" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }"></text>
					</view>
				</view>

				<view class="bg-img search-bg-img" v-if="fixedStyleBg">
					<image v-if="diyComponent.curBgImg"
                        :src="img(diyComponent.curBgImg)" mode="widthFix" class="w-full h-full"
                        :show-menu-by-longpress="true" />
                    <view v-else class="w-full h-full bg-[#ccc]"></view>
				</view>
			</view>
            <!-- 解决fixed定位后导航栏塌陷的问题 -->
			<template v-if="diyStore.mode != 'decorate'">
				<view v-if="diyComponent.positionWay == 'fixed' && props.scrollBool != undefined && props.scrollBool != -1" class="u-navbar-placeholder" :style="{ width: '100%', paddingTop: moduleHeight }"></view>
			</template>

			<!-- 店铺信息 -->
			<view class="relative mt-[200rpx] mx-[20rpx] rounded-[24rpx] box-border">
				<view class="flex items-center" @click="toDetail">
					<view class="flex items-center">
						<view class="text-[30rpx] font-600 truncate max-w-[260rpx]" :style="{ color : diyComponent.shop.textColor }">{{ shopInfo.siteName }}</view>
						<view class="flex items-center text-[24rpx] ml-[8rpx]" :style="{ color : diyComponent.shop.textColor }">{{shopInfo.follow_number}}人关注</view>
					</view>
					<view class="flex-1 flex justify-end">
						<view @click.stop="followShop" class="follow-box  flex-center box-border" :class="{'bg-white':  isFollow == 0,'border': isFollow == 1}">
							<text class="follow-self text-primary" :class="{'active': isFollow == 1 }">{{ isFollow  == 0 ? t('unFollowed') : t('followed') }}</text>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 轮播图 -->
			<scroll-view :scroll-x="true" class="px-[20rpx] mt-[20rpx] box-border" v-if="diyComponent.bgImgStyle == 'custom' && diyComponent.bgImgArr">
				<view class="w-full flex items-center gap-[10rpx] mb-[14rpx]">
					<view v-if="diyComponent.bgVideo" class="flex-shrink-0 w-[140rpx] h-[88rpx] rounded-[8rpx] overflow-hidden items-center justify-center relative box-border" @click.stop="changeItemBg(diyComponent.bgVideo)">
						<video :src="img(diyComponent.bgVideo)" :controls="false" :autoplay="false" :loop="false" :muted="true" objectFit="cover" class="w-full h-full video-box"></video>
						<view class="absolute left-[0] right-[0] bottom-[0] top-[0] z-[1000]"></view>
					</view>
					<view v-for="(item,index) in diyComponent.bgImgArr.split(',')" :key="index" class="flex-shrink-0 w-[140rpx] h-[88rpx] rounded-[8rpx] overflow-hidden inline-flex items-center justify-center relative box-border" @click="changeItemBg(item)">
						<image :src="img(item)" class="w-[140rpx] h-[88rpx] align-middle" mode="aspectFill"></image>
					</view>
				</view>
			</scroll-view>
        </view>
        <view class="relative" v-if="diyComponent.style == 'style-2'">
            <view class="bg-img" :class="{'bottom200': diyComponent.bgGradient == true}">
				<image v-if="diyComponent.swiper.control && diyComponent.swiper.list && diyComponent.swiper.list[swiperIndex]?.imageUrl"
                    :src="img(diyComponent.swiper.list[swiperIndex]?.imageUrl)" mode="scaleToFill" class="w-full h-full"
                    :show-menu-by-longpress="true" />
				<view v-else class="w-full h-full bg-[#ccc]"></view>
				<view class="bg-img-box" :style="bgImgBoxStyle"></view>
			</view>
            <view class="fixed-wrap" :style="fixedStyle">
				<!-- 搜索 -->
				<view class="diy-search-wrap relative z-10" :style="!props.global.topStatusBar.isShow ? navbarInnerStyle : ''">
					<view class="search-left flex-center h-[60rpx] rounded-[30rpx] box-border arrow-left pl-[12rpx] pr-[20rpx] leading-[1]" :style="navbarInnerArrowStyle">
						<text class="nc-iconfont text-[#fff] nc-icon-zuoV6xx text-[18px]" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }" @click.stop="backToPrevious()"></text>
						<text class="w-[2rpx] h-[26rpx] bg-[#fff] ml-[8rpx] mr-[16rpx]" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }"></text>
						<text class="iconfont iconshouyev6mm text-[#fff] text-[16px]" @click.stop="redirect({ url: '/app/pages/index/index', mode: 'reLaunch' })" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }"></text>
					</view>

					<view class="search-content" @click.stop="toLink" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ?  diyComponent.fixedSearch.bgColor : diyComponent.search.bgColor }">
						<text class="input-content text-[#fff] text-[24rpx] leading-[68rpx]" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }">{{isShowSearchPlaceholder ? diyComponent.search.text : ''}}</text>
						<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 w-[80rpx] h-[52rpx] flex items-center justify-center rounded-[50rpx] text-[28rpx] text-[#fff]" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.btnBgColor : diyComponent.search.btnBgColor, color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.btnColor  : diyComponent.search.btnColor }"></text>
						<swiper class="swiper-wrap" :interval="diyComponent.search.hotWord.interval * 1000" autoplay="true" vertical="true" circular="true" v-if="!isShowSearchPlaceholder">
							<swiper-item class="swiper-item" v-for="(item) in diyComponent.search.hotWord.list" :key="item.id">
								<view class="leading-[64rpx] text-[24rpx]" :style="{color:  props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }">{{ item.text }}</view>
							</swiper-item>
						</swiper>
					</view>
					<view class="w-[56rpx] h-[56rpx] flex items-center justify-center rounded-[50%] ml-[16rpx]" :style="{backgroundColor: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ?  diyComponent.fixedSearch.bgColor : diyComponent.search.bgColor }" v-if="shopInfo.qrcode_shop_config.is_show" @click="openShareFn">
						<text class="nc-iconfont nc-icon-fukuanmaV6xx" :style="{color: props.scrollBool == 1 && diyComponent.positionWay == 'fixed'  ? diyComponent.fixedSearch.color : diyComponent.search.color }"></text>
					</view>
				</view>

				<view class="bg-img" v-if="fixedStyleBg">
					<image v-if="diyComponent.swiper.control && diyComponent.swiper.list && diyComponent.swiper.list[swiperIndex].imageUrl"
                        :src="img(diyComponent.swiper.list[swiperIndex].imageUrl)" mode="widthFix" class="w-full h-full"
                        :show-menu-by-longpress="true" />
                    <view v-else class="w-full h-full bg-[#ccc]"></view>
				</view>
			</view>
            <!-- 解决fixed定位后导航栏塌陷的问题 -->
			<template v-if="diyStore.mode != 'decorate'">
				<view v-if="diyComponent.positionWay == 'fixed' && props.scrollBool != undefined && props.scrollBool != -1" class="u-navbar-placeholder" :style="{ width: '100%', paddingTop: moduleHeight }"></view>
			</template>

             <!-- 店铺信息 -->
             <view class="diy-mall-shop-info relative px-[24rpx] py-[20rpx] box-border min-h-[120rpx]" :style="fixedShopInfoStyle">
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
                        <view @click.stop="followShop" class="follow-style2 flex-center box-border" :class="{'primary-btn-bg':  isFollow == 0,'border': isFollow == 1}">
                            <text class="nc-iconfont text-[22rpx]  mt-[2rpx] text-[#fff] nc-icon-guanzhuV6xx" v-if="isFollow  == 0"></text>
                            <text class="font22 text-white" :class="{'ml8': isFollow == 1 }">{{ isFollow  == 0 ? t('unFollowed') : t('followed') }}</text>
                        </view>
                    </view>
                </view>
            </view>
			
			<!-- 轮播图 -->
            <view class="relative" :class="{'mx20': swiperStyleBool && diyComponent.swiper.swiperStyle != 'style-3', 'swiper-style-3': diyComponent.swiper.swiperStyle == 'style-3'}">
                <swiper v-if="diyComponent.swiper.control" class="swiper" :style="{ height: imgHeight }" autoplay="true"
                        circular="true" @change="swiperChange"
                        :class="{ 'swiper-left': diyComponent.swiper.indicatorAlign == 'left', 'swiper-right': diyComponent.swiper.indicatorAlign == 'right', 'ns-indicator-dots': diyComponent.swiper.indicatorStyle == 'style-2', 'ns-indicator-dots-three': diyComponent.swiper.indicatorStyle == 'style-3' }"
                        :previous-margin="swiperStyleBool ? 0 : '34rpx'" :next-margin="swiperStyleBool ? 0 : '34rpx'"
                        :interval="diyComponent.swiper.interval * 1000" :indicator-dots="isShowDots"
                        :indicator-color="diyComponent.swiper.indicatorColor"
                        :indicator-active-color="diyComponent.swiper.indicatorActiveColor">
                    <swiper-item class="swiper-item" v-for="(item,index) in diyComponent.swiper.list" :key="item.id" :style="swiperWarpCss">
                        <view @click="diyStore.toRedirect(item.link)">
                            <view class="item" :style="{height: imgHeight}">
                                <image v-if="item.imageUrl" :src="img(item.imageUrl)" mode="scaleToFill" :style="swiperWarpCss"
                                       :class="['w-full h-full',{'swiper-animation': (swiperIndex != index && diyComponent.swiper.indicatorStyle != 'style-3')}]"
                                       :show-menu-by-longpress="true" />
                                <image v-else :src="img('static/resource/images/diy/figure.png')" :style="swiperWarpCss"
                                       mode="scaleToFill"
                                       :class="['w-full h-full',{'swiper-animation': (swiperIndex != index && diyComponent.swiper.indicatorStyle != 'style-3')}]"
                                       :show-menu-by-longpress="true" />
                            </view>
                        </view>
                    </swiper-item>
                </swiper>
                <!-- #ifdef MP-WEIXIN -->
                <view v-if="diyComponent.swiper.list.length > 1" :class="[
						'swiper-dot-box',
						{ 'straightLine': diyComponent.swiper.indicatorStyle == 'style-2' },
						{ 'straightLineStyle2': diyComponent.swiper.indicatorStyle == 'style-3' },
						{ 'swiper-left': diyComponent.swiper.indicatorAlign == 'left' },
						{ 'swiper-right': diyComponent.swiper.indicatorAlign == 'right' }
					]">
                    <view v-for="(numItem, numIndex) in diyComponent.swiper.list" :key="numIndex"
                          :class="['swiper-dot', { active: numIndex == swiperIndex }]"
                          :style="[numIndex == swiperIndex ? { backgroundColor: diyComponent.swiper.indicatorActiveColor } : { backgroundColor: diyComponent.swiper.indicatorColor }]"></view>
                </view>
                <!-- #endif -->
            </view>
        </view>
		<share-poster ref="sharePosterRef" posterType="mall_shop"  :posterParam="posterParam" :copyUrl="copyUrl" :copyUrlParam="copyUrlParam" />
	</view>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch,onMounted, nextTick,getCurrentInstance } from 'vue'
import { img,  redirect,getToken,currRoute, distance, getTopFixedStatusName } from '@/utils/common'
import { t } from '@/locale'
import useDiyStore from '@/app/stores/diy'
import { useLogin } from '@/hooks/useLogin'
import { editShopCollect } from '@/app/api/shop';
import useConfigStore from '@/stores/config'
import useSystemStore from '@/stores/system'
import useShopStore from '@/addon/mall/stores/shop';

const instance = getCurrentInstance();
const props = defineProps(['component', 'index', 'pullDownRefreshCount', 'global', 'scrollBool','overallData']);

const systemStore = useSystemStore()
const shopStore = useShopStore()

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
	if (props.scrollBool == 1) {
		style += `background-color: ${diyComponent.value.fixedSearch.bgColor};`
	} else {
		style += `background-color: ${diyComponent.value.search.bgColor};`
	}
	return style;
})

// 返回上一页
const backToPrevious = () => {
	if(getCurrentPages().length > 1){
		uni.navigateBack({
			delta: 1,
			success: () => {}
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
			if (diyComponent.value.swiper.swiperStyle != 'style-3') {
				const query = uni.createSelectorQuery().in(instance);
				query.select('.fixed-wrap').boundingClientRect((data:any) => {
					moduleHeight.value = (data?.height || 0) + 'px';
				}).exec();
			} else {
				moduleHeight.value = ''
			}
			
		})
	})
}

const fixedStyleBg = ref(false);
const fixedStyle = computed(()=>{
	let style = '';
	if (diyComponent.value.style == 'style-1' && diyComponent.value.swiper && diyComponent.value.swiper.swiperStyle == 'style-3') {
		style += 'position: absolute;z-index: 99;left: 0;right: 0;';
	}
	// #ifdef H5
	if(props.global.topStatusBar.isShow && props.global.topStatusBar.style == 'style-4') {
		style += 'top:' + diyStore.topTabarHeight + 'px;';
	}
	// #endif

	if (diyStore.mode == 'decorate') return style;
	
	if(diyComponent.value.positionWay == 'fixed') {
		if (props.scrollBool != undefined && props.scrollBool != -1) {
			style += 'position: fixed;z-index: 99;top: 0;left: 0;right: 0;';
		}

		if (props.scrollBool == 1 || props.scrollBool == 2) {
			// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
			if (props.global.topStatusBar.isShow) {
                style += 'top:' + diyStore.topTabarHeight + 'px;';
            }
			// #endif
			// #ifdef H5
            if (props.global.topStatusBar.isShow && props.global.topStatusBar.style == 'style-4') {
                style += 'top:' + diyStore.topTabarHeight + 'px;';
            }
            // #endif
		}
		
		
		fixedStyleBg.value = false;
		if (props.scrollBool == 1) {
			let str = diyComponent.value.fixedBgColor || '';
			let arr = str.split(',');
			let num = diyComponent.value.fixedBgColor ? parseInt(arr[arr.length-1]) : 0;
			if (!diyComponent.value.fixedBgColor || num == 0) {
				fixedStyleBg.value = true;
				// 风格二时，选中视频，搜索框滚动到顶部的背景色默认为白色
				if(diyComponent.value.style == 'style-2' && diyComponent.value.curBgImg == diyComponent.value.bgVideo){
					fixedStyleBg.value = false;
					style += 'background-color: #fff;';
				}
			} else {
				fixedStyleBg.value = false;
				style += 'background-color:' + diyComponent.value.fixedBgColor + ';';
			}
		}
	}
	return style;
})


const fixedShopInfoStyle = computed(()=>{
	let style = '';
	if (diyComponent.value.swiper && diyComponent.value.swiper.swiperStyle == 'style-3') {
		// #ifdef H5
		style += `position: absolute;z-index: 10;top: 104rpx; left: 0;right: 0;`;
		// #endif
		// #ifdef MP-WEIXIN
        style += `position: absolute;z-index: 10;top: ${104 + (Number(systemStore.menuButtonInfo.top) - 20) * 2 }rpx; left: 0;right: 0;`;
		// #endif
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
const bgImgBoxStyle = computed(() => {
    let style = '';
    let str = props.global.pageStartBgColor ? props.global.pageStartBgColor : 'rgba(255,255,255,1)';
    if (str.indexOf('(') > -1) {
        let arr = str.split('(')[1].split(')')[0].split(',');
        if (diyComponent.value.bgGradient == true) {
            style += `background: linear-gradient(rgba(${ arr[0] }, ${ arr[1] }, ${ arr[2] }, 0) 65%, rgba(${ arr[0] }, ${ arr[1] }, ${ arr[2] }, 0.6) 70%, rgba(${ arr[0] }, ${ arr[1] }, ${ arr[2] }, 0.85) 80%, rgba(${ arr[0] }, ${ arr[1] }, ${ arr[2] }, 0.95) 90%,  rgb(${ arr[0] }, ${ arr[1] }, ${ arr[2] }, 1) 100%);`;
        }
    } else {
        style += `background: (${ str });`;
    }
    return style;
});

// 轮播样式二
const swiperStyleBool = computed(() => {
    const style = diyComponent.value.swiper?.swiperStyle == 'style-2' || diyComponent.value.swiper?.swiperStyle == 'style-3' ? true : false;
    return style;
})

const imgHeight = computed(() => {
    return (diyComponent.value.swiper?.imageHeight * 2) + 'rpx';
})

const swiperIndex = ref(0);

const swiperChange = e => {
    swiperIndex.value = e.detail.current;
};

const swiperWarpCss = computed(() => {
    let style = '';
    if (diyComponent.value.swiper?.topRounded) style += 'border-top-left-radius:' + diyComponent.value.swiper?.topRounded * 2 + 'rpx;';
    if (diyComponent.value.swiper?.topRounded) style += 'border-top-right-radius:' + diyComponent.value.swiper?.topRounded * 2 + 'rpx;';
    if (diyComponent.value.swiper?.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.swiper?.bottomRounded * 2 + 'rpx;';
    if (diyComponent.value.swiper?.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.swiper?.bottomRounded * 2 + 'rpx;';
    return style;
})

const navbarInnerStyle = ref('')
// 如果是小程序，获取右上角胶囊的尺寸信息，避免导航栏右侧内容与胶囊重叠(支付宝小程序非本API，尚未兼容)
// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
// 导航栏内部盒子的样式
// 导航栏宽度，如果在小程序下，导航栏宽度为胶囊的左边到屏幕左边的距离
// 如果是各家小程序，导航栏内部的宽度需要减少右边胶囊的宽度
navbarInnerStyle.value += 'padding-top:' + systemStore.menuButtonInfo.top + 'px;';
// #endif

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
	// 判断让轮播指示器是否出现
    // #ifdef H5
    isShowDots = computed(() => {
        return diyComponent.value?.swiper?.list?.length > 1;
    });
    // #endif

	// 如果是小程序，获取右上角胶囊的尺寸信息，避免导航栏右侧内容与胶囊重叠(支付宝小程序非本API，尚未兼容)
	// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
	if(diyComponent.value.positionWay == 'fixed') {
		if (props.global.topStatusBar.isShow == false) {
            navbarInnerStyle.value = ''
            let rightButtonWidth = systemStore.menuButtonInfo.width ? systemStore.menuButtonInfo.width * 2 + 'rpx' : '70rpx';
            navbarInnerStyle.value += 'padding-right:calc(' + rightButtonWidth + ' + 30rpx);';
            navbarInnerStyle.value += 'padding-top:' + systemStore.menuButtonInfo.top + 'px;';
        } else if (props.global.topStatusBar) {
            navbarInnerStyle.value = ''
        }
	} else {
        let rightButtonWidth = systemStore.menuButtonInfo.width ? systemStore.menuButtonInfo.width * 2 + 'rpx' : '70rpx';
        navbarInnerStyle.value += 'padding-right:calc(' + rightButtonWidth + ' + 30rpx);';
    }
	// #endif
});

const shopInfo:any = ref({})
const videoRef = ref(null)
const refresh = () => {
	setModuleLocation();
	if(diyComponent.value.swiper && diyComponent.value.swiper.list){
		diyComponent.value.swiper.list.forEach((item: any) => {
			if (item.imageUrl == '') {
				item.imgWidth = 690;
				item.imgHeight = 330;
			}
		});
	}
	// 装修模式下设置默认图
	if (diyStore.mode == 'decorate') {
		shopInfo.value = {
			siteId: 0,
			icon: '',
			siteName: '周六福珠宝旗舰店',
			typeName: '直营店铺',
			rate: 5,
			isFollow: 0,
			follow_number: 0,
			qrcode_shop_config: {
				is_hidden_plat_index: 0,
				is_show: 1
			}
		};
	} else {
		setShopInfo();
	}
}

const setShopInfo = ()=> {
	if (diyStore.siteId) {
		if(shopStore.shopInfo[diyStore.siteId]){
			shopInfo.value = shopStore.shopInfo[diyStore.siteId];
			isFollow.value = shopInfo.value.isFollow;
		} else {
			shopStore.getShopInfoFn(diyStore.siteId,(data:any)=>{
				shopInfo.value = data
				isFollow.value = shopInfo.value.isFollow;
			})
		}
		copyUrlFn()
	}
}

const changeItemBg = (data: any) => {
	diyComponent.value.curBgImg = data;
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
		is_follow: isFollow.value == 0 ? 1 : 0,
		addon: 'mall'
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
		shopInfo.value.isFollow = isFollow.value
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


/************* 分享海报-start **************/
const sharePosterRef: any = ref(null);
const copyUrl = ref('/addon/mall/pages/site/index')
const copyUrlParam = ref('');
let posterParam: any = {};

// 分享海报链接
const copyUrlFn = ()=>{
	copyUrlParam.value = '?site_id=' + diyStore.siteId;
}

const openShareFn = ()=>{
    posterParam.site_id = diyStore.siteId;
	sharePosterRef.value.openShare()
}
/************* 分享海报-end **************/

</script>

<style lang="scss" scoped>
.video-box{
	:deep(.uni-video-cover-duration){
		margin-top: 10rpx;
		font-size: 24rpx;
	}
	:deep(.uni-video-cover-play-button){
		width: 40rpx;
		height: 40rpx;
	}
}
.shop-info-search-wrap{
	.bg-img {
        position: absolute;
        width: 100%;
        top: 0;
        bottom: 0;
        z-index: 0;
        -webkit-filter: blur(0);
        filter: blur(0);
        overflow: hidden;

        uni-image, image {
            -webkit-filter: blur(15px);
            filter: blur(15px);
            -webkit-transform: scale(2) translateY(15%);
            transform: scale(2) translateY(15%);
        }

        &.no-filter {
            uni-image, image {
                -webkit-filter: blur(0);
                filter: blur(0);
            }
        }

        .bg-img-box {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
        }
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
	&.style-1{
		.bg-img:not(.search-bg-img){
			uni-image, image {
				-webkit-filter: blur(0);
				filter: blur(0);
				-webkit-transform: scale(1) translateY(0);
				transform: scale(1) translateY(0);
			}
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
.diy-search-wrap-style2{
	display: flex;
	position: fixed;
	top: 0;
	left: 0;
	right: 0;
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
.info-gradient{
	background-image: linear-gradient(rgba(248, 248, 248, 0), rgba(248, 248, 248, 0) 65%, #f8f8f8);
}
.info-bg{
	width: 100%;
    height: 580rpx;
    background-size: cover;
    background-position: center center;
    background-repeat: no-repeat;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 0;
}

.swiper-animation {
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

// 指示器样式一
.swiper :deep(.uni-swiper-dot) {
    width: 12rpx;
    height: 12rpx;
}

// 指示器样式二
.swiper.ns-indicator-dots :deep(.uni-swiper-dot) {
    width: 18rpx;
    height: 6rpx;
    border-radius: 4rpx;
}

.swiper.ns-indicator-dots :deep(.uni-swiper-dot-active) {
    width: 36rpx;
}

// 指示器样式三
.swiper.ns-indicator-dots-three :deep(.uni-swiper-dot) {
    width: 8rpx;
    height: 8rpx !important;
    border-radius: 6rpx;
    margin-right: 14rpx;
}

.swiper.ns-indicator-dots-three :deep(.uni-swiper-dot):last-of-type {
    margin-right: 0;
}

.swiper.ns-indicator-dots-three :deep(.uni-swiper-dot-active) {
    width: 30rpx;
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
            border-radius: 6rpx;

            &.active {
                width: 36rpx;
            }
        }
    }

    &.straightLineStyle2 {
        .swiper-dot {
            width: 10rpx;
            height: 10rpx;
            border-radius: 6rpx;
            margin: 0 14rpx 0 0;

            &.last-of-type {
                margin-right: 0;
            }

            &.active {
                width: 30rpx;
            }
        }
    }
}

.swiper-style-3 {
    :deep(.uni-swiper-dots-horizontal) {
        bottom: 46rpx !important;
    }

    .swiper-dot-box {
        bottom: 38rpx !important;
    }
}
.primary-btn-bg {
	background: linear-gradient(94deg, #FB7939 0%, #FF4142 99%), #FF4142;
}
.follow-box{
	padding: 8rpx 18rpx;
	border-radius: 50rpx;
	&.border {
		border: 2rpx solid #fff;
	}
}
.follow-self{
	font-size: 22rpx;
	&.active{
		margin-left: 8rpx;
		color: #fff !important;
	}
}
.bottom200 {
	bottom: -200rpx !important;

}
.follow-style2{
	width: 120rpx;
	height: 50rpx;
	border-radius: 50rpx;
	&.border {
		border: 2rpx solid #fff;
	}
	.font22{
		font-size: 22rpx;
	}
	.ml8{
		margin-left: 8rpx;
	}
}
.mx20 {
	margin-left: 20rpx;
	margin-right: 20rpx;
}
</style>
