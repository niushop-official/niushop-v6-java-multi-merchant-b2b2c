<template>
    <el-dialog v-model="showDialog" :title="formData.adv_id ? t('updateAdv') : t('addAdv')" width="30%"
        class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading">
            <el-form-item :label="t('advName')" prop="adv_title">
                <el-input v-model.trim="formData.adv_title" clearable :placeholder="t('advNamePlaceholder')" class="input-width" maxlength="20" />
            </el-form-item>
            <el-form-item :label="t('advPosition')" prop="ap_name">
                <el-input v-model="formData.ap_name" disabled class="input-width" />
            </el-form-item>
            <el-form-item :label="t('webLink')" prop="url">
                <nav-link v-model="formData.adv_url" />
            </el-form-item>
            <el-form-item :label="t('advImg')" prop="adv_image">
                <upload-image v-model="formData.adv_image" />
            </el-form-item>
            <el-form-item :label="t('sort')">
                <el-input v-model="formData.slide_sort" clearable :placeholder="t('sortPlaceholder')" class="input-width" maxlength="6"/>
            </el-form-item>
            <!-- <el-form-item :label="t('background')">
                <el-color-picker v-model="formData.background" />
            </el-form-item> -->
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
import { addAdv, editAdv, getAdvInfo } from '@/app/api/diy'
import navLink from '@/app/views/web/components/nav-link.vue'
const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    ap_key: '',
    ap_name: '',
    adv_id: '',
    adv_title: '',
    adv_url: {},
    adv_image: '',
    slide_sort: '',
    background: ''

}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        adv_title: [
            { required: true, message: t('advNamePlaceholder'), trigger: 'blur' }
        ],
        adv_image: [
            { required: true, message: t('advImagePlaceholder'), trigger: 'blur' }
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
    const save = formData.adv_id ? editAdv : addAdv

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
    if (row.adv_id) {
        const data = await (await getAdvInfo(row.adv_id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
            })
        }
    } else {
        formData.ap_key = row.ap_key
        formData.ap_name = row.ap_name
    }
    loading.value = false
}

// 展示链接
const link: any = ref([])

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
