<template>
    <div class="min-h-[500px]" v-loading="loading">
        <div class="main-container" v-if="payInfo">
            <div class="mt-[40px] py-[56px]  px-[30px] bg-[#fff]  flex items-center justify-between mb-[20px] rounded-[var(--rounded-big)]">
                <div class="flex">
                    <img src="@/assets/images/pay/pay.png" class="w-[100px] h-[100px]">
                    <div class="ml-[30px] w-[375px] flex flex-col justify-center">
                        <div class="text-[20px] text-[#333] font-700 mb-[20px]">订单提交成功，去付款吧~</div>
                    </div>
                </div>
                <div class="flex flex-col">
                    <div class="text-[#333] text-[14px] mb-[20px] flex items-baseline">
                        <span> 应付总额： </span>
                       <div class="text[var(--el-price)] text-[14px] font-500">
                        <span class="price-font">￥</span>
                        <span class="text-[30px] price-font">{{payInfo.money}}</span>
                       </div>
                    </div>
                </div>
            </div>
            <div class="bg-[#fff] p-[30px] rounded-[var(--rounded-big)]">
                <div class="text-[16px] text-[#282828] mb-[30px]">选择以下支付方式</div>
                <div class="flex items-center mb-[30px]" v-if="payInfo.pay_type_list.length">
                    <template  v-for="(item, index) in payInfo.pay_type_list" :key="index">
                        <div class="w-[270px] h-[80px] border-[1px] border-solid cursor-pointer rounded-[8px] flex justify-center items-center mr-[20px]" :class="{'border-[var(--el-color-primary)]' : item.key == type}" @click="payTypeFn(item.key)">
                            <div class="iconfont icon-weixinzhifu !text-[36px] text-[#09bb07] mr-[14px]" v-if="item.key == 'wechatpay'"></div>
                            <div class="iconfont icon-weixinzhifu !text-[36px] text-[#09bb07] mr-[14px]" v-else-if="item.key == 'huifu_wechatpay'"></div>
                            <div class="iconfont icon-zhifubaozhifu !text-[36px] text-[#00aaea] mr-[14px]" v-else-if="item.key == 'alipay'"></div>
                            <div class="iconfont icon-zhifubaozhifu !text-[36px] text-[#00aaea] mr-[14px]" v-else-if="item.key == 'huifu_alipay'"></div>
                            <div class="iconfont icon-yuezhifu !text-[36px] text-[#fe9c01] mr-[14px]" v-else></div>
                            <div class="text-[#4E4E4E] text-[16px] leading-[21px]">{{ item.name }}</div>
                        </div>
                    </template>
                </div>
                <div class="mt-[30px] flex justify-end items-center">
                    <div class="w-[150px] h-[50px] text-[16px] bg-[var(--el-color-primary)] rounded-[var(--rounded-xl)] text-white text-center leading-[50px] cursor-pointer" @click="payFn">去支付</div>
                </div>
            </div>
        </div>
        <el-dialog v-model="dialogPay"  :title="qrcodeData.title" width="400px" center :show-close="false" custom-class="pay !rounded-[var(--rounded-big)]">
            <div v-if=" type == 'wechatpay' || type == 'alipay' || type == 'huifu_wechatpay' || type == 'huifu_alipay'">
				<div class="flex justify-center items-center" >
					<img class="w-[200px] h-[200px]" :src="alipayCode" v-if="type == 'alipay'|| type == 'huifu_alipay'">
					<img class="w-[200px] h-[200px]" :src="wechatpayCode" alt="" v-if="type == 'wechatpay' || type == 'huifu_wechatpay'">
				</div>
                <div class="mt-[30px] text-center text-[16px]">{{ qrcodeData.desc }}</div>
			</div>
            <template #footer>
                <div class="dialog-footer">
                    <el-button type="primary" @click="cancelPayFn">放弃支付</el-button>
                </div>
        </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive,computed,watch } from 'vue'
import { useRoute,useRouter } from 'vue-router'
import { debounce,isUrl } from '@/utils/common'
import QRCode from 'qrcode'
import { getPayInfo,pay } from '@/app/api/pay'

const route = useRoute()
const router = useRouter()
const tradeId = route.query.trade_id
const tradeType = route.query.trade_type
const loading = ref(false)
let payIntervalId: any;
let payInfo = ref<any>(null)
const type = ref<any>('')
const getPayInfoFn = ()=>{
    loading.value = true
    getPayInfo(tradeType, tradeId).then((res: any) =>{
        payInfo.value = res.data
        loading.value = false
        // 支付类型
        type.value = res.data.pay_type_list[0] ? res.data.pay_type_list[0].key : ''
        //检测支付状态，当status为二时，表示支付完成，需跳转订单列表
		if (payInfo.value.status == 2) {
			clearInterval(payIntervalId);
			router.replace(`/pay/pay_succeed?trade_id=${tradeId}&trade_type=${tradeType}`)
		}
        // if (['alipay', 'wechatpay'].includes(type.value)) {
        //     payFn()
        // }
    }).catch((res) => {
		loading.value = false;
	})
}
getPayInfoFn()

