<template>
    <view class="transfer_type min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()" v-if="!loading">
        <view class="sidebar-margin card-template mt-[20rpx]">
            <u-form labelPosition="left" :model="formBrank" errorType='toast' :rules="formBrankRules" ref="formRef">
                <view>
                    <u-form-item label="提现账户类型" prop="type" required="true" labelWidth="200rpx">
                        <view class="flex items-center flex-1" @click="showBankTypePopup = true">
                            <view class="flex-1 flex items-center justify-end" :class="{'text-[var(--text-color-light9)]': !formBrank.type }">
                                <image v-if="formBrank.type == 1" class="h-[28rpx] w-[30rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/bank-icon.png')" mode="heightFix" />
                                <image v-if="formBrank.type == 2" class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/alipay-icon.png')" mode="widthFix" />
                                <image v-if="formBrank.type == 3 || formBrank.type == 4" class="h-[34rpx] w-[34rpx] mr-[10rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')" mode="widthFix" />
                                <text class="text-[30rpx]">{{ formBrank.type ? formBrank.type_name : '请选择提现账户类型' }}</text>
                            </view>
                            <text class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                    </u-form-item>
                </view>
                <template v-if="formBrank.type == 1">
                    <view>
                        <u-form-item label="提现账号对应姓名" prop="name" required="true" labelWidth="250rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.name" border="none"  maxlength="30" placeholder="请输入提现账号对应姓名" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="提现银行账号" prop="account_no" required="true" labelWidth="240rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.account_no" border="none"  maxlength="30" placeholder="请输入提现银行账号" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
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
                <template v-if="formBrank.type == 2">
                    <view>
                        <u-form-item label="支付宝账号对应姓名" prop="name" required="true" labelWidth="280rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.name" border="none"  maxlength="30" placeholder="请输入支付宝账号对应姓名" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="支付宝账号" prop="account_no" required="true" labelWidth="280rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.account_no" border="none"  maxlength="30" placeholder="请输入支付宝账号" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="支付宝收款码" prop="account_img" required="true" labelWidth="280rpx">
                            <view class="flex-1 flex justify-end">
                                <upload-img  v-model="formBrank.account_img" :max-count="1" />
                            </view>
                        </u-form-item>
                    </view>
                </template>
                <template v-if="formBrank.type == 3">
                    <view>
                        <u-form-item label="真实姓名" prop="name" required="true" labelWidth="200rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.name" border="none"  maxlength="30" placeholder="请输入真实姓名" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="微信账号" prop="account_no" required="true" labelWidth="200rpx">
                            <u-input fontSize="30rpx" v-model.trim="formBrank.account_no" border="none"  maxlength="30" placeholder="请输入微信账号" placeholderClass="!text-[var(--text-color-light9)] text-[30rpx]" inputAlign="right" />
                        </u-form-item>
                    </view>
                    <view>
                        <u-form-item label="微信收款码" prop="account_img" required="true" labelWidth="200rpx">
                            <view class="flex-1 flex justify-end">
                                <upload-img  v-model="formBrank.account_img" :max-count="1" />
                            </view>
                        </u-form-item>
                    </view>
                </template>
            </u-form>
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
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.type == 1 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.type != 1}" @click="handleBankType(1)"></text>
                    </view>
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center leading-[50rpx]">
                            <image class="h-[34rpx] w-[34rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/alipay-icon.png')" mode="widthFix" />
                            <text class="text-[30rpx] ml-[20rpx]">支付宝</text>
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.type == 2 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.type != 2}" @click="handleBankType(2)"></text>
                    </view>
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center leading-[50rpx]">
                            <image class="h-[34rpx] w-[34rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')" mode="widthFix" />
                            <text class="text-[30rpx] ml-[20rpx]">微信</text>                            
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.type == 3 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.type != 3}" @click="handleBankType(3)"></text>
                    </view>
                    <view class="mb-[50rpx] flex items-center justify-between">
                        <view class="flex items-center leading-[50rpx]">
                            <image class="h-[34rpx] w-[34rpx] align-middle" :src="img('static/resource/images/member/apply_withdrawal/wechat_code.png')" mode="widthFix" />
                            <text class="text-[30rpx] ml-[20rpx]">微信零钱</text>
                        </view>
                        <text  class=" iconfont text-color text-[36rpx] w-[36rpx] h-[36rpx] rounded-[18rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formBrank.type == 4 ,'border-[#ddd] border-solid border-[2rpx]':formBrank.type != 4}" @click="handleBankType(4)"></text>
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
import { addShopSettAccount, editShopSettAccount, getShopSettAccountInfo} from '@/app/api/account'
import { onLoad } from '@dcloudio/uni-app'

// 结算账户表单数据
const formBrank = reactive<any>({
    type: 1,
    type_name: '银行卡',
    bank_name: '',
    bank_address: '',
    name: '',
    account_no: '',
    account_img: ''
})
const id = ref(0)
const loading = ref(true)
const shopAccount = ref<any>('') // 店铺账户信息
const formRef = ref()

onLoad((option: any) => { 
    id.value = option.id
    if (id.value) {
        getShopSettAccountInfoFn()
    } else {
        loading.value = false
    }   
})
const getShopSettAccountInfoFn = () => {
    loading.value = true
    getShopSettAccountInfo(id.value).then((res: any) => {
        shopAccount.value = res.data
        Object.keys(formBrank).forEach((key: string) => {
            if (shopAccount.value[key] != undefined) formBrank[key] = shopAccount.value[key]
        })
        if (formBrank.type == 1) {
            formBrank.type_name = '银行卡'
        } else if (formBrank.type == 2) {
            formBrank.type_name = '支付宝'
        } else if (formBrank.type == 3) {
            formBrank.type_name = '微信'
        } else if (formBrank.type == 4) {
            formBrank.type_name = '微信零钱'
        }
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}


// 表单验证规则
const formBrankRules = computed(() => {
    return {
        name: [
            { required: true, message: '请输入提现账号对应姓名', trigger: 'blur' }
        ],
        account_no: [
            { required: true, message: formBrank.type == 1 ? '请输入提现银行账号' : (formBrank.type == 2 ? '请输入支付宝账号' : '请输入微信账号'), trigger: 'blur' }
        ],
        bank_name: [
            { required: true, message: '请输入银行名称', trigger: 'blur' }
        ],
        bank_address: [
            { required: true, message: '请输入银行地址', trigger: 'blur' }
        ],
        account_img: [
            { required: (formBrank.type == 2 || formBrank.type == 3), message: formBrank.type == 2 ? '请上传支付宝收款码' : '请上传微信收款码', trigger: 'blur' }
        ]
    }
})

const operateLoading = ref(false)

const save = () => {
    formRef.value.validate().then(() => {
        if (operateLoading.value) return
        operateLoading.value = true
        let api = id.value ? editShopSettAccount : addShopSettAccount
        id.value ? formBrank.id = id.value : ''
        api(formBrank).then((res: any) => {
            operateLoading.value = false
            redirect({url: '/app/pages/account/transfer_type'});
        }).catch(() => {
            operateLoading.value = false
        })
    })
}

// 提现账户类型
const showBankTypePopup = ref(false)

const handleBankType = (value: number) => {
    formBrank.type = value
    if(formBrank.type == 1){
        formBrank.type_name = '银行卡'
    }else if(formBrank.type == 2){
        formBrank.type_name = '支付宝'
    }else if(formBrank.type == 3){
        formBrank.type_name = '微信'
    }else if(formBrank.type == 4){
        formBrank.type_name = '微信零钱'
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