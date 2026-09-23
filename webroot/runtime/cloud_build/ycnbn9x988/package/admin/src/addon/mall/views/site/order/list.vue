<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form label-width="70px" :inline="true" :model="orderTable.searchParam" ref="searchFormRef" class="page-form">

                    <el-form-item :label="t('orderInfo')" prop='search_name'>
                        <el-input class="search-input" v-model.trim="orderTable.searchParam.search_name" clearable>
                            <template #prepend>
                                <el-select v-model="orderTable.searchParam.search_type" clearable class="!w-[120px]">
                                    <el-option :label="t('orderNo')" value="order_no"></el-option>
                                    <el-option :label="t('outTradeNo')" value="out_trade_no"></el-option>
                                    <el-option :label="t('goodsName')" value="goods_name"></el-option>
                                </el-select>
                            </template>
                        </el-input>
                    </el-form-item>
                    <el-form-item :label="t('memberInfo')" prop='keyword'>
                        <el-input class="search-input" v-model.trim="orderTable.searchParam.keyword" :placeholder="t('memberInfoPlaceholder')" clearable />
                    </el-form-item>
                    <el-form-item :label="t('payType')" prop='pay_type'>
                        <el-select v-model="orderTable.searchParam.pay_type" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item, index) in payTypeData" :key="index" :label="item.name" :value="item.key"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('fromType')" prop='order_from'>
                        <el-select v-model="orderTable.searchParam.order_from" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item, index) in orderFromData" :key="index" :label="item" :value="index"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('goodsSource')" prop="goods_source">
                        <el-select v-model="orderTable.searchParam.goods_source" :placeholder="t('goodsSourcePlaceholder')" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,index) in goodsSource" :key="index" :label="item.name" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('配送方式')" prop="delivery_type">
                        <el-select v-model="orderTable.searchParam.delivery_type" :placeholder="t('请选择配送方式')" clearable  class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,index) in deliveryTypeData" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('订单类型')" prop="order_type">
                        <el-select v-model="orderTable.searchParam.order_type" :placeholder="t('请选择订单类型')" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,index) in orderType" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="orderTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" class="search-input" />
                    </el-form-item>
                    <el-form-item :label="t('payTime')" prop="pay_time">
                        <el-date-picker v-model="orderTable.searchParam.pay_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" class="search-input" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadOrderList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                        <el-button type="primary" @click="exportSelectEvent">{{ t('export') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
                <el-tab-pane :label="t('all')" name=""></el-tab-pane>
                <el-tab-pane :label="item.name" :name="item.status" v-for="(item,index) in statusData" :key="index"></el-tab-pane>
            </el-tabs>
            <div>
                <!-- todo 后续完善，增加批量发货，再修改判断逻辑 -->
                <div class="mb-[10px] flex items-center">
                    <el-button @click="batchPrintElectronicSheet" size="small" v-if="activeName == 3">
                        {{ t('batchPrintElectronicSheet') }}
                    </el-button>
                    <el-button @click="batchSelectDelivery" size="small" v-if="activeName == 2">{{ t('批量选择发货')}}</el-button>
                    <el-button @click="batchPrintDelivery" size="small" v-if="activeName == 2">{{ t('批量打单发货')}}</el-button>
                    <el-button @click="batchDeleteFn" size="small" v-if="activeName == -1"> {{ t('批量删除') }}</el-button>
                </div>
                <el-table :data="orderTable.data" size="large" class="table-top" @select-all="selectAllCheck">
                    <el-table-column type="selection" width="40" />
                    <el-table-column :label="t('orderGoods')" min-width="200" />
                    <el-table-column :label="t('goodsPriceNumber')" min-width="120" />
                    <el-table-column :label="t('rightsProtection')" min-width="120" />
                    <el-table-column :label="t('orderMoney')" min-width="120" />
                    <el-table-column :label="t('buyInfo')" min-width="120" />
                    <el-table-column :label="t('deliveryType')" min-width="100" />
                    <el-table-column :label="t('orderStatus')" min-width="100" />
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120" />
                </el-table>
                <div class="table-body min-h-[150px]" v-loading="orderTable.loading">
                    <div v-if="!orderTable.loading">
                        <template v-if="orderTable.data.length">
                            <div v-for="(item, index) in orderTable.data" :key="index">
                                <div class="flex items-center justify-between bg-[#f7f8fa] mt-[10px] border-[#e4e7ed] border-solid border-b-[1px] px-3 h-[35px] text-[12px] text-[#666]">
                                    <div>
                                        <span>{{ t('orderNo') }}：{{ (item as any).order_no }}</span>
                                        <span class="ml-5">{{ t('createTime') }}：{{ (item as any).create_time }}</span>
                                        <!-- <span class="ml-5">{{ t('orderFrom') }}：{{ (item as any).order_form_name }}</span> -->
                                        <span class="ml-5" v-if="item.order_type_name">{{ t('订单类型') }}：{{ (item as any).order_type_name }}</span>
                                        <span class="ml-5" v-if="item.pay">{{ t('payType') }}：{{ (item as any).pay.type_name }}</span>
                                        <span class="ml-5 cursor-pointer" v-if="item.supply"  @click.stop="router.push({path:'/supply/select/supply', query: { site_id: item.supply_id }})">{{ t('supplyName') }}：<span class="text-[var(--el-color-primary)]">{{ item.supply.site_name }}</span></span>
                                    </div>
                                    <div>
                                        <!-- <el-button type="primary" link>{{ t('offlinePayment') }}</el-button> -->
                                        <el-button type="primary" link @click="printTicketEvent(item)" v-if="item.isSupportPrintTicket">{{ t('printTicket') }}</el-button>
                                        <el-button type="primary" link @click="openElectronicSheetPrintDialog(item)" v-if="item.isSupportElectronicSheet">{{ t('electronicSheetPrintTitle') }}</el-button>
                                        <el-button type="primary" link @click="detailEvent(item)">{{ t('info') }}</el-button>
                                        <el-button type="primary" link @click="setNotes(item)">{{ t('notes') }}</el-button>
                                        <el-button type="primary" link @click="getPayInfoFn(item)" v-if="item.out_trade_no">{{ t('支付信息') }}</el-button>
                                    </div>
                                </div>

                                <el-table :data="(item as any).order_goods" size="large" :show-header="false" :span-method="arraySpanMethod"  :ref="(el: any) => { setTableRef(el, index) }" @selection-change="handleSelectChange($event, item)">
                                    <el-table-column type="selection" width="40" />
                                    <el-table-column align="left" min-width="200">
                                        <template #default="{ row }">
                                            <div class="flex cursor-pointer">
                                                <div class="flex items-center min-w-[50px] mr-[10px]">
                                                    <img class="w-[50px] h-[50px]" v-if="row.goods_image_thumb_small" :src="img(row.goods_image_thumb_small)" alt="" />
                                                    <img class="w-[50px] h-[50px]" v-else src="" alt="" />
                                                </div>
                                                <div class="flex flex-col items-start">
                                                    <el-tooltip class="box-item" effect="light" placement="top">
                                                        <template #content>
                                                            <div class="max-w-[250px]">{{ row.goods_name }}</div>
                                                        </template>
                                                        <p class="multi-hidden text-[14px]">{{ row.goods_name }}</p>
                                                    </el-tooltip>
                                                    <span class="text-[12px] text-[#999] truncate" v-if="row.sku_name">{{ row.sku_name }}</span>
                                                </div>
                                            </div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column min-width="120">
                                        <template #default="{ row }">
                                            <div class="flex flex-col">
                                                <span class="text-[13px]">￥{{ row.goods_money }}</span>
                                                <span class="text-[13px] mt-[5px]">{{ row.num }}{{ t('piece') }}</span>
                                            </div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column min-width="120">
                                        <template #default="{ row }">
                                            <div class="cursor-pointer flex items-center flex-wrap" @click.stop="refundEventLink(item,row)" v-if="row.status != 1">
                                                <span class="mr-[3px] text-[13px]">退款金额￥{{ Number(row.refund_money) ? row.refund_money : row.apply_money }}</span>
                                                <div class="text-[13px] text-[var(--el-color-primary)] flex items-center">
                                                    <span>{{ row.status_name }}</span>
                                                    <span class="iconfont iconxiangyoujiantou !text-[10px] text-[#888]"></span>
                                                </div>
                                            </div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column min-width="120" class-name="border-0 border-l-[1px] border-solid border-[var(--el-table-border-color)]">
                                        <template #default>
                                            <span class="text-[14px]">订单金额：￥{{ item.order_money }}</span><br />
                                            <span class="text-[14px]">店铺结算：￥{{ item.shop_money }}</span><br />
                                            <span class="text-[14px]">平台结算：￥{{ item.mall_money }}</span><br />
                                            <span class="text-[14px]" v-if="item.supply_money">供货商货款：￥{{ item.supply_money }}</span><br />
                                            <span class="text-[14px]" v-if="item.supply_refund_money">供货商货款退款：￥{{ item.supply_refund_money }}</span><br />
                                            <div v-if="item.pay">{{ item.member_id !== item.pay.main_id && item.pay.status == 2 ? item.pay.pay_type_name : '' }}</div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column min-width="120">
                                        <template #default>
                                            <div class="flex flex-col">
                                                <span class="text-[12px] text-primary" v-if="item.member">{{ item.member.nickname }}</span>
                                                <span class="text-[12px] mt-[5px]">{{ item.taker_name }} {{ item.taker_mobile }}</span>
                                                <span class="text-[12px] mt-[5px]">{{ item.taker_full_address }}</span>
                                            </div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column min-width="100">
                                        <template #default>
                                            <div>
                                                <div class="text-[14px]">{{ item.delivery_type_name }}</div>
                                                <div class="text-[14px] cursor-pointer text-primary" @click="toDelivery(item)">{{ item.local_delivery_status_name }}</div>
                                            </div>
                                        </template>
                                    </el-table-column>
                                    <el-table-column min-width="100">
                                        <template #default>
                                            <span class="text-[14px]">{{ item.status_name.name }}</span>
                                        </template>
                                    </el-table-column>
                                    <el-table-column align="right" min-width="120">
                                        <template #default>
                                            <template v-if="item.status == 1 && !item.supply_id">
                                                <el-button type="primary" link @click="close(item)" >{{ t('orderClose') }}</el-button>
                                                <el-button v-if="item.mall_discount_money == 0" type="primary" link @click="orderAdjustMoney(item)">{{ t('editPrice') }}</el-button>
                                            </template>
                                            <el-button type="primary" v-if="(item.status == 1 || item.status == 2) && item.delivery_type != 'virtual' && !item.supply_id && item.delivery_type!='store'" link @click="orderEditAddressFn(item)">{{ t('editAddress') }}</el-button>
                                            <el-button type="primary" link @click="delivery(item,'add')" v-if="item.status == 2 && !item.supply_id && item.delivery_type!='store' && !(item.delivery_type == 'virtual' && item.sub_delivery_type == 'verify')">{{ t('sendOutGoods') }}</el-button>
                                            <!-- 物流配送 -->
                                            <el-button type="primary" link @click="delivery(item,'edit')" v-if="item.status == 3 && !item.supply_id && item.delivery_type != 'virtual'&& item.delivery_type == 'express'">{{ t('modifyOutGoods') }}</el-button>
                                            <!-- 同城配送 -->
                                            <el-button type="primary" link @click="delivery(item,'edit')" v-if="item.status == 3 && !item.supply_id && item.delivery_type != 'virtual'&& item.delivery_type == 'local_delivery' && (item.local_delivery_status == -1 || item.local_delivery_status == -2 || item.local_delivery_status == 8)">{{ t('modifyOutGoods') }}</el-button>
                                            <el-button type="primary" link @click="finish(item)" v-if="item.status == 3 && !item.supply_id && !(item.delivery_type == 'virtual' && item.sub_delivery_type == 'verify')">{{t('confirmTakeDelivery') }}</el-button>
                                            <el-button type="primary"  v-if="item.is_refund_show && item.status != 1 && item.status != -1 && !item.supply_id && item.status != 6" link @click="refundEvent(item)">{{ t('voluntaryRefund') }}</el-button>
                                            <el-button type="primary" v-if="item.status == -1" link @click="deleteEvent(item)">{{ t('delete') }}</el-button>
                                            <el-button type="primary" v-if="item.delivery_type == 'store' && item.status == 2" link @click="verifyEvent(item)">提货</el-button>
                                            <el-button type="primary" v-if="item.delivery_type == 'virtual' && item.sub_delivery_type == 'verify' && item.status == 3" link @click="verifyEvent(item)">核销</el-button>
                                        </template>
                                    </el-table-column>
                                </el-table>
                                <div v-if="item.shop_remark" class="text-[14px] min-h-[30px] leading-[30px] px-3 bg-[#fff0e5] text-[#ff7f5b]">
                                    <span class="mr-[5px]">{{ t('notes') }}：</span>
                                    <span>{{ item.shop_remark }}</span>
                                </div>
                            </div>
                        </template>
                        <el-empty v-else :image-size="1" :description="t('emptyData')" />
                    </div>
                </div>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="orderTable.page" v-model:page-size="orderTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="orderTable.total"
                        @size-change="loadOrderList()" @current-change="loadOrderList" />
                </div>
            </div>
        </el-card>
        <adjust-money ref="orderAdjustMoneyActionDialog" @complete="loadOrderList"/>
        <delivery-action ref="deliveryActionDialog" @complete="loadOrderList"></delivery-action>
        <order-notes ref="orderNotesDialog" @complete="loadOrderList"></order-notes>
        <order-export-select ref="selectExportDialog" @complete="exportEvent" />
        <export-sure ref="exportSureDialog" v-model:show="flag" :type="export_type" isSite="true" :searchParam="orderTable.searchParam" @close="handleClose" />
        <order-edit-address ref="orderEditAddressDialog" @complete="loadOrderList"/>
        <electronic-sheet-print ref="electronicSheetPrintDialog" @complete="electronicSheetPrintComplete" />
        <shop-active-refund ref="shopActiveRefundDialog" @complete="loadOrderList" />
        <pay-info ref="payInfoDialog" />
        <batch-print-delivery-action ref="batchPrintDeliveryDialog" @complete="handlePrintDeliveryConfirm" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getVerifyCode } from '@/app/api/verify'
import { getOrderList, getOrderStatus, orderClose, orderFinish, getOrderPayType, getOrderFrom, orderDelete, getOrderType } from '@/app/lang/zh-cn/api/site/order'
import { getGoodsSource } from '@/app/lang/zh-cn/api/site/goods'
import { printTicket } from '@/app/api/printer'
import { getShopDeliveryType } from '@/app/api/delivery_site'
import DeliveryAction from '@/addon/mall/views/site/order/components/delivery-action.vue'
import OrderNotes from '@/addon/mall/views/site/order/components/order-notes.vue'
import OrderExportSelect from '@/addon/mall/views/site/order/components/order-export-select.vue'
import orderEditAddress from '@/addon/mall/views/site/order/components/order-edit-address.vue'
import AdjustMoney from '@/addon/mall/views/site/order/components/adjust-money.vue'
import ShopActiveRefund from '@/addon/mall/views/site/order/components/shop-active-refund.vue'
import electronicSheetPrint from '@/addon/mall/views/site/order/components/electronic-sheet-print.vue'
import BatchPrintDeliveryAction from '@/addon/mall/views/site/order/components/batch-print-delivery-action.vue'
import { img, setTablePageStorage, getTablePageStorage } from '@/utils/common'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { cloneDeep } from 'lodash-es'
import { useBatchDelivery } from '@/addon/mall/views/site/order/public/js/batchDelivery'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const activeName: any = ref(route.query.status || '')

const statusData = ref([])
const payTypeData = ref<any[]>([])
const orderFromData = ref([])
const goodsSource: any = ref({}) // 商品来源
const deliveryTypeData = ref([])
const orderType = ref([])
const setFormData = async () => {
    statusData.value = await (await getOrderStatus()).data
    payTypeData.value = await (await getOrderPayType()).data
    orderFromData.value = await (await getOrderFrom()).data
    goodsSource.value = await (await getGoodsSource()).data
    deliveryTypeData.value = await (await getShopDeliveryType()).data
    orderType.value = await (await getOrderType()).data
}
setFormData()

const multipleSelection: any = reactive({}) // 选中数据
const multipleTable = reactive<Record<number, any>>({}) // 使用object而不是array儲存表格引用
const isSelectAll = ref(false)

// 保存表格引用
const setTableRef = (el: any, index: number) => {
    if (el) {
        multipleTable[index] = el
    }
}

const selectAllCheck = () => {
    if (!isSelectAll.value) {
        isSelectAll.value = true
        for (let i = 0; i < orderTable.data.length; i++) {
            let isAdd = false
            // 確保multipleTable中有對應索引的元素
            if (multipleTable[i]) {
                for (let j = 0; j < orderTable.data[i].order_goods.length; j++) {
                    multipleTable[i].toggleRowSelection(orderTable.data[i].order_goods[j], true)
                    isAdd = true
                }
                if (isAdd) {
                    multipleSelection['order_' + orderTable.data[i].order_id] = cloneDeep(orderTable.data[i].order_goods)
                }
            }
        }
    } else {
        isSelectAll.value = false
        for (let v = 0; v < orderTable.data.length; v++) {
            if (multipleTable[v]) {
                multipleTable[v].clearSelection()
                delete multipleSelection['order_' + orderTable.data[v].order_id]
            }
        }
    }
}

// 监听表格复选框
const handleSelectChange = (selection: any, row: any) => {
    if (selection.length > 0) {
        multipleSelection['order_' + row.order_id] = selection
    } else {
        delete multipleSelection['order_' + row.order_id]
    }
}

const orderTable: any = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        search_type: 'order_no',
        search_name: '',
        keyword: '',
        pay_type: '',
        order_from: '',
        goods_source: '',
        delivery_type: '',
        status: route.query.status || '',
        create_time: [],
        pay_time: [],
        extend_field_1: route.query.active_id || '',
        order_type: route.query.order_type || ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取订单列表
 */
const loadOrderList = (page: number = 1) => {
    orderTable.loading = true
    orderTable.page = page

    getOrderList({
        page: orderTable.page,
        limit: orderTable.limit,
        ...orderTable.searchParam
    }).then(res => {
        orderTable.loading = false
        orderTable.data = res.data.data.map((el: any) => {
            el.isSupportElectronicSheet = false // 是否支持打印电子面单
            el.isSupportPrintTicket = false // 是否支持打印小票

            // 只有待发货、待收货，物流配送的情况下才能打印电子面单
            if (el.delivery_type == 'express' && el.status == 3) {
                el.isSupportElectronicSheet = true
            }

            //  待发货、待收货、已完成状态下可以打印小票
            if (el.delivery_type != 'virtual' && (el.status == 2 || el.status == 3 || el.status == 5)) {
                el.isSupportPrintTicket = true
            }
            el.order_goods.forEach((v: any) => {
                v.rowNum = el.order_goods.length
                v.order_id = el.order_id
                v.delivery_type = el.delivery_type
                v.isSupportElectronicSheet = el.isSupportElectronicSheet
            })
            return el
        })

        // 处理主力退款按钮是否出现
        orderTable.data.forEach((item: any, index: number, arr: any) => {
            let refundOrderNum = 0
            item.order_goods.forEach((orderItem: any, orderIndex: number) => {
                if (orderItem.is_enable_refund == 1) {
                    refundOrderNum++
                }
            })
            arr[index].is_refund_show = refundOrderNum > 0 ? true : false;
        })
        orderTable.total = res.data.total
        setTablePageStorage(orderTable.page, orderTable.limit, orderTable.searchParam)
    }).catch(() => {
        orderTable.loading = false
    })
}

loadOrderList(getTablePageStorage(orderTable.searchParam).page)

const handleClick = (event: any) => {
    orderTable.searchParam.status = event
    isSelectAll.value = false
    for (let key in multipleSelection) {
        delete multipleSelection[key]
    }
    loadOrderList()
}

// 合并表格行
const arraySpanMethod = ({
    row,
    column,
    rowIndex,
    columnIndex
}: any) => {

    //第 0 列 = selection 列 = 不合并
    if (columnIndex === 0) {
        return [1, 1]
    }

    if (rowIndex === 0) {
        if (columnIndex === 0) {
            return [row.rowNum, 1]
        } else if (columnIndex > 3) {
            return [row.rowNum, 1]
        } else {
            return [1, 1]
        }
    } else {
        if (columnIndex === 0) {
            return [0, 0]
        } else if (columnIndex > 3) {
            return [0, 0]
        } else {
            return [1, 1]
        }
    }
}

/**
 * 订单导出
 */
const exportSureDialog = ref(null)
const export_type = ref('')
const flag = ref(false)
const handleClose = (val: any) => {
    flag.value = val
}
const exportEvent = (data: any) => {
    export_type.value = data
    flag.value = true
}

const selectExportDialog: Record<string, any> | null = ref(null)

/**
 * 订单导出类型选择
 */
const exportSelectEvent = () => {
    selectExportDialog.value.showDialog = true
}

// 订单详情
const detailEvent = (data: any) => {
    router.push('/mall/order/detail?order_id=' + data.order_id)
}

const memberEvent = (member_id: number) => {
    const routeUrl = router.resolve({
        path: '/shop/member/detail',
        query: { member_id }
    })
    window.open(routeUrl.href, '_blank')
}

const refundEventLink = (item:any, row:any) => {
    const routeUrl = router.resolve({
        path: '/mall/order/refund/detail',
        query: { refund_id: row.refund_id }
    })
    window.open(routeUrl.href, '_blank')
}

const close = (data: any) => {
    ElMessageBox.confirm(t('orderCloseTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        orderClose(data.order_id).then(() => {
            loadOrderList()
        })
    })
}

