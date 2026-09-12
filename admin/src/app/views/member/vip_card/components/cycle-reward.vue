<template>
    <el-form ref="formRef" :model="formData" label-width="100px" :rules="formRules" class="page-form">
        <el-form-item label="权益图标" prop="icon">
            <upload-image v-model="formData.icon" :limit="1" />
        </el-form-item>
        <el-form-item label="权益名称" prop="name">
            <el-input v-model.tirm="formData.name" clearable maxlength="20" placeholder="请输入权益名称" class="input-width" />
        </el-form-item>
        <el-form-item label="权益简介" prop="desc">
            <el-input v-model.tirm="formData.desc" clearable maxlength="200" placeholder="请输入权益说简介" class="input-width" />
        </el-form-item>
        <el-form-item label="是否启用" prop="status">
            <el-radio-group v-model="formData.status">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="规则设置" prop="item_list">
            <div class="el-form-item__content">
                <el-button class="mt-[5px]" type="primary" link @click="addItem()">添加规则</el-button>
                <div v-for="(item, index) in formData.item_list"  :key="index" class="w-[100%]">
                    <div class="level-header bg-[#f5f5f5]  flex pl-[10px] justify-between mb-[18px] mt-[25px]">
                        <div class="font-bold text-[#333]">规则{{ index + 1 }}</div>
                        <span class="cursor-pointer text-primary mr-[10px]" v-if="formData.item_list.length > 1 && index !== 0" @click="deleteRow(index)">删除</span>
                    </div>
                    <el-form-item label="规则名称" :prop="`item_list[${index}].name`" :rules="[{ required: true, message: '请输入规则名称', trigger: 'blur' }]" class="!mb-[18px]">
                        <el-input v-model.trim="item.name" clearable placeholder="请输入规则名称" class="input-width" maxlength="60" />
                    </el-form-item>
                    <el-form-item label="周期" :prop="`item_list[${index}].benefits_config.time`" class="!mb-[18px]"
                        :rules="[{
                                    trigger: 'blur',
                                    validator: (rule: any, value: any, callback: any) => {
                                        if(item.benefits_config.time_type == 'month'){
                                            if (item.benefits_config.time_month == '') {
                                                callback('请输入天数')
                                            } else if (item.benefits_config.time_month < 0) {
                                                callback('天数不能小于0')
                                            } else if (!Test.digits(item.benefits_config.time_month)){
                                                callback('天数格式错误')
                                            } else if (item.benefits_config.time_month > 31) {
                                                callback('天数不能大于31')
                                            } else {
                                                callback()
                                            }
                                        } else if(item.benefits_config.time_type == 'day') {
                                            if (item.benefits_config.time_day == '') {
                                                callback('请输入天数')
                                            } else if (item.benefits_config.time_day < 0) {
                                                callback('天数不能小于0')
                                            } else if (!Test.digits(item.benefits_config.time_day)){
                                                callback('天数格式错误')
                                            } else {
                                                callback()
                                            }
                                        } else {
                                            callback()
                                        }
                                    }
                                }]">
                        <div>
                            <el-radio-group v-model="item.benefits_config.time_type">
                                <el-radio label="month">
                                    <div class="flex items-center">
                                        <span class="ml-[10px] el-form-item__label">每月</span>
                                        <div class="w-[100px]">
                                            <el-input v-model.trim="item.benefits_config.time_month" clearable />
                                        </div>
                                        <span class="ml-[15px] el-form-item__label">号赠送以下礼品</span>
                                    </div>
                                </el-radio>
                                <el-radio label="week">
                                    <div class="flex items-center">
                                        <span class="ml-[10px] el-form-item__label">每</span>
                                        <div class="w-[100px]">
                                            <el-select v-model="item.benefits_config.time_week" placeholder="请选择" style="width: 100px;">
                                                <el-option  label="周一" :value="1" />
                                                <el-option  label="周二" :value="2" />
                                                <el-option  label="周三" :value="3" />
                                                <el-option  label="周四" :value="4" />
                                                <el-option  label="周五" :value="5" />
                                                <el-option  label="周六" :value="6" />
                                                <el-option  label="周日" :value="7" />
                                            </el-select>
                                        </div>
                                        <span class="ml-[15px] el-form-item__label">赠送以下礼品</span>
                                    </div>
                                </el-radio>
                                <el-radio label="day">
                                    <div class="flex items-center">
                                        <span class="ml-[10px] el-form-item__label">每隔</span>
                                        <div class="w-[100px]">
                                            <el-input v-model.trim="item.benefits_config.time_day" clearable />
                                        </div>
                                        <span class="ml-[15px] el-form-item__label">天赠送以下礼品</span>
                                    </div>
                                </el-radio>
                            </el-radio-group>
                        </div>
                    </el-form-item>
                    <div class="pl-[100px] item-wrap">
                        <member-gift ref="giftRef" v-model="item.benefits_config.gifts" />
                    </div>
                </div>
            </div>
        </el-form-item>
        <el-form-item label="权益说明" prop="explain">
            <div class="flex-1">
            <editor v-model="formData.explain" :height="300"  class="editor-width" />
            </div>
        </el-form-item>
    </el-form>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import memberGift from '@/app/views/member/components/member-gift.vue'
