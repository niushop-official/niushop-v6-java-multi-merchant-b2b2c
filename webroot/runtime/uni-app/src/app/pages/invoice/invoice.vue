<template>
    <view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden" :style="themeColor()">
        <view class="m-[var(--top-m)] sidebar-margin px-[var(--pad-sidebar-m)] py-[var(--pad-top-m)] rounded-[var(--rounded-big)] bg-white">
            <u-form labelPosition="left" :model="formData" labelWidth="200rpx"
                    :labelStyle="{'font-size': '28rpx'}" errorType='toast' :rules="rules" ref="formRef">
                <view>
                    <view class="mt-[10rpx]">
                        <u-form-item label="发票类型">
                            <view class="flex" v-if="formData.header_type == 1">
                                <view class="box-border leading-[50rpx] text-[28rpx]">增值税电子普通发票</view>
                            </view>
                            <view class=" flex-1 flex justify-between items-center" v-if="formData.header_type == 2" @click="invoiceTypePopup = true">
                                <view class="box-border leading-[50rpx] text-[28rpx]">{{  formData.type == 1 ? '增值税电子普通发票' : '增值税专用发票'}}</view>
                                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light6)]"></text>
                            </view>
                        </u-form-item>
                    </view>
                    <view class="mt-[10rpx]">
                        <u-form-item label="抬头类型">
                            <view class="flex">
                                <view class="h-[60rpx] box-border rounded px-[30rpx] mr-[20rpx] leading-[56rpx] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)]"
                                    :class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': formData.header_type == 1}"
                                    @click="formData.header_type = 1,formData.type = 1,invoiceType = 1">个人
                                </view>
                                <view class="h-[60rpx] box-border rounded px-[30rpx] leading-[56rpx] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)]"
                                    :class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': formData.header_type == 2}"
                                    @click="formData.header_type = 2">企业
                                </view>
                            </view>
                        </u-form-item>
                    </view>
                    <view class="mt-[10rpx]">
                        <u-form-item label="发票内容" prop="header_name">
                            <view class="flex flex-wrap">
                                <template v-for="(item,index) in config.invoice_content">
                                    <view class="box-border rounded px-[20rpx] py-[12rpx] leading-[1.4] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)] my-[10rpx]"
                                        :class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': formData.content_type == item.key, 'mr-[20rpx]': (config.invoice_content.length - 1) != index}"
                                        @click="formData.content_type = item.key">{{ item.name }}</view>
                                </template>
                            </view>
                        </u-form-item>
                    </view>
                    <view class="mt-[10rpx]">
                        <u-form-item label="邮箱" prop="email">
                            <u-input fontSize="28rpx" v-model.trim="formData.email" border="none" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" clearable placeholder="请输入邮箱号" ></u-input>
                        </u-form-item>
                    </view>
                    <view class="mt-[10rpx]">
                        <u-form-item label="发票抬头" prop="header_name">
                            <u-input fontSize="28rpx" v-model.trim="formData.header_name" border="none" maxlength="50" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" clearable placeholder="请输入发票抬头" />
                        </u-form-item>
                    </view>
                    <view v-if="formData.header_type == 2">
                        <view class="mt-[10rpx]">
                            <u-form-item label="纳税人识别号" prop="tax_number">
                                <u-input fontSize="28rpx" v-model.trim="formData.tax_number" border="none" clearable placeholder="请输入纳税人识别号" maxlength="20"  placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" @change="inputChange" />
                            </u-form-item>
                        </view>
                        <view class="mt-[10rpx]">
                            <u-form-item label="注册地址">
                                <u-input fontSize="28rpx" v-model="formData.address" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable maxlength="120" placeholder="(选填)请输入企业注册地址" />
                            </u-form-item>
                        </view>
                        <view class="mt-[10rpx]">
                            <u-form-item label="注册电话">
                                <u-input fontSize="28rpx" v-model="formData.telephone" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable maxlength="12" placeholder="(选填)请输入企业注册电话" />
                            </u-form-item>
                        </view>
                        <view class="mt-[10rpx]">
                            <u-form-item label="开户银行">
                                <u-input fontSize="28rpx" v-model="formData.bank_name" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable maxlength="50" placeholder="(选填)请输入企业开户银行" />
                            </u-form-item>
                        </view>
                        <view class="mt-[10rpx]">
                            <u-form-item label="银行账号">
                                <u-input fontSize="28rpx" v-model="formData.bank_card_number" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable maxlength="25"  placeholder="(选填)请输入企业开户银行账号" />
                            </u-form-item>
                        </view>
                    </view>
                </view>
            </u-form>
        </view>
        <view class="w-full">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] box-border">
                <button class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx] rounded-[100rpx] text-[26rpx] font-500" @click="confirm">提交</button>
            </view>
        </view>
        <u-popup :show="invoiceTypePopup">
            <view class="px-[30rpx] pb-[30rpx] bg-[#f5f5f5]" @touchmove.prevent.stop>
                <view class="flex items-center h-[90rpx] justify-between">
                    <text>发票类型选择</text>
                    <text class="iconfont iconguanbi" @click="invoiceTypePopup = false"></text>
                </view>
                <view class="h-[450rpx] mt-[20rpx]">
                    <u-radio-group v-model="invoiceType" placement="column">
                        <view class="flex items-center bg-[#fff] rounded-[16rpx] px-[30rpx] py-[34rpx] mb-[16rpx]">
                            <u-radio activeColor="var(--primary-color)" :name="1"></u-radio>
                            <view class="ml-[20rpx]">
                                <view class="text-[28rpx]">增值税电子普通发票</view>
                                <view class="text-[22rpx] mt-[10rpx] text-[#909090]">默认发送至所提供的电子邮件</view>
                            </view>
                        </view>
                        <view class="flex items-center bg-[#fff] rounded-[16rpx] px-[30rpx] py-[34rpx] mb-[16rpx]">
                            <u-radio activeColor="var(--primary-color)"  :name="2"></u-radio>
                            <view class="ml-[20rpx]">
                                <view class="text-[28rpx]">增值税专用发票</view>
                                <view class="text-[22rpx] mt-[10rpx] text-[#909090]">纸质发票开出后将以邮寄形式交付</view>
                            </view>
                        </view>
                    </u-radio-group>
                </view>
                <u-button type="primary" class="mt-[40rpx]" shape="circle" @click="invoiceTypePopupFn">确定</u-button>
            </view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { ref, computed, nextTick } from 'vue'
