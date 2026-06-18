<template>
    <div class="w-full min-h-[100%]" v-loading="loading">
        <div class="main-container" v-if="Object.values(detail).length">
            <el-breadcrumb class="my-[20px]" :separator-icon="ArrowRight">
				<el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/')">{{ t('index') }}</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/order/list')">{{ t('myOrder') }}</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item >
                    <span class="text-[#666]">{{ t('orderDetail') }}</span>
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div>
                <div class="bg-[#fff] p-[30px] mb-[20px] rounded-[var(--rounded-big)]">
                    <div class="text-[16px] text-[#333]">
                        <span>{{ t('orderStatus') }}</span>
                        <span>{{ detail.status_name.name }}</span>
                    </div>
                    <div class="mt-[40px]" v-if="['1','2','3','5'].indexOf(detail.status) != -1">
                        <el-steps  style="flex-basis: 25%;" :active="current" finish-status="success" align-center>
                            <el-step :title="t('waitPay')" />
                            <el-step :title="t('waitSend')" />
                            <el-step :title="t('waitReceive')" />
                            <el-step :title="t('finish')" />
                        </el-steps>
                    </div>
                </div>
                <div class="bg-[#fff] p-[30px] mb-[20px] rounded-[var(--rounded-big)]">
                    <div class="text-[16px] text-[#333] mb-[30px]">{{ t('orderInfo') }}</div>
                    <div class="flex justify-between ">
                        <div class="w-[540px] text-[#333] text-[14px]">
                            <div class="mb-[20px]">{{ t('orderNo') }}{{ detail.order_no }}</div>
                            <div class="mb-[20px]">{{ t('createTime') }}{{ detail.create_time }}</div>
                            <div class="mb-[20px]" v-if="detail.pay">{{ t('payTypeName') }}{{ detail.pay.type_name }}</div>
                            <div class="mb-[20px]" v-if="detail.pay">{{ t('payTime') }}{{ detail.pay.pay_time }}</div>
                            <div class="mb-[20px]" v-if="detail.delivery_type == 'virtual' && detail.taker_mobile">{{ t('mobile') }}{{ detail.taker_mobile }}</div>
                        </div>
                        <div class="w-[1px] h-[124px] bg-[#eee]" v-if="detail.delivery_type != 'virtual'"></div>
                        <div class="ml-[30px] w-[565px] text-[#333] text-[14px]" v-if="detail.delivery_type != 'virtual'">
                            <div class="mb-[20px]">{{ t('name') }}{{ detail.taker_name }}</div>
                            <div class="mb-[20px]">{{t('mobile')}}{{ detail.taker_mobile }}</div>
                            <div>{{ t('address')}}{{ detail.taker_full_address }}</div>
                        </div>
                    </div>
                </div>
                <div class="bg-[#fff] p-[30px] mb-[20px] rounded-[var(--rounded-big)]">
                    <div class="mb-[30px] flex items-center cursor-pointer site"  v-if="detail.site"  @click="router.push({ path: '/shop/detail', query: { site_id: detail.site.site_id } })">
                        <span class="iconfont icon-Vector-25 text-[#999] mr-[6px] site-style"></span>
                        <span class="text-[14px] text-[#333] site-style">{{ detail.site.site_name }}</span>
                    </div>
                    <div>
                        <div class="mb-[30px]" v-for="(item,index) in detail.order_goods" :key="index">
                            <div class="flex justify-between items-center">
                                <div class="flex cursor-pointer" @click="router.push({ path: '/goods/detail', query: { id: item.goods_id } })">
                                    <div class="flex items-center shrink-0">
                                        <el-image class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]"  :src="img(item.goods_image_thumb_small ? item.goods_image_thumb_small : '')" fit="cover">
                                            <template #error>
                                                <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                            </template>
                                        </el-image>
                                    </div>
                                    <div class="ml-[20px] flex flex-col justify-center">
                                        <div class="text-[14px] text-[#333] font-500 w-[420px] truncate mb-[14px]">{{ item.goods_name }}</div>
                                        <div class="text-[14px] text-[#333] oppoSans-R" v-if="item.sku_name"> 规格：{{ item.sku_name }}</div>
                                    </div>
                                </div>
                                <div class="flex items-center justify-center">
                                    <div class="w-[160px] text-[14px] text-[#333] oppoSans-R">￥{{item.price}}</div>
                                    <div class="w-[100px] text-[14px] text-[#333] text-right oppoSans-R">x{{item.num}}</div>
                                </div>
                                <div class="w-[80px]">
                                    <div v-if="(item.status != 1) || (item.is_enable_refund == 1)">
                                        <el-button round class="w-[80px]  !h-[32px] !border-[#ccc] !text-[#333] oppoSans-M" v-if="item.status != 1" @click="refundDetail(item.order_refund_no)">{{ t('refund') }}</el-button>
                                        <el-button round class="w-[80px]  !h-[32px] !border-[#ccc] !text-[#333] oppoSans-M" v-else-if="item.is_enable_refund == 1"  @click="applyRefund(item.order_goods_id)">{{ t('applyRefund') }}</el-button>
                                    </div>
                                </div>
                            </div>
                            <!-- 商品的万能表单信息 -->
                            <div :class="{'diy-form-wrap mt-[30px]' : goodsDiyFormData.length }" v-if="item.form_record_id">
                                <diy-form-detail :record_id="item.form_record_id" @callback="getGoodsDiyFormDetailCallback" />
                            </div>
                         </div>
                    </div>
                    <el-divider border-style="dashed" />
                    <div>
                        <!-- 待付款订单的万能表单信息 -->
                        <diy-form-detail v-if="detail.form_record_id" :record_id="detail.form_record_id" @callback="getOrderDiyFormDetailCallback" />
                        <div class="flex items-center justify-between text-[14px] text-[#666] mb-[10px] cursor-pointer" v-if="detail.gift_order_goods && detail.gift_order_goods.length > 0">
                            <span>{{ t('赠品') }}</span>
                            <view class="flex items-center text-[28rpx]" @click="giftGooodsRef.open(detail.gift_order_goods)">
                                <view class="text-[24rpx] flex-1 flex gap-x-[8px] justify-end text-primary">
                                    <view v-for="(cItem, index) in detail.gift_order_goods" :key="index" class="flex">
                                        <el-image class="w-[30px] h-[30px] rounded-[var(--rounded-small)]" :src="img(cItem.sku_image)" model="aspectFill" v-if="index < 5">
                                             <template #error>
                                                <img src="@/assets/images/goods_default.png" class="w-[30px] h-[30px] rounded-[var(--rounded-small)]">
                                            </template>
                                        </el-image>
                                    </view>
                                </view>
                                <view class="text-[28rpx] ml-[4px]">共<text class="price-font">{{ getGiftNum(detail.gift_order_goods) }}</text>件</view>
                                <span class="iconfont icon-youV6xx !text-[16px]"></span>
                            </view>
                        </div>

                        <div class="flex items-center text-[14px] text-[#666] mb-[20px]">
                            <span>{{ t('deliveryStyle') }}</span>
                            <span class="ml-[auto]">{{ detail.delivery_type_name }}</span>
                        </div>
                        <div class="flex items-center text-[14px] text-[#666]">
                            <span>{{ t('remark') }}</span>
                            <span class="ml-[auto]">{{ detail.member_remark ? detail.member_remark : '-' }}</span>
                        </div>
                       
                    </div>
                </div>
                <template v-if="isShowVerify">
                    <div class="p-[30px] bg-[#fff] mb-[20px] rounded-[var(--rounded-big)]" v-if="verifyInfo && verifyInfo.length">
                        <el-carousel height="220px" v-if="verifyInfo.length > 1" arrow="always" :autoplay="false" indicator-position="none">
                            <el-carousel-item v-for="(item,index) in verifyInfo" :key="index">
                                <div class="flex flex-col items-center justify-center">
                                    <el-image style="width: 150px; height: 150px" :src="item.qrcode" :fit="cover" />
                                </div>
                                <div class="w-full my-[10px] border-0 border-t-[1px] border-dashed border-[#e5e5e5] "></div>
                                <div class="flex items-center justify-center">
                                    <span class="text-[14px] font-bold">{{item.code}}</span>
                                    <span class="text-[#666] text-[12px] ml-[5px] border-[1px] border-solid border-[#666] bg-[#f7f7f7] px-[6px] py-[3px] rounded" @click="copy(item.code)">{{ t('copy') }}</span>
                                </div>
                            </el-carousel-item>
                        </el-carousel>
                        <template v-else>
                            <div class="flex flex-col items-center justify-center">
                                <el-image style="width: 150px; height: 150px" :src="verifyInfo[0].qrcode" :fit="cover" />
                            </div>
                            <div class="w-full my-[10px] border-0 border-t-[1px] border-dashed border-[#e5e5e5] "></div>
                            <div class="flex items-center justify-center">
                                <span class="text-[14px] font-bold">{{verifyInfo[0].code}}</span>
                                <span class="text-[#666] text-[14px] ml-[5px] border-[1px] border-solid border-[#666] bg-[#f7f7f7] px-[6px] py-[3px] rounded cursor-pointer" @click="copy(verifyInfo[0].code)">{{ t('copy') }}</span>
                            </div>
                        </template>
                    </div>
                    <div class="p-[30px] bg-[#fff] mb-[20px] rounded-[var(--rounded-big)]">
                        <div class="pb-[10px] border-0 border-b-[1px] border-dashed border-[#e5e5e5]">核销信息</div>
                        <div class="flex mt-[15px] justify-between leading-[16px]">
                            <div class="text-[14px]">{{ t('verifyNum') }}</div>
                            <div class="price-font font-500 text-[14px]">
                                {{'剩余'+(verifyGoodsData.num - verifyGoodsData.verify_count)+'次'}}/{{'共'+verifyGoodsData.num+'次'}}
                            </div>
                        </div>
                        <div class="flex mt-[15px] justify-between leading-[16px]">
                            <div class="text-[14px]">{{ t('validity') }}</div>
                            <div class="price-font font-500 text-[14px]">
                                {{verifyGoodsData.verify_expire_time ? verifyGoodsData.verify_expire_time : '永久'}}
                            </div>
                        </div>
                    </div>
                </template>
                <div class="p-[30px] bg-[#fff] mb-[20px] rounded-[var(--rounded-big)]">
                    <div class="flex justify-between items-center mb-[20px] text-[14px]">
                        <span class="text-[#333]">{{ t('goodsMoney') }}</span>
                        <span class="text-[#666] price-font">￥{{ detail.goods_money }}</span>
                    </div>
                    <div class="flex justify-between items-center mb-[20px] text-[14px]" v-if="Number(detail.delivery_money)">
                        <span class="text-[#333]">{{ t('deliveryMoney') }}</span>
                        <span class="text-[#666] price-font">￥{{ detail.delivery_money }}</span>
                    </div>
                    <div class="flex justify-between items-center mb-[20px] text-[14px]" v-if="Number(detail.discount_money)">
                        <div class="flex items-center">
                            <span class="text-[#333]">{{ t('discountMoney') }}</span>
                            <el-tooltip class="box-item" effect="light" placement="top">
                                <template #content>
                                    <div v-if="Number(detail.full_reduction_money)">满减活动：-￥{{ detail.full_reduction_money }}</div>
                                    <div v-if="Number(detail.coupon_money)">优惠券：-￥{{ detail.coupon_money }}</div>
                                </template>
                                <el-icon color="#666">
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                        </div>
                        
                        <span class="text-[#666] price-font">-￥{{ detail.discount_money }}</span>
                    </div>
                    <div class="flex justify-between items-center mb-[20px] text-[14px]" v-if="Number(detail.mall_discount_money)">
                       <div class="flex items-center">
                            <span class="text-[#333]">{{ t('mallDiscountMoney') }}</span>
                            <el-tooltip class="box-item" effect="light" placement="top">
                                <template #content>
                                    <div v-if="Number(detail.mall_coupon_money)">优惠券：-￥{{ detail.mall_coupon_money }}</div>
                                    <div v-if="Number(detail.point_deduct_money)">积分抵扣：-￥{{ detail.point_deduct_money }}</div>
                                </template>
                                <el-icon color="#666">
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                       </div>
                        <span class="text-[#666] price-font">-￥{{ detail.mall_discount_money }}</span>
                    </div>
                    <div class="flex justify-between items-center text-[14px]">
                        <span class="text-[#333]">{{ t('orderMoney') }}</span>
                        <div class="text-[var(--el-price)]">
                            <span class="price-font">￥</span>
                            <span class="text-[22px] font-600 price-font">{{ detail.order_money }}</span>
                        </div>
                    </div>
                </div>
                <div class="bg-[#fff] text-right py-[18px] px-[30px] rounded-[var(--rounded-big)]" v-if="(detail.status == 1) || (detail.status == 3) || (detail.status == 5 && detail.is_evaluate != 1 &&  evaluateConfig.is_evaluate == 1) || showLogistics(detail)"> 
                    <el-button  v-if="detail.status == 1" plain  round  class="oppoSans-M !text-[#666] !border-[#ccc]" @click="orderBtnFn( 'close')">{{t('orderClose')}}</el-button>
                    <el-button  v-if="detail.status == 1"  type="primary"  round class="oppoSans-M"  @click="orderBtnFn('pay')">{{t('topay')}}</el-button>
                    <el-button  v-if="detail.status == 3" type="primary"  round class="oppoSans-M" @click="orderBtnFn('finish')">{{t('orderFinish')}}</el-button>
                    <template v-if="detail.status == 5">
                         <el-button v-if="detail.is_evaluate != 1 && evaluateConfig.is_evaluate == 1" plain round  lass="oppoSans-M !text-[#666] !border-[#ccc]" @click="orderBtnFn('evaluate')">{{t('evaluate')}}</el-button>
                    </template>
                    <el-button  v-if="showLogistics(detail)"  plain round lass="oppoSans-M !text-[#666] !border-[#ccc]"  @click="orderBtnFn('logistics')">{{t('logistics')}}</el-button>
                </div>
            </div>
        </div>
        <!-- 评价 -->
        <add-evaluate   ref="addEvaluateRef"  @complete="getOrderInfoFn(orderId)" ></add-evaluate>
        <!-- 物流包裹 -->
        <delivery-package ref="packageRef"></delivery-package>
        <!-- 组合支付 -->
        <group-pay ref="groupPayRef"></group-pay>
        <!-- 组合关闭订单 -->
        <group-close ref="groupCloseRef" @confirm="closeGroupOrder"></group-close>
        <!-- 赠品 -->
        <gift-goods ref="giftGooodsRef"/>
    </div>
