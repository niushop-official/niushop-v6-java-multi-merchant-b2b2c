<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none base-bg !px-[35px]" shadow="never">
                <el-row class="flex">
                    <el-col :span="12">
                        <div class="statistic-card">
                            <el-statistic
                                :value="accountInfo.withdrawal_money ? parseFloat(accountInfo.withdrawal_money).toFixed(2) : '0.00'"></el-statistic>
                            <div class="statistic-footer">
                                <div class="text-[14px] text-[#666]">
                                    <span>{{ t('totalTransfered') }}</span>
                                </div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="statistic-card">
                            <el-statistic
                                :value="accountInfo.money_cash_outing ? parseFloat(accountInfo.money_cash_outing).toFixed(2) : '0.00'"></el-statistic>
                            <div class="statistic-footer">
                                <div class="text-[14px] text-[#666]">
                                    <span>{{ t('totalCashOuting') }}</span>
                                </div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </el-card>

            <el-card class="box-card !border-none mb-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="orderTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('cashOutNumber')" prop="cash_out_no">
                        <el-input v-model="orderTableData.searchParam.cash_out_no" class="w-[240px]"
                            :placeholder="t('cashOutNumberPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('cashOutStatus')" prop="status">
                        <el-select v-model="orderTableData.searchParam.status" clearable class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item" :value="key" v-for="(item, key) in cashOutStatusList" :key="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('cashOutMethod')" prop="transfer_type">
                        <el-select v-model="orderTableData.searchParam.transfer_type" clearable class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item.name" :value="key" v-for="(item, key) in transfertype" :key="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('applyTime')" prop="create_time">
                        <el-date-picker v-model="orderTableData.searchParam.create_time" type="datetimerange"
                            value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                            :end-placeholder="t('endDate')" />
                    </el-form-item>
                    <el-form-item :label="t('transferTime')" prop="transfer_time">
                        <el-date-picker v-model="orderTableData.searchParam.transfer_time" type="datetimerange"
                            value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                            :end-placeholder="t('endDate')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadOrderList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                        <el-button type="primary" @click="exportEvent">{{ t('export') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="orderTableData.data" size="large" v-loading="orderTableData.loading">
                    <template #empty>
                        <span>{{ !orderTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="cash_out_no" :label="t('cashOutNumber')" align="center" min-width="140" />
                    <el-table-column prop="transfer_account" :label="t('bankAccountNo')" align="center" min-width="140" />
                    <el-table-column prop="transfer_type_name" :label="t('cashOutMethod')" align="center" min-width="140" />
                    <el-table-column prop="apply_money" :label="t('applicationForWithdrawalAmount')" align="center"
                        min-width="140" />
                    <el-table-column prop="status_name" :label="t('cashOutStatus')" align="center" min-width="100" />
                    <el-table-column :label="t('transferVoucher')" align="center" min-width="100">
                        <template #default="{ row }">
                            <img class="w-[50px] mx-auto" v-if="row.transfer_voucher" :src="img(row.transfer_voucher)"
                                alt="" object-fit="contain">
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('applyTime')" min-width="140" align="center">
                        <template #default="{ row }">
                            {{ row.create_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" align="right" fixed="right" width="230">
                        <template #default="{ row }">
                            <el-button v-if="(row.status == 2 || row.status == 4) && row.transfer_type == 4" type="primary" link @click="cashOut(row.id)">{{ t('cashOut') }}</el-button>
                            <el-button type="primary" link @click="detailEvent(row.id)">{{ t('detail') }}</el-button>
                            <el-button v-if="row.status == 1 || row.status == 2" type="primary" link
                                @click="transferCancel(row.id)">{{ t('transferCancel') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="orderTableData.page" v-model:page-size="orderTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="orderTableData.total"
                        @size-change="loadOrderList()" @current-change="loadOrderList" />
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
                <el-form-item :label="t('applicationForWithdrawalAmount')">
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
        <export-sure ref="exportSureDialog" :show="flag" type="site_shop_cash_out" isSite="true" :searchParam="orderTableData.searchParam" @close="handleClose" />
        <transfer-popup ref="transferDialogRef" @closeCallback="loadOrderList"/>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getCashOutList, getShopAccountInfo, getTransfertype, transferShopCashCancel, getShopCashOutInfo, getShopCashOutStatus, getShopCashOutTransfer } from '@/app/api/shop_site'
import { img } from '@/utils/common'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import TransferPopup from '@/app/views/shop/site/account/components/transfer-popup.vue'

const cashOutStatusList = ref([])
const checkStatusList = async () => {
    cashOutStatusList.value = await (await getShopCashOutStatus()).data
}
checkStatusList()

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const orderTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        create_time: [],
        status: '',
        cash_out_no: '',
        transfer_time: '',
        transfer_type: ''
    }
})

// 获取转账方式
const transfertype = ref<Array<Object | any>>([])
const getTransfertypeFn = async () => {
    transfertype.value = await (await getTransfertype()).data
}
getTransfertypeFn()

const searchFormRef = ref<FormInstance>()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadOrderList()
}

/**
 * 获取提现列表
 */
const loadOrderList = (page: number = 1) => {
    orderTableData.loading = true
    orderTableData.page = page

    getCashOutList({
        page: orderTableData.page,
        limit: orderTableData.limit,
        ...orderTableData.searchParam
    }).then(res => {
        orderTableData.loading = false
        orderTableData.data = res.data.data
        orderTableData.total = res.data.total
    }).catch(() => {
        orderTableData.loading = false
    })
}
loadOrderList()

const accountInfo = ref({})
const setAccountInfo = () => {
    getShopAccountInfo().then(res => {
        accountInfo.value = res.data
    })
}
setAccountInfo()
/**
 * 详情
 * @param data
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
const detailEvent = (id: any) => {
    cashOutLoading.value = true
    getShopCashOutInfo(id).then(res => {
        cashOutInfo.value = res.data
        cashOutShowDialog.value = true
        cashOutLoading.value = false
    }).catch(() => {
        loadOrderList()
    })
}
// 取消提现
const transferCancel = (id: number) => {
    ElMessageBox.confirm(t('transferCancelTip'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        transferShopCashCancel(id).then(res => {
            loadOrderList()
        })
    }).catch(() => { })
}
/**
 * 提现记录导出
 */
const exportSureDialog = ref(null)
const flag = ref(false)
const handleClose = (val) => {
    flag.value = val
}
const exportEvent = (data: any) => {
    flag.value = true
}

// 提现
const transferUrl = ref('')
const transferDialogRef = ref()
const cashOut = (id: number) => {
    getShopCashOutTransfer(id).then(res => {
        transferUrl.value = res.data
        transferDialogRef.value.show(transferUrl.value)
    })
}

</script>

<style lang="scss" scoped></style>
