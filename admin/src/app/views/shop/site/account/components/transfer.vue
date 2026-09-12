<template>
    <el-dialog v-model="showDialog" :title="t('transfer')" width="550px" class="diy-dialog-wrap" :destroy-on-close="true">
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
            <el-form-item :label="t('settAccount')" prop="account_id">
                <div class="flex items-center">
                    <el-select v-model="formData.account_id" clearable :placeholder="t('settAccountPlaceholder')" class="input-width">
                        <el-option v-for="(item, index) in settAccountList" :key="item.id" :label="`${item.name}(${item.type_name})`" :value="item.id" />
                    </el-select>
                    <div class="ml-[10px]">
                        <span class="cursor-pointer text-primary" @click="toAdd">{{ t('toAdd') }}</span>
                    </div>
                </div>
            </el-form-item>
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
import { getSettAccountList } from '@/app/api/site'
import { useRouter } from 'vue-router'

const router = useRouter()
const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    account_id: '',
    apply_money: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })
const formRef = ref<FormInstance>()

// 账户数据
const settAccountList = ref([])

// 加载账户列表
const loadAccounts = () => {
    getSettAccountList().then(res => {
        loading.value = false
        settAccountList.value = res.data
    }).catch(() => {
        loading.value = false
    })
}
loadAccounts()

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
        ],
        account_id: [
            {
                trigger: 'change',
                required: true,
                message: t('settAccountPlaceholder')
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

// 去添加
const toAdd = () => {
    const url = router.resolve({
        path: '/site/sett_account/list'
    })
    window.open(url.href)
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
