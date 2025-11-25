<template>
     <el-dialog v-model="dialogSelectCoupon" title="选择优惠劵"  width="780px" center :show-close="false" custom-class="coupon">
        <div v-if="couponData[currentActive]">
            <div class="flex flex-wrap justify-between">
                <template v-if="couponData[currentActive].couponList">
                    <div class="w-[350px] h-[110px] flex items-center mb-[20px] cursor-pointer" v-for="(item,index) in couponData[currentActive].couponList" @click="selectCoupon(item)">
                        <div class="w-[100px] h-[110px] bg-primary flex flex-col justify-center items-center relative border-box rounded-[8px] coupon-item">
                            <div class="text-[#fff]">
                                <span class="text-[14px] price-font">¥</span>
                                <span class="text-[26px] price-font">{{item.price }}</span>
                            </div>
                            <div v-if="item.min_condition_money > 0" class="text-[12px] text-[#fff] mt-[4px]">满{{ item.min_condition_money }}可用</div>
                            <div v-else class="text-[12px] text-[#fff] mt-[4px]">无门槛券</div>
                        </div>
                        <div class="w-[250px] h-[110px] bg-[#fff] pl-[15px] relative flex flex-col justify-center rounded-[8px]">
                            <div class="text-[18px] text-[#333]  mb-[20px] truncate  leading-[24px] w-[196px]">{{ item.title }}</div>
                            <div class="text-[14px] text-[#999] leading-[18px]">有效期至{{ item.expire_time ? item.expire_time.slice(0, 10): '' }}</div>
                            <div class="absolute top-[10px] right-[10px]">
                                <div class="iconfont icon-xuanze1 !text-primary" v-if="couponData[currentActive].coupon && couponData[currentActive].coupon.id == item.id"></div>
                                <div class="iconfont icon-checkbox_nol" v-else></div>
                            </div>
                        </div>
                    </div>
                </template>
                <template v-if="couponData[currentActive].disabledCouponList">
                    <div class="w-[350px] h-[110px] flex items-center mb-[20px]" v-for="(item,index) in couponData[currentActive].disabledCouponList">
                        <div class="w-[100px] h-[110px] bg-[#EEA1A1] flex flex-col justify-center items-center relative border-box rounded-[8px] coupon-item">
                            <div class="text-[#fff]">
                                <span class="text-[14px] price-font">¥</span>
                                <span class="text-[26px] font-bold price-font">{{parseInt(item.price)}}</span>
                            </div>
                            <div v-if="item.min_condition_money > 0" class="text-[12px] text-[#fff] mt-[4px]">满{{ item.min_condition_money }}可用</div>
                            <div v-else class="text-[12px] text-[#fff] mt-[4px]">无门槛券</div>
                        </div>
                        <div class="w-[250px] h-[110px] bg-[#fff] pl-[15px] relative flex flex-col justify-center rounded-[8px]">
                            <div class="text-[18px] text-[#666]  mb-[20px] truncate  leading-[24px] w-[196px]">{{ item.title }}</div>
                            <div class="text-[14px] text-[#999] leading-[18px]">有效期至{{ item.expire_time ? item.expire_time.slice(0, 10): '' }}</div>
                        </div>
                    </div>
                </template>
            </div>
        </div>
        <template #footer>
            <div class="dialog-footer">
                <el-button  @click="dialogSelectCoupon = false">取消</el-button>
                <el-button type="primary" class="ml-[12px]" @click="confirm">确认</el-button>
            </div>
        </template>
     </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { orderCoupon } from '@/addon/mall/api/order'
const prop = defineProps({
    orderKey: {
        type: Object,
        default: ()=>{}
    }
})
const currentActive = ref(0)
let dialogSelectCoupon = ref(false)
let couponData =ref({}) 
const emits = defineEmits(['confirm'])
if (prop.orderKey && !Object.keys(couponData.value).length ) {
    orderCoupon({body: prop.orderKey }).then(({data}) => {
            for(let key in data){
                couponData.value[key] = {}
                couponData.value[key].couponList = []
                couponData.value[key].disabledCouponList = []
                if(data[key].length){
                    data[key].forEach(item => {
                        item.is_normal  ?  couponData.value[key].couponList.push(item) : couponData.value[key].disabledCouponList.push(item)
                    })
                    if(couponData.value[key].couponList.length){
                        couponData.value[key].coupon = couponData.value[key].couponList[0]
                    }
                }
            }
            emits('confirm', couponData.value)
    }).catch()
}
const open = (data:any)=> {
    let coupon_id = data.discount.coupon_id
    currentActive.value = data.site_id
	if(coupon_id){
        for(let i=0;i<couponData.value[currentActive.value].couponList.length;i++){
            if(couponData.value[currentActive.value].couponList[i].id == coupon_id){
                couponData.value[currentActive.value].coupon = couponData.value[currentActive.value].couponList[i];
                break;
            }
        }
	}
    dialogSelectCoupon.value = true
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
    dialogSelectCoupon.value = false
}
defineExpose({
    open,
    couponData: couponData.value
})
</script>

<style lang="scss" scoped>
.line2-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
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