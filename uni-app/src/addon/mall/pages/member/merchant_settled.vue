<template>
    <view class="bg-[#fff]  min-h-[100vh] overflow-hidden" :style="themeColor()" v-if="!repeatLoading">
        <view class="relative" v-if="diyData" :style="{height: headStyle, backgroundImage: 'url(' + img(diyData.apply_banner) + ')',backgroundSize: 'cover', backgroundPosition: 'bottom',  backgroundRepeat: 'no-repeat'}">
            <!-- #ifdef MP-WEIXIN -->
            <top-tabbar v-if="systemStore.systemInfo && topTabarObj" :data="param"  :scrollBool="topTabarObj?.getScrollBool()" />
            <!-- #endif -->
            <view class="side-tab" :style="{top: topStyle}" @click="redirect({url:'/addon/mall/pages/member/merchant_record'})">
                <text class="nc-iconfont nc-icon-shenheV6xx icon"></text>
                <text class="desc">申请记录</text>
            </view>
        </view> 
        <view class="mt-[-150rpx] relative  card-template !px-[40rpx] merchant-settled">
            <view class="mb-[30rpx] text-[34rpx] leading-[48rpx] font-500 text-[#333]">{{ applyTitle }}</view>
            <u-form labelPosition="left" :model="formData" :label-style="{'font-size':'28rpx'}"  class="pl-[15rpx]"
                labelWidth="150rpx" errorType='toast' :rules="rules" ref="formRef">
                <view>
                    <u-form-item :label="t('siteName')" prop="site_name" required  >
                        <u-input fontSize="28rpx" v-model.trim="formData.site_name" border="none" clearable
                            maxlength="20" :placeholder="t('siteNamePlaceholder')"
                            placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                    </u-form-item>
                </view>
                <view>
                    <u-form-item :label="t('userName')" prop="user_name" required  >
                        <u-input fontSize="28rpx" v-model.trim="formData.user_name" border="none" clearable
                            maxlength="20" :placeholder="t('userNamePlaceholder')"
                            placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                    </u-form-item>
                </view>
                <view>
                    <u-form-item :label="t('userMobile')" prop="user_mobile" required  >
                        <u-input fontSize="28rpx" v-model.trim="formData.user_mobile" border="none" clearable
                            :placeholder="t('useMobilePlaceholder')"
                            placeholderClass="!text-[var(--text-color-light9)] text-[28rpx]" />
                    </u-form-item>
                </view>
                <view>
                    <u-form-item :label="t('mobileCode')" prop="mobile_code" required>
                        <u-input  fontSize="28rpx" v-model="formData.mobile_code" type="number" maxlength="4" border="none" :placeholder="t('mobileCodePlaceholder')"  placeholderClass="!text-[var(--text-color-light9)] !text-[28rpx]">
                            <template #suffix>
                                <sms-code :mobile="formData.user_mobile" type="merchant_settled" v-model="formData.mobile_key"></sms-code>
                            </template>
                        </u-input>
                    </u-form-item>
                </view>
                <view>
                    <u-form-item :label="t('categoryName')" prop="category_id" required  >
                        <view class="flex items-center flex-1" @click="showPicker  = true">
                            <view class="flex-1">
                                <view class="" v-if="formData.category_id">{{formData.category_name}}</view>
                                <view class="text-[var(--text-color-light9)]" v-else>
                                    {{ t('categoryNamePlaceholder') }}</view>
                            </view>
                            <text
                                class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                    </u-form-item>
                </view>
                <view v-if="formData.child_category_id">
                    <u-form-item :label="t('childCategoryName')" prop="child_category_id" required  >
                        <view class="flex items-center flex-1" @click="showChildPicker  = true">
                            <view class="flex-1">
                                <view class="" v-if="formData.child_category_id">{{formData.child_category_name}}</view>
                                <view class="text-[var(--text-color-light9)]" v-else>
                                    {{ t('categoryNamePlaceholder') }}</view>
                            </view>
                            <text
                                class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                        </view>
                    </u-form-item>
                </view>
                <view>
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
            <view>
                <diy-form v-if="diyData" ref="diyFormRef" :form_id="diyData.form_id"  :form_sign="'memberId_'+userInfo?.member_id+'_formId_'+diyData.form_id" :storage_name="'diyFormStorageByMerchantSettled'"/>
            </view>
            <view class="text-[22rpx]  text-[#666] mt-[30rpx] mb-[20rpx] flex items-center w-full"
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
        <u-loading-page bg-color="rgb(248,248,248)" :loading="loading" loadingText=""></u-loading-page>
        <u-action-sheet :actions="siteCategoryList" :show="showPicker" :closeOnClickOverlay="true" :safeAreaInsetBottom="true" @close="showPicker = false" @select="changeCategory"></u-action-sheet>
        <u-action-sheet :actions="siteChildCategoryList" :show="showChildPicker" :closeOnClickOverlay="true" :safeAreaInsetBottom="true" @close="showChildPicker = false" @select="changeChildCategory"></u-action-sheet>
        

        <fab-float />
    </view>
