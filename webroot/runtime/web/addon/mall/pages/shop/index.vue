<template>
    <div class="bg-[#f5f5f5]">
        <div class="main-container">
            <div class="pt-[10px] flex justify-between mt-[25px]">
                <div class="w-[224px] flex flex-col">
                    <info />
                    <category @categoryClick="categoryClick"/>
                </div>
                <div class="flex-1 ml-[18px] pb-[30px]">
                    <el-carousel :interval="3000" height="330px" arrow="never">
                        <el-carousel-item v-for="(item,index) in advInfo.adv_list" :key="index">
                            <NuxtLink :to="item.adv_url && item.adv_url.url ? item.adv_url.url : '' ">
                                <div class="h-full index-carousel rounded-[var(--rounded-mid)]">
                                    <img :src="img(item.adv_image)" alt="" class="w-full h-full rounded-[var(--rounded-mid)]">
                                </div>
                            </NuxtLink>
                        </el-carousel-item>
                    </el-carousel>
                    <div class="pt-[40px] pb-[30px] text-center">
                        <h1 class="font-700 text-[24px]">{{ t('shopRecommend') }}</h1>
                        <div class="mt-[15px] text-[#999] text-[14px]">{{ t('shopTips') }}</div>
                    </div>
                    <div class="flex flex-wrap items-center">
                        <div class="bg-[#fff] w-[224px] cursor-pointer rounded-[var(--rounded-big)] overflow-hidden mb-[20px]" :class="{'mr-[20px]':(index + 1) % 4}" v-for="(item,index) in goodsTableData.data" :key="index"  @click="toDetail(item.goods_id)">
                            <div class="w-[224px] h-[224px]">
                                <el-image class="w-[224px] h-[224px]" :src="img(item.goods_cover_thumb_mid ? item.goods_cover_thumb_mid : '')" fit="cover">
                                    <template #error>
                                        <img src="@/assets/images/goods_default.png" class="w-[224px] h-[224px]">
                                    </template>
                                </el-image>
                            </div>
                            <div class="p-[15px]">
                                <div class="mb-[8px] text-[22px] text-[var(--el-color-primary)] price-font">¥{{ item.goodsSku.price }}</div>
                                <div class="mb-[12px] text-[#333] h-[40px] multi-hidden text-[14px] leading-[20px]">{{ item.goods_name }}</div>
                                <div class="text-[#999] text-[12px] leading-[16px]">
                                    <div>{{item.sale_num}}{{ t('saleNum') }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref,reactive } from 'vue'
import category from './components/category/index.vue'
import info from './components/info/index.vue'
import { getGoodsPages} from '@/addon/mall/api/goods'
import { useRouter, useRoute } from 'vue-router'
import { getAdvInfo } from '@/app/api/index'

const router = useRouter()
const route = useRoute()
const detail = ref([])

const goodsTableData = reactive({
	page: 1,
	limit: 15,
	total: 0,
	loading: true,
	data: [],
	searchParam: {
        site_id:'',
	}
})
goodsTableData.searchParam = Object.assign(goodsTableData.searchParam,route.query)


//获取广告位
// 广告位
const advInfo = ref({})
const getAdvInfoFn = () =>{
	getAdvInfo({ap_key:'ADV_INDEX'}).then(res =>{
		advInfo.value = res.data
	})
}
getAdvInfoFn()
//获取店铺商品列表
const loadGoodsList = (page = 1) => {
	goodsTableData.page = page
	goodsTableData.loading = true
	getGoodsPages({
		page: goodsTableData.page,
        limit: goodsTableData.limit,
        ...goodsTableData.searchParam
	}).then(res =>{
		goodsTableData.data = res.data.data,
		goodsTableData.total = res.data.total

	})
}
const toDetail = (goods_id) => {
	router.push(`/goods/detail?id=${goods_id}`)
}
const categoryClick = (id:any)=>{
  router.push({path:'/shop/detail',query:{site_id:route.query.site_id,goods_category:id}})
}

loadGoodsList()

</script>

<style lang="scss" scoped>

</style>