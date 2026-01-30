<template>
    <div class="main-container">
      <el-card class="box-card !border-none" shadow="never">
        <div class="flex justify-between items-center">
            <span class="text-page-title">{{ pageName }}</span>
        </div>

        <!-- 搜索 -->
        <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
            <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
                <el-form-item :label="t('活动名称')" prop="title">
                    <el-input v-model.trim="tableData.searchParam.title" :placeholder="t('请输入活动名称')" />
                </el-form-item>
                <el-form-item :label="t('活动时间')" prop="time">
                    <el-date-picker v-model="time" type="datetimerange" range-separator="-"
                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')" @change="pintuanTimeChange"/>
                </el-form-item>
                <el-form-item :label="t('审核状态')" prop="audit_status">
                    <el-select v-model="tableData.searchParam.audit_status" :placeholder="t('请选择审核状态')" clearable>
                        <el-option v-for="(item, key) in auditStatus" :key="key" :label="item" :value="key" />
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadList()">{{ t("search") }}</el-button>
                    <el-button @click="resetForm(searchFormRef)">{{ t("reset") }}</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 列表 -->
        <div>
            <el-tabs v-model="tableData.searchParam.status" class="goods-tabs" @tab-click="tabHandleClick">
                <el-tab-pane :label="t('全部')" name=""></el-tab-pane>
                <el-tab-pane v-for="(label, value) in statusList" :key="value" :label="label" :name="value"></el-tab-pane>
            </el-tabs>
            <el-table :data="tableData.data" size="large" v-loading="tableData.loading" ref="goodBankListTableRef" @selection-change="handleSelectionChange">
                <template #empty>
                    <span>{{ !tableData.loading ? t("emptyData") : "" }}</span>
                </template>
                <el-table-column prop="title" :label="t('活动名称')" min-width="130" />
                <el-table-column :label="t('商品信息')" min-width="200" >
                    <template #default="{ row }">
                        <div class="flex items-center cursor-pointer" @click="previewEvent(row)">
                            <div class="mr-[10px] rounded-full w-[50px] h-[50px] flex items-center justify-center">
                                <el-image v-if="row.goods_data.cover" :src="img(row.goods_data.cover)" fit="cover" class="w-[50px] h-[50px]" >
                                    <template #error>
                                        <img class="w-[50px] h-[50px]" src="@/addon/pintuan/assets/goods_default.png" alt="">
                                    </template>
                                </el-image>
                                <img class="w-[50px] h-[50px]" v-else src="@/addon/pintuan/assets/goods_default.png" alt="">
                            </div>
                            <div class="flex flex-col items-start">
                                <span class="using-hidden" :title="row.goods_data.title">
                                    {{ row.goods_data.title }}
                                </span>
                            </div>

                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="type_name" :label="t('活动类型')" min-width="160" />
                <el-table-column prop="start_time" :label="t('活动时间')" min-width="160">
                    <template #default="{ row }">
                        <div>
                            <p>开始：{{ row.start_time }}</p>
                            <p>结束：{{ row.end_time }}</p>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="status_name" :label="t('status')" min-width="80" />
                <el-table-column prop="sort"  min-width="120"  class-name="sort-column">
                    <template #header>
                        <span class="mr-[4px]">{{t('sort')}}</span>
                        <el-tooltip class="box-item" effect="dark" :content="t('sortPlaceholder')"  placement="bottom">
                            <el-icon><QuestionFilled /></el-icon>
                        </el-tooltip>
                    </template>
                    <template #default="{ row }">
                        <el-input v-model="row.sort" class="input-width-sort" maxlength="10" @input="sortInputListener($event, row)" />
                    </template>
                </el-table-column>
                <el-table-column prop="audit_status_name" :label="t('审核状态')" min-width="130" />
                <el-table-column prop="create_time" :label="t('createTime')" min-width="130" />
                <el-table-column :label="t('operation')" fixed="right" align="right" width="260">
                    <template #default="{ row }">
                        <div class="flex items-center justify-end">
                            <el-button type="primary" link @click="spreadEvent(row)" class="!ml-[12px]">推广</el-button>
                            <el-button type="primary" link @click="statEvent(row.id)" class="!ml-[12px]">统计</el-button>
                            <el-button type="primary" link @click="orderEvent(row.id)" class="!ml-[12px]">订单</el-button>
                            <el-button v-if="row.audit_status == 1" type="primary" link @click="auditEvent(row.id)" class="!ml-[12px]">审核</el-button>
                            <el-button type="primary" link @click="detailEvent(row.id)" class="!ml-[12px]">详情</el-button>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadList()" @current-change="loadList" />
            </div>
        </div>
    </el-card>
    <!-- 审核弹框 -->
     <el-dialog v-model="auditShowDialog" :title="t('审核')" width="460px" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="auditFormData" label-width="90px" ref="auditFormRef" :rules="auditFormRules" class="page-form">
            <el-form-item :label="t('审核状态')" prop="status">
                    <el-radio-group v-model="auditFormData.audit_status">
                        <el-radio :label="2">通过</el-radio>
                        <el-radio :label="-1">拒绝</el-radio>
                    </el-radio-group>
                </el-form-item>
            <el-form-item :label="t('拒绝原因')" prop="audit_reason" v-if="auditFormData.audit_status == -1">
                <el-input v-model.tirm="auditFormData.audit_reason" type="textarea" rows="4" clearable class="input-width"  :maxlength="100"  :show-word-limit="true" />
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="auditCancel">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="auditConfirm(auditFormRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
    <active-detail-popup ref="activeDetailDialog"></active-detail-popup>
    <!-- 商品推广弹出框 -->
    <spread-popup ref="spreadPopupRef" />
    </div>
  </template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { getPintuanAdminList, getPintuanAdminInit, editPintuanAdminSort, editPintuanAdminAudit } from '@/addon/pintuan/api/admin/active'
