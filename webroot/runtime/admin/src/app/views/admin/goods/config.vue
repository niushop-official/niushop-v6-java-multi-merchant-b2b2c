<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="95" ref="formRef"  class="page-form" v-loading="loading">
                <el-form-item label="品牌审核" prop="is_verify_brand">
                    <el-radio-group  v-model="formData.is_verify_brand">
                        <el-radio :label="1">开启</el-radio>
                        <el-radio :label="0">关闭</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap" v-if="!loading">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">保存</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsConfig, setGoodsConfig } from '@/app/api/basic_admin'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref(false)
const formData = ref({
    is_verify_brand: 0
})
const formRef = ref()
const getGoodsConfigFn = () => {
    loading.value = true
    getGoodsConfig().then(res => {
        formData.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getGoodsConfigFn()

const onSave = async (formEl: any) => {
    await formEl.validate(async (valid: any) => {
        if (valid) {
            loading.value = true
            setGoodsConfig(formData.value).then(res => {
                getGoodsConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

</script>

<style scoped>

</style>