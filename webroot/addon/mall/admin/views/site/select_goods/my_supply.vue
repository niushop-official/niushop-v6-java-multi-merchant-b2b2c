<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="supplyTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('供货商名称')" prop="keyword">
                        <el-input v-model.trim="supplyTable.searchParam.keyword" :placeholder="t('请输入供货商名称')" maxlength="60" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadSupplyList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-table :data="supplyTable.data" size="large" v-loading="supplyTable.loading" ref="goodsListTableRef">
                <template #empty>
                    <span>{{ !supplyTable.loading ? t('emptyData') : '' }}</span>
                </template>
                <el-table-column :label="t('供货商名称')" min-width="100">
                    <template #default="{ row }">
                       <div class="flex items-center cursor-pointer" @click="router.push({ path: '/supply/select/supply', query: { site_id: row.supply_id }} )">
                            <el-image class="w-[30px] h-[30px] rounded-full align-middle mr-[10px]" :src="img(row.siteJoin.front_end_logo)" fit="cover">
                                <template #error>
                                    <img src="@/addon/mall/assets/supply_default.png" class="w-[30px] h-[30px] rounded-full">
                                </template>
                            </el-image>
                            <span>{{ row.siteJoin.site_name }}</span>
                       </div>
                    </template>
                </el-table-column>
                <el-table-column prop="select_num" :label="t('挑货数量')" min-width="100" />
                <el-table-column prop="select_up_num" :label="t('挑货上架数量')" min-width="100" />
                <el-table-column prop="create_time" :label="t('合作时间')" min-width="100" />
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="supplyTable.page" v-model:page-size="supplyTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="supplyTable.total"
                        @size-change="loadSupplyList()" @current-change="loadSupplyList" />
            </div>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { FormInstance } from 'element-plus'
import { getMysupplyList } from '@/addon/mall/api/site/select_goods'

const router = useRouter()
const route = useRoute()

const pageName = route.meta.title

const supplyTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取供货商列表
 */
const loadSupplyList = (page: number = 1) => {
    supplyTable.loading = true
    supplyTable.page = page

    getMysupplyList({
        page: supplyTable.page,
        limit: supplyTable.limit,
        ...supplyTable.searchParam
    }).then(res => {
        supplyTable.loading = false
        supplyTable.data = res.data.data
        supplyTable.total = res.data.total
    }).catch(() => {
        supplyTable.loading = false
    })
}

loadSupplyList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()

    loadSupplyList()
}
</script>

<style lang="scss" scoped>

</style>