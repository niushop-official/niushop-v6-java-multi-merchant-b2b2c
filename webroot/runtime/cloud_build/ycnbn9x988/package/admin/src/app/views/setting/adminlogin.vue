<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <el-form :model="formData" label-width="150px" ref="ruleFormRef" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">

                    <h3 class="panel-title !text-sm">{{ t('admin') }}</h3>

                    <el-form-item :label="t('isCaptcha')" prop="formData.is_auth_register">
                        <el-switch v-model="formData.is_captcha"/>
                    </el-form-item>
                    <el-form-item :label="t('验证类型')" prop="captcha_type">
                        <el-radio-group v-model="formData.captcha_type">
                            <el-popover placement="bottom" :width="180" trigger="hover" v-for="(item,key) in captchaType" :key="key">
                                <template #reference>
                                    <el-radio :label="key">{{ item.name }}</el-radio>
                                </template>
                                <div class="flex justify-center">
                                    <img :src="img(item.example_img)" alt="" class="w-[150px] h-[150px]" />
                                </div>
                            </el-popover>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item :label="t('bgImg')">
                        <upload-image v-model="formData.bg" />
                        <div class="form-tip">{{t('adminBgImgTip')}}</div>
                    </el-form-item>

                </el-card>

                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="panel-title !text-sm">{{ t('site') }}</h3>

                    <el-form-item :label="t('isCaptcha')" prop="formData.is_auth_register">
                        <el-switch v-model="formData.is_site_captcha"/>
                    </el-form-item>

                    <el-form-item :label="t('验证类型')" prop="site_captcha_type">
                        <el-radio-group v-model="formData.site_captcha_type">
                            <el-popover placement="bottom" :width="180" trigger="hover" v-for="(item,key) in captchaType" :key="key">
                                <template #reference>
                                    <el-radio :label="key">{{ item.name }}</el-radio>
                                </template>
                                <div class="flex justify-center">
                                    <img :src="img(item.example_img)" alt="" class="w-[150px] h-[150px]" />
                                </div>
                            </el-popover>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item :label="t('bgImg')">
                        <upload-image v-model="formData.site_bg" />
                        <div class="form-tip">{{t('siteBgImgTip')}}</div>
                    </el-form-item>

                </el-card>

                <el-card class="box-card !border-none mt-4" shadow="never" v-if="userStore.checkAddon('circle')">
                    <h3 class="panel-title !text-sm">{{ t('circle') }}</h3>

                    <el-form-item :label="t('isCaptcha')" prop="formData.is_auth_register">
                        <el-switch v-model="formData.is_circle_captcha"/>
                    </el-form-item>

                    <el-form-item :label="t('验证类型')" prop="circle_captcha_type">
                        <el-radio-group v-model="formData.circle_captcha_type">
                            <el-popover placement="bottom" :width="180" trigger="hover" v-for="(item,key) in captchaType" :key="key">
                                <template #reference>
                                    <el-radio :label="key">{{ item.name }}</el-radio>
                                </template>
                                <div class="flex justify-center">
                                    <img :src="img(item.example_img)" alt="" class="w-[150px] h-[150px]" />
                                </div>
                            </el-popover>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item :label="t('bgImg')">
                        <upload-image v-model="formData.circle_bg" />
                        <div class="form-tip">{{t('circleBgImgTip')}}</div>
                    </el-form-item>

                </el-card>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(ruleFormRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getCaptchaType } from '@/app/api/notice'
import { getConfigLogin, setConfigLogin } from '@/app/api/sys'
import { FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'
import useUserStore from '@/stores/modules/user'

const route = useRoute()
const pageName = route.meta.title
const userStore = useUserStore()

const loading = ref(true)
const ruleFormRef = ref<FormInstance>()
const formData = reactive<Record<string, number | string>>({
    is_captcha: 0,
    is_site_captcha: 0,
    is_circle_captcha: 0,
    bg: '',
    site_bg: '',
    circle_bg: '',
    captcha_type: 'blockPuzzle',
    site_captcha_type: 'blockPuzzle',
    circle_captcha_type: 'blockPuzzle'
})

const getFormData = async (id: number = 0) => {
    const data = await (await getConfigLogin()).data
    Object.keys(formData).forEach((key: string) => {
        if (['is_captcha', 'is_site_captcha', 'is_circle_captcha'].includes(key)) formData[key] = Boolean(Number(data[key]))
        else formData[key] = data[key]
    })
    loading.value = false
}
getFormData()

const captchaType = ref()
const getCaptchaTypeFn = () => {
    getCaptchaType().then((res: any) => {
        captchaType.value = res.data
    })
}
getCaptchaTypeFn()

const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate((valid) => {
        if (valid) {
            const save = JSON.parse(JSON.stringify(formData))
            Object.keys(save).forEach((key) => {
                if (['is_captcha', 'is_site_captcha', 'is_circle_captcha'].includes(key)) save[key] = Number(save[key])
            })

            setConfigLogin(save).then(() => {
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
.el-form .form-tip{
    line-height: 1.5;
    margin-top: 5px;
}
</style>
