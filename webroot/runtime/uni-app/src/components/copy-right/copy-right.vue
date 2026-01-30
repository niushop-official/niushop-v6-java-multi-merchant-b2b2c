<template>
    <view>
        <view class="flex flex-col justify-center items-center p-[30rpx]" :style="{ color:textColor}" v-if="systemStore.copyright?.icp || systemStore.copyright?.gov_record">
            <view class="text-[22rpx]" v-if="systemStore.copyright?.icp">
                备案号：{{ systemStore.copyright.icp }}
            </view>
            <view class="text-[22rpx] flex items-center mt-[20rpx]" v-if="systemStore.copyright?.gov_record" @click="systemStore.copyright?.gov_url && redirect({ url: systemStore.copyright.gov_url})">
                <img :src="img('static/resource/images/copy_right.png')" mode="heightFix" class="w-[28rpx] h-[28rpx] mr-[10rpx]" />
                {{ systemStore.copyright.gov_record }}
            </view>
        </view>
    </view>
</template>

<script setup lang="ts"> 
import { ref, nextTick } from 'vue'
import { redirect,img } from '@/utils/common'
import useSystemStore from '@/stores/system';
const systemStore = useSystemStore()


const prop = defineProps({
    textColor: {
        type: String,
        default: '#ccc'
    },
})

</script>

<style scoped lang="scss">

</style>