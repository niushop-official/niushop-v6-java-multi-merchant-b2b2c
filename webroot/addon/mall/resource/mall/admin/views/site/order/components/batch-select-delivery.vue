<template>
    <el-drawer v-model="showDialog" :title="title" :close="handleClose" size="1300" header-class="!mb-[0px]">
        <div class="main-container">
            <!-- 筛选表单 -->
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form label-width="70px" :inline="true" :model="orderList.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item label="订单信息" prop="search_name">
                        <el-input class="search-input" v-model.trim="orderList.searchParam.search_name" placeholder="请输入订单编号/收货人姓名/收货人电话" clearable>
                            <template #prepend>
                                <el-select v-model="orderList.searchParam.search_type" clearable class="!w-[120px]">
                                    <el-option :label="t('订单编号')" value="order_no"></el-option>
                                    <el-option :label="t('交易流水号')" value="out_trade_no"></el-option>
                                    <el-option :label="t('商品名称')" value="goods_name"></el-option>
                                </el-select>
                            </template>
                        </el-input>
                    </el-form-item>
                     <el-form-item :label="t('挑货状态')" prop="goods_source">
                        <el-select v-model="orderList.searchParam.goods_source" :placeholder="t('goodsSourcePlaceholder')" :disabled="true"  clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,index) in goodsSource" :key="index" :label="item.name" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('配送方式')" prop="delivery_type">
                        <el-select v-model="orderList.searchParam.delivery_type" :placeholder="t('请选择配送方式')" :disabled="true" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,index) in deliveryTypeData" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('发货状态')" prop="status">
                        <el-select v-model="orderList.searchParam.status" :placeholder="t('请选择发货状态')" :disabled="true" clearable class="search-input">
                            <el-option  label="全部" value="all" />
                            <el-option v-for="(item,index) in statusData" :key="index" :label="item.name" :value="item.status" />
                        </el-select>
                    </el-form-item>
                    <el-form-item label="下单时间" prop="create_time">
                        <el-date-picker  v-model="orderList.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间" end-placeholder="结束时间" class="search-input" />
                    </el-form-item>

                    <el-form-item label="支付时间" prop="pay_time">
                        <el-date-picker v-model="orderList.searchParam.pay_time"  type="datetimerange"  value-format="YYYY-MM-DD HH:mm:ss" start-placeholder="开始时间" end-placeholder="结束时间" class="search-input" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadOrderList()">搜索</el-button>
                        <el-button @click="resetForm">重置</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <!-- 订单列表 -->
            <div>
                <el-table :data="orderList.data" ref="mainRef" size="large" class="table-top" >
                    <el-table-column  width="50">
                        <template #header>
                            <el-checkbox  v-model="isSelectAll" :true-value="true" :false-value="false" @change="selectAllCheck" />
                        </template>
                    </el-table-column>
                    <el-table-column label="订单商品" min-width="200" />
                    <el-table-column label="单价/数量" min-width="120" />
                    <el-table-column label="退款状态" min-width="100" />
                    <el-table-column label="订单金额" min-width="120" />
                    <el-table-column label="买家信息" min-width="120" />
                    <el-table-column label="配送方式" min-width="100" />
                    <el-table-column label="交易状态" min-width="100" />
                </el-table>
                <div v-loading="orderList.loading">
                    <div class="mt-[20px]" v-for="(item, index) in orderList.data" :key="index">
                        <div class="bg-[#f7f8fa] h-[32px] flex items-center px-[16px] text-[12px] text-[#666]">
                            <span class="mr-[20px]">订单编号：{{ item.order_no }}</span>
                            <span class="mr-[20px]">下单时间：{{ item.create_time }}</span>
                            <span v-if="item.pay_type_name">支付类型：{{ item.pay_type_name }}</span>
                        </div>

                        <div class="order-goods-table">
                            <!-- 订单表格 -->
                            <el-table  :data="item.order_goods" size="large"  :show-header="false" :span-method="(params: any) => arraySpanMethod(params, item.order_goods.length)"  :ref="(el: any) => { setTableRef(el, index) }">
                                <el-table-column width="50">
                                    <template #default="{ row }">
                                        <el-checkbox  :model-value="!!multipleSelection['goods_' + row.order_goods_id]"  :disabled="handleDisabled(row)" @change="(val: any) => handleGoodsSelect(val, row, item)" />
                                    </template>
                                </el-table-column>
                                <el-table-column align="left" min-width="200">
                                    <template #default="{ row }">
                                        <div class="flex cursor-pointer">
                                            <div class="flex items-center min-w-[50px] mr-[10px]">
                                                <img class="w-[50px] h-[50px]" v-if="row.goods_image" :src="img(row.goods_image)" alt="" />
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
                                            <span class="text-[13px] mt-[5px]">{{ row.num }}件</span>
                                        </div>
                                    </template>
                                </el-table-column>
                                <el-table-column min-width="100">
                                    <template #default="{ row }">
                                        <span class="text-[12px] text-red-500" v-if="row.status == 2">{{ row.status_name }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column min-width="120" class-name="border-0 border-l-[1px] border-solid border-[var(--el-table-border-color)]">
                                    <template #default>
                                        <span class="text-[14px]">订单金额：￥{{ item.order_money }}</span><br />
                                        <span class="text-[14px]">店铺结算：￥{{ item.shop_money }}</span>
                                    </template>
                                </el-table-column>
                                <el-table-column min-width="120">
                                    <template #default>
                                        <div class="flex flex-col">
                                            <span class="text-[12px] text-primary cursor-pointer" v-if="item.member">{{ item.member.nickname }}</span>
                                            <span class="text-[12px] mt-[5px]">{{ item.taker_name }} {{ item.taker_mobile }}</span>
                                            <span class="text-[12px] mt-[5px]">{{ item.taker_full_address }}</span>
                                        </div>
                                    </template>
                                </el-table-column>
                                <el-table-column min-width="100">
                                    <template #default>
                                        <div class="text-[14px]">{{ item.delivery_type_name }}</div>
                                    </template>
                                </el-table-column>
                                <el-table-column min-width="100">
                                    <template #default>
                                        <span class="text-[14px]">{{ item.status_name.name }}</span>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </div>
                    </div>
                </div>
            </div>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="orderList.page" v-model:page-size="orderList.limit"
                    layout="total, sizes, prev, pager, next, jumper" :total="orderList.total" @size-change="loadOrderList()"
                    @current-change="loadOrderList" />
            </div>
        </div>
        <template #footer>
            <div class="flex justify-between items-center">
                <div>
                    <div class="layui-table-bottom-left-container text-[14px] mr-[10px]" v-show="selectGoodsNum">
                        <span>{{ t('已选') }}</span>
                        <span class="text-primary mx-[2px]">{{ selectGoodsNum }}</span>
                        <span>{{ t('个订单项') }}</span>
                        <span>,最大可选择{{ selectedNumber }}个订单项</span>
                    </div>
                </div>
                <div>
                    <el-button @click="handleClose">取消</el-button>
                    <el-button type="primary" :loading="repeatLoading" @click="batchDeliveryEvent" v-if="operationType== 'select_delivery'">{{ buttonText }}</el-button>
                    <el-button type="primary" @click="batchDeliveryEvent" v-else>{{ buttonText }}</el-button>
                </div>
            </div>
        </template>
    </el-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref, defineExpose, defineEmits, computed } from 'vue'
