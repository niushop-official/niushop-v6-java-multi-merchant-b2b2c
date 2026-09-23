<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="right">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" label-width="70px" :model="localRecordTable.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item :label="t('交易单号')" prop="trade_no">
                        <el-input v-model.trim="localRecordTable.searchParam.trade_no" :placeholder="t('请输入交易单号')" class="search-input" clearable />
                    </el-form-item>
                    <el-form-item :label="t('deliveryNo')" prop="delivery_no">
                        <el-input v-model.trim="localRecordTable.searchParam.delivery_no" :placeholder="t('deliveryNoPlaceholder')" class="search-input" clearable />
                    </el-form-item>
                    <el-form-item :label="t('交易类型')" prop="trade_type">
                        <el-select v-model="localRecordTable.searchParam.trade_type" :placeholder="t('请选择交易类型')" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,key) in tradeType" :key="key" :label="item" :value="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadLocalDeliveryList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div class="mt-[10px]">
                <el-table :data="localRecordTable.data" size="large" v-loading="localRecordTable.loading">
                    <template #empty>
                        <span>{{ !localRecordTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="delivery_service_name" :label="t('deliveryServiceName')" min-width="120" />
                    <el-table-column prop="trade_type_name" :label="t('交易类型')" min-width="120" />
                    <el-table-column prop="body" :label="t('goodsInfo')" min-width="120" />
                    <el-table-column prop="delivery_no" :label="t('deliveryNo')" min-width="120" />
                    <el-table-column prop="trade_no" :label="t('交易单号')" min-width="120" />
                    <el-table-column prop="delivery_start" :label="t('deliveryStart')" min-width="120">
                        <template #default="{ row }">
                            <div class="cursor-pointer" @click="openMap(row,'start')">
                                <span>{{ row.delivery_start }}</span>
                                <span class="text-primary ml-[4px]  nc-iconfont nc-icon-chakandituV6xx"></span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="delivery_end" :label="t('deliveryEnd')" min-width="120">
                        <template #default="{ row }">
                            <div class="cursor-pointer"  @click="openMap(row, 'end')">
                                <span>{{ row.delivery_end }}</span>
                                <span class="text-primary ml-[4px] cursor-pointer nc-iconfont nc-icon-chakandituV6xx"></span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="status_name" :label="t('deliveryStatus')" min-width="120" />
                    <el-table-column  :label="t('deliveryDistance')" min-width="120">
                        <template #default="{ row }">
                            <div v-if="row.delivery_distance">
                                {{ distance(row.delivery_distance) }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('deliveryMoney')" min-width="120">
                        <template #default="{ row }">
                           ￥{{row.delivery_money }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="120" />
                    <el-table-column prop="remark" :label="t('remark')" min-width="120" />
                    <el-table-column :label="t('operation')" fixed="right" min-width="120" align="right">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row)">{{ t('detail') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="localRecordTable.page" v-model:page-size="localRecordTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="localRecordTable.total"
                        @size-change="loadLocalDeliveryList()" @current-change="loadLocalDeliveryList" />
                </div>
            </div>
        </el-card>
        <third-delivery-detail ref="thirdDeliveryDetailRef" />
        <!-- 查看地图 -->
        <map-info ref="mapInfoRef" />
    </div>
</template>

<script setup lang="ts">
import { FormInstance } from 'element-plus'
import { t } from '@/lang'
import { reactive, ref } from 'vue'
import { distance } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getDeliveryRecordList } from '@/app/api/delivery_admin'
import { getTrade } from '@/app/api/basic_site'
import ThirdDeliveryDetail from '@/app/views/admin/local_delivery/components/local-delivery-detail.vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const localRecordTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        trade_no: '',
        delivery_no: '',
        trade_type: '',
    }
})

const searchFormRef = ref<FormInstance>()

const tradeType = ref<any>([])
const getTradeFn = () => {
    getTrade().then((res: any) => {
        tradeType.value = res.data
    })
}
getTradeFn()

/**
 * 获取配送记录列表
 */
const loadLocalDeliveryList = (page: number = 1) => {
    localRecordTable.loading = true
    localRecordTable.page = page

    getDeliveryRecordList({
        page: localRecordTable.page,
        limit: localRecordTable.limit,
        ...localRecordTable.searchParam
    }).then((res: any) => {
        localRecordTable.loading = false
        localRecordTable.data = res.data.data
        localRecordTable.total = res.data.total
    }).catch(() => {
        localRecordTable.loading = false
    })
}
loadLocalDeliveryList()

// 详情
const thirdDeliveryDetailRef = ref()
const detailEvent = (row: any) => {
    thirdDeliveryDetailRef.value?.setFormData(row)
}

// 查看地图
const mapInfoRef = ref()
const openMap = (data: any, type: string) => {
    const obj: any = {}
    if (type == 'start') {
        obj.taker_latitude = data.delivery_start_lat
        obj.taker_longitude = data.delivery_start_lng
        obj.taker_address = data.delivery_start
    } else {
        obj.taker_latitude = data.delivery_end_lat
        obj.taker_longitude = data.delivery_start_lng
        obj.taker_address = data.delivery_end
    }
    mapInfoRef.value?.setFormData(obj)
}
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadLocalDeliveryList()
}
</script>

<style scoped>

</style>