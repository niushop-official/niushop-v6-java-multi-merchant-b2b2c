<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="shopTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('店铺名称')" prop="keyword">
                        <el-input v-model.trim="shopTable.searchParam.keyword" :placeholder="t('请输入店铺名称')" maxlength="60" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadShopList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-table :data="shopTable.data" size="large" v-loading="shopTable.loading" ref="goodsListTableRef">
                <template #empty>
                    <span>{{ !shopTable.loading ? t('emptyData') : '' }}</span>
                </template>
                <el-table-column :label="t('店铺名称')" min-width="100">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <el-image class="w-[30px] h-[30px] rounded-full align-middle mr-[10px]" :src="img(row.shopJoin.front_end_logo)" fit="cover">
                                <template #error>
                                    <img src="@/addon/supply/assets/supply_default.png" class="w-[30px] h-[30px] rounded-full">
                                </template>
                            </el-image>
                            <span>{{ row.shopJoin.site_name }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="select_num" :label="t('挑货数量')" min-width="100" />
                <el-table-column prop="select_up_num" :label="t('挑货上架数量')" min-width="100" />
                <el-table-column prop="create_time" :label="t('合作时间')" min-width="100" />
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="shopTable.page" v-model:page-size="shopTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="shopTable.total"
                        @size-change="loadShopList()" @current-change="loadShopList" />
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
import { getShopList } from '@/addon/supply/api/site/select_goods'

const router = useRouter()
const route = useRoute()

const pageName = route.meta.title

const shopTable = reactive({
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
 * 获取合作店铺列表
 */
const loadShopList = (page: number = 1) => {
    shopTable.loading = true
    shopTable.page = page

    getShopList({
        page: shopTable.page,
        limit: shopTable.limit,
        ...shopTable.searchParam
    }).then(res => {
        shopTable.loading = false
        shopTable.data = res.data.data
        shopTable.total = res.data.total
    }).catch(() => {
        shopTable.loading = false
    })
}

loadShopList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()

    loadShopList()
}
</script>

<style lang="scss" scoped>

</style>