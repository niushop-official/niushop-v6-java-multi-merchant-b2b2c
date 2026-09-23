<template>
    <view :style="themeColor()">
        <!-- #ifdef MP-WEIXIN || APP-PLUS -->
        <top-tabbar :data="topTabbarData" scrollBool="1" :isBack="false"/>
        <!-- #endif -->
        <view class="bg-page min-h-[100vh] overflow-hidden flex flex-col" v-if="!loading">
            <view v-if="!info" class="pb-[100rpx]">
                <view class="empty-page">
                    <image class="img" :src="img('static/resource/images/system/login.png')" mode="aspectFit" />
                    <view class="desc">暂未登录</view>
                    <button shape="circle" plain="true" class="btn" @click="toLogin">去登录</button>
                </view>
                <ns-goods-recommend></ns-goods-recommend>
            </view>
            <view v-else-if="!cartList.length" class="pb-[100rpx]">
                <view class="empty-page">
                    <image class="img" :src="img('addon/mall/cart-empty.png')" mode="aspectFit" />
                    <view class="desc">赶紧去逛逛, 购买心仪的商品吧</view>
                    <button shape="circle" plain="true" class="btn" @click="redirect({ url: '/addon/mall/pages/goods/list' })">去逛逛</button>
                </view>
                <ns-goods-recommend></ns-goods-recommend>
            </view>
            <block v-else>
                <view class="flex-1 h-0">
                    <scroll-view class="scroll-height box-border" :scroll-y="true">
                        <view class="py-[var(--top-m)] sidebar-margin">
                            <view class="box-border"  v-if="cartList.length">
                                <view class="bg-[#fff] flex px-[var(--pad-sidebar-m)] justify-between items-center h-[80rpx] box-border font-400 text-[24rpx] mb-[24rpx] leading-[30rpx] rounded-[var(--rounded-big)]">
                                    <view class="flex items-center text-[24rpx] text-[#333]">
                                        <text>共<text class="text-[28rpx] mx-[2rpx] text-[var(--price-text-color)]">{{checkedNum }}</text>件商品</text>
                                    </view>
                                    <text @click="isEdit = !isEdit" class="text-[var(--text-color-light6)] text-[24rpx]">{{ isEdit ? '完成' : '管理' }}</text>
                                </view>
                                <view v-for="(item, index) in cartList" :key="index" class="bg-[#fff] mb-[20rpx] rounded-[var(--rounded-big)] pb-[10rpx]">
                                    <view class="flex px-[var(--pad-sidebar-m)] py-[16rpx] items-baseline">
                                        <text v-if="item.disabled" class="iconfont text-color check-wrap" :class="{ 'iconxuanze1 ':item.checked,'bg-gray':!item.checked}"></text>
                                        <text v-else class="iconfont text-color check-wrap" :class="{ 'iconxuanze1 ':item.checked,'bg-gray':!item.checked}" @click="isClickSite(item)"></text>
                                        <text @click="redirect({url: '/addon/mall/pages/site/index', param: {site_id: item.site_id }})">
                                            <text class="iconfont iconVector-25 text-[32rpx]"></text>
                                            <text class="ml-[12rpx] text-[28rpx] max-w-[300rpx] truncate">{{item.site_name }}</text>
                                            <text class="nc-iconfont nc-icon-youV6xx text-[28rpx]"></text>
                                        </text>
                                        
                                    </view>
                                    <u-swipe-action ref="swipeActiveRef">
                                        <block v-for="(subItem, subIndex) in item.goods_list" :key="subIndex">
                                            <view v-if="subItem.goodsSku"  class="py-[20rpx] overflow-hidden w-full">
                                                <u-swipe-action-item :options="cartOptions" @click="swipeClick(index,subIndex,subItem)">
                                                    <view class="flex px-[var(--pad-sidebar-m)]">
                                                        <text class="self-center w-[34rpx] h-[34rpx] rounded-[17rpx] mr-[32rpx] bg-[#F5F5F5] overflow-hidden flex-shrink-0" v-if="subItem.disabled"></text>
                                                        <text v-else class="change-wrap self-center iconfont text-color  overflow-hidden flex-shrink-0" :class="{ 'iconxuanze1':subItem.checked,'bg-gray':!subItem.checked}" @click="changeItem(item,subItem)">
                                                        </text>
                                                        <view class="relative w-[200rpx] h-[200rpx] rounded-[var(--goods-rounded-big)] overflow-hidden">
                                                            <up-image class="rounded-[var(--goods-rounded-big)] overflow-hidden" width="200rpx" height="200rpx" @click="toDetail(subItem)" :src="img(subItem.goodsSku.sku_image_thumb_mid||'')" mode="aspectFill">
                                                                <template #error>
                                                                    <image class="w-[200rpx] h-[200rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
                                                                </template>
                                                            </up-image>
                                                            <view v-if="subItem.disabled" class="absolute top-0 right-0 left-0 bottom-0 flex justify-center items-center bg-black bg-opacity-50 text-white rounded-[var(--goods-rounded-big)]">已下架</view>
                                                        </view>
                                                        <view class="flex flex-1 flex-col justify-between ml-[20rpx]">
                                                            <view class="w-[100%] flex flex-col items-baseline">
                                                                <view class="text-[#333] text-[28rpx] max-h-[80rpx] leading-[40rpx] multi-hidden font-400">
                                                                    {{ subItem.goods.goods_name }}
                                                                </view>
                                                                <view class="box-border max-w-[376rpx] mt-[10rpx] px-[14rpx] h-[46rpx] leading-[46rpx] truncate text-[var(--text-color-light6)] bg-[#F5F5F5] text-[22rpx] rounded-[20rpx]" v-if="subItem.goodsSku && subItem.goodsSku.sku_spec_format" @click="buyFn(subItem,'update_cart')">
                                                                    <text>{{ subItem.goodsSku.sku_spec_format }}</text>
                                                                    <text class="nc-iconfont nc-icon-xiaV6xx text-[24rpx] ml-[8rpx]"></text>
                                                                </view>
                                                            </view>
                                                            <view v-if="subItem.goods && subItem.goods.goods_label_name && subItem.goods.goods_label_name.length"
                                                            class="flex flex-wrap mb-[auto]">
                                                                <template v-for="(tagItem, tagIndex) in subItem.goods.goods_label_name">
                                                                    <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')" />
                                                                    <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                                                </template>
                                                            </view>
                                                            <view class="mt-[10rpx]">
                                                                <view class="flex justify-between items-end self-end  w-[100%]">
                                                                    <view class="text-[var(--price-text-color)] price-font truncate max-w-[200rpx]">
                                                                        <text class="text-[24rpx] font-500">￥</text>
                                                                        <text class="text-[40rpx] font-500">{{ parseFloat(goodsPrice(subItem)).toFixed(2).split('.')[0] }}</text>
                                                                        <text class="text-[24rpx] font-500">.{{ parseFloat(goodsPrice(subItem)).toFixed(2).split('.')[1] }}</text>
                                                                        <image class="h-[24rpx] ml-[6rpx]" v-if="priceType(subItem) == 'svip_price'" :src="img('addon/mall/SVIP.png')" mode="heightFix" />
                                                                        <image class="h-[24rpx] ml-[6rpx]" v-if="priceType(subItem) == 'member_price'" :src="img('addon/mall/VIP.png')" mode="heightFix" />
                                                                        <image class="h-[24rpx] ml-[6rpx]" v-if="priceType(subItem) == 'discount_price'" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                                                    </view>
                                                                    <u-number-box v-model="subItem.num" :min="numLimit(subItem).min" :max="numLimit(subItem).max" integer :step="1" input-width="68rpx" input-height="52rpx" button-size="52rpx" disabledInput  @change="numChange($event, subItem)">
                                                                        <template #minus>
                                                                            <view class="relative w-[26rpx] h-[26rpx]" @click="reduceNumChange(subItem)">
                                                                                <text :class="{ 'text-color-light9': subItem.num === numLimit(subItem).min, 'text-color-light3': subItem.num !== numLimit(subItem).min }" class="num-box-wrap  absolute flex items-center justify-center font-500 nc-iconfont nc-icon-jianV6xx"></text>
                                                                            </view>
                                                                        </template>
                                                                        <template #input>
                                                                            <input
                                                                            class="text-[#303133] text-[28rpx] mx-[14rpx] w-[80rpx] h-[44rpx] bg-[var(--temp-bg)] leading-[44rpx] text-center rounded-[6rpx]"
                                                                            type="number" @input="goodsSkuInputFn(subItem)"
                                                                            @blur="goodsSkuBlurFn($event, subItem)"
                                                                            @click.stop v-model="subItem.num" />
                                                                        </template>
                                                                        <template #plus>
                                                                            <view class="relative w-[26rpx] h-[26rpx]" @click="addNumChange(subItem)">
                                                                                <text :class="{ 'text-color-light9': subItem.num === numLimit(subItem).max, ' text-color-light3': subItem.num !== numLimit(subItem).max }" class="num-box-wrap absolute flex items-center justify-center  font-500 nc-iconfont nc-icon-jiahaoV6xx"></text>
                                                                            </view>
                                                                        </template>
                                                                    </u-number-box>
                                                                </view>
                                                                <view v-if="subItem.promotion_list[0]" class="text-[22rpx] text-primary flex items-center mt-[10rpx]" @click="manjianOpenFn(subItem)">
                                                                    <text>{{ subItem.promotion_list[0] }}</text>
                                                                    <text class="nc-iconfont nc-icon-youV6xx text-[20rpx]"></text>
                                                                </view>
                                                            </view>
                                                        </view>
                                                    </view>
                                                </u-swipe-action-item>
                                            </view>
                                        </block>
                                    </u-swipe-action>
                                    <!-- 赠品 -->
                                    <view class="flex items-center justify-end px-[var(--pad-sidebar-m)]" v-if="item.gift_goods_list && item.gift_goods_list.length">
                                        <view class="p-[16rpx] bg-[#F8F8F8] rounded-[10rpx] flex items-center">
                                            <text class="text-[22rpx] mr-[16rpx]">赠品</text>
                                            <view class="flex gap-x-[10rpx] justify-end text-primary">
                                                <view v-for="(cItem, index) in item.gift_goods_list" :key="index" class="flex">
                                                    <up-image radius="var(--goods-rounded-small)" width="60rpx" height="60rpx" :src="img(cItem.sku_image)" model="aspectFill" v-if="index < 5">
                                                        <template #error>
                                                            <image class="w-[60rpx] h-[60rpx] rounded-[var(--goods-rounded-small)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"/>
                                                        </template>
                                                    </up-image>
                                                </view>
                                            </view>
                                            <view class="text-[22rpx] ml-[10rpx]">共{{ item.gift_goods_list.length }}件</view>
                                            <text class="nc-iconfont nc-icon-youV6xx text-[20rpx] text-[var(--text-color-light9)]"></text>
                                        </view>
                                    </view>
                                </view>
                            </view>
                        </view>
                        <ns-goods-recommend></ns-goods-recommend>
                    </scroll-view>
                </view>
            </block>
        </view>
        
        <!--  #ifdef  H5 -->
        <view v-if="cartList.length" class="flex h-[96rpx] items-center bg-[#fff] fixed left-0 right-0 bottom-[50px] pl-[30rpx] pr-[20rpx] box-solid mb-ios justify-between border-0 border-t-[2rpx] border-solid border-[#f6f6f6] z-10090">
            <view class="flex items-center" @click="allChange">
                <text class="self-center iconfont text-color text-[34rpx] mr-[10rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0" :class=" isSelectAll ? 'iconxuanze1' : 'bg-[#F5F5F5]'"></text>
                <text class="font-400 text-[#303133] text-[26rpx]">全选</text>
            </view>
            <view class="flex items-center">
                <view class="flex-1 flex items-center justify-between" v-if="!isEdit">
                    <view class="flex flex-col mr-[20rpx]" @click="showManyGoodsCartPopup">
                        <view class="leading-[1] text-[var(--price-text-color)]">
                            <text class="rice-font text-[24rpx] font-bold">￥</text>
                            <text class="price-font text-[40rpx] font-bold">{{ parseFloat(calculateData.basic.order_money).toFixed(2).split('.')[0] }} </text>
                            <text class="price-font text-[24rpx] font-bold">.{{ parseFloat(calculateData.basic.order_money).toFixed(2).split('.')[1] }} </text>
                        </view>
                        <view class="flex items-baseline justify-end text-[20rpx] text-[#ff4d4f] mt-[4rpx]" v-if="calculateData.basic.discount_money">
                            <span>已减</span>
                            <text class="price-font">¥{{ parseFloat(calculateData.basic.discount_money).toFixed(2) }}</text>
                            <text class="nc-iconfont nc-icon-shangV6xx-1 text-[26rpx] ml-[8rpx]"></text>
                        </view>
                    </view>
                    <button  class="w-[180rpx] h-[70rpx] font-500 text-[26rpx] leading-[70rpx] !text-[#fff] m-0 rounded-full primary-btn-bg remove-border" @click="settlement">结算</button>
                </view>
                <view class="flex-1 flex items-center justify-end" v-else>
                    <button class="w-[180rpx] h-[70rpx] font-500 text-[26rpx] leading-[70rpx] !text-[#fff] m-0 rounded-full primary-btn-bg remove-border" @click="deleteCartFn">删除</button>
                </view>
            </view>
        </view>
        <!--  #endif -->
        <!--  #ifndef  H5 -->
        <view v-if="cartList.length" class="pl-[30rpx] pr-[20rpx] flex h-[96rpx] items-center bg-[#fff] z-10090 fixed left-0 right-0 bottom-[100rpx] box-solid mb-ios justify-between border-0 border-t-[2rpx] border-solid border-[#f6f6f6]">
            <view class="flex items-center" @click="allChange">
                <text class="self-center iconfont text-color all-check-wrap  overflow-hidden flex-shrink-0" :class=" isSelectAll ? 'iconxuanze1' : 'bg-gray'"></text>
                <text class="font-400 text-[#303133] text-[26rpx]">全选</text>
            </view>
            <view class="flex items-center">
                <view class="flex-1 flex items-center justify-between" v-if="!isEdit">
                    <view class="flex flex-col mr-[20rpx]" @click="showManyGoodsCartPopup">
                        <view class="leading-[1] text-[var(--price-text-color)]">
                            <text class="rice-font text-[24rpx] font-bold">￥</text>
                            <text class="price-font text-[40rpx] font-bold">{{ parseFloat(calculateData.basic.order_money).toFixed(2).split('.')[0] }} </text>
                            <text class="price-font text-[24rpx] font-bold">.{{ parseFloat(calculateData.basic.order_money).toFixed(2).split('.')[1] }} </text>
                        </view>
                        <view class="flex items-baselinie justify-end  text-[20rpx] text-[#ff4d4f] mt-[4rpx]" v-if="calculateData.basic.discount_money">
                            <span>已减</span>
                            <text class="price-font">¥{{ parseFloat(calculateData.basic.discount_money).toFixed(2) }}</text>
                            <text class="nc-iconfont nc-icon-shangV6xx-1 text-[26rpx] ml-[8rpx]"></text>
                        </view>
                    </view>
                     <!-- #ifdef H5 -->
                    <button  class="w-[188rpx] h-[70rpx] font-500 text-[26rpx] leading-[72rpx] !text-[#fff] m-0 rounded-full primary-btn-bg remove-border" @click="settlement">结算</button>
                     <!-- #endif -->
                    <!-- #ifdef MP-WEIXIN -->
                    <button class="w-[180rpx] h-[70rpx] font-500 text-[26rpx] leading-[70rpx] !text-[#fff] m-0 rounded-full primary-btn-bg remove-border"
                        @click="settlement">结算</button>
                    <!-- #endif -->
                </view>
                <view class="flex-1 flex items-center justify-end" v-else>
                    <button class="w-[188rpx] h-[70rpx] font-500 text-[26rpx] leading-[72rpx] !text-[#fff] m-0 rounded-full primary-btn-bg remove-border" @click="deleteCartFn">删除</button>
                </view>
            </view>
        </view>
        <!--  #endif -->
        <loading-page :loading="loading"></loading-page>
        <tabbar />
        <!-- 强制绑定手机号 -->
		<bind-mobile ref="bindMobileRef" /> 
        <ns-goods-youhui ref="youhuiShowRef"></ns-goods-youhui>
        <ns-goods-sku ref="goodsSkuRef" :goods-detail="nsGoodsSkuData" @update="getCartGoodsListFn" class="goods-sku"></ns-goods-sku>
        <many-goods-cart-popup ref="manyGoodsCartPopupRef" class="many-goods" :calculate-data="calculateData"></many-goods-cart-popup>
    </view>
