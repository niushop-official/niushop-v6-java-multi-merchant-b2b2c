<template>
    <el-dialog v-model="showDialog" :title="formData.category_id ? t('updateMallShopCategory') : t('addMallShopCategory')" width="500px" :destroy-on-close="true">
        <el-form :model="formData" label-width="150px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
            <el-form-item :label="t('categoryName')" prop="category_name">
                <el-input v-model.trim="formData.category_name" clearable :placeholder="t('categoryNamePlaceholder')" class="input-width" />
            </el-form-item>
            <el-form-item :label="t('commissionRate')" prop="commission_rate">
                <el-input v-model="formData.commission_rate" clearable :placeholder="t('commissionRatePlaceholder')" class="input-width"  @keyup="filterDigit($event)">
                    <template #append>%</template>
                </el-input>
            </el-form-item>
            <el-form-item :label="t('image')">
                <upload-image v-model="formData.image" />
            </el-form-item>
            <el-form-item :label="t('sort')" prop="sort">
                <el-input v-model="formData.sort" clearable :placeholder="t('sortPlaceholder')" class="input-width" @keyup="filterNumber($event)" maxlength="6" />
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
import { addShopCategory, editShopCategory, getShopCategoryInfo } from '@/app/api/shop_admin'
import { filterNumber, filterDigit } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    category_id: 0,
    category_name: '',
    commission_rate: '',
    image: '',
    sort: 0
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        category_name: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
        ],
        commission_rate: [
            { required: true, message: t('commissionRatePlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (Number(value) > 100) {
                        return callback(new Error(t('commissionRateTips')))
                    } else {
                        return callback()
                    }
                },
                trigger: 'blur'
            }
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
    const save = formData.category_id > 0 ? editShopCategory : addShopCategory

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
        const data = await (await getShopCategoryInfo(row.category_id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
            })
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
