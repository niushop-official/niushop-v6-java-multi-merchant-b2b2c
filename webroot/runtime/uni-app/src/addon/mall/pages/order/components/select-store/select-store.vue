<template>
    <u-popup :show="show" @close="handleClose" mode="bottom" :round="10">
        <view @touchmove.prevent.stop class="popup-common">
            <view class="title">请选择门店</view>
            <scroll-view scroll-y="true" class="h-[50vh]">
                <view class="p-[var(--popup-sidebar-m)] pt-0 text-sm">
                    <view class="card-template mt-[var(--top-m)] border-1 border-[#eee] border-solid  mb-[var(--top-m)]"
                        :class="{'!border-primary bg-[var(--primary-color-light2)]': store && store.store_id == item.store_id}"
                        v-for="item in storeList" @click="selectStore(item)">
                        <view class="flex items-center mb-[20rpx]">
                            <view class="flex-1 using-hidden text-[30rpx] text-[#333]">{{ item.store_name }} </view>
                            <view v-if="item.hasOwnProperty('distance')" class="flex-shrink-0 ml-[20rpx] text-[#999] text-[24rpx] font-normal">{{ distance(item.distance) }}</view>
                        </view>
                        <view class="flex items-center justify-between">
                            <view class="flex-1">
                                <view class=" text-[22rpx] leading-[1.4] text-[#666]">
                                    <text class="nc-iconfont nc-icon-dizhiguanliV6xx text-[26rpx] mr-[12rpx]"></text>
                                    <text class="flex-shrink-0">门店地址：</text>
                                    <text>{{ item.full_address }}</text>
                                </view>
                                <view class="mt-[12rpx] text-[22rpx] text-[#666]">
                                    <text class="nc-iconfont nc-icon-shijianV6xx text-[26rpx] mr-[12rpx]"></text>
                                    <text>营业时间：{{ item.trade_time }}</text>
                                </view>
                            </view>
                            <view class="w-[1rpx] h-[42rpx] bg-[#ddd] mx-[24rpx]"></view>
                            <view class="flex-shrink-0">
                                <image @click="callFn(item)" class="w-[42rpx] h-[42rpx] mr-[20rpx]" :src="img('addon/mall/payment/phone.png')" mode="heightFix" />
                                <image @click="getAddress(item)" class="w-[42rpx] h-[42rpx]" :src="img('addon/mall/payment/navigation.png')" mode="heightFix" />
                            </view>
                        </view>
                    </view>
                </view>
                <view class="h-[50vh] flex items-center flex-col justify-center" v-if="loading">
                    <u-loading-icon :vertical="true"></u-loading-icon>
                </view>
                <view class="h-[95%] flex items-center flex-col justify-center" v-if="!loading && !storeList.length">
                    <u-empty text="没有可选择的门店" width="214" :icon="img('static/resource/images/empty.png')" />
                </view>
            </scroll-view>
            <view class="btn-wrap">
                <button class="primary-btn-bg btn" @click="confirm">确认</button>
            </view>
        </view>
    </u-popup>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getDeliveryStoreList } from '@/app/api/basic'
import { img, isWeixinBrowser, distance } from '@/utils/common'
import wechat from '@/utils/wechat'

const show = ref(false)
const loading = ref(true)
const storeList = ref<object[]>([])
const store = ref<null | object>(null)
const initParam = ref<any>({})

// 位置
// #ifdef H5
if (isWeixinBrowser()) {
    wechat.init();
}
// #endif
const getAddress = (data: any) => {
    // #ifdef H5
    if (isWeixinBrowser()) {
        wechat.openLocation({
            latitude: Number(data.latitude),
            longitude: Number(data.longitude),
            name: data.full_address,
            address: data.full_address
        })
    }
    // #endif
    // #ifdef MP
	uni.openLocation({
		latitude: Number(data.latitude),
		longitude: Number(data.longitude),
        name: data.full_address,
        address: data.full_address,
		success: function () { }
	});
    // #endif
}

const callFn = (data: any) => {
    uni.makePhoneCall({
        phoneNumber: data.mobile,
        success: (res: any) => {
        },
        fail: (res: any) => {
        }
    })
}


const open = (data: any) => {
    initParam.value.site_id = data.site_id
    initParam.value.delivery_type = data.delivery_type
    storeList.value = data.store_list || []
    store.value = data.take_store || storeList.value[0] || null
    loading.value = false
    show.value = true
}

const selectStore = (data: object) => {
    if (store.value) {
        store.value = store.value.store_id != data.store_id ? data : null
    } else {
        store.value = data
    }
}
const handleClose = () => {
    store.value = null
	show.value = false
}

const emits = defineEmits(['confirm'])

const confirm = () => {
    if (!store.value) {
        const title = initParam.value.delivery_type == 'local_delivery'? '请选择配送门店' : '请选择自提点'
        uni.showToast({ title, icon: 'none' })
        return
    }
    store.value.site_id = initParam.value.site_id
    emits('confirm', store.value)
    store.value = null
    show.value = false
}

const distanceFormat = (distance: any) => {
    distance = parseFloat(distance)
    if (distance < 1000) {
        return `${ parseInt(distance) }m`;
    } else {
        const km = (distance / 1000).toFixed(3)
        return `${ km }km`;
    }
}

defineExpose({
    open
})
</script>

<style lang="scss" scoped></style>
