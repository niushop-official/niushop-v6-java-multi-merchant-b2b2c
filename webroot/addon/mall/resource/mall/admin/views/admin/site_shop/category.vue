<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addSiteCategory">
                    {{ t('addcategory') }}
                </el-button>
            </div>
            <div class="mt-[10px]">
                <el-table :data="siteCategoryList"  ref="tableRef" size="large" v-loading="loading" row-key="category_id" :tree-props="{ children: 'child_list', hasChildren: 'hasChildren' }">
                    <template #empty>
                        <span>{{ !loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="name" :label="t('name')">
                        <template #default="{ row }">
                            <i class="order-0 iconfont icontuodong vues-rank mr-[8px]"></i>
                            <span class="order-2">{{ row.name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="service_rate" :label="t('service_rate')">
                        <template #default="{ row }">
                            <span v-if="row.pid == 0">{{ row.service_rate }}</span>
                            <span v-else></span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" :label="t('sort')" class-name="sort-column">
                        <template #default="{ row }">
                            <el-input class="!w-[70px]" v-model="row.sort" @input="handleSortChange(row)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" :label="t('status')">
                        <template #default="{ row }">
                            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="(value) => handleStatusChange(row, value)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" :label="t('create_time')">
                        <template #default="{ row }">
                            <span>{{ row.create_time }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('action')" fixed="right" align="right">
                        <template #default="{ row }">
                            <el-button v-if="row.pid == 0" type="primary" link @click="addSiteCategory(row)">{{ t('addChild') }}</el-button>
                            <el-button type="primary" link @click="editSiteCategory(row)">{{ t('edit') }}</el-button>
                            <el-button v-if="row.pid != 0" type="primary" link @click="deleteSiteCategory(row)">{{ t('delete') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            <site-category-edit ref="editSiteCategoryDialog" @complete="getSiteCategoryList" />
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { t } from '@/lang'
import SiteCategoryEdit from '@/addon/mall/views/admin/site_shop/components/edit.vue'
import { getSiteCategoryList as getSiteCategoryListApi, deleteSiteCategory as deleteSiteCategoryApi, editSiteCategory as editSiteCategoryApi } from '@/app/api/site_category'
import Sortable from 'sortablejs'
import { useTemplateRefsList } from '@vueuse/core'
import { useRoute, useRouter } from 'vue-router'

const tableRef = useTemplateRefsList<HTMLElement>()
const loading = ref(true)
const siteCategoryList = ref<any[]>([])
const editSiteCategoryDialog: Record<string, any> | null = ref(null)
const route = useRoute()
const router = useRouter()
const addon = ref('mall')
const pageName = route.meta.title
// 正则表达式
const regExp = {
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/
}

onMounted(() => {
    nextTick(() => {
        rowDrop()
    })
    getSiteCategoryList()
})

// 拖拽排序
const rowDrop = () => {
    const tbody = tableRef.value[0]?.$el.querySelector('.el-table__body-wrapper tbody')
    if (tbody) {
        Sortable.create(tbody, {
            handle: '.vues-rank',
            animation: 300,
            onMove: ({ dragged, related }) => {
                const oldRow = siteCategoryList.value[dragged.rowIndex]
                const newRow = siteCategoryList.value[related.rowIndex]
                if (oldRow.pid !== newRow.pid) {
                    return false
                }
            },
            onStart: () => {
                // 开始拖拽前的处理
            },
            onEnd: e => {
                const oldRow = siteCategoryList.value[e.oldIndex]
                const newRow = siteCategoryList.value[e.newIndex]
                if (e.oldIndex === e.newIndex || oldRow.pid !== newRow.pid) return

                // 处理拖拽后的排序逻辑
                const currRow = siteCategoryList.value.splice(e.oldIndex, 1)[0]
                siteCategoryList.value.splice(e.newIndex, 0, currRow)

                // 更新排序
                const pid = newRow.pid
                const currentRows = siteCategoryList.value
                    .filter(c => c.pid === pid)
                    .map((item, index) => {
                        return {
                            category_id: item.category_id,
                            sort: 9999 - index
                        }
                    })

                // 这里可以添加调用后端API更新排序的逻辑
            }
        })
    }
}

const getSiteCategoryList = async () => {
    loading.value = true
    try {
        const response = await getSiteCategoryListApi({ addon: addon.value })
        siteCategoryList.value = response.data
    } catch (error) {
        ElMessage.error('获取行业类目列表失败')
    } finally {
        loading.value = false
    }
}

const addSiteCategory = (row: any = {}) => {
    // 标记为添加子级模式
    if (row.category_id) {
        row.isAddChild = true
    }
    editSiteCategoryDialog.value?.setFormData(row, addon.value)
}

const editSiteCategory = (row: any) => {
    editSiteCategoryDialog.value?.setFormData(row, addon.value)
}

const deleteSiteCategory = (row: any) => {
    ElMessageBox.confirm(
        !row.child_list || !row.child_list.length ? '确定要删除该行业类目吗？' : '该行业类目下有子类目，确定要删除吗？',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(async () => {
        try {
            await deleteSiteCategoryApi(row.category_id)
            ElMessage.success('删除成功')
            getSiteCategoryList()
        } catch (error) {
            ElMessage.error('删除失败')
        }
    })
}

const handleSortChange = async (row: any) => {
    if (isNaN(row.sort) || !regExp.number.test(row.sort)) {
        ElMessage({
            type: 'warning',
            message: `${t('sortTips')}`
        })
        return
    }
    try {
        await editSiteCategoryApi({ ...row, category_id: row.category_id })
    } catch (error) {
        // getSiteCategoryList()
    }
}

const handleStatusChange = async (row: any, value: boolean) => {
    try {
        await editSiteCategoryApi({ ...row, category_id: row.category_id, status: value ? 1 : 0 })
    } catch (error) {
        getSiteCategoryList()
    }
}

</script>

<style scoped>
:deep(.el-table__row) {
    > .el-table__cell:nth-child(1) {
        .cell {
            display: flex;
            align-items: center;
            .el-table__expand-icon,
            .el-table__placeholder {
                order: 1;
            }
        }
    }
}
</style>
