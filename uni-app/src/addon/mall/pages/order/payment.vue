<template>
    <view :style="themeColor()" class="payment-wrap">
        <view class="payment-body min-h-[100vh]" v-if="orderData">
            <!-- #ifdef MP || APP-PLUS -->
            <top-tabbar :data="topTabbarData" :scrollBool="topTabarObj.getScrollBool()"/>
            <!-- #endif -->
            <view class="pt-[30rpx] sidebar-margin payment-bottom">
                <!-- 配送方式 -->
                <view class="mb-[var(--top-m)] rounded-[var(--rounded-big)] bg-white" v-if="orderData.basic.has_goods_types.includes('real') && orderData.basic.is_need_take_address" :style="{backgroundImage: `url(${img('addon/mall/payment/head_bg.png')})`, backgroundSize: '100%', backgroundRepeat: 'no-repeat', backgroundPosition: 'bottom'}">
                    <view class="min-h-[140rpx] flex items-center px-[30rpx]">
                        <view class="w-full" @click="toSelectAddress">
                            <view v-if="!$u.test.isEmpty(orderData.delivery.take_address)" class="py-[30rpx]">
                                <view class="flex items-center">
                                    <image class="w-[60rpx] h-[60rpx] mr-[20rpx] flex-shrink-0" :src="img('addon/mall/payment/position_01.png')" mode="aspectFit"/>
                                    <view class="flex flex-col overflow-hidden">
                                        <text class="text-[26rpx] text-[var(--text-color-light9)] mt-[16rpx] truncate max-w-[536rpx]">{{orderData.delivery.take_address.full_address.split(orderData.delivery.take_address.address)[0]}}</text>
                                        <text class="leading-[1.3] font-500 text-[30rpx] mt-[14rpx] text-[#333] truncate max-w-[536rpx]">{{orderData.delivery.take_address.address}}</text>
                                        <view class="flex items-center text-[26rpx] text-[var(--text-color-light6)] mt-[16rpx]">
                                            <text class="mr-[16rpx]">{{ orderData.delivery.take_address.name }}</text>
                                            <text>{{ mobileHide(orderData.delivery.take_address.mobile) }}</text>
                                        </view>
                                        <view v-if="orderData.delivery.error" class="text-[24rpx] text-primary mt-[16rpx]">
                                            <text class="nc-iconfont nc-icon-tanhaoV6xx text-primary text-[24rpx]"></text>
                                            {{ orderData.delivery.error }}
                                        </view>
                                    </view>
                                    <text class="ml-auto nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]"></text>
                                </view>
                                
                            </view>
                            <view v-else class="flex items-center py-[30rpx]">
                                <image class="w-[26rpx] h-[30rpx] mr-[10rpx]" :src="img('addon/mall/payment/position_02.png')" mode="aspectFit"/>
                                <text class="text-[28rpx]">添加收货地址</text>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)] ml-auto"></text>
                            </view>
                        </view>
                    </view>
                </view>
                <view v-if="orderData.basic.has_goods_types.length == 1 && orderData.basic.has_goods_types.includes('virtual') && orderData.basic.virtual_verify_mobile == 1" class="flex items-center mb-[var(--top-m)] rounded-[var(--rounded-big)] bg-white p-[30rpx]">
                    <text class="nc-iconfont nc-icon-dianhuaV6xx mr-[20rpx]"></text>
                    <input type="Number" v-model="createData.delivery.taker_mobile" class="text-[28rpx] text-[#333] w-[80%]" maxlength="11" placeholder="请填写手机号" placeholder-class="text-[var(--text-color-light9)] text-[28rpx]">
                </view>
                <view class="mb-[var(--top-m)]">
                    <template v-for="(item, key, index) in orderData.order_list">
                        <view class=" mb-[var(--top-m)] card-template">
                            <view class="mb-[20rpx] flex items-center">
                                <text class="iconfont iconVector-25 text-[28rpx]"></text>
                                <text class="text-[28rpx] font-400 ml-[12rpx] flex-1 truncate">{{ item.site_info.site_name }}</text>
                            </view>
                            <view class="mb-[30rpx]">
                                <view v-for="(subItem, subKey, subIndex) in item.goods_data" :key="subIndex" :class="{'pb-[40rpx]': (subIndex + 1) != Object.keys(item.goods_data).length}">
                                    <view class="flex">
                                        <up-image radius="var(--goods-rounded-big)" width="180rpx" height="180rpx" :src="img(subItem.sku_image)" mode="aspectFill">
                                            <template #error>
                                                <image class="w-[180rpx] h-[180rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
                                            </template>
                                        </up-image>
                                        <view class="flex flex-1 w-0 flex-col justify-between ml-[20rpx] py-[6rpx]">
                                            <view class="line-normal">
                                                <view class="truncate text-[#303133] text-[28rpx] leading-[32rpx]">
                                                    <text v-if="subItem.order_type_name" class="relative top-[-4rpx] inline-block mr-[4rpx] bg-primary text-[#fff] text-[18rpx] rounded-[4rpx] leading-[30rpx] px-[6rpx]">{{ subItem.order_type_name }}</text>
                                                    {{ subItem.goods.goods_name }}
                                                </view>
                                                <view v-if="subItem.sku_name" class="mt-[14rpx] flex">
                                                    <text class="truncate text-[24rpx] text-[var(--text-color-light9)] leading-[28rpx]">{{ subItem.sku_name }}</text>
                                                </view>
                                            </view>
                                            <view class="mb-auto goods-alert"  v-if="subItem.error">
                                                <u-alert type="error" :description="subItem.error" class="leading-[24rpx] !inline-block" fontSize="11"></u-alert>
                                            </view>
                                            <view class="flex justify-between items-center">
                                                <view class="text-[var(--price-text-color)] flex items-baseline  price-font">
                                                    <text class="text-[24rpx] font-500">￥</text>
                                                    <text class="text-[40rpx] font-500">{{ parseFloat(subItem.price).toFixed(2).split('.')[0] }}</text>
                                                    <text class="text-[24rpx] font-500">.{{ parseFloat(subItem.price).toFixed(2).split('.')[1] }}</text>
                                                </view>
                                                <view class="font-400 text-[28rpx] text-[#333]">
                                                    <text>x</text>
                                                    <text>{{ subItem.num }}</text>
                                                </view>
                                            </view>
                                        </view>
                                    </view>
                                    <view class="card-template !p-[0] !pt-[20rpx] -mb-[36rpx]" v-if="Number(subItem.goods.form_id) && userInfo.member_id">
                                        <diy-form ref="diyFormGoodsRef" :form_id="subItem.goods.form_id" :relate_id="subItem.sku_id"  :form_sign="'goodsDetail_' + subItem.sku_id+'_siteId_'+subItem.site_id+'_memberId_'+userInfo?.member_id+'_formId_'+subItem.goods.form_id" :storage_name="'diyFormStorageByGoodsDetail_' + subItem.sku_id+'_siteId_'+subItem.site_id+'_memberId_'+userInfo?.member_id" form_border="none"/>
                                    </view>
                                </view>  
                            </view>
                        </view>
                        
                        <view class="card-template py-[24rpx] mb-[var(--top-m)]">
                            <diy-form v-if="item.form_id" class="mb-[18rpx] -mt-[10rpx]" ref="diyFormRef" :form_id="item.form_id" :relate_id="item.site_id" :form_sign="'siteId_'+ item.site_id +'_memberId_'+userInfo.member_id+'_formId_'+item.form_id" :storage_name="'diyFormStorageByOrderPayment'"/> 
                            <!-- 赠品 -->
                            <view class="flex items-center text-[#303133] leading-[30rpx]" @click="giftGooodsRef.open(item.basic.full_gift_sku_list)" v-if="item.basic.full_gift_sku_list && item.basic.full_gift_sku_list.length">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">赠品</view>
                                <view class="text-[24rpx] flex-1 flex gap-x-[10rpx] justify-end text-primary">
                                    <view v-for="(cItem, index) in item.basic.full_gift_sku_list" :key="index" class="flex">
                                        <up-image radius="var(--goods-rounded-small)" width="60rpx" height="60rpx" :src="img(cItem.sku_image)" model="aspectFill" v-if="index < 5">
                                            <template #error>
                                                <image class="w-[60rpx] h-[60rpx] rounded-[var(--goods-rounded-small)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"/>
                                            </template>
                                        </up-image>
                                    </view>
                                </view>
                                <view class="text-[28rpx] ml-[10rpx]">共<text class="price-font">{{ getGiftNum(item.basic.full_gift_sku_list) }}</text>件</view>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]" :style="iconfontStyle"></text>
                            </view>
                            <!-- 买家留言 -->
                            <view class="bg-white flex items-center leading-[30rpx] mt-[26rpx]" v-if="createData.body[key]">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">买家留言</view>
                                <view class="flex-1 text-[#303133]">
                                    <input type="text" v-model="createData.body[key].member_remark" class="text-right text-[28rpx] text-[#333]" maxlength="50" placeholder="请输入留言信息给卖家" placeholder-class="text-[var(--text-color-light9)] text-[28rpx]">
                                </view>
                            </view>
                            <!-- 配送方式 -->
                            <view class="flex items-center text-[#303133] leading-[30rpx] mt-[36rpx]" @click="deliveryRef.open(item)" v-if="item.delivery && item.delivery.delivery_type_list">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">配送方式</view>
                                <view v-if="item.delivery.error" class="text-[24rpx] text-primary">
                                    <text class="nc-iconfont nc-icon-tanhaoV6xx text-primary text-[24rpx]"></text>
                                    {{ item.delivery.error }}
                                </view>
                                <view class="flex-1 w-0 text-right truncate">
                                    <text class="text-[28rpx] text-[var(--text-color-light9)]" :class="{'!text-[#333]': createData.body[key].delivery.delivery_type}" >{{( createData.body[key].delivery.delivery_type ? item.delivery.delivery_type_list[createData.body[key].delivery.delivery_type].name : '') || '请选择配送方式' }}</text>
                                </view>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]" :style="iconfontStyle"></text>
                            </view>
                            <!-- 门店自提 -->
                            <view  v-if="createData.body[key].delivery.delivery_type == 'store' || (createData.body[key].delivery.delivery_type == 'local_delivery' && orderData.config.delivery.is_local_delivery_store_select)"  @click="openSelectStore(item)">
                                <view v-if="!$u.test.isEmpty(item.delivery.take_store)" class="flex items-center box-border px-[20rpx] py-[30rpx] bg-[#f6f6f6] rounded-[16rpx] mt-[30rpx]">
                                    <view class="flex flex-col flex-1">
                                        <view class="leading-[1] mb-[14rpx]">
                                            <text class="text-[28rpx]  text-[#303133] mr-[10rpx]">{{ item.delivery.take_store.store_name }}</text>
                                            <text class="nc-iconfont nc-icon-youV6xx text-[24rpx] text-[var(--text-color-light9)]"></text>
                                        </view>
                                        <view class="text-[22rpx] text-[var(--text-color-light6)] leading-[1.4] multi-hidden">
                                            <template v-if="item.delivery.take_store.hasOwnProperty('distance')">
                                                <text class="flex-shrink-0">距您{{ distance(item.delivery.take_store.distance) }}</text>
                                                <text class="w-[1rpx] h-[16rpx] bg-[#ddd] mx-[10rpx] inline-block"></text>
                                            </template>
                                            <text class="max-w-[400rpx]">{{ item.delivery.take_store.full_address }}</text>
                                        </view>
                                         
                                    </view>
                                    <view class="w-[1rpx] h-[44rpx] bg-[#D9D9D9] mx-[30rpx]"></view>
                                    <view class="flex flex-col items-center flex-shrink-0" @click.stop="getAddress(item.delivery.take_store)">
                                        <text class="nc-iconfont nc-icon-dingweiV6mm-2 text-[32rpx] mb-[6rpx]"></text>
                                        <text class="text-[22rpx]">地图</text>
                                    </view>
                                </view>
                            </view>
                           <ns-select-time :ref="(el: any) => setSelectTimeRefs(el, key)" :rules="service_time[key]" v-if="service_time[key] && Object.keys(service_time[key]).length" :isQuantum="true" :isOpen="item.delivery.take_store.time_is_open" @change="(e) => { getTime(e, key) }" @getDate="(e) => { getDate(e, key) }"></ns-select-time>
                           <!-- 门店自提时间 -->
                           <view class="flex items-center text-[#303133] leading-[30rpx] mt-[36rpx]" v-if="createData.body[key].delivery.delivery_type == 'store' && item.delivery.take_store.time_is_open && !$u.test.isEmpty(item.delivery.take_store)"  @click="handleTime(key)">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">提货时间</view>
                                <view class="flex-1 flex justify-end truncate">
                                    <view class="text-[28rpx] text-[#333] ml-2 text-right" :class="{'text-[#63676D]': !createData.body[key].delivery.buyer_ask_delivery_time }">{{ createData.body[key].delivery.buyer_ask_delivery_time ? showGetDate[key] : '选择提货时间' }}</view>
                                </view>
                                <text class="text-[26rpx] text-[var(--text-color-light9)] nc-iconfont nc-icon-youV6xx" :style="iconfontStyle"></text>
                            </view>
                            <!-- 同城配送时间 -->
                            <view class="flex items-center text-[#303133] leading-[30rpx] mt-[36rpx]" v-if="createData.body[key].delivery.delivery_type == 'local_delivery' && item.delivery.take_store.time_is_open && !$u.test.isEmpty(item.delivery.take_store)"  @click="handleTime(key)">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">{{createData.body[key].delivery.local_delivery_type =='subscribe' ? '预约配送':'配送时间'}}</view>
                                <view class="flex-1 flex justify-end truncate">
                                    <view v-if="createData.body[key].delivery.local_delivery_type =='subscribe'" class="text-[28rpx] text-[#333] ml-2 text-right" :class="{'text-[#63676D]': !createData.body[key].delivery.buyer_ask_delivery_time }">{{ createData.body[key].delivery.buyer_ask_delivery_time ? showGetDate[key] : '选择时间' }}</view>
                                    <view v-else class="text-[28rpx] text-[#333] ml-2 text-right" >{{ isDelivery[key]?.show ? isDelivery[key]?.timeGap : '当前时间不支持配送' }}</view>
                                </view>
                                <text class="text-[26rpx] text-[var(--text-color-light9)] nc-iconfont nc-icon-youV6xx" :style="iconfontStyle"></text>
                            </view>
                            <!-- 运费 -->
                            <view class="flex items-center leading-[30rpx] mt-[36rpx]" v-if="parseFloat(item.basic.delivery_money)">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">运费</view>
                                <view class="flex-1 w-0 text-right price-font text-[32rpx]">
                                    ￥{{ parseFloat(item.basic.delivery_money).toFixed(2) }}
                                </view>
                            </view>
                            <!-- 发票 -->
                            <view class="flex items-center text-[#303133] leading-[30rpx] mt-[36rpx]" @click="invoiceRef.open(item)" v-if="item.config && item.config.invoice &&item.config.invoice.is_invoice == 1">
                                <view class="text-[28rpx] w-[150rpx] text-[#303133]">发票信息</view>
                                <view class="flex-1 w-0 text-right truncate">
                                    <text class="text-[28rpx] text-[#333]">{{( createData.body[key].invoice ? createData.body[key].invoice.header_name : '') || '不需要发票' }}</text>
                                </view>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light9)]" :style="iconfontStyle"></text>
                            </view>
                            <view class="mt-[36rpx]" v-if="couponData[key] && couponData[key].couponList.length && item.is_shop_coupon">
                                <!-- 优惠券 -->
                                <view class="flex items-center h-[40rpx] leading-[40rpx]" @click="couponRef.open(createData.body[key])">
                                    <view class="text-[28rpx] w-[150rpx] text-[#303133] flex-shrink-0">优惠券</view>
                                    <view class="flex-1 flex justify-end truncate">
                                        <text v-if="item.discount && item.discount.coupon" class="text-[var(--primary-color)] text-[28rpx] truncate price-font">
                                            -{{ parseFloat(item.discount.coupon.money).toFixed(2) }}
                                        </text>
                                        <text class="text-[28rpx] text-gray-subtitle" v-else>请选择优惠券</text>
                                    </view>
                                    <text class="nc-iconfont nc-icon-youV6xx -mb-[2rpx] text-[26rpx] text-[var(--text-color-light9)]"></text>
                                </view>
                            </view>
                            <!-- 优惠金额 -->
                            <view class="mt-[36rpx]" v-if="parseFloat(item.basic.discount_money)">
                                <view class="flex items-center leading-[30rpx]">
                                    <view class="text-[28rpx] w-[150rpx] text-[#303133]">优惠金额</view>
                                    <view class="flex-1 flex justify-end truncate" @click="handleShopDiscount(item.basic)">
                                        <text class="text-right price-font text-[28rpx]">-￥{{ parseFloat(item.basic.discount_money).toFixed(2)}}</text>
                                        <text class="nc-iconfont nc-icon-youV6xx -mb-[2rpx] text-[26rpx] text-[var(--text-color-light9)]"></text>
                                    </view> 
                                </view>
                            </view>
                            <view class="mt-[30rpx] flex justify-end items-center text-[28rpx]">
                                <text>共{{item.basic.total_num}}件</text>
                                <text  class="ml-[10rpx]">小计：</text>
                                <text class="price-font text-[32rpx]">￥{{parseFloat(item.basic.order_money).toFixed(2)}}</text>
                            </view>
                        </view>
                    </template>
                </view>
                <!-- 平台优惠券 -->
                <view class="mb-[var(--top-m)] card-template" v-if="platformCouponData && platformCouponData.length && orderData.is_plat_coupon">
                    <view class="flex items-center h-[40rpx] leading-[40rpx]" @click="platformCouponRef.open(createData.mall_discount.platform_coupon_id)">
                        <view class="text-[28rpx] w-[150rpx] text-[#303133] flex-shrink-0">平台优惠券</view>
                        <view class="flex-1 flex justify-end truncate">
                            <text  v-if="orderData.discount && orderData.discount.platform_coupon" class="text-[var(--primary-color)] text-[28rpx] truncate price-font">-￥{{ parseFloat(orderData.discount.platform_coupon.money).toFixed(2) }} </text>
                            <text class="text-[28rpx] text-gray-subtitle" v-else>请选择优惠券</text>
                        </view>
                        <text class="nc-iconfont nc-icon-youV6xx -mb-[2rpx] text-[26rpx] text-[var(--text-color-light9)]"></text>
                    </view>
                </view>
                <view class="card-template mb-[20rpx]" v-if="!orderData.basic.is_need_take_address && (orderData.basic.has_select_delivery_types.includes('store')) ">
                    <view class="card-template-item  justify-between">
                        <view class="text-color text-[26rpx]">姓名</view>
                        <input class="text-right" maxlength="20" placeholder-style="color:#B1B3B5;font-size:26rpx" placeholder="请输入" v-model="createData.delivery.taker_name" />
                    </view>
                    <view class="card-template-item justify-between">
                        <view class="text-color text-[26rpx]">预留手机</view>
                        <input class="text-right" maxlength="11" placeholder-style="color:#B1B3B5;font-size:26rpx" placeholder="请输入" v-model="createData.delivery.taker_mobile" />
                    </view>
                </view>
                <!-- 积分抵扣 -->
                <view class="mb-[var(--top-m)] card-template" v-if="pointConfig.is_open && orderData.basic.is_can_point_deduct">
                    <view class="flex items-center h-[40rpx] leading-[40rpx]" >
                            <view class="text-[28rpx] text-[#303133] flex-shrink-0">使用{{orderData.basic.pre_use_point}}积分可抵扣 <text class="price-font text-[var(--price-text-color)]">￥{{ parseFloat(orderData.basic.pre_point_deduct_money).toFixed(2)}}</text> </view>                        <view class="flex-1 flex justify-end truncate">
                            <u-switch v-model="createData.is_use_point_deduct" activeColor="var(--primary-color)" size="20" :activeValue="1" :inactiveValue="0" @change="pointChange"></u-switch>
                        </view>
                    </view>
                </view>
                <view class="card-template">
                    <view class="title">价格明细</view>
                    <view class="card-template-item">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">商品金额</view>
                        <view class="flex-1 w-0 text-right  price-font text-[#333] text-[32rpx]">
                            ￥{{ parseFloat(orderData.basic.goods_money).toFixed(2) }}
                        </view>
                    </view>
                    <view class="card-template-item" v-if="Number(orderData.basic.delivery_money) > 0">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">配送运费</view>
                        <view class="flex-1 w-0 text-right price-font text-[#333] text-[32rpx]">
                            ￥{{ parseFloat(orderData.basic.delivery_money).toFixed(2) }}
                        </view>
                    </view>
                    <view class="card-template-item" v-if="Number(orderData.basic.shop_discount_money) > 0">
                        <view class="text-[28rpx] w-[150rpx] leading-[30rpx] text-[#303133]">店铺优惠</view>
                        <view class="flex-1 w-0 text-right text-[var(--price-text-color)] text-[32rpx] price-font leading-[1]">
                            -￥{{ parseFloat(orderData.basic.shop_discount_money).toFixed(2) }}
                        </view>
                    </view>
                    <view class="card-template-item" v-if="Number(orderData.basic.mall_discount_money) > 0">
                        <view class="text-[28rpx] w-[180rpx] leading-[30rpx] text-[#303133]">平台优惠</view>
                        <view class="flex-1 flex items-center justify-end truncate" @click="handlePlatformDiscount(orderData.basic)">
                            <text class="text-right price-font text-[32rpx] text-[var(--price-text-color)]">-￥{{ parseFloat(orderData.basic.mall_discount_money).toFixed(2) }}</text>
                            <text class="nc-iconfont nc-icon-youV6xx -mb-[2rpx] text-[26rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                    </view>
                </view>
            </view>
            <u-tabbar :fixed="true" :placeholder="true" :safeAreaInsetBottom="true" zIndex="10">
                <view class="flex-1 flex items-center justify-between pl-[30rpx] pr-[20rpx]">
                    <view class="flex items-baseline">
                        <text class="text-[26rpx] text-[#333] leading-[32rpx]">合计：</text>
                        <view class="inline-block">
                            <text class="text-[26rpx] font-500 text-[var(--price-text-color)] price-font leading-[30rpx]">￥</text>
                            <text class="text-[44rpx]  font-500  text-[var(--price-text-color)] price-font leading-[46rpx]">{{ parseFloat(orderData.basic.order_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[26rpx]  font-500  text-[var(--price-text-color)] price-font leading-[46rpx]">.{{ parseFloat(orderData.basic.order_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                    <button class="min-w-[196rpx] flex items-center justify-center h-[70rpx] font-500 text-[26rpx] leading-[70rpx] !text-[#fff] m-0  rounded-full  primary-btn-bg remove-border" :class="{'opacity-60': isCalculateCompleted}" hover-class="none" @click="create">
                        <text :class="{'mr-[10rpx]': isCalculateCompleted }">提交订单</text>
                        <u-loading-icon v-if="isCalculateCompleted" color="#fff" size='14' show></u-loading-icon>
                    </button>
                </view>
            </u-tabbar>
            
            <!-- 选择配送方式 -->
            <select-delivery :order-key="createData.body" ref="deliveryRef" @confirm="confirmDelivery"/>  
            <!-- 选择自提点 -->
            <select-store ref="storeRef" @confirm="confirmSelectStore" v-show="orderData && orderData.basic && orderData.basic.has_goods_types && orderData.basic.has_goods_types.includes('real')"/>    
            
            <!-- 选择优惠券 -->
            <select-coupon :order-key="createData.body" ref="couponRef" @confirm="confirmSelectCoupon"/>
            <!-- 选择平台优惠券 -->
            <platform-coupon :order-key="createData.body" ref="platformCouponRef" @confirm="confirmPlatformCoupon"/>
            <!-- 发票 -->
            <invoice ref="invoiceRef" @confirm="confirmInvoice" />
            <!-- 地址 -->
			<address-list ref="addressRef" @confirm="confirmAddress" back="/addon/mall/pages/order/payment" />
            <!-- 赠品 -->
            <gift-goods ref="giftGooodsRef"/>
            <!-- 店铺优惠 -->
            <shop-discount ref="shopDiscountRef" />
            <!-- 平台优惠 -->
            <platform-discount ref="platformDiscountRef" />

            <pay ref="payRef" @close="payClose" />
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getLocal } from '@/app/api/basic'
import { orderCreateCalculate, orderCreate ,getPointDeduct} from '@/addon/mall/api/order'
import { redirect, img, mobileHide, deepClone, distance, isWeixinBrowser} from '@/utils/common'
import { useSubscribeMessage } from '@/hooks/useSubscribeMessage'
import selectDelivery from './components/select-delivery/select-delivery'
import selectStore from './components/select-store/select-store'
import selectCoupon from './components/select-coupon/select-coupon'
import platformCoupon from './components/platform-coupon/platform-coupon'
import addressList from './components/address-list/address-list'
import giftGoods from './components/gift-goods/gift-goods'
import invoice from '@/components/invoice/invoice.vue'
import nsSelectTime from '@/addon/mall/components/ns-select-time'
import { topTabar } from '@/utils/topTabbar'
import shopDiscount from './components/shop-discount/shop-discount.vue'
import platformDiscount from './components/platform-discount/platform-discount.vue'
import diyForm from '@/addon/components/diy-form/index.vue'
import useMemberStore from '@/stores/member'
import wechat from '@/utils/wechat'
import { currentTime, timeStamp } from '@/addon/mall/components/ns-select-time/date.js'
import useSystemStore from '@/stores/system';
import { useLocation } from '@/hooks/useLocation'

const topTabarObj = topTabar()
let topTabbarData = topTabarObj.setTopTabbarParam({ title: '待付款订单' })


/************** 定位-start ****************/
const locationVal = useLocation(true);

/************** 定位-end ****************/

// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)
const systemStore = useSystemStore();
const createData = ref({
    body:{},
    order_key: '',
    delivery:{
		take_address_id:'',
        taker_mobile: ''
	},
    mall_discount: {
        platform_coupon_id:''
    },
    form_data: {}, // 万能表单数据（商品+待付款订单）
    position: '',
    is_use_point_deduct: 0,
})
const diyFormRef: any = ref(null)
const diyFormGoodsRef: any = ref(null)
const orderData = ref(null)
const deliveryRef = ref()
const giftGooodsRef = ref()
const storeRef = ref()
const couponRef = ref()
const platformCouponRef = ref()
const invoiceRef = ref()
const payRef = ref()
const addressRef = ref()
const createLoading = ref(false)
const service_time = ref({}) //获取配置时间
const selectTimeRef = ref({})
uni.getStorageSync('orderCreateData') && Object.assign(createData.value, uni.getStorageSync('orderCreateData'))
// 选择地址之后跳转回来
const selectAddress = uni.getStorageSync('selectAddressCallback')
if (selectAddress) {
    createData.value.order_key = ''
    for(let i in createData.value.body){
        createData.value.body[i].order_key = ''
         if(selectAddress.siteDelivery){
            createData.value.body[i].delivery = {}
            createData.value.body[i].delivery = {
                delivery_type: selectAddress.siteDelivery[i].delivery_type,
                local_delivery_type: selectAddress.siteDelivery[i].local_delivery_type,
                buyer_ask_delivery_time: selectAddress.siteDelivery[i].buyer_ask_delivery_time,
            }
         }
    }
    createData.value.delivery.take_address_id = selectAddress.address_id
    uni.removeStorage({ key: 'selectAddressCallback' })
}

onShow(() => { 
    // 刷新定位
	locationVal.refresh();
    createData.value.position = systemStore.diyAddressInfo
})

// 同城配送时间
const setSelectTimeRefs =  (el: any, index: any) => {
    selectTimeRef.value[index] = (el)
}

const handleTime = (key: any) => {
    if ( selectTimeRef.value[key]) {
       selectTimeRef.value[key].show = true
    }
    
}

const isDelivery = ref({})

// 时间(月日时间段)
const getTime = (e: any, site_id: any) => {
	if (createData.value.body[site_id].delivery.delivery_type == 'local_delivery') {
		isDelivery.value[site_id] = {
            show : true,
        }
        let now = (currentTime().seconTime * 1000 ) + (orderData.value.order_list[site_id].delivery.take_store.time_interval * 60 * 1000)
        isDelivery.value[site_id].timeGap = `立即配送(${currentTime().hourTime}-${timeStamp(now).hourTime})`
		if(e.includes('立即配送') ){
			createData.value.body[site_id].delivery.buyer_ask_delivery_time = '';
			createData.value.body[site_id].delivery.local_delivery_type = 'now';
		}else {
			createData.value.body[site_id].delivery.buyer_ask_delivery_time = e;
			createData.value.body[site_id].delivery.local_delivery_type = 'subscribe';
		}
	}else {
		createData.value.body[site_id].delivery.buyer_ask_delivery_time = e;
	}
    createData.value.body[site_id].delivery.flag = true // 处理预约时间会闪一下立即配送
}

const showGetDate = ref({})
const getDate = (e: any, site_id: any) => {
	if (createData.value.body[site_id].delivery.delivery_type == 'local_delivery') {
		if (e.includes('立即配送')) {
			showGetDate.value[site_id] = null
		} else {
			showGetDate.value[site_id] = e
		}
	}else {
		showGetDate.value[site_id] = e
	}
}

/**
 * 订单计算
 */
const isCalculateCompleted = ref(true)
const calculate =  (callback:any = null) => {
    isCalculateCompleted.value = true
    orderCreateCalculate(createData.value).then((res: any) => {
        orderData.value = res.data
        createData.value.order_key = res.data.order_key
        if(orderData.value.basic.has_select_delivery_types.length == 1 && orderData.value.basic.has_select_delivery_types.includes('store') ){
        	createData.value.delivery.taker_name = orderData.value.delivery.taker_name
            createData.value.delivery.taker_mobile = orderData.value.delivery.taker_mobile
        }
        Object.values(orderData.value.order_list).forEach((item: any) =>{
            if(createData.value.body[item.site_id] != undefined){
                createData.value.body[item.site_id].order_key = item.order_key
                createData.value.body[item.site_id].member_remark = ''
                createData.value.body[item.site_id].site_id= item.site_id 
                createData.value.body[item.site_id].delivery = {}
                createData.value.body[item.site_id].delivery.delivery_type = item.delivery.delivery_type
                createData.value.body[item.site_id].delivery.take_store_id = item.delivery.take_store.store_id
                createData.value.body[item.site_id].delivery.local_delivery_type = 'now'
                if(createData.value.body[item.site_id].delivery.delivery_type == 'local_delivery'){
                    service_time.value[item.site_id] = {}
                    service_time.value[item.site_id] = {
                        time_interval: item.delivery.take_store.time_interval,
                        time_week: formatTimeWeek(item.delivery.take_store.time_type, item.delivery.take_store.time_week),
                        trade_time_json: item.delivery.take_store.trade_time_json,
                        most_day: 7,
                        advance_day: 0,
                        type:"subscribe"
                    }
                } 
                if (item.delivery.take_store && createData.value.body[item.site_id].delivery.delivery_type == 'store') {
                    service_time.value[item.site_id] = {}
                    service_time.value[item.site_id] = {
                        time_interval: item.delivery.take_store.time_interval,
                        time_week: item.delivery.take_store.time_week,
                        trade_time_json: item.delivery.take_store.trade_time_json
                    };
                }
                
            }
        })
        isCalculateCompleted.value = false
        callback && callback()
    })
}

calculate()

/**
 * 订单创建
 */
const create = () => {
    if (!verify() || createLoading.value || isCalculateCompleted.value) return
    if (diyFormGoodsRef.value) {
        let pass = true;
        for (let i = 0; i < diyFormGoodsRef.value.length; i++) {
            if (!diyFormGoodsRef.value[i].verify()) {
                pass = false;
                break;
            }
        }
        if (!pass) return;
    }

    if (diyFormRef.value) {
        let pass = true;
        for (let i = 0; i < diyFormRef.value.length; i++) {
            if (!diyFormRef.value[i].verify()) {
                pass = false;
                break;
            }
        }
        if (!pass) return;
    }

    createLoading.value = true

    createData.value.form_data = {}

    let diyFormGoodsData = {}
    if(diyFormGoodsRef.value){
        diyFormGoodsRef.value.forEach((formGoodsItem: any, formGoodsIndex: any) => {
            diyFormGoodsData[formGoodsItem.getData().relate_id] = formGoodsItem.getData()
        })
    }

    let diyFormOrderData = {}
    if(diyFormRef.value){
        diyFormRef.value.forEach((formItem: any, formIndex: any) => {
            diyFormOrderData[formItem.getData().relate_id] = formItem.getData()
        })
    }

    Object.values(orderData.value.order_list).forEach((item: any, index:any) => {
        createData.value.form_data[item.site_id] = {}
        if(diyFormOrderData[item.site_id]) createData.value.form_data[item.site_id].order = diyFormOrderData[item.site_id]
        createData.value.form_data[item.site_id].goods = {}
        Object.values(item.goods_data).forEach((subItem: any, subIndex: any) => {
            if(diyFormGoodsData[subItem.sku_id]) createData.value.form_data[item.site_id].goods[subItem.sku_id] = diyFormGoodsData[subItem.sku_id]
        })
    })

    orderCreate(createData.value).then(({ data }) => {
        if(diyFormGoodsRef.value){
            diyFormGoodsRef.value.forEach((formGoodsItem: any, formGoodsIndex: any) => {
                formGoodsItem.clearStorage()
            })
        }

        if(diyFormRef.value){
            diyFormRef.value.forEach((formItem: any, formIndex: any) => {
                formItem.clearStorage()
            })
        }

        createData.value.form_data = {}
        if (orderData.value.basic.order_money == 0) {
            redirect({ url: '/addon/mall/pages/order/list', mode:'reLaunch' })
        } else {
            // 订阅消息
            useSubscribeMessage().request('mall_order_pay,mall_order_delivery')
            payRef.value?.open(data.trade_type, data.trade_id, `/addon/mall/pages/order/list`)
        }
    }).catch(() => {
        createData.value.form_data = {}
        createLoading.value = false
    })
}

/**
 * 下单校验
 */
const verify = () => {
    if (!orderData.value.delivery.take_address) {
        uni.showToast({ title: '请选择收货地址', icon: 'none' })
        return false
    }
    if (orderData.value?.basic?.has_goods_types?.length == 1 && orderData.value?.basic?.has_goods_types.includes('virtual') && orderData.value?.basic?.virtual_verify_mobile == 1){
        if (createData.value.delivery.taker_mobile == '') {
            uni.showToast({ title: '请填写手机号', icon: 'none' })
            return false
        } else if (!/^1[3-9]\d{9}$/.test(createData.value.delivery.taker_mobile)) {
            uni.showToast({ title: '手机号格式不正确，请重新输入', icon: 'none' })
            return false
        }
    }
    if(!orderData.value?.basic?.is_need_take_address && orderData.value?.basic?.has_select_delivery_types.includes('store') ){
        if (createData.value.delivery.taker_name == '') {
            uni.showToast({ title: '请填写姓名', icon: 'none' })
            return false
        }
        if (createData.value.delivery.taker_mobile == '') {
            uni.showToast({ title: '请填写手机号', icon: 'none' })
            return false
        }
    }
    return true
}

//赠品数量
const getGiftNum = (data) => { 
    if (!Array.isArray(data) || data.length === 0) return 0;
    return data.reduce((total, item) => {
        const giftNum = Number(item.num) || 0;
        return total + giftNum;
    }, 0);
};

/**
 * 支付弹窗关闭
 */
const payClose = () => {
    redirect({ url: '/addon/mall/pages/order/list', mode: 'redirectTo'  })
}

/**
 * 选择地址
 */

const toSelectAddress = () => {
    let data: any = {};
    if (createData.value.body) {
        let hasLocalDelivery = false;
        let siteDelivery: any = {}
        for(let key in createData.value.body) {
            siteDelivery[key] = createData.value.body[key].delivery
        }
        for(let key in createData.value.body) {
            const item = createData.value.body[key];
            if(item.delivery.delivery_type == 'local_delivery'){
                hasLocalDelivery = true
                break;
            }
        }
        if (hasLocalDelivery) {
            data.delivery = 'local_delivery';
        } else {
            data.delivery = 'express';
        }
        data.siteDelivery = siteDelivery;
    }
    
	data.type =  data.delivery == 'local_delivery' ? 'location_address' : 'address';
	data.id = orderData.value.delivery.take_address.id;
	addressRef.value.open(data);
}

const couponData = computed(() => {
    return couponRef.value?.couponData || {}
})
/**
 * 选择优惠券
 */
const confirmSelectCoupon =  async (data:any) => {
    for(let key in data){
        if(data[key].coupon){
            createData.value.body[key].discount = {}
            createData.value.body[key].discount.coupon_id = data[key].coupon.id
        }else{
            createData.value.body[key].discount = {}
            createData.value.body[key].discount.coupon_id= ''
        }
    }
    createData.value.mall_discount.platform_coupon_id = 0
    createData.value.order_key = ''
    for(let i in createData.value.body){
        createData.value.body[i].order_key = ''
    }
   await calculate(()=>{
        platformCouponRef.value?.orderPlatformCouponFn()
        
   })

}
/**
 * 积分抵扣
 */
const pointConfig = ref({})
const getPointDeductFn = ()=> { 
    getPointDeduct().then((res:any)=>{
        pointConfig.value = res.data
    }) 
}
getPointDeductFn()

const pointChange = (value:any)=>{
    calculate()
}

/**
 * 选择配送方式
 */
const confirmDelivery = (data:any) =>{
    createData.value.body[data.site_id].delivery.delivery_type = data.delivery_type
    createData.value.body[data.site_id].delivery.take_store_id = ''
    createData.value.order_key = ''
    for(let i in createData.value.body){
        createData.value.body[i].order_key = ''
    }
    calculate()
}
/**
 * 选择自提点
 */
const confirmSelectStore = (store: any) => {
    createData.value.body[store.site_id].delivery.take_store_id = ((store && store.store_id) ? store.store_id : 0)
    calculate()
}


// 同城配送
const openSelectStore = (data: any) => {
    
    let obj = {
        site_id: data.site_id,
        take_store: deepClone(data.delivery.take_store),
        store_list: deepClone(data.delivery.take_store_list),
    }
    storeRef.value.open(obj)
}

const platformCouponData = computed(() => {
    return platformCouponRef.value?.couponList || []
})
// 选择平台优惠劵
const confirmPlatformCoupon = (data:any) =>{
    createData.value.mall_discount.platform_coupon_id = data ? data.id : 0
    createData.value.order_key = ''
    for(let i in createData.value.body){
        createData.value.body[i].order_key = ''
    }
    calculate()
}

const confirmInvoice = (invoice: object) => {
    createData.value.body[invoice.site_id].invoice = {}
    createData.value.body[invoice.site_id].invoice = uni.$u.deepClone(invoice.invoice)
}
// 选择地址
const confirmAddress = (data:object) => {
	createData.value.order_key = ''
    for(let i in createData.value.body){
        createData.value.body[i].order_key = ''
    }
	createData.value.delivery.delivery_type = data.delivery
	createData.value.delivery.take_address_id = data.address_id
	calculate();
}

const formatTimeWeek = (timeType: number, weekStr: string): string[] => {
    if (timeType === 0) {
        // 每天都可以预约
        return ['1', '2', '3', '4', '5', '6', '0'];
    }
    // 自定义时间周
    return weekStr ? weekStr.split(',').map(item => item.trim()) : [];
};

// 位置
// #ifdef H5
if (isWeixinBrowser()) {
    wechat.init();
}
// #endif
const getAddress = (data: any) => {
    // #ifdef H5
    if (isWeixinBrowser()) {
        wechat.openLocation({
            latitude: Number(data.latitude),
            longitude: Number(data.longitude),
            name: data.full_address,
            address: data.full_address
        })
    }
    // #endif
    // #ifdef MP
	uni.openLocation({
		latitude: Number(data.latitude),
		longitude: Number(data.longitude),
        name: data.full_address,
        address: data.full_address,
		success: function () { }
	});
    // #endif
}

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


// 店铺优惠
const shopDiscountRef = ref()
const handleShopDiscount = (data: any) =>{
    shopDiscountRef.value.open(data)
}

// 平台优惠
const platformDiscountRef = ref()
const handlePlatformDiscount = (data: any) =>{
    platformDiscountRef.value.open(data)
}
</script>

<style lang="scss" scoped>
.goods-alert :deep(.u-alert){
    padding: 6rpx 16rpx !important;
}
:deep(.u-alert) {
   width: 100% !important;
   box-sizing: border-box!important;
}

.line-normal{
    line-height: normal;
}

.bg-color {
    background: linear-gradient(94deg, var(--primary-help-color2) 0%, var(--primary-color) 69%), var(--primary-color);
}

.payment-bottom {
    padding-bottom: calc(20rpx + constant(safe-area-inset-bottom));
    padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
}

.payment-wrap {
    background: linear-gradient(180deg, transparent 0%, transparent 80%, var(--page-bg-color) 100%);
}

.payment-body {
    --shop-payment-header-color: #F42612; // 待支付头部颜色
    --shop-payment-header-tab-color: #FEEAE9; // 待支付头部颜色
    /*  #ifdef MP  */
    background: linear-gradient(180deg, transparent 0%, transparent 10%, var(--page-bg-color) 30%);
    /*  #endif  */
    /*  #ifndef MP */
    background: linear-gradient(180deg, transparent 0%, var(--page-bg-color) 25%);
    /*  #endif  */
    &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        z-index: -1;
        /*  #ifdef MP  */
        height: 560rpx;
        background: linear-gradient(180deg, var(--shop-payment-header-color) 20%, var(--page-bg-color) 90%), var(--page-bg-color);
        /*  #endif  */
        /*  #ifndef MP  */
        height: 360rpx;
        background: linear-gradient(180deg, var(--shop-payment-header-color) 0%, var(--page-bg-color) 90%), var(--page-bg-color);
        /*  #endif  */
    }
}
</style>
