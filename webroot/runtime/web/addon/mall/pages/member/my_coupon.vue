<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="full">
            <div>
                <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
                    <el-tab-pane :label="t('unused')" name="1"></el-tab-pane>
                    <el-tab-pane :label="t('hasUsed')" name="2"></el-tab-pane>
                    <el-tab-pane :label="t('expired')" name="3"></el-tab-pane>
                </el-tabs>
                <div class="min-h-[300px]" v-loading="couponTableData.loading">
					
					<div class="grid grid-cols-3 gap-x-15px gap-y-15px" v-if="couponTableData.data.length && !couponTableData.loading" >
					    <template v-for="(item,index) in couponTableData.data" :key="index">
					        <div class="w-[292px] h-[126px] flex items-center justify-between rounded-[8px] box-border coupon-item" :class="{'coupon-use': activeName  == 1,'coupon-used': activeName  == 2,'coupon-cancel': activeName  == 3}">
								<div class="w-[248px] py-[20px] flex box-border">
									<div class="w-[90px] flex flex-col items-center justify-center">
										<p class="text-[16px] mb-[5px]" :class="{'text-primary': activeName != 3, 'text-[#999]': activeName == 3}">
											<span class="price-font text-[14px] mr-[2px]">￥</span>
											<span class="text-[28px] font-500 price-font">{{item.coupon_price}}</span>
										</p>
										<span class="text-[12px] px-[7px] leading-[20px] text-center rounded-[10px]" :class="{'text-primary bg-[#FFE8E9]': activeName != 3, 'text-[#CFCFCF] bg-[#F0F0F0]': activeName == 3 }">{{item.type_name}}</span>
									</div>
									<div class="flex-1 ">
										<div class="text-[16px] leading-[24px]" v-if="item.min_condition_money === '0.00'">无门槛使用</div>
										<div class="text-[16px] leading-[24px]" v-else>满{{ item.coupon_min_price }}元可用</div>
										<div class="text-[#999] text-[12px] mt-[4px] truncate max-w-[150px]">{{item.title}}</div>
										<div class="mt-[22px] text-[12px] text-[#666] " v-if="activeName != 3">
											<div v-if="item.valid_type == 1">领取之日起<span>{{ item.length || '' }}</span>天内有效</div>
											<div v-else> 有效期至<span>{{ item.expire_time ? item.expire_time.slice(0, 10) : '' }}</span></div>
										</div>
										<div class="mt-[20px] text-[12px] text-[#666] " v-else>
											<div ><span>该优惠券已失效无法使用</span></div>
										</div>
									</div>
								</div>
					        	<div v-if="activeName == 1" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]" @click="toUse(item)">去使用</div>
                                <div v-if="activeName == 2" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]">已使用</div>
                                <div v-if="activeName == 3" class="w-[44px] px-[14px] text-[16px] text-[#fff] cursor-pointer flex items-center leading-[24px]">已过期</div>
					        </div>
					    </template>
					</div>
                    <div class="flex items-center justify-center min-h-[300px] w-full" v-if="!couponTableData.data.length && !couponTableData.loading" >
                        <el-empty :description="t('emptyTips')" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { getMyCouponList } from '@/addon/mall/api/coupon'
import { useRouter } from 'vue-router'

const router = useRouter()
const activeName = ref<any>('1')
// 获取会员列表
const couponTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: []
})

const loadCouponList = (page = 1) =>{
    couponTableData.page = page
    couponTableData.loading = true
    getMyCouponList({
        page: couponTableData.page,
        limit: couponTableData.limit,
        status: activeName.value
    }).then(res =>{
        couponTableData.loading = false
        couponTableData.total = res.data.total
        couponTableData.data = res.data.data
    })
}
loadCouponList()
// 去使用
const toUse = (data: any) =>{
    router.push({ path: '/goods/list', query: { coupon_id: data.coupon_id,site_id: data.site_id }})
}
// 切换状态
const handleClick = (event: any) =>{
    activeName.value = event
    loadCouponList()
}
</script>

<style lang="scss" scoped>
.box-shadow{
    box-shadow: 0 3px 20px rgba(0,0,0,.08);
}

.coupon-use{
	background-image: url(@/assets/images/member/coupon_use.png);
}
.coupon-used{
	background-image: url(@/assets/images/member/coupon_used.png);
}
.coupon-cancel{
	background-image: url(@/assets/images/member/coupon_cancel.png);
}

.coupon-item{
	background-position: center center;
	background-size: 100% 100%;
	background-repeat: no-repeat;
}
</style>