<template>
    <!-- 满减 -->
    <view @touchmove.prevent.stop>
        <u-popup class="manjian-popup" :show="youhuiShow" @close="youhuiShow = false" zIndex="999999">
            <view class="min-h-[480rpx] popup-common bg-[#f5f5f5]" @touchmove.prevent.stop>
                <view class="title !pt-[30rpx] !pb-[40rpx]">优惠</view>
                <view class="px-[var(--popup-sidebar-m)] pb-[30rpx]">
                    <scroll-view :scroll-y="true" class="h-[800rpx]" v-if="skuPromotionInfo && skuPromotionInfo.price_details">
                        <view>
                            <view class="flex justify-center">
                                <view class="bg-[var(--primary-color-light)] p-[20rpx] rounded-[16rpx] flex flex-col justify-center">
                                    <view class="text-center text-[#FF0000] leading-[40rpx]">
                                        <text class="text-[36rpx] price-font">￥{{ skuPromotionInfo.price_details.hand_price }}</text>
                                    </view>
                                    <view class="text-[22rpx] mt-[6rpx] text-primary leading-[30rpx] text-center">预估到手</view>
                                </view>
                            </view>
                            <view class="discount-box" v-if="skuPromotionInfo.price_details.price_list && Object.keys(skuPromotionInfo.price_details.price_list).length > 0">
                                <view class="text-[24rpx] font-500 mb-[20rpx]">当前购买可使用以下优惠</view>
                                <view class="flex justify-center items-center">
                                    <view class="w-[160rpx] h-[106rpx] flex flex-col items-center justify-center bg-[var(--primary-color-light)] rounded-[16rpx] relative z-10">
                                        <view class="price-font text-[32rpx] text-primary">￥{{ skuPromotionInfo.price_details.price_list.original_price.price }}</view>
                                        <text class="text-primary text-[20rpx] leading-[30rpx]">{{ skuPromotionInfo.price_details.price_list.original_price.desc }}</text>
                                    </view>
                                    <view class="flex items-center" v-if="Number(skuPromotionInfo.price_details.price_list.admin_promotion?.price)">
                                        <view class="text-[#FF0000] mx-[30rpx] nc-iconfont nc-icon-jianV6xx"></view>
                                        <view class="w-[160rpx] h-[106rpx] flex flex-col items-center justify-center bg-[var(--primary-color-light)] rounded-[16rpx] relative z-10">
                                            <view class="price-font text-[32rpx] text-primary">￥{{ skuPromotionInfo.price_details.price_list.admin_promotion.price }}</view>
                                            <text class="text-primary text-[20rpx] leading-[30rpx]">{{ skuPromotionInfo.price_details.price_list.admin_promotion.desc }}</text>
                                        </view>
                                    </view>
                                    <view class="flex items-center" v-if="Number(skuPromotionInfo.price_details.price_list.site_promotion?.price)">
                                        <view class="text-[#FF0000] mx-[30rpx] nc-iconfont nc-icon-jianV6xx"></view>
                                        <view class="w-[160rpx] h-[106rpx] flex flex-col items-center justify-center bg-[var(--primary-color-light)] rounded-[16rpx] relative z-10">
                                            <view class="price-font text-[32rpx] text-primary">￥{{ skuPromotionInfo.price_details.price_list.site_promotion.price }}</view>
                                            <text class="text-primary text-[20rpx] leading-[30rpx]">{{ skuPromotionInfo.price_details.price_list.site_promotion.desc }}</text>
                                        </view>
                                    </view>
                                </view>
                            </view>
                        </view>
                        <view v-if="skuPromotionInfo.activity_list && Object.keys(skuPromotionInfo.activity_list).length">
                            <view class="text-[24rpx] font-500 mt-[30rpx] mb-[20rpx] leading-[34rpx]">商品可参与以下优惠促销活动</view>
                            <view class="card-template !py-0 bg-[#fff]">
                                <view class="py-[30rpx] flex justify-between items-center border-0 border-b-[2rpx] border-solid border-[#eee]" v-if="skuPromotionInfo.activity_list.discount" @click="toDetail('discount', skuPromotionInfo.activity_list.discount.activity_id)">
                                    <view class="flex flex-1">
                                        <view class="h-[40rpx] text-[18rpx] font-500 text-primary border-[1rpx] flex justify-center items-center border-solid border-primary px-[8rpx] rounded-[8rpx] box-border">{{ skuPromotionInfo.activity_list.discount.name }}</view>
                                        <view class="ml-[14rpx] flex-1">
                                            <view class="text-[24rpx] text-[#333] font-500 leading-[36rpx]">{{ skuPromotionInfo.activity_list.discount.title }}</view>
                                            <view class="text-[20rpx] text-[#999] font-500 mt-[8rpx]">活动时间：{{ skuPromotionInfo.activity_list.discount.activity_time }}</view>
                                        </view>
                                    </view>
                                    <text class="!text-[24rpx] text-[#999] iconfont iconxiangyoujiantou"></text>
                                </view>
                                <view class="py-[30rpx] flex justify-between items-center border-0 border-b-[2rpx] border-solid border-[#eee]" v-if="skuPromotionInfo.activity_list.point_deduct">
                                    <view class="flex flex-1">
                                        <view class="h-[40rpx] text-[18rpx] font-500 text-primary border-[1rpx] flex justify-center items-center border-solid border-primary px-[8rpx] rounded-[8rpx] box-border">{{ skuPromotionInfo.activity_list.point_deduct.name }}</view>
                                        <view class="text-[24rpx] text-[#333] font-500 leading-[36rpx] ml-[14rpx] flex-1">{{ skuPromotionInfo.activity_list.point_deduct.title }}</view>
                                    </view>
                                    <!-- <text class="!text-[24rpx] text-[#999] iconfont iconxiangyoujiantou"></text> -->
                                </view>
                                <view class="py-[30rpx] flex justify-between items-center border-0 border-b-[2rpx] border-solid border-[#eee]" v-if="skuPromotionInfo.activity_list.full_reduction" @click="toDetail('full_reduction', skuPromotionInfo.activity_list.full_reduction.activity_id)">
                                    <view class="flex flex-1">
                                        <view class="h-[40rpx] text-[18rpx] font-500 text-primary border-[1rpx] flex justify-center items-center border-solid border-primary px-[8rpx] rounded-[8rpx] box-border">{{ skuPromotionInfo.activity_list.full_reduction.name }}</view>
                                        <view class="text-[24rpx] text-[#333] font-500 leading-[36rpx] ml-[14rpx] flex-1">{{ skuPromotionInfo.activity_list.full_reduction.title }}</view>
                                    </view>
                                    <text class="!text-[24rpx] text-[#999] iconfont iconxiangyoujiantou"></text>
                                </view>
                                <view class="py-[30rpx] flex items-start" v-if="skuPromotionInfo.activity_list.full_gift" @click="toDetail('full_gift', skuPromotionInfo.activity_list.full_gift.activity_id)">
                                    <view class="h-[40rpx] text-[18rpx] font-500 text-primary border-[1rpx] flex justify-center items-center border-solid border-primary px-[8rpx] rounded-[8rpx] box-border"> {{getGiftNum(skuPromotionInfo.activity_list.full_gift.gift_list)}}件赠品</view>
                                    <view class="flex-1 ml-[20rpx]">
                                        <view class="text-[#333] text-[24rpx] font-500 leading-[34rpx]">
                                            {{ skuPromotionInfo.activity_list.full_gift.title }}
                                        </view>
                                        <scroll-view :scroll-x="true" class="whitespace-nowrap mt-[20rpx]" @touchmove.prevent.stop v-if="skuPromotionInfo.activity_list.full_gift.gift_list && skuPromotionInfo.activity_list.full_gift.gift_list.length">
                                            <view class="w-[488rpx] flex gap-x-[16rpx]">
                                                <view class="flex items-center" v-for="(item, index) in skuPromotionInfo.activity_list.full_gift.gift_list" :key="index">
                                                    <view class="w-[100rpx] h-[100rpx] overflow-hidden relative rounded-[var(--goods-rounded-small)]">
                                                        <up-image width="100rpx" height="100rpx" :radius="'var(--goods-rounded-small)'" :src="img(item.goods_cover)" model="aspectFill" class="align-middle">
                                                            <template #error>
                                                                <image class="rounded-[var(--goods-rounded-small)] overflow-hidden w-[100rpx] h-[100rpx]" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                                                            </template>
                                                        </up-image>
                                                        <view class="absolute left-0 top-0 bottom-0 right-0 box-border flex items-end">
                                                            <view class="w-full bg-[rgba(141,141,141,0.7)] text-[#fff] text-[20rpx] text-right py-[2rpx] px-[6rpx] box-border"> ×{{ item.num }}</view>  
                                                        </view>
                                                    </view>
                                                </view>
                                            </view>
                                        </scroll-view>
                                    </view>
                                    <text class="!text-[24rpx] text-[#999] iconfont mt-[6rpx] iconxiangyoujiantou"></text>
                                </view>
                            </view>
                        </view>
                        <!-- <view class="text-[24rpx] text-[var(--text-color-light9)] font-500 mt-[20rpx] mb-[20rpx] leading-[34rpx]">以上优惠为系统根据可享活动/优惠的预估值</view>
                        <view v-if="skuPromotionInfo.coupon_list && skuPromotionInfo.coupon_list.length">
                           <view class="text-[24rpx] font-500 mt-[30rpx] mb-[20rpx] leading-[34rpx]">可领取优惠券</view>
                           <view class="mb-[30rpx] bg-[#fff] flex items-center  rounded-[var(--rounded-small)]"
                                v-for="(item, index) in skuPromotionInfo.coupon_list" :key="index">
                                <view class="flex flex-col items-center my-[20rpx] w-[200rpx] border-0 border-r-[2rpx] border-dashed border-[rgba(0,0,0,.1)]">
                                    <view class="text-xs price-font">
                                        <text class="text-[28rpx]">￥</text>
                                        <text class="text-[48rpx]">{{ Number(item.price) }}</text>
                                    </view>
                                    <text class="text-xs mt-[12rpx]">{{ Number(item.min_condition_money) ? ('满' + Number(item.min_condition_money) + '元可用') : '无门槛' }}</text>
                                </view>
                                <view class="ml-[20rpx] flex-1 flex flex-col py-[20rpx]">
                                    <text class="text-xs font-500">{{ item.title }}</text>
                                    <text class="text-xs text-[var(--text-color-light6)] mt-[12rpx]">{{ item.valid_type == 1 && ('领取之日起' + item.length + '天内有效') || item.valid_type == 2 && ('领取之日起至' + item.valid_end_time) }}</text>
                                </view>
                                <text v-if="item.btnType === 'collecting'" class="bg-[var(--primary-color)] mr-[20rpx] w-[106rpx] box-border text-center text-[#fff] h-[50rpx] text-[22rpx] px-[20rpx] leading-[50rpx] rounded-[100rpx]" @click="getCouponFn(item, index)">领取</text>
                                <text v-else class="!bg-[var(--primary-help-color4)] mr-[20rpx] text-[#fff] mr-[20rpx] h-[50rpx] text-[22rpx] px-[20rpx] leading-[50rpx] rounded-[100rpx]">{{ item.btnType === 'collected' ? '已领完' : '已领取' }}</text>
                            </view>
                        </view> -->
                    </scroll-view>
                </view>
            </view>
        </u-popup>
        <u-popup :show="couponShow" mode="center" round="8" @close="couponShow = false" :overlay="false" zIndex="9999999">
            <view class="p-[20rpx] rounded-[12rpx] bg-[#333] text-[#fff] text-[24rpx]">领取成功</view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { img, deepClone, redirect } from '@/utils/common'
