<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-form :model="formData" label-width="95" ref="formRef"  class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <el-form-item prop="community_is_use">
                        <span>{{ t('communityIsUse') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.community_is_use">
                            <el-radio :value="1">{{ t('isCommentsOpen') }}</el-radio>
                            <el-radio :value="0">{{ t('isCommentsClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item prop="content_review_status">
                        <span>{{ t('contentIsToExamine') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.content_review_status">
                            <el-radio :value="1">{{ t('isCommentsOpen') }}</el-radio>
                            <el-radio :value="0">{{ t('isCommentsClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item prop="community_comments_status">
                        <span>{{ t('isComments') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.community_comments_status">
                            <el-radio :value="1">{{ t('isCommentsOpen') }}</el-radio>
                            <el-radio :value="0">{{ t('isCommentsClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item prop="comment_moderation_status">
                        <span>{{ t('commentsIsToExamine') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.comment_moderation_status">
                            <el-radio :value="1">{{ t('isCommentsOpen') }}</el-radio>
                            <el-radio :value="0">{{ t('isCommentsClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-card>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { getConfig, setConfig } from '@/addon/sow_community/api/admin/config'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title
const formData = ref<any>({
    community_is_use: 1,
    content_review_status: 1,
    community_comments_status: 1,
    comment_moderation_status: 1
})

const loading = ref(false)
const getConfigFn = () => {
    loading.value = true
    getConfig().then(res => {
        Object.keys(formData.value).forEach((key: string) => {
            if (res.data[key] != undefined) formData.value[key] = Number(res.data[key])
        })
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getConfigFn()
const formRef = ref()

const onSave = async (formEl: any) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid:any) => {
        if (valid) {
            loading.value = true
            setConfig(formData.value).then(res => {
                getConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>
<style lang="scss" scoped>
</style>
