<template>
	<view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden order-list" :style="themeColor()">
		<view class="fixed left-0 top-0 right-0 z-10" v-if="statusLoading">
			<scroll-view :scroll-x="true" class="tab-style-2">
				<view class="tab-content">
					<view class="tab-items" :class="{ 'class-select': orderState === item.status.toString() }" @click="orderStateFn(item.status)" v-for="(item, index) in orderStateList">{{ item.name }}</view>
				</view>
			</scroll-view>
		</view>

		<mescroll-body ref="mescrollRef" top="88rpx" @init="mescrollInit" :down="{ use: false }" @up="getMallOrderFn">
			<view class="sidebar-margin pt-[var(--top-m)]" v-if="list.length">
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
										<view class="max-w-[322rpx] text-[28rpx] leading-[40rpx] font-400 truncate text-[#303133]">{{ subitem.goods_name }}</view>
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
						<view class="flex justify-end text-[28rpx] mt-[20rpx] items-center" v-if="(item.status == 1) || (item.status == 3) || (item.status == 5 && evaluateConfig.is_evaluate == 1)">
							<view class="text-[24rpx] font-500 leading-[52rpx] h-[56rpx] min-w-[150rpx] text-center border-[2rpx] border-solid border-[var(--text-color-light9)] rounded-full text-[var(--text-color-light6)] box-border" v-if="item.status == 1" @click.stop="orderBtnFn(item, 'close')">{{ t('orderClose') }}</view>
							<view class="text-[24rpx] font-500 flex-center h-[56rpx] min-w-[150rpx] text-center border-[0] text-[#fff] primary-btn-bg rounded-full ml-[20rpx] box-border" @click.stop="orderBtnFn(item, 'pay')" v-if="item.status == 1">{{ t('topay') }}</view>
							<view class="text-[24rpx] font-500 flex-center h-[56rpx] min-w-[150rpx] text-center border-[0] text-[#fff] primary-btn-bg rounded-full ml-[20rpx] box-border" @click.stop="orderBtnFn(item, 'finish')" v-if="item.status == 3">{{ t('orderFinish') }}</view>
							<view class="text-[24rpx] font-500 leading-[52rpx] h-[56rpx] min-w-[150rpx] text-center border-[2rpx] border-solid border-[var(--text-color-light9)] rounded-full ml-[20rpx]  text-[var(--text-color-light6)] box-border" v-if="item.status == 5 && evaluateConfig.is_evaluate == 1" @click.stop="orderBtnFn(item, 'evaluate')">{{ item.is_evaluate == 1 ? t('selectedEvaluate') : t('evaluate') }}</view>
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
		<!-- #ifdef MP-WEIXIN -->
        <!-- 小程序隐私协议 -->
        <wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
        <!-- #endif -->
	</view>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue';
import { t } from '@/locale'
import { img, redirect } from '@/utils/common';
import { getMallOrderStatus, getMallOrder, orderClose, orderFinish,getMallOrderPay,getMallOrderClose } from '@/addon/mall/api/order';
import { getEvaluateConfig } from '@/addon/mall/api/shop';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom, onShow } from '@dcloudio/uni-app';
import useConfigStore from "@/stores/config";

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
import groupPay from './components/group-pay/group-pay.vue'
import groupClose from './components/group-close/group-close.vue'
const list = ref<Array<Object>>([]);
const loading = ref<boolean>(false);
const statusLoading = ref<boolean>(false);
const orderState = ref('')
const orderStateList = ref<any>([]);
const evaluateConfig = ref("")

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
	getMescroll().resetUpScroll();
});
const getMallOrderFn = (mescroll:any) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size,
		status: orderState.value
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
	}
}

// 支付
let groupPayRef: any = ref(null)
const payFn = (item:any) => {
	getMallOrderPay(item.order_id).then((res: any) =>{
		if(res.data.is_allow_pay){
			payRef.value?.open(item.order_type, res.data.group_id, `/addon/mall/pages/order/detail?order_id=${item.order_id}`);
		}else{
			if(res.data.order_list.length){
				groupPayRef.value.open(res.data,item)
			}else{
				payRef.value?.open(item.order_type, res.data.group_id, `/addon/mall/pages/order/detail?order_id=${item.order_id}`);
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
