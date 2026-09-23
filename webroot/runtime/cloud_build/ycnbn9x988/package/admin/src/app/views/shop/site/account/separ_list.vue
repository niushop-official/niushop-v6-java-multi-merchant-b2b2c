<template>
    <div class="main-container">
        <div class="p-[20px]" :class="{'bg-[#fff] min-h-[50vh]': !separList.length && !loading }">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-row :gutter="20" v-if="Object.keys(separList).length && !loading">
                <el-col :span="8" v-for="(item,index) in separList" :key="index">
                    <div class="p-[20px] bg-[#fff] rounded-[4px]">
                        <div class="flex justify-between items-center">
                            <div>
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
                        <div class="flex justify-between items-center mt-[30px]">
                            <div>
                                <el-switch v-model="item.site_payment.status" active-value="on" inactive-value="off" active-text="开" inactive-text="关" inline-prompt  @change="handleChange(item)" v-if="item.site_payment && Object.keys(item.site_payment).length > 0 && item.site_payment.is_settle == 3"/>
                            </div>
                            <div class="flex items-center">
                                <div class="text-[14px] text-primary border-[1px] border-solid border-primary w-[80px] h-[30px] text-center leading-[28px] box-border rounded-[4px] cursor-pointer tracking-[2px]" @click="toLink(item)" v-if="item.site_payment && JSON.stringify(item.site_payment) === '[]'">配置</div>
                                <div class="text-[14px] text-primary border-[1px] border-solid border-primary w-[80px] h-[30px] text-center leading-[28px] box-border rounded-[4px] cursor-pointer tracking-[2px]" @click="toInfo(item)" v-if="item.site_payment && item.site_payment.infos && Object.keys(item.site_payment.infos).length > 0">查看</div>
                                <div class="text-[14px] text-primary border-[1px] border-solid border-primary w-[80px] h-[30px] text-center leading-[28px] box-border rounded-[4px] cursor-pointer tracking-[2px] ml-[10px]" @click="toLink(item)" v-if="item.site_payment && Object.keys(item.site_payment).length > 0 && item.site_payment.is_settle != 2">修改</div>
                            </div>
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
import { getSitePayment, setSitePaymentStatus } from '@/app/api/shop_site'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const separList = ref<any>({})
const loading = ref(false)
const getSitePaymentFn = () => {
    loading.value = true
    getSitePayment().then((res:any) => {
        separList.value = res.data
        loading.value = false
    })
}
getSitePaymentFn()

const handleChange = (data:any) => {
    setSitePaymentStatus({id: data.site_payment.id, status: data.site_payment.status}).then((res:any) => {
        getSitePaymentFn()
    })
}

const toLink = (item:any) => {
    router.push('/' + item.site_url)
}
const toInfo = (item:any) => {
    router.push('/' + item.site_info_url)
}
</script>
<style lang="scss" scoped>
.app-text {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    white-space: break-spaces;
}
</style>