<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">添加会员卡</el-button>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="vipcardTableData.searchParam" ref="searchFormRef">
                    <el-form-item label="会员卡名称" prop="card_name">
                        <el-input v-model="vipcardTableData.searchParam.card_name" class="w-[240px]" placeholder="请输入会员卡名称" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadVipcardList()">搜索</el-button>
                        <el-button @click="resetForm(searchFormRef)">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-table :data="vipcardTableData.data" size="large" v-loading="vipcardTableData.loading" ref="memberTableRef">

                    <template #empty>
                        <span>{{ !vipcardTableData.loading ? '暂无数据' : '' }}</span>
                    </template>
                    <el-table-column prop="card_name" label="会员卡名称" min-width="120" />
                    <el-table-column label="有效期" min-width="180">
                        <template #default="{ row }">
                            <div>
                                <div v-for="(item,index) in row.item" :key="index">
                                    <span>{{ item.price }}元，</span>
                                    <span v-if="item.time_limit">{{ item.time_limit }}{{ item.time_type == 'day' ? '天' : item.time_type == 'month' ? '月' : '年' }}，</span>
                                    <span v-if="item.renew_price">续费{{ item.renew_price }}元</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="权益" min-width="120">
                        <template #default="{ row }">
                            <div>
                                <div v-for="(item,index) in row.benefits_list" :key="index">{{ item.name }}</div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="buy_num" label="领卡会员" min-width="120" />
                    <el-table-column prop="status" label="状态" min-width="120">
                        <template #default="{ row }">
                            <el-switch v-model="row.status" :active-value="1" :inactive-value="2" inline-prompt active-text="开启" inactive-text="关闭" @change="(value) => updateStatus(row, value)" />
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="right" fixed="right" min-width="170">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="buyEvent(row)">购卡会员</el-button>
                            <el-button type="primary" link @click="detailEvent(row)">编辑</el-button>
                            <el-button type="primary" link @click="deleteEvent(row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="vipcardTableData.page" v-model:page-size="vipcardTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="vipcardTableData.total"
                        @size-change="loadVipcardList()" @current-change="loadVipcardList" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { setTablePageStorage, getTablePageStorage } from '@/utils/common'
import { getVipcardList, updateVipcardStatus, deleteVipcard } from '@/app/api/vip_card'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const vipcardTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        card_name: ''
    }
})
const searchFormRef = ref<FormInstance>()

// 获取会员列表
const loadVipcardList = (page: number = 1) => {
    vipcardTableData.loading = true
    vipcardTableData.page = page

    getVipcardList({
        page: vipcardTableData.page,
        limit: vipcardTableData.limit,
        ...vipcardTableData.searchParam
    }).then((res: any) => {
        vipcardTableData.loading = false
        vipcardTableData.data = res.data.data
        vipcardTableData.total = res.data.total
        setTablePageStorage(vipcardTableData.page, vipcardTableData.limit, vipcardTableData.searchParam)
    }).catch(() => {
        vipcardTableData.loading = false
    })
}
loadVipcardList(getTablePageStorage(vipcardTableData.searchParam).page)

/**
 * 添加会员
 */
const addEvent = () => {
    router.push('/member/vip_card/add')
}

const detailEvent = (row: any) => {
    router.push({
        path: '/member/vip_card/add',
        query: {
            card_id: row.card_id
        }
    })
}
const buyEvent = (row: any) => {
    router.push({
        path: '/member/vip_card/buy_card',
        query: {
            card_id: row.card_id
        }
    })
}
const updateStatus = (row: any, value: number) => {
    updateVipcardStatus({
        card_id: row.card_id,
        status: value
    }).then(() => {
        loadVipcardList()
    }).catch(() => {
        row.status = value == 1 ? 2 : 1
    })
}

const deleteEvent = (row: any) => {
    ElMessageBox.confirm('确定要删除该会员卡吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        deleteVipcard(row.card_id).then(() => {
            loadVipcardList(getTablePageStorage(vipcardTableData.searchParam).page)
        })
    })
}
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadVipcardList()
}
</script>

<style scoped>

</style>