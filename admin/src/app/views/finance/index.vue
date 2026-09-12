<template>
	<div class="main-container">
		<el-card class="box-card !border-none platform-check mb-[20px]" shadow="never">
			<template #header>
				<div class="flex justify-between items-start">
					<span class="text-lg font-extrabold mr-[10px]">{{  pageName }}</span>
					<div class="flex items-center">
						<el-form :inline="true" label-width="70px" class="page-form">
							<el-form-item :label="t('时间选择')" prop="date">
								<el-date-picker v-model="date" type="daterange" value-format="YYYY-MM-DD" :start-placeholder="t('startTime')" :end-placeholder="t('endTime')" class="search-input" />
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
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('订单收入总金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[24px] font-bold text-[#000] leading-[28px]">{{ parseFloat(totalStat.order_actual_money).toFixed(2)  }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-01.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
					<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('退款支出金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.order_actual_refund_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-02.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('佣金支出金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.shop_commission).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-03.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('平台手续费') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.mall_service_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-04.png" />
						</div>
					</div>
				</el-col>
			</el-row>
			<el-row :gutter="24" class="mb-[20px]">
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('商家支付分账金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.shop_separate_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-07.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('优惠券金额') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.mall_coupon_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-08.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6" v-if="user_store.checkAddon('supply')">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('供货商货款') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.supply_goods_money).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-09.png" />
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="bg-[#F6F8FF] rounded-[8px] border-[#ECF1FD] border-solid border-[1px] p-[20px]">
						<div class="text-[16px] text-[#000] font-500 pb-[8px] leading-[22px] border-b-[1px] border-solid border-[#ECF1FD] mb-[20px]">{{ t('运费') }}</div>
						<div class="flex justify-between items-center">
							<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-left">{{ parseFloat(totalStat.shipping_fee).toFixed(2) }}</div>
							<img class="w-[40px] h-[40px]" src="@/app/assets/images/finance/icon-10.png" />
						</div>
					</div>
				</el-col>
			</el-row>
		</el-card>
		<el-card class="box-card !border-none" shadow="never">
			<el-tabs v-model="financeTable.searchParam.type" class="goods-tabs" @tab-click="tabHandleClick">
				<el-tab-pane :label="t('日账单')" name="day"></el-tab-pane>
				<el-tab-pane :label="t('月账单')" name="month"></el-tab-pane>
			</el-tabs>
			<el-table :data="financeTable.data" size="large" v-loading="financeTable.loading">
				<template #empty>
					<span>{{ !financeTable.loading ? t('emptyData') : '' }}</span>
				</template>
				<el-table-column type="index" :label="t('序号')" :index="indexMethod"  min-width="50" />
				<el-table-column prop="time" :label="t('日期')" min-width="120" />
				<el-table-column prop="balance_money" :label="financeTable.searchParam.type == 'day' ? t('当日结余'): t('当月结余')" min-width="120" />
				<el-table-column prop="shop_commission" :label="t('佣金')" min-width="120" />
				<el-table-column prop="order_receivable_money" :label="t('订单应收金额')" min-width="120" />
				<el-table-column prop="order_refund_money" :label="t('订单应退金额')" min-width="120" />
				<el-table-column prop="shop_separate_money" :label="t('商家分账')" min-width="120" />
				<el-table-column prop="supply_goods_money" :label="t('供货商货款')" min-width="120" v-if="user_store.checkAddon('supply')" />
				<el-table-column align="right" :label="t('操作')" min-width="120">
					<template #default="{ row }">
						<el-button type="primary" link @click="detailFn(row)">详情</el-button>
					</template>
				</el-table-column>
			</el-table>
			<div class="mt-[16px] flex justify-end">
				<el-pagination v-model:current-page="financeTable.page" v-model:page-size="financeTable.limit"
					layout="total, sizes, prev, pager, next, jumper" :total="financeTable.total"
					@size-change="loadFinanceList()" @current-change="loadFinanceList" />
			</div>
		</el-card>
		<finance-detail ref="financeDetailRef" />
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getFinanceAccountsTotal, getFinanceAccountsList } from '@/app/api/stat'
import financeDetail from '@/app/views/finance/components/finance-detail.vue'
import { useRoute, useRouter } from 'vue-router'
import userStore from '@/stores/modules/user'

const user_store = userStore()
const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const date = ref<any>([])
const totalStat = ref<any>({
    order_actual_money: 0,
    order_actual_refund_money: 0,
    shop_commission: 0,
    mall_service_money: 0,
    recharge_money: 0,
    recharge_pay_money: 0,
    shop_separate_money: 0,
    mall_coupon_money: 0,
    supply_goods_money: 0,
    shipping_fee: 0
})

// 收款统计
const checkAccountsTotal = () => {
    getFinanceAccountsTotal({ date: date.value }).then((res: any) => {
        totalStat.value = res.data
    })
}
checkAccountsTotal()

const indexMethod = (index: number) => {
    return index + 1
}

const financeTable = reactive({
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
    financeTable.loading = true
    financeTable.page = page

    getFinanceAccountsList({
        page: financeTable.page,
        limit: financeTable.limit,
        ...financeTable.searchParam
    }).then(res => {
        financeTable.loading = false
        financeTable.data = res.data.data
        financeTable.total = res.data.total
    }).catch(() => {
        financeTable.loading = false
    })
}
loadFinanceList()
// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    financeTable.searchParam.type = tab.props.name
    loadFinanceList()
}

const getFinanceFn = () => {
    financeTable.searchParam.date = date.value
    checkAccountsTotal()
    loadFinanceList()
}

const financeDetailRef = ref()
const detailFn = (row: any) => {
    financeDetailRef.value.showDialog = true
    financeDetailRef.value.setFormData(row)
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
