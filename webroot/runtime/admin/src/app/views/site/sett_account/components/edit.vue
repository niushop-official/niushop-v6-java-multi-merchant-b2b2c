<template>
    <el-dialog v-model="showDialog" :title="title" width="480" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
            <el-form-item :label="t('accountType')" prop="type">
                <el-select v-model="formData.type" clearable :placeholder="t('accountTypePlaceholder')" class="input-width">
                    <el-option label="银行卡" :value="1" />
                    <el-option label="支付宝" :value="2" />
                    <el-option label="微信" :value="3" />
                    <el-option label="微信零钱" :value="4" />
                </el-select>
            </el-form-item>
            <view v-if="formData.type == 1">
                <el-form-item :label="t('accountName')" prop="name">
                    <el-input v-model.trim="formData.name" clearable :placeholder="t('accountNamePlaceholder')" class="input-width" />
                </el-form-item>
                <el-form-item :label="t('accountBankNo')" prop="account_no">
                    <el-input v-model.trim="formData.account_no" clearable :placeholder="t('accountBankNoPlaceholder')" class="input-width" @keyup="filterNumber($event)"/>
                </el-form-item>
                <el-form-item :label="t('accountBankName')" prop="bank_name">
                    <el-input v-model.trim="formData.bank_name" clearable :placeholder="t('accountBankNamePlaceholder')" class="input-width" />
                </el-form-item>
                <el-form-item :label="t('accountBankAddress')" prop="bank_address">
                    <el-input v-model.trim="formData.bank_address" clearable :placeholder="t('accountBankAddressPlaceholder')" class="input-width" />
                </el-form-item>
            </view>
            <view v-if="formData.type == 2">
                <el-form-item :label="t('accountName')" prop="name">
                    <el-input v-model.trim="formData.name" clearable :placeholder="t('accountNamePlaceholder')" class="input-width" />
                </el-form-item>
                <el-form-item :label="t('accountAlipayNo')" prop="account_no">
                    <el-input v-model.trim="formData.account_no" clearable :placeholder="t('accountAlipayNoPlaceholder')" class="input-width" />
                </el-form-item>
                <el-form-item :label="t('accountAlipayCodeImg')" prop="account_img">
                    <upload-image v-model="formData.account_img" :limit="1" />
                </el-form-item>
            </view>
            <view v-if="formData.type == 3">
                <el-form-item :label="t('accountName')" prop="name">
                    <el-input v-model.trim="formData.name" clearable :placeholder="t('accountNamePlaceholder')" class="input-width" />
                </el-form-item>
                <el-form-item :label="t('accountWechatNo')" prop="account_no">
                    <el-input v-model.trim="formData.account_no" clearable :placeholder="t('accountWechatNoPlaceholder')" class="input-width" />
                </el-form-item>
                <el-form-item :label="t('accountWechatCodeImg')" prop="account_img">
                    <upload-image v-model="formData.account_img" :limit="1" />
                </el-form-item>
            </view>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { getSettAccountInfo, addSettAccount, editSettAccount } from '@/app/api/site'
import { filterNumber } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)
const title = ref('')

/**
 * 表单数据
 */
const initialFormData = {
    id: '',
    type: '',
    name: '',
    account_no: '',
    bank_name: '',
    bank_address: '',
    account_img: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        type: [
            { required: true, message: t('accountTypePlaceholder'), trigger: 'change' }
        ],
        name: [
            { required: true, message: t('accountNamePlaceholder'), trigger: 'blur' }
        ],
        account_no: [
            { required: true, validator: accountNo, trigger: 'blur' }
        ],
        bank_name: [
            { required: true, message: t('accountBankNamePlaceholder'), trigger: 'blur' }
        ],
        bank_address: [
            { required: true, message: t('accountBankAddressPlaceholder'), trigger: 'blur' }
        ],
        account_img: [
            { required: true, validator: accountImg, trigger: 'blur' }
        ]
    }
})

const accountNo = (rule: any, value: any, callback: any) => {
    if (formData.type == 1 && !value) {
        callback(new Error(t('accountBankNoPlaceholder')))
    } else if (formData.type == 2 && !value) {
        callback(new Error(t('accountAlipayNoPlaceholder')))
    } else if (formData.type == 3 && !value) {
        callback(new Error(t('accountWechatNoPlaceholder')))
    } else {
        callback()
    }
}

const accountImg = (rule: any, value: any, callback: any) => {
    if (formData.type == 2 && !value) {
        callback(new Error(t('accountAlipayCodeImgPlaceholder')))
    } else if (formData.type == 3 && !value) {
        callback(new Error(t('accountWechatCodeImgPlaceholder')))
    } else {
        callback()
    }
}

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const save = formData.id ? editSettAccount : addSettAccount

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = formData

            save(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const setFormData = async (row: any = null) => {
    Object.assign(formData, initialFormData)
    loading.value = true

    if (row) {
        title.value = t('editAccount')
        Object.keys(formData).forEach((key: string) => {
            if (row[key] != undefined) formData[key] = row[key]
        })
    } else {
        title.value = t('addAccount')
    }
    loading.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
<style lang="scss">
.diy-dialog-wrap .el-form-item__label {
    height: auto !important;
}
</style>