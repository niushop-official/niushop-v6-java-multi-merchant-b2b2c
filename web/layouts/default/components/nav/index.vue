<template>
    <div class="bg-[#fff] border-b-[2px] border-b-[var(--el-color-primary)] border-solid">
        <div class="flex items-center  w-[1200px] mx-auto h-[46px] leading-[46px] bg-[#fff]" >
            <div class="category w-[200px] bg-primary text-[#fff] flex items-center justify-center cursor-pointer text-[16px] leading-[46px] relative rounded-t-[12px]" @click="router.push({ path: '/goods/list' })">
                <span class="iconfont icon-fenleiV6xx mr-[8px] "></span>
                <span>商品分类</span>
                
            </div>
            <div class="px-20px flex item-center flex-1 overflow-x-auto">
                <template v-for="(item,index) in navList" :key="index" >
                    <div class="px-[20px] text-[#333] text-[16px] leading-[46px] cursor-pointer" :class="{'!text-[var(--el-color-primary)]':activeName == item.nav_url}" @click="handleClick(item)">{{ item.nav_title }}</div>
                </template>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref,reactive, watch} from "vue";
import { isUrl } from '@/utils/common'
import { getNavList } from '@/app/api/index'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeName = ref('')
const navList = ref([])
// 获取导航链接
const getNavListFn = () =>{
    getNavList().then((res: any)=>{
        navList.value = res.data
        navList.value.forEach((item:any) =>{
            if(item.nav_url && item.nav_url.url){
                if(route.path == item.nav_url.url){
                    activeName.value = item.nav_url
                }
            }
        })
    })
}
getNavListFn()
const handleClick = (item: any) => {
    activeName.value = item.nav_url
    if (item.nav_url && item.nav_url.url) {
        if(isUrl(item.nav_url.url)){
            if (item.is_blank == 1) {
                window.open(item.nav_url.url)
            } else {
                window.open(item.nav_url.url, '_self')
            }
           
        }else{
            if (item.is_blank == 1) {
                const url = router.resolve({
                    path: item.nav_url.url
                })
                window.open(url.href)
            } else {
                router.push({ path: item.nav_url.url })
            }
        }
        
    }
}

</script>

<style lang="scss" scoped>
.bg-color{
    background-color: rgba(0,0,0,.4);
}
:deep(.popover-box){
    top:200px !important;
}

</style>