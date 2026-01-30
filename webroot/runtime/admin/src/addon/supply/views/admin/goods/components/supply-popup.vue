<template>
    <div>
        <el-dialog v-model="showDialog" :title="t('查看挑货')" width="1000px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
            <el-table :data="goodsTable.data" size="large" v-loading="goodsTable.loading" max-height="400">
                <template #empty>
                    <span>{{ !goodsTable.loading ? t('emptyData') : '' }}</span>
                </template>

                <el-table-column prop="goods_id" :label="t('商品名称')" min-width="200">
                    <template #default="{ row }">
                        <div class="flex items-center cursor-pointer" @click.stop="goodsPreviewEvent(row)">
                            <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                                <el-image v-if="row.goods_cover" class="w-[70px] h-[70px]" :src="img(row.goods_cover)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[70px] h-[70px]" src="@/addon/supply/assets/goods_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                                <img v-else class="w-[70px] h-[70px]" src="@/addon/supply/assets/goods_default.png" fit="contain" />
                            </div>
                            <div class="ml-2">
                                <span :title="row.goods_name" class="multi-hidden text-[var(--el-color-primary)]">{{ row.goods_name }}</span>
                                <el-popover placement="right" :width="500" trigger="click" v-if="row.skuList.length">
                                    <template #reference>
                                        <span @click.stop class="text-primary flex items-center text-[12px]">共{{row.skuList.length}}个规格<span class="iconfont iconjiantouxia ml-[4px] !text-[10px]"></span></span>
                                    </template>
                                    <el-table :data="row.skuList">
                                        <el-table-column width="200" property="sku_name" :label="t('skuName')" />
                                        <el-table-column width="110" :label="t('skuPrice')">
                                            <template #default="{ row }">￥{{ row.price }}</template>
                                        </el-table-column>
                                        <el-table-column width="80" property="sale_num" :label="t('saleNum')" />
                                        <el-table-column width="80" property="stock" :label="t('stock')" />
                                    </el-table>
                                </el-popover>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="price" :label="t('售价')" min-width="120">
                    <template #default="{ row }">
                        {{ row.goodsSku.price }}
                    </template>
                </el-table-column>
                <el-table-column :label="t('挑货店铺')" min-width="120">
                    <template #default="{ row }">
                       <div class="cursor-pointer  text-[var(--el-color-primary)]" @click.stop="shopPreviewEvent(row)" >{{ row.site.site_name }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="status_name" :label="t('status')" min-width="100" />
                <el-table-column prop="sale_num" :label="t('销量')" min-width="120" />
                <el-table-column prop="create_time" :label="t('挑货时间')" min-width="160" />
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="goodsTable.page" v-model:page-size="goodsTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="goodsTable.total"
                        @size-change="loadGoodsList()" @current-change="loadGoodsList" />
            </div>
        </el-dialog>
    </div>
</template>
<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive } from 'vue'
import { img } from '@/utils/common'
import { getShopSupply } from '@/addon/supply/api/admin/select_goods'
import { useRouter } from 'vue-router'

const router = useRouter()
const showDialog = ref(false)
const goods: any = reactive({})
const goodsTable = reactive<any>({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})

/**
 * 获取商品列表
 */
const loadGoodsList = (page: number = 1) => {
    goodsTable.loading = true
    goodsTable.page = page

    getShopSupply({
        goods_id: goods.goods_id
    }).then(res => {
        goodsTable.loading = false
        goodsTable.data = res.data.data
        goodsTable.total = res.data.total
    }).catch(() => {
        goodsTable.loading = false
    })
}

// 商品预览
const goodsPreviewEvent = (data: any) => {
    const url = router.resolve({
        path: '/preview/wap',
        query: {
            page: `/addon/mall/pages/goods/detail?goods_id=${data.goods_id}`
        }
    })
    window.open(url.href)
}

// 店铺预览
const shopPreviewEvent = (data: any) => {
    const url = router.resolve({
        path: '/preview/wap',
        query: {
            page: `/addon/mall/pages/index?site_id=${data.site_id}`
        }
    })
    window.open(url.href)
}

const show = (data: any) => {
    Object.assign(goods, data)
    loadGoodsList()
    showDialog.value = true
}

defineExpose({
    showDialog,
    show
})
</script>