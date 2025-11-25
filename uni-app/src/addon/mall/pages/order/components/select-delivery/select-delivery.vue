<template>
    <view @touchmove.prevent.stop>
        <u-popup :show="show" @close="show = false">
            <view class="popup-common" @touchmove.prevent.stop>
                <view class="title">配送方式</view>
                <scroll-view scroll-y="true" class="h-[450rpx] px-[30rpx] box-border">
                    <template v-if="deliveryTypeList.length">
                        <u-radio-group v-model="deliveryType" placement="column" iconPlacement="right">
                            <u-radio activeColor="var(--primary-color)" :labelSize="'30rpx'" labelColor="#333" :customStyle="{marginBottom: '34rpx'}" v-for="(item, index) in deliveryTypeList" :key="index" :label="item.name" :name="item.key"></u-radio>
                        </u-radio-group>
                    </template>
                    <view  class="empty-page-popup !mt-0" v-else>
                        <image class="img" :src="img('static/resource/images/system/empty.png')" model="aspectFit" />
                        <view class="desc">暂无配送方式</view>
                    </view>
                </scroll-view>
                <view class="btn-wrap" v-if="deliveryTypeList.length">
                    <button class="primary-btn-bg btn" @click="save">确定</button>
                </view>
            </view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { img } from '@/utils/common';
import { cloneDeep } from 'lodash-es'

const show = ref(false)
const siteId = ref(0)

// 配送方式
const deliveryTypeList = ref([])
const deliveryType = ref('')
const deliveryTypeName = ref('')

const emit = defineEmits(['confirm'])

const save = () => {
    const data = {
        site_id: siteId.value,
        delivery_type: deliveryType.value
    }
    emit('confirm', data)
    show.value = false
}

const open = (data:any) => {
    siteId.value = data.site_id
    deliveryType.value = data.delivery.delivery_type
    if (data.delivery.delivery_type_list) {
        deliveryTypeList.value = cloneDeep(Object.values(data.delivery.delivery_type_list))
    }
    show.value = true
}

defineExpose({
    open
})
</script>

<style scoped>

</style>
