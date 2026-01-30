<template>
    <view :style="themeColor()">   
        <view class="bg-[var(--page-bg-color)]" v-if="!loading">
            <view v-if="!loading" class="pb-20rpx">
                <view style="min-height: calc(50vh  + 150rpx);" v-if="isShowLocalDelivery">
                    <!-- #ifdef MP-WEIXIN -->
                    <top-tabbar :data="topTabbarData" :scrollBool="topTabarObj.getScrollBool()" />
                    <!-- #endif -->
                    <map-show ref="mapShowRef" tradeType="mall" :tradeNo="detail.order_no" @comfirm="handleComfirm"  v-if="detail.order_no" />
                </view>
                
                <view v-if="detail.status_name && !isShowLocalDelivery" class="pl-[40rpx] pr-[50rpx] bg-linear pb-[100rpx]">
                    <!-- #ifdef MP-WEIXIN -->
                    <top-tabbar :data="topTabbarData" :scrollBool="topTabarObj.getScrollBool()" />
                    <!-- #endif -->
                    <view class="flex justify-between items-center pt-[40rpx]">
                        <view class="text-[#fff] text-[36rpx] font-500 leading-[42rpx]">
                            <text>{{ detail.status_name.name }}</text>
                        </view>
                        <image v-if="detail.status == 1" class="w-[180rpx] h-[140rpx]" :src="img('addon/mall/detail/payment.png')"  mode="aspectFit"/>
                        <image v-if="detail.status == 2" class="w-[180rpx] h-[140rpx]" :src="img('addon/mall/detail/deliver_goods.png')" mode="aspectFit"/>
                        <image v-if="detail.status == 3" class="w-[180rpx] h-[140rpx]" :src="img('addon/mall/detail/receive.png')" mode="aspectFit"  />
                        <image v-if="detail.status == 5" class="w-[180rpx] h-[140rpx]" :src="img('addon/mall/detail/complete.png')" mode="aspectFit" />
                        <image v-if="detail.status == -1" class="w-[180rpx] h-[140rpx]" :src="img('addon/mall/detail/close.png')"  mode="aspectFit" />
                         <image v-if="detail.status == 6" class="w-[180rpx] h-[140rpx]" :src="img('addon/pintuan/detail/pintuaning.png')"  mode="aspectFit" />
                    </view>
                </view>
               <view class="sidebar-margin card-template mt-[-86rpx] relative" v-if="detail.delivery_type == 'local_delivery' && isShowLocalDelivery">
                    <view class="flex items-center" @click="localeDeliveryFn">
                        <view class="flex-1">
                            <view class="mb-[20rpx] flex items-baseline leading-[40rpx] min-h-[46rpx]">
                                <text class="text-[26rpx] font-500 mr-[10rpx]">预计</text>
                                <text class="text-[46rpx] font-600">{{ expectedFinishTime }}</text>
                            </view>
                            <!-- <view class="mb-[20rpx] leading-[40rpx] text-[30rpx] font-500">{{ detail.local_delivery_status_name }}</view> -->
                            
                            
                            <view class="flex">
                                <text class="text-[26rpx] font-500 mr-[10rpx]">送至</text>
                                <view class="flex flex-col">
                                    <text class="text-[26rpx] mb-[12rpx] leading-[26rpx]">{{ detail.taker_full_address }}</text>
                                    <view class="flex leading-[38rpx] overflow-hidden">
                                        <text class="text-[26rpx]">{{ detail.taker_name }}</text>
                                        <text class="text-[26rpx] ml-[15rpx]">{{ detail.taker_mobile }}</text>
                                    </view>
                                </view>
                            </view>
                        </view>
                        <text class="nc-iconfont nc-icon-youV6xx ml-[10rpx]" v-if="detail.delivery_type == 'local_delivery' && ['3','5'].indexOf(detail.status) != -1"></text>
                    </view>
                    
                </view>
                <view class="sidebar-margin mt-[-86rpx] card-template" v-if="detail.delivery_type != 'virtual' && !isShowLocalDelivery">
                    <view v-if="detail.delivery_type == 'express'">
                        <view class="text-[#303133] flex">
                            <text class="nc-iconfont nc-icon-dizhiguanliV6xx text-[40rpx] pt-[12rpx] mr-[20rpx]"></text>
                            <view class="flex flex-col">
                                <view class="text-[30rpx] leading-[38rpx] overflow-hidden">
                                    <text>{{ detail.taker_name }}</text>
                                    <text class="ml-[15rpx]">{{ detail.taker_mobile }}</text>
                                </view>
                                <view class="mt-[12rpx] text-[24rpx] text-[var(--text-color-light6)] leading-[26rpx]">
                                    {{detail.taker_full_address.split(detail.taker_address)[0]}}{{detail.taker_address}}
                                </view>
                            </view>
                        </view>
                        
                    </view>
                    <view v-if="detail.delivery_type == 'store'">
                        <view class="flex items-baseline pb-[22rpx] border-0 border-b-[1px] border-solid border-[#f5f5f5]">
                            <text class="nc-iconfont nc-icon-huocheV6mm text-primary text-[28rpx]"></text>
                            <text class="text-[28rpx] text-primary mx-[12rpx]">到店自提</text>
                            <text class="text-[24rpx] text-[var(--text-color-light9)]">请尽快前往自提点取货</text>
                        </view>
                        <view class="flex items-center justify-between mt-[14rpx] mb-[18rpx]">
                            <view class="max-w-[428rpx]">
                                <view class="text-[30rpx] font-500 text-[#303133] mb-[12rpx]">{{ detail.store.store_name }}</view>
                                <view class="text-[24rpx] text-[var(--text-color-light6)] leading-[1.4] mb-[12rpx]">{{ detail.store.full_address }}</view>
                                <view class="text-[22rpx] text-[var(--text-color-light9)]">营业时间：{{ detail.store.trade_time }}</view>
                            </view>
                            <view class="flex flex-col items-center w-[188rpx] h-[104rpx]" :style="{ backgroundImage: 'url(' + img('addon/mall/detail/map.png') + ')',backgroundSize: 'cover', backgroundRepeat: 'no-repeat'}">
                                <view class="distance-wrap using-hidden bg-[#fff] py-[4rpx] px-[16rpx] rounded-[6rpx] text-[20rpx] text-[#333]" v-if="detail.store.distance">距您{{ distance(detail.store.distance) }}</view>
                                <view class="triangle"></view>
                                <view class="w-[54rpx] h-[50rpx] bg-[#fff] p-[6rpx] rounded-[6rpx] box-border">
                                    <up-image class="overflow-hidden" width="40rpx" height="38rpx" radius="6rpx" :src="img(detail.site.front_end_logo)" model="aspectFill">
                                        <template #error>
                                            <image :src="img('addon/mall/shop/shop_default.png')" class="w-[40rpx] h-[38rpx] align-middle rounded-[6rpx]"></image>
                                        </template>
                                    </up-image>
                                </view>
                                <view class="triangle"></view>
                                <view class="w-[6rpx] h-[6rpx] bg-primary rounded-full"></view>
                            </view>
                        </view>
                        <view class="px-[72rpx] pt-[24rpx] flex items-center justify-between border-0 border-t-[1rpx] border-solid border-[#eee]">
                            <view class="flex items-center" @click="callFn">
                                <text class="nc-iconfont nc-icon-dianhuaV6xx text-[24rpx] mr-[8rpx]"></text>
                                <text class="text-[24rpx]">联系自提点</text>
                            </view>
                            <view class="flex items-center" @click="getDistance">
                                <text class="nc-iconfont nc-icon-fenxiangV6xx1 text-[24rpx] mr-[8rpx]"></text>
                                <text class="text-[24rpx]">导航至自提点</text>
                            </view>
                        </view>
                    </view>
                    <view class="flex" v-if="detail.delivery_type == 'local_delivery'">
                        <text @click="getAddress" class="nc-iconfont nc-icon-dizhiguanliV6xx text-[40rpx] pt-[12rpx] mr-[20rpx]"></text>
                        <view class="flex flex-col ml-[20rpx]">
                            <view class="flex leading-[38rpx] overflow-hidden">
                                <text class="text-[30rpx]">{{ detail.taker_name }}</text>
                                <text class="text-[30rpx] ml-[15rpx]">{{ detail.taker_mobile }}</text>
                            </view>
                            <text class="text-[24rpx] mt-[12rpx] leading-[26rpx]">{{ detail.taker_full_address }}</text>
                        </view>
                    </view>
                </view>
                <!-- 自提核销-->
                <template v-if="isShowSelfPickup">
                    <view class="sidebar-margin  mt-[var(--top-m)] card-template" v-if="selfPickupInfo && selfPickupInfo.length">
                        <swiper class="h-[450rpx]" circular indicator-dots="true" v-if="selfPickupInfo.length > 1">
                            <swiper-item v-for="(item,index) in selfPickupInfo" :key="index">
                                <view class="flex flex-col items-center justify-center">
                                    <image :src="item.qrcode" class="w-[300rpx] h-[auto]" mode="widthFix" />
                                </view>
                                <view class="flex items-center justify-center mt-[30rpx]">
                                    <text class="text-[28rpx] font-500">{{ item.code }}</text>
                                    <text class="text-[var(--text-color-light6)] text-[24rpx] ml-[10rpx] border-[2rpx] border-solid border-[#666] bg-[#f7f7f7] px-[12rpx] py-[6rpx] rounded" @click="copy(item.code)">复制</text>
                                </view>
                            </swiper-item>
                        </swiper>
                        <template v-else>
                            <view class="flex flex-col items-center justify-center">
                                <image :src="selfPickupInfo[0].qrcode" class="w-[300rpx] h-[auto]" mode="widthFix" />
                            </view>
                            <view class="flex items-center justify-center mt-[30rpx]">
                                <text class="text-[28rpx] font-500">{{ selfPickupInfo[0].code }}</text>
                                <text class="text-[var(--text-color-light6)] text-[24rpx] ml-[10rpx] border-[2rpx] border-solid border-[#666] bg-[#f7f7f7] px-[12rpx] py-[6rpx] rounded" @click="copy(selfPickupInfo[0].code)">复制</text>
                            </view>
                        </template>

                    </view>
                </template>
                <view v-if="detail.delivery_type == 'virtual' && detail.taker_mobile" class="flex items-center mb-[var(--top-m)] rounded-[var(--rounded-big)] bg-white p-[30rpx] mb-[20rpx] -mt-[86rpx] mx-[20rpx]">
                    <text class="nc-iconfont nc-icon-dianhuaV6xx mr-[20rpx]"></text>
                    <text>{{ detail.taker_mobile }}</text>
                </view>
                <view class="sidebar-margin card-template" :style="detail.delivery_type == 'virtual' && !detail.taker_mobile ? 'margin-top: -86rpx' : 'margin-top: 20rpx'">
                    <view class="flex items-center leading-[1]">
                        <text class="iconfont iconVector-25 text-[32rpx]"></text>
                        <text class="text-[28rpx] font-400 ml-[12rpx] flex-1 truncate">{{detail.site.site_name}}</text>
                    </view>
                    <view class="mt-[30rpx]" v-for="(goodsItem, goodsIndex) in detail.order_goods" :key="goodsIndex">
                        <view class="order-goods-item flex justify-between flex-wrap">
                            <view class="w-[150rpx] h-[150rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" @click="goodsEvent(goodsItem.goods_id)">
                                <up-image class="overflow-hidden" radius="var(--goods-rounded-big)" width="150rpx" height="150rpx"
                                    :src="img(goodsItem.goods_image_thumb_small ? goodsItem.goods_image_thumb_small : '')"
                                    mode="aspectFill">
                                    <template #error>
                                        <image class="w-[150rpx] h-[150rpx] rounded-[var(--goods-rounded-big)] overflow-hidden"
                                            :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill">
                                        </image>
                                    </template>
                                </up-image>
                            </view>
                            <view class="ml-[20rpx] flex flex-1 flex-col justify-between">
                                <view>
                                    <view class="text-[28rpx] max-w-[490rpx] truncate leading-[40rpx] text-[#333]">
                                        <text v-if="detail.order_type_name" class="relative top-[-4rpx] inline-block mr-[4rpx] bg-primary text-[#fff] text-[18rpx] rounded-[4rpx] leading-[30rpx] px-[6rpx]">{{ detail.order_type_name }}</text>
                                        {{ goodsItem.goods_name }}
                                    </view>
                                    <view v-if="goodsItem.sku_name">
                                        <view class="text-[22rpx] mt-[14rpx] text-[var(--text-color-light9)] truncate max-w-[490rpx] leading-[28rpx]">{{ goodsItem.sku_name }}</view>
                                    </view>
                                </view>
                                <view class="flex justify-between items-baseline leading-[28rpx] text-[#333]">
                                    <view class="price-font">
                                        <text class="text-[24rpx]">￥</text>
                                        <text class="text-[40rpx] font-500">{{ parseFloat(goodsItem.price).toFixed(2).split('.')[0] }}</text>
                                        <text class="text-[24rpx] font-500">.{{ parseFloat(goodsItem.price).toFixed(2).split('.')[1] }}</text>
                                    </view>
                                    <text class="text-right text-[26rpx]">x{{ goodsItem.num }}</text>
                                </view>
                            </view>
                            <view class="flex justify-end  self-end w-[100%] mt-[30rpx]" v-if="(goodsItem.status != '1') || (goodsItem.is_enable_refund == 1 && detail.status != '6')">	
                                <view v-if="goodsItem.status != '1'"
                                    class="order-grey-hollow-btn"
                                    @click="redirect({ url: '/addon/mall/pages/refund/detail', param: { order_refund_no : goodsItem.order_refund_no } })">查看退款</view>
                                <view v-else-if="goodsItem.is_enable_refund == 1 && detail.status != '6'"
                                    class="order-grey-hollow-btn ml-[20rpx]"
                                    @click="applyRefund(goodsItem.order_goods_id)">申请退款</view>
                            </view>
                        </view>
                        <!-- 商品的万能表单信息 -->
                        <view :class="{'diy-form-wrap mt-[30rpx] -mb-[36rpx]' : goodsDiyFormData.length }" v-if="goodsItem.form_record_id">
                            <diy-form-detail :record_id="goodsItem.form_record_id" completeLayout="style-2" @callback="getGoodsDiyFormDetailCallback" />
                        </view>
                    </view>
                </view>
                <!-- 待付款订单的万能表单信息 -->
                <view class="order-form" :class="{'sidebar-margin mt-[var(--top-m)] card-template' : orderDiyFormData.length }"
                    v-if="detail.form_record_id">
                    <diy-form-detail :record_id="detail.form_record_id" completeLayout="style-2" @callback="getOrderDiyFormDetailCallback" />
                </view>
                <view class="sidebar-margin mt-[var(--top-m)] card-template">
                    <view class="justify-between card-template-item">
                        <view class="text-[28rpx]">{{ t('orderNo') }}</view>
                        <view class="flex items-center text-[28rpx]">
                            <text>{{ detail.order_no }}</text>
                            <text class="w-[2rpx] h-[20rpx] bg-[#999] mx-[10rpx]"></text>
                            <text class="text-[var(--primary-color)]" @click="copy(detail.order_no)">复制</text>
                        </view>
                    </view>
                    <view v-if="detail.out_trade_no" class="justify-between card-template-item">
                        <view class="text-[28rpx]">{{ t('orderTradeNo') }}</view>
                        <view class="text-[28rpx]">{{ detail.out_trade_no }}</view>
                    </view>
                    <view class="justify-between card-template-item">
                        <view class="text-[28rpx]">{{ t('createTime') }}</view>
                        <view class="text-[28rpx]">{{ detail.create_time }}</view>
                    </view>
                    <view v-if="detail.pay" class="card-template-item justify-between">
                        <view  class="text-[28rpx]">{{ t('payTypeName') }}</view>
                        <view  class="text-[28rpx]">{{ detail.pay.type_name }}</view>
                    </view>
                    <view v-if="detail.pay && detail.member_id !== detail.pay.main_id && detail.pay.status == 2 "
                        class="card-template-item justify-end">
                        <view class="friend-pay relative px-[20rpx] py-[12rpx] bg-[#F2F2F2] rounded-[10rpx] flex items-center">
                            <u-avatar :src="img(detail.pay.pay_member_headimg)" size="20" leftIcon="none" :default-url="img('static/resource/images/default_headimg.png')" />
                            <text class="ml-[14rpx] text-[24rpx] using-hidden">{{ detail.pay.pay_member }}{{ t('helpPay') }}</text>
                        </view>
                    </view>
                    <view v-if="detail.pay" class="card-template-item justify-between">
                        <view class="text-[28rpx]">{{ t('payTime') }}</view>
                        <view class="text-[28rpx]">{{ detail.pay.pay_time }}</view>
                    </view>
                </view>
                <view class="sidebar-margin mt-[var(--top-m)] card-template">
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">{{ t('deliveryType') }}</view>
                        <view class="text-[28rpx]">{{ detail.delivery_type_name }}</view>
                    </view>
                    <view class=" card-template-item justify-between" v-if="detail.buyer_ask_delivery_time">
                        <view class="text-[28rpx]">{{ detail.delivery_type == 'store' ? '提货时间': '预约配送时间' }}</view>
                        <view class="text-[28rpx]">{{ detail.buyer_ask_delivery_time }}</view>
                    </view>
                    <template v-if="detail.order_delivery && detail.order_delivery.length &&  detail.delivery_type == 'local_delivery'">
                        <view class="card-template-item justify-between" v-if="detail.order_delivery[0].delivery_service_name">
                            <view class="text-[28rpx]">配送平台</view>
                            <view class="text-[28rpx]">{{ detail.order_delivery[0].delivery_service_name }}</view>
                        </view>
                        <view class="card-template-item justify-between" v-if="detail.order_delivery[0].local_delivery_status_name">
                            <view class="text-[28rpx]">配送状态</view>
                            <view class="text-[28rpx]">{{ detail.order_delivery[0].local_delivery_status_name }}</view>
                        </view>
                        <view class="card-template-item justify-between" v-if="detail.order_delivery[0].rider_name">
                            <view class="text-[28rpx]">配送员</view>
                            <view class="text-[28rpx]">{{ detail.order_delivery[0].rider_name }}</view>
                        </view>
                        <view class="card-template-item justify-between" v-if="detail.order_delivery[0].rider_mobile">
                            <view class="text-[28rpx]">配送员电话</view>
                            <view class="text-[28rpx]">{{ detail.order_delivery[0].rider_mobile }}</view>
                        </view>
                        <!-- <view class="card-template-item justify-between" v-if="detail.order_delivery[0].delivery_service && detail.order_delivery[0].delivery_service != 'merchant' && ['2','3','4','7'].indexOf(detail.order_delivery[0].local_delivery_status) != -1 " @click="redirect({url: '/addon/mall/pages/delivery/track', param: { order_no: detail.order_no, delivery_service: detail.order_delivery[0].delivery_service }})">
                            <view class="text-[28rpx]">配送员轨迹</view>
                            <view class="text-[28rpx]">
                                <text class="text-primary">查看</text>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                        </view> -->
                    </template>
                </view>
                <!-- 核销码 -->
                <block v-if="isShowVerify">
                    <view class="sidebar-margin  mt-[var(--top-m)] card-template" v-if="verifyInfo && verifyInfo.length">
                        <swiper class="h-[450rpx]" circular indicator-dots="true" v-if="verifyInfo.length > 1">
                            <swiper-item v-for="(item,index) in verifyInfo" :key="index">
                                <view class="flex flex-col items-center justify-center">
                                    <image :src="item.qrcode" class="w-[300rpx]" mode="widthFix"></image>
                                </view>
                                <view class="flex items-center justify-center mt-[30rpx]">
                                    <text class="text-[28rpx] font-500">{{item.code}}</text>
                                    <text class="text-[var(--text-color-light6)] text-[24rpx] ml-[10rpx] border-[2rpx] border-solid border-[#666] bg-[#f7f7f7] px-[12rpx] py-[6rpx] rounded" @click="copy(item.code)">复制</text>
                                </view>
                            </swiper-item>
                        </swiper>
                        <block v-else>
                            <view class="flex flex-col items-center justify-center">
                                <image :src="verifyInfo[0].qrcode" class="w-[300rpx]" mode="widthFix"></image>
                            </view>
                            <view class="flex items-center justify-center mt-[30rpx]">
                                <text class="text-[28rpx] font-500">{{verifyInfo[0].code}}</text>
                                <text class="text-[var(--text-color-light6)] text-[24rpx] ml-[10rpx] border-[2rpx] border-solid border-[#666] bg-[#f7f7f7] px-[12rpx] py-[6rpx] rounded" @click="copy(verifyInfo[0].code)">复制</text>
                            </view>
                        </block>
                        
                    </view>
                    <view class="sidebar-margin mt-[var(--top-m)] card-template">
                        <view class="title">核销信息</view>
                        <view class="card-template-item justify-between">
                            <view class="text-[28rpx]">核销次数</view>
                            <view class="price-font font-500 text-[28rpx]">
                                {{'剩余'+(verifyGoodsData.num - verifyGoodsData.verify_count)+'次'}}/{{'共'+verifyGoodsData.num+'次'}}
                            </view>
                        </view>
                        <view class="card-template-item justify-between">
                            <view class="text-[28rpx]">有效期</view>
                            <view class="price-font font-500 text-[28rpx]">
                                {{verifyGoodsData.verify_expire_time ? verifyGoodsData.verify_expire_time : '永久'}}
                            </view>
                        </view>
                    </view>
                </block>
                <view class="sidebar-margin mt-[var(--top-m)] card-template">
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">{{ t('goodsMoney') }}</view>
                        <view class="price-font font-500">
                            <text class="text-[28rpx]">￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.goods_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.goods_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">{{ t('discountMoney') }}</view>
                        <view class="price-font font-500">
                            <text class="text-[28rpx]">-￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.discount_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.discount_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">{{ t('mallDiscountMoney') }}</view>
                        <view class="price-font font-500">
                            <text class="text-[28rpx]">-￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.mall_discount_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.mall_discount_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">{{ t('deliveryMoney') }}</view>
                        <view class="price-font font-500">
                            <text class="text-[28rpx]">￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.delivery_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.delivery_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                    <view class="card-template-item justify-between items-baseline">
                        <view class="text-[28rpx]">{{ t('orderMoney') }}：</view>
                        <view class="text-[var(--price-text-color)] price-font">
                            <text class="text-[28rpx]">￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.order_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.order_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                </view>

                <view class="flex z-2 justify-between items-center bg-[#fff] fixed left-0 right-0 bottom-0 min-h-[100rpx] pl-[30rpx] pr-[20rpx] flex-wrap  pb-ios">
                    <view class="flex"></view>
                    <view class="flex justify-end">
                        <view @click="orderBtnFn('index')" class="order-grey-hollow-btn ml-[20rpx]">{{ t('index') }}</view>
                        <view class="order-grey-hollow-btn ml-[20rpx]" v-if="detail.order_type == 'pintuan' && detail.extend_field_2 > 0" @click="handlePintuan">查看拼团</view>
                        <view class="order-grey-hollow-btn ml-[20rpx]" v-if="showLogistics(detail)" @click="orderBtnFn('logistics')">
                            {{ t('logisticsTracking') }}
                        </view>
                        <view class="order-grey-hollow-btn ml-[20rpx]" v-if="detail.status == 1" @click="orderBtnFn('close')">{{ t('orderClose') }}</view>
                        <view class="order-primary-solid-btn primary-btn-bg  ml-[20rpx]" @click="orderBtnFn('pay')" v-if="detail.status == 1">{{ t('topay') }}</view>
                        <view class="order-primary-solid-btn primary-btn-bg  ml-[20rpx]" v-if="detail.status == 3" @click="orderBtnFn('finish')" >{{ t('orderFinish') }}</view>
                        <template v-if="detail.status == 5">
                            <view v-if="detail.is_evaluate == 1 || (detail.is_evaluate != 1 && evaluateConfig.is_evaluate == 1)" class="order-grey-hollow-btn ml-[20rpx] "
                                @click="orderBtnFn('evaluate')">{{ detail.is_evaluate == 1 ? t('selectedEvaluate') : t('evaluate') }}</view>
                        </template>
                    </view>
                </view>
            
            </view>
            <view class="tab-bar-placeholder"></view>
            <pay ref="payRef" @close="payClose"></pay>
            <logistics-tracking ref="materialRef"></logistics-tracking>
            <delivery-record ref="deliveryRecordRef"></delivery-record>
        </view>

        <loading-page :loading="loading"></loading-page>

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
import { ref, reactive, computed,nextTick } from 'vue';
import { onLoad, onShow, onPageScroll } from '@dcloudio/uni-app'
import { t } from '@/locale'
import { img, redirect, copy, goback, distance } from '@/utils/common';
import { getMallOrderDetail, orderClose, orderFinish, getMallOrderPay,getMallOrderClose } from '@/addon/mall/api/order';
import { getEvaluateConfig } from '@/addon/mall/api/shop';
import { getVerifyCode } from '@/app/api/verify';

