<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="accountApplyTable.searchParam" ref="searchFormRef">
<!--                    <el-form-item :label="t('店铺名称')" prop="site_name">
                        <el-input v-model="accountApplyTable.searchParam.site_name"  :placeholder="t('请输入店铺名称')" />
                    </el-form-item>-->
                    <el-form-item :label="t('入驻平台')" prop="type">
                        <el-select v-model="accountApplyTable.searchParam.type" clearable :placeholder="t('请选择入驻平台')" class="input-width">
                            <el-option :label="t('全部')" value="" />
                            <el-option :label="item" :value="index" v-for="(item, index) in typeList" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="accountApplyTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadAccountApplyList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-table :data="accountApplyTable.data" size="large" v-loading="accountApplyTable.loading">
                    <template #empty>
                        <span>{{ !accountApplyTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="site_name" :label="t('店铺名称')" min-width="120" />
                    <el-table-column prop="type_name" :label="t('入驻平台')" min-width="120" />
                    <el-table-column prop="user_type" :label="t('入驻类型 ')" min-width="120" />
                    <el-table-column prop="extend_no" :label="t('分账编号')" min-width="120" />
<!--                    <el-table-column prop="status_name" :label="t('审核状态')" min-width="120" />-->
                    <el-table-column prop="create_time" :label="t('创建时间')" min-width="120" />
                    <el-table-column :label="t('operation')" align="right" fixed="right" width="190">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row.id)">{{ t('详情') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="accountApplyTable.page" v-model:page-size="accountApplyTable.limit" layout="total, sizes, prev, pager, next, jumper"  :total="accountApplyTable.total" @size-change="loadAccountApplyList()"  @current-change="loadAccountApplyList" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { getAccountApply, getAccountApplyType } from '@/app/api/shop_admin'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title

const typeList = ref<any>([])
const accountApplyTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        site_name: '',
        type: '',
        create_time: []
    }
})

const searchFormRef = ref<FormInstance>()

const initData = () => {
    getAccountApplyType().then((res: any) => {
        typeList.value = res.data
    })
}
initData()
/**
 * 获取申请列表
 */
const loadAccountApplyList = (page: number = 1) => {
    accountApplyTable.loading = true
    accountApplyTable.page = page

    getAccountApply({
        page: accountApplyTable.page,
        limit: accountApplyTable.limit,
        ...accountApplyTable.searchParam

    }).then((res: any) => {
        accountApplyTable.loading = false
        accountApplyTable.data = res.data.data
        accountApplyTable.total = res.data.total
    }).catch(() => {
        accountApplyTable.loading = false
    })
}
loadAccountApplyList()

/**
 * 查看详情
 * @param id
 */
const detailEvent = (id: number) => {
    router.push({ path: '/shop/account_apply_info', query: { id } })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadAccountApplyList()
}
</script>

<style scoped>

</style>