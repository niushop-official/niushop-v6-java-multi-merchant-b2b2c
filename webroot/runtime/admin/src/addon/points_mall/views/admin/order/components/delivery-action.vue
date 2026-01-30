<template>
    <el-dialog v-model="showDialog" :title="showType == 'add' ? t('delivery') : t('modifyDelivery')" :width="showType =='add' ?'800px' :'1000px'" class="diy-dialog-wrap" :destroy-on-close="true" :close-on-click-modal="false" @close="handleClose">
        <div v-loading="loading">

            <el-alert type="warning" :closable="false" class="!mb-[10px]" v-if="isTradeManaged">
                <template #default>
                    <p>{{ t('weappDeliveryTips') }}</p>
                    <p>{{ t('deliveryTips') }}</p>
                </template>
            </el-alert>
            <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules" class="page-form mb-[30px]">
                <el-form-item :label="t('deliveryType')" prop="delivery_type">
                    <el-radio-group v-model="formData.delivery_type" @change="deliveryChange">
                        <el-radio :label="index" v-for="(item, index) in deliveryType" :key="index">{{ item }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <template v-if="formData.delivery_type == 'express'">
                        <el-form-item :label="t('deliveryWay')">
                        <el-radio-group v-model="formData.delivery_way">
                            <el-radio label="manual_write">{{ t('manualWriteWay') }}</el-radio>
                            <!-- <el-radio label="electronic_sheet">{{ t('electronicSheetWay') }}</el-radio> -->
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="t('company')" prop="express_company_id">
                        <el-select v-model="formData.express_company_id" :placeholder="t('companyPlaceholder')" class="input-width" @change="companyChange">
                            <el-option v-for="(item) in companyData" :key="item.index" :label="item.company_name" :value="item.company_id" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('expressNumber')" v-if="formData.delivery_way == 'manual_write'" prop="express_number">
                        <el-input v-model.trim="formData.express_number" clearable :placeholder="t('expressNumberPlaceholder')" class="input-width" maxlength="30" />
                    </el-form-item>
                    <el-form-item :label="t('electronicSheetTemplate')" v-if="formData.delivery_way == 'electronic_sheet'" prop="electronic_sheet_id">
                        <el-select v-model="formData.electronic_sheet_id" :placeholder="t('electronicSheetTemplatePlaceholder')" clearable class="input-width">
                            <el-option v-for="(item) in electronicSheetData" :key="item.id" :label="item.template_name" :value="item.id" />
                        </el-select>
                    </el-form-item>
                </template>
            </el-form>
            <el-table :data="goodsDataArr" size="large" @selection-change="handleSelectionChange" ref="tableRef" max-height="400px">
                <el-table-column type="selection" width="55" :selectable="selectable" />
                <el-table-column prop="goods_name" :label="t('goodsName')" min-width="200" >
                    <template #default="{ row }">
                        <div class="flex cursor-pointer">
                            <div class="flex items-center min-w-[50px] mr-[10px]">
                                <img class="w-[50px] h-[50px]" v-if="row.goods_image" :src="img(row.goods_image)" alt="">
                                <img class="w-[50px] h-[50px]" v-else src="" alt="">
                            </div>
                            <div class="flex  flex-col items-start">
                                <span class="multi-hidden text-[14px]">{{row.goods_name}}</span>
                                <span class="text-[#999] text-[12px]" v-if="row.sku_name">{{row.sku_name}}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="num" :label="t('num')" min-width="80" />
                <el-table-column prop="status_name" :label="t('refundStatusName')" min-width="80" />
                <el-table-column prop="delivery_status_name" :label="t('deliveryStatusName')" min-width="80" />
                <el-table-column  :label="t('deliveryPlatform')" min-width="80" v-if="showType == 'edit' && formData.delivery_type == 'local_delivery'">
                    <template #default="{ row }">
                        <span>{{ row.delivery_package && row.delivery_package.delivery_service_name }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="express_company_name" :label="t('company')" min-width="80" v-if="showType == 'edit' && formData.delivery_type == 'express'">
                    <template #default="{ row }">
                        <span>{{ row.delivery_package?.company?.company_name }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="express_number" :label="t('expressNumber')" min-width="80" v-if="showType == 'edit'  && formData.delivery_type == 'express'">
                    <template #default="{ row }">
                        <span>{{ row.delivery_package?.express_number }}</span>
                    </template>
                </el-table-column>
            </el-table>
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
import { ref, reactive, computed, nextTick } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { FormInstance, ElMessage } from 'element-plus'
import { getOrderDeliveryType, orderDelivery } from '@/addon/points_mall/api/admin/order'
import { getIsTradeManaged } from '@/app/api/weapp'
import { cloneDeep } from 'lodash-es'
import { getElectronicSheetConfig, getElectronicSheetList, printElectronicSheet, getCompanyList, getShopDeliverList } from '@/app/api/delivery_site'
import { useRouter } from 'vue-router'
import { loadCLodop, getLodop } from '@/utils/lodop'

const router = useRouter()
const showDialog = ref(false)
const loading = ref(false)
interface companyDataType{
    company_id: number,
    company_name: string,
    index: number
}
const companyData = ref<companyDataType[]>([])
const isHasVirtual = ref(false)
const deliveryType = ref([])
const isTradeManaged = ref(false)

getCompanyList({}).then((data) => {
    companyData.value = data.data
})

getIsTradeManaged().then(res => {
    isTradeManaged.value = res.data.is_trade_managed
})

// getElectronicSheetConfig().then((res:any) => {
//     if (res.data) {
//         loadCLodop(res.data)
//     }
// })

const electronicSheetAll: any = ref([])
const electronicSheetData: any = ref([])
getElectronicSheetList({
    status: 1,
    support_trade: 'mall'
}).then((res: any) => {
    if (res.data) {
        electronicSheetAll.value = res.data
    }
})

const companyChange = (value: any) => {
    electronicSheetData.value = []
    formData.electronic_sheet_id = ''
    electronicSheetAll.value.forEach((item: any) => {
        if (item.express_company_id == value) {
            electronicSheetData.value.push(cloneDeep(item))
        }
    })
}

const deliverData = ref([])
getShopDeliverList({}).then((res: any) => {
    deliverData.value = res.data
})

/**
 * 表单数据
 */
const goodsData = ref([])
const initialFormData = {
    order_id: 0,
    delivery_type: '',
    delivery_way: 'manual_write',
    local_delivery_type: 'merchant',
    goods_weight: 1,
    local_deliver_id: '',
    deliver_mobile: '',
    express_company_id: '',
    express_number: '',
    electronic_sheet_id: '',
    order_goods_ids: []
}

const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 正则表达式
const regExp: any = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}

// 表单验证规则
const formRules = computed(() => {
    return {
        delivery_type: [
            { required: true, message: t('deliveryTypePlaceholder'), trigger: 'blur' }
        ],
        express_company_id: [
            { required: true, validator: companyPass, trigger: 'blur' }
        ],
        express_number: [
            { required: true, validator: expressNumberPass, trigger: 'blur' }
        ],
        electronic_sheet_id: [
            { required: true, validator: electronicSheetIdPass, trigger: 'blur' }
        ],
        local_deliver_id: [
            { required: true, validator: localDeliverIdPass, trigger: 'blur' }
        ],
        local_delivery_type: [
            { required: true, validator: thirdDeliveryTypePass, trigger: ['blur', 'change'] }
        ],
        goods_weight: [
            { required: true, validator: goodsWeightPass, trigger: 'blur' }
        ]
    }
})

const companyPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express' && value === '') {
        callback(new Error(t('companyPlaceholder')))
    } else {
        callback()
    }
}

const expressNumberPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express' && formData.delivery_way == 'manual_write' && value === '') {
        callback(new Error(t('expressNumberPlaceholder')))
    } else {
        callback()
    }
}

const electronicSheetIdPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'express' && formData.delivery_way == 'electronic_sheet' && value === '') {
        callback(new Error(t('electronicSheetTemplatePlaceholder')))
    } else {
        callback()
    }
}

const localDeliverIdPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'local_delivery' && formData.delivery_way == 'store_delivery' && value == '') {
        callback(new Error(t('deliverNamePlaceholder')))
    } else {
        callback()
    }
}

const thirdDeliveryTypePass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'local_delivery' && formData.delivery_way == 'third_party_delivery' && (value == '' || value == undefined)) {
        callback(new Error(t('thirdDeliveryNamePlaceholder')))
    } else {
        callback()
    }
}

const goodsWeightPass = (rule: any, value: any, callback: any) => {
    if (formData.delivery_type == 'local_delivery' && formData.delivery_way == 'third_party_delivery' && value == '') {
        callback(new Error(t('goodsWeightPlaceholder')))
    } else if (isNaN(value) || !regExp.special.test(value)) {
        callback(new Error(t('weightTips')))
    } else if (value <= 0) {
        callback(new Error(t('weightNotZeroTips')))
    } else {
        callback()
    }
}


