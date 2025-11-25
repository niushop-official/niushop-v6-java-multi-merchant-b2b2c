<template>
    <view class="bg-[var(--page-bg-color)]  min-h-[100vh] overflow-hidden" :style="themeColor()" v-if="systemStore.auditFlag && !repeatLoading">
        <!-- #ifdef H5 -->
        <view class="bg-img min-h-[100vh] overflow-hidden relative"  :style="{background: 'url(' + img('addon/mall/member/enter.png') + ') left top /cover no-repeat'}">
        <!-- #endif -->
        <!-- #ifdef MP-WEIXIN -->
        <view class="bg-img min-h-[100vh] overflow-hidden relative" :style="{background: 'url(' + img('addon/mall/member/enter_uniapp.png') + ')  left top /cover no-repeat'}">
            <top-tabbar :data="param" class="top-header" />
            <!-- #endif -->
            <view class="side-tab" :style="{top: topStyle}"
                @click="redirect({url:'/addon/mall/pages/member/merchant_record'})">
                <text class="nc-iconfont nc-icon-shenheV6xx icon"></text>
                <text class="desc">申请记录</text>
            </view>
            <view class="mt-[317rpx] mx-[var(--sidebar-m)]  card-template mb-[40rpx] merchant-settled">
                <u-form labelPosition="left" :model="formData" :label-style="{'font-size':'28rpx'}"  class="pl-[15rpx]"
                    labelWidth="150rpx" errorType='toast' :rules="rules" ref="formRef">
                    <view>
                        <u-form-item :label="t('siteName')" prop="site_name" required  >
                            <u-input fontSize="28rpx" v-model.trim="formData.site_name" border="none" clearable
                                maxlength="20" :placeholder="t('siteNamePlaceholder')"
                                placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('userName')" prop="user_name" required  >
                            <u-input fontSize="28rpx" v-model.trim="formData.user_name" border="none" clearable
                                maxlength="20" :placeholder="t('userNamePlaceholder')"
                                placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('userMobile')" prop="user_mobile" required  >
                            <u-input fontSize="28rpx" v-model="formData.user_mobile" border="none" clearable
                                :placeholder="t('useMobilePlaceholder')"
                                placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('categoryId')" prop="category_id" required  >
                            <view class="flex items-center flex-1" @click="showPicker  = true">
                                <view class="flex-1">
                                    <view class="" v-if="formData.category_id">{{formData.category_name}}</view>
                                    <view class="text-[var(--text-color-light9)]" v-else>
                                        {{ t('categoryIdPlaceholder') }}</view>
                                </view>
                                <text
                                    class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('groupId')" prop="group_id" required  >
                            <view class="flex items-center flex-1" @click="siteGroupPopup = true">
                                <view class="flex-1">
                                    <view class="" v-if="formData.group_id">{{formData.group_name}}</view>
                                    <view class="text-[var(--text-color-light9)]" v-else>
                                        {{ t('groupIdPlaceholder') }}</view>
                                </view>
                                <text
                                    class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('account')" prop="account" required  >
                            <u-input fontSize="28rpx" v-model.trim="formData.account" border="none" clearable
                                maxlength="20" :placeholder="t('accountPlaceholedr')"
                                placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('password')" prop="password" :border-bottom="false" required>
                            <u-input v-model="formData.password" border="none" :password="isPassword" maxlength="40" :placeholder="t('passwordPlaceholedr')" autocomplete="new-password" class="!bg-transparent" :disabled="real_name_input" fontSize="26rpx" placeholderClass="!text-[var(--text-color-light9)] text-[26rpx]">
                                <template #suffix>
                                    <view @click="changePassword" v-if="formData.password">
                                        <u-icon :name="isPassword?'eye-off':'eye-fill'" color="#b9b9b9" size="20"></u-icon>
                                    </view>
                                </template>
                            </u-input>
                        </u-form-item>
                    </view>
                    <view class="mt-[16rpx]">
                        <u-form-item :label="t('businessLicense')" labelWidth="640rpx" prop="business_license" required  
                            class="business_license !flex-col">
                            <view>
                                <view class="text-[22rpx] text-[#999] leading-[30rpx]">
                                    {{ t('businessLicenseTips') }}</view>
                                <view class="mt-[30rpx]">
                                    <u-upload :fileList="businessLicenseList" @afterRead="afterRead"
                                        @delete="deletePic" multiple :maxCount="5"></u-upload>
                                </view>
                            </view>
                        </u-form-item>
                    </view>
                </u-form>
                <view class="text-[22rpx]  text-[#666] mt-[40rpx] mb-[20rpx] flex items-center w-full"
                    @click="isAgree = !isAgree">
                    <text class="text-[var(--primary-color)] text-[32rpx] mr-[12rpx]"
                        :class="isAgree ? 'iconfont iconxuanze1' : 'nc-iconfont nc-icon-yuanquanV6xx'"></text>
                    {{ t('agreeTips') }}
                    <view @click.stop="redirect({ url: '/app/pages/auth/agreement?key=shop_apply' })">
                        <text class="text-primary">《{{ t('enterAgreement') }}》</text>
                    </view>
                </view>
                <view>
                    <button hover-class="none"
                        class="primary-btn-bg text-[#fff] h-[80rpx] leading-[80rpx] rounded-[100rpx] text-[26rpx] font-500"
                        @click="save">{{ t('save') }}</button>
                </view>
            </view>
        </view>

        <u-loading-page bg-color="rgb(248,248,248)" :loading="loading" loadingText=""></u-loading-page>
        <u-action-sheet :actions="shopCategoryList" :show="showPicker" :closeOnClickOverlay="true" :safeAreaInsetBottom="true" @close="showPicker = false" @select="changeCategory"></u-action-sheet>
        <!-- 店铺套餐 -->
        <u-popup :show="siteGroupPopup" @close="siteGroupPopup = false" :closeable="true">
            <view class="px-[30rpx] pb-[30rpx]" @touchmove.prevent.stop>
                <view class="text-center p-[30rpx] text-[30rpx]">{{ t('groupId') }}</view>
                <scroll-view scroll-y="true" class="h-[450rpx] mt-[20rpx]">
                    <u-radio-group v-model="curSiteGroupId" placement="column">
                        <u-radio activeColor="var(--primary-color)" :labelSize="'26rpx'"
                            :customStyle="{marginBottom: '30rpx'}" v-for="(item, index) in siteGroupList"
                            :key="index" :label="item.group_name" :name="item.group_id"></u-radio>
                    </u-radio-group>
                </scroll-view>
                <button
                    class="mt-[40rpx] primary-btn-bg text-[#fff] h-[80rpx] leading-[80rpx] rounded-[100rpx] text-[26rpx] font-500"
                    @click="siteGroupPopupFn">{{ t('save') }}</button>
            </view>
        </u-popup>
            
            
        <u-popup :show="registerPopup" @close="registerPopup = false" round="10" mode="center" :closeOnClickOverlay="false">
            <view class="w-[80vw] px-[30rpx] py-[50rpx]" v-if="accountInfo && accountInfo.uid">
                <view class="flex border-1 border-[#efefef] border-solid p-[20rpx] rounded-[20rpx]">
                    <view class="mr-[30rpx]" >
                        <u-avatar :src="img(accountInfo.head_img)" size="55" leftIcon="none" :default-url="img('static/resource/images/default_headimg.png')"  />
                    </view>
                    <view class="flex flex-col py-[2rpx]">
                        <view class="text-[32rpx]">
                            {{accountInfo.username}}
                        </view>
                        <view class="text-[#999999] flex-1 flex justify-end items-end" v-if="accountInfo.mobile">
                            {{accountInfo.mobile}}
                        </view>
                        <view v-else class="text-[#999999] flex-1 flex justify-end items-end text-[24rpx] pb-[10rpx]">
                            暂未绑定手机号
                        </view>
                    </view>
                </view>
                <view class="leading-[1.4] my-[30rpx]">
                    该账号已存在，是否继续使用该账号并将该账号作为店铺的登录账号
                </view>
                <view class="text-[#999] leading-[1.2] text-[24rpx]">
                    注：如继续使用该账号，登录密码则为该账号的原密码！
                </view>
                <view class="flex">
                    <button
                        class="mt-[40rpx] w-full border-1 border-solid border-[#efefef] bg-[#fff] h-[80rpx] mr-[15rpx] leading-[80rpx] rounded-[100rpx] text-[26rpx] font-500"
                        @click="cancelAccount">取消</button>
                    <button
                        class="mt-[40rpx] w-full ml-[15rpx] primary-btn-bg text-[#fff] h-[80rpx] leading-[80rpx] rounded-[100rpx] text-[26rpx] font-500"
                        @click="registerPopupFn">确定</button>
                </view>
            </view>
        </u-popup>
        </view>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { img, redirect, pxToRpx } from '@/utils/common'
