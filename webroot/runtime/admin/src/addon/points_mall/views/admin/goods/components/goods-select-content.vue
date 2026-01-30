<template>
    <div>
        <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
            <el-form-item :label="t('goodsSelectPopupGoodsName')" prop="active_name" class="form-item-wrap">
                <el-input v-model.trim="tableData.searchParam.name" :placeholder="t('goodsSelectPopupGoodsNamePlaceholder')" />
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
            <el-table-column :label="t('goodsSelectPopupGoodsInfo')" min-width="30%" >
                <template #default="{ row }">
                    <div class="flex items-center cursor-pointer">
                        <div class="min-w-[60px] h-[60px] flex items-center justify-center">
                            <el-image v-if="row.goods_cover_thumb_small" class="w-[60px] h-[60px]" :src="img(row.goods_cover_thumb_small)" fit="contain">
                                <template #error>
                                    <div class="image-slot">
                                        <img class="w-[60px] h-[60px]" src="@/addon/points_mall/assets/goods_default.png" />
                                    </div>
                                </template>
                            </el-image>
                            <img v-else class="w-[70px] h-[60px]" src="@/addon/points_mall/assets/goods_default.png" fit="contain" />
                        </div>
                        <div class="ml-2">
                            <span :title="row.goods_name" class="multi-hidden">{{ row.goods_name }}</span>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column :label="t('goodsSelectPopupPrice')" min-width="20%">
                <template #default="{ row }">
                    <p v-if="Number(row.point)">{{ row.point }}{{ t('goodsSelectPopupGoodsPointUnit') }}</p>
                    <p v-if="Number(row.price)">{{ row.price }}{{ t('goodsSelectPopupGoodsPriceUnit') }}</p>
                </template>
            </el-table-column>
            <el-table-column :label="t('goodsSelectPopupStock')" min-width="15%" >
                <template #default="{ row }">
                    <span>{{ row.stock }}</span>
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
import { getGoodsPageList } from '@/addon/points_mall/api/admin/goods'
import { FormInstance, ElMessage } from 'element-plus'
import { img } from '@/utils/common'

const prop = defineProps({
    goods_id: {
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
        name: '',
        status: '1',
        verify_goods_ids: []
    }
})

// 已选数据
const selectData: any = reactive({
    goods_id: prop.goods_id
})

// 获取积分商品列表
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    if (selectData.goods_id) {
        tableData.searchParam.verify_goods_ids = [selectData.goods_id]
    }

    getGoodsPageList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data.data

        tableData.data.forEach((item: any) => {
            item.checked = item.goods_id == selectData.goods_id
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
        selectData.goods_id = row.goods_id
    } else {
        selectData.goods_id = 0 // 未选中，移除当前
    }
    setGoodsSelected()
}

// 表格设置选中状态
const setGoodsSelected = () => {
    nextTick(() => {
        for (let i = 0; i < tableData.data.length; i++) {
            tableData.data[i].checked = false
            if (selectData.goods_id == tableData.data[i].goods_id) {
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
    if (selectData.goods_id == 0) {
        ElMessage({
            type: 'warning',
            message: `${ t('pointExchangeSelectContentTips') }`
        })
        return
    }
    return {
        name: 'POINTS_MALL_GOODS_SELECT',
        title: selectData.goods_name,
        url: `/addon/points_mall/pages/goods/detail?goods_id=${ selectData.goods_id }`,
        action: '',
        goods_id: selectData.goods_id
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
