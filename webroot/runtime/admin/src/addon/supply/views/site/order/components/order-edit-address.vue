<template>
    <el-dialog v-model="showDialog" :title="t('editAddress')" width="700px" class="edit-address-dialog-wrap" :destroy-on-close="true" :before-close="handleClose">
        <div v-loading="loading">
            <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules" class="page-form">
                <el-form-item :label="t('contacts')"  prop="taker_name">
                    <el-input v-model.trim="formData.taker_name" clearable :placeholder="t('contactsPlaceholder')" class="input-width" maxlength="10" />
                </el-form-item>
                <el-form-item :label="t('ContactInformation')"  prop="taker_mobile">
                    <el-input v-model.trim="formData.taker_mobile" clearable :placeholder="t('ContactInformationPlaceholder')" class="input-width" maxlength="15" />
                </el-form-item>
                <el-form-item :label="t('address')" prop="address_area">
                    <el-select v-model="formData.taker_province" value-key="id" clearable class="w-[214px]"  ref="provinceRef" :placeholder="t('province')" @change="addressChange">
                        <el-option :label="t('province')"  :value="0"/>
                        <el-option v-for="(item, index) in areaList.province" :key="index" :label="item.name"  :value="item.id"/>
                    </el-select>
                    <el-select v-model="formData.taker_city" value-key="id" clearable class="w-[214px] ml-3" ref="cityRef" :placeholder="t('city')" @change="addressChange">
                        <el-option :label="t('city')"  :value="0"/>
                        <el-option v-for="(item, index) in areaList.city " :key="index" :label="item.name"  :value="item.id"/>
                    </el-select>
                    <el-select v-model="formData.taker_district" value-key="id" clearable class="w-[214px] mt-[20px]"  ref="districtRef" :placeholder="t('area')" @change="addressChange">
                        <el-option :label="t('area')" :value="0"/>
                        <el-option v-for="(item, index) in areaList.district " :key="index" :label="item.name"  :value="item.id" />
                    </el-select>
                    <el-input v-model.trim="formData.taker_address" clearable :placeholder="t('detailedAddress')" class="!w-[214px] mt-[20px] ml-3" maxlength="30"/>
                </el-form-item>
            </el-form>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, watch, nextTick } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { getCompanyList } from '@/app/api/delivery_site'
import { getOrderEditAddress, orderEditAddress } from '@/addon/supply/api/site/order'
import { getAreaListByPid } from '@/app/api/sys'

const showDialog = ref(false)
const loading = ref(false)

interface companyDataType{
    company_id: number,
    company_name: string,
    index: number
}
const companyData = ref<companyDataType[]>([])

getCompanyList({}).then((data) => {
    companyData.value = data.data
})

/**
 * 表单数据
 */
const initialFormData = {
    order_id: 0,
    delivery_type: '',
    taker_name: '',
    taker_mobile: '',
    taker_full_address: '',
    taker_address: '',
    taker_province: 0,
    taker_city: 0,
    taker_district: 0
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()
// 表单验证规则
const formRules = computed(() => {
    return {
        taker_name: [
            { required: true, validator: companyPass, trigger: 'blur' }
        ],
        taker_mobile: [
            { required: true, validator: ContactInformation, trigger: 'blur' }
        ],
        address_area: [
            {
                required: true,
                validator: (rule: any, value: any, callback: any) => {
                    if (!formData.taker_province) {
                        callback(new Error(t('provincePlaceholder')))
                    }
                    if (!formData.taker_city) {
                        callback(new Error(t('cityPlaceholder')))
                    }
                    if (areaList.district.length && !formData.taker_district) {
                        callback(new Error(t('districtPlaceholder')))
                    }
                    if (!formData.taker_address) {
                        callback(new Error(t('detailedAddress')))
                    }
                    callback()
                }
            }
        ]
    }
})

const companyPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express' && value === '') {
        callback(new Error(t('contactsPlaceholder')))
    } else {
        callback()
    }
}

const ContactInformation = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express') {
        const reg = /^1[3-9]\d{9}$/
        if (value === '') {
            callback(new Error(t('ContactInformationPlaceholder')))
        } else if (value.length != 11) {
            callback(new Error('请输入11位手机号'))
        } else if (!reg.test(value)) {
            callback(new Error('请输入正确的手机号'))
        }
    }
    callback()
}

const emit = defineEmits(['complete'])

const addressChange=()=>{
    formData.taker_address = ''
}

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            
            const address = [
                formData.taker_province ? addressName(areaList.province, formData.taker_province) : '',
                formData.taker_city ? addressName(areaList.city, formData.taker_city) : '',
                formData.taker_district ? addressName(areaList.district, formData.taker_district) : '',
                formData.taker_address
            ]
            formData.taker_full_address = address.join('')

            orderEditAddress(formData).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
                initData()
            }).catch(() => {
                loading.value = false
            })
            
        }
    })
}

const getAddressInfoFn = (data:any) => {
    getOrderEditAddress(data).then((res) => {
        for (const i in formData) {
            if (res.data[i]) {
                formData[i] = res.data[i]
            }
        }
        loading.value = false
    })
}

// 初始化
const setFormData = async (data: any) => {
    loading.value = true
    const params = {
        order_id: data.order_id,
        delivery_type: data.delivery_type
    }
    nextTick(() => {
        getAddressInfoFn(params)
    })
}

interface areaType{
    province: any[],
    city: any[],
    district: any[]
}
const areaList = reactive<areaType>({
    province: [],
    city: [],
    district: []
})

const provinceRef = ref()
const cityRef = ref()
const districtRef = ref()

/**
 * 获取市
 */
watch(() => formData.taker_province, (nval) => {
    if (nval) {
        getAreaListByPid(formData.taker_province).then(res => {
            areaList.city = res.data
            const cityId = formData.taker_city
            if (cityId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (cityId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.taker_city = cityId
                    return
                }
            }
            formData.taker_city = 0
        })
    } else {
        formData.taker_city = 0
    }
})

/**
* 获取区
 */
watch(() => formData.taker_city, (nval) => {
    if (nval) {
        getAreaListByPid(formData.taker_city).then(res => {
            areaList.district = res.data

            const districtId = formData.taker_district
            if (districtId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (districtId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.taker_district = districtId
                    return
                }
            }
            formData.taker_district = 0
        })
    } else {
        formData.taker_district = 0
    }
})

/**
 * 获取省
 */
getAreaListByPid(0).then(res => {
    areaList.province = res.data
})

const handleClose = (next:any) => {
    initData()
    next()
}
const initData = () => {
    const data = {
        order_id: 0,
        delivery_type: '',
        taker_name: '',
        taker_mobile: '',
        taker_full_address: '',
        taker_address: '',
        taker_province: 0,
        taker_city: 0,
        taker_district: 0,

    }
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })
}

const addressName = (data,id)=>{
    const address = data.find(item => item.id === id);
    return address.name;
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped>
</style>
<style lang="scss">
.edit-address-dialog-wrap .el-form-item__label {
    height: auto !important;
}
.edit-address-dialog-wrap .el-dialog__body {
    padding: 15px 20px !important;
}
</style>
