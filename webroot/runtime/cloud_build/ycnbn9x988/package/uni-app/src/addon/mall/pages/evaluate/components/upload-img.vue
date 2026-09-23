<template>
    <view class="flex flex-wrap">
        <view v-for="(item, index) in value" class="mb18 relative" :class="{'mr18': (index + 1) % 4 != 0 }">
            <up-image  width="140rpx" height="140rpx" radius="var(--goods-rounded-big)" :src="img(item || '')" mode="aspectFill" @click="imgListPreview(item)">
                <template #error>
                    <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                </template>
            </up-image>
            <view class="absolute top-0 right-[0] bg-[#373737] flex justify-end h-[28rpx] w-[28rpx] rounded-bl-[40rpx]" @click.stop="deleteImg(index)">
                <text class="nc-iconfont nc-icon-guanbiV6xx !text-[20rpx] mt-[2rpx] mr-[2rpx] text-[#fff]"></text>
            </view>
        </view>
        <view class="w-[140rpx] h-[140rpx]" v-show="value.length < maxCount">
            <u-upload @afterRead="afterRead" :maxCount="maxCount" :multiple="prop.multiple">
                <view class="flex items-center justify-center w-[140rpx] h-[140rpx] border-[2rpx] border-dashed border-[#ddd] text-center text-[var(--text-color-light9)] rounded-[var(--goods-rounded-big)]">
                    <view>
                        <view class="nc-iconfont nc-icon-xiangjiV6xx text-[50rpx]"></view>
                        <view class="text-[24rpx] mt-[12rpx]">{{ value.length }}/{{ maxCount }}</view>
                    </view>
                </view>
            </u-upload>
        </view>
    </view>
</template>
<script lang="ts" setup>
import { computed } from 'vue';
import { img } from '@/utils/common';
import { uploadImage } from '@/app/api/system'

const prop = defineProps({
    modelValue: {
        type: String || Array,
    },
    maxCount: {
        type: Number,
        default: 9
    },
    multiple: {
        type: Boolean,
        default: false
    }
})
const emit = defineEmits(['update:modelValue'])
const value: any = computed({
    get() {
        return prop.modelValue
    },
    set(value) {
        emit('update:modelValue', value)
    }
})
const maxCount = computed(() => {
    return prop.maxCount
})
const afterRead = (event: any) => {
    if (prop.multiple) {
        event.file.forEach((file: any) => {
            upload({ file })
        })
    } else {
        upload(event)
    }
}

const upload = (event: any) => {
    if (value.value?.length >= maxCount.value) {
        uni.showToast({ title: `最多允许上传${ maxCount.value }张图片`, icon: 'none' })
        return false
    }

    uploadImage({
        filePath: event.file.url,
        name: 'file'
    }).then(res => {
        if (value.value?.length < maxCount.value) value.value.push(res.data.url)
    }).catch(() => {
    })
}

const deleteImg = (index: number) => {
    value.value.splice(index, 1)
}

//预览图片
const imgListPreview = (item: any) => {
    if (item === '') return false
    var urlList = []
    urlList.push(img(item))  //push中的参数为 :src="item.img_url" 中的图片地址
    uni.previewImage({
        indicator: "number",
        loop: true,
        urls: urlList
    })
}
</script>
<style lang="scss" scoped>
.mb18 {
    margin-bottom: 18rpx;
}
.mr18 {
    margin-right: 18rpx;
}
</style>