</template>

<script setup lang="ts">
import { ref, computed, watch, nextTick } from 'vue'
import useMemberStore from '@/stores/member'
import { useLogin } from '@/hooks/useLogin'
import { onShow } from '@dcloudio/uni-app'
import { img, redirect, debounce, deepClone } from '@/utils/common'
import useCartStore from '@/addon/mall/stores/cart'
import { getCartGoodsList, calculate } from '@/addon/mall/api/cart'
import { getGoodsSkuPromotionDetail } from '@/addon/mall/api/goods';
import bindMobile from '@/components/bind-mobile/bind-mobile.vue';
import { t } from "@/locale";
import { useGoods } from '@/addon/mall/hooks/useGoods'
import nsGoodsRecommend from '@/addon/mall/components/ns-goods-recommend/ns-goods-recommend.vue';
import nsGoodsYouhui from '@/addon/mall/components/ns-goods-youhui/ns-goods-youhui.vue';
import nsGoodsSku from '@/addon/mall/components/ns-goods-sku/ns-goods-sku.vue'
import manyGoodsCartPopup from '@/addon/mall/pages/goods/components/many-goods-cart-popup.vue'
import { topTabar } from '@/utils/topTabbar';
import { useShare } from '@/hooks/useShare'
const { setShare } = useShare()

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let topTabbarData = topTabarObj.setTopTabbarParam({ title: '购物车', topStatusBar: { textColor: '#333' }})
/********* 自定义头部 - end ***********/

