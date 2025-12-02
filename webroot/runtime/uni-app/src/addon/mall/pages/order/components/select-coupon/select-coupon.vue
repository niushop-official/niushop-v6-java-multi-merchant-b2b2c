<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10" :closeable="true">
		<view @touchmove.prevent.stop v-if="couponData[currentActive]" class="popup-common">
			<view class="title">请选择优惠券</view>
			<view class="-mt-[20rpx]" >
				<u-tabs :list="tabs" @click="switchTab" :current="current" itemStyle="width:50%;height:88rpx;box-sizing: border-box;font-size: 28rpx;" :activeStyle="{
					color: 'var(--primary-color)'
				}" lineColor="var(--primary-color)"></u-tabs>
			</view>
			<scroll-view scroll-y="true" class="h-[50vh] pt-[10rpx]">
				<view class="px-[var(--popup-sidebar-m)] pb-[30rpx] pt-0 text-sm" v-show="current == 0">
					<template v-if="couponData[currentActive].couponList">
						<view class="mt-[var(--top-m)] px-[var(--pad-sidebar-m)] py-[var(--pad-top-m)] border-1 border-[#eee] border-solid rounded-[20rpx]"
							:class="{'!border-[var(--primary-color)] bg-[var(--primary-color-light2)]': couponData[currentActive].coupon && couponData[currentActive].coupon.id == item.id}"
							v-for="(item,index) in couponData[currentActive].couponList"
							@click="selectCoupon(item)">
							<view class="flex border-0 !border-b border-[#eee] border-dashed pb-[20rpx]" :class="{ '!border-[var(--primary-color)]': couponData[currentActive].coupon && couponData[currentActive].coupon.id == item.id }">
								<view class="flex-1 w-0">
									<view class="text-[30rpx] mb-[20rpx] font-500">{{ item.title }}</view>
									<view class="text-[24rpx] text-[var(--text-color-light6)]" v-if="item.min_condition_money > 0">满{{ item.min_condition_money }}可用</view>
									<view class="text-[24rpx] text-[var(--text-color-light6)]" v-else>无门槛券</view>
								</view>
								<view class="ftext-[36rpx] price-font"><text class="text-xs mr-[2rpx]">￥</text>{{ item.price }}</view>
							</view>
							<view class="pt-[20rpx] text-[24rpx] text-[var(--text-color-light6)]">{{ item.create_time }} ~ {{ item.expire_time }}期间有效</view>
						</view>
					</template>
				</view>
				<view class="px-[var(--popup-sidebar-m)] pb-[30rpx] pt-0 text-sm" v-show="current == 1">
					<template v-if="couponData[currentActive].disabledCouponList">
						<view class="mt-[var(--top-m)] px-[var(--pad-sidebar-m)] py-[var(--pad-top-m)] border-1 !border-[#eee] border-solid rounded-[var(--rounded-mid)] bg-[var(--temp-bg)]" v-for="item in couponData[currentActive].disabledCouponList">
							<view class="flex border-0 !border-b !border-[#ddd] border-dashed pb-[20rpx]">
								<view class="flex-1 w-0">
									<view class="text-[30rpx] mb-[20rpx] font-500">{{ item.title }}</view>
									<view class="text-[24rpx] text-[var(--text-color-light9)]" v-if="item.min_condition_money > 0">满{{ item.min_condition_money }}可用</view>
									<view class="text-[24rpx] text-[var(--text-color-light9)]" v-else>无门槛券</view>
								</view>
								<view class="text-[36rpx] price-font"><text class="text-xs mr-[2rpx]">￥</text>{{ item.price }}</view>
							</view>
							<view class="pt-[20rpx] text-[24rpx]  text-[var(--text-color-light9)]">{{ item.create_time }} ~ {{ item.expire_time }}期间有效</view>
							<view class="text-[24rpx] pt-[10rpx] flex  text-[var(--text-color-light9)]">
								不可用原因：{{ item.error }}
							</view>
						</view>
					</template>
				</view>
			</scroll-view>
			<view class="btn-wrap">
                <button class="primary-btn-bg btn" @click="confirm">确认</button>
			</view>
		</view>
    </u-popup>
</template>

<script setup lang="ts">
    import { ref, watch, computed } from 'vue'
    import { orderCoupon } from '@/addon/mall/api/order'

    const prop = defineProps({
        orderKey: {
            type: Object,
            default: ()=>{}
        }
    })
    const current = ref(0)
    const currentActive = ref(0)
    const show = ref(false)
    let couponData =ref({}) 
    const emits = defineEmits(['confirm'])
    if (prop.orderKey && !Object.keys(couponData.value).length ) {
        orderCoupon({ body: prop.orderKey }).then(({ data }) => {
            for (let key in data) {
                couponData.value[key] = {}
                couponData.value[key].couponList = []
                couponData.value[key].disabledCouponList = []
                if (data[key].length) {
                    data[key].forEach(item => {
                        item.is_normal ? couponData.value[key].couponList.push(item) : couponData.value[key].disabledCouponList.push(item)
                    })
                    if (couponData.value[key].couponList.length) {
                        couponData.value[key].coupon = couponData.value[key].couponList[0]
                    }
                }
            }
            emits('confirm', couponData.value)
        }).catch()
    }
    const tabs = computed(() => {
        return [
            { name: `可用优惠券（${ couponData.value[currentActive.value].couponList.length}）`, key: 'normal' },
            { name: `不可用优惠券（${couponData.value[currentActive.value].disabledCouponList.length}）`, key: 'disabled' }
        ]
    })

    const switchTab = (event)=> {
        current.value = event.index
    }
   
    const open = (data:any)=> {
        show.value = true
        let coupon_id = data. discount.coupon_id
        currentActive.value = data.site_id
	    if(coupon_id){
            for(let i=0;i<couponData.value[currentActive.value].couponList.length;i++){
                if(couponData.value[currentActive.value].couponList[i].id == coupon_id){
                    couponData.value[currentActive.value].coupon = couponData.value[currentActive.value].couponList[i];
                    break;
                }
            }
	    }
    }

    const selectCoupon = (data: object)=> {
        if (couponData.value[currentActive.value].coupon) {
            couponData.value[currentActive.value].coupon = couponData.value[currentActive.value].coupon.id != data.id ? data : null
        } else {
            couponData.value[currentActive.value].coupon = data
        }
    }

    const confirm = ()=> {
        emits('confirm', couponData.value)
        show.value = false
    }

    defineExpose({
    	open,
        couponData: couponData.value
    })
</script>

<style lang="scss" scoped></style>
