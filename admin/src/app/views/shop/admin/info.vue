<template>
    <div class="main-container" v-loading="loading">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/shop/list' })" />
        </el-card>
        <el-card class="box-card !border-none" shadow="never">
            <el-tabs v-model="activeName" @tab-change="handleClickTab">
                <el-tab-pane :label="t('basicInfoTab')" name="basic"></el-tab-pane>
                <el-tab-pane :label="t('settlementAccountTab')" name="settlement"></el-tab-pane>
                <el-tab-pane :label="t('financeInfoTab')" name="finance"></el-tab-pane>
            </el-tabs>
            <el-form :model="formData" label-width="170px" ref="formRef" :rules="formRules" class="page-form"
                v-if="activeName == 'basic'">
                <el-card class="box-card !border-none relative" shadow="never">
                    <el-form-item :label="t('shopName')" prop="site_name">
                        <el-input v-model="formData.site_name" clearable :placeholder="t('shopNamePlaceholder')" maxlength="20" class="input-width" show-word-limit />
                    </el-form-item>
					<el-form-item :label="t('businessType')" prop="select_type">
					    <el-radio-group v-model="formData.select_type" class="ml-4">
					       <el-radio label="group">{{t('groupSelect')}}</el-radio>
					       <el-radio label="diy">{{t('manualSelect')}}</el-radio>
					    </el-radio-group>
					</el-form-item>
					<el-form-item :label="t('groupSelect')" prop="group_id" v-if="formData.select_type == 'group'">
					    <el-select v-model="formData.group_id" clearable :placeholder="t('groupNamePlaceholder')" class="input-width">
					        <el-option :label="item['group_name']" :value="item['group_id']" v-for="(item, index) in groupList" :key="index" />
					    </el-select>
					</el-form-item>
					<el-form-item :label="t('manualSelect')" prop="app" v-if="formData.select_type == 'diy'">
					    <el-select v-model="formData.app" clearable :placeholder="t('typeListPlaceholder')" class="input-width" multiple @change="changeAppList">
					        <el-option :label="item['title']" :value="item['key']" v-for="(item, index) in appList" :key="index" />
					    </el-select>
					</el-form-item>
					<el-form-item :label="t('addonsList')" prop="addons" v-if="formData.select_type == 'diy' && formData.app.length && addonSelectList.length">
					    <el-select v-model="formData.addons" clearable :placeholder="t('addonsListPlaecholder')" class="input-width" multiple>
					        <el-option :label="item['title']" :value="item['key']" v-for="(item, index) in addonSelectList" :key="index" />
					    </el-select>
					</el-form-item>
                    <el-form-item :label="t('mallServiceRate')" prop="commission_rate">
                        <el-input v-model="formData.commission_rate" clearable :placeholder="t('mallServiceRatePlaceholder')"
                            class="input-width" maxlength="10">
                             <template #append>%</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item :label="t('supplyServiceRate')" prop="supply_service_rate" v-if="user_store.checkAddon('supply')">
                        <el-input v-model="formData.supply_service_rate" clearable :placeholder="t('supplyServiceRatePlaceholder')"
                            class="input-width" maxlength="10">
                             <template #append>%</template>
                        </el-input>
                    </el-form-item>
                    <el-form-item :label="t('contactMobile')" prop="phone">
                        <div>
                            <el-input v-model="formData.phone" clearable :placeholder="t('contactMobilePlaceholder')"
                                class="input-width" show-word-limit />
                            <p class="text-[12px] text-[#a9a9a9]">{{ t('contactMobileDesc') }}</p>
                        </div>
                    </el-form-item>
                    <el-form-item :label="t('isSelf')" prop="is_self">
                        <el-radio-group v-model="formData.is_self" class="ml-4">
                            <el-radio :label="1">{{ t('selfShop') }}</el-radio>
                            <el-radio :label="0">{{ t('notSelfShop') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="t('categoryName')" prop="category_id">
                        <el-select v-model="formData.category_id" clearable :placeholder="t('categoryNamePlaceholder')"
                            class="input-width">
                            <el-option :label="item.category_name" :value="item.category_id"
                                v-for="(item, index) in categoryList" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('businessLicense')" prop="business_license">
                        <upload-image v-model="formData.business_license"  :limit="10" />
                    </el-form-item>
<!--                    <el-form-item :label="t('expireTime')" prop="expire_time" class="input-width">-->
<!--                        <el-date-picker v-model="formData.expire_time" clearable type="datetime"-->
<!--                            :placeholder="t('expireTimePlaceholder')" />-->
<!--                    </el-form-item>-->
                    <el-form-item :label="t('siteAddress')" prop="address_area">
                        <el-select v-model="formData.province_id" value-key="id" clearable class="w-[200px]"  ref="provinceRef">
                            <el-option :label="t('provincePlaceholder')" :value="0"/>
                            <el-option v-for="(item, index) in areaList.province " :key="index" :label="item.name"  :value="item.id"/>
                        </el-select>
                        <el-select v-model="formData.city_id" value-key="id" clearable class="w-[200px] ml-3" ref="cityRef">
                            <el-option :label="t('cityPlaceholder')" :value="0"/>
                            <el-option v-for="(item, index) in areaList.city " :key="index" :label="item.name"  :value="item.id"/>
                        </el-select>
                        <el-select v-model="formData.district_id" value-key="id" clearable class="w-[200px] ml-3"  ref="districtRef">
                            <el-option :label="t('districtPlaceholder')" :value="0"/>
                            <el-option v-for="(item, index) in areaList.district " :key="index" :label="item.name"  :value="item.id"/>
                        </el-select>
                    </el-form-item>

                    <el-form-item prop="address">
                        <el-input v-model.trim="formData.address" clearable :placeholder="t('addressPlaceholder')" @input="areaChange()" class="input-width" />
                    </el-form-item>

                    <el-form-item>
                        <div id="container" class="w-[800px] h-[520px] relative z-[1]" v-loading="mapLoading"></div>
                    </el-form-item>
                </el-card>
            </el-form>
            <!-- 结算账户 -->
            <el-form :model="formBank" label-width="150px" ref="formBankRef" :rules="formBankRules" class="page-form"
                v-if="activeName == 'settlement'">
                <el-card class="box-card !border-none relative" shadow="never">
                    <el-form-item :label="t('accountType')">
                        <el-radio-group v-model="formBank.bank_type" class="ml-4">
                            <el-radio :label="1">银行卡</el-radio>
                            <el-radio :label="2">支付宝</el-radio>
                            <el-radio :label="3">微信</el-radio>
                            <el-radio :label="4">微信零钱</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <div v-if="formBank.bank_type == 1">
                        <el-form-item :label="t('bankAccountName')" prop="bank_account_name">
                            <el-input v-model="formBank.bank_account_name" clearable
                                :placeholder="t('bankAccountNamePlaceholder')" class="input-width" />
                        </el-form-item>
                        <el-form-item :label="t('companyBankAccountNumber')" prop="bank_account_no">
                            <el-input v-model="formBank.bank_account_no" clearable
                                :placeholder="t('companyBankPlaceholder')" class="input-width"
                                @keyup="filterNumber($event)" />
                        </el-form-item>
                        <el-form-item :label="t('bankBranchName')" prop="bank_name">
                            <el-input v-model="formBank.bank_name" clearable :placeholder="t('bankBranchNamePlaceholder')"
                                class="input-width" />
                        </el-form-item>
                        <el-form-item :label="t('bankAddress')" prop="bank_address">
                            <el-input v-model="formBank.bank_address" clearable :placeholder="t('bankAddressPlaceholder')"
                                class="input-width" />
                        </el-form-item>
                    </div>
                    <div v-else-if="formBank.bank_type == 2">
                        <el-form-item :label="t('alipayAccountName')" prop="alipay_name">
                            <el-input v-model="formBank.alipay_name" clearable
                                :placeholder="t('alipayAccountNamePlaceholder')" class="input-width" />
                        </el-form-item>
                        <el-form-item :label="t('alipayBankAccountNumber')" prop="alipay_account_no">
                            <el-input v-model="formBank.alipay_account_no" clearable
                                :placeholder="t('alipayBankPlaceholder')" class="input-width" />
                        </el-form-item>
                        <el-form-item :label="t('alipayCode')" prop="alipay_payment_code">
                            <upload-image v-model="formBank.alipay_payment_code" :limit="1" />
                        </el-form-item>
                    </div>
                    <div v-else-if="formBank.bank_type == 3">
                        <el-form-item :label="t('wechatName')" prop="wechat_name">
                            <el-input v-model="formBank.wechat_name" clearable :placeholder="t('wechatNamePlaceholder')" class="input-width" maxlength="20" />
                        </el-form-item>
                        <el-form-item :label="t('wechatAccountNo')" prop="wechat_account_no">
                            <el-input v-model="formBank.wechat_account_no" clearable :placeholder="t('wechatAccountNoPlaceholder')" class="input-width" maxlength="40" />
                        </el-form-item>
                        <el-form-item :label="t('wechatCode')" prop="wechat_payment_code">
                            <upload-image v-model="formBank.wechat_payment_code" :limit="1" />
                        </el-form-item>
                    </div>
                </el-card>
            </el-form>
            <div v-if="activeName == 'finance'">
                <el-card class="box-card !border-none" shadow="never">
                    <div class="card-header mb-[20px]">
                        <span class="text-[18px] font-[600] text-[#333] leading-[24px]">{{ t("assetProfile") }}</span>
                    </div>
                    <el-row :gutter="20">
                        <el-col :span="6">
                            <div>
                                <el-statistic :value="shopAccount.money">
                                    <template #title>
                                        <div class="text-[14px] mb-[9px] text-[#666]">
                                            {{ t("usedBalance") }}
                                        </div>
                                    </template>
                                </el-statistic>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div>
                                <el-statistic :value="parseFloat(shopAccount.withdrawal_money).toFixed(2)">
                                    <template #title>
                                        <div class="text-[14px] mb-[9px] text-[#666]">
                                            {{ t("moneyGetOut") }}
                                        </div>
                                    </template>
                                </el-statistic>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div>
                                <el-statistic :value="shopAccount.money_cash_outing">
                                    <template #title>
                                        <div class="text-[14px] mb-[9px] text-[#666]">
                                            {{ t("moneyCashOuting") }}
                                        </div>
                                    </template>
                                </el-statistic>
                            </div>
                        </el-col>
                        <el-col :span="6">
                            <div>
                                <el-statistic :value="shopAccount.shop_deposit">
                                    <template #title>
                                        <div class="text-[14px] mb-[9px] text-[#666]">
                                            {{ t("securityDeposit") }}
                                        </div>
                                    </template>
                                </el-statistic>
                            </div>
                        </el-col>
                    </el-row>
                </el-card>
                <div>
                    <div class="flex items-center text-[14px] leading-[42px]">
                        <span class="px-[20px] border-[1px] border-solid border-[#f1f1f1] cursor-pointer"
                            :class="{ 'bg-[#F7F7F7]': shopStatus == 'account' }"
                            @click="handleClickAccount('account')">账户收支</span>
                        <span class="px-[20px] border-[1px] border-solid border-[#f1f1f1] border-l-[0px] cursor-pointer"
                            :class="{ 'bg-[#F7F7F7]': shopStatus == 'cashOut' }"
                            @click="handleClickAccount('cashOut')">提现记录</span>
                    </div>
                    <div>
                        <!-- 账户收支 -->
                    </div>
                    <div class="mt-[20px]" v-if="shopStatus == 'account'">
                        <el-table :data="accountTableData.data" size="large" v-loading="accountTableData.loading">
                            <template #empty>
                                <span>{{ !accountTableData.loading ? t('emptyData') : '' }}</span>
                            </template>
                            <el-table-column prop="money" :label="t('money')" min-width="180" />
                            <el-table-column prop="from_type_name" :label="t('fromType')" min-width="180" />
                            <el-table-column prop="create_time" :label="t('createTime')" min-width="180" />
                            <el-table-column prop="memo" :label="t('remark')" min-width="230"
                                :show-overflow-tooltip="true" />
                        </el-table>
                        <div class="mt-[16px] flex justify-end">
                            <el-pagination v-model:current-page="accountTableData.page"
                                v-model:page-size="accountTableData.limit" layout="total, sizes, prev, pager, next, jumper"
                                :total="accountTableData.total" @size-change="loadAccountList()"
                                @current-change="loadAccountList" />
                        </div>
                    </div>
                    <!-- 提现记录 -->
                    <div class="mt-[20px]" v-if="shopStatus == 'cashOut'">
                        <el-table :data="moneyCashTableData.data" size="large" v-loading="moneyCashTableData.loading">
                            <template #empty>
                                <span>{{ !moneyCashTableData.loading ? t('emptyData') : '' }}</span>
                            </template>
                            <el-table-column prop="order_no" :show-overflow-tooltip="true" :label="t('transferRealname')"  min-width="160">
                                <template #default="{ row }">
                                    <div class="flex items-center cursor-pointer ">
                                        <img class="w-[50px] h-[50px] mr-[10px] flex-shrink-0" v-if="row.siteInfo.front_end_logo"
                                            :src="img(row.siteInfo.front_end_logo)" alt="">
                                        <img class="w-[50px] h-[50px] mr-[10px] flex-shrink-0" v-else
                                            src="@/app/assets/images/site_logo.png" alt="">
                                        <div class="flex flex flex-col">
                                            <span>{{ row.siteInfo.site_name || '' }}</span>
                                            <span>{{ row.siteInfo.phone || '' }}</span>
                                        </div>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="cash_out_no" :label="t('cashOutNo')" min-width="180" />
                            <el-table-column prop="transfer_type_name" :label="t('transferTypeName')" min-width="180" />
                            <el-table-column  :label="t('transferAccount')" min-width="180">
                                <template #default="{ row }">
                                    <div v-if="row.transfer_type == 1">
                                        <div>持卡人姓名:{{ row.transfer_realname }}</div>
                                        <div>银行卡号:{{ row.transfer_account}}</div>
                                        <div>银行名称：{{ row.transfer_bank}}</div>
                                    </div>
                                    <div v-else-if="row.transfer_type == 2">
                                        <div>真实姓名:{{ row.transfer_realname }}</div>
                                        <div>收款账号:{{ row.transfer_account}}</div>
                                    </div>
                                    <div v-else-if="row.transfer_type == 3">
                                        <div>真实姓名:{{ row.transfer_realname }}</div>
                                        <div>收款账号:{{ row.transfer_account}}</div>
                                    </div>
                                    <div v-else>--</div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="apply_money" :label="t('applyMoney')" min-width="180" />
                            <el-table-column prop="create_time" :label="t('createTime')" min-width="180" />
                        </el-table>
                        <div class="mt-[16px] flex justify-end">
                            <el-pagination v-model:current-page="moneyCashTableData.page"
                                v-model:page-size="moneyCashTableData.limit"
                                layout="total, sizes, prev, pager, next, jumper" :total="moneyCashTableData.total"
                                @size-change="loadMoneyCashList()" @current-change="loadMoneyCashList" />
                        </div>
                    </div>
                </div>
            </div>
        </el-card>
        <div class="fixed-footer-wrap" v-if="activeName == 'settlement' || activeName == 'basic'">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)" v-if="activeName == 'basic'">{{ t('confirm')
                }}</el-button>
                <el-button type="primary" @click="onSave(formBankRef)" v-else>{{ t('confirm') }}</el-button>
                <el-button @click="back()">{{ t('cancel') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getShopInfo, getShopAccount, getShopAccountLog, getShopWithdrawal, setShopAccount, setShopInfo, getShopGroupAll, getShopCategoryAll,getAppList, getAddonList } from '@/app/api/shop_admin'
import { getMap, getAreaListByPid, getAreaByCode } from '@/app/api/sys'
import { createMarker, latLngToAddress, addressToLatLng } from '@/utils/qqmap'
import { filterNumber, debounce, img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import useTabbarStore from '@/stores/modules/tabbar'
import userStore from '@/stores/modules/user'

const user_store = userStore()
const tabbarStore = useTabbarStore()
const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const id: number = parseInt(route.query.id)
const loading = ref(true)
const activeName = ref('basic')
const addonSelectList = ref([])
const appList = ref([])
/**
 * 表单数据
 */
const initialFormData = {
    site_name: '',
    group_id: 0,
    commission_rate: '',
    supply_service_rate: '',
    phone: '',
    // expire_time: 0,
    is_self: 0,
    category_id: '',
    business_license: '',
    province_id: 0,
    city_id: 0,
    district_id: 0,
    address: '',
    full_address: '',
    latitude: 39.908626,
    longitude: 116.397190,
    app:[],
    addons:'',
    select_type:'group',
}
const formData: Record<string, any> = reactive({ ...initialFormData })

// 结算账户表单数据
const formBank = reactive<any>({
    bank_type: 1,
    bank_account_name: '',
    bank_account_no: '',
    bank_name: '',
    bank_address: '',
    alipay_name: '',
    alipay_account_no: '',
    alipay_payment_code: '',
    wechat_name: '',
    wechat_account_no: '',
    wechat_payment_code: ''
})
// 店铺账户信息
const shopAccount = ref<any>('')
const setFormData = async (id: number = 0) => {
    Object.assign(formData, initialFormData)
    const data = await (await getShopInfo(id)).data
    shopAccount.value = await (await getShopAccount(id)).data
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })
    formData.commission_rate = data.shop?.commission_rate
    formData.supply_service_rate = data.shop?.supply_service_rate
    formData.latitude = formData.latitude || 39.908626
    formData.longitude = formData.longitude || 116.397190
    Object.keys(formBank).forEach((key: string) => {
        if (shopAccount.value[key] != undefined) formBank[key] = shopAccount.value[key]
    })
    if(formData.app && formData.app.length){
        setAddonList()
    }
    if(!formData.group_id){
        formData.select_type = 'diy'
        formData.group_id = ''
    }else{
        formData.select_type = 'group'
    }
    loading.value = false
}
const setAppList = async () => {
    appList.value = await (await getAppList()).data
}
setAppList()
const setAddonList = async () => {
    addonSelectList.value = await (await getAddonList({app:formData.app})).data
}
const changeAppList = (e:any) =>{
    setAddonList()
}
if (id) setFormData(id)
else loading.value = false

