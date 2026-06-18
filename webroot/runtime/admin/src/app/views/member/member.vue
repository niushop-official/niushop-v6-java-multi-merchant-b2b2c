<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">{{ t('addMember') }}</el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="memberTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('memberInfo')" prop="keyword">
                        <el-input v-model="memberTableData.searchParam.keyword" class="w-[240px]" :placeholder="t('memberInfoPlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('registerChannel')" prop="register_channel">
                        <el-select v-model="memberTableData.searchParam.register_channel" clearable :placeholder="t('channelPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item" :value="key" v-for="(item, key) in channelList" :key="key" />
                        </el-select>
                    </el-form-item>

                    <el-form-item :label="t('memberLabel')" prop="member_label">
                        <el-select v-model="memberTableData.searchParam.member_label" collapse-tags clearable :placeholder="t('memberLabelPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item['label_name']" :value="item['label_id']" v-for="(item, index) in labelSelectData" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="memberTableData.searchParam.create_time" type="datetimerange"
                            value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                            :end-placeholder="t('endDate')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadMemberList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="exportEvent">{{ t('export') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div class="mb-[10px] flex items-center">
                <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                <el-button @click="batchSetLabel" size="small">{{ t("批量设置标签") }}</el-button>
                <el-button @click="batchSetLevel" size="small">{{ t("批量设置等级") }}</el-button>
                <el-button @click="batchModifyBalance" size="small">{{ t("批量设置余额") }}</el-button>
                <el-button @click="batchModifyPoint" size="small">{{ t("批量设置积分") }}</el-button>
                <el-button @click="batchSendCoupon" size="small" v-if="user_store.checkAddon('mall')">{{ t("批量送优惠券") }}</el-button>
            </div>
            <div class="mt-[10px]">
                <el-table :data="memberTableData.data" size="large" v-loading="memberTableData.loading" ref="memberTableRef" @selection-change="handleSelectionChange">

                    <template #empty>
                        <span>{{ !memberTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" />
                    <el-table-column prop="member_no" :label="t('memberNo')" min-width="120" />
                    <el-table-column prop="nickname" :show-overflow-tooltip="true" :label="t('memberInfo')" min-width="170">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-if="row.headimg" :src="img(row.headimg)" alt="">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                                <div class="flex flex flex-col">
                                    <span>{{ row.nickname || '' }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="parent_member.nickname" :show-overflow-tooltip="true" :label="t('parentMemberInfo')" min-width="170">
                        <template #default="{ row }">
                            <div class="flex items-center" v-if="row.parent_member">
                                <div class="flex flex flex-col">
                                    <span>{{ row.parent_member.nickname || '-' }}</span>
                                </div>
                            </div>
                            <div class="" v-else>-</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="member_level_name" :label="t('memberLevel')" min-width="130" :show-overflow-tooltip="true" >
                        <template #default="{ row }">
                            <div>
                                {{ row.member_level_name || '-' }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="mobile" :label="t('mobile')" min-width="120" >
                        <template #default="{ row }">
                            <div>
                                {{ row.mobile || '-' }}
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="point" :label="`${t('point')}/${t('balance')}`" min-width="200" align="left">
                        <template #default="{ row }">
                            <div >
                                <div>{{ t('point') }}: {{ Number.parseFloat(row.point).toFixed(2) }} 
                                    <el-icon @click="adjustPoint(row)" class="ml-2 cursor-pointer" :size="12">
                                        <EditPen color="#273CE2" />
                                    </el-icon>
                                </div>
                                <div>{{ t('balance') }}: {{ Number(row.balance).toFixed(2) }}
                                    <el-icon @click="adjustBalance(row)" class="ml-2 cursor-pointer" :size="12">
                                        <EditPen color="#273CE2" />
                                    </el-icon>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="member_label" :label="t('lable')" min-width="120" align="center">
                        <template #default="{ row }">
                            <div class="flex flex-col items-center" v-if="row.member_label_array && row.member_label_array.length > 0">
                                <div v-for="(item, key) in row.member_label_array" class="my-[3px]" :key="key">
                                    <el-tag type="info">{{ item.label_name }}</el-tag>
                                </div>
                            </div>
                            <div class="flex flex-col items-center" v-else>-</div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="register_channel_name" :label="t('registerChannel')" min-width="110" align="center" />

                    <el-table-column prop="member_label" :label="t('status')" min-width="120" align="center">
                        <template #default="{ row }">
                            <el-tag type="success" v-if="row.status == 1" @click="lockMember(row, 0)" class="cursor-pointer">{{ t('normal') }}</el-tag>
                            <el-tag type="error" v-else @click="lockMember(row, 1)" class="cursor-pointer">{{ t('lock') }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('createTime')" min-width="150" align="center">
                        <template #default="{ row }">
                            {{ row.create_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('lastVisitTime')" min-width="150" align="center"> 
                        <template #default="{ row }">
                            {{ row.last_visit_time || '' }}
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" align="right" fixed="right" min-width="170">
                        <template #default="{ row }">
                            <div class="flex items-center justify-end">
                                <el-button type="primary" link @click="detailEvent(row)">{{ t('detail') }}</el-button>
                                <!--<el-button type="primary" link @click="deleteEvent(row)">{{ t('memberDelete') }}</el-button>-->
                                <el-dropdown>
                                    <el-button type="primary" link  class="!ml-[12px]">{{ t('更多') }}</el-button>
                                    <template #dropdown>
                                        <el-dropdown-menu>
                                            <el-dropdown-item v-if="user_store.checkAddon('mall')" @click="sendCouponEvent(row)">{{ t('送优惠券') }}</el-dropdown-item>
                                            <el-dropdown-item @click="adjustBalance(row)">{{ t('设置余额') }}</el-dropdown-item>
                                            <el-dropdown-item @click="adjustPoint(row)">{{ t('设置积分') }}</el-dropdown-item>
                                            <el-dropdown-item @click="setMemberLable(row)">{{ t('设置标签') }}</el-dropdown-item>
                                            <el-dropdown-item @click="setMemberLevle(row)">{{ t('设置等级') }}</el-dropdown-item>
                                            <el-dropdown-item @click="modifyRecommenderEvent(row)">{{ t('修改推荐人') }}</el-dropdown-item>
                                            <el-dropdown-item @click="resetPasswordEvent(row)">{{ t('重置密码') }}</el-dropdown-item>
                                        </el-dropdown-menu>
                                    </template>
                                </el-dropdown>
                            </div>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="memberTableData.page" v-model:page-size="memberTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="memberTableData.total"
                        @size-change="loadMemberList()" @current-change="loadMemberList" />
                </div>
            </div>

            <point-edit ref="pointDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)" />
		    <balance-edit ref="balanceDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)" />
            <batch-account ref="batchAccountDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)" />
            <add-member ref="addMemberDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)" />
            <edit-member ref="editMemberDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)" />
            <export-sure ref="exportSureDialog" :show="flag" type="member" :searchParam="memberTableData.searchParam" @close="handleClose" />
            <detail-member ref="detailMemberDialog"  @load="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)"></detail-member>
            <reset-password ref="resetPasswordDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)"></reset-password>
            <modify-recommender ref="modifyRecommenderDialog" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)" />
            <component :is="sendComponent.component"  ref="sendCouponRef" v-if="sendComponent.component" @complete="loadMemberList(getTablePageStorage(memberTableData.searchParam).page)"/>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, defineAsyncComponent } from 'vue'
import { t } from '@/lang'
import { setTablePageStorage, getTablePageStorage, img } from '@/utils/common'
import { getRegisterChannelType, getMemberList, getMemberLabelAll, editMemberStatus, deleteMember, getMemberSendComponent } from '@/app/api/member'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import AddMember from '@/app/views/member/components/add-member.vue'
import EditMember from '@/app/views/member/components/edit-member.vue'
import detailMember from '@/app/views/member/components/detail-member.vue'
import resetPassword from '@/app/views/member/components/reset-password.vue'
import PointEdit from '@/app/views/member/components/member-point-edit.vue'
import BalanceEdit from '@/app/views/member/components/member-balance-edit.vue'
import BatchAccount from '@/app/views/member/components/member-batch-account.vue'
import modifyRecommender from '@/app/views/member/components/modify-recommender.vue'
import userStore from '@/stores/modules/user'
import { concat } from 'lodash-es'

const user_store = userStore()
const route = useRoute()
const pageName = route.meta.title
const memberTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        register_type: '',
        member_label: '',
        register_channel: '',
        create_time: []
    }
})

const searchFormRef = ref<FormInstance>()

// 获取注册方式
const channelList = ref([])
const setChannelList = async () => {
    channelList.value = await (await getRegisterChannelType({})).data
}
setChannelList()

// 获取全部标签
const labelSelectData = ref([])
const getMemberLabelAllFn = async () => {
    labelSelectData.value = await (await getMemberLabelAll()).data
}
getMemberLabelAllFn()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadMemberList()
}

// 获取会员列表
const loadMemberList = (page: number = 1) => {
    memberTableData.loading = true
    memberTableData.page = page

    getMemberList({
        page: memberTableData.page,
        limit: memberTableData.limit,
        ...memberTableData.searchParam
    }).then(res => {
        memberTableData.loading = false
        memberTableData.data = res.data.data
        memberTableData.total = res.data.total
        setTablePageStorage(memberTableData.page, memberTableData.limit, memberTableData.searchParam)
    }).catch(() => {
        memberTableData.loading = false
    })
}
loadMemberList(getTablePageStorage(memberTableData.searchParam).page)

const router = useRouter()
const addMemberDialog: Record<string, any> | null = ref(null)
const editMemberDialog: Record<string, any> | null = ref(null)
const detailMemberDialog: Record<string, any> | null = ref(null)


// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    memberTableRef.value.toggleAllSelection()
}

const memberTableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (
        multipleSelection.value.length > 0 &&
        multipleSelection.value.length < memberTableData.data.length
    ) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == memberTableData.data.length) {
        toggleCheckbox.value = true
    }
}
// 批量设置会员标签
const batchSetLabel  = () => { 
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('请选择要操作的数据')}`
        })
        return
    }
    const memberIds: any = []
    multipleSelection.value.forEach((item: any) => {
        memberIds.push(item.member_id)
    })
    const data = ref({
        type: 'member_label',
        title: t('批量设置会员标签'),
        memberIds
    })
    editMemberDialog.value.batchSetDialogType(data.value)
    editMemberDialog.value.showDialog = true
}

// 批量设置会员等级
const batchSetLevel = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('请选择要操作的数据')}`
        })
        return
    }
    const memberIds: any = []
    multipleSelection.value.forEach((item: any) => {
        memberIds.push(item.member_id)
    })
    const data = ref({
        type: 'member_level',
        title: t('批量设置会员标签'),
        memberIds
    })
    editMemberDialog.value.batchSetDialogType(data.value)
    editMemberDialog.value.showDialog = true
}
/**
 * 设置标签
 */
function setMemberLable(res: any) {
    const data = ref({
        type: 'member_label',
        id: res.member_id,
        title: t('setLable'),
        data: res
    })
    editMemberDialog.value.setDialogType(data.value)
    editMemberDialog.value.showDialog = true
}

