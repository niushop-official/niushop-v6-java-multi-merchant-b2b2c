<template>
    <view class="bg-[var(--page-bg-color)] min-h-[100vh] overflow-hidden" :style="themeColor()">
        <view>
            <shop-head ref="shopHeadRef" :tab="true" @load="handleList"  @change="handleChange"/> 
        </view>
        <mescroll-body ref="mescrollRef"  @init="mescrollInit" :down="{ use: false }"  @up="getGoodsListFn">
            <view class="sidebar-margin pt-[20rpx]"  v-if="goodsList.length" :class="{ 'biserial-goods-list': !listType }">
                <template v-if="listType">
                    <view class="bg-white flex p-[20rpx] overflow-hidden rounded-[var(--rounded-small)]" :class="{ 'mt-[20rpx]': index > 0 }"  v-for="(item,index) in goodsList" :key="item.goods_id" @click="toLink(item)">
                        <up-image radius="var(--rounded-mid)" width="270rpx" height="270rpx" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
                            <template #error>
                                <image class="w-[270rpx] h-[270rpx] overflow-hidden rounded-[var(--rounded-mid)]" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                            </template>
                        </up-image>
                        <view class="flex-1 flex flex-col ml-[20rpx] py-[6rpx] relative">
                            <view class="text-[30rpx] font-500 leading-[40rpx] text-[#303133] multi-hidden mb-[10rpx]">
                                <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                {{ item.goods_name }}
                            </view>
                            <view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden mb-[5rpx]" v-if="item.sub_title">{{ item.sub_title }}</view>
                            <view v-if="item.goods_label_name && item.goods_label_name.length" class="flex flex-wrap mb-[10rpx]">
                                <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                    <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')"/>
                                    <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                </template>
                            </view>
                            <view class="mt-auto flex justify-between items-center">
                                <view class="flex flex-col">
                                    <view class="flex items-baseline leading-[1]">
                                        <view
                                            class="font-bold text-[var(--price-text-color)] price-font block truncate max-w-[350rpx]">
                                            <text class="text-[24rpx] font-500 mr-[4rpx]">￥</text>
                                            <text class="text-[40rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2) }}</text>
                                        </view>
                                        <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
                                        <image v-else-if="diyGoods.priceType(item) == 'discount_price'" class="max-w-[80rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                    </view>
                                    <text  class="mt-[8rpx] text-[22rpx] text-[var(--text-color-light9)]">已售{{ item.sale_num }}{{ item.unit || '件' }}</text>
                                </view>
                                <view class="absolute right-[16rpx] bottom-[16rpx]" @click.stop v-if="!item.isMaxBuy">

                                    <template v-if="item.goods_type == 'real'">
                                        <view  class="relative w-[46rpx] h-[46rpx] rounded-[50%] flex items-center justify-center text-[#fff] bg-[var(--primary-color)]" @click.stop="itemCart(item, 'itemCart' + index)">
                                            <text class="nc-iconfont text-[30rpx] nc-icon-gouwucheV6xx6"></text>
                                            <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                            :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                        </view>
                                    </template>
                                </view>
                            </view>
                        </view>
                    </view>
                </template>
                <template v-else>
                    <view>
                        <template v-for="(item,index) in goodsList" :key="index">
                            <view v-if="(index % 2) == 0" class="flex flex-col bg-[#fff] box-border rounded-[var(--rounded-mid)] overflow-hidden mb-[var(--top-m)]"  @click="toLink(item)">
                                <image v-if="item.goods_cover_thumb_mid" class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]"  :src="img(item.goods_cover_thumb_mid)" :mode="'aspectFill'" @error="item.goods_cover_thumb_mid='static/resource/images/diy/shop_default.jpg'"/>
                                <image v-else class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]" :src="img('static/resource/images/diy/shop_default.jpg')" :mode="'aspectFill'"/>
                                <view class="relative min-h-[44rpx] px-[16rpx] flex-1 pt-[16rpx] pb-[20rpx] flex flex-col justify-between">
                                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden" >
                                        <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                        {{ item.goods_name }}
                                    </view>
                                    <view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sub_title">
                                        {{ item.sub_title }}
                                    </view>
                                    <view v-if="item.goods_label_name && item.goods_label_name.length" class="flex flex-wrap">
                                        <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                            <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')" />
                                            <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                        </template>
                                    </view>
                                    <view class="flex justify-between flex-wrap items-center mt-[20rpx]">
                                        <view class="flex flex-col">
                                            <view class="flex items-baseline leading-[1]">
                                                <view class="text-[var(--price-text-color)] price-font block truncate max-w-[270rpx]">
                                                    <text class="text-[24rpx] font-400">￥</text>
                                                    <text class="text-[40rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[0] }}</text>
                                                    <text class="text-[24rpx] font-500">.{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[1] }}</text>
                                                </view>
                                                <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
                                                <image v-else-if="diyGoods.priceType(item) == 'discount_price'" class="max-w-[80rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                            </view>
                                            <text  class="text-[22rpx] mt-[8rpx] text-[var(--text-color-light9)]">
                                                已售{{ item.sale_num }}{{ item.unit || '件' }}
                                            </text>
                                        </view>
                                        <view class="absolute right-[16rpx] bottom-[16rpx]" @click.stop v-if="!item.isMaxBuy">
                                            <template v-if="item.goods_type == 'real'">
                                                <view  class="relative w-[46rpx] h-[46rpx] rounded-[50%] flex items-center justify-center text-[#fff] bg-[var(--primary-color)]" @click.stop="itemCart(item, 'itemCart' + index)">
                                                    <text class="nc-iconfont text-[30rpx] nc-icon-gouwucheV6xx6"></text>
                                                    <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                                    :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                                </view>
                                            </template>
                                        </view>
                                    </view>
                                </view>
                            </view>
                        </template>
                    </view>
                    <view>
                        <template v-for="(item,index) in goodsList">
                            <view v-if="(index % 2) == 1" class="flex flex-col bg-[#fff] box-border rounded-[var(--rounded-mid)] overflow-hidden mb-[var(--top-m)]"  @click="toLink(item)">
                                <image v-if="item.goods_cover_thumb_mid" class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]"  :src="img(item.goods_cover_thumb_mid)" :mode="'aspectFill'" @error="item.goods_cover_thumb_mid='static/resource/images/diy/shop_default.jpg'"/>
                                <image v-else class="w-[100%] h-[344rpx] rounded-tl-[var(--rounded-mid)] rounded-tr-[var(--rounded-mid)]" :src="img('static/resource/images/diy/shop_default.jpg')" :mode="'aspectFill'"/>
                                <view class="relative min-h-[44rpx] px-[16rpx] flex-1 pt-[16rpx] pb-[20rpx] flex flex-col justify-between">
                                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden">
                                        <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                        {{ item.goods_name }}
                                    </view>
                                    <view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sub_title"> {{ item.sub_title }}</view>
                                    <view v-if="item.goods_label_name && item.goods_label_name.length" class="flex flex-wrap">
                                        <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                            <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')" />
                                            <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                        </template>
                                    </view>
                                    <view class="flex justify-between flex-wrap items-center mt-[20rpx]">
                                        <view class="flex flex-col">
                                            <view class="flex items-baseline leading-[1]">
                                                <view class="text-[var(--price-text-color)] price-font block truncate max-w-[270rpx]">
                                                    <text class="text-[24rpx] font-400">￥</text>
                                                    <text class="text-[40rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[0] }}</text>
                                                    <text class="text-[24rpx] font-500">.{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[1] }}</text>
                                                </view>
                                                <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
                                                <image v-else-if="diyGoods.priceType(item) == 'discount_price'" class="max-w-[80rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                            </view>
                                            <text class="text-[22rpx] mt-[8rpx] text-[var(--text-color-light9)]">
                                                已售{{ item.sale_num }}{{ item.unit || '件' }}
                                            </text>
                                        </view>
                                        <view class="absolute right-[16rpx] bottom-[16rpx]" @click.stop v-if="!item.isMaxBuy">
                                            <template v-if="item.goods_type == 'real'">
                                                <view  class="relative w-[46rpx] h-[46rpx] rounded-[50%] flex items-center justify-center text-[#fff] bg-[var(--primary-color)]" @click.stop="itemCart(item, 'itemCart' + index)">
                                                    <text class="nc-iconfont text-[30rpx] nc-icon-gouwucheV6xx6"></text>
                                                    <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                                    :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                                </view>
                                            </template>
                                        </view>
                                    </view>
                                </view>
                            </view>
                        </template>
                    </view>
                </template>
            </view>
            <mescroll-empty v-if="!goodsList.length && loading" :option="{tip : '暂无商品'}"></mescroll-empty>
        </mescroll-body>
        <add-cart-popup ref="cartRef" />
        <site-tabbar :site-id="siteId" v-if="siteId"  />
    </view>