// 监听路由变化关闭定时器
watch(
	() => router.currentRoute.value.path,
	(toPath) => {
		if (toPath != '/pay/pay') {
			clearInterval(payIntervalId)
		}
	}, { immediate: true, deep: true }
)

// 确定支付
let wechatpayCode = ref('') //微信支付码
let alipayCode = ref('') //支付宝支付码
let dialogPay = ref(false) //弹框数据
let qrcodeData = ref<any>({})
const runtimeConfig = useRuntimeConfig()
const payFn = () => {
    if (loading.value) return
    loading.value = true
    pay({
			trade_type: payInfo.value?.trade_type,
            trade_id: payInfo.value?.trade_id,
			type: type.value
		}).then((res: any)=> {
            if(type.value == 'balancepay'){
                router.replace(`/pay/pay_succeed?trade_id=${tradeId}&trade_type=${tradeType}`);
                return;
            }else if (type.value  == 'wechatpay') {
                QRCode.toDataURL(res.data.code_url, { errorCorrectionLevel: 'L', margin: 0, width: 100 }).then(url => {
                    wechatpayCode.value = url
                });
                clearInterval(payIntervalId);
                qrcodeData.value.title ='微信支付'
                qrcodeData.value.desc = '请使用微信扫描二维码支付'
                dialogPay.value = true
                payIntervalId = setInterval(() => {
                    getPayInfoCirculationFn(route.query.trade_id, route.query.trade_type);
                }, 2000)
            }else if (type.value  == 'alipay') {
                QRCode.toDataURL(res.data.qr_code, { errorCorrectionLevel: 'L', margin: 0, width: 100 }).then(url => {
                    alipayCode.value = url
                });
                clearInterval(payIntervalId);
                qrcodeData.value.title ='支付宝支付'
                qrcodeData.value.desc = '请使用支付宝扫描二维码支付'
                dialogPay.value = true
                payIntervalId = setInterval(() => {
                    getPayInfoCirculationFn(route.query.trade_id, route.query.trade_type);
                }, 2000)
                
            }else if(type.value  == 'huifu_alipay'){
                QRCode.toDataURL(res.data.url, { errorCorrectionLevel: 'L', margin: 0, width: 100 }).then(url => {
                    alipayCode.value = url
                });
                clearInterval(payIntervalId);
                qrcodeData.value.title ='支付宝支付'
                qrcodeData.value.desc = '请使用支付宝扫描二维码支付'
                dialogPay.value = true
                payIntervalId = setInterval(() => {
                    getPayInfoCirculationFn(route.query.trade_id, route.query.trade_type);
                }, 2000)

            } else if(type.value  == 'huifu_wechatpay') {
                let url_type = res.data.to_type
                if(url_type == 'cashier'){
                    let cur_url = `${runtimeConfig.public.VITE_IMG_DOMAIN || location.origin }/wap${res.data.url}?pay_type=${type.value}&trade_id=${tradeId}&trade_type=${tradeType}`
                    console.log(cur_url)
                    QRCode.toDataURL(cur_url, { errorCorrectionLevel: 'L', margin: 0, width: 100 }).then(url => {
                        console.log(url)
                        wechatpayCode.value = url
                    });
                    clearInterval(payIntervalId);
                    qrcodeData.value.title ='微信支付'
                    qrcodeData.value.desc = '请使用微信扫描二维码支付'
                    dialogPay.value = true
                    payIntervalId = setInterval(() => {
                        getPayInfoCirculationFn(route.query.trade_id, route.query.trade_type);
                    }, 2000)
                }

            } else if(res.data.url){
                isUrl(res.data.url) ? window.location.href = res.data.url : router.replace(res.data.url);
            } 
            loading.value = false;
        }).catch((res) => {
            loading.value = false;
        })
}

//选择支付
const payTypeFn =debounce((payType) => {
	type.value = payType
}) 
// 用于不断请求支付结果
const getPayInfoCirculationFn = (trade_id, trade_type) => {
	getPayInfo(trade_type, trade_id).then((res) => {
		//检测支付状态，当status为二时，表示支付完成，需跳转订单列表
		if (res.data.status == 2) {
			clearInterval(payIntervalId);
			router.replace(`/pay/pay_succeed?trade_id=${tradeId}&trade_type=${tradeType}`);
		}
	}).catch((err) => {
        clearInterval(payIntervalId);
    })
}
// 放弃支付
const cancelPayFn = () =>{
    clearInterval(payIntervalId);
    dialogPay.value = false
}

</script>

<style lang="scss" scoped>
.count :deep(.el-statistic__number){
    color:#e93323;
    font-size: 14px;
    font-weight: normal;
}
:deep(.pay .el-dialog__header){
    padding: 30px 0 36px!important;
    margin-right: 0!important;
}
:deep(.pay .el-dialog__title){
    color: #333!important;
    font-size: 20px!important;
}
:deep(.pay .el-dialog__body){
    padding: 0 25px 30px!important;
}
</style>
