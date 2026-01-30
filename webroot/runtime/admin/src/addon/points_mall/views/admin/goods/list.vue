<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <div>
                    <el-button type="primary" @click="addEvent">{{ t('addGoods') }} </el-button>
                    <el-button type="primary" @click="fastAdd">快速添加</el-button>
                </div>
            </div>

             <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="goodsTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('goodsName')" prop="goods_name">
                        <el-input v-model.trim="goodsTable.searchParam.goods_name" :placeholder="t('goodsNamePlaceholder')" maxlength="60" />
                    </el-form-item>
                    <el-form-item :label="t('goodsCategory')" prop="goods_category">
                        <el-cascader :props="goodsCategoryProps" v-model="goodsTable.searchParam.goods_category" :options="goodsCategoryOptions" :placeholder="t('goodsCategoryPlaceholder')" clearable filterable />
                    </el-form-item>
                     <el-form-item :label="t('goodsGroup')" prop="category">
                        <el-select v-model="goodsTable.searchParam.category" :placeholder="t('goodsGroupPlaceholder')" clearable >
                            <el-option v-for="(item,index) in goodsGroupOptions" :key="index" :label="item.category_name" :value="item.category_id" />
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
                    <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                    <el-button @click="batchGoodsStatus(1)" size="small" v-if="goodsTable.searchParam.status != '1'">{{ t('batchOnGoods') }}</el-button>
                    <el-button @click="batchGoodsStatus(0)" size="small" v-if="goodsTable.searchParam.status != '0'">{{ t('batchOffGoods') }}</el-button>
                    <el-button @click="batchDeleteGoods" size="small">{{ t('batchDeleteGoods') }}</el-button>
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
                                                <img class="w-[90px] h-[90px]" src="@/addon/points_mall/assets/goods_default.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[90px] h-[90px]" src="@/addon/points_mall/assets/goods_default.png" fit="contain" />
                                </div>
                                <div class="ml-2  flex flex-col items-start">
                                    <span :title="row.goods_name" class="multi-hidden">{{ row.goods_name }}</span>
                                    <span class="text-primary text-[12px]">{{ row.goods_type_name }}</span>
                                    <div class="flex flex-wrap mt-[4px] gap-[4px]">
                                        <el-tooltip v-for="(item, index) in row.active" :key="index" placement="top">
                                            <template #content>
                                                <div style="white-space: pre-wrap">
                                                    {{item.name.trim() || item.short?.active_name }}
                                                </div>
                                            </template>
                                            <span class="text-[12px] text-white rounded-[4px] px-[4px] leading-[18px]"  @click.stop="activeclick(item)" :style="{ backgroundColor: item.short?.bg_color || '#333' }">
                                                {{ item.short?.name }}
                                            </span>
                                        </el-tooltip>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="price" :label="t('兑换积分')" min-width="120" align="right" sortable="custom">
                        <template #default="{ row }">
                            <div class="cursor-pointer price-wrap">
                                <span>{{ parseInt(row.goodsSku.point) }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="price" :label="t('兑换金额')" min-width="120" align="right" sortable="custom">
                        <template #default="{ row }">
                            <div class="cursor-pointer price-wrap">
                                <span>{{ row.goodsSku.price }}</span>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="stock" :label="t('stock')" min-width="120" sortable="custom">
                        <template #default="{ row }">
                            <div class="stock-wrap" >
                                <span>{{ row.stock }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sale_num" :label="t('saleNum')" min-width="100" sortable="custom" />
                    <el-table-column prop="status" :label="t('status')" min-width="100">
                        <template #default="{ row }">
                            <div v-if="row.status == 1">{{ t('statusOn') }}</div>
                            <div v-if="row.status == 0">{{ t('statusOff') }}</div>
                            <div v-if="row.status == -1">{{ t('waitAudit') }}</div>
                            <div v-if="row.status == -3">{{ t('waitOffLine') }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort"  min-width="120" sortable="custom"  class-name="sort-column">
                        <template #header>
                            <span class="mr-[4px]">{{t('sort')}}</span>
                            <el-tooltip class="box-item" effect="dark" :content="t('sortPlaceholder')"  placement="bottom">
                                <el-icon><QuestionFilled /></el-icon>
                            </el-tooltip>
                        </template>
                        <template #default="{ row }">
                            <el-input v-model="row.sort" class="input-width-sort" maxlength="10" @input="sortInputListener($event, row)" />
                        </template>
                    </el-table-column>

                    <el-table-column prop="create_time" :label="t('createTime')" min-width="150" sortable="custom">
                        <template #default="{ row }">
                            <div>{{ row.create_time }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="spreadEvent(row)">{{ t('spreadGoods') }}</el-button>
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" v-if="row.status == 1" link @click="statusChange(row, 0)">{{ t('statusActionOff') }}</el-button>
                            <el-button type="primary" v-else-if="row.status == 0" link @click="statusChange(row, 1)">{{ t('statusActionOn') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.goods_id)">{{ t('delete') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="goodsTable.page" v-model:page-size="goodsTable.limit" layout="total, sizes, prev, pager, next, jumper" :total="goodsTable.total" @size-change="loadGoodsList()" @current-change="loadGoodsList" />
                </div>
            </div>
        </el-card>
        <mall-goods-fast-add-popup ref="mallGoodsFastAddPopupRef"  />
        <!-- 商品推广弹出框 -->
       <spread-popup ref="spreadPopupRef" />
    </div>
</template>
<script  lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { debounce, img, filterDigit } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { cloneDeep } from 'lodash-es'
import { getCategoryTree } from '@/app/api/basic_admin'
import { getCategory } from '@/addon/points_mall/api/admin/category'
import { getGoodsPageList, getGoodsStatus, editGoodsSort, editGoodsStatus, deleteGoods } from '@/addon/points_mall/api/admin/goods'
import mallGoodsFastAddPopup from '@/addon/points_mall/views/admin/goods/components/mall-goods-fast-add-popup.vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const goodsTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        goods_name: '',
        goods_category: [],
        category: '',
        start_sale_num: '',
        end_sale_num: '',
        start_price: '',
        end_price: '',
        status: '',
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

// 商品类目
const goodsCategoryOptions: any = reactive([])
const goodsCategoryProps = {
    checkStrictly: true,
    emitPath: false
}

// 商品分组
const goodsGroupOptions: any = reactive([])

// 商品状态
const goodsStatus: any = reactive({})

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
}

initData()

const refreshGoodsGroup = () => {
    getCategory().then((res) => {
        goodsGroupOptions.splice(0, goodsGroupOptions.length, ...res.data)
    })
}
refreshGoodsGroup()

const getGoodsStatusFn = () => {
    getGoodsStatus().then((res) => {
        const data = res.data
        if (data) {
            Object.assign(goodsStatus, data)
        }
    })
}
getGoodsStatusFn()
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

// 修改商品上下架状态
let statusRepeat = false
const statusChange = (row: any, value: any) => {
    if (value) {
        if (statusRepeat) return
        statusRepeat = true
        editGoodsStatus({
            goods_ids: row.goods_id,
            status: value
        }).then((res) => {
            statusRepeat = false
            getGoodsStatusFn()
            loadGoodsList()
        }).catch(() => {
            statusRepeat = false
        })
    } else {
        ElMessageBox.confirm(t('statusChangeTips'), t('warning'),
            {
                confirmButtonText: t('confirm'),
                cancelButtonText: t('cancel'),
                type: 'warning'
            }
        ).then(() => {
            editGoodsStatus({
                goods_ids: row.goods_id,
                status: value
            }).then((res) => {
                loadGoodsList()
            })
        })
    }
}

// 批量设置上下架
const batchGoodsStatus = (status: any) => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedGoodsTips')}`
        })
        return
    }

    const goodsIds: any = []
    multipleSelection.value.forEach((item: any) => {
        goodsIds.push(item.goods_id)
    })

    editGoodsStatus({
        goods_ids: goodsIds,
        status
    }).then((res) => {
        loadGoodsList()
    })
}

