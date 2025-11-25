<template>
    <view :style="warpCss" class="overflow-hidden" v-if="diyComponent && diyComponent.shop_info && Object.keys(diyComponent.shop_info).length">
        <view class="card-template" v-if="systemStore.auditFlag">
            <view class="flex justify-between items-center">
                <view class="flex flex-1">
                    <up-image class="overflow-hidden" width="86rpx" height="86rpx" radius="6rpx" :src="diyShop.siteLogo(diyComponent.shop_info)" model="aspectFill">
                        <template #error>
                            <image :src="img('addon/mall/shop/shop_default.png')" class="w-[86rpx] h-[86rpx] align-middle"></image>
                        </template>
                    </up-image>
                    <view class="ml-[20rpx] flex flex-col justify-center">
                        <view class="flex items-center">
                            <text class="text-[28rpx] text-[#282828] font-600 leading-[38rpx] max-w-[360rpx] truncate">{{ diyShop.siteName(diyComponent.shop_info) }}</text>
                            <text class="ml-[8rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] rounded-[4rpx] px-[4rpx] leading-[28rpx]" v-if="diyComponent.shop_info.is_self">{{diyComponent.shop_info.is_self ? '自营' : ''}}</text>
                        </view>
                        <view class="text-[22rpx] text-[var(--text-color-light6)] mt-[8rpx] leading-[30rpx]">{{diyComponent.shop_info.follow_number}}人关注</view>
                    </view>
                </view>
                <view class="w-[114rpx] h-[50rpx] leading-[50rpx] bg-[var(--primary-color)] text-[#fff] text-center rounded-[25rpx] text-[28rpx]" @click="toShopDetail(diyComponent.shop_info.site_id)">进店</view>
            </view>
        </view>
    </view>
</template>

<script setup lang="ts">
import { computed, watch, onMounted, nextTick } from 'vue';
import { img, redirect } from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
import useSystemStore from '@/stores/system'
import { useShop } from '@/addon/mall/hooks/useShop'
import useGoodsDetailStore from '@/addon/mall/stores/goodsDetail'

const props = defineProps(['component', 'index', 'value']);
const diyStore = useDiyStore();
const emits = defineEmits(['update:componentIsShow']); //商品数据加载完成之后触发
const diyShop = useShop();

const  systemStore = useSystemStore() // 审核

const diyComponent = computed(() => {
    if (diyStore.mode == 'decorate') {
        const obj = {
            shop_info: {
                front_end_logo: '',
                site_name: '店铺名称',
                is_self: 0,
                follow_number: 10
            }
        }
        systemStore.auditFlag = true;
        return Object.assign({},obj,diyStore.value[props.index]);
    } else {
        return Object.assign({}, props.component, useGoodsDetailStore().goodsDetail);
    }
})

const warpCss = computed(() => {
    let style = '';
    style += 'position:relative;';
    if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${ diyComponent.value.componentGradientAngle },${ diyComponent.value.componentStartBgColor },${ diyComponent.value.componentEndBgColor });`;
    else style += 'background-color:' + (diyComponent.value.componentStartBgColor || diyComponent.value.componentEndBgColor) + ';';

    if (diyComponent.value.componentBgUrl) {
        style += `background-image:url('${ img(diyComponent.value.componentBgUrl) }');`;
        style += 'background-size: cover;background-repeat: no-repeat;';
    }

    if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    return style;
})

onMounted(() => {
    // 装修模式下刷新
    if (diyStore.mode == 'decorate') {
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                if (newValue && newValue.componentName == 'MallGoodsDetailShopInfo') {
                    nextTick(() => {})
                }
            }
        )
    } else {
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                if (newValue && newValue.componentName == 'MallGoodsDetailShopInfo') {
                    if (!diyComponent.value.isShow) {
                        emits('update:componentIsShow', false)   
                    }else{
                        emits('update:componentIsShow', true)
                    }
                }
            },
            { immediate: true }
        )
    }
});

//进入店铺
const toShopDetail = (site_id:any) => {
	redirect({ url: '/addon/mall/pages/index', param: { site_id } })
}
</script>
<style lang="scss" scoped>
.card-template{
    background-color: transparent !important;
    border-radius: 0 !important;
}
</style>
