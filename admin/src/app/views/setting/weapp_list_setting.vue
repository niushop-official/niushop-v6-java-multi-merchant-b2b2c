<template>
    <div class="main-container bg-[#fff] rounded-[4px]">
        <div class="flex ml-[18px] justify-between items-center pt-[20px]">
			<span class="text-page-title">{{ pageName }}</span>
		</div>
        <el-form :model="formData" label-width="150px" ref="formRef" class="page-form" v-loading="loading">
            <el-card class="box-card !border-none" shadow="never">
                <el-form-item :label="t('isOpen')">
                    <el-switch v-model="formData.is_open" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-card>
        </el-form>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" :loading="loading" @click="save(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { setWeappSetting, getWeappSetting } from '@/app/api/sys'
import { FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const loading = ref(true)

const formData = reactive<any>({
    is_open: true
})

const formRef = ref<FormInstance>()

/**
 * 获取配置
 */
getWeappSetting().then(res => {
    Object.assign(formData, res.data)
    formData.is_open = Number(formData.is_open)
    loading.value = false
})

/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            setWeappSetting(formData).then(() => {
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style scoped>

</style>