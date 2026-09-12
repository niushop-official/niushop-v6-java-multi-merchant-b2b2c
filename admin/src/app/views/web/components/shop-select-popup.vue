<template>
    <div>
        <div @click="show">
            <slot>
                <el-button>选择店铺</el-button>
                <div class="inline-block ml-[10px] text-[14px]" v-show="shopIds.length">
                    <span>已选</span>
                    <span class="text-primary mx-[2px]">{{ shopIds.length }}</span>
                    <span>个</span>
                </div>
            </slot>
        </div>
        <el-dialog v-model="showDialog" title="店铺选择" width="1000px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
            <el-form :inline="true" :model="shopTable.searchParam" ref="searchFormRef">
                <el-form-item prop="select_type" class="form-item-wrap">
                    <el-select v-model="shopTable.searchParam.select_type" @change="handleSelectTypeChange">
                        <el-option label="全部店铺" value="all" />
                        <el-option label="已选店铺" value="selected" />
                    </el-select>
                </el-form-item>
                <el-form-item label="所属行业" prop="category_id" class="form-item-wrap">
                    <el-select v-model="shopTable.searchParam.category_id" placeholder="请选择所属行业" clearable>
                        <el-option v-for="(item,index) in shopCategory" :key="index" :label="item.name" :value="item.category_id" />
                    </el-select>
                </el-form-item>
                <el-form-item class="form-item-wrap">
                    <el-button type="primary" @click="loadShopsList()">{{ t('search') }}</el-button>
                    <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                </el-form-item>
            </el-form>
            <div>
                <el-table :data="shopTable.data" ref="shopTableRef" size="large" v-loading="shopTable.loading"
                    height="490px" @select="handleSelectChange" @select-all="handleSelectAllChange">
                    <template #empty>
                        <span>{{ !shopTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" />
                    <el-table-column prop="site_name" label="店铺名称" min-width="120"/>
                    <el-table-column prop="category_name" label="所属行业" min-width="120"/>
                </el-table>
                <div class="mt-[16px] flex">
                    <div class="flex items-center flex-1">
                        <div class="layui-table-bottom-left-container mr-[10px]" v-show="selectShopNum">
                            <span>已选择</span>
                            <span class="text-primary mx-[2px]">{{ selectShopNum }}</span>
                            <span>个店铺</span>
                        </div>
                        <el-button type="primary" link @click="clear" v-show="selectShopNum">取消选择</el-button>
                    </div>
                    <el-pagination v-model:current-page="shopTable.page" v-model:page-size="shopTable.limit"
                    layout="total, sizes, prev, pager, next, jumper" :total="shopTable.total"
                    @size-change="loadShopsList()" @current-change="loadShopsList" />
                </div>
            </div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="save">{{ t('confirm') }}</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>
<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive, computed, nextTick } from 'vue'
import { cloneDeep } from 'lodash-es'
import { ElMessage, FormInstance } from 'element-plus'
import { getShopSelect } from '@/app/api/shop_admin'
import { getSiteCategoryAll } from '@/app/api/site_category'

const prop = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    max: {
        type: Number,
        default: 0
    },
    min: {
        type: Number,
        default: 0
    }
})

const emit = defineEmits(['update:modelValue', 'shopSelect'])

const shopIds: any = computed({
    get () {
        return prop.modelValue
    },
    set (value) {
        emit('update:modelValue', value)
    }
})

const showDialog = ref(false)

// 已选店铺列表
const selectShop: any = reactive({})

// 已选店铺数量
const selectShopNum: any = computed(() => {
    return Object.keys(selectShop).length
})

// 初始化数据
const shopCategory = ref<any>([])
const initData = () => {
    getSiteCategoryAll({addon: 'mall'}).then((res: any) => {
        shopCategory.value = res.data
    })
}

initData()

// 查询全部/已选店铺
const handleSelectTypeChange = (value: any) => {
    loadShopsList()
}

const shopTableRef = ref()
const shopTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        select_type: 'all',
        category_id: '',
        verify_site_ids: '',
        site_ids: '',
    }
})

