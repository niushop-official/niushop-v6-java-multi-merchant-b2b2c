<template>
    <view @touchmove.prevent.stop>
        <u-popup class="amount-detail-popup" mode="bottom"  :show="goodsPopup" :closeable="true" @close="goodsPopup = false">
            <view class="min-h-[600rpx] popup-common bg-[#fff] mb-[20rpx] px-[24rpx]">
                <view class="title font-700 !py-[30rpx]">金额明细</view>
                <!-- 商品图片+价格横向滚动区 -->
                <scroll-view :scroll-x="true" class="whitespace-nowrap card-template bg-[#f8f8f8] mb-[20rpx]">
                    <view class="w-full box-border flex items-center gap-x-[10rpx]">
                        <view  class="flex flex-col items-center flex-shrink-0" style="pointer-events: none;" v-for="(item, index) in goodsList" :key="item.sku_id || index">
                            <up-image  width="160rpx"  height="160rpx"  :radius="'12rpx'" style="pointer-events: none;" :src="img(item.sku_image)" mode="aspectFill">
                                <template #error>
                                    <image class="w-[160rpx] h-[160rpx] rounded-[12rpx]" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"
                                    ></image>
                                </template>
                            </up-image>
                            <view class="flex items-center justify-between w-[160rpx]">
                                <view class="text-[var(--price-text-color)] price-font truncate max-w-[100rpx]">
                                    <text class="text-[24rpx]">￥</text>
                                    <text class="text-[36rpx] font-500">{{ parseFloat(item.price).toFixed(2).split('.')[0] }}</text>
                                    <text class="text-[24rpx] font-500">.{{ parseFloat(item.price).toFixed(2).split('.')[1] }}</text>
                                </view>
                                <text class="text-[24rpx] text-[#666">×{{ item.num }}</text>
                            </view>
                        </view>
                    </view>
                </scroll-view>

                <!-- 金额明细卡片 -->
                <view class="card-template bg-[#F8F8F8] mb-[20rpx]">
                    <!-- 商品总价 -->
                    <view class="card-template-item justify-between">
                        <text class="text-[28rpx] font-500">商品总价</text>
                        <text class="text-[32rpx] font-500 price-font">¥{{ localCalculateData.basic.goods_money }}</text>
                    </view>
                    
                    <!-- 共减 -->
                    <view class="card-template-item justify-between !mb-[10rpx]">
                        <text class="text-[28rpx] font-500">共减</text>
                        <text class="text-[32rpx] font-500 price-font text-[var(--price-text-color)]">¥{{ parseFloat(localCalculateData.basic.discount_money).toFixed(2) }}</text>
                    </view>
                    
                    <!-- 提示文字 -->
                    <view class="text-[22rpx] text-[#999] mb-[34rpx]">
                        以下优惠不包含优惠券,请在结算页查看
                    </view>
                    
                    <!-- 店铺优惠 -->
                     <view class="card-template-item justify-between !mb-[0rpx]">
                        <text class="text-[24rpx]">店铺优惠</text>
                        <view class="flex items-center" @click="shopDiscount = !shopDiscount">
                            <text class="text-[30rpx] text-[var(--price-text-color)] price-font">-¥{{ parseFloat(calculateData.basic.shop_discount_money).toFixed(2) }}</text>
                            <text class="nc-iconfont text-color-light3 font26" :class="{'nc-icon-shangV6xx-1': shopDiscount, 'nc-icon-xiaV6xx': !shopDiscount }"></text>
                        </view>
                    </view>
                    <view class="card-template p-[20rpx] mt-[20rpx]" v-show="shopDiscount">   
                        <view class="card-template-item justify-between">
                            <text class="text-[24rpx] text-[#666]">满减优惠</text>
                            <text class="text-[26rpx] price-font">-¥{{ calculateData.basic.full_reduction_money }}</text>
                        </view>
                    </view>
                </view>

                <!-- 赠品区域 -->
                <view class="card-template bg-[#F8F8F8]" v-if="giftGoodsList.length">
                    <view class="card-template-item justify-between justify-between !mb-[20rpx]">
                        <text class="text-[30rpx] font-700">赠品</text>
                        <view class="flex items-baseline">
                            <text class="text-[26rpx]">共{{ giftGoodsList.length }}件</text>
                        </view>
                    </view>
                    
                    <!-- 赠品横向滚动列表 -->
                    <scroll-view :scroll-x="true" class="whitespace-nowrap">
                        <view class="w-full box-border flex gap-x-[16rpx]">
                            <view 
                                v-for="(cItem, index) in giftGoodsList" 
                                :key="cItem.sku_id || index" 
                                style="pointer-events: none;"
                                class="flex flex-col items-center flex-shrink-0"
                            >
                                <up-image 
                                    style="pointer-events: none;"
                                    radius="12rpx" 
                                    width="120rpx" 
                                    height="120rpx" 
                                    :src="img(cItem.sku_image)" 
                                    mode="aspectFill"
                                >
                                    <template #error>
                                        <image 
                                            class="w-[120rpx] h-[120rpx] rounded-[12rpx]" 
                                            :src="img('static/resource/images/diy/shop_default.jpg')" 
                                            mode="aspectFill"
                                        />
                                    </template>
                                </up-image>
                                <view class="text-[20rpx] text-[#666] mt-[8rpx] text-center max-w-[100rpx] truncate">
                                    {{ cItem.goods.goods_name }}
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
import { ref, watch } from 'vue';
import { img, redirect, getToken } from '@/utils/common'

