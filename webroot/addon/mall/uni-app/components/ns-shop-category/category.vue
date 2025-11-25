<template>
	<view :style="themeColor()">
        <category-template-one  />
    </view>
</template>

<script setup lang="ts">
import { onLoad, onShow } from '@dcloudio/uni-app'
import categoryTemplateOne from '@/addon/mall/components/ns-shop-category/category-template-one.vue';

</script>