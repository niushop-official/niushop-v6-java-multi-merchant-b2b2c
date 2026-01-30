<template>
    <view class="transfer_type min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()" v-if="!loading">
        <view class="sidebar-margin card-template mt-[20rpx]">
            <u-form labelPosition="left" :model="formBrank" errorType='toast' :rules="formBrankRules" ref="formRef">
                <view>
                    <u-form-item label="提现账户类型" prop="bank_type" required="true" labelWidth="200rpx">
                        <view class="flex items-center flex-1" @click="showBankTypePopup = true">
                            <view class="flex-1 flex items-center justify-end" :class="{'text-[var(--text-color-light9)]': !formBrank.bank_type }">
                                <image v-if="formBrank.bank_type == 1" class="h-[28rpx] w-[30rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/bank-icon.png')" mode="heightFix" />
                                <image v-if="formBrank.bank_type == 2" class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/alipay-icon.png')" mode="widthFix" />
                                <image v-if="formBrank.bank_type == 3" class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')" mode="widthFix" />
                                <image v-if="formBrank.bank_type == 4" class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat.png')" mode="widthFix" />
                                <text class="text-[30rpx]">{{ formBrank.bank_type ? formBrank.bank_type_name : '请选择提现账户类型' }}</text>
                            </view>
                            <text class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                    </u-form-item>
                </view>
                <template v-if="formBrank.bank_type == 1">
                    <view>
                        <u-form-item label="提现账号对应姓名" prop="bank_account_name" required="true" labelWidth="250rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.bank_account_name" border="none"  maxlength="30" placeholder="请输入提现账号对应姓名" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="提现银行账号" prop="bank_account_no" required="true" labelWidth="240rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.bank_account_no" border="none"  maxlength="30" placeholder="请输入提现银行账号" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="银行名称" prop="bank_name" required="true" labelWidth="240rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.bank_name" border="none"  maxlength="80" placeholder="请输入银行名称" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="银行地址" prop="bank_address" required="true" labelWidth="240rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.bank_address" border="none"  maxlength="80" placeholder="请输入银行地址" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>   
                </template>
                <template v-if="formBrank.bank_type == 2">
                    <view>
                        <u-form-item label="支付宝账号对应姓名" prop="alipay_name" required="true" labelWidth="280rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.alipay_name" border="none"  maxlength="30" placeholder="请输入支付宝账号对应姓名" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="支付宝账号" prop="alipay_account_no" required="true" labelWidth="280rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.alipay_account_no" border="none"  maxlength="30" placeholder="请输入支付宝账号" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="支付宝收款码" prop="alipay_payment_code" required="true" labelWidth="280rpx">
                            <view class="flex-1 flex justify-end">
                                <upload-img  v-model="formBrank.alipay_payment_code" :max-count="1" />
                            </view>
                        </u-form-item>
                    </view>
                </template>
                <template v-if="formBrank.bank_type == 3">
                    <view>
                        <u-form-item label="真实姓名" prop="wechat_name" required="true" labelWidth="200rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.wechat_name" border="none"  maxlength="30" placeholder="请输入真实姓名" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="微信账号" prop="wechat_account_no" required="true" labelWidth="200rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.wechat_account_no" border="none"  maxlength="30" placeholder="请输入微信账号" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="微信收款码" prop="wechat_payment_code" required="true" labelWidth="200rpx">
                            <view class="flex-1 flex justify-end">
                                <upload-img  v-model="formBrank.wechat_payment_code" :max-count="1" />
                            </view>
                        </u-form-item>
                    </view>
                </template>
            </u-form>
        </view>
        <view v-if="formBrank.bank_type == 4" class="sidebar-margin mt-[30rpx] text-[var(--text-color-light9)] text-[24rpx]">
            说明：通过客户端授权走商家转账提现
        </view>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] w-full fixed bottom-0 left-0 right-0 box-border">
                <button hover-class="none" class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx]  text-[26rpx] font-500"  @click="save"   :loading="operateLoading" >确定</button>
            </view>
        </view>
        <!-- 提现账户类型 -->
        <u-popup :show="showBankTypePopup" @close="showBankTypePopup = false">
            <view class="popup-common" @touchmove.prevent.stop>
                <view class="title">提现账户类型</view>
                <scroll-view scroll-y="true" class="h-[400rpx] px-[30rpx] box-border">
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center">
                            <image class="h-[28rpx] w-[30rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/bank-icon.png')" mode="heightFix" />
                            <text class="text-[30rpx] ml-[20rpx]">银行卡</text>
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.bank_type == 1 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.bank_type != 1}" @click="handleBankType(1)"></text>
                    </view>
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center leading-[50rpx]">
                            <image class="h-[34rpx] w-[34rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/alipay-icon.png')" mode="widthFix" />
                            <text class="text-[30rpx] ml-[20rpx]">支付宝</text>
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.bank_type == 2 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.bank_type != 2}" @click="handleBankType(2)"></text>
                    </view>
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center leading-[50rpx]">
                            <image class="h-[34rpx] w-[34rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')" mode="widthFix" />
                            <text class="text-[30rpx] ml-[20rpx]">微信</text>                            
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.bank_type == 3 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.bank_type != 3}" @click="handleBankType(3)"></text>
                    </view>
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center leading-[50rpx]">
                            <image class="h-[34rpx] w-[34rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat.png')" mode="widthFix" />
                            <text class="text-[30rpx] ml-[20rpx]">微信零钱</text>
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.bank_type == 4 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.bank_type != 4}" @click="handleBankType(4)"></text>
                    </view>
                </scroll-view>
                <view class="btn-wrap">
                    <button class="primary-btn-bg btn" @click="changeBankType">确定</button>
                </view>
            </view>
        </u-popup>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { redirect, img } from '@/utils/common'
