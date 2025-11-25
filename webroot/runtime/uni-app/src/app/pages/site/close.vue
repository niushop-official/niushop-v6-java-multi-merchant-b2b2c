<template>
    <view class="min-h-[100vh] overflow-hidden" :style="themeColor()">
        <view class="content mt-[200rpx]">
            <image class="img" :src="img('static/resource/images/site/close.png')" mode="widthFix" />
            <view class="title">{{ t('storeTitle') }}</view>
        </view>
        <view class="to-link" @click="redirect({ url: '/app/pages/index/index' })">
            <text>{{ t('toIndex') }}</text>
            <text class="nc-iconfont nc-icon-xiangyouV6xx-1 ml-[20rpx]"></text>
        </view>
    </view>
</template>

<script setup lang="ts">
import { img, redirect } from '@/utils/common'
import { t } from '@/locale'
</script>

<style lang="scss" scoped>
.content{
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    .img{
        width: 400rpx;
    }
    .title{
        margin-top: 24rpx;       
        font-size: 36rpx;
    }
}
.to-link{
    font-size: 28rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: 120rpx;
    color: var(--primary-color);
}
</style>