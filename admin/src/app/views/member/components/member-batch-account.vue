<template>
    <el-dialog v-model="showDialog" :title="title || t('updateMember')" width="550px" :destroy-on-close="true">
        <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading" @submit.enter.prevent>

            <el-form-item :label="t('adjustBalance')" prop="adjust">
                 <el-input v-model.number="formData.adjust" clearable  :placeholder="t('adjustPlaceholder')" class="input-width">
                     <template #prepend>
                        <el-select v-model="formData.adjust_type" placeholder="请选择" style="width: 80px">
                            <el-option :label="t('增加')" :value="1" />
                            <el-option :label="t('减少')" :value="-1" />
                        </el-select>
                    </template>
                 </el-input>
            </el-form-item>

            <el-form-item :label="t('memo')" prop="memo">
                <el-input v-model="formData.memo" type="textarea" rows="4" clearable :placeholder="t('memoPlaceholder')" class="input-width"/>
            </el-form-item>

        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{t('confirm')}}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { batchAdjustAccount } from '@/app/api/member'

const showDialog = ref(false)
const loading = ref(true)
const repeat = ref(false)
const title = ref('')
/**
 * 表单数据
 */
const initialFormData = {
    member_ids: [],
    account_type: '',
    memo: '',
    adjust: '',
    adjust_type: 1,
    account_data: '',
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        adjust: [
            { required: true, message: t('adjustBalancePlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    const adjust = Math.abs(parseFloat(formData.adjust))
                    if (formData.adjust <0) {
                        callback(new Error(t('调整数量需大于0')))
                    }
                    if (!adjust) {
                        callback(new Error(t('adjustBalancePlaceholder')))
                    }

                    callback()
                },
                trigger: 'blur'
            }
        ]
    }
})

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            if (repeat.value) return
            repeat.value = true

            formData.account_data = Math.abs(parseFloat(formData.adjust)) * formData.adjust_type
            const data = formData

            batchAdjustAccount(data).then(res => {
                loading.value = false
                repeat.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
                repeat.value = false
                // showDialog.value = false
            })
        }
    })
}

const emit = defineEmits(['complete'])

const setFormData = async (data: any = null) => {
    Object.assign(formData, initialFormData)
    loading.value = true
    formData.member_ids = data.memberIds
    formData.account_type = data.account_type
    title.value = data.title
    loading.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
