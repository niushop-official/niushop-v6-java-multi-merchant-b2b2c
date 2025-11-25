<template>
    <el-dialog v-model="showDialog" :title="t('detail')" width="800px" :destroy-on-close="true">
        <el-scrollbar height="400px" v-loading="loading">
            <el-form :model="logData" label-width="100px" ref="formRef" class="px-[10px]" label-position="left">
                <el-row>
                    <el-col :span="12" v-if="logData.header_name">
                        <el-form-item :label="t('headerName')">
								<div class="input-width">{{ logData.header_name }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.header_type_name">
                        <el-form-item :label="t('headTypeName')">
								<div class="input-width">{{ logData.header_type_name }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.name">
                        <el-form-item :label="t('name')">
								<div class="input-width">{{ logData.name  }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.invoice_number">
                        <el-form-item :label="t('invoiceNumber')">
								<div class="input-width">{{ logData.invoice_number}}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.type_name">
                        <el-form-item :label="t('typeName')">
								<div class="input-width">{{ logData.type_name}}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.tax_number">
                        <el-form-item :label="t('taxNumber')">
								<div class="input-width">{{ logData.tax_number}}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.money">
                        <el-form-item :label="t('money')">
								<div class="input-width">{{ logData.money}}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.invoice_time">
                        <el-form-item :label="t('invoiceTime')">
								<div class="input-width">{{ logData.invoice_time === 0 ? '' : logData.invoice_time }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.invoice_voucher">
                        <el-form-item :label="t('invoiceVoucher')">
								<div><img class="w-[50px] max-h-[50px] inline-block"  :src="img(logData.invoice_voucher)" alt="" @click="previewImage" ></div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.bank_name">
                        <el-form-item :label="t('bankTame')">
								<div class="input-width">{{ logData.bank_name }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.bank_card_number">
                        <el-form-item :label="t('bankCardNumber')">
								<div class="input-width">{{ logData.bank_card_number }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.address">
                        <el-form-item :label="t('address')">
								<div class="input-width">{{ logData.address }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.telephone">
                        <el-form-item :label="t('telephone')">
								<div class="input-width">{{ logData.telephone }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label="t('email')">
								<div class="input-width">{{ logData.email }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.mobile">
                        <el-form-item :label="t('mobile')" >
								<div class="input-width">{{ logData.mobile }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label="t('createTime')">
								<div class="input-width">{{ logData.create_time }}</div>
						</el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="logData.remark">
                        <el-form-item :label="t('remark')">
								<div class="input-width break-all">{{ logData.remark }}</div>
						</el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </el-scrollbar>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
            </span>
        </template>
    </el-dialog>
    <el-image-viewer :url-list="previewImageList" v-if="imageViewer.show" @close="imageViewer.show = false" :initial-index="imageViewer.index" :zoom-rate="1" />
</template>

<script lang="ts" setup>
import { ref,reactive } from 'vue'
import { t } from '@/lang'
import { getInvoiceDetail } from '@/app/api/basic_site'
import { img } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)
interface LogDataType {
    header_name?: string
    header_type_name?: string
    name?: string
    invoice_number?: string
    type_name?: string
    tax_number?: string
    money?: string
    invoice_time?: any
    invoice_voucher?: string
    bank_name?: string
    bank_card_number?: string
    address?: string
    telephone?: string
    email?: string
    mobile?: string
    create_time?: string
    remark?: string
}
const logData = ref<LogDataType>({})
const getInvoiceDetailList = async () => {
    logData.value = await (await getInvoiceDetail(id)).data
    loading.value = false
    if(logData.value.invoice_voucher) {
        previewImageList.push(img(logData.value.invoice_voucher))
    }
}

let id = 0
const setFormData = async (row: any = null) => {
    loading.value = true
    if (row) {
        id = row.id
        getInvoiceDetailList()
    }
}

let previewImageList: string[] = reactive([])

/**
 * 查看图片
 */
const imageViewer = reactive({
    show: false,
    index: 0
})
const previewImage = () => {
    imageViewer.show = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped>
.el-form-item__content .input-width{
    word-break: break-all;
	overflow: hidden;
	display: -webkit-box;
	-webkit-box-orient: vertical;
    margin-right: 20px;
}
</style>
