<template>
    <div class="main-container">

        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="goodsTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('goodsName')" prop="goods_name">
                        <el-input v-model.trim="goodsTable.searchParam.goods_name" :placeholder="t('goodsNamePlaceholder')" maxlength="60" />
                    </el-form-item>
                    <el-form-item :label="t('goodsCategory')" prop="goods_mall_category">
                        <el-cascader :props="goodsCategoryProps" v-model="goodsTable.searchParam.goods_mall_category" :options="goodsCategoryOptions" :placeholder="t('goodsCategoryPlaceholder')" clearable filterable />
                    </el-form-item>
                    <el-form-item :label="t('goodsSource')" prop="goods_source">
                        <el-select v-model="goodsTable.searchParam.goods_source" :placeholder="t('goodsSourcePlaceholder')" clearable>
                            <el-option v-for="(item,index) in goodsSource" :key="index" :label="item.name" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('saleNum')" prop="sale_num">
                        <div class="region-input">
                            <input type="text" :placeholder="t('startSaleNumPlaceholder')" maxlength="10" v-model.trim="goodsTable.searchParam.start_sale_num" @keyup="filterDigit($event)">
                            <span class="separator">-</span>
                            <input type="text" :placeholder="t('endSaleNumPlaceholder')" maxlength="10" v-model.trim="goodsTable.searchParam.end_sale_num" @keyup="filterDigit($event)">
                        </div>
                    </el-form-item>
                    <el-form-item :label="t('skuPrice')">
                        <div class="region-input">
                            <input type="text" :placeholder="t('startPricePlaceholder')" maxlength="10" v-model.trim="goodsTable.searchParam.start_price" @keyup="filterDigit($event)">
                            <span class="separator">-</span>
                            <input type="text" :placeholder="t('endPricePlaceholder')" maxlength="10" v-model.trim="goodsTable.searchParam.end_price" @keyup="filterDigit($event)">
                        </div>
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadGoodsList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">

                <el-tabs v-model="goodsTable.searchParam.status" class="goods-tabs" @tab-click="tabHandleClick">
                    <el-tab-pane :label="t('statusAll')" name=""></el-tab-pane>
                    <el-tab-pane v-for="item in goodsStatus" :key="item.value" :label="item.label" :name="item.value"></el-tab-pane>
                </el-tabs>

                <div class="mb-[10px] flex items-center">
                </div>

                <el-table :data="goodsTable.data" size="large" v-loading="goodsTable.loading" ref="goodsListTableRef" @sort-change="sortChange" @selection-change="handleSelectionChange">
                    <template #empty>
                        <span>{{ !goodsTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" />

                    <el-table-column prop="goods_id" :label="t('goodsInfo')" min-width="300">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer" @click="previewEvent(row)">
                                <div class="min-w-[90px] h-[90px] flex items-center justify-center">
                                    <el-image v-if="row.goods_cover_thumb_small" class="w-[90px] h-[90px]" :src="img(row.goods_cover_thumb_small)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[90px] h-[90px]" src="@/addon/mall/assets/goods_default.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[90px] h-[90px]" src="@/addon/mall/assets/goods_default.png" fit="contain" />
                                </div>
                                <div class="ml-2">
                                    <span :title="row.goods_name" class="multi-hidden">{{ row.goods_name }}</span>
                                    <div v-if="row.supply_id">{{ t('supplyName') }}：{{ row.supply_name }}</div>
                                    <span class="text-primary text-[12px]">{{ row.goods_type_name }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="site_name" :label="t('siteName')" min-width="120">
                        <template #default="{ row }">
                            <div>{{ row.site.site_name }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="price" :label="t('skuPrice')" min-width="120" align="right" sortable="custom">
                        <template #default="{ row }">
                            <div class="price-wrap">
                                <span>￥{{ row.goodsSku.price }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('lablel')" min-width="120" align="center">
                        <template #default="{ row }">
                            <div class="flex flex-col items-center">
                                <div v-for="(item, key) in row.goods_label_array" class="my-[3px]" :key="key">
                                    <el-tag type="info">{{ item.label_name }}</el-tag>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sale_num" :label="t('saleNum')" min-width="100" sortable="custom" />
                    <el-table-column prop="status" :label="t('status')" min-width="100">
                        <template #default="{ row }">
                            <div v-if="row.status == 1">{{ t('statusOn') }}</div>
                            <div v-if="row.status == 0">{{ t('statusOff') }}</div>
                            <div v-if="row.status == -1">{{ t('waitAudit') }}</div>
                            <div v-if="row.status == -2">{{ t('auditrefuse') }}</div>
                            <div v-if="row.status == -3">{{ t('waitOffLine') }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" min-width="120" sortable="custom" class-name="sort-column">
                        <template #header>
                            <span class="mr-[4px]">{{ t('sort') }}</span>
                            <el-tooltip class="box-item" effect="dark" :content="t('sortPlaceholder')" placement="bottom">
                                <el-icon>
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                        </template>
                        <template #default="{ row }">
                            <el-input v-model.tirm="row.sort" class="input-width-sort" maxlength="10" @input="sortInputListener($event, row)" />
                        </template>
                    </el-table-column>

                    <el-table-column prop="create_time" :label="t('createTime')" min-width="150" sortable="custom">
                        <template #default="{ row }">
                            <div>{{ row.create_time }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="apply_time" :label="t('申请审核时间')" min-width="150" sortable="custom">
                        <template #default="{ row }">
                            <div>{{ row.apply_time || '' }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="spreadEvent(row)">{{ t('spreadGoods') }}</el-button>
                            <el-button type="primary" link @click="editLabel(row)">{{ t('editLabel') }}</el-button>
                            <div v-if="row.status == 1">
                                <!-- 强制下架 -->
                                <el-button type="primary" link @click="waitOffLine(row)">{{ t('waitOffLine') }}</el-button>
                            </div>
                            <div v-else-if="row.status == -1">
                                <!-- 审核通过 -->
                                <el-button type="primary" link @click="auditEvent(row)">{{ t('审核') }}</el-button>
                                <!-- 审核拒绝 -->
                                <!-- <el-button type="primary" link @click="auditRefuse(row)">{{ t('auditRefuse') }}</el-button> -->
                            </div>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex">
                    <div class="flex items-center flex-1"></div>

                    <el-pagination v-model:current-page="goodsTable.page" v-model:page-size="goodsTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="goodsTable.total"
                        @size-change="loadGoodsList()" @current-change="loadGoodsList" />
                </div>

            </div>

        </el-card>

        <!-- 商品推广弹出框 -->
       <spread-popup ref="spreadPopupRef" />
       <!-- 待审核 -->
        <goods-aduit-popup ref="goodsAuditPopupRef" @confirm="loadGoodsList()" />

        <!-- 审核拒绝弹框 -->
        <el-dialog v-model="refuseShowDialog" :title="t('waitOffLine')" width="460px" class="diy-dialog-wrap" :destroy-on-close="true">
            <el-form :model="refuseFormData" label-width="90px" ref="refuseFormRef" :rules="refundFormRules" class="page-form">
                <el-form-item :label="t('offlineReason')" prop="audit_reason">
                    <el-input v-model.tirm="refuseFormData.audit_reason" type="textarea" rows="4" clearable class="input-width"  :maxlength="100"  :show-word-limit="true" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="refuseShowDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="loading" @click="confirm(refuseFormRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>

        <!-- 编辑标签弹框 -->
        <el-dialog v-model="showDialog" :title="t('label')" width="500px" :destroy-on-close="true">
            <el-form :model="saveData" label-width="90px" class="page-form" v-loading="labelLoading">
                <el-form-item :label="t('label')">
                    <el-select v-model="saveData.label_ids" multiple collapse-tags collapse-tags-tooltip :placeholder="t('labelPlaceholder')" class="input-width">
                        <el-option :label="item.label_name" :value="item.label_id" v-for="(item, index) in labelData" :key="index" />
                    </el-select>
                </el-form-item>
            </el-form>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="labelLoading" @click="handleLabel">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { debounce, img, filterDigit } from '@/utils/common'
import { ElMessage, FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { cloneDeep } from 'lodash-es'
import { getCategoryTree } from '@/app/api/basic_admin'
import { getGoodsPageList, getGoodsStatus, editGoodsSort, offline, getLabelList, editGoodsLabel, getGoodsInfo, getGoodsSource } from '@/addon/mall/api/admin/goods'
import goodsAduitPopup from '@/addon/mall/views/admin/goods/components/goods-audit-popup.vue'

const route = useRoute()
const pageName = route.meta.title
const repeat = ref(false)

const router = useRouter()

const goodsTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        goods_name: '',
        goods_mall_category: [],
        goods_type: '',
        brand_id: '',
        label_ids: '',
        goods_source: '',
        start_sale_num: '',
        end_sale_num: '',
        start_price: '',
        end_price: '',
        status: Object.keys(route.query).length ? (route.query.status === '' ? route.query.status : parseInt(route.query.status)) : 1,
        order: '',
        sort: ''
    }
})

const searchFormRef = ref()

// 正则表达式
const regExp = {
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/
}

// 条件筛选查询

// 商品分类
const goodsCategoryOptions: any = reactive([])
const goodsCategoryProps = {
    checkStrictly: true,
    emitPath: false
}

// 商品状态
const goodsStatus: any = reactive({})

// 商品来源
const goodsSource: any = reactive({})

// 初始化数据
const initData = () => {
    // 查询商品分类树结构
    getCategoryTree().then((res) => {
        const data = res.data
        if (data) {
            const goodsCategoryTree: any = []
            data.forEach((item: any) => {
                const children: any = []
                if (item.child_list) {
                    item.child_list.forEach((childItem: any) => {
                        const grandson: any = []
                        if (childItem.child_list) {
                            childItem.child_list.forEach((grandsonItem: any) => {
                                grandson.push({
                                    label: grandsonItem.category_name,
                                    value: grandsonItem.category_id
                                })
                            })
                        }
                        children.push({
                            label: childItem.category_name,
                            value: childItem.category_id,
                            children: grandson
                        })
                    })
                }
                goodsCategoryTree.push({
                    value: item.category_id,
                    label: item.category_name,
                    children
                })
            })
            goodsCategoryOptions.splice(0, goodsCategoryOptions.length, ...goodsCategoryTree)
        }
    })

    getGoodsStatus().then((res) => {
        const data = res.data
        if (data) {
            Object.assign(goodsStatus, data)
        }
    })
    // 商品来源
    getGoodsSource().then((res) => {
        const data = res.data
        if (data) {
            Object.assign(goodsSource, data)
        }
    })
}

initData()

// 当前选中tab页面
const tabHandleClick = (tab: any, event: Event) => {
    goodsTable.searchParam.status = tab.props.name
    loadGoodsList()
}

// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    goodsListTableRef.value.toggleAllSelection()
}

const goodsListTableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (multipleSelection.value.length > 0 && multipleSelection.value.length < goodsTable.data.length) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == goodsTable.data.length) {
        toggleCheckbox.value = true
    }
}

// 商品预览
const previewEvent = (data: any) => {
    const url = router.resolve({
        path: '/preview/wap',
        query: {
            page: `/addon/mall/pages/goods/detail?goods_id=${data.goods_id}`
        }
    })
    window.open(url.href)
}

// 监听排序
const sortChange = (event: any) => {
    let sort = ''
    if (event.order == 'ascending') {
        sort = 'asc'
    } else if (event.order == 'descending') {
        sort = 'desc'
    }
    if (sort) {
        goodsTable.searchParam.order = event.prop
        goodsTable.searchParam.sort = sort
    }
    loadGoodsList()
}

// 修改排序号
const sortInputListener = debounce((sort, row) => {
    if (isNaN(sort) || !regExp.number.test(sort)) {
        ElMessage({
            type: 'warning',
            message: `${t('sortTips')}`
        })
        return
    }
    editGoodsSort({
        goods_id: row.goods_id,
        sort
    }).then((res) => {
        // loadGoodsList();
    })
})

/**
 * 获取商品列表
 */
const loadGoodsList = (page: number = 1) => {
    if (goodsTable.searchParam.start_sale_num && !regExp.number.test(goodsTable.searchParam.start_sale_num)) {
        ElMessage({
            type: 'warning',
            message: `${t('startSaleNumTips')}`
        })
        return
    }
    if (goodsTable.searchParam.end_sale_num && !regExp.number.test(goodsTable.searchParam.end_sale_num)) {
        ElMessage({
            type: 'warning',
            message: `${t('endSaleNumTips')}`
        })
        return
    }
    if (Number(goodsTable.searchParam.start_sale_num) > Number(goodsTable.searchParam.end_sale_num)) {
        ElMessage({
            type: 'warning',
            message: `${t('goodsSaleNumTips')}`
        })
        return
    }
    if (goodsTable.searchParam.start_price && !regExp.digit.test(goodsTable.searchParam.start_price)) {
        ElMessage({
            type: 'warning',
            message: `${t('startPriceTips')}`
        })
        return
    }
    if (goodsTable.searchParam.end_price && !regExp.digit.test(goodsTable.searchParam.end_price)) {
        ElMessage({
            type: 'warning',
            message: `${t('endPriceTips')}`
        })
        return
    }
    if (Number(goodsTable.searchParam.start_price) > Number(goodsTable.searchParam.end_price)) {
        ElMessage({
            type: 'warning',
            message: `${t('goodsPriceTips')}`
        })
        return
    }

    goodsTable.loading = true
    goodsTable.page = page

    const searchData = cloneDeep(goodsTable.searchParam)

    getGoodsPageList({
        page: goodsTable.page,
        limit: goodsTable.limit,
        ...searchData
    }).then(res => {
        goodsTable.loading = false
        goodsTable.data = res.data.data
        goodsTable.total = res.data.total
    }).catch(() => {
        goodsTable.loading = false
    })
}

loadGoodsList()

// 商品推广
const spreadPopupRef: any = ref(null)

const spreadEvent = (data: any) => {
    const pagePath = '/addon/mall/pages/goods/detail'
    const paramsArr = [
        { name: 'goods_id', value: data.goods_id }
    ]
    const title = '商品推广'
    const folder = 'goods'
    spreadPopupRef.value?.show(pagePath, paramsArr, title, folder)
}
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    goodsTable.searchParam.start_price = ''
    goodsTable.searchParam.end_price = ''
    goodsTable.searchParam.start_sale_num = ''
    goodsTable.searchParam.end_sale_num = ''

    loadGoodsList()
}
// 强制下架
const waitOffLine = (data: any) => {
    refuseFormData.goods_ids = data.goods_id
    refuseShowDialog.value = true
}

// 待审核
const goodsAuditPopupRef = ref<any>(null)
const auditEvent = (data: any) => {
    goodsAuditPopupRef.value?.show(data)
}

// 强制下架
const loading = ref(false)
const refuseFormRef = ref()
const refuseShowDialog = ref(false)
const refuseFormData: Record<string, any> = reactive({
    audit_reason: '',
    goods_ids: ''
})
// 表单验证规则
const refundFormRules = computed(() => {
    return {
        audit_reason: [
            { required: true, message: t('auditReasonPlaceholder'), trigger: 'blur' }
        ]
    }
})
// 拒绝审核,强制下架
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = {
                goods_ids: refuseFormData.goods_ids,
                audit_reason: refuseFormData.audit_reason
            }
            offline(data).then(res => {
                loading.value = false
                refuseShowDialog.value = false
                refuseFormData.audit_reason = ''
                loadGoodsList()
            }).catch(() => {
                loading.value = false
                refuseShowDialog.value = false
            })
        }
    })
}

