<template>
    <view :style="warpCss" class="overflow-hidden many-shop-list" v-if="shopList && Object.keys(shopList).length">
        <!-- 装修模式，可以辅助选中模块 -->
        <view v-if="diyStore.mode == 'decorate'" class="absolute left-[0] right-[0] top-[0] bottom-[0] z-10"></view>
        <!-- 店铺列表 -->
        <mescroll-body ref="mescrollRef"  @init="mescrollInit" :down="{ use: false }"  @up="getShopListFn">
            <view :class="{'mb-[20rpx]': (index + 1) != shopList.length }" :style="itemStyle" v-for="(item, index) in shopList" :key="index" @click="toSite(item)">
                <view class="p-[20rpx] box-border">
                    <view class="flex items-center">
                        <view class="w-[70rpx] h-[70rpx] rounded-[12rpx] overflow-hidden mr-[16rpx]">
                            <up-image radius="12rpx" width="70rpx" height="70rpx" :src="diyShop.siteLogo(item)" mode="aspectFill">
                                <template #error>
                                    <image :src="img('addon/mall/shop/shop_default.png')" class="w-[70rpx] h-[70rpx] rounded-[12rpx]"></image>
                                </template>
                            </up-image>
                        </view>
                        <view>
                            <view class="text-[28rpx] text-[#333] truncate max-w-[530rpx]" :style="{ color : diyComponent.siteNameStyle.color, fontWeight : diyComponent.siteNameStyle.fontWeight }" >{{ diyShop.siteName(item) }}</view>
                            <view class="text-[22rpx] text-[#666] mt-[6rpx]">
                                <text class="mr-[16rpx]">{{ item.category_name }}</text>
                                <text v-if="diyComponent.distance.isShow && item.distance">{{ distance(item.distance) }}</text>
                            </view>
                        </view>
                    </view>
                    <view class="gird-wrap box-border mt-[22rpx]" v-if="item.goods && item.goods.length">
                        <view class="bg-[#fff]" :style="itemCss" v-for="(subItem, subIndex) in item.goods" @click.stop="toLink(subItem)">
                            <up-image :width="style2Width"  :height="style2Width" :radius="imageRounded.val" :src="img(subItem.goods_cover || '')" model="aspectFill">
                                <template #error>
                                    <image :style="{'width': style2Width,'height': style2Width, 'border-radius': imageRounded.val}" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                                </template>
                            </up-image>
                            <view class="relative min-h-[40rpx] px-[10rpx] pt-[16rpx] pb-[20rpx]">
                                <view class="text-[24rpx] text-[#333] using-hidden mb-[10rpx] ">{{ subItem.goods_name }}</view>
                                <view class="text-[var(--price-text-color)] font-bold price-font block truncate max-w-[160rpx] leading-[1] overflow-hidden">
                                    <text class="text-[20rpx] font-400 mr-[2rpx]">￥</text>
                                    <text class="text-[32rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(subItem)).toFixed(2) }}</text>
                                </view>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
        </mescroll-body>    
    </view>
</template>

<script setup lang="ts">
// 品牌好店
import { ref, reactive, computed, watch, onMounted, nextTick } from 'vue';
import { redirect, img, distance } from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
import { getShopWebList } from '@/addon/mall/api/shop';
import { useGoods } from '@/addon/mall/hooks/useGoods'
import { useShop } from '@/addon/mall/hooks/useShop'
import useSystemStore from '@/stores/system'
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const systemStore = useSystemStore()

const diyGoods = useGoods();
const diyShop = useShop();

const props = defineProps(['component', 'index', 'value']);
const diyStore = useDiyStore();

const shopList = ref<Array<any>>([]);

const diyComponent = computed(() => {
    if (props.value) {
        return props.value;
    } else if (diyStore.mode == 'decorate') {
        return diyStore.value[props.index];
    } else {
        return props.component;
    }
})

