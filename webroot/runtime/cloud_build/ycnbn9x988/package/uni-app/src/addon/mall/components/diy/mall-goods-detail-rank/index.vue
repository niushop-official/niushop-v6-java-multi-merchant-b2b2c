<template>
    <view :style="warpCss" class="overflow-hidden" v-if="diyComponent && diyComponent.rank">
        <view class="px-[24rpx] py-[20rpx] overflow-hidden flex items-center justify-between" @click="toLink">
            <view class="flex items-center">
                <image  class="max-w-[102rpx] h-[40rpx] mr-[10rpx]" :src="img(diyComponent.rank.img)" mode="heightFix" />
                <text class="text-[26rpx] text-[#333]">{{ goodsRank.name }}·第{{ goodsRank.rank_num }}名</text>
            </view>
            <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light6)] ml-[8rpx]"></text>
        </view>
    </view>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue';
import { img, redirect } from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
import useGoodsDetailStore from '@/addon/mall/stores/goodsDetail'
import { getGoodsRank } from '@/addon/mall/api/goods';

const props = defineProps(['component', 'index', 'value']);
const diyStore = useDiyStore();
const emits = defineEmits(['update:componentIsShow']); //商品数据加载完成之后触发
const diyComponent = computed(() => {
    if (diyStore.mode == 'decorate') {
        goodsRank.value = {
            name: '肉类美食爆款榜',
            rank_num: 6
        }
        return Object.assign({},diyStore.value[props.index]);
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
                if (newValue && newValue.componentName == 'MallGoodsDetailRank') {
                    nextTick(() => {})
                }
            }
        )
    } else {
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                if (newValue && newValue.componentName == 'MallGoodsDetailRank') {
                    if (!diyComponent.value.isShow) {
                        emits('update:componentIsShow', false)
                    } else if (diyComponent.value.goods_id) {
                        getGoodsRankFn()
                    }
                }
            },
            { immediate: true }
        )
    }
});

const goodsRank = ref<any>({})
const getGoodsRankFn = () => {
    getGoodsRank({ goods_id: diyComponent.value.goods_id }).then((res: any) => {
        if (Array.isArray(res.data) && res.data.length === 0) {
            emits('update:componentIsShow', false)
            return
        }
        goodsRank.value = res.data
        emits('update:componentIsShow', true)
    })
}

const toLink = () => {
    redirect({
        url: '/addon/mall/pages/goods/rank'
    })
}
</script>
<style lang="scss" scoped>
.card-template{
    background-color: transparent !important;
    border-radius: 0 !important;
    padding: 30rpx 0 0;
    .title{
       padding: 0 24rpx; 
    }
}
</style>
