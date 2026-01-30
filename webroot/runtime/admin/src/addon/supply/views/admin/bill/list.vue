<template>
	<div class="main-container">
		<el-card class="box-card !border-none platform-check mb-[20px]" shadow="never">
			<template #header>
				<div class="flex justify-between items-start">
					<span class="text-lg font-extrabold mr-[10px]">{{  pageName }}</span>
					<div class="flex items-center">
						<el-form :inline="true">
							<el-form-item :label="t('时间选择')" prop="date">
								<el-date-picker v-model="date" type="daterange" value-format="YYYY-MM-DD" :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"  />
							</el-form-item>
							<el-form-item>
                                <el-button type="primary"  @click="getFinanceFn()">{{ t('search') }}</el-button>
                            </el-form-item>
						</el-form>
					</div>
				</div>
			</template>
			<el-row :gutter="24" class="mb-[20px]">
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('供货商收入') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[24px] font-bold text-[#000] leading-[28px]">{{ parseFloat(totalStat.order_actual_money).toFixed(2)  }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/site/icon-01.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('退款支出') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.order_refund_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/site/icon-04.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
					<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('平台手续费') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.service_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/site/icon-06.png" />
						</div>
					</div>
				</el-col>
			</el-row>
		</el-card>
		<el-card class="box-card !border-none" shadow="never">
			<el-tabs v-model="supplyTable.searchParam.type" class="goods-tabs" @tab-click="tabHandleClick">
				<el-tab-pane :label="t('日账单')" name="day"></el-tab-pane>
				<el-tab-pane :label="t('月账单')" name="month"></el-tab-pane>
			</el-tabs>
			<el-table :data="supplyTable.data" size="large" v-loading="supplyTable.loading">
				<template #empty>
					<span>{{ !supplyTable.loading ? t('emptyData') : '' }}</span>
				</template>
				<el-table-column type="index" :label="t('序号')" :index="indexMethod"  min-width="50" />
				<el-table-column prop="time" :label="t('日期')" min-width="120" />
				<el-table-column prop="balance_money" :label="supplyTable.searchParam.type == 'day' ? t('当日结余'): t('当月结余')"  min-width="120" />
				<el-table-column prop="order_receivable_money" :label="t('订单应收金额')" min-width="120" />
				<el-table-column prop="order_refund_money" :label="t('订单应退金额')" min-width="120" />
				<el-table-column prop="service_money" :label="t('平台手续费')" min-width="120" />
				<el-table-column align="right" :label="t('操作')" min-width="120">
					<template #default="{ row }">
						<el-button type="primary" link @click="detailFn(row)">详情</el-button>
					</template>
				</el-table-column>
			</el-table>
			<div class="mt-[16px] flex justify-end">
				<el-pagination v-model:current-page="supplyTable.page" v-model:page-size="supplyTable.limit"
					layout="total, sizes, prev, pager, next, jumper" :total="supplyTable.total"
					@size-change="loadFinanceList()" @current-change="loadFinanceList" />
			</div>
		</el-card>
		<bill-detail ref="billDetailRef" />
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getSupplyAccountsTotal, getSupplyAccountsList } from '@/addon/supply/api/admin/bill'
import billDetail from '@/addon/supply/views/admin/bill/components/bill-detail.vue'
import { useRoute, useRouter } from 'vue-router'
import userStore from '@/stores/modules/user'

const user_store = userStore()
const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const date = ref<any>([])
const totalStat = ref<any>({
    order_actual_money: 0,
    order_refund_money: 0,
    service_money: 0
})

// 收款统计
const checkAccountsTotal = () => {
    getSupplyAccountsTotal({ date: date.value }).then((res: any) => {
        totalStat.value = res.data
    })
}
checkAccountsTotal()

const indexMethod = (index: number) => {
    return index + 1
}

const supplyTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        type: 'day',
        date: []
    }
})

/**
 * 获取订单列表
 */
const loadFinanceList = (page: number = 1) => {
    supplyTable.loading = true
    supplyTable.page = page

    getSupplyAccountsList({
        page: supplyTable.page,
        limit: supplyTable.limit,
        ...supplyTable.searchParam
    }).then(res => {
        supplyTable.loading = false
        supplyTable.data = res.data.data
        supplyTable.total = res.data.total
    }).catch(() => {
        supplyTable.loading = false
    })
}
loadFinanceList()
// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    supplyTable.searchParam.type = tab.props.name
    loadFinanceList()
}

const getFinanceFn = () => {
    supplyTable.searchParam.date = date.value
    checkAccountsTotal()
    loadFinanceList()
}

const billDetailRef = ref()
const detailFn = (row: any) => {
    billDetailRef.value.showDialog = true
    billDetailRef.value.setFormData(row)
}
</script>

<style lang="scss" scoped>
:deep(.platform-check .el-card__header){
    border-bottom: none;
    padding-bottom: 0;
}
html.dark{
	.el-col > div{
		background: var(--el-bg-color-overlay);
		div{
			color: #fff;
		}
	}
}
</style>