</template>

<script lang="ts" setup>
import { ref, computed, reactive, nextTick } from 'vue'
import { img, redirect, pxToRpx, deepClone } from '@/utils/common'
import { t } from '@/locale'
import { uploadImage } from '@/app/api/system'
import { onLoad, onPageScroll } from '@dcloudio/uni-app'
import { topTabar } from '@/utils/topTabbar'
import { getSiteCategoryAll } from '@/app/api/site'
import { getShopConfig, setShopApply, editShopApply, getShopApply, checkhaveAccount } from '@/addon/mall/api/merchant'
import { getFormRecord } from '@/app/api/diy_form'
import useSystemStore from '@/stores/system'
import useMemberStore from '@/stores/member'
import FabFloat from '@/components/fab-float/fab-float.vue'
import diyForm from '@/addon/components/diy-form/index.vue'


const systemStore = useSystemStore()
// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

/********* 自定义头部 - start ***********/
const param = ref<any>({})
let topTabarObj = null
/********* 自定义头部 - end ***********/

const applyTitle = ref('商家入驻')

// 头部图片的高度
const headStyle = computed(() => {
    let style = Object.keys(systemStore.menuButtonInfo).length ? (pxToRpx(Number(systemStore.menuButtonInfo.height)) + pxToRpx(systemStore.menuButtonInfo.top) + pxToRpx(8) + 452) + 'rpx' : '452rpx'
    return style
})
const topStyle = computed(() => {
    let style = Object.keys(systemStore.menuButtonInfo).length ? pxToRpx(Number(systemStore.menuButtonInfo.height) + systemStore.menuButtonInfo.top + 8) + 50 + 'rpx' : '50rpx'
    return style
})

// 审核
const showPicker = ref(false)
const showChildPicker = ref(false)
const formData = reactive<any>({
    apply_id: '',
    site_name: '',
    user_name: '',
    user_mobile: '',
    mobile_code: '',
    mobile_key: '',
    category_id: '',
    category_name: '',
    child_category_id: '',
    child_category_name: '',
    business_license: [],
    agree: false,
    refuse_reason: '',
    uid:0,
    form_data: {},
})
const diyData = ref<any>(null)
const diyFormRef = ref<any>(null)

onLoad((option: any) => {
    // console.log('auditFlag:', systemStore.auditFlag)
    // console.log('repeatLoading:', repeatLoading.value)
    // console.log('systemInfo:', systemStore.systemInfo)
    formData.apply_id = option.apply_id || ''
    try {
        topTabarObj = topTabar()
        param.value = topTabarObj.setTopTabbarParam({ title: applyTitle.value })
    } catch (e) {
        console.error('topTabbar init error:', e)
    }
    getShopConfigFn()
    checkStatus()
})

