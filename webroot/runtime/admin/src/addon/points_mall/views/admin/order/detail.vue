<template>
	<div class="main-container">
		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/points_mall/order/index' })" />
        </el-card>
		<el-form :model="formData" label-width="140px" ref="formRef" class="page-form" v-loading="loading" label-position="left">
			<el-card class="box-card !border-none relative" shadow="never" v-if="formData">
				<h3 class="panel-title">{{ t('orderInfo') }}</h3>
				<div class="row-bg px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('orderNo')">
						<div class="input-width">{{ formData.order_no }}</div>
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
						<div class="input-width line-feed">{{ formData.member_remark ?? '--' }}</div>
					</el-form-item>
					<el-form-item :label="t('notes')">
						<div class="input-width line-feed">{{ formData.shop_remark ?? '--' }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title">{{ t('配送信息') }}</h3>
				<div class="row-bg px-[30px] mb-[20px] multiple-layout">
					<el-form-item :label="t('deliveryType')">
						<div class="input-width">{{ formData.delivery_type_name }}</div>
					</el-form-item>
					<el-form-item :label="t('takerName')" v-if="formData.delivery_type == 'express'">
						<div class="input-width">{{ formData.taker_name }}</div>
					</el-form-item>
					<el-form-item :label="t('takerMobile')" v-if="formData.delivery_type == 'express'|| formData.delivery_type == 'virtual'">
						<div class="input-width">{{ formData.taker_mobile }}</div>
					</el-form-item>
					<el-form-item :label="t('takerFullAddress')" v-if="formData.delivery_type == 'express'">
						<div class="input-width">{{ formData.taker_full_address }}</div>
					</el-form-item>
				</div>
				<h3 class="panel-title">{{ t('orderInfo') }}</h3>
				<div class="mb-[20px]">
					<p>
						<span class="ml-[30px] text-[14px] mr-[20px]">{{ t('orderStatus') }}：</span>
						<span class="text-[14px]">{{ formData.status_name.name }}</span>
					</p>
					<div class="flex mt-[10px]">
						<span class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#ff7f5b] bg-[#fff0e5] cursor-pointer" @click="setNotes">{{ t('notes') }}</span>
						<span class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#5c96fc] bg-[#ebf3ff] cursor-pointer" @click="delivery" v-if="formData.status == 2">{{ t('delivery') }}</span>
						<span class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#5c96fc] bg-[#ebf3ff] cursor-pointer" @click="close" v-if="formData.status == 1">{{ t('close') }}</span>
						<span class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#5c96fc] bg-[#ebf3ff] cursor-pointer" @click="finish" v-if="formData.status == 3">{{ t('finish') }}</span>
						<span class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#5c96fc] bg-[#ebf3ff] cursor-pointer" @click="orderEditAddressFn" v-if="(formData.status == 1 || formData.status == 2) && formData.delivery_type != 'virtual'">{{ t('editAddress') }}</span>
						<div class="flex" v-if="formData.order_delivery">
							<template v-for="(item, index) in formData.order_delivery" :key="index">
								<span v-if="item.delivery_type == 'express' && item.sub_delivery_type == 'express'" class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#ff7f5b] bg-[#fff0e5] cursor-pointer" @click="packageEvent(item.code, formData.taker_mobile)">{{ t('package') }}{{ index + 1 }}</span>
								<span v-if="item.delivery_type == 'express' && item.sub_delivery_type == 'none_express'" class="text-[14px] px-[15px] py-[5px] ml-[30px] text-[#ff7f5b] bg-[#fff0e5] cursor-pointer" @click="packageEvent(item.code, formData.taker_mobile)">{{ t('noLogisticsRequired') }}</span>
							</template>
						</div>
					</div>
					<div class="flex ml-[30px] mt-[15px]">
						<span class="text-[14px] text-[#ff7f5b]">{{ t('remind') }}：</span>
						<div class="ml-[10px]">
							<p class="text-[14px] text-[#a4a4a4]">{{ t('remindTips1') }}</p>
							<p class="text-[14px] text-[#a4a4a4]">{{ t('remindTips2') }}</p>
							<p class="text-[14px] text-[#a4a4a4]">{{ t('remindTips3') }}</p>
						</div>
					</div>
				</div>

				<h3 class="panel-title">{{ t('goodsDetail') }}</h3>
				<el-table :data="formData.order_goods" size="large">
					<el-table-column :label="t('goodsName')" align="left" width="300">
						<template #default="{ row }">
							<div class="flex">
								<div class="flex items-center shrink-0">
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
					<el-table-column  v-if="formData.is_refund_status_show" :label="t('refundState')" min-width="50" align="left">
						<template #default="{ row }">
							<div class="cursor-pointer flex items-center flex-wrap"  v-if="row.status != 1" @click.stop="refundEventLink(row)" >
								<span class="mr-[3px] text-[13px]">退款金额￥{{ row.apply_money }}</span>
								<div class="text-[var(--el-color-primary)] text-[13px]">{{ row.status_name || '--' }}
									<span class="iconfont iconxiangyoujiantou !text-[10px] text-[#888]"></span>
								</div>
							</div>
							<span v-else class="text-[13px]">--</span>
						</template>
					</el-table-column>
					<el-table-column :label="t('price')" min-width="50" align="left">
						<template #default="{ row }">
							<span>{{ parseInt(row.goods_point) }}积分</span>
							<span>+￥{{ row.price }}</span>
						</template>
					</el-table-column>
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
							<div class="text-base flex-1 pl-[30px]">
								<span>{{ parseInt(formData.goods_point) }}积分</span>
								<span>+￥{{ formData.goods_money }}</span>
							</div>
						</div>
						<div class="flex mb-[10px]">
							<div class="text-base flex-1">{{ t('orderMoney') }}</div>
							<div class="text-base flex-1 pl-[30px] text-[red]">
								<span>{{ parseInt(formData.point) }}积分</span>
								<span>+￥{{ formData.order_money }}</span>
							</div>
						</div>
						<div class="flex mb-[10px]" v-if="Number(formData.refund_money)">
							<div class="text-base flex-1">{{ t('refundMoney') }}</div>
							<div class="text-base flex-1 pl-[30px] text-[red]">{{ formData.refund_money }}</div>
						</div>
					</div>
				</div>
				<h3 class="mt-[50px] mb-[20px]" v-if="formData.order_log.length > 0">{{ t('operateLog') }}</h3>
				<div class="mb-[100px]" style="min-height: 100px" v-if="formData.order_log.length > 0">
					<div class="flex" v-for="(items, index) in formData.order_log" :key="index">
						<div class="mr-[20px] min-w-[71px]">
							<div class="leading-[1] w-full text-[14px] w-[100px] flex justify-end">
								{{ items.create_time && items.create_time.split(' ')[0] }}
							</div>
							<div class="leading-[1] w-full text-[14px]  w-[100px] flex justify-end mt-[15px]">
								{{ items.create_time && items.create_time.split(' ')[1] }}
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
		<delivery-action ref="deliveryActionDialog" @complete="setFormData(orderId)" />
		<order-notes ref="orderNotesDialog" @complete="setFormData(orderId)" />
		<delivery-package ref="packageDialog" />
		<order-edit-address ref="orderEditAddressDialog" @complete="setFormData(orderId)"/>
        <form-detail ref="formDetailDialog" />
	</div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getOrderDetail, orderClose, orderFinish } from '@/addon/points_mall/api/admin/order'
