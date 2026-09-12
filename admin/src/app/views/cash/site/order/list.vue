<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" label-width="70px"  :model="cashTableData.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item label="订单信息" prop='search_name'>
						<el-input class="search-input" v-model="cashTableData.searchParam.search_name" clearable>
                            <template #prepend>
                                <el-select v-model="cashTableData.searchParam.search_type" clearable class="!w-[120px]">
                                    <el-option label="订单编号" value="order_no"></el-option>
                                    <el-option label="交易号" value="out_trade_no"></el-option>
                                </el-select>
                            </template>
                        </el-input>
					</el-form-item>
                    <el-form-item label="支付类型" prop='pay_type'>
						<el-select v-model="cashTableData.searchParam.pay_type" clearable class="search-input">
							<el-option  label="全部" value="all" />
							<el-option v-for="(item, index) in payTypeData" :key="index" :label="item.name" :value="item.key"></el-option>
						</el-select>
					</el-form-item>
                    <el-form-item label="订单来源" prop='from_type'>
						<el-select v-model="cashTableData.searchParam.order_from" clearable class="search-input">
							<el-option  label="全部" value="all" />
							<el-option v-for="(item, index) in orderFromData" :key="index" :label="item" :value="index"></el-option>
						</el-select>
					</el-form-item>
                    <el-form-item label="创建时间" prop="create_time">
						<el-date-picker v-model="cashTableData.searchParam.create_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间"
							end-placeholder="结束时间" class="search-input" />
					</el-form-item>
					<el-form-item label="支付时间" prop="pay_time">
						<el-date-picker v-model="cashTableData.searchParam.pay_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间"
							end-placeholder="结束时间" class="search-input" />
					</el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadCashSiteList()">搜索</el-button>
                        <el-button @click="resetForm(searchFormRef)">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
                    <el-tab-pane label="全部" name=""></el-tab-pane>
                    <el-tab-pane :label="item.name" :name="item.status" v-for="(item,index) in statusData" :key="index"></el-tab-pane>
                </el-tabs>
                <el-table :data="cashTableData.data" size="large" v-loading="cashTableData.loading" ref="memberTableRef">

                    <template #empty>
                        <span>{{ !cashTableData.loading ? '暂无数据' : '' }}</span>
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
                    <el-table-column prop="site_name" label="店铺名称" min-width="120" />
                    <el-table-column prop="order_money" label="支付金额" min-width="120" />
                    <el-table-column prop="total_money" label="买单金额" min-width="120" />
                    <el-table-column prop="pay_type_name" label="支付类型" min-width="120" />
                    <el-table-column  label="订单状态" min-width="120">
                        <template #default="{ row }">
                            <div>{{ row.status_info.name }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" label="下单时间" min-width="120" />
                    <el-table-column  label="支付时间" min-width="120">
                        <template #default="{ row }">
                            <div>{{ row.pay_time ? row.pay_time : ''  }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="right" fixed="right" min-width="100">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row)">详情</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="cashTableData.page" v-model:page-size="cashTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="cashTableData.total"
                        @size-change="loadCashSiteList()" @current-change="loadCashSiteList" />
                </div>
            </div>
        </el-card>
        <order-detail ref="orderDetailRef" />
    </div>
</template>
<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'
import { img, setTablePageStorage, getTablePageStorage } from '@/utils/common'
import { getOrderPayType, getOrderFrom } from '@/app/api/sys'
import { getCashSiteList, getCashSiteStatus } from '@/app/api/cash_site'
import orderDetail from './components/order-detail.vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const cashTableData = reactive({
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
        status: '',
        create_time: [],
        pay_time: []
    }
})
const searchFormRef = ref<FormInstance>()
const payTypeData = ref<any[]>([])
const orderFromData = ref([])
const statusData = ref<any>([])
const activeName = ref('')
const setFormData = () => {
    getOrderPayType().then((res: any) => {
        payTypeData.value = res.data
    })
    getOrderFrom().then((res: any) => {
        orderFromData.value = res.data
    })
    getCashSiteStatus().then((res: any) => {
        statusData.value = res.data
    })
}
setFormData()

// 获取列表
const loadCashSiteList = (page: number = 1) => {
    cashTableData.loading = true
    cashTableData.page = page

    getCashSiteList({
        page: cashTableData.page,
        limit: cashTableData.limit,
        ...cashTableData.searchParam
    }).then((res: any) => {
        cashTableData.loading = false
        cashTableData.data = res.data.data
        cashTableData.total = res.data.total
        setTablePageStorage(cashTableData.page, cashTableData.limit, cashTableData.searchParam)
    }).catch(() => {
        cashTableData.loading = false
    })
}
loadCashSiteList(getTablePageStorage(cashTableData.searchParam).page)

const handleClick = (event: any) => {
    cashTableData.searchParam.status = event
    loadCashSiteList()
}
const orderDetailRef = ref()
const detailEvent = (row: any) => {
    orderDetailRef.value.setFormData(row)
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadCashSiteList(getTablePageStorage(cashTableData.searchParam).page)
}
</script>
<style lang="scss" scoped>
.input-item {
	width: 150px !important;
}

</style>