// 店铺套餐
const groupList = ref([])
const categoryList = ref([])
const setGroupList = () => {
    getShopGroupAll().then(res => {
        groupList.value = res.data
    })
}
setGroupList()
// 店铺分类
const setCategoryList = () => {
    getShopCategoryAll().then(res => {
        categoryList.value = res.data
    })
}
setCategoryList()

const formRef = ref<FormInstance>()
const formRules = computed(() => {
    return {
        site_name: [
            { required: true, message: t('shopNamePlaceholder'), trigger: 'blur' }
        ],
        group_id: [
            { required: true, message: t('groupNamePlaceholder'), trigger: 'blur' }
        ],
        category_id: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
        ],
        app: [
            { required: formData.select_type == 'diy' ? true : false, message: t('typeListPlaceholder'), trigger: 'blur' }
        ],
        phone: [
            { required: true, message: t('contactMobilePlaceholder'), trigger: 'blur' },
            {
                validator: (rule: any, value: string, callback: any) => {
                    const phonePattern = /^(?:(?:\+|00)86)?1[3-9]\d{9}$|^(?:(?:\d{3}-)?\d{8}|^(?:\d{4}-)?\d{7,8})(?:-\d+)?$|^[48]00-?\d{3}-?\d{4}$|^(?:\+?\d{1,3}[-\s.]?)?(?:\(?\d{2,4}\)?[-\s.]?)?\d{2,4}[-\s.]?\d{3,4}[-\s.]?\d{3,4}$/
                    if (!value) {
                        return callback(new Error(t('contactMobilePlaceholder')))
                    } else if (!phonePattern.test(value)) {
                        return callback(new Error(t('contactMobileTips')))
                    } else {
                        return callback()
                    }
                },
                trigger: 'blur'
            }
        ],
        // commission_rate: [
        //     { required: true, message: t('mallServiceRatePlaceholder'), trigger: 'blur' } 
        // ],
        supply_service_rate: [
            { required: true, message: t('supplyServiceRatePlaceholder'), trigger: 'blur' } 
        ],
        // expire_time: [
        //     { required: true, message: t('expireTimePlaceholder'), trigger: 'blur' }
        // ],
        // address_area: [
        //     {
        //         validator: (rule: any, value: any, callback: any) => {
        //             if (!formData.province_id) {
        //                 callback(new Error(t('provincePlaceholder')))
        //             }
        //             if (!formData.city_id) {
        //                 callback(new Error(t('cityPlaceholder')))
        //             }
        //             if (areaList.district.length && !formData.district_id) {
        //                 callback(new Error(t('districtPlaceholder')))
        //             }
        //             callback()
        //         }
        //     }
        // ],
        // address: [
        //     { required: true, message: t('addressPlaceholder'), trigger: 'blur' }
        // ]
    }
})
const formBankRef = ref<FormInstance>()
// 表单验证规则
const formBankRules = computed(() => {
    return {
        bank_account_name: [
            { required: true, message: t('bankAccountNamePlaceholder'), trigger: 'blur' }
        ],
        bank_account_no: [
            { required: true, message: t('companyBankPlaceholder'), trigger: 'blur' }
        ],
        bank_name: [
            { required: true, message: t('bankBranchNamePlaceholder'), trigger: 'blur' }
        ],
        bank_address: [
            { required: true, message: t('bankAddressPlaceholder'), trigger: 'blur' }
        ],
        alipay_name: [
            { required: true, message: t('alipayAccountNamePlaceholder'), trigger: 'blur' }
        ],
        alipay_account_no: [
            { required: true, message: t('alipayBankPlaceholder'), trigger: 'blur' }
        ],
        alipay_payment_code: [
            { required: true, message: t('alipayPaymentCodePlaceholder'), trigger: 'blur' }
        ],
        wechat_name: [
            { required: true, message: t('wechatNamePlaceholder'), trigger: 'blur' }
        ],
        wechat_account_no: [
            { required: true, message: t('wechatAccountNoPlaceholder'), trigger: 'blur' }
        ],
        wechat_payment_code: [
            { required: true, message: t('wechatPaymentCodePlaceholder'), trigger: 'blur' }
        ]
    }
})
const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    if(formData.select_type == 'diy'){
        formData.group_id = 0
    }
    const save = activeName.value == 'basic' ? setShopInfo : setShopAccount
    const data = activeName.value == 'basic' ? formData : formBank
    data.id = id
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
			
            if (activeName.value == 'basic') {
                const address = [
                    data.province_id ? provinceRef.value.selectedLabel : '',
                    data.city_id ? cityRef.value.selectedLabel : '',
                    data.district_id ? districtRef.value.selectedLabel : '',
                    data.address
                ]
                data.full_address = address.join('')
            }

            save(data).then(res => {
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const back = () => {
    tabbarStore.removeTab(route.path)
    router.push({ path: '/shop/list' })
}
// 切换订单状态
const handleClickTab = (event: any) => {
    activeName.value = event
    if (activeName.value == 'basic') {
        setTimeout(() => {
            initMap()
        }, 500)
    }
}

const shopStatus = ref('account')
const handleClickAccount = (data: any) => {
    shopStatus.value = data
}
// 账户收支
const accountTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})
/**
 * 获取账户收支
 */
