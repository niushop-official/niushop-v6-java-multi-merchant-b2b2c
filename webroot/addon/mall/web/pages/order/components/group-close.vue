<template>
    <el-dialog v-model="dialogGroupPClose" title="以下订单需一起关闭" width="600px">
       <div class="max-h-[500px] overflow-y-auto group-pay pr-[10px] flex flex-col">
           <div v-for="(item,index) in currentData.order_list" :key="index" class="mb-[20px]">
               <div class="mb-[20px]">
                   <span class="iconfont icon-Vector-25 text-[#999] mr-[6px]"></span>
                   <span class="text-[14px] text-[#333]">{{item.site && item.site.site_name ? item.site.site_name  :''}}</span>
               </div>
               <div v-for="(subItem,subIndex) in item.order_goods" :key="subIndex" class="w-full flex mb-[20px]">
                   <div class="flex items-center flex-shrink-0">
                       <img class="w-[60px] h-[60px]" :src="img(subItem.goods_image_thumb_small ? subItem.goods_image_thumb_small : '')" />
                   </div>
                   <div class="ml-[20px] flex flex-col justify-between flex-1">
                       <div>
                           <div class="text-[14px] text-[#333] font-500  truncate mb-[6px] max-w-[460px]">{{ subItem.goods_name }}</div>
                           <div class="text-[12px] text-[#999] oppoSans-R" v-if="subItem.sku_name">规格：{{ subItem.sku_name }}</div>
                       </div>
                       <div class="flex items-center justify-between mt-auto">
                           <div class="text-[14px] text-[#333]">￥{{subItem.price}}</div>
                           <div class="text-[14px] text-[#333] text-right">x{{subItem.num}}</div>
                       </div>
                   </div>
               </div>
           </div>
       </div>
       <template #footer>
           <div class="dialog-footer">
               <el-button type="primary" @click="confirm">关闭订单</el-button>
           </div>
       </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { deepClone,img } from '@/utils/common'
import { getMallOrderClose } from '@/addon/mall/api/order'

let dialogGroupPClose = ref(false)
const currentData = ref<any>({}) // 组合支付数据
const open = (data:any,row:any) => {
   currentData.value = deepClone(data)
   dialogGroupPClose.value = true
}
const emit = defineEmits(['confirm'])
const confirm = () =>{
    getMallOrderClose(currentData.value.group_id).then(res =>{
        dialogGroupPClose.value = false
        emit('confirm')
    }).catch(() =>{
        dialogGroupPClose.value = false
    })
}
defineExpose({
   open
})
</script>

<style lang="scss" scoped>
.group-pay::-webkit-scrollbar{
   width:4px;
   border-radius:2px;
   background-color:#f1f1f1;
}
.group-pay::-webkit-scrollbar-thumb{
   background-color:#c1c1c1;
   border-radius:2px;
}
</style>