import { t } from '@/lang'
import { getGoodsSource } from '@/app/lang/zh-cn/api/site/goods'
import { getOrderList, getOrderStatus } from '@/app/lang/zh-cn/api/site/order'
import { getShopDeliveryType } from '@/app/api/delivery_site'
import { img } from '@/utils/common'
import { cloneDeep } from 'lodash-es'
import { FormInstance } from 'element-plus'
import { useBatchDelivery } from '@/addon/mall/views/site/order/public/js/batchDelivery'

const showDialog = ref(false)
const orderList = reactive<any>({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        search_type: 'order_no',
        search_name: '',
        delivery_type: 'express',
        goods_source: 'self_goods',
        create_time: [],
        pay_time: [],
        status: 2
    }
})
const searchFormRef = ref<FormInstance>()
const operationType = ref('select_delivery')
const selectedNumber = ref(100)
const goodsSource: any = ref({}) // 商品来源
const deliveryTypeData = ref([])
const statusData = ref<any>([])

const title = computed(() => {
    return operationType.value === 'print_delivery' ? '批量打单发货' : '批量选择发货'
})

const buttonText = computed(() => {
    return operationType.value === 'print_delivery' ? '下一步' : '批量发货'
})

// 已选订单数量
const selectGoodsNum: any = computed(() => {
    return Object.keys(multipleSelection).length
})

const initData = () => {
    getOrderStatus().then((res) => {
        statusData.value = res.data
    })
    getGoodsSource().then((res) => {
        goodsSource.value = res.data
    })
    getShopDeliveryType().then((res) => {
        deliveryTypeData.value = res.data
    })
}
initData()

const multipleSelection: any = reactive({}) // 选中数据
const multipleTable = reactive<Record<number, any>>({}) // 使用object而不是array儲存表格引用
const isSelectAll = ref(false)

// 保存表格引用
const setTableRef = (el: any, index: number) => {
    if (el) {
        multipleTable[index] = el
    }
}
const selectAllCheck = (value: any) => {
    if (isSelectAll.value) {
        for (let i = 0; i < orderList.data.length; i++) {
            // 確保multipleTable中有對應索引的元素
            if (multipleTable[i]) {
                for (let j = 0; j < orderList.data[i].order_goods.length; j++) {
                    if (Object.keys(multipleSelection).length < selectedNumber.value && orderList.data[i].order_goods[j].status !== 2 && (orderList.data[i].order_goods[j].delivery_status == '' || orderList.data[i].order_goods[j].delivery_status == 'wait_delivery')) {
                        multipleTable[i].toggleRowSelection(orderList.data[i].order_goods[j], true)
                        multipleSelection['goods_' + orderList.data[i].order_goods[j].order_goods_id] = cloneDeep(orderList.data[i].order_goods[j])
                    }
                }
            }
        }
    } else {
        for (let v = 0; v < orderList.data.length; v++) {
            if (multipleTable[v]) {
                multipleTable[v].clearSelection()
                for (let w = 0; w < orderList.data[v].order_goods.length; w++) {
                    delete multipleSelection['goods_' + orderList.data[v].order_goods[w].order_goods_id]
                }
            }
        }
    }
}