</template>

<script setup lang="ts">
import {computed,  ref } from 'vue'
import { img,  redirect } from '@/utils/common'
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import addCartPopup from '@/addon/mall/pages/goods/components/add-cart-popup.vue'
import { useGoods } from '@/addon/mall/hooks/useGoods'
import { getShopInfo } from '@/addon/mall/api/shop';
import { getShopGoodsPages } from '@/addon/mall/api/goods';
import useCartStore from '@/addon/mall/stores/cart'
import useMemberStore from '@/stores/member'
import { useLogin } from '@/hooks/useLogin'
import shopHead from '@/addon/mall/pages/home/components/shop-head/shop-head.vue'
const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const cartStore = useCartStore();
// 查询购物车列表
cartStore.getList();
const memberStore = useMemberStore()
const cartList = computed(() => cartStore.cartList)
const userInfo = computed(() => memberStore.info)
const shopInfo = ref<any>({})
const diyGoods = useGoods();
const siteId = ref(0);
const goodsList = ref<Array<any>>([]);
const loading = ref<boolean>(false);
const searchType = ref<any>('all'); // 搜索类型  all:全部  price:价格  sales:销量
const listType = ref(true)

onLoad((option: any) => {
    siteId.value = option.site_id;
    getShopInfoFn();
})

