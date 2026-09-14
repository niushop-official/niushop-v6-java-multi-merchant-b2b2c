<template>
    <x-skeleton :type="skeleton.type" :loading="skeleton.loading" :config="skeleton.config" class="relative">
        <view :style="warpCss" class="overflow-hidden">
            <view :style="maskLayer"></view>
            <view class="py-[20rpx] flex gap-x-[24rpx]" v-if="diyComponent.searchCondition.isShow">
                <view class="flex-1 h-[56rpx] bg-[#fff] rounded-[12rpx] flex-center text-[24rpx] text-[#282828] gap-x-[4rpx]"  :class="{ 'font-500': searchType == 'default' }" @click="searchTypeFn('default')">综合</view>
                <view class="flex-1 h-[56rpx] bg-[#fff] rounded-[12rpx] flex-center text-[24rpx] text-[#282828] gap-x-[4rpx]" @click="searchTypeFn('space')">
                    <text>距离</text>
                    <text v-if="space" class="text-[24rpx] nc-iconfont" :class="{'font-500 text-primary': searchType == 'space','nc-icon-shangV6xx-1': space == 'asc', 'nc-icon-xiaV6xx': space == 'desc' }"></text>
                    <text v-else class="nc-iconfont nc-icon-shangxiaxuanzeV6xx text-[30rpx]"></text>
                </view>
                <view class="flex-1 h-[56rpx] bg-[#fff] rounded-[12rpx] flex-center text-[24rpx] text-[#282828] gap-x-[4rpx]" @click="searchTypeFn('sale_num')">
                    <text>销量</text>
                    <text v-if="sale_num" class="text-[24rpx] nc-iconfont" :class="{'font-500 text-primary': searchType == 'sale_num','nc-icon-shangV6xx-1': sale_num == 'asc', 'nc-icon-xiaV6xx': sale_num == 'desc' }"></text>
                    <text v-else class="nc-iconfont nc-icon-shangxiaxuanzeV6xx text-[30rpx]"></text>
                </view>
                <view class="flex-1 h-[56rpx] bg-[#fff] rounded-[12rpx] flex-center text-[24rpx] text-[#282828] gap-x-[4rpx]" @click="searchTypeFn('price')">
                    <text>价格</text>
                    <text v-if="price" class="text-[24rpx] nc-iconfont" :class="{'font-500 text-primary': searchType == 'price','nc-icon-shangV6xx-1': price == 'asc', 'nc-icon-xiaV6xx': price == 'desc' }"></text>
                    <text v-else class="nc-iconfont nc-icon-shangxiaxuanzeV6xx text-[30rpx]"></text>
                </view>
            </view>
            <view :class="{'diy-mall-goods-list relative flex flex-wrap justify-between': diyComponent.style != 'style-2', 'biserial-goods-list': diyComponent.style == 'style-2'}">
                <template v-if="diyComponent.style == 'style-1'">
                    <view class="bg-white w-full flex p-[20rpx] overflow-hidden" :class="{ 'mt-[20rpx]': index > 0 }" :style="itemCss" v-for="(item,index) in goodsList" :key="item.goods_id" @click="toLink(item)">
                        <up-image :radius="imageRounded.val" width="270rpx" height="270rpx" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
                            <template #error>
                                <image class="w-[270rpx] h-[270rpx] overflow-hidden" :style="imageRounded.style" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                            </template>
                        </up-image>
                        <view class="flex-1 flex flex-col ml-[20rpx] py-[6rpx] relative">
                            <view class="text-[28rpx] leading-[40rpx] text-[#303133] multi-hidden mb-[10rpx]" :style="{ color : diyComponent.goodsNameStyle.color, fontWeight : diyComponent.goodsNameStyle.fontWeight }" v-if="diyComponent.goodsNameStyle.control">
                                <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                {{ item.goods_name }}
                            </view>
							<view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden mb-[5rpx]" v-if="item.sub_title">{{ item.sub_title }}</view>
                            <view class="flex mb-[10rpx]" v-if="item.site?.distance && diyComponent.distanceStyle.control">
								<view class="bg-[#f6f6f6] px-[10rpx] h-[42rpx] rounded-[6rpx] flex items-center using-hidden">
									<view class="flex items-center">
										<view class="w-[28rpx] h-[28rpx] rounded-[2rpx] overflow-hidden mr-[8rpx] flex-center">
											<up-image radius="2rpx" width="28rpx" height="28rpx" :src="diyShop.siteLogo(item.site)" mode="aspectFill">
												<template #error>
													<image :src="img('addon/mall/shop/shop_default.png')" class="w-[28rpx] h-[28rpx] rounded-[2rpx] align-middle"></image>
												</template>
											</up-image>
										</view>
										<view class="text-[24rpx] max-w-[200rpx] truncate">{{ item.site?.site_name }}</view>
									</view>
									<view class="w-[2rpx] h-[18rpx] bg-[#bbb] mx-[10rpx] flex-shrink-0"></view>
									<view class="text-[24rpx] flex-shrink-0">{{ distance(item.site.distance) }}</view>
								</view>
							</view>
                            <view v-if="item.goods_label_name && item.goods_label_name.length && diyComponent.labelStyle.control" class="flex flex-wrap mb-[10rpx]">
                                <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                    <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')"/>
                                    <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                </template>
                            </view>
                            <view class="mt-auto flex justify-between items-center">
                                <view class="flex flex-col">
                                    <view class="flex items-baseline leading-[1]" v-if="diyComponent.priceStyle.control">
                                        <view
                                            class="font-bold text-[var(--price-text-color)] price-font block truncate max-w-[350rpx]"
                                            :style="{ color : diyComponent.priceStyle.color }">
                                            <text class="text-[24rpx] font-500 mr-[4rpx]">￥</text>
                                            <text class="text-[40rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2) }}</text>
                                        </view>
                                        <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
										<image v-else-if="diyGoods.priceType(item) == 'discount_price'" class="max-w-[80rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                    </view>
                                    <text v-if="diyComponent.saleStyle.control" class="mt-[8rpx] text-[22rpx] text-[var(--text-color-light9)]" :style="{ color : diyComponent.saleStyle.color }">已售{{ item.sale_num }}{{ item.unit || '件' }}</text>
                                </view>
                                <view class="absolute right-[16rpx] bottom-[16rpx]" @click.stop v-if="diyComponent.btnStyle.control && !item.isMaxBuy || diyStore.mode == 'decorate'">

                                    <template v-if="item.goods_type == 'real' || diyStore.mode == 'decorate'">
                                        <view v-if="diyComponent.btnStyle.style == 'button'" :style="goodsBtnCss" class="relative px-[18rpx] h-[48rpx] flex items-center justify-center bg-[red]" @click.stop="itemCart(item, 'itemCart' + index)">
                                            <text class="text-[20rpx]">{{ diyComponent.btnStyle.text }}</text>
                                            <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                            :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                        </view>
                                        <view v-else :style="goodsBtnCss" class="relative w-[46rpx] h-[46rpx] rounded-[50%] flex items-center justify-center" @click.stop="itemCart(item, 'itemCart' + index)">
                                            <text :class="['nc-iconfont', 'text-[30rpx]', diyComponent.btnStyle.style]"></text>
                                            <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                            :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                        </view>
                                    </template>
                                </view>
                            </view>
                        </view>
                    </view>
                </template>
                <template v-if="diyComponent.style == 'style-2'">
                    <view>
                        <template v-for="(item,index) in goodsList">
                            <view v-if="(index%2) == 0" class="flex flex-col bg-[#fff] box-border overflow-hidden"
                                  :class="{'mt-[24rpx]': index > 1}" :style="itemCss" @click="toLink(item)">
                                <up-image :radius="imageRounded.val" :width="style2Width" :height="style2Width" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
                                    <template #error>
                                        <image :style="{'width': style2Width,'height': style2Width, 'border-radius': imageRounded.val}" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                                    </template>
                                </up-image>
                                <view class="relative min-h-[44rpx] px-[16rpx] flex-1 pt-[16rpx] pb-[20rpx] flex flex-col justify-between">
                                    <view class="flex mb-[16rpx]" v-if="item.site?.distance && diyComponent.distanceStyle.control">
										<view class="bg-[#f6f6f6] px-[8rpx] h-[40rpx] rounded-[6rpx] flex items-center">
											<view class="flex items-center">
												<view class="w-[28rpx] h-[28rpx] rounded-[2rpx] overflow-hidden mr-[8rpx] flex-center flex-shrink-0">
													<up-image radius="2rpx" width="28rpx" height="28rpx" :src="diyShop.siteLogo(item.site)" mode="aspectFill">
														<template #error>
															<image :src="img('addon/mall/shop/shop_default.png')" class="w-[28rpx] h-[28rpx] rounded-[2rpx] align-middle"></image>
														</template>
													</up-image>
												</view>
												<view class="text-[22rpx] using-hidden">{{ item.site?.site_name }}</view>
											</view>
											<view class="w-[2rpx] h-[18rpx] bg-[#bbb] mx-[10rpx]"></view>
											<view class="text-[22rpx] flex-shrink-0">{{ distance(item.site.distance) }}</view>
										</view>
									</view>
                                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden" :style="{ color : diyComponent.goodsNameStyle.color, fontWeight : diyComponent.goodsNameStyle.fontWeight }" v-if="diyComponent.goodsNameStyle.control">
                                        <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                        {{ item.goods_name }}
                                    </view>
									<view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sub_title">
									    {{ item.sub_title }}
									</view>
                                    <view v-if="item.goods_label_name && item.goods_label_name.length && diyComponent.labelStyle.control" class="flex flex-wrap">
                                        <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                            <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')" />
                                            <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                        </template>
                                    </view>
                                    <view class="flex justify-between flex-wrap items-center mt-[20rpx]">
                                        <view class="flex flex-col">
                                            <view class="flex items-baseline leading-[1]" v-if="diyComponent.priceStyle.control">
                                                <view class="text-[var(--price-text-color)] price-font block truncate max-w-[270rpx]" :style="{ color : diyComponent.priceStyle.color }">
                                                    <text class="text-[24rpx] font-400">￥</text>
                                                    <text class="text-[40rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[0] }}</text>
                                                    <text class="text-[24rpx] font-500">.{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[1] }}</text>
                                                </view>
                                                <image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
												<image v-else-if="diyGoods.priceType(item) == 'discount_price'" class="max-w-[80rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                            </view>
                                            <text v-if="diyComponent.saleStyle.control" class="text-[22rpx] mt-[8rpx] text-[var(--text-color-light9)]" :style="{ color : diyComponent.saleStyle.color }">
                                                已售{{ item.sale_num }}{{ item.unit || '件' }}
                                            </text>
                                        </view>
                                        <view class="absolute right-[16rpx] bottom-[16rpx]" @click.stop v-if="diyComponent.btnStyle.control && !item.isMaxBuy || diyStore.mode == 'decorate'">
                                            <template v-if="item.goods_type == 'real' || diyStore.mode == 'decorate'">
                                                <view v-if="diyComponent.btnStyle.style == 'button'" :style="goodsBtnCss" class="relative px-[18rpx] h-[48rpx] flex items-center justify-center bg-[red]" @click.stop="itemCart(item, 'itemCart' + index)">
                                                    <text class="text-[20rpx]">{{ diyComponent.btnStyle.text }}</text>
                                                    <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                                    :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                                </view>
                                                <view v-else :style="goodsBtnCss" class="relative w-[46rpx] h-[46rpx] rounded-[50%] flex items-center justify-center" @click.stop="itemCart(item, 'itemCart' + index)">
                                                    <text :class="['nc-iconfont', 'text-[30rpx]', diyComponent.btnStyle.style]"></text>
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
                            <view v-if="(index%2) == 1" class="flex flex-col bg-[#fff] box-border overflow-hidden" :class="{'mt-[24rpx]': index > 1}" :style="itemCss" @click="toLink(item)">
                                <up-image :width="style2Width" :height="style2Width" :radius="imageRounded.val" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
                                    <template #error>
                                        <image :style="{'width': style2Width,'height': style2Width, 'border-radius': imageRounded.val}" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                                    </template>
                                </up-image>
                                <view class="relative min-h-[44rpx] px-[16rpx] flex-1 pt-[16rpx] pb-[20rpx] flex flex-col justify-between">
                                    <view class="flex mb-[16rpx]" v-if="item.site?.distance && diyComponent.distanceStyle.control">
										<view class="bg-[#f6f6f6] px-[8rpx] h-[40rpx] rounded-[6rpx] flex items-center">
											<view class="flex items-center">
												<view class="w-[28rpx] h-[28rpx] rounded-[2rpx] overflow-hidden mr-[8rpx] flex-center flex-shrink-0">
													<up-image radius="2rpx" width="28rpx" height="28rpx" :src="diyShop.siteLogo(item.site)" mode="aspectFill">
														<template #error>
															<image :src="img('addon/mall/shop/shop_default.png')" class="w-[28rpx] h-[28rpx] rounded-[2rpx] align-middle"></image>
														</template>
													</up-image>
												</view>
												<view class="text-[22rpx] using-hidden">{{ item.site?.site_name }}</view>
											</view>
											<view class="w-[2rpx] h-[18rpx] bg-[#bbb] mx-[10rpx]"></view>
											<view class="text-[22rpx] flex-shrink-0">{{ distance(item.site.distance) }}</view>
										</view>
									</view>
                                    <view class="text-[#303133] leading-[40rpx] text-[28rpx] multi-hidden" :style="{ color : diyComponent.goodsNameStyle.color, fontWeight : diyComponent.goodsNameStyle.fontWeight }" v-if="diyComponent.goodsNameStyle.control">
                                        <view class="brand-tag" v-if="item.goods_brand && Object.values(item.goods_brand).length" :style="diyGoods.baseTagStyle(item.goods_brand)">{{ item.goods_brand.brand_name }}</view>
                                        {{ item.goods_name }}
                                    </view>
									<view class="text-[24rpx] text-[#999] leading-[30rpx] using-hidden my-[5rpx]" v-if="item.sub_title"> {{ item.sub_title }}</view>
                                    <view v-if="item.goods_label_name && item.goods_label_name.length && diyComponent.labelStyle.control" class="flex flex-wrap">
                                        <template v-for="(tagItem, tagIndex) in item.goods_label_name">
                                            <image class="img-tag" v-if="tagItem.style_type == 'icon' && tagItem.icon" :src="img(tagItem.icon)" mode="heightFix" @error="diyGoods.error(tagItem,'icon')" />
                                            <view class="base-tag" v-else-if="tagItem.style_type == 'diy' || !tagItem.icon" :style="diyGoods.baseTagStyle(tagItem)">{{ tagItem.label_name }}</view>
                                        </template>
                                    </view>
                                    <view class="flex justify-between flex-wrap items-center mt-[20rpx]">
                                        <view class="flex flex-col">
                                            <view class="flex items-baseline leading-[1]" v-if="diyComponent.priceStyle.control">
                                                <view class="text-[var(--price-text-color)] price-font block truncate max-w-[270rpx]" :style="{ color : diyComponent.priceStyle.color }">
                                                    <text class="text-[24rpx] font-400">￥</text>
                                                    <text class="text-[40rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[0] }}</text>
                                                    <text class="text-[24rpx] font-500">.{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2).split('.')[1] }}</text>
                                                </view>
											 	<image v-if="diyGoods.priceType(item) == 'member_price'" class="max-w-[50rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/VIP.png')" mode="heightFix" />
												<image v-else-if="diyGoods.priceType(item) == 'discount_price'" class="max-w-[80rpx] h-[28rpx] ml-[6rpx]" :src="img('addon/mall/discount.png')" mode="heightFix" />
                                            </view>
                                            <text v-if="diyComponent.saleStyle.control" class="text-[22rpx] mt-[8rpx] text-[var(--text-color-light9)]" :style="{ color : diyComponent.saleStyle.color }">
                                                已售{{ item.sale_num }}{{ item.unit || '件' }}
                                            </text>
                                        </view>
                                        <view class="absolute right-[16rpx] bottom-[16rpx]" @click.stop v-if="diyComponent.btnStyle.control && !item.isMaxBuy || diyStore.mode == 'decorate'">
                                            <template v-if="item.goods_type == 'real' || diyStore.mode == 'decorate'">
                                                <view v-if="diyComponent.btnStyle.style == 'button'" :style="goodsBtnCss" class="relative px-[18rpx] h-[48rpx] flex items-center justify-center bg-[red]" @click.stop="itemCart(item, 'itemCart' + index)">
                                                    <text class="text-[20rpx]">{{ diyComponent.btnStyle.text }}</text>
                                                    <view v-if="cartList['goods_' + item.goods_id] && cartList['goods_' + item.goods_id].totalNum"
                                                    :class="['absolute right-[-16rpx] top-[-16rpx] rounded-[30rpx] h-[30rpx] min-w-[30rpx] text-center leading-[26rpx] bg-[var(--primary-color)] text-[#fff] text-[20rpx] font-500 box-border box-border border-[2rpx] border-solid border-[#fff]', cartList['goods_' + item.goods_id].totalNum > 9 ? 'px-[10rpx]' : '']">{{ cartList['goods_' + item.goods_id].totalNum }}</view>
                                                </view>
                                                <view v-else :style="goodsBtnCss" class="relative w-[46rpx] h-[46rpx] rounded-[50%] flex items-center justify-center" @click.stop="itemCart(item, 'itemCart' + index)">
                                                    <text :class="['nc-iconfont', 'text-[30rpx]', diyComponent.btnStyle.style]"></text>
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
                <template v-if="diyComponent.style == 'style-3'">
                    <view :style="style3Css" v-if="goodsList.length">
                        <scroll-view :id="'warpStyle3-'+diyComponent.id" class="whitespace-nowrap min-h-[290rpx]" :scroll-x="true">
                            <view :id="'item'+index+diyComponent.id"  class="w-[214rpx] mb-[6rpx] inline-block bg-[#fff] box-border overflow-hidden" :class="{'!mr-[0rpx]' : index == (goodsList.length-1)}" :style="itemCss+itemStyle3" v-for="(item,index) in goodsList" :key="item.goods_id" @click="toLink(item)">
                                <up-image width="214rpx" height="160rpx" :radius="imageRounded.val" :src="img(item.goods_cover_thumb_mid || '')" model="aspectFill">
                                    <template #error>
                                        <image class="w-[214rpx] h-[160rpx]" :style="imageRounded.style" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill" />
                                    </template>
                                </up-image>
                                <view class="relative min-h-[40rpx] px-[10rpx] pt-[16rpx] pb-[10rpx]">
                                    <view class="text-[26rpx] text-[#303133] truncate" :style="{ color : diyComponent.goodsNameStyle.color, fontWeight : diyComponent.goodsNameStyle.fontWeight }" v-if="diyComponent.goodsNameStyle.control">{{ item.goods_name }}</view>
                                    <view class="text-[var(--price-text-color)] pt-[16rpx] pb-[6rpx] font-bold price-font block truncate max-w-[160rpx] leading-[1] overflow-hidden" :style="{ color : diyComponent.priceStyle.color }" v-if="diyComponent.priceStyle.control">
                                        <text class="text-[20rpx] font-400 mr-[2rpx]">￥</text>
                                        <text class="text-[36rpx] font-500">{{ parseFloat(diyGoods.goodsPrice(item)).toFixed(2) }}</text>
                                    </view>
                                    <view v-if="diyComponent.btnStyle.control" class="absolute right-[10rpx] bottom-[12rpx]">
                                        <view v-if="diyComponent.btnStyle.style != 'button'" :style="goodsBtnCss" class="w-[40rpx] h-[40rpx] rounded-[50%] flex items-center justify-center">
                                            <text :class="[diyComponent.btnStyle.style]" class="nc-iconfont text-[28rpx]"></text>
                                        </view>
                                    </view>
                                </view>
                            </view>
                        </scroll-view>
                    </view>

                </template>
                <add-cart-popup ref="cartRef" />
			</view>
		</view>
	</x-skeleton>
