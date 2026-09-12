<template>
    <el-dialog v-model="showDialog" :title="title || t('赠送优惠券')"  width="850px" :destroy-on-close="true">
        <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading" @submit.enter.prevent>

            <el-form-item :label="t('优惠券')" prop="coupon_data">
                <coupon-select-popup ref="couponSelectPopupRef" v-model="formData.couponIds" @couponSelect="couponSelect" :min="1" :max="10" :status="1" />
            </el-form-item>
            <el-form-item class="mt-[15px]" v-if="formData.coupon_data && formData.coupon_data.length">
            <div class="coupon_list w-[800px]" >
                <el-table :data="formData.coupon_data" size="large" max-height="400" :row-style="{height: '80px'}">
                <el-table-column prop="title" :label="t('优惠券名称')" min-width="120">
                    <template #default="{ row }">
                        <div>{{ row.title }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="type_name" :label="t('优惠券类型')" min-width="100">
                    <template #default="{ row }">
                        <div>{{ row.type_name }}</div>
                    </template>
                </el-table-column>
                <el-table-column prop="price" :label="t('优惠券面值')" min-width="100">
                    <template #default="{ row }">
                        <div>￥{{ row.price }}</div>
                    </template>
                </el-table-column>
                <!-- <el-table-column  :label="t('useThreshold')" min-width="130" >
                    <template #default="{ row }">
                        <span v-if="row.min_condition_money == '0.00'">无门槛</span>
                        <span v-else >满{{ row.min_condition_money }}元可用</span>
                    </template>
                </el-table-column>
                <el-table-column  :label="t('termOfValidity')" min-width="210">
                    <template #default="{ row }">
                        <span v-if="row.valid_type == 1">  领取之日起{{ row.length || '' }} 天内有效</span>
                        <span v-else> 使用截止时间至{{ row.valid_end_time || ''}} </span>
                    </template>
                </el-table-column> -->
                <el-table-column prop="num" :label="t('赠送数量')" min-width="120">
                    <template #default="{ row, $index }">
                        <el-form-item
                        :prop="`coupon_data[${$index}].num`" 
                        :rules="[{ required: true, trigger: 'blur', validator: (rule: any, value: any, callback: any) => {
                                if (value === null || value === '') {
                                    callback(t('请输入数量'))
                                }else if (isNaN(value) || !regExp.number.test(value)) {
                                    callback(t('格式不正确'))
                                }else if (value <= 0) {
                                    callback(t('数量必须大于0'))
                                }else {
                                    callback();
                                }
                            } }]">
                            <el-input v-model.number ="row.num" class="w-[70px]" maxlength="6" clearable>
                                <template #append>张</template>
                            </el-input>
                        </el-form-item>
                    </template>
                </el-table-column>
                <el-table-column :label="t('operation')" align="right" min-width="80">
                    <template #default="{ row, $index }">
                        <el-button type="primary" link @click="deleteCouponEvents(row)">{{ t("delete") }}</el-button>
                    </template>
                </el-table-column>
            </el-table>
            </div>

        </el-form-item>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>

    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { sendCoupon } from '@/app/api/marketing_admin'
import { img,deepClone } from '@/utils/common'
import couponSelectPopup from '@/app/views/admin/marketing/coupon/components/common-coupon-select-popup.vue'

const showDialog = ref(false)
const loading = ref(false)
const title = ref('')

const formData = ref({
    coupon_data:[],
    couponIds:[],
    member_ids:[]
})

const formRef = ref<FormInstance>()

const formRules = computed(() => {
    return {
        coupon_data:[
            { required: true, trigger: 'blur', validator: (rule: any, value: any, callback: any) => { 
                if (value.length === 0) {
                    callback(t('请选择优惠券'))
                }else {
                    callback()
                }
            } }
        ]
    }
})
// 正则表达式
const regExp: any = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}

const emit = defineEmits(['complete'])
// 选择优惠券
const couponSelect = (data: any) => {
    const arr = []
    for (const key in data) {
        const coupons: any = data[key]
        let coupon: any = {
            price: coupons.price,
            title: coupons.title,
            type_name: coupons.type_name,
            coupon_id: coupons.id,
            min_condition_money: coupons.min_condition_money,
            valid_type: coupons.valid_type,
            valid_end_time: coupons.valid_end_time,
            length: coupons.length,
            num: 1
        }

        if (formData.value.coupon_data.length) {
            formData.value.coupon_data.forEach((el: any) => {
                if (el.coupon_id == coupon.coupon_id) {
                    coupon = Object.assign(coupon, el)
                }
            })
        }
        arr.push(deepClone(coupon))
    }
    formData.value.coupon_data = arr

    if(formRef.value.validateField){
        formRef.value.validateField('coupon_data')
    }
}
const deleteCouponEvents = (row: any) => {
    const couponsIndex = formData.value.coupon_data.findIndex((el: any) => el.coupon_id === row.coupon_id)
    if (couponsIndex !== -1) {
        formData.value.coupon_data.splice(couponsIndex, 1)
    }

    const couponsIdIndex = formData.value.couponIds.indexOf(row.coupon_id)
    if (couponsIndex !== -1) {
        formData.value.couponIds.splice(couponsIdIndex, 1)
    }

    console.log(formData.value.couponIds)
}
/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            sendCoupon(formData.value).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const setFormData = async (data: any = null) => {
    formData.value.coupon_data = []
    formData.value.couponIds = []
    formData.value.member_ids = data.member_ids
    title.value = data.title
    showDialog.value = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped lang="scss">
.coupon_list :deep(.cell) {
    // min-height: 35px !important;
    overflow: initial !important;
}
.coupon_list :deep(.el-form-item__error ) {
    padding-top: 6px;
    left: 2px;
}
</style>