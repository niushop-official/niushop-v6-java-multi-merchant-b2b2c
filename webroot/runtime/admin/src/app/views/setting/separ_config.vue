<template>
    <div class="main-container">
        <div cass="p-[20px]" :class="{'bg-[#fff] min-h-[50vh]': !separList.length && !loading }">
            <div class="flex justify-between items-center mb-[10px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-row :gutter="20"  v-if="Object.keys(separList).length && !loading">
                <el-col :span="8" v-for="(item,index) in separList" :key="index">
                    <div class="p-[20px] bg-[#fff] rounded-[4px] box-border">
                        <div class="flex justify-between items-center">
                            <div class="flex-1">
                                <div class="app-text text-[16px] font-[500] max-w-[170px] mb-[10px]">{{ item.name }}</div>
                                <el-tooltip class="box-item"  effect="light"  placement="bottom">
                                    <template #content>
                                        <div class="text-[12px] w-[400px]">{{ item.desc }}</div>
                                    </template>
                                    <div class="app-text text-[12px] text-[#999]  cursor-pointer">{{ item.desc }}</div>
                                </el-tooltip>
                            </div>
                            <el-image class="w-[80px] h-[80px] flex-shrink-0" :src="img(item.icon)" fit="contain">
                                <template #error>
                                    <img class="w-[80px] h-[80px]" src="@/app/assets/images/index/app_default.png" />
                                </template>
                            </el-image>
                        </div>
                        <div class="flex justify-end items-center mt-[30px]">
                            <div class="text-[14px] text-primary border-[1px] border-solid border-primary w-[80px] h-[30px] text-center leading-[28px] box-border rounded-[4px] cursor-pointer tracking-[2px]" @click="toLink(item)">配置</div>
                        </div>
                    </div>
                </el-col>
            </el-row>
            <div class="flex justify-center items-center mt-[50px]" v-if="!separList.length && !loading">
                <el-empty  :image="img('static/resource/images/empty.png')" />
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getAdminPayment } from '@/app/api/shop_admin'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const separList = ref<any>({})
const loading = ref(false)
const getAdminPaymentFn = () => {
    loading.value = true
    getAdminPayment().then((res:any) => {
        separList.value = res.data
        loading.value = false
    })
}
getAdminPaymentFn()

const toLink = (item:any) => {
    router.push('/' + item.admin_url)
}
</script>
<style lang="scss" scoped>
.app-text {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 1;
    -webkit-box-orient: vertical;
    white-space: break-spaces;
}
</style>