// 处理商品选择
const handleGoodsSelect = (checked: boolean, row: any, order: any) => {
    if (selectGoodsNum.value >= selectedNumber.value) {
        return false
    }
    if (checked) {
        multipleSelection['goods_' + row.order_goods_id] = {
            ...row,
            order_id: order.order_id,
            order_no: order.order_no,
            member_id: order.member_id
        }
    } else {
        delete multipleSelection['goods_' + row.order_goods_id]
    }
}

// 合并表格行
const arraySpanMethod = ({ row, column, rowIndex, columnIndex }: any, length: number) => {
    // 0:选择框 1:商品信息 2:单价数量 3:退款状态 4:订单金额 5:买家信息 6:配送方式 7:订单状态
    if (columnIndex >= 4) {
        if (rowIndex === 0) {
            return [length, 1]
        } else {
            return [0, 0]
        }
    }
    return [1, 1]
}

// 加载订单列表
const loadOrderList = (page: number = 1) => {
    orderList.loading = true
    orderList.page = page
    getOrderList({
        page: orderList.page,
        limit: orderList.limit,
        ...orderList.searchParam
    }).then((res) => {
        orderList.loading = false
        orderList.data = res.data.data || []
        orderList.total = res.data.total || 0
        handleChange()
    })
}
const mainRef = ref()
const handleChange = () => {
    // 检查当前页所有可选商品是否都被选中
    let selectableGoodsCount = 0
    let selectedSelectableGoodsCount = 0
    const selectedKeys = Object.keys(multipleSelection)

    orderList.data.forEach(order => {
        if (Array.isArray(order.order_goods)) {
            order.order_goods.forEach(good => {
                if (good.status !== 2) { // 状态为2的商品不可选中
                    selectableGoodsCount++
                    // 检查这个可选商品是否在 multipleSelection 中
                    const key = 'goods_' + good.order_goods_id
                    if (selectedKeys.includes(key)) {
                        selectedSelectableGoodsCount++
                    }
                }
            })
        }
    })

    // 如果当前页面所有可选商品都已被选中，并且可选商品总数大于0，则认为是全选
    if (selectableGoodsCount > 0 && selectedSelectableGoodsCount === selectableGoodsCount) {
        isSelectAll.value = true
    } else {
        isSelectAll.value = false
    }
}

// 禁用
const handleDisabled = (row: any) => {
    let disabled = false
    if (Object.keys(row).length) {
        if (row.status == 2) {
            disabled = true
        }
        if (row.delivery_status && row.delivery_status != 'wait_delivery') {
            disabled = true
        }
    }
    return disabled
}
// 重置表单
const resetForm = () => {
    searchFormRef.value?.resetFields()
    orderList.searchParam.delivery_type = 'express'
    orderList.searchParam.status = 2
    loadOrderList()
}

// 关闭抽屉
const handleClose = (done: () => void) => {
    showDialog.value = false
    resetForm()
}

const batchDelivery = useBatchDelivery()

// 批量发货
const emit = defineEmits(['batchDelivery'])
const repeatLoading = ref(false)
const batchDeliveryEvent = () => {
    const selectedGoods = cloneDeep(Object.values(multipleSelection))
    if (selectedGoods.length === 0) {
        return
    }
    if (operationType.value === 'select_delivery') {
        if (repeatLoading.value) {
            return false
        }
        repeatLoading.value = true
        batchDelivery.handleBatchDelivery({
            goods: selectedGoods,
            type: operationType.value
        }, () => {
            repeatLoading.value = false
            showDialog.value = false
            emit('batchDelivery', selectedGoods)
        })
    } else {
        emit('batchDelivery', selectedGoods)
        showDialog.value = false
    }
}
// 打开抽屉
const open = async (type: string = 'select_delivery') => {
    operationType.value = type
    showDialog.value = true
    resetSelection()
    loadOrderList()
}
// 重置选择状态
const resetSelection = () => {
    isSelectAll.value = false
    for (const key in multipleSelection) {
        delete multipleSelection[key]
    }

    // 清除表格的选中状态
    for (let v = 0; v < orderList.data.length; v++) {
        if (multipleTable[v]) {
            multipleTable[v].clearSelection()
        }
    }
}

defineExpose({
    showDialog,
    open
})
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

.goods-row {
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
        border-bottom: none;
    }
}

/* 多行超出隐藏 */
.multi-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    line-clamp: 2;
    -webkit-box-orient: vertical;
}

</style>
