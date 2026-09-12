<template>
    <div class="main-container supply">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <div class="flex items-center">
                    <el-image class="max-w-[158px] max-h-[24px] cursor-pointer" :src="img(logoUrl ? logoUrl : '')" fit="contain" @click="router.push('/supply/select/list')">
                        <template #error>
                            <img class="max-w-[158px] max-h-[24px]" src="@/app/assets/images/site_login_logo.png" />
                        </template>
                    </el-image>
                    <div class="w-[1px] h-[20px] bg-[#D9D9D9] mx-[16px]"></div>
                    <img src="@/addon/mall/assets/brand_supply.png" alt="" class="mr-[50px] w-[90px] h-[22px]">
                    <div class="search w-[480px] h-[40px]  flex items-center border-box">
                        <div class="border-box border-[#e1e1e1]  border-solid border-[1px] rounded-l-[4px] flex-1">
                            <el-input v-model="supplyTable.searchParam.keyword" class="!h-[38px]" clearable placeholder="搜索商品或供货商店铺">
                                <template #prepend>
                                    <el-select v-model="supplyTable.searchParam.type" style="width:100px;height: 40px; font-size: 12px;margin: 0;">
                                        <el-option label="商品" value="goods" />
                                        <el-option label="供货商" value="shop" />
                                    </el-select>
                                </template>
                            </el-input>
                        </div>
                        <div class="w-[80px] h-full flex justify-center items-center leading-[38px] text-[#fff] bg-[var(--price-color)] cursor-pointer rounded-r-[4px]" @click="searchFn">
                            <span class="nc-iconfont nc-icon-sousuo-duanV6xx1 !text-[14px]"></span>
                            <span class="text-[#fff] text-[14px] ml-[6px]">搜索</span>
                        </div>
                    </div>
                </div>
                <div class="flex items-center">
                    <div class="w-[100px] h-[40px] box-border border-solid border-[1px] border-[#bbb] cursor-pointer flex items-center justify-center rounded-[4px] bg-[#fafafa]" @click="router.push('/supply/select/list')">
                        <span class="nc-iconfont nc-icon-gouwudaiV6xx1 !text-[14px]"></span>
                        <span class="text-[14px] ml-[6px]">选品平台</span>
                    </div>
                    <div class="w-[110px] h-[40px] box-border border-solid border-[1px] border-[#bbb] cursor-pointer flex items-center justify-center ml-[20px] rounded-[4px] bg-[#fafafa]" @click="router.push('/supply/select/my_supply')">
                        <span class="nc-iconfont nc-icon-a-wodeV6xx-36 !text-[14px]"></span>
                        <span class="text-[14px] ml-[6px]">合作供货商</span>
                    </div>
                </div>
            </div>
            <div class="border-[1px] border-solid border-[#ccc] p-[20px] mb-[20px] flex justify-between items-center">
                <div class="flex items-center text-[14px]">
                    <span class="mr-[14px] text-[#999]">排序:</span>
                    <div class="flex-1 flex items-center text-[#333] h-[34px]">
                        <span class="mr-[20px] cursor-pointer oppoSans-M" :class="{'text-[var(--price-color)]': supplyTable.searchParam.order == ''}" @click="orderClick('')">综合排序</span>
                        <div class="mr-[20px] cursor-pointer flex items-center"  @click="orderClick('supply_sale_num')">
							<span class="oppoSans-M" :class="{'text-[var(--price-color)]': supplyTable.searchParam.order == 'supply_sale_num'}">销量</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999] box-border">
								<div class="!text-[14px] nc-iconfont nc-icon-shangV6xx-1 text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': supplyTable.searchParam.order == 'supply_sale_num' && supplyTable.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] nc-iconfont nc-icon-xiaV6xx  mt-[-7px] text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': supplyTable.searchParam.order == 'supply_sale_num' && supplyTable.searchParam.sort == 'desc'}"></div>
							</div>
						</div>
                        <div class="mr-[20px] cursor-pointer flex items-center"  @click="orderClick('supply_follow_num')">
							<span class="oppoSans-M" :class="{'text-[var(--price-color)]': supplyTable.searchParam.order == 'supply_follow_num'}">热度</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999] box-border">
								<div class="!text-[14px] nc-iconfont nc-icon-shangV6xx-1 text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': supplyTable.searchParam.order == 'supply_follow_num' && supplyTable.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] nc-iconfont nc-icon-xiaV6xx  mt-[-7px] text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': supplyTable.searchParam.order == 'supply_follow_num' && supplyTable.searchParam.sort == 'desc'}"></div>
							</div>
						</div>
                    </div>
                </div>
            </div>
            <div class="min-h-[30vh]" v-loading="supplyTable.loading">
                <div class="flex h-[300px] border-solid border-[#f2f2f2] border-[1px] mb-[20px] overflow-hidden" v-for="(item,index) in supplyTable.data" :key="index">
                    <div class="w-[230px] h-[300px] left-style overflow-hidden flex-shrink-0" @click="router.push({ path: '/supply/select/supply', query: { site_id: item.site_id }} )">
                        <div class="absolute w-full h-full left-0 top-0 right-0 bottom-0 z-[1]">
                            <el-image class="w-full h-full overflow-hidden align-top left-bg" :src="img(item.logo)" fit="cover"> </el-image>
                        </div>
                        <div class="h-full relative z-[3] cursor-pointer flex flex-col justify-center items-center">
                            <el-image class="w-[80px] h-[80px] mb-[16px] rounded-full" :src="img(item.front_end_logo)" fit="cover">
                                <template #error>
                                    <img class="w-[80px] h-[80px] rounded-full" src="@/app/assets/images/supply_default.png" />
                                </template>
                            </el-image>
                            <div class="text-[16px] text-[#fff] mb-[9px] truncate max-w-[200px] oppoSans-M">{{ item.site_name }}</div>
                            <div class="text-[12px] text-[rgba(255,255,255,0.7)] mb-[50px]">{{item.shop && item.shop.supply_follow_num ? (item.shop.supply_follow_num+'家店铺合作') : '' }}</div>
                            <div class="w-[100px] h-[34px] border-solid border-[1px] border-[#fff] text-center leading-[32px] text-[14px] text-[#fff]">进店逛逛</div>
                        </div>
                    </div>
                    <div class="flex-1 h-[300px] box-border py-[18px] pl-[20px] flex flex-nowrap">
                        <div class="w-[155px] h-full flex flex-col cursor-pointer box-border" :class="{'mr-[18px]': (subIndex + 1) % 6 }" v-for="(subItem,subIndex) in item.goods_list" :key="subIndex" @click="todetail(subItem.goods_id)">
                            <el-image  class="w-[155px] h-[155px]" :src="img(subItem.goods_cover_thumb_mid)" fit="cover">
                                <template #error>
                                    <div class="image-slot">
                                        <img class="w-[155px] h-[155px]" src="@/addon/mall/assets/goods_default.png" />
                                    </div>
                                </template>
                            </el-image>
                            <div class="pt-[12px]">
                                <div class="text-[14px] truncate mb-[10px] leading-[20px]">{{ subItem.goods_name }}</div>
                                <div class="text-[#999] text-[12px] mb-[4px] leading-[18px]">
                                    <span class="mr-[10px]">建议零售价</span>
                                    <span class="price-font">￥{{ moneyFormat(subItem.goodsSku.price)  }}</span>
                                </div>
                                <div class="text-[#999] text-[12px] mb-[10px] leading-[18px]">
                                    <span>供货价</span>
                                    <span class="ml-[10px] text-[#333] price-font">￥{{ moneyFormat(subItem.goodsSku.cost_price) }}</span>
                                </div>
                                <div class="text-[16px] text-[var(--price-color)] flex items-center  leading-[18px]">
                                    <span class="mr-[4px] oppoSans-M text-[14px]">赚</span>
                                    <span class="price-font">￥{{ moneyFormat(subItem.goodsSku.earn_price) }}</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="supplyTable.page" v-model:page-size="supplyTable.limit" layout="total, sizes, prev, pager, next, jumper" :total="supplyTable.total"  @size-change="loadSupplyTableList()" @current-change="loadSupplyTableList" />
            </div>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { t } from '@/lang'