const loadAccountList = (page: number = 1) => {
    accountTableData.loading = true
    accountTableData.page = page
    getShopAccountLog({
        page: accountTableData.page,
        limit: accountTableData.limit,
        id: id
    }).then(res => {
        accountTableData.loading = false
        accountTableData.data = res.data.data
        accountTableData.total = res.data.total
    }).catch(() => {
        accountTableData.loading = false
    })
}
loadAccountList()
// 提现记录
const moneyCashTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})
/**
 * 获取账户信息
 */
const loadMoneyCashList = (page: number = 1) => {
    moneyCashTableData.loading = true
    moneyCashTableData.page = page
    getShopWithdrawal({
        page: moneyCashTableData.page,
        limit: moneyCashTableData.limit,
        id: id
    }).then(res => {
        moneyCashTableData.loading = false
        moneyCashTableData.data = res.data.data
        moneyCashTableData.total = res.data.total
    }).catch(() => {
        moneyCashTableData.loading = false
    })
}
loadMoneyCashList()

interface areaType{
    province: any[],
    city: any[],
    district: any[]
}
const areaList = reactive<areaType>({
    province: [],
    city: [],
    district: []
})
const provinceRef = ref()
const cityRef = ref()
const districtRef = ref()

/**
 * 获取省
 */
