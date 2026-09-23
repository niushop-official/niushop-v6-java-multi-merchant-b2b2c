<template>
    <view :style="themeColor()">
        <view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden" v-if="!loading" >
            <view class="px-[30rpx] bg-linear h-[150rpx] flex items-center">
                <view class="w-[110rpx] h-[110rpx] flex items-center justify-center">
                    <text class="iconfont iconfapiao text-[80rpx] text-[#fff]"></text>
                </view>
                <view class="ml-[20rpx]">
                    <view class="text-[30rpx] text-[#fff] mb-[6rpx] font-700 leading-[40rpx]">{{ detail.is_invoice_name }}</view>
                    <view class="text-[24rpx] text-[#fff] leading-[32rpx] opacity-80">{{ t('applyTime') }}{{ detail.create_time }}</view>
                </view>
            </view>
            <view class="mx-[var(--sidebar-m)] mt-[var(--top-m)]">
                <view class="card-template  mb-[var(--top-m)]">
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('invoiceType') }}</view>
                        <view>{{ detail.type_name }}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('headerType') }}</view>
                        <view>{{ detail.header_type_name }}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('headerName') }}</view>
                        <view class="break-all">{{ detail.header_name }}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]" v-if="detail.name">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('name') }}</view>
                        <view>{{ detail.name }}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]" v-if="detail.email">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('email') }}</view>
                        <view>{{detail.email}}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]" v-if="detail.invoice_number">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('invoiceNumber') }}</view>
                        <view>{{detail.invoice_number}}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]" v-if="detail.money">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('money') }}</view>
                        <view>{{detail.money}}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]" v-if="detail.invoice_time">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('invoiceTime') }}</view>
                        <view>{{detail.invoice_time}}</view>
                    </view>
                    <template v-if="detail.header_type == 2">
                        <view class="justify-between card-template-item text-[28rpx]" v-if="detail.tax_number">
                            <view class="flex-shrink-0 mr-[20rpx]">{{ t('taxNumber') }}</view>
                            <view class="break-all">{{detail.tax_number}}</view>
                        </view>
                        <view class="justify-between card-template-item text-[28rpx]" v-if="detail.bank_name">
                            <view class="flex-shrink-0 mr-[20rpx]">{{ t('bankTame') }}</view>
                            <view>{{detail.bank_name}}</view>
                        </view>
                        <view class="justify-between card-template-item text-[28rpx]" v-if="detail.bank_card_number">
                            <view class="flex-shrink-0 mr-[20rpx]">{{ t('bankCardNumber') }}</view>
                            <view>{{detail.bank_card_number}}</view>
                        </view>

                        <view class="justify-between card-template-item text-[28rpx]" v-if="detail.telephone">
                            <view class="flex-shrink-0 mr-[20rpx]">{{ t('telephone') }}</view>
                            <view>{{detail.telephone}}</view>
                        </view>
                        <view class="justify-between card-template-item text-[28rpx]" v-if="detail.address">
                            <view class="flex-shrink-0 mr-[20rpx]">{{ t('address') }}</view>
                            <view>{{detail.address}}</view>
                        </view>
                    </template>
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('applyCreateTime') }}</view>
                        <view>{{detail.create_time}}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]" v-if="detail.invoice_voucher">
                        <view class="flex-shrink-0 mr-[20rpx]">{{ t('invoiceVoucher') }}</view>
                        <view @click="previewImage(detail.invoice_voucher)">
                            <image :src="img(detail.invoice_voucher)"  model="aspectFill" class="w-[80rpx] h-[80rpx] overflow-hidden" />
                        </view>
                    </view>
                </view>
                <view class="card-template mb-[var(--top-m)]">
                    <block v-for="(item, index) in detail.orderData.orderGoods">
                        <view class="order-goods-item flex justify-between flex-wrap mt-[30rpx]">
                            <view class="w-[150rpx] h-[150rpx] rounded-[var(--goods-rounded-big)] overflow-hidden">
                                <up-image class="overflow-hidden" radius="var(--goods-rounded-big)" width="150rpx" height="150rpx" :src="img(item.sku_image)" model="aspectFill">
                                    <template #error>
                                        <image
										class="w-[150rpx] h-[150rpx] rounded-[var(--goods-rounded-big)] overflow-hidden"
										:src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill">
									</image>
                                    </template>
                                </up-image>
                            </view>
                            <view class="ml-[20rpx] flex flex-1 flex-col justify-between">
                                <view>
                                    <view class="text-[28rpx] max-w-[490rpx] truncate leading-[40rpx] text-[#333]">{{ item.goods_name }}</view>
                                    <view v-if="item.sku_name">
                                        <view class="text-[22rpx] mt-[14rpx] text-[var(--text-color-light9)] truncate max-w-[490rpx] leading-[28rpx]">{{ item.sku_name }}</view>
                                    </view>
                                </view>
                                <view class="flex justify-between items-baseline leading-[28rpx] text-[#333]">
                                    <view class="price-font">
                                        <text class="text-[24rpx]">￥</text>
                                        <text class="text-[40rpx] font-500">{{ parseFloat(item.price).toFixed(2).split('.')[0] }}</text>
                                        <text class="text-[24rpx] font-500">.{{ parseFloat(item.price).toFixed(2).split('.')[1] }}</text>
                                    </view>
                                    <text class="text-right text-[26rpx]">x{{ item.num }}</text>
                                </view>
                            </view>
                        </view>
                    </block>
                </view>
                <view class="card-template  mb-[40rpx]">
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view>{{ t('orderNo') }}</view>
                        <view class="flex items-center">
                            <text>{{ detail.orderData.order_no }}</text>
                            <text class="w-[2rpx] h-[20rpx] bg-[#999] mx-[10rpx]"></text>
                            <text class="text-[#EF900A]" @click="copy(detail.orderData.order_no)">{{ t('copy') }}</text>
                        </view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view>{{ t('orderTime') }}</view>
                        <view>{{ detail.orderData.create_time }}</view>
                    </view>
                    <view v-if="detail.orderData.pay" class="justify-between card-template-item text-[28rpx]">
                        <view>{{ t('payTypeName') }}</view>
                        <view>{{ detail.orderData.pay.type_name }}</view>
                    </view>
                    <view v-if="detail.orderData.pay"  class="justify-between card-template-item text-[28rpx]">
                        <view>{{ t('payTime') }}</view>
                        <view>{{ detail.orderData.pay.pay_time }}</view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view>{{ t('goodsMoney') }}</view>
                        <view class="price-font font-500">
                            <text class="text-[28rpx]">￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.orderData.goods_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.orderData.goods_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                    <view class="justify-between card-template-item text-[28rpx]">
                        <view>{{ t('orderMoney') }}</view>
                        <view class="price-font font-500">
                            <text class="text-[28rpx]">￥</text>
                            <text class="text-[28rpx]">{{ parseFloat(detail.orderData.order_money).toFixed(2).split('.')[0] }}</text>
                            <text class="text-[28rpx]">.{{ parseFloat(detail.orderData.order_money).toFixed(2).split('.')[1] }}</text>
                        </view>
                    </view>
                </view>
            </view>
        </view>
         <!-- #ifdef MP-WEIXIN -->
        <!-- 小程序隐私协议 -->
        <wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
        <!-- #endif -->
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { t } from '@/locale'
import { img, copy } from '@/utils/common';
import { onLoad,onPageScroll, onReachBottom } from '@dcloudio/uni-app'
import { getMallInvoiceDetail } from '@/app/api/invoice';
let loading = ref<boolean>(true);
let detail = ref<any>({});
let id = ref('')
onLoad((option) => {
	id.value = option.id;
	getMallInvoiceDetailFn(id.value);
});
const getMallInvoiceDetailFn = (id: any) =>{
    loading.value = true;
    getMallInvoiceDetail(id).then(res =>{
        detail.value = res.data 
        loading.value = false;
    })
}
// 预览图片
const previewImage = (data:any)=>{
    if (data === '') return false
		let urlList = []
		urlList.push(img(data)) 
		uni.previewImage({
			indicator: "number",
			loop: true,
			urls: urlList
		})
}
</script>

<style lang="scss" scoped>
.bg-linear {
	background: linear-gradient( 94deg, #FF0000 15%, #ff7900 87%);
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
</style>