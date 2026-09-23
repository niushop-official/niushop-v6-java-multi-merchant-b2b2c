<template>
    <!-- 满减 -->
    <view @touchmove.prevent.stop>
        <u-popup class="manjian-popup" :show="manjianShow" @close="manjianShow = false" zIndex="999999">
            <view class="min-h-[480rpx] popup-common bg-[#f5f5f5]" @touchmove.prevent.stop>
                <view class="title !pt-[30rpx] !pb-[40rpx]">优惠</view>
                <view class="px-[var(--popup-sidebar-m)] pb-[30rpx]">
                    <scroll-view :scroll-y="true" class="h-[800rpx]">
                        <view>
                            <view class="flex justify-center" v-if="Object.keys(enoughRule).length > 0">
                                <view class="bg-[var(--primary-color-light)] p-[20rpx] rounded-[16rpx] flex flex-col justify-center">
                                    <view class="text-center text-[#FF0000] leading-[40rpx]">
                                        <text class="text-[36rpx] price-font">￥{{ finalPrice }}</text>
                                    </view>
                                    <view class="text-[22rpx] mt-[6rpx] text-primary leading-[30rpx] text-center">预估到手</view>
                                </view>
                            </view>
                            <view class="discount-box" v-if="Object.keys(enoughRule).length > 0">
                                <view class="text-[24rpx] font-500 mb-[20rpx]">当前购买可使用以下优惠</view>
                                <view class="flex justify-center items-center">
                                    <view class="w-[160rpx] h-[106rpx] flex flex-col items-center justify-center bg-[var(--primary-color-light)] rounded-[16rpx] relative z-10">
                                        <view class="price-font text-[32rpx] text-primary">￥{{ originPrice }}</view>
                                        <text class="text-primary text-[20rpx] leading-[30rpx]">商品售价</text>
                                    </view>
                                    <view class="text-[#FF0000] mx-[30rpx] nc-iconfont nc-icon-jianV6xx"></view>
                                    <view class="w-[160rpx] h-[106rpx] flex flex-col items-center justify-center bg-[var(--primary-color-light)] rounded-[16rpx] relative z-10">
                                        <view class="price-font text-[32rpx] text-primary">￥{{ enoughRule.deduct_price }}</view>
                                        <text class="text-primary text-[20rpx] leading-[30rpx]">活动优惠</text>
                                    </view>
                                </view>
                            </view>
                        </view>
                        <view>
                            <view class="text-[24rpx] font-500 mt-[30rpx] mb-[20rpx] leading-[34rpx]">商品可参与以下优惠促销活动</view>
                            <view class="card-template !py-0 bg-[#fff]">
                                <view class="py-[30rpx] flex justify-between items-center border-0 border-b-[2rpx] border-solid border-[#eee]" v-if="detail.full_reduction && Object.keys(fullReductionshowRule).length > 0" @click="toDetail('full_reduction', detail.full_reduction)">
                                    <view class="flex flex-1">
                                        <view class="h-[40rpx] text-[24rpx] font-500 text-primary border-[1rpx] flex justify-center items-center border-solid border-primary px-[8rpx] rounded-[8rpx] box-border">满减满折</view>
                                        <view class="text-[28rpx] text-[#333] font-500 leading-[36rpx] ml-[14rpx] flex-1">
                                            <text v-if="detail.full_reduction?.rule_type=='cycle'">每</text>满<text>{{ fullReductionshowRule.amount }}</text>{{ detail.full_reduction?.type == 'amount' ? '元' : '件' }}，{{ detail.full_reduction?.promotion_type == 'reduction' ? '减' : '打' }}<text>{{ fullReductionshowRule.reduce }}</text>{{ detail.full_reduction?.promotion_type == 'reduction' ? '元' : '折' }}
                                        </view>
                                    </view>
                                    <text class="!text-[24rpx] text-[#999] iconfont iconxiangyoujiantou"></text>
                                </view>
                                <view class="py-[30rpx] flex items-start" v-if="detail.full_gift && Object.keys(fullGiftShowRule).length > 0" @click="toDetail('full_gift', detail.full_gift)">
                                    <view class="h-[40rpx] text-[24rpx] font-500 text-primary border-[1rpx] flex justify-center items-center border-solid border-primary px-[8rpx] rounded-[8rpx] box-border"> {{getGiftNum(fullGiftShowRule.gift_sku_data)}}件赠品</view>
                                    <view class="flex-1 ml-[20rpx]">
                                        <view class="text-[#333] text-[28rpx] font-500 leading-[34rpx]">
                                            <text v-if="detail.full_gift?.rule_type=='cycle'">每</text>满<text>{{ fullGiftShowRule.amount }}</text>元，送以下赠品
                                        </view>
                                        <view class="flex flex-wrap gap-[16rpx] mt-[20rpx]">
                                            <view class="flex items-center" v-for="(item, index) in fullGiftShowRule.gift_sku_data" :key="index">
                                                <view class="w-[100rpx] h-[100rpx] overflow-hidden relative rounded-[var(--goods-rounded-small)]">
                                                    <up-image width="100rpx" height="100rpx" :radius="'var(--goods-rounded-small)'" :src="img(item.sku_image)" model="aspectFill">
                                                        <template #error>
                                                            <image class="rounded-[var(--goods-rounded-small)] overflow-hidden w-[100rpx] h-[100rpx]" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                                                        </template>
                                                    </up-image>
                                                    <view class="absolute left-0 bottom-0 bg-[rgba(141,141,141,0.7)] text-[#fff] text-[20rpx] w-full text-right py-[2rpx] px-[6rpx] box-border">×{{ item.num }}</view>
                                                </view>
                                            </view>
                                        </view>
                                    </view>
                                    <text class="!text-[24rpx] text-[#999] iconfont mt-[6rpx] iconxiangyoujiantou"></text>
                                </view>
                            </view>
                        </view>
                    </scroll-view>
                </view>
            </view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { computed, nextTick, ref } from 'vue';