import logisticsTracking from '@/addon/mall/pages/order/components/logistics-tracking/logistics-tracking.vue'
import deliveryRecord from '@/addon/mall/pages/order/components/delivery-record/delivery-record.vue'
import useConfigStore from "@/stores/config";
import { topTabar } from '@/utils/topTabbar'
import groupPay from './components/group-pay/group-pay.vue'
import groupClose from './components/group-close/group-close.vue'
import diyFormDetail from '@/addon/components/diy-form-detail/index.vue'
import useSystemStore from '@/stores/system';
import { useLocation } from '@/hooks/useLocation'

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let topTabbarData = topTabarObj.setTopTabbarParam({ title: '订单详情' })
/********* 自定义头部 - end ***********/

const systemStore = useSystemStore();

const isShowLocalDelivery = ref(false)
const detail = ref<any>({});
const loading = ref<boolean>(true);
const type = ref('')
const orderId = ref('')
const orderStepsShow = ref(false)
const evaluateConfig = ref<Object>({});

const sendMessageTitle = ref('')
const sendMessagePath = ref('')
const sendMessageImg = ref('')

const wxPrivacyPopupRef:any = ref(null)

/************** 定位-start ****************/
const locationVal = useLocation(true);

/************** 定位-end ****************/

onLoad((option:any) => {
    if (option.order_id) {
        orderId.value = option.order_id;
        // #ifdef MP
        nextTick(()=>{
            if(wxPrivacyPopupRef.value) wxPrivacyPopupRef.value.proactive();
        })
        // #endif
    } else {
        let parameter = {
            url: '/addon/mall/pages/order/list',
            title: '缺少订单id'
        };
        goback(parameter)
    }
	
});

