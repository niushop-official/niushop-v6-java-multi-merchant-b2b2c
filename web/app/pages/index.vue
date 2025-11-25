<template>
    <div id="content" class="w-full bg-[#f6f6f6]">
		<div class="bg-[#fff]">
			<div class="main-container flex h-[410px]">
				<div class="relative">
					<div class="absolute z-2 flex overflow-hidden" @mouseleave.stop="handleOut($event)">
						<el-scrollbar height="376px" class="w-[200px] px-[20px] py-[17px]">
							<div>
								<template v-for="(item,index) in categoryList" :key="index">
									<div class="flex items-center mb-[24px] category-first cursor-pointer"  :class="`index${index}`"  @mouseenter.stop="subMenuClick($event)">
										<el-image class="w-[40px] h-[40px] rounded-full" :src="img(item.image)" fit="cover">
											<template #error>
												<img class="w-[40px] h-[40px] rounded-full" src="@/assets/images/nav/default_category.png" />
											</template>
										</el-image>
										<div class="ml-[10px]">
											<div class="text-[14px] truncate text-[#333] w-[109px] hover:text-[var(--el-color-primary)]" @click.stop="toGoodsList(item.category_id)">{{item.category_name}}</div>
											<div class="flex flex-wrap mt-[8px]" v-if="item.child_list && item.child_list.length">
												<div class="text-[12px] text-[#999] max-w-[49px] truncate mr-[10px] hover:text-[var(--el-color-primary)]" @click.stop="toGoodsList(item.child_list[0].category_id)">{{ item.child_list[0].category_name }}</div>
												<div class="text-[12px] text-[#999] max-w-[49px] truncate hover:text-[var(--el-color-primary)]" @click.stop="toGoodsList(item.child_list[1].category_id)">{{ item.child_list[1] ? item.child_list[1].category_name:'' }}</div>
											</div>
										</div>
									</div>
								</template>
							</div>
						</el-scrollbar>
						<div class="box-border w-[1000px] bg-[#fafafa] pt-[20px] text-[#333] rounded-r-[12px] " v-if="rightShow">
							<el-scrollbar  height="390px">
								<template v-for="(subItem,subIndex) in categoryList[tabActive]?.child_list" :key="subIndex">
									<div>
										<div class="px-[20px] text-[14px] text-[#333] !leading-[20px] cursor-pointer text-left mb-[30px]" :class="{'mb-[10px]': !subItem.child_list }" @click.stop="toGoodsList(subItem.category_id)">{{subItem.category_name}}</div>
										<div class="flex-1 flex flex-wrap">
											<template v-for="(grandItem,grandIndex) in subItem.child_list" :key="grandIndex">
												<div class="flex items-center cursor-pointer px-[20px] mb-[30px]" @click.stop="toGoodsList(grandItem.category_id)">
													<div class="h-[60px] flex-shink-0 mr-[10px]">
														<el-image class="w-[60px] h-[60px] " :src="img(grandItem.image)" fit="cover">
															<template #error>
																<div class="image-slot">
																	<img class="w-[60px] h-[60px]" src="@/assets/images/nav/default_category.png" />
																</div>
															</template>
														</el-image>
													</div>
													<div class="text-[14px] text-[#999] text-left w-[140px] h-[60px] leading-[60px] truncate">{{grandItem.category_name}}</div>
												</div>
											</template>	
										</div>
									</div>
								</template>
							</el-scrollbar>
						</div>
					</div>
				</div>
				<div class="w-[740px] h-[380px] ml-[220px] mt-[15px] home-carousel">
					<el-carousel :interval="3000" height="380px" arrow="never" class="rounded-[12px]">
						<el-carousel-item v-for="(item,index) in advInfo.adv_list">
							<NuxtLink :to="item.adv_url && item.adv_url.url ? item.adv_url.url : '' ">
								<div class="h-full index-carousel">
									<img :src="img(item.adv_image)" alt="" class="w-full h-full">
								</div>
							</NuxtLink>
						</el-carousel-item>
					</el-carousel>
				</div>
				<div class="ml-[30px] mt-[15px] w-[220px]">
					<div class="flex items-center">
						<div class="w-[50px] h-[50px] flex-shrink-0" >
							<img v-if="(info && !info.headimg) || !info" class="w-[50px] h-[50px] rounded-full" src="@/assets/images/default_headimg.png" alt="">
							<img v-else :src="img(info.headimg)" class="w-[50px] h-[50px] rounded-full" alt="">
						</div>
						<div class="ml-[10px]">
							<div class="text-[14px] text-[#999] leading-[18px] mb-[10px]">Hi~你好</div>
							<NuxtLink to="/member/center" v-if="info">
								<div class="text-[14px] text-primary">{{ info.nickname }}</div>
							</NuxtLink>
							<div v-else  class="text-[16px] text-primary cursor-pointer"  @click="toLogin">{{ t('login') }}/{{ t('register') }}</div>
						</div>
					</div>
					<div class="h-[36px] rounded-[100px] bg-primary text-[#fff] flex-center text-[12px] my-[30px] cursor-pointer" @click="handleDetail('/member/merchant_settled')">申请入驻</div>
					<div class="border-b-[1px] border-dashed border-[#eee] pb-[8px] mb-[20px]" v-if="articleHotList.length">
						<div class="flex items-center mb-[18px] cursor-pointer" v-for="(item,index) in articleHotList" :key="index" @click="router.push(`/article/detail?id=${item.id}`)">
							<span class="w-[34px] h-[18px] bg-[#e8f1ff] text-[#116DFE] text-[12px] flex-center rounded-[3px] rounded-br-[10px] mr-[10px] flex-shrink-0">资讯</span>
							<span class="truncate text-[14px] text-[#666]">{{ item.title }}</span>
						</div>
					</div>
					<div class="flex-center">
						<div class="flex-1 flex flex-col items-center cursor-pointer" @click="handleDetail('/member/collect/goods')">
							<span class="iconfont icon-shoucang2 text-primary !text-[18px]"></span>
							<div class="text-[12px] mt-[6px]">我的收藏</div>
						</div>
						<div class="flex-1 flex flex-col items-center cursor-pointer" @click="handleDetail('/member/collect/shop')">
							<span class="iconfont icon-Vector-25 text-primary !text-[18px]"></span>
							<div class="text-[12px] mt-[6px]">关注店铺</div>
						</div>
						<div class="flex-1 flex flex-col items-center cursor-pointer" @click="handleDetail('/member/browse')">
							<span class="iconfont icon-zujiV6xx text-primary !text-[20px]"></span>
							<div class="text-[12px] mt-[6px]">我的足迹</div>
						</div>
					</div>
				</div>
            </div>   
		</div>
		<div class="main-container">
			<div class="mt-[40px]">
				<div class="mt-[30px] mb-[10px]" v-if="shopList.config && shopList.config.is_show_shop">
					<div class="flex justify-between items-center mb-[20px]">
						<div class="leading-[40px] h-[40px] text-[24px]  text-[#333]"> 甄选好店</div>
					</div>
					<div class="flex box-border">
						<div class="w-[288px] h-[422px]">
							<el-image :src="img(shopList.config.shop_ad_image)" fit="cover" class="w-[288px] h-[422px] rounded-tl-[16px] rounded-br-[16px]"/>
						</div>
						<div class="flex flex-wrap flex-1 w-[912px]">
							<template v-for="(item,index) in shopList.list" :key="index">
								<div class="w-[288px] h-[203px] bg-[#fff] rounded-[16px] py-[15px] pl-[15px] mb-[15px] ml-[16px] cursor-pointer" @click="router.push('/shop/detail?site_id='+item.site_id)">
									<div class="flex items-center mb-[15px] pr-[20px]">
										<div class="flex-center">
											<el-image class="w-[36px] h-[36px] rounded-[50%] mr-[10px]" :src="img(item.front_end_logo ? item.front_end_logo : '')" fit="cover">
												<template #error>
													<img src="@/assets/images/shop_default.png" alt="">
												</template>
											</el-image>
											<div class="flex-center">
												<div class="text-[14px] text-[#333] mr-[6px] max-w-[160px] font-500 truncate">{{ item.site_name }}</div>
												<div class="bg-primary rounded-[2px] text-[12px] text-[#fff] py-[2px] px-[4px]" v-if="item.shop && item.shop.is_self">自营</div>
											</div>
										</div>
									</div>
									<div class="flex">
										<div class="w-[122px] h-[122px] relative rounded-[12px] overflow-hidden mr-[14px]" v-for="(subItem,subIndex) in item.goods_list" :key="subIndex" @click.stop="toDetail(subItem.goods_id)">
											<el-image class="w-[122px] h-[122px]" :src="img(subItem.goods_cover_thumb_small)" fit="cover">
												<template #error>
													<img src="@/assets/images/goods_default.png" class="w-[122px] h-[122px]">
												</template>
											</el-image>
											<div class="px-[8px] py-[4px] price-font text-[12px] font-500 text-[#fff] absolute bottom-0 right-0 price-style">
												<span class="oppoSans-M">￥</span>
												<span class="text-16px">{{ subItem.goodsSku.price }}</span>
											</div>
										</div>
									</div>
								</div>
							</template>
						</div>
					</div>
				</div>
				<template v-for="(item,index) in floorList" :key="index">
					<div class="mt-[30px] mb-[10px]">
						<div class="flex justify-between items-center mb-[20px]" v-if="item.config && (item.config?.title || item.config?.sub_title || item.config?.url)">
							<div class="flex items-center">
								<div class="leading-[40px] h-[40px]">
									<span class="text-[24px]  text-[#333]">{{item.config.title}}</span>
									<span class="text-[#999] text-[12px] ml-[10px]">{{item.config.sub_title}}</span>
								</div>
							</div>
							<div class="text-[14px] text-[#999] cursor-pointer oppoSans-R" v-if="item.config && item.config.url && item.config.url.url" @click="toLink(item.config.url.url)">
								<span>更多</span>
								<span class="iconfont icon-youV6xx ml-[4px]"></span>
							</div>
						</div>
						<div class="flex box-border">
							<div class="w-[224px] h-[628px] mr-[20px]" v-if="item.config.adv_img1">
								<el-image :src="img(item.config.adv_img1)" fit="cover" class="w-full max-w-[628px] rounded-tl-[16px] rounded-br-[16px]"/>
							</div>
							<div class="flex  flex-wrap flex-1">
								<template v-for="(subItem,subIndex) in item.goods_list" :key="subIndex">
									<div class="w-[224px] h-[304px] mb-[20px] bg-[#fff] py-[11px] cursor-pointer rounded-[var(--rounded-big)]" :class="{'mr-[20px]': (item.config.adv_img1 ? (subIndex + 1) % 4 :   (subIndex + 1) % 5 ) }" @click="toDetail(subItem.goods_id)" v-if="item.config.adv_img1 ? (subIndex < 8) : (subIndex < 10)">
										<div class="w-full h-[200px] mb-[10px] flex items-center justify-center" >
											<el-image class="rounded-[var(--rounded-mid)]" style="width: 200px; height: 200px" :src="img(subItem.goods_cover)" fit="cover">
												<template #error>
                                                    <img src="@/assets/images/goods_default.png" class="w-[200px] h-[200px]">
                                                </template>
											</el-image>
										</div>
										<div class="mx-[10px]">
											<div class="mb-[10px] h-[42px] text-[14px] multi-hidden text-[#333] leading-[21px]">{{ subItem.goods_name }}</div>
											<div class="flex items-center justify-between flex-wrap">
												<div class="text-[var(--el-price)] flex items-baseline">
													<span class="text-[12px] price-font">￥</span>
													<span class="text-[20px] price-font">{{parseFloat(goodsPrice(subItem)).toFixed(2)}}</span>
													<img  v-if="priceType(subItem) == 'member_price'" class="h-[14px] ml-[3px] w-[24px]" src="@/assets/images/addon/VIP.png" />
													<img   v-if="priceType(subItem) == 'discount_price'" class="h-[12px] ml-[3px] w-[36px]" src="@/assets/images/addon/discount.png" />
												</div>
												<div class="text-[12px] leading-[16px] text-[#999]">已售{{subItem.sale_num}}{{ subItem.unit || '件' }}</div>
											</div>
										</div>
									</div>
								</template>
							</div>
						</div>
						<div class="w-[1200px] mt-[10px]" v-if="item.config.adv_img2">
							<el-image  :src="img(item.config.adv_img2)" fit="cover" class="w-full h-auto rounded-tl-[var(--rounded-mid)] rounded-br-[var(--rounded-mid)]"/>
						</div>
					</div>
				</template>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { getToken} from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'
