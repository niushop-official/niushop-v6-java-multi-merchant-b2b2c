<template>
    <el-dialog v-model="dialogGiftGoods" title="赠品" width="600px">
        <div class="max-h-[500px] overflow-y-auto gift-goods pr-[10px]" v-if="giftData && giftData.length">
            <div v-for="(cItem, index) in giftData" :key="index" class="flex mb-[20px] bg-[#f9f9f9] rounded-[8px] p-[10px] cursor-pointer" @click="toDetail(cItem.goods_id)">
                <div class="flex items-center shrink-0">
                    <el-image class="w-[60px] h-[60px] rounded-[var(--rounded-small)]"  :src="img(cItem.sku_image ? cItem.sku_image : '')" fit="cover">
                        <template #error>
                            <img src="@/assets/images/goods_default.png" class="w-[60px] h-[60px] rounded-[var(--rounded-small)]">
                        </template>
                    </el-image>
                </div>
                <div class="ml-[20px] flex flex-col justify-between flex-1">
                    <div>
                        <div class="text-[14px] text-[#333] font-500  truncate mb-[6px] max-w-[460px]">{{ cItem.goods.goods_name }}</div>
                        <div class="text-[12px] text-[#999] oppoSans-R" v-if="cItem.sku_name">规格：{{ cItem.sku_name }}</div>
                    </div>
                    <div class="flex items-center justify-end mt-auto">
                        <div class="text-[14px] text-[#333] text-right">x{{ cItem.num }}</div>
                    </div>
                </div>
            </div>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button type="primary" @click="confirm">关闭</el-button>
            </div>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { deepClone } from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const dialogGiftGoods = ref(false)
const giftData = ref<any>(null)

const open = (data:any)=> {
    dialogGiftGoods.value = true
    giftData.value = deepClone(data)
}

const confirm = ()=> {
    dialogGiftGoods.value = false
}

const toDetail = (goods_id:number) =>{
    const url = router.resolve({
        path: '/goods/detail',
        query: { id: goods_id }
    })
    window.open(url.href) 
}
defineExpose({
	open,
})
</script>

<style lang="scss" scoped>
.gift-goods::-webkit-scrollbar{
	width:4px;
	border-radius:2px;
	background-color:#f1f1f1;
}
.gift-goods::-webkit-scrollbar-thumb{
	background-color:#c1c1c1;
	border-radius:2px;
}
</style>