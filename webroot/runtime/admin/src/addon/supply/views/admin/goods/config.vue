<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="95" ref="formRef" :rules="rules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="text-[14px] pl-[15px]">{{ t('shopVerify') }}</h3>
                    <el-form-item>
                        <div>
                            <el-radio-group class="mx-[10px]" v-model="formData.is_verify_open">
                                <el-radio :label="1">{{ t('isVerifyOpen') }}</el-radio>
                                <el-radio :label="0">{{ t('isVerifyClose') }}</el-radio>
                            </el-radio-group>
                            <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">{{ t('isVerifyOpenInfoBottom') }}
                            </p>
                        </div>
                    </el-form-item>
                </el-card>
            </el-form>

        </el-card>
        <div class="fixed-footer-wrap" v-if="!loading">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { getConfig, setConfig } from '@/addon/supply/api/admin/goods'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title
const formData = ref({
    is_verify_open: 0
})
const rules = reactive({})
const formRef = ref()

const loading = ref(false)
const getConfigFn = () => {
    loading.value = true
    getConfig().then(res => {
        formData.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getConfigFn()

const onSave = async (formEl: any) => {
    await formEl.validate(async (valid: any) => {
        if (valid) {
            loading.value = true
            setConfig(formData.value).then(res => {
                getConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>
<style lang="scss" scoped></style>
