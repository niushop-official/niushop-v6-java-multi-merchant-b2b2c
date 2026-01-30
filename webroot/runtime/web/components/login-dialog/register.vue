<template>
    <div>
        <div class="bg-white w-full py-[60px] px-[30px] !rounded-[var(--rounded-big)]">
            <div class="flex items-end justify-center mb-[30px]">
                <div class="text-[18px] cursor-pointer text-[#999] leading-[24px] oppoSans-R" :class="{ '!text-[#333] font-600': type == item.type,'mr-[70px]': (index+1) != registerType.length }" v-for="(item,index) in registerType" @click="type = item.type">{{item.title }}</div>
            </div>
            <el-form :model="formData" ref="formRef" :rules="formRules" :validate-on-rule-change="false">
                <div v-show="type == 'username'">
                    <el-form-item prop="username">
                        <div class="flex-1 h-[50px] border-[1px] border-solid border-[#ccc] rounded-[8px] flex items-center">
                            <el-input v-model="formData.username" :placeholder="t('usernamePlaceholder')" clearable :inline-message="true">
                                <template #prefix>
                                    <span class="iconfont icon-woV6xx1 !mr-[14px]"></span>
                                </template>
                            </el-input>
                        </div>
                    </el-form-item>
                    <el-form-item prop="password">
                        <div class="flex-1 h-[50px] border-[1px] border-solid border-[#ccc] rounded-[8px] flex items-center">
                            <el-input v-model="formData.password" :placeholder="t('passwordPlaceholder')" type="password" clearable :show-password="true">
                                <template #prefix>
                                    <span class="iconfont icon-mima !mr-[14px]"></span>
                                </template>
                            </el-input>
                        </div>
                    </el-form-item>
                    <el-form-item prop="confirm_password">
                        <div class="flex-1 h-[50px] border-[1px] border-solid border-[#ccc] rounded-[8px] flex items-center">
                            <el-input v-model="formData.confirm_password" :placeholder="t('confirmPasswordPlaceholder')" type="password" clearable :show-password="true">
                                <template #prefix>
                                    <span class="iconfont icon-mima !mr-[14px]"></span>
                                </template>
                            </el-input>
                        </div>
                    </el-form-item>
                </div>
                <div v-show="type == 'mobile' || configStore.login.is_bind_mobile">
                    <el-form-item prop="mobile">
                        <div class="flex-1 h-[50px] border-[1px] border-solid border-[#ccc] rounded-[8px] flex items-center">
                            <el-input v-model="formData.mobile" :placeholder="t('mobilePlaceholder')" clearable>
                                <template #prefix>
                                    <span class="iconfont icon-shoujiV6xx !mr-[14px]"></span>
                                </template>
                            </el-input>
                        </div>
                    </el-form-item>
                    <el-form-item prop="mobile_code">
                        <div class="flex-1 h-[50px] border-[1px] border-solid border-[#ccc] rounded-[8px] flex items-center">
                            <el-input v-model="formData.mobile_code" :placeholder="t('codePlaceholder')">
                                <template #prefix>
                                    <span class="iconfont icon-a-zhibao5 !mr-[14px]"></span>
                                </template>
                                <template #suffix>
                                    <sms-code :mobile="formData.mobile" type="login" v-model="formData.mobile_key" @click="sendSmsCode" ref="smsCodeRef"></sms-code>
                                </template>
                            </el-input>
                        </div>
                    </el-form-item>
                </div>
                <!-- <div v-show="type == 'username'">
                    <el-form-item prop="captcha_code">
                        <div class="flex-1 h-[50px] border-[1px] border-solid border-[#ccc] rounded-[8px] flex items-center">
                            <el-input v-model="formData.captcha_code" :placeholder="t('captchaPlaceholder')">
                                <template #prefix>
                                    <span class="iconfont icon-a-zhibao5 !mr-[14px]"></span>
                                </template>
                                <template #suffix>
                                    <div class="py-0 leading-none">
                                        <el-image :src="captcha.image.value" class="h-[30px] cursor-pointer" @click="captcha.refresh()"></el-image>
                                    </div>
                                </template>
                            </el-input>
                        </div>
                    </el-form-item>
                </div> -->

                <div class="flex justify-end">
                    <el-button type="primary" link @click="typeChange" class="!text-[12px]">{{ t('haveAccount') }}，{{ t('toLogin') }}</el-button>
                </div>

                <div class="mt-[20px]">
                    <el-button type="primary" class="w-full !h-[50px] !rounded-[8px] oppoSans-M" size="large" @click="handleRegister" :loading="loading">{{ loading ? t('registering') : t('register') }}</el-button>
                </div>

                <div class="text-[12px] leading-[24px] flex items-center w-full mt-[20px]" v-if="configStore.login.agreement_show">
                    <span class="iconfont text-primary mr-[5px]" :class="isAgree ? 'icon-xuanze1' : 'icon-checkbox_nol'" @click="isAgree = !isAgree"></span>
                    {{ t('registerAgreeTips') }}
                    <NuxtLink :to="service">
                            <span class="text-primary mx-[4px]">{{ t('userAgreement') }}</span>
                    </NuxtLink>
                        {{ t('and') }}
                    <NuxtLink :to="privacy">
                            <span class="text-primary mx-[4px]">{{ t('privacyAgreement') }}</span>
                     </NuxtLink>
                 </div>
            </el-form>
        </div> 
        <!-- 验证组件 -->
    	<verify @success="success"   :captchaType="configStore.login.captcha_type" :imgSize="{ width: '330px', height: '155px' }" ref="verifyRef"></verify>
    </div>
