<template>
    <div class="w-full bg-[#f5f5f5]">
        <div class="main-container pb-[40px] pt-[20px]">
			<el-breadcrumb class="mb-[20px]" :separator-icon="ArrowRight" v-if="breadcrumbList.length">
				<el-breadcrumb-item v-for="(item,index) in breadcrumbList" :key="index">
					<span v-if="item.category_id" class="cursor-pointer" @click="categoryClick(item.category_id)">{{item.category_name}}</span>
					<span v-else class="cursor-pointer text-[#333] font-600" @click="categoryClick(item.category_id)">{{item.category_name}}</span>
				</el-breadcrumb-item>
				<el-breadcrumb-item v-if="goodsTableData.searchParam.keyword&&breadcrumbList.length"><span>{{ goodsTableData.searchParam.keyword }}</span></el-breadcrumb-item>
			</el-breadcrumb>
			<div class="bg-[#fff] px-[16px] py-[25px] rounded-[var(--rounded-mid)]">
				<div class="flex  text-[14px] ">
					<span class="mr-[20px]  text-[#969696]">分类:</span>
					<div class="flex-1 text-[#424242] border-b-[1px] border-dotted border-[#efefef] pb-[10px] flex flex-wrap">
						<span class="w-[100px] text-center px-[8px] box-border truncate mr-[20px] mb-[15px] cursor-pointer" :class="{'text-primary': goodsTableData.searchParam.goods_mall_category == all_id}" @click="categoryClick(all_id)">全部</span>
						<span v-for="(item,index) in categoryList" :key="index" class="w-[100px] text-center px-[8px] box-border truncate  mb-[15px] mr-[20px] hover:text-primary cursor-pointer" :class="{'text-primary': goodsTableData.searchParam.goods_mall_category == item.category_id}" @click="categoryClick(item.category_id)">{{item.category_name}}</span>
					</div>
				</div>
				<div class="flex items-center text-[14px] mt-[20px]">
					<span class="mr-[20px] text-[#969696]">排序:</span>
					<div class="flex-1 flex items-center text-[#424242] ">
						<span class="mr-[30px] cursor-pointer" :class="{'text-primary': goodsTableData.searchParam.order == ''}" @click="orderClick('')">综合</span>
						<div class="mr-[30px] cursor-pointer flex items-center"  @click="orderClick('sale_num')">
							<span :class="{'text-primary': goodsTableData.searchParam.order == 'sale_num'}">销量</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999]">
								<div class="!text-[14px] iconfont icon-shangV6xx-1 text-[#999]" :class="{'!text-primary': goodsTableData.searchParam.order == 'sale_num'&&goodsTableData.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] iconfont icon-xiaV6xx mt-[-7px] text-[#999]" :class="{'!text-primary': goodsTableData.searchParam.order == 'sale_num'&&goodsTableData.searchParam.sort == 'desc'}"></div>
							</div>
						</div>
						<div class="mr-[30px] cursor-pointer flex items-center" @click="orderClick('price')">
							<span :class="{'text-primary': goodsTableData.searchParam.order == 'price'}">价格</span>
							<div class="h-[20px] overflow-hidden ml-[6px] text-[#999]">
								<div class="!text-[14px] iconfont icon-shangV6xx-1 text-[#999]" :class="{'!text-primary': goodsTableData.searchParam.order == 'price'&&goodsTableData.searchParam.sort != 'desc'}"></div>
								<div class="!text-[14px] iconfont icon-xiaV6xx mt-[-7px] text-[#999]" :class="{'!text-primary': goodsTableData.searchParam.order == 'price'&&goodsTableData.searchParam.sort == 'desc'}"></div>
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
							<el-button type="primary" size="mini" class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]"  style="margin-left: 15px" @click="inputUp">确定</el-button>
						</div>
					</div>
				</div>
			</div>
			<div v-loading="goodsTableData.loading">
				<div class="h-[70%]" v-if="goodsTableData.data.length">
					<div class="flex flex-wrap mt-[20px]">
						<template v-for="(item, index) in goodsTableData.data" :key="item.goods_id">
							<div data-aos="fade-up"  data-aos-anchor="null" data-aos-anchor-placement="top-bottom" data-aos-duration="1000" data-aos-easing="ease-out-back">
								<div class="w-[228px] bg-[#fff]  mb-[15px] cursor-pointer rounded-[var(--rounded-big)] overflow-hidden goods-item" @click="toDetail(item.goods_id)" :class="{'mr-[15px]': (index + 1) % 5 }">
									<div class="w-[228px] h-[228px]">
										<el-image class="w-[228px] h-[228px]" :src="img(item.goods_cover_thumb_mid ? item.goods_cover_thumb_mid : '')" fit="cover">
											<template #error>
												<img src="@/assets/images/goods_default.png" class="w-[228px] h-[228px]">
											</template>
										</el-image>
									</div>
									<div class="p-[15px]">
										<div class="text-[22px] text-primary flex items-baseline mb-[8px]">
											<span class="price-font">¥{{ diyGoods.goodsPrice(item).toFixed(2) }}</span>
											<img  v-if="diyGoods.priceType(item) == 'member_price'" class="h-[14px] ml-[3px] w-[24px]" src="@/assets/images/addon/VIP.png" />
											<img   v-if="diyGoods.priceType(item) == 'discount_price'" class="h-[12px] ml-[3px] w-[36px]" src="@/assets/images/addon/discount.png" />
										</div>
										<div class="flex items-center multi-hidden text-[14px] leading-[20px] text-[#333] h-[40px] mb-[12px]">
											{{item.goods_name}}
										</div>
										<div class="flex justify-between items-center">
											<div class="flex items-center">
												<span class="iconfont icon-Vector-25 text-primary mr-[6px] !text-[14px]"></span>
												<span v-if="item.site" class="text-[12px] text-[#666] max-w-[100px] truncate">{{ item.site.site_name}}</span>
											</div>
											<div class="text-[12px] leading-[16px] text-[#999]">已售{{item.sale_num}}{{ item.unit || '件' }}</div>
										</div>
									</div>
								</div>
							</div>
						</template>
					</div>
					<div class="mt-[20px] flex justify-center">
						<el-pagination v-if="route.query.coupon_id" background  small hide-on-single-page v-model:current-page="goodsTableData.page" v-model:page-size="goodsTableData.limit"
								layout="prev, pager, next" :total="goodsTableData.total" @size-change="loadGoodsCouponList()"
								@current-change="loadGoodsCouponList" />
						<el-pagination v-else background  small hide-on-single-page v-model:current-page="goodsTableData.page" v-model:page-size="goodsTableData.limit"
								layout="prev, pager, next" :total="goodsTableData.total" @size-change="loadGoodsList()"
								@current-change="loadGoodsList" />
					</div>
				</div>
				<div v-else class="min-h-[500px] bg-[#fff]">
					<el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import {ref, reactive,watch} from 'vue'