import { getInvoiceConfig } from '@/app/api/invoice'
import { addInvoice } from '@/app/api/invoice'
import { uploadImage } from '@/app/api/system'
import {  img,redirect ,deepClone } from '@/utils/common'
import { onLoad } from '@dcloudio/uni-app'

const orderId = ref('')
const tradeType = ref('')
onLoad((option: any) => {
    orderId.value = option.order_id || '';
    tradeType.value = option.trade_type || '';
})

const config = ref<any>({
    invoice_content: []
})

const formData: any = ref({
    header_type: 1,
    header_name: '',
    type: 1,
    content_type: '',
    email: '',
    tax_number: '',
    telephone: '',
    address: '',
    bank_name: '',
    bank_card_number: ''
})

getInvoiceConfig().then((res: any) => {
    config.value.invoice_content = []
    if(Object.keys(res.data).length){
        for(const key in res.data){
            let temp = {
                key: key,
                name: res.data[key]
            }
            config.value.invoice_content.push(temp)
        }
        formData.value.content_type = config.value.invoice_content[0].key
    }
    
}).catch()

const inputChange = (e: any) => {
    nextTick(() => {
        formData.value.tax_number = e.replace(/[^0-9a-zA-Z]/g, '')
    })
}

const formRef: any = ref(null)


const rules = computed(() => {
    return {
        'email': [{
			type: 'string',
			required: true,
			message: '请输入邮箱号',
			trigger: ['blur', 'change'],
		},
        {
            validator(rule:any, value:any, callback:any) {
                const limit = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
                if (!limit.test(value)) {
                    callback(new Error('请输入正确的邮箱号'))
                } else {
                    callback()
                }
            }
        }],
        'header_name': {
            type: 'string',
            required: true,
            message: '请输入发票抬头',
            trigger: ['blur', 'change'],
        },
        'tax_number': [{
            type: 'string',
            required: formData.value.header_type == 2,
            message: '请输入纳税人识别号',
            trigger: ['blur', 'change'],
        }, {
            validator(rule: any, value: any, callback: any) {
                const limit = /^[0-9a-zA-Z]+$/;
                if (!limit.test(value) && formData.header_type == 2) {
                    callback(new Error('请输入正确的纳税人识别号'))
                } else {
                    callback()
                }
            }
        }]
    }
})

const confirm = () => {
    formRef.value.validate().then(() => {
        let submitData = deepClone(formData.value)
        submitData.trade_id = orderId.value
        submitData.trade_type = tradeType.value
        addInvoice(submitData).then((res)=>{
            navigateBack()
        }).catch((err)=>{
        })
    })
}

// 发票类型
const invoiceTypePopup = ref(false)
const invoiceType = ref(1) 
const invoiceTypePopupFn = () =>{
	formData.value.type = invoiceType.value
	invoiceTypePopup.value = false
}

const navigateBack = () => {
    if (getCurrentPages().length > 1) {
        uni.navigateBack({
            delta: 1
        });
    } else {
        redirect({
            url: '/app/pages/index/index',
            mode: 'reLaunch'
        });
    }
}
</script>

<style lang="scss" scoped>
:deep(.u-form-item__body__left){
    align-items:flex-start !important;
}
:deep(.u-input--square){
    margin-top: 5rpx !important;
}
</style>