getAreaListByPid(0).then(res => {
    areaList.province = res.data
})

let mapKey: string = ''
onMounted(() => {
    const mapScript = document.createElement('script')
    getMap().then(res => {
        mapKey = res.data.key
        mapScript.type = 'text/javascript'
        mapScript.src = 'https://map.qq.com/api/gljs?libraries=tools,service&v=1.exp&key=' + res.data.key
        document.body.appendChild(mapScript)
    })
    mapScript.onload = () => {
        setTimeout(() => {
            initMap()
        }, 500)
    }
})

/**
* 初始化地图
 */
let map: any
let marker: any
const mapLoading = ref(true)
const initMap = () => {
    const TMap = (window as any).TMap
    const LatLng = TMap.LatLng
    const center = new LatLng(formData.latitude, formData.longitude)

    map = new TMap.Map('container', {
        center,
        zoom: 14
    })

    map.on('tilesloaded', () => {
        mapLoading.value = false
    })

    marker = createMarker(map)

    map.on('click', (evt: any) => {
        map.setCenter(evt.latLng)
        marker.updateGeometries({
            id: 'center',
            position: evt.latLng
        })
        latLngChange(evt.latLng.lat, evt.latLng.lng)
    })

    latLngChange(center.lat, center.lng)
}

const storeArea = reactive({
    province_id: 0,
    city_id: 0,
    district_id: 0
})