// 订单调整价格
const orderAdjustMoneyActionDialog: Record<string, any> | null = ref(null)
const orderAdjustMoney = (data: any) => {
    orderAdjustMoneyActionDialog.value.setFormData(data)
    orderAdjustMoneyActionDialog.value.showDialog = true
}

const deliveryActionDialog: Record<string, any> | null = ref(null)
/**
 * 发货
 */
const delivery = (data: any, type: string) => {
    deliveryActionDialog.value.setFormData(data, type)
    deliveryActionDialog.value.showDialog = true
}

const orderNotesDialog: Record<string, any> | null = ref(null)
/**
 * 设置备注
 */
const setNotes = (data: any) => {
    orderNotesDialog.value.setFormData(data)
    orderNotesDialog.value.showDialog = true
}

// 订单完成
const finish = (data: any) => {
    ElMessageBox.confirm(t('orderFinishTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        orderFinish(data.order_id).then(() => {
            loadOrderList()
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadOrderList()
}

/**
 * 修改地址
 */
const orderEditAddressDialog: Record<string, any> | null = ref(null)
const orderEditAddressFn = async(data: any) => {
    orderEditAddressDialog.value.showDialog = true
    orderEditAddressDialog.value.setFormData(data)
}

// 打印电子面单
const electronicSheetPrintDialog: Record<string, any> | null = ref(null)

// 单个订单打印电子面单
const openElectronicSheetPrintDialog = (data: any) => {
    let formData = cloneDeep(data)
    formData.print_type = 'single'
    electronicSheetPrintDialog.value.setFormData(formData)
    electronicSheetPrintDialog.value.showDialog = true
}

// 批量打印电子面单
const batchPrintElectronicSheet = () => {
    let noSupportCount = 0
    let order_ids = []
    for (let key in multipleSelection) {
        // multipleSelection[key] is now an array of goods
        // We check the first good for isSupportElectronicSheet
        if (multipleSelection[key][0].isSupportElectronicSheet) {
            order_ids.push(multipleSelection[key][0].order_id)
        } else {
            noSupportCount++
        }
    }

    if (noSupportCount && order_ids.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${ t('notSupportPrintElectronicSheetTips') }`
        })
        return
    }

    if (order_ids.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${ t('batchEmptySelectedOrderTips') }`
        })
        return
    }

    electronicSheetPrintDialog.value.setFormData({
        order_id: order_ids.toString(),
        print_type: 'multiple'
    })
    electronicSheetPrintDialog.value.showDialog = true
}

