<template>
    <div  class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <el-page-header :content="package_id ? t('editRecharge') : t('addRecharge')" :icon="ArrowLeft" @back="back()" />
        </el-card>

        <el-card class="box-card mt-[15px] !border-none" shadow="never" v-loading="loading">
            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form">
                <el-form-item :label="t('rechargeName')" prop="package_name">
                    <el-input v-model.trim="formData.package_name" clearable :placeholder="t('namePlaceholder')" class="input-width" maxlength="10" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('price')" prop="price">
                    <el-input v-model.trim="formData.price" clearable placeholder="0.00" class="input-width-short" maxlength="5">
                        <template #append>{{ t('yuan') }}</template>
                    </el-input>
                </el-form-item>
                <el-form-item :label="t('giveMoney')" prop="give_money">
                    <el-input v-model.trim="formData.give_money" clearable placeholder="0.00" class="input-width-short" maxlength="5">
                        <template #append>{{ t('yuan') }}</template>
                    </el-input>
                </el-form-item>
                <div>
                    <package-gift v-if="!loading" ref="giftRef" v-model="formData.gift_json"/>
                </div>
                <el-form-item :label="t('sort')" prop="sort">
                    <el-input v-model.number="formData.sort" clearable placeholder="0" class="input-width-short" maxlength="8" @keyup="filterNumber($event)" @blur="formData.sort = $event.target.value" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('status')" prop="status">
                    <el-switch v-model="formData.status" class="input-width" :active-value="1" :inactive-value="0" />
                </el-form-item>
            </el-form>

        </el-card>

        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="save()">{{ t("save") }}</el-button>
                <el-button @click="back()">{{ t("cancel") }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, computed, reactive } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { FormInstance } from 'element-plus'
import { filterNumber } from '@/utils/common'
import { addRechargePackage, editRechargePackage, getRechargePackageInfo } from '@/app/api/recharge'
import packageGift from '@/app/views/recharge/package/components/package-gift.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const packageId = route.query.package_id
const formData = reactive({
    package_id: 0,
    package_name: '',
    price: '',
    give_money: '',
    sort: '',
    status: 1,
    gift_json: {}
})
const giftRef = ref(null)
const formRef = ref<FormInstance>()

// 正则表达式
const regExp: any = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}

const formRules = computed(() => {
    return {
        package_name: [{ required: true, message: t('namePlaceholder'), trigger: 'blur' }],
        price: [{
            required: true,
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (value === null || value === '') {
                    callback(t('pricePlaceholder'))
                } else if (isNaN(value) || !regExp.digit.test(value)) {
                    callback(t('limitTips'))
                } else if (value <= 0) {
                    callback(t('priceMustBeGreaterThanZero'))
                } else {
                    callback()
                }
            }
        }],
        give_money: [{
            trigger: 'blur',
            validator: (rule: any, value: any, callback: any) => {
                if (isNaN(value) || !regExp.digit.test(value)) {
                    callback(t('limitTips'))
                } else if (value <= 0) {
                    callback(t('giveMoneyMustBeGreaterThanZero'))
                } else {
                    callback()
                }
            }
        }]
    }
})

const getRechargePackageInfoFn = () => {
    if (packageId) {
        // 获取详情
        loading.value = true
        const data = {
            package_id: Number(packageId)
        }
        getRechargePackageInfo(data).then((res) => {
            const data = res.data
            if (data) {
                Object.assign(formData, data)
                loading.value = false
            }
        })
    }
}

getRechargePackageInfoFn()

const save = async () => {
    // 触发表单校验
    await formRef.value?.validate(async (valid) => {
        if (valid) {
            if (!await giftRef.value?.verify()) return
            loading.value = true
            if (packageId) {
                formData.package_id = Number(packageId)
                editRechargePackage(formData).then((res) => {
                    loading.value = false
                    back()
                }).catch(() => {
                    loading.value = false
                })
            } else {
                addRechargePackage(formData).then((res) => {
                    loading.value = false
                    back()
                }).catch(() => {
                    loading.value = false
                })
            }
        }
    })
}

const back = () => {
    router.push('/recharge/package/list')
}
</script>

<style lang="scss" scoped>
.input-width-short{
    width: 190px;
}
</style>
