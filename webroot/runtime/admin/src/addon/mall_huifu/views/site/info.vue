<template>
    <div class="main-container">
        <el-card  class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{pageName}}</span>
            </div>
            <el-form :model="huifuInfo" label-width="200px" ref="formRef"  class="page-form" v-loading="loading"  v-if="Object.keys(huifuInfo).length">
                <el-form-item :label="t('用户类型')">
                    <div>{{ huifuInfo.type == 1 ? '企业' : '个人' }}</div>
                </el-form-item>
                <template v-if="huifuInfo.type == 1">
                    <el-form-item :label="t('公司类型')">
                        <div>{{ huifuInfo.ent_type_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('企业用户名称')">
                        <div>{{ huifuInfo.reg_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('经营简称')">
                        <div>{{ huifuInfo.short_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('营业执照')">
                        <el-image class="w-[100px] h-[100px]" :src="img(huifuInfo.license_img)" :preview-src-list="[img(huifuInfo.license_img)]" :initial-index="0" :fit="cover" />
                    </el-form-item>
                    <el-form-item :label="t('营业执照编号')">
                        <div>{{ huifuInfo.license_code }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证照有效期起始日期')">
                        <div>{{ huifuInfo.license_begin_date }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证照有效期结束日期')">
                        <div>{{ huifuInfo.license_validity_type == 1 ? '长久有效' : huifuInfo.license_end_date }}</div>
                    </el-form-item>
                    <el-form-item :label="t('注册地址')">
                        <div>{{ huifuInfo.reg_prov_name }}{{ huifuInfo.reg_area_name }}{{ huifuInfo.reg_district_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('注册详细地址')">
                       <div>{{  huifuInfo.reg_detail }}</div>
                    </el-form-item>
                    <el-form-item :label="t('法人证件类型')">
                        <div>{{  huifuInfo.legal_cert_type_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证件人像面')">
                        <el-image class="w-[100px] h-[100px]" :src="img(huifuInfo.legal_cert_front_pic)" :preview-src-list="[img(huifuInfo.legal_cert_front_pic)]" :initial-index="0" :fit="cover" />
                    </el-form-item>
                    <el-form-item :label="t('证件国徽面')">
                        <el-image class="w-[100px] h-[100px]" :src="img(huifuInfo.legal_cert_back_pic)" :preview-src-list="[img(huifuInfo.legal_cert_back_pic)]" :initial-index="0" :fit="cover" />
                    </el-form-item>
                    <el-form-item :label="t('法人姓名')">
                        <div>{{ huifuInfo.legal_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('法人证件号码')">
                        <div>{{ huifuInfo.legal_cert_no }}</div>
                    </el-form-item>
                    <el-form-item :label="t('法人证件有效期开始日期')">
                        <div>{{ huifuInfo.legal_cert_begin_date }}</div>
                    </el-form-item>
                    <el-form-item :label="t('法人证件有效期截止日期')">
                        <div>{{ huifuInfo.legal_cert_validity_type == 1 ? '长久有效' :  huifuInfo.legal_cert_end_date }}</div>
                    </el-form-item>
                    <el-form-item :label="t('联系人姓名')">
                        <div>{{ huifuInfo.contact_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('联系人手机号')">
                        <div>{{ huifuInfo.contact_mobile }}</div>
                    </el-form-item>
                    <el-form-item :label="t('联系人电子邮箱')" v-if="huifuInfo.contact_email">
                        <div>{{ huifuInfo.contact_email }}</div>
                    </el-form-item>
                </template>
                <template v-if="huifuInfo.type == 2">
                    <el-form-item :label="t('姓名')">
                        <div>{{ huifuInfo.per_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证件类型')">
                        <div>{{ huifuInfo.per_cert_type_name }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证件人像面')">
                        <el-image class="w-[100px] h-[100px]" :src="img(huifuInfo.per_cert_front_pic)" :preview-src-list="[img(huifuInfo.per_cert_front_pic)]" :initial-index="0" :fit="cover" />
                    </el-form-item>
                    <el-form-item :label="t('证件国徽面')">
                        <el-image class="w-[100px] h-[100px]" :src="img(huifuInfo.per_cert_back_pic)" :preview-src-list="[img(huifuInfo.per_cert_back_pic)]" :initial-index="0" :fit="cover" />
                    </el-form-item>
                    <el-form-item :label="t('证件号码')">
                        <div>{{ huifuInfo.per_cert_no }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证件有效期开始日期')">
                        <div>{{ huifuInfo.per_cert_begin_date }}</div>
                    </el-form-item>
                    <el-form-item :label="t('证件有效期截止日期')">
                        <div>{{ huifuInfo.per_cert_end_date }}</div>
                    </el-form-item>
                    <el-form-item :label="t('联系人手机号')">
                        <div>{{ huifuInfo.per_mobile_no }}</div>
                    </el-form-item>
                    <el-form-item :label="t('联系人电子邮箱')">
                        <div>{{ huifuInfo.per_email }}</div>
                    </el-form-item>
                </template>

                <el-form-item :label="t('商户通知标识')" v-if="huifuInfo.sms_send_flag">
                    <div>{{ huifuInfo.sms_send_flag }}</div>
                </el-form-item>

                <el-form-item :label="t('卡类型')">
                    <div>{{ huifuInfo.card_type_name }}</div>
                </el-form-item>

                <el-form-item :label="t('卡户名')" v-if="huifuInfo.card_name">
                    <div>{{ huifuInfo.card_name }}</div>
                </el-form-item>
                <el-form-item :label="t('卡号')" v-if="huifuInfo.card_no">
                    <div>{{ huifuInfo.card_no }}</div>
                </el-form-item>
                <el-form-item :label="t('持卡人证件类型')" v-if="huifuInfo.cert_type_name">
                    <div>{{ huifuInfo.cert_type_name }}</div>
                </el-form-item>
                <el-form-item :label="t('持卡人证件号码')" v-if="huifuInfo.cert_no">
                    <div>{{ huifuInfo.cert_no }}</div>
                </el-form-item>
                <el-form-item :label="t('持卡人证件有效期开始日期')" v-if="huifuInfo.cert_begin_date">
                    <div>{{ huifuInfo.cert_begin_date }}</div>
                </el-form-item>
                <el-form-item :label="t('持卡人证件有效期截止日期')" v-if="huifuInfo.cert_end_date || huifuInfo.cert_validity_type">
                    <div>{{ huifuInfo.cert_validity_type == 1 ? '长久有效': huifuInfo.cert_end_date }}</div>
                </el-form-item>
                <el-form-item :label="t('银行所在地址')" v-if="huifuInfo.card_prov_name">
                    <div>{{ huifuInfo.card_prov_name }}{{ huifuInfo.card_area_name  }}</div>
                </el-form-item>
                <el-form-item :label="t('开户行')" v-if="huifuInfo.bank_code_name">
                    <div>{{ huifuInfo.bank_code_name }}</div>
                </el-form-item>
                <el-form-item :label="t('支行联行号')" v-if="huifuInfo.branch_code">
                    <div>{{ huifuInfo.branch_code }}</div>
                </el-form-item>
                <el-form-item :label="t('支行名称')" v-if="huifuInfo.branch_name">
                    <div>{{ huifuInfo.branch_name }}</div>
                </el-form-item>
                <el-form-item :label="t('银行卡绑定手机号')" v-if="huifuInfo.mp">
                    <div>{{ huifuInfo.mp }}</div>
                </el-form-item>
                <el-form-item :label="t('结算批次')" v-if="huifuInfo.settle_batch_no">
                    <div>{{ huifuInfo.settle_batch_no }}</div>
                </el-form-item>
                <el-form-item :label="t('是否优先到账')" v-if="huifuInfo.settle_is_priority_receipt">
                    <div>{{ huifuInfo.settle_is_priority_receipt }}</div>
                </el-form-item>
                <el-form-item :label="t('自定义结算处理时间')" v-if="huifuInfo.settle_time">
                    <div>{{ huifuInfo.settle_time }}</div>
                </el-form-item>
                <el-form-item :label="t('结算周期')" v-if="huifuInfo.settle_cycle">
                    <div>{{ huifuInfo.settle_cycle }}</div>
                </el-form-item>
                <el-form-item :label="t('结算方式')" v-if="huifuInfo.settle_pattern">
                    <div>{{ huifuInfo.settle_pattern }}</div>
                </el-form-item>
                <el-form-item :label="t('手续费内外扣标记')" v-if="huifuInfo.out_settle_flag">
                    <div>{{ huifuInfo.out_settle_flag }}</div>
                </el-form-item>
                <el-form-item :label="t('手续费外扣商户号')" v-if="huifuInfo.out_settle_huifuid">
                    <div>{{ huifuInfo.out_settle_huifuid }}</div>
                </el-form-item>
                <el-form-item :label="t('手续费外扣账户类型')" v-if="huifuInfo.out_settle_acct_type">
                    <div>{{ huifuInfo.out_settle_acct_type }}</div>
                </el-form-item>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { getHuifuInfo } from '@/addon/mall_huifu/api/site/config'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const huifuInfo = ref({})
const loading = ref(false)

const getHuifuInfoFn = () => {
    loading.value = true
    getHuifuInfo().then(res => {
        huifuInfo.value = res.data
        loading.value = false
    })
}
getHuifuInfoFn()
</script>

<style lang="scss" scoped>

</style>