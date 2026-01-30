<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()">
        <view class="px-[24rpx] py-[30rpx] flex items-center justify-between " @click="redirect({ url: '/app/pages/account/transfer_type' })">
            <text class="text-[28rpx] ">到账方式</text>
            <view class="flex items-center">
                <view v-if="formData.bank_type == 1" class="flex items-center mr-[20rpx]">
                    <image class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/bank-icon.png')" mode="widthFix" />
                    <text class="text-[28rpx]">{{ formData.bank_account_no }}</text>
                </view>
                <view v-if="formData.bank_type == 2" class="flex items-center mr-[20rpx]">
                    <image class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/alipay-icon.png')" mode="widthFix" />
                    <text class="text-[28rpx]">{{ formData.alipay_account_no }}</text>
                </view>
                <view v-if="formData.bank_type == 3" class="flex items-center mr-[20rpx]">
                    <image class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')" mode="widthFix" />
                    <text class="text-[28rpx]">{{ formData.wechat_account_no }}</text>
                </view>
                <view v-if="formData.bank_type == 4" class="flex items-center mr-[20rpx]">
                    <image class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat.png')" mode="widthFix" />
                    <text class="text-[28rpx]">微信零钱</text>
                </view>
                <view class="nc-iconfont nc-icon-youV6xx text-[28rpx] flex-center"></view>
            </view>
        </view>
        <view class="sidebar-margin card-template mt-[20rpx]">
            <view class="title">提现金额</view>
            <view class="flex pt-[50rpx] pb-[20rpx] items-center border-0 border-b-[2rpx] border-solid border-[#F1F2F5]">
                <text class="pt-[4rpx] text-[54rpx] text-[#333] price-font">￥</text>
                <input type="digit" class="h-[88rpx] leading-[88rpx] pl-[10rpx] flex-1 font-500 text-[64rpx] bg-[#fff]" v-model="formData.apply_money" maxlength="7" placeholder="请输入提现金额" placeholder-class="apply-price" :adjust-position="false" />
                <text @click="clearMoney" v-if="formData.apply_money" class="nc-iconfont nc-icon-cuohaoV6xx1 !text-[32rpx] text-[var(--text-color-light9)]"></text>
            </view>
            <view class="pt-[20rpx] flex items-center px-[4rpx] text-[24rpx] text-[var(--text-color-light6)] leading-[36rpx]">
                <text>可提现金额：￥{{ moneyFormat(formData.money) }}</text>
                <text class="text-primary ml-[20rpx]" @click="allCashOut">全部提现</text>
            </view>
        </view>
        <view class="footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] w-full fixed bottom-0 left-0 right-0 box-border">
                <button hover-class="none" class="h-[80rpx] !text-[#fff] leading-[80rpx] bg-primary  text-[26rpx]" :class="{'opacity-50': formData.apply_money == '' || formData.apply_money == 0}" :loading="repeatLoading" @click="cashOut">立即提现</button>
            </view>
        </view>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { img, moneyFormat, redirect } from '@/utils/common'
import { getCashApply, getShopAccountInfo } from '@/app/api/account'
import { onShow } from '@dcloudio/uni-app'

/**
 * 表单数据
 */
const initialFormData = {
    apply_money: '',
    bank_account_no: ''

}
const formData: Record<string, any> = reactive({ ...initialFormData })
const loading = ref(true)
const repeatLoading = ref(false)


const getShopAccountInfoFn = () => {
    loading.value = true
    getShopAccountInfo().then((res: any) => {
        Object.assign(formData, res.data)
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

onShow(() => {
    getShopAccountInfoFn()
})

// 清空提现金额
const clearMoney = () => {
    formData.apply_money = '';
}

const  allCashOut = () => {
    formData.apply_money = formData.money;
}
const  cashOut = () => {
    if (formData.apply_money == '') {
        uni.showToast({
            title: '请输入提现金额',
            icon: 'none'
        })
        return false
    }
    if (formData.apply_money < 0) {
        uni.showToast({
            title: '提现金额不能小于0',
            icon: 'none'
        })
        return false
    }
    if(repeatLoading.value) return false
    repeatLoading.value = true
    getCashApply(formData).then((res: any) => {
        repeatLoading.value = false
        redirect({ url: '/app/pages/account/cash_out_result', param: { id: res.data.id },  mode: 'redirectTo' })
    }).catch(() => {
        repeatLoading.value = false
    })
}
</script>

<style lang="scss" scoped>
:deep(.apply-price) {
    color: var(--text-color-light9);
    font-size: 28rpx;
    font-weight: normal;
    line-height: 76rpx;
}
.footer {
    height: calc(100rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(100rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>