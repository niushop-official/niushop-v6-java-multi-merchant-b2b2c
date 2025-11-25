<template>
    <div class="main-container" v-loading="loading">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/shop/apply' })" />
        </el-card>
        <el-card class="box-card !border-none" shadow="never">
            <el-form :model="detail" label-width="100px" ref="formRef" class="page-form" v-loading="loading"
                label-position="left">
                <el-card class="box-card !border-none" shadow="never">
                    <el-row class="pl-[15px] mb-[20px]">
                        <el-col :span="8">
                            <el-form-item :label="t('siteName')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.site_name }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('userName')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.user_name }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('userMobile')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.user_mobile }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('nickname')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.nickname }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('categoryId')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.category_name || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('groupId')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.group_name || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('createTime')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.create_time || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('updateTime')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.update_time || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('status')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.status_name || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.audit_time">
                            <el-form-item :label="t('auditTime')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.audit_time || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.refuse_reason">
                            <el-form-item :label="t('refuseReason')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.refuse_reason || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('openStatus')">
                                <span class="text-[14px] text-[#666666]">
                                    {{ detail.open_status_name || '' }}
                                </span>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item :label="t('businessLicense')">
                                <div class="flex items-center" v-if="detail.business_license">
                                    <div v-for="(item, index) in detail.business_license.split(',')" :key="index">
                                        <el-image style="width: 100px; height: 100px" class="mr-[10px]" :src="img(item)" :zoom-rate="1.2"
                                            :max-scale="7" :min-scale="0.2" :preview-src-list="[img(item)]"
                                            :initial-index="4" fit="cover" />
                                    </div>
                                </div>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-card>
            </el-form>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { getShopApplyInfo } from '@/app/api/shop_admin'
import { img } from '@/utils/common'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const apply_id: number = parseInt(route.query.id || 0)
const loading = ref(false)
const detail = ref({})

const getShopApplyInfoFn = () => {
    loading.value = true
    getShopApplyInfo(apply_id).then(({ data }) => {
        detail.value = data
        loading.value = false
    }).catch()
}
getShopApplyInfoFn()

</script>

<style lang="scss" scoped></style>
