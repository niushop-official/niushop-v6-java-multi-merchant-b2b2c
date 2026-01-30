<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="treasureTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('treasureName')" prop="keyword">
                        <el-input v-model.trim="treasureTable.searchParam.keyword" :placeholder="t('treasureNamePlaceholder')" maxlength="60" />
                    </el-form-item>
                    <el-form-item :label="t('relateType')">
                        <el-select v-model="treasureTable.searchParam.relate_type" :placeholder="t('relateTypePlaceholder')">
                            <el-option v-for="(item,index) in treasureType" :key="index" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadTreasureList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">

                <el-tabs v-model="treasureTable.searchParam.audit_status"  @tab-click="tabHandleClick">
                    <el-tab-pane v-for="(item,index) in treasureStatus" :key="index" :label="item.label" :name="item.value"></el-tab-pane>
                </el-tabs>

                <div class="mb-[10px] flex items-center" v-if="['0','1'].indexOf(treasureTable.searchParam.audit_status) != -1">
                    <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                    <el-button @click="batchAuditPassedIn()" size="small">{{ t('batchAuditPassed') }}</el-button>
                    <el-button @click="batchAuditRefuseIn(0)" size="small">{{ t('batchAuditRefuse') }}</el-button>
                </div>

                <el-table :data="treasureTable.data" size="large" v-loading="treasureTable.loading" ref="treasureListTableRef"  @selection-change="handleSelectionChange">
                    <template #empty>
                        <span>{{ !treasureTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" :selectable="treasureSelectTableFn" />

                    <el-table-column  :label="t('treasureInfo')" min-width="300">
                        <template #default="{ row }">
                            <div class="flex items-center" >
                                <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                                    <el-image v-if="row.treasure_image" class="w-[70px] h-[70px]" :src="img(row.treasure_image)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" fit="contain" />
                                </div>
                                <div class="ml-2  flex flex-col items-start">
                                    <span :title="row.treasure_name" class="multi-hidden">{{ row.treasure_name }}</span>
                                    <span class="text-primary text-[12px]">{{ row.treasure_sub_name }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="relate_type_name" :label="t('relateTypeName')" min-width="120" />
                    <el-table-column prop="treasure_price" :label="t('treasurePrice')" min-width="120" />
                    <el-table-column  :label="t('reviewTime')" min-width="180">
                        <template #default="{ row }">
                            <span class="multi-hidden">{{ row.create_time }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('currentStatus')" min-width="120">
                        <template #default="{ row }">
                            {{ row.audit_status_name }}
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('operation')" min-width="120"  fixed="right" align="right">
                        <template #default="{ row }">
                            <!-- 审核通过 -->
                            <el-button v-if="row.audit_status == 1"  type="primary" link @click="auditPassedFn(row)">{{ t('auditPassed') }}</el-button>
                            <!-- 审核拒绝 -->
                            <el-button v-if="row.audit_status == 1" type="primary" link @click="auditRefuse(row)">{{ t('auditRefuse') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="treasureTable.page" v-model:page-size="treasureTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="treasureTable.total"
                        @size-change="loadTreasureList()" @current-change="loadTreasureList" />
                </div>

            </div>

        </el-card>

        <!-- 审核拒绝弹框 -->
        <el-dialog v-model="refuseShowDialog" :title="t('auditRefuse')" width="460px" class="diy-dialog-wrap" :destroy-on-close="true">
            <el-form :model="refuseFormData" label-width="90px" ref="refuseFormRef" :rules="refundFormRules" class="page-form">
                <el-form-item :label="t('refuseReason')" prop="audit_reason">
                    <el-input v-model.tirm="refuseFormData.audit_reason" type="textarea" rows="4" clearable class="input-width"  :maxlength="100"  :show-word-limit="true" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="refuseShowDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="loading" @click="confirm(refuseFormRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>

    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'

import { getTreasureList, getTreasureType, treasureJoin, getTreasureStatus, setTreasureAgree, setTreasureRefuse } from '@/addon/sow_community/api/admin/treasure'

const route = useRoute()
const pageName = route.meta.title
const repeat = ref(false)
const refuseFormRef = ref()

const treasureTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        relate_type: '',
        select_type: 'all',
        audit_status: '0'
    }
})

/**
 * 获取商品列表
 */
const loadTreasureList = (page: number = 1) => {
    treasureTable.loading = true
    treasureTable.page = page

    getTreasureList({
        page: treasureTable.page,
        limit: treasureTable.limit,
        ...treasureTable.searchParam
    }).then((res: any) => {
        treasureTable.loading = false
        treasureTable.data = res.data.data
        treasureTable.total = res.data.total
        multipleSelection.value = []
    }).catch(() => {
        treasureTable.loading = false
    })
}

const searchFormRef = ref<FormInstance>()

// 宝贝类型下拉框
const treasureType: any = ref([])
// 宝贝状态
const treasureStatus: any = ref([])

// 初始化数据
const initData = () => {
    // 宝贝类型
    getTreasureType().then((res: any) => {
        const data = res.data
        if (data) {
            treasureType.value = []
            for (const k in data) {
                const obj = {
                    value: k,
                    label: data[k]
                }
                treasureType.value.push(obj)
            }
            if (treasureType.value.length) {
                treasureTable.searchParam.relate_type = treasureType.value[0].value
            }
            loadTreasureList()
        }
    })

    // 宝贝状态
    getTreasureStatus().then((res: any) => {
        const data = res.data
        if (data) {
            for (const k in data) {
                const obj = {
                    value: k,
                    label: data[k]
                }
                treasureStatus.value.push(obj)
            }
        }
    })
}

initData()

const treasureSelectTableFn = (row: any) => {
    if (row.audit_status != 1) {
        return false
    } else {
        return true
    }
}

// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    treasureTable.searchParam.audit_status = tab.props.name
    loadTreasureList()
}

// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    treasureListTableRef.value.toggleAllSelection()
}

const treasureListTableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (multipleSelection.value.length > 0 && multipleSelection.value.length < treasureTable.data.length) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == treasureTable.data.length && treasureTable.data.length && multipleSelection.value.length) {
        toggleCheckbox.value = true
    }
}

