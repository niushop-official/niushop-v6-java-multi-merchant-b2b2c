<template>
    <view class="flex flex-wrap">
        <view v-for="(item, index) in images.data" :key="index" class="relative" :class="{'mr-[20rpx]': (index + 1) % 3 != 0 ,'mb-[20rpx]': maxCount > 1}">
            <up-image class="rounded-[10rpx] overflow-hidden" width="200rpx" height="200rpx" :src="img(item || '')" model="aspectFill" @click="imgListPreview(item)">
                <template #error>
                    <u-icon name="photo" color="#999" size="50"></u-icon>
                </template>
            </up-image>
            <view class="absolute top-0 right-0 bg-[#373737] flex justify-end h-[28rpx] w-[28rpx] rounded-bl-[40rpx]" @click.stop="deleteImg(index)">
                <text class="nc-iconfont nc-icon-guanbiV6xx !text-[20rpx] mt-[2rpx] mr-[2rpx] text-[#fff]"></text>
            </view>
            <view class="absolute top-0 left-0 bg-primary flex-center h-[28rpx] w-[54rpx] rounded-tl-[16rpx] rounded-br-[16rpx]  text-[18rpx] text-[#fff]" v-if="index == 0">封面</view>
        </view>
        <view class="w-[200rpx] h-[200rpx]" v-show="images.data.length < maxCount">
            <u-upload @afterRead="afterRead" :maxCount="maxCount" :multiple="prop.multiple">
                <view class="flex items-center justify-center w-[200rpx] h-[200rpx] border-[2rpx] border-dashed border-[#ddd] text-center text-[var(--text-color-light9)] rounded-[var(--goods-rounded-big)]">
                    <view>
                        <view class="nc-iconfont nc-icon-xiangjiV6xx text-[50rpx]"></view>
                        <view class="text-[24rpx] mt-[12rpx]">{{ title }}</view>
                    </view>
                </view>
            </u-upload>
        </view>

    </view>
</template>
<script lang="ts" setup>
import { reactive,toRaw, computed, watch } from 'vue';
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
    },
    title: {
        type: String,
        default: '点击上传'
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

const images: Record<string, any> = reactive({
    data: []
})

const setValue = () => {
    value.value = toRaw(images.data).toString()
}

watch(() => value.value, () => {
    images.data = [
        ...value.value.split(',').filter((item: string) => { return item })
    ]
    setValue()
}, { immediate: true })

const maxCount = computed(() => {
    return prop.maxCount
})
const afterRead = (event:any) => {
    if (prop.multiple) {
        event.file.forEach((file: any) => {
            upload({ file })
        })
    } else {
        upload(event)
    }
}

const upload = (event:any) => {
    if (images.data?.length >= maxCount.value) {
        uni.showToast({ title: `最多允许上传${maxCount.value}张图片`, icon: 'none' })
        return false
    }

    uploadImage({
        filePath: event.file.url,
        name: 'file'
    }).then((res: any) => {
        if (images.data?.length < maxCount.value){
            images.data.push(res.data.url)
        } 
        setValue()
    }).catch(() => {
    })
}

const deleteImg = (index:number)=>{
    images.data.splice(index, 1)
    setValue()
}

//预览图片
const imgListPreview = (item:any) => {
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
