<template>
    <view class="bg-[var(--page-bg-color)] min-h-screen" :style="themeColor()">
        <mescroll-body ref="mescrollRef" @init="mescrollInit" :down="{ use: false }">
            <view v-if="info.length">
                <view class="px-[var(--sidebar-m)] pt-[var(--top-m)]">
                <template v-for="(item, index)  in info" :key="index">
                        <view class="card-template mb-[var(--top-m)]">
                            <view class="bg-[var(--temp-bg)] p-[20rpx] rounded-[var(--rounded-mid)] flex" @click="redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: item.goods_id } })">
                                <up-image radius="var(--goods-rounded-mid)" width="150rpx" height="150rpx" :src="img(item.order_goods.goods_image_thumb_mid ? item.order_goods.goods_image_thumb_mid : '')" model="aspectFill">
                                    <template #error>
                                        <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                    </template>
                                </up-image>
                                <view class="flex-1 flex flex-wrap ml-[20rpx] my-[4rpx]">
                                    <view>
                                        <view class="text-[26rpx] leading-[40rpx] max-w-[450rpx] truncate">{{ item.order_goods.goods_name }}</view>
                                        <view class="max-w-[450rpx] mt-[14rpx] truncate text-[22rpx] text-[var(--text-color-light9)] leading-[28rpx]" v-if="item.order_goods.sku_name">{{ item.order_goods.sku_name }}</view>
                                    </view>
                                    <view class="mt-auto w-full flex justify-between items-center">
                                        <view class="flex items-baseline  price-font">
                                            <text class="text-[24rpx] font-500">￥</text>
                                            <text class="text-[40rpx] font-500">{{ parseFloat(item.order_goods.price).toFixed(2).split('.')[0] }}</text>
                                            <text class="text-[24rpx] font-500">.{{ parseFloat(item.order_goods.price).toFixed(2).split('.')[1] }}</text>
                                        </view>
                                        <text class="font-400 text-[28rpx] text-[#333]">x{{ item.order_goods.num }}</text>
                                    </view>
                                </view>
                            </view>
                            <view class="flex items-center mt-[30rpx]">
                                <u-rate :count="5" v-model="item.scores" active-color="var(--primary-color)" :size="'36rpx'" gutter="1" readonly></u-rate>
                                <text class="ml-[16rpx] text-[28rpx] pt-[2rpx] text-[var(--primary-color)]">{{ item.scores === 1 ? '差评' : item.scores === 2 || item.scores === 3 ? '中评' : '好评' }}</text>
                            </view>
                            <view class="px-[2rpx] text-[28rpx] text-[#333] mt-[16rpx] mb-[20rpx] break-all leading-[1.5]">{{ item.content }}</view>
                            <template v-if="item.image_mid.length === 1">
                                <view class="mt-[10rpx]">
                                    <up-image  width="420rpx" height="420rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[0])" mode="aspectFill" @click="imgListPreview(item.images,0)">
                                        <template #error>
                                            <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                        </template>
                                    </up-image>
                                </view>
                            </template>
                            <template v-if="item.image_mid.length === 2">
                                <view class="flex justify-between mt-[10rpx]">
                                    <up-image width="322rpx" height="322rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[0])" mode="aspectFill" @click="imgListPreview(item.images,0)">
                                        <template #error>
                                            <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                        </template>
                                    </up-image>
                                    <up-image width="322rpx" height="322rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[1])" mode="aspectFill" @click="imgListPreview(item.images,1)">
                                        <template #error>
                                            <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                        </template>
                                    </up-image>
                                </view>
                            </template>
                            <template v-if="item.image_mid.length === 3">
                                <view class="flex justify-between mt-[10rpx]">
                                    <up-image width="430rpx" height="430rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[0])" mode="aspectFill" @click="imgListPreview(item.images,0)">
                                        <template #error>
                                            <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                        </template>
                                    </up-image>
                                    <view>
                                        <up-image width="205rpx" height="205rpx" :radius="'var(--goods-rounded-big)'"  :src="img(item.image_mid[1])" mode="aspectFill" @click="imgListPreview(item.images, 1)">
                                            <template #error>
                                                <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                            </template>
                                        </up-image>
                                        <view class="mt-[20rpx]">
                                            <up-image  width="205rpx" height="205rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[2])" mode="aspectFill" @click="imgListPreview(item.images, 2)">
                                                <template #error>
                                                    <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                                </template>
                                            </up-image>
                                        </view>
                                    </view>
                                </view>
                            </template>
                            <template v-if="item.image_mid.length === 4">
                                <view class="flex flex-wrap mt-[10rpx]">
                                    <up-image width="215rpx" height="215rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[0])" mode="aspectFill" @click="imgListPreview(item.images,0)">
                                        <template #error>
                                            <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                        </template>
                                    </up-image>
                                    <view class="mr-[15rpx] mb-[15rpx]">
                                        <up-image width="215rpx" height="215rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[1])" mode="aspectFill" @click="imgListPreview(item.images, 1)">
                                            <template #error>
                                                <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                            </template>
                                        </up-image>
                                    </view>
                                    <view class="mr-[15rpx]">
                                        <up-image width="215rpx" height="215rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[2])" mode="aspectFill" @click="imgListPreview(item.images,2)">
                                            <template #error>
                                                <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                            </template>
                                        </up-image>
                                    </view>
                                    <view class="mr-[15rpx]">
                                        <up-image width="215rpx" height="215rpx" :radius="'var(--goods-rounded-big)'" :src="img(item.image_mid[3])" mode="aspectFill" @click="imgListPreview(item.images, 3)">
                                            <template #error>
                                                <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                            </template>
                                        </up-image>
                                    </view>
                                </view>
                            </template>
                            <template v-if="item.image_mid.length > 4">
                                <view class="flex flex-wrap mt-[10rpx]">
                                    <view v-for="(imageItem, imageIndex) in item.image_mid" class="mb10" :class="{'mr10':(imageIndex + 1) % 3 != 0}">
                                        <up-image width="211rpx" height="211rpx" :src="img(imageItem)" mode="aspectFill" :radius="'var(--goods-rounded-big)'"  @click="imgListPreview(item.image_mid, imageIndex)">
                                            <template #error>
                                                <u-icon name="photo" color="var(--text-color-light9)" size="50"></u-icon>
                                            </template>
                                        </up-image>
                                    </view>
                                </view>
                            </template>
                            <view v-if="item.explain_first !=''" class="text-[26rpx] !text-[var(--text-color-light6)] mt-[20rpx] pt-[20rpx] border-0 border-t-[2rpx] border-solid border-[#F1F2F5] w-[100%] overflow-clip leading-[34rpx] break-all">
                                <text class="text-[var(--primary-color)]">商家回复：</text>
                                <text>{{ item.explain_first }}</text>
                            </view>
                        </view>
                    </template>
                </view>
                <view class="card-template sidebar-margin">
                    <view class="title">服务评分</view>
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">商品质量</view>
                        <u-rate :count="5" v-model="server.quality_score" active-color="var(--primary-color)" :size="'36rpx'" gutter="1" readonly></u-rate>
                    </view>
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">商家服务</view>
                        <u-rate :count="5" v-model="server.service_score" active-color="var(--primary-color)" :size="'36rpx'" gutter="1" readonly></u-rate>
                    </view>
                    <view class="card-template-item justify-between">
                        <view class="text-[28rpx]">发货物流</view>
                        <u-rate :count="5" v-model="server.logistics_score" active-color="var(--primary-color)" :size="'36rpx'" gutter="1" readonly></u-rate>
                    </view>
                </view>
            </view>
            <mescroll-empty v-if="!info.length && !loading" :option="{tip : '暂无评价'}"></mescroll-empty>
            <loading-page :loading="loading"></loading-page>
        </mescroll-body>
    </view>
