<template>
    <el-dialog v-model="showDialog" :title="title" width="480" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading">
            <el-form-item :label="t('categoryName')" prop="category_name">
                <el-input v-model.trim="formData.category_name" clearable :placeholder="t('categoryNamePlaceholder')" class="input-width" maxlength="10" show-word-limit />
            </el-form-item>
            <el-form-item :label="t('pid')" prop="pid">
                <el-cascader :props="goodsCategoryProps" v-model="formData.pid" :options="goodsCategoryOptions" :placeholder="t('goodsCategoryPlaceholder')" :disabled="formData.child_count" clearable filterable class="input-width" />
            </el-form-item>
            <el-form-item :label="t('image')">
                <upload-image v-model="formData.image" />
            </el-form-item>
            <el-form-item :label="t('sort')" prop="sort">
                <el-input v-model.trim="formData.sort" clearable :placeholder="t('sortPlaceholder')" class="input-width" maxlength="8" />
            </el-form-item>
            <el-form-item :label="t('isShow')" prop="is_show">
                <el-switch v-model="formData.is_show" class="input-width" :active-value="1" :inactive-value="2" />
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
import { addCategory, editCategory, getCategoryInfo, getCategoryTree } from '@/app/api/basic_admin'

const showDialog = ref(false)
const loading = ref(false)
const title = ref('')

/**
 * 表单数据
 */
const initialFormData = {
    category_id: '',
    category_name: '',
    image: '',
    pid: 0,
    is_show: 1,
    child_count: 0,
    sort: 100,
    level: 1
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        category_id: [
            { required: true, message: t('categoryIdPlaceholder'), trigger: 'blur' }
        ],
        category_name: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
        ],
        pid: [
            { required: true, message: t('pidPlaceholder'), trigger: 'change' }
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
    const save = formData.category_id ? editCategory : addCategory

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

// 获取全部分类
const goodsCategoryOptions: any = reactive([])
const goodsCategoryProps = {
    checkStrictly: true,
    emitPath: false
}
const initData = () => {
    getCategoryTree().then((res) => {
        const data = res.data
        if (data) {
            const goodsCategoryTree: any = []
            goodsCategoryTree.push({
                label: '顶级分类',
                value: 0
            })
            data.forEach((item: any) => {
                const children: any = []
                if (item.child_list) {
                    item.child_list.forEach((childItem: any) => {
                        if (childItem.category_id == formData.category_id) {
                            children.push({
                                value: childItem.category_id,
                                label: childItem.category_name,
                                disabled: true
                            })
                        } else {
                            children.push({
                                value: childItem.category_id,
                                label: childItem.category_name
                            })
                        }
                    })
                }
                if (item.category_id == formData.category_id) {
                    goodsCategoryTree.push({
                        value: item.category_id,
                        label: item.category_name,
                        disabled: true,
                        children
                    })
                } else {
                    goodsCategoryTree.push({
                        value: item.category_id,
                        label: item.category_name,
                        children
                    })
                }
            })
            goodsCategoryOptions.splice(0, goodsCategoryOptions.length, ...goodsCategoryTree)
        }
    })
}
// initData()
const setFormData = async (row: any = null) => {
    Object.assign(formData, initialFormData)
    loading.value = true

    if (row) {
        title.value = t('updateCategory')
        const data = await (await getCategoryInfo(row.category_id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
            })
        }
    } else {
        title.value = t('addCategory')
    }
    initData()
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