const diyGoods = useGoods();
const memberStore = useMemberStore()
const info = computed(() => memberStore.info)
const loading = ref(true)
const optionLoading = ref(false)
const total = ref('0.00')
const cartList = ref<object[]>([])
const isEdit = ref(false)
const cartStore = useCartStore();
const youhuiShowRef: any = ref(null);
const manyGoodsCartPopupRef: any = ref(null)
// 购物车计算数据
const calculateData = ref<any>({
    basic:{
        order_money: 0,
        discount_money: 0
    }
})

const getCartGoodsListFn = () => {
    getCartGoodsList({}).then(({data}) => {
		cartList.value = []
		loading.value = false
		cartList.value = Object.values(data).map((item: any)=> {
            item.checked = false
            item.goods_list.map((subItem: any) =>{
                if (subItem.goods.status == 1 && subItem.goods.delete_time == 0) {
                    if (subItem.goodsSku.stock) {
                        if (subItem.num > subItem.goodsSku.stock) subItem.num = subItem.goodsSku.stock;
                        subItem.checked = false
                    }else{
                        subItem.disabled = true
                    }
                    
                } else {
                    subItem.disabled = true
                }
                return subItem
            })
            item.disabled = item.goods_list.every((subItem: any) => subItem.disabled)
            return item
		})
        isSelectAll.value = false
        allChange()
        cartStore.isAddCartRecommend = false
    }).catch((err)=>{
		if(err.code==401){
			cartList.value = []
			loading.value = false
		}
	})
}


