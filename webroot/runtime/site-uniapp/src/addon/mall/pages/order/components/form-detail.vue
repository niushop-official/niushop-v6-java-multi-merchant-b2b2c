<template>
    <view  @touchmove.prevent.stop>
        <u-popup :show="show" @close="show = false" mode="bottom" :round="10">
            <view @touchmove.prevent.stop class="popup-common flex flex-col">
                <view class="title">表单详情</view>
                <scroll-view :scroll-y="true" class="h-[50vh] card-template">
                    <view class="justify-between card-template-item" v-for="(field, fieldKey) in tableData[0]?.recordsFieldList || []" :key="fieldKey">
                        <view class="text-[28rpx]">{{ field.field_name }}</view>
                        <view class="text-[28rpx]">{{ tableData[0]?.recordsFieldList[fieldKey].render_value }}</view>
                    </view>
                </scroll-view>
            </view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'
import { getFormRecordsInfo } from '@/app/api/diy_form'

const loading = ref(true)
const show = ref(false)
const tableData = ref<any>([])
const open = (data: any) => {
    loading.value = true
    getFormRecordsInfo(data).then((res: any) => {
        tableData.value = [res.data];
        loading.value = false
        show.value = true
    }).catch(() => {
        loading.value = false
    })
    
}

defineExpose({
    open
})
</script>

<style scoped>

</style>