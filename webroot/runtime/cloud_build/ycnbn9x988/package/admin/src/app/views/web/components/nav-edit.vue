<template>
    <el-dialog v-model="showDialog" :title="formData.id ? t('updateNav') : t('addNav')" width="50%" class="diy-dialog-wrap"
        :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading">
            <el-form-item :label="t('navTitle')" prop="nav_title">
                <el-input v-model="formData.nav_title" clearable :placeholder="t('navTitlePlaceholder')"
                    class="input-width" maxlength="20" />
            </el-form-item>
            <el-form-item :label="t('navUrl')" prop="nav_url">
                <nav-link v-model="formData.nav_url" />
            </el-form-item>
            <el-form-item :label="t('sort')">
                <el-input-number v-model="formData.sort" clearable :placeholder="t('sortPlaceholder')"
                    class="input-width" maxlength="6" />
            </el-form-item>
            <el-form-item :label="t('isBlank')">
                <el-radio-group v-model="formData.is_blank" :placeholder="t('isBlankPlaceholder')">
                    <el-radio :label="1">{{ t('yes') }}</el-radio>
                    <el-radio :label="0">{{ t('no') }}</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item :label="t('isShow')">
                <el-radio-group v-model="formData.is_show" :placeholder="t('isShowPlaceholder')">
                    <el-radio :label="1">{{ t('show') }}</el-radio>
                    <el-radio :label="0">{{ t('hidden') }}</el-radio>
                </el-radio-group>
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
import { addNav, editNav, getNavInfo } from '@/app/api/web'
import navLink from '@/app/views/web/components/nav-link.vue'

const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    id: '',
    nav_title: '',
    nav_url: {},
    sort: '',
    is_blank: 1,
    nav_icon: '',
    is_show: 1
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        nav_title: [
            { required: true, message: t('navTitlePlaceholder'), trigger: 'blur' }
        ],
        nav_url: [
            { required: true, message: t('navUrlPlaceholder'), trigger: 'blur' }
        ],
        sort: [
            { required: true, message: t('sortPlaceholder'), trigger: 'blur' },
            { validator: (rule: any, value: string, callback: any) => { if (value && !/^\d{0,100}$/.test(value)) { callback(new Error(t('generateBetween'))) } else { callback() } } }
        ],
        is_blank: [
            { required: true, message: t('isBlankPlaceholder'), trigger: 'blur' }
        ],
        nav_icon: [
            { required: true, message: t('navIconPlaceholder'), trigger: 'blur' }
        ],
        is_show: [
            { required: true, message: t('isShowPlaceholder'), trigger: 'blur' }
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
    const save = formData.id ? editNav : addNav

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
        const data = await (await getNavInfo(row.id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
                // if (data[key] != undefined && key == 'is_blank') formData[key] = String(data[key])
                // if (data[key] != undefined && key == 'is_show') formData[key] = String(data[key])
            })
        }
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
