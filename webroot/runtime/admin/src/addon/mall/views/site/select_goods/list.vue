<template>
    <div class="main-container supply">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between  items-center mb-[30px]">
                <div class="flex items-center">
                    <el-image class="max-w-[158px] h-[24px]" :src="img(logoUrl ? logoUrl : img('/static/resource/images/web/logo.png'))" fit="contain">
                        <template #error>
                            <img class="max-w-[158px] h-[24px]" :src="img('/static/resource/images/web/logo.png')" />
                        </template>
                    </el-image>
                    <div class="w-[1px] h-[20px] bg-[#D9D9D9] mx-[16px]"></div>
                    <img src="@/addon/mall/assets/brand_supply.png" alt="" class="mr-[50px] w-[90px] h-[22px]">
                    <div class="search w-[480px] h-[40px]  flex items-center border-box">
                        <div class="border-box border-[#e1e1e1]  border-solid border-[1px] rounded-l-[4px] !flex-1">
                            <el-input v-model="goodsTable.searchParam.keyword" class="!h-[38px]"  clearable placeholder="搜索商品或供货商店铺">
                                <template #prepend>
                                    <el-select v-model="goodsTable.searchParam.type" style="width:100px; height: 40px;font-size: 14px;margin: 0;">
                                        <el-option label="商品" value="goods" />
                                        <el-option label="供货商" value="shop" />
                                    </el-select>
                                </template>
                            </el-input>
                        </div>
                        <div class="w-[80px] h-full flex justify-center items-center leading-[40px] text-[#fff]  bg-[var(--price-color)] flex-shrink-0 cursor-pointer rounded-r-[4px]" @click="searchFn()">
                            <span class="nc-iconfont nc-icon-sousuo-duanV6xx1 !text-[14px]"></span>
                            <span class="text-[#fff] text-[14px] ml-[6px]">搜索</span>
                        </div>
                    </div>
                </div>
                <div class="flex items-center">
                    <div class="w-[100px] h-[40px] box-border border-solid border-[1px] border-[#bbb] cursor-pointer flex items-center justify-center rounded-[4px] bg-[#fafafa]" @click="router.push('/supply/select/goods')">
                        <span class="nc-iconfont nc-icon-daishouhuoV6xx-3 !text-[14px]"></span>
                        <span class="text-[14px] ml-[6px]">供货平台</span>
                    </div>
                    <div class="w-[110px] h-[40px] box-border border-solid border-[1px] border-[#bbb] cursor-pointer flex items-center justify-center ml-[20px] rounded-[4px] bg-[#fafafa]" @click="mySupplyEvent">
                        <span class="nc-iconfont nc-icon-a-wodeV6xx-36 !text-[14px]"></span>
                        <span class="text-[14px] ml-[6px]">合作供货商</span>
                    </div>
                </div>
            </div>
            <div class="mb-[20px] min-w-[1600px]">
                <div class="flex  text-[14px] p-[20px] pb-[5px] border-[1px] border-solid border-[#ccc] border-b-0">
                    <span class="mr-[14px] text-[#999]">类目:</span>
                    <div class="flex-1 flex items-center flex-wrap text-[#333]">
                        <span class="mr-[20px] mb-[15px] cursor-pointer oppoSans-M" :class="{'text-[var(--price-color)]': goodsTable.searchParam.goods_mall_category == ''}" @click="handleCategory('')">全部</span>
                        <span class="mr-[20px] mb-[15px] cursor-pointer oppoSans-M" :class="{'text-[var(--price-color)]' : goodsTable.searchParam.goods_mall_category ==item.category_id }" v-for="(item,index) in categoryList" :key="index" @click="handleCategory(item.category_id)">{{ item.category_name }}</span>
                    </div>
                </div>
                <div class="flex items-center text-[14px] p-[20px] border-[1px] border-solid border-[#ccc] filter">
                    <span class="mr-[14px] text-[#999]">排序:</span>
                    <div class="flex-1 flex items-center text-[#333] h-[34px]">
                        <span class="mr-[20px] cursor-pointer oppoSans-M" :class="{'text-[var(--price-color)]': goodsTable.searchParam.order == ''}" @click="orderClick('')">综合排序</span>
                        <div class="mr-[20px] cursor-pointer flex items-center"  @click="orderClick('new')">
							<span class="oppoSans-M"  :class="{'text-[var(--price-color)]': goodsTable.searchParam.order == 'new'}">上新</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999] box-border">
								<div class="!text-[14px] nc-iconfont nc-icon-shangV6xx-1 text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': goodsTable.searchParam.order == 'new' && goodsTable.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] nc-iconfont nc-icon-xiaV6xx  mt-[-7px] text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': goodsTable.searchParam.order == 'new' && goodsTable.searchParam.sort == 'desc'}"></div>
							</div>
						</div>
                        <div class="mr-[20px] cursor-pointer flex items-center"  @click="orderClick('sel')">
							<span class="oppoSans-M" :class="{'text-[var(--price-color)]': goodsTable.searchParam.order == 'sel'}">精选</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999] box-border">
								<div class="!text-[14px] nc-iconfont nc-icon-shangV6xx-1 text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': goodsTable.searchParam.order == 'sel' && goodsTable.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] nc-iconfont nc-icon-xiaV6xx  mt-[-7px] text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': goodsTable.searchParam.order == 'sel' && goodsTable.searchParam.sort == 'desc'}"></div>
							</div>
						</div>
                        <div class="mr-[20px] cursor-pointer flex items-center"  @click="orderClick('sale')">
							<span class="oppoSans-M" :class="{'text-[var(--price-color)]': goodsTable.searchParam.order == 'sale'}">销量</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999] box-border">
								<div class="!text-[14px] nc-iconfont nc-icon-shangV6xx-1 text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': goodsTable.searchParam.order == 'sale' && goodsTable.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] nc-iconfont nc-icon-xiaV6xx  mt-[-7px] text-[#999] leading-[1]" :class="{'!text-[var(--price-color)]': goodsTable.searchParam.order == 'sale' && goodsTable.searchParam.sort == 'desc'}"></div>
							</div>
						</div>
                        <div class="mr-[20px] flex items-center">
                            <span class="text-[14px] oppoSans-M">零售价：</span>
							<el-input class="!w-[140px] !text-[14px] !rounded-[4px]" size="mini" v-model="goodsTable.searchParam.start_price" onkeyup="this.value = this.value.replace(/[^\d.]/g,'');" clearable  :placeholder="t('最低价格')" />
							<span class="mx-[4px]"> ~ </span>
							<el-input class="!w-[140px] !text-[14px] !rounded-[4px]" size="mini" v-model="goodsTable.searchParam.end_price" onkeyup="this.value = this.value.replace(/[^\d.]/g,'');" clearable :placeholder="t('最高价格')" />
						</div>
                        <div class="mr-[20px] flex items-center">
                            <span class="text-[14px] oppoSans-M">供货价：</span>
							<el-input class="!w-[140px] !text-[14px]" size="mini" v-model="goodsTable.searchParam.start_cost_price" onkeyup="this.value = this.value.replace(/[^\d.]/g,'');"  clearable :placeholder="t('最低价格')" />
							<span class="mx-[4px]"> ~ </span>
							<el-input class="!w-[140px] !text-[14px]" size="mini" v-model="goodsTable.searchParam.end_cost_price" onkeyup="this.value = this.value.replace(/[^\d.]/g,'');" clearable :placeholder="t('最高价格')" />
						</div>
                        <div class="flex items-center mr-[20px]">
                            <span class="text-[14px] oppoSans-M">挑货状态：</span>
                            <el-select v-model="goodsTable.searchParam.select_status" class="!w-[160px] !rounded-[4px]" :placeholder="t('请选择挑货状态')" clearable>
                                <el-option  label="已挑货" :value="1" />
                                <el-option  label="未挑货" :value="2" />
                            </el-select>
                        </div>
                        <el-button type="primary" size="mini" class="!bg-[var(--price-color)] !border-[var(--price-color)] text-[14px] mr-[10px] !rounded-[4px]" @click="inputUp">确定</el-button>
                        <el-button  size="mini" class="!m-0 text-[14px] !rounded-[4px] hover:bg-[#fff]" @click="clear">清空</el-button>
                    </div>
                </div>
            </div>
            <div class="min-h-[30vh]" v-loading="goodsTable.loading">
                <div class="flex flex-wrap " v-if="goodsTable.data.length">
                    <div class="w-[222px] h-[382px]  border-solid border-[#f2f2f2] border-[1px] box-border cursor-pointer overflow-hidden relative" :class="{'mr-[16px] mb-[30px]': (index + 1) % 7 }" v-for="(item,index) in goodsTable.data" :key="index" @click="todetail(item.goods_id)">
                        <el-image class="w-[222px] h-[222px] align-middle" :src="img(item.goods_cover_thumb_mid)" fit="cover">
                            <template #error>
                                <img src="@/addon/mall/assets/goods_default.png" class="w-[222px] h-[222px] align-middle">
                            </template>
                        </el-image>
                        <div class="p-[14px] relative">
                            <div class="text-[14px] mb-[10px]  flex items-center justify-between">
                                <div class="truncate leading-[20px]">{{ item.goods_name }}</div>
                                <el-checkbox v-model="item.checked" class="!h-[20px]" size="large" @click.stop="() =>{}" v-if="!item.mall_goods_id" />
                            </div>
                            <div class="flex items-center leading-[18px] mb-[8px] cursor-pointer" @click.stop="router.push({path: '/supply/select/supply', query: { site_id: item.site_id }})">
                                <el-image class="w-[16px] h-[16px] rounded-full align-middle" :src="img(item.site.front_end_logo)" fit="cover">
                                    <template #error>
                                        <img src="@/addon/mall/assets/supply_default.png" class="w-[16px] h-[16px] rounded-full">
                                    </template>
                                </el-image>
                                <div class="text-[12px] ml-[4px] title">{{ item.site.site_name }}</div>
                                <span class="nc-iconfont nc-icon-youV6xx"></span>
                            </div>
                            <div class="text-[#999] text-[12px] mb-[4px] leading-[18px]">
                                <span class="mr-[10px]">建议零售价</span>
                                <span class="price-font">￥{{ moneyFormat(item.goodsSku.price) }}</span>
                            </div>
                            <div class="text-[#999] text-[12px] mb-[10px] leading-[18px]">
                                <span>供货价</span>
                                <span class="ml-[10px] text-[#333] price-font">￥{{ moneyFormat(item.goodsSku.cost_price) }}</span>
                            </div>
                            <div class="bg-[#FFF5F5] enrn-active rounded-r-[14px] rounded-bl-[14px] flex items-center justify-between h-[28px] pl-[10px]">
                                <div class="text-[var(--price-color)] flex items-center  leading-[18px]">
                                    <span class="mr-[4px] oppoSans-M text-[12px]">赚</span>
                                    <span class="price-font font-[600] text-[14px]">￥{{ moneyFormat(item.goodsSku.earn_price) }}</span>
                                </div>
                                <div class="w-[66px] h-[28px] bg-[var(--price-color)] rounded-full text-center leading-[28px] text-[#fff] text-[14px]"  @click.stop="handlePickGood(item.goods_id)" v-if="!item.mall_goods_id">去挑货</div>
                                <div class="w-[66px] h-[28px] bg-[var(--price-color)] rounded-full text-center leading-[28px] text-[#fff] text-[14px]"  @click.stop="toDoods(item)" v-else>查看</div>
                            </div>
                        </div>
                        <div class="absolute bottom-[45px] right-[6px] leading-[1]" v-if="item.mall_goods_id">
                            <span class="nc-iconfont nc-icon-yitiaohuo !text-[66px] text-[var(--price-color)]"></span>
                        </div>
                    </div>
                </div>
                <el-empty description="暂无数据" v-else />
            </div>
            <div class="mt-[16px] flex justify-between items-center">
                <div class="flex items-center">
                    <span class="text-[14px]">已选{{ goodsIds.length }}项，批量操作：</span>
                    <el-button @click="handleBatch" size="small">批量挑货</el-button>
                </div>
                <el-pagination v-model:current-page="goodsTable.page" v-model:page-size="goodsTable.limit" layout="total, sizes, prev, pager, next, jumper" :total="goodsTable.total" :page-sizes="[14, 28, 140, 280]"  @size-change="loadGoodsList()" @current-change="loadGoodsList" />
            </div>
        </el-card>
        <goods-add-popup ref="goodsAddRef" @confirm="loadGoodsList()" />
        <supply-batch-popup ref="supplyBatchRef" @confirm="loadGoodsList()" />
    </div>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { t } from '@/lang'
