<template>
    <el-container class="w-screen h-screen">
        <el-main class="p-0 min-w-[1200px]">
            <el-header class="z-10 sticky top-0">
                <layout-header />
            </el-header>
            <layout-nav />
            <slot></slot>
            <layout-footer />
            <layout-fixed />
        </el-main>
    </el-container>
</template>

<script lang="ts" setup>
import layoutHeader from './default/components/header/index.vue'
import layoutFooter from './default/components/footer/index.vue'
import layoutNav from './default/components/nav/shop.vue'
import layoutFixed from './default/components/fiexd/index.vue'
</script>

<style lang="scss" scoped>
.el-header {
    --el-header-padding: 0;
    height: 140px;
}

.el-main {
    --el-main-padding: 0;
}
</style>
