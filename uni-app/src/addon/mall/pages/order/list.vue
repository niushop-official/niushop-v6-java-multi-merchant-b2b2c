<template>
	<view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden order-list" :style="themeColor()">
		<view class="search-box px-[30rpx] z-10 bg-[#f6f6f6] fixed top-0 left-0 right-0 box-border">
            <view class="h-[100rpx] flex items-center flex-1  justify-between" :style="navbarInnerStyle">
                <!-- #ifdef MP-WEIXIN || APP-PLUS -->
                <view class="back-wrap text-[26px] nc-iconfont nc-icon-zuoV6xx" @click="back"></view>
                <!-- #endif -->
                <view class="search-input bg-[#fff]">
                    <text @click.stop="searchNameFn" class="nc-iconfont nc-icon-sousuo-duanV6xx1 btn"></text>
                    <input class="input" type="text" v-model.trim="searchName" placeholder="请输入商品名称" @confirm="searchNameFn" placeholderClass="text-[var(--text-color-light9)]">
                    <text v-if="searchName" class="nc-iconfont nc-icon-cuohaoV6xx1 clear" @click="searchName=''"></text>
                </view>
            </view> 
        </view>
		<view class="fixed left-0 top-0 right-0 z-10" v-if="statusLoading" :style="`top: ${categoryTop}`">
			<scroll-view :scroll-x="true" class="tab-style-2">
				<view class="tab-content">
					<view class="tab-items" :class="{ 'class-select': orderState === item.status.toString() }" @click="orderStateFn(item.status)" v-for="(item, index) in orderStateList">{{ item.name }}</view>
				</view>
			</scroll-view>
		</view>

		<mescroll-body ref="mescrollRef" :top="listTop" @init="mescrollInit" :down="{ use: false }" @up="getMallOrderFn">
			<view class="sidebar-margin mt-[var(--top-m)]" v-if="list.length">
				<template v-for="(item, index) in list" :key="index">
					<view class="mb-[var(--top-m)] card-template">
						<view @click.stop="toLink(item)">
							<view class="flex justify-between items-center">
								<view class="text-[#303133] text-[26rpx] font-400 leading-[36rpx] flex items-center">
									<text class="iconfont iconVector-25 text-[28rpx]"></text>
									<text class="text-[26rpx] font-400 ml-[12rpx] max-w-[480rpx] truncate">{{ item.site ? item.site.site_name : '' }}</text>
								</view>
								<view class="text-[#303133] text-[26rpx] leading-[34rpx]" :class="{'text-primary': item.status  == 1,'!text-[var(--text-color-light9)]' :item.status  == 5 || item.status  == -1}">{{ item.status_name.name }}</view>
							</view>
							<view class="flex box-border mt-[20rpx]" v-for="(subitem, index) in item.order_goods" :key="index">
								<up-image width="150rpx" height="150rpx" :radius="'var(--goods-rounded-big)'" :src="img(subitem.goods_image_thumb_small ? subitem.goods_image_thumb_small : '')" mode="aspectFill">
									<template #error>
										<image class="w-[150rpx] h-[150rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
									</template>
								</up-image>
								<view class="ml-[20rpx] flex flex-1 flex-col box-border">
									<view class="flex justify-between items-baseline">
										<view class="max-w-[322rpx] text-[28rpx] leading-[40rpx] font-400 truncate text-[#303133]">
											<text v-if="item.order_type_name" class="relative top-[-4rpx] inline-block mr-[4rpx] bg-primary text-[#fff] text-[18rpx] rounded-[4rpx] leading-[30rpx] px-[6rpx]">{{ item.order_type_name }}</text>
											{{ subitem.goods_name }}
										</view>
										<view class="text-right leading-[42rpx] ml-[10rpx] price-font">
											<text class="text-[22rpx]">￥</text>
											<text class="text-[32rpx] font-500">{{parseFloat(subitem.price).toFixed(2).split('.')[0] }}</text>
											<text class="text-[22rpx] font-500">.{{parseFloat(subitem.price).toFixed(2).split('.')[1] }}</text>
										</view>
									</view>
									<view class="flex  justify-between items-baseline text-[#303133] mt-[14rpx]">
										<view>
											<view class="text-[24rpx] text-[var(--text-color-light6)] font-400 truncate leading-[34rpx] max-w-[369rpx] mb-[10rpx]" v-if="subitem.sku_name">{{ subitem.sku_name }}</view>
											<view class="text-[24rpx] font-400 leading-[34rpx] text-[var(--text-color-light6)]" v-if="item.delivery_type != 'virtual'">
												{{t('deliveryType') }}：{{item.delivery_type_name}}
											</view>
											<view class="text-[24rpx] font-400 leading-[34rpx] text-[var(--text-color-light6)]" v-else>{{t('createTime') }} ：{{item.create_time}}</view>
										</view>
										<text class="text-right text-[26rpx] font-400 w-[90rpx] leading-[36rpx]">x{{ subitem.num }}</text>
									</view>
								</view>
							</view>
						</view>
                        <view class="justify-between card-template-item mt-[16rpx]" v-if="item.gift_order_goods && item.gift_order_goods.length > 0" >
                            <view class="text-[28rpx]">赠品</view>
                            <view class="flex items-center text-[28rpx]" @click="giftGooodsRef.open(item.gift_order_goods)">
                                <view class="text-[24rpx] flex-1 flex gap-x-[10rpx] justify-end text-primary">
                                    <view v-for="(cItem, index) in item.gift_order_goods" :key="index" class="flex">
                                        <up-image radius="var(--goods-rounded-small)" width="60rpx" height="60rpx" :src="img(cItem.sku_image)" model="aspectFill" v-if="index < 5">
                                            <template #error>
                                                <image class="w-[60rpx] h-[60rpx] rounded-[var(--goods-rounded-small)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"/>
                                            </template>
                                        </up-image>
                                    </view>
                                </view>
                                <view class="text-[28rpx] ml-[10rpx]">共<text class="price-font">{{ getGiftNum(item.gift_order_goods) }}</text>件</view>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]" :style="iconfontStyle"></text>

                            </view>
                        </view>
						<view class="flex justify-end items-center mt-[20rpx]">
							<view class="flex items-baseline">
								<view class="text-[22rpx] font-400 leading-[30rpx] text-[#303133]">{{ t('actualPayment') }}：</view>
								<view class="leading-[1] text-[var(--price-text-color)]">
									<text class="ext-[22rpx] leading-[26rpx] price-font">￥</text>
									<text class="text-[36rpx] font-500 leading-[40rpx] price-font">{{ parseFloat(item.order_money).toFixed(2).split('.')[0]  }}</text>
									<text class="text-[22rpx] font-500 leading-[28rpx] price-font">.{{ parseFloat(item.order_money).toFixed(2).split('.')[1]  }}</text>
								</view>
							</view>
						</view>
						<view class="flex justify-end text-[28rpx] mt-[20rpx] items-center" v-if="(item.status == 1) || (item.status == 3) || (item.status == 5 && evaluateConfig.is_evaluate == 1) || (item.order_type == 'pintuan' && item.extend_field_2 > 0)">
							<view class="ml-[20rpx] order-grey-hollow-btn" v-if="item.order_type == 'pintuan' && item.extend_field_2 > 0" @click="handlePintuan(item)">查看拼团</view>
							<view class="ml-[20rpx] order-grey-hollow-btn" v-if="item.status == 1" @click.stop="orderBtnFn(item, 'close')">{{ t('orderClose') }}</view>
							<view class="order-primary-solid-btn primary-btn-bg ml-[20rpx]" @click.stop="orderBtnFn(item, 'pay')" v-if="item.status == 1">{{ t('topay') }}</view>
							<view class="order-primary-solid-btn primary-btn-bg  ml-[20rpx]" @click.stop="orderBtnFn(item, 'finish')" v-if="item.status == 3">{{ t('orderFinish') }}</view>
							<view class="order-grey-hollow-btn ml-[20rpx]" v-if="item.status == 5 && evaluateConfig.is_evaluate == 1" @click.stop="orderBtnFn(item, 'evaluate')">{{ item.is_evaluate == 1 ? t('selectedEvaluate') : t('evaluate') }}</view>
							<view class="text-[24rpx] font-500 leading-[52rpx] h-[56rpx] min-w-[150rpx] text-center border-[2rpx] border-solid border-[#ccc] rounded-full text-[var(--text-color-light3)] ml-[20rpx] box-border" v-if="item.can_invoice" @click.stop="orderBtnFn(item, 'invoice')">开票</view>
						</view>
					</view>
				</template>
			</view>
			<mescroll-empty :option="{tip : '暂无订单'}" v-if="!list.length && loading"></mescroll-empty>
		</mescroll-body>
		<pay ref="payRef" @close="payClose"></pay>
		<!-- 组合支付 -->
		<group-pay ref="groupPayRef"></group-pay>
		<!-- 组合关闭订单 -->
		<group-close ref="groupCloseRef" @confirm="closeGroupOrder"></group-close>
         <!-- 赠品 -->
        <gift-goods ref="giftGooodsRef"/>
		<!-- #ifdef MP-WEIXIN -->
        <!-- 小程序隐私协议 -->
        <wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
        <!-- #endif -->
	</view>