const loadShopsList = (page: number = 1, callback: any = null) => {
    shopTable.loading = true
    shopTable.page = page

    const searchData = cloneDeep(shopTable.searchParam);

    if (searchData.select_type == 'selected') {
        const site_ids = <any>[]
        for (let k in selectShop) {
            site_ids.push(parseInt(k.replace('site_', '')))
        }
        searchData.site_ids = site_ids 
    } else {
        searchData.site_ids = '';
    }

    getShopSelect({
        page: shopTable.page,
        limit: shopTable.limit,
        ...searchData
    }).then((res: any) => {
        shopTable.loading = false
        shopTable.data = res.data.data
        shopTable.total = res.data.total

        if (callback) callback(res.data.verify_site_ids)

        setShopSelected()
    }).catch(() => {
        shopTable.loading = false
    })
}

loadShopsList()

// 选择商品分类
const handleSelectChange = (selection: any, row: any) => {
    // 是否选中
    let isSelected = false
    for (let i = 0; i < selection.length; i++) {
        if (selection[i].site_id == row.site_id) {
            isSelected = true
            break
        }
    }
    if (isSelected) {
        selectShop['site_' + row.site_id] = row
    } else {
        // 未选中，删除当前店铺
        delete selectShop['site_' + row.site_id]
    }
}

// 监听表格全选
const handleSelectAllChange = (selection: any) => {
    if (selection.length) {
        selection.forEach((item: any) => {
            selectShop['site_' + item.site_id] = item
        })
    } else {
        // 未选中，删除当前页面的数据
        shopTable.data.forEach((item: any) => {
            delete selectShop['site_' + item.site_id]
        })
    }
}

// 表格设置选中状态
const setShopSelected = () => {
    nextTick(() => {
        if (!shopTableRef.value) return
        for (let i = 0; i < shopTable.data.length; i++) {
            shopTableRef.value.toggleRowSelection(shopTable.data[i], false)
            if (selectShop['site_' + shopTable.data[i].site_id]) {
                shopTableRef.value.toggleRowSelection(shopTable.data[i], true)
            }
        }
    })
}
const show = () => {
    // 检测店铺id集合是否存在，移除不存在的店铺id，纠正数据准确性
    shopTable.searchParam.verify_site_ids = shopIds.value
    loadShopsList(1, (verify_site_ids: any) => {
        // 第一次打开弹出框时，纠正数据，并且赋值已选店铺
        if (shopIds.value) {
            shopIds.value.splice(0, shopIds.value.length, ...verify_site_ids)
            // 先删除 selectShop 中已经不再存在于 shopIds 中的店铺
            for (const key in selectShop) {
                const shopId = key.replace('site_', '')
                if (!shopIds.value.includes(Number(shopId))) {
                    delete selectShop[key] // 删除不存在的店铺
                }
            }
            shopIds.value.forEach((item: any) => {
                if (!selectShop['site_' + item]) {
                    selectShop['site_' + item] = {}
                }
            })

            // 赋值已选择的店铺
            for (let i = 0; i < shopTable.data.length; i++) {
                if (shopIds.value.indexOf(shopTable.data[i].site_id) != -1) {
                    selectShop['site_' + shopTable.data[i].site_id] = shopTable.data[i]
                }
            }
        }
    })
    showDialog.value = true
}
// 清空已选店铺
const clear = () => {
    for (const k in selectShop) {
        delete selectShop[k]
    }
    setShopSelected()
}

const searchFormRef = ref<FormInstance>()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()

    loadShopsList()
}
const save = () => {
    if (prop.min && selectShopNum.value < prop.min) {
        ElMessage({
            type: 'warning',
            message: `${t('所选店铺数量不能少于')}${prop.min}${t('个')}`
        })
        return
    }

    if (prop.max && prop.max > 0 && selectShopNum.value && selectShopNum.value > prop.max) {
        ElMessage({
            type: 'warning',
            message: `${t('所选店铺数量不能超过')}${prop.max}${t('个')}`
        })
        return
    }

    const ids: any = []
    for (const k in selectShop) {
        ids.push(parseInt(k.replace('site_', '')))
    }

    shopIds.value.splice(0, shopIds.value.length, ...ids)
    emit('shopSelect', selectShop)
    showDialog.value = false
}

defineExpose({
    showDialog,
    selectShop,
    selectShopNum
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