<template>
    <el-dialog v-model="showDialog" title="批量挑货" width="700px" class="dialog-wrap" :destroy-on-close="true">
        <div class="supply-popup">
            <el-alert  type="warning" :closable="false">
                <template #title>
                    <span class="!text-[14px]">温馨提示：批量挑货商品至店铺, 默认按建议零售价进行定价</span>
                </template>
            </el-alert>
        </div>
        <div>
            <div class="my-[10px]">已选 {{ goodsIds.length }} 件商品：</div>
            <el-table :data="tableData" height="350"  v-loading="loading">
                <el-table-column  label="商品">
                    <template #default="{ row }">
                            <div class="flex items-center cursor-pointer">
                                <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                                    <el-image  class="w-[70px] h-[70px]" :src="img(row.goods_cover_thumb_small)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[70px] h-[70px]" src="@/addon/mall/assets/goods_default.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                </div>
                                <div class="ml-2">
                                    <span :title="row.goods_name" class="multi-hidden">{{ row.goods_name }}</span>
                                </div>
                            </div>
                        </template>
                </el-table-column>
                <el-table-column label="供货价">
                    <template #default="{ row }">
                        <div>￥{{ moneyFormat(row.goodsSku.cost_price) }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="name" label="建议零售价">
                    <template #default="{ row }">
                        <div>￥{{ moneyFormat(row.goodsSku.price) }}</div>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">取消</el-button>
                <el-button type="primary" class="!bg-[var(--price-color)] !border-[var(--price-color)]" :loading="repeatLoading" @click="save">仅添加暂不上架</el-button>
                <el-button type="primary" class="!bg-[var(--price-color)] !border-[var(--price-color)]" :loading="flagLoading" @click="comfrim">立即上架</el-button>
            </span>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { img, moneyFormat } from '@/utils/common'
import { getBatchPick, batchPickGoods } from '@/app/lang/zh-cn/api/site/select_goods'
import { cloneDeep } from 'lodash-es'

const showDialog = ref(false)
const loading = ref(false)
const repeatLoading = ref(false)
const flagLoading = ref(false)

const tableData = ref([])

const goodsIds = ref([])
const open = (ids: any) => {
    goodsIds.value = cloneDeep(ids)
    loading.value = true
    getBatchPick({ goods_ids: ids }).then(res => {
        tableData.value = res.data
        loading.value = false
    })
    showDialog.value = true
}

const emit = defineEmits(['confirm'])
// 仅添加暂不上架
const save = () => {
    if (repeatLoading.value) return false
    repeatLoading.value = true
    const data = {
        goods_ids: goodsIds.value,
        status: 0 // 0仅添加暂不上架 1立即上架
    }
    batchPickGoods(data).then(res => {
        emit('confirm')
        repeatLoading.value = false
        showDialog.value = false
    }).catch(() => {
        repeatLoading.value = false
    })
}

// 立即上架
const comfrim = () => {
    if (flagLoading.value) return false
    flagLoading.value = true
    const data = {
        goods_ids: goodsIds.value,
        status: 1 // 0仅添加暂不上架 1立即上架
    }
    batchPickGoods(data).then(res => {
        emit('confirm')
        flagLoading.value = false
        repeatLoading.value = false
        showDialog.value = false
    }).catch(() => {
        flagLoading.value = false
        repeatLoading.value = false
    })
}
defineExpose({
    showDialog,
    open
})
</script>
<style lang="scss" scoped>

</style>