import { img, deepClone, redirect } from '@/utils/common'
import { cloneDeep } from 'lodash-es'
import { t } from '@/locale'
import { getShopMarketingInfo } from '@/addon/mall/api/goods';

const manjianShow = ref(false);
const enoughRule = ref({});  // 达到的规则
const fullReductionshowRule = ref({});   // 满减下面展示的规则
const fullGiftShowRule = ref({});   // 满赠下面展示的规则
const detail = ref({});  // 活动详情
const originPrice = ref(0);
const finalPrice  = computed(() => {
    return Number(originPrice.value - (enoughRule.value?.deduct_price || 0)).toFixed(2)
});
const info = ref({});  //商品信息
const open = (data: any) => {
    info.value = data;
    originPrice.value =Number(data.price) * Number(data.num);
    console.log(data);
    getShopMarketingInfo({
        market_ids :data.market_ids ,
        price: data.price,
        num: data.num
    }).then(res => {
        detail.value = res.data;
        // 查找 数组中第一个 is_enough:1 的对象 赋值 满减
        enoughRule.value = detail.value.full_reduction?.rule_json.find(item => item.is_enough === 1) || {};
        // 查找 数组中第一个 is_show:1 的对象 赋值
        fullReductionshowRule.value = detail.value.full_reduction?.rule_json.find(item => item.is_show === 1) || {};
        fullGiftShowRule.value = detail.value.full_gift?.rule_json.find(item => item.is_show === 1) || {};
        nextTick(() => {
            // 打开弹窗
            manjianShow.value = true;
        })
    }) 
}
//赠品数量
const getGiftNum = (data) => { 
    if (!Array.isArray(data) || data.length === 0) return 0;
    return data.reduce((total, item) => {
        const giftNum = Number(item.num) || 0;
        return total + giftNum;
    }, 0);
};
const toDetail = (type: any,data: any) => { 
    let goods_data = {
       type,
       id:data.id
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
  padding: 16rpx 20rpx;
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