import { getAdvInfo, getFloor, getCategoryTree, getWebShopList,checkShopStatus } from '@/app/api/index'
import { getArticleHot } from '@/app/api/article'
import useMemberStore from '@/stores/member'
import useConfigStore from '@/stores/config'

const router = useRouter()
const memberStore = useMemberStore()
const configStore = useConfigStore()
const info = computed(() => memberStore.info)
// 一级菜单样式控制
const tabActive = ref(0)
const categoryList = ref<any>([])
const getCategoryTreeFn = () => {
    getCategoryTree().then((res: any) => {
        categoryList.value = res.data
    })
}
getCategoryTreeFn()

// 控制右侧展示
const rightShow = ref(false)
const subMenuClick = (event: any) =>{
    if(event.target.className.indexOf('index') !== -1){
        let data = event.target.className.split('index') 
        tabActive.value = Number(data[1])
        if(categoryList.value[tabActive.value].child_list&& categoryList.value[tabActive.value].child_list.length){
			rightShow.value = true
		}else{
			rightShow.value = false
		}
    }
}
const handleOut = (event: any)=>{
    rightShow.value = false
}
// 去详情
const toGoodsList = (id: any) =>{
    rightShow.value = false
    router.push({path:'/goods/list',query:{goods_mall_category:id}})
}

