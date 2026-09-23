<template>
    <el-dialog v-model="showDialog" :title="t('verifyShopApply')" width="500px" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
            <el-form-item :label="t('status')" prop="status">
                <el-radio-group v-model="formData.status" class="ml-4">
                    <el-radio :label="2">{{ t('pass') }}</el-radio>
                    <el-radio :label="-1">{{ t('refuse') }}</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item :label="t('refuseReason')" prop="refuse_reason" v-if="formData.status === -1">
                <el-input v-model="formData.refuse_reason" type="textarea" clearable :placeholder="t('refuseReasonPlaceholder')" class="input-width" />
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
import type { FormInstance } from 'element-plus'
import { verifyShopApply, getShopApplyInfo  } from '@/app/lang/zh-cn/api/admin/site_shop'
const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    apply_id: 0,
    status: 2,
    refuse_reason: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        status: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
        ]
    }
})

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = formData

            verifyShopApply(data).then(res => {
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
        const data = await (await getShopApplyInfo(row.apply_id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
            })
            formData.status = 2
        }
    }
    loading.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