// 批量设置审核通过
const batchAuditPassedIn = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedGoodsTips')}`
        })
        return
    }
    ElMessageBox.confirm(t('isAuditPassed'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        const treasureIds: any = []
        multipleSelection.value.forEach((item: any) => {
            treasureIds.push(item.treasure_id)
        })
        const obj = {
            treasure_id: treasureIds
        }
        auditPassedFn(obj)
    }).catch(()=>{})
}

// 批量设置审核拒绝
const batchAuditRefuseIn = (value: any) => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedGoodsTips')}`
        })
        return
    }

    const treasureIds: any = []
    multipleSelection.value.forEach((item: any) => {
        treasureIds.push(item.treasure_id)
    })
    refuseFormData.treasure_id = treasureIds
    refuseShowDialog.value = true
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadTreasureList()
}

const refuseShowDialog = ref(false)
const refuseFormData: Record<string, any> = reactive({
    audit_reason: '',
    treasure_id: ''
})
const loading = ref(false)

// 表单验证规则
const refundFormRules = computed(() => {
    return {
        audit_reason: [
            { required: true, message: t('refuseReasonPlaceholder'), trigger: 'blur' }
        ]
    }
})

// 审核拒绝
const auditRefuse = (data: any) => {
    refuseFormData.treasure_id = [data.treasure_id]
    refuseShowDialog.value = true
}
// 审核通过
let passedRepeat = false
const auditPassedFn = (data: any) => {
    if (passedRepeat) return
    passedRepeat = true
    const treasure_ids: any = typeof data.treasure_id == 'object' ? [...data.treasure_id] : [data.treasure_id]
    setTreasureAgree({'treasure_ids': treasure_ids}).then(res => {
        passedRepeat = false
        loadTreasureList()
    }).catch(() => {
        passedRepeat = false
    })
}
// 拒绝审核,强制下架
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = {
                treasure_ids: [...refuseFormData.treasure_id],
                audit_reason: refuseFormData.audit_reason
            }
            setTreasureRefuse(data).then(res => {
                loading.value = false
                refuseShowDialog.value = false
                refuseFormData.audit_reason = ''
                loadTreasureList()
            }).catch(() => {
                loading.value = false
                refuseShowDialog.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
</style>