// 电子面单完成事件
const electronicSheetPrintComplete = () => {
    isSelectAll.value = false
    for (const v in orderTable.data) {
        if (multipleTable[v]) {
            multipleTable[v].clearSelection()
            delete multipleSelection['order_' + orderTable.data[v].order_id];
        }
    }
}

const repeat = ref(false)

/**
 * 打印小票
 */
const printTicketEvent = (data: any) => {
    if (repeat.value) return
    repeat.value = true

    printTicket({
        type: 'mallGoodsOrder', // 小票模板类型
        trigger: 'manual', // 触发时机：手动触发
        // 业务参数，根据自身业务传值
        business: {
            order_id: data.order_id
        }
    }).then((res: any) => {
        repeat.value = false
    }).catch(() => {
        repeat.value = false
    })
}

/**
 * 商家主动退款
 */
const shopActiveRefundDialog: Record<string, any> | null = ref(null)
const refundEvent = (data: any) => {
    shopActiveRefundDialog.value.setFormData(data)
    shopActiveRefundDialog.value.showDialog = true
}

// 支付信息
const payInfoDialog = ref<any>(null)
const getPayInfoFn = (data: any) => {
    payInfoDialog.value.setFormData({
        trade_id: data.order_id,
        trade_type: 'mall'
    })
}

