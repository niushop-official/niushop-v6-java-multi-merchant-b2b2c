<template>
    <div class="main-container">
      <el-card class="box-card !border-none" shadow="never">
        <div class="flex justify-between items-center">
            <span class="text-page-title">{{ pageName }}</span>
        </div>

        <!-- 搜索 -->
        <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
            <el-form label-width="70px" :inline="true" :model="tableData.searchParam" ref="searchFormRef" class="page-form">
                <el-form-item :label="t('name')" prop="title">
                    <el-input v-model.trim="tableData.searchParam.title" :placeholder="t('namePlaceholder')" class="search-input" clearable />
                </el-form-item>
                <el-form-item :label="t('activityTime')" prop="create_time">
                    <el-date-picker v-model="tableData.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" @change="manjianTimeChange" class="search-input" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadManjianList()">{{ t("search") }}</el-button>
                    <el-button @click="resetForm(searchFormRef)">{{ t("reset") }}</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 列表 -->
        <el-tabs v-model="tableData.searchParam.status" class="goods-tabs" @tab-click="tabHandleClick">
            <el-tab-pane :label="t('all')" name=""></el-tab-pane>
            <el-tab-pane v-for="(label, value) in statusList" :key="value" :label="label" :name="value"></el-tab-pane>
        </el-tabs>
        <div>
            <el-table :data="tableData.data" size="large" v-loading="tableData.loading">
                <template #empty>
                    <span>{{ !tableData.loading ? t("emptyData") : "" }}</span>
                </template>
                <el-table-column prop="title" :label="t('name')" min-width="130" />

                <el-table-column prop="type_name" :label="t('活动类型')" min-width="130" />
                <el-table-column :label="t('activityTime')"  min-width="150">
                    <template #default="{ row }">
                        <div>
                            <p>开始：{{ row.start_time }}</p>
                            <p>结束：{{ row.end_time }}</p>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="status_name" :label="t('status')" min-width="130" />
                <el-table-column prop="create_time" :label="t('createTime')" min-width="150">
                    <template #default="{ row }">
                        <div>{{ row.create_time }}</div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="detailEvent(row.id)">{{ t('detail') }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadManjianList()" @current-change="loadManjianList" />
            </div>
        </div>
      </el-card>
      <manjian-detail ref="manjianDetailDialog"></manjian-detail>
    </div>
  </template>
<script lang="ts" setup>
// 添加满减送活动
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { FormInstance, ElMessageBox, ElMessage } from 'element-plus'
import { getFullReductionList ,getFullReductionInit} from "@/addon/mall/api/admin/marketing";
import { setTablePageStorage, getTablePageStorage ,getCurrentDataTime} from '@/utils/common'
import manjianDetail from '@/addon/mall/views/admin/marketing/full_reduction/components/detail-drawer.vue'


const router = useRouter()
const route = useRoute()
const pageName = route.meta.title
const repeat = ref(false)
const searchFormRef = ref<FormInstance>()
// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        create_time: [],
        // start_time: '',
        // end_time: '',
        title: '',
        status: route.query.status || '',
    }
})
const status = ref()

// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    tableData.searchParam.status = tab.props.name
    status.value = tab.props.name
    loadManjianList()
}
const manjianTimeChange = (val: any) => {
    // if (val == null) {
    //     tableData.searchParam.start_time = ''
    //     tableData.searchParam.end_time = ''
    //     return
    // }
    // tableData.searchParam.start_time = getCurrentDataTime(JSON.parse(JSON.stringify(val[0])))
    // tableData.searchParam.end_time = getCurrentDataTime(JSON.parse(JSON.stringify(val[1])))
}

// 获取列表
const loadManjianList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getFullReductionList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then((res) => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
        setTablePageStorage(tableData.page, tableData.limit, tableData.searchParam)
    }).catch(() => {
        tableData.loading = false
    })
}

loadManjianList(getTablePageStorage(tableData.searchParam).page)

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadManjianList()
}


const statusList = ref<{ [key: string]: string }>({})
const getManjianStatusListFn = () => {
    getFullReductionInit().then((res) => {
        statusList.value = res.data.status_list
    })
}

getManjianStatusListFn()

// 详情
const manjianDetailDialog: Record<string, any> | null = ref(null)
const detailEvent = (id:number) => {
    const data = { id }
    manjianDetailDialog.value.setFormData(data)
    manjianDetailDialog.value.showDialog = true
}



</script>

<style lang="scss" scoped>
</style>