import Test from '@/utils/test'

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => {
            return {}
        }
    }
})
const emits = defineEmits(['update:modelValue'])

const formData = ref<any>({
    icon: '',
    name: '',
    desc: '',
    status: 0,
    item_list: [{
        name: '',
        benefits_config: {
            time_month: '',
            time_week: '',
            time_day: '',
            time_type: 'day',
            gifts: {
                balance: {
                    is_use: 0,
                    money: ''
                },
                point: {
                    is_use: 0,
                    num: ''
                },
                shop_coupon: {
                    is_use: 0,
                    coupon_id: [],
                    coupon_list: {}
                }
            }
        }
    }],
    explain: ''
})

const formRef = ref(null)
const giftRef = ref(null)

const addItem = () => {
    formData.value.item_list.push({
        name: '',
        benefits_config: {
            time_month: '',
            time_week: '',
            time_day: '',
            gifts: {
                balance: {
                    is_use: 0,
                    money: ''
                },
                point: {
                    is_use: 0,
                    num: ''
                },
                shop_coupon: {
                    is_use: 0,
                    coupon_id: [],
                    coupon_list: {}
                }
            }
        }
    })
}

const deleteRow = (index: number) => {
    formData.value.item_list.splice(index, 1)
}

const value = computed({
    get () {
        return props.modelValue
    },
    set (value) {
        emits('update:modelValue', value)
    }
})

watch(() => value.value, (nval, oval) => {
    if ((!oval || !Object.keys(oval).length) && Object.keys(nval).length) {
        Object.assign(formData.value, value.value)
    }
}, { immediate: true })

watch(() => formData.value, () => {
    value.value = formData.value
}, { deep: true })

const formRules = computed(() => {
    return {
        icon: [
            {
                required: true,
                message: '请上传图标',
                trigger: 'blur'
            }
        ],
        name: [
            {
                required: true,
                message: '请输入权益名称',
                trigger: 'blur'
            }
        ],
        status: [
            {
                required: true,
                message: '请选择是否启用',
                trigger: 'blur'
            }
        ]
    }
})
const verify = async () => {
    let verify = true
    await formRef.value?.validate((valid) => {
        verify = valid
    })
    if (!verify) return verify

    const refs = Array.isArray(giftRef.value) ? giftRef.value : (giftRef.value ? [giftRef.value] : [])
    for (let i = 0; i < refs.length; i++) {
        if (!await refs[i].verify()) {
            verify = false
            break
        }
    }
    return verify
}

defineExpose({
    verify
})
</script>

<style  lang="scss" scoped>
.item-wrap :deep(.el-form-item){
    margin-bottom: 18px !important;
}
:deep(.el-radio-group){
    display: flex;
    flex-direction: column;
    gap: 18px;
    align-items: flex-start;
}
</style>
