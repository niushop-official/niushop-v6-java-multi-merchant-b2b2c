<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">

			<div class="flex justify-between items-center">
				<span class="text-page-title">{{ pageName }}</span>
			</div>
			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" label-width="70px" :model="siteAccountLogTable.searchParam" ref="searchFormRef" class="page-form">
					<el-form-item :label="t('tradeNo')" prop="trade_no">
						<el-input v-model="siteAccountLogTable.searchParam.trade_no" :placeholder="t('tradeNoPlaceholder')" clearable  class="search-input" />
					</el-form-item>
					<el-form-item :label="t('createTime')" prop="create_time">
						<el-date-picker v-model="siteAccountLogTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" class="search-input" />
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="loadSiteAccountLogList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
						<el-button type="primary" @click="exportEvent">{{ t('export') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="siteAccountLogTable.data" size="large" v-loading="siteAccountLogTable.loading">
					<template #empty>
						<span>{{ !siteAccountLogTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="trade_no" :label="t('tradeNo')" min-width="120" />
					<el-table-column prop="main_name" :label="t('mainName')" min-width="100" />
					<el-table-column prop="trade_type_name" :label="t('tradeType')" min-width="100" />
					<el-table-column prop="type_name" :label="t('type')" min-width="100" />
					<el-table-column prop="value" :label="t('money')" min-width="80" align="right" />
					<el-table-column :label="t('createTime')" min-width="120" align="center">
						<template #default="{ row }">
							{{ row.create_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('remark')" min-width="120" align="center">
						<template #default="{ row }">
							{{ row.memo }}
						</template>
					</el-table-column>
				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="siteAccountLogTable.page"
						v-model:page-size="siteAccountLogTable.limit" layout="total, sizes, prev, pager, next, jumper"
						:total="siteAccountLogTable.total" @size-change="loadSiteAccountLogList()"
						@current-change="loadSiteAccountLogList" />
				</div>
			</div>
		</el-card>
		<export-sure ref="exportSureDialog" :show="flag" type="site_finance_log" isSite="true" :searchParam="siteAccountLogTable.searchParam" @close="handleClose" />
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getFinanceList } from '@/app/api/site'
import type { FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const siteAccountLogTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        site_id: '',
        type: '',
        money: '',
        trade_no: '',
        create_time: ''
    }
})

const searchFormRef = ref<FormInstance>()

// 去空操作
watch(() => siteAccountLogTable.searchParam.trade_no, (nval) => {
    siteAccountLogTable.searchParam.trade_no = nval.trim();
})

/**
 * 获取站点账单记录列表
 */
const loadSiteAccountLogList = (page: number = 1) => {
    siteAccountLogTable.loading = true
    siteAccountLogTable.page = page

    getFinanceList({
        page: siteAccountLogTable.page,
        limit: siteAccountLogTable.limit,
        ...siteAccountLogTable.searchParam
    }).then(res => {
        siteAccountLogTable.loading = false
        siteAccountLogTable.data = res.data.data
        siteAccountLogTable.total = res.data.total
    }).catch(() => {
        siteAccountLogTable.loading = false
    })
}
loadSiteAccountLogList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadSiteAccountLogList()
}

/**
 * 财务流水导出
 */
const exportSureDialog = ref(null)
const flag = ref(false)
const handleClose = (val) => {
    flag.value = val
}
const exportEvent = (data: any) => {
    flag.value = true
}
</script>

<style lang="scss" scoped></style>
