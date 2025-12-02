<template>
    <view :style="themeColor()">
        <swiper :indicator-dots="false" :autoplay="false" :disable-touch="true" :current="step" class="h-screen" :duration="300" v-if="detail">
            <swiper-item>
                <scroll-view scroll-y="true" class="bg-page min-h-screen overflow-hidden">
                    <view class="card-wrap goods-card">
                        <view class="flex">
                            <view class="goods-img-wrap">
                                <image class="goods-img" :src="img(orderDetail.sku_image || 'static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                            </view>
                            <view class="flex-1 min-w-0 ml-[20rpx]">
                                <view class="goods-name">{{ orderDetail.goods_name }}</view>
                                <view v-if="orderDetail.sku_name" class="goods-spec">{{ orderDetail.sku_name }}</view>
                            </view>
                        </view>
                    </view>

                    <view class="card-wrap">
                        <view class="py-[var(--pad-top-m)] flex items-center" @click="selectRefundType(1)">
                            <view class="flex-1">
                                <view class="card-title">仅退款</view>
                                <view class="card-desc" v-if="orderDetail.goods_type !='virtual'">未收到货，或与商家协商一致不用退货只退款</view>
                                <view class="card-desc" v-else>与商家协商一致仅退款</view>
                            </view>
                            <text class="nc-iconfont nc-icon-youV6xx text-[28rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                        <view class="py-[var(--pad-top-m)] flex items-center border-0 !border-t !border-[#f5f5f5] border-solid" v-if="orderDetail.goods_type == 'real' && (!orderDetail.delivery_status || orderDetail.delivery_status != 'wait_delivery')" @click="selectRefundType(2)">
                            <view class="flex-1">
                                <view class="card-title">退货退款</view>
                                <view class="card-desc">已收到货，需退还收到的货物</view>
                            </view>
                            <text class="nc-iconfont nc-icon-youV6xx text-[28rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                    </view>
                </scroll-view>
            </swiper-item>
            <swiper-item>
                <scroll-view scroll-y="true" class="bg-page min-h-screen overflow-hidden">
                    <view class="card-wrap">
                        <view class="py-[var(--pad-top-m)] flex justify-between items-center">
                            <view class="section-title">退款原因</view>
                            <view class="flex ml-[auto] items-center h-[30rpx]" @click="refundCausePopup = true">
                                <text class="text-[26rpx] reason-text truncate max-w-[460rpx]">{{ formData.reason || '请选择' }}</text>
                                <text class="nc-iconfont nc-icon-youV6xx pt-[4rpx] text-[24rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                        </view>
                    </view>
                    <view class="card-wrap">
                        <view class="py-[var(--pad-top-m)]">
                            <view class="flex items-center justify-between">
                                <view class="section-title">退款金额</view>
                                <view class="flex justify-end items-center text-[var(--price-text-color)] price-font">
                                    <text class="font-500 text-[36rpx] leading-none">￥</text>
                                    <text class="font-500 text-[36rpx] leading-none">{{ formData.apply_money }}</text>
                                </view>
                            </view>
                            <view class="text-right text-[24rpx] text-[var(--text-color-light9)] mt-[10rpx]">
                                <text v-if="refundMoney.is_refund_delivery === 1 && Number(refundMoney.refund_delivery_money) > 0" class="ml-[10rpx]">(包含运费￥{{ refundMoney.refund_delivery_money }})</text>
                            </view>
                        </view>
                    </view>
                    <view class="card-wrap">
                        <view class="pt-[var(--pad-top-m)] pb-[14rpx]">
                            <view class="text-[28rpx] flex items-center font-500">
                                <text>上传凭证</text>
                                <text class="text-[24rpx] text-[var(--text-color-light9)] ml-[10rpx] font-normal">选填</text>
                            </view>
                            <view class="mt-[30rpx]">
                                <u-upload :fileList="voucherListPreview"
                                    @afterRead="afterRead" @delete="deletePic" multiple  :maxCount="9"></u-upload>
                            </view>
                        </view>
                    </view>
                    <view class="card-wrap">
                        <view class="py-[var(--pad-top-m)]">
                            <view class="text-[28rpx] flex items-center font-500">
                                <text>补充描述</text>
                                <text class="text-[24rpx] text-[var(--text-color-light9)] ml-[10rpx] font-normal">选填</text>
                            </view>
                            <view class="mt-[30rpx] h-[200rpx]">
                                <textarea class="leading-[1.5] h-[100%] w-[100%] text-[28rpx]" v-model="formData.remark" :maxlength="100" cols="30" rows="5" placeholder="补充描述,有助于更好的处理售后问题" placeholder-class="text-[26rpx] text-[var(--text-color-light9)]"></textarea>
                            </view>
                        </view>
                    </view>
                    <view class="w-full">
                        <view class="py-[var(--top-m)] px-[var(--sidebar-m)] box-border">
                            <button class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx] rounded-[100rpx] text-[26rpx] font-500" :loading="operateLoading" @click="save">提交</button>
                        </view>
                    </view>

                    <!-- 退款原因 -->
                    <u-popup :show="refundCausePopup" @close="refundCausePopup = false">
                        <view class="popup-common" @touchmove.prevent.stop>
                            <view class="title">退款原因</view>
                            <scroll-view scroll-y="true" class="h-[450rpx] px-[30rpx] box-border">
                                <u-radio-group v-model="currReasonName" placement="column" iconPlacement="right">
                                    <u-radio activeColor="var(--primary-color)" :labelSize="'30rpx'" labelColor="#333"  :customStyle="{marginBottom: '34rpx'}" v-for="(item, index) in reason" :key="index" :label="item" :name="item"></u-radio>
                                </u-radio-group>
                            </scroll-view>
                            <view class="btn-wrap">
								<button class="primary-btn-bg btn" @click="refundCausePopupFn">确定</button>
							</view>
                        </view>
                    </u-popup>
                </scroll-view>
            </swiper-item>
        </swiper>
        <!-- #ifdef MP-WEIXIN -->
        <!-- 小程序隐私协议 -->
        <wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
        <!-- #endif -->
    </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { redirect, img, moneyFormat, goback } from '@/utils/common'
