<template>
    <!-- 商品服务 -->
    <view :style="warpCss" class="overflow-hidden relative" v-if="diyComponent && diyComponent.goods && Object.keys(diyComponent.goods).length">
        <view class="card-template" v-if="serviceConfig && serviceConfig.length && isShowTemplate">
            <view @click="servicesDataShow = !servicesDataShow" v-if="diyComponent.service && diyComponent.service.length && serviceConfig.includes('goods_service')" class="card-template-item">
                <text class="text-[#333] text-[26rpx] leading-[30rpx] font-400 flex-shrink-0">服务</text>
                <view class="text-[#343434] text-[26rpx] leading-[30rpx] font-400 truncate ml-auto">{{ diyComponent.service[0].service_name }}</view>
                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light6)] ml-[8rpx]"></text>
            </view>
            <view @click="buyFn" v-if="diyComponent.goodsSpec && diyComponent.goodsSpec.length && serviceConfig.includes('spec_select')" class="card-template-item">
                <text class="text-[#333] text-[26rpx] leading-[30rpx] font-400 flex-shrink-0 mr-[20rpx]">已选</text>
                <view class="ml-auto text-right truncate flex-1 text-[#343434] text-[26rpx] leading-[30rpx] font-400">{{ diyComponent.sku_spec_format }}</view>
                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light6)] ml-[8rpx]"></text>
            </view>
            <view class="card-template-item" @click="distributionDataOpen" v-if="diyComponent.goods.goods_type == 'real' && diyComponent.goods.delivery_type_list && diyComponent.goods.delivery_type_list.length && serviceConfig.includes('delivery_info')">
                <text class="text-[#333] text-[26rpx] leading-[30rpx] font-400 flex-shrink-0">配送</text>
                <view class="ml-auto flex items-center text-[#343434] text-[26rpx] leading-[30rpx] font-400">{{ diyComponent.goods.delivery_type_name }}</view>
                <text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light6)] ml-[8rpx]"></text>
            </view>
        </view>
        <!-- 服务 -->
        <view @touchmove.prevent.stop>
            <u-popup class="popup-type" :show="servicesDataShow" @close="servicesDataShow = false">
                <view class="min-h-[480rpx] popup-common" @touchmove.prevent.stop>
                    <view class="title">商品服务</view>
                    <scroll-view class="h-[520rpx]" scroll-y="true">
                        <view class="pl-[22rpx] pb-[28rpx] pr-[37rpx]">
                            <view class="flex mb-[28rpx]" v-for="(item, index) in diyComponent.service">
                                <image class="mt-[4rpx] w-[32rpx] h-[32rpx] mr-[14rpx]" :src="img(item.image || 'addon/mall/icon_service.png')" mode="aspectFit" />
                                <view class="flex-1">
                                    <view class="text-[30rpx] leading-[36rpx] text-[#333] mb-[12rpx]">{{ item.service_name }}</view>
                                    <view class="text-[24rpx] leading-[36rpx] text-[var(--text-color-light9)]">{{ item.desc }}</view>
                                </view>
                            </view>
                        </view>
                    </scroll-view>
                </view>
            </u-popup>
        </view>
        <!-- 配送 -->
        <view @touchmove.prevent.stop>
            <u-popup class="popup-type" :show="distributionDataShow" @close="distributionDataShow = false">
                <view class="min-h-[360rpx] popup-common" @touchmove.prevent.stop>
                    <view class="title">配送方式</view>
                    <scroll-view class="h-[520rpx]" scroll-y="true">
                        <view class="px-[var(--popup-sidebar-m)]">
                            <view class="flex mb-[40rpx]" v-for="(item, index) in diyComponent.goods.delivery_type_list">
                                <image class="mt-[4rpx] w-[32rpx] h-[32rpx] mr-[14rpx]" :src="img('addon/mall/icon_service.png')" mode="aspectFit" />
                                <view class="flex-1">
                                    <view class="text-[30rpx] leading-[36rpx] text-[#333] mb-[8rpx]">{{ item.name }}</view>
                                    <view class="text-[24rpx] leading-[36rpx] text-[var(--text-color-light9)]">{{ item.desc }}</view>
                                </view>
                            </view>
                        </view>
                    </scroll-view>
                </view>
            </u-popup>
        </view>
        <!-- 装修时，防止点击 -->
        <view v-if="diyStore.mode == 'decorate'" class="absolute z-10 top-0 right-0 bottom-0 left-0"></view>
    </view>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted, nextTick } from 'vue';
import { img , redirect} from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
import useMemberStore from '@/stores/member'
import useGoodsDetailStore from '@/addon/mall/stores/goodsDetail'


const props = defineProps(['component', 'index', 'value', 'global']);
const diyStore = useDiyStore();
const emits = defineEmits(['update:componentIsShow']); //商品数据加载完成之后触发

const servicesDataShow = ref<boolean>(false)
const distributionDataShow = ref<boolean>(false) //配送


// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

const diyComponent = computed(() => {
    if (diyStore.mode == 'decorate') {
        const obj = {
            service: [{service_name: '24小时配送'}],
            goods: {
                delivery_type_list: [
                    {
                        name: '物流配送'
                    }
                ]
            },
            goodsSpec: ['红色', '小'],
            sku_spec_format: '红色,小',
        }
        return Object.assign({}, obj, diyStore.value[props.index]);
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

// 判断商品属性模块是否展示
const isShowTemplate = ref(false);
const isGoodsPropertyTemp = () => {
    isShowTemplate.value = false;
    if (
        (diyComponent.value.service && diyComponent.value.service.length && serviceConfig.value.includes('goods_service')) ||
        (diyComponent.value.goodsSpec && diyComponent.value.goodsSpec.length && serviceConfig.value.includes('spec_select')) ||
        (diyComponent.value.goods.goods_type == 'real' && diyComponent.value.goods.delivery_type_list && diyComponent.value.goods.delivery_type_list.length && serviceConfig.value.includes('delivery_info')) || diyStore.mode == 'decorate'
        ) {
        isShowTemplate.value = true;
    }
    emits('update:componentIsShow', isShowTemplate.value)
}



/************ 选择配送方式-start ****************/
const selectDeliveryType = ref(0);
const distributionDataOpen = (() => {
    distributionDataShow.value = true;
});
const distributionListFn = ((data: any, index: any) => {
    selectDeliveryType.value = index;
    distributionDataShow.value = false;
    uni.setStorageSync('distributionType', data.name);
});
/************ 选择配送方式-end ****************/

const buyFn = (type: any) => {
    useGoodsDetailStore().setGoodsDetail({isOpenSkuBuy: true, skuBuyType: type});
}

onMounted(() => {
    refresh()
    // 装修模式下刷新
    if (diyStore.mode == 'decorate') {
        isGoodsPropertyTemp()
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                if (newValue && newValue.componentName == 'MallGoodsDetailPurchaseService') {
                    nextTick(() => {
                        refresh()
                    })
                }
            }
        )
    } else {
        isGoodsPropertyTemp()
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                if (newValue && newValue.componentName == 'MallGoodsDetailPurchaseService') {
                    emits('update:componentIsShow', isShowTemplate.value)
                }
            },
            { immediate: true }
        )
    }
});

const serviceConfig = ref([])
const refresh = () =>{
    serviceConfig.value = (typeof diyComponent.value?.serviceConfig == 'object') ? diyComponent.value?.serviceConfig : diyComponent.value?.serviceConfig.split(',');
}
</script>
<style lang="scss" scoped>
.card-template{
    background-color: transparent !important;
    border-radius: 0 !important;
}
</style>
