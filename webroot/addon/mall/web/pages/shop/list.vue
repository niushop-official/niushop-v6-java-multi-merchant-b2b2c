<template>
    <div class="w-full bg-[#fff]">
        <div class="bg-[#fff] ">
            <div class="main-container pt-[10px] pb-[15px]">
                <div class="flex flex-wrap">
                    <template v-for="(item,index) in shopCategory" :key="index">
                        <div class="text-[14px] h-[26px] leading-[24px] mr-[15px] mt-[15px] px-[12px] rounded-[13px] text-[#666] border-[1px] border-solid border-[#e4e4e4] cursor-pointer" :class="{'!text-[#fff] bg-[var(--el-color-primary)] !border-[var(--el-color-primary)] ':item.category_id== shopTableData.searchParam.category_id}" @click="handleType(item.category_id)">{{ item.category_name }}</div>
                    </template>
                </div>
            </div>
        </div>
        <div class="main-container pb-[20px]">
            <div class="min-h-[30vh]" v-loading="shopTableData.loading">
                <div v-if="shopTableData.data.length">
                    <div class="street-list mt-[40px] min-h-[300px]" >
                        <template v-for="(item, index) in shopTableData.data" :key="item.site_id">
                            <div class="mb-[15px] cursor-pointer">
                                <div  class="w-[228px] border-solid border-[#eee] border-[1px] pb-[20px] rounded-[16px] bg-[#fff] flex items-center flex-col overflow-hidden box-border"  @click="toDetail(item.site_id)" >
                                    <div class="w-[228px] h-[228px] overflow-hidden">
                                        <el-image :src="img(item.logo)" fit="cover" class="w-[228px] h-[228px] align-middle">
                                            <template #error>
                                               <img :src="img(`static/resource/images/site/back0${ Math.floor(Math.random() * 4) + 1}.jpg`)" alt="" class="w-[228px] h-[228px]">
                                            </template>
                                        </el-image>
                                        <img :src="img(item.logo)" v-if="item.logo" class="w-[228px] h-[228px] align-middle" />
                                    </div>
                                    <div class="relative w-[80px] h-[80px] rounded-full border-[#eee] border-solid border-[2px] flex items-center justify-center mb-[18px] mt-[-36px]">
                                        <el-image :src="img(item.front_end_logo)" fit="cover" class="w-[80px] h-[80px] rounded-full" >
                                            <template #error>
                                               <img src="@/assets/images/shop_default.png" alt="" class="w-[80px] h-[80px] rounded-full">
                                            </template>
                                        </el-image>
                                    </div>
                                    <div class="flex justify-center">
                                        <div class="w-[170px] text-center truncate">{{item.site_name}}</div>
                                    </div>
                                    <div class="flex justify-center items-center text-[14px] leading-[26px]">
                                        <span class="text-[#838383]">主营：</span>
                                        <span class="max-[140px] truncate flex justify-center">{{ item.category_name }}</span>
                                    </div>
                                </div>
                            </div>
                        </template>
                    </div>
                    <div class="mt-[16px] flex justify-center">
                        <el-pagination v-model:current-page="shopTableData.page" v-model:page-size="shopTableData.limit" layout=" prev, pager, next" :total="shopTableData.total" background hide-on-single-page  @size-change="loadShopList()" @current-change="loadShopList" />
                    </div>
                </div>
                <div v-if="!shopTableData.data.length && !shopTableData.loading">
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from  'vue'
import { getShopList, getShopCategory} from '@/addon/mall/api/shop'
import { useRouter,useRoute } from 'vue-router'
const route = useRoute()
const router = useRouter()
let shopCategory = ref([])
const shopTableData = reactive<any>({
    page: 1,
	limit: 10,
	total: 0,
	loading: true,
	data: [],
	searchParam: {
		keyword:'',
        category_id:''
	}
})
// 店铺列表
const loadShopList = (page = 1) => {
	shopTableData.page = page
	shopTableData.loading = true
	getShopList({
		page: shopTableData.page,
        limit: shopTableData.limit,
        ...shopTableData.searchParam
	}).then(res =>{
        shopTableData.total = res.data.total
		shopTableData.data = res.data.data
        shopTableData.loading = false
	})
}
watch(()=> route.path ,(newValue,oldValue)=>{
    if(route.query.keyword) shopTableData.searchParam.keyword = route.query.keyword
    loadShopList()
},{immediate:true,deep: true})
// 店铺分类
const getShopCategoryFn = () =>{
    getShopCategory().then(res =>{
        const obj = { category_name: "全部", category_id: '' }
		shopCategory.value.push(obj)
		shopCategory.value = shopCategory.value.concat(res.data)
    })
}
getShopCategoryFn()
//切换分类
const handleType = (val) =>{
    shopTableData.searchParam.category_id = val
    loadShopList()
}
// 详情
const toDetail = (id) => {
	router.push(`/shop/detail?site_id=${id}`)
}

</script>

<style lang="scss" scoped>
.transform{
    transform: translateX(50%);
}
.street-list{
    grid-row-gap: 15px;
    grid-column-gap: 15px;
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    grid-template-rows: auto;
}

</style>