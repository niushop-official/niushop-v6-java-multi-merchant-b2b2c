<template>
    <div class="h-[30px]">
        <el-button type="primary" link :disabled="!sendSms.canGetCode.value" @click="handleClick">{{ sendSms.text.value }}</el-button>
    </div>

</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { useSendSms } from '@/hooks/useSendSms'
const prop = defineProps({
    mobile: String,
    type: {
        type: String,
        default: ''
    },
    modelValue: {
        type: String,
        default: ''
    }
})

const emits = defineEmits(['update:modelValue', 'click'])

const value = computed({
    get () {
        return prop.modelValue
    },
    set (value) {
        emits('update:modelValue', value)
    }
})

const loading = ref(false)
const formData = reactive<any>({
    mobile: '',
    captcha_code: '',
    captcha_key: '',
    type: prop.type
})

const sendSms = useSendSms()

const handleConfirm = async (data: any, callback: any = null) => {
    formData.captcha_code = data.captcha_code
    formData.mobile = prop.mobile
    const sendRes = await sendSms.send(formData)
    if (sendRes) {
        value.value = sendRes
        loading.value = false
    } else if (sendRes === false) {
        loading.value = false
    }
    if (callback) callback()
}

const handleClick = () => {
    emits('click')
}

defineExpose({
    handleConfirm
})
</script>

<style lang="scss" scoped></style>