<template>
    <el-dialog v-model="status" :title="t('exportTip')" width="900px" :close-on-click-modal="true" :close-on-press-escape="false" :show-close="true" custom-class="export-dialog" :before-close="close">
        <div v-loading="loadingColumns" class="export-container">
            <template v-if="exportColumns.length > 0">
                <div class="export-header-tip mb-[20px]">请选择您要导出的数据项</div>

                <el-checkbox-group v-model="selectedColumns" @change="handleCheckedCitiesChange">
                    <div class="grid grid-cols-5 gap-[10px]">
                        <div v-for="col in exportColumns" :key="col.key" class="export-item"  :class="{ 'is-active': selectedColumns.includes(col.key), 'is-mandatory': col.is_required }" :style="{ cursor: col.is_required && selectedColumns.includes(col.key) ? 'not-allowed' : 'pointer', opacity: col.is_required ? '0.9' : '1' }" @click="toggleColumn(col.key)">
                            <span class="text-[14px]">{{ col.name }}</span>
                            <el-checkbox :label="col.key" class="hidden-checkbox"></el-checkbox>
                            <div class="active-icon" v-if="selectedColumns.includes(col.key)">
                                <span class="nc-iconfont nc-icon-duihaoV6mm text-primary"></span>
                            </div>
                        </div>
                    </div>
                </el-checkbox-group>
             </template>
             <span v-else>{{ t('exportPlaceholder') }}</span>
        </div>
        <template #footer>
            <div class="flex justify-between items-center w-full">
                <div class="flex items-center">
                    <el-button type="primary" link @click="handleCheckAllChange(true)" class="!text-[var(--el-color-primary)]">全部选择</el-button>
                    <el-button type="primary" link @click="handleCheckAllChange(false)" class="!text-[var(--el-color-primary)] ml-[10px]">全部取消</el-button>
                </div>
                <div class="flex items-center">
                    <el-button @click="close">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="detectionExportFn" :loading="loading">{{ t('exportConfirm') }}</el-button>
                </div>
            </div>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from 'vue'
import { t } from '@/lang'
import { exportData, exportDataCheck, getExportColumns } from '@/app/api/sys'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Select } from '@element-plus/icons-vue'

const prop = defineProps({
    show: {
        type: Boolean,
        default: false
    },
    isSite: {
        type: String,
        default: ''
    },
    type: {
        type: String,
        default: ''
    },
    searchParam: {
        type: Object,
        default: () => {
            return {}
        }
    }
})

const loading = ref(false)
const loadingColumns = ref(false)
const exportColumns = ref<any[]>([])
const selectedColumns = ref<string[]>([])
const checkAll = ref(false)
const isIndeterminate = ref(false)

const status = computed({
    get() {
        return prop.show
    },
    set(value) {
        emit('update:show', value)
    }
})
const emit = defineEmits(['update:show', 'close'])

const router = useRouter()

watch(() => prop.show, async (val) => {
    if (val) {
        loading.value = false
        loadingColumns.value = true
        try {
            const res = await getExportColumns(prop.type)
            if (res.data && Object.keys(res.data).length > 0) {
                exportColumns.value = Object.keys(res.data).map(key => ({
                    key,
                    name: res.data[key].name,
                    is_required: res.data[key].is_required || false
                }))
                // 初始化时选中所有列
                selectedColumns.value = exportColumns.value.map(col => col.key)
                checkAll.value = true
                isIndeterminate.value = false
            } else {
                exportColumns.value = []
            }
        } catch (e) {
            exportColumns.value = []
        } finally {
            loadingColumns.value = false
        }
    }
})

const handleCheckAllChange = (val: any) => {
    if (val) {
        // 全选
        selectedColumns.value = exportColumns.value.map(col => col.key)
    } else {
        // 全部取消,但保留必选列
        const mandatoryColumns = exportColumns.value.filter(col => col.is_required).map(col => col.key)
        selectedColumns.value = mandatoryColumns
    }
    isIndeterminate.value = false
    checkAll.value = val
}