const toDelivery = (data: any) => {
    const url = router.resolve({
        path: '/site/delivery/local/record',
        query: {
            trade_no: data.order_no,
            trade_type: 'mall'
        }
    })
    window.open(url.href)
}
// 重置选择状态
const resetSelection = () => {
    isSelectAll.value = false
    for (const key in multipleSelection) {
        delete multipleSelection[key]
    }

    // 清除表格的选中状态
    for (let v = 0; v < orderTable.data.length; v++) {
        if (multipleTable[v]) {
            multipleTable[v].clearSelection()
        }
    }
}
// 删除
const deleteEvent = (data: any) => {
    ElMessageBox.confirm(t('deleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        orderDelete({ order_ids: [data.order_id] }).then(() => {
            loadOrderList(getTablePageStorage(orderTable.searchParam).page)
        }).catch(() => {
        })
    })
}
// 批量删除
const batchDeleteFn = () => {
    const orderIds: number[] = []
    for (const key in multipleSelection) {
        orderIds.push(multipleSelection[key][0].order_id)
    }
    if (orderIds.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedOrderTips')}`
        })
        return
    }

    ElMessageBox.confirm(t('batchDeleteTips'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }).then(() => {
        orderDelete({ order_ids: orderIds }).then(() => {
            resetSelection() // 重置选择状态
            loadOrderList(getTablePageStorage(orderTable.searchParam).page)
        }).catch(() => {
        })
    })
}