</template>
<script lang="ts" setup>
import { ref,reactive,computed } from 'vue'
import { usernameRegister, mobileRegister, wechatCheck } from '@/app/api/auth'
import useMemberStore from '@/stores/member'
import useConfigStore from '@/stores/config'
import { FormInstance } from 'element-plus'
import verify from '@/components/verifition/Verify.vue'
import test from '@/utils/test'

const memberStore = useMemberStore()
const configStore = useConfigStore()
configStore.getLoginConfig()

// 跳转
const service = ref('')
const privacy = ref('')
if(location.pathname.indexOf('web') != -1){
    service.value = '/web/auth/agreement?key=service'
    privacy.value = '/web/auth/agreement?key=privacy'
}else{
    service.value = '/auth/agreement?key=service'
    privacy.value = '/auth/agreement?key=privacy'
}

const type = ref('')
const registerType = computed(() => {
    const value = []
    configStore.login.is_username && (value.push({ type: 'username', title: t('usernameRegister') }))
    if(configStore.login.is_mobile || configStore.login.is_bind_mobile) {
        value.push({ type: 'mobile', title: t('mobileRegister') })
    }
    type.value = value[0] ? value[0].type : ''
    return value
})



const loading = ref(false)
const formData = reactive({
    username: '',
    password: '',
    confirm_password: '',
    mobile: '',
    mobile_code: '',
    mobile_key: '',
    captcha_key: '',
    captcha_code: '',
    captcha_type: ''
})

const formRules = computed(() => {
    return {
        username: [
            {
                required: type.value == 'username',
                message: t('usernamePlaceholder'),
                trigger: ['blur', 'change'],
            }
        ],
        password: [
            {
                required: type.value == 'username',
                message: t('passwordPlaceholder'),
                trigger: ['blur', 'change']
            }
        ],
        confirm_password: [
            {
                validator(rule: any, value: string, callback: any) {
                    if(type.value == 'username'){
                        if (value == '') {
                            return callback(new Error(t('confirmPasswordPlaceholder')))
                        } else if (value !== formData.password) {
                            return callback(new Error(t('confirmPasswordError')))
                        } else {
                            return callback()
                        }
                    } else {
                        callback()
                    }
                    
                    return 
                },
                trigger: ['change', 'blur'],
            }
        ],
        mobile: [
            {
                validator(rule: any, value: string, callback: any) {
                    if (type.value == 'mobile' || configStore.login.is_bind_mobile){
                        if (value == '') {
                            return callback(new Error(t('mobilePlaceholder')))
                        }else if (!test.mobile(value)) {
                            return callback(new Error(t('mobileError')))
                        } else {
                            return callback()
                        }
                    } else {
                        return callback()
                    }
                },
                trigger: ['change', 'blur'],
            }
        ],
        mobile_code: [
            {
                required: Boolean(type.value == 'mobile' || configStore.login.is_bind_mobile),
                message: t('codePlaceholder'),
                trigger: ['blur', 'change']
            }
        ]
    }
})

const isAgree = ref(false)
const formRef = ref<FormInstance>()

const handleRegister = async () => {
    await formRef.value?.validate(async (valid, fields) => {
        if (valid) {
            if (!type.value && !configStore.login.is_bind_mobile){
                ElMessage.error(t('loginTypeTips'))
                return false;
            }

            if (configStore.login.agreement_show && !isAgree.value) {
                ElMessage.error(t('isAgreeTips'))
                return false;
            }

            if (loading.value) return
            loading.value = true

            if(type.value == 'username') {
                verifyRef.value?.show()
            } else {
                formData.captcha_type = configStore.login.captcha_type
                mobileRegister(formData).then((res: any) => {
                    memberStore.setToken(res.data.token)
                    memberStore.logClose()
                }).catch(() => {
                    loading.value = false
                    captcha?.refresh()
                })
            }

            
        }
    })
}
let show = ref(false)
const wechatCheckFn = () =>{
    wechatCheck().then((res:any) =>{
        show.value = res.data
    })
}
wechatCheckFn()

// 验证码 - start
const verifyRef = ref<any>(null)
const success = (params: any) => {
    if(type.value == 'username') {
        formData.captcha_code = params.captchaVerification
        formData.captcha_key = params.captcha_key
        formData.captcha_type = configStore.login.captcha_type
        usernameRegister(formData).then((res: any) => {
            memberStore.setToken(res.data.token)
            memberStore.logClose()
        }).catch(() => {
            loading.value = false
            captcha?.refresh()
        })
    } else {
        smsCodeRef.value?.send({captcha_code: params.captchaVerification})
    }
}
// 验证码 - end

// 验证码
const captcha = useCaptcha(formData)
captcha.refresh()

// 获取手机验证码
const smsCodeRef = ref<AnyObject | null>(null)
const sendSmsCode = async () => {
    await formRef.value?.validateField('mobile', async (valid, fields) => {
        if (valid) {
            verifyRef.value?.show()
        }
    })
}
//去登录
const emit = defineEmits(['typeChange'])
const typeChange = ()=>{
    emit('typeChange','login')
}
</script>

<style lang="scss" scoped>
:deep(.el-checkbox.el-checkbox--large){
    height: 0px;
    margin-right: 5px;
}
</style>