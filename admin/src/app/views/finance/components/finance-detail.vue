<template>
    <el-drawer v-model="showDialog" :title="popTitle" direction="rtl" :before-close="handleClose" class="finance-detail-drawer">
        <div class="main-container" >
            <div class="mb-[20px]">{{ detail.time }}</div>
            <div class="mb-[30px]">
                <div class="flex items-center justify-between">
                    <span class="text-[15px] text-[#666] font-semibold">实际收入</span>
                    <span class="text-[20px] font-semibold">{{ detail.income_money }}</span>
                </div>
                <el-row :gutter="24">
                    <el-col :span="12">
                        <div class="my-[30px] flex items-center justify-between">
                            <span class="text-[14px] text-[#666] font-medium ml-[20px] pl-[8px] select">订单实收</span>
                            <span class="text-[15px] text-[#666] font-semibold">{{ detail.order_actual_money }}</span>
                        </div>
                        <div class="py-[20px] px-[15px] bg-[#f9f9f9]">
                            <div class="flex items-center justify-between mb-[20px]">
                                <span class="text-[14px] text-[#999]">订单应收</span>
                                <span class="text-[14px] text-[#666]">{{ detail.order_receivable_money }}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-[14px] text-[#999]">平台优惠券补贴</span>
                                <span class="text-[14px] text-[#666]">{{ detail.mall_coupon_money }}</span>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="my-[30px] flex items-center justify-between">
                            <span class="text-[14px] text-[#666] font-medium ml-[20px] pl-[8px] select">订单实退</span>
                            <span class="text-[15px] text-[#666] font-semibold">{{ detail.order_actual_refund_money }}</span>
                        </div>
                        <div class="py-[20px] px-[15px] bg-[#f9f9f9]">
                            <div class="flex items-center justify-between mb-[20px]">
                                <span class="text-[14px] text-[#999]">订单应退</span>
                                <span class="text-[14px] text-[#666]">{{ detail.order_refund_money }}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-[14px] text-[#999]">退还平台优惠券补贴</span>
                                <span class="text-[14px] text-[#666]">{{ detail.mall_refund_coupon_money }}</span>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="mb-[30px]">
                <div class="flex items-center justify-between">
                    <span class="text-[15px] text-[#666] font-semibold">实际支出</span>
                    <span class="text-[20px] font-semibold">{{ detail.outlay_money }}</span>
                </div>
                <el-row :gutter="24">
                    <el-col :span="12">
                        <div class="my-[30px] flex items-center justify-between">
                            <span class="text-[14px] text-[#666] font-medium ml-[20px] pl-[8px] select">商家分账</span>
                            <span class="text-[15px] text-[#666] font-semibold">{{ detail.separate_money }}</span>
                        </div>
                        <div class="py-[20px] px-[15px] bg-[#f9f9f9]">
                            <div class="flex items-center justify-between mb-[20px]">
                                <span class="text-[14px] text-[#999]">支付分账</span>
                                <span class="text-[14px] text-[#666]">{{ detail.shop_separate_money }}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-[14px] text-[#999]">退款分账</span>
                                <span class="text-[14px] text-[#666]">{{ detail.shop_refund_separate_money }}</span>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="my-[30px] flex items-center justify-between">
                                <span class="text-[14px] text-[#666] font-medium ml-[20px] pl-[8px] select">供货商分账</span>
                                <span class="text-[15px] text-[#666] font-semibold">{{ detail.supply_all_separate_money }}</span>
                            </div>
                        <div class="py-[20px] px-[15px] bg-[#f9f9f9]">
                            <div class="flex items-center justify-between mb-[20px]">
                                <span class="text-[14px] text-[#999]">支付分账</span>
                                <span class="text-[14px] text-[#666]">{{ detail.supply_separate_money }}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-[14px] text-[#999]">退款分账</span>
                                <span class="text-[14px] text-[#666]">{{ detail.supply_refund_separate_money }}</span>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="my-[30px] flex items-center justify-between">
                            <span class="text-[14px] text-[#666] font-medium ml-[20px] pl-[8px] select">佣金</span>
                            <span class="text-[15px] text-[#666] font-semibold">{{ detail.commission }}</span>
                        </div>
                        <div class="py-[20px] px-[15px] bg-[#f9f9f9]">
                            <div class="flex items-center justify-between mb-[20px]">
                                <span class="text-[14px] text-[#999]">支付佣金</span>
                                <span class="text-[14px] text-[#666]">{{ detail.shop_commission }}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-[14px] text-[#999]">退还佣金</span>
                                <span class="text-[14px] text-[#666]">{{ detail.shop_refund_commission }}</span>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="my-[30px] flex items-center justify-between">
                            <span class="text-[14px] text-[#666] font-medium ml-[20px] pl-[8px] select">运费</span>
                            <span class="text-[15px] text-[#666] font-semibold">{{ detail.shipping_fee }}</span>
                        </div>
                        <div class="py-[20px] px-[15px] bg-[#f9f9f9]">
                            <div class="flex items-center justify-between mb-[20px]">
                                <span class="text-[14px] text-[#999]">支付运费</span>
                                <span class="text-[14px] text-[#666]">{{ detail.delivery_money }}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-[14px] text-[#999]">退还运费</span>
                                <span class="text-[14px] text-[#666]">{{ detail.refund_delivery_money }}</span>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <div class="flex items-center justify-between">
                <span class="text-[16px] text-[#666]">{{ detail.type == 'day' ? t('当日结余'): t('当月结余') }}</span>
                <span class="text-[20px] font-bold text-[#e93323]">{{ detail.balance_money }}</span>
            </div>
        </div>
    </el-drawer>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'

const showDialog = ref(false)
const popTitle: string = '账单详情'
const detail = ref({})
const handleClose = (done: () => void) => {
    showDialog.value = false
}

const setFormData = (row: any = null) => {
    detail.value = row
}

defineExpose({
    showDialog,
    setFormData
})
</script>
<style lang="scss">
.finance-detail-drawer{
    width: 1300px !important;
}
.select{
    position: relative;
    &::before{
        position: absolute;
        content: "";
        width: 3px;
        height: 18px;
        background: var(--el-color-primary);
        top: 0;
        left: 0;
    }
}
</style>