onShow(() => { 
    // 刷新定位
	locationVal.refresh();
    orderDetailFn(orderId.value);
})




getEvaluateConfig().then(({ data }) => {
    evaluateConfig.value = data
})

const orderDetailFn = (id: any) => {
	loading.value = true;
	getMallOrderDetail({
        order_id: id,
        position: systemStore.diyAddressInfo
    }).then((res: any) => {
		detail.value = res.data;
        isShowLocalDelivery.value = detail.value.show_local_delivery_info
        if(isShowLocalDelivery.value){
           topTabbarData = topTabarObj.setTopTabbarParam({ title: '订单详情', topStatusBar: { textColor: '#333' }})
        }
        if(res.data.order_goods && res.data.order_goods.length && isShowVerify.value){
			let obj: any = {};
			obj.order_goods_id = res.data.order_goods[0].order_goods_id
            obj.site_id = res.data.order_goods[0].site_id
			getVerifyCodeFn(obj);
		}
        if (res.data.order_goods && res.data.order_goods.length && isShowSelfPickup.value) {
		    let obj: any = {};
		    obj.order_id = res.data.order_id
            obj.site_id = res.data.site_id
		    getSelfPickupCodeFn(obj);
		}

        sendMessageTitle.value = detail.value.order_goods[0].goods_name
		sendMessageImg.value = img(detail.value.order_goods[0].goods_image_thumb_small || '')

		loading.value = false;
       
	}).catch(() => {
		loading.value = false;
	})
}