/**
 * 设置等级
 */
function setMemberLevle(res: any) {
    const data = ref({
        type: 'member_level',
        id: res.member_id,
        title: t('等级'),
        data: res
    })
    editMemberDialog.value.setDialogType(data.value)
    editMemberDialog.value.showDialog = true
}

// 重置密码
const resetPasswordDialog: Record<string, any> | null = ref(null)
const resetPasswordEvent = (data: any) => {
    resetPasswordDialog.value.setFormData(data)
}

// 编辑积分余额
const pointDialog: Record<string, any> | null = ref(null)
const balanceDialog: Record<string, any> | null = ref(null)
const batchAccountDialog: Record<string, any> | null = ref(null)
/**
 * 调整积分
 */
const adjustPoint = (data: any) => {
    pointDialog.value.setFormData(data)
    pointDialog.value.showDialog = true
}

/**
 * 调整余额
 */
const adjustBalance = (data: any) => {
    balanceDialog.value.setFormData(data)
    balanceDialog.value.showDialog = true
}

// 批量修改会员余额
const batchModifyBalance = () => { 
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('请选择要操作的数据')}`
        })
        return
    }
    const memberIds: any = []
    multipleSelection.value.forEach((item: any) => {
        memberIds.push(item.member_id)
    })
    const data = ref({
        account_type: 'balance',
        title: t('批量调整会员余额'),
        memberIds
    })
    batchAccountDialog.value.setFormData(data.value)
    batchAccountDialog.value.showDialog = true
}
// 批量修改会员积分
const batchModifyPoint = () => { 
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('请选择要操作的数据')}`
        })
        return
    }
    const memberIds: any = []
    multipleSelection.value.forEach((item: any) => {
        memberIds.push(item.member_id)
    })
    const data = ref({
        account_type: 'point',
        title: t('批量调整会员积分'),
        memberIds
    })
    batchAccountDialog.value.setFormData(data.value)
    batchAccountDialog.value.showDialog = true
}

