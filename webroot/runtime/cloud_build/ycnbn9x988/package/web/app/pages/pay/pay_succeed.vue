<template>
	<div class="w-full bg-[#F7F8FA] page-height" v-loading="loading">
		<div v-if="Object.values(payInfo).length">
			<div class="flex items-center justify-center py-[40px]">
				<div class="inline-block bg-[#00b42a] rounded-[50%] w-[35px] h-[35px] text-center leading-[40px]">
					<el-icon color="#fff"><Select /></el-icon>
				</div>
				<h3 class="text-[#00b42a] ml-[10px] text-[25px]">{{ t('payMente') }}</h3>
			</div>
			<div class="w-[1200px] mx-[auto] bg-[#fff] rounded-[var(--rounded-big)]">
				<div class="py-[20px] px-[20px] text-[14px]">
					<h3 class="text-center my-[30px] text-[40px] price-font" v-if="payInfo.type == 'balancepay'">￥{{payInfo.money}}</h3>
					<h3 class="text-center my-[30px] text-[40px] price-font" v-else>￥{{payInfo.money}}</h3>
					<div class="bg-[#f7f8fa] py-[20px] px-[30px] mb-[30px] rounded-[var(--rounded-big)]">
						<div class="flex mb-[20px]">
							<span>{{ t('payNumber') }}</span>
							<span class="flex-1 text-right">{{payInfo.out_trade_no}}</span>
						</div>
						<div class="flex mb-[20px]">
							<span>{{ t('orderTime') }}</span>
							<span class="flex-1 text-right">{{payInfo.create_time}}</span>
						</div>
						<div class="flex">
							<span>{{ t('payType') }}</span>
							<span class="flex-1 text-right">{{payInfo.type_name}}</span>
						</div>
					</div>
					<div class="flex items-center justify-center mb-[40px]">
						<el-button plain round class="!border-[#ccc] !text-[#333] oppoSans-M" @click="toOderList">{{ t('lookOrders') }}</el-button>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import { Select } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { getPayInfo } from '@/app/api/pay'

// 需要登录
definePageMeta({
	middleware: "auth"
});
const route = useRoute();
const router = useRouter();
const tradeId = route.query.trade_id
const tradeType = route.query.trade_type
let loading = ref(true);
let payInfo = ref({});

// 支付信息
const getPayInfoFn = (trade_id,trade_type)=>{
	loading.value = true;
	getPayInfo(trade_type, trade_id).then((res) => {
		payInfo.value = res.data;
		loading.value = false;
	}).catch((res) => {
		loading.value = false;
	})
}
getPayInfoFn(tradeId,tradeType);

const toOderList = ()=>{
	router.push('/order/list')
}
</script>

<style lang="scss" scoped>
.box-card {
	border: none !important;
}
.text-color {
	color: var(--jjext-color-brand);
}
.page-height {
	min-height: calc(100vh - 170px);
	background-color: var(--el-bg-color-page);
}
</style>