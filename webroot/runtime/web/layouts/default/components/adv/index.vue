<template>
    <div class="min-w-[1200px]" v-if="webConfig && webConfig.is_show_top">
        <div class="relative">
            <el-image :src="img(webConfig.top_image)" @click="toLink" fit="cover" class="w-full h-auto box-border cursor-pointer slide-down-animate" />
            <div class="absolute top-[35px] right-[35px] py-[3px] px-[10px] leading-[20px] text-[14px] text-[#fff] border-[1px] border-[#fff] border-solid rounded-[24px] bg-[rgba(0,0,0,0.6)] text-[12px] flex-center cursor-pointer" @click="handleClose">
                <span v-if="webConfig.close_type == 'auto'">{{ seconds }}秒后</span>
                <span>关闭</span>
                <span class="iconfont icon-a-guanbi34"></span>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, watch } from 'vue'
import { img } from '@/utils/common'  
import { getWebConfig } from '@/app/api/index' 
import { useRouter } from 'vue-router';

const router = useRouter();
const webConfig = ref<any>(null)
const seconds = ref(0)
const timer = ref<any>(null)
// 封装广告显示/隐藏逻辑
const setAdVisible = (visible: boolean) => {
    if (webConfig.value) {
        webConfig.value.is_show_top = visible ? 1 : 0
    }
}
const clearTimer = () => {
    if (timer.value) {
        clearInterval(timer.value)
        timer.value = null
    }
}
const getWebConfigFn = () => {
    getWebConfig().then((res: any) => {
        webConfig.value = res.data
        if (!webConfig.value || !webConfig.value.is_show_top) {
            setAdVisible(false)
            return
        }
        if (webConfig.value.close_type == 'auto') {
            seconds.value = webConfig.value.close_time
            if (seconds.value > 0) {
                startCountdown()
            } else {
                // 关闭时间为0，直接隐藏
                handleClose()
            }
        }
    }).catch(() => { })
}

const toLink = () => {
    if (webConfig.value.top_link) {
        router.push(webConfig.value.top_link.url)
    }
}

const startCountdown = () => {
    clearTimer() // 先清理旧定时器
    timer.value = setInterval(() => {
        if (seconds.value > 0) {
            seconds.value--
        } else {
            handleClose()
        }
    }, 1000)
}

const handleClose = () => {
    setAdVisible(false)
    clearTimer()
}

watch(() => router.currentRoute.value.path,
    (newPath) => {
        clearTimer() // 路由切换先清理定时器
        if (newPath === '/') {
            getWebConfigFn()
        } else {
            setAdVisible(false)
        }
    },
    { immediate: true } // 初始化时执行一次
)


onUnmounted(() => {
    clearTimer()
})
</script>
<style lang="scss" scoped>

@keyframes slideDown {
    0% {
        opacity: 0; 
        transform: translateY(-100%); 
    }
    100% {
        opacity: 1; 
        transform: translateY(0); 
    }
}

// 应用动画类
.slide-down-animate {
    animation: slideDown 0.3s ease-out forwards; // 0.3秒动画时长，缓出效果
    opacity: 0;
    transform: translateY(-100%);
}

:deep(.el-image) {
    width: 100%;
    height: auto;
}
</style>