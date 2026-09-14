<template>
    <div class="main-container" v-loading="loading">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/mall/shop/list' })" />
        </el-card>
        <el-card class="box-card !border-none" shadow="never">
            <el-tabs v-model="activeName" @tab-change="handleClickTab">
                <el-tab-pane :label="t('basicInfoTab')" name="basic"></el-tab-pane>
                <el-tab-pane :label="t('financeInfoTab')" name="finance"></el-tab-pane>
            </el-tabs>
            <el-form :model="formData" label-width="170px" ref="formRef" :rules="formRules" class="page-form"
                v-if="activeName == 'basic'">
                <el-card class="box-card !border-none relative" shadow="never">
                    <el-form-item :label="t('shopName')" prop="site_name">
                        <el-input v-model="formData.site_name" clearable :placeholder="t('shopNamePlaceholder')" maxlength="20" class="input-width" show-word-limit />
                    </el-form-item>
					<el-form-item :label="t('categoryName')" prop="category_name">
                        <el-input v-model="formData.category_name" clearable :placeholder="t('categoryNamePlaceholder')" maxlength="20" class="input-width" disabled />
                    </el-form-item>
                    <el-form-item :label="t('childCategoryName')" prop="child_category_id" v-if="formData.child_category_id">
					    <el-select v-model="formData.child_category_id" clearable :placeholder="t('childCategoryNamePlaceholder')" class="input-width">
					        <el-option :label="item['name']" :value="item['category_id']" v-for="(item, index) in childCategoryList" :key="index" />
					    </el-select>
					</el-form-item>
                    <el-form-item :label="t('circle')" prop="circle_id" v-if="user_store.checkAddon('circle')">
                        <el-cascader v-model="formData.circle_id" :options="circleOptions" :props="{ checkStrictly: true, value: 'id', label: 'name', children: 'children', maxDepth: 3 }" clearable class="input-width" :placeholder="t('selectCirclePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('serviceRate')" prop="service_rate">
                        <el-input v-model="formData.service_rate" clearable :placeholder="t('serviceRatePlaceholder')"
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
                    <el-form-item :label="t('frontEndLogo')">
                        <div>
                            <upload-image v-model="formData.front_end_logo" />
                            <p class="text-[12px] text-[#a9a9a9]">{{ t('frontEndLogoPlaceholder') }}</p>
                        </div>
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
                        <map-selector
                            ref="mapSelectorRef"
                            :longitude="formData.longitude"
                            :latitude="formData.latitude"
                            :zoom="14"
                            :width="800"
                            :height="500"
                            @locationChange="handleLocationChange"
                        />
                    </el-form-item>
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
        <div class="fixed-footer-wrap" v-if="activeName == 'basic'">
            <div class="fixed-footer !z-[9999]">
                <el-button type="primary" @click="onSave(formRef)">{{ t('confirm') }}</el-button>
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
import { getShopInfo, editShop } from '@/app/lang/zh-cn/api/admin/site_shop'
import { getShopAccountLog, getShopWithdrawal } from '@/app/api/shop_admin'
import { getAreaListByPid, getAreaByCode } from '@/app/api/sys'
import { getCircleList } from '@/app/api/site'
import { filterNumber, debounce, img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import useTabbarStore from '@/stores/modules/tabbar'
import userStore from '@/stores/modules/user'
import MapSelector from '@/components/map-selector/index.vue'

const user_store = userStore()
const tabbarStore = useTabbarStore()
const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const id: number = parseInt(route.query.id)
const loading = ref(true)
const activeName = ref('basic')
const mapSelectorRef = ref()
/**
 * 表单数据
 */
const initialFormData = {
    site_name: '',
    service_rate: '',
    phone: '',
    // expire_time: 0,
    is_self: 0,
    category_id: '',
    child_category_id: '',
    circle_id: '',
    category_name: '',
    front_end_logo: '',
    business_license: '',
    province_id: 0,
    city_id: 0,
    district_id: 0,
    address: '',
    full_address: '',
    latitude: 39.908626,
    longitude: 116.397190
}
const formData: Record<string, any> = reactive({ ...initialFormData })

// 店铺账户信息
const shopAccount = ref<any>('')
// 子级行业列表
const childCategoryList = ref<any>([])
const setFormData = async (id: number = 0) => {
    Object.assign(formData, initialFormData)
    const data = await (await getShopInfo(id)).data
    shopAccount.value = data
    childCategoryList.value = data.child_category_list
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })
    formData.latitude = formData.latitude || 39.908626
    formData.longitude = formData.longitude || 116.397190
    loading.value = false
}

//商圈
const circleOptions = ref<any[]>([])
const loadCircleList = () => {
    getCircleList({}).then((res: any) => {
        const data = res.data
        if (data && data.length) {
            circleOptions.value = data
        }
    })
}
loadCircleList()

if (id) setFormData(id)
else loading.value = false

