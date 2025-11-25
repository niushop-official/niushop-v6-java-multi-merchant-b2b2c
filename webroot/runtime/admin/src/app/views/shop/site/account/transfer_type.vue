<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <!-- 收款方式 -->
            <el-form :model="formBrank" label-width="150px" ref="formBrankRef" :rules="formBrankRules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none relative" shadow="never">
                    <el-form-item :label="t('accountType')">
                        <el-radio-group v-model="formBrank.bank_type" class="ml-4">
                            <el-radio :label="1">银行卡</el-radio>
                            <el-radio :label="2">支付宝</el-radio>
                            <el-radio :label="3">微信</el-radio>
                            <el-radio :label="4">微信零钱</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <div v-if="formBrank.bank_type == 1">
                        <el-form-item :label="t('bankAccountName')" prop="bank_account_name">
                            <el-input v-model="formBrank.bank_account_name" clearable :placeholder="t('bankAccountNamePlaceholder')" class="input-width" maxlength="20" />
                        </el-form-item>
                        <el-form-item :label="t('companyBankAccountNumber')" prop="bank_account_no">
                            <el-input v-model="formBrank.bank_account_no" clearable :placeholder="t('companyBankPlaceholder')" class="input-width" @keyup="filterNumber($event)" maxlength="40" />
                        </el-form-item>
                        <el-form-item :label="t('bankBranchName')" prop="bank_name">
                            <el-input v-model="formBrank.bank_name" clearable :placeholder="t('bankBranchNamePlaceholder')" class="input-width" maxlength="40" />
                        </el-form-item>
                        <el-form-item :label="t('bankAddress')" prop="bank_address">
                            <el-input v-model="formBrank.bank_address" clearable :placeholder="t('bankAddressPlaceholder')" class="input-width" maxlength="60" />
                        </el-form-item>
                    </div>
                    <div v-else-if="formBrank.bank_type == 2">
                        <el-form-item :label="t('alipayAccountName')" prop="alipay_name">
                            <el-input v-model="formBrank.alipay_name" clearable :placeholder="t('alipayAccountNamePlaceholder')" class="input-width" maxlength="20" />
                        </el-form-item>
                        <el-form-item :label="t('alipayBankAccountNumber')" prop="alipay_account_no">
                            <el-input v-model="formBrank.alipay_account_no" clearable :placeholder="t('alipayBankPlaceholder')" class="input-width" maxlength="40" />
                        </el-form-item>
                        <el-form-item :label="t('alipayCode')" prop="alipay_payment_code">
                            <upload-image v-model="formBrank.alipay_payment_code" :limit="1" />
                        </el-form-item>
                    </div>
                    <div v-else-if="formBrank.bank_type == 3">
                        <el-form-item :label="t('wechatName')" prop="wechat_name">
                            <el-input v-model="formBrank.wechat_name" clearable :placeholder="t('wechatNamePlaceholder')" class="input-width" maxlength="20" />
                        </el-form-item>
                        <el-form-item :label="t('wechatAccountNo')" prop="wechat_account_no">
                            <el-input v-model="formBrank.wechat_account_no" clearable :placeholder="t('wechatAccountNoPlaceholder')" class="input-width" maxlength="40" />
                        </el-form-item>
                        <el-form-item :label="t('wechatCode')" prop="wechat_payment_code">
                            <upload-image v-model="formBrank.wechat_payment_code" :limit="1" />
                        </el-form-item>
                    </div>
                </el-card>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formBrankRef)">{{ t('confirm') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { getShopAccountInfo, setShopAccount } from '@/app/api/shop_site'
import { filterNumber } from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref(true)

/**
 * 表单数据
 */

// 结算账户表单数据
const formBrank = reactive<any>({
    bank_type: 1,
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

// 店铺账户信息
const shopAccount = ref<any>('')
const setFormData = async () => {
    shopAccount.value = await (await getShopAccountInfo()).data
    Object.keys(formBrank).forEach((key: string) => {
        if (shopAccount.value[key] != undefined) formBrank[key] = shopAccount.value[key]
    })
    loading.value = false
}

setFormData()

const formBrankRef = ref<FormInstance>()
// 表单验证规则
const formBrankRules = computed(() => {
    return {
        bank_account_name: [
            { required: true, message: t('bankAccountNamePlaceholder'), trigger: 'blur' }
        ],
        bank_account_no: [
            { required: true, message: t('companyBankPlaceholder'), trigger: 'blur' }
        ],
        bank_name: [
            { required: true, message: t('bankBranchNamePlaceholder'), trigger: 'blur' }
        ],
        bank_address: [
            { required: true, message: t('bankAddressPlaceholder'), trigger: 'blur' }
        ],
        alipay_name: [
            { required: true, message: t('alipayAccountNamePlaceholder'), trigger: 'blur' }
        ],
        alipay_account_no: [
            { required: true, message: t('alipayBankPlaceholder'), trigger: 'blur' }
        ],
        alipay_payment_code: [
            { required: true, message: t('alipayPaymentCodePlaceholder'), trigger: 'blur' }
        ],
        wechat_name: [
            { required: true, message: t('wechatNamePlaceholder'), trigger: 'blur' }
        ],
        wechat_account_no: [
            { required: true, message: t('wechatAccountNoPlaceholder'), trigger: 'blur' }
        ],
        wechat_payment_code: [
            { required: true, message: t('wechatPaymentCodePlaceholder'), trigger: 'blur' }
        ]
    }
})

const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const save = setShopAccount
    const data = formBrank
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            save(data).then(res => {
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

</script>

<style lang="scss" scoped></style>
