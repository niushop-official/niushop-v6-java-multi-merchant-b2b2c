<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="memberTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('memberInfo')" prop="keyword">
                        <el-input v-model="memberTableData.searchParam.keyword" class="w-[240px]" :placeholder="t('memberInfoPlaceholder')" />
                    </el-form-item>

                    <el-form-item :label="t('isFollow')" prop="is_follow">
                        <el-select v-model="memberTableData.searchParam.is_follow" clearable :placeholder="t('isFollowPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item" :value="key" v-for="(item, key) in followList" :key="key" />
                        </el-select>
                    </el-form-item>

                    <el-form-item :label="t('registerChannel')" prop="register_channel">
                        <el-select v-model="memberTableData.searchParam.register_channel" clearable :placeholder="t('channelPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item" :value="key" v-for="(item, key) in channelList" :key="key" />
                        </el-select>
                    </el-form-item>

                    <el-form-item :label="t('memberLabel')" prop="shop_member_label">
                        <el-select v-model="memberTableData.searchParam.shop_member_label" collapse-tags clearable :placeholder="t('memberLabelPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item['label_name']" :value="item['label_id']"
                                v-for="(item, index) in labelSelectData" :key="index" />
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
                        <el-button type="primary" @click="exportEvent">{{ t('export') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div class="mt-[10px]">
                <el-table :data="memberTableData.data" size="large" v-loading="memberTableData.loading">

                    <template #empty>
                        <span>{{ !memberTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
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
                    <el-table-column :label="t('mobile')" min-width="120">
                        <template #default="{ row }">
                            <div>{{ mobileHide(row.mobile) }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="is_follow_name" :label="t('isFollow')" min-width="90" align="center" />
                    <el-table-column :label="t('lable')" min-width="90" align="center">
                        <template #default="{ row }">
                            <div class="flex flex-col items-center">
                                <div v-for="(item, key) in row.shop_member_label_array" class="my-[3px]" :key="key">
                                    <el-tag type="info">{{ item.label_name }}</el-tag>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="register_channel_name" :label="t('registerChannel')" min-width="110" align="center" />
                    <el-table-column :label="t('firstConsumTime')" min-width="110" align="center">
                        <template #default="{ row }">
                            {{ row.first_consum_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('lastConsumTime')" min-width="110" align="center">
                        <template #default="{ row }">
                            {{ row.last_consum_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" align="right" fixed="right" width="100">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <el-button type="primary" link @click="detailEvent(row)">{{ t('detail') }}</el-button>
                                <el-button type="primary" link @click="setMemberLable(row)">{{ t('setLable') }}</el-button>
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

            <edit-member ref="editMemberDialog" @complete="loadMemberList()" />
            <export-sure ref="exportSureDialog" :show="flag" type="shop_member" isSite="true" :searchParam="memberTableData.searchParam" @close="handleClose" />
            <detail-member ref="detailMemberDialog"  @load="loadMemberList()"></detail-member>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img, mobileHide } from '@/utils/common'
import { getShopMember, getShopRegisterChannel, getShopIsFollow, getShopMemberLabelAll } from '@/app/api/shop_site'
import { FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import EditMember from '@/app/views/shop/site/member/components/edit-member.vue'
import DetailMember from '@/app/views/shop/site/member/components/detail-member.vue'
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
        is_follow: '',
        shop_member_label: '',
        register_channel: '',
        create_time: []
    }
})

const searchFormRef = ref<FormInstance>()

// 获取注册方式
const channelList = ref([])
const setChannelList = async () => {
    channelList.value = await (await getShopRegisterChannel({})).data
}
setChannelList()

// 获取关注状态
const followList = ref([])
const setFollowList = async () => {
    followList.value = await (await getShopIsFollow({})).data
}
setFollowList()

// 获取全部标签
const labelSelectData = ref([])
const getMemberLabelAllFn = async () => {
    labelSelectData.value = await (await getShopMemberLabelAll()).data
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

    getShopMember({
        page: memberTableData.page,
        limit: memberTableData.limit,
        ...memberTableData.searchParam
    }).then(res => {
        memberTableData.loading = false
        memberTableData.data = res.data.data
        memberTableData.total = res.data.total
    }).catch(() => {
        memberTableData.loading = false
    })
}
loadMemberList()

const editMemberDialog: Record<string, any> | null = ref(null)
const detailMemberDialog: Record<string, any> | null = ref(null)
/**
 * 设置标签
 */
function setMemberLable(res: any) {
    const data = ref({
        type: 'shop_member_label',
        id: res.member_id,
        title: t('setLable'),
        data: res
    })
    editMemberDialog.value.setDialogType(data.value)
    editMemberDialog.value.showDialog = true
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
 * 会员详情
 */
const detailEvent = (res: any) => {
    const data = { id: res.member_id }
    detailMemberDialog.value.setFormData(data)
    detailMemberDialog.value.showDialog = true
}

</script>

<style lang="scss" scoped></style>
