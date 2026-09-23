<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form label-width="70px" :inline="true" :model="importFileTableData.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="importFileTableData.searchParam.create_time" type="datetimerange"
                            value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startTime')"
                            :end-placeholder="t('endTime')" class="search-input" />
                    </el-form-item>
                    <el-form-item :label="t('operationType')" prop='type'>
						<el-select v-model="importFileTableData.searchParam.type" clearable class="search-input">
							<el-option  label="全部" value="all" />
							<el-option v-for="(v,k) in deliveryType" :key="k" :label="v" :value="k"></el-option>
						</el-select>
					</el-form-item>
                    <el-form-item :label="t('state')" prop='status'>
						<el-select v-model="importFileTableData.searchParam.status" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(v,k) in deliveryState" :key="k" :label="v" :value="k"></el-option>
						</el-select>
					</el-form-item>
                    <el-form-item :label="t('operator')" prop='pay_type'>
                        <el-select v-model="importFileTableData.searchParam.main_id" clearable class="input-width search-input" filterable>
                            <el-option  label="全部" value="all" />
                            <el-option v-for="item in allUserList" :key="item.uid" :label="item.username" :value="item.uid">
                                <div class="flex items-center">
                                    <el-avatar :src="img(item.head_img)" size="small" class="mr-[10px]" v-if="item.head_img" />
                                    <img src="@/app/assets/images/member_head.png" alt="" class="mr-[10px] w-[24px]" v-else>
                                    {{ item.username }}
                                </div>
                            </el-option>
                        </el-select>
					</el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadInvoiceList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                        <el-button type="primary" @click="importEvent">{{ t('importData') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <!-- 表格数据 -->
            <div>
                <el-table :data="importFileTableData.data" size="large" v-loading="importFileTableData.loading">
                    <template #empty>
                        <span>{{ !importFileTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="type_name" :label="t('operationType')" min-width="100" />
                    <el-table-column :label="t('operator')" min-width="100" >
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <span>{{ row?.user?.username }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="total_num" :label="t('totalNum')" min-width="120" />
                    <el-table-column prop="success_num" :label="t('succeedNum')" min-width="120" />
                    <el-table-column prop="fail_num" :label="t('failuresNum')" min-width="120" />
                    <el-table-column prop="status_name" :label="t('state')" min-width="100" />
                    <el-table-column :label="t('operationTime')" min-width="150">
                        <template #default="{ row }">
                            {{ row.create_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="180">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="examineDownloadFn(row.output)" v-if="row.status == 2 && row.output">{{ t('downloadRecord') }}</el-button>
                            <el-button type="primary" link @click="examineDownloadFn(row.fail_output)" v-if="row.status == 2 && row.fail_output">{{ t('causeFailure') }}</el-button>
                            <el-button type="primary" link @click="checkCauseFn(row)" v-if="row.status == 3">{{ t('checkCause') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="importFileTableData.page"
                        v-model:page-size="importFileTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="importFileTableData.total"
                        @size-change="loadInvoiceList()" @current-change="loadInvoiceList" />
                </div>
                <batch-operation-select ref="batchOperationSelectDialog" @select="handleOperationSelect" />
                <import-data ref="importDataDialog" @complete="loadInvoiceList()" />
                <batch-select-delivery ref="batchSelectDeliveryDialog" @batch-delivery="handleBatchDelivery" />
                <batch-print-delivery-action ref="batchPrintDeliveryDialog" @complete="handlePrintDeliveryConfirm" />

                <!-- 失败原因弹窗 -->
                <el-dialog v-model="causeFailureDialog" :title="t('checkCause1')" width="420px">
                    <div class="break-all -mt-[20px]">
                        {{causeFailureContent}}
                    </div>
                    <template #footer>
                        <span class="dialog-footer">
                            <el-button @click="causeFailureDialog = false">{{ t('confirm') }}</el-button>
                        </span>
                    </template>
                </el-dialog>
            </div>
        </el-card>

    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getOrderBatchDeliveryList, getOrderBatchDeliveryType, getOrderBatchDeliveryState, addBatchOrderDelivery } from '@/app/lang/zh-cn/api/site/order'
import { FormInstance } from 'element-plus'
import { getSiteUserList } from '@/app/api/user'
import importData from '@/addon/mall/views/site/order/components/import-data.vue'
import batchOperationSelect from '@/addon/mall/views/site/order/components/batch-operation-select.vue'
import batchSelectDelivery from '@/addon/mall/views/site/order/components/batch-select-delivery.vue'
import batchPrintDeliveryAction from '@/addon/mall/views/site/order/components/batch-print-delivery-action.vue'
import { useRouter, useRoute } from 'vue-router'
import { img } from '@/utils/common'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const deliveryType = ref({})
const deliveryState = ref([])
const allUserList = ref([])
const setFormData = async () => {
    const typeRes = await getOrderBatchDeliveryType()
    // 将对象格式转换为 { key: name } 格式,用于 el-select
    if (typeRes.data && typeof typeRes.data === 'object') {
        const typeObj: Record<string, string> = {}
        Object.keys(typeRes.data).forEach(key => {
            typeObj[key] = typeRes.data[key].name
        })
        deliveryType.value = typeObj
    }
    deliveryState.value = await (await getOrderBatchDeliveryState()).data
    allUserList.value = await (await getSiteUserList()).data
}
setFormData()

const importFileTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        create_time: '',
        main_id: '',
        type: '',
        status: ''
    }
})

const searchFormRef = ref<FormInstance>()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    importFileTableData.searchParam.main_id = ''
    loadInvoiceList()
}

/**
 * 获取批发列表数据
 */
const loadInvoiceList = (page: number = 1) => {
    importFileTableData.loading = true
    importFileTableData.page = page
    getOrderBatchDeliveryList({
        page: importFileTableData.page,
        limit: importFileTableData.limit,
        ...importFileTableData.searchParam
    }).then(res => {
        importFileTableData.loading = false
        importFileTableData.data = res.data.data
        importFileTableData.total = res.data.total
    }).catch(() => {
        importFileTableData.loading = false
    })
}
loadInvoiceList()

// 下载
const examineDownloadFn = (path:any) => {
    let url = `${import.meta.env.VITE_IMG_DOMAIN || location.origin}/${path}`
    window.open(url)
}

/**
 * 批量操作选择
 */
const batchOperationSelectDialog: Record<string, any> | null = ref(null)
const importDataDialog: Record<string, any> | null = ref(null)
const batchSelectDeliveryDialog: Record<string, any> | null = ref(null)
const currentOperationType = ref('')

// 点击导入数据按钮,先打开操作选择弹窗
const importEvent = (data: any) => {
    batchOperationSelectDialog.value.open()
}

// 处理用户选择的操作类型
const handleOperationSelect = (type: string) => {
    currentOperationType.value = type
    switch (type) {
        case 'print_delivery':
            // 打开批量选择发货抽屉
            batchSelectDeliveryDialog.value.open(currentOperationType.value)
            break
        case 'delivery':
            // 打开批量导入发货弹窗
            importDataDialog.value.open(currentOperationType.value)
            break
        case 'select_delivery':
            // 打开批量选择发货抽屉
            batchSelectDeliveryDialog.value.open(currentOperationType.value)
            break
    }
}

// ... existing code ...

const batchPrintDeliveryDialog: Record<string, any> | null = ref(null)
const selectedGoodsForPrint = ref<any[]>([])

// 处理批量发货
const handleBatchDelivery = (goods: any[]) => {
    if (currentOperationType.value === 'print_delivery') {
        selectedGoodsForPrint.value = goods
        batchPrintDeliveryDialog.value.open({
            list: goods,
            type: currentOperationType.value
        })
        return false
    }
    if (currentOperationType.value === 'select_delivery') {
        loadInvoiceList()
    }
}

// 处理打单发货确认
const handlePrintDeliveryConfirm = () => {
    loadInvoiceList()
}

const causeFailureDialog = ref(false)
const causeFailureContent = ref('') //失败原因内容
// 查看失败原因
const checkCauseFn = (data: any) => {
    causeFailureContent.value = data.fail_remark
    causeFailureDialog.value = true
}
</script>

<style lang="scss" scoped></style>