import { useLogin } from '@/hooks/useLogin'
import useMemberStore from '@/stores/member'
import { getGoodsSkuPromotionDetail } from '@/addon/mall/api/goods';
import { getCoupon } from '@/app/api/marketing';

// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

const youhuiShow = ref(false);
const skuPromotionInfo = ref<any>({});  //商品信息
const skuId = ref('');
const open = (data: any,sku_id: any) => {
    if (!data || !data.price_details) return
    skuPromotionInfo.value = deepClone(data)
    skuPromotionInfo.value.coupon_list = (data.coupon_list || []).map((el: any) => {
        if (el.sum_count != -1 && el.receive_count === el.sum_count) {
            el.btnType = 'collected'//已领完
        }
        if (!userInfo.value) {
            el.btnType = 'collecting'//领用
        } else {
            if (el.is_receive && el.limit_count === el.member_receive_count) {
                el.btnType = 'using'//去使用
            } else {
                el.btnType = 'collecting'//领用
            }
        }
        return el
    });
    skuId.value = sku_id
    youhuiShow.value = true
}
//赠品数量
const getGiftNum = (data: any) => { 
    if (!Array.isArray(data) || data.length === 0) return 0;
    return data.reduce((total, item) => {
        const giftNum = Number(item.num) || 0;
        return total + giftNum;
    }, 0);
};

