<template>
    <div class="main-container">
        <!-- 添加优惠券按钮 -->
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-[20px]">{{pageName}}</span>
            </div>

             <!-- 搜索 -->
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form label-width="70px" :inline="true" :model="tableData.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item :label="t('title')" prop="title">
                        <el-input v-model="tableData.searchParam.title" :placeholder="t('titlePlaceholder')" maxlength="20" class="search-input" clearable/>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadCouponList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

             <!-- 列表 -->
            <div class="mt-[10px]">
                <el-table :data="tableData.data" size="large" v-loading="tableData.loading">
                    <template #empty>
                        <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
                    </template>

                    <el-table-column prop="title" :label="t('title')" min-width="130" />
                    <el-table-column  :show-overflow-tooltip="true" :label="t('shopInfo')" min-width="160">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <div class="flex flex flex-col">
                                <span>{{ row.site.site_name || '' }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                    <el-table-column prop="type_name" :label="t('type')" min-width="130" />
                    <el-table-column prop="price" :label="t('price')" min-width="130" >
                        <template #default="{ row }">
                            <span >¥{{row.price}}</span>
                        </template>
                    </el-table-column>

                    <el-table-column  :label="t('receiveType')" min-width="130" >
                        <template #default="{ row }">
                            <span v-if="row.receive_type == 1">是</span>
                            <span v-else >否</span>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('count')" min-width="160" :show-overflow-tooltip="true">
                        <template #header>
                            <div style="display: inline-flex; align-items: center">
                                <span class="mr-[5px]">{{ t('count') }}</span>
                                <el-tooltip class="box-item" effect="light" placement="top">
                                    <template #content>{{t('countTips1')}}<br />{{t('countTips2')}}<br />{{t('countTips3')}}<br />{{t('countTips4')}} </template>
                                    <el-icon color="#666">
                                        <QuestionFilled />
                                    </el-icon>
                                </el-tooltip>
                            </div>
                        </template>
                        <template #default="{ row }">
                            <div v-if="row.receive_type == 1 && row.sum_count != '-1'">{{ t('sumCount') }}：{{ row.remain_count || 0 }} / {{ row.sum_count || 0 }} 张</div>
                            <div v-else>{{ t('sumCount') }}：{{ t('noLimit') }}</div>
                            <div>{{ t('receiveCount') }}：{{ row.receive_count || 0 }} 张</div>
                            <div>{{ t('giveCount') }}：{{ row.give_count || 0 }} 张</div>
                            <div>{{ t('receiveUseCount') }}：{{ row.receive_use_count || 0 }} 张</div>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('threshold')" min-width="130" >
                        <template #default="{ row }">
                            <span v-if="row.min_condition_money == '0.00'">无门槛</span>
                            <span v-else >满{{ row.min_condition_money }}元可用</span>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('validType')" min-width="220">
                        <template #default="{ row }">
                            <span v-if="row.valid_type == 1">  领取之日起{{ row.length || '' }} 天内有效</span>
                            <span v-else> 使用截止时间至{{ row.valid_end_time || ''}} </span>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('receiveTypeTime')" min-width="220">
                        <template #default="{ row }">
                            <div v-if="row.receive_type == 1">
                                <div v-if="parseInt(row.start_time) != 0 && parseInt(row.end_time) !=0" class="flex flex-col">
                                    <span>开始时间：{{row.start_time}}</span>
                                    <span>结束时间：{{row.end_time}}</span>
                                </div>
                                <div v-else>不限时</div>
                            </div>
                            <span v-else>--</span>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="160">
                       <template #default="{ row }">
							<el-button type="primary" link @click="collectionEvent(row)">{{ t('receive') }}</el-button>
						</template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
                        @size-change="loadCouponList()" @current-change="loadCouponList" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCouponList } from '@/app/api/marketing_admin'
import { FormInstance } from 'element-plus'
import { t } from '@/lang'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title

// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        title: ''
    }
})

const searchFormRef = ref<FormInstance>()

// 选中数据
// const selectData = ref<any[]>([])

const loadCouponList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getCouponList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
    }).catch(() => {
        tableData.loading = false
    })
}
loadCouponList()

// 领取记录
const collectionEvent = (data: any) => {
    router.push('/marketing/coupon/get_list?id=' + data.id)
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadCouponList()
}

</script>
<style lang="scss" scoped>
.main-container {

    .mall-marketing-list-particulars {
        margin-top: 20px;
    }
}
</style>