onShow(() => {

    setTimeout(() => {
            let share = {
                title: '购物车',
                desc: '',
            }

            setShare({
                wechat: {
                    ...share
                },
                weapp: {
                    ...share
                }
            });
    }, 600);

	getCartGoodsListFn()
	cartStore.getList();
})

const goodsSkuInputFn = (data: any) => {
    setTimeout(() => {
        if (!data.num || data.num <= numLimit(data).min) {
            data.num = numLimit(data).min;
        }
        if (data.num >= numLimit(data).max) {
            data.num = numLimit(data).max;
        }
        uni.$u.debounce((event: any) => {
            cartStore.increase({
                id: data.id,
                goods_id: data.goods_id,
                sku_id: data.sku_id,
                stock: data.goodsSku.stock,
                sale_price: data.goodsSku.sale_price,
                site_id: data.site_id,
                num: Number(data.num)
            }, 0,()=>{
                getCartGoodsListFn()
                // 计算购物车金额
                calculateCartAmount();
            });
        }, 500)
    }, 0)
}

const goodsSkuBlurFn = (event: any, subItem: any) => {
    setTimeout(() => {
        const data: any = subItem
        if (!data.num || data.num <= numLimit(data).min) {
            data.num = numLimit(data).min;
        }
        if (data.num >= numLimit(data).max) {
            data.num = numLimit(data).max;
        }

        uni.$u.debounce((event: any) => {
            cartStore.increase({
                id: data.id,
                goods_id: data.goods_id,
                sku_id: data.sku_id,
                stock: data.goodsSku.stock,
                sale_price: data.goodsSku.sale_price,
                site_id: data.site_id,
                num: Number(data.num)
            }, 0,()=>{
                getCartGoodsListFn()
                // 计算购物车金额
                calculateCartAmount();
            });
        }, 500)
    }, 0)
}