const getShopInfoFn = ()=> {
    getShopInfo(siteId.value).then((res: any) => {
        if (res.data) {
            shopInfo.value = {
                siteId: res.data.site_id,
                icon: res.data.front_end_logo ? res.data.front_end_logo : res.data.icon,
                siteName: res.data.site_name,
                typeName: res.data.is_self == 1 ? '自营店铺' : '直营店铺',
                follow_number: res.data.follow_number,
                isFollow: res.data.member_info?.is_follow,
                distance: res.data.distance,
                background_h5: res.data.background_h5,
            }
        }
    })
}
const getGoodsListFn = (mescroll: any) => {
    loading.value = false;
    let data = {
        site_id: siteId.value,
        page:  mescroll.num,
		limit: mescroll.size,
        order: searchType.value === 'all' ? '' : searchType.value,
    }
    getShopGoodsPages(data).then((res: any) => {
        let newArr = (res.data.data as Array<Object>);
        if (Number(mescroll.num) === 1) {
			goodsList.value = []; //如果是第一页需手动制空列表
		}
        goodsList.value = goodsList.value.concat(newArr);
        mescroll.endSuccess(newArr.length);
        loading.value = true;
        goodsMaxBuy()
    }).catch(() => {
        loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	});
}
const handleList = (data: any) => {
    searchType.value = data.searchType;
    getMescroll().resetUpScroll();
}

