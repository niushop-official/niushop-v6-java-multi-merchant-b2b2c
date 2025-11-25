<template>
    <view :style="themeColor()">
        <view class="w-screen h-screen flex flex-col items-center">
            <view class="flex-1 flex flex-col items-center w-full pt-[180rpx]">
                <text class="nc-iconfont nc-icon-duihaoV6mm text-[var(--primary-color)] mb-[30rpx] !text-[65rpx]"></text>
                <view class="px-[30rpx] text-center leading-[1.3] text-[35rpx] font-bold mb-[30rpx]">登录成功</view>
            </view>
           <view class="pb-[260rpx] action-wrap" v-if="status == 1">
                <button class="w-[380rpx] !border-0 h-[80rpx] text-[28rpx] !text-[#ffffff] !bg-[var(--primary-color)] flex-center font-500 rounded-[6rpx]" :plain="true" @click="finishFn">访问首页</button>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getFormResultInfo } from '@/app/api/diy_form';
import { geth5Status } from '@/app/api/auth';
import { t } from '@/locale'
import { redirect } from '@/utils/common'
const status = ref(0)
onLoad((data: any) => {
	geth5Status().then((res)=>{
		status.value = res.data.is_open
	})
})


const finishFn = () => {
    redirect({ url: '/app/pages/index/index'})
}
</script>

<style lang="scss" scoped>
.action-wrap {
    button {
        margin-bottom: 30rpx;

        &:last-child {
            margin-bottom: 0;
        }
    }
}
</style>
