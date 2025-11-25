<template>
    <!--核销记录-->
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card mt-[10px] !border-none table-search-wrap" shadow="never">
                <el-form :inline="true" :model="recordTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('verifyCode')" prop="code">
                        <el-input v-model="recordTable.searchParam.code" :placeholder="t('verifyCodePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('verifyType')" prop="type">
                        <el-select v-model="recordTable.searchParam.type" clearable :placeholder="t('verifyTypePlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item.name" :value="key" v-for="(item, key) in verifyTypeList" :key="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('核销员类型')" prop="verifier_type">
                        <el-select v-model="recordTable.searchParam.verifier_type" clearable :placeholder="t('请选择核销员类型')" class="input-width" @change="verifierTypeChange">
                            <el-option :label="item" :value="key" v-for="(item, key) in verifyerTypeList" :key="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('verifyer')" prop="verifier_member_id" v-if="recordTable.searchParam.verifier_type == 'member'">
                        <el-select v-model="recordTable.searchParam.verifier_member_id" clearable :placeholder="t('verifierPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item.member.nickname" :value="item.member_id" v-for="(item, key) in verifierList" :key="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('verifyer')" prop="verifier_member_id" v-else>
                        <el-select v-model="recordTable.searchParam.verifier_member_id" clearable :placeholder="t('verifierPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item.username" :value="item.uid" v-for="(item, key) in userList" :key="key" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('verifyTime')" prop="create_time">
                        <el-date-picker v-model="recordTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadRecordList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="recordTable.data" size="large" v-loading="recordTable.loading">
                    <template #empty>
                        <span>{{ !recordTable.loading ? t('emptyData') : '' }}</span>
                    </template>

                    <el-table-column prop="code" :show-overflow-tooltip="true" :label="t('verifyCode')"  min-width="150" />
                    <el-table-column prop="type_name" :label="t('verifyType')" min-width="150" />
                    <el-table-column :label="t('verifyTime')" min-width="120" :show-overflow-tooltip="true">
                        <template #default="{ row }">
                            {{ row.create_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="verifier_name" :label="t('verifyerName')" min-width="120"  />
                    <el-table-column prop="verifier_type_name" :label="t('verifyerType')" min-width="120"  />
                    <el-table-column :label="t('operation')" align="right" fixed="right" width="100">
                        <template #default="{ row }">
                            <div class="flex justify-end">
                                <el-button type="primary" link @click="detailEvent(row)">{{ t('详情') }}</el-button>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="recordTable.page" v-model:page-size="recordTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="recordTable.total"
                        @size-change="loadRecordList()" @current-change="loadRecordList" />
                </div>
            </div>

            <verify-detail ref="verifyDetailDialog" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { FormInstance } from 'element-plus'
import { getVerifyRecord, getVerifyTypeList, getVerifierTypeList, getVerifierSelect, getUserList } from '@/app/api/verify'
import verifyDetail from '@/app/views/shop/site/marketing/components/verify-detail.vue'

import { img } from '@/utils/common'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const recordTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        code: '',
        type: '',
        verifier_type: 'member',
        verifier_member_id: '',
        create_time: []
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取核销记录列表
 */
const loadRecordList = (page: number = 1) => {
    recordTable.loading = true
    recordTable.page = page

    getVerifyRecord({
        page: recordTable.page,
        limit: recordTable.limit,
        ...recordTable.searchParam
    }).then(res => {
        recordTable.loading = false
        recordTable.data = res.data.data
        recordTable.total = res.data.total
    }).catch(() => {
        recordTable.loading = false
    })
}
loadRecordList()

/**
 * 获取核销类型
 */
const verifyTypeList = ref<any>([])
const setVerifyTypeList = () => {
    getVerifyTypeList().then(res => {
        verifyTypeList.value = res.data
    }).catch()
}
setVerifyTypeList()

/**
 * 获取核销员类型
 */
const verifyerTypeList = ref<any>([])
const getVerifierTypeListFn = () => {
    getVerifierTypeList().then(res => {
        verifyerTypeList.value = res.data
    }).catch()
}
getVerifierTypeListFn()

/**
 * 获取店铺管理员
 */
const userList = ref<any>([])
const getUserListFn = () => {
    getUserList().then(res => {
        userList.value = res.data
    }).catch()
}
getUserListFn()
/**
 * 获取核销员
 */
const verifierList = ref<any>([])
const setVerifyerList = () => {
    getVerifierSelect().then(res => {
        verifierList.value = res.data
    }).catch()
}
setVerifyerList()

const verifierTypeChange = () => {
    recordTable.searchParam.verifier_member_id = ''
}

// 重置
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadRecordList()
}

// 详情
let verifyDetailDialog: Record<string, any> | null = ref(null)
const detailEvent = (data: any) => {
    verifyDetailDialog.value.setFormData({code: data.code})
    verifyDetailDialog.value.showDialog = true
}
</script>

<style lang="scss" scoped></style>
