<template>
     <view class="empty-page relative">
        <image class="img" :src="img('static/resource/images/system/empty.png')" mode="aspectFill" />
        <view class="desc">{{ props.desc }}</view>
    </view>
</template>
<script setup lang="ts">
import { img } from '@/utils/common';
const props = defineProps({
    desc: {
        type: String,
        default: '暂无数据'
    }
});
</script>