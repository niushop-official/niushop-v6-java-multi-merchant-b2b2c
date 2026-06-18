<template>
    <div class="main-container">
        <el-table :data="memberAccountLogTableData.data" size="large" v-loading="memberAccountLogTableData.loading">

                    <template #empty>
                        <span>{{ !memberAccountLogTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="account_data" :label="t('accountData')" min-width="100" align="left">
                        <template #default="{ row }">
                            <div class="whitespace-nowrap">
                                <span v-if="row.account_data >= 0">+{{ row.account_data }}</span>
                                <span v-else>{{ row.account_data }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="account_sum" :label="t('剩余金额')" min-width="110" align="left" />
                    <el-table-column prop="account_type_name" :label="t('balanceType')" min-width="150" align="center" />
                    <el-table-column prop="from_type_name" :label="t('fromType')" min-width="120" align="" />
                    <el-table-column prop="create_time" :show-overflow-tooltip="true" :label="t('createTime')" min-width="150" />

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="memberAccountLogTableData.page"
                        v-model:page-size="memberAccountLogTableData.limit" layout="total, sizes, prev, pager, next, jumper"
                        :total="memberAccountLogTableData.total" @size-change="loadMemberAccountLogList()"
                        @current-change="loadMemberAccountLogList" />
                </div>
    </div>
</template>
<script lang="ts" setup> 
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getBalanceList} from '@/app/api/member'
import { img } from '@/utils/common'

const props = defineProps({
    member_id: {
        type: Number,
        default: 0
    }
})

const memberAccountLogTableData = reactive({
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
    memberAccountLogTableData.loading = true
    memberAccountLogTableData.page = page
    getBalanceList({
        page: memberAccountLogTableData.page,
        limit: memberAccountLogTableData.limit,
        ...memberAccountLogTableData.searchParam
    }).then(res => {
        memberAccountLogTableData.loading = false
        memberAccountLogTableData.data = res.data.data
        memberAccountLogTableData.total = res.data.total
    }).catch(() => {
        memberAccountLogTableData.loading = false
    })
}
loadMemberAccountLogList()
</script>
<style lang="scss" scoped>
</style>