// 接收父组件传入的计算数据
const props = defineProps({
  calculateData: {
    type: Object,
    default: () => ({
      basic: {
        goods_money: 0.00,       // 商品总价
        discount_money: 0.00,     // 共减金额
        mall_discount_money: 0.00,     // 店铺优惠
        shop_discount_money: 0.00,     // 平台优惠
        full_reduction_money: 0.00,   // 满减优惠
        official_subtotal: 0.00, // 官方直降小计
        cross_shop_discount: 0.00,// 跨店满减
        cross_shop_subtotal: 0.00,// 跨店满减小计
      },
      official_goods_image: '',    // 官方直降商品图片
      cross_shop_goods_image: '',  // 跨店满减商品图片
      order_list: {}
    })
  }
})

// 响应式数据
const goodsPopup = ref(false)       // 弹窗显隐控制
const goodsList = ref([])           // 商品列表
const giftGoodsList = ref([])       // 赠品列表
const localCalculateData = ref({ ...props.calculateData }) // 本地计算数据
const shopDiscount =ref(true)


// 暴露方法给父组件调用
defineExpose({
  goodsPopup,
  open: () => {
    goodsPopup.value = true
  },
  close: () => {
    goodsPopup.value = false
  }
})

// 深度监听计算数据变化，更新赠品列表和折扣金额
watch(() => props.calculateData, (newData) => {
  // 深拷贝新数据到本地
  localCalculateData.value = JSON.parse(JSON.stringify(newData))
  
  // 计算折扣金额
  if (newData && newData.basic) {
    localCalculateData.value.basic.discount_money = parseFloat(newData.basic?.mall_discount_money || 0) + parseFloat(newData.basic?.shop_discount_money || 0)
  }
  
  // 更新赠品列表
  giftGoodsList.value = []
  if (newData && newData.order_list) {
    Object.values(newData.order_list || {}).forEach((item: any) => {
      giftGoodsList.value.push(...(item.basic?.full_gift_sku_list || []))
    })
  }

    // 初始化商品列表并添加isSelected字段（默认未选中）
  goodsList.value = []
  Object.values(newData.order_list || {}).forEach((item: any) => {
    const goodsData = Object.values(item.goods_data) || [];
    // 为每个商品添加isSelected字段，默认未选中
    const goodsWithSelect = goodsData.map((good: any) => ({
      ...good,
      isSelected: false // 初始未选中
    }));
    goodsList.value.push(...goodsWithSelect);
  })
}, { deep: true, immediate: true });
</script>

<style lang="scss">
:deep(.amount-detail-popup .u-slide-up-enter-to.u-slide-up-enter-active){
    bottom: 100px !important;
}
:deep(.amount-detail-popup .u-transition.u-fade-enter-to.u-fade-enter-active){
    bottom: 100px !important;
}
.text-color-light3 {
    color: var(--text-color-light3) !important;
}
.font26 {
    font-size: 26rpx !important;
}
</style>