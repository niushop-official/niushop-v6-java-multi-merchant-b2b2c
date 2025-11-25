<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" class="w-[100px]" @click="addEvent">
                    {{ t('addCategory') }}
                </el-button>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="mallShopCategoryTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('categoryName')" prop="category_name">
                        <el-input v-model="mallShopCategoryTable.searchParam.category_name"
                            :placeholder="t('categoryNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadMallShopCategoryList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div class="mt-[20px]">
                <el-table :data="mallShopCategoryTable.data" row-key="category_id" size="large"
                    v-loading="mallShopCategoryTable.loading">
                    <template #empty>
                        <span>{{ !mallShopCategoryTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="category_name" :label="t('categoryName')" min-width="120" />
                    <el-table-column :label="t('image')" min-width="100" align="left">
                        <template #default="{ row }">
                            <el-avatar v-if="row.image" :src="img(row.image)" />
                            <img v-else class="w-[50px] h-[50px]" src="@/app/assets/images/category_default.png" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="commission_rate" :label="t('commissionRate')" min-width="120" />
                    <el-table-column prop="sort" :label="t('sortNum')" min-width="120" />
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="100">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.category_id)">{{ t('delete')
                            }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="mallShopCategoryTable.page"
                        v-model:page-size="mallShopCategoryTable.limit" layout="total, sizes, prev, pager, next, jumper"
                        :total="mallShopCategoryTable.total" @size-change="loadMallShopCategoryList()"
                        @current-change="loadMallShopCategoryList" />
                </div>
            </div>

            <CategoryEdit ref="editMallShopCategoryDialog" @complete="loadMallShopCategoryList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getShopCategoryList, deleteShopCategory } from '@/app/api/shop_admin'
import { img } from '@/utils/common'
import { ElMessageBox, FormInstance } from 'element-plus'
import CategoryEdit from '@/app/views/shop/admin/components/category-edit.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const mallShopCategoryTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        category_name: ''
    }
})

/**
 * 获取 商品分类列表
 */
const loadMallShopCategoryList = (page: number = 1) => {
    mallShopCategoryTable.loading = true
    mallShopCategoryTable.page = page
    getShopCategoryList({
        page: mallShopCategoryTable.page,
        limit: mallShopCategoryTable.limit,
        ...mallShopCategoryTable.searchParam
    }).then(res => {
        mallShopCategoryTable.loading = false
        mallShopCategoryTable.data = res.data.data
        mallShopCategoryTable.total = res.data.total
    }).catch(() => {
        mallShopCategoryTable.loading = false
    })
}
loadMallShopCategoryList()

const editMallShopCategoryDialog: Record<string, any> | null = ref(null)

/**
 * 添加 商品分类
 */
const addEvent = () => {
    editMallShopCategoryDialog.value.setFormData()
    editMallShopCategoryDialog.value.showDialog = true
}

/**
 * 编辑 商品分类
 * @param data
 */
const editEvent = (data: any) => {
    editMallShopCategoryDialog.value.setFormData(data)
    editMallShopCategoryDialog.value.showDialog = true
}

/**
 * 删除 商品分类
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('mallShopCategoryDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteShopCategory(id).then(() => {
            loadMallShopCategoryList()
        }).catch(() => {
        })
    })
}
// 重置
const searchFormRef = ref<FormInstance>()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadMallShopCategoryList()
}

</script>

<style lang="scss" scoped></style>
