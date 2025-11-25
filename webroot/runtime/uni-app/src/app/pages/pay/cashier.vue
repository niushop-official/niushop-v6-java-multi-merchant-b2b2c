<template>
     <view class="w-screen h-screen bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()" v-if="payInfo">
        <view class="m-[var(--sidebar-m)] card-template flex flex-col justify-center items-center">
            <view class="my-[40rpx]">
                <view v-if="systemStore.site?.front_end_logo" class="w-[220rpx]">
                    <image class="w-[220rpx]" :src="img(systemStore.site?.front_end_logo)" mode="widthFix" @error="systemStore.site.front_end_logo='static/resource/images/logo.png'" />
                </view>
            </view>
            <view class="text-[56rpx] font-500 mb-[32rpx] price-font">
                <text class="text-[36rpx] mr-[6rpx]">{{ t('currency') }}</text>
                <text>{{ moneyFormat(payInfo.money) }}</text>
            </view>
            <view class="text-[26rpx] text-center text-[#999] mb-[20rpx] leading-[32rpx] multi-hidden">
                {{ payInfo.body }}
            </view>
            <view class="text-[26rpx] text-[#999]">
                订单编号：{{ payInfo.out_trade_no }}
            </view>
        </view>
        <view class="mx-[var(--popup-sidebar-m)] px-[30rpx] bg-[#fff] rounded-[20rpx] bg-[var(--temp-bg)]">
            <view class="text-[28rpx] py-[30rpx]">选择支付方式</view>
            <block v-if="payInfo.pay_type_list.length">
                <view class="pay-item py-[30rpx] flex items-center border-0 border-b border-solid border-[#eee]" v-for="(item, index) in payInfo.pay_type_list" :key="index" @click="type = item.key">
                <u-image :src="img(item.icon)" width="50rpx" height="50rpx"></u-image>
                <view class="flex-1 px-[20rpx] text-[28rpx] font-500">{{ item.name }}</view>
                <u-icon name="checkbox-mark" color="var(--primary-color)" v-if="item.key == type"></u-icon>
                </view>
            </block>
            <view class="py-[30rpx] text-center text-[24rpx] text-gray-subtitle" v-else>{{ t('pay.notHavePayType') }}</view>
        </view>
        <view class="tab-bar-placeholder"></view>
        <view class="fixed bottom-[0] tab-bar left-0 right-0 px-[var(--sidebar-m)] bg-[var(--page-bg-color)] btn-wrap">
            <button class="primary-btn-bg btn" hover-class="none" :loading="loading" @click="confirmPay">{{ t('pay.confirmPay') }}</button>
        </view>
    </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { t } from '@/locale'
import { onLoad } from '@dcloudio/uni-app'
import { img, redirect, isWeixinBrowser, moneyFormat } from '@/utils/common'
import { getPayInfo, pay } from '@/app/api/pay'
import { paymentService } from '@/utils/payment'
import useSystemStore from '@/stores/system'

const systemStore = useSystemStore()

const payInfo = ref<AnyObject | null>(null)
const type = ref('')
const loading = ref(false)
const repeat = ref(false)
const trade_id = ref('')
const trade_type = ref('')
const pay_type = ref('')
const out_trade_no = ref('')

onLoad( async (option: any) => {
    await systemStore.getSiteInfoFn()
    pay_type.value = option.pay_type || ''
    trade_id.value = option.trade_id || ''
    trade_type.value = option.trade_type || ''
    out_trade_no.value = option.out_trade_no || ''
    open(trade_type.value, trade_id.value, option.payReturn, option.scene)
})




onMounted(async () => {
  await paymentService.init()
})

const open = (tradeType: string, tradeId: number, payReturn: string = '', scene: string = '') => {

    if (repeat.value) return
    repeat.value = true

    // 设置支付后跳转页面
    uni.setStorageSync('payReturn', encodeURIComponent(payReturn))

    // 帮人付款时过滤帮付选项
    const obj: any = {}
    if (scene) {
        obj.scene = scene
    }
    getPayInfo(tradeType, tradeId, obj).then((res: any) => {

        let { data } = res
        payInfo.value = data

        if (uni.$u.test.isEmpty(data)) {
            uni.showToast({ title: t('pay.notObtainedInfo'), icon: 'none' })
            return
        }
        if (data.money == 0) {
            toPayResult()
            return
        }
        type.value = pay_type.value ? pay_type.value :  (data.pay_type_list[0] ? data.pay_type_list[0].key : '')
        
        repeat.value = false

    }).catch(() => {
        repeat.value = false
    })
}

/**
 * 确认支付
 */
 const confirmPay = () => {
  if (uni.$u.test.isEmpty(type.value)) {
    uni.showToast({ title: t('pay.notHavePayType'), icon: 'none' })
    return
  }
  if (type.value == 'friendspay') {
    redirect({
      url: '/app/pages/friendspay/share',
      param: {
        id: payInfo.value?.trade_id,
        type: payInfo.value?.trade_type,
      },
      mode: 'redirectTo'
    })
    return false
  }
  if (loading.value) return
  loading.value = true

  pay({
    trade_type: payInfo.value?.trade_type,
    trade_id: payInfo.value?.trade_id,
    type: type.value,
    openid: uni.getStorageSync('openid') || ''
  }).then((res: any) => {
    handlePayment(res.data, type.value);
  }).catch(() => {
    loading.value = false
  })
}
const handlePayment = async (res: any, payType: any) => {
  try {
    loading.value = true

    await paymentService.autoPay({order: res, pay_type: payType, payInfo: payInfo,loading: loading })

    // 支付成功处理
    toPayResult()
  } catch (error) {
    loading.value = false
    console.log(error);
  } finally {
    loading.value = false
  }
}

const toPayResult = () => {
  redirect({
    url: '/app/pages/pay/result',
    param: { trade_type: payInfo.value?.trade_type, trade_id: payInfo.value?.trade_id },
    mode: 'redirectTo'
  })
}

</script>

<style lang="scss" scoped>
.tab-bar-placeholder {
    padding-bottom: calc(constant(safe-area-inset-bottom) + 166rpx);
    padding-bottom: calc(env(safe-area-inset-bottom) + 166rpx);
}

.tab-bar {
    padding-bottom: calc(constant(safe-area-inset-bottom) + 30rpx);
    padding-bottom: calc(env(safe-area-inset-bottom) + 30rpx);
}

.btn-wrap{
    padding: 20rpx 20rpx 30rpx;
    .btn{
        color: #fff;
        height: 80rpx;
        line-height: 80rpx;
        border-radius: 100rpx;
        font-size: 26rpx;
        font-weight: 500;
    }
}
</style>