</template>

<script setup lang="ts">
// 商品列表
import { ref, reactive, computed, watch, onMounted, nextTick, getCurrentInstance } from 'vue';
import { redirect, img, distance } from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
import { getGoodsComponents } from '@/addon/mall/api/goods';
import { useGoods } from '@/addon/mall/hooks/useGoods'
import { useShop } from '@/addon/mall/hooks/useShop'
import addCartPopup from '@/addon/mall/pages/goods/components/add-cart-popup.vue'
import useCartStore from '@/addon/mall/stores/cart'
import useMemberStore from '@/stores/member'
import { useLogin } from '@/hooks/useLogin'
import useSystemStore from '@/stores/system'

const cartStore = useCartStore();
// 查询购物车列表
cartStore.getList();

const memberStore = useMemberStore()
const cartList = computed(() => cartStore.cartList)
const userInfo = computed(() => memberStore.info)

const systemStore = useSystemStore();

const diyGoods = useGoods();
const diyShop = useShop();
const props = defineProps(['component', 'index', 'value']);
const diyStore = useDiyStore();
const emits = defineEmits(['loadingFn']); //商品数据加载完成之后触发

const skeleton = reactive({
	type: '',
	loading: diyStore.mode == 'decorate' ? false : true,
	config: {}
})

const goodsList = ref<Array<any>>([]);