import { t } from '@/locale'
import { uploadImage } from '@/app/api/system'
import { onLoad } from '@dcloudio/uni-app'
import { topTabar } from '@/utils/topTabbar'
import { getShopCategory, getSiteGroup, setShopApply, editShopApply, getShopApply, checkShopStatus, checkhaveAccount } from '@/addon/mall/api/merchant'
import useSystemStore from '@/stores/system'
const isPassword = ref(true)
const registerPopup = ref(false)
const changePassword = () => {
    isPassword.value = !isPassword.value
}
const systemStore = useSystemStore()

/********* 自定义头部 - start ***********/
const topTabarObj = topTabar()
let param = topTabarObj.setTopTabbarParam({ title: '商家入驻' })
/********* 自定义头部 - end ***********/
const topStyle = computed(() => {
    let style = Object.keys(systemStore.menuButtonInfo).length ? pxToRpx(Number(systemStore.menuButtonInfo.height) + systemStore.menuButtonInfo.top + 8) + 50 + 'rpx' : '50rpx'
    return style
})
const registerPopupFn = () =>{
    loading.value = true
    const api = formData.apply_id ? editShopApply : setShopApply
    formData.business_license = formData.business_license.join(',')
    formData.uid =accountInfo.value.uid
    formData.password = ''
    api(formData).then(res => {
        registerPopup.value = false
        setTimeout(() => {
            redirect({
                url: '/addon/mall/pages/member/merchant_record'
            })
        }, 100)
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
const cancelAccount = () =>{
    formData.account = ''
    formData.password = ''
    formData.uid  = 0
    loading.value = false
    registerPopup.value = false
}
// 审核

const showPicker = ref(false)
const formData = reactive({
    apply_id: '',
    site_name: '',
    user_name: '',
    user_mobile: '',
    category_id: '',
    category_name: '',
    group_id: '',
    group_name: '',
    business_license: [],
    agree: false,
    refuse_reason: '',
    account:'',
    password:'',
    uid:0
})
onLoad((option) => {
    formData.apply_id = option.apply_id || ''
    checkStatus()
})
const changeCategory = (e) =>{
    formData.category_id = e.category_id
    formData.category_name = e.category_name
}
// 详情
const repeatLoading = ref(true)
const getShopApplyFn = (id) => {
    repeatLoading.value = true
    getShopApply(id).then(({ data }) => {
        Object.keys(formData).forEach((key) => {
            if (data[key] != undefined) formData[key] = data[key]
            if (data[key] != undefined && key == 'business_license') formData[key] = data[key]
                .split(',')
        })
        curCategoryId.value = formData.category_id
        curSiteGroupId.value = formData.group_id
        repeatLoading.value = false
    }).catch(() => {
        repeatLoading.value = false
    })
}

const checkStatus = () => {
    checkShopStatus().then((res) => {
        if (res.data) {
            redirect({
                url: '/addon/mall/pages/member/merchant_record',
                mode: 'redirectTo'
            })
        } else{
            if (formData.apply_id) {
                getShopApplyFn(formData.apply_id)
            } else {
                repeatLoading.value = false
            }
        }
    })
}

// 上传照片
const businessLicenseList = computed(() => {
    if (typeof formData.business_license == 'string') {
        formData.business_license = formData.business_license.split(',')
    }
    return formData.business_license.map(item => {
        return {
            url: img(item)
        }
    })
})

const afterRead = (event) => {
    event.file.forEach(item => {
        uploadImage({
            filePath: item.url,
            name: 'file'
        }).then(res => {
            if (formData.business_license.length < 5) {
                formData.business_license.push(res.data.url)
            }
        }).catch(() => {})
    })
}

const deletePic = (event) => {
    formData.business_license.splice(event.index, 1)
}

// 店铺分类
const curCategoryId = ref('')
const shopCategoryList = ref([])
const getShopCategoryFn = () => {
    getShopCategory().then(res => {
        shopCategoryList.value = res.data
        shopCategoryList.value.forEach((item,index)=>{
            item.name = item.category_name
        })
    })
}
getShopCategoryFn()
const categoryPopupFn = () => {
    formData.category_id = curCategoryId.value;
    let data = shopCategoryList.value.find(item => item.category_id === curCategoryId.value)
    formData.category_name = data.category_name
    categoryPopup.value = false;
}
// 店铺套餐
const siteGroupPopup = ref(false)
const curSiteGroupId = ref('')
const siteGroupList = ref([])
const getSiteGroupFn = () => {
    getSiteGroup().then(res => {
        siteGroupList.value = res.data
    })
}
getSiteGroupFn()
const siteGroupPopupFn = () => {
    formData.group_id = curSiteGroupId.value;
    let data = siteGroupList.value.find(item => item.group_id === curSiteGroupId.value)
    formData.group_name = data.group_name
    siteGroupPopup.value = false;
}
// 规则
const rules = computed(() => {
    return {
        'site_name': {
            type: 'string',
            required: true,
            message: t('siteNamePlaceholder'),
            trigger: ['blur', 'change']
        },
        'user_name': {
            type: 'string',
            required: true,
            message: t('userNamePlaceholder'),
            trigger: ['blur', 'change']
        },
        'user_mobile': [{
                type: 'string',
                required: true,
                message: t('useMobilePlaceholder'),
                trigger: ['blur', 'change']
            },
            {
                validator(rule, value, callback) {
                    let mobile = /^1[3-9]\d{9}$/;
                    if (!mobile.test(value)) {
                        callback(new Error(t('mobileError')))
                    } else {
                        callback()
                    }
                }
            }
        ],
        'category_id': {
            type: 'number',
            required: true,
            message: t('categoryIdPlaceholder'),
            trigger: ['blur', 'change'],
        },
        'group_id': {
            type: 'number',
            required: true,
            message: t('groupIdPlaceholder'),
            trigger: ['blur', 'change'],
        },
        'business_license': {
            type: 'array',
            min: 1,
            required: true,
            message: t('businessLicensePlaceholder'),
            trigger: ['blur', 'change'],
        },
        'account': {
            type: 'string',
            required: true,
            message: t('accountPlaceholedr'),
            trigger: ['blur', 'change']
        },
        'password': {
            type: 'string',
            required: true,
            message: t('passwordPlaceholedr'),
            trigger: ['blur', 'change']
        },
    }
})	

const isAgree = ref(false)
const formRef = ref(null)
const loading = ref(false)
const accountInfo = ref({})
const save = () => {
    formRef.value.validate().then(() => {
        if (!isAgree.value) {
            uni.showToast({
                title: t('isAgreeTips'),
                icon: 'none'
            });
            return false;
        }
        if (loading.value) return
        checkhaveAccount({account:formData.account}).then((requestData)=>{
            if(requestData.data.uid){
                registerPopup.value = true
                accountInfo.value = requestData.data
            }else{
                loading.value = true
                const api = formData.apply_id ? editShopApply : setShopApply
                formData.business_license = formData.business_license.join(',')
                api(formData).then(res => {
                    setTimeout(() => {
                        redirect({
                            url: '/addon/mall/pages/member/merchant_record'
                        })
                    }, 100)
                    loading.value = false
                }).catch(() => {
                    loading.value = false
                })
            }
        })
        
        
    })
}
</script>

<style lang="scss" scoped>
    :deep(.business_license .u-form-item__body) {
        display: flex;
        flex-direction: column !important;
    }

    /*  #ifndef  H5  */
    :deep(.merchant-settled .u-form .u-form-item) {
        &:last-child {
            .u-form-item__body {
                flex-direction: column !important;
            }
        }
    }

    /*  #endif  */
    :deep(.u-upload__button) {
        width: 120rpx !important;
        height: 120rpx !important;
        border: 2rpx dashed #ddd;
        border-radius: 20rpx !important;
        background-color: transparent !important;
    }

    :deep(.u-upload__wrap__preview__image) {
        width: 120rpx !important;
        height: 120rpx !important;
    }
</style>