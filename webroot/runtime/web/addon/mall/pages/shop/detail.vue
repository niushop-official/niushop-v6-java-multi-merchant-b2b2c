<template>
    <div class="bg-[#f5f5f5]">
        <div class="main-container" v-loading ="goodsTableData.loading">
            <div class="pt-[10px] flex justify-between mt-[25px]">
                <div class="w-[224px] flex flex-col">
                    <info />
                    <category @categoryClick="categoryClick" />
                </div>
                <div class="flex-1 ml-[18px] pb-[30px]">
                    <div class="flex items-center text-[14px] bg-[#fff] px-[20px] py-[10px] rounded-[var(--rounded-mid)]">
                            <span class="mr-[20px] text-[#969696]">排序:</span>
                            <div class="flex-1 flex items-center text-[#424242] ">
                                <span class="mr-[30px] cursor-pointer" :class="{'text-primary': goodsTableData.searchParam.order == ''}" @click="orderClick('')">综合</span>
                                <div class="mr-[30px] cursor-pointer flex items-center" @click="orderClick('sale_num')">
                                    <span :class="{'text-primary': goodsTableData.searchParam.order == 'sale_num'}">销量</span>
                                    <div class="h-[20px] overflow-hidden ml-[6px] text-[#999]">
                                        <div class="!text-[14px] iconfont icon-shangV6xx-1" :class="{'text-primary': goodsTableData.searchParam.order == 'sale_num'&&goodsTableData.searchParam.sort != 'desc'}"></div>
                                        <div class="!text-[14px] iconfont icon-xiaV6xx mt-[-7px]" :class="{'text-primary': goodsTableData.searchParam.order == 'sale_num'&&goodsTableData.searchParam.sort == 'desc'}"></div>
                                    </div>
                                </div>
                                <div class="mr-[30px] cursor-pointer flex items-center" @click="orderClick('price')">
                                    <span :class="{'text-primary': goodsTableData.searchParam.order == 'price'}">价格</span>
                                    <div class="h-[20px] overflow-hidden ml-[6px] text-[#999]">
                                        <div class="!text-[14px] iconfont icon-shangV6xx-1" :class="{'text-primary': goodsTableData.searchParam.order == 'price'&&goodsTableData.searchParam.sort != 'desc'}"></div>
                                        <div class="!text-[14px] iconfont icon-xiaV6xx mt-[-7px]" :class="{'text-primary': goodsTableData.searchParam.order == 'price'&&goodsTableData.searchParam.sort == 'desc'}"></div>
                                    </div>
                                </div>
                                <div class="" style="margin-left: 15px">
                                    <el-input style="width: 84px" size="mini" v-model="start_price" onkeyup="this.value = this.value.replace(/[^\d.]/g,'');">
                                        <template #prefix>
                                            <span>￥</span>
                                        </template>
                                    </el-input>
                                    <span> ~ </span>
                                    <el-input style="width: 84px" size="mini" v-model="end_price" onkeyup="this.value = this.value.replace(/[^\d.]/g,'');">
                                        <template #prefix>
                                            <span>￥</span>
                                        </template>
                                    </el-input>
                                    <el-button type="primary" size="mini" style="margin-left: 15px" @click="inputUp">确定</el-button>
                                </div>
                            </div>
                    </div>
                    <div class="mt-[20px]" v-if="goodsTableData.data.length">
                        <div class="flex flex-wrap items-center" >
                            <div class="bg-[#fff]  w-[224px] cursor-pointer rounded-[var(--rounded-big)] mb-[20px] overflow-hidden" :class="{'mr-[20px]':(index + 1) % 4}" v-for="(item,index) in goodsTableData.data" :key="index"  @click="toDetail(item.goods_id)">
                                <div class="w-[224px] h-[224px]">
                                    <el-image class="w-[224px] h-[224px]" :src="img(item.goods_cover_thumb_mid ? item.goods_cover_thumb_mid : '')" fit="cover">
                                        <template #error>
                                            <img src="@/assets/images/goods_default.png" class="w-[224px] h-[224px]">
                                        </template>
                                    </el-image>
                                </div>
                                <div class="p-[15px]">
                                    <div class="text-[22px] text-primary flex items-baseline mb-[8px]">
                                        <span class="price-font">¥{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2) }}</span>
                                        <img  v-if="diyGoods.priceType(item) == 'member_price'" class="h-[14px] ml-[3px] w-[24px]" src="@/assets/images/addon/VIP.png" />
                                        <img   v-if="diyGoods.priceType(item) == 'discount_price'" class="h-[12px] ml-[3px] w-[36px]" src="@/assets/images/addon/discount.png" />
                                    </div>
                                    <div class="text-[#333] h-[40px] multi-hidden text-[14px] leading-[20px] mb-[12px]">{{ item.goods_name }}</div>
                                    <div class="text-[#999] text-[12px] leading-[16px]">
                                        <div>已售{{item.sale_num}}{{item.unit || '件' }}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="mt-[20px] flex justify-center">
                            <el-pagination v-if="route.query.coupon_id" background  small hide-on-single-page v-model:current-page="goodsTableData.page" v-model:page-size="goodsTableData.limit"
                                    layout="prev, pager, next" :total="goodsTableData.total" @size-change="getGoodsCouponPagesFn()"
                                    @current-change="getGoodsCouponPagesFn" />
                            <el-pagination v-else background  small hide-on-single-page v-model:current-page="goodsTableData.page" v-model:page-size="goodsTableData.limit"
                                    layout="prev, pager, next" :total="goodsTableData.total" @size-change="loadGoodsList()"
                                    @current-change="loadGoodsList" />
                        </div>
                    </div>
                    <div class="min-h-[320px] bg-[#fff]" v-else>
                        <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
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
import { getShopGoodsPages, getGoodsCouponPages} from '@/addon/mall/api/goods'
import { useRouter, useRoute } from 'vue-router'
import { getToken} from '@/utils/common'
import { useGoods } from '@/addon/mall/hooks/useGoods'