// 是否展示地图
let expectedFinishTime = ref()
const handleComfirm = (val: any) => {
    if(val){
        isShowLocalDelivery.value = true
    } else {
        isShowLocalDelivery.value = false
    }
    expectedFinishTime.value = val?.expected_finish_time

}

// 支付
let groupPayRef = ref(null)
const payFn = (item:any) => {
	getMallOrderPay(item.order_id).then((res: any) => {
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
let groupCloseRef = ref(null)
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
								orderDetailFn(item.order_id);
							}).catch(() =>{
							})
						}
					}else{
						orderDetailFn(item.order_id);
					}
				})
			}
		}
	})
}
const closeGroupOrder = () =>{
	orderDetailFn(orderId.value);
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
					orderDetailFn(item.order_id);
				})
			}
		}
	})
    // #endif

    // #ifdef MP-WEIXIN
    // 检测微信小程序是否已开通发货信息管理服务
    if (item.pay && (item.pay.type == 'wechatpay' || item.pay.type == 'huifu_wechatpay' ) && item.is_trade_managed && wx.openBusinessView) {
        wx.openBusinessView({
            businessType: 'weappOrderConfirm',
            extraData: {
                // merchant_id: item.mch_id,
                transaction_id: item.pay.trade_no
            },
            success: (res : any) => {
                setTimeout(() => {
                    orderFinish(item.order_id).then((data) => {
                        orderDetailFn(item.order_id);
                    })
                }, 2000)
            },
            fail: (res : any) => {
                console.log('小程序确认收货组件打开失败 fail', res);
            }
        })
    } else {
        uni.showModal({
            title: '提示',
            content: '您确定物品已收到吗？',
            confirmColor: useConfigStore().themeColor['--primary-color'],
            success: res => {
                if (res.confirm) {
                    orderFinish(item.order_id).then((data) => {
                        orderDetailFn(item.order_id);
                    })
                }
            }
        })
    }
    // #endif
}

