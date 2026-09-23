<template>
    <div class="w-full bg-page">
        <div class="main-container" v-loading="loading">
            <el-breadcrumb class="my-[20px] !leading-[18px]" :separator-icon="ArrowRight">
				<el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/')">首页</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="text-[#666]">商户入驻申请</span>
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div class="bg-[#fff] pt-[50px] pb-[20px] px-[170px] relative rounded-[var(--rounded-big)]">
                <div>
                    <div class="absolute top-[30px] right-[20px] text-[14px] text-primary cursor-pointer" @click="router.push({ path: '/member/merchant_record'})">
                        <span class="iconfont icon-jishibenV6xx !text-[14px]"></span>
                        <span>申请记录</span>
                    </div>
                    <div class="text-[24px] text-[#333] mb-[50px] text-center leading-[32px]">入驻申请</div>
                    <el-form :model="formData" label-width="120px" ref="formRef" size="large"  :rules="formRules" class="page-form" >
                        <el-form-item :label="t('siteName')" prop="site_name">
                            <el-input v-model.trim="formData.site_name" clearable  :placeholder="t('siteNamePlaceholder')" maxlength="20" />
                        </el-form-item>
                        <el-form-item :label="t('userName')" prop="user_name">
                            <el-input v-model.trim="formData.user_name" clearable  :placeholder="t('userNamePlaceholder')" maxlength="25"/>
                        </el-form-item>
                        <el-form-item :label="t('mobile')" prop="user_mobile">
                            <el-input v-model.trim="formData.user_mobile" clearable   :placeholder="t('mobilePlaceholder')"/>
                        </el-form-item>
                        <el-form-item :label="t('验证码')" prop="mobile_code">
                            <el-input v-model="formData.mobile_code" :placeholder="t('codePlaceholder')">
								<template #suffix>
									<sms-code :mobile="formData.user_mobile" type="merchant_settled" v-model="formData.mobile_key"
										@click="sendSmsCode" ref="smsCodeRef"></sms-code>
								</template>
							</el-input>
                        </el-form-item>
                        <el-form-item :label="t('shopCategory')" prop="category_id">
                            <el-select class="w-full" v-model="formData.category_id" clearable  :placeholder="t('shopCategoryPlaceholder')">
                                <el-option v-for="(item, index) in shopCategoryList" :key="index" :label="item.category_name" :value="item.category_id"/>
                            </el-select>
                        </el-form-item >
                        <el-form-item :label="t('siteGroup')"  prop="group_id">
                            <el-select class="w-full" v-model="formData.group_id" clearable  :placeholder="t('siteGroupPlaceholder')">
                                <el-option v-for="(item, index) in siteGroupList" :key="index" :label="item.group_name" :value="item.group_id"/>
                            </el-select>
                        </el-form-item >
                        <el-form-item :label="t('businessLicense')" prop="business_license">
                            <div>
                                <div class="text-[12px] text-[#666] leading-[40px]">请上传营业执照及行业相关资质证明图片</div>
                                <div class="text-[12px] text-[#999] mb-[20px] leading-[12px]">{{t('businessLicenseTips')}}</div>
                                <upload-file  v-model="formData.business_license" :limit="5" width="138px" height="138px" ></upload-file>
                            </div>
                        </el-form-item>
                        <el-form-item label="" prop="agree">
                            <div class="text-[14px] flex items-center leading-[40px]">
                                <el-checkbox v-model="formData.agree"/>
                                <span>{{ t('agree') }}</span>
                                <span class="text-[var(--el-color-primary)] cursor-pointer" @click="toLink">{{ t('enterApply') }}</span>
                            </div>
                        </el-form-item>
                        <el-form-item label="">
                            <el-button type="primary" round class="!text-[#fff] oppoSans-M" @click="confirm(formRef)">{{ t('save') }}</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </div>
        <!-- 验证组件 -->
    	<verify @success="success"  :captchaType="configStore.login.captcha_type" :imgSize="{ width: '330px', height: '155px' }" ref="verifyRef"></verify>
    </div>
</template>

