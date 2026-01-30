<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="95" ref="formRef" :rules="rules" class="page-form" v-loading="loading">
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="text-[14px] pl-[15px]">{{ t('closeOrderInfo') }}</h3>
                    <el-form-item prop="close_length">
                        <div>
                            <el-radio-group class="mx-[10px]" v-model="formData.is_close">
                                <el-radio :label="1">{{ t('isEvaluateOpen') }}</el-radio>
                                <el-radio :label="2">{{ t('isEvaluateClose') }}</el-radio>
                            </el-radio-group>
                            <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">{{ t('closeOrderCancelInfoBottom') }}</p>
                            <div v-if="formData.is_close == 1">
                                <p class="!text-sm mt-[5px]">
                                    <span>{{ t('closeOrderInfoLeft') }}</span>
                                    <el-input v-model="formData.close_length" class="!w-[120px] mx-[10px]" onkeyup="this.value = this.value.replace(/[^\d]/g,'');" clearable />
                                    <span>{{ t('closeOrderInfoRight') }}</span>
                                </p>
                                <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">{{ t('closeOrderInfoBottom') }}</p>
                            </div>
                        </div>
                    </el-form-item>
                </el-card>
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="text-[14px] pl-[15px]">{{ t('confirm') }}</h3>
                    <el-form-item prop="finish_length">
                        <div>
                            <el-radio-group class="mx-[10px]" v-model="formData.is_finish">
                                <el-radio :label="1">{{ t('isEvaluateOpen') }}</el-radio>
                                <el-radio :label="2">{{ t('isEvaluateClose') }}</el-radio>
                            </el-radio-group>
                            <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">{{ t('confirmCancelBottom') }}</p>
                            <div v-if="formData.is_finish == 1">
                                <p class="!text-sm mt-[5px]">
                                    <span>{{ t('confirmLeft') }}</span>
                                    <el-input v-model="formData.finish_length" class="!w-[120px] mx-[10px]" onkeyup="this.value = this.value.replace(/[^\d]/g,'');" clearable />
                                    <span>{{ t('confirmRight') }}</span>
                                </p>
                                <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">{{ t('confirmBottom') }}</p>
                            </div>
                        </div>
                    </el-form-item>
                </el-card>
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="text-[14px] pl-[15px]">是否启用延时结算</h3>
                    <el-form-item prop="delay_days">
                        <div>
                            <el-radio-group class="mx-[10px]" v-model="formData.is_delay_spear">
                                <el-radio :label="1">开启</el-radio>
                                <el-radio :label="0">关闭</el-radio>
                            </el-radio-group>
                            <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">关闭后订单完成后将即时结算</p>
                            <div v-if="formData.is_delay_spear == 1">
                                <p class="!text-sm mt-[5px]">
                                    <span>订单收货完成后</span>
                                    <el-input v-model="formData.delay_days" class="!w-[120px] mx-[10px]" onkeyup="this.value = this.value.replace(/[^\d]/g,'');" clearable />
                                    <span>天,自动结算</span>
                                </p>
                                <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">时间必须在1-15天之间，订单开始结算后将自动取消订单退款业务</p>
                            </div>
                        </div>
                    </el-form-item>
                </el-card>
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="text-[14px] pl-[15px]">{{ t('evaluate') }}</h3>
                    <el-form-item>
                        <span>{{ t('isEvaluate') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.is_evaluate">
                            <el-radio :label="1">{{ t('isEvaluateOpen') }}</el-radio>
                            <el-radio :label="0">{{ t('isEvaluateClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item>
                        <span>{{ t('evaluateIsToExamine') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.evaluate_is_to_examine">
                            <el-radio :label="1">{{ t('isEvaluateOpen') }}</el-radio>
                            <el-radio :label="0">{{ t('isEvaluateClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item>
                        <span>{{ t('isSelfEvaluate') }}</span>
                        <el-radio-group class="mx-[10px]" v-model="formData.is_self_evaluate">
                            <el-radio :label="1">{{ t('isSelfEvaluateOpen') }}</el-radio>
                            <el-radio :label="0">{{ t('isSelfEvaluateClose') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-card>
                <el-card class="box-card !border-none" shadow="never">
                    <h3 class="text-[14px] pl-[15px]">自动好评</h3>
                    <el-form-item prop="good_evaluate_days">
                        <div>
                            <el-radio-group class="mx-[10px]" v-model="formData.is_auto_good_evaluate">
                                <el-radio :label="1">开启</el-radio>
                                <el-radio :label="0">关闭</el-radio>
                            </el-radio-group>
                            <div v-if="formData.is_auto_good_evaluate == 1">
                                <p class="!text-sm mt-[5px]">
                                    <span>订单完成</span>
                                    <el-input v-model="formData.good_evaluate_days" class="!w-[120px] mx-[10px]" onkeyup="this.value = this.value.replace(/[^\d]/g,'');" clearable />
                                    <span>天,后自动好评</span>
                                </p>
                                <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">自动好评时间必须在1-30天之间</p>
                            </div>
                        </div>
                    </el-form-item>
                </el-card>
            </el-form>

        </el-card>
        <div class="fixed-footer-wrap" v-if="!loading">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { getConfig, setConfig } from '@/addon/mall/api/admin/order'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const formData = ref({
    close_length: 10,
    finish_length: 1,
    is_close: 1,
    is_finish: 1,
    is_delay_spear: 2,
    delay_days: 1,
    is_evaluate: 1,
    evaluate_is_to_examine: 0,
    evaluate_is_show: 1,
    is_self_evaluate: 0,
    is_auto_good_evaluate: 1,
    good_evaluate_days: 1
})
const validCloseLength = (rule: any, value: any, callback: Function) => {
    if (formData.value.is_close != 2) {
        if (value == '') {
            return callback(new Error(t('CloseLengthPlaceholder')))
        } else if (Number(value) >= 10 && Number(value) <= 1440) {
            return callback()
        } else {
            return callback(new Error(t('closeOrderInfoBottom')))
        }
    } else {
        return callback()
    }
}
const validFinishLength = (rule: any, value: any, callback: Function) => {
    if (formData.value.is_finish != 2) {
        if (value == '') {
            return callback(new Error(t('finishLengthPlaceholder')))
        } else if (Number(value) >= 1 && Number(value) <= 30) {
            return callback()
        } else {
            return callback(new Error(t('confirmBottom')))
        }
    } else {
        return callback()
    }
}
const validDelayDays = (rule: any, value: any, callback: Function) => {
    if (formData.value.is_delay_spear == 1) {
        if (value == '') {
            return callback(new Error(t('delayDaysPlaceholder')))
        } else if (Number(value) >= 1 && Number(value) <= 15) {
            return callback()
        } else {
            return callback(new Error(t('delayDaysBottom')))
        }
    } else {
        return callback()
    }
}
const validGoodEvaluateDays = (rule: any, value: any, callback: Function) => {
    if (formData.value.is_auto_good_evaluate == 1) {
        if (value == '') {
            return callback(new Error('请输入自动好评时间'))
        } else if (Number(value) >= 1 && Number(value) <= 30) {
            return callback()
        } else {
            return callback(new Error('自动好评时间必须在1-30天之间'))
        }
    } else {
        return callback()
    }
}
const rules = reactive({
    close_length: [
        { validator: validCloseLength, trigger: 'blur' }
    ],
    finish_length: [
        { validator: validFinishLength, trigger: 'blur' }
    ],
    delay_days: [
        { validator: validDelayDays, trigger: 'blur' }
    ],
    good_evaluate_days: [
        { validator: validGoodEvaluateDays, trigger: 'blur' }
    ]
})
const loading = ref(false)
const getConfigFn = () => {
    loading.value = true
    getConfig().then(res => {
        Object.values(res.data).forEach(el => {
            formData.value = Object.assign(formData.value, el)
        })
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getConfigFn()
const formRef = ref()

const onSave = async (formEl: any) => {
    await formEl.validate(async (valid: any) => {
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
<style lang="scss" scoped></style>
