<template>
    <div class="main-container">
      <el-card class="box-card !border-none" shadow="never">
        <div class="flex justify-between items-center">
            <span class="text-page-title">{{ pageName }}</span>
            <el-button type="primary" @click="handleChange">{{ t("添加活动") }}</el-button>
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
            <div class="mb-[10px] flex items-center">
                <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                <el-dropdown class="mr-[10px] !text-primary w-[125px]">
                    <span class="el-dropdown-link flex items-center">
                        <span>{{ currentSelectMode === 'all' ? t('全选所有页') : t('全选当前页')}}</span>(<span class="text-center inline-block">{{ selectedCount }}</span>)
                        <el-icon class="ml-[2px]">
                            <arrow-down />
                        </el-icon>
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item class="select-wrap" :class="{ active: currentSelectMode === 'all' }" @click="selectAllPages">
                                全选所有页
                            </el-dropdown-item>
                            <el-dropdown-item class="select-wrap"  :class="{ active: currentSelectMode === 'page' }" @click="toggleChange">
                                全选当前页
                            </el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
                <!-- v-if="formData.status == 0 || formData.status==1" -->
                <el-button @click="batchSetGoods" size="small">{{ t('批量设置详情模版') }}</el-button>
            </div>
            <el-table :data="tableData.data" size="large" v-loading="tableData.loading" ref="goodBankListTableRef" @selection-change="handleSelectionChange">
                <template #empty>
                    <span>{{ !tableData.loading ? t("emptyData") : "" }}</span>
                </template>
                <el-table-column type="selection" width="55" />
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
                <el-table-column  :label="t('审核状态')" min-width="130">
                    <template #default="{ row }">
                        <div>
                            <div>{{ row.audit_status_name }}</div>
                            <div v-if="row.audit_reason">拒绝原因：{{ row.audit_reason }}</div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="create_time" :label="t('createTime')" min-width="130" />
                <el-table-column :label="t('operation')" fixed="right" align="right" width="260">
                    <template #default="{ row }">
                        <div class="flex items-center justify-end">
                            <template v-for="(op, index) in getOperations(row)" :key="index">
                                <template v-if="['goods', 'stat', 'order', 'edit','spread'].includes(op.key)">
                                    <el-button type="primary" link @click="op.action" class="!ml-[12px]">{{ op.label }}</el-button>
                                </template>
                            </template>
                            <el-dropdown v-if="getOperations(row).filter(op => !['goods', 'stat', 'order', 'edit','spread'].includes(op.key)).length > 0">
                                <el-button type="primary" link  class="!ml-[12px]">{{ t('更多') }}</el-button>
                                <template #dropdown>
                                    <el-dropdown-menu>
                                        <template v-for="(op, index) in getOperations(row)" :key="index">
                                            <el-dropdown-item v-if="!['goods', 'stat', 'order', 'edit','spread'].includes(op.key)" @click="op.action">
                                                {{ op.label }}
                                            </el-dropdown-item>
                                        </template>
                                    </el-dropdown-menu>
                                </template>
                            </el-dropdown>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadList()" @current-change="loadList" />
            </div>
        </div>
      </el-card>
      <active-detail-popup ref="activeDetailDialog"></active-detail-popup>
      <exchange-record-popup ref="exchangeRecordDialog"></exchange-record-popup>
        <!-- 批量设置弹出框 -->
        <goods-batch-settings-popup ref="goodsBatchSettingPopupRef" />
       <!-- 商品推广弹出框 -->
       <spread-popup ref="spreadPopupRef" />
    </div>
  </template>
<script lang="ts" setup>
import { ref, reactive, nextTick, computed } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { getPintuanList, getPintuanInit, deletePintuan, closePintuan } from '@/addon/pintuan/api/site/active'
import { FormInstance, ElMessageBox, ElMessage } from 'element-plus'
import { setTablePageStorage, getTablePageStorage, getCurrentDataTime, img } from '@/utils/common'
import activeDetailPopup from '@/addon/pintuan/views/site/active/components/active-detail-popup.vue'
import exchangeRecordPopup from '@/addon/pintuan/views/site/active/components/exchange-record-popup.vue'
import spreadPopup from '@/components/spread-popup/index.vue'
import goodsBatchSettingsPopup from '@/addon/pintuan/views/site/active/components/goods-batch-settings-popup.vue'

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

// 全选所有页时排除的 ID
const excludedIds = ref<number[]>([])
// 是否全选所有页
const isSelectAllPages = ref(false)
const restoringSelection = ref(false)
// 批量复选框
const currentSelectMode = ref<'all' | 'page' | null>(null)

// 全选当前页
const toggleChange = () => {
    restoringSelection.value = true // 加锁
    if (currentSelectMode.value === 'page') {
        isSelectAllPages.value = false
        currentSelectMode.value = null
        excludedIds.value = []
        multipleSelection.value = []
        goodBankListTableRef.value.clearSelection()
    } else {
        isSelectAllPages.value = false
        currentSelectMode.value = 'page'
        excludedIds.value = []
        multipleSelection.value = []
        tableData.data.forEach(row => {
            goodBankListTableRef.value.toggleRowSelection(row, true)
        })
        multipleSelection.value = [...tableData.data]
    }
    nextTick(() => {
        restoringSelection.value = false // 解锁
    })
}

