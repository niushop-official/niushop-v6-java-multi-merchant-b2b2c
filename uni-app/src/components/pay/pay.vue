<template>
    <u-popup :show="show" :round="10" @close="handleClose" :closeable="true" bgColor="#fff" zIndex="10081" :closeOnClickOverlay="false">
        <view class="flex flex-col h-[65vh] popup-common" v-if="payInfo" @touchmove.prevent.stop>
            <view class="head">
                <view class="title">{{ t('pay.payTitle') }}</view>
                <view class="flex items-end justify-center w-full text-xl font-bold py-[20rpx] price-font">
                    <view class="text-base mr-[4rpx]">{{ t('currency') }}</view>
                    {{ moneyFormat(payInfo.money) }}
                </view>
            </view>
            <scroll-view :scroll-y="true" class="flex-1 pt-[20rpx] min-h-[38vh]">
                <view class="flex items-center text-[28rpx] px-[36rpx] py-[20rpx] mb-[10rpx]">
                    <view class="text-[var(--text-color-light6)]">{{ t('pay.orderInfo') }}</view>
                    <view class="text-right flex-1 pl-[30rpx] py-[4rpx] truncate">{{ payInfo.body }}</view>
                </view>
                <view class="mx-[var(--popup-sidebar-m)] px-[30rpx] bg-white rounded-[20rpx] bg-[var(--temp-bg)]">
                    <template v-if="payInfo.pay_type_list.length">
                        <view class="pay-item py-[30rpx] flex items-center border-0 border-b border-solid border-[#eee]" v-for="(item, index) in payInfo.pay_type_list" :key="index" @click="type = item.key">
                            <u-image :src="img(item.icon)" width="50rpx" height="50rpx"></u-image>
                            <view class="flex-1 px-[20rpx] text-[28rpx] font-500">{{ item.name }}</view>
                            <u-icon name="checkbox-mark" color="var(--primary-color)" v-if="item.key == type"></u-icon>
                        </view>
                    </template>
                    <view class="py-[30rpx] text-center text-[24rpx] text-gray-subtitle" v-else>{{ t('pay.notHavePayType') }}</view>
                </view>
            </scroll-view>
            <view class="btn-wrap flex-shrink-0">
                <button class="primary-btn-bg btn" hover-class="none" :loading="loading" @click="confirmPay">{{ t('pay.confirmPay') }}</button>
            </view>
        </view>
    </u-popup>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { t } from '@/locale'
import { getPayInfo, pay } from '@/app/api/pay'
import { img, redirect, moneyFormat } from '@/utils/common'
import { paymentService } from '@/utils/payment'

const prop = defineProps({
    ignorePay: {
        type: Array,
        default () {
            return []
        }
    },
    reformat: {
        type: String,
        default: ''
    }
})

const show = ref(false)
const loading = ref(false)

const payInfo = ref<AnyObject | null>(null)
const type = ref('')


onMounted(async () => {
    await paymentService.init()
})


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
/**
 * 检测是否是支付后返回
 */
uni.$on('checkIsReturnAfterPayment', () => {
    const data = uni.getStorageSync('paymenting')
    if (uni.getStorageSync('paymenting')) {
        redirect({
            url: '/app/pages/pay/result',
            param: {
                trade_type: data.trade_type,
                trade_id: data.trade_id
            },
            mode: 'redirectTo',
            success() {
                uni.removeStorageSync('paymenting')
            }
        })
    }
})
const repeat = ref(false)
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
        payInfo.value = data
        if(prop.ignorePay) {
            data.pay_type_list = data.pay_type_list.filter((item: any) => {
                return !prop.ignorePay.includes(item.key)
            })
        }
        type.value = data.pay_type_list[0] ? data.pay_type_list[0].key : ''
        show.value = true
        repeat.value = false

    }).catch(() => {
        repeat.value = false
    })
}

const emits = defineEmits(['close', 'confirm'])
const toPayResult = () => {
    emits('confirm')
    redirect({
        url: '/app/pages/pay/result',
        param: { trade_type: payInfo.value?.trade_type, trade_id: payInfo.value?.trade_id },
        mode: 'redirectTo'
    })
}


const handleClose = () => {
    uni.removeStorageSync('paymenting')
    show.value = false
    emits('close')
}

defineExpose({
    open  
})
</script>

<style lang="scss" scoped>
.pay-item:last-child {
  border: none;
}
</style>
