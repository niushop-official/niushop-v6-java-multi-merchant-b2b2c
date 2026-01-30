<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()">
        <view class="sidebar-margin card-template mt-[var(--sidebar-m)]" v-if="cashOutInfo">
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">账单编号</view>
                <view class="text-[28rpx]">{{ cashOutInfo.cash_out_no }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">店铺名称</view>
                <view class="text-[28rpx]">{{ cashOutInfo.siteInfo.site_name }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">联系电话</view>
                <view class="text-[28rpx]">{{ cashOutInfo.siteInfo.phone }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">提现方式</view>
                <view class="text-[28rpx]">{{ cashOutInfo.transfer_type_name }}</view>
            </view>
            <template  v-if="cashOutInfo.transfer_type == '1'">
                <view class="justify-between card-template-item">
                    <view class="text-[28rpx]">开户人姓名</view>
                    <view class="text-[28rpx]">{{ cashOutInfo.transfer_realname }}</view>
                </view>
                <view class="justify-between card-template-item">
                    <view class="text-[28rpx]">银行名称</view>
                    <view class="text-[28rpx]">{{ cashOutInfo.transfer_bank }}</view>
                </view>
                <view class="justify-between card-template-item">
                    <view class="text-[28rpx]">银行卡号</view>
                    <view class="text-[28rpx]">{{ cashOutInfo.transfer_account }}</view>
                </view>
            </template>
            <template  v-if="cashOutInfo.transfer_type == '2'">
                <view class="justify-between card-template-item">
                    <view class="text-[28rpx]">支付宝姓名</view>
                    <view class="text-[28rpx]">{{ cashOutInfo.transfer_realname }}</view>
                </view>
                <view class="justify-between card-template-item">
                    <view class="text-[28rpx]">支付宝账号</view>
                    <view class="text-[28rpx]">{{ cashOutInfo.transfer_account }}</view>
                </view>
            </template>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">提现金额</view>
                <view class="text-[28rpx]">{{ cashOutInfo.apply_money }}元</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">提现状态</view>
                <view class="text-[28rpx]">{{ cashOutInfo.status_name }}</view>
            </view>
            <view class="justify-between card-template-item" v-if="cashOutInfo.status == 3">
                <view class="text-[28rpx]">转账凭证</view>
                <view class="flex items-center" @click="previewImg()">
                    <image :src="img(cashOutInfo.transfer_voucher)" class="w-[140rpx] h-[140rpx] rounded-[var(--rounded-small)]" />
                </view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">申请时间</view>
                <view class="text-[28rpx]">{{ cashOutInfo.create_time }}</view>
            </view>
            <view class="justify-between card-template-item" v-if="cashOutInfo.refuse_reason">
                <view class="text-[28rpx]">拒绝原因</view>
                <view class="text-[28rpx]">{{ cashOutInfo.refuse_reason }}</view>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { img } from '@/utils/common'
import { onLoad, onUnload } from '@dcloudio/uni-app'
import { getShopCashOutInfo } from '@/app/api/account'


const relatedId = ref('')
const cashOutInfo = ref<any>(null)
const loading = ref(true)
onLoad((option: any) => {
    relatedId.value = option.related_id || ''
    getShopCashOutInfoFn(relatedId.value)
})

const  getShopCashOutInfoFn = (id: any) => {
    loading.value = true
    getShopCashOutInfo(id).then((res: any) => {
        cashOutInfo.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}


const previewImg = () => {
    uni.previewImage({
        urls: [img(cashOutInfo.value.transfer_voucher)],
        indicator: "number",
        loop: true
    })
}

// 关闭预览图片
onUnload(() => {
    // #ifdef  H5 || APP
    try {
        uni.closePreviewImage()
    } catch (e) {

    }
    // #endif
})

</script>

<style scoped>

</style>