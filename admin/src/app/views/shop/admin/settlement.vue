<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none base-bg !px-[35px]" shadow="never">
                <el-row class="flex">
                    <el-col :span="8">
                        <div class="statistic-card">
                            <el-statistic value=""></el-statistic>
                            <div class="statistic-footer">
                                <div class="text-[14px] text-[#666] flex items-center">
                                    <span>{{ t('settleAccount') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('settleTips')" placement="bottom" popper-class="w-[250px]">
                                        <el-icon><QuestionFilled /></el-icon>
                                    </el-tooltip>
                                </div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="statistic-card">
                            <el-statistic value=""></el-statistic>
                            <div class="statistic-footer">
                                <div class="text-[14px] text-[#666]">
                                    <span>{{ t('platformAllCommission') }}</span>
                                </div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="statistic-card">
                            <el-statistic value=""></el-statistic>
                            <div class="statistic-footer">
                                <div class="text-[14px] text-[#666]">
                                    <span>{{ t('siteAllCommission') }}</span>
                                </div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </el-card>
            <el-card class="box-card !border-none mb-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="orderTableData.searchParam" ref="searchFormRef">
                        <el-form-item :label="t('payment')" prop="create_time">
                            <el-date-picker v-model="orderTableData.searchParam.create_time" type="datetimerange"
                                value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                                :end-placeholder="t('endDate')" />
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="loadSettleList()">{{ t('search') }}</el-button>
                            <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                        </el-form-item>
                </el-form>
                <div class="mt-[10px]">
                    <el-table :data="orderTableData.data" size="large" v-loading="orderTableData.loading">
                        <template #empty>
                            <span>{{ !orderTableData.loading ? t('emptyData') : '' }}</span>
                        </template>

                        <el-table-column prop="order_no"  :label="t('checkNo')" min-width="140"/>

                        <el-table-column prop="apply_money" :label="t('shopNum')" min-width="140"  />

                        <el-table-column prop="money" :label="t('orderMoney')" min-width="140"  />

                        <el-table-column prop="service_money" :label="t('storeSettleMoney')"  min-width="140" />

                        <el-table-column prop="status_name" :label="t('platformCommission')"  min-width="140" />

                        <el-table-column prop="status_name" :label="t('siteCommission')"  min-width="140" />

                        <el-table-column :label="t('accountStartTime')" min-width="140">
                            <template #default="{ row }">
                                {{ row.create_time || '' }}
                            </template>
                        </el-table-column>

                        <el-table-column :label="t('accountEndTime')" min-width="140">
                            <template #default="{ row }">
                                {{ row.audit_time || '' }}
                            </template>
                        </el-table-column>

                        <el-table-column :label="t('settlementTime')" min-width="140">
                            <template #default="{ row }">
                                {{ row.transfer_time || '' }}
                            </template>
                        </el-table-column>

                        <el-table-column :label="t('operation')" align="right" fixed="right" width="230">
                            <template #default>
                                <el-button type="primary" link>{{ item }}</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                    <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="orderTableData.page" v-model:page-size="orderTableData.limit"
                            layout="total, sizes, prev, pager, next, jumper" :total="orderTableData.total"
                            @size-change="loadSettleList()" @current-change="loadSettleList" />
                    </div>
                </div>

            </el-card>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getCashOutList, getCashOutStat } from '@/app/api/member'
import { FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const orderTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        create_time: []
    }
})

const statistics = ref({
    transfered: 0,
    cash_outing: 0
})
const checkStatInfo = () => {
    getCashOutStat().then(res => {
        statistics.value = res.data
    })
}
checkStatInfo()

/**
 * 获取提现列表
 */
const loadSettleList = (page: number = 1) => {
    orderTableData.loading = true
    orderTableData.page = page

    getCashOutList({
        page: orderTableData.page,
        limit: orderTableData.limit,
        ...orderTableData.searchParam
    }).then(res => {
        orderTableData.loading = false
        orderTableData.data = res.data.data
        orderTableData.total = res.data.total
    }).catch(() => {
        orderTableData.loading = false
    })
}
loadSettleList()

// 重置
const searchFormRef = ref<FormInstance>()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadSettleList()
}
</script>

<style lang="scss" scoped></style>
