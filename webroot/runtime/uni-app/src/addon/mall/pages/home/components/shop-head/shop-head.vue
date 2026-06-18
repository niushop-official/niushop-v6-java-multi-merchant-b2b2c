<template>
	<view class="shop-head-wrap">
		<view class="relative">
			<view class="bg-img-one">
				<image  :src="img(shopInfo.background_h5)" mode="scaleToFill" @error="shopInfo.background_h5 = 'addon/mall/site_index_bg.jpg'" class="w-full h-[390rpx]" :show-menu-by-longpress="true"/>
				<view class="absolute left-0 right-0 bottom-0 top-0" :style="bgImgBoxStyle"></view>
			</view>
			<view class="fixed-wrap" :style="fixedStyle">
				<!-- 搜索 -->
				<view class="diy-search-wrap relative z-10" @click.stop="toLink" :style="navbarInnerStyle">
					<view class="search-left flex-center h-[60rpx] rounded-[30rpx] box-border arrow-left pl-[12rpx] pr-[20rpx] leading-[1]" :style="navbarInnerArrowStyle">
						<text class="nc-iconfont text-[#fff] nc-icon-zuoV6xx text-[18px]" @click.stop="backToPrevious()"></text>
						<text class="w-[2rpx] h-[26rpx] bg-[#fff] ml-[8rpx] mr-[16rpx]"></text>
						<text class="iconfont iconshouyev6mm text-[#fff] text-[16px]" @click.stop="redirect({ url: '/app/pages/index/index', mode: 'reLaunch' })"></text>
					</view>

					<view class="search-content" @click.stop="toLink" :style="{backgroundColor: 'rgba(255, 255, 255, 0.2);' }">
						<text class="input-content text-[#fff] text-[24rpx] leading-[68rpx]">请输入搜索关键词</text>
						<text class="nc-iconfont nc-icon-sousuo-duanV6xx1 w-[80rpx] h-[52rpx] flex items-center justify-center rounded-[50rpx] text-[28rpx] text-[#fff]"></text>
					</view>

				</view>

				<view class="bg-img" v-if="fixedStyleBg">
					<view class="absolute left-0 right-0 bottom-0 top-0  z-100" style="background-color: rgba(0, 0, 0, 0.7);"></view>
					<image  :src="img(shopInfo.background_h5)" mode="widthFix" @error="shopInfo.background_h5 = 'addon/mall/site_index_bg.jpg'" class="w-full h-full" :show-menu-by-longpress="true"/>
				</view>
			</view>

			<!-- 解决fixed定位后导航栏塌陷的问题 -->
			<view v-if="scrollBool != -1" class="u-navbar-placeholder" :style="{ width: '100%', paddingTop: moduleHeight }"></view>

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
                            <view class="text-[28rpx] truncate max-w-[230rpx] text-[#fff]">{{ shopInfo.siteName }}</view>
                            <view class="nc-iconfont nc-icon-youV6xx text-[28rpx] ml-[10rpx] mt-[4rpx] text-[#fff]"></view>
                        </view>
                        <view class="flex items-center mt-[14rpx]">
                            <text class="text-[#fff] text-[23rpx] font-bold leading-[1]">{{shopInfo.follow_number}}</text>
							<text class="text-[#fff] text-[22rpx]">人关注</text>
							<text v-if="shopInfo.distance" class="text-[22rpx] text-[#fff] ml-[16rpx]">{{ distance(shopInfo.distance) }}</text>
                        </view>
                    </view>
                    <view class="flex-1 flex justify-end">
                        <view @click.stop="followShop" class="w-[120rpx] h-[50rpx] rounded-[50rpx] flex-center box-border" :class="{'primary-btn-bg':  shopInfo.isFollow == 0,'border-[2rpx] border-solid border-[#fff]': shopInfo.isFollow == 1}">
                            <text class="nc-iconfont text-[22rpx]  mt-[2rpx] text-[#fff] nc-icon-guanzhuV6xx" v-if="shopInfo.isFollow  == 0"></text>
                            <text class="text-[22rpx] text-[#fff]" :class="{'ml-[8rpx]': shopInfo.isFollow == 1 }">{{ shopInfo.isFollow  == 0 ? t('unFollowed') : t('followed') }}</text>
                        </view>
                    </view>
                </view>
            </view>

			<!-- 店铺列表筛选 -->
			<view class="relative flex justify-between items-center h-[84rpx] text-[24rpx] text-[#fff]" v-if="tab">
				<view class="flex-1 flex-center">
					<text class="w-[116rpx] h-[44rpx] rounded-full flex-center font-500"  :class="{ '!text-[var(--primary-color)] bg-[#fff] font-700': searchType == 'all' }" @click="searchTypeFn('all')">默认</text>
				</view>
				<view class="flex-1 flex-center">
					<text class="w-[116rpx] h-[44rpx] rounded-full flex-center font-500"  :class="{ '!text-[var(--primary-color)] bg-[#fff] font-700': searchType == 'sale_num' }" @click="searchTypeFn('sale_num')">销量</text>
				</view>
				<view class="flex-1 flex-center">
					<text class="w-[116rpx] h-[44rpx] rounded-full flex-center font-500"  :class="{ '!text-[var(--primary-color)] bg-[#fff] font-700': searchType == 'price' }" @click="searchTypeFn('price')">价格</text>
				</view>
				<view class="flex-1 flex-center">
					<view :class="['iconfont text-[34rpx] text-[#fff] -mb-[2rpx]', listType ? 'icona-yingyongzhongxinV6xx-32' : 'icona-yingyongliebiaoV6xx-32']" @click="listIconBtn"></view>
				</view>
			</view>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { computed, ref, onMounted, nextTick,getCurrentInstance } from 'vue'
