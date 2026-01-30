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
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('订单总金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[24px] font-bold text-[#000] leading-[28px]">{{ parseFloat(totalStat.order_money).toFixed(2)  }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/point-01.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('订单总退款金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.order_refund_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/point-02.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
					<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('订单总收入') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.order_income_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/point-03.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
					<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('总订单数') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.order_num).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/point-04.png" />
						</div>
					</div>
				</el-col>
			</el-row>
		</el-card>
		<el-card class="box-card !border-none" shadow="never">
			<el-tabs v-model="pointMallTable.searchParam.type" class="goods-tabs" @tab-click="tabHandleClick">
				<el-tab-pane :label="t('日账单')" name="day"></el-tab-pane>
				<el-tab-pane :label="t('月账单')" name="month"></el-tab-pane>
			</el-tabs>
			<el-table :data="pointMallTable.data" size="large" v-loading="pointMallTable.loading">
				<template #empty>
					<span>{{ !pointMallTable.loading ? t('emptyData') : '' }}</span>
				</template>
				<el-table-column type="index" :label="t('序号')" :index="indexMethod"  min-width="50" />
				<el-table-column prop="time" :label="t('日期')" min-width="120" />
				<el-table-column prop="order_money" :label="t('订单总金额')" min-width="120" />
				<el-table-column prop="order_refund_money" :label="t('订单总退款金额')" min-width="120" />
				<el-table-column prop="order_income_money" :label="t('订单总收入')" min-width="120" />
				<el-table-column prop="order_num" :label="t('总订单数')" min-width="120" />
			</el-table>
			<div class="mt-[16px] flex justify-end">
				<el-pagination v-model:current-page="pointMallTable.page" v-model:page-size="pointMallTable.limit"
					layout="total, sizes, prev, pager, next, jumper" :total="pointMallTable.total"
					@size-change="loadFinanceList()" @current-change="loadFinanceList" />
			</div>
		</el-card>

	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getPointsMallAccountsTotal, getPointsMallAccountsList } from '@/addon/points_mall/api/admin/bill'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const date = ref<any>([])
const totalStat = ref<any>({
    order_money: 0,
    order_refund_money: 0,
    order_income_money: 0,
    order_num: 0
})

// 收款统计
const checkAccountsTotal = () => {
    getPointsMallAccountsTotal({ date: date.value }).then((res: any) => {
        totalStat.value = res.data
    })
}
checkAccountsTotal()

const indexMethod = (index: number) => {
    return index + 1
}

const pointMallTable = reactive({
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
    pointMallTable.loading = true
    pointMallTable.page = page

    getPointsMallAccountsList({
        page: pointMallTable.page,
        limit: pointMallTable.limit,
        ...pointMallTable.searchParam
    }).then(res => {
        pointMallTable.loading = false
        pointMallTable.data = res.data.data
        pointMallTable.total = res.data.total
    }).catch(() => {
        pointMallTable.loading = false
    })
}
loadFinanceList()
// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    pointMallTable.searchParam.type = tab.props.name
    loadFinanceList()
}

const getFinanceFn = () => {
    pointMallTable.searchParam.date = date.value
    checkAccountsTotal()
    loadFinanceList()
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
