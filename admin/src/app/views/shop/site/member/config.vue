<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="150px" ref="ruleFormRef" :rules="formRules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="panel-title">{{ t('老用户判定规则') }}</h3>
                    <el-form-item :label="t('会员注册')" prop="old_member_length">
                        <el-input v-model.trim="formData.old_member_length" :placeholder="t('请输入')" class="!w-[214px]" clearable @keyup="filterNumber($event)"  @blur="formData.old_member_length = $event.target.value"/>
                        <span class="ml-[10px] text-[13px]">天后自动成为老用户</span>
                    </el-form-item>
                </el-card>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(ruleFormRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getShopMemberConfig, setShopMemberConfig } from '@/app/api/shop_site'
import { FormInstance, FormRules } from 'element-plus'
import { filterNumber } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref(true)
const ruleFormRef = ref<FormInstance>()
const formRules = reactive<FormRules>({})

const formData = reactive<Record<string, number | string>>({
    old_member_length: 0
})

const setFormData = async () => {
    const data = await (await getShopMemberConfig()).data
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })
    loading.value = false
}
setFormData()

const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate((valid) => {
        if (valid) {
            setShopMemberConfig(formData).then(() => {
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