import { img, moneyFormat } from '@/utils/common'
import { cloneDeep } from 'lodash-es'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import goodsAddPopup from './components/goods-add-popup.vue'
import { getCategoryList } from '@/app/api/basic_admin'
import { getGoodsList } from '@/addon/mall/api/site/select_goods'
import supplyBatchPopup from './components/supply-batch-popup.vue'
import useSystemStore from '@/stores/modules/system'
const systemStore = useSystemStore()

const logoUrl = computed(() => {
    return systemStore.website.front_end_logo
})

const route = useRoute()
const router = useRouter()

const goodsTable = reactive<any>({
    page: 1,
    limit: 14,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: route.query.keyword || '',
        goods_mall_category: '',
        type: 'goods',
        start_price: '',
        end_price: '',
        select_status: '',
        start_cost_price: '',
        end_cost_price: '',
        order: '',
        sort: ''
    }
})

/**
 * 获取商品列表
 */
const loadGoodsList = (page: number = 1) => {
    goodsTable.loading = true
    goodsTable.page = page

    const searchData = cloneDeep(goodsTable.searchParam)

    getGoodsList({
        page: goodsTable.page,
        limit: goodsTable.limit,
        ...searchData
    }).then((res: any) => {
        goodsTable.loading = false
        goodsTable.data = res.data.data.map((item: any) => {
            item.checked = false
            return item
        })
        goodsTable.total = res.data.total
    }).catch(() => {
        goodsTable.loading = false
    })
}

