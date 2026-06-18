<template>
    <el-dialog v-model="showDialog" title="批量打单发货" width="600px" :destroy-on-close="true" :close-on-click-modal="false" @close="handleClose">
        <div v-loading="loading">
            <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules" class="page-form mb-[30px]">
                <el-form-item label="物流公司" prop="express_company_id">
                    <el-select v-model="formData.express_company_id" placeholder="请选择物流公司" class="input-width" @change="companyChange">
                        <el-option v-for="(item) in companyData" :key="item.index" :label="item.company_name" :value="item.company_id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="电子面单" prop="electronic_sheet_id">
                    <el-select v-model="formData.electronic_sheet_id" placeholder="请选择电子面单" clearable class="input-width">
                        <el-option v-for="(item) in electronicSheetData" :key="item.id" :label="item.template_name" :value="item.id" />
                    </el-select>
                </el-form-item>

                <el-form-item label="发货地址" prop="address_id">
                    <el-select v-model="formData.address_id" placeholder="请选择发货地址" clearable class="input-width">
                        <el-option v-for="(item) in addressData" :key="item.id" :label="item.full_address" :value="item.id" />
                    </el-select>
                    <div class="ml-[10px] whitespace-nowrap">
                        <span class="cursor-pointer text-primary mr-[10px]" @click="toAddress">新增发货地址</span>
                        <span class="cursor-pointer text-primary" @click="refreshAddress">刷新</span>
                    </div>
                </el-form-item>
            </el-form>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">取消</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { getElectronicSheetList, getCompanyList, getOrderAddressList } from '@/app/api/delivery_site'
import { useRouter } from 'vue-router'
import { useBatchDelivery } from '@/addon/mall/views/site/order/public/js/batchDelivery'

const router = useRouter()
const showDialog = ref(false)
const loading = ref(false)
const companyData = ref<any[]>([])
const electronicSheetAll = ref<any[]>([])
const electronicSheetData = ref<any[]>([])
const addressData = ref<any[]>([])
const selectedGoodsForPrint = ref<any>([])
const currentOperationType = ref('')

const formData = reactive({
    delivery_type: 'express',
    delivery_way: 'electronic_sheet',
    express_company_id: '',
    electronic_sheet_id: '',
    address_id: ''
})

const formRef = ref<FormInstance>()

const formRules = computed(() => {
    return {
        express_company_id: [
            { required: true, message: '请选择物流公司', trigger: 'blur' }
        ],
        electronic_sheet_id: [
            { required: true, message: '请选择电子面单', trigger: 'blur' }
        ],
        address_id: [
            { required: true, message: '请选择发货地址', trigger: 'blur' }
        ]
    }
})

// 初始化数据
const initData = async () => {
    loading.value = true
    try {
        const companyRes = await getCompanyList({})
        companyData.value = companyRes.data

        const sheetRes = await getElectronicSheetList({
            status: 1,
            support_trade: 'mall'
        })
        electronicSheetAll.value = sheetRes.data || []

        await getAddressList()
    } catch (error) {
        console.error(error)
    } finally {
        loading.value = false
    }
}

const getAddressList = async () => {
    const addressRes = await getOrderAddressList()
    addressData.value = addressRes.data || []
}

const companyChange = (value: any) => {
    electronicSheetData.value = []
    formData.electronic_sheet_id = ''
    electronicSheetAll.value.forEach((item: any) => {
        if (item.express_company_id == value) {
            electronicSheetData.value.push(cloneDeep(item))
        }
    })
}

const toAddress = () => {
    const url = router.resolve({
        path: '/site/site_address'
    })
    window.open(url.href)
}

const refreshAddress = async () => {
    await getAddressList()
}

const emit = defineEmits(['complete'])

const batchDelivery = useBatchDelivery()

const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            const data = cloneDeep(formData)
            const address = addressData.value.find((item: any) => item.id == data.address_id)
            if (address) {
                data.address = {
                    contact_name: address.contact_name,
                    mobile: address.mobile,
                    province_name: address.province_name,
                    city_name: address.city_name,
                    district_name: address.district_name,
                    address: address.address
                }
            }
            batchDelivery.handleBatchDelivery({
                goods: selectedGoodsForPrint.value,
                formData: data,
                type: currentOperationType.value
            }, () => {
                emit('complete', data)
                showDialog.value = false
                loading.value = false
            })
        }
    })
}

const handleClose = () => {
    showDialog.value = false
}

const open = (data: any) => {
    showDialog.value = true
    formData.express_company_id = ''
    formData.electronic_sheet_id = ''
    formData.address_id = ''
    selectedGoodsForPrint.value = cloneDeep(data.list)
    currentOperationType.value = data.type
    initData()
}

defineExpose({
    showDialog,
    open
})
</script>

<style lang="scss" scoped>
</style>
