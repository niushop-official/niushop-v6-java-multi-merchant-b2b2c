<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-form :model="formData" label-width="95" ref="formRef"  class="page-form" v-loading="loading">
                <el-form-item :label="(t('value'))" prop="value" >
                    <div class="flex-1">
                        <el-input v-model.trim="formData.value" :placeholder="t('valuePlaceholder')" type="textarea" maxlength="500" show-word-limit rows="15" class="!w-[500px]" clearable />
                        <div class="text-[12px] text-[#999]">{{ t('valueTips') }}</div>
                    </div>
                </el-form-item>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { t } from '@/lang'
import { useRoute } from 'vue-router'
import { getSensitive, setSensitive } from '@/addon/sow_community/api/admin/sensitive'

const route = useRoute()
const pageName = route.meta.title

const formData = ref<any>({
    value: ''
})

const loading = ref(false)

const getSensitiveFn = () => {
    loading.value = true
    getSensitive().then(res => {
        Object.keys(formData.value).forEach((key: string) => {
            if (res.data[key] != undefined) formData.value[key] = res.data[key]
        })
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getSensitiveFn()

const formRef = ref()

const onSave = async (formEl: any) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid:any) => {
        if (valid) {
            loading.value = true
            setSensitive(formData.value).then(res => {
                getSensitiveFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>

</style>