// 总共数量
// 选择数量
const checkedNum = computed(() => {
	let num = 0
	cartList.value.forEach((item: any) => {
		item.goods_list.forEach((subItem: any)=>{
            if(subItem.checked){
                num += Number(subItem.num)
           }
        })
	})
	return num
})

const toLogin = () => {
	useLogin().setLoginBack({ url: '/addon/mall/pages/goods/cart' })
}

const toDetail = (data: any) => {
    redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id } })
}

const numChange = (event: any, subItem: any) => {
	uni.$u.debounce((event: any) => {
		const data = subItem
		cartStore.increase({
			id: data.id,
			goods_id: data.goods_id,
			sku_id: data.sku_id,
			stock: data.goodsSku.stock,
			sale_price: data.goodsSku.sale_price,
			num: data.num,
            site_id: data.site_id
		}, 0,()=>{
            getCartGoodsListFn()
            // 计算购物车金额
            calculateCartAmount();
        });
	}, 500)
}

const addNumChange = (data: any) => {
    if (data.num >= data.goods.stock) {
        uni.showToast({ title: "商品库存不足", icon: 'none' });
        return;
    }

    if (data.goods.is_limit) {
        let tips = `该商品单次限购${ data.goods.max_buy }件`;
        if (data.goods.limit_type != 1) { //单次限购
            tips = `该商品每人限购${ data.goods.max_buy }件`;
        }
        if (data.num >= data.goods.max_buy) {
            uni.showToast({ title: tips, icon: 'none' })
        }
    }
}

