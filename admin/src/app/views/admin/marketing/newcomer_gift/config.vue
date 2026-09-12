<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[5px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
        </el-card>

        <el-card class="box-card mt-[15px] !border-none" shadow="never">
            <el-tabs v-model="activeTab" class="newcomer-tabs">
                <el-tab-pane name="records">
                    <template #label>
                        <span class="tab-label">{{ t('receiveList') }}</span>
                        <el-badge class="ml-[5px] newcomer-badge" />
                    </template>
                    <el-card class="box-card !border-none mb-[10px] table-search-wrap" shadow="never">
                        <el-form :inline="true" label-width="90px" :model="recordsSearch" ref="searchFormRef" class="page-form">
                            <el-form-item :label="t('memberInfo')" prop="keywords">
                                <el-input v-model="recordsSearch.keywords" :placeholder="t('searchKeywords')" clearable class="search-input" />
                            </el-form-item>
                            <el-form-item :label="t('createTime')" prop="create_time">
                                <el-date-picker v-model="recordsSearch.create_time" type="datetimerange"
                                    value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                                    :end-placeholder="t('endDate')" class="search-input" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="getRecordsList()">{{ t('search') }}</el-button>
                                <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </el-card>
                    
                    <el-table :data="recordsList" v-loading="recordsLoading" size="large">
                        <el-table-column prop="member_id" :label="t('memberId')" min-width="100" :show-overflow-tooltip="true">
                            <template #default="{ row }">
                                {{ row.member_no }}
                            </template>
                        </el-table-column>
                        <el-table-column :label="t('memberInfo')" min-width="140" :show-overflow-tooltip="true">
                            <template #default="{ row }">
                                <div class="flex items-center cursor-pointer" @click="toMember(row.member_id)">
                                    <img class="w-[50px] h-[50px] mr-[10px]" v-if="row.headimg" :src="img(row.headimg)" alt="">
                                    <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                                    <div class="flex flex flex-col">
                                        <span>{{ row.nickname }}</span>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="mobile" :label="t('mobile')" min-width="90">
                            <template #default="{ row }">
                                {{ row.mobile }}
                            </template>
                        </el-table-column>
                        <el-table-column :label="t('giftContent')" min-width="300">
                            <template #default="{ row }">
                                <div v-if="row.gifts_content">
                                    <div v-for="(item, key) in row.gifts_content" :key="key">
                                        <div v-if="item.is_use && item.content" class="text-sm">
                                            <span class="text-gray-500 ml-[5px]">{{ item.content }}</span>
                                        </div>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="create_time" :label="t('receiveTime')" min-width="180" />
                    </el-table>
                    <div class="flex justify-end mt-[15px]" v-if="recordsTotal > 0">
                        <el-pagination v-model:current-page="recordsPage" v-model:page-size="recordsLimit" :total="recordsTotal" layout="total, prev, pager, next" />
                    </div>
                </el-tab-pane>
                <el-tab-pane :label="t('newcomerGiftConfig')" name="config" v-loading="configLoading">
                    <el-form class="page-form" :model="formData" label-width="120px" ref="formRef">
                        <h3 class="panel-title !text-sm">{{ t('basicInfo') }}</h3>

                        <el-form-item :label="t('isOpen')" prop="is_open">
                            <div>
                                <el-switch v-model="formData.is_open" :active-value="1" :inactive-value="0" />
                                <div class="text-sm text-gray-400 mt-[5px]">{{ t('isOpenTips') }}</div>
                            </div>
                        </el-form-item>
                    </el-form>

                    <h3 class="panel-title !text-sm">{{ t('newcomerGift') }}</h3>
                    <div class="pl-[100px]">
                        <member-gift ref="giftRef" v-model="formData.gifts"/>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-card>

        <div class="fixed-footer-wrap" v-if="activeTab === 'config'">
            <div class="fixed-footer">
                <el-button type="primary" :loading="saveLoading" @click="save(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'
import memberGift from '@/app/views/member/components/member-gift.vue'
import { getNewcomerGiftConfig, setNewcomerGiftConfig, getNewcomerGiftRecords } from '@/app/api/marketing_admin'

const route = useRoute()
const pageName = route.meta.title

const activeTab = ref('records')
const giftRef = ref<any>(null)
const configLoading = ref(true)
const saveLoading = ref(false)
const recordsLoading = ref(false)
const recordsList = ref([])
const recordsTotal = ref(0)
const recordsPage = ref(1)
const recordsLimit = ref(15)
const searchFormRef = ref<FormInstance>()
const recordsSearch = reactive({
    keywords: '',
    create_time: [] as string[]
})

const formData = reactive<Record<string, any>>({
    is_open: 0,
    gifts: {}
})

const formRef = ref<FormInstance>()

// 获取配置
const getConfig = () => {
    configLoading.value = true
    getNewcomerGiftConfig().then(({ data }) => {
        Object.assign(formData, data)
        configLoading.value = false
    }).catch(() => {
        configLoading.value = false
    })
}

// 获取领取记录
const getRecordsList = () => {
    recordsLoading.value = true
    getNewcomerGiftRecords({
        page: recordsPage.value,
        limit: recordsLimit.value,
        keywords: recordsSearch.keywords,
        create_time: recordsSearch.create_time
    }).then(({ data }) => {
        recordsList.value = data.data
        recordsTotal.value = data.total
        recordsLoading.value = false
    }).catch(() => {
        recordsLoading.value = false
    })
}

// 重置搜索
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    getRecordsList()
}

// 监听分页参数变化
watch([recordsPage, recordsLimit], () => {
    getRecordsList()
})

getRecordsList()
getConfig()

/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (saveLoading.value || !formEl) return

    // 验证礼包配置
    if (!await giftRef.value?.verify()) return

    saveLoading.value = true
    setNewcomerGiftConfig(formData).then(() => {
        saveLoading.value = false
        getConfig()
    }).catch(() => {
        saveLoading.value = false
    })
}

/**
 * 会员详情
 */
const toMember = (member_id: number) => {
    window.open(`/admin/member/detail?id=${member_id}`)
}
</script>

<style lang="scss" scoped>
.newcomer-tabs {
    :deep(.el-badge__content) {
        background-color: #ff4d4f;
        border: none;
    }
}
</style>
