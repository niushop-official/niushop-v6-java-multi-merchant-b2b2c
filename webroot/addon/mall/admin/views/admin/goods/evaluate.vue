<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="evaluateTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('goodsName')" prop="goods_name">
                        <el-input v-model="evaluateTable.searchParam.goods_name" clearable :placeholder="t('goodsNamePlaceholder')" class="input-width" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadEvaluateList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="evaluateTable.data" size="large" v-loading="evaluateTable.loading">
                    <template #empty>
                        <span>{{ !evaluateTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column :label="t('goodsInfo')" min-width="120" align="left">
                        <template #default="{ row }">
                            <div class="flex cursor-pointer">
                                <div class="flex items-center min-w-[50px] mr-[10px]">
                                    <el-image v-if="row.goods.goods_cover_thumb_small" class="w-[50px] h-[50px]"
                                        :src="img(row.goods.goods_cover_thumb_small)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[50px] h-[50px]"
                                                    src="@/addon/mall/assets/goods_default.png" />
                                            </div>
                                        </template>
                                    </el-image>

                                    <img v-else class="w-[50px] h-[50px]" src="@/addon/mall/assets/goods_default.png"
                                        fit="contain" />
                                </div>
                                <div class="flex">
                                    <p class="multi-hidden">{{ row.goods.goods_name }}</p>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('content')" min-width="240" align="left">
                        <template #default="{ row }">
                            <div>
                                <p class="text-[14px]">{{ row.content }}</p>
                                <div class="flex flex-wrap mt-[10px]" v-if="row.images?.length > 0">
                                    <div v-for="(imageItem, imageIndex) in row.images" :key="imageIndex" class="mr-4">
                                        <el-image v-if="imageItem" class="w-[40px] h-[40px]" :src="img(imageItem)"
                                            fit="contain" :preview-src-list="row.previewList" :zoom-rate="1.2" :hide-on-click-modal="true"
                                            :max-scale="7" :min-scale="0.2" :initial-index="imageIndex">
                                            <template #error>
                                                <div class="image-slot">
                                                    <img class="w-[40px] h-[40px]"
                                                        src="@/addon/mall/assets/goods_default.png" />
                                                </div>
                                            </template>
                                        </el-image>
                                        <img v-else class="w-[40px] h-[40px]" src="@/addon/mall/assets/goods_default.png" />
                                    </div>
                                </div>
                                <p class="mt-[15px] text-[14px]" v-if="row.explain_first"><span class="text-[#ff7f5b]">{{
                                    t('explainFirst') }}：</span>{{ row.explain_first }}</p>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('scores')" min-width="110" align="left">
                        <template #default="{ row }">
                            <el-rate v-model="row.scores" disabled />
                        </template>
                    </el-table-column>
                    <el-table-column prop="audit_name" :label="t('auditName')" min-width="80" />
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="120" />
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="evaluateTable.page" v-model:page-size="evaluateTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="evaluateTable.total"
                        @size-change="loadEvaluateList()" @current-change="loadEvaluateList" />
                </div>
            </div>
        </el-card>

    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getEvaluateList } from '@/addon/mall/api/admin/goods'
import { img } from '@/utils/common'
import { FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const evaluateTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        goods_name: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取商品评价列表
 */
const loadEvaluateList = (page: number = 1) => {
    evaluateTable.loading = true
    evaluateTable.page = page

    getEvaluateList({
        page: evaluateTable.page,
        limit: evaluateTable.limit,
        ...evaluateTable.searchParam
    }).then(res => {
        evaluateTable.loading = false
        evaluateTable.data = res.data.data
        evaluateTable.data.map((item: any) => {
            // 先判断 images 是否为有效数组，空值则赋值为空数组
            item.previewList = item.images ? item.images.map((el: any) => {
                return img(el)
            }) : []; // 空值时赋值为空数组，避免 previewList 为 undefined
            return item
        })
        evaluateTable.total = res.data.total
    }).catch(() => {
        evaluateTable.loading = false
    })
}
loadEvaluateList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadEvaluateList()
}
</script>

<style lang="scss" scoped>
/* 多行超出隐藏 */
.multi-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}</style>
