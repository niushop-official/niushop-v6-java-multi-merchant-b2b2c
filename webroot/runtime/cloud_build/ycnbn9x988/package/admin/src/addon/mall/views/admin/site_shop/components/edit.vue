<template>
    <el-dialog v-model="showDialog" :title="title" width="480" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
            <el-form-item :label="t('categoryName')" prop="name">
                <el-input v-model.trim="formData.name" clearable :placeholder="t('categoryNamePlaceholder')" class="input-width" maxlength="50" show-word-limit />
            </el-form-item>
            <el-form-item :label="t('pid')" prop="pid">
                <el-select v-model="formData.pid" clearable :placeholder="t('pidPlaceholder')" class="input-width" :disabled="formData.category_id !== ''">
                    <el-option :label="t('top_level')" :value="0" />
                    <el-option
                        v-for="item in parentSiteCategoryList"
                        :key="item.category_id"
                        :label="item.name"
                        :value="item.category_id"
                    />
                </el-select>
            </el-form-item>
            <el-form-item :label="t('group')" prop="group_id" v-if="formData.pid === 0">
                <el-select v-model="formData.group_id" clearable :placeholder="t('groupPlaceholder')" class="input-width">
                    <el-option
                        v-for="item in groupList"
                        :key="item.group_id"
                        :label="item.group_name"
                        :value="item.group_id"
                    />
                </el-select>
                <p class="text-[12px] text-[#a9a9a9]">{{ t('groupTips') }}</p>
            </el-form-item>
            <el-form-item :label="t('sort')" prop="sort">
                <el-input v-model="formData.sort" clearable :placeholder="t('sortPlaceholder')" class="input-width" @keyup="filterNumber($event)" maxlength="6"/>
            </el-form-item>
            <el-form-item :label="t('status')" prop="status">
                <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" class="input-width" />
            </el-form-item>
            <el-form-item :label="t('service_rate')" prop="service_rate" v-if="formData.pid === 0">
                <el-input v-model="formData.service_rate" clearable :placeholder="t('serviceRatePlaceholder')" class="input-width" @keyup="filterDigit($event)"/>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('submit') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { addSiteCategory, editSiteCategory, getSiteCategoryAll, getSiteCategoryInfo } from '@/app/api/site_category'
import { getShopGroupAll } from '@/app/api/shop_admin'
import { filterNumber, filterDigit } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)
const title = ref('')

// 正则表达式
const regExp = {
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/
}

/**
 * 表单数据
 */
const initialFormData = {
    category_id: '',
    name: '',
    pid: 0,
    group_id: '',
    sort: 0,
    status: 1,
    service_rate: 0,
    level: 1,
    addon: '',
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        name: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
        ],
        pid: [
            { required: true, message: t('pidPlaceholder'), trigger: 'change' }
        ],
        sort: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (isNaN(value) || !regExp.number.test(value)) {
                        callback(new Error(t('sortTips')))
                    } else {
                        callback()
                    }
                }
            }
        ],
        service_rate: [
            { required: true, message: t('serviceRatePlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (Number(value) > 100) {
                        return callback(new Error(t('serviceRateTips')))
                    } else {
                        return callback()
                    }
                },
                trigger: 'blur'
            }
        ]
    }
})

interface optionListType {
    category_id: number
    name: string
}
const parentSiteCategoryList = ref<optionListType[]>([])
const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const save = formData.category_id ? editSiteCategory : addSiteCategory

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

// 获取父级行业类目
const getParentSiteCategoryList = (addon: string = '') => {
    getSiteCategoryAll({ addon }).then(res => {
        // 过滤父级类目列表，排除当前编辑的类目和其子级类目
        parentSiteCategoryList.value = res.data.filter((el: any) => el.level === 1 && el.category_id != formData.category_id)
    })
}

const groupList = ref([])
const setGroupList = async () => {
    groupList.value = await (await getShopGroupAll({ app: 'mall' })).data
}
setGroupList()

const setFormData = async (row: any = null, addon: string = '') => {
    Object.assign(formData, initialFormData)
    loading.value = true

    if (row && row.category_id) {
        // 检查是否是添加子级模式
        if (row.isAddChild) {
            // 添加子级模式
            title.value = t('add')
            formData.pid = row.category_id
            formData.addon = addon
        } else {
            // 编辑模式
            title.value = t('edit')
            const data = await (await getSiteCategoryInfo(row.category_id)).data
            if (data) {
                Object.keys(formData).forEach((key: string) => {
                    if (data[key] != undefined) formData[key] = data[key]
                })
            }
        }
    } else {
        // 普通添加模式
        title.value = t('add')
        if (addon) {
            formData.addon = addon
        }
    }
    getParentSiteCategoryList(addon)
    loading.value = false
    showDialog.value = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>
</style>
<style lang="scss">
.diy-dialog-wrap .el-form-item__label {
    height: auto !important;
}
</style>