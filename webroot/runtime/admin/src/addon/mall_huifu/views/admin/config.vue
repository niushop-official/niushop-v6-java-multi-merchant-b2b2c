<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <el-form :model="formData" label-width="220px" ref="formRef" :rules="formRules" class="page-form" >
                <el-form-item :label="t('渠道商号')" prop="platform_huifu_id">
                    <el-input v-model.trim="formData.platform_huifu_id" clearable :placeholder="t('请输入渠道商号')" maxlength="20" class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('支付商户号')" prop="huifu_id">
                    <el-input v-model.trim="formData.huifu_id" clearable :placeholder="t('请输入支付商户号')" maxlength="20" class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('节假日结算手续费率')" prop="fixed_ratio">
                    <el-input v-model.trim="formData.fixed_ratio" clearable :placeholder="t('请输入节假日结算手续费率')"  class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('sys_id')" prop="sys_id">
                    <el-input v-model.trim="formData.sys_id" clearable :placeholder="t('请输入sys_id')"  class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('product_id')" prop="product_id">
                    <el-input v-model.trim="formData.product_id" clearable :placeholder="t('请输入product_id')"  class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('服务商公钥')" prop="rsg_merch_public_key">
                    <el-input v-model.trim="formData.rsg_merch_public_key" clearable :placeholder="t('请输入服务商公钥')"  class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('汇付公钥')" prop="rsa_huifu_public_key">
                    <el-input v-model.trim="formData.rsa_huifu_public_key" clearable :placeholder="t('请输入汇付公钥')"  class="input-width"/>
                </el-form-item>
                <el-form-item :label="t('服务商私钥')" prop="rsa_merch_private_key">
                    <el-input v-model.trim="formData.rsa_merch_private_key" clearable :placeholder="t('请输入服务商私钥')"  class="input-width"/>
                </el-form-item>
            </el-form>
        </el-card>
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
import { useRoute } from 'vue-router'
import { FormInstance, FormRules } from 'element-plus'
import { getConfigHuifu, setConfigHuifu } from '@/addon/mall_huifu/api/admin/config'

const route = useRoute()
const pageName = route.meta.title
const loading = ref(true)

const formData = ref<Record<string, any>>({
    platform_huifu_id: '',
    huifu_id: '',
    fixed_ratio: '',
    sys_id: '',
    product_id: '',
    rsg_merch_public_key: '',
    rsa_huifu_public_key: '',
    rsa_merch_private_key: ''
})
const formRef = ref<FormInstance>()
const formRules = reactive<FormRules>({
    platform_huifu_id: [
        { required: true, message: t('请输入渠道商号'), trigger: 'blur' }
    ],
    huifu_id: [
        { required: true, message: t('请输入支付商户号'), trigger: 'blur' }
    ],
    fixed_ratio: [
        { required: true, message: t('请输入节假日结算手续费率'), trigger: 'blur' },
        {
            validator: (rule: any, value: any, callback: any) => {
                if (value <= 0) {
                    callback(new Error(t('节假日结算手续费率不能小于等于0')))
                } else {
                    callback()
                }
            }
        }
    ],
    sys_id: [
        { required: true, message: t('请输入sys_id'), trigger: 'blur' }
    ],
    product_id: [
        { required: true, message: t('请输入product_id'), trigger: 'blur' }
    ],
    rsg_merch_public_key: [
        { required: true, message: t('请输入服务商公钥'), trigger: 'blur' }
    ],
    rsa_huifu_public_key: [
        { required: true, message: t('请输入汇付公钥'), trigger: 'blur' }
    ],
    rsa_merch_private_key: [
        { required: true, message: t('请输入服务商私钥'), trigger: 'blur' }
    ]
})

const setFormData = () => {
    getConfigHuifu().then((res: any) => {
        formData.value = res.data
        loading.value = false
    })
}

setFormData()

/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            setConfigHuifu(formData.value).then(() => {
                setFormData()
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