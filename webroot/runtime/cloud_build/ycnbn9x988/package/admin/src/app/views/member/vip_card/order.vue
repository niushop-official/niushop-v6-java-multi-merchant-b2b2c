<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="orderTableData.searchParam" ref="searchFormRef">
                    <el-form-item label="订单信息" prop='search_name'>
						<el-select v-model="orderTableData.searchParam.search_type" clearable class="input-item">
							<el-option label="订单编号" value="order_no"></el-option>
							<el-option label="交易号" value="out_trade_no"></el-option>
                            <el-option label="会员卡名称" value="card_name"></el-option>
						</el-select>
						<el-input class="input-item ml-3" v-model="orderTableData.searchParam.search_name" />
					</el-form-item>
                    <el-form-item label="支付类型" prop='pay_type'>
						<el-select v-model="orderTableData.searchParam.pay_type" clearable >
							<el-option  label="全部" value="all" />


							<el-option v-for="(item, index) in payTypeData" :key="index" :label="item.name" :value="item.key"></el-option>
						</el-select>
					</el-form-item>
                    <el-form-item label="订单来源" prop='from_type'>
						<el-select v-model="orderTableData.searchParam.order_from" clearable >
							<el-option  label="全部" value="all" />


							<el-option v-for="(item, index) in orderFromData" :key="index" :label="item" :value="index"></el-option>
						</el-select>
					</el-form-item>
                    <el-form-item label="创建时间" prop="create_time">
						<el-date-picker v-model="orderTableData.searchParam.create_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间"
							end-placeholder="结束时间" class="search-input" />
					</el-form-item>
					<el-form-item label="支付时间" prop="pay_time">
						<el-date-picker v-model="orderTableData.searchParam.pay_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间"
							end-placeholder="结束时间" class="search-input" />
					</el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadOrderList()">搜索</el-button>
                        <el-button @click="resetForm(searchFormRef)">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-table :data="orderTableData.data" size="large" v-loading="orderTableData.loading" ref="memberTableRef">

                    <template #empty>
                        <span>{{ !orderTableData.loading ? '暂无数据' : '' }}</span>
                    </template>
                    <el-table-column prop="order_no" label="订单号" min-width="160" />
                    <el-table-column label="会员信息" min-width="120">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-if="row.member.headimg" :src="img(row.member.headimg)" alt="">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                                <div class="flex flex flex-col">
                                    <span>{{ row.member.nickname || '' }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="body" label="会员卡名称" min-width="120" />
                    <el-table-column prop="order_money" label="支付金额" min-width="120" />
                    <el-table-column prop="pay_type_name" label="支付类型" min-width="120" />
                    <el-table-column prop="status_name" label="订单状态" min-width="120" />
                    <el-table-column prop="create_time" label="下单时间" min-width="120" />
                    <el-table-column prop="pay_time" label="支付时间" min-width="120" />
                    <el-table-column label="操作" align="right" fixed="right" min-width="100">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row)">详情</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="orderTableData.page" v-model:page-size="orderTableData.limit"
                            layout="total, sizes, prev, pager, next, jumper" :total="orderTableData.total"
                            @size-change="loadOrderList()" @current-change="loadOrderList" />
                    </div>
            </div>
        </el-card>
    </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'
import { img, setTablePageStorage, getTablePageStorage } from '@/utils/common'
import { getOrderList } from '@/app/api/vip_card'
import { getOrderPayType, getOrderFrom } from '@/app/api/sys'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const orderTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        search_type: '',
        search_name: '',
        pay_type: '',
        order_from: '',
        create_time: [],
        pay_time: []
    }
})
const searchFormRef = ref<FormInstance>()
const payTypeData = ref<any[]>([])
const orderFromData = ref([])
const setFormData = () => {
    getOrderPayType().then((res: any) => {
        payTypeData.value = res.data
    })
    getOrderFrom().then((res: any) => {
        orderFromData.value = res.data
    })
}
setFormData()

// 获取列表
const loadOrderList = (page: number = 1) => {
    orderTableData.loading = true
    orderTableData.page = page

    getOrderList({
        page: orderTableData.page,
        limit: orderTableData.limit,
        ...orderTableData.searchParam
    }).then((res: any) => {
        orderTableData.loading = false
        orderTableData.data = res.data.data
        orderTableData.total = res.data.total
        setTablePageStorage(orderTableData.page, orderTableData.limit, orderTableData.searchParam)
    }).catch(() => {
        orderTableData.loading = false
    })
}
loadOrderList(getTablePageStorage(orderTableData.searchParam).page)

const detailEvent = (row: any) => {
    router.push({
        path: '/member/vip_card/order/detail',
        query: {
            order_id: row.order_id
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadOrderList(getTablePageStorage(orderTableData.searchParam).page)
}
</script>
<style lang="scss" scoped>
.input-item {
	width: 150px !important;
}

</style>
