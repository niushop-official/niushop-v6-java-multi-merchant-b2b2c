<template>
    <div class="w-full bg-[#f5f5f5] mb-[-40px]" :style="{backgroundImage:'url(' + img('addon/mall/web/coupon_bg.png') + ')'}" v-loading="couponTable.loading">
        <div class="main-container pt-[30px]">
            <!-- <div>
                <el-carousel :interval="3000" height="500px" arrow="never" class="rounded-[12px]">
                    <el-carousel-item v-for="(item,index) in advInfo.adv_list">
                        <NuxtLink :to="item.adv_url && item.adv_url.url ? item.adv_url.url : '' ">
                            <div class="h-full index-carousel">
                                <img :src="img(item.adv_image)" alt="" class="w-full h-full">
                            </div>
                        </NuxtLink>
                    </el-carousel-item>
                </el-carousel>
            </div> -->
			<h1 class="text-center text-[30px] font-bold">领券中心</h1>
			<div class="mt-[30px]" v-if="couponTable.data.length">
				<div class="flex flex-wrap ">
					<template v-for="(item,index) in couponTable.data">
						<div class="w-[384px] h-[142px] mb-[20px] box-shadow flex rounded-[10px] coupon-item" :class="{'coupon-get': item.btnType === 'collecting','coupon-gouse': item.btnType === 'using', 'coupon-done': item.btnType === 'collected' || item.btnType === 'used' , 'mr-[24px]': (index + 1) % 3}">
							<div class="w-[330px] py-[16px] pl-[16px] flex box-border">
								<div class="flex items-center justify-center mr-[16px]" v-if="item.goods">
									<el-image class="w-[105px] h-[105px] rounded-[10px]" :src="img(item.goods.goods_cover ?item.goods.goods_cover : '' )" fit="cover" >
										<template #error>
											<img src="@/assets/images/goods_default.png" class="w-[105px] h-[105px]">
										</template>
									</el-image>
								</div>
								<div class="flex-1 flex items-center">
									<div class="flex flex-col justify-between">
										<div class="text-[12px] text-[#969696]">
											<p class="text-[16px] text-primary ">
												<span class="price-font">￥</span>
												<span class="text-[28px] font-500 price-font">{{item.coupon_price}}</span>
											</p>
											<div class="mt-[12px] text-[14px] text-[#282828] leading-[22px] flex items-center">
												<span class="text-primary text-[12px] px-[7px] leading-[20px] text-center rounded-[10px] bg-color">{{item.type_name}}</span>
												<div class="ml-[5px]">
													<span v-if="item.min_condition_money === '0.00'">无门槛使用</span>
													<span v-else>满{{ item.coupon_min_price }}元可用</span>
												</div>
											</div>
										</div>
										<div class="mt-[20px] text-[11px] text-[#969696] leading-[22px] flex items-center">
											<div v-if="item.valid_type == 1">领取之日起<span>{{ item.length || '' }}</span>天内有效</div>
											<div v-else> 有效期至<span>{{ item.valid_end_time ? item.valid_end_time.slice(0, 10) : '' }}</span></div>
										</div>
									</div>
								</div>
							</div>
							<div v-if="item.btnType === 'collecting'" class="w-[54px] px-[18px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]" @click="getCouponFn(item.id,index)">立即领取</div>
							<div v-if="item.btnType === 'using'" class="w-[54px] px-[18px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]" @click="toUse(item.id)">去使用</div>
							<div v-if="item.btnType === 'collected'" class="w-[54px] px-[18px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]">已领完</div>
							<div v-if="item.btnType === 'used'" class="w-[54px] px-[18px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]">已使用</div>
						</div>
					</template>
				</div>
				<div class="mt-[20px] flex justify-center">
					<el-pagination background  small hide-on-single-page v-model:current-page="couponTable.page" v-model:page-size="couponTable.limit" layout="prev, pager, next" :total="couponTable.total" @size-change="loadCouponList()" @current-change="loadCouponList" />
				</div>
			</div>
			<div v-else>
				<div class="min-h-[400px]">
					<el-empty :image="img('addon/mall/web/coupon-empty.png')" :image-size="300" description="暂无优惠券" />
				</div>
			</div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from  'vue'
import { getAdvInfo } from '@/app/api/index'
import { getMallCouponList, getCoupon} from '@/addon/mall/api/coupon'
import { useRouter } from 'vue-router'
import useMemberStore from '@/stores/member'
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)
const router = useRouter()

// 广告位
const advInfo = ref({})
const getAdvInfoFn = () =>{
	getAdvInfo({ap_key:'ADV_COUPON_INDEX'}).then(res =>{
		advInfo.value = res.data
	})
}
// getAdvInfoFn()

/**
 * 获取优惠券列表
 */
 const couponTable = reactive<any>({
	page: 1,
	limit: 15,
	total: 0,
	loading: true,
	data: []
})
const loadCouponList = (page = 1) => {
	couponTable.page = page
	couponTable.loading = true
	getMallCouponList({
		page: couponTable.page,
        limit: couponTable.limit
	}).then(res =>{
        couponTable.loading = false
		couponTable.total = res.data.total
        couponTable.data = (res.data.data).map(el =>{
            if (!userInfo.value) {
				if (el.sum_count != -1 && el.receive_count === el.sum_count) {
					el.btnType = 'collected'//已领完
				} else {
					el.btnType = 'collecting'//领用
				}
			} else {
				if (el.is_receive) {
					if (el.member_receive_count < el.limit_count) {
						if (el.need_receive) {
							el.btnType = 'collecting'//领用
						} else {
							el.btnType = 'using'//去使用
						}
					} else {
						if (el.need_receive) {
							el.btnType = 'used'//已使用
						} else {
							el.btnType = 'using'//去使用
						}
					}
				} else {
					if (el.sum_count != -1 && el.receive_count === el.sum_count) {
						el.btnType = 'collected'//已领完
					} else {
						el.btnType = 'collecting'//领用
					}
				}
			}
            return el
        })
	})
}
loadCouponList()

// 领取优惠劵
const getCouponFn = (id,index)=>{
    if(!userInfo.value){
        memberStore.logOpen()
       return false
    }
    getCoupon({coupon_id:id,number:1,type:'receive'}).then(res=>{
        couponTable.data[index].btnType = 'using'
    })
}
// 去使用
const toUse = (id) =>{
    router.push(`/goods/list?coupon_id=${id}`)
}
</script>

<style lang="scss" scoped>
.box-shadow{
    box-shadow: 0 3px 20px rgba(0,0,0,.08)
}
.bg-color{
    background: rgba(233,51,35,.1)
}
.coupon-get{
	background-image: url(@/assets/images/coupon/coupon_bg_get.png);
}
.coupon-gouse{
	background-image: url(@/assets/images/coupon/coupon_bg_gouse.png);
}
.coupon-done{
	background-image: url(@/assets/images/coupon/coupon_bg_done.png);
}
.coupon-item{
	background-position: center center;
	background-size: 100% 100%;
	background-repeat: no-repeat;
}
</style>