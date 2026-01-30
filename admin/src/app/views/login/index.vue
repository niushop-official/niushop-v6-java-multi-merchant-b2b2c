<template>
    <el-container :class="['w-full h-screen bg-page flex flex-col', { 'login-wrap': loginType == 'admin' }]">
        <!-- 平台端登录 -->
        <el-main class="login-main items-center justify-center flex-1 h-0" v-if="loginType == 'admin'">
            <div class="flex rounded-2xl shadow overflow-hidden">
                <div class="login-main-left w-[450px] flex flex-wrap justify-center">
                    <template v-if="loginConfig">
                        <el-image v-if="loginConfig.bg && !imgLoading" class="w-[450px] h-[400px]" :src="img(loginConfig.bg)" fit="cover" />
                        <img v-else src="@/app/assets/images/login/login_index_left.png" alt="">
                    </template>
                </div>
                <div class="login flex flex-col w-[400px] h-[400px] p-[40px]">
                    <h3 class="text-center text-lg font-bold mb-[10px]">{{ webSite.site_name || t('siteTitle') }}</h3>
                    <h3 class="text-center text-2xl font-bold mb-[26px]">{{ t('platform') }}</h3>
                    <el-form :model="form" ref="formRef" :rules="formRules" class="mt-[30px]">
                        <el-form-item prop="username">
                            <el-input v-model.trim="form.username" :placeholder="t('userPlaceholder')" autocomplete="off" @keyup.enter="handleLogin(formRef)" class="h-[40px]"></el-input>
                        </el-form-item>

                        <el-form-item prop="password" class="mt-[30px]">
                            <el-input v-model.trim="form.password" :placeholder="t('passwordPlaceholder')" type="password" autocomplete="new-password" @keyup.enter="handleLogin(formRef)" :show-password="true" class="h-[40px]"></el-input>
                        </el-form-item>

                        <el-form-item>
                            <el-button type="primary" class="mt-[30px] !h-[40px] w-full" @click="handleLogin(formRef)" :loading="loading">{{ loading ? t('logging') : t('login') }}</el-button>
                        </el-form-item>
                    </el-form>
                </div>
            </div>
        </el-main>

        <!-- 站点端登录 -->
        <el-main class="login-main  bg-[#fff] w-full login-site-main items-center h-screen justify-evenly !p-0" v-else-if="!imgLoading && loginType == 'site'">
            <div class="flex overflow-hidden h-screen w-full relative">
                <div class="w-[130px] h-[40px] overflow-hidden absolute top-[20px] left-[20px]" v-if="webSite.icon">
                    <el-image class="w-full h-full" :src="img(webSite.icon)" fit="contain">
                        <template #error>
                            <div class="flex justify-center items-center w-full h-full">
                                <img class="max-w-[130px]" src="@/app/assets/images/logo.png" alt="" object-fit="contain" />
                            </div>
                        </template>
                    </el-image>
                </div>
                <img v-if="loginConfig.site_bg" class="hidden h-[100%] lg:block" :src="img(loginConfig.site_bg)" />
                <img v-else class="hidden h-[100%] lg:block" src="@/app/assets/images/site_login_bg.png" />
                <div class="w-[100%] flex flex-col absolute right-0 top-0 h-screen lg:w-[60%]">
                    <div class="flex justify-center items-center flex-1 h-0">
                        <div class="site-login-item w-[45%]">
                            <div class="w-[90%] mx-auto">
                                <div class="flex items-center mb-[30px]">
                                    <span class="text-[30px] font-500 mr-[10px]">欢迎登录</span>
                                    <span class="text-[20px] text-[#666] font-500">{{ webSite.site_name || t('siteTitle') }}后台管理系统</span>
                                </div>
                                <div class="w-full min-h-[460px]">
                                    <div class="relative overflow-hidden max-w-[460px] min-h-[460px] bg-[#fff] login-box-wrap">
                                        <div class="p-[30px] top-0 left-0 w-full h-full transition-transform duration-300 ease-linear transform" :class="{'translate-x-0': switchLogin === 'login','-translate-x-full': switchLogin !== 'login'}">
                                            <div class="flex items-center mb-[60px]">
                                                <div class="text-[18px] cursor-pointer font-bold mr-[40px]" :class="{'select-wrap': type == 'username'}" @click="handleType('username')">密码登录</div>
                                                <div class="text-[18px] cursor-pointer font-bold" :class="{'select-wrap': type == 'mobile'}" @click="handleType('mobile')">手机登录</div>
                                            </div>
                                            <el-form :model="form" ref="formRef" :rules="formRules" class="form-two">
                                                <template v-if="type == 'username'">
                                                    <el-form-item prop="username" class="h-[60px] bg-[#F9FAFB] rounded-[6px]">
                                                        <el-input v-model.trim="form.username" :placeholder="t('userPlaceholder')" autocomplete="off" :input-style="{boxShadow: 'none'}" @keyup.enter="handleLogin(formRef)" class="h-[60px]">
                                                            <template #prefix>
                                                                <img class="max-w-[24px]" src="@/app/assets/images/login/username.png" alt="" object-fit="contain" />
                                                            </template>
                                                        </el-input>
                                                    </el-form-item>

                                                    <el-form-item prop="password" class="mt-[38px] h-[60px] bg-[#F9FAFB] rounded-[6px]">
                                                        <el-input v-model.trim="form.password" :placeholder="t('passwordPlaceholder')" type="password" autocomplete="new-password" @keyup.enter="handleLogin(formRef)" :show-password="true" class="h-[60px]">
                                                            <template #prefix>
                                                                <img class="max-w-[24px]" src="@/app/assets/images/login/password.png" alt="" object-fit="contain" />
                                                            </template>
                                                        </el-input>
                                                    </el-form-item>
                                                </template>
                                                <template v-if="type == 'mobile'">
                                                    <el-form-item prop="mobile" class="h-[60px] bg-[#F9FAFB] rounded-[6px]">
                                                        <el-input v-model.trim="form.mobile" :placeholder="t('请输入手机号')" autocomplete="off" :input-style="{boxShadow: 'none'}" class="h-[60px]" :inline-message="true" :maxlength="11">
                                                            <template #prefix>
                                                                <img class="max-w-[24px]" src="@/app/assets/images/login/mobile.png" alt="" object-fit="contain" />
                                                            </template>
                                                        </el-input>
                                                    </el-form-item>
                                                    <el-form-item prop="mobile_code" class="mt-[38px] h-[60px] bg-[#F9FAFB] rounded-[6px]">
                                                        <el-input v-model="form.mobile_code" :placeholder="t('请输入验证码')" autocomplete="off" :input-style="{boxShadow: 'none'}" class="h-[60px]" :inline-message="true"  :maxlength="4">
                                                            <template #suffix>
                                                                <sms-code :mobile="form.mobile" type="login" v-model="form.mobile_key"
                                                                    @click="sendSmsCode" ref="smsCodeRef"></sms-code>
                                                            </template>
                                                            <template #prefix>
                                                                <img class="max-w-[24px]" src="@/app/assets/images/login/code.png" alt="" object-fit="contain" />
                                                            </template>
                                                        </el-input>
                                                    </el-form-item>
                                                </template>

                                                <el-form-item>
                                                    <el-button type="primary" class="mt-[38px] !h-[50px]  w-full" @click="handleLogin(formRef)" :loading="loading">{{ loading ? t('logging') : t('login') }}</el-button>
                                                </el-form-item>
                                            </el-form>
                                        </div>
                                        <div class="p-[30px] absolute top-0 left-0 w-full h-full transition-transform duration-300 ease-linear transform" :class="{'translate-x-0': switchLogin === 'site','translate-x-full': switchLogin !== 'site'}">
                                            <div class="flex items-center cursor-pointer mb-[20px]" @click="logoutFn">
                                                <span class="nc-iconfont nc-icon-zuoV6xx mr-[2px]"></span>
                                                <span class="text-[14px]">退出</span>
                                            </div>
                                            <div class="text-[24px] font-600 mb-[10px]">您可以进入以下管理后台</div>
                                            <div class="text-[16px] text-[#666] mb-[14px]">已绑定了以下组织，您可以进入以下任一组织</div>
                                            <div class="max-h-[270px] overflow-y-auto site-list-warp">
                                                <div class="p-[22px] flex items-center rounded-[10px] mb-[10px] cursor-pointer" :class="{'site-style1':(index + 1) % 3 == 1,'site-style2': (index + 1) % 3 == 2, 'site-style3': (index + 1) % 3 == 0}" v-for="(item,index) in siteList" :key="index" @click="selectSite(item)">
                                                    <el-image class="w-[36px] h-[36px] rounded-full" :src="img(item.front_end_logo)" fit="contain">
                                                        <template #error>
                                                            <div class="w-[36px] h-[36px] rounded-full flex items-center justify-center" :class="{'bg-[#165dff]':(index + 1) % 3 == 1,'bg-[#FF7D00]': (index + 1) % 3 == 2, 'bg-[#722ed1]': (index + 1) % 3 == 0}">
                                                                <span class="nc-iconfont nc-icon-dianpuV6xx1 text-[#fff]"></span>
                                                            </div>
                                                        </template>
                                                    </el-image>
                                                    <div class="flex-1 ml-[10px]">{{ item.site_name }}</div>
                                                    <span class="nc-iconfont nc-icon-youV6xx text-[20px]"></span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="flex items-center justify-center mt-[20px] pb-[20px] text-sm text-[#666]" v-if="copyright">
                        <a :href="copyright.gov_url" v-if="copyright.gov_record" class="flex" target="_blank">
                            <img src="@/app/assets/images/gov_icon.png" alt="" class="h-[20px] mr-1">
                            <span class="mr-3">公安备案号:{{ copyright.gov_record }}</span>
                        </a>
                        <a href="https://beian.miit.gov.cn/" target="_blank" v-if="copyright.icp">
                            <span class="mr-3">备案号:{{ copyright.icp }}</span>
                        </a>
                        <a :href="copyright.copyright_link" target="_blank">
                            <span class="mr-3" v-if="copyright.company_name">{{ copyright.company_name }}</span>
                            <span class="mr-3" v-if="copyright.copyright_desc">©{{ copyright.copyright_desc }}</span>
                        </a>
                    </div>
                </div>
            </div>
        </el-main>

        <div class="flex items-center justify-center mt-[20px] pb-[20px] text-sm text-[#666]" v-if="copyright  && loginType == 'admin'">
            <a :href="copyright.gov_url" v-if="copyright.gov_record" class="flex" target="_blank">
                <img src="@/app/assets/images/gov_icon.png" alt="" class="h-[20px] mr-1">
                <span class="mr-3">公安备案号:{{ copyright.gov_record }}</span>
            </a>
            <a href="https://beian.miit.gov.cn/" target="_blank" v-if="copyright.icp">
                <span class="mr-3">备案号:{{ copyright.icp }}</span>
            </a>
            <a :href="copyright.copyright_link" target="_blank">
                <span class="mr-3" v-if="copyright.company_name">{{ copyright.company_name }}</span>
                <span class="mr-3" v-if="copyright.copyright_desc">©{{ copyright.copyright_desc }}</span>
            </a>
        </div>

        <!-- 验证组件 -->
        <verify @success="success"  :captchaType="loginType == 'admin' ? loginConfig.captcha_type : loginConfig.site_captcha_type" :imgSize="{ width: '330px', height: '155px' }" ref="verifyRef" v-if="loginConfig"></verify>
        <!-- <el-footer></el-footer> -->
    </el-container>