// 未登录
const toLogin = () =>{
    if(!getToken() && !configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile){
        ElMessage.error('商家未开启普通账号登录注册')
        return false
    }
    memberStore.logOpen()
}

//获取热门资讯信息
const articleHotList = ref([])
const articleHotLoading = ref(true)
const getArticleHotFn = () =>{
    getArticleHot({limit:4}).then((res: any) =>{
        articleHotList.value = res.data
        articleHotLoading.value = false
    }).catch(() =>{
        articleHotLoading.value = false
    })
}
getArticleHotFn()
// 申请入驻,收藏，足迹，店铺
const handleDetail = (data: any) =>{
	if(!getToken()){
        if(!configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile){
            ElMessage.error('商家未开启普通账号登录注册')
            return false
        }else{
            memberStore.logOpen()
            return false
        }
    }
	console.log(data)
	if(data == '/member/merchant_settled'){
		checkShopStatus().then((res: any)=>{
			if(res.data){
				router.push('/member/merchant_record')
			}else{
				router.push(data)
			}
		})
	}else{
		router.push(data)
	}
}



// 广告位
const advInfo = ref({})
const getAdvInfoFn = () =>{
	getAdvInfo({ap_key:'ADV_INDEX'}).then((res: any) =>{
		advInfo.value = res.data
	})
}
getAdvInfoFn()