<script  setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { getShopCategory, getSiteGroup, setShopApply,getShopApply, editShopApply,checkhaveAccount} from '@/addon/mall/api/merchant'
import { useRouter,useRoute } from 'vue-router'
import { ArrowRight } from '@element-plus/icons-vue'
import { FormInstance } from 'element-plus'
import useConfigStore from '@/stores/config'
import verify from '@/components/verifition/Verify.vue'

const configStore = useConfigStore()

const router = useRouter()
const route = useRoute()
const loading = ref(false)
/**
 * 表单数据
 */
 const initialFormData = {
    apply_id: '',
    site_name: '',
    user_name: '',
    user_mobile: '',
    mobile_code: '',
    mobile_key: '',
    category_id: '',
    group_id:'',
	business_license:'',
    agree: false,
    refuse_reason:'',
	uid:''
}
const formData = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()
// 表单验证规则
const formRules = computed(() => {
    return {
        site_name: [
            { required: true, message: t('siteNamePlaceholder'), trigger: 'blur' }
        ],
        user_name: [
            { required: true, message: t('userNamePlaceholder'), trigger: 'blur' }
        ],
        user_mobile: [
            { required: true, message: t('mobilePlaceholder'), trigger: 'blur' },
            { required: true, validator: validatePhone, trigger: 'blur' }
        ],
        mobile_code: [
            { required: true, message: t('请输入验证码'), trigger: 'blur' }
        ],
        category_id: [
            { required: true, message: t('shopCategoryPlaceholder'), trigger: 'blur' }
        ],
        group_id: [
            { required: true, message: t('siteGroupPlaceholder'), trigger: 'blur' }
        ],
        business_license: [
        { required: true, message: t('businessLicensePlaceholder'), trigger: 'blur' }
        ]
    }
})
// 验证手机号格式
const validatePhone = (rule, value, callback) => {
    const phonePattern = /^1[3456789]\d{9}$/
    if (!value) {
        return callback(new Error(t('请输入手机号')))
    } else if (!phonePattern.test(value)) {
        return callback(new Error(t('请输入正确的手机号')))
    } else {
        return callback()
    }
}

// 店铺分类
const shopCategoryList = ref([])
const getShopCategoryFn = () =>{
    getShopCategory().then(res =>{
        shopCategoryList.value = res.data
    })
}
getShopCategoryFn()
// 店铺套餐
const siteGroupList = ref([])
const getSiteGroupFn = () =>{
    getSiteGroup().then(res =>{
        siteGroupList.value = res.data
    })
}
getSiteGroupFn()

// 店铺申请详情
const getShopApplyFn = (id) =>{
    loading.value = true 
    getShopApply(id).then(res=>{
        const data = res.data
        if(JSON.stringify(data) != '[]'){
            Object.keys(formData).forEach(key =>{
                if(data[key] != undefined)  formData[key] = data[key]
            })
        }
        loading.value = false 
    }).catch(() =>{
        loading.value = false 
    })
}
if(route.query.apply_id){
    getShopApplyFn(route.query.apply_id)
}
// 提交申请
const confirm = async (formEl: any) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            if(!formData.agree){
                ElMessage.error('请勾选并同意入驻协议')
                return
            }
            loading.value = true
			submitNest()
        }
    })
}

const submitNest = () =>{
	const api =  formData.apply_id ? editShopApply : setShopApply
	api(formData).then(res => {
	     loading.value = false
	     for(let key in formData){
	        if(formData[key] != undefined && key == 'agree') formData[key] = false
	        formData[key] = ''
	     }
	     router.push({ path: '/member/merchant_record' })
	 }).catch(() => {
	     loading.value = false
	     router.push({ path: '/member/merchant_record' })
	 })
}

const toLink = () =>{
    const url = router.resolve({
        path: '/auth/agreement',
        query: { key: 'shop_apply' }
    })
    window.open(url.href) 
}


// 验证码 - start
const verifyRef = ref<any>(null)
const success = (params: any) => {
    smsCodeRef.value?.send({captcha_code: params.captchaVerification})
}
// 验证码 - end

const smsCodeRef = ref<AnyObject | null>(null)
const sendSmsCode = async () => {
	await formRef.value?.validateField('user_mobile', async (valid, fields) => {
		if (valid) {
			verifyRef.value?.show()
		}
	})
}
</script>

<style lang="scss" scoped>
:deep(.el-checkbox){
    margin-right: 7px;
}
</style>