import DeliveryAction from '@/addon/points_mall/views/admin/order/components/delivery-action.vue'
import orderEditAddress from '@/addon/points_mall/views/admin/order/components/order-edit-address.vue'
import OrderNotes from '@/addon/points_mall/views/admin/order/components/order-notes.vue'
import deliveryPackage from '@/addon/points_mall/views/admin/order/components/delivery-package.vue'
import { useRoute, useRouter } from 'vue-router'
import { img } from '@/utils/common'
import { ElMessageBox } from 'element-plus'
import { cloneDeep } from 'lodash-es'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const orderId: number = parseInt(route.query.order_id as string)
const loading = ref(true)

const formData: Record<string, any> | null = ref(null)
const formDetailDialog: any = ref(null)
const formDetailEvent = (data: any) => {
    formDetailDialog.value.show(data)
}

const setFormData = async (orderId: number = 0) => {
    loading.value = true
    formData.value = null
    try {
        const { data } = await getOrderDetail(orderId)
        formData.value = data

        let refundOrderNum = 0

        formData.value.order_goods.forEach((orderItem: any) => {
            if (orderItem.is_enable_refund == 1) {
                refundOrderNum++
            }
        })
        formData.value.is_refund_show = refundOrderNum > 0
    } catch (error) {
    } finally {
        loading.value = false
    }
}

if (orderId) setFormData(orderId)
else loading.value = false

const close = () => {
    ElMessageBox.confirm(t('orderCloseTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }).then(() => {
        orderClose(orderId).then(() => {
            setFormData(orderId)
        })
    })
}

const deliveryActionDialog: Record<string, any> | null = ref(null)
/**
 * 发货
 */
const delivery = () => {
    deliveryActionDialog.value.setFormData(formData.value)
    deliveryActionDialog.value.showDialog = true
}

const orderNotesDialog: Record<string, any> | null = ref(null)
/**
 * 设置备注
 */
const setNotes = () => {
    orderNotesDialog.value.setFormData(formData.value)
    orderNotesDialog.value.showDialog = true
}
// 订单完成
const finish = () => {
    ElMessageBox.confirm(t('orderFinishTips'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }
    ).then(() => {
        orderFinish(orderId).then(() => {
            setFormData(orderId)
        })
    })
}

const packageDialog: Record<string, any> | null = ref(null)
/**
 * 发货
 */
const packageEvent = (id: number, mobile: number) => {
    packageDialog.value.setFormData(id, mobile)
    packageDialog.value.showDialog = true
}

const repeat = ref(false)

/**
 * 修改地址
 */
const orderEditAddressDialog :Record<string, any> | null = ref(null)
const orderEditAddressFn = async () => {
    let data = cloneDeep(formData.value)
    orderEditAddressDialog.value.showDialog = true
    orderEditAddressDialog.value.setFormData(data)
}


const memberEvent = (id: number) => {
    const routeUrl = router.resolve({
        path: '/member/detail',
        query: { id }
    })
    window.open(routeUrl.href, '_blank')
}

const refundEventLink = (row:any) => {
    const routeUrl = router.resolve({
        path: '/points_mall/order/refund/detail',
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

.line-feed {
	word-wrap: break-word;
}
</style>
