<template>
    <el-container class="w-screen h-screen">
        <el-main class="p-0 min-w-[1200px]" id="main">
            <layout-adv />
            <el-header class="z-10 sticky top-0">
                <layout-header />
                <layout-nav />
            </el-header>
            <slot></slot>
            <layout-footer />
            <layout-fixed />
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import layoutHeader from './default/components/header/index.vue'
import layoutFooter from './default/components/footer/index.vue'
import layoutNav from './default/components/nav/index.vue'
import layoutFixed from './default/components/fiexd/index.vue'
import layoutAdv from './default/components/adv/index.vue'
import {watch, nextTick} from 'vue'
import {useRouter} from 'vue-router';

const router = useRouter();
watch(() => router.currentRoute.value.path, (newValue, oldValue) => {
    nextTick(() => {
        if (document.getElementById('main')) {
            document.getElementById('main').scrollTop = 0
        }
    })
}, {immediate: true})
</script>

<style lang="scss" scoped>
.el-header {
    --el-header-padding: 0;
    height: 188px;
}

.el-main {
    --el-main-padding: 0;
}
</style>
