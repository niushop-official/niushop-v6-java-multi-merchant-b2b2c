<template>
    <div class="main-container">
        <!-- 佣金概览 -->
        <el-card class="card !border-none" shadow="never">
            <el-row>
                <el-col :span="6">
                    <div class="ml-[10px]">
                        <el-statistic :precision="2" :value="fenxiaoAccount.shop_commission">
                            <template #title>
                                <div class="flex items-center mb-[10px]">
                                    <span class="mr-[3px] text-[14px] text-[#909399]">{{ t('shopCommission') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('shopCommissionTips')" placement="right">
                                        <el-icon size="14px">
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="ml-[10px]">
                        <el-statistic :precision="2" :value="fenxiaoAccount.shop_refund_commission">
                            <template #title>
                                <div class="flex items-center mb-[10px]">
                                    <span class="mr-[3px] text-[14px] text-[#909399]">{{ t('shopRefundCommission') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('shopRefundCommissionTips')" placement="right">
                                        <el-icon size="14px">
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="ml-[10px]">
                        <el-statistic :precision="2" :value="fenxiaoAccount.settlement_commission">
                            <template #title>
                                <div class="flex items-center mb-[10px]">
                                    <span class="mr-[3px] text-[14px] text-[#909399]">{{ t('settlementCommission') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('settlementCommissionTips')" placement="right">
                                        <el-icon size="14px">
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="ml-[10px]">
                        <el-statistic :precision="2" :value="fenxiaoAccount.unsettlement_commission">
                            <template #title>
                                <div class="flex items-center mb-[10px]">
                                    <span class="mr-[3px] text-[14px] text-[#909399]">{{ t('unSettlementCommission') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('unSettlementCommissionTips')" placement="right">
                                        <el-icon size="14px">
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                </el-col>
            </el-row>
        </el-card>
        <!-- 佣金概览 end -->
        <!-- 用户佣金统计 start -->
        <el-card class="card !border-none" shadow="never">
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="memberTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('memberInfo')" prop="keyword">
                        <el-input v-model.trim="memberTable.searchParam.keyword" :placeholder="t('memberInfoPlaceholder')" maxlength="60" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadMemberList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-table :data="memberTable.data" size="large" v-loading="memberTable.loading" ref="goodsListTableRef">
                <template #empty>
                    <span>{{ !memberTable.loading ? t('emptyData') : '' }}</span>
                </template>
                <el-table-column prop="goods_id" :label="t('memberInfo')" min-width="200">
                    <template #default="{ row }">
                        <div class="flex items-center cursor-pointer">
                            <div class="min-w-[50px] h-[50px] flex items-center justify-center">
                                <el-image v-if="row.member.headimg" class="w-[50px] h-[50px]" :src="img(row.member.headimg)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[50px] h-[50px] rounded-full" src="@/app/assets/images/member_head.png" alt="">
                                        </div>
                                    </template>
                                </el-image>
                                <img class="w-[50px] h-[50px] rounded-full" v-else src="@/app/assets/images/member_head.png" alt="">
                            </div>
                            <div class="ml-2">
                                <span :title="(row.member.nickname || row.member.username)" class="multi-hidden">{{row.member.nickname || row.member.username}}</span>
                                <span class="text-primary text-[12px]">{{row.member.mobile || '--'}}</span>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="shop_commission" :label="t('shopCommission')" min-width="120" />
                <el-table-column prop="shop_refund_commission" :label="t('shopRefundCommission')" min-width="120" />
            </el-table>
            <div class="mt-[16px] flex justify-end">
                <el-pagination v-model:current-page="memberTable.page" v-model:page-size="memberTable.limit"
                    layout="total, sizes, prev, pager, next, jumper" :total="memberTable.total"
                    @size-change="loadMemberList" @current-change="loadMemberList" />
            </div>
        </el-card>
        <!-- 用户佣金统计 end -->
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getFenxiaoAccountStat, getFenxiaoMemberStat } from '@/addon/mall_fenxiao/api/site/stat'
import { img } from '@/utils/common'
import { cloneDeep } from 'lodash-es'
import { FormInstance } from 'element-plus'
const fenxiaoAccount = ref<any>([]) // 获取分销统计
const loading = ref(true)
const getStatInfoFn = async () => {
    fenxiaoAccount.value = await (await getFenxiaoAccountStat()).data
    loading.value = false
    loadMemberList()
}
getStatInfoFn()

const searchFormRef = ref()
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()

    loadMemberList()
}

const memberTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        keyword: ''
    }
})

const loadMemberList = (page: number = 1) => {
    memberTable.loading = true
    const searchData = cloneDeep(memberTable.searchParam)
    getFenxiaoMemberStat({
        page: memberTable.page,
        limit: memberTable.limit,
        ...searchData
    }).then((res:any) => {
        memberTable.data = res.data.data
        memberTable.total = res.data.total
        memberTable.loading = false
    })
}

</script>

<style lang="scss" scoped></style>
