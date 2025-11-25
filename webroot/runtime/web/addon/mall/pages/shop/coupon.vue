<template>
    <div class="bg-[#f5f5f5]">
        <div class="main-container">
            <div class="pt-[10px] flex justify-between mt-[25px]">
                <div class="w-[224px] flex flex-col">
                    <info />
                    <category />
                </div>
                <div class="flex-1 pb-[30px] px-[15px] pt-[15px] ml-[18px] bg-[#fff] rounded-[var(--rounded-big)]" v-loading="couponTable.loading">
                    <div v-if="couponTable.data.length">
                        <div class="grid grid-cols-3 gap-x-15px gap-y-15px" >
                            <template v-for="(item,index) in couponTable.data">
								<div class="w-[292px] h-[126px] flex items-center justify-between rounded-[8px] box-border coupon-item" :class="{'coupon-get': item.btnType === 'collecting','coupon-gouse': item.btnType === 'using', 'coupon-done': item.btnType === 'collected' || item.btnType === 'used'}">
									<div class="w-[248px] py-[20px] flex box-border">
										<div class="w-[90px] flex flex-col items-center justify-center">
											<p class="text-[16px]" :class="{'text-[#999]': item.btnType == 'collected', 'text-primary': item.btnType != 'collected' }">
												<span class="price-font">￥</span>
												<span class="text-[28px] font-500 price-font">{{item.coupon_price}}</span>
											</p>
											<span class="text-primary text-[12px] px-[7px] leading-[20px] text-center rounded-[10px] bg-color mt-[5px]" :class="{'text-[#CFCFCF] bg-[#F0F0F0]': item.btnType == 'collected', 'text-primary bg-[#FFE8E9]': item.btnType != 'collected' } ">{{item.type_name}}</span>
										</div>
										<div class="flex-1 ">
											<div class="text-[16px] leading-[24px]" v-if="item.min_condition_money === '0.00'">无门槛使用</div>
											<div class="text-[16px] leading-[24px]" v-else>满{{ item.coupon_min_price }}元可用</div>
											<div class="text-[#999] text-[12px] mt-[4px] truncate max-w-[150px]">{{item.title}}</div>
											<div class="mt-[22px] text-[12px] text-[#666]">
												<div v-if="item.valid_type == 1">领取之日起<span>{{ item.length || '' }}</span>天内有效</div>
												<div v-else> 有效期至<span>{{ item.valid_end_time ? item.valid_end_time.slice(0, 10) : '' }}</span></div>
											</div>
										</div>
									</div>
									<div v-if="item.btnType === 'collecting'" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]" @click="getCouponFn(item.id,index)">立即领取</div>
                                    <div v-if="item.btnType === 'using'" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]" @click="toUse(item)">去使用</div>
                                    <div v-if="item.btnType === 'collected'" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]">已领完</div>
                                    <div v-if="item.btnType === 'used'" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]">已使用</div>
								</div>
                            </template>
                        </div>
                        <div class="mt-[20px] flex justify-center">
                            <el-pagination background  small hide-on-single-page v-model:current-page="couponTable.page" v-model:page-size="couponTable.limit" layout="prev, pager, next" :total="couponTable.total" @size-change="loadCouponList()" @current-change="loadCouponList" />
                        </div>
                    </div>
                    <div v-else>
                        <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')" />
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref,reactive,computed } from 'vue'
import category from './components/category/index.vue'
import info from './components/info/index.vue'
import { getMallCouponList, getCoupon} from '@/addon/mall/api/coupon'
import useMemberStore from '@/stores/member'
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

const memberStore = useMemberStore()

const userInfo = computed(() => memberStore.info)

/**
 * 获取优惠券列表
 */
 const couponTable = reactive<any>({
	page: 1,
	limit: 15,
	total: 0,
	loading: true,
	data: [],
    searchParam:{
        site_id:''
    }
})
couponTable.searchParam.site_id = route.query.site_id
const loadCouponList = (page = 1) => {
	couponTable.page = page
	couponTable.loading = true
	getMallCouponList({
		page: couponTable.page,
        limit: couponTable.limit,
        ...couponTable.searchParam
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
//领用
const getCouponFn = (id,index) =>{
    if(!userInfo.value){
        memberStore.logOpen()
       return false
    }
    getCoupon({
        coupon_id:id,
        number:1,
        type:'receive'
    }).then(res =>{
        couponTable.data[index].is_receive = true
        couponTable.data[index].btnType = 'using'//去使用
    })
}

// 去使用
const toUse = (data:any) =>{
    router.push({path:'/shop/detail',query:{site_id:data.site_id,coupon_id:data.id}})
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
	background-image: url(@/assets/images/member/coupon_use.png);
}
.coupon-gouse{
	background-image: url(@/assets/images/member/coupon_used.png);
}
.coupon-done{
	background-image: url(@/assets/images/member/coupon_cancel.png);
}
.coupon-item{
	background-position: center center;
	background-size: 100% 100%;
	background-repeat: no-repeat;
}
</style>