</template>
<script lang="ts" setup>
import { computed, ref, watch ,reactive} from 'vue'
import { copy } from '@/utils/common'
import { ArrowRight } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { getEvaluateConfig } from '@/addon/mall/api/goods'
import { getMallOrderDetail, getMallOrderPay,orderClose, orderFinish, getMallOrderClose} from '@/addon/mall/api/order'
import { getVerifyCode } from '@/app/api/verify';
import AddEvaluate from '@/addon/mall/pages/order/components/add-evaluate.vue'
import deliveryPackage from '@/addon/mall/pages/order/components/delivery-package.vue'
import GroupPay from '@/addon/mall/pages/order/components/group-pay.vue'
import GroupClose from '@/addon/mall/pages/order/components/group-close.vue'
import diyFormDetail from '@/addon/components/diy-form-detail/index.vue'
import giftGoods from '@/addon/mall/pages/order/components/gift-goods.vue'

definePageMeta({
    middleware: "auth"
});
const route = useRoute()
const router = useRouter()
let detail = ref({})
let orderId = route.query.order_id
let loading = ref(false);
const giftGooodsRef = ref()
const getOrderInfoFn = (order_id:any) => {
    loading.value = true
    getMallOrderDetail(order_id).then((res:any) => {
        detail.value = res.data
        if(res.data.order_goods && res.data.order_goods.length && isShowVerify.value){
			let obj = {};
			obj.order_goods_id = res.data.order_goods[0].order_goods_id
            obj.site_id = res.data.order_goods[0].site_id
			getVerifyCodeFn(obj);
		}
        getStatus()
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getOrderInfoFn(orderId)
const orderBtnFn = (type = '') => {
	if (type == 'pay'){
        payFn(detail.value)
    }else if (type == 'close') {
		close(detail.value);
	} else if (type == 'finish') {
		finish(detail.value);
	}else if (type == 'evaluate') {
		evaluateFn(detail.value)
	}else if (type == 'logistics') {
        packageEvent(detail.value)
	}
}

// 获取评论设置
const evaluateConfig = ref('')
const evaluateEvent = () => {
	getEvaluateConfig().then((res:any) => {
		evaluateConfig.value = res.data
	})
}
evaluateEvent()
// 支付
let groupPayRef = ref(null)
const payFn = (item:any) => {
	getMallOrderPay(item.order_id).then((res:any) =>{
		if(res.data.is_allow_pay){
            router.push({ path: '/pay/pay', query: {trade_type: 'mall',trade_id: res.data.group_id } })
		}else{
            if(res.data.order_list.length){
			    groupPayRef.value.open(res.data,item)
            }else{
                router.push({ path: '/pay/pay', query: {trade_type: 'mall',trade_id: res.data.group_id } })
            }
		}
	})
}
//取消订单
const groupCloseRef = ref(null)
const close = (item:any) => {
    ElMessageBox.confirm('您确定要关闭该订单吗？', '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            confirmButtonClass:'!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]',
            cancelButtonClass:'!border-[#dcdfe6]',
            type: 'warning'
        }
    ).then(() => {
        orderClose(item.order_id).then((res: any) => {
            if(Object.keys(res.data).length && !res.data.is_allow_close){
                if(res.data.order_list.length){
				    groupCloseRef.value.open(res.data)
                }else{
                    getMallOrderClose(res.data.group_id).then(res =>{
						getOrderInfoFn(orderId)
					}).catch(() =>{
					})
                }
			}else{
				getOrderInfoFn(orderId)
			}
        }).catch(() => {
        })
    })
}
const closeGroupOrder = ()=>{
    getOrderInfoFn(orderId)
}

//订单完成
const finish = (item: any) => {
	ElMessageBox.confirm('您确定物品已收到吗？', '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            confirmButtonClass:'!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]',
            cancelButtonClass:'!border-[#dcdfe6]',
            type: 'warning'
        }
    ).then(() => {
        orderFinish(item.order_id).then((res: any) => {
            getOrderInfoFn(orderId)
        }).catch(() => {
     
        })
    })
}
// 申请退款
const applyRefund = (id:any) =>{
    router.push({path:'/refund/apply',query:{order_id: detail.value.order_id,order_goods_id:id}})
}
// 查看退款
const refundDetail = (id:any) =>{
    router.push({path:'/refund/detail',query:{order_refund_no: id}})
}
//添加评论
const addEvaluateRef = ref(null)
const evaluateFn = (item: any) =>{
    addEvaluateRef.value.setFormData(item.order_id)
    addEvaluateRef.value.dialogEvaluateVisible = true
}
// 判断展示物流
const showLogistics = (data: any) => {
    let flag = false
    if (data.delivery_type != 'express') return false;
    for (let i = 0; i < data.order_delivery.length; i++) {
        let item = data.order_delivery[i];
        if (item.sub_delivery_type === 'express' && data.status === '3') {
            flag = true;
            break;
        } else if (item.sub_delivery_type === 'express' && data.status === '5') {
            flag = true;
            break;
        } else {
            flag = false
        }
    }
    return flag
}
/**
 * 发货
 */
 const packageRef = ref(null)
