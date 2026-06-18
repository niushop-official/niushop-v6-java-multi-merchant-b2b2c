<template>
    <div class="main-container">
     <el-table :data="payListTable.data" size="large" v-loading="payListTable.loading">
					<template #empty>
						<span>{{ !payListTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="out_trade_no" :label="t('outTradeNo')" min-width="120" />
					<el-table-column prop="body" :label="t('body')" min-width="180" show-overflow-tooltip>
                        <template #default="{ row }">
							<div class="multi-hidden">
                                {{ row.body || '-' }}
                            </div>
						</template>
                    </el-table-column>			
					<el-table-column prop="money" :label="t('money')" min-width="120" align="right" />
					<el-table-column prop="type_name" :label="t('typeName')" min-width="120" />
					<el-table-column prop="channel_name" :label="t('channelName')" min-width="120" />
					<el-table-column prop="status_name" :label="t('statusName')" min-width="120" />
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

				</el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="payListTable.page"
                        v-model:page-size="payListTable.limit" layout="total, sizes, prev, pager, next, jumper"
                        :total="payListTable.total" @size-change="loadMemberAccountLogList()"
                        @current-change="loadMemberAccountLogList" />
                </div>
    </div>
</template>
<script lang="ts" setup> 
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getPayList} from '@/app/api/site'
import { img } from '@/utils/common'
const props = defineProps({
    member_id: {
        type: Number,
        default: 0
    }
})

const payListTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keywords: '',
        from_type: '',
        create_time: '',
        mobile: '',
        member_id: props.member_id,
        balance_type: ''
    }
})


/**
 * 获取会员账单表列表
 */
const loadMemberAccountLogList = (page: number = 1) => {
    payListTable.loading = true
    payListTable.page = page
    getPayList({
        page: payListTable.page,
        limit: payListTable.limit,
        ...payListTable.searchParam
    }).then(res => {
        payListTable.loading = false
        payListTable.data = res.data.data
        payListTable.total = res.data.total
    }).catch(() => {
        payListTable.loading = false
    })
}
loadMemberAccountLogList()
</script>
<style lang="scss" scoped>
</style>