const reduceNumChange = (data: any) => {
    if (data.goods.is_limit && data.goods.min_buy) {
        let tips = `该商品起购${ data.goods.min_buy }件`;
        if (data.num <= data.goods.min_buy) {
            uni.showToast({ title: tips, icon: 'none' })
        }
    }
}

const numLimit = (data: any) => {
    let obj = {
        min: 1,
        max: data.goodsSku.stock || 1
    };

    // 限购 - 是否开启限购
    if (data.goods.is_limit) {
        if (data.goods.max_buy) {
            let max_buy = 0;
            max_buy = data.goods.max_buy;

            if (max_buy > data.goods.stock) {
                obj.max = data.goods.stock
            } else if (max_buy <= data.goods.stock) {
                obj.max = max_buy;
            }
        }
    }

    // 起售
    if (data.goods.min_buy > 0) {
        obj.min = data.goods.min_buy;
    }
    return obj;
}


const cartOptions = ref([
    {
        text: t('delete'),
        style: {
            backgroundColor: 'var(--primary-color)',
            width: '100rpx',
            height: '100%',
            borderRadius: '10rpx'
        }
    }
])

const swipeActiveRef = ref(null)
const swipeClick = (index: any, subIndex: any, subItem: any) => {
	if (optionLoading.value) return
	optionLoading.value = true
	cartStore.delete(subItem.id, () => {
		cartList.value[index].goods_list.splice(subIndex, 1) 
        if(cartList.value[index].goods_list.length == 0){
            cartList.value.splice(index, 1)
        }
		nextTick(()=>{
            if(swipeActiveRef.value){
                swipeActiveRef.value[index].closeOther()
            }
            // 计算购物车金额
            calculateCartAmount();
        })
		optionLoading.value = false
	})
}