const formRef = ref<FormInstance>()
const formRules = computed(() => {
    return {
        site_name: [
            { required: true, message: t('shopNamePlaceholder'), trigger: 'blur' }
        ],
        category_id: [
            { required: true, message: t('categoryNamePlaceholder'), trigger: 'blur' }
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
        // service_rate: [
        //     { required: true, message: t('serviceRatePlaceholder'), trigger: 'blur' } 
        // ],
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

const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const save = editShop
    const data = { ...formData }   
    data.site_id = id
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
                // 处理circle_id，只传最后一级id
                if (Array.isArray(data.circle_id) && data.circle_id.length > 0) {
                    data.circle_id = data.circle_id[data.circle_id.length - 1]
                }
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
    router.push({ path: '/mall/shop/list' })
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

onMounted(() => {
    // 地图组件会自动初始化
})

/**
* 处理地图位置变化
 */
const handleLocationChange = (data: { lat: number, lng: number, address: any }) => {
    formData.latitude = data.lat
    formData.longitude = data.lng

    // 处理地址信息
    if (data.address) {
        // 腾讯地图返回格式
        if (data.address.formatted_addresses && !data.address.addressComponent) {
            formData.address = data.address.formatted_addresses?.recommend || ''
            const addressStr = data.address.address || ''
            matchAreaFromAddress(addressStr)
        } else if (data.address.addressComponent) {
            // 天地图返回格式
            // 使用 formatted_addresses.recommend 或 formatted_address
            const detailAddr = data.address.formatted_addresses?.recommend || data.address.formatted_address || data.address.poi || ''
            formData.address = detailAddr
            matchAreaFromTianditu(data.address.addressComponent)
        }
    }
}

// 从地址字符串匹配省市区（腾讯地图）
const matchAreaFromAddress = (addressStr: string) => {
    const provinceMatch = addressStr.match(/^(.+?省|.+?市|.+?自治区)/)
    const cityMatch = addressStr.match(/(.+?市)/)
    const districtMatches = addressStr.match(/(.+?区|.+?县)/g)

    if (provinceMatch) {
        const provinceName = provinceMatch[1]
        const matchedProvince = areaList.province.find(item =>
            item.name.includes(provinceName) || provinceName.includes(item.name)
        )

        if (matchedProvince) {
            formData.province_id = matchedProvince.id
            getAreaListByPid(matchedProvince.id).then((cityRes: any) => {
                areaList.city = cityRes.data || []

                if (cityMatch) {
                    const cityName = cityMatch[1]
                    const matchedCity = areaList.city.find(item =>
                        item.name.includes(cityName) || cityName.includes(item.name)
                    )

                    if (matchedCity) {
                        formData.city_id = matchedCity.id
                        getAreaListByPid(matchedCity.id).then((districtRes: any) => {
                            areaList.district = districtRes.data || []

                            if (districtMatches && districtMatches.length > 0) {
                                const districtName = districtMatches[districtMatches.length - 1]
                                const matchedDistrict = areaList.district.find(item =>
                                    item.name.includes(districtName) || districtName.includes(item.name)
                                )
                                if (matchedDistrict) {
                                    formData.district_id = matchedDistrict.id
                                }
                            }
                        })
                    }
                }
            })
        }
    }
}

// 从天地图地址组件匹配省市区
const matchAreaFromTianditu = (addressComp: any) => {
    if (!addressComp) return

    // 匹配省
    if (addressComp.province) {
        const matchedProvince = areaList.province.find(item =>
            item.name.includes(addressComp.province) || addressComp.province.includes(item.name)
        )
        if (matchedProvince) {
            formData.province_id = matchedProvince.id
            getAreaListByPid(matchedProvince.id).then((cityRes: any) => {
                areaList.city = cityRes.data || []

                // 匹配市
                if (addressComp.city) {
                    const matchedCity = areaList.city.find(item =>
                        item.name.includes(addressComp.city) || addressComp.city.includes(item.name)
                    )
                    if (matchedCity) {
                        formData.city_id = matchedCity.id
                        getAreaListByPid(matchedCity.id).then((districtRes: any) => {
                            areaList.district = districtRes.data || []

                            // 匹配区
                            if (addressComp.county) {
                                const matchedDistrict = areaList.district.find(item =>
                                    item.name.includes(addressComp.county) || addressComp.county.includes(item.name)
                                )
                                if (matchedDistrict) {
                                    formData.district_id = matchedDistrict.id
                                }
                            }
                        })
                    }
                }
            })
        }
    }
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

const areaChange = debounce(async () => {
    setTimeout(async () => {
        const province = areaList.province.find(item => item.id == formData.province_id)?.name || ''
        const city = areaList.city.find(item => item.id == formData.city_id)?.name || ''
        const district = areaList.district.find(item => item.id == formData.district_id)?.name || ''

        const fullAddress = [
            formData.province_id ? (provinceRef.value?.selectedLabel || province) : '',
            formData.city_id ? (cityRef.value?.selectedLabel || city) : '',
            formData.district_id ? (districtRef.value?.selectedLabel || district) : '',
            formData.address
        ].join('')
        if (fullAddress && mapSelectorRef.value) {
            try {
                const result = await mapSelectorRef.value.searchAddress(fullAddress)
                formData.latitude = result.lat
                formData.longitude = result.lng
            } catch (error) {
                console.error('地址搜索失败:', error)
            }
        }
    }, 500)
}, 500)

</script>

<style lang="scss" scoped>
:deep(#map-container) {
    position: relative;
    z-index: 1 !important;
}

:deep(#map-container div) {
    z-index: 1 !important;
}
</style>
