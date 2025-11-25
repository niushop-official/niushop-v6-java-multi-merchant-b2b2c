<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">

			<div class="flex justify-between items-center">
				<span class="text-page-title">{{ pageName }}</span>
			</div>
			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" :model="separAccountTable.searchParam" ref="searchFormRef">
					<el-form-item :label="t('tradeNo')" prop="trade_no">
						<el-input v-model="separAccountTable.searchParam.trade_no" :placeholder="t('tradeNoPlaceholder')" />
					</el-form-item>
					<el-form-item :label="t('status')" prop="status">
                        <el-select v-model="separAccountTable.searchParam.status" :placeholder="t('statusPlaceholder')" clearable>
                            <el-option v-for="(item,index) in statusList" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
					<el-form-item :label="t('createTime')" prop="create_time">
						<el-date-picker v-model="separAccountTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" />
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="loadSeparAccountList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="separAccountTable.data" size="large" v-loading="separAccountTable.loading">
					<template #empty>
						<span>{{ !separAccountTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="trade_no" :label="t('tradeNo')" min-width="150" />
					<el-table-column prop="site_name" :label="t('siteName')" min-width="100" />
					<el-table-column prop="trade_money" :label="t('tradeMoney')" min-width="80" />
					<el-table-column prop="trade_type_name" :label="t('tradeTypeName')" min-width="80" />
					<el-table-column  :label="t('账单数据')" min-width="100">
						<template #default="{ row }">
							<div v-for="(item,index) in row.separ_data" :key="index">{{ item.money_type_name }}: {{ item.value }}</div>
						</template>
					</el-table-column>
					<el-table-column  :label="t('status')" min-width="100">
						<template #default="{ row }">
							<div>{{ row.status_name }}</div>
							<div v-if="row.status == 'fail'" class="text-[#FF1500]">失败原因：{{  row.fail_reason }}</div>
						</template>
					</el-table-column>

					<el-table-column :label="t('separTime')" min-width="120" align="center">
						<template #default="{ row }">
							{{ row.separ_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('createTime')" min-width="120" align="center">
						<template #default="{ row }">
							{{ row.create_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('operate')" min-width="120" align="right" fixed="right">
						<template #default="{ row }">
							<el-button type="primary" link @click="detailEvent(row.id)">{{ t('查看明细') }}</el-button>
						</template>
					</el-table-column>
				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="separAccountTable.page"
						v-model:page-size="separAccountTable.limit" layout="total, sizes, prev, pager, next, jumper"
						:total="separAccountTable.total" @size-change="loadSeparAccountList()"
						@current-change="loadSeparAccountList" />
				</div>
			</div>
		</el-card>
		<account-detail ref="accountDetailRef" />
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { getSiteSpearList, getSpearStatus } from '@/app/api/separ'
import { type FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'
import accountDetail from './components/account-detail.vue'

const route = useRoute()
const pageName = route.meta.title

const statusList = ref([])

const separAccountTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        trade_no: '',
        status: '',
        create_time: ''
    }
})

const searchFormRef = ref<FormInstance>()

// 去空操作
watch(() => separAccountTable.searchParam.trade_no, (nval) => {
    separAccountTable.searchParam.trade_no = nval.trim()
})

const init = () => {
    getSpearStatus().then(res => {
        statusList.value = res.data
    })
}
init()
/**
 * 获取站点账单记录列表
 */
const loadSeparAccountList = (page: number = 1) => {
    separAccountTable.loading = true
    separAccountTable.page = page

    getSiteSpearList({
        page: separAccountTable.page,
        limit: separAccountTable.limit,
        ...separAccountTable.searchParam
    }).then(res => {
        separAccountTable.loading = false
        separAccountTable.data = res.data.data
        separAccountTable.total = res.data.total
    }).catch(() => {
        separAccountTable.loading = false
    })
}
loadSeparAccountList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadSeparAccountList()
}

const accountDetailRef = ref()
const detailEvent = (id: any) => {
    accountDetailRef.value.open(id)
}

</script>

<style lang="scss" scoped></style>