const handleChange = (data: any) => {
    listType.value = data.listType;
}
/*************** 加入购物车 - start ***********************/
const goodsMaxBuy = () => {
    goodsList.value.forEach((data: any, index: any) => {
        data.isMaxBuy = false;

        let maxBuyNum = -1;
        // 限购 - 是否开启限购
        if (data.is_limit) {
            if (data.max_buy) {
                let max_buy = 0;
                if (data.limit_type == 1) { //单次限购
                    max_buy = data.max_buy;
                } else { // 单人限购
                    let buyVal = data.max_buy - (data.has_buy || 0);
                    max_buy = buyVal > 0 ? buyVal : 0;
                }

                if (max_buy > data.stock) {
                    maxBuyNum = data.stock
                } else if (max_buy <= data.stock) {
                    maxBuyNum = max_buy;
                }
            }
        }
        if (maxBuyNum == 0) {
            data.isMaxBuy = true;
        }
    })
}

//点击商品购物车按钮
const cartRef = ref()
const itemCart = (row: any) => {
    // 虚拟商品，并且需要核销，禁止加入购物车
    if (row.goods_type == 'virtual') {
        return toLink(row)
    }

    if (!userInfo.value) {
        useLogin().setLoginBack({ url: '/addon/mall/pages/shop/home/goods', param: { site_id: siteId.value } })
        return false
    }

    cartRef.value.open(row.goodsSku.sku_id)

}

/*************** 加入购物车 - end ***********************/

const toLink = (data: any) => {
    redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id } })
}

</script>

<style lang="scss" scoped>
@import '@/addon/mall/styles/common.scss';

.shop-head-wrap{
	.bg-img{
		position: absolute;
		width: 100%;
		top: 0;
		bottom: 0;
		z-index: 0;
		-webkit-filter: blur(0);
		filter: blur(0);
		overflow: hidden;
	}
	.bg-img-one{
		position: absolute;
		width: 100%;
		top: 0;
		bottom: 0;
		z-index: 0;
		-webkit-filter: blur(0);
		filter: blur(0);
		overflow: hidden;
		uni-image, image{
			-webkit-filter: blur(0px);
			filter: blur(0px);
			-webkit-transform: scale(1);
			transform: scale(1);
		}
		.bg-img-box{
			position: absolute;
			top: 0;
			right: 0;
			bottom: 0;
			left: 0;
		}
	}
}

.fixed-wrap {
	&.fixed {
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		z-index: 991;
		transition: background .3s;
	}
}

.diy-search-wrap{
	display: flex;
	position: relative;
	align-items: center;
	padding: 20rpx;
	.img-wrap{
		display: flex;
		align-items: center;
		justify-content: center;
		width: 140rpx;
		height: 60rpx;
		margin-right: 30rpx;
		image{
			width: 100%;
			height:100%;
		}
	}
	.search-left{
		border-radius: 50rpx;
		background-color: rgba(255,255,255,.2);
		margin-right: 20rpx;
	}
	.search-content {
		display: flex;
		align-items: center;
		padding-left: 32rpx;
		padding-right: 6rpx;
		border-radius: 50rpx;
		background-color: rgba(255, 255, 255, .2);
		flex: 1;
		position: relative;

		.input-content, .uni-input {
			box-sizing: border-box;
			display: block;
			height: 64rpx;
			line-height: 64rpx;
			width: 100%;
			padding-right: 20rpx;
			color: #fff;
			background: none;
		}
		.iconfont {
			font-size: 30rpx;
			font-weight: bold;
			color: #fff;
		}
		.swiper-wrap{
			position: absolute;
			width:80%;
			height: 64rpx;
			line-height: 64rpx;
			color:#fff;
		}
	}

}

.shop-type{
	background: linear-gradient( 90deg, #FFD6B5 0%, #FFBA8C 100%);
}
.search-box {
	&::after {
		content: '';
		width: 20rpx;
		height: 20rpx;
		background-color: #fff;
		position: absolute;
		left: 50%;
		top: -10rpx;
		transform: translateX(-50%) rotate(45deg);
	}
}

.biserial-goods-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-gap: 10px;
}
</style>