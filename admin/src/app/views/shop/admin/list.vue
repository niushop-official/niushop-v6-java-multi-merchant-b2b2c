<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <div>
                    <el-button type="primary" class="w-[100px]" @click="addEvent">
                        {{ t('addSite') }}
                    </el-button>
                    <el-button type="default" class="w-[100px]" @click="toSiteLink">
                        {{ t('toSite') }}
                    </el-button>
                </div>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="shopTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('siteName')" prop="keyword">
                        <el-input v-model="shopTableData.searchParam.keyword" :placeholder="t('siteNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('businessType')" prop="app">
                        <el-select v-model="shopTableData.searchParam.app" clearable
                            :placeholder="t('businessTypePlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item['title']" :value="item['key']"
                                v-for="(item, index) in appList" :key="index"/>
                        </el-select>
                    </el-form-item>
					<el-form-item :label="t('group')" prop="status">
                        <el-select v-model="shopTableData.searchParam.group_id" clearable :placeholder="t('groupIdPlaceholder')" class="input-width" filterable>
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item.group_name" :value="index" v-for="(item, index) in groupList" :key="index"/>
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('status')" prop="status">
                        <el-select v-model="shopTableData.searchParam.status" clearable :placeholder="t('statusPlaceholder')" class="input-width">
                            <el-option :label="t('selectPlaceholder')" value="" />
                            <el-option :label="item" :value="index" v-for="(item, index) in statusList" :key="index"/>
                        </el-select>
                    </el-form-item>
                  <el-form-item :label="t('isSelf')" prop="is_self">
                        <el-select v-model="shopTableData.searchParam.is_self" clearable class="input-width">
                          <el-option :label="t('selectPlaceholder')" value="" />
                          <el-option :label="t('selfShop')" value="1" />
                          <el-option :label="t('notSelfShop')" value="0" />
                        </el-select>
                  </el-form-item>


                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="shopTableData.searchParam.create_time" type="datetimerange"
                            value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')"
                            :end-placeholder="t('endDate')" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadShopList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[20px]">
                <el-table :data="shopTableData.data" size="large" v-loading="shopTableData.loading">

                    <template #empty>
                        <span>{{ !shopTableData.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="site_id" :label="t('siteId')" min-width="100" :show-overflow-tooltip="true" />

                    <el-table-column :label="t('siteInfo')" min-width="280" align="left">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-if="row.front_end_logo" :src="img(row.front_end_logo)" alt="">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/site_logo.png" alt="">
                                <div class="flex flex flex-col">
                                    <span>{{ row.site_name || '' }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('commissionRate')" min-width="150" >
                        <template #default="{ row }">
                            <div v-if="row.shop">{{ row.shop.commission_rate }}%</div>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('supplyRate')" min-width="150"  v-if="user_store.checkAddon('supply')">
                        <template #default="{ row }">
                            <div v-if="row.shop">{{ row.shop.supply_service_rate }}%</div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('manager')" min-width="120" align="left">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <div class="flex flex flex-col">
                                    <span>{{ row.admin.username || '' }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="app_name" :label="t('businessType')" min-width="150"  />
                    <el-table-column prop="category_name" :label="t('categoryName')" min-width="150"  />
                    <el-table-column prop="is_self" :label="t('isSelf')" min-width="100">
                        <template #default="{ row }">
                            <div v-if="row.is_self == 1">{{ t('selfShop') }}</div>
                            <div v-if="row.is_self == 0">{{ t('notSelfShop') }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('status')" min-width="100" align="center">
                        <template #default="{ row }">
                            <el-tag class="ml-2" type="success" v-if="row.status == 1">{{ row.status_name }}</el-tag>
                            <el-tag class="ml-2" type="error" v-else-if="row.status == 3">
                                {{ row.status_name }}
                            </el-tag>
                            <el-tag class="ml-2" type="error" v-else>
                                {{ row.status_name }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('businessStatus')" min-width="100" align="center">
                        <template #default="{ row }">
                            <el-tag class="ml-2" type="success" v-if="row.business_status == 1">{{ row.business_status_name }}</el-tag>
                            <el-tag class="ml-2" type="error" v-else>{{ row.business_status_name }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="180" :show-overflow-tooltip="true" />

                    <el-table-column :label="t('operation')" min-width="200" align="right" fixed="right">
                        <template #default="{ row }">
							<el-button type="primary" link @click="reserPassword(row)">{{ t('resetPassword') }}</el-button>
                            <el-button type="primary" link @click="openClose(row.status, row.site_id)"
                                v-if="row.status == 1 || row.status == 3">{{ row.status == 1 ? t('closeTxt') : t('openTxt') }}</el-button>
                                <!--  <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>-->
                                <!--<el-button type="primary" link @click="deleteEvent(row)">{{ t('delete') }}</el-button>-->
                            <el-button type="primary" link @click="infoEvent(row)">{{ t('info') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="shopTableData.page" v-model:page-size="shopTableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="shopTableData.total"
                        @size-change="loadShopList()" @current-change="loadShopList" />
                </div>
            </div>
        </el-card>
		<el-dialog v-model="resetPasswordDialog" :title=" t('resetPassword')" width="500">
            <el-form ref="passwordFormRef" :model="formPassword" :rules="formPasswordRules" label-width="100px" v-loading="formLoading">
				<el-form-item :label="t('password')" prop="password">
                    <el-input v-model="formPassword.password" clearable :placeholder="t('passwordResetPlaceholder')" class="input-width" :show-password="true" type="password" :readonly="password_input" @click="password_input = false" @blur="password_input = true" />
				</el-form-item>
				<el-form-item :label="t('confirmPassword')" prop="confirm_password">
                    <el-input v-model="formPassword.confirm_password" :placeholder="t('confirmPasswordPlaceholder')" type="password" :show-password="true" clearable class="input-width" :readonly="confirm_password_input" @click="confirm_password_input = false" @blur="confirm_password_input = true" />
				</el-form-item>
            </el-form>
            <template #footer>
				<div class="dialog-footer">
					<el-button @click="closeDialog">{{t('cancel')}}</el-button>
					<el-button type="primary" :loading="formLoading" @click="confrimPassword()">{{t('confrim')}}</el-button>
				</div>
            </template>
		</el-dialog>
        <edit-shop ref="addSiteDialog" @complete="loadShopList()" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { img } from '@/utils/common'
import { t } from '@/lang'
import { getShopPageList, getShopGroupAll, getStatusList, getAppList, closeShop, openShop, deleteShop,resetPassword } from '@/app/api/shop_admin'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import EditShop from '@/app/views/shop/admin/components/edit-shop.vue'
import { getInstalledAddonList } from '@/app/api/addon'
import userStore from '@/stores/modules/user'
const formLoading = ref(false)
const route = useRoute()
const pageName = route.meta.title
const user_store = userStore()
const groupList = ref([])
const passwordFormRef = ref<FormInstance>()
const password_input = ref(true)
const confirm_password_input = ref(true)
const resetPasswordDialog= ref(false)
const statusList = ref([])
const formPassword = reactive({
    password: '',
    confirm_password: '',
    id: ''
})
const shopTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        group_id: '',
        app: 'all',
        status: '',
        site_domain: '',
        create_time: [],
        expire_time: [],
        is_self: ''
    }
})
shopTableData.searchParam.status = route.query.id || ''
const setGroupList = async () => {
    const obj = await (await getShopGroupAll({})).data

    groupList.value = obj
    obj.forEach((item:any, index:any) => {
        if (!groupList.value[item.app]) {
            groupList.value[item.app] = []
            groupList.value[item.app].push(item)
        } else {
            groupList.value[item.app].push(item)
        }
    })
}
setGroupList()

const setStatusList = async () => {
    statusList.value = await (await getStatusList()).data
}

setStatusList()
const appList = ref([])
const getAppListFn = async () => {
    appList.value = await (await getAppList()).data
}
getAppListFn()

const searchFormRef = ref<FormInstance>()

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadShopList()
}
// 表单验证规则
const formPasswordRules = computed(() => {
    return {
        password: [
            { required: true, message: t('passwordPlaceholder'), trigger: 'blur' }
        ],
        confirm_password: [
            { required: true, message: t('confirmPasswordPlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    if (true && value != formPassword.password) callback(new Error(t('confirmPasswordError')))
                    else callback()
                },
                trigger: 'blur'
            }
        ]
    }
})
/**
 * 应用选择
 */
const appChangeFn = () => {
    shopTableData.searchParam.group_id = ''
}

/**
 * 获取应用列表
 */
const addonList = ref([])
getInstalledAddonList().then(({ data }) => {
    addonList.value = data
}).catch()

/**
 * 获取店铺列表
 */
const loadShopList = (page: number = 1) => {
    shopTableData.loading = true
    shopTableData.page = page
    shopTableData.searchParam.app = shopTableData.searchParam.app == 'all' ? '' : shopTableData.searchParam.app
    getShopPageList({
        page: shopTableData.page,
        limit: shopTableData.limit,
        ...shopTableData.searchParam
    }).then(res => {
        shopTableData.loading = false
        shopTableData.data = res.data.data
        shopTableData.total = res.data.total
    }).catch(() => {
        shopTableData.loading = false
    })
}
loadShopList()

const router = useRouter()

const addSiteDialog: Record<string, any> | null = ref(null)
/**
 * 添加店铺
 */
const addEvent = (data: any) => {
    addSiteDialog.value.setFormData()
    addSiteDialog.value.showDialog = true
}

/**
 * 店铺详情
 * @param data
 */
const infoEvent = (data: any) => {
    router.push({ path: '/shop/info', query: { id: data.site_id } })
}

/**
 * 编辑店铺详情
 * @param data
 */
const editEvent = (data: any) => {
    addSiteDialog.value.setFormData(data)
    addSiteDialog.value.showDialog = true
}

/**
 * 店铺登录
 */
const toSiteLink = () => {
    window.open(`${location.origin}/site/`)
}

const openClose = (i, site_id) => {
    if (i == 1) {
        closeShop({ site_id }).then(res => {
            loadShopList()
        })
    }
    if (i == 3) {
        openShop({ site_id }).then(res => {
            loadShopList()
        })
    }
}

const deleteEvent = (data: any) => {
    ElMessageBox.confirm(t('siteDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteShop(data.site_id).then(res => {
            loadShopList()
        }).catch(() => {
        })
    })
}
const reserPassword = (data: any) =>{
    formPassword.id = data.site_id
    resetPasswordDialog.value = true
}
const closeDialog = () =>{
    formPassword.password = ''
    formPassword.confirm_password = ''
    resetPasswordDialog.value = false // 关闭对话框
}
/**
 * 确认
 * @param formEl
 */
const confrimPassword = async () => {
    if (formLoading.value || !passwordFormRef.value) return
	
    await passwordFormRef.value.validate(async (valid) => {
        if (valid) {
            formLoading.value = true
            resetPassword(formPassword).then(res => {
                formLoading.value = false
                closeDialog() // 关闭对话框
            }).catch(() => {
                formLoading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
:deep(.warm-prompt) {
    .el-alert__description {
        margin: 0;
    }
}</style>
