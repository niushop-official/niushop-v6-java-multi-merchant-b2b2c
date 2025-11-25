<template>
    <el-dialog v-model="dialogAddressVisible" title="收货地址" width="800">
        <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules" class="page-form">
            <el-form-item :label="t('name')" prop="name">
                <el-input v-model.trim="formData.name" clearable :placeholder="t('namePlaceholder')" class="input-width" />
            </el-form-item>
            <el-form-item :label="t('mobile')" prop="mobile">
                <el-input v-model.trim="formData.mobile" clearable :placeholder="t('mobilePlaceholder')" class="input-width" />
            </el-form-item>
            <el-form-item :label="t('address')" prop="address_area">
				<el-select v-model="formData.province_id" value-key="id" clearable class="w-[200px]"  ref="provinceRef">
					<el-option :label="t('provincePlaceholder')" :value="0"/>
					<el-option v-for="(item, index) in areaList.province " :key="index" :label="item.name"  :value="item.id"/>
				</el-select>
				<el-select v-model="formData.city_id" value-key="id" clearable class="w-[200px] ml-3" ref="cityRef">
					<el-option :label="t('cityPlaceholder')" :value="0"/>
					<el-option v-for="(item, index) in areaList.city " :key="index" :label="item.name"  :value="item.id"/>
				</el-select>
				<el-select v-model="formData.district_id" value-key="id" clearable class="w-[200px] ml-3"  ref="districtRef">
					<el-option :label="t('districtPlaceholder')" :value="0"/>
					<el-option v-for="(item, index) in areaList.district " :key="index" :label="item.name"  :value="item.id"/>
				</el-select>
			</el-form-item>
            <el-form-item :label="t('fullAddress')"  prop="address">
                <el-input v-model.trim="formData.address" clearable :placeholder="t('fullAddressPlaceholder')" type="textarea" maxlength="50" />
            </el-form-item>
            <el-form-item :label="t('isDefault')" >
                <el-switch v-model="formData.is_default" :activeValue="1" :inactiveValue="0" />
            </el-form-item>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="dialogAddressVisible = false" dark>取消</el-button>
                <el-button type="primary" class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]" @click="onSave(formRef)" dark>确定</el-button>
            </div>
         </template>
    </el-dialog> 
</template>

<script setup lang="ts">
import { ref, reactive,computed,watch } from 'vue'
import { getAreaListByPid, addAddress, editAddress, getAddressInfo } from '@/addon/mall/api/address'

const dialogAddressVisible = ref(false)
const loading = ref(false)
/**
 * 表单数据
 */
 const initialFormData = reactive({
    id: 0,
    name:'',
    mobile:'',
    province_id: 0,
    province_name: '',
    city_id: 0,
    city_name: '',
    district_id: 0,
    district_name: '',
    address: '',
    full_address:'',
    is_default: 0,
    type: 'address'
})
const formData = reactive({ ...initialFormData })

const setFormData = async (row) => {
    Object.assign(formData, initialFormData)
    if (row) {
        const data = await (await getAddressInfo(row)).data
        Object.keys(formData).forEach((key) => {
            if (data[key] != undefined) formData[key] = data[key]
        })
    }
}
setFormData()

const formRef = ref(null)
const formRules = computed(() => {
    return {
        name: [
            { required: true, message: t('namePlaceholder'), trigger: 'blur' }
        ],
        mobile: [
            { required: true, message: t('mobilePlaceholder'), trigger: 'blur' },
            { required: true, validator: validatePhone, trigger: 'blur' }
        ],
        address_area: [
            { required: true, validator: validatePass, trigger: 'blur' }
        ],
        address: [
            { required: true, message: t('fullAddressPlaceholder'), trigger: 'blur' }
        ]
    }
})
const validatePass = (rule, value, callback) => {
    if (!formData.province_id) {
        callback(new Error(t('请选择省份')))
     }
    if (!formData.city_id) {
        callback(new Error(t('请选择城市')))
    }
    if (areaList.district.length && !formData.district_id) {
        callback(new Error(t('请选择区县')))
    }
    callback()
}
// 验证手机号格式
const validatePhone = (rule, value, callback) => {
    const phonePattern = /^1[3456789]\d{9}$/
   if (!phonePattern.test(value)) {
        return callback(new Error(t('请输入正确的手机号')))
    } else {
        return callback()
    }
}
const areaList = reactive({
    province: [],
    city: [],
    district: []
})
const checkAreatress = () => {
    getAreaListByPid(0).then(res => {
        areaList.province = res.data
    })
}
checkAreatress()

/**
 * 获取市
 */
 watch(() => formData.province_id, (nval) => {
    if (nval) {
        getAreaListByPid(formData.province_id).then(res => {
            areaList.city = res.data

            const cityId = formData.city_id
            if (cityId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (cityId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.city_id = cityId
                    return
                }
            }
            formData.city_id = 0
        })
    } else {
        formData.city_id = 0
    }
})

/**
* 获取区
 */
 watch(() => formData.city_id, (nval) => {
    if (nval) {
        getAreaListByPid(formData.city_id).then(res => {
            areaList.district = res.data

            const districtId = formData.district_id
            if (districtId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (districtId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.district_id = districtId
                    return
                }
            }
            formData.district_id = 0
        })
    } else {
        formData.district_id = 0
    }
})

const emit = defineEmits(['complete','select'])
const provinceRef = ref()
const cityRef = ref()
const districtRef = ref()
// 提交申请
const onSave = async (formEl) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
    
            loading.value = true
            const data = formData
            const address = [
                data.province_id ? provinceRef.value.selectedLabel : '',
                data.city_id ? cityRef.value.selectedLabel : '',
                data.district_id ? districtRef.value.selectedLabel : '',
                data.address
            ]
            data.full_address = address.join('')

            const save = formData.id ? editAddress : addAddress
            
            save(data).then(res => {
                loading.value = false
                dialogAddressVisible.value = false
                emit('complete')
                emit('select', res.data)
            }).catch(() => {
                loading.value = false
                dialogAddressVisible.value = false
            })
        }
    })
}
defineExpose({
    dialogAddressVisible,
    setFormData
})
</script>

<style lang="scss" scoped>
.input-width {
        width: 300px;
    }
</style>