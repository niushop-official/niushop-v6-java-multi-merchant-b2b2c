<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <el-form :model="formData" label-width="150px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">

                    <el-form-item :label="t('siteName')" prop="site_name">
                        <el-input v-model.trim="formData.site_name" :placeholder="t('siteNamePlaceholder')" class="input-width" clearable maxlength="20" show-word-limit />
                    </el-form-item>

                    <el-form-item :label="t('icon')">
                        <div>
                            <upload-image v-model="formData.icon" />
                            <p class="text-[12px] text-[#a9a9a9]">{{ t('iconPlaceholder') }}</p>
                        </div>
                    </el-form-item>
                    <el-form-item :label="t('frontEndLogo')">
                        <div>
                            <upload-image v-model="formData.front_end_logo" />
                            <p class="text-[12px] text-[#a9a9a9]">{{ t('frontEndLogoPlaceholder') }}</p>
                        </div>
                    </el-form-item>

                    <!-- <el-form-item :label="t('logo')">
                        <div>
                            <upload-image v-model="formData.logo" />
                            <p class="text-[12px] text-[#a9a9a9]">{{ t('logoPlaceholder') }}</p>
                        </div>
                    </el-form-item> -->

                    <el-form-item :label="t('frontEndName')">
                        <el-input v-model="formData.front_end_name" :placeholder="t('frontEndNamePlaceholder')" class="input-width" clearable maxlength="20" show-word-limit />
                    </el-form-item>

                    <el-form-item :label="t('keywords')">
                        <el-input v-model="formData.keywords" :placeholder="t('keywordsPlaceholder')" class="input-width" clearable maxlength="20" show-word-limit />
                    </el-form-item>

                    <el-form-item :label="t('desc')">
                        <el-input v-model="formData.desc" type="textarea" rows="4" clearable :placeholder="t('descPlaceholder')" class="input-width" maxlength="100" show-word-limit />
                    </el-form-item>
                    </el-card>
                    <el-card class="box-card !border-none" shadow="never" v-if="appType == 'admin'">
                    <h3 class="panel-title !text-sm">{{ t('serviceInformation') }}</h3>
                    <el-form-item :label="t('contactsTel')">
                        <el-input v-model="formData.tel" :placeholder="t('contactsTelPlaceholder')" class="input-width" clearable maxlength="20" show-word-limit />
                    </el-form-item>
                    <el-form-item :label="t('wechatCode')">
                        <upload-image v-model="formData.wechat_code" />
                    </el-form-item>
                    <el-form-item :label="t('customerServiceCode')">
                        <upload-image v-model="formData.enterprise_wechat" />
                    </el-form-item>
                </el-card>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" :loading="loading" @click="save(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { setWebsite, getWebsite, getService } from '@/app/api/sys'
import { FormInstance, FormRules } from 'element-plus'
import { getAppType } from '@/utils/common'
import { useRoute } from 'vue-router'
import useUserStore from '@/stores/modules/user'
import useSystemStore from '@/stores/modules/system'

const route = useRoute()
const pageName = route.meta.title
const loading = ref(true)
const appType = ref(getAppType())
const activeName = ref('basic')
const formData = reactive<Record<string, string>>({
    site_name: '',
    logo: '',
    desc: '',
    latitude: '',
    keywords: '',
    longitude: '',
    province_id: '',
    city_id: '',
    district_id: '',
    address: '',
    full_address: '',
    business_hours: '',
    phone: '',
    front_end_name: '',
    front_end_logo: '',
    icon: '',
    wechat_code: '',
    enterprise_wechat: '',
    tel: ''
})

const setFormData = async (id: number = 0) => {
    const data = await (await getWebsite()).data
    Object.keys(formData).forEach((key: string) => {
        if (data[key] != undefined) formData[key] = data[key]
    })

    const serviceData = await (await getService()).data
    formData.wechat_code = serviceData.wechat_code
    formData.enterprise_wechat = serviceData.enterprise_wechat
    formData.tel = serviceData.tel

    loading.value = false
}
setFormData()

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = reactive<FormRules>({
    site_name: [
        { required: true, message: t('siteNamePlaceholder'), trigger: 'blur' }
    ],
    front_end_name: [
        { required: true, message: t('frontEndNamePlaceholder'), trigger: 'blur' }
    ]
})

/**
 * 保存
 */
const save = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            setWebsite(formData).then(() => {
                loading.value = false
                useUserStore().getSiteInfo()
                useSystemStore().getWebsiteInfo()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped></style>