const selectable = (row:any, index:number) => {
    if ((row.status == 2 || row.delivery_status != 'wait_delivery' || row.status == 3) && (showType.value == 'add' || !showType.value)) {
        return false
    }
    return true
}
interface goodsDataType{
    goods_type:string
}
const goodsDataArr = ref<goodsDataType[]>([])
const deliveryChange = () => {
    const arr: any = []
    if (formData.delivery_type && formData.delivery_type == 'virtual') {
        goodsData.value.forEach((item: any, index) => {
            if (item.goods_type == 'virtual') {
                arr.push(item)
            }
        })
    } else if (formData.delivery_type && formData.delivery_type != 'virtual') {
        goodsData.value.forEach((item: any, index) => {
            if (item.goods_type != 'virtual') {
                arr.push(item)
            }
        })
    }
    goodsDataArr.value = cloneDeep(arr)
    if (formData.delivery_type && formData.delivery_type == 'express') {
        formData.delivery_way = 'manual_write'
    } else if (formData.delivery_type && formData.delivery_type == 'local_delivery') {
        formData.delivery_way = 'store_delivery'
    }
}
const handleSelectionChange = (val:any) => {
    formData.order_goods_ids = cloneDeep([])
    formData.delivery_ids = cloneDeep([])
    for (const v in val) {
        formData.order_goods_ids.push(val[v].order_goods_id)
        if (showType.value == 'edit') {
            formData.delivery_ids.push(val[v].delivery_id)
        }
    }
}

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    if (formData.order_goods_ids.length <= 0) {
        ElMessage({
            message: t('orderGoodsPlaceholder'),
            type: 'warning'
        })
        return
    }

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = formData
            orderDelivery(data).then(res => {
                if (formData.delivery_type == 'express' && formData.delivery_way == 'electronic_sheet') {
                    // 打印电子面单
                    printElectronicSheetFn()
                }
                loading.value = false
                showDialog.value = false
                emit('complete')
                initFormData()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const showType = ref('')
const tableRef = ref()
const setFormData = async (row: any = null, type:any) => {
    loading.value = true
    showType.value = type
    if (row) {
        formData.order_id = row.order_id
        formData.delivery_type = ''
        formData.delivery_way = 'manual_write'
        formData.local_delivery_type = 'merchant'
        goodsData.value = row.order_goods
        goodsDataArr.value = row.order_goods
        isHasVirtual.value = false
        await getOrderDeliveryType({
            delivery_type: row.delivery_type
        }).then((data) => {
            deliveryType.value = data.data
            for (const v in data.data) {
                formData.delivery_type = v
                break
            }
            deliveryChange()
        })

        for (let i = 0; i < row.order_goods.length; i++) {
            if (row.order_goods[i].goods_type == 'virtual') {
                isHasVirtual.value = true
                break
            }
        }
        if (isHasVirtual.value) {
            Object.assign(deliveryType.value, { virtual: t('virtualDelivery') })
        }
    }
    loading.value = false
}

const initFormData = () => {
    formData.order_id = 0
    formData.delivery_type = ''
    formData.express_company_id = ''
    formData.electronic_sheet_id = ''
    formData.express_number = ''
    formData.order_goods_ids = []
}

const printElectronicSheetFn = () => {
    const LODOP = getLodop()
    if (!LODOP) return

    printElectronicSheet({
        print_type: 'multiple',
        order_id: formData.order_id,
        electronic_sheet_id: formData.electronic_sheet_id,
        order_goods_ids: formData.order_goods_ids,
        print_trade_type: 'mall_order_print'
    }).then((res: any) => {
        if (res.data) {
            const data = res.data
            let haveSuccessData = false
            LODOP.PRINT_INIT('打印电子面单') // 只能初始化一次

            // 单订单打印
            for (let i = 0; i < data.length; i++) {
                if (data[i].success) {
                    // 调用打印机，开始打印
                    const html = data[i].print_template
                    LODOP.ADD_PRINT_HTM(0, 0, '100%', '100%', html)
                    LODOP.NewPage() // 批量打印，分页
                    haveSuccessData = true
                }
            }

            if (haveSuccessData) {
                LODOP.PREVIEW() // 预览
            }
        }
    })
}

const handleClose = () => {
    showDialog.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
<style lang="scss">
.diy-dialog-wrap .el-form-item__label {
    height: auto !important;
}
</style>