const diyComponent = computed(() => {
	if(props.value) {
		return props.value;
	}else if (diyStore.mode == 'decorate') {
		return diyStore.value[props.index];
	} else {
		return props.component;
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

const imageRounded = computed(() => {
    const obj = {
        val: '',
        style: ''
    };
    if (diyComponent.value.imgElementRounded) {
        obj.val = diyComponent.value.imgElementRounded * 2 + 'rpx';
        obj.style += 'border-radius:' + diyComponent.value.imgElementRounded * 2 + 'rpx;';
    }
    return obj;
})

// 背景图加遮罩层
const maskLayer = computed(() => {
    let style = '';
    if (diyComponent.value.componentBgUrl) {
        style += 'position:absolute;top:0;width:100%;';
        style += `background: rgba(0,0,0,${ diyComponent.value.componentBgAlpha / 10 });`;
        style += `height:${ height.value }px;`;

        if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
        if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
        if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
        if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    }

    return style;
});

const itemCss = computed(() => {
    let style = '';
    if (diyComponent.value.elementBgColor) style += 'background-color:' + diyComponent.value.elementBgColor + ';';
    if (diyComponent.value.topElementRounded) style += 'border-top-left-radius:' + diyComponent.value.topElementRounded * 2 + 'rpx;';
    if (diyComponent.value.topElementRounded) style += 'border-top-right-radius:' + diyComponent.value.topElementRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomElementRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomElementRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomElementRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomElementRounded * 2 + 'rpx;';
    if (diyComponent.value.style == 'style-2') {
        if (diyComponent.value.margin && diyComponent.value.margin.both) style += 'width: calc((100vw - ' + (diyComponent.value.margin.both * 4) + 'rpx - 20rpx) / 2);'
        else style += 'width: calc((100vw - 20rpx) / 2 );'
    }
    return style;
})

const goodsBtnCss = computed(() => {
    let style = '';
    if (diyComponent.value.btnStyle.style == 'button' && diyComponent.value.btnStyle.aroundRadius) style += 'border-radius:' + diyComponent.value.btnStyle.aroundRadius * 2 + 'rpx;';
    if (diyComponent.value.btnStyle.startBgColor && diyComponent.value.btnStyle.endBgColor) {
        style += `background:linear-gradient(${ diyComponent.value.btnStyle.startBgColor },${ diyComponent.value.btnStyle.endBgColor });`;
    } else {
        style += 'background-color:' + (diyComponent.value.btnStyle.startBgColor || diyComponent.value.btnStyle.endBgColor) + ';';
    }
    if (diyComponent.value.btnStyle.textColor) style += 'color:' + diyComponent.value.btnStyle.textColor + ';';
    if (diyComponent.value.btnStyle.style == 'button' && diyComponent.value.btnStyle.fontWeight) style += 'font-weight: bold;';
    return style;
})

const style2Width = computed(() => {
    let style = '';
    if (diyComponent.value.margin && diyComponent.value.margin.both) style += 'calc((100vw - ' + (diyComponent.value.margin.both * 4) + 'rpx - 20rpx) / 2)'
    else style += 'calc((100vw - 20rpx) / 2 )'
    return style;
})

const style3Css = computed(() => {
    let style = '';
    style += 'padding:0 20rpx;';
    if (diyComponent.value.margin && diyComponent.value.margin.both) {
        style += 'width: calc(100vw - ' + ((diyComponent.value.margin.both * 4) + 40) + 'rpx);'
    } else {
        style += 'box-sizing: border-box; width: 100vw;';
    }
    return style;
})
//商品样式三
const itemStyle3 = ref('');
const setItemStyle3 = () => {
    // #ifdef MP-WEIXIN
    uni.createSelectorQuery().in(instance).select('#warpStyle3-' + diyComponent.value.id).boundingClientRect((res: any) => {
        uni.createSelectorQuery().in(instance).select('#item0' + diyComponent.value.id).boundingClientRect((data: any) => {
            itemStyle3.value = `margin-right:${ (res.width - data.width * 3) / 2 }px;`
        }).exec()
    }).exec()
    // #endif
    // #ifdef H5
    itemStyle3.value = 'margin-right:14rpx;'
    // #endif
}

const price = ref('');
const sale_num = ref('');
const space = ref('');
const searchType = ref('default');
const clickFlag = ref(false);  //是否点击排序

const getGoodsListFn = () => {
    let data = {
        site_id: diyStore.siteId,
        num: (diyComponent.value.source == 'all' || diyComponent.value.source == 'category') ? diyComponent.value.num : '',
        goods_ids: diyComponent.value.source == 'custom' ? diyComponent.value.goods_ids : '',
        goods_category: diyComponent.value.source == 'category' ? diyComponent.value.goods_category : '',
        order: clickFlag.value ?  (searchType.value == 'space' ? 'distance' : searchType.value) : diyComponent.value.sortWay,
        sort: clickFlag.value ? (searchType.value == 'price' ? price.value : searchType.value == 'sale_num' ? sale_num.value : space.value) : diyComponent.value.sortType,
        position: systemStore.diyAddressInfo,
        is_location: diyComponent.value.position.control ? 1 : 0,
        is_local_delivery: diyComponent.value.localDelivery.control ? 1 : 0,
		
    }
    getGoodsComponents(data).then((res: any) => {
        goodsList.value = res.data;
        skeleton.loading = false;
        emits('loadingFn', res.data)
        if (diyComponent.value.componentBgUrl) {
            setTimeout(() => {
                const query = uni.createSelectorQuery().in(instance);
                query.select('.diy-mall-goods-list').boundingClientRect((data: any) => {
                    if (data) height.value = data.height;
                }).exec();
            }, 1000)
        }
        nextTick(()=>{
            setTimeout(()=>{
                if(diyComponent.value.style == 'style-3') setItemStyle3()
            },500)
        })
        goodsMaxBuy()
    });
}

// 搜索
const searchTypeFn = (type:any) => {
    clickFlag.value = true;
	searchType.value = type;
	if (type == 'default') {
		sale_num.value = '';
		price.value = '';
	}
    if (type == 'space') {
        sale_num.value = '';
		price.value = '';
		if(space.value){
			space.value = space.value == 'asc' ? 'desc' : 'asc';
		}else{
			space.value = 'asc';
		}
	}
    if (type == 'sale_num') {
		price.value = '';
        space.value = '';
		if(sale_num.value){
			sale_num.value = sale_num.value == 'asc' ? 'desc' : 'asc';
		}else{
			sale_num.value = 'asc';
		}
	}
	if (type == 'price') {
		sale_num.value = '';
        space.value = '';
		if(price.value){
			price.value = price.value == 'asc' ? 'desc' : 'asc';
		}else{
			price.value = 'asc';
		}
	}
	
	getGoodsListFn()
}
/*************** 加入购物车 - start ***********************/
const goodsMaxBuy = () => {
    goodsList.value.forEach((data, index) => {
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

// 商品价格
const goodsPrice = (data: any) => {
    let price = "0.00";
	price = data.goodsSku.show_price
    return price;
}

const animationAddCart = (row: any, id: any) => {
    if (cartRepeatFlag.value) return false
    cartRepeatFlag.value = true

    let obj: any = {
        goods_id: row.goodsSku.goods_id,
        sku_id: row.goodsSku.sku_id,
        sale_price: goodsPrice(row),
        stock: row.goodsSku.stock
    };
    if (row.id) {
        obj.num = row.num;
        obj.id = row.id;
    }

    // 起购
    let num = 1;
    if (row.min_buy > 0 && !row.num) {
        num = row.min_buy;
    } else {
        num = 1;
    }

    cartStore.increase(obj, num, () => {
        cartRepeatFlag.value = false
        cartStore.isAddCartRecommend = true
    });
}

//点击商品购物车按钮
const cartRef = ref()
const cartRepeatFlag = ref<Boolean>(false)
const itemCart = (row: any, id: any) => {
    if(diyStore.mode == 'decorate') return false
    // 虚拟商品，并且需要核销，禁止加入购物车
    if (row.goods_type == 'virtual') {
        return toLink(row)
    }
    if (diyComponent.value.btnStyle.cartEvent !== 'cart') {
        return toLink(row)
    }

    if (!userInfo.value) {
        useLogin().setLoginBack({ url: '/addon/shop/pages/index' })
        return false
    }

    cartRef.value.open(row.goodsSku.sku_id)

}

/*************** 加入购物车 - end ***********************/

const initSkeleton = () => {
    if (diyComponent.value.style == 'style-1') {

        // 单列 风格
        skeleton.type = 'list'
        skeleton.config = {
            textRows: 2
        };
    } else if (diyComponent.value.style == 'style-2') {

        // 两列 风格
        skeleton.type = 'waterfall'
        skeleton.config = {
            headHeight: '320rpx',
            gridRows: 1,
            textRows: 2,
            textWidth: ['100%', '80%']
        };
    } else if (diyComponent.value.style == 'style-3') {

        // 横向滑动 风格
        skeleton.type = 'waterfall'
        skeleton.config = {
            gridRows: 1,
            gridColumns: 3,
            headHeight: '200rpx',
            textRows: 2,
            textWidth: ['100%', '80%']
        };
    }
}

const instance = getCurrentInstance();
const height = ref(0)

onMounted(() => {
    refresh();
    // 装修模式下刷新
    if (diyStore.mode == 'decorate') {
        watch(
            () => diyComponent.value,
            (newValue, oldValue) => {
                if (newValue && newValue.componentName == 'MallGoodsList') {
                    nextTick(() => {
                        const query = uni.createSelectorQuery().in(instance);
                        query.select('.diy-mall-goods-list').boundingClientRect((data: any) => {
                            if(data) height.value = data.height;
                        }).exec();
                        if(diyComponent.value.style == 'style-3') setItemStyle3()
                    })
                }
            }
        )
    } else {
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
            goods_cover_thumb_small: "",
            goods_name: "商品名称",
            sale_num: "100",
            unit: "件",
            goodsSku: { show_price: 100 },
            site:{
                site_name: "店铺名称",
                distance: "1000",
                front_end_logo: ""
            }
        };
        goodsList.value.push(obj);
        goodsList.value.push(obj);
        nextTick(()=>{
            if(diyComponent.value.style == 'style-3') setItemStyle3()
        })
    } else {
        initSkeleton();
        getGoodsListFn();
    }

}

const toLink = (data: any) => {
    redirect({ url: '/addon/mall/pages/goods/detail', param: { goods_id: data.goods_id } })
}


</script>

<style lang="scss" scoped>
@import '@/addon/mall/styles/common.scss';

.biserial-goods-list {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-gap: 10px;
}
</style>