const latLngChange = (lat: number, lng: number) => {
    latLngToAddress({ mapKey, lat, lng }).then(({ message, result }) => {
        if (message == 'query ok' || message == 'Success') {
            formData.latitude = result.location.lat
            formData.longitude = result.location.lng
            formData.address = result.formatted_addresses && result.formatted_addresses.recommend ? result.formatted_addresses.recommend : ''

            getAreaByCode(result.ad_info.adcode).then(({ data }) => {
                storeArea.province_id = data.province ? data.province.id : 0
                storeArea.city_id = data.city ? data.city.id : 0
                storeArea.district_id = data.district ? data.district.id : 0
            })
        } else {
            console.error(message, result)
        }
    }).catch(err => {
        // console.log(err)
    })
}

/**
 * 获取市
 */
watch(() => formData.province_id, (nval) => {
    if (nval) {
        getAreaListByPid(formData.province_id).then(res => {
            areaList.city = res.data

            const cityId = formData.city_id
            if (cityId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (cityId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.city_id = cityId
                    return
                }
            }
            formData.city_id = 0
            areaChange()
        })
    } else {
        formData.city_id = 0
    }
})

/**
* 获取区
 */
watch(() => formData.city_id, (nval) => {
    if (nval) {
        getAreaListByPid(formData.city_id).then(res => {
            areaList.district = res.data

            const districtId = formData.district_id
            if (districtId) {
                let isExist = false
                for (let i = 0; i < res.data.length; i++) {
                    if (districtId == res.data[i].id) {
                        isExist = true
                        break
                    }
                }
                if (isExist) {
                    formData.district_id = districtId
                    return
                }
            }
            areaChange()
            formData.district_id = 0
        })
    } else {
        formData.district_id = 0
    }
})