</template>

<script setup lang="ts">
import { ref, nextTick ,computed } from 'vue';
import { t } from '@/locale'
import { img, redirect, pxToRpx } from '@/utils/common';
import { getMallOrderStatus, getMallOrder, orderClose, orderFinish,getMallOrderPay,getMallOrderClose } from '@/addon/mall/api/order';
import { getEvaluateConfig } from '@/addon/mall/api/shop';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom, onShow } from '@dcloudio/uni-app';
import useConfigStore from "@/stores/config";
import giftGoods from '@/addon/mall/pages/order/components/gift-goods/gift-goods.vue'

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
import groupPay from './components/group-pay/group-pay.vue'
import groupClose from './components/group-close/group-close.vue'
import { topTabar } from '@/utils/topTabbar';
import useSystemStore from '@/stores/system';
const systemStore = useSystemStore()

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()

let topTabbarData = topTabarObj.setTopTabbarParam({ topStatusBar:{style :'style-5',textColor:'#606266'} })
/********* 自定义头部 - end ***********/

const navbarInnerStyle = ref('')
// #ifdef MP-WEIXIN || MP-BAIDU || MP-TOUTIAO || MP-QQ
let rightButtonWidth = systemStore.menuButtonInfo.width ? systemStore.menuButtonInfo.width * 2 + 'rpx' : '70rpx';
navbarInnerStyle.value += 'padding-right:calc(' + rightButtonWidth + ' + 30rpx);';
navbarInnerStyle.value += 'padding-top:' + systemStore.menuButtonInfo.top + 'px;';
navbarInnerStyle.value += 'padding-bottom: 8px;';
navbarInnerStyle.value+= 'height:' + systemStore.menuButtonInfo.height + 'px;';
// #endif

