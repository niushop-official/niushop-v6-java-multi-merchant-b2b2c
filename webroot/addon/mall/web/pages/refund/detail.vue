<template>
    <div class="w-full min-h-[100%]" v-loading="loading">
        <div class="main-container" v-if="Object.values(detail).length">
            <el-breadcrumb class="my-[20px]" :separator-icon="ArrowRight">
				<el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/')">{{ t('index') }}</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/refund/list')">{{ t('refundList') }}</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item >
                    <span class="text-[#666]">{{ t('refunddetail') }}</span>
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div class="">
                <div class="px-[30px] py-[18px] mb-[20px] bg-[#fff] rounded-[var(--rounded-big)]">
                    <div class="text-[20px] font-500 text-[#333] mb-[15px]" v-if="detail.status_name">{{ detail.status_name }}</div>
                    <div  v-if="['1'].indexOf(detail.status) != -1" class="text-[#666] text-[14px] font-400 oppoSans-R">退款前请与商家协商一致,有助于更好的处理售后问题。</div>
                    <div v-if="['2','6','7'].indexOf(detail.status) != -1" class="text-[#666] text-[14px] font-400 oppoSans-R">商家已同意您的退款,若您已收到商品请您尽快退回，感谢您对我们的支持!</div>
                    <div v-if="['4'].indexOf(detail.status) != -1" class="text-[#666] text-[14px] font-400 oppoSans-R">商家确认收货后进行退款,请您耐心等待，若有异议请联系平台客服处理!</div>
                    <div  v-if="['3','5'].indexOf(detail.status) != -1" class="text-[#666] text-[14px] font-400 oppoSans-R">{{ detail.shop_reason }}</div>
                    <div v-if="['8'].indexOf(detail.status) != -1" class="text-[#666] text-[14px] font-400 oppoSans-R">商家已为您退款，若商家已寄出商品请您尽快寄回，感谢您对我们的支持!</div>
                    <div v-if="['-1'].indexOf(detail.status) != -1" class="text-[#666] text-[14px] font-400 oppoSans-R">此退款单已关闭，若对此订单存在疑问，请联系商家协商处理。</div>
                </div>
                <div class="bg-[#fff] p-[30px] mb-[20px] rounded-[var(--rounded-big)]">
                    <div class="text-[16px] text-[#333] mb-[30px]">{{ t('refundInfo') }}</div>
                    <div class="flex justify-between">
                        <div class="w-[540px] text-[#333] text-[14px]">
                            <div class="mb-[20px] oppoSans-R">{{ t('refundNo') }}{{ detail.order_refund_no }}</div>
                            <div class="mb-[20px] oppoSans-R">{{ t('applyTime') }}{{ detail.create_time }}</div>
                            <div class="mb-[20px] oppoSans-R">{{ t('refundMethod') }}{{ detail.refund_type_name }}</div>
                            <div class="oppoSans-R" v-if="detail.status == 3||detail.status == 5">{{ t('merchantRefuse') }}{{ detail.shop_reason }}</div>
                        </div>
                        <div class="w-[1px] h-[102px] bg-[#eee]"></div>
                        <div class="ml-[30px] w-[565px] text-[#333] text-[14px]">
                            <div class="mb-[20px] oppoSans-R">{{ t('refundReason') }}{{ detail.reason || '--' }}</div>
                             <div class="mb-[20px] oppoSans-R">
                                <span>{{ t('refundMoney') }}</span>
                                <span class="price-font">￥{{ detail.status == 1 ? parseFloat(detail.apply_money).toFixed(2) : parseFloat(detail.money).toFixed(2) }}</span>
                            </div>
                            <div class="mb-[20px] oppoSans-R">{{ t('refundRemark') }}{{ detail.remark }}</div>
                            <div class="oppoSans-R flex items-center">
                                <span>{{ t('退款凭证：') }}</span>
                                <div class="flex">
                                    <el-image class="w-[45px] h-[45px] rounded-[var(--rounded-small)] mr-[10px]"  :src="img(item)" fit="cover" :preview-src-list="detail.voucherList" :hide-on-click-modal="true" v-for="(item,index) in detail.voucher" :key="index"></el-image>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="p-[30px] mb-[20px] bg-[#fff] rounded-[var(--rounded-big)]">
                    <div class="mb-[30px] text-[16px] text-[#333] leading-[20px]">{{ t('goodsInfo') }}</div>
                    <div class="flex justify-between items-center mb-[30px]">
                        <div class="flex">
                            <div class="flex items-center flex-shrink-0">
                                <el-image class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]"  :src="img(detail.order_goods.goods_image  ? detail.order_goods.goods_image  : '')" fit="cover">
                                    <template #error>
                                        <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                    </template>
                                </el-image>
                            </div>
                            <div class="ml-[20px] flex flex-col justify-center">
                                <div class="text-[14px] text-[#333] font-500 w-[420px] truncate mb-[14px]">{{ detail.order_goods.goods_name }}</div>
                                <div class="text-[14px] text-[#333] oppoSans-R" v-if="detail.order_goods.sku_name"> 规格：{{ detail.order_goods.sku_name }}</div>
                            </div>
                        </div>
                        <div class="flex items-center justify-center">
                            <div class="w-[160px] text-[14px] text-[#333] oppoSans-R">￥{{detail.order_goods.price}}</div>
                        </div>    
                    </div>
                </div>
                <div class="px-[30px] bg-[#fff] py-[18px] mb-[20px] rounded-[var(--rounded-big)]" v-if="detail.refund_log.length">
                    <div class="flex justify-between items-center leading-[26px] text-[14px]">
                        <span>{{ t('consultRecord') }}</span>
                        <div class="text-[var(--el-color-primary)] cursor-pointer" @click="recordShow = !recordShow">{{ t('consultRecord') }}
                            <i class="iconfont icon-xiajiantou !text-[14px]" v-if="!recordShow"></i>
                            <i class="iconfont icon-xiangshangjiantou !text-[14px]" v-else></i>
                        </div>
                    </div>
                    <div class="mt-[10px]" v-show="recordShow">
                        <div class="flex" v-for="(item, index) in detail.refund_log" :key="index">
                            <div>
                                <div class="w-[12px] h-[12px] flex items-center bg-[#e4e7ed]  rounded-[999px]"></div>
                                <div class="w-[2px] h-[70px] bg-[#e4e7ed] mx-auto"  v-if="index + 1 != detail.refund_log.length"></div>
                            </div>
                            <div>
                                <div class="leading-[1] ml-[18px] text-[13px] text-[#909399]">
                                    {{item.create_time}}
                                </div>
                                <div class="leading-[1] ml-[18px] text-[14px] mt-[8px] font-bold">
                                    {{item.main_name}}
                                </div>
                                <div class="leading-[1] ml-[18px] text-[14px] mt-[8px]">
                                    {{item.type_name || '--'}}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="px-[30px] py-[18px] bg-[#fff] flex justify-end items-center rounded-[var(--rounded-big)]"  v-if="['2','3','5'].indexOf(detail.status) != -1 || ['6','7','8','-1'].indexOf(detail.status) == -1">
                    <el-button v-if="['6','7','8','-1'].indexOf(detail.status) == -1"  type="primary" round class="oppoSans-M" @click="refundConfirm(detail)" >{{ t('refundApply') }}</el-button>
                    <el-button v-if="['3'].indexOf(detail.status) != -1" type="primary" round class="oppoSans-M"  @click="refundBtnFn(detail,'edit')">{{ t('edit') }}</el-button>
                    <el-button v-if="['2'].indexOf(detail.status) != -1" type="primary" round class="oppoSans-M" @click="trackingFn(detail,false)">{{ t('writeLogistic') }}</el-button>
                    <el-button v-if="['5'].indexOf(detail.status) != -1" type="primary"  round class="oppoSans-M" @click="trackingFn(detail,true)">{{ t('editLogistic') }}</el-button>
                </div>
            </div>
        </div>
        <logistics-tracking ref="trackingRef" @complete="refundDetailFn"></logistics-tracking>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { getRefundDetail, closeRefund} from '@/addon/mall/api/refund'
