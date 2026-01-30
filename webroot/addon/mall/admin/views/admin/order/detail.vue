<template>
	<div class="main-container">
		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/mall/order/index' })" />
        </el-card>
		<el-form :model="formData" label-width="140px" ref="formRef" class="page-form" v-loading="loading" label-position="left">
			<el-card class="box-card !border-none relative" shadow="never" v-if="formData">
				<h3 class="panel-title">{{ t('orderInfo') }}</h3>
				<div class="row-bg px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('orderNo')">
						<div class="input-width">{{ formData.order_no }}</div>
					</el-form-item>
					<el-form-item :label="t('siteName')" v-if="formData.site_name">
						<div class="input-width">{{ formData.site_name }}</div>
					</el-form-item>
					<el-form-item :label="t('orderForm')">
						<div class="input-width">{{ formData.order_from_name }}</div>
					</el-form-item>
					<el-form-item :label="t('outTradeNo')" v-if="formData.out_trade_no">
						<div class="input-width">{{ formData.out_trade_no }}</div>
					</el-form-item>
					<el-form-item :label="t('payType')" v-if="formData.pay">
						<div class="input-width">{{ formData.pay.type_name }}</div>
					</el-form-item>
					<el-form-item :label="t('memberRemark')">
						<div class="input-width">{{ formData.member_remark ?? '--' }}</div>
					</el-form-item>
					<el-form-item :label="t('notes')">
						<div class="input-width">{{ formData.mall_remark ?? '--' }}</div>
					</el-form-item>
					<el-form-item :label="t('supplyName')" v-if="formData.supply">
						<div class="input-width line-feed">{{ formData.supply.site_name  }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title">{{ t('配送信息') }}</h3>
				<div class="row-bg px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('deliveryType')">
						<div class="input-width">{{ formData.delivery_type_name }}</div>
					</el-form-item>
					<el-form-item :label="t('预约配送时间')" v-if="formData.buyer_ask_delivery_time">
						<div class="input-width">{{ formData.buyer_ask_delivery_time }}</div>
					</el-form-item>
					<template v-if="formData.order_delivery && formData.order_delivery.length && formData.delivery_type == 'local_delivery'">
						<el-form-item :label="t('配送平台')">
							<div class="input-width">{{ formData.order_delivery[0].delivery_service_name }}</div>
						</el-form-item>
						<el-form-item :label="t('配送状态')">
							<div class="input-width">{{ formData.order_delivery[0].local_delivery_status_name }}</div>
						</el-form-item>
						<el-form-item :label="t('配送员')">
							<div class="input-width">{{ formData.order_delivery[0].rider_name }}</div>
						</el-form-item>
						<el-form-item :label="t('配送员电话')">
							<div class="input-width">{{ formData.order_delivery[0].rider_mobile }}</div>
						</el-form-item>
					</template>
					<el-form-item :label="t('takerName')" v-if="formData.delivery_type == 'express' || formData.delivery_type == 'local_delivery'">
						<div class="input-width">{{ formData.taker_name }}</div>
					</el-form-item>
					<el-form-item :label="t('takerMobile')" v-if="formData.delivery_type == 'express' || formData.delivery_type == 'local_delivery' || formData.delivery_type == 'virtual'">
						<div class="input-width">{{ formData.taker_mobile }}</div>
					</el-form-item>
					<el-form-item :label="t('takerFullAddress')" v-if="formData.delivery_type == 'express' || formData.delivery_type == 'local_delivery'">
						<div class="input-width">{{ formData.taker_full_address }}</div>
					</el-form-item>
					<template v-if="formData.delivery_type == 'store'">
						<el-form-item :label="t('storeName')">
							<div class="input-width">{{ formData.store.store_name }}</div>
						</el-form-item>
						<el-form-item :label="t('storeAddress')">
							<div class="input-width">{{ formData.store.full_address }}</div>
						</el-form-item>
						<el-form-item :label="t('storeMobile')">
							<div class="input-width">{{ formData.store.store_mobile }}</div>
						</el-form-item>
						<el-form-item :label="t('tradeTime')">
							<div class="input-width">{{ formData.store.trade_time }}</div>
						</el-form-item>
					</template>
				</div>
				<h3 class="panel-title">{{ t('orderStatus') }}</h3>
				<div class="mb-[20px]">
					<p>
						<span class="ml-[30px] text-[14px] mr-[20px]">{{ t('orderStatus') }}：</span>
						<span class="text-[14px]">{{ formData.status_name.name }}</span>
					</p>
				</div>
				<h3 class="panel-title">{{ t('settlementInfo') }}</h3>
				<div class="px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('shopMoney')">
						<div class="input-width">{{ formData.shop_money }}</div>
					</el-form-item>
					<el-form-item :label="t('mallMoney')">
						<div class="input-width">{{ formData.mall_money }}</div>
					</el-form-item>
					<el-form-item :label="t('shopRefundMoney')">
						<div class="input-width">{{ formData.shop_refund_money }}</div>
					</el-form-item>
					<el-form-item :label="t('mallRefundMoney')">
						<div class="input-width">{{ formData.mall_refund_money }}</div>
					</el-form-item>
					<el-form-item :label="t('mallCouponMoney')">
						<div class="input-width">{{ formData.mall_coupon_money }}</div>
					</el-form-item>
					<el-form-item :label="t('mallRefundCouponMoney')">
						<div class="input-width">{{ formData.mall_refund_coupon_money }}</div>
					</el-form-item>
					<el-form-item v-if="formData.is_install_fenxiao" :label="t('shopCommission')">
						<div class="input-width">{{ formData.shop_commission }}</div>
					</el-form-item>
					<el-form-item v-if="formData.is_install_fenxiao" :label="t('shopRefundCommission')">
						<div class="input-width">{{ formData.shop_refund_commission }}</div>
					</el-form-item>
					<el-form-item v-if="formData.supply_money" :label="t('supplyMoney')">
						<div class="input-width">{{ formData.supply_money }}</div>
					</el-form-item>
					<el-form-item v-if="formData.supply_refund_money" :label="t('supplyRefundMoney')">
						<div class="input-width">{{ formData.supply_refund_money }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title" v-if="formData.form_record_show==1">{{ t('formDetail') }}</h3>
                <div class="row-bg px-[30px] mb-[20px]" v-if="formData.form_record_show==1">
                    <div class="grid grid-cols-3 gap-x-[20px]">
                        <el-form-item v-for="(field, fieldKey) in tableData[0]?.recordsFieldList || []" :key="fieldKey" :label="field.field_name" min-width="200">
                            <component :is="tableData[0]?.recordsFieldList[fieldKey].detailComponent" :data="tableData[0]?.recordsFieldList[fieldKey]" />
                        </el-form-item>
                    </div>
                </div>
				<h3 class="panel-title">{{ t('goodsDetail') }}</h3>
				<el-table :data="formData.order_goods" size="large">
					<el-table-column :label="t('goodsName')" align="left" width="300">
						<template #default="{ row }">
							<div class="flex">
								<div class="flex items-center flex-shrink-0">
									<img class="w-[50px] h-[50px] mr-[10px]" :src="img(row.goods_image)" />
								</div>
								<div class="flex flex-col">
									<p :title="row.goods_name" class="multi-hidden text-[14px] cursor-pointer">{{ row.goods_name }}</p>
									<span class="text-[12px] text-[#999]">{{ row.sku_name }}</span>
								</div>
							</div>
						</template>
					</el-table-column>
					<el-table-column :label="t('deliveryState')" min-width="50" align="left">
						<template #default="{ row }">
							{{row.delivery_status_name}}
						</template>
					</el-table-column>
					<el-table-column :label="t('deliveryType')" min-width="50" align="left">
						<template #default="{ row }">
							{{row.delivery_type_name || '--'}}
						</template>
					</el-table-column>
					<el-table-column v-if="formData.is_refund_status_show" :label="t('refundState')" min-width="50" align="left">
						<template #default="{ row }">
							<div class="cursor-pointer flex items-center flex-wrap"  v-if="row.status != 1" @click.stop="refundEventLink(row)" >
								<span class="mr-[3px] text-[13px]">退款金额￥{{ Number(row.refund_money) ? row.refund_money : row.apply_money }}</span>
								<div class="text-[var(--el-color-primary)] text-[13px]">{{ row.status_name || '--' }}
									<span class="iconfont iconxiangyoujiantou !text-[10px] text-[#888]"></span>
								</div>
							</div>
							<span v-else class="text-[13px]">--</span>
						</template>
					</el-table-column>
					<el-table-column prop="price" :label="t('price')" min-width="50" align="left" />
					<el-table-column prop="num" :label="t('num')" min-width="50" align="right" />
					<el-table-column label="操作" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" v-if="row.form_record_show==1" link @click="formDetailEvent(row.form_record_id)">{{ t('formDetail') }}</el-button>
                        </template>
                    </el-table-column>
				</el-table>

				<div class="py-[12px] px-[16px] border-b border-color flex justify-end">
					<div class="w-[310px] flex flex-col text-right">
						<div class="flex mb-[10px]">
							<div class="text-base flex-1">{{ t('goodsMoney') }}</div>
							<div class="text-base flex-1 pl-[30px]">{{ formData.goods_money }}</div>
						</div>
						<div class="flex mb-[10px]">
							<div class="text-base flex-1">{{ t('discountMoney') }}</div>
							<div class="text-base flex-1 pl-[30px]">{{ formData.discount_money }}</div>
						</div>
						<div class="flex mb-[10px]">
								<div class="text-base flex-1">{{ t('mallDiscountMoney') }}</div>
								<div class="text-base flex-1 pl-[30px]">{{ formData.mall_discount_money }}</div>
							</div>
						<div class="flex mb-[10px]">
							<div class="text-base flex-1">{{ t('deliveryMoney') }}</div>
							<div class="text-base flex-1 pl-[30px]">{{ formData.delivery_money }}</div>
						</div>
						<div class="flex mb-[10px]">
							<div class="text-base flex-1">{{ t('orderMoney') }}</div>
							<div class="text-base flex-1 pl-[30px] text-[red]">{{ formData.order_money }}</div>
						</div>
						<div class="flex mb-[10px]">
							<div class="text-base flex-1">{{ t('refundMoney') }}</div>
							<div class="text-base flex-1 pl-[30px] text-[red]">{{ formData.refund_money }}</div>
						</div>
					</div>
				</div>
				<h3 class="mt-[50px] mb-[20px]" v-if="formData.order_log.length > 0">{{ t('operateLog') }}</h3>
				<div class="mb-[100px]" style="min-height: 100px" v-if="formData.order_log.length > 0">
					<div class="flex" v-for="(items, index) in formData.order_log" :key="index">
						<div class="mr-[20px] min-w-[71px]">
							<div class="leading-[1] w-full text-[14px] w-[100px] flex justify-end" v-if="items.create_time">
								{{ items.create_time.split(' ')[0] }}
							</div>
							<div class="leading-[1] w-full text-[14px]  w-[100px] flex justify-end mt-[15px]" v-if="items.create_time">
								{{ items.create_time.split(' ')[1] }}
							</div>
						</div>
						<div>
							<div class="w-[16px] h-[16px] flex items-center bg-[#D1EBFF] border-[1px] border-[#0091FF] rounded-[999px]">
								<div class="w-[8px] h-[8px] mx-auto bg-[#0091FF] rounded-[999px]"></div>
							</div>
							<div v-if="index + 1 != formData.order_log.length" class="w-[2px] h-[50px] bg-[#D1EBFF] mx-auto">
							</div>
						</div>
						<div>
							<div class="leading-[1] ml-[20px] text-[14px]">
								{{ items.main_type_name }}{{ items.main_name }}
							</div>
							<div class="leading-[1] ml-[20px] text-[14px] mt-[15px]">
								<span>{{ items.type_name }}</span>
								<span class="ml-[10px]">{{items.content}}</span>
							</div>
						</div>
					</div>
				</div>
			</el-card>
			<el-card class="box-card !border-none relative" shadow="never" v-if="!loading && !formData">
				<el-empty :description="t('orderInfoEmpty')" />
			</el-card>
		</el-form>
		<form-detail ref="formDetailDialog" />
	</div>
</template>

<script lang="ts" setup>
import { ref, defineAsyncComponent } from 'vue'
import { t } from '@/lang'
import { getOrderDetail } from '@/addon/mall/api/admin/order'
import { useRoute, useRouter } from 'vue-router'
import { img } from '@/utils/common'
import { getFormRecordsInfo } from '@/app/api/diy_form_admin'
import FormDetail from '@/addon/mall/views/admin/order/components/form-detail.vue'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const orderId: number = parseInt(route.query.order_id as string)
const loading = ref(true)

const formData: Record<string, any> | null = ref(null)
const tableData = ref([])
const modules: any = import.meta.glob('@/**/*.vue')
const formDetailDialog: any = ref(null)
const formDetailEvent = (data: any) => {
    formDetailDialog.value.show(data)
}

const setFormData = async (orderId: number = 0) => {
    loading.value = true
    formData.value = null
	try {
		await getOrderDetail(orderId).then(({ data }) => {
			formData.value = data
		}).catch(() => {
		})
		// 获取订单的表单详情
        if (formData.value.form_record_id) {
            try {
                const orderFormResponse = await getFormRecordsInfo(formData.value.form_record_id);
                formData.value.form_details = orderFormResponse.data;
                if (formData.value.form_details && formData.value.form_details.recordsFieldList) {
                    const recordsFieldList = formData.value.form_details.recordsFieldList;
                    for (const key in recordsFieldList) {
                        if (modules[recordsFieldList[key].detailComponent]) {
                            recordsFieldList[key].detailComponent = defineAsyncComponent(modules[recordsFieldList[key].detailComponent]);
                        } else {
                            delete recordsFieldList[key];
                        }
                    }
                    tableData.value = [formData.value.form_details];
                }
            } catch (error) {
            }
        }
	} catch (error) {
    } finally {
        loading.value = false
    }
}
if (orderId) setFormData(orderId)
else loading.value = false

const refundEventLink = (row:any) => {
    const routeUrl = router.resolve({
        path: '/mall/order/refund/detail',
        query: { refund_id: row.refund_id }
    })
    window.open(routeUrl.href, '_blank')
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