watch(() => formData.district_id, (nval) => {
    if (nval) {
        areaChange()
    }
})

const areaChange = debounce(() => {
    setTimeout(() => {
        const address = [
            formData.province_id ? provinceRef.value.selectedLabel : '',
            formData.city_id ? cityRef.value.selectedLabel : '',
            formData.district_id ? districtRef.value.selectedLabel : '',
            formData.address
        ]
        addressToLatLng({ mapKey, address: address.join('') }).then(({ message, result }) => {
            if (message == 'Success' || message == 'query ok') {
                const latLng = new (window as any).TMap.LatLng(result.location.lat, result.location.lng)
                map.setCenter(latLng)
                marker.updateGeometries({
                    id: 'center',
                    position: latLng
                })
                formData.latitude = result.location.lat
                formData.longitude = result.location.lng
            } else {
                // console.log(message, result)
            }
        })
    }, 500)
}, 500)

/**
 * 地图点选获取市
 */
watch(() => storeArea.province_id, (nval) => {
    if (nval) {
        getAreaListByPid(storeArea.province_id).then(res => {
            areaList.city = res.data
            formData.province_id = storeArea.province_id
            formData.city_id = storeArea.city_id
        })
    }
})

/**
* 地图点选获取区
 */
watch(() => storeArea.city_id, (nval) => {
    if (nval) {
        getAreaListByPid(storeArea.city_id).then(res => {
            areaList.district = res.data
            formData.city_id = storeArea.city_id
            formData.district_id = storeArea.district_id
        })
    }
})

/**
 * 地图点选获取区
 */
watch(() => storeArea.district_id, (nval) => {
    if (nval) {
        formData.district_id = storeArea.district_id
    }
})
</script>

<style lang="scss" scoped></style>