//判断是否全选
const isSelectAll = ref(false)
const isallclick = ()=>{
    const isActve = cartList.value.every((item: any) => {
        return item.checked
    })
    if (isActve) { isSelectAll.value = true } else { isSelectAll.value= false }
}
// 选择单个商品
const changeItem = (data: any, value: any) =>{
    value.checked = !value.checked
    let arr: any =[];
    data.goods_list.forEach((item: any) => {
        if(item.checked != undefined){
            arr.push(item)
        }
    })
    const isActve = arr.every((item: any) => {
        return item.checked
    })
    if (isActve) { data.checked = true } else { data.checked = false }
    isallclick()
    // 计算购物车金额
    calculateCartAmount();
}
// 选择店铺
const isClickSite = (data: any) =>{
    data.checked = !data.checked
    if (data.checked) {
        data.goods_list.forEach((item: any) => {
            if(!item.disabled){
                item.checked = true
            }
        })
    } else {
        data.goods_list.forEach((item: any) => {
            if(!item.disabled){
                item.checked = false
            }
        });
    }
    isallclick()
    // 计算购物车金额
    calculateCartAmount();
}
// 全选
const allChange = () =>{
    isSelectAll.value = !isSelectAll.value
    if (isSelectAll.value) {
        cartList.value.forEach((item: any) => {
            item.checked = item.goods_list.some((item: any) => {
                return !item.disabled
            })
            item.goods_list.forEach((subItem: any) => {
                if(!subItem.disabled){
                    subItem.checked = true
                }
            })
        })
    } else {           
        cartList.value.forEach((item: any) => {
            item.checked = false
            item.goods_list.forEach((subItem: any) => {
                if(!subItem.disabled){
                    subItem.checked = false
                }
            })
        })
    }
    // 计算购物车金额
    calculateCartAmount();
}

//满减送
const skuPromotionDetail = ref({})
const manjianOpenFn = (subItem: any) => { 
    getGoodsSkuPromotionDetail({
        sku_id: subItem.sku_id || '',
    }).then((res: any) => {
        skuPromotionDetail.value = res.data
        youhuiShowRef.value.open(skuPromotionDetail.value,subItem.sku_id);
    })
	
}


//强制绑定手机号
const bindMobileRef: any = ref(null)
const isBindMobile = ref(uni.getStorageSync('isbindmobile'))
/**
 * 结算
 */
const settlement = () => {
    // #ifdef H5
    // if(uni.getStorageSync('isbindmobile')){
    //     bindMobileRef.value.open()
    //     return false
    // }
    // #endif

	if (!checkedNum.value) {
		uni.showToast({ title: '还没有选择商品', icon: 'none' })
		return
	}
    let body: any = {}
	cartList.value.forEach((item: any) => {
        body[item.site_id] = {
            order_type: 'mall'
        }
	})
    for(let key in body){
        cartList.value.forEach((item: any) => {
            if (item.site_id == key) {
                body[key].cart_ids = []
                item.goods_list.forEach((subItem: any) => {
                    if (subItem.checked) {
                        body[key].cart_ids.push(subItem.id)
                    }
                })
            }
        })
        if (!body[key].cart_ids.length){
            delete body[key]
        }
    }
	uni.setStorage({
		key: 'orderCreateData',
		data: {
			body: body
		},
		success() {
			redirect({ url: '/addon/mall/pages/order/payment' })
		}
	})
}

/**
 * 删除
 */
const deleteCartFn = () => {
	if (!checkedNum.value) {
		uni.showToast({ title: '还没有选择商品', icon: 'none' })
		return
	}
	if (optionLoading.value) return
	optionLoading.value = true

	const ids:any = []
	if(isSelectAll.value){
        cartList.value.forEach((item: any) => {
            item.goods_list.forEach((subItem: any) => {
                ids.push(subItem.id)
            })
        })
    }else{
        cartList.value.forEach((item: any) => {
            item.goods_list.forEach((subItem: any) => {
                if (subItem.checked) {
                    ids.push(subItem.id)
                }
            })
        })
    }

	cartStore.delete(ids, () => {
		getCartGoodsListFn()
		// 计算购物车金额
		calculateCartAmount();
		optionLoading.value = false
	})
}
// 价格类型 
let priceType = (data:any) =>{
    let type = "";
    type = data.goodsSku.show_type
    return type;
}
// 商品价格
let goodsPrice = (data:any) =>{
    let price = "0.00";
    price = data.goodsSku.show_price
    return price;
}

// 计算购物车金额
const calculateCartAmount = debounce(() => {
    // 构建请求参数
    let body: any = {};
    cartList.value.forEach((item: any) => {
        body[item.site_id] = {
            cart_ids: []
        };
        item.goods_list.forEach((subItem: any) => {
            if (subItem.checked) {
                body[item.site_id].cart_ids.push(subItem.id);
            }
        });
        // 如果该店铺没有选中的商品，删除该店铺数据
        if (!body[item.site_id].cart_ids.length) {
            delete body[item.site_id];
        }
    });
    console.log('计算购物车金额参数:', body);
    
    // 如果有选中的商品，调用计算接口
    calculate({ body }).then((res: any) => {
        calculateData.value = res.data
        // 如果有赠品，则将赠品列表填充到对应店铺中
        cartList.value.forEach((item: any) => {
            item.gift_goods_list = calculateData.value.order_list[item.site_id].basic.full_gift_sku_list || []
        })
        calculateData.value.basic.discount_money = parseFloat(calculateData.value.basic?.mall_discount_money || 0) + parseFloat(calculateData.value.basic?.shop_discount_money || 0)
    }).catch((err) => {
        console.log('计算购物车金额失败:', err);
        // 错误处理，不影响用户操作
    });
}, 300);

