<template>
    <div>
        <!-- 充值设置 -->
        <el-card class="box-card !border-none main-container" shadow="never">
            <div class="flex justify-between items-center mb-[10px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-if="!loading">
                <el-card class="box-card !border-none" shadow="never">
                    <el-form-item label="开关" prop="is_use">
                        <div class="flex items-center mx-[5px]">
                            <el-switch v-model="formData.is_use" :active-value="1" :inactive-value="0" />
                        </div>
                    </el-form-item>
                    <el-form-item label="自动取消订单" prop="close_length">
                        <div>
                            <p class="!text-sm">
                                <span>提交订单</span>
                                <el-input v-model.trim="formData.close_length" class="!w-[120px] mx-[10px]" @keyup="filterNumber($event)" clearable />
                                <span>分钟未付款</span>
                            </p>
                            <p class="text-[12px] text-[#a9a9a9] leading-normal  mt-[5px]">订单取消时间必须在10-1440分钟之间</p>
                        </div>
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
import { useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'
import { setVipcardConfig, getVipcardConfig } from '@/app/api/vip_card'
import { filterNumber } from '@/utils/common'

const route = useRoute()
const pageName = route.meta.title

const formData = ref({
    is_use: 1,
    recharge_explain: '',
    min_price: '',
    close_length: ''
})
const loading = ref(true)
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
        close_length: [
            { validator: validCloseLength, trigger: 'blur' }
        ]
    }
})

const validCloseLength = (rule:any, value:any, callback:Function) => {
    if (value) {
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

const getVipcardConfigFn = () => {
    loading.value = true
    getVipcardConfig().then((res: any) => {
        formData.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getVipcardConfigFn()

/** ** 提交 ****/
const onSave = async () => {
    await formRef.value?.validate(async(valid) => {
        if (valid) {
            setVipcardConfig(formData.value).then(() => {
                getVipcardConfigFn()
            }).catch(() => {
            })
        }
    })
}
</script>

<style lang="scss" scoped>
.input-width-short{
    width: 150px;
}
</style>
