<template>
    <div>
        <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
            <el-form-item :label="t('标题')" prop="title" class="form-item-wrap">
                <el-input v-model.trim="tableData.searchParam.title" :placeholder="t('请选择标题')" />
            </el-form-item>
            <el-form-item :label="t('栏目')" prop="category_id" class="form-item-wrap">
                <el-select v-model="tableData.searchParam.category_id" clearable :placeholder="t('请选择栏目')" class="input-width">
                    <el-option :label="t('selectPlaceholder')" value="" />
                    <el-option :label="item.name" :value="item.category_id" v-for="(item, index) in categoryList" :key="index" />
                </el-select>
            </el-form-item>
            <el-form-item class="form-item-wrap">
                <el-button type="primary" @click="loadList()">{{ t('search') }}</el-button>
                <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
            </el-form-item>
        </el-form>
        <el-table :data="tableData.data" size="large" ref="tableRef" v-loading="tableData.loading">
            <template #empty>
                <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
            </template>
            <el-table-column min-width="7%">
                <template #default="{ row }">
                    <el-checkbox v-model="row.checked" @change="handleCheckChange($event,row)" />
                </template>
            </el-table-column>
            <el-table-column prop="category_name" :label="t('栏目')" min-width="20%" />
            <el-table-column prop="title" :show-overflow-tooltip="true" :label="t('标题')" min-width="20%" />
            <el-table-column :label="t('封面')" min-width="15%" align="center">
                <template #default="{ row }">
                    <el-image class="w-12 h-12" v-if="row.image_thumb_small" :src="img(row.image_thumb_small)" fit="contain" />
                </template>
            </el-table-column>
        </el-table>
        <div class="mt-[16px] flex justify-end">
            <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"  @size-change="loadList()" @current-change="loadList" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, nextTick } from 'vue'
import { t } from '@/lang'
import { getArticleSelect, getArticleCategoryAll } from '@/app/api/article'
import { FormInstance, ElMessage } from 'element-plus'
import { img } from '@/utils/common'

const prop = defineProps({
    id: {
        type: [Number, String],
        default: 0
    }
})

const searchFormRef = ref<FormInstance>()
const tableRef = ref()

const tableData: any = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        title: '',
        category_id: ''
    }
})

// 已选数据
const selectData: any = reactive({
    id: prop.id
})

const categoryList = ref<any>([])

const setCategoryList = async () => {
    categoryList.value = await (await getArticleCategoryAll({})).data
}
setCategoryList()
// 获取积分商品列表
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    if (selectData.id) {
        tableData.searchParam.verify_id = [selectData.id]
    }

    getArticleSelect({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data.data

        tableData.data.forEach((item: any) => {
            item.checked = item.id == selectData.id
        })
        tableData.total = res.data.total
        setGoodsSelected()
    }).catch(() => {
        tableData.loading = false
    })
}

loadList()

const handleCheckChange = (isSelect: any, row: any) => {
    if (isSelect) {
        selectData.id = row.id
    } else {
        selectData.id = 0 // 未选中，移除当前
    }
    setGoodsSelected()
}

// 表格设置选中状态
const setGoodsSelected = () => {
    nextTick(() => {
        for (let i = 0; i < tableData.data.length; i++) {
            tableData.data[i].checked = false
            if (selectData.id == tableData.data[i].id) {
                tableData.data[i].checked = true
                Object.assign(selectData, tableData.data[i])
            }
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadList()
}

const getData = () => {
    if (selectData.id == 0) {
        ElMessage({
            type: 'warning',
            message: '请选择文章'
        })
        return
    }
    return {
        name: 'ARTICLE_SELECT',
        title: selectData.title,
        url: `/app/pages/article/detail?id=${selectData.id}`,
        action: '',
        id: selectData.id
    }
}

defineExpose({
    getData
})
</script>

<style lang="scss" scoped>
.form-item-wrap {
    margin-right: 10px !important;
    margin-bottom: 10px !important;

    &.last-child {
        margin-right: 0 !important;
    }
}
</style>
