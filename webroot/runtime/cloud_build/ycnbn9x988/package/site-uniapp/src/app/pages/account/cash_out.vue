<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()">
        <view class="mx-[24rpx] mt-[30rpx] bg-white rounded-[16rpx] shadow-[0_4rpx_16rpx_rgba(0,0,0,0.04)]">
            <view class="px-[30rpx] py-[28rpx] flex items-center justify-between">
                <text class="text-[30rpx] font-500 text-[var(--text-color)]">到账方式</text>
                <view class="flex items-center" @click="showAccountSelector = true">
                    <view v-if="selectedAccount" class="flex items-center mr-[16rpx]">
                        <image class="h-[40rpx] w-[40rpx] mr-[10rpx]" :src="getAccountIcon(selectedAccount.type)" mode="widthFix" />
                        <text class="text-[30rpx] text-[var(--text-color)]">{{ selectedAccount.type_name }}</text>
                    </view>
                    <view v-else class="text-[30rpx] text-[var(--text-color-light9)] mr-[16rpx]">请选择</view>
                    <view class="nc-iconfont nc-icon-youV6xx text-[28rpx] text-[var(--text-color-light9)]"></view>
                </view>
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
                <button hover-class="none" class="h-[80rpx] !text-[#fff] leading-[80rpx] bg-primary  text-[26rpx]" :class="{'opacity-50': !canCashOut}" :loading="repeatLoading" @click="cashOut">立即提现</button>
            </view>
        </view>
        <u-popup :show="showAccountSelector" @close="showAccountSelector = false">
            <view class="popup-common" @touchmove.prevent.stop>
                <view class="title">选择到账方式</view>
                <scroll-view scroll-y class="h-[400rpx] px-[30rpx] box-border">
                    <view v-if="accountList.length === 0" class="flex flex-col items-center justify-center py-[80rpx]">
                        <image :src="img('static/resource/images/empty.png')" class="w-[160rpx] h-[160rpx] mb-[24rpx]" mode="widthFix" />
                        <text class="text-[28rpx] text-[var(--text-color-light9)] mb-[24rpx]">暂无提现账户</text>
                    </view>
                    <view v-else class="">
                        <view  v-for="account in accountList" :key="account.id" class="flex items-center justify-between mb-[40rpx]" @click="selectAccount(account)">
                            <view class="flex items-center">
                                <image class="h-[48rpx] w-[48rpx] mr-[20rpx]" :src="getAccountIcon(account.type)" mode="aspectFit" />
                                <view class="text-[30rpx]">
                                    <text>{{ account.type_name }}</text>
                                    <text v-if="account.account_no">({{ String(account.account_no).slice(-4) }})</text>
                                </view>
                            </view>
                            <text class="self-center iconfont text-color text-[34rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': selectedAccount?.id == account.id,'bg-[#fff] border-solid border-[2rpx] border-[#ccc]':selectedAccount?.id != account.id}"></text>
                        </view>
                    </view>
                </scroll-view>
                 <view class="btn-wrap flex">
                    <button hover-class="none" class="flex-1 btn !text-[#333] mr-[20rpx]" @click="goToAddAccount">去添加</button>
                    <button hover-class="none" class="flex-1 primary-btn-bg btn" @click="confirmAccount">确认选择</button>
                </view>
            </view>
        </u-popup>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { img, moneyFormat, redirect } from '@/utils/common'
import { getCashApply, getShopAccountInfo, getShopSettAccountList } from '@/app/api/account'
import { onShow } from '@dcloudio/uni-app'

const initialFormData = {
    apply_money: '',
    bank_account_no: '',
    money: '',
    bank_type: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })
const loading = ref(true)
const repeatLoading = ref(false)
const showAccountSelector = ref(false)
const accountList = ref<any[]>([])
const selectedAccount = ref<any>(null)

const canCashOut = computed(() => {
    return selectedAccount.value && formData.apply_money && parseFloat(formData.apply_money) > 0
})

const getAccountIcon = (type: number) => {
    const icons: Record<number, string> = {
        1: img('static/resource/images/member/apply_withdrawal/bank-icon.png'),
        2: img('static/resource/images/member/apply_withdrawal/alipay-icon.png'),
        3: img('static/resource/images/member/apply_withdrawal/wechat_code.png'),
        4: img('static/resource/images/member/apply_withdrawal/wechat.png')
    }
    return icons[type] || icons[1]
}

const getShopAccountInfoFn = () => {
    loading.value = true
    Promise.all([getShopAccountInfo(), getShopSettAccountList()]).then(([accountInfoRes, accountListRes]) => {
        Object.assign(formData, accountInfoRes.data)
        accountList.value = accountListRes.data
        if (accountList.value.length > 0) {
            const defaultAccount = accountList.value.find(a => a.type == accountInfoRes.data.bank_type)
            selectedAccount.value = defaultAccount || accountList.value[0]
        }
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

onShow(() => {
    getShopAccountInfoFn()
})

const clearMoney = () => {
    formData.apply_money = '';
}

const allCashOut = () => {
    formData.apply_money = formData.money;
}

const goToAddAccount = () => {
    showAccountSelector.value = false
    redirect({ url: '/app/pages/account/transfer_type' })
}

const selectAccount = (account: any) => {
    selectedAccount.value = account
}

const confirmAccount = () => {
    if (selectedAccount.value) {
        showAccountSelector.value = false
    }
}

const cashOut = () => {
    if (!selectedAccount.value) {
        uni.showToast({
            title: '请选择到账方式',
            icon: 'none'
        })
        return false
    }
    if (formData.apply_money == '') {
        uni.showToast({
            title: '请输入提现金额',
            icon: 'none'
        })
        return false
    }
    if (parseFloat(formData.apply_money) < 0) {
        uni.showToast({
            title: '提现金额不能小于0',
            icon: 'none'
        })
        return false
    }
    if(repeatLoading.value) return false
    repeatLoading.value = true
    getCashApply({
        account_id: selectedAccount.value.id,
        apply_money: formData.apply_money,
        money: formData.money
    }).then((res: any) => {
        repeatLoading.value = false
        redirect({ url: '/app/pages/account/cash_out_result', param: { id: res.data.id }, mode: 'redirectTo' })
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