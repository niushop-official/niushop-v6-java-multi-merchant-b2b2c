<template>
    <div>
        <!-- 榜单设置 -->
        <el-card class="box-card !border-none main-container" shadow="never">
            <div class="flex justify-between items-center mb-[10px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-form :model="formData" label-width="120px" ref="formRef" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="panel-title !text-sm pl-[15px]">{{ t('客服设置') }}</h3>
                    <el-form-item :label="t('客服电话')" prop="customer_phone" >
                        <el-input v-model.trim="formData.customer_phone" clearable class="!w-[500px]" :placeholder="t('请输入客服电话')" maxlength="20" show-word-limit />
                    </el-form-item>
                    <el-form-item :label="t('客服二维码')" prop="customer_qrcode" >
                        <upload-image v-model="formData.customer_qrcode" :limit="1" />
                    </el-form-item>
                    <el-form-item :label="t('客服引导话术')" prop="customer_guided_words" >
                       <el-input v-model="formData.customer_guided_words" :placeholder="t('请输入客服引导话术')" type="textarea" maxlength="500" show-word-limit rows="5" class="!w-[500px]" clearable />
                     </el-form-item>

                </el-card>
            </el-form>
        </el-card>

        <!-- 提交按钮 -->
        <div class="fixed-footer-wrap">
            <div class="fixed-footer h-[48px]">
                <el-button type="primary" @click="onSave">{{ t("save") }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { t } from '@/lang'
import { useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'
import { getPintuanConfig, setPintuanConfig } from '@/addon/pintuan/api/site/config'

const route = useRoute()
const pageName = route.meta.title

const formData = ref({
    customer_phone: '',
    customer_qrcode: '',
    customer_guided_words: ''
})

const formRef = ref<FormInstance>()


const loading = ref(false)
const getPintuanConfigFn = () => {
    loading.value = true
    getPintuanConfig().then((res: any) => {
        Object.keys(formData.value).forEach((key) => {
            if (res.data[key]) formData.value[key] = res.data[key]
        })
        loading.value = false
    })
}

getPintuanConfigFn()

const onSave = async () => {
    if (loading.value) return
    await formRef.value?.validate(async (valid) => {
        if (valid) {
            // fillData(); // 填充数据
            loading.value = true
            setPintuanConfig(formData.value).then(() => {
                getPintuanConfigFn()
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
</style>