</template>

<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { t } from '@/lang'
import storage from '@/utils/storage'
import { getLoginConfig } from '@/app/api/auth'
import { getHomeSiteList } from '@/app/api/home'
import useUserStore from '@/stores/modules/user'
import { setWindowTitle, img, getAppType } from '@/utils/common'
import { getWebCopyright } from '@/app/api/sys'
import useSystemStore from '@/stores/modules/system'
import { ADMIN_ROUTE, SITE_ROUTE } from '@/router/routers'

const loading = ref(false)
const imgLoading = ref(false)
const userStore = useUserStore()
const route = useRoute()
const router = useRouter()
const copyright = ref<any>(null)
const type = ref<any>('username')
const switchLogin = ref<any>('login')
const siteList = ref<any>([])

// 切换登录方式
const handleType = (val: string) => {
    type.value = val
}

getWebCopyright().then(({ data }) => {
    copyright.value = data
})

route.redirectedFrom && (route.query.redirect = route.redirectedFrom.path)

const webSite = computed(() => useSystemStore().website)
// 判断登录页面[平台或者站点]
const loginType = ref(getAppType())

watch(() => webSite.value, () => {
    if (loginType.value == 'site') {
        setWindowTitle(webSite.value.site_name + '-' + t('siteLogin'))
    } else {
        setWindowTitle(webSite.value.site_name + '-' + t('adminLogin'))
    }
})

