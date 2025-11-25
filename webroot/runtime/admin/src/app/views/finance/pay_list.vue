<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">

			<div class="flex justify-between items-center">
				<span class="text-page-title">{{ pageName }}</span>
			</div>
			<el-card class="box-card !border-none base-bg !px-[35px]" shadow="never">
				<el-row class="flex">
					<el-col :span="6" class="min-w-[100px] mb-[15px]" v-for="(item, index) in payStat">
						<div class="statistic-card">
							<el-statistic :value="item ? item.toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="footer-item text-[14px] text-[#666]">
									<span>{{ t(index) }}</span>
								</div>
							</div>
						</div>
					</el-col>
				</el-row>
			</el-card>
			<!-- <el-card class="box-card !border-none base-bg !px-[35px]" shadow="never">
				<el-row class="flex">
					<el-col :span="8" class="min-w-[100px]" v-for="(item, index) in channelStat">
						<div class="statistic-card">
							<el-statistic :value="item ? item.toFixed(2) : '0.00'"></el-statistic>
							<div class="statistic-footer">
								<div class="footer-item text-[14px] text-[#666]">
									<span>{{ t(index) }}</span>
								</div>
							</div>
						</div>
					</el-col>
				</el-row>
			</el-card> -->
			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" :model="payListTable.searchParam" ref="searchFormRef">
					<el-form-item :label="t('outTradeNo')" prop="out_trade_no">
						<el-input v-model="payListTable.searchParam.out_trade_no"
							:placeholder="t('outTradeNoPlaceholder')" />
					</el-form-item>

					<el-form-item :label="t('typeName')" prop="type">
						<el-select v-model="payListTable.searchParam.type" clearable class="input-width"
							:placeholder="t('typeNamePlaceholder')">
							<el-option :label="t('selectPlaceholder')" value="" />
							<el-option :label="item.name" :value="key" v-for="(item, key) in payTypeList" :key="key" />
						</el-select>
					</el-form-item>

					<el-form-item :label="t('channelName')" prop="channel">
						<el-select v-model="payListTable.searchParam.channel" clearable class="input-width"
							:placeholder="t('channelNamePlaceholder')">
							<el-option :label="t('selectPlaceholder')" value="" />
							<el-option :label="item" :value="key" v-for="(item, key) in channelList" :key="key" />
						</el-select>
					</el-form-item>
					<el-form-item :label="t('createTime')" prop="create_time">
						<el-date-picker v-model="payListTable.searchParam.create_time" type="datetimerange"
							value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
							:end-placeholder="t('endDate')" />
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="loadPayList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
						<el-button type="primary" @click="exportEvent">{{ t('export') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="payListTable.data" size="large" v-loading="payListTable.loading">
					<template #empty>
						<span>{{ !payListTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="out_trade_no" :label="t('outTradeNo')" min-width="120" />
					<el-table-column prop="body" :label="t('body')" min-width="120" />
					<el-table-column :show-overflow-tooltip="true" :label="t('mainInfo')" align="left" min-width="140">
						<template #default="{ row }">
							<div class="flex items-center cursor-pointer " @click="toMember(row.member.member_id)">
								<img class="w-[50px] h-[50px] mr-[10px]" v-if="row.member.headimg"
									:src="img(row.member.headimg)" alt="">
								<img class="w-[50px] h-[50px] mr-[10px]" v-else
									src="@/app/assets/images/default_headimg.png" alt="">
								<div class="flex flex flex-col">
									<span>{{ row.member.nickname || '' }}</span>
									<span>{{ row.member.mobile || '' }}</span>
								</div>
							</div>
						</template>
					</el-table-column>
					<el-table-column prop="money" :label="t('money')" min-width="120" align="right" />
					<el-table-column prop="type_name" :label="t('typeName')" min-width="120" />
					<el-table-column prop="channel_name" :label="t('channelName')" min-width="120" />
					<el-table-column prop="status_name" :label="t('statusName')" min-width="120" />
					<el-table-column :label="t('createTime')" min-width="150" align="center">
						<template #default="{ row }">
							{{ row.create_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('payTime')" min-width="150" align="center">
						<template #default="{ row }">
							{{ row.pay_time || '' }}
						</template>
					</el-table-column>
					<el-table-column :label="t('operation')" align="right" fixed="right" min-width="120">
						<template #default="{ row }">
							<el-button type="primary" link @click="detailEvent(row)">{{ t('detail') }}</el-button>
						</template>
					</el-table-column>

				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="payListTable.page" v-model:page-size="payListTable.limit"
						layout="total, sizes, prev, pager, next, jumper" :total="payListTable.total"
						@size-change="loadPayList()" @current-change="loadPayList" />
				</div>
			</div>
			<el-dialog v-model="showDialog" :title="t('accountDetail')" width="550px" :destroy-on-close="true">
				<el-form :model="formData" label-width="110px" ref="formRef" class="page-form">
					<el-form-item :label="t('outTradeNo')">
						<div class="input-width"> {{ formData.out_trade_no }} </div>
					</el-form-item>
					<el-form-item :label="t('body')">
						<div class="input-width"> {{ formData.body }} </div>
					</el-form-item>
					<!-- <el-form-item :label="t('mainInfo')">
						<div class="flex items-center cursor-pointer " @click="toMember(formData.member.member_id)">
							<img class="w-[50px] h-[50px] mr-[10px]" v-if="formData.member.headimg"
								:src="img(formData.member.headimg)" alt="">
							<img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png"
								alt="">
							<div class="flex flex flex-col">
								<span>{{ formData.member.nickname || '' }}</span>
								<span>{{ formData.member.mobile || '' }}</span>
							</div>
						</div>
					</el-form-item> -->
					<el-form-item :label="t('money')">
						<div class="input-width"> {{ formData.money }} </div>
					</el-form-item>
					<el-form-item :label="t('typeName')">
						<div class="input-width"> {{ formData.type_name }} </div>
					</el-form-item>
					<el-form-item :label="t('channelName')">
						<div class="input-width"> {{ formData.channel_name }} </div>
					</el-form-item>
					<el-form-item :label="t('statusName')">
						<div class="input-width"> {{ formData.status_name }} </div>
					</el-form-item>
					<el-form-item :label="t('createTime')">
						<div class="input-width"> {{ formData.create_time }} </div>
					</el-form-item>
					<el-form-item :label="t('payTime')">
						<div class="input-width"> {{ formData.pay_time }} </div>
					</el-form-item>
				</el-form>

				<template #footer>
					<span class="dialog-footer">
						<el-button type="primary" @click="showDialog = false">{{ t('confirm') }}</el-button>
					</span>
				</template>
			</el-dialog>
			<export-sure ref="exportSureDialog" :show="flag" type="pay" :searchParam="payListTable.searchParam"
				@close="handleClose" />
		</el-card>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { getPayList, getPayTypeStat, getChannelStat, getPayTypeList, getChannelList } from '@/app/api/site'
import type { FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { img } from '@/utils/common'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const payListTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        site_id: '',
        type: '',
        channel: '',
        money: '',
        out_trade_no: '',
        create_time: ''
    }
})

const searchFormRef = ref<FormInstance>()

// 去空操作
watch(() => payListTable.searchParam.out_trade_no, (nval) => {
    payListTable.searchParam.out_trade_no = nval.trim();
})

/**
 * 获取支付记录列表
 */
const loadPayList = (page: number = 1) => {
    payListTable.loading = true
    payListTable.page = page

    getPayList({
        page: payListTable.page,
        limit: payListTable.limit,
        ...payListTable.searchParam
    }).then(res => {
        payListTable.loading = false
        payListTable.data = res.data.data
        payListTable.total = res.data.total
    }).catch(() => {
        payListTable.loading = false
    })
}
loadPayList()

// 获取支付方式
const payTypeList = ref<Array<Object | any>>([])
const getPayTypeFn = async () => {
    payTypeList.value = await (await getPayTypeList()).data
}
getPayTypeFn()

// 获取支付渠道
const channelList = ref<Array<Object | any>>([])
const getChannelFn = async () => {
    channelList.value = await (await getChannelList()).data
}
getChannelFn()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadPayList()
}

/**
 * 支付方式统计
 */
interface PayStat {
	wechatpay: Number,
	alipay: Number,
	balancepay: Number,
	friendspay: Number
}

const payStat = ref<PayStat>({
    wechatpay: 0,
    alipay: 0,
    balancepay: 0,
    friendspay: 0
})
const getPayStatData = async () => {
    payStat.value = await (await getPayTypeStat()).data
}
getPayStatData()

/**
 * 支付渠道统计
 */
// interface ChannelStat { }

// const channelStat = ref<ChannelStat>()
// const getChannelStatData = async () => {
// 	channelStat.value = await (await getChannelStat()).data
// }
// getChannelStatData()

/**
 * 详情
 */
const showDialog = ref(false)
const formData = ref({
    out_trade_no: '',
    body: '',
    trade_no: '',
    type_name: '',
    channel_name: '',
    status_name: '',
    money: 0,
    pay_time: '',
    create_time: '',
    member: {
        member_id: 0,
        nickname: '',
        headimg: '',
        mobile: ''
    }
})
const detailEvent = (info: any) => {
    showDialog.value = true
    formData.value = info
}

/**
 * 会员详情
 */
const toMember = (memberId: number) => {
    router.push(`/member/detail?id=${memberId}`)
}

/**
 * 支付记录导出
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