const packageEvent = (data:any) => {
	packageRef.value.setFormData(data)
	packageRef.value.showDialog = true
}

// 判断当前步骤条的状态
let current = ref(0)
const getStatus = () => {
	if(detail.value.status == 1){
		return current.value = 0
	}else if(detail.value.status == 2){
		return current.value = 1
	}else if(detail.value.status == 3){
		return current.value = 2
	}else if(detail.value.status == 5){
		return current.value = 4
	}
}

/************ 虚拟商品核销-start ***************/ 
let verifyGoodsData = ref({}) //虚拟商品
const isShowVerify = computed(() => {
	let bool =  false;
	if(detail.value.order_goods.length == 1){
		verifyGoodsData.value = detail.value.order_goods[0]
		
		let data = detail.value.order_goods[0];
		bool = data.is_verify == 1 && data.goods_type == 'virtual' && data.delivery_status == 'delivery_finish' && detail.value.status == 3 ? true : false;
	}
	return bool
})
let verifyInfo = ref([])
const getVerifyCodeFn = (data:any) => {
	verifyInfo.value = [];
	getVerifyCode('shopVirtualGoods', data).then((res) => {
		verifyInfo.value = res.data;
	})
}
/************ 虚拟商品核销-end ***************/ 
// 商品表单信息
const goodsDiyFormData = ref([]);
const getGoodsDiyFormDetailCallback = (data: any) => {
    goodsDiyFormData.value = data;
}

// 订单表单信息
const orderDiyFormData = ref([]);
const getOrderDiyFormDetailCallback = (data: any) => {
    orderDiyFormData.value = data;
}

//赠品数量
const getGiftNum = (data) => { 
    if (!Array.isArray(data) || data.length === 0) return 0;
    return data.reduce((total, item) => {
        const giftNum = Number(item.num) || 0;
        return total + giftNum;
    }, 0);
};
</script>
<style lang="scss" scoped>

:deep(.el-step__head.is-success){
    --el-color-success: #e93323;
}
:deep(.el-step__title.is-success){
    --el-color-success: #333;
}

:deep(.el-table th.el-table__cell) {
    -webkit-user-select: none;
    user-select: none;
    background-color: #F5F7F9;
    color: #333;
}
.line-feed {
    word-wrap: break-word;
    word-break: break-all;
}
.site{
    &:hover{
        .site-style{
            color:var( --el-color-primary);
        }
    }
}
</style>