const router = useRouter()
const route = useRoute()

const diyGoods = useGoods();

const goodsTableData = reactive({
	page: 1,
	limit: 16,
	total: 0,
	loading: true,
	data: [],
	searchParam: {
		keyword: '',
		goods_category: '',
        coupon_id: '',
        site_id:'',
		order:'',
		sort:'desc',
		start_price:'',
		end_price:''
	}
})
goodsTableData.searchParam = Object.assign(goodsTableData.searchParam,route.query)
//获取店铺列表
const loadGoodsList = (page = 1) => {
	goodsTableData.page = page
	goodsTableData.loading = true
	getShopGoodsPages({
		page: goodsTableData.page,
        limit: goodsTableData.limit,
        ...goodsTableData.searchParam
	}).then(res =>{
		goodsTableData.data = res.data.data,
		goodsTableData.total = res.data.total
        goodsTableData.loading = false

	})
}
// 获取优惠劵可用商品列表
const getGoodsCouponPagesFn = (page = 1) =>{
    goodsTableData.page = page
	goodsTableData.loading = true
	getGoodsCouponPages({
		page: goodsTableData.page,
        limit: goodsTableData.limit,
        ...goodsTableData.searchParam
	}).then(res =>{
		goodsTableData.data = res.data.data,
		goodsTableData.total = res.data.total
        goodsTableData.loading = false

	})
}
if(route.query.coupon_id){
    getGoodsCouponPagesFn()
}else{
    loadGoodsList()
}
const toDetail = (goods_id) => {
	router.push(`/goods/detail?id=${goods_id}`)
}

//input价格控制
const start_price = ref()
const end_price = ref()
const inputUp=()=>{
      let input1 = start_price.value == "" ? "0" : start_price.value;
      let input2 = end_price.value == "" ? "0" : end_price.value;
      if (input1 === "0" && input2 === "0") return false;
      if (!isNaN(parseInt(input1)) && !isNaN(parseInt(input2))) {
        if (parseInt(input1) > parseInt(input2) && input2 != "0") {
			ElMessage({
            message: "你所选择的区间不对哦！",
            type: "warning",
          });
          start_price.value = "";
          end_price.value = "";
          return false;
        }
		goodsTableData.searchParam.start_price = start_price.value
		goodsTableData.searchParam.end_price = end_price.value
		if(route.query.coupon_id){
            getGoodsCouponPagesFn()
        }else{
            loadGoodsList()
        }
      } else {
        ElMessage({
          message: "请输入正确的内容",
          type: "warning",
        });
      }
}
const categoryClick = (id:any)=>{
    goodsTableData.searchParam.goods_category = id
    if(route.query.coupon_id){
        getGoodsCouponPagesFn()
    }else{
        loadGoodsList()
    }
    router.push({path:'/shop/detail',query:goodsTableData.searchParam})
}

const orderClick=(item:string)=>{
	if(item == '' && item == goodsTableData.searchParam.order){
		return false
	}else if(item == goodsTableData.searchParam.order){
		goodsTableData.searchParam.sort = goodsTableData.searchParam.sort=='desc'?'asc':'desc'
		
	}else{
		goodsTableData.searchParam.order = item
		goodsTableData.searchParam.sort = 'desc'
	}
	if(route.query.coupon_id){
        getGoodsCouponPagesFn()
    }else{
        loadGoodsList()
    }
}
// 价格类型 
let priceType = (data: any) =>{
	let type = "";
	if(data.is_discount && data.goodsSku.sale_price != data.goodsSku.price){
		type = 'discount_price'// 折扣
	}else if(data.member_discount && getToken() && data.goodsSku.member_price != data.goodsSku.price){
		type = 'member_price' // 会员价
	}else{ 
		type = ""
	}
	return type;
}
// 商品价格
let goodsPrice = (data: any) =>{
	let price = "0.00";
	if(data.is_discount && data.goodsSku.sale_price != data.goodsSku.price){
		price = data.goodsSku.sale_price ? data.goodsSku.sale_price : data.goodsSku.price // 折扣价
	}else if(data.member_discount && getToken() && data.goodsSku.member_price != data.goodsSku.price){
		price = data.goodsSku.member_price?data.goodsSku.member_price:data.goodsSku.price // 会员价
	}else{
		price = data.goodsSku.price
	}
	return price;
}
</script>

<style lang="scss" scoped>

</style>