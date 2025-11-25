<template>
    <el-dialog v-model="showDialog" :title="t('transfer')" width="500px" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading">
            <el-form-item :label="t('price')" prop="apply_money">
                <div>
                    <el-input v-model.trim="formData.apply_money" clearable :placeholder="t('pricePlaceholder')"
                        class="input-width" maxlength="20">
                        <template #append>{{ t('yuan') }}</template>
                    </el-input>
                    <div class="text-[12px] leading-[14px] text-[#999] mt-[5px]">{{ t('applyMoneyTips') }}</div>
                </div>
            </el-form-item>
            <div v-if="formData.bank_type == 1">
                <el-form-item :label="t('bankCardName')">
                    <div class="input-width"> {{ formData.bank_account_name }} </div>
                </el-form-item>
                <el-form-item :label="t('bankCardNumber')">
                    <div class="input-width"> {{ formData.bank_account_no }} </div>
                </el-form-item>
                <el-form-item :label="t('bankName')">
                    <div class="input-width"> {{ formData.bank_name }} </div>
                </el-form-item>
                <el-form-item :label="t('bankAddress')">
                    <div class="input-width"> {{ formData.bank_address }} </div>
                </el-form-item>
            </div>
            <div v-else-if="formData.bank_type == 2">
                <el-form-item :label="t('alipayName')">
                    <div class="input-width"> {{ formData.alipay_name }} </div>
                </el-form-item>
                <el-form-item :label="t('alipayAccount')">
                    <div class="input-width"> {{ formData.alipay_account_no }} </div>
                </el-form-item>
            </div>
            <div v-else-if="formData.bank_type == 3">
                <el-form-item :label="t('wechatName')">
                    <div class="input-width"> {{ formData.wechat_name }} </div>
                </el-form-item>
                <el-form-item :label="t('wechatAccount')">
                    <div class="input-width"> {{ formData.wechat_account_no }} </div>
                </el-form-item>
            </div>
            <div v-else-if="formData.bank_type == 4">
                <el-form-item :label="t('wechatpay')">
                    <div class="input-width"> 微信零钱 </div>
                </el-form-item>
            </div>
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
import { FormInstance, ElMessage } from 'element-plus'
import { getCashApply } from '@/app/api/shop_site'

const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    apply_money: '',
    bank_account_no: ''

}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()
// 正则表达式
const regExp = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}
// 表单验证规则
const formRules = computed(() => {
    return {
        apply_money: [
            {
                trigger: 'blur',
                required: true,
                validator: (rule: any, value: any, callback: any) => {
                    if (value === '') {
                        callback(new Error(t('pricePlaceholder')))
                    } else if (isNaN(value) || !regExp.digit.test(value)) {
                        callback(new Error(t('priceTips')))
                    } else if (value < 0) {
                        callback(new Error(t('priceNotZeroTips')))
                    } else if (value > 1000) {
                        callback(new Error(t('applyMoneyTips')))
                    } else {
                        callback()
                    }
                }
            }
        ]
    }
})

const emit = defineEmits(['complete', 'transfer'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = formData

            getCashApply(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
                emit('transfer')
                ElMessage({
                    message: t('alipyMoneyAlert'),
                    type: 'success'
                })
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

// 获取字典数据

const setFormData = async (row: any = null) => {
    Object.assign(formData, row)
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
