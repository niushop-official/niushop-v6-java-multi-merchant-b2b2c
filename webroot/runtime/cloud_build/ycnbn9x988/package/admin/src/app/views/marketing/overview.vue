<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[10px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <div class="text-[14px] text-[#999]">仅统计优惠券、满减/送、限时折扣、秒杀、多人拼团、赠品 活动</div>
        </el-card>
        <template v-for="(item, key) in promotionList" :key="key">
            <el-card class="box-card !border-none my-[10px]" shadow="never" v-if="promotionList[key].list.length">
                <template #header>
                    <div>{{ item.title }}</div>
                </template>
                <el-row :gutter="20" class="py-[10px] flex flex-wrap gap-y-[16px] box-border">
                    <el-col :span="6" v-for="(subItem,subIndex) in item.list" :key="subIndex">
                        <div class="bg-[#f7f8fa] flex items-center px-[20px] py-[16px] rounded-[2px]">
                            <el-image class="w-[40px] h-[40px] mr-[10px] rounded-[5px]" :src="img(subItem.icon)" fit="contain">
                                <template #error>
                                    <div class="image-slot">
                                        <img class="w-[40px] h-[40px]" src="@/app/assets/images/index/app_default.png" />
                                    </div>
                                </template>
                            </el-image>
                            <div class="flex-1">
                                <div class="text-[14px] mb-[4px]">{{ subItem.title }}</div>
                                <div class="text-[12px] text-[#999] leading-[16px]">{{ subItem.desc }}</div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </el-card>
        </template>
        <el-card class="box-card !border-none my-[10px]" shadow="never">
            <template #header>
                <div>活动记录</div>
            </template>
            <el-tabs v-model="activeTable.searchParam.activeName" class="demo-tabs" @tab-change="handleClick">
				<el-tab-pane :label="t('all')" name=""></el-tab-pane>
				<el-tab-pane :label="item.name" :name="item.status" v-for="(item,index) in activeList" :key="index"></el-tab-pane>
			</el-tabs>
            <div>
                <el-table :data="activeTable.data" size="large" v-loading="activeTable.loading" ref="activeTableRef">
                    <template #empty>
                        <span>{{ !activeTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                </el-table>
                <el-pagination v-model:current-page="activeTable.page" v-model:page-size="activeTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="activeTable.total"
                        @size-change="loadActiveList()" @current-change="loadActiveList" />
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getAdminpromotion } from '@/app/api/marketing'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const activeTable = reactive<any>({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        activeName: ''
    }
})
const promotionList = ref<any>([])
const activeList = ref<any>([])

const getAdminpromotionFn = () => {
    getAdminpromotion().then((res: any) => {
        promotionList.value = res.data
    })
}

getAdminpromotionFn()

const loadActiveList = (page: number = 1) => {
    activeTable.loading = true
    activeTable.page = page
    loadActive().then((res: any) => {
        activeTable.loading = false
        activeTable.data = res.data.data
        activeTable.total = res.data.total
    })
}

const handleClick = (event: any) => {
    activeTable.searchParam.activeName = event
    loadActiveList()
}
</script>

<style scoped>

</style>