const repeat = ref(false)
const batchDeleteGoods = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedGoodsTips')}`
        })
        return
    }

    ElMessageBox.confirm(t('batchGoodsDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (repeat.value) return
        repeat.value = true

        const goodsIds: any = []
        multipleSelection.value.forEach((item: any) => {
            goodsIds.push(item.goods_id)
        })

        deleteGoods({
            goods_ids: goodsIds
        }).then(() => {
            toggleCheckbox.value = false
            isIndeterminate.value = false
            loadGoodsList()
            repeat.value = false
        }).catch(() => {
            repeat.value = false
        })
    })
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
    }).then((res: any) => {
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
    const pagePath = '/addon/points_mall/pages/goods/detail'
    const paramsArr = [
        { name: 'goods_id', value: data.goods_id }
    ]
    const title = '商品推广'
    const folder = 'goods'
    spreadPopupRef.value?.show(pagePath, paramsArr, title, folder)
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
/**
 * 添加商品
 */
const addEvent = () => {
    router.push('/points_mall/goods/edit')
}

/**
 * 编辑商品
 * @param data
 */
const editEvent = (data: any) => {
    console.log(data)
    router.push({
        path: '/points_mall/goods/edit',
        query: {
            goods_id: data.goods_id
        }
    })
}
// 删除商品
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('goodsDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (repeat.value) return
        repeat.value = true
        deleteGoods({
            goods_ids: id
        }).then(() => {
            loadGoodsList()
            repeat.value = false
        }).catch(() => {
            repeat.value = false
        })
    })
}

// 快速添加商品
const mallGoodsFastAddPopupRef = ref()
const fastAdd = () => {
    mallGoodsFastAddPopupRef.value.showDialog = true
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
</script>