const handleCheckedCitiesChange = (value: string[]) => {
    const checkedCount = value.length
    checkAll.value = checkedCount === exportColumns.value.length
    isIndeterminate.value = checkedCount > 0 && checkedCount < exportColumns.value.length
}

const toggleColumn = (key: string) => {
    const column = exportColumns.value.find(col => col.key === key)

    // 如果是必选列,不允许取消选中
    if (column && column.is_required) {
        const index = selectedColumns.value.indexOf(key)
        if (index > -1) {
            // 已选中,不允许取消
            return
        } else {
            // 未选中,允许选中
            selectedColumns.value.push(key)
        }
    } else {
        // 普通列,允许切换
        const index = selectedColumns.value.indexOf(key)
        if (index > -1) {
            selectedColumns.value.splice(index, 1)
        } else {
            selectedColumns.value.push(key)
        }
    }
    handleCheckedCitiesChange(selectedColumns.value)
}

/**
 * 导出报表并跳转到下载页
 */
const detectionExportFn = () => {
    if (selectedColumns.value.length == 0) {
        ElMessage.error('最少需选择一项数据')
        return false
    }
    loading.value = true
    const url = router.resolve({
        path: prop.isSite ? '/site/shop/setting/export' : '/admin/setting/export'
    })

    // Construct the object for export_column
    let exportColumnParam: any = null
    if (exportColumns.value.length > 0 && selectedColumns.value.length > 0) {
        exportColumnParam = {}
        selectedColumns.value.forEach(key => {
            const col = exportColumns.value.find(c => c.key === key)
            if (col) {
                exportColumnParam[key] = { name: col.name }
            }
        })
    }

    const params = { page: 1, limit: 1, ...prop.searchParam }
    if (exportColumnParam) {
        params.export_column = exportColumnParam
    }

    exportDataCheck(prop.type, params).then((res: any) => {
        if (res.data) {
            const exportParams = { ...prop.searchParam }
            if (exportColumnParam) {
                exportParams.export_column = exportColumnParam
            }
            exportData(prop.type, exportParams).then(() => {
                loading.value = false
                emit('close', false)
                setTimeout(() => {
                    window.open(url.href)
                }, 100)
            })
        } else {
            loading.value = false
            ElMessage.error(res.msg)
        }
    }).catch(() => {
        loading.value = false
    })
}
// 关闭弹框
const close = () => {
    emit('close', false)
}
</script>

<style lang="scss" scoped>
.export-container {
    padding: 10px;
    min-height: 300px;
}

.export-header-tip {
    background-color: var(--el-color-primary-light-9);
    border: 1px solid var(--el-color-primary-light-8);
    padding: 12px 20px;
    border-radius: 4px;
    color: var(--el-text-color-regular);
    font-size: 14px;
}

.export-item {
    position: relative;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    padding: 12px 10px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;
    user-select: none;
    height: 45px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-regular);

    // &:hover {
    //     border-color: var(--el-color-primary);
    //     color: var(--el-color-primary);
    // }

    &.is-active {
        border-color: var(--el-color-primary);
        color: var(--el-color-primary);
        background-color: #fff;
    }

    .hidden-checkbox {
        display: none;
    }

    .active-icon {
        position: absolute;
        bottom: -7px;
        right: -6px;
        background-color: #fff;
        border-radius: 50%;
        width: 16px;
        height: 16px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
}

:deep(.export-dialog) {
    .el-dialog__header {
        border-bottom: 1px solid var(--el-border-color-lighter);
        margin-right: 0;
        padding-bottom: 15px;
    }
    .el-dialog__body {
        padding: 20px;
    }
    .el-dialog__footer {
        border-top: 1px solid var(--el-border-color-lighter);
        padding-top: 15px;
    }
}
</style>