import logisticsTracking from '@/addon/mall/pages/refund/components/logistics-tracking.vue'

const route = useRoute()
const router = useRouter()
let orderRefundNo = route.query.order_refund_no
const recordShow = ref(false)
let detail = ref<any>({})
let loading = ref(true)

const refundDetailFn = () => {
	loading.value = true
	getRefundDetail(orderRefundNo).then((res:any) => {
		detail.value = res.data;
        detail.value.voucher = detail.value.voucher.filter(item => item && item.trim())
        if(detail.value.voucher.length){
            detail.value.voucherList = detail.value.voucher.map(item=>{
                return img(item)
            })
        }
		loading.value = false;
	}).catch(() => {
		loading.value = false;
	})
}
refundDetailFn()

// 申请退款
const refundConfirm = (data)=>{
    ElMessageBox.confirm('撤销之后本次申请将会关闭,如后续仍有问题可再次发起申请。', '提示',
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        closeRefund(data.order_refund_no).then((res) => {
			refundDetailFn(orderRefundNo)
		}).catch(() => {
			
		})
    })
}
// 物流弹框
const trackingRef = ref(null)
const trackingFn = (data,status) =>{
    trackingRef.value.setFormData(data.order_refund_no,status)
    trackingRef.value.showDialog = true
}
const refundBtnFn = (data, type) => {
	if(type == 'edit'){
        router.push(`/refund/apply?order_refund_no=${data.order_refund_no}`)
	}
}
</script>

<style lang="scss" scoped>
/* 多行超出隐藏 */
.multi-hidden {
	word-break: break-all;
	text-overflow: ellipsis;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}
</style>