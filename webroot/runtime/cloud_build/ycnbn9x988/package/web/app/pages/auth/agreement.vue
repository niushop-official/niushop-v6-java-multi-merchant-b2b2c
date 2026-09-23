<template>
    <div class="ml-[20px] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div>
            <div>
                <template v-if="agreement">
                    <div  v-if="agreement.title && agreement.content">
                        <h2 class="text-center">{{ agreement.title }}</h2>
                        <div class="content-wrapper" v-html="agreement.content"></div>
                    </div>
                    <el-empty 
                        :description="t('protocolNotConfigured')"  
                        :image-size="200" 
                        :image="img('static/resource/images/system/empty.png')" 
                        v-else 
                    />
                </template>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { getAgreementInfo } from '@/app/api/system'
import { useRoute } from 'vue-router';

const agreement = ref<any | null>(null)
const route = useRoute()

// 监听路由参数变化，获取协议内容
watch(() => route.query.key, (newVal) => {
    if (route.query.key) {
        getAgreementInfo(route.query.key)
            .then(({ data }) => {
                agreement.value = data
            })
            .catch(err => {
                console.error('获取协议信息失败:', err)
            })
    }
}, { immediate: true })
</script>

<style lang="scss" scoped>
::v-deep(span) {
	text-wrap-mode:wrap !important;
	display:inline-block;
	max-width:960px;
}
</style>
    