</template>
<script lang="ts" setup>
import { ref } from 'vue';
import { getOrderEvaluate } from '@/addon/mall/api/evaluate';
import { onLoad, onUnload, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import { img, redirect, goback } from '@/utils/common';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const info = ref<Array<any>>([])
const server = ref({
    quality_score: 0,
    service_score: 0,
    logistics_score: 0,
})
const loading = ref(true)
onLoad((option: any) => {
    if (option.order_id) {
        getOrderEvaluateFn(option.order_id)
    } else {
        let parameter = {
            url: '/addon/mall/pages/evaluate/list',
            title: '缺少订单id'
        };
        goback(parameter)
    }
})
const getOrderEvaluateFn = (id: any) => {
    loading.value = true
    getOrderEvaluate(id).then((res: any) => {
        info.value = res.data
        server.value.quality_score = info.value[0].quality_score
        server.value.service_score = info.value[0].service_score
        server.value.logistics_score = info.value[0].logistics_score
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

//预览图片
const imgListPreview = (data: any, index: any) => {
    if (data.length === 0) return false
    var urlList = []
    urlList = data.map((item: any) =>{
        return img(item)
    })
    uni.previewImage({
        indicator: "number",
        current: index,
        loop: true,
        urls: urlList
    })
}

// 关闭预览图片
onUnload(() => {
    // #ifdef  H5 || APP
    try {
        uni.closePreviewImage()
    } catch (e) {

    }
    // #endif
})
</script>
<style lang="scss" scoped>
.mb10 {
    margin-bottom: 10rpx;
}
.mr10 {
    margin-right: 10rpx;
}
</style>