// 验证码 - start
const verifyRef = ref(null)
const success = (params: any) => {
    if (type.value == 'username') {
        loginFn({ captcha_code: params.captchaVerification })
    }
    if (type.value == 'mobile') {
        form.captcha_code = params.captchaVerification
        form.captcha_type = loginType.value == 'admin' ? loginConfig.value.captcha_type : loginConfig.value.site_captcha_type
        smsCodeRef.value?.handleConfirm(form)
    }
}
// 验证码 - end

const form = reactive({
    username: '',
    password: '',
    mobile: '',
    mobile_code: '',
    mobile_key: '',
    captcha_code: '',
    captcha_type: ''
})

// 获取登录配置信息
const loginConfig = ref()
const getLoginConfigFn = async (id: number = 0) => {
    imgLoading.value = true
    const data = await (await getLoginConfig()).data
    loginConfig.value = data
    imgLoading.value = false
}
getLoginConfigFn()

const formRef = ref<FormInstance>()

const formRules = computed(() => {
    return {
        username: [
            { required: true, message: t('userPlaceholder'), trigger: 'blur' }
        ],
        password: [
            { required: true, message: t('passwordPlaceholder'), trigger: 'blur' }
        ],
        mobile: [
            { required: true, message: t('请输入手机号'), trigger: ['blur', 'change'] },
            {
                validator (rule: any, value: string, callback: any) {
                    const reg = /^1[3-9]\d{9}$/
                    if (!reg.test(value)) {
                        callback(new Error('请输入正确的手机号'))
                    } else {
                        callback()
                    }
                },
                trigger: ['blur']
            }
        ],
        mobile_code: [
            { required: type.value == 'mobile', message: t('请输入验证码'), trigger: ['change'] }
        ]
    }
})

