<template>
    <view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden" :style="themeColor()">
        <web-view :src="url"></web-view>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { diyRedirect } from '@/utils/common'
import { getDeliveryTrack } from '@/app/api/basic'
const orderNo = ref('')
const deliveryService = ref('')
const url = ref('')

onLoad((option: any) => {
    orderNo.value = option.order_no || ''
    deliveryService.value = option.delivery_service || ''
    getDeliveryTrackFn()
})


const getDeliveryTrackFn = () => {
    getDeliveryTrack({
        order_no: orderNo.value,
        delivery_service: deliveryService.value
    }).then((res: any) => {
        url.value = res.data.url
        // diyRedirect(obj)
    })

}

</script>

<style>

</style>