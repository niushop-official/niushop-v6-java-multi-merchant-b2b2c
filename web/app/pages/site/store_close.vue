<template>
    <div class="w-screen h-screen flex flex-col items-center justify-center">
        <div class="flex flex-col items-center justify-center">
            <el-image class="w-[300rpx]" :src="img('static/resource/images/site/close.png')" fit="contain" />
            <div class="text-[18px]">我的店铺 已打烊</div>
        </div>
        <div class="flex items-center text-[var(--el-color-primary)] mt-[50px] cursor-pointer" @click="toDetail">
            <span class="mr-[8px]">去往首页</span>
            <span class="iconfont icon-Frame-1"></span>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
definePageMeta({
    layout: "container"
});
const router = useRouter()
// 详情
const toDetail = () => {
	router.push(`/`)
}

</script>

<style lang="scss" scoped></style>