<template>
    <div class="w-full min-h-[100%]" v-loading="loading">
        <div class="main-container" v-if="Object.values(detail).length">
            <el-breadcrumb class="my-[20px]" :separator-icon="ArrowRight">
				<el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/')">{{ t('index') }}</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="text-[#666]">{{ t('apply') }}</span>
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div ></div>
            <div class="px-[30px] pt-[30px] pb-[20px] bg-[#fff] rounded-[var(--rounded-big)]">
                <div>
                    <div class="mb-[30px] flex items-center cursor-pointer"  v-if="detail.site"  @click="router.push({ path: '/shop/detail', query: { site_id: detail.site.site_id } })">
                        <span class="iconfont icon-Vector-25 text-[#999] mr-[6px]"></span>
                        <span class="text-[14px] text-[#333]">{{ detail.site.site_name }}</span>
                    </div>
                    <div class="flex justify-between items-center mb-[30px]">
                        <div class="flex">
                            <div class="flex items-center flex-shrink-0">
                                <el-image class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]"  :src="img(orderDetail.goods_image_thumb_small ? orderDetail.goods_image_thumb_small : '')" fit="cover">
                                    <template #error>
                                        <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                    </template>
                                </el-image>
                            </div>
                            <div class="ml-[20px] flex flex-col justify-center">
                                <div class="text-[14px] text-[#333] font-500 w-[420px] truncate mb-[14px]">{{ orderDetail.goods_name }}</div>
                                <div class="text-[14px] text-[#333] oppoSans-R" v-if="orderDetail.sku_name">规格：{{ orderDetail.sku_name }}</div>
                            </div>
                        </div>
                        <div class="flex items-center justify-center">
                            <div class="w-[160px] text-[14px] text-[#333] price-font">￥{{orderDetail.price}}</div>
                            <div class="w-[100px] text-[14px] text-[#333] text-right">x{{orderDetail.num}}</div>
                            <div class="w-[160px] text-right text-primary">
                                <span class="text-[12px] price-font">￥</span>
                                <span class="text-[18px] font-600 price-font">{{parseFloat(orderDetail.price * orderDetail.num).toFixed(2)}}</span>
                            </div>
                        </div>    
                    </div>
                </div>
                <el-divider border-style="dashed" />
                <div class="mt-[20px]">
                    <el-form :model="formData" label-width="100px" ref="formRef" :rules="formRules" class="page-form">
                        <el-form-item :label="t('refundType')" prop="refund_type">
                            <el-radio-group v-model="formData.refund_type" class="ml-4">
                                <el-radio :label="1" >{{ t('refundOnly') }}</el-radio>
                                <el-radio :label="2" v-if="orderDetail.goods_type == 'real' && (!orderDetail.delivery_status || orderDetail.delivery_status != 'wait_delivery')">{{ t('returnRefund') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('refundMoney')" prop="apply_money">
                            <div>
                                <el-input v-model.trim="formData.apply_money" clearable :placeholder="t('applyMoneyPlaceholder')" class="input-width" />
                                <div class="text-xs text-[#999] mt-[6px]">
                                    <span>{{ t('maxRefundMoney') }}￥{{ refundMoney.refund_money }}</span>
                                    <span v-if="refundMoney.is_refund_delivery === 1 && Number(refundMoney.refund_delivery_money) > 0" class="ml-[5px]">({{ t('includeDelivery') }} ￥{{ refundMoney.refund_delivery_money }})</span>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('refundReason')" prop="reason">
                            <el-select v-model="formData.reason"  clearable class="input-width" placeholder="请选择">
                                <el-option v-for="(item, index) in reason" :key="index" :label="item" :value="item" />
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="t('refundRemark')" >
                            <el-input v-model.trim="formData.remark" clearable  type="textarea" :placeholder="t('remarkPlaceholder')" class="input-width"   maxlength="100"/>
                        </el-form-item>
                        <el-form-item :label="t('上传凭证')" prop="voucher">
                            <upload-file  v-model="formData.voucher" :limit="9" width="100px" height="100px" ></upload-file>
                        </el-form-item>
                    </el-form>
                </div>
                <div class="ml-[100px]">
                    <el-button type="primary" round class="oppoSans-M" @click="confirm(formRef)">{{ t('submit') }}</el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { moneyFormat } from '@/utils/common'
import { getMallOrderDetail } from '@/addon/mall/api/order'
import { getRefundReason, applyRefund,editRefund,getRefundDetail, getRefundMoney, getRefundMoneyAgain } from '@/addon/mall/api/refund'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
let orderGoodsId =route.query.order_goods_id
let orderId = route.query.order_id
let orderRefundNo = route.query.order_refund_no
let loading = ref(false)
const detail = ref<any>({})
const orderDetail = ref({})
const formData = ref<any>({
    order_id: orderId,
    order_goods_id: orderGoodsId,
    refund_type: 1,
    apply_money: '',
    reason: '',
    voucher: '',
    remark: ''
})
// 获取商品详情
const getMallOrderDetailFn = (id: any) =>{
    loading.value = true
    getMallOrderDetail(id).then(({ data }) => {
        detail.value = data
		detail.value.order_goods.forEach((item,index)=>{
			if(orderGoodsId == item.order_goods_id){
				orderDetail.value = item;
			}
		})
        loading.value = false
    })
} 
let refundMoney = ref({}) // 可退款金额
const getRefundMoneyFn = (id) =>{
    getRefundMoney({order_goods_id: id}).then(res =>{
        refundMoney.value = res.data
        formData.value.apply_money = moneyFormat(refundMoney.value.refund_money)
    })
}


// 获取退款详情
const getRefundDetailFn = (id) =>{
    loading.value = true
    getRefundDetail(id).then(({ data }) => {
        detail.value = data
        orderDetail.value = data.order_goods
        formData.value.order_goods_id = data.order_goods_id;
		formData.value.order_id = data.order_id;
        formData.value.order_refund_no = data.order_refund_no;
		formData.value.remark = data.remark;
		formData.value.reason = data.reason;
        loading.value = false
    })
}

const getRefundMoneyAgainFn = (id) =>{
    getRefundMoneyAgain({order_refund_no: id}).then(res =>{
        refundMoney.value = res.data
        formData.value.apply_money = moneyFormat(refundMoney.value.refund_money)
    })
}

if(orderRefundNo){
    getRefundDetailFn(orderRefundNo)
    getRefundMoneyAgainFn(orderRefundNo)
}else{
    getMallOrderDetailFn(orderId)
    getRefundMoneyFn(orderGoodsId)
}


// 退款原因
const reason = ref([])
const getRefundReasonFn = ()=>{
    getRefundReason().then(({ data }) => {
        reason.value = data
    }).catch()
}
getRefundReasonFn()

const formRef = ref(null)
const formRules = computed(() => {
    return {
        refund_type: [
            { required: true, message: t('refundTypePlaceholder'), trigger: 'change' }
        ],
        apply_money: [
            { required: true, message: t('applyMoneyPlaceholder'), trigger: 'blur' },
            { required: true, validator: validateMoney, trigger: 'blur' }
        ],
        reason: [
            { required: true, message: t('reasonPlaceholder'), trigger: ['blur','change'] }
        ],
    }
})
// 验证退款金额
const validateMoney = (rule, value, callback) => {
    if ( Number(value).toFixed(2) < 0) {
        return callback(new Error(t('退款金额不能为0,保留两位小数')))
    } else if (Number(value)>Number(refundMoney.value.refund_money)) {
        return callback(new Error(t('退款金额不能大于可退款总额')))
    } else {
        return callback()
    }
}
const confirm = async (formEl) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            formData.value.voucher = formData.value.voucher.split(',')
            const save = orderRefundNo ? editRefund : applyRefund
            save(formData.value).then(res => {
                loading.value = false
                setTimeout(()=> {
                    router.push({path:'/order/detail',query: {order_id: formData.value.order_id}})
                }, 1000)
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
.input-width{
    width:540px;
}
</style>