<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10" :closeable="true">
		<view @touchmove.prevent.stop class="popup-common">
			<view class="title">请选择优惠券</view>
			<scroll-view scroll-y="true" class="h-[50vh] pt-[10rpx]">
				<view class="px-[var(--popup-sidebar-m)] pb-[30rpx] pt-0 text-sm">
					<template v-if="couponList">
						<view class="mt-[var(--top-m)] px-[var(--pad-sidebar-m)] py-[var(--pad-top-m)] border-1 border-[#eee] border-solid rounded-[20rpx]"
							:class="{'!border-[var(--primary-color)] bg-[var(--primary-color-light2)]': coupon && coupon.id == item.id}"
							v-for="(item,index) in couponList"
							@click="selectCoupon(item)">
							<view class="flex border-0 !border-b border-[#eee] border-dashed pb-[20rpx]" :class="{'!border-[var(--primary-color)]': coupon && coupon.id == item.id }">
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
			</scroll-view>
			<view class="btn-wrap">
				<button class="primary-btn-bg btn" @click="confirm">确认</button>
			</view>
		</view>
    </u-popup>
</template>

<script setup lang="ts">
    import { ref } from 'vue'
    import { orderPlatformCoupon } from '@/addon/mall/api/order'

    const prop = defineProps({
        orderKey: {
            type: Object,
            default: ()=>{}
        }
    })
    const show = ref(false)
    let couponList =ref<object[]>([]) 
    const coupon = ref<null | object>(null)
    const emits = defineEmits(['confirm'])
    const orderPlatformCouponFn = ()=>{
        if (prop.orderKey) {
            orderPlatformCoupon({ body: prop.orderKey }).then(({ data }) => {
                couponList.value = data
                // if (couponList.value.length) {
                //     coupon.value = couponList.value[0]
                //     emits('confirm', coupon.value)
                // }
            }).catch()
        }
    }
 
    const open = (coupon_id:any)=> {
        show.value = true
	    if(coupon_id){
            for(let i=0;i<couponList.value.length;i++){
	            if(couponList.value[i].id == coupon_id){
                    coupon.value = couponList.value[i];
	                break;
	            }
	        }
	    }
    }

    const selectCoupon = (data: object)=> {
        if (coupon.value) {
            coupon.value = coupon.value.id != data.id ? data : null
        } else {
            coupon.value = data
        }
    }

    const confirm = ()=> {
        emits('confirm', coupon.value)
        show.value = false
    }
    defineExpose({
    	open,
        orderPlatformCouponFn,
        couponList: couponList
    })
</script>

<style lang="scss" scoped></style>
