<template>
    <el-dialog v-model="dialogInvoiceVisible" title="发票信息" width="600">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form">
            <el-form-item label="发票类型" prop="type">
               <div>
                    <div class="flex items-center">
                        <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3] flex box-border px-[40px] leading-[32px] text-[14px]" :class="{'!border-primary text-primary': formData.type == 1}" @click="formData.type = 1">
                            <div>增值税电子普通发票</div>
                            <div class="iconfont icon-xuanzhong4 !text-[22px] text-primary absolute bottom-[-3px] right-[-1px]" v-if="formData.type == 1"></div>
                        </div>
                        <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3] flex box-border px-[40px] leading-[32px] text-[14px]" :class="{'!border-primary text-primary': formData.type == 2}" @click="formData.type = 2,formData.header_type = 2"  v-show="formData.header_type == 2">
                            <div>增值税专用发票</div>
                            <div class="iconfont icon-xuanzhong4 !text-[22px] text-primary absolute bottom-[-3px] right-[-1px]" v-if="formData.type == 2"></div>
                        </div>
                    </div>
                    <div class="text-[#999] text-[12px] mt-[10px] leading-[20px]">{{ formData.type == 1 ? '默认发送至所提供的电子邮件' : '纸质发票开出后将以邮寄形式交付'}}</div>
               </div>
            </el-form-item>
            <el-form-item label="抬头类型" prop="header_type">
                <div class="flex items-center">
                    <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3] flex box-border px-[40px] leading-[32px] text-[14px]" :class="{'!border-primary text-primary': formData.header_type == 1}" @click="formData.header_type = 1,formData.type = 1" >
                        <div>个人</div>
                        <div class="iconfont icon-xuanzhong4 !text-[22px] text-primary absolute bottom-[-3px] right-[-1px]" v-if="formData.header_type == 1"></div>
                    </div>
                    <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3] flex box-border px-[40px] leading-[32px] text-[14px]" :class="{'!border-primary text-primary': formData.header_type == 2}" @click="formData.header_type = 2">
                        <div>企业</div>
                        <div class="iconfont icon-xuanzhong4 !text-[22px] text-primary absolute bottom-[-3px] right-[-1px]" v-if="formData.header_type == 2"></div>
                    </div>
                </div>
            </el-form-item>
            <el-form-item label="发票内容" prop="name">
                <div class="flex items-center flex-wrap">
                    <template v-for="(item,index) in currentConfig.config.invoice.invoice_content" :key="index">
                        <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3] flex box-border px-[40px] leading-[32px] text-[14px] mb-[5px]" :class="{'!border-primary text-primary': formData.name == item}" @click="formData.name = item">
                            <div>{{ item }}</div>
                            <div class="iconfont icon-xuanzhong4 !text-[22px] text-primary absolute bottom-[-3px] right-[-1px]" v-if="formData.name == item"></div>
                        </div>
                    </template>
                </div>
            </el-form-item>
            <el-form-item label="发票抬头" prop="header_name">
                <el-input v-model.trim="formData.header_name" clearable placeholder="请输入发票抬头" class="input-width" maxlength="30" @keyup="filterSpecial($event)" @blur="formData.header_name = $event.target.value"/>
            </el-form-item>
            <el-form-item label="收票邮箱" prop="email">
                <el-input v-model.trim="formData.email" clearable placeholder="请输入收票邮箱" class="input-width" />
            </el-form-item>
            <div v-if="formData.header_type == 2">
                <el-form-item label="纳税人识别号" prop="tax_number">
                    <el-input v-model.trim="formData.tax_number" clearable placeholder="请输入纳税人识别号" class="input-width" />
                </el-form-item>
                <el-form-item label="注册地址" prop="address">
                    <el-input v-model.trim="formData.address" clearable placeholder="请输入注册地址" class="input-width" />
                </el-form-item>
                <el-form-item label="注册电话" prop="telephone">
                    <el-input v-model.trim="formData.telephone" clearable placeholder="请输入注册电话" class="input-width" />
                </el-form-item>
                <el-form-item label="开户银行" prop="bank_name">
                    <el-input v-model.trim="formData.bank_name" clearable placeholder="请输入开户银行" class="input-width" />
                </el-form-item>
                <el-form-item label="银行账号" prop="bank_card_number">
                    <el-input v-model.trim="formData.bank_card_number" clearable placeholder="请输入银行账号" class="input-width" />
                </el-form-item>
            </div>
        </el-form>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="cancel" dark>取消</el-button>
                <el-button type="primary" class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]" @click="onSave(formRef)" dark>确定</el-button>
            </div>
         </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { filterSpecial } from '@/utils/common'
const dialogInvoiceVisible = ref(false)
const currentConfig = ref({}) // 当前发票配置
const formData = reactive({
    header_type: 1,
	email: '',
    header_name: '',
    type: 1,
    name: '',
    tax_number: '',
    telephone: '',
    address: '',
    bank_name: '',
    bank_card_number: ''
})
const setFormData = (row) => {
    if (row) {
        Object.assign(currentConfig.value, row)
    }
}
setFormData()
const open = (data) =>{
    if(data){
        console.log(data)
        Object.keys(formData).forEach((key) => {
            if (data[key] != undefined) formData[key] = data[key]
        })
    }
}
open()
const formRef = ref(null)
const formRules = computed(() => {
    return {
        header_name: [
            { required: true, message: '请输入发票抬头', trigger: 'blur' }
        ],
        email: [
            { required: true, message: '请输入收票邮箱', trigger: 'blur' },
            { required: true, validator: validateEmaile, trigger: 'blur' }
        ],
        tax_number: [
            { required: true, validator: validateTaxNumber, trigger: 'blur' }
        ]
        
    }
})
// 验证邮箱
const validateEmaile = (rule, value, callback) => {
    const limit = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
   if (!limit.test(value)) {
        return callback(new Error(t('请输入正确的收票邮箱')))
    } else {
        return callback()
    }
}

// 验证税号
const validateTaxNumber = (rule, value, callback) =>{
    const limit = /^[0-9a-zA-Z]+$/;
    if (!limit.test(value) && formData.header_type == 2) {
        callback(new Error('请输入正确的纳税人识别号'))
    } else {
        callback()
    }
}
const emit = defineEmits(['confirm','cancel'])
const onSave = async (formEl) => {
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            const invoiceData = {
                site_id: currentConfig.value.site_id,
                invoice: formData
            }
            emit('confirm', invoiceData)
            dialogInvoiceVisible.value = false
        }
    })
}
const cancel = () =>{
    emit('cancel',currentConfig.value.site_id)
    dialogInvoiceVisible.value = false
}
defineExpose({
    dialogInvoiceVisible,
    setFormData,
    open
})
</script>

<style lang="scss" scoped>

</style>