import { getGoodsPages, getGoodsCouponPages} from '@/addon/mall/api/goods'
import { getCategoryTree } from '@/app/api/index'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import { getToken} from '@/utils/common'
import { useGoods } from '@/addon/mall/hooks/useGoods'

const router = useRouter()
const route = useRoute()

const diyGoods = useGoods();
const goodsTableData = reactive({
	page: 1,
	limit: 25,
	total: 0,
	loading: true,
	data: [],
	searchParam: {
		keyword: '',
		goods_mall_category: '',
		order:'',
		sort:'desc',
		start_price:'',
		end_price:''
	}
})
  // 这里处理路由query变化的逻辑
  goodsTableData.searchParam = Object.assign({
	keyword: '',
	goods_mall_category: '',
	order:'',
	sort:'desc',
	start_price:'',
	end_price:''
 },route.query)
//获取分类列表
const categoryListAll = ref([])
const categoryList = ref([
	{
		category_id: '',
		category_name: '',
		category_level: '',
		child_list: []
	}
])
const all_id = ref('')
const breadcrumbList = ref([])
const getCategoryTreeFn = () => {
    getCategoryTree().then((res: any) => {
		categoryListAll.value = res.data
		setCategoryList(categoryListAll.value)
        
    })
}
getCategoryTreeFn()
//设置分类数据及面包屑
const setCategoryList = (data:Array<any>)=>{
	if(goodsTableData.searchParam.goods_mall_category){
		categoryList.value = categoryTreeFilter(data,goodsTableData.searchParam.goods_mall_category)
	}else{
		all_id.value = ''
		categoryList.value = data
	}
	breadcrumbList.value = all_id.value?findParentNodes(categoryListAll.value,goodsTableData.searchParam.goods_mall_category,[{category_name:'全部',category_id:''}]):[]
}