const warpCss = computed(() => {
    var style = '';
    style += 'position:relative;';
    if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${ diyComponent.value.componentGradientAngle },${ diyComponent.value.componentStartBgColor },${ diyComponent.value.componentEndBgColor });`;
    else style += 'background-color:' + (diyComponent.value.componentStartBgColor || diyComponent.value.componentEndBgColor) + ';';

    if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    return style;
})

const imageRounded = computed(() => {
    var obj = {
        val: '',
        style: ''
    };
    if (diyComponent.value.imgElementRounded) {
        obj.val = diyComponent.value.imgElementRounded * 2 + 'rpx';
        obj.style += 'border-radius:' + diyComponent.value.imgElementRounded * 2 + 'rpx;';
    }
    return obj;
})

const itemStyle = computed(() => {
    var style = '';
    if (diyComponent.value.siteStyle.startBgColor && diyComponent.value.siteStyle.endBgColor) style += `background:linear-gradient(${ diyComponent.value.siteStyle.gradientAngle },${ diyComponent.value.siteStyle.startBgColor },${ diyComponent.value.siteStyle.endBgColor });`;
    else style += 'background-color:' + (diyComponent.value.siteStyle.startBgColor || diyComponent.value.siteStyle.endBgColor) + ';';
    
    if (diyComponent.value.siteStyle.bgUrl) {
        style += `background-image:url('${ img(diyComponent.value.siteStyle.bgUrl) }');`;
        style += 'background-size: cover;background-repeat: no-repeat;';
    }
    if (diyComponent.value.topElementRounded) style += 'border-top-left-radius:' + diyComponent.value.topElementRounded * 2 + 'rpx;';
    if (diyComponent.value.topElementRounded) style += 'border-top-right-radius:' + diyComponent.value.topElementRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomElementRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomElementRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomElementRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomElementRounded * 2 + 'rpx;';
    return style;
})

const itemCss = computed(() => {
	var style = '';
    style += 'border-radius:' + diyComponent.value.imgElementRounded * 2 + 'rpx;';
    if (diyComponent.value.style == 'style-2') {
        if (diyComponent.value.margin && diyComponent.value.margin.both) style += 'width: calc((100vw - ' + (diyComponent.value.margin.both * 4) + 'rpx - 32rpx - 40rpx) / 3);'
        else style += 'width: calc((100vw - 32rpx - 40rpx) / 3 );'
    }
    return style; 
})

const style2Width = computed(() => {
    var style = '';
    if (diyComponent.value.margin && diyComponent.value.margin.both) style += 'calc((100vw - ' + (diyComponent.value.margin.both * 4) + 'rpx - 32rpx - 40rpx) / 3)'
    else style += 'calc((100vw - 32rpx - 40rpx) / 3 )'
    return style;
})

onMounted(() => {
    refresh();
    // 装修模式下刷新
    if (diyStore.mode != 'decorate') {
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                refresh();
            },
            { deep: true }
        )
    }
});
watch(() => systemStore.diyAddressInfo,(newValue, oldValue) => {
    if(newValue){
        refresh();
    }
}, { deep: true })

const refresh = () => {
	// 装修模式下设置默认图
	if (diyStore.mode == 'decorate') {
		let obj = {
            site_name: "店铺名称",
            category_name:"分类名称",
            logo:"",
            goods_list:[{
                goods_name:"商品名称",
                goods_cover:"",
                goodsSku: { show_price: 100 }
            },{
                goods_name:"商品名称",
                goods_cover:"",
                goodsSku: { show_price: 100 }
            },{
                goods_name:"商品名称",
                goods_cover:"",
                goodsSku: { show_price: 100 }
            }]
		};
        if(diyComponent.value.distance.isShow){
            obj.distance = 100
        }
		shopList.value.push(obj);
		shopList.value.push(obj);
	} else {
        // 修复：初始化时手动构造默认的mescroll参数
        const defaultMescroll = {
            num: 1,
            size: 10,
            endSuccess: (len: number) => {
                // 空实现，避免分页回调报错
                const mescroll = getMescroll();
                if (mescroll) mescroll.endSuccess(len);
            },
            endErr: () => {
                // 空实现，避免报错
                const mescroll = getMescroll();
                if (mescroll) mescroll.endErr();
            }
        };
        getShopListFn(defaultMescroll);
    }
}

const getShopListFn = (mescroll: any) => {
    if(diyStore.mode == 'decorate') return;
	// 获取店铺列表	
    let data = {
        page: mescroll.num,
		limit: mescroll.size,
        is_self: diyComponent.value.is_self,
        category_id: diyComponent.value.category_id,
        position: systemStore.diyAddressInfo,
        order:  diyComponent.value.distance.sort,
        is_only_area: diyComponent.value.position.control ? 1 : 0,
        delivery_type: diyComponent.value.localDelivery.control ? ['local_delivery'] : ''
    }
    getShopWebList(data).then((res: any) => {
        let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (Number(mescroll.num) === 1) {
			shopList.value = []; //如果是第一页需手动制空列表
		}
		shopList.value = shopList.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
    }).catch(() => {
		mescroll.endErr(); // 请求失败, 结束加载
	})
}

const toLink = (data:any) => {
    redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id } })
}

const toSite = (data:any) => {
    redirect({ url: '/addon/mall/pages/index', param: { site_id: data.site_id } })
}
</script>
<style lang="scss" scoped>
.gird-wrap{
    width: 100%;
    display: grid;
    grid-template-rows: auto;
    grid-template-columns: repeat(3, 1fr);
    grid-gap: 16rpx;
}
/*  #ifdef  H5  */
:deep(.mescroll-body){
    min-height: auto !important;
}
:deep(.mescroll-upwarp){
    display: none !important;
}
/*  #endif  */
</style>