import { getShopAccountInfo, setShopAccount } from '@/app/api/account'

// 结算账户表单数据
const formBrank = reactive<any>({
    bank_type: 1,
    bank_type_name: '银行卡',
    bank_account_name: '',
    bank_account_no: '',
    bank_name: '',
    bank_address: '',
    alipay_name: '',
    alipay_account_no: '',
    alipay_payment_code: '',
    wechat_name: '',
    wechat_account_no: '',
    wechat_payment_code: ''
})
const loading = ref(true)
const shopAccount = ref<any>('') // 店铺账户信息
const formRef = ref()
const getShopAccountInfoFn = () => {
    loading.value = true
    getShopAccountInfo().then((res: any) => {
        shopAccount.value = res.data
        Object.keys(formBrank).forEach((key: string) => {
            if (shopAccount.value[key] != undefined) formBrank[key] = shopAccount.value[key]
        })
        if (formBrank.bank_type == 1) {
            formBrank.bank_type_name = '银行卡'
        } else if (formBrank.bank_type == 2) {
            formBrank.bank_type_name = '支付宝'
        } else if (formBrank.bank_type == 3) {
            formBrank.bank_type_name = '微信'
        } else if (formBrank.bank_type == 4) {
            formBrank.bank_type_name = '微信零钱'
        }
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getShopAccountInfoFn()


// 表单验证规则
const formBrankRules = computed(() => {
    return {
        bank_account_name: [
            { required: true, message: '请输入提现账号对应姓名', trigger: 'blur' }
        ],
        bank_account_no: [
            { required: true, message: '请输入提现银行账号', trigger: 'blur' }
        ],
        bank_name: [
            { required: true, message: '请输入银行名称', trigger: 'blur' }
        ],
        bank_address: [
            { required: true, message: '请输入银行地址', trigger: 'blur' }
        ],
        alipay_name: [
            { required: true, message: '请输入支付宝账号对应姓名', trigger: 'blur' }
        ],
        alipay_account_no: [
            { required: true, message: '请输入支付宝账号', trigger: 'blur' }
        ],
        alipay_payment_code: [
            { required: true, message: '请上传支付宝收款码', trigger: 'blur' }
        ],
        wechat_name: [
            { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        wechat_account_no: [
            { required: true, message: '请输入微信账号', trigger: 'blur' }
        ],
        wechat_payment_code: [
            { required: true, message: '请上传微信收款码', trigger: 'blur' }
        ]
    }
})

const operateLoading = ref(false)

const save = () => {
    formRef.value.validate().then(() => {
        if (operateLoading.value) return
        operateLoading.value = true

        setShopAccount(formBrank).then((res: any) => {
            operateLoading.value = false
            setTimeout(() => {
                if (getCurrentPages().length > 1) {
                    uni.navigateBack({
                        delta: 1
                    });
                } else {
                    redirect({url: '/app/pages/account/index'});
                }
            }, 1000)
        }).catch(() => {
            operateLoading.value = false
            
        })
    })
}

// 提现账户类型
const showBankTypePopup = ref(false)

const handleBankType = (value: number) => {
    formBrank.bank_type = value
    if(formBrank.bank_type == 1){
        formBrank.bank_type_name = '银行卡'
    }else if(formBrank.bank_type == 2){
        formBrank.bank_type_name = '支付宝'
    }else if(formBrank.bank_type == 3){
        formBrank.bank_type_name = '微信'
    }else if(formBrank.bank_type == 4){
        formBrank.bank_type_name = '微信零钱'
    }
}
const changeBankType = () => {
    showBankTypePopup.value = false
}
</script>

<style lang="scss" scoped>
.transfer_type :deep(.u-form-item__body__right) {
    display: flex;
}

.footer {
    height: calc(100rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(100rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>