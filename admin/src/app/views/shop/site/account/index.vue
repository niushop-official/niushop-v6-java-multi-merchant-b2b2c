<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">
			<div class="flex justify-between items-center">
				<span class="text-page-title">{{ pageName }}</span>
			</div>
			<el-card class="box-card !border-none base-bg !px-[35px]" shadow="never">
				<el-row class="flex">
					<el-col :span="8" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic
								:value="accountInfo.money ? parseFloat(accountInfo.money).toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="text-[14px] text-[#666]">
									<span>{{ t('usableBalance') }}</span>
									<el-button type="primary" link @click="transferClick">{{ t('transfer') }}</el-button>
								</div>
							</div>
						</div>
					</el-col>
					<el-col :span="8" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic
								:value="`${accountInfo.withdrawal_money ? parseFloat(accountInfo.withdrawal_money).toFixed(2) : '0.00'}/${accountInfo.money_cash_outing ? parseFloat(accountInfo.money_cash_outing).toFixed(2) : '0.00'}`"></el-statistic>
							<div class="statistic-footer">
								<div class="text-[14px] text-[#666]">
									<span>{{ t('totalCashOuting') }}</span>
								</div>
							</div>
						</div>
					</el-col>
					<el-col :span="8" class="min-w-[100px]">
						<div class="statistic-card">
							<el-statistic
								:value="accountInfo.money_get ? parseFloat(accountInfo.money_get).toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="text-[14px] text-[#666]">
									<span>{{ t('storetotalPay') }}</span>
								</div>
							</div>
						</div>
					</el-col>
				</el-row>
			</el-card>
			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" label-width="70px" :model="shopAccountLogTable.searchParam" ref="searchFormRef" class="page-form">
					<el-form-item :label="t('tradeNo')" prop="related_id">
						<el-input v-model="shopAccountLogTable.searchParam.related_id"
							:placeholder="t('tradeNoPlaceholder')" clearable  class="search-input" />
					</el-form-item>
					<el-form-item :label="t('type')" class="items-center" prop="from_type">
						<el-select v-model="shopAccountLogTable.searchParam.from_type" class="search-input"
							:placeholder="t('accountTypePlaceholder')">
							<el-option  label="全部" value="all" />
							<el-option v-for="(item, index) in accountType" :key="index" :label="item.name" :value="index" />
						</el-select>
					</el-form-item>
					<el-form-item :label="t('createTime')" prop="create_time">
						<el-date-picker v-model="shopAccountLogTable.searchParam.create_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
							:end-placeholder="t('endDate')" class="search-input" />
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="loadShopAccountLogList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
						<el-button type="primary" @click="exportEvent">{{ t('export') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="shopAccountLogTable.data" size="large" v-loading="shopAccountLogTable.loading">
					<template #empty>
						<span>{{ !shopAccountLogTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="from_type_name" :label="t('type')" min-width="120" />
					<el-table-column prop="money" :label="t('money')" min-width="120" align="right" />
					<el-table-column :label="t('createTime')" min-width="150" align="center">
						<template #default="{ row }">
							{{ row.create_time || '' }}
						</template>
					</el-table-column>
					<el-table-column prop="memo" :label="t('remark')" min-width="120" />
					<el-table-column :label="t('operation')" align="right" fixed="right" width="230">
						<template #default="{ row }">
							<el-button type="primary" link @click="detailEvent(row.from_type, row.related_id)">{{ t('info')
							}}</el-button>
						</template>
					</el-table-column>
				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="shopAccountLogTable.page"
						v-model:page-size="shopAccountLogTable.limit" layout="total, sizes, prev, pager, next, jumper"
						:total="shopAccountLogTable.total" @size-change="loadShopAccountLogList()"
						@current-change="loadShopAccountLogList" />
				</div>
			</div>
		</el-card>
		<!-- 详情 -->
		<el-dialog v-model="cashOutShowDialog" :title="t('cashOutDetail')" width="500px" :destroy-on-close="true">
			<el-form :model="cashOutInfo" label-width="120px" ref="formRef" class="page-form" v-loading="cashOutLoading">
				<el-form-item :label="t('nickname')">
					<div class="input-width"> {{ cashOutInfo.siteInfo.site_name }} </div>
				</el-form-item>
				<el-form-item :label="t('contact')">
					<div class="input-width"> {{ cashOutInfo.siteInfo.phone }} </div>
				</el-form-item>
				<el-form-item :label="t('cashOutMethod')">
					<div class="input-width"> {{ cashOutInfo.transfer_type_name }} </div>
				</el-form-item>
				<div v-if="cashOutInfo.transfer_type == '1'">
					<el-form-item :label="t('bankCardName')">
						<div class="input-width"> {{ cashOutInfo.transfer_realname }} </div>
					</el-form-item>
					<el-form-item :label="t('bankName')">
						<div class="input-width"> {{ cashOutInfo.transfer_bank }} </div>
					</el-form-item>
					<el-form-item :label="t('bankCardNumber')">
						<div class="input-width"> {{ cashOutInfo.transfer_account }} </div>
					</el-form-item>
				</div>
				<div v-if="cashOutInfo.transfer_type == '2'">
					<el-form-item :label="t('alipayName')">
						<div class="input-width"> {{ cashOutInfo.transfer_realname }} </div>
					</el-form-item>
					<el-form-item :label="t('alipayAccount')">
						<div class="input-width"> {{ cashOutInfo.transfer_account }} </div>
					</el-form-item>
				</div>
				<el-form-item :label="t('price')">
					<div class="input-width"> {{ cashOutInfo.apply_money }}{{ t('yuan') }} </div>
				</el-form-item>
				<el-form-item :label="t('cashOutStatus')">
					<div class="input-width"> {{ cashOutInfo.status_name }} </div>
				</el-form-item>
				<el-form-item v-if="cashOutInfo.status == 3" :label="t('transferVoucher')">
					<div class="input-width">
						<el-image v-if="cashOutInfo.transfer_voucher" class="max-w-[250px] mx-auto"
							:src="img(cashOutInfo.transfer_voucher)" :preview-src-list="[img(cashOutInfo.transfer_voucher)]"
							fit="contain" />
					</div>
				</el-form-item>
				<el-form-item :label="t('applyTime')">
					<div class="input-width"> {{ cashOutInfo.create_time || '' }} </div>
				</el-form-item>
				<el-form-item :label="t('refuseReason')" v-if="cashOutInfo.refuse_reason">
					<div class="input-width"> {{ cashOutInfo.refuse_reason }} </div>
				</el-form-item>
			</el-form>
			<template #footer>
				<span class="dialog-footer">
					<el-button type="primary" @click="cashOutShowDialog = false">{{ t('confirm') }}</el-button>
				</span>
			</template>
		</el-dialog>
		<transfer ref="transferDialogRef" @complete="loadShopAccountLogList" @transfer="setAccountInfo" />
		<export-sure ref="exportSureDialog" :show="flag" type="shop_account_log" isSite="true"
			:searchParam="shopAccountLogTable.searchParam" @close="handleClose" />
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getShopAccountInfo, getShopAccountLog, getShopAccountTypeList, getShopCashOutInfo } from '@/app/api/shop_site'
import type { FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import transfer from '@/app/views/shop/site/account/components/transfer.vue'
const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const shopAccountLogTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        from_type: '',
        related_id: '',
        create_time: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取店铺账单记录列表
 */
const loadShopAccountLogList = (page: number = 1) => {
    shopAccountLogTable.loading = true
    shopAccountLogTable.page = page

    getShopAccountLog({
        page: shopAccountLogTable.page,
        limit: shopAccountLogTable.limit,
        ...shopAccountLogTable.searchParam
    }).then(res => {
        shopAccountLogTable.loading = false
        shopAccountLogTable.data = res.data.data
        shopAccountLogTable.total = res.data.total
    }).catch(() => {
        shopAccountLogTable.loading = false
    })
}
loadShopAccountLogList()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadShopAccountLogList()
}

const accountType = ref([])
const checkAccountType = () => {
    getShopAccountTypeList().then(res => {
        accountType.value = res.data
    })
}
checkAccountType()

const accountInfo = ref({})
const setAccountInfo = () => {
    getShopAccountInfo().then(res => {
        accountInfo.value = res.data
    })
}
setAccountInfo()

// 提现按钮
const transferDialogRef = ref({})
const transferClick = () => {
	transferDialogRef.value.setFormData({
		money: accountInfo.value.money
	})
    transferDialogRef.value.showDialog = true
}

/**
 * 账单详情
 */
const cashOutShowDialog = ref(false)
const cashOutInfo = ref({
    siteInfo: {
        site_name: '',
        phone: ''
    },
    nickname: '',
    account_type_name: '',
    transfer_type: '',
    transfer_type_name: '',
    transfer_realname: '',
    transfer_bank: '',
    transfer_account: '',
    apply_money: 0,
    service_money: 0,
    money: 0,
    status: 0,
    status_name: '',
    refuse_reason: '',
    transfer_voucher: '',
    create_time: '',
    transfer_time: '',
    audit_time: ''
})
const cashOutLoading = ref(false)

const detailEvent = (from_type: any, related_id: any) => {
    if (from_type == 'cash_out') {
        cashOutLoading.value = true
        getShopCashOutInfo(related_id).then(res => {
            cashOutInfo.value = res.data
            cashOutShowDialog.value = true
            cashOutLoading.value = false
        }).catch(() => {
            loadShopAccountLogList()
        })
    } else if (from_type == 'site_separate' || from_type == 'order' || from_type == 'coupon') {
        router.push(`/site/mall/order/detail?order_id=${related_id}`)
    } else if (from_type == 'supply_separate') {
        router.push(`/site/supply/order/detail?order_id=${related_id}`)
    }
}

/**
 * 财务流水导出
 */
const exportSureDialog = ref(null)
const flag = ref(false)
const handleClose = (val) => {
    flag.value = val
}
const exportEvent = (data: any) => {
    flag.value = true
}

</script>

<style lang="scss" scoped></style>
