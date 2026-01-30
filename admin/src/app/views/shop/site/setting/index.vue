<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <el-tabs v-model="activeName" @tab-change="handleClickTab">
                <el-tab-pane :label="t('basicInfoTab')" name="basic"></el-tab-pane>
                <el-tab-pane :label="t('storeInfoTab')" name="store"></el-tab-pane>
                <el-tab-pane :label="t('messageReceiverTab')" name="messageReceiver"></el-tab-pane>
                <!-- <el-tab-pane :label="t('settlementAccountTab')" name="settlement"></el-tab-pane> -->
            </el-tabs>
            <div v-loading="loading">
                <!-- 基础信息 -->
                <el-form :model="formData" label-width="150px" ref="formRef" :rules="formRules" class="page-form" v-if="activeName == 'basic'">
                    <el-card class="box-card !border-none" shadow="never">
                        <h3 class="panel-title !text-sm">{{ t('basicSetting') }}</h3>

                        <el-form-item :label="t('siteName')" prop="site_name">
                            <el-input v-model.trim="formData.site_name" :placeholder="t('siteNamePlaceholder')" class="input-width" clearable :disabled="true" maxlength="20" show-word-limit />
                        </el-form-item>

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
                            <div id="container" class="w-[800px] h-[520px] relative z-[1]" v-loading="mapLoading"></div>
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
                <!-- 店铺信息 -->
                <el-form :model="storeInfo" label-width="150px"  class="page-form" v-if="activeName == 'store'">
                    <el-card class="box-card !border-none relative" shadow="never" >
                        <el-form-item :label="t('storeName')">
                            <div>{{storeInfo.site_name}}</div>
                        </el-form-item>
                        <el-form-item :label="t('storeCategory')">
                            <div>{{storeInfo.category_name}}</div>
                        </el-form-item>
                        <el-form-item :label="t('businessType')">
                            <div>{{storeInfo.app_name.join('、')}}</div>
                        </el-form-item>
                        <el-form-item :label="t('storeClass')">
                            <div>{{storeInfo.is_self ? '自营' : '直营'}}</div>
                        </el-form-item>
                        <el-form-item :label="t('storeEnterTime')">
                            <div>{{storeInfo.create_time}}</div>
                        </el-form-item>
                        <el-form-item :label="t('storeExpireTime')" v-if="storeInfo.expire_time">
                            <div>{{storeInfo.expire_time}}</div>
                        </el-form-item>
                    </el-card>
                </el-form>
                <!-- 消息接收人 -->
                <el-form :model="formMessageReceiver" label-width="150px" ref="formMessageReceiverRef" :rules="formMessageReceiverRules"  class="page-form" v-if="activeName == 'messageReceiver'">
                    <el-card class="box-card !border-none relative" shadow="never" >
                        <el-form-item :label="t('messageReceiver')" prop="uid">
							<el-select v-model="formMessageReceiver.uid" filterable reserve-keyword :placeholder="t('messageReceiverPlaceholder')"
								:loading="searchLoading" style="width: 240px">
                                <el-option v-for="item in filteredUserList" :key="item.uid" :label="item.username" :value="item.uid">
                                    <div class="flex items-center">
                                        <el-avatar :src="img(item.head_img)" size="small" class="mr-[10px]" v-if="item.head_img" />
                                        <img src="@/app/assets/images/member_head.png" alt="" class="mr-[10px] w-[24px]" v-else>
                                        {{ item.username }}
                                    </div>
                                </el-option>
							</el-select>
						</el-form-item>
                    </el-card>
                </el-form>
            </div>
        </el-card>
        <div class="fixed-footer-wrap" v-if="activeName == 'basic'|| activeName == 'messageReceiver'">
            <div class="fixed-footer">
                <el-button type="primary" :loading="loading" @click="save(formRef)" v-if="activeName == 'basic'">{{ t('save') }}</el-button>
                <el-button type="primary" :loading="loading" @click="save(formMessageReceiverRef)" v-else>{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed, onMounted, watch } from 'vue'
