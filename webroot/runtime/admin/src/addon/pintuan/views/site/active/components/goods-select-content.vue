<template>
    <div>
        <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
            <el-form-item :label="t('goodsSelectPopupGoodsName')" prop="name" class="form-item-wrap">
                <el-input v-model.trim="tableData.searchParam.goods_name" :placeholder="t('goodsSelectPopupGoodsNamePlaceholder')" maxlength="60" />
            </el-form-item>
            <el-form-item class="form-item-wrap">
                <el-button type="primary" @click="loadGoodsList()">{{ t('search') }}</el-button>
                <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
            </el-form-item>
        </el-form>

        <el-table :data="tableData.data" size="large" v-loading="tableData.loading" ref="goodsListTableRef">
            <template #empty>
                <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
            </template>

            <el-table-column min-width="7%">
                <template #default="{ row }">
                    <el-checkbox v-model="row.checked" @change="handleCheckChange($event,row)" />
                </template>
            </el-table-column>

            <el-table-column prop="title" :label="t('名称')" min-width="25%" >
                <template #default="{ row }">
                    <div class="flex items-center cursor-pointer pr-[15px]">
                        <div class="min-w-[60px] h-[60px] flex items-center justify-center">
                            <el-image v-if="row.cover_thumb_small" class="w-[60px] h-[60px]" :src="img(row.cover_thumb_small)" fit="contain">
                                <template #error>
                                    <div class="image-slot">
                                        <img class="w-[60px] h-[60px]" src="@/addon/pintuan/assets/goods_default.png" />
                                    </div>
                                </template>
                            </el-image>
                            <img v-else class="w-[60px] h-[60px]" src="@/addon/pintuan/assets/goods_default.png" fit="contain" />
                        </div>
                        <div class="ml-2 flex flex-col items-start">
                            <span :title="row.title" class="multi-hidden leading-[1.4]">{{ row.title }}</span>
                        </div>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="type_name" :label="t('团购价')" min-width="15%" >
                <template #default="{ row }">
                    ￥{{ Number.parseFloat(row.show_price).toFixed(2)  }}
                </template>
            </el-table-column>
            <el-table-column :label="t('活动名称')" min-width="18%">
                <template #default="{ row }">
                    {{ row.pintuan.title }}
                </template>
            </el-table-column>
            <el-table-column :label="t('活动类型')" min-width="15%">
                <template #default="{ row }">
                    {{ row.pintuan.type === 'normal' && '普通' || row.pintuan.type === 'ladder' && '阶梯' || row.pintuan.type === 'referral' && '老带新' }}
                </template>
            </el-table-column>
        </el-table>
        <div class="mt-[16px] flex">
            <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit"
                layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
                @size-change="loadGoodsList()" @current-change="loadGoodsList" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive, nextTick } from 'vue'
import { cloneDeep } from 'lodash-es'
import { img } from '@/utils/common'
import { ElMessage } from 'element-plus'
import { getPintuanDiyGoods } from '@/addon/pintuan/api/admin/active'

const prop = defineProps({
    goodsId: {
        type: [Number, String],
        default: 0
    }
})

// 已选数据
const selectData: any = reactive({
    pintuan_id: prop.goodsId
})

const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        goods_name: '',
        verify_goods_ids: ''
    }
})
const searchFormRef = ref()

const goodsListTableRef = ref()
/**
 * 获取商品列表
 */
const loadGoodsList = (page: number = 1, callback: any = null) => {
    tableData.loading = true
    tableData.page = page
    if (selectData.pintuan_id) {
        tableData.searchParam.verify_pintuan_ids = [selectData.pintuan_id]
    }
    const searchData: any = cloneDeep(tableData.searchParam);
    getPintuanDiyGoods({
        page: tableData.page,
        limit: tableData.limit,
        ...searchData
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.data.forEach((item: any) => {
            item.checked = item.id == selectData.pintuan_id
        })
        tableData.total = res.data.total

        setGoodsSelected()
    }).catch(() => {
        tableData.loading = false
    })
}
loadGoodsList()
const handleCheckChange = (isSelect: any, row: any) => {
    if (isSelect) {
        selectData.pintuan_id = row.id
    } else {
        selectData.pintuan_id = 0 // 未选中，移除当前
    }
    setGoodsSelected()
}
// 表格设置选中状态
const setGoodsSelected = () => {
    nextTick(() => {
        for (let i = 0; i < tableData.data.length; i++) {
            tableData.data[i].checked = false
            if (selectData.pintuan_id == tableData.data[i].id) {
                tableData.data[i].checked = true
                Object.assign(selectData, tableData.data[i])
            }
        }
    })
}
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    tableData.searchParam.goods_name = ''

    loadGoodsList()
}

const getData = () => {
    if (selectData.pintuan_id == 0) {
        ElMessage({
            type: 'warning',
            message: `${ t('请选择商品') }`
        })
        return;
    }
    return {
        name: 'PINTUAN_GOODS_SELECT',
        title: selectData.title,
        url: `/addon/pintuan/pages/goods/detail?pintuan_id=${ selectData.pintuan_id }`,
        action: '',
        goodsId: selectData.default_sku.goods_id
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
