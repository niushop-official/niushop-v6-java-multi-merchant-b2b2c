<template>
    <el-drawer v-model="showDialog" title="买单详情" direction="rtl" :before-close="handleClose" size="1000px">
        <div class="main-container" v-loading="loading">
            <el-form :model="cashInfo" label-width="100px" ref="formRef" class="page-form" v-loading="loading" label-position="left">
                <el-row v-if="Object.keys(cashInfo).length">
                    <el-col :span="8">
                        <el-form-item label="订单编号">
                            <div class="input-width">
                                <span>{{ cashInfo.order_no }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="会员信息">
                            <div class="input-width flex items-center">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-if="cashInfo.member.headimg" :src="img(cashInfo.member.headimg)" alt="">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                                <div class="flex flex flex-col">
                                    <span>{{ cashInfo.member.nickname || '' }}</span>
                                </div>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="店铺名称">
                            <div class="input-width">
                                <span>{{ cashInfo.site_name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="支付金额">
                            <div class="input-width">
                                <span>{{ cashInfo.order_money }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="买单金额">
                            <div class="input-width">
                                <span>{{ cashInfo.total_money }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="Number(cashInfo.plat_discount_money)">
                        <el-form-item label="平台优惠">
                            <div class="input-width">
                                <span>{{ cashInfo.plat_discount_money }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="Number(cashInfo.discount_money)">
                        <el-form-item label="店铺优惠">
                            <div class="input-width">
                                <span>{{ cashInfo.discount_money }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="订单来源">
                            <div class="input-width">
                                <span>{{ cashInfo.order_from_name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="交易流水号">
                            <div class="input-width">
                                <span>{{ cashInfo.out_trade_no }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="支付方式">
                            <div class="input-width">
                                <span>{{ cashInfo.pay_type_name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="订单状态">
                            <div class="input-width">
                                <span>{{ cashInfo.status_info.name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="下单时间">
                            <div class="input-width">
                                <span>{{ cashInfo.create_time }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="cashInfo.pay_time">
                        <el-form-item label="支付时间">
                            <div class="input-width">
                                <span>{{ cashInfo.pay_time }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
    </el-drawer>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { img } from '@/utils/common'
import { getCashAdminDetail } from '@/app/api/cash_admin'

const showDialog = ref(false)
const loading = ref(true)
const cashInfo = ref<any>({})

const getCashInfo = (id: number) => {
    loading.value = true
    getCashAdminDetail(id).then((res: any) => {
        cashInfo.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

const handleClose = (done: () => void) => {
    showDialog.value = false
}

const setFormData = (row: any = null) => {
    if (row && row.order_id) {
        showDialog.value = true
        getCashInfo(row.order_id)
    }
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>

</style>