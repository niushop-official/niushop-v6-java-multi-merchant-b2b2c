<template>
    <div @touchmove.prevent.stop>
        <el-dialog v-model="formPrivacyPop" :before-close="closeFn" zIndex="500" :round="8">
            <div class="flex flex-col items-center w-[620px] pt-[25px]">
                <div class="text-[16px] font-bold">{{ t('diyForm.prompt') }}</div>
                <div class="text-center px-[20px] py-[15px] leading-[1.5] min-h-[45px]">{{ data }}</div>
                <div class="flex items-center justify-center border-solid border-[0] border-t-[1px] border-[#e6e6e6] w-[100%] h-[45px] text-[14px]">
                    <span @click="closeFn">{{ t('diyForm.know') }}</span>
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

const formPrivacyPop = ref(false);
const open = () => {
    formPrivacyPop.value = true
}
const closeFn = () => {
    formPrivacyPop.value = false
}

defineExpose({
    open
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';
</style>
