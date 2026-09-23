<template>
    <div class="main-container">
        <el-table :data="tableData.data" size="large" v-loading="tableData.loading">
            <template #empty>
                <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
            </template>
            <el-table-column prop="order_no" :label="t('订单编号')" min-width="50" align="left" />
            <el-table-column prop="goods_money" :label="t('商品总价')" min-width="50" align="left" />
            <el-table-column prop="order_money" :label="t('实付金额')" min-width="50" align="left" />
            <el-table-column prop="pay_time" :label="t('交易时间')" min-width="50" align="left" />
            <el-table-column prop="expire_time" :label="t('买家/收货人')" min-width="50" align="left" >
                <template #default="{row}">
                    <div class="">
                        <div>{{ row.taker_name }} <span class="ml-1">{{ row.taker_mobile }}</span> </div>
                        <div>{{ row.taker_full_address }}</div>
                    </div>
                </template>
            </el-table-column>
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
import { getShopMemberConsumeList } from  '@/app/api/shop_site'

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
        member_id: props.member_id,
    }
})


/**
 * 获取会员账单表列表
 */
const loadMemberAccountLogList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page
    getShopMemberConsumeList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam,
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