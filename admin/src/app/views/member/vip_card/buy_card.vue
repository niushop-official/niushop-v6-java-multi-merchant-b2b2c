<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="buycardTableData.searchParam" ref="searchFormRef">
                    <el-form-item label="会员信息" prop="keyword">
                        <el-input v-model="buycardTableData.searchParam.keyword" class="w-[240px]" placeholder="请输入会员信息" />
                    </el-form-item>
                    <el-form-item label="会员卡名称" prop="card_name">
                        <el-input v-model="buycardTableData.searchParam.card_name" class="w-[240px]" placeholder="请输入会员卡名称" />
                    </el-form-item>
                    <el-form-item label="购买时间" prop="create_time">
                        <el-date-picker v-model="buycardTableData.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间" end-placeholder="结束时间" class="search-input" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadBuycardList()">搜索</el-button>
                        <el-button @click="resetForm(searchFormRef)">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-table :data="buycardTableData.data" size="large" v-loading="buycardTableData.loading" ref="memberTableRef">
                    <template #empty>
                        <span>{{ !buycardTableData.loading ? '暂无数据' : '' }}</span>
                    </template>
                    <el-table-column label="会员卡名称" min-width="120">
                        <template #default="{ row }">
                            <div>{{ row.vip_card.card_name }}</div>
                        </template>
                    </el-table-column>
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
                    <el-table-column label="卡规格信息" min-width="120">
                        <template #default="{ row }">
                            <div>
                                <span>{{ row.vip_card_item.item_name }}</span>
                                <span class="ml-[8px]">{{ row.vip_card_item.time_limit }}{{ row.vip_card_item.time_type == 'day' ? '天' : row.vip_card_item.time_type == 'month' ? '月' : '年' }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="is_expire_name" label="是否过期" min-width="120" />
                    <el-table-column prop="is_trial_name" label="是否试用" min-width="120" />
                    <el-table-column prop="expire_time" label="过期时间" min-width="120" />
                    <el-table-column prop="create_time" label="购买时间" min-width="120" />
                    <el-table-column label="操作" align="right" fixed="right" min-width="170">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="orderEvent(row)">查看订单</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="buycardTableData.page" v-model:page-size="buycardTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="buycardTableData.total"
                        @size-change="loadBuycardList()" @current-change="loadBuycardList" />
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
import { getBuycardList } from '@/app/api/vip_card'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const buycardTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        card_name: '',
        create_time: [],
        card_id: route.query.card_id || ''
    }
})
const searchFormRef = ref<FormInstance>()

// 获取会员列表
const loadBuycardList = (page: number = 1) => {
    buycardTableData.loading = true
    buycardTableData.page = page

    getBuycardList({
        page: buycardTableData.page,
        limit: buycardTableData.limit,
        ...buycardTableData.searchParam
    }).then((res: any) => {
        buycardTableData.loading = false
        buycardTableData.data = res.data.data
        buycardTableData.total = res.data.total
        setTablePageStorage(buycardTableData.page, buycardTableData.limit, buycardTableData.searchParam)
    }).catch(() => {
        buycardTableData.loading = false
    })
}
loadBuycardList(getTablePageStorage(buycardTableData.searchParam).page)

const orderEvent = (row: any) => {
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
    loadBuycardList(getTablePageStorage(buycardTableData.searchParam).page)
}

</script>

<style scoped>

</style>