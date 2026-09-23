<template>
    <div class="w-screen h-screen flex flex-col items-center justify-center">
        <el-empty :description="t('noSite')" :image="img('static/resource/images/site/close.png')" image-size="300px" />
    </div>
</template>

<script lang="ts" setup>
import useSystemStore from '@/stores/system'
definePageMeta({
    layout: "container"
});
useSystemStore().getSiteInfoFn(() =>{
    navigateTo('/', { replace: true })
})
</script>

<style lang="scss" scoped></style>