import { t } from '@/locale'
import { getMallOrderDetail } from '@/addon/mall/api/order'
import { getRefundReason, applyRefund, getRefundMoney } from '@/addon/mall/api/refund'
import { uploadImage } from '@/app/api/system'
import { useSubscribeMessage } from '@/hooks/useSubscribeMessage'
import useConfigStore from '@/stores/config'

interface OrderGoods {
  order_goods_id?: number
  sku_image?: string
  goods_name?: string
  sku_name?: string
  goods_type?: string
  delivery_status?: string
}

interface RefundForm {
  order_id: number
  order_goods_id: number
  refund_type: string
  apply_money: string
  reason: string
  remark: string
  voucher: string[]
}

const configStore = useConfigStore()
const themeColor = () => configStore.getThemeColor()

const detail = ref<any>(null)
const orderDetail = ref<OrderGoods>({})
const orderGoodsId = ref(0)
const step = ref(0)
const refundCausePopup = ref(false)
const formData = ref<RefundForm>({
  order_id: 0,
  order_goods_id: 0,
  refund_type: '',
  apply_money: '',
  reason: '',
  remark: '',
  voucher: []
})
const refundMoney = ref<Record<string, any>>({})
const reason = ref<string[]>([])
const currReasonName = ref('')

getRefundReason()
  .then((res: any) => {
    reason.value = res?.data || []
    if (reason.value.length) currReasonName.value = reason.value[0]
  })
  .catch(() => {})

onLoad((data?: Record<string, any>) => {
  const query = data || {}
  orderGoodsId.value = Number(query.order_goods_id) || 0
  formData.value.order_goods_id = orderGoodsId.value
  formData.value.order_id = Number(query.order_id) || 0
  if (query.order_id && query.order_goods_id) {
    getMallOrderDetail(Number(query.order_id))
      .then((res: any) => {
        detail.value = res?.data || null
        const goodsList: OrderGoods[] = detail.value?.order_goods || []
        goodsList.forEach((item: OrderGoods) => {
          if (orderGoodsId.value === item.order_goods_id) {
            orderDetail.value = item
          }
        })
      })
      .catch(() => {})
    getRefundMoney({ order_goods_id: query.order_goods_id })
      .then((res: any) => {
        refundMoney.value = res?.data || {}
        formData.value.apply_money = moneyFormat(refundMoney.value.refund_money || 0)
      })
      .catch(() => {})
  } else {
    goback({
      url: '/addon/mall/pages/order/list',
      title: '缺少订单id'
    })
  }
})