// 商品标签
const showDialog = ref(false)
const labelData = ref([])
const getLabelListFn = () => {
    getLabelList({}).then(res => {
        labelData.value = res.data
    })
}
getLabelListFn()
const saveData = reactive({
    goods_ids: [],
    label_ids: []
})
const labelLoading = ref(false)
const editLabel = (data: any) => {
    getGoodsInfo(data.goods_id).then(res => {
        saveData.label_ids = res.data.label_ids?.map(el => {
            return Number(el)
        })
        showDialog.value = true
    })
    saveData.goods_ids = []
    saveData.goods_ids.push(data.goods_id)
}
// 保存标签
const handleLabel = () => {
    if (labelLoading.value) return
    labelLoading.value = true
    editGoodsLabel(saveData).then(res => {
        showDialog.value = false
        loadGoodsList()
        saveData.label_ids = []
        labelLoading.value = false
    }).catch(() => {
        labelLoading.value = false
    })
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
}

.input-width-sort {
    width: 70px;
}

.price-wrap {
    &:hover {
        .icon-wrap {
            visibility: visible;
        }
    }
}

.stock-wrap {
    &:hover {
        .icon-wrap {
            visibility: visible;
        }
    }
}

:deep(.sort-column .cell) {
    display: flex;
    align-items: center;
}
</style>
