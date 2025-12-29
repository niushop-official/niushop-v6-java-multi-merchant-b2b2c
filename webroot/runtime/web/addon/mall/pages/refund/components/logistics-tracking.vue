<template>
     <el-dialog v-model="showDialog" width="800px">
        <div class="min-h-[300px]" v-loading="loading">
            <div  v-if="(Object.keys(detail).length)">
                <div class="flex mb-[20px]">
                    <img class="w-[80px] h-[80px]"  :src="img(detail.order_goods.goods_image ? detail.order_goods.goods_image : '')" alt="">
                    <div class="ml-[10px] flex-1 flex flex-col justify-between">
                        <div>
                            <div class="text-[14px] text-[#333] leading-[20px] h-[40px]">{{ detail.order_goods.goods_name }}</div>
                            <div class="mt-[8px] text-[12px] text-[#999] leading-[16px] oppoSans-R" v-if="detail.order_goods.sku_name">规格：{{ detail.order_goods.sku_name }}</div>
                        </div>
                        <div class="mt-auto flex  justify-between w-[100%]">
                            <span class="">￥{{ detail.order_goods.price }}</span>
                            <span class="text-[12px] text-[#666]">x{{ detail.order_goods.num }}</span>
                        </div>
                    </div>
                </div>
                <el-form label-position="left" label-width="100px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="联系人">{{ detail.refund_address.contact_name }}</el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="手机号">{{ detail.refund_address.mobile }}</el-form-item>
                        </el-col>
                        <el-col :span="12">
                                <el-form-item label="退货地址">{{ detail.refund_address.full_address || '--' }}</el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules">
                    <el-form-item :label="t('expressCompany')" prop="express_company">
                        <el-input v-model.trim="formData.express_company" clearable :placeholder="t('expressCompanyPlaceholder')" class="input-width" />
                    </el-form-item>
                    <el-form-item :label="t('expressNumber')" prop="express_number">
                        <el-input v-model.trim="formData.express_number" clearable :placeholder="t('expressNumberPlaceholder')" class="input-width" />
                    </el-form-item>
                    <el-form-item :label="t('remark')" prop="remark">
                        <el-input v-model.trim="formData.remark" type="textarea" clearable :placeholder="t('remarkPlaceholder')" class="input-width" />
                    </el-form-item>
                </el-form>
            </div>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button @click="showDialog = false" dark>取消</el-button>
                <el-button type="primary" class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]" @click="onSave(formRef)" dark>确定</el-button>
            </div>
         </template>
     </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive} from 'vue'
import { getRefundDetail, refundDelivery, editRefundDelivery }  from '@/addon/mall/api/refund'

const showDialog = ref(false) // 物流弹框
let detail = ref({})
let loading = ref(false)
// 物流信息
const formData = ref({
	express_number: '',
	express_company: '',
	remark: ''
})
const isEditDelivery = ref(false)
const setFormData = async (row,status) => {
    loading.value = true
    if(status) isEditDelivery.value = true
    if (row) {
        const data = await (await getRefundDetail(row)).data
        detail.value = data
        // 赋值物流信息
		if(isEditDelivery.value && detail.value.delivery){
			formData.value.express_number = detail.value.delivery.express_number
			formData.value.express_company = detail.value.delivery.express_company
			formData.value.remark = detail.value.delivery.remark
		}
    }
    loading.value = false
}
setFormData()

const formRef = ref(null)
const formRules = computed(() => {
    return {
        express_number: [
            { required: true, message: t('expressNumberPlaceholder'), trigger: 'blur' }
        ],
        express_company: [
            { required: true, message: t('expressCompanyPlaceholder'), trigger: 'blur' }
        ]
    }
})
const emit = defineEmits(['complete'])
// 提交申请
const onSave = async (formEl) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = {delivery: formData.value,order_refund_no: detail.value.order_refund_no}
            const save = isEditDelivery.value ? editRefundDelivery : refundDelivery
            save(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
                showDialog.value = false
            })
        }
    })
}
defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped>
.input-width{
    width: 340px;
}
</style>