<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)] overflow-hidden" :style="themeColor()">
        <view class="empty-page">
            <image class="img" :src="img('static/resource/images/site/close.png')" mode="aspectFit" />
            <view class="desc">{{ t('siteClose') }}</view>
        </view>
    </view>
</template>


<script setup lang="ts">
import { img } from '@/utils/common'
import { t } from '@/locale'
import { redirect } from '@/utils/common';
import { getSiteInfo } from '@/app/api/system';
import { onLoad } from '@dcloudio/uni-app';

onLoad(()=> {
    getSiteInfo().then(()=>{
        redirect({ url: '/app/pages/index/index' })
    })
})
</script>

<style lang="scss" scoped></style>