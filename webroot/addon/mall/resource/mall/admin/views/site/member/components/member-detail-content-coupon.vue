<template>
    <div class="main-container">
        <el-table :data="tableData.data" size="large" v-loading="tableData.loading">
            <template #empty>
                <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
            </template>
            <el-table-column prop="title" :label="t('title')" min-width="50" align="left" />
            <el-table-column prop="price" :label="t('price')" min-width="50" align="left" />
            <el-table-column prop="min_condition_money" :label="t('minConditionMoney')" min-width="50" align="left" />
            <el-table-column prop="create_time" :label="t('createTime')" min-width="50" align="left" />
            <el-table-column prop="expire_time" :label="t('expireTime')" min-width="50" align="left" />
        </el-table>
        <div class="mt-[16px] flex justify-end">
            <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit"
                layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
                @size-change="loadMemberAccountLogList()" @current-change="loadMemberAccountLogList" />
        </div>
    </div>
</template>
<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getShopMemberCoupon } from '@/app/api/marketing_site'

const props = defineProps({
    member_id: {
        type: Number,
        default: 0
    }
})

const tableData = reactive({
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
    tableData.loading = true
    tableData.page = page
    getShopMemberCoupon(props.member_id, {
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
    }).catch(() => {
        tableData.loading = false
    })
}
loadMemberAccountLogList()
</script>
<style lang="scss" scoped></style>