const inputWidth = computed(() => {
  return function (value: string | number) {
    if (value === '' || value === 0) {
      return '60rpx'
    } else {
      return String(value).length * 17 + 'rpx'
    }
  }
})

const selectRefundType = (type: number) => {
  formData.value.refund_type = String(type)
  step.value = 1
}

const voucherListPreview = computed(() => {
  return formData.value.voucher.map((item: string) => {
    return { url: img(item) }
  })
})

const afterRead = (event: any) => {
  event.file.forEach((item: any) => {
    uploadImage({
      filePath: item.url,
      name: 'file'
    })
      .then((res: any) => {
        if (formData.value.voucher.length < 9) {
          formData.value.voucher.push(res.data.url)
        }
      })
      .catch(() => {})
  })
}

const deletePic = (event: any) => {
  formData.value.voucher.splice(event.index, 1)
}

const operateLoading = ref(false)
const save = () => {
  if (!formData.value.reason) {
    uni.showToast({
      title: '请选择退款原因',
      icon: 'none'
    })
    return false
  }

  if (Number(formData.value.apply_money) <= 0) {
    uni.showToast({
      title: '退款金额不能为0,保留两位小数',
      icon: 'none'
    })
    return false
  }

  if (operateLoading.value) return
  operateLoading.value = true
  applyRefund(formData.value)
    .then(() => {
      operateLoading.value = false
      useSubscribeMessage().request('mall_refund_agree,mall_refund_refuse')
      setTimeout(() => {
        redirect({ url: '/addon/mall/pages/order/detail', param: { order_id: formData.value.order_id } })
      }, 1000)
    })
    .catch(() => {
      operateLoading.value = false
    })
}

const refundCausePopupFn = () => {
  formData.value.reason = currReasonName.value
  refundCausePopup.value = false
}

const handleInput = (event: any) => {
  if (Number(event.detail.value) > Number(refundMoney.value.refund_money || 0)) {
    uni.showToast({
      title: '退款金额不能大于可退款总额',
      icon: 'none'
    })
  }
}
</script>
<style lang="scss" scoped>
    .card-wrap {
        margin: var(--top-m) var(--sidebar-m);
        padding: var(--pad-top-m) var(--pad-sidebar-m);
        border-radius: var(--rounded-big);
        background-color: #fff;
        box-shadow: 0 6rpx 24rpx rgba(0, 0, 0, 0.04);
        backdrop-filter: saturate(180%) blur(8px);
    }
    .goods-card {
        display: flex;
        align-items: center;
    }
    .goods-img-wrap {
        width: 120rpx;
        height: 120rpx;
        border-radius: var(--goods-rounded-small);
        overflow: hidden;
        flex-shrink: 0;
    }
    .goods-img {
        width: 100%;
        height: 100%;
        border-radius: var(--goods-rounded-small);
        object-fit: cover;
        image-rendering: -webkit-optimize-contrast;
        image-rendering: crisp-edges;
        transform: translateZ(0);
    }
    .goods-name {
        font-size: 28rpx;
        line-height: 1.4;
        font-weight: 500;
        color: #1d1d1f;
        text-shadow: 0 0 0 transparent;
    }
    .goods-spec {
        margin-top: 8rpx;
        font-size: 24rpx;
        line-height: 1.3;
        color: var(--text-color-light9);
    }
    .card-title {
        font-size: 30rpx;
        font-weight: 500;
        color: #1d1d1f;
    }
    .card-desc {
        margin-top: 18rpx;
        font-size: 24rpx;
        line-height: 1.4;
        color: var(--text-color-light9);
    }
    .section-title {
        font-size: 28rpx;
        font-weight: 500;
        color: #1d1d1f;
    }
    .reason-text {
        color: var(--text-color-light9);
    }
    :deep(.u-upload__button) {
        width: 70px !important;
        height: 70px !important;
        border: 1px dashed #ddd;
		background-color: #fff;
		border-radius: 20rpx !important;
    }
    :deep(.u-upload__wrap__preview__image) {
        width: 70px!important;
        height: 70px!important;
    }
</style>
