<template>
    <div>
        <!-- 榜单设置 -->
        <el-card class="box-card !border-none main-container" shadow="never">
            <div class="flex justify-between items-center mb-[10px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <div class="flex justify-start items-center">
                        <el-form-item  label="审核开关" prop="is_verify_open">
                            <div>
                                <el-radio-group class="mx-[10px]" v-model="formData.is_verify_open">
                                    <el-radio :label="1">开启</el-radio>
                                    <el-radio :label="0">关闭</el-radio>
                                </el-radio-group>
                                <div class="text-[#999] text-[12px]">审核关闭后店铺端添加拼团活动时将自动通过审核</div>
                            </div>
                        </el-form-item>
                    </div>
                    <div class="flex justify-start items-center">
                        <h3 class="panel-title !text-sm pl-[15px]">{{ t('自动关闭订单') }}</h3>
                        <el-form-item class="ml-[-100px]" prop="is_close">
                            <el-radio-group class="mx-[10px]" v-model="formData.is_close">
                                <el-radio :label="1">开启</el-radio>
                                <el-radio :label="0">关闭</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </div>
                    <el-form-item prop="close_length" v-if="formData.is_close == 1">
                        <div>
                            <p class="!text-sm">
                                <span>{{ t('提交订单') }}</span>
                                <el-input v-model.trim="formData.close_length" class="!w-[120px] mx-[10px]" @keyup="filterNumber($event)" clearable />
                                <span>{{ t('分钟未付款，自动关闭订单') }}</span>
                            </p>
                            <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">{{ t('订单关闭时间必须在10-1440分钟之间') }}</p>
                        </div>
                    </el-form-item>
                    <h3 class="panel-title !text-sm pl-[15px]">{{ t('拼团分享设置(公共)') }}</h3>
                    <el-form-item :label="t('分享标题')" prop="share_config">
                        <div>
                            <el-input v-model.trim="formData.share_config.common.title" clearable class="!w-[500px]" :placeholder="t('请输入分享标题')" maxlength="100" show-word-limit />
                            <el-button class="ml-[20px]" type="primary" @click="defaultActiveTitleEvent('commonTitle')" plain>{{ t('使用默认内容') }}</el-button>
                        </div>

                    </el-form-item>
                    <el-form-item :label="t('分享内容')" prop="desc">
                        <div>
                            <el-input v-model="formData.share_config.common.desc" :placeholder="t('请输入分享内容')" type="textarea" maxlength="500" show-word-limit rows="5" class="!w-[500px]" clearable />
                            <el-button class="ml-[20px]" type="primary" @click="defaultActiveTitleEvent('commomDesc')" plain>{{ t('使用默认内容') }}</el-button>
                        </div>
                    </el-form-item>
                    <h3 class="panel-title !text-sm pl-[15px]">{{ t('规则设置') }}</h3>
                    <el-form-item :label="t('拼团规则')" prop="pintuan_brief" >
                        <el-input v-model="formData.pintuan_brief" :placeholder="t('请输入拼团规则')" type="textarea" maxlength="500" show-word-limit rows="5" class="!w-[500px]" clearable />
                    </el-form-item>
                    <h3 class="panel-title !text-sm pl-[15px]">{{ t('广告图') }}</h3>
                    <el-form-item :label="t('活动页广告图')" prop="banner" >
                        <upload-image v-model="formData.banner" :limit="1" />
                    </el-form-item>
                </el-card>
            </el-form>
        </el-card>

        <!-- 提交按钮 -->
        <div class="fixed-footer-wrap">
            <div class="fixed-footer h-[48px]">
                <el-button type="primary" @click="onSave">{{ t("save") }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed } from 'vue'
import { t } from '@/lang'
import { filterNumber } from '@/utils/common'
import { useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'
import { getPintuanAdminConfig, setPintuanAdminConfig, getPintuanAdminShareKeyword, getPintuanAdminShareInit } from '@/addon/pintuan/api/admin/config'

const route = useRoute()
const pageName = route.meta.title

const formData = ref({
    title: '',
    desc: '',
    banner: '',
    close_length: 0,
    is_close: 0,
    is_verify_open: 0,
    title_default: '',
    desc_default: '',
    pintuan_brief: '',
    share_config: {
        group: {
            title: '',
            desc: ''
        },
        normal: {
            title: '',
            desc: ''
        },
        common: {
            title: '',
            desc: ''
        }
    }
})

const formRef = ref<FormInstance>()
const defaultData = ref({})
const getPintuanAdminShareInitFn = () => {
    getPintuanAdminShareInit().then(res => {
        defaultData.value = res.data
    })
}
getPintuanAdminShareInitFn()

const formRules = computed(() => {
    return {
        close_length: [
            { validator: validCloseLength, trigger: 'blur' }
        ]
    }
})

const validCloseLength = (rule:any, value:any, callback:Function) => {
    if (formData.value.is_close != 0) {
        if (value == '') {
            return callback(new Error(t('请输入订单关闭时间')))
        } else if (Number(value) >= 10 && Number(value) <= 1440) {
            return callback()
        } else {
            return callback(new Error(t('订单关闭时间必须在10-1440分钟之间')))
        }
    } else {
        return callback()
    }
}

const loading = ref(false)
const getPintuanAdminConfigFn = () => {
    loading.value = true
    getPintuanAdminConfig().then((res: any) => {
        Object.keys(formData.value).forEach((key) => {
            if (res.data[key]) formData.value[key] = res.data[key]
        })
        formData.value.is_close = Number(res.data.is_close)
        loading.value = false
    })
}

getPintuanAdminConfigFn()
/**
 * 使用默认说明
 */
const defaultActiveTitleEvent = (type:any) => {
    if (type == 'groupTitle') {
        formData.value.share_config.group.title = defaultData.value.group_title
    } else if (type == 'normalTitle') {
        formData.value.share_config.normal.title = defaultData.value.normal_title
    } else if (type == 'groupDesc') {
        formData.value.share_config.group.desc = defaultData.value.group_desc
    } else if (type == 'normalDesc') {
        formData.value.share_config.normal.desc = defaultData.value.normal_desc
    } else if (type == 'commonTitle') {
        formData.value.share_config.common.title = defaultData.value.common_title
    } else if (type == 'commomDesc') {
        formData.value.share_config.common.desc = defaultData.value.common_desc
    }
}
// 分享设置
const addgroupableDataOptions = ref([])
const addnormalableDataOptions = ref([])
const getPintuanAdminShareKeywordFn = () => {
    getPintuanAdminShareKeyword().then((res: any) => {
        if (res.data) {
            addgroupableDataOptions.value = res.data.group
            addnormalableDataOptions.value = res.data.normal
        }
    })
}
getPintuanAdminShareKeywordFn()

const onSave = async () => {
    if (loading.value) return
    await formRef.value?.validate(async (valid) => {
        if (valid) {
            // fillData(); // 填充数据
            loading.value = true
            setPintuanAdminConfig(formData.value).then(() => {
                getPintuanAdminConfigFn()
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
</style>
