<template>
    <div>
        <el-dialog v-model="showDialog" :title="t('查看供货信息')" width="800px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
            <el-table :data="[supplyData]" size="large" v-loading="loading" max-height="400">
                <template #empty>
                    <span>{{ !loading ? t('emptyData') : '' }}</span>
                </template>

                <el-table-column prop="goods_id" :label="t('商品')" min-width="200">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                                <el-image v-if="row.goods.goods_cover" class="w-[70px] h-[70px]" :src="img(row.goods.goods_cover)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[70px] h-[70px]" src="@/addon/mall/assets/goods_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                                <img v-else class="w-[70px] h-[70px]" src="@/addon/mall/assets/goods_default.png" fit="contain" />
                            </div>
                            <div class="ml-2">
                                <span :title="row.goods.goods_name" class="multi-hidden">{{ row.goods.goods_name }}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="goods_id" :label="t('供货商品')" min-width="200">
                    <template #default="{ row }">
                        <div class="flex items-center cursor-pointer" @click.stop="toDetail(row)">
                            <span :title="row.supply.goods_name" class="multi-hidden text-[var(--el-color-primary)]">{{ row.supply.goods_name }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column :label="t('供货商')" min-width="120">
                    <template #default="{ row }">
                       <div class="cursor-pointer  text-[var(--el-color-primary)]" @click.stop="handleSupply(row)">{{ row.supply.site.site_name }}</div>
                    </template>
                </el-table-column>
            </el-table>
        </el-dialog>
    </div>
</template>
<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive } from 'vue'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getSupplyGoods } from '@/app/lang/zh-cn/api/site/select_goods'

const router = useRouter()
const route = useRoute()

const showDialog = ref(false)
const loading = ref(false)
const supplyData: any = reactive({
    goods: {},
    supply: {
        site: {}
    }
})

/**
 * 获取商品列表
 */
const loadGoods = () => {
    loading.value = true
    getSupplyGoods(supplyData.goods.supply_goods_id).then(res => {
        supplyData.supply = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

const toDetail = (data: any) => {
    const url = router.resolve({
        path: '/supply/select/detail',
        query: {
            goods_id: data.supply.goods_id
        }
    })
    window.open(url.href)
}

const handleSupply = (data: any) => {
    const url = router.resolve({
        path: '/supply/select/supply',
        query: {
            site_id: data.supply.site_id
        }
    })
    window.open(url.href)
}
const show = (data: any) => {
    supplyData.goods = data
    loadGoods()
    showDialog.value = true
}

defineExpose({
    showDialog,
    show
})
</script>