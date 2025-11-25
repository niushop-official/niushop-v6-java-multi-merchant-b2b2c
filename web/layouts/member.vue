<template>
    <el-container class="w-screen h-screen">
        <el-main class="p-0 min-w-[1200px]" id="main">
            <el-header class="z-10 sticky top-0">
                <layout-header />
                <layout-nav />
            </el-header>
            <div class="bg-page pt-6">
                <div class="main-container flex justify-between">
                    <sidebar></sidebar>
                    <div v-if="agreeShow"><slot></slot></div>
                    <div v-else><layout-error ></layout-error></div>
                </div>
            </div>
            <layout-footer />
            <layout-fixed />
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import layoutHeader from './default/components/header/index.vue'
import layoutFooter from './default/components/footer/index.vue'
import layoutNav from './default/components/nav/index.vue'
import layoutFixed from './default/components/fiexd/index.vue'
import layoutError from './default/components/error/index.vue'
import sidebar from '@/components/sidebar/index.vue'
import { getToken } from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
let agreeShow = ref(false)
watch(()=>({ path: router.currentRoute.value.path, token: getToken() }) ,(newValue)=>{
    if(newValue.path  === '/auth/agreement' || newValue.token){
        agreeShow.value = true
    }else{
        agreeShow.value = false
    } 
},{immediate:true,deep: true})
</script>

<style lang="scss" scoped>
.el-header {
    --el-header-padding: 0;
    height: 188px;
}

.el-main {
    --el-main-padding: 0;
}
</style>
