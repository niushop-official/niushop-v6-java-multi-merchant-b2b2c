<template>
    <el-dialog v-model="transferDialog" :title="t('transfer')" width="420px">
        <div>
            <el-form :model="transferForm" ref="transferFormRef" :rules="formRules" label-width="80px" label-position="left" >
                <el-form-item :label="t('transferNumber')" prop="transfer_no">
                    <el-input v-model="transferForm.transfer_no" minlength="150"/>
                </el-form-item>
                <el-form-item :label="t('transferVoucher')" prop="transfer_voucher">
                    <upload-image v-model="transferForm.transfer_voucher" />
                </el-form-item>
            </el-form>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="transferDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="refundDialogLoading" @click="refundDialogConfirm(transferFormRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { transferShopCashOutList } from '@/app/api/shop_admin'
import { number } from 'echarts'
const transferDialog = ref(false)

/**
 * 表单数据
 */
const transferForm = ref({
    id:0,
    transfer_no: '',
    transfer_voucher: ''
})

const refundDialogLoading = ref(false)
let id = 0
const setTransferData = async (id: number) => {
    if (id) {
        
        transferForm.value={
            id,
            transfer_no: '',
            transfer_voucher: ''
        }
    }
}

const emit = defineEmits(['complete'])
const refundDialogConfirm = async (formEl: FormInstance | undefined) => {
    if (refundDialogLoading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            refundDialogLoading.value = true
            transferShopCashOutList(transferForm.value).then((res) => {
                emit('complete')
                transferDialog.value = false
                refundDialogLoading.value = false
            }).catch(() => {
                refundDialogLoading.value = false
            })
        }
    })
}
const transferFormRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        transfer_no: [{ required: true, message: t('transferNumberPlaceholder'), trigger: 'blur' }],
        transfer_voucher: [{ required: true, message: t('transferVoucherPlaceholder'), trigger: 'change' }],
    }
})
defineExpose({
    transferDialog,
    setTransferData
})
</script>