// #ifdef APP-PLUS
navbarInnerStyle.value += 'padding-top:' + systemStore.systemInfo.statusBarHeight + 'px;';
// #endif
const categoryTop = computed(() => {
    let height = Object.keys(systemStore.menuButtonInfo).length && systemStore.menuButtonInfo.height ? (pxToRpx(Number(systemStore.menuButtonInfo.height)) + pxToRpx(systemStore.menuButtonInfo.top) + pxToRpx(8)) : 100;
    
    return height + 'rpx';
});

const listTop = computed(() => {
    let height = Object.keys(systemStore.menuButtonInfo).length && systemStore.menuButtonInfo.height ? (pxToRpx(Number(systemStore.menuButtonInfo.height)) + pxToRpx(systemStore.menuButtonInfo.top) + pxToRpx(8) + 88) : 188;
    // let height = Object.keys(systemStore.menuButtonInfo).length && systemStore.menuButtonInfo.height ? 88: 128;
    return height + 'rpx';
});


const list = ref<Array<Object>>([]);
const loading = ref<boolean>(false);
const statusLoading = ref<boolean>(false);
const orderState = ref('')
const orderStateList = ref<any>([]);
const evaluateConfig = ref("")
const giftGooodsRef = ref()
const mch_id = ref('')
const isTradeManaged = ref(false)

const wxPrivacyPopupRef: any = ref(null)

onLoad((option: any) => {
	orderState.value = option.status || "";
	evaluateEvent()
	getMallOrderStatusFn();
	// #ifdef MP
    nextTick(() => {
        if (wxPrivacyPopupRef.value) wxPrivacyPopupRef.value.proactive();
    })
    // #endif
});

const evaluateEvent = () => {
	getEvaluateConfig().then((data: any) => {
		evaluateConfig.value = data.data
	})
}
onShow(() => {
	nextTick(() => {
		getMescroll().resetUpScroll();
	})
	
});