const curChildCategoryId = ref('')
const siteChildCategoryList = ref([])
const changeCategory = (e) =>{
    formData.category_id = e.category_id
    formData.category_name = e.name
    
    // 重置子分类
    formData.child_category_id = ''
    formData.child_category_name = ''
    
    // 检查是否有子分类
    if (e.child_list && Array.isArray(e.child_list) && e.child_list.length > 0) {
        // 子分类列表
        siteChildCategoryList.value = e.child_list
        // 自动选中第一个子分类
        formData.child_category_id = e.child_list[0].category_id
        formData.child_category_name = e.child_list[0].name
    }
    
    // 关闭选择器
    showPicker.value = false
}
const changeChildCategory = (e) =>{
    formData.child_category_id = e.category_id
    formData.child_category_name = e.name
    showChildPicker.value = false
}
// 详情
const repeatLoading = ref(true)
const getShopApplyFn = (id: any) => {
    repeatLoading.value = true
    getShopApply(id).then(({ data }) => {
        Object.keys(formData).forEach((key) => {
            if (data[key] != undefined) formData[key] = data[key]
            if (data[key] != undefined && key == 'business_license') formData[key] = data[key]
                .split(',')
        })
        formData.form_data.record_id = data.form_record_id
        curCategoryId.value = formData.category_id
        curChildCategoryId.value = formData.child_category_id
        if(data.form_record_id){
            getFormRecordFn(data)
        }
        repeatLoading.value = false
    }).catch(() => {
        repeatLoading.value = false
    })
}

const getFormRecordFn = (data: any) => {
    getFormRecord({
        record_id: data.form_record_id
    }).then((res: any) => {
        nextTick(() => {
            diyFormRef.value.setFormData(res.data)
        })
    }).catch(() => {
    })
}

const getShopConfigFn = () => {
    getShopConfig().then((res: any) => {
        diyData.value = res.data
        if(res.data.apply_title) {
            applyTitle.value = res.data.apply_title
            uni.setNavigationBarTitle({
                title: applyTitle.value
            })
            if (topTabarObj) param.value = topTabarObj.setTopTabbarParam({ title: applyTitle.value })
        }
    })
}
const checkStatus = () => {
    if (formData.apply_id) {
        getShopApplyFn(formData.apply_id)
    } else {
        repeatLoading.value = false
    }
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
const siteCategoryList = ref([])
const getShopCategoryFn = () => {
    getSiteCategoryAll({
        addon: 'mall'
    }).then(res => {
        siteCategoryList.value = res.data
    })
}
getShopCategoryFn()

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
        'business_license': {
            type: 'array',
            min: 1,
            required: true,
            message: t('businessLicensePlaceholder'),
            trigger: ['blur', 'change'],
        },
        'mobile_code': {
            type: 'number',
            required: true,
            message: t('mobileCodePlaceholder'),
            trigger: ['blur', 'change'],
        }
    }
})	

const isAgree = ref(false)
const formRef = ref(null)
const loading = ref(false)
const save = () => {
    formRef.value.validate().then(() => {
        if (diyFormRef.value && !diyFormRef.value.verify()) return;
        if (!isAgree.value) {
            uni.showToast({
                title: t('isAgreeTips'),
                icon: 'none'
            });
            return false;
        }
        if (loading.value) return
        loading.value = true
        if (diyFormRef.value) {
            Object.assign(formData.form_data, deepClone(diyFormRef.value.getData()))
        }
        console.log(formData);
        const api = formData.apply_id ? editShopApply : setShopApply
        formData.business_license = formData.business_license.join(',')
        api(formData).then(res => {
            if (diyFormRef.value) diyFormRef.value.clearStorage();
            formData.form_data = {};
            setTimeout(() => {
                redirect({
                    url: '/addon/mall/pages/member/merchant_record'
                })
            }, 100)
            loading.value = false
        }).catch(() => {
            loading.value = false
        })
    })
}
</script>

<style lang="scss" scoped>
:deep(.merchant-settled  .u-form-item__body__left__content__required){
    left: -12px !important;
}
:deep(.business_license .u-form-item__body) {
    display: flex;
    flex-direction: column !important;
}

/*  #ifndef  H5  */
:deep(.merchant-settled .u-form .u-form-item) {
    &:last-child {
        .u-form-item__body {
            flex-direction: column !important;
            padding-bottom: 10rpx !important;
        }
        .u-form-item__body__right{
            margin-top: 20rpx !important;
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
:deep(.u-action-sheet){
    max-height: 600rpx !important;
    overflow-y: auto!important;
}
</style>
