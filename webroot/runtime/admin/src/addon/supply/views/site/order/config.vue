<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex ml-[18px] justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="95" ref="formRef" :rules="rules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="panel-title !text-sm pl-[15px]">{{ t('invoice') }}</h3>
                    <el-form-item>
                        <span>{{ t('isInvoice') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.is_invoice">
                            <el-radio label="2">{{ t('isInvoiceClose') }}</el-radio>
                            <el-radio label="1">{{ t('isInvoiceOpen') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item class="invoice">
                        <div class="flex">
                            <div>{{ t('invoiceContent') }}</div>
                            <div class="mx-[10px]">
                                <el-form-item :prop="`invoice_content[${index}]`" v-for="(item, index) in formData.invoice_content" :key="index" :rules="[{ validator: (rule:any, value:any, callback:Function) => {
                                            if (formData.is_invoice === '1') {
                                                if (value === ''){
                                                    return callback(t('invoicePlaceholder'))
                                                } else {
                                                    return callback()
                                                }
                                            } else {
                                                return callback()
                                            }
                                        }, trigger: 'blur' }]">
                                    <div :class="['w-[120px] relative', index ? 'mt-[15px]' : '']" >
                                        <el-input v-model.trim="formData.invoice_content[index]" maxlength="20" class="!w-[120px]" clearable />
                                        <el-icon v-if="index" color="rgba(0, 0, 0, 0.3)" class="!absolute right-[-6px] top-[-5px]" @click="clearInvoiceContent(index)">
                                            <CircleCloseFilled />
                                        </el-icon>
                                    </div>
                                </el-form-item>
                            </div>
                            <el-button @click="insertInvoiceContent">{{ t('insert') }}</el-button>
                        </div>
                    </el-form-item>
                    <!-- <el-form-item prop="invoice_type">
                        <span class="mr-[10px]">{{ t('invoiceType') }}</span>
                        <el-checkbox-group v-model="formData.invoice_type">
                            <el-checkbox label="1">{{ t('electronicInvoice') }}</el-checkbox>
                            <el-checkbox label="2">{{ t('paperInvoice') }}</el-checkbox>
                        </el-checkbox-group>
                    </el-form-item> -->
                </el-card>
            </el-form>
            <div class="fixed-footer-wrap" v-if="!loading">
                <div class="fixed-footer">
                    <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
                </div>
            </div>
        </el-card>
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { getConfig, setConfig } from '@/addon/supply/api/site/order'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title
const formData = ref({
    invoice_content: [''],
    invoice_type: [],
    is_invoice: '1'
})
const validInvoiceType = (rule:any, value:any, callback:Function) => {
    if (formData.value.is_invoice === '1') {
        if (!value.length) {
            return callback(new Error(t('invoiceTypePlaceholder')))
        } else {
            return callback()
        }
    } else {
        return callback()
    }
}
const rules = ref({
    invoice_type: [
        { validator: validInvoiceType, trigger: 'change' }
    ]
})
const loading = ref(false)
const getConfigFn = () => {
    loading.value = true
    getConfig().then(res => {
        Object.values(res.data).forEach(el => {
            formData.value = Object.assign(formData.value, el)
        })
        if (!formData.value.invoice_content.length) formData.value.invoice_content.push('')
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
const insertInvoiceContent = () => {
    formData.value.invoice_content.push('')
}
const clearInvoiceContent = (index:number) => {
    formData.value.invoice_content.splice(index, 1)
}
getConfigFn()
const formRef = ref()

const onSave = async (formEl: any) => {
    await formEl.validate(async (valid:any) => {
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
<style lang="scss" scoped>
</style>