loadGoodsList()

const categoryList = ref<any>([])
const getCategoryListFn = () => {
    getCategoryList({ level: 1 }).then((res: any) => {
        categoryList.value = res.data
    })
}
getCategoryListFn()

const handleCategory = (data: any) => {
    goodsTable.searchParam.goods_mall_category = data
    loadGoodsList()
}
// 排序点击
const orderClick = (item:string) => {
    if (item == '' && item == goodsTable.searchParam.order) {
        return false
    } else if (item == goodsTable.searchParam.order) {
        goodsTable.searchParam.sort = goodsTable.searchParam.sort == 'desc' ? 'asc' : 'desc'
    } else {
        goodsTable.searchParam.order = item
        goodsTable.searchParam.sort = 'desc'
    }
    loadGoodsList()
}

const inputUp = () => {
    const startPrice = goodsTable.searchParam.start_price == '' ? '0' : goodsTable.searchParam.start_price
    const endprice = goodsTable.searchParam.end_price == '' ? '0' : goodsTable.searchParam.end_price
    const startCostPrice = goodsTable.searchParam.start_cost_price == '' ? '0' : goodsTable.searchParam.start_cost_price
    const endCostPrice = goodsTable.searchParam.end_cost_price == '' ? '0' : goodsTable.searchParam.end_cost_price
    if (!isNaN(parseInt(startPrice)) && !isNaN(parseInt(endprice)) && !isNaN(parseInt(startCostPrice)) && !isNaN(parseInt(endCostPrice))) {
        if (parseInt(startPrice) > parseInt(endprice) && goodsTable.searchParam.end_price != '') {
            ElMessage({
                message: '你所选择的零售价区间不对哦！',
                type: 'warning'
            })
            goodsTable.searchParam.start_price = ''
            goodsTable.searchParam.end_price = ''
            return false
        } else if (parseInt(startCostPrice) > parseInt(endCostPrice) && goodsTable.searchParam.end_cost_price != '') {
            ElMessage({
                message: '你所选择的零售价区间不对哦！',
                type: 'warning'
            })
        }
        loadGoodsList()
    } else {
        ElMessage({
            message: '请输入正确的内容',
            type: 'warning'
        })
    }
}

