<template>
    <el-dialog v-model="showDialog" :title="t('选择商品')" width="1000px" :destroy-on-close="true">
        <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef" class="mt-3">
            <el-form-item :label="t('goodsSelectPopupGoodsName')" prop="goods_name" class="form-item-wrap">
                <el-input v-model.trim="tableData.searchParam.goods_name" :placeholder="t('goodsSelectPopupGoodsNamePlaceholder')" maxlength="60" />
            </el-form-item>
            <el-form-item class="form-item-wrap">
                <el-button type="primary" @click="loadList()">{{ t('search') }}</el-button>
                <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
            </el-form-item>
        </el-form>
        <el-table  :data="tableData.data" size="large" v-loading="tableData.loading" ref="goodsTableRef" max-height="400"  class="hide-selection-header">
            <template #empty>
                <span>{{ !tableData.loading ? t("emptyData") : "" }}</span>
            </template>
            <!-- 保留复选框样式，但通过逻辑限制单选 -->
            <el-table-column min-width="3%">
                <template #default="{ row }">
                    <el-checkbox v-model="row.checked" @change="handleCheckChange($event,row)" />
                </template>
            </el-table-column>
              <!-- <el-table-column type="selection" width="55" :selectable="isSelectable" /> -->
            <el-table-column prop="goods_id" :label="t('商品信息')" min-width="45%">
                <template #default="{ row }">
                    <div class="flex cursor-pointer">
                        <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                            <el-image v-if="row.cover" class="w-[70px] h-[70px]" :src="img(row.cover)" fit="contain">
                                <template #error>
                                    <div class="image-slot">
                                        <img class="w-[70px] h-[70px]" src="@/addon/pintuan/assets/goods_default.png" />
                                    </div>
                                </template>
                            </el-image>
                            <img v-else class="w-[70px] h-[70px]" src="@/addon/pintuan/assets/goods_default.png" fit="contain" />
                        </div>
                        <div class="ml-2  flex flex-col items-start justify-between">
                            <span :title="row.title" class="multi-hidden">{{ row.title }}</span>
                            <div v-if="row.supply_id">供货商：{{ row.supply_name  }}</div>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="price" :label="t('价格')" min-width="25%">
                <template #default="{ row }">
                    <span>{{ row.price }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="price" :label="t('库存')" min-width="25%">
                <template #default="{ row }">
                    <span>{{ row.stock }}</span>
                </template>
            </el-table-column>
        </el-table>
        <div class="mt-[16px] flex justify-end">
            <el-pagination v-model:current-page="tableData.page"  v-model:page-size="tableData.limit"
            layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadList()"  @current-change="loadList" />
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="onSave()" :disabled="Object.keys(selectData).length == 0">{{ t('confirm') }}</el-button>
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, nextTick } from 'vue'
import { t } from '@/lang'
import { type TableInstance, type FormInstance, ElMessage } from 'element-plus'
import { getPintuanGoodsSelect } from '@/addon/pintuan/api/site/active'
import { img } from '@/utils/common'

const showDialog = ref(false)
const emit = defineEmits(['confirm'])

// 已选数据
const selectData: any = reactive({})
const goodsTableRef = ref<TableInstance>()

const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        goods_name: '',
    }
})


const loadList = (page: number = 1) => {
    tableData.page = page
    tableData.loading = true
    getPintuanGoodsSelect({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then((res) => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
        tableData.data.forEach((item: any) => {
            item.checked = item.source_id == selectData.source_id
        })
    }).catch(() => {
        tableData.loading = false
    })
}

const searchFormRef = ref<FormInstance>()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadList()
}


const handleCheckChange = (isSelect: boolean, row: any) => {
    if (isSelect) {
    // 单选，直接把当前行赋值给 selectData
        Object.assign(selectData, row)
    } else {
    // 再次点击取消，清空对象
        for (const key in selectData) {
            delete selectData[key]
        }
    }
    setGoodsSelected()
}

// 表格设置选中状态
const setGoodsSelected = () => {
    nextTick(() => {
        for (let i = 0; i < tableData.data.length; i++) {
            tableData.data[i].checked = selectData.source_id === tableData.data[i].source_id
        }
    })
}

// 确认保存
const onSave = () => {
    if (selectData && Object.keys(selectData).length > 0) {
        emit('confirm', {
            data: selectData
        })
        showDialog.value = false
    }
}


// 弹窗显示接口
const show = () => {
    // clearSelectData()
    loadList()
    showDialog.value = true
}

defineExpose({
    showDialog,
    show,
})
</script>


<style lang="scss" scoped>
.hide-selection-header {
  // 定位到表头的复选框单元格
    :deep(.el-table__header-wrapper .el-table-column--selection .cell) {
    display: none; // 隐藏表头复选框
  }
}
</style>
