<template>
    <div class="main-container" v-loading="loading">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/shop/account_apply' })" />
        </el-card>
        <el-card class="box-card !border-none" shadow="never" v-if="detail">
            <el-form :model="detail" label-width="140px"  ref="formRef" :inline="true" class="page-form" v-loading="loading" label-position="left">
                <h3 class="panel-title">{{ t('店铺信息') }}</h3>
                <el-row class="pl-[30px] mb-[20px]">
                    <el-col :span="8">
                        <el-form-item :label="t('店铺名称')">
                            <div class="text-[14px] text-[#666666]"> {{ detail.site.site_name }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item :label="t('入驻平台')">
                            <div class="text-[14px] text-[#666666]"> {{ detail.type_name }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item :label="t('分账编号')">
                            <div class="text-[14px] text-[#666666]"> {{ detail.extend_no }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item :label="t('审核状态')">
                            <div class="text-[14px] text-[#666666]"> {{ detail.status_name }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="detail.status == 3">
                        <el-form-item :label="t('失败原因')">
                            <div class="text-[14px] text-[#666666]"> {{ detail.fail_reason }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item :label="t('创建时间')">
                            <div class="text-[14px] text-[#666666]"> {{ detail.create_time }}</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <div>
                    <h3 class="panel-title">{{ t('基本信息') }}</h3>
                    <el-row class="pl-[30px] mb-[20px]">
                        <el-col :span="24">
                            <el-form-item :label="t('用户类型')">
                                <div class="text-[14px] text-[#666666]">{{ detail.infos.type == 1 ? '企业' : '个人' }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-row v-if="detail.infos.type == 1">
                                <el-col :span="8">
                                    <el-form-item :label="t('营业执照附件')">
                                        <el-image class="w-[100px] h-[100px]" :src="img(detail.infos.license_img)" :preview-src-list="[img(detail.infos.license_img)]" :initial-index="0" fit="cover" />
                                    </el-form-item>
                                </el-col>
                                <el-col :span="8">
                                    <el-form-item :label="t('法人身份证人像面')">
                                        <el-image class="w-[100px] h-[100px]" :src="img(detail.infos.legal_cert_front_pic)" :preview-src-list="[img(detail.infos.legal_cert_front_pic)]" :initial-index="0" fit="cover" />
                                    </el-form-item>
                                </el-col>
                                <el-col :span="8">
                                    <el-form-item :label="t('法人身份证国徽面')">
                                        <el-image class="w-[100px] h-[100px]" :src="img(detail.infos.legal_cert_back_pic)" :preview-src-list="[img(detail.infos.legal_cert_back_pic)]" :initial-index="0" fit="cover" />
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row v-else>
                                <el-col :span="8">
                                    <el-form-item :label="t('法人身份证人像面')">
                                        <el-image class="w-[100px] h-[100px]" :src="img(detail.infos.per_cert_front_pic)" :preview-src-list="[img(detail.infos.per_cert_front_pic)]" :initial-index="0" fit="cover" />
                                    </el-form-item>
                                </el-col>
                                <el-col :span="8">
                                    <el-form-item :label="t('法人身份证国徽面')">
                                        <el-image class="w-[100px] h-[100px]" :src="img(detail.infos.per_cert_back_pic)" :preview-src-list="[img(detail.infos.per_cert_back_pic)]" :initial-index="0" fit="cover" />
                                    </el-form-item>
                                </el-col>
                            </el-row>
                        </el-col>
                        <template v-if="detail.infos.type == 1">
                            <el-col :span="8">
                                <el-form-item :label="t('企业注册名称')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.reg_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('经营简称')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.short_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('营业执照编号')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.license_code }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('证照有效期')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.license_begin_date }} ~ {{ detail.infos.license_validity_type == 1 ? '长久有效' : detail.infos.license_end_date }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('注册地址')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.reg_prov_name }}{{ detail.infos.reg_area_name }}{{ detail.infos.reg_district_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('注册详细地址')">
                                    <div class="text-[14px] text-[#666666]">{{  detail.infos.reg_detail }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('法人证件类型')">
                                    <div class="text-[14px] text-[#666666]">{{  detail.infos.legal_cert_type_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('法人姓名')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.legal_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('联系人姓名')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.contact_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('法人证件号')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.legal_cert_no }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('法人证件有效期')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.legal_cert_begin_date }} ~ {{ detail.infos.legal_cert_validity_type == 1 ? '长久有效' :  detail.infos.legal_cert_end_date }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('手机号')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.contact_mobile }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('电子邮箱')" v-if="detail.infos.contact_email">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.contact_email }}</div>
                                </el-form-item>
                            </el-col>
                        </template>
                        <template v-if="detail.infos.type == 2">
                            <el-col :span="8">
                                <el-form-item :label="t('证件类型')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.per_cert_type_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('姓名')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.per_name }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('证件号码')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.per_cert_no }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('证件有效期')">
                                   <div class="text-[14px] text-[#666666]">{{ detail.infos.per_cert_begin_date }} ~ {{ detail.infos.per_cert_validity_type == 1 ? '长期有效'  : detail.infos.per_cert_end_date }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('手机号')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.per_mobile_no }}</div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('电子邮箱')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.per_email }}</div>
                                </el-form-item>
                            </el-col>
                           <el-col :span="8">
                                <el-form-item :label="t('地址')">
                                    <div class="text-[14px] text-[#666666]">{{ detail.infos.per_address }}</div>
                                </el-form-item>
                           </el-col>
                        </template>
                    </el-row>
                    <h3 class="panel-title">{{ t('银行卡绑定') }}</h3>
                    <el-row class="pl-[30px] mb-[20px]">
                        <el-col :span="8" v-if="detail.infos.card_type_name">
                            <el-form-item :label="t('银行账户类型')">
                                <div>{{ detail.infos.card_type_name }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.card_name">
                            <el-form-item :label="t('银行账户名称')">
                                <div>{{ detail.infos.card_name }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.card_no">
                            <el-form-item :label="t('银行卡号')">
                                <div>{{ detail.infos.card_no }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.cert_type_name">
                            <el-form-item :label="t('持卡人证件类型')">
                                <div>{{ detail.infos.cert_type_name }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.cert_no">
                            <el-form-item :label="t('持卡人证件号码')">
                                <div>{{ detail.infos.cert_no }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.cert_begin_date">
                            <el-form-item :label="t('持卡人证件有效期')">
                                <div>{{ detail.infos.cert_begin_date }} ~ {{ detail.infos.cert_validity_type == 1 ? '长久有效': detail.infos.cert_end_date }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.card_prov_name">
                            <el-form-item :label="t('银行所在地址')">
                                <div>{{ detail.infos.card_prov_name }}{{ detail.infos.card_area_name  }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.bank_code_name">
                            <el-form-item :label="t('开户行')">
                                <div>{{ detail.infos.bank_code_name }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.branch_code">
                             <el-form-item :label="t('支行联行号')">
                                <div>{{ detail.infos.branch_code }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8" v-if="detail.infos.branch_name">
                            <el-form-item :label="t('支行名称')">
                                <div>{{ detail.infos.branch_name }}</div>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <h3 class="panel-title">{{ t('结算配置') }}</h3>
                    <el-row class="pl-[30px] mb-[20px]">
                        <el-col :span="8">
                            <el-form-item :label="t('起结金额')">
                                <div>{{ detail.infos.min_amt }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item :label="t('留存金额')">
                                <div>{{ detail.infos.remained_amt }}</div>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </div>
            </el-form>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getAccountApplyInfo } from '@/app/api/shop_admin'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const id = parseInt(route.query.id || 0)
const loading = ref(false)
const detail = ref<any>()

const getAccountApplyInfoFn = () => {
    loading.value = true
    getAccountApplyInfo(id).then(({ data }) => {
        detail.value = data
        detail.value.infos.min_amt = detail.value.infos.min_amt == '' ? '0.00' : detail.value.infos.min_amt
        detail.value.infos.remained_amt = detail.value.infos.remained_amt == '' ? '0.00' : detail.value.infos.remained_amt
        loading.value = false
    }).catch()
}
getAccountApplyInfoFn()
</script>

<style scoped>

</style>