// 修改推荐人
const modifyRecommenderDialog: Record<string, any> | null = ref(null)
const modifyRecommenderEvent = (data: any) => { 
    modifyRecommenderDialog.value.setFormData(data)
}

// 送优惠券
const modules: any = import.meta.glob('@/**/*.vue')
const sendComponent = ref<any>({})
const getMemberSendComponentFn = () => {
    getMemberSendComponent('send_coupon').then((res: any) => {
        sendComponent.value = res.data
        sendComponent.value.component && (sendComponent.value.component = defineAsyncComponent(modules[sendComponent.value.component]))
    })
}
getMemberSendComponentFn()

const sendCouponRef: Record<string, any> | null = ref(null)
const sendCouponEvent = (row: any) => {
    const data = ref({
        title: t('赠送优惠券'),
        member_ids: [row.member_id]
    })
    sendCouponRef.value.setFormData(data.value)
}

// 批量赠送优惠券
const batchSendCoupon = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('请选择要操作的数据')}`
        })
        return
    }
    const memberIds: any = []
    multipleSelection.value.forEach((item: any) => {
        memberIds.push(item.member_id)
    })
    const data = ref({
        title: t('批量赠送优惠券'),
        member_ids: memberIds
    })
    sendCouponRef.value.setFormData(data.value)
}

/**
 * 删除
 */
function deleteEvent(res: any) {
    ElMessageBox.confirm(t('memberDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteMember(res.member_id).then(() => {
            loadMemberList(getTablePageStorage(memberTableData.searchParam).page)
        }).catch(() => {
        })
    })
}

/**
 * 添加会员
 */
const addEvent = () => {
    addMemberDialog.value.setFormData()
    addMemberDialog.value.showDialog = true
}

/**
 * 会员详情
 */
const detailEvent = (res: any) => {
    router.push('/member/detail?id=' + res.member_id)
    // const routeData = router.resolve(`/member/detail?id=${res.member_id}`)
    // window.open(routeData.href, '_blank')
    
}
/**
 * 会员导出
 */
const exportSureDialog = ref(null)
const flag = ref(false)
const handleClose = (val) => {
    flag.value = val
}
const exportEvent = (data: any) => {
    flag.value = true
}

/**
 * 变更会员状态
 */
const lockMember = (res: any, status: any) => {
    editMemberStatus({
        status: status,
        member_ids: [res.member_id]
    }).then(res => {
        if (res.code >= 0) {
            loadMemberList(getTablePageStorage(memberTableData.searchParam).page)
        }
    })
}
</script>

<style lang="scss" scoped></style>
