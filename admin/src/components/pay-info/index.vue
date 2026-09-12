<template>
    <el-dialog v-model="showDialog" :title="t('payInfo.payInfoTip')" width="600px" :close-on-click-modal="true" :close-on-press-escape="false" :show-close="false">
        <el-form :model="payInfoData" label-width="120px" ref="formRef" class="page-form" v-loading="loading">
            <el-form-item :label="t('payInfo.outTradeNo')">
                <div class="input-width">{{ payInfoData.out_trade_no  }}</div>
            </el-form-item>
            <el-form-item :label="t('payInfo.tradeNo')" v-if="payInfoData.trade_no && payInfoData.type !== 'balancepay'">
                <div class="input-width">{{ payInfoData.trade_no  }}</div>
            </el-form-item>
            <el-form-item :label="t('payInfo.orderInfo')">
                <div class="input-width leading-[24px]">{{ payInfoData.body  }}</div>
            </el-form-item>
            <el-form-item :label="t('payInfo.orderMoney')">
                <div class="input-width">{{ payInfoData.money  }}</div>
            </el-form-item>
             <el-form-item :label="t('payInfo.channelName')">
                <div class="input-width">{{ payInfoData.channel_name  }}</div>
            </el-form-item>
            <el-form-item :label="t('payInfo.typeName')">
                <div class="input-width">{{ payInfoData.type_name  }}</div>
            </el-form-item>
            <el-form-item :label="t('payInfo.payStatus')">
                <div class="input-width">{{ payInfoData.status_name  }}</div>
            </el-form-item>
            <el-form-item :label="t('payInfo.payTime')">
                <div class="input-width">{{ payInfoData.pay_time  }}</div>
            </el-form-item>
            <el-form-item :label="t('汇付交易流水号')" v-if="payInfoData.type == 'huifu_wechatpay' || payInfoData.type == 'huifu_alipay'">
                <div>{{ payInfoData.json?.hf_seq_id  }}</div>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="showDialog = false">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { t } from '@/lang'
import { img, getAppType } from '@/utils/common'
import { getAdminPayInfo, getSitePayInfo } from '@/app/api/sys'

const showDialog = ref(false)
const payInfoData = ref<any>({})
const loading = ref(false)

const setFormData = (data: any) => {
    loading.value = true
    const api = ['admin', 'circle'].includes(getAppType()) ? getAdminPayInfo : getSitePayInfo
    api(data).then((res: any) => {
        payInfoData.value = res.data
        if (payInfoData.value.json) {
            payInfoData.value.json = JSON.parse(payInfoData.value.json)
        }
        showDialog.value = true
        loading.value = false
    })
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>

</style>