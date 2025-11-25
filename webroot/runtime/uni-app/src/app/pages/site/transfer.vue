<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()">
        <view class="sidebar-margin card-template mt-[20rpx] !pt-[60rpx] !pb-[40rpx]">
            <view class="flex items-center flex-col mb-[80rpx]">
                <text class="text-[60rpx] font-bold price-font mb-[16rpx]">{{ cashOutInfo.apply_money }}</text>
                <text class="text-[28rpx] text-[#333]" :class="{'text-primary': cashOutInfo.status == 1, 'text-[#999]': cashOutInfo.status == 4 }">{{ cashOutInfo.status_name }}</text>
            </view>
            <view>
                <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]">
                    <text class="text-[#333] w-[200rpx]">{{ t('cashOutNo') }}</text>
                    <text class="text-[#333]">{{ cashOutInfo.cash_out_no }}</text>
                </view>
                <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]"
                        v-if="Number(cashOutInfo.service_money)">
                    <text class="text-[#333] w-[200rpx]">{{ t('serviceMoney') }}</text>
                    <text class="text-[#333]">￥{{ cashOutInfo.service_money }}</text>
                </view>
                <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]">
                    <text class="text-[#333] w-[200rpx]">{{ t('createTime') }}</text>
                    <text class="text-[#333]">{{ cashOutInfo.create_time }}</text>
                </view>
                <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]" v-if="cashOutInfo.status && cashOutInfo.audit_time">
                    <text class="text-[#333] w-[200rpx]">{{ t('auditTime') }}</text>
                    <text class="text-[#333]">{{ cashOutInfo.audit_time }}</text>
                </view>
                <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]" v-if="cashOutInfo.status == 3">
                    <text class="text-[#333] w-[200rpx]">{{ t('transferTypeName') }}</text>
                    <text class="text-[#333] truncate">{{ cashOutInfo.transfer_type_name }}</text>
                </view>
            </view>
        </view>
        <view class="sidebar-margin card-template mt-[20rpx]">
            <view class="title">{{ t('proceedsInfo') }}</view>
            <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]" v-if="cashOutInfo.transfer_type == '4'">
                <text class="text-[#333] w-[200rpx]">{{ t('transferAccount') }}</text>
                <text class="text-[#333] truncate">微信零钱</text>
            </view>
            <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]">
                <text class="text-[#333] w-[200rpx]">{{ t('transferNickname') }}</text>
                <text class="text-[#333]">{{ cashOutInfo.siteInfo && cashOutInfo.siteInfo.site_name }}</text>
            </view>
        </view>
        <view class="sidebar-margin card-template my-[20rpx]" v-if="cashOutInfo.status == 3">
            <view class="title">{{ t('transferInfo') }}</view>
            <view class="flex justify-between text-[28rpx] leading-[32rpx]" v-if="cashOutInfo.transfer_time">
                <text class="text-[#333] w-[200rpx]">{{ t('transferTime') }}</text>
                <text class="text-[#333] truncate">{{ cashOutInfo.transfer_time }}</text>
            </view>
            <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]" v-if="cashOutInfo.transfer_no">
                <text class="text-[#333] w-[200rpx]">{{ t('transferNo') }}</text>
                <text class="text-[#333] truncate">{{ cashOutInfo.transfer_no }}</text>
            </view>
            <view class="flex justify-between text-[28rpx] mt-[34rpx] leading-[32rpx]" v-if="cashOutInfo.remark">
                <text class="text-[#333] w-[200rpx] flex-shrink-0">{{ t('transferRemark') }}</text>
                <text class="text-[#333] ml-[20rpx]">{{ cashOutInfo.remark }}</text>
            </view>
        </view>
        <view class="tab-bar-placeholder" v-if="cashOutInfo.status == 2 || cashOutInfo.status == 4"></view>
        <view class="fixed bottom-[0] tab-bar left-0 right-0 px-[var(--sidebar-m)] bg-[var(--page-bg-color)]">
            <button class="h-[80rpx] !text-[#fff] leading-[80rpx] primary-btn-bg rounded-[50rpx] text-[26rpx]" :loading="operateLoading" v-if="cashOutInfo.transfer_type == '4' && cashOutInfo.status == 2" @click="cashOut">提现</button>
            <button class="h-[80rpx] !text-[#fff] leading-[80rpx] primary-btn-bg rounded-[50rpx] text-[26rpx]"  :loading="operateLoading"  v-else-if="cashOutInfo.transfer_type == '4' && cashOutInfo.status == 4" @click="cashTransfer">提现</button>
        </view>
        <loading-page :loading="tansferLoading" bgColor="rgba(0,0,0,0.2)"></loading-page>
    </view>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/locale'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { redirect, goback, isWeixinBrowser, getToken, handleOnloadParams } from '@/utils/common';