// 获取核销码
const verifyEvent = (data: any) => {
    if (repeat.value) return
    repeat.value = true
    getVerifyCode({
        trade_id: data.order_id,
        trade_type: 'mall'
    }).then(res => {
        repeat.value = false
        const url = router.resolve({
            path: '/marketing/verify/index',
            query: { code: res.data[0].code }
        })
        window.open(url.href)
    })
}

// 批量发货相关
const batchPrintDeliveryDialog: Record<string, any> | null = ref(null)
const selectedGoodsForPrint = ref<any[]>([])
const currentOperationType = ref('')

// 验证批量发货
const validateBatchDelivery = () => {
    const selectedOrders = Object.values(multipleSelection)
    if (selectedOrders.length === 0) {
        ElMessage.warning('请选择要操作的订单')
        return false
    }
    let hasNonExpress = false
    let hasRefund = false
    let hasNotWaitDelivery = false
    let hasSupply = false
    for (const goodsList of selectedOrders as any[]) {
        if (goodsList[0].delivery_type !== 'express') {
            hasNonExpress = true
        }
        // console.table(goodsList[0])
        if (goodsList[0].supply_id > 0) {
            hasSupply = true
        }

        for (const good of goodsList) {
            if (good.status != 1) {
                hasRefund = true
                break
            }
            if (good.delivery_status && good.delivery_status != 'wait_delivery') {
                hasNotWaitDelivery = true
                break
            }
        }

        if (hasNonExpress || hasRefund || hasNotWaitDelivery || hasSupply) break
    }

    if (hasNonExpress || hasRefund || hasNotWaitDelivery || hasSupply) {
        ElMessage.warning('仅支持自建商品的物流配送且处于未退款—待发货状态的订单进行批量发货操作')
        return false
    }
    return true
}