import { img,  redirect,getToken,currRoute, distance } from '@/utils/common'
import { t } from '@/locale'
import { useLogin } from '@/hooks/useLogin'
import { editShopCollect } from '@/addon/mall/api/shop';
import { onLoad, onPageScroll } from '@dcloudio/uni-app';
import useShopStore from '@/addon/mall/stores/shop';

const props = defineProps({
    tab: {
        type: Boolean,
        default: false
    }
})

const emit = defineEmits(['load','change'])

const instance = getCurrentInstance();
const scrollBool = ref(-1)

// 获取系统状态栏的高度
let systemInfo = uni.getSystemInfoSync();
let platform = systemInfo.platform;
const shopInfo = ref<any>({})
const siteId = ref<any>('')
const listType = ref(true)
const searchType = ref('all')

onLoad((option: any) => {
    siteId.value = option.site_id
})

const shopStore = useShopStore()
const  setShopInfo = () => {
    if(shopStore.shopInfo[siteId.value]){
        shopInfo.value = shopStore.shopInfo[siteId.value]
    }else{
        shopStore.getShopInfoFn(siteId.value,(data:any)=>{
        	shopInfo.value = data
        })

    }
}


onPageScroll((e) => {
    if (e.scrollTop <= 0) {
        scrollBool.value = -1
    } else if (e.scrollTop > 20) {
        scrollBool.value = 1
    } else {
        scrollBool.value = 2
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
	style += `background-color: rgba(255, 255, 255, 0.2);`
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
	var style = '';
	
	if (scrollBool.value != -1) {
		style += 'position: fixed;z-index: 10;left: 0;right: 0;';
	}
	
	fixedStyleBg.value = false;
	if (scrollBool.value == 1) {
		fixedStyleBg.value = true;
	}
	return style;
})

// 背景渐变
const bgImgBoxStyle = computed(()=>{
	var style = '';
	style += `background: rgba(51,51,51,0.6)`;
	return style;
});


let menuButtonInfo: any = {};
const navbarInnerStyle = ref('')

onMounted(() => {
	setModuleLocation();
	// 如果是小程序，获取右上角胶囊的尺寸信息，避免导航栏右侧内容与胶囊重叠(支付宝小程序非本API，尚未兼容)
	// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
	menuButtonInfo = uni.getMenuButtonBoundingClientRect();
	// 导航栏内部盒子的样式
	// 导航栏宽度，如果在小程序下，导航栏宽度为胶囊的左边到屏幕左边的距离
	// 如果是各家小程序，导航栏内部的宽度需要减少右边胶囊的宽度
	let rightButtonWidth = menuButtonInfo.width ? menuButtonInfo.width * 2 + 'rpx' : '70rpx';
	navbarInnerStyle.value += 'padding-right:calc(' + rightButtonWidth + ' + 30rpx);';
	navbarInnerStyle.value += 'padding-top:' + menuButtonInfo.top + 'px;';
	// #endif
    setShopInfo()
});


const toDetail = ()=>{
	redirect({ url: '/addon/mall/pages/shop/detail?site_id=' + shopInfo.value.siteId })
}

// 关注店铺
const followShop = ()=> {

	// 检测是否登录
	if (!getToken()) {
		useLogin().setLoginBack({ url: '/' + currRoute() + '?site_id=' + shopInfo.value.siteId })
		return;
	}

	editShopCollect({
		site_id: shopInfo.value.siteId,
		is_follow: shopInfo.value.isFollow == 0 ? 1 : 0
	}).then(res => {
		if (shopInfo.value.isFollow) {
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
		shopInfo.value.isFollow = shopInfo.value.isFollow == 0 ? 1 : 0
	})
}
// 跳转查询商品
const toLink = () =>{
	if(shopInfo.value.siteId){
		redirect({url:`/addon/mall/pages/shop/search?site_id=${shopInfo.value.siteId}`})
	}
}

//列表样式切换
const listIconBtn = () => {
	listType.value = !listType.value
	emit('change', {listType: listType.value})
}
const searchTypeFn = (type:any) => {
	searchType.value = type;
    emit('load', {searchType: searchType.value})
}



</script>

<style lang="scss" scoped>
.shop-head-wrap{
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
.primary-btn-bg {
	background: linear-gradient(94deg, #FB7939 0%, #FF4142 99%), #FF4142;
}
</style>
