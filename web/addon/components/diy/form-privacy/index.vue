<template>
    <div @touchmove.prevent.stop>
        <el-dialog v-model="formDetailPrivacyPop" :before-close="closeFn" :round="8">
            <div class="w-[285px]  popup-common center">
                <div class="text-center my-5">{{ t('diyForm.tips') }}<br />{{ data }}</div>
                <div class="flex justify-between">
                    <button class="w-[50%] h-[50px] rounded-[0px] leading-[50px] !bg-[transform] border-solid border-[0] border-t-[1px] border-[#e6e6e6] !text-[#333]" @click="call"> {{ t('diyForm.call') }}</button>
                    <button class="w-[50%] h-[50px] rounded-[0px] border-solid border-[0] border-t-[1px]  border-l-[1px] bo border-[#e6e6e6] leading-[50px] !bg-[transform] !text-[var(--primary-color)]" @click="closeFn">{{ t('diyForm.know') }}</button>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';


const props = defineProps(['data']);

const data = computed(() => {
    let str = props.data || '已开启隐私保护，提交后会部分打码，只有你自己和管理员才能查看完整信息';
    return str;
})

const formDetailPrivacyPop = ref(false);
const open = () => {
    formDetailPrivacyPop.value = true
}
const closeFn = () => {
    formDetailPrivacyPop.value = false
}
const call = () => {
    uni.makePhoneCall({
        phoneNumber: props.data
    });

}

defineExpose({
    open
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';
</style>