// 首页楼层
const floorList = ref<any>([])
const paginationObj = ref({
	total: 0,
	limit: 2,
	page: 1
});
//无限滚动
const scrollLock = ref(true)
onMounted(() => {
	nextTick(() => {
		document.getElementById('main').addEventListener('scroll', handleScroll);
	})
})
onUnmounted(() => {
	document.getElementById('main')?.removeEventListener('scroll', handleScroll);
})
const handleScroll = () => {
	if (scrollLock.value) {

		let scrollTop = document.getElementById('main').scrollTop
		let height = document.getElementById('content').clientHeight - document.getElementById('main')?.clientHeight - 100
		console.log(scrollTop, height)
		if (scrollTop >= height) {
			paginationObj.value.page++
			getFloorFn()
		}
	}
	// console.log(document.getElementById('main').scrollTop)
}
const getFloorFn = () =>{
		let obj = {
		page: paginationObj.value.page,
		limit: paginationObj.value.limit
	}
	scrollLock.value = false
	getFloor(obj).then((res: any) =>{
		scrollLock.value = true
		floorList.value = floorList.value.concat(res.data.data)
		paginationObj.value.total = res.data.total;
		if (res.data.total == floorList.value.length) scrollLock.value = false
	})
}
getFloorFn()

// 甄选店铺
const shopList = ref<any>({})
const getWebShopListFn = () =>{
	getWebShopList().then((res: any) =>{
		shopList.value = res.data
	})
}
getWebShopListFn()
// 查看更多
const toLink = (url:string) =>{
    // 外部链接
    if (url.indexOf('https') != -1 || url.indexOf('http') != -1) {
        window.open(url)
    }else {
        router.push(url)
    }
}
// 商品详情
const toDetail = (goods_id:number) =>{
	router.push(`/goods/detail?id=${goods_id}`)
}
	// 价格类型 
	let priceType = (data:any) =>{
		return data.goodsSku.show_type
	}
	// 商品价格
	let goodsPrice = (data:any) =>{
		let price = data.goodsSku.show_price
        return parseFloat(price);
	}
</script>

<style lang="scss" scoped>
.category-first:last-child{
	margin-bottom: 0px!important;
}

.brick-item{
	transition: all .2s linear;
	&:hover{
		box-shadow: 0 15px 30px rgba(0,0,0,.1);
		transform: translate3d(0,-2px,0)
	}
}
.price-style{
	background: rgba(0, 0, 0, .5);
    border-radius: 12px 0;
}
/* 多行超出隐藏 */
.multi-hidden {
	word-break: break-all;
	text-overflow: ellipsis;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}
.floor-bg{
	/*background-image: url(@/assets/images/index/floor-bg.png);*/
	/*background-position: center center;*/
	/*background-size: 100% 100%;*/
	/*background-repeat: no-repeat;*/
}
</style>