// 优惠券



// 领取优惠券
const couponShow = ref(false);
let timer: any = null;
const getCouponFn = (data: any, index: any) => {
    // 检测是否登录
    if (!userInfo.value) {
        useLogin().setLoginBack({
            url: '/addon/mall/pages/goods/detail',
            param: { sku_id: skuId.value }
        })
        return false
    }
    getCoupon({
        coupon_id: data.id || '',
        number: 1,
    }).then(res => {
        couponShow.value = true
        nextTick(() => {
            clearTimeout(timer)
            timer = setTimeout(() => {
                couponShow.value = false
            }, 1500)
        })
        getGoodsSkuPromotionDetailFn();
    })
}

const getGoodsSkuPromotionDetailFn = () => {
    getGoodsSkuPromotionDetail({
        sku_id: skuId.value || '',
    }).then((res: any) => {
        if (!res.data?.coupon_list) return
        skuPromotionInfo.value.coupon_list = res.data.coupon_list.map((el: any) => {
            if (el.sum_count != -1 && el.receive_count === el.sum_count) {
                el.btnType = 'collected'//已领完
            }
            if (!userInfo.value) {
                el.btnType = 'collecting'//领用
            } else {
                if (el.is_receive && el.limit_count === el.member_receive_count) {
                    el.btnType = 'using'//去使用
                } else {
                    el.btnType = 'collecting'//领用
                }
            }
            return el
        });
    })
}
const toDetail = (type: any,activity_id: any) => { 
    let goods_data = {
       type,
       id:activity_id
    }
    uni.setStorageSync('manjianData', goods_data)
	redirect({ url: '/addon/mall/pages/goods/gift'})
}
defineExpose({
    open
})
</script>

<style lang="scss" scoped>
::v-deep .manjian-popup .u-slide-up-enter-to {
    z-index: 999999 !important;
}
/* 外层：三角形边框色（和优惠框边框一致） */
.discount-box {
  position: relative;
  border: 1rpx solid var(--primary-color);
  border-radius: 16rpx;
  padding: 30rpx 24rpx;
  margin: 20rpx 0;
}

/* 外层三角形（边框色） */
.discount-box::before {
content: "";
    position: absolute;
    top: -1rpx;
    left: 50%;
    width: 18rpx;
    height: 19rpx;
    border: 1rpx solid var(--primary-color);
    background-color: #f5f5f5;
    box-sizing: border-box;
    -webkit-transform: rotate(45deg) translate(-50%);
    transform: rotate(45deg) translate(-50%)
}

/* 内层三角形（背景色，覆盖外层内侧） */
.discount-box::after {
    content: "";
    position: absolute;
    top: 0;
    left: 50%;
    width: 18px;
    height: 18px;
    background-color: #f5f5f5;
    -webkit-transform: translateX(-50%);
    transform: translateX(-50%);
}

</style>
