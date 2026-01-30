<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="shippingTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('tradeNo')" prop="trade_no">
                        <el-input v-model="shippingTableData.searchParam.trade_no" :placeholder="t('tradeNoPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('status')" prop="status">
                        <el-select v-model="shippingTableData.searchParam.status" clearable :placeholder="t('statusPlaceholder')" class="input-width">
                            <el-option :label="t('all')" value="" />
                            <el-option :label="t('success')" :value="1" />
                            <el-option :label="t('fail')" :value="0" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('uploadTime')" prop="upload_time">
						<el-date-picker v-model="shippingTableData.searchParam.upload_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
							:end-placeholder="t('endDate')" />
					</el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadShippingList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div class="mt-[10px]">
                <el-table :data="shippingTableData.data" size="large" v-loading="shippingTableData.loading">
                    <template #empty>
                        <span>{{ !shippingTableData.loading ? t('emptyData') : '' }}</span>
                    </template>

                    <el-table-column prop="trade_no" :show-overflow-tooltip="true" :label="t('tradeNo')" min-width="140" />
                    <el-table-column prop="transaction_id" :show-overflow-tooltip="true"  :label="t('transactionId')" min-width="140" />
                    <el-table-column prop="logistics_type_name" :label="t('logisticsTypeName')" min-width="120" />
                    <el-table-column prop="delivery_mode_name" :label="t('deliveryModeName')" min-width="120" />

                    <el-table-column prop="is_all_delivered" :label="t('isAllDelivered')" min-width="120">
                        <template #default="{ row }">
                           {{ row.is_all_delivered ? t('yes') : t('no') }}
                        </template>
                    </el-table-column>

                   <el-table-column prop="status" :label="t('status')" min-width="140">
                        <template #default="{ row }">
                          <div>
                                <div>{{ row.status == 1 ? t('success') : t('fail') }}</div>
                                <div v-if="row.status == 0">{{ t('failReason') }} {{ row.errmsg }}</div>
                          </div>
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('uploadTime')" min-width="140" align="center">
                        <template #default="{ row }">
                            {{ row.upload_time || '' }}
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button v-if="row.status == 0" type="primary" link @click="updateEvent(row)">{{ t('again') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="shippingTableData.page" v-model:page-size="shippingTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="shippingTableData.total"
                        @size-change="loadShippingList()" @current-change="loadShippingList" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getShippingList, updateShippingRetry } from '@/app/api/delivery_weapp'
import { useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'

const route = useRoute()
const pageName = route.meta.title

const shippingTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        trade_no: '',
        status: '',
        upload_time: []
    }
})
const searchFormRef = ref<FormInstance>()
/**
 * 获取小程序发货列表
 */
const loadShippingList = (page: number = 1) => {
    shippingTableData.loading = true
    shippingTableData.page = page

    getShippingList({
        page: shippingTableData.page,
        limit: shippingTableData.limit,
        ...shippingTableData.searchParam
    }).then((res: any) => {
        shippingTableData.loading = false
        shippingTableData.data = res.data.data
        shippingTableData.total = res.data.total
    }).catch(() => {
        shippingTableData.loading = false
    })
}
loadShippingList()

const updateEvent = (data: any) => {
    updateShippingRetry({
        trade_id: data.trade_id,
        trade_type: data.trade_type
    }).then((res: any) => {
        loadShippingList()
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadShippingList()
}
</script>

<style scoped>

</style>