// 全选所有页
const selectAllPages = () => {
    restoringSelection.value = true // 加锁
    if (currentSelectMode.value === 'all') {
        isSelectAllPages.value = false
        currentSelectMode.value = null
        excludedIds.value = []
        multipleSelection.value = []
        goodBankListTableRef.value.clearSelection()
    } else {
        excludedIds.value = []
        multipleSelection.value = []
        isSelectAllPages.value = true
        currentSelectMode.value = 'all'
        tableData.data.forEach(row => {
            goodBankListTableRef.value.toggleRowSelection(row, true)
        })
    }
    nextTick(() => {
        restoringSelection.value = false // 解锁
    })
}

const selectedCount = computed(() => {
    if (isSelectAllPages.value) {
        return tableData.total - excludedIds.value.length
    } else {
        return multipleSelection.value.length
    }
})

/** ***************** 批量设置-start *************************/
const goodsBatchSettingPopupRef = ref()
const batchSetGoods = () => {
    const isNoneSelected =
    (!isSelectAllPages.value && multipleSelection.value.length === 0) ||
    (isSelectAllPages.value && excludedIds.value.length === tableData.total)

    if (isNoneSelected) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedGoodsTips')}`
        })
        return
    }
    const info = getBatchPayload()
    goodsBatchSettingPopupRef.value.show(info)
}

/** ***************** 批量设置-end *************************/
const getBatchPayload = () => {
    if (isSelectAllPages.value) {
        return {
            is_all: 1,
            ids: excludedIds.value,
            where: {
                ...tableData.searchParam
            }
        }
    } else {
        return {
            is_all: 0,
            ids: multipleSelection.value.map(item => item.id),
            where: {
                ...tableData.searchParam
            }
        }
    }
}

// 获取列表
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getPintuanList({
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
loadList(getTablePageStorage(tableData.searchParam).page)

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
const statEvent = (id:number) => {
    router.push('/pintuan/active/stat?id=' + id)
}
const handleChange = () => {
    router.push('/pintuan/active/add')
}

const statusList = ref<{ [key: string]: string }>({})
const getPintuanInitFn = () => {
    getPintuanInit().then((res) => {
        statusList.value = res.data.status_list
        auditStatus.value = res.data.audit_status_list
    })
}
getPintuanInitFn()

// 编辑秒杀活动
const editEvent = (data: any) => {
    router.push('/pintuan/active/edit?id=' + data.id)
}

// 详情
const activeDetailDialog: Record<string, any> | null = ref(null)
const detailEvent = (id:number) => {
    const data = { id }
    activeDetailDialog.value.setFormData(data)
    activeDetailDialog.value.showDialog = true
}
// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

const goodBankListTableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (
        multipleSelection.value.length > 0 &&
        multipleSelection.value.length < tableData.data.length
    ) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == tableData.data.length) {
        toggleCheckbox.value = true
    }
}

// 删除
let deleteRepeat = false
const deleteEvent = (id:number) => {
    ElMessageBox.confirm(t('确定要删除该活动吗？'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (deleteRepeat) return
        deleteRepeat = true
        deletePintuan(id).then(() => {
            deleteRepeat = false
            loadList(getTablePageStorage(tableData.searchParam).page)
        }).catch(() => {
            deleteRepeat = false
        })
    })
}

// 关闭
let closeRepeat = false
const closeEvent = (id:number) => {
    ElMessageBox.confirm(t('确定要关闭该活动吗？'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (closeRepeat) return
        closeRepeat = true
        closePintuan(id).then(() => {
            closeRepeat = false
            loadList(getTablePageStorage(tableData.searchParam).page)
        }).catch(() => {
            closeRepeat = false
        })
    })
}

const orderEvent = (id: number) => {
    const routeUrl = router.resolve({
        path: '/mall/order/index',
        query: {
            pintuan_id: id
        }
    })
    window.open(routeUrl.href, '_blank')
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
const getOperations = (row: any) => {
    const ops: { label: string, key: string, action: () => void }[] = []
    if (row.status != 'end') {
        ops.push({ label: t('推广'), key: 'spread', action: () => spreadEvent(row) })
    }
    if (row.status == 'wait' || row.status == 'process') {
        ops.push({ label: t('edit'), key: 'edit', action: () => editEvent(row) })
    }
    ops.push({ label: t('统计'), key: 'stat', action: () => statEvent(row.id) })
    ops.push({ label: t('订单'), key: 'order', action: () => orderEvent(row.id) })
    ops.push({ label: t('info'), key: 'info', action: () => detailEvent(row.id) })

    if (row.status == 'process') {
        ops.push({ label: t('关闭'), key: 'close', action: () => closeEvent(row.id) })
    }
    if (row.status != 'process' && row.is_show_del) {
        ops.push({ label: t('delete'), key: 'delete', action: () => deleteEvent(row.id) })
    }

    return ops
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
</script>

<style lang="scss" scoped>
</style>
