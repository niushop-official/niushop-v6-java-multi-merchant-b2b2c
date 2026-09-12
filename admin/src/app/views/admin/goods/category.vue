<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addCategory') }}
                </el-button>
            </div>
            <div class="mt-[10px]">
                <el-tabs v-model="currentTab" class="goods-tabs" @tab-click="tabHandleClick">
                    <el-tab-pane v-for="(item,key) in tradeTypeList" :key="key" :label="item" :name="key"></el-tab-pane>
                </el-tabs>
                <el-table :data="categoryTable.data" ref="tableRef" size="large" v-loading="categoryTable.loading" row-key="category_id" :tree-props="{ hasChildren: 'hasChildren', children: 'child_list' }">
                    <template #empty>
                        <span>{{ !categoryTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column :label="t('categoryName')" min-width="200">
                        <template #default="{ row }">
                            <i class="order-0 iconfont icontuodong vues-rank mr-[8px]"></i>
                            <span class="order-2">{{ row.category_name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('image')" min-idth="200" align="left">
                        <template #default="{ row }">
                            <div class="h-[30px]">
                                <el-image class="w-[30px] h-[30px] " :src="img(row.image)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[30px] h-[30px]" src="@/app/assets/images/category_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="is_show" :label="t('isShow')" min-width="200">
                        <template #default="{ row }">
                            <el-switch v-model="row.is_show" :active-value="1" :inactive-value="2" inline-prompt active-text="是" inactive-text="否" @change="(value) => showClick(row, value)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort"  min-width="120">
                        <template #header>
                            <div class="flex items-center">
                                <span class="mr-[4px]">{{t('sort')}}</span>
                                <el-tooltip  class="cursor-pointer" effect="dark" content="排序号为从大到小排序" placement="bottom">
                                    <el-icon><QuestionFilled /></el-icon>
                                </el-tooltip>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="spreadEvent(row)">{{ t('spreadGoodsCategory') }}</el-button>
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row)">{{ t('delete') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
            </div>

            <category-edit ref="editCategoryDialog" :addon="currentTab" @complete="loadCategoryList" />
        </el-card>

        <!-- 商品分类推广弹出框 -->
        <goods-category-spread-popup ref="goodsCategorySpreadPopupRef" />

    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted, nextTick } from 'vue'
import { t } from '@/lang'
import { getCategoryTree, deleteCategory, editCategory, getCategoryTradeType } from '@/app/api/basic_admin'
import { img } from '@/utils/common'
import { ElMessageBox } from 'element-plus'
import categoryEdit from '@/app/views/admin/goods/components/category-edit.vue'
import goodsCategorySpreadPopup from '@/app/views/admin/goods/components/goods-category-spread-popup.vue'
import { useRoute, useRouter } from 'vue-router'
import { cloneDeep } from 'lodash-es'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const categoryTable = reactive({
    loading: true,
    data: []
})

onMounted(() => {
    getCategoryTradeTypeFn()
})

const currentTab = ref('')
const tradeTypeList = ref([])
const getCategoryTradeTypeFn = () => {
    getCategoryTradeType().then(res => {
        tradeTypeList.value = res.data
        currentTab.value = Object.keys(tradeTypeList.value)[0]
        loadCategoryList()
    })
}

// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    currentTab.value = tab.props.name
    loadCategoryList()
}
/**
 * 获取商品分类列表
 */
const loadCategoryList = () => {
    categoryTable.loading = true

    getCategoryTree({
        addon: currentTab.value
    }).then(res => {
        categoryTable.loading = false
        categoryTable.data = res.data
    }).catch(() => {
        categoryTable.loading = false
    })
}
// const updateCategoryFn = (params: any) => {
//     updateCategory(params).then(res => {
//         // getCategoryTree({
//         //     page: categoryTable.page,
//         //     limit: categoryTable.limit,
//         // }).then(res => {
//         //     categoryTable.data = res.data.data
//         //     categoryTable.total = res.data.total
//         // })
//     })
// }

const showClick = (row: any, value: number) => {
    const obj = cloneDeep(row)
    obj.is_show = value
    delete obj.child_list
    editCategory(obj).catch(() => {
        row.is_show = value == 1 ? 2 : 1
    })
}
const editCategoryDialog: Record<string, any> | null = ref(null)

/**
 * 添加商品分类
 */
const addEvent = () => {
    editCategoryDialog.value.setFormData()
    editCategoryDialog.value.showDialog = true
}

/**
 * 编辑商品分类
 * @param data
 */
const editEvent = (data: any) => {
    editCategoryDialog.value.setFormData(data)
    editCategoryDialog.value.showDialog = true
}

/**
 * 删除商品分类
 */
const deleteEvent = (row: any) => {
    ElMessageBox.confirm(!row.child_list || !row.child_list.length ? t('categoryDeleteTips') : t('categoryDeleteTips1'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteCategory(row.category_id).then(() => {
            loadCategoryList()
        }).catch(() => {
        })
    })
}

// 商品分类推广
const goodsCategorySpreadPopupRef: any = ref(null)

const spreadEvent = (data: any) => {
    goodsCategorySpreadPopupRef.value.show(data)
}

</script>

<style lang="scss" scoped>
:deep(.el-table__row) {
    > .el-table__cell:nth-child(1) {
        .cell {
            display: flex;
            align-items: center;

            .el-table__expand-icon,
            .el-table__indent,
            .el-table__placeholder {
                order: 1;
            }

        }

    }
}
</style>