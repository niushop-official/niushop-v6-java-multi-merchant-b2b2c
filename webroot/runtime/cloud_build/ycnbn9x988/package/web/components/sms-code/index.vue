<template>
    <div class="h-[30px] leading-[28px]">
        <el-button type="primary" link class="!text-[12px]" :disabled="!sendSms.canGetCode.value" @click="handleClick">{{ sendSms.text.value }}</el-button>
    </div>
</template>

<script lang="ts" setup>
import useConfigStore from '@/stores/config'
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
    get() {
        return prop.modelValue
    },
    set(value) {
        emits('update:modelValue', value)
    }
})

const configStore = useConfigStore()
const loading = ref(false)
const formData = reactive<any>({
    mobile: '',
    captcha_code: '',
    captcha_key: '',
    type: prop.type
})

const sendSms = useSendSms()

const send = async (data: any, callback: any = null) => {
    formData.captcha_code = data.captcha_code
    formData.captcha_type = configStore.login.captcha_type
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
    send
})
</script>

<style lang="scss" scoped></style>