import { img, moneyFormat } from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'
import { cloneDeep } from 'lodash-es'
import { getSupplyList } from '@/app/lang/zh-cn/api/site/select_goods'
import { ElMessage } from 'element-plus'
import useSystemStore from '@/stores/modules/system'

const systemStore = useSystemStore()

const logoUrl = computed(() => {
    return systemStore.website.icon
})

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const supplyTable = reactive<any>({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: route.query.keyword || '',
        type: 'shop',
        order: '',
        sort: 'desc'
    }
})

/**
 * 获取商品列表
 */
const loadSupplyTableList = (page: number = 1) => {
    supplyTable.loading = true
    supplyTable.page = page

    const searchData = cloneDeep(supplyTable.searchParam)

    getSupplyList({
        page: supplyTable.page,
        limit: supplyTable.limit,
        ...searchData
    }).then((res: any) => {
        supplyTable.loading = false
        supplyTable.data = res.data.data
        supplyTable.total = res.data.total
    }).catch(() => {
        supplyTable.loading = false
    })
}

loadSupplyTableList()

// 点击排序
const orderClick = (val: string) => {
    if (val == '' && val == supplyTable.searchParam.order) {
        return false
    } else if (val == supplyTable.searchParam.order) {
        supplyTable.searchParam.sort = supplyTable.searchParam.sort == 'desc' ? 'asc' : 'desc'
    } else {
        supplyTable.searchParam.order = val
        supplyTable.searchParam.sort = 'desc'
    }
    loadSupplyTableList()
}
// 搜索
const searchFn = () => {
    if (supplyTable.searchParam.type == 'goods') {
        router.push({ path: '/supply/select/list', query: { keyword: supplyTable.searchParam.keyword } })
    }
    if (supplyTable.searchParam.type == 'shop') {
        loadSupplyTableList()
    }
}

const todetail = (id:any) => {
    router.push({
        path: '/supply/select/detail',
        query: {
            goods_id: id
        }
    })
}

</script>

<style lang="scss" scoped>
.left-style{
    position: relative;
    &::before{
        content: "";
        width: 100%;
        height: 100%;
        background: rgba(66, 66, 66, .7);
        position: absolute;
        left: 0;
        top: 0;
        z-index: 2;
    }
}
.left-bg{
    -webkit-filter: blur(10px);
    filter: blur(10px);
}
:deep(.search .el-select__wrapper){
    height: 40px;
}
:deep(.search .el-input-group__prepend){
    background-color: inherit;
    box-shadow: none;
    padding: 0;
}
:deep(.search .el-select  .el-select__wrapper){
    box-shadow: none !important;
    padding-left: 20px;
    box-sizing: border-box;
}
:deep(.search .el-select__selected-item){
    font-family:  'oppoSans-M';
}
:deep(.search .el-input__wrapper){
    box-shadow: none!important;
}

</style>