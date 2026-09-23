<template>
    <view class="text-[26rpx]" :class="{'text-primary': sendSms.canGetCode.value, 'text-gray-300': !sendSms.canGetCode.value}" @click="handleSend">{{ sendSms.tips.value }}</view>
    <u-code :seconds="sendSms.seconds" :change-text="sendSms.changeText" ref="smsRef" @change="sendSms.codeChange"></u-code>
    <Verify @success='success' :mode="'pop'" :captchaType="configStore.login.captcha_type" ref="verifyRef" :imgSize="{width:'310px',height:'155px'}"></Verify>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { useSendSms } from '@/hooks/useSendSms'
import { useCaptcha } from '@/hooks/useCaptcha'
import { t } from '@/locale'
import Verify from '@/components/verify/verify.vue'
import useConfigStore from '@/stores/config'

const configStore = useConfigStore()


const prop = defineProps({
    mobile: String,
    type: String,
    isAgree: {
        type: Boolean,
        default: true
    },
    modelValue: {
        type: String,
        default: ''
    }
})

const emit = defineEmits(['update:modelValue'])
const value = computed({
    get() {
        return prop.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})

const verifyRef: any = ref(null)
const smsRef: any = ref(null)
const sendSms = useSendSms(smsRef)
const show = ref(false)

const formData: any = reactive({
    mobile: '',
    captcha_code: '',
    captcha_key: '',
    type: prop.type,
    captcha_type: ''
})

const captcha = useCaptcha(formData)

const handleSend = async() => {
    if (smsRef.value.canGetCode) {
        formData.mobile = prop.mobile
        if (!prop.isAgree) {
            uni.showToast({ title: t('isAgreeTips'), icon: 'none' });
            return
        }
        if (uni.$u.test.isEmpty(formData.mobile)) {
            uni.showToast({ title: t('mobilePlaceholder'), icon: 'none' });
            return
        }
        if (!uni.$u.test.mobile(formData.mobile)) {
            uni.showToast({ title: t('mobileError'), icon: 'none' });
            return
        }

        verifyRef.value.show()
    }
}

const success = async (data: any) => {
    verifyRef.value.close()
    formData.captcha_code = data.captchaVerification
    formData.captcha_key = data.captcha_key
    formData.captcha_type = configStore.login.captcha_type
    const sendRes = await sendSms.send(formData)
    if (sendRes) {
        value.value = sendRes
    }
}
</script>

<style>
</style>