//赠品数量
const getGiftNum = (data) => { 
    if (!Array.isArray(data) || data.length === 0) return 0;
    return data.reduce((total, item) => {
        const giftNum = Number(item.num) || 0;
        return total + giftNum;
    }, 0);
};
const iconfontStyle = computed(() => {
	let style = "";
    // #ifdef H5
    style += "position: relative;top: 1rpx;";
    // #endif
    // #ifdef MP
    style += "position: relative;top: 4rpx;";
    // #endif
    return style;
})
const getMallOrderFn = (mescroll:any) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size,
		status: orderState.value,
		keyword: searchName.value,
	};

	getMallOrder(data).then((res: any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (mescroll.num == 1) {
			list.value = []; //如果是第一页需手动制空列表
		}
		list.value = list.value.concat(newArr);
		mescroll.endSuccess(newArr.length);

		mch_id.value = res.data.mch_id;
        isTradeManaged.value = res.data.is_trade_managed;
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}

const getMallOrderStatusFn = () => {
	statusLoading.value = false;
	orderStateList.value = [];
	let obj = { name: '全部', status: '' };
	orderStateList.value.push(obj);

	getMallOrderStatus().then((res: any) => {
		Object.values(res.data).forEach((item, index) => {
			orderStateList.value.push(item);
		});
		statusLoading.value = true;
	}).catch(() => {
		statusLoading.value = true;
	})
}

const orderStateFn = (status: any) => {
	orderState.value = status.toString();
	list.value = [];
	getMescroll().resetUpScroll();
};

const toLink = (data: any) => {
	redirect({ url: '/addon/mall/pages/order/detail', param: { order_id: data.order_id } })
}

const searchName = ref('')
const searchNameFn = () => {
    list.value = [];
    getMescroll().resetUpScroll();
}
const back = () => {
    if (getCurrentPages().length > 1) {
        uni.navigateBack()
    } else {
        redirect({
            url: '/app/pages/index/index',
            mode: 'reLaunch'
        });
    }
}

// 支付
const payRef = ref(null)
const orderBtnFn = (data: any, type = '') => {
	if (type == 'pay'){
		payFn(data)
	}else if (type == 'close') {
		close(data);
	} else if (type == 'finish') {
		finish(data);
	} else if (type == 'evaluate') {
		if (!data.is_evaluate) {
			redirect({ url: '/addon/mall/pages/evaluate/order_evaluate', param: { order_id: data.order_id } })
		} else {
			redirect({ url: '/addon/mall/pages/evaluate/order_evaluate_view', param: { order_id: data.order_id } })
		}
	} else if (type == 'invoice') {

        redirect({ url: '/app/pages/invoice/invoice', param: { order_id: data.order_id, trade_type: 'mall' } })
    }
}

// 支付
let groupPayRef: any = ref(null)
const payFn = (item:any) => {
	getMallOrderPay(item.order_id).then((res: any) =>{
		if(res.data.is_allow_pay){
			payRef.value?.open('mall', res.data.group_id, `/addon/mall/pages/order/detail?order_id=${item.order_id}`);
		}else{
			if(res.data.order_list.length){
				groupPayRef.value.open(res.data,item)
			}else{
				payRef.value?.open('mall', res.data.group_id, `/addon/mall/pages/order/detail?order_id=${item.order_id}`);
			}
		}
	})
}

//关闭订单

let groupCloseRef: any = ref(null)
const close = (item: any) => {
	uni.showModal({
		title: '提示',
		content: '您确定要关闭该订单吗？',
		confirmColor: useConfigStore().themeColor['--primary-color'],
		success: res => {
			if (res.confirm) {
				orderClose(item.order_id).then((res: any) => {
					if(Object.keys(res.data).length && !res.data.is_allow_close){
						if(res.data.order_list.length){
							groupCloseRef.value.open(res.data)
						}else{
							getMallOrderClose(res.data.group_id).then(res =>{
								getMescroll().resetUpScroll();
							}).catch(() =>{
							})
						}
					}else{
						getMescroll().resetUpScroll();
					}
				})
			}
		}
	})
}
const closeGroupOrder = () =>{
	getMescroll().resetUpScroll();
}

//订单完成
const finish = (item: any) => {
	// 如果不在微信小程序中
    // #ifndef MP-WEIXIN
	uni.showModal({
		title: '提示',
		content: '您确定物品已收到吗？',
		confirmColor: useConfigStore().themeColor['--primary-color'],
		success: res => {
			if (res.confirm) {
				orderFinish(item.order_id).then((data) => {
					getMescroll().resetUpScroll();
				})
			}
		}
	})
	// #endif

	// #ifdef MP-WEIXIN
    // 检测微信小程序是否已开通发货信息管理服务
    if (item.pay && (item.pay.type == 'wechatpay' || item.pay.type == 'huifu_wechatpay') && isTradeManaged.value && wx.openBusinessView) {
        wx.openBusinessView({
            businessType: 'weappOrderConfirm',
            extraData: {
                // merchant_id: mch_id.value,
               transaction_id: item.pay.trade_no
            },
            success: (res:any) => {
				setTimeout(() => {
					orderFinish(item.order_id).then((data) => {
						getMescroll().resetUpScroll();
					})
				}, 2000)
            },
            fail: (res:any) => {
                console.log('小程序确认收货组件打开失败 fail', res);
            }
        })
    }else{
        uni.showModal({
            title: '提示',
            content: '您确定物品已收到吗？',
            confirmColor: useConfigStore().themeColor['--primary-color'],
            success: res => {
                if (res.confirm) {
                    orderFinish(item.order_id).then((data) => {
                        getMescroll().resetUpScroll();
                    })
                }
            }
        })
    }
    // #endif
}

const handlePintuan = (data: any) => {
    redirect({
        url: '/addon/pintuan/pages/order/detail',
        param: {
            order_id: data.order_id
        }
    })
}
</script>
<style>
.order-list .mescroll-body {
	padding-bottom: constant(safe-area-inset-bottom) !important;
	padding-bottom: env(safe-area-inset-bottom) !important;
}
</style>
<style lang="scss" scoped>
.text-color {
	color: var(--primary-color);
}

.bg-color {
	background-color: var(--primary-color);
}

</style>