import { getTransferDetail, getTransferShop, getTransferCheck } from '@/app/api/shop';
import wechat from '@/utils/wechat'

// #ifdef H5
if (isWeixinBrowser()) {
    wechat.init();
}
// #endif

const cashOutInfo = ref<any>({});
const loading = ref<boolean>(true);
const tansferLoading = ref<boolean>(false);
const operateLoading = ref(false)
let timer: any = null
let cod = ref('')
let openId = uni.getStorageSync('openid') || ''
onLoad((option: any) => {
    
    // #ifdef MP-WEIXIN
    // 处理小程序场景值参数
    option = handleOnloadParams(option);
    // #endif

    cod.value = option.cod || '';

})
onShow(()=>{
    if(cod.value){
        getTransferCheckFn()
    } else {
        if (getToken()) {
            let parameter = {
                url: '/app/pages/index/index',
                title: '提现详情不存在',
                mode: 'reLaunch'
            };
            goback(parameter);
        }
    }
})
const getTransferCheckFn = () => {
    getTransferCheck({ code: cod.value }).then((res: any) => {
        getTransferFn(res.data)
    }).catch(()=>{
        setTimeout(() => {
            redirect({ url: '/app/pages/index/index',  mode:'reLaunch'})
        }, 2000);
    })
}
const getTransferFn = (id: any) => {
    loading.value = true;
    getTransferDetail(id).then((res: any) => {
        cashOutInfo.value = res.data;
        loading.value = false;
        tansferLoading.value = false;
    }).catch(() => {
        loading.value = false;
        tansferLoading.value = false;
    })

}

const cashOut = () => {
    if(!getToken()){
        useLogin().setLoginBack({
			url: '/app/pages/site/transfer',
			param: { cod: cod.value}
		})
        return false
    }

    if (!openId) {
        uni.showToast({
            title: '请到微信公众号或小程序中进行提现',
            icon: 'none'
        })
        return false
    }

    if (operateLoading.value) return false
    operateLoading.value = true

    let obj = {
        id: cashOutInfo.value.id,
        open_id: openId
    }

    getTransferShop(obj).then((res: any) => {
        let obj = {
            mchId: res.data.extra.mch_id,
            appId: res.data.extra.appid,
            package: res.data.package_info
        }
        wechat.transfer(obj, (res: any) => {
            operateLoading.value = false;
            loading.value = true
            tansferLoading.value = true
            timer = setTimeout(() => {
                getTransferFn(cashOutInfo.value.id)
                clearTimeout(timer)
            }, 6000)
        })
    })

}

const cashTransfer = () => {
    if(!getToken()){
        useLogin().setLoginBack({
			url: '/app/pages/site/transfer',
			param: { cod: cod.value}
		})
        return false
    }
    if (!openId) {
        uni.showToast({
            title: '请到微信公众号或小程序中进行提现',
            icon: 'none'
        })
        return false
    }
    if (operateLoading.value) return false
    operateLoading.value = true
    let obj = {
        mchId: cashOutInfo.value.transfer.extra.mch_id,
        appId: cashOutInfo.value.transfer.extra.appid,
        package: cashOutInfo.value.transfer.package_info
    }
    wechat.transfer(obj, (res: any) => {
        operateLoading.value = false;
        timer = setTimeout(() => {
            getTransferFn(cashOutInfo.value.id)
            clearTimeout(timer)
        }, 6000)
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
</style>