const goodsEvent = (id: number) => {
    if(detail.value.order_type == 'pintuan') {
        redirect({
            url: '/addon/pintuan/pages/goods/detail',
            param: {
                pintuan_id: detail.value.extend_field_1
            }
        })
    } else {
        redirect({
            url: '/addon/mall/pages/goods/detail',
            param: {
                goods_id: id
            }
        })
    }
}
const handlePintuan = () => {
    redirect({
        url: '/addon/pintuan/pages/order/detail',
        param: {
            order_id: detail.value.order_id
        }
    })
}



const payRef = ref(null)
const materialRef: any = ref(null)
const orderBtnFn = (type = '') => {
	if (type == 'pay'){
        payFn(detail.value)
    }else if (type == 'close') {
		close(detail.value);
	} else if (type == 'finish') {
		finish(detail.value);
	} else if (type == 'index') {
		redirect({
			url: '/app/pages/index/index',
            mode: 'reLaunch'
		});
	} else if (type == 'logistics') {
		if (detail.value.order_delivery.length > 0) {
			let params = {
				id: detail.value.order_delivery[0].id,
				mobile: detail.value.taker_mobile
			}
			let list = ref([])
			detail.value.order_delivery.forEach((item: any, index: number) => {
                if(item.sub_delivery_type == 'express'){
                    item.name = `包裹${index + 1}`
                    list.value.push(item)
                }
			})

			materialRef.value.open(params);
			materialRef.value.packageList = list.value
		}
	} else if (type == 'evaluate') {
		if (!detail.value.is_evaluate) {
			redirect({ url: '/addon/mall/pages/evaluate/order_evaluate', param: { order_id: detail.value.order_id } })
		} else {
			redirect({ url: '/addon/mall/pages/evaluate/order_evaluate_view', param: { order_id: detail.value.order_id } })
		}
	}
}
const dateFormat = (res, type) => {
    let data;
    if (res.indexOf('/') != -1) {
        data = res.split("/");
    } else if (res.indexOf('-') != -1) {
        data = res.split("-");
    }

    let time;
    const index = new Date(res).getDay();
    const week = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
    if (type == "yearMonthDay") {
        time = data[0] + '年' + data[1] + '月' + data[2] + '日';
    } else if (type == "yearMonthDayWeek") {
        time = data[0] + '年' + data[1] + '月' + data[2] + '日 ' + week[index];
    } else if (type == "monthDayWeek") {
        time = data[1] + '月' + data[2] + '日 ' + week[index];
    } else {
        time = data[1] + '月' + data[2] + '日';
    }

    return time;
}

