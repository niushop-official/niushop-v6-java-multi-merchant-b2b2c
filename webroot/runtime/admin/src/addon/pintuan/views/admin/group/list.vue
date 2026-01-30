<template>
    <div class="main-container">
      <el-card class="box-card !border-none" shadow="never">
        <div class="flex justify-between items-center">
            <span class="text-page-title">{{ pageName }}</span>
        </div>

        <!-- 搜索 -->
        <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
            <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef" @submit.prevent>
                <el-form-item :label="t('活动名称')" prop="pintuan_title">
                    <el-input v-model.trim="tableData.searchParam.pintuan_title" :placeholder="t('请输入活动名称')" />
                </el-form-item>
                <el-form-item :label="t('团长名称')" prop="leader_name">
                    <el-input v-model.trim="tableData.searchParam.leader_name" :placeholder="t('请输入团长名称')" />
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadList()">{{ t("search") }}</el-button>
                    <el-button @click="resetForm(searchFormRef)">{{ t("reset") }}</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <!-- 列表 -->
        <el-tabs v-model="tableData.searchParam.status" class="goods-tabs" @tab-click="tabHandleClick">
                <el-tab-pane :label="t('全部')" name=""></el-tab-pane>
                <el-tab-pane :label="t('拼团中')" name="process"></el-tab-pane>
                <el-tab-pane :label="t('拼团成功')" name="success"></el-tab-pane>
                <el-tab-pane :label="t('拼团失败')" name="failed"></el-tab-pane>
            </el-tabs>
        <div class="mt-[15px]">
            <el-table :data="tableData.data" size="large" v-loading="tableData.loading" ref="pintuanGroupTableRef" @selection-change="handleSelectionChange">
                <template #empty>
                    <span>{{ !tableData.loading ? t("emptyData") : "" }}</span>
                </template>
                <el-table-column prop="name" :label="t('活动名称')" min-width="150" >
                    <template #default="{ row }">
                        <div class="cursor-pointer">{{ row.pintuan_title }}</div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('团长信息')" min-width="150" >
                    <template #default="{ row }">
                        <div class="flex items-center cursor-pointer" @click="memberEvent(row.leader_id)">
                            <el-image v-if="row.leader_headimg" class="w-[50px] h-[50px] mr-[10px]" :src="img(row.leader_headimg)" fit="contain">
                                <template #error>
                                    <div class="image-slot">
                                        <img class="w-[50px] h-[50px] mr-[10px]" src="@/app/assets/images/member_head.png" />
                                    </div>
                                </template>
                            </el-image>
                            <img v-else class="w-[50px] h-[50px] mr-[10px]" src="@/app/assets/images/member_head.png" fit="contain" />

                            {{ row.leader_name }}</div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('进度')" min-width="120" >
                    <template #default="{ row }">
                        <div >{{ row.current_num }}/{{ row.target_num }}</div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('状态')" min-width="120" >
                    <template #default="{ row }">
                        <div class="cursor-pointer">{{ row.status_name }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="expire_time" :label="t('拼团时间')" min-width="150">
                        <template #default="{ row }">
                            <div>
                                <div>开始时间：{{ row.start_time }}</div>
                                <div>结束时间：{{ row.expire_time }}</div>
                            </div>
                        </template>
                    </el-table-column>

                <el-table-column :label="t('operation')" fixed="right" align="right" min-width="100">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="detailEvent(row.id)">{{t("detail")}}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadList()" @current-change="loadList" />
            </div>
        </div>
      </el-card>
      <group-detail-popup ref="groupDetailDialog"></group-detail-popup>
    </div>
  </template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getPintuanAdminGroupList } from '@/addon/pintuan/api/admin/group'
import groupDetailPopup from '@/addon/pintuan/views/admin/group/components/group-detail-popup.vue'
import { FormInstance } from 'element-plus'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title
// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        pintuan_title: '',
        leader_name: '',
        status: ''
    }
})
const searchFormRef = ref<FormInstance>()
// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    tableData.searchParam.status = tab.props.name
    loadList()
}
// 获取列表
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getPintuanAdminGroupList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then((res) => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
    }).catch(() => {
        tableData.loading = false
    })
}

loadList()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadList()
}

// 详情
const groupDetailDialog: Record<string, any> | null = ref(null)
const detailEvent = (id:number) => {
    const data = { id }
    groupDetailDialog.value.setFormData(data)
    groupDetailDialog.value.showDialog = true
}

const memberEvent = (id: number) => {
    const routeUrl = router.resolve({
        path: '/member/detail',
        query: { id }
    })
    window.open(routeUrl.href, '_blank')
}
</script>

<style lang="scss" scoped>
</style>
