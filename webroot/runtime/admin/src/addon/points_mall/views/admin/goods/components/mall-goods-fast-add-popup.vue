<template>
    <el-dialog v-model="showDialog" :title="t('商品选择')" width="60%" :destroy-on-close="true">
        <el-alert type="warning" :closable="false" class="!mb-[20px]">
            <template #default>
                <p>商品信息取自商城，库存单独配置，不关联商城商品</p>
            </template>
        </el-alert>
        <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
                <el-form-item :label="t('goodsSelectPopupSiteName')" prop="site_id" class="form-item-wrap">
                    <el-select v-model="tableData.searchParam.site_id">
                        <el-option :label="t('goodsSelectPopupSelectPlaceholder')" value="" />
                        <el-option v-for="item in shopList" :key="item.site_id" :label="item.site_name" :value="item.site_id" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('goodsSelectPopupGoodsName')" prop="keyword" class="form-item-wrap">
                    <el-input v-model="tableData.searchParam.keyword" :placeholder="t('goodsSelectPopupGoodsNamePlaceholder')" maxlength="60" />
                </el-form-item>
                <el-form-item :label="t('goodsSelectPopupGoodsCategory')" prop="goods_mall_category" class="form-item-wrap">
                <el-cascader v-model="tableData.searchParam.goods_mall_category"
                        :options="goodsCategoryOptions" :placeholder="t('goodsSelectPopupGoodsCategoryPlaceholder')"
                        clearable :props="{ value: 'value', label: 'label', emitPath:false }" />
                </el-form-item>
                <el-form-item class="form-item-wrap">
                    <el-button type="primary" @click="loadGoodsList()">{{ t('search') }}</el-button>
                    <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                </el-form-item>
            </el-form>
        <!-- 列表 -->
        <div class="mt-[10px]">
            <el-table :data="tableData.data" ref="tableRef" row-key="id" size="large" v-loading="tableData.loading" max-height="450">
                <template #empty>
                    <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
                </template>

                <el-table-column :label="t('goodsSelectPopupGoodsInfo')" min-width="130">
                    <template #default="{ row }">
                        <div class="flex items-center cursor-pointer">
                            <div class="min-w-[60px] h-[60px] flex items-center justify-center">
                                <el-image v-if="row.goods_cover" class="w-[60px] h-[60px]" :src="img(row.goods_cover)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[60px] h-[60px]" src="@/addon/points_mall/assets/goods_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                                <img v-else class="w-[60px] h-[60px]" src="@/addon/points_mall/assets/goods_default.png" fit="contain" />
                            </div>
                            <div class="ml-2">
                                <span :title="row.goods_name" class="multi-hidden">{{ row.goods_name }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('goodsSelectPopupPrice')" min-width="130">
                    <template #default="{ row }">
                      ￥{{ row.price }}
                    </template>
                </el-table-column>
                <el-table-column prop="site_name" :label="t('店铺名称')" min-width="130" />
                <el-table-column :label="t('goodsSelectPopupStock')" min-width="130" align="right">
                    <template #default="{ row }">
                        <span>{{ row.stock }}</span>
                    </template>
                </el-table-column>
                <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="selectEvent(row)">选择</el-button>
                        </template>
                    </el-table-column>
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total" @size-change="loadGoodsList()" @current-change="loadGoodsList" />
            </div>
        </div>
    </el-dialog>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRouter } from 'vue-router'
import { getAdminGoodsSelect, getGoodsType } from '@/addon/points_mall/api/admin/goods'
import { getShopList } from '@/app/api/shop_admin'
import { getCategoryTree } from '@/app/api/basic_admin'
import { FormInstance } from 'element-plus'

const router = useRouter()
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        site_id: '',
        keyword: '',
        goods_mall_category: [],
        goods_type: ''
    }
})

const showDialog = ref(false)
const searchFormRef = ref()

// 商品分类
const goodsCategoryOptions: any = reactive([])

// 商品类型
const goodsType: any = reactive([])

// 店铺列表
const shopList: any = reactive([])
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
                        // 三级分类
                        const thirdChildren: any = []
                        if (childItem.child_list) {
                            childItem.child_list.forEach((thirdChildItem: any) => {
                                thirdChildren.push({
                                    value: thirdChildItem.category_id,
                                    label: thirdChildItem.category_name
                                })
                            })
                        }

                        children.push({
                            value: childItem.category_id,
                            label: childItem.category_name,
                            children: thirdChildren
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

    // 商品类型
    getGoodsType().then((res) => {
        const data = res.data
        if (data) {
            for (const k in data) {
                goodsType.push(data[k])
            }
        }
    })
    // 店铺列表
    getShopList({
        status: 1
    }).then((res: any) => {
        if (res.data) {
            shopList.splice(0, shopList.length, ...res.data)
        }
    })
}
initData()
const loadGoodsList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getAdminGoodsSelect({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then((res: any) => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
    }).catch(() => {
        tableData.loading = false
    })
}
loadGoodsList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()

    loadGoodsList()
}

const selectEvent = (data: any) => {
    router.push({
        path: '/admin/points_mall/goods/edit',
        query: {
            goods_id: data.goods_id,
            source: 'mall'
        }
    })
}

defineExpose({
    showDialog
})
</script>