const applyRefund = (orderGoodsId: number) => {
	redirect({
		url: '/addon/mall/pages/refund/apply',
		param: {
			order_id: detail.value.order_id,
			order_goods_id: orderGoodsId
		}
	})
}

const getAddress = () => {
	uni.openLocation({
		latitude: Number(detail.value.taker_latitude),
		longitude: Number(detail.value.taker_longitude),
		success: function () { }
	});
}

const showLogistics = (data: any) => {
    let status = false
    if (data.delivery_type != 'express') return false;
    for (let i = 0; i < data.order_delivery.length; i++) {
        let item = data.order_delivery[i];
        if (item.sub_delivery_type === 'express' && data.status === '3') {
            status = true;
            break;
        } else if (item.sub_delivery_type === 'express' && data.status === '5') {
            status = true;
            break;
        } else {
            status = false
        }
    }
    return status
}
/************ 虚拟商品核销-start ***************/ 
let verifyGoodsData = ref({}) //虚拟商品
const isShowVerify = computed(() => {
	let bool =  false;
	if(detail.value.order_goods.length == 1){
		verifyGoodsData.value = detail.value.order_goods[0]
		
		let data = detail.value.order_goods[0];
		bool = data.is_verify == 1 && data.goods_type == 'virtual' && data.delivery_status == 'delivery_finish' && detail.value.status == 3 ? true : false;
	}
	return bool
})
let verifyInfo = ref([])
const getVerifyCodeFn = (data:any) => {
	verifyInfo.value = [];
	
	getVerifyCode('shopVirtualGoods', data).then((res) => {
		verifyInfo.value = res.data;
	})
}
/************ 虚拟商品核销-end ***************/ 

