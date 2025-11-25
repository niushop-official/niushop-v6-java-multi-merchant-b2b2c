<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="shopApplyTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('status')" prop="status">
                        <el-select v-model="shopApplyTableData.searchParam.status" clearable
                            :placeholder="t('groupIdPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item" :value="index" v-for="(item, index) in statusList" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('keywords')" prop="keywords">
                        <el-input v-model="shopApplyTableData.searchParam.keywords"
                            :placeholder="t('keywordsPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('groupId')" prop="group_id">
                        <el-select v-model="shopApplyTableData.searchParam.group_id" clearable
                            :placeholder="t('groupIdPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item['group_name']" :value="item['group_id']"
                                v-for="(item, index) in groupList.all" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('categoryId')" prop="category_id">
                        <el-select v-model="shopApplyTableData.searchParam.category_id" clearable
                            :placeholder="t('categoryIdPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item['category_name']" :value="item['category_id']"
                                v-for="(item, index) in categoryList.all" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="shopApplyTableData.searchParam.create_time" type="datetimerange"
                            value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                            :end-placeholder="t('endDate')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadShopApplyList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-table :data="shopApplyTableData.data" size="large" v-loading="shopApplyTableData.loading">
                    <template #empty>
                        <span>{{ !shopApplyTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="user_name" :label="t('userName')" min-width="120" show-overflow-tooltip />
                    <el-table-column prop="user_mobile" :label="t('userMobile')" min-width="120" show-overflow-tooltip />
                    <el-table-column prop="site_name" :label="t('siteName')" min-width="120" show-overflow-tooltip
                        align="center" />
                    <el-table-column prop="nickname" :label="t('nickname')" min-width="120" show-overflow-tooltip />
                    <el-table-column prop="category_name" :label="t('categoryId')" min-width="120" show-overflow-tooltip />
                    <el-table-column prop="group_name" :label="t('groupId')" min-width="120" show-overflow-tooltip />
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="180" align="center" />
                    <el-table-column prop="status_name" :label="t('status')" min-width="120" show-overflow-tooltip />
                    <el-table-column prop="audit_time" :label="t('auditTime')" min-width="180" align="center">
                        <template #default="{ row }">
                            <div v-if="row.audit_time == 0"></div>
                            <div v-else>{{ row.audit_time }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="open_status_name" :label="t('openStatus')" min-width="120"
                        show-overflow-tooltip />
                    <el-table-column :label="t('operation')" align="right" fixed="right" width="190">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row.apply_id)">{{ t('detail') }}</el-button>
                            <el-button type="primary" link @click="setupEvent(row)"
                                v-if="row.status == 2 && row.open_status == 1">{{ t('setupShop') }}</el-button>
                            <el-button type="primary" link @click="shopDetailEvent(row.site_id)"
                                v-if="row.status == 2 && row.open_status == 2">{{ t('shopDetail') }}</el-button>
                            <el-button type="primary" link @click="verifyEvent(row)" v-if="row.status == 1">{{ t('verify')
                            }}</el-button>
                            <!-- <el-button type="primary" link @click="deleteEvent(row.apply_id)">{{ t('delete') }}</el-button> -->
                        </template>
                    </el-table-column>
                </el-table>

                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="shopApplyTableData.page"
                        v-model:page-size="shopApplyTableData.limit" layout="total, sizes, prev, pager, next, jumper"
                        :total="shopApplyTableData.total" @size-change="loadShopApplyList()"
                        @current-change="loadShopApplyList" />
                </div>
            </div>
            <ApplyVerify ref="verifyApplyDialog" @complete="loadShopApplyList" />
            <setup-shop ref="setupShopDialog" @complete="loadShopApplyList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getShopApplyList, getShopGroupAll, getShopCategoryAll, getApplyStatusList, deleteShopApply } from '@/app/api/shop_admin'
import { img } from '@/utils/common'
import ApplyVerify from '@/app/views/shop/admin/components/apply-verify.vue'
import SetupShop from '@/app/views/shop/admin/components/setup-shop.vue'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title
const groupList = ref({
    all: []
})
const categoryList = ref({
    all: []
})
const statusList = ref([])

const shopApplyTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        status: '',
        keywords: '',
        group_id: '',
        category_id: '',
        create_time: [],
        last_time: []
    }
})
const setGroupList = async () => {
    const obj = await (await getShopGroupAll({})).data

    groupList.value.all = obj
    obj.forEach((item: any, index: any) => {
        if (!groupList.value[item.app]) {
            groupList.value[item.app] = []
            groupList.value[item.app].push(item)
        } else {
            groupList.value[item.app].push(item)
        }
    })
}
setGroupList()

const setCategoryList = async () => {
    const obj = await (await getShopCategoryAll({})).data

    categoryList.value.all = obj
    obj.forEach((item: any, index: any) => {
        if (!categoryList.value[item.app]) {
            categoryList.value[item.app] = []
            categoryList.value[item.app].push(item)
        } else {
            categoryList.value[item.app].push(item)
        }
    })
}
setCategoryList()

const setStatusList = async () => {
    statusList.value = await (await getApplyStatusList()).data
}
setStatusList()


const searchFormRef = ref<FormInstance>()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadShopApplyList()
}

/**
 * 获取申请列表
 */
const loadShopApplyList = (page: number = 1) => {
    shopApplyTableData.loading = true
    shopApplyTableData.page = page

    getShopApplyList({
        page: shopApplyTableData.page,
        limit: shopApplyTableData.limit,
        ...shopApplyTableData.searchParam

    }).then(res => {
        shopApplyTableData.loading = false
        shopApplyTableData.data = res.data.data
        shopApplyTableData.total = res.data.total
    }).catch(() => {
        shopApplyTableData.loading = false
    })
}
loadShopApplyList()

const verifyApplyDialog: Record<string, any> | null = ref(null)
const setupShopDialog: Record<string, any> | null = ref(null)

/**
 * 查看详情
 * @param id
 */
const detailEvent = (id: number) => {
    router.push({ path: '/shop/apply_info', query: { id } })
}

/**
 * 申请审核
 * @param data
 */
const verifyEvent = (data: any) => {
    verifyApplyDialog.value.setFormData(data)
    verifyApplyDialog.value.showDialog = true
}

/**
 * 开店
 * @param data
 */
const setupEvent = (data: any) => {
    setupShopDialog.value.setFormData({
        apply_id: data.apply_id,
        site_name: data.site_name,
        group_id: data.group_id,
        phone: data.user_mobile,
        category_id: data.category_id,
        business_license: data.business_license,
		uid:data.uid,
        username:data.account,
        password:data.password,
        confirm_password:data.password
    })
    setupShopDialog.value.showDialog = true
}

/**
 * 查看店铺详情
 * @param id
 */
const shopDetailEvent = (id: number) => {
    router.push({ path: '/shop/info', query: { id: id } })
}

/**
 * 删除申请
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('mallShopApplyDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteShopApply(id).then(() => {
            loadShopApplyList()
        }).catch(() => {
        })
    })
}
</script>

<style lang="scss" scoped></style>
