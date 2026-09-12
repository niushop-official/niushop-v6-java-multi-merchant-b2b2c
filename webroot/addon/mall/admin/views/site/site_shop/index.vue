<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <div v-loading="loading">
                <!-- 基础信息 -->
                <el-form :model="formData" label-width="150px" ref="formRef" :rules="formRules" class="page-form">
                    <el-card class="box-card !border-none" shadow="never">

                        <el-form-item :label="t('storeName')" prop="site_name">
                            <el-input v-model.trim="formData.site_name" :placeholder="t('siteNamePlaceholder')" class="input-width" clearable :disabled="true" maxlength="20" show-word-limit />
                        </el-form-item>
                        <el-form-item :label="t('storeCategory')">
                            <div>{{shopBaseInfo.category_name}}</div>
                        </el-form-item>
                        <el-form-item :label="t('childCategory')" v-if="shopBaseInfo.child_category_name">
                            <div>{{shopBaseInfo.child_category_name}}</div>
                        </el-form-item>
                        <el-form-item :label="t('storeClass')">
                            <div>{{shopBaseInfo.is_self ? '自营' : '直营'}}</div>
                        </el-form-item>
                        <el-form-item :label="t('storeEnterTime')">
                            <div>{{shopBaseInfo.create_time}}</div>
                        </el-form-item>
                        <!-- <el-form-item :label="t('storeExpireTime')" v-if="shopBaseInfo.expire_time">
                            <div>{{shopBaseInfo.expire_time}}</div>
                        </el-form-item> -->

                        <el-form-item :label="t('联系人姓名')" prop="contact_name">
                            <el-input v-model.trim="formData.contact_name" :placeholder="t('请输入联系人姓名')" class="input-width" clearable maxlength="50" />
                        </el-form-item>

                        <el-form-item :label="t('contactMobile')" prop="phone">
                            <div>
                                <el-input v-model="formData.phone" clearable :placeholder="t('contactMobilePlaceholder')" class="input-width" />
                                <p class="text-[12px] text-[#a9a9a9]">{{ t('contactMobileDesc') }}</p>
                            </div>
                        </el-form-item>

                        <el-form-item :label="t('businessHours')" prop="business_hours">
                            <el-input v-model.trim="formData.business_hours" :placeholder="t('businessHoursPlaceholder')" class="input-width" clearable maxlength="50" show-word-limit />
                        </el-form-item>

                        <el-form-item :label="t('businessStatus')">
                            <el-switch v-model="formData.business_status" :active-value="1" :inactive-value="0"/>
                        </el-form-item>

                        <el-form-item :label="t('keywords')">
                            <el-input v-model="formData.keywords" :placeholder="t('keywordsPlaceholder')" class="input-width" clearable maxlength="20" show-word-limit />
                        </el-form-item>

                        <el-form-item :label="t('desc')">
                            <el-input v-model="formData.desc" type="textarea" rows="4" clearable :placeholder="t('descPlaceholder')" class="input-width" maxlength="100" show-word-limit />
                        </el-form-item>

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
                            <div>
                                <el-input v-model="formData.address" :placeholder="t('siteAddressPlaceholder')" class="input-width" clearable  @input="areaChange()"  />
                                <div class="text-[12px] text-[#999] mt-[6px]">修改店铺地址后将同步修改系统默认自提点地址</div>
                            </div>
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
                    <el-card class="box-card !border-none" shadow="never">

                        <!-- <el-form-item :label="t('icon')">
                            <div>
                                <upload-image v-model="formData.icon" />
                                <p class="text-[12px] text-[#a9a9a9]">{{ t('iconPlaceholder') }}</p>
                            </div>
                        </el-form-item> -->
                        <el-form-item :label="t('frontEndLogo')">
                            <div>
                                <upload-image v-model="formData.front_end_logo" />
                                <p class="text-[12px] text-[#a9a9a9]">{{ t('frontEndLogoPlaceholder') }}</p>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('logo')">
                            <div>
                                <upload-image v-model="formData.logo" />
                                <p class="text-[12px] text-[#a9a9a9]">{{ t('logoPlaceholder') }}</p>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('H5商户背景图')" prop="background_h5">
                            <div>
                                <upload-image v-model="formData.background_h5" />
                                <p class="text-[12px] text-[#a9a9a9]">建议图片尺寸：375*180像素；图片格式：jpg、png、jpeg。</p>
                            </div>
                        </el-form-item>
                        <!-- <el-form-item :label="t('frontEndName')">
                            <el-input v-model="formData.front_end_name" :placeholder="t('frontEndNamePlaceholder')" class="input-width" clearable maxlength="20" show-word-limit />
                        </el-form-item> -->

                    </el-card>
                </el-form>
            </div>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer !z-[9999]">
                <el-button type="primary" :loading="loading" @click="save(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed, onMounted, watch } from 'vue'
import { t } from '@/lang'
import { getShopBaseInfo, setShopBaseInfo } from '@/app/lang/zh-cn/api/site/site_shop'
import { getAreaListByPid, getAreaByCode } from '@/app/api/sys'
import { FormInstance, FormRules } from 'element-plus'
import { img, getAppType, debounce } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import useUserStore from '@/stores/modules/user'
import useSystemStore from '@/stores/modules/system'
import MapSelector from '@/components/map-selector/index.vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref(true)
const appType = ref(getAppType())
const activeName = ref('basic')
const shopBaseInfo = ref<any>({})
const mapSelectorRef = ref()
const formData = reactive<Record<string, any>>({
    site_name: '',
    contact_name: '',
    phone: '',
    business_hours: '',
    business_status: 1,
    logo: '',
    icon: '',
    background_h5: '',
    keywords: '',
    desc: '',
    front_end_name: '',
    front_end_logo: '',
    province_id: 0,
    city_id: 0,
    district_id: 0,
    address: '',
    full_address: '',
    latitude: 39.908626,
    longitude: 116.397190
})

// 店铺账户信息
const setFormData = async (id: number = 0) => {
    const data = await (await getShopBaseInfo()).data
    shopBaseInfo.value = data
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })
    formData.latitude = formData.latitude || 39.908626
    formData.longitude = formData.longitude || 116.397190

    loading.value = false
}
setFormData()

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = reactive<FormRules>({
    contact_name: [
        { required: true, message: t('请输入联系人姓名'), trigger: 'blur' }
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
    ]
    // business_hours: [
    //     { required: true, message: t('businessHoursPlaceholder'), trigger: 'blur' }
    // ],
    // front_end_name: [
    //     { required: true, message: t('frontEndNamePlaceholder'), trigger: 'blur' }
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
})

/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const data: any = formData
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
            setShopBaseInfo(data).then(() => {
                loading.value = false
                // console.log(shopBaseInfo.value)
                // console.log(data)
                appType.value == 'admin' ? useSystemStore().getWebsiteInfo() : useUserStore().getSiteInfo()
                if (activeName.value == 'basic' && shopBaseInfo.value.province_id && shopBaseInfo.value.city_id && shopBaseInfo.value.district_id && (data.province_id != shopBaseInfo.value.province_id || data.city_id != shopBaseInfo.value.city_id || data.district_id != shopBaseInfo.value.district_id || data.address != shopBaseInfo.value.address)) {
                    setFormData()
                }
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

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