/************ 自提核销-start ***************/
// const verifyGoodsData = ref({}) //虚拟商品
const isShowSelfPickup = computed(() => {
    let bool = false;
    bool = detail.value.delivery_type == 'store' && detail.value.status == 2 ? true : false;
    return bool
})
const selfPickupInfo = ref([])
const getSelfPickupCodeFn = (data: any) => {
    selfPickupInfo.value = [];

    getVerifyCode('shopPickUpOrder', data).then((res: any) => {
        selfPickupInfo.value = res.data;
    })
}
/************ 自提核销-end ***************/

const callFn = () => {
	uni.makePhoneCall({
		phoneNumber: detail.value.store.mobile
	})
}

const getDistance  = () => {
    uni.openLocation({
		latitude: Number(detail.value.store.latitude),
		longitude: Number(detail.value.store.longitude),
		success: function () { }
	});
}
// 商品表单信息
const goodsDiyFormData = ref([]);
const getGoodsDiyFormDetailCallback = (data: any) => {
    if (data) {
        goodsDiyFormData.value = data;
    }
}

// 订单表单信息
const orderDiyFormData = ref([]);
const getOrderDiyFormDetailCallback = (data: any) => {
    if (data) {
        orderDiyFormData.value = data;
    }
}


// 配送记录
const deliveryRecordRef = ref(null)
const localeDeliveryFn = () => {
    if(detail.value.delivery_type == 'local_delivery' && ['3','5'].indexOf(detail.value.status) != -1){
        deliveryRecordRef.value?.open(detail.value.local_delivery_order_log);
    }
}
</script>
<style lang="scss" scoped>
.distance-wrap{
    box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, .0784);
}
.triangle{
    content: "";
    border-top: 3px solid #fff;
    border-right: 5px solid transparent;
    border-left: 5px solid transparent;
    box-shadow: 0px 0px 8px 0px rgba(0, 0, 0, .0784);
}
.text-item {
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.order-goods-item:nth-child(1) {
	margin-top: 0rpx;
}

.text-color {
	color: $u-primary;
}

.bg-color {
	background-color: $u-primary;
}

.bg-linear {
	background: linear-gradient( 94deg, #FF0000 15%, #ff7900 87%);
}



.tab-bar-placeholder {
	padding-bottom: calc(constant(safe-area-inset-bottom) + 100rpx);
	padding-bottom: calc(env(safe-area-inset-bottom) + 100rpx);
}
:deep(.u-image__error){
	background-color: transparent !important;
}

.friend-pay {
    &::after {
        content: '';
        display: block;
        width: 20rpx;
        height: 20rpx;
        background-color: #f2f2f2;
        position: absolute;
        right: 30rpx;
        top: 0;
        transform: translateY(-50%) rotate(45deg);
        border-radius: 4rpx;
    }
}
</style>
<style lang="scss">
.order-form{
    ::v-deep .draggable-element:last-of-type {
    	.detail-two-content{
            margin-bottom: 0 !important;
        }
    }
}
</style>