watch(
    () => cartStore.isAddCartRecommend,
    (newValue, oldValue) => {
        if(newValue){
            getCartGoodsListFn()
            // 计算购物车金额
            setTimeout(() => {
                calculateCartAmount();
            }, 100);
        }
    })

// 改变购物车规格
const goodsSkuRef: any = ref(null);
const nsGoodsSkuData = ref()
const buyFn = (data: any,type: any) => {
    nsGoodsSkuData.value = data
    goodsSkuRef.value.open(type)
}

// 显示金额明细弹窗
const showManyGoodsCartPopup = () => {
    // 先计算购物车金额
    calculateCartAmount();
    // 延迟一下确保计算完成
    setTimeout(() => {
        // 显示弹窗
        if (manyGoodsCartPopupRef.value) {
            // 这里需要根据many-goods-cart-popup组件的实际API来控制显示
            // 假设组件有open方法
            if (manyGoodsCartPopupRef.value.open) {
                manyGoodsCartPopupRef.value.open();
            } else {
                // 如果没有open方法，直接修改goodsPopup属性
                // 这需要在组件中暴露该属性
                console.log('manyGoodsCartPopupRef:', manyGoodsCartPopupRef.value);
            }
        }
    }, 100);
}
</script>
<style lang="scss" scoped>
@import '@/addon/mall/styles/common.scss';
.remove-border {
	&::after {
		border: none;
	}
}
:deep(uni-page) {
	background: var(--page-bg-color);
}

uni-page-body {
	height: 100%;
}

.check-wrap { 
    font-size: 34rpx;
    margin-right: 20rpx;
    width: 34rpx;
    height: 34rpx;
    border-radius: 17rpx;
}
.change-wrap {
    font-size: 34rpx;
    margin-right: 32rpx;
    width: 34rpx;
    height: 34rpx;
    border-radius: 17rpx;
    line-height: 1;
}
.bg-gray {
    background-color: #F5F5F5;
}
.num-box-wrap {
    font-size: 24rpx;
    left: -20rpx;
    top: -20rpx;
    bottom: -20rpx;
    right: -20rpx;
}
.text-color-light3 {
    color: #303133;

}
.text-color-light9 {
    color: var(var(--text-color-light9));
}
.all-check-wrap {
    font-size: 30rpx;
    margin-right: 20rpx;
     width: 34rpx;
    height: 34rpx;
    border-radius: 17rpx;
}
.text-color {
	color: var(--primary-color);
}

.bg-color {
	background-color: var(--primary-color);
}

:deep(.tab-bar-placeholder) {
	display: none !important;
}

:deep(.u-tabbar__placeholder) {
	display: none !important;
}

/*  #ifdef  H5  */
.scroll-height {
	height: calc(100vh - 100rpx - 50px - constant(safe-area-inset-bottom));
	height: calc(100vh - 100rpx - 50px - env(safe-area-inset-bottom));
}
/*  #endif  */

/*  #ifndef  H5  */
.scroll-height {
	height: calc(100vh - 200rpx - constant(safe-area-inset-bottom));
	height: calc(100vh - 200rpx - env(safe-area-inset-bottom));
}
/*  #endif  */

.text-ellipsis{
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
}
:deep(.u-swipe-action-item__right) {
    padding: 2rpx;
}
:deep(.u-swipe-action-item__right__button__wrapper){
    padding:0 10rpx !important;
}
:deep(.u-swipe-action-item__right__button__wrapper__text){
    font-size:24rpx !important;
}
:deep(.u-tabbar .u-tabbar__content) {
    z-index: 99999 !important;
}
</style>
<style>
/*  #ifndef  H5  */
.many-goods :deep(.u-transition) {
	bottom: 100px !important;
}
/*  #endif  */
.goods-sku :deep(.u-transition) {
    z-index: 999999 !important;
}
</style>