//过滤分类数据
const categoryTreeFilter = (arr:Array<any>,id:string)=>{
	for(var i =0;i<arr.length;i++){
		if(arr[i].category_id.toString() === id){
			if(arr[i].level===3){
				all_id.value = arr[i].pid.toString()
				return arr
			}else{
				all_id.value = arr[i].category_id.toString()
				return arr[i].child_list||[]
			}
			
		}else if(arr[i].child_list&&arr[i].child_list.length){
			const found:any[] =  categoryTreeFilter(arr[i].child_list,id)
			if (found) {
				return found;
			}
		}
	}
}
//获取all_id所有父级
const findParentNodes =(tree:Array<any>, category_id:string, parents:Array<any> = [])=>{
    for (let node of tree) {
        if (node.category_id.toString() === category_id) {
			return [...parents, node]; // 包括目标节点本身
        }
        if (node.child_list&&node.child_list.length) {
            let found:any = findParentNodes(node.child_list, category_id, [...parents, node]);
            if (found) {
                return found;
            }
        }
    }
    return null;
}
//分类点击
const categoryClick = (id:any)=>{
	goodsTableData.searchParam.goods_mall_category = id
	router.push({path:'/goods/list',query:goodsTableData.searchParam})

}
//排序点击
const orderClick=(item:string)=>{
	if(item == '' && item == goodsTableData.searchParam.order){
		return false
	}else if(item == goodsTableData.searchParam.order){
		goodsTableData.searchParam.sort = goodsTableData.searchParam.sort=='desc'?'asc':'desc'
		
	}else{
		goodsTableData.searchParam.order = item
		goodsTableData.searchParam.sort = 'desc'
	}
	router.push({path:'/goods/list',query:goodsTableData.searchParam})
}
const start_price = ref('')
const end_price = ref('')
const inputUp=()=>{
      let input1 = start_price.value == "" ? "0" : start_price.value;
      let input2 = end_price.value == "" ? "0" : end_price.value;
    //   if (input1 === "0" && input2 === "0") return false;
      if (!isNaN(parseInt(input1)) && !isNaN(parseInt(input2))) {
        if (parseInt(input1) > parseInt(input2) && end_price.value != "") {
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
		router.push({path:'/goods/list',query:goodsTableData.searchParam})
      } else {
        ElMessage({
          message: "请输入正确的内容",
          type: "warning",
        });
      }
	  
}


// 搜索商品列表
const loadGoodsList = (page = 1) => {
	goodsTableData.page = page
	goodsTableData.loading = true
	getGoodsPages({
		page: goodsTableData.page,
        limit: goodsTableData.limit,
		...goodsTableData.searchParam
	}).then((res:any) =>{
		goodsTableData.data = res.data.data
		goodsTableData.total = res.data.total
		goodsTableData.loading = false
	})
}

// 搜索有可用优惠劵的列表
const loadGoodsCouponList = (page = 1) => {
	goodsTableData.page = page
	goodsTableData.loading = true
	getGoodsCouponPages({
		page: goodsTableData.page,
        limit: goodsTableData.limit,
		...goodsTableData.searchParam
	}).then((res:any) =>{
		goodsTableData.data = res.data.data
		goodsTableData.total = res.data.total
		goodsTableData.loading = false
	})
}

if(route.query.coupon_id){
	loadGoodsCouponList()
}else{
	loadGoodsList()
}

const toDetail = (goods_id:number) => {
	router.push(`/goods/detail?id=${goods_id}`)
}

watch(() => route.query, () => {
  // 这里处理路由query变化的逻辑
 goodsTableData.searchParam = Object.assign({
	keyword: '',
	goods_mall_category: '',
	order:'',
	sort:'desc',
	start_price:'',
	end_price:''
 },route.query)
 setCategoryList(categoryListAll.value)
 if(route.query.coupon_id){
	loadGoodsCouponList()
 }else{
	loadGoodsList()
 }
});
</script>

<style lang="scss" scoped>

</style>