import { t } from '@/lang'
import { setShopAccount, setShopBaseInfo, getShopAccountInfo, getShopInfo, getShopBaseInfo } from '@/app/api/shop_site'
import { setNoticeRecipient, getNoticeRecipient } from '@/app/api/notice'
import { getAllSiteUserList } from '@/app/api/user'
import { getMap, getAreaListByPid, getAreaByCode } from '@/app/api/sys'
import { createMarker, latLngToAddress, addressToLatLng } from '@/utils/qqmap'
import { FormInstance, FormRules } from 'element-plus'
import { img, getAppType, debounce } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import useUserStore from '@/stores/modules/user'
import useSystemStore from '@/stores/modules/system'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref(true)
const appType = ref(getAppType())
const activeName = ref('basic')
const shopBaseInfo = ref<any>({})
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
// 结算账户表单数据
const formBrank = reactive<any>({
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
// 消息接收人表单数据
const formMessageReceiver = reactive<{ uid : string | number }>({
    uid: '', // 单选，值为用户uid
})
// 店铺信息
const storeInfo = ref({
    site_name: '',
    category_name: '',
    app_name: [],
    is_self: 0,
    create_time: '',
    expire_time: ''
})
// 店铺账户信息
const shopAccount = ref<any>('')
const setFormData = async (id: number = 0) => {
    const data = await (await getShopBaseInfo()).data
    shopBaseInfo.value = data
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })
    formData.latitude = formData.latitude || 39.908626
    formData.longitude = formData.longitude || 116.397190

    shopAccount.value = await (await getShopAccountInfo()).data
    Object.keys(formBrank).forEach((key: string) => {
        if (shopAccount.value[key] != undefined) formBrank[key] = shopAccount.value[key]
    })
    storeInfo.value = await (await getShopInfo()).data
    loading.value = false
}
setFormData()

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = reactive<FormRules>({
    site_name: [
        { required: true, message: t('siteNamePlaceholder'), trigger: 'blur' }
    ],
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
    ],
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
const formBrankRef = ref<FormInstance>()
// 表单验证规则
const formBrankRules = computed(() => {
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
// 切换订单状态
const handleClickTab = (event: any) => {
    activeName.value = event
    if (activeName.value == 'basic') {
        setTimeout(() => {
            initMap()
        }, 500)
    }
}

// 状态管理（分离不同场景的loading，避免冲突）
const initLoading = ref(true) // 初始化用户列表的加载状态
const searchLoading = ref(false) // 搜索时的加载状态

// 表单引用与验证规则
const formMessageReceiverRef = ref<FormInstance>()
const formMessageReceiverRules = reactive<FormRules>({
    uid: [
        { required: true, message: t('messageReceiverPlaceholder'), trigger: 'blur' } // 修正提示信息
    ]
})
// 用户列表数据（直接使用原始列表，无需冗余变量）
const allUserList = ref<{ uid : string | number; username : string }[]>([])

// 过滤后的用户列表（用于搜索展示）
const filteredUserList = computed(() => {
    if (!searchQuery.value) return allUserList.value // 无搜索时展示全部
    // 本地过滤：匹配用户名（不区分大小写）
    return allUserList.value.filter(item =>
        item.username.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
})
// 搜索关键词
const searchQuery = ref('')

// 初始化：获取所有用户列表
const getInitUserList = async () => {
    try {
        initLoading.value = true
        const res = await getAllSiteUserList()
        allUserList.value = res.data || [] // 确保数据格式正确
    } catch (err) {
        console.error('获取用户列表失败：', err)
        allUserList.value = []
    } finally {
        initLoading.value = false
    }
}
const getConfig = () => {
    getNoticeRecipient().then((res) => {
        formMessageReceiver.uid = res.data.uid
    })
}
getConfig()
// 搜索方法（本地过滤，无需远程接口）
const handleSearch = (query : string) => {
    searchQuery.value = query
    // 模拟搜索延迟（可选，增强体验）
    if (query) {
        searchLoading.value = true
        setTimeout(() => {
            searchLoading.value = false
        }, 100)
    }
}
/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const api = activeName.value == 'basic' ? setShopBaseInfo : setNoticeRecipient
    const data: any = activeName.value == 'basic' ? formData : formMessageReceiver
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
            api(data).then(() => {
                loading.value = false
                // console.log(shopBaseInfo.value)
                // console.log(data)
                appType.value == 'admin' ? useSystemStore().getWebsiteInfo() : useUserStore().getSiteInfo()
                if (activeName.value == 'basic' && shopBaseInfo.value.province_id && shopBaseInfo.value.city_id && shopBaseInfo.value.district_id && (data.province_id != shopBaseInfo.value.province_id || data.city_id != shopBaseInfo.value.city_id || data.district_id != shopBaseInfo.value.district_id || data.address != shopBaseInfo.value.address)) {
                    // ElMessageBox.confirm(t('检测到您修改了店铺地址,是否要将地址库中的收发货地址也同步修改'), t('warning'),
                    //     {
                    //         confirmButtonText: t('去修改'),
                    //         cancelButtonText: t('cancel'),
                    //         type: 'warning'
                    //     }
                    // ).then(() => {
                    //     newWindow()
                    // }).catch(() => {
                        setFormData()
                    // })
                }
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

// 新窗口打开
const newWindow = () => {
    const url = router.resolve({
        path: '/site/site_address'
    })
    window.open(url.href)
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
    getInitUserList()
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
        const province = areaList.province.map((item) => { if (item.id == formData.province_id) { return item.name } })
        const city = areaList.city.map((item) => { if (item.id == formData.city_id) { return item.name } })
        const district = areaList.district.map((item) => { if (item.id == formData.district_id) { return item.name } })

        const address = [
            formData.province_id ? (provinceRef.value.selectedLabel || province) : '',
            formData.city_id ? (cityRef.value.selectedLabel || city) : '',
            formData.district_id ? (districtRef.value.selectedLabel || district) : '',
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
