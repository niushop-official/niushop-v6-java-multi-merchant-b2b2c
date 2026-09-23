<template>
    <el-dialog v-model="dialogPlatformCoupon" title="选择优惠劵"  width="780px" center :show-close="false" custom-class="coupon">
        <div>
            <div class="flex flex-wrap justify-between">
                <template v-if="couponList">
                    <div class="w-[350px] h-[110px] flex items-center mb-[20px] cursor-pointer" v-for="(item,index) in couponList" @click="selectCoupon(item)">
                        <div class="w-[100px] h-[110px] bg-primary flex flex-col justify-center items-center relative border-box rounded-[8px] coupon-item">
                            <div class="text-[#fff]">
                                <span class="text-[14px] price-font">¥</span>
                                <span class="text-[26px] price-font">{{item.price}}</span>
                            </div>
                            <div v-if="item.min_condition_money > 0" class="text-[12px] text-[#fff] mt-[4px]">满{{ item.min_condition_money }}可用</div>
                            <div v-else class="text-[12px] text-[#fff] mt-[4px]">无门槛券</div>
                        </div>
                        <div class="w-[250px] h-[110px] bg-[#fff] pl-[15px] relative flex flex-col justify-center rounded-[8px]">
                            <div class="text-[18px] text-[#333] font-500  mb-[20px] truncate leading-[24px] w-[196px]">{{ item.title }}</div>
                            <div class="text-[14px] text-[#999] leading-[18px]">有效期至{{ item.expire_time ? item.expire_time.slice(0, 10): '' }}</div>
                            <div class="absolute top-[10px] right-[10px]">
                                <div class="iconfont icon-xuanze1 !text-primary" v-if="coupon && coupon.id == item.id"></div>
                                <div class="iconfont icon-checkbox_nol" v-else></div>
                            </div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button  @click="dialogPlatformCoupon = false">取消</el-button>
                <el-button type="primary" class="ml-[12px]" @click="confirm">确认</el-button>
            </div>
        </template>
     </el-dialog>
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
const dialogPlatformCoupon = ref(false)
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
    dialogPlatformCoupon.value = true
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
    dialogPlatformCoupon.value = false
}
defineExpose({
    open,
    orderPlatformCouponFn,
    couponList: couponList
})
</script>

<style lang="scss" scoped>
.coupon-item{
    :before{
        content: '';
        display: block;
        position: absolute;
        top:-7px;
        right: -7px;
        width: 14px;
        height: 14px;
        border-radius: 50%;
        background-color: #f5f5f5;
        z-index: 10;
    }
    :after{
        content: '';
        display: block;
        position: absolute;
        bottom: -7px;
        right: -7px;
        width: 14px;
        height: 14px;
        border-radius: 50%;
        background-color: #f5f5f5;
        z-index: 10;
    }
}
</style>