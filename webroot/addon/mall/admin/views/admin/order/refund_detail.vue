<template>
	<div class="main-container">
		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/mall/order/refund' })" />
        </el-card>
		<el-form :model="formData" label-width="110px" class="page-form" v-loading="loading" label-position="left">
			<el-card class="box-card !border-none relative" shadow="never" v-if="formData">
				<h3 class="panel-title">{{ t('orderInfo') }}</h3>

				<div class="row-bg px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('orderNo')">
						<div class="input-width cursor-pointer text-[var(--el-color-primary)]" @click="router.push({ path: '/mall/order/detail', query: { order_id: formData.order_id } })">{{ formData.order_main.order_no }}</div>
					</el-form-item>
					<el-form-item :label="t('orderForm')">
						<div class="input-width">{{ formData.order_main.order_from_name }}</div>
					</el-form-item>
					<el-form-item :label="t('outTradeNo')" v-if="formData.order_main.out_trade_no">
						<div class="input-width">{{ formData.order_main.out_trade_no }}</div>
					</el-form-item>
					<el-form-item :label="t('payType')" v-if="formData.pay_refund">
						<div class="input-width">{{ formData.pay_refund.type_name }}</div>
					</el-form-item>
					<el-form-item :label="t('deliveryType')">
						<div class="input-width">{{ formData.order_main.delivery_type_name }}</div>
					</el-form-item>
					<el-form-item :label="t('takerName')">
						<div class="input-width">{{ formData.order_main.taker_name }}</div>
					</el-form-item>
					<el-form-item :label="t('takerMobile')">
						<div class="input-width">{{ formData.order_main.taker_mobile }}</div>
					</el-form-item>
					<el-form-item :label="t('takerFullAddress')">
						<div class="input-width">{{ formData.order_main.taker_full_address }}</div>
					</el-form-item>
					<el-form-item :label="t('memberRemark')">
						<div class="input-width">{{ formData.order_main.member_remark }}</div>
					</el-form-item>
					<el-form-item :label="t('notes')">
						<div class="input-width">{{ formData.order_main.mall_remark }}</div>
					</el-form-item>
					<el-form-item :label="t('supplyName')" v-if="formData.supply">
						<div class="max-w-[100%]">{{ formData.supply.site_name }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title">{{ t('afterSales') }}</h3>
				<div class="row-bg px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('orderRefundNo')">
						<div class="input-width">{{ formData.order_refund_no }}</div>
					</el-form-item>
					<el-form-item :label="t('refundType')">
						<div class="input-width">{{ formData.refund_type_name }}</div>
					</el-form-item>
					<el-form-item :label="t('createTime')">
						<div class="input-width">{{ formData.create_time }}</div>
					</el-form-item>
					<el-form-item :label="t('refundMoney')">
						<div class="input-width">￥{{ formData.apply_money }}</div>
					</el-form-item>
					<el-form-item :label="t('refundReason')">
						<div class="input-width">{{ formData.reason }}</div>
					</el-form-item>
					<el-form-item :label="t('realRefundMoney')">
						<div class="input-width">￥{{ formData.money }}</div>
					</el-form-item>
					<el-form-item :label="t('refundVoucher')" v-if="formData.voucher && formData.voucher.length">
						<div class="input-width flex">
							<div class="mr-3" v-for="(voucherItem, voucherIndex) in formData.voucher" :key="voucherIndex">
								<el-image v-if="voucherItem" class="w-[70px] h-[70px]" :src="img(voucherItem)" fit="contain" :preview-src-list="[img(voucherItem)]">
									<template #error>
										<div class="image-slot">
											<img class="w-[70px] h-[70px]" src="@/addon/mall/assets/goods_default.png" />
										</div>
									</template>
								</el-image>
							</div>
						</div>
					</el-form-item>
					<el-form-item :label="t('refundRemark')">
						<div class="max-w-[100%] break-all">{{ formData.remark }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title" v-if="formData.status == 4">买家退货信息</h3>
				<div class="row-bg px-[30px] mb-[20px] multiple-layout" v-if="formData.status == 4">
					<el-form-item :label="t('expressCompany')">
						<div class="input-width">{{ formData.delivery.express_company }}</div>
					</el-form-item>
					<el-form-item :label="t('expressNumber')">
						<div class="input-width">{{ formData.delivery.express_number }}</div>
					</el-form-item>
					<el-form-item :label="t('expressRemark')">
						<div class="input-width">{{ formData.delivery.remark }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title">{{ t('refundStatus') }}</h3>
				<div class="mb-[20px]">
					<p>
						<span class="ml-[30px] text-[14px] mr-[20px]">{{ t('refundStatus') }}：</span>
						<span class="text-[14px]">{{ formData.status_name }}</span>
					</p>
				</div>

				<h3 class="panel-title">{{ t('goodsDetail') }}</h3>
				<el-table :data="formData.order_goods" size="large">
					<el-table-column :label="t('goodsName')" align="left" width="300">
						<template #default="{ row }">
							<div class="flex">
								<div class="flex items-center w-[50px] h-[50px] mr-[10px]">
									<img class="w-[50px] h-[50px]" :src="img(row.goods_image)" />
								</div>
								<div class="flex flex-col flex-1">
									<span>{{ row.goods_name }}</span>
								</div>
							</div>
						</template>
					</el-table-column>
					<el-table-column prop="price" :label="t('price')" min-width="50" align="left" />
					<el-table-column prop="num" :label="t('num')" min-width="50" align="right" />
				</el-table>
				<h3 class="mt-[50px] mb-[20px]" v-if="formData.refund_log.length > 0">{{ t('operateLog') }}</h3>
				<div class="mb-[100px]" style="min-height: 100px">
					<template v-if="formData.refund_log.length > 0">
						<div class="flex" v-for="(items, index) in formData.refund_log" :key="index">
							<div class="mr-[20px] min-w-[71px]">
								<div class="leading-[1] w-full text-[14px] w-[100px] flex justify-end">
									{{ items.create_time.split(' ')[0] }}
								</div>
								<div class="leading-[1] w-full text-[14px]  w-[100px] flex justify-end mt-[15px]">
									{{ items.create_time.split(' ')[1] }}
								</div>
							</div>
							<div>
								<div class="w-[16px] h-[16px] flex items-center bg-[#D1EBFF] border-[1px] border-[#0091FF] rounded-[999px]">
									<div class="w-[8px] h-[8px] mx-auto bg-[#0091FF] rounded-[999px]"></div>
								</div>
								<div v-if="index + 1 != formData.refund_log.length" class="w-[2px] h-[50px] bg-[#D1EBFF] mx-auto">
								</div>
							</div>
							<div>
								<div class="leading-[1] ml-[20px] text-[14px]">
									{{ items.main_type_name }}{{ items.main_name }}
								</div>
								<div class="leading-[1] ml-[20px] text-[14px] mt-[15px]">
									{{ items.type_name }}
								</div>
							</div>
						</div>
					</template>
				</div>
			</el-card>
			<el-card class="box-card !border-none relative" shadow="never" v-if="!loading && !formData">
				<el-empty :description="t('orderInfoEmpty')" />
			</el-card>
		</el-form>

	</div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { orderRefundDetail } from '@/addon/mall/api/admin/order'
import { useRoute, useRouter } from 'vue-router'
import { img } from '@/utils/common'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const refundId: number = parseInt(route.query.refund_id as string)
const loading = ref(true)

const formData: Record<string, any> | null = ref(null)

const setFormData = async (refundId: number = 0) => {
    loading.value = true
    formData.value = null
    await orderRefundDetail(refundId)
        .then(({ data }) => {
            formData.value = data
            formData.value.order_goods = [data.order_goods]
        })
        .catch(() => {
        })
    loading.value = false
}

if (refundId) setFormData(refundId)
else loading.value = false
</script>

<style lang="scss" scoped></style>