const handleLogin = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate((valid, fields) => {
        if (valid) {
            if ((parseInt(loginConfig.value.is_captcha) && loginType.value == 'admin') || (parseInt(loginConfig.value.is_site_captcha) && loginType.value == 'site' && type.value == 'username')) {
                verifyRef.value.show()
            } else {
                loginFn()
            }
        }
    })
}

// data 验证码
const loginFn = (data = {}) => {
    loading.value = true
    if (loginType.value == 'admin') {
        userStore.login({ username: form.username, password: form.password, captcha_type: loginConfig.value.captcha_type, ...data }, loginType.value).then(res => {
            storage.set({ key: 'app_type', data: loginType.value })
            const { query: { redirect } } = route
            if (typeof redirect === 'string') {
                router.push(redirect)
            } else {
                router.push({ name: loginType.value == 'admin' ? ADMIN_ROUTE.children[0].name : SITE_ROUTE.children[0].name })
            }
        }).catch(() => {
            loading.value = false
        })
    } else {
        if (type.value == 'mobile') {
            userStore.mobileLoginFn({ mobile: form.mobile, mobile_code: form.mobile_code, mobile_key: form.mobile_key, captcha_type: loginConfig.value.site_captcha_type, ...data }).then(res => {
                storage.set({ key: 'app_type', data: loginType.value })
                getHomeSiteList().then((res) => {
                    siteList.value = res.data
                    switchLogin.value = 'site'
                })
            }).catch(() => {
                loading.value = false
            })
        } else {
            userStore.login({ username: form.username, password: form.password, captcha_type: loginConfig.value.site_captcha_type, ...data }, loginType.value).then(res => {
                storage.set({ key: 'app_type', data: loginType.value })
                getHomeSiteList().then((res) => {
                    siteList.value = res.data
                    switchLogin.value = 'site'
                })
            }).catch(() => {
                loading.value = false
            })
        }
    }
}

// 手机号登录
const smsCodeRef = ref(null)

const sendSmsCode = async () => {
    await formRef.value?.validateField('mobile', async (valid, fields) => {
        if (valid) {
            verifyRef.value?.show()
        }
    })
}

// 选择店铺
const selectSite = (site: any) => {
    storage.set({ key: 'siteId', data: site.site_id })
    storage.set({ key: 'siteInfo', data: site })
    storage.set({ key: 'comparisonSiteIdStorage', data: site.site_id })
    useUserStore().$patch((site) => {
        site.siteInfo = site
    })
    location.href = `${location.origin}/site/`
}
// 退出登录
const logoutFn = () => {
    userStore.logout()
}
</script>

<style lang="scss" scoped>
.site-bg {
    background-image: url("@/app/assets/images/site_bg.jpg");
    background-repeat: no-repeat;
    background-size: cover;
}
.login-wrap {
    background-image: url("@/app/assets/images/login/login_index_bg.png");
    background-repeat: no-repeat;
    background-size: cover;
}

.login-main {
    display: flex !important;

    .login {
        background: var(--el-bg-color);
    }

    .el-form-item__error {
        top : 45px;
    }

}
.login-box-wrap {
    box-shadow: 0px 0px 14px 0px rgba(0,0,0,0.2);
    border-radius: 14px;
    border: 1px solid #d8d8d8;
}
.form-two :deep(.el-input__wrapper) {
    box-shadow: none !important;
    padding: 10px !important;
    background-color: #F9FAFB !important;
    box-sizing: border-box;
}
.select-wrap{
    position: relative;
    color: var(--el-color-primary);
    &::before{
        content: "";
        display: inline-block;
        position: absolute;
        bottom: -10px;
        left: 50%;
        transform: translateX(-50%);
        width: 72px;
        height: 2px;
        background-color: var(--el-color-primary);
    }
}
.site-style1 {
    background: #e8efff;
    border: 1px solid rgba(22,93,255,0.3);
}
.site-style2 {
    background: #fff2e6;
    border: 1px solid rgba(255,125,0,0.3);
}
.site-style3 {
    background: #F3F4FF;
    border: 1px solid #C8CEFF;
}
 .site-list-warp::-webkit-scrollbar{
    display: none;
 }
@media only screen and (max-width: 750px) {
    .login-main-left {
        display: none;
    }
}
</style>
