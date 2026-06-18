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
                                :value="statistics.withdrawal_money ? parseFloat(statistics.withdrawal_money).toFixed(2) : '0.00'"></el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item text-[14px] text-[#666]">
                                    <span>{{ t('totalTransfered') }}</span>
                                </div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <div class="statistic-card">
                            <el-statistic
                                :value="statistics.money_cash_outing ? parseFloat(statistics.money_cash_outing).toFixed(2) : '0'"></el-statistic>
                            <div class="statistic-footer">
                                <div class="footer-item text-[14px] text-[#666]">
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

                    <el-form-item :label="t('shopInfo')" prop="keyword">
                        <el-input v-model="orderTableData.searchParam.keyword" class="w-[240px]"
                            :placeholder="t('shopInfoPlaceholder')" />
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
                    <el-form-item :label="t('auditTime')" prop="audit_time">
                        <el-date-picker v-model="orderTableData.searchParam.audit_time" type="datetimerange"
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

                    <el-table-column prop="order_no" :show-overflow-tooltip="true" :label="t('shopInfo')" align="center"
                        min-width="180">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer " @click="toShop(row.siteInfo.site_id)">
                                <img class="w-[50px] h-[50px] mr-[10px] flex-shrink-0" v-if="row.siteInfo.front_end_logo"
                                    :src="img(row.siteInfo.front_end_logo)" alt="">
                                <img class="w-[50px] h-[50px] mr-[10px] flex-shrink-0" v-else
                                    src="@/app/assets/images/site_logo.png" alt="">
                                <div class="flex flex-col items-start">
                                    <span>{{ row.siteInfo.site_name || '' }}</span>
                                    <span>{{ row.siteInfo.phone || '' }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="cash_out_no" :label="t('cashOutNumber')" align="center" min-width="180" />
                    <el-table-column prop="transfer_type_name" :label="t('cashOutMethod')" align="center" min-width="130" />
                    <el-table-column prop="apply_money" :label="t('applicationForWithdrawalAmount')" min-width="130"
                        align="center" />
                    <el-table-column prop="status_name" :label="t('cashOutStatus')" align="center" min-width="130">
                        <template #default="{ row }">
                            <div>{{ row.status_name }}</div>
                            <div v-if="row.status == 2 && row.transfer_type == '4'"
                                class="text-[12px] text-[var(--el-color-success)]">(等待商户收款)</div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('applyTime')" min-width="180" align="center">
                        <template #default="{ row }">
                            {{ row.create_time || '' }}
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('auditTime')" min-width="180" align="center">
                        <template #default="{ row }">
                            {{ row.audit_time || '' }}
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('transferTime')" min-width="180" align="center">
                        <template #default="{ row }">
                            {{ row.transfer_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('transferVoucher')" align="center" min-width="100">
                        <template #default="{ row }">
                            <el-image class="w-[50px] mx-auto" v-if="row.transfer_voucher" :src="img(row.transfer_voucher)"
                                :preview-src-list="[img(row.transfer_voucher)]" alt="" fit="cover">
                            </el-image>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" align="right" fixed="right" min-width="230">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row.id)">{{ t('detail') }}</el-button>
                            <template v-if="row.status == 1">
                                <el-button type="primary" link @click="auditApprovedEvent(row.id)">{{ t('approved')
                                }} </el-button>
                                <el-button type="primary" link @click="auditRefuseOpen(row.id)">{{ t('refuse')
                                }}</el-button>
                            </template>
                            <el-button v-if="row.status == 2 && row.transfer_type !== '4'" type="primary" link @click="transferEvent(row.id)">{{
                                t('transfer') }}</el-button>
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
                    <el-form-item :label="t('alipayCode')">
                        <div class="input-width"> <el-image class="w-[50px] mx-auto" :src="img(cashOutInfo.transfer_payment_code)" :preview-src-list="[img(cashOutInfo.transfer_payment_code)]" alt="" fit="cover"/> </div>
                    </el-form-item>
                </div>
                <div v-if="cashOutInfo.transfer_type == '3'">
                    <el-form-item :label="t('wechatName')">
                        <div class="input-width"> {{ cashOutInfo.transfer_realname }} </div>
                    </el-form-item>
                    <el-form-item :label="t('wechatAccount')">
                        <div class="input-width"> {{ cashOutInfo.transfer_account }} </div>
                    </el-form-item>
                    <el-form-item :label="t('wechatCode')">
                        <div class="input-width"> <el-image class="w-[50px] mx-auto" :src="img(cashOutInfo.transfer_payment_code)" :preview-src-list="[img(cashOutInfo.transfer_payment_code)]" alt="" fit="cover"/> </div>
                    </el-form-item>
                </div>
                <el-form-item :label="t('applicationForWithdrawalAmount')">
                    <div class="input-width"> {{ cashOutInfo.apply_money }} {{ t('yuan') }}</div>
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
                <el-form-item :label="t('auditTime')" v-if="cashOutInfo.audit_time">
                    <div class="input-width"> {{ cashOutInfo.audit_time || '' }} </div>
                </el-form-item>
                <el-form-item :label="t('transferTime')" v-if="cashOutInfo.transfer_time">
                    <div class="input-width"> {{ cashOutInfo.transfer_time || '' }} </div>
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

        <!-- 是否审核 -->
        <el-dialog v-model="auditShowDialog" :title="t('reasonsRefusal')" width="500px" :destroy-on-close="true">
            <el-form :model="auditParams" label-width="90px" ref="formRef" :rules="formRules" class="page-form">
                <el-form-item :label="t('reasonsRefusal')" prop="refuse_reason">
                    <el-input v-model="auditParams.refuse_reason" clearable :placeholder="t('reasonsRefusalPlaceholder')"
                        class="input-width" type="textarea" maxlength="100" :show-word-limit="true" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="auditShowDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="loading" @click="auditRefuseEvent">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
        <transfer-dialog ref="transferDialogRef" @complete="loadOrderList" />
        <export-sure ref="exportSureDialog" :show="flag" type="admin_shop_cash_out"
            :searchParam="orderTableData.searchParam" @close="handleClose" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getShopCashOutList, getShopCashOutInfo, auditShopCashOut, getShopTransferType, getWithdrawinfo, getShopCashOutStatus } from '@/app/api/shop_admin'
import { img } from '@/utils/common'
import { ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { AnyObject } from '@/types/global'
import transferDialog from './components/transfer-dialog.vue'

const cashOutStatusList = ref([])
const checkStatusList = async () => {
    cashOutStatusList.value = await (await getShopCashOutStatus()).data
}
checkStatusList()
const transferShowDialog = ref(false)
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
        order_no: '',
        member_id: 0,
        create_time: [],
        status: '',
        cash_out_no: '',
        keyword: '',
        audit_time: '',
        transfer_time: '',
        transfer_type: ''
    }
})

const statistics = ref({
    withdrawal_money: 0,
    money_cash_outing: 0
})
//统计
const getWithdrawinfoFn = () => {
    getWithdrawinfo().then(res => {
        statistics.value = res.data
    })
}
getWithdrawinfoFn()
// 获取店铺转账方式
const transfertype = ref<Array<Object | any>>([])
const getTransfertypeFn = async () => {
    transfertype.value = await (await getShopTransferType()).data
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

    getShopCashOutList({
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


/**
 * 转账
 * @param id
 */
const transferDialogRef = ref()
const transferEvent = (id: number) => {
    transferDialogRef.value.setTransferData(id)
    transferDialogRef.value.transferDialog = true
}

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

/************* 提现审核 *****************/

/**
 * 提现审核通过
 */
const auditApprovedEvent = (id: number) => {
    ElMessageBox.confirm(t('确定要通过审核吗'), t('warning'),
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
        }
    ).then(() => {
        auditShopCashOut({ id, status: 2 }).then(res => {
            loadOrderList()
        })
    })
}
/**
 * 提现审核拒绝
 */
const auditParams = ref({
    id: 0,
    status: -1,
    refuse_reason: ''
})
const auditShowDialog = ref(false)
// 表单验证规则
const formRules = reactive<FormRules>({
    refuse_reason: [
        { required: true, message: t('reasonsRefusalPlaceholder'), trigger: 'blur' }
    ],
})

const auditRefuseOpen = (id: number) => {
    auditParams.value.id = id
    auditParams.value.refuse_reason = ''
    auditShowDialog.value = true

}
const loading = ref(false)
const formRef = ref()
const auditRefuseEvent = (data: any) => {
    if (loading.value || !formRef) return
    formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            auditShopCashOut(auditParams.value).then(res => {
                loading.value = false;
                auditShowDialog.value = false
                loadOrderList()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

/**
 * 店铺详情
 */
const toShop = (id: number) => {
    router.push(`/admin/shop/info?id=${id}`)
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

</script>

<style lang="scss" scoped></style>