// 获取选中商品（包含同订单下的其他有效商品）
const getSelectedGoodsWithSiblings = () => {
    const finalGoodsList: any[] = []
    const selectedOrderIds = Object.keys(multipleSelection).map(key => parseInt(key.replace('order_', '')))

    // 遍历当前页面的订单
    orderTable.data.forEach((order: any) => {
        if (selectedOrderIds.includes(order.order_id)) {
            // 如果订单被选中（有部分或全部商品被选中），则获取该订单下所有 status == 1 的商品
            order.order_goods.forEach((good: any) => {
                if (good.status == 1) {
                    finalGoodsList.push(good)
                }
            })
            // 从 selectedOrderIds 中移除已处理的订单
            const index = selectedOrderIds.indexOf(order.order_id)
            if (index > -1) {
                selectedOrderIds.splice(index, 1)
            }
        }
    })

    // 处理跨页选中的订单（无法获取同订单其他商品，只能使用已选中的）
    selectedOrderIds.forEach(orderId => {
        const key = 'order_' + orderId
        if (multipleSelection[key]) {
            finalGoodsList.push(...multipleSelection[key])
        }
    })

    return finalGoodsList
}

const batchDelivery = useBatchDelivery()

// 批量选择发货
const batchSelectDelivery = () => {
    if (!validateBatchDelivery()) return
    ElMessageBox.confirm(t('确定要批量选择发货吗？'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }).then(() => {
        currentOperationType.value = 'select_delivery'
        const goodsList = getSelectedGoodsWithSiblings()
        batchDelivery.handleBatchDelivery({
            goods: goodsList,
            type: 'select_delivery'
        }, () => {
            loadOrderList()
            resetSelection()
        })
    })
}

// 批量打单发货
const batchPrintDelivery = () => {
    if (!validateBatchDelivery()) return
    ElMessageBox.confirm(t('确定要批量打单发货吗？'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }).then(() => {
        currentOperationType.value = 'print_delivery'
        const goodsList = getSelectedGoodsWithSiblings()

        selectedGoodsForPrint.value = goodsList
        batchPrintDeliveryDialog.value.open({
            list: goodsList,
            type: 'print_delivery'
        })
    })
}

// 处理打单发货确认
const handlePrintDeliveryConfirm = () => {
    loadOrderList()
    resetSelection()
}
</script>

<style lang="scss" scoped>
.table-top :deep(.el-table__body-wrapper) {
    display: none;
}

.input-item {
    width: 150px !important;
}

:deep(.el-table) {
    --el-table-row-hover-bg-color: var(--el-transfer-border-color);
}

/* 多行超出隐藏 */
.multi-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
</style>
