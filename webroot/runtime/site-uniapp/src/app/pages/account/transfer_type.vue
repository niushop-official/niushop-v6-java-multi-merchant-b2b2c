<template>
    <view class="transfer_type min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()" v-if="!loading">
        <view class="sidebar-margin mt-[20rpx]">
            <view  v-for="account in settAccountList" :key="account.id" class="card-template mb-[20rpx] shadow-[0_4rpx_20rpx_rgba(0,0,0,0.05)]" >
                <view class="flex justify-between">
                    <view class="flex items-center">
                        <image  v-if="account.type == 1" class="h-[48rpx] w-[48rpx] mr-[20rpx]" :src="img('static/resource/images/member/apply_withdrawal/bank-icon.png')"  mode="aspectFit" />
                        <image   v-if="account.type == 2"  class="h-[48rpx] w-[48rpx] mr-[20rpx]" :src="img('static/resource/images/member/apply_withdrawal/alipay-icon.png')"  mode="widthFix" />
                        <image   v-if="account.type == 3"  class="h-[48rpx] w-[48rpx] mr-[20rpx]" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')"  mode="widthFix" />
                        <image  v-if="account.type == 4"  class="h-[48rpx] w-[48rpx] mr-[20rpx]" :src="img('static/resource/images/member/apply_withdrawal/wechat.png')"  mode="widthFix"  />
                        <view>
                            <text class="text-[30rpx] font-500 text-[var(--text-color)]">{{ account.type_name }}</text>
                            <text class="text-[30rpx] font-500 text-[var(--text-color)]" v-if="account.account_no">({{ String(account.account_no).slice(-4) }})</text>
                        </view>
                    </view>
                    <view class="flex items-center  gap-[24rpx]">
                        <text class="nc-iconfont nc-icon-shanchu-yuangaiV6xx lex-shrink-0 text-[32rpx]" @click.stop="deleteAccount(account)"></text>
                        <text class="nc-iconfont nc-icon-xiugaiV6xx flex-shrink-0 text-[32rpx]" @click.stop="editAccount(account)"></text>
                    </view>
                </view>
                
            </view>
        </view>
        <view class="empty-page" v-if="settAccountList.length === 0 && !loading">
            <image class="img" :src="img('static/resource/images/system/empty.png')" mode="aspectFill" />
            <view class="desc">暂无提现账户</view>
        </view>
        <view class="footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] w-full fixed bottom-0 left-0 right-0 box-border">
                <button hover-class="none" class="h-[80rpx] !text-[#fff] leading-[80rpx] bg-primary  text-[26rpx]" @click="redirect({url: '/app/pages/account/edit_transfer_type'})">添加账户</button>
            </view>
        </view>


        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { redirect, img } from '@/utils/common'
import { getShopSettAccountList,  delShopSettAccount } from '@/app/api/account'

const loading = ref(true)
const settAccountList = ref<any[]>([])
const operateLoading = ref(false)




const loadAccounts = () => {
    loading.value = true
    getShopSettAccountList().then((res: any) => {
        settAccountList.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

loadAccounts()


const getTypeName = (type: number) => {
    const types: Record<number, string> = {
        1: '银行卡',
        2: '支付宝',
        3: '微信',
        4: '微信零钱'
    }
    return types[type] || ''
}


const editAccount = (account: any) => {
    redirect({url: '/app/pages/account/edit_transfer_type',param: {id: account.id }})
}

const deleteAccount = (account: any) => {
    uni.showModal({
        title: '确认删除',
        content: `确定要删除${getTypeName(account.type)}账户吗？`,
        success: (res) => {
            if (res.confirm) {
                operateLoading.value = true
                delShopSettAccount(account.id).then(() => {
                    operateLoading.value = false
                    loadAccounts()
                }).catch(() => {
                    operateLoading.value = false
                })
            }
        }
    })
}


</script>

<style lang="scss" scoped>
.footer {
    height: calc(100rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(100rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>