<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">

			<div class="flex justify-between items-center">
				<span class="text-page-title">{{ pageName }}</span>
			</div>
			<el-card class="box-card !border-none base-bg !px-[35px]" shadow="never">
				<el-row class="flex">
					<el-col :span="6" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic :value="settlementInfo.shop_wait_settlement_money ? settlementInfo.shop_wait_settlement_money.toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="footer-item text-[14px] text-[#666]">
									<span>{{ t('shopWaitSettlementMoney') }}</span>
								</div>
							</div>
						</div>
					</el-col>
					<el-col :span="6" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic :value="settlementInfo.mall_wait_settlement_money ? settlementInfo.mall_wait_settlement_money.toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="footer-item text-[14px] text-[#666]">
									<span>{{ t('mallWaitSettlementMoney') }}</span>
								</div>
							</div>
						</div>
					</el-col>
					<el-col :span="6" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic :value="settlementInfo.shop_settlement_money ? settlementInfo.shop_settlement_money.toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="footer-item text-[14px] text-[#666]">
									<span>{{ t('shopSettlementMoney') }}</span>
								</div>
							</div>
						</div>
					</el-col>
					<el-col :span="6" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic :value="settlementInfo.mall_settlement_money ? settlementInfo.mall_settlement_money.toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="footer-item text-[14px] text-[#666]">
									<span>{{ t('mallSettlementMoney') }}</span>
								</div>
							</div>
						</div>
					</el-col>
				</el-row>
			</el-card>
			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" :model="settlementOrderTable.searchParam" ref="searchFormRef">
					<el-form-item :label="t('orderInfo')" prop='keyword'>
						<el-input class="input-item ml-3" v-model="settlementOrderTable.searchParam.keyword" :placeholder="t('keywordPlaceholder')" />
					</el-form-item>
					<el-form-item :label="t('createTime')" prop="create_time">
						<el-date-picker v-model="settlementOrderTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" />
					</el-form-item>
					<el-form-item :label="t('payTime')" prop="pay_time">
						<el-date-picker v-model="settlementOrderTable.searchParam.pay_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" />
					</el-form-item>
					<el-form-item>
						<el-button type="primary" @click="loadSettlementOrderList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="settlementOrderTable.data" size="large" v-loading="settlementOrderTable.loading">
					<template #empty>
						<span>{{ !settlementOrderTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="order_no" :label="t('orderNo')" min-width="120" />
					<el-table-column :show-overflow-tooltip="true" :label="t('shopInfo')" align="left" min-width="200">
						<template #default="{ row }">
							<div class="flex items-center cursor-pointer" @click="toShop(row.site.site_id)">
								<img class="w-[50px] h-[50px] mr-[10px]" v-if="row.site.front_end_logo" :src="img(row.site.front_end_logo)" alt="">
								<img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
								<div class="flex flex flex-col truncate">
									<span>{{ row.site.site_name || '' }}</span>
									<span>{{ row.site.phone || '' }}</span>
								</div>
							</div>
						</template>
					</el-table-column>
					<el-table-column prop="order_money" :label="t('orderMoney')" min-width="120" align="right" />
					<el-table-column prop="shop_money" :label="t('shopMoney')" min-width="120" align="right" />
					<el-table-column prop="mall_money" :label="t('mallMoney')" min-width="120" align="right" />
					<el-table-column :label="t('createTime')" min-width="150" align="center">
						<template #default="{ row }">
							{{ row.create_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('payTime')" min-width="150" align="center">
						<template #default="{ row }">
							{{ row.pay_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('isSettlement')" min-width="150" align="center">
						<template #default="{ row }">
							{{ row.settlement_status_name }}
						</template>
					</el-table-column>
					<el-table-column :label="t('operation')" align="right" fixed="right" min-width="70">
						<template #default="{ row }">
							<el-button type="primary" link @click="detailEvent(row)">{{ t('detail') }}</el-button>
						</template>
					</el-table-column>
				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="settlementOrderTable.page"
						v-model:page-size="settlementOrderTable.limit" layout="total, sizes, prev, pager, next, jumper"
						:total="settlementOrderTable.total" @size-change="loadSettlementOrderList()"
						@current-change="loadSettlementOrderList" />
				</div>
			</div>
		</el-card>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getSettlementOrderList, getSettlementInfo } from '@/addon/mall/api/admin/order'
import type { FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const settlementOrderTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        create_time: [],
        pay_time: []
    }
})

const searchFormRef = ref<FormInstance>()

// 去空操作
watch(() => settlementOrderTable.searchParam.keyword, (nval) => {
    settlementOrderTable.searchParam.keyword = nval.trim()
})

/**
 * 获取站点账单记录列表
 */
const loadSettlementOrderList = (page: number = 1) => {
    settlementOrderTable.loading = true
    settlementOrderTable.page = page

    getSettlementOrderList({
        page: settlementOrderTable.page,
        limit: settlementOrderTable.limit,
        ...settlementOrderTable.searchParam
    }).then(res => {
        settlementOrderTable.loading = false
        settlementOrderTable.data = res.data.data
        settlementOrderTable.total = res.data.total
    }).catch(() => {
        settlementOrderTable.loading = false
    })
}
loadSettlementOrderList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadSettlementOrderList()
}

const settlementInfo = ref({
    shop_wait_settlement_money: 0,
    mall_wait_settlement_money: 0,
    shop_settlement_money: 0,
    mall_settlement_money: 0
})
const setSettlementInfo = () => {
    getSettlementInfo().then(res => {
        settlementInfo.value = res.data
    })
}
setSettlementInfo()

/**
 * 店铺详情
 */
const toShop = (id: number) => {
    router.push(`/admin/shop/info?id=${id}`)
}

/**
 * 订单详情
 */
const detailEvent = (data: any) => {
    router.push('/mall/order/detail?order_id=' + data.order_id)
}

</script>

<style lang="scss" scoped></style>
