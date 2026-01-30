<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addCategory') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="categoryTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('categoryName')" prop="category_name">
                        <el-input v-model.trim="categoryTable.searchParam.category_name" :placeholder="t('categoryNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('isShow')" prop="status">
                        <el-select v-model="categoryTable.searchParam.status" :placeholder="t('isShowPlaceholder')" clearable>
                            <el-option  :label="t('open')" :value="1" />
                            <el-option  :label="t('off')" :value="0" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadCategoryList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="categoryTable.data" size="large" v-loading="categoryTable.loading">
                    <template #empty>
                        <span>{{ !categoryTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="category_name" :label="t('categoryName')" min-width="120" />
                    <el-table-column  :label="t('isShow')" min-width="120">
                        <template #default="{ row }">
                            <el-tag class="cursor-pointer" :type="row.status != 0 ? 'success' : 'danger'" @click="showClick(row)">{{ row.status != 0 ? t('open') : t('off') }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" :label="t('sort')" min-width="120">
                        <template #default="{ row }">
                            <el-input v-model.trim="row.sort" class="!w-[100px]" maxlength="8" @blur="sortInputListener(row.sort, row)" />
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.category_id)">{{ t('delete') }}
                            </el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="categoryTable.page" v-model:page-size="categoryTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="categoryTable.total"
                        @size-change="loadCategoryList()" @current-change="loadCategoryList" />
                </div>
            </div>

            <category-edit ref="editCategoryDialog" @complete="loadCategoryList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getCategoryList, deleteCategory, modifyCategorySort, modifyCategoryStatus } from '@/addon/points_mall/api/admin/category'
import { debounce } from '@/utils/common'
import { ElMessageBox, FormInstance, ElMessage } from 'element-plus'
import CategoryEdit from '@/addon/points_mall/views/admin/category/components/category-edit.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const categoryTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        category_name: '',
        status: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取社区分类列表
 */
const loadCategoryList = (page: number = 1) => {
    categoryTable.loading = true
    categoryTable.page = page

    getCategoryList({
        page: categoryTable.page,
        limit: categoryTable.limit,
        ...categoryTable.searchParam
    }).then((res: any) => {
        categoryTable.loading = false
        categoryTable.data = res.data.data
        categoryTable.total = res.data.total
    }).catch(() => {
        categoryTable.loading = false
    })
}
loadCategoryList()

const editCategoryDialog: Record<string, any> | null = ref(null)

/**
 * 添加社区分类
 */
const addEvent = () => {
    editCategoryDialog.value.setFormData()
    editCategoryDialog.value.showDialog = true
}

/**
 * 编辑社区分类
 * @param data
 */
const editEvent = (data: any) => {
    editCategoryDialog.value.setFormData(data)
    editCategoryDialog.value.showDialog = true
}

/**
 * 删除社区分类
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('categoryDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteCategory(id).then(() => {
            loadCategoryList()
        }).catch(() => {
        })
    })
}

const showClick = (row: any) => {
    row.status = row.status === 1 ? 0 : 1
    modifyCategoryStatus({
        category_id: row.category_id,
        status: row.status
    }).then((res: any) => {
    })
}

// 修改排序号
const sortInputListener = debounce((sort, row) => {
    if (isNaN(sort) || !/^\d{0,8}$/.test(sort)) {
        ElMessage({
            type: 'warning',
            message: `${t('sortTips')}`
        })
        return
    }
    if (sort > 99999999) {
        row.sort = 99999999
    }
    modifyCategorySort({
        category_id: row.category_id,
        sort
    }).then((res: any) => {
    })
})

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadCategoryList()
}
</script>

<style lang="scss" scoped>
</style>