import { ElMessage, FormInstance } from 'element-plus'
import { debounce, getCurrentDataTime, img } from '@/utils/common'
import activeDetailPopup from '@/addon/pintuan/views/admin/active/components/active-detail-popup.vue'
import spreadPopup from '@/components/spread-popup/index.vue'
const router = useRouter()
const route = useRoute()
const pageName = route.meta.title
const searchFormRef = ref<FormInstance>()
// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        start_time: '',
        end_time: '',
        title: '',
        status: route.query.status || '',
        audit_status: ''
    }
})
const status = ref()
const auditStatus = ref([])

// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    tableData.searchParam.status = tab.props.name
    status.value = tab.props.name
    loadList()
}

const time = ref([])

// 获取列表
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getPintuanAdminList({
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

const pintuanTimeChange = (val: any) => {
    if (val == null) {
        tableData.searchParam.start_time = ''
        tableData.searchParam.end_time = ''
        return
    }
    tableData.searchParam.start_time = getCurrentDataTime(JSON.parse(JSON.stringify(val[0])))
    tableData.searchParam.end_time = getCurrentDataTime(JSON.parse(JSON.stringify(val[1])))
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    tableData.searchParam.start_time = ''
    tableData.searchParam.end_time = ''
    time.value = []
    loadList()
}

const statusList = ref<{ [key: string]: string }>({})
const getPintuanAdminInitFn = () => {
    getPintuanAdminInit().then((res) => {
        statusList.value = res.data.status_list
        auditStatus.value = res.data.audit_status_list
    })
}
getPintuanAdminInitFn()

// 详情
const activeDetailDialog: Record<string, any> | null = ref(null)
const detailEvent = (id:number) => {
    const data = { id }
    activeDetailDialog.value.setFormData(data)
    activeDetailDialog.value.showDialog = true
}

// 商品推广
const spreadPopupRef = ref(null)
const spreadEvent = (data: any) => {
    const pagePath = '/addon/pintuan/pages/goods/detail'
    // 多参数数组
    const paramsArr = [
        { name: 'pintuan_id', value: data.goods_data.pintuan_id }
    ]
    const title = '活动推广'
    const folder = 'goods'

    spreadPopupRef.value?.show(pagePath, paramsArr, title, folder)
}


const statEvent = (id:number) => {
    router.push('/pintuan/active/stat?id=' + id)
}

// 商品预览
const previewEvent = (data: any) => {
    const url = router.resolve({
        path: '/preview/wap',
        query: {
            page: `/addon/pintuan/pages/goods/detail?pintuan_id=${data.goods_data.pintuan_id}`
        }
    })
    window.open(url.href)
}

// 正则表达式
const regExp = {
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/
}

// 修改排序号
const sortInputListener = debounce((sort, row) => {
    if (isNaN(sort) || !regExp.number.test(sort)) {
        ElMessage({
            type: 'warning',
            message: `${t('sortTips')}`
        })
        return
    }
    editPintuanAdminSort({
        id: row.id,
        sort: sort
    }).then((res) => {
    })
})

// 审核
const auditShowDialog = ref(false)
const auditFormData = reactive({
    id: '',
    audit_status: 2,
    audit_reason: ''
})
const auditFormRef = ref<FormInstance>()
const loading = ref(false)

// 表单验证规则
const auditFormRules = computed(() => {
    return {
        audit_reason: [
            { required: true, message: t('请输入拒绝原因'), trigger: 'blur' }
        ]
    }
})

const auditEvent = (id: any) => {
    auditFormData.id = id
    auditFormData.audit_status = 2
    auditFormData.audit_reason = ''
    auditShowDialog.value = true
}

const auditConfirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            editPintuanAdminAudit(auditFormData).then(() => {
                loading.value = false
                auditShowDialog.value = false
                loadList()
            }).catch(() => {
                loading.value = false
                auditShowDialog.value = false
            })
        }
    })
}

const auditCancel = () => {
    auditShowDialog.value = false
    auditFormData.id = ''
    auditFormData.audit_status = 2
    auditFormData.audit_reason = ''
}

const orderEvent = (id: number) => {
    const routeUrl = router.resolve({
        path: '/mall/order/index',
        query: {
            pintuan_id: id,
        }
    })
    window.open(routeUrl.href, '_blank')
}
</script>

<style lang="scss" scoped>
</style>
