<template>
    <view :style="themeColor()">
        <view class="error-msg">{{ errorMsg }}</view>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { redirect } from '@/utils/common';
import { onLoad } from '@dcloudio/uni-app'
import { getMsgJumpPath } from '@/app/api/system'

const transactionId = ref('') //微信订单号
const merchantId = ref('')  //支付下单商户的商户号
const outTradeNo = ref('') //商户系统内部订单号
const errorMsg = ref('')

onLoad((options: any) => {
    if (options.transaction_id || options.merchant_trade_no || options.merchant_id) {
        transactionId.value = options.transaction_id;
        merchantId.value = options.merchant_id;
        outTradeNo.value = options.merchant_trade_no;
        getMsgJumpPathFn();
    } else {
        errorMsg.value = '缺少merchant_trade_no参数';
    }
});

const getMsgJumpPathFn = () => {
    getMsgJumpPath({
        out_trade_no: outTradeNo.value,
        transaction_id: transactionId.value,
        merchant_id: merchantId.value,
    }).then((res: any) => {
        if (res.data && res.data.path) {
            // 跳转到设置的页面
            redirect({ url: '/' + res.data.path, mode: 'reLaunch' })
        }
    });
}
</script>
<style lang="scss" scoped>
</style>