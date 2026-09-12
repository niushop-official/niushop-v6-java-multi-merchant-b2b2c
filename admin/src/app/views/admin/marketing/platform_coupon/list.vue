<template>
    <div class="main-container">
        <!-- 添加优惠券按钮 -->
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-page-title">{{pageName}}</span>
                <el-button type="primary" @click="handleChange">
                    {{ t('addCoupon') }}
                </el-button>
            </div>

             <!-- 搜索 -->
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form label-width="70px" :inline="true" :model="tableData.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item :label="t('title')" prop="title">
                        <el-input v-model.trim="tableData.searchParam.title" :placeholder="t('titlePlaceholder')" class="search-input" clearable />
                    </el-form-item>

                    <el-form-item :label="t('status')" prop="status">
                        <el-select v-model="tableData.searchParam.status" clearable :placeholder="t('statusPlaceholder')" class="search-input">
                            <el-option :label="item" :value="index" v-for="(item, index) in statusList" :key="index"/>
                        </el-select>
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
                    <el-table-column prop="status_name" :label="t('statusName')" min-width="130" />
                    <el-table-column  :label="t('validType')" min-width="210">
                        <template #default="{ row }">
                            <span v-if="row.valid_type == 1">  领取之日起{{ row.length || '' }} 天内有效</span>
                            <span v-else> 使用截止时间至{{ row.valid_end_time || ''}} </span>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('receiveTypeTime')" min-width="210">
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
                        <el-button type="primary" link @click="spreadEvent(row)">{{ t('spreadGoods') }}</el-button>
                            <el-button type="primary" link @click="editEvent(row)" v-if="row.status == 1">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row)" v-if="row.status != 1">{{ t('delete') }}</el-button>
                            <el-button type="primary" link @click="closeEvent(row)" v-if="row.status == 1">{{ t('close') }}</el-button>
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
        <!-- 商品推广弹出框 -->
		<coupon-spread-popup ref="couponSpreadPopupRef"/>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPlatformCouponList, deletePlatformCoupon, closePlatformCoupon, getStatusList } from '@/app/api/marketing_admin'
import { ElMessageBox, FormInstance } from 'element-plus'
import { t } from '@/lang'
import couponSpreadPopup from '@/app/views/admin/marketing/platform_coupon/components/coupon-spread-popup.vue'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title

const statusList = ref([])

// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        title: '',
        status: ''
    }
})

const searchFormRef = ref<FormInstance>()

const loadCouponList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getPlatformCouponList({
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

const setStatusList = async () => {
    statusList.value = await (await getStatusList()).data
}

setStatusList()

// 商品推广
const couponSpreadPopupRef: any = ref(null)

const spreadEvent = (data: any) => {
    couponSpreadPopupRef.value.show(data)
}
// 添加优惠券
const handleChange = () => {
    router.push('/marketing/platform_coupon/add')
}
// 编辑优惠券
const editEvent = (data: any) => {
    router.push('/marketing/platform_coupon/edit?id=' + data.id)
}

// 领取记录
const collectionEvent = (data: any) => {
    router.push('/marketing/platform_coupon/get_list?id=' + data.id)
}

/**
 * 删除
 */
const deleteEvent = (data: any) => {
    ElMessageBox.confirm(t('couponDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deletePlatformCoupon(data.id).then(() => {
            loadCouponList()
        }).catch(() => {
        })
    })
}

// 关闭
const closeEvent = (data: any) => {
    ElMessageBox.confirm(t('couponCloseTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        closePlatformCoupon(data.id).then(() => {
            loadCouponList()
        }).catch(() => {
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadCouponList()
}

</script>
<style lang="scss" scoped>
.main-container {

    .shop-marketing-list-particulars {
        margin-top: 20px;
    }
}
</style>