// 搜索
const searchFn = () => {
    if (goodsTable.searchParam.type == 'goods') {
        loadGoodsList()
    }
    if (goodsTable.searchParam.type == 'shop') {
        router.push({ path: '/supply/select/goods', query: { keyword: goodsTable.searchParam.keyword } })
    }
}

const todetail = (id: any) => {
    const url = router.resolve({
        path: '/supply/select/detail',
        query: {
            goods_id: id
        }
    })
    window.open(url.href)
}
const clear = () => {
    goodsTable.searchParam.order = ''
    goodsTable.searchParam.sort = ''
    goodsTable.searchParam.keyword = ''
    goodsTable.searchParam.goods_mall_category = ''
    goodsTable.searchParam.start_cost_price = ''
    goodsTable.searchParam.end_cost_price = ''
    goodsTable.searchParam.start_price = ''
    goodsTable.searchParam.end_price = ''
    goodsTable.searchParam.select_status = ''
    loadGoodsList()
}

const goodsAddRef = ref()
const handlePickGood = (id: any) => {
    goodsAddRef.value.open(id)
}

const goodsIds = computed(() => {
    return goodsTable.data.filter((item: any) => item.checked).map((item: any) => item.goods_id)
})

const supplyBatchRef = ref()
const handleBatch = () => {
    if (goodsIds.value.length === 0) {
        ElMessage.error('请选择商品')
        return false
    }
    supplyBatchRef.value.open(goodsIds.value)
}

// 去查看
const toDoods = (data: any) => {
    const query: any = {
        goods_id: data.mall_goods_id
    }
    if (data.goods_type == 'real') {
        const url = router.resolve({
            path: '/mall/goods/real_edit',
            query
        })
        window.open(url.href)
    } else {
        const url = router.resolve({
            path: '/mall/goods/virtual_edit',
            query
        })
        window.open(url.href)
    }
}

// 跳转到商品分类，添加分类
const mySupplyEvent = () => {
    const url = router.resolve({
        path: "/supply/select/my_supply"
    })
    window.open(url.href)
}
</script>

<style lang="scss" scoped>
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

:deep(.filter .el-input__wrapper){
    border-radius: 4px !important;
}
:deep(.filter .el-select__wrapper){
    border-radius: 4px !important;
}
:deep(.el-button:not(.is-round)){
    border-radius: 4px!important;
}
.enrn-active{
    position: relative;
    &::after{
        content: '';
        position: absolute;
        top: -7px;
        left: 54px;
        width: 14px;
        height: 14px;
        background-color: #FFF5F5;
        z-index: 1;
        transform: rotate(45deg);
    }
}
.title{
    font-family:  'oppoSans-M';
    font-weight:  500;
}
.supply{
    --el-color-primary: #e53a40 !important;
    --el-color-primary-light-9: #fff3f4 !important;
    --el-color-primary-light-8: #ffe7e9 !important;
    --el-color-primary-light-7: #ffdadd !important;
    --el-color-primary-light-5:  #ffbcc1 !important;
    --el-color-primary-light-3: #ff9e9a !important;
    --el-color-primary-dark-2: #a42a2e !important;
}
.el-select-dropdown .el-select-dropdown__item.is-selected{
    color: #e53a40 !important;
}
</style>