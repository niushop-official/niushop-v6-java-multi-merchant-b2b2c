<template>
    <div class="main-container supply">
        <el-card class="box-card !border-none mb-[16px]" shadow="never" v-if="Object.keys(shopInfo).length">
            <div class="flex">
                <el-image class="w-[150px] h-[150px] mr-[20px] rounded-[6px]" :src="img(shopInfo.front_end_logo)" fit="cover">
                    <template #error>
                        <img class="w-[150px] h-[150px] rounded-[6px]" src="@/app/assets/images/supply_default.png" />
                    </template>
                </el-image>
                <div class="flex flex-col justify-between">
                   <div class="flex items-center leading-[22px] mb-[16px]">
                        <span class="text-[18px] max-w-[300px] truncate oppoSans-M">{{ shopInfo.site_name }}</span>
                        <span class="text-[12px] text-[var(--price-color)] ml-[6px]" v-if="shopInfo.supply_follow_num">{{ shopInfo.supply_follow_num }}家店铺合作</span>
                   </div>
                    <div class="text-[14px] text-[#333] mb-[30px] multi-hidden max-w-[900px] leading-[16px]" v-if="shopInfo.desc">{{ shopInfo.desc }}</div>
                    <div class="flex items-center" v-if="shopInfo.service_list && shopInfo.service_list.length">
                        <div class="flex flex-col items-center mr-[40px]" v-for="(item,index) in shopInfo.service_list" :key="index">
                            <img :src="img(item.image)"  class="w-[30px] h-[30px]">
                            <div class="text-[14px] text-[#333] mt-[7px]">{{ item.service_name }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </el-card>
        <div class="bg-[#fff]">
            <div class="flex justify-between items-center border-b-[1px] border-[#ebeef5] border-solid p-[20px] pb-[8px]">
                <div class="flex items-center">
                    <span class="text-[16px] mr-[30px] cursor-pointer" :class="{'tab-item': goodsTable.searchParam.order == 'sift'}" @click="handleClick('sift')">精选</span>
                    <span class="text-[16px] mr-[30px] cursor-pointer" :class="{'tab-item': goodsTable.searchParam.order == 'new'}"  @click="handleClick('new')">上新</span>
                    <span class="text-[16px] mr-[30px] cursor-pointer" :class="{'tab-item': goodsTable.searchParam.order == 'sale'}" @click="handleClick('sale')">爆款</span>
                </div>
                <div class="flex items-center">
                    <el-input class="w-[200px]" v-model.trim="goodsTable.searchParam.keyword" clearable :placeholder="t('请输入商品关键字')" />
                    <el-button  class="!bg-[var(--price-color)] !text-[#fff] !border-[var(--price-color)]" @click="loadGoodsList()">{{ t('搜索') }}</el-button>
                </div>
            </div>
            <div>
            <div class=" min-h-[30vh]" v-loading="goodsTable.loading">
                <div class="p-[20px] flex flex-wrap" v-if="goodsTable.data.length">
                    <div class="w-[220px] h-[356px]  border-solid border-[#f2f2f2] border-[1px] box-border cursor-pointer overflow-hidden relative" :class="{'mr-[19px] mb-[30px]': (index + 1) % 7 }" v-for="(item,index) in goodsTable.data" :key="index" @click="todetail(item.goods_id)">
                        <el-image class="w-[220px] h-[220px] align-middle" :src="img(item.goods_cover_thumb_mid)" fit="cover">
                            <template #error>
                                <img src="@/addon/mall/assets/goods_default.png" class="w-[220px] h-[220px] align-middle">
                            </template>
                        </el-image>
                        <div class="p-[14px] relative z-[10]">
                            <div class="text-[14px] mb-[10px]  flex items-center justify-between">
                                <div class="truncate leading-[20px]">{{ item.goods_name }}</div>
                                <el-checkbox v-model="item.checked" class="!h-[20px]" size="large" @click.stop="() =>{}" v-if="!item.mall_goods_id" />
                            </div>
                            <div class="text-[#999] text-[12px] mb-[4px] leading-[18px]">
                                <span class="mr-[10px]">建议零售价</span>
                                <span class="price-font">￥{{ moneyFormat(item.goodsSku.price) }}</span>
                            </div>
                            <div class="text-[#999] text-[12px] mb-[10px] leading-[18px]">
                                <span>供货价</span>
                                <span class="ml-[10px] text-[#333] price-font">￥{{ moneyFormat(item.goodsSku.cost_price) }}</span>
                            </div>
                            <div class="bg-[#FFF5E8] enrn-active rounded-r-[14px] rounded-bl-[14px] flex items-center justify-between h-[28px] pl-[10px]">
                                <div class="text-[16px] text-[var(--price-color)] flex items-center  leading-[18px]">
                                    <span class="mr-[4px] oppoSans-M text-[12px]">赚</span>
                                    <span  class="price-font font-[600] text-[14px]">￥{{ moneyFormat(item.goodsSku.earn_price) }}</span>
                                </div>
                                <div class="w-[66px] h-[28px] bg-[var(--price-color)] rounded-full text-center leading-[28px] text-[#fff] text-[14px]" @click.stop="handlePickGood(item.goods_id)" v-if="!item.mall_goods_id">去挑货</div>
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
            <div class="px-[20px] pb-[20px] flex justify-between items-center">
                <div class="flex items-center">
                    <span class="text-[14px]">已选{{ goodsIds.length }}项，批量操作：</span>
                    <el-button @click="handleBatch" size="small">批量挑货</el-button>
                </div>
                <el-pagination v-model:current-page="goodsTable.page" v-model:page-size="goodsTable.limit" layout="total, sizes, prev, pager, next, jumper" :total="goodsTable.total" :page-sizes="[14, 28, 140, 280]"  @size-change="loadGoodsList()" @current-change="loadGoodsList" />
            </div>
        </div>
        </div>
        <goods-add-popup ref="goodsAddRef" @confirm="loadGoodsList()" />
        <supply-batch-popup ref="supplyBatchRef" @confirm="loadGoodsList()" />
    </div>
</template>
<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { t } from '@/lang'
import { img, moneyFormat } from '@/utils/common'
import { cloneDeep } from 'lodash-es'
import { useRouter, useRoute } from 'vue-router'
import { getSupplyDetail, getGoodsList } from '@/app/lang/zh-cn/api/site/select_goods.js'
import goodsAddPopup from './components/goods-add-popup.vue'
import supplyBatchPopup from './components/supply-batch-popup.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const siteId: any = route.query.site_id
const shopInfo = ref<any>({})

const getShopInfoFn = () => {
    getSupplyDetail(siteId).then((res: any) => {
        shopInfo.value = res.data
    })
}
getShopInfoFn()

const goodsTable: any = reactive({
    page: 1,
    limit: 14,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        site_id: siteId,
        keyword: '',
        order: 'sift'

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

// tab
const handleClick = (tab: any) => {
    goodsTable.searchParam.order = tab
    loadGoodsList()
}

const goodsIds = computed(() => {
    return goodsTable.data.filter((item: any) => item.checked).map((item: any) => item.goods_id)
})
const goodsAddRef = ref()
const handlePickGood = (id: any) => {
    goodsAddRef.value.open(id)
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
</script>
<style lang="scss" scoped>
.tab-item{
    position: relative;
    color: var(--price-color);
    font-weight: 600;
    &::after{
        content: '';
        position: absolute;
        bottom: -13px;
        left: 50%;
        width: 24px;
        height: 2px;
        background-color: var(--price-color);
        transform: translateX(-50%);
    }
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
        background-color: #FFF5E8;
        z-index: 1;
        transform: rotate(45deg);
    }
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
</style>