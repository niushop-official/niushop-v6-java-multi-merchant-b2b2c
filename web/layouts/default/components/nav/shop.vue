<template>
    <div class="bg-[#fff]">
        <div class="flex items-center justify-between w-[1200px] mx-auto h-[44px] leading-[44px] bg-[#fff]" >
            <div class="flex items-center">
                <NuxtLink :to="'/shop/index?site_id='+route.query.site_id">
                    <div class=" w-[208px] bg-[var(--el-color-primary)] text-[#fff] text-center cursor-pointer text-[14px] leading-[44px] rounded-t-[12px]">店铺首页</div>
                </NuxtLink>
                <NuxtLink :to="'/shop/detail?site_id='+route.query.site_id">
                    <div class="h-[26px] leading-[26px] px-[20px] text-[14px] hover:text-[var(--el-color-primary)]" :class="{'text-[var(--el-color-primary)]':route.path=='/shop/detail'}">全部商品</div>
                </NuxtLink>
                <NuxtLink :to="'/shop/coupon?site_id='+route.query.site_id">
                    <div class="h-[26px] leading-[26px] px-[20px] text-[14px] hover:text-[var(--el-color-primary)]" :class="{'text-[var(--el-color-primary)]':route.path=='/shop/coupon'}">领优惠券</div>
                </NuxtLink>
            </div>
            <div></div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { watch} from "vue";
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
// watch(()=> route ,(newValue,oldValue)=>{
//     if(oldValue&&newValue.path===oldValue.path){
//         location.reload()
//     }
// },{immediate:true,deep: true})
</script>