<template>
	<div class="w-full min-h-[100%] bg-[#fff] pb-[20px]  detail" v-loading="loading">
		<div class="main-container" v-if="Object.keys(detail).length">
			<div class="pt-[30px]">
				<div class="flex ">
					<div class="w-[530px] flex flex-col">
						<div class="w-full h-[530px] relative overflow-hidden">
							<div class="absolute top-0 left-0 w-full h-full transition-transform duration-300 ease-linear transform" :class="{'translate-x-0':switchMedia === 'img','translate-x-full':switchMedia != 'img'}">
								<el-image class="w-[530px] h-[530px] rounded-[var(--rounded-big)]" :src="img(detail.goods.goods_image_thumb_big.length ? detail.goods.goods_image_thumb_big[imgActive] : '')">
									<template #error>
										<img src="@/assets/images/goods_default.png" class="w-[530px] h-[530px] rounded-[var(--rounded-big)]">
									</template>
								</el-image>
							</div>
							<div @touchmove.stop.prevent class="media-mode absolute top-0 left-0 w-full h-full transition-transform duration-300 ease-linear transform" :class="{'translate-x-0':switchMedia === 'video','-translate-x-full':switchMedia != 'video'}" >
								<video :src="img(detail.goods.goods_video)" :poster="img(detail.goods.goods_cover_thumb_mid)"  preload="auto" controls="true" autoplay muted loop class="w-[530px] h-[530px] bg-[#333]" ref="videoRef"></video>
							</div>
						</div>
						<div class="flex justify-between mt-[20px]">
							<div class="h-[90px] w-[20px] flex items-center justify-center cursor-pointer" @click="imgListClick('left')">
								<el-icon color="#ccc" size="14">
									<ArrowLeftBold />
								</el-icon>
							</div>
							<div class="relative flex-1 overflow-hidden h-[90px]">
								<div class="absolute flex items-center h-[100%] transition-all ease-in-out duration-500" :style="{ 'left': imgListLeft + 'px' }">
									<template v-if="detail.goods.goods_image_thumb_small">
										<div v-for="(item, index) in detail.goods.goods_image_thumb_small" :key="index" @mouseover="imgListHover(index)">
											<div :class="['w-[86px] h-[86px] relative box-border mx-[6px] box-border border-[2px] border-solid cursor-pointer align-middle rounded-[var(--rounded-small)]', imgActive == index ? 'border-primary' : 'border-transparent']">
												<el-image class="w-full h-full rounded-[var(--rounded-small)]"  :src="img(item)" alt="" fit="contain"></el-image>
												<div class="absolute top-0 left-0 right-0 bottom-0 flex-center" v-if="detail.goods.goods_video != ''  && index == 0 ">
													<div class="w-[30px] h-[30px] bg-[rgba(102,102,102,0.8)] rounded-full flex-center">
														<span class="iconfont icon-a-xiangyouV6mm text-[#fff] text-[30px]"></span>
													</div>
												</div>
											</div>
										</div>
									</template>
								</div>
							</div>
							<div class="h-[90px] w-[20px] flex items-center justify-center cursor-pointer" @click="imgListClick('right')">
								<el-icon  color="#ccc" size="14">
									<ArrowRightBold />
								</el-icon>
							</div>
						</div>
					</div>
					<div class="flex-1 ml-[40px] pt-[20px]">
						<div class="text-[18px] leading-[28px] text-[#333] truncate max-w-[570px] mb-[20px]">{{ detail.goods.goods_name }}</div>
						<div class="text-[14px] leading-[18px] text-[#999] truncate max-w-[570px] mb-[30px]" v-if="detail.goods.sub_title">{{ detail.goods.sub_title }}</div>
						<div class="w-[630px] bg-[#F3F3F3] rounded-[var(--rounded-mid)] overflow-hidden mb-[20px] ">
							<div  v-if="priceType == 'discount_price'" class="discount-bg h-[52px] box-border px-[10px] py-[15px]">
								<div class="flex items-center">
									<div class="h-[16px]"> 
										<img class="h-[16px] w-auto align-middle" src="@/assets/images/addon/discount_price.png" alt="">
									</div>
									<div class="flex items-baseline text-[#fff] leading-[18px] ml-[10px]">
										<span class="text-[14px] mr-[6px] ">{{ t('endOfRange') }}</span>
										<el-countdown  :value="discountTime"  format="DD[天]HH[时]mm[分]ss[秒]"  :value-style="{ fontSize: '14px',color:'#fff',letterSpacing: '1px' }"/>
									</div>
								</div>
							</div>
							<div class="px-[20px] py-[20px] border-box bg-img">
								<div class="flex items-center justify-between flex-1">
									<div class="flex items-baseline">
										<div class="inline-block">
											<span class="text-[14px] mr-[2px] text-[#EF000C] price-font">￥</span>
											<span class="text-[#EF000C] text-[24px] price-font">{{parseFloat(goodsPrice).toFixed(2) }}</span>
										</div>
										<img  v-if="priceType == 'member_price'" class="h-[14px] ml-[6px] w-[24px]" src="@/assets/images/addon/VIP.png" />
										<div class="text-[12px] text-[#666] line-through price-font ml-[14px]" v-if="Number(detail.market_price)">￥{{ detail.market_price }}</div>
									</div>
									<div class="text-[12px] leading-[16px]">
										<span class="text-[#999] oppoSans-R">{{ t('totalSales') }} {{ detail.goods.sale_num }}{{ detail.goods.unit }}</span>
									</div>
								</div>
								
								<div class="flex items-center justify-between" v-if="couponList.length">
									<div class="flex flex-nowrap overflow-hidden">
										<template v-for="(item, index) in couponList" :key="index">
											<div class="square bg-primary text-[12px] whitespace-nowrap  px-[10px] leading-[24px] text-[#fff] mr-[10px] mt-[20px] ">
												<span>{{ item.title }}</span>
											</div>
										</template>
									</div>
									<div class="text-[12px] cursor-pointer text-primary mt-[20px] flex-shrink-0 ml-[40px]" @click="couponListShow = true">领劵</div>
								</div>
							</div>
						</div>
						<div class="flex items-center mb-[20px]" v-if="detail.label_info">
							<span class="text-[14px] w-[80px] mr-[10px] text-[#999]">{{ t('goodsLabel') }}</span>
							<div class="flex flex-nowrap overflow-hidden max-w-[540px]">
								<template v-for="(item, index) in detail.label_info" :key="index">
									<div  class="tag-item px-[10px] leading-[20px]  whitespace-nowrap border-[1px] border-solid border-[var(--el-color-primary)] text-[var(--el-color-primary)] mr-[10px]">
										{{ item.label_name }}
									</div>
								</template>
							</div>
						</div>
						<div class="flex items-center mb-[20px]" v-if="detail.brand_info">
							<span class="text-[14px] w-[80px] mr-[10px] text-[#999]">{{ t('goodsBrand') }}</span>
							<div class="flex-1 text-[14px] text-[#666]">{{detail.brand_info.brand_name}}</div>
						</div>
						<div class="flex items-center  mb-[20px]" v-if="detail.service && detail.service.length">
							<span class="text-[14px] w-[80px] mr-[10px] text-[#999]">{{t('service')}}</span>
							<div class="flex-1 text-[14px] text-[#666] truncate max-w-[540px]">
								<template v-for="(item,index) in detail.service">
									<span>{{item.service_name}}</span>
									<span v-if="index < detail.service.length-1">，</span>
								</template>
							</div>
						</div>
						<!-- 规格 -->
						<template v-for="(item, index) in goodsDetail.goodsSpec" :key="index">
							<div class="flex">
								<div class="h-[32px] leading-[32px] text-[14px] text-[#999] w-[80px] box-border flex-shrink-0  mr-[10px] truncate">{{ item.spec_name }}</div>
								<div class="flex items-center flex-wrap">
									<template v-for="(subItem, subIndex) in item.values" :key="subIndex">
										<div class="px-[20px] h-[36px] text-[12px] leading-[36px] box-border border-[1px] border-solid border-[#d3d3d3] mr-[14px] mb-[14px] relative cursor-pointer rounded-[var(--rounded-small)] hover:border-[var(--el-color-primary)] hover:text-[var(--el-color-primary)]"
											:class="{ '!border-[var(--el-color-primary)] text-[var(--el-color-primary)] ': subItem.selected }" @click="change(subItem, index)">
											<span>{{ subItem.name }}</span>
										</div>
									</template>
								</div>
							</div>
						</template>
						<div class="flex mb-[20px]">
							<div class="h-[32px] leading-[32px] text-[14px] text-[#999] w-[80px]  box-border flex-shrink-0 mr-[10px]">{{ t('num') }}</div>
							<div class="flex items-center">
								<el-input-number v-model.number="buyNum"  :max="maxBuy" :min="minBuy" :step="1" step-strictly   size="large" @blur="inputNum(buyNum)"/>
								<div class="text-[14px] ml-[14px] text-[#5a5a5a]">
									<span>{{t('stock')}}{{ detail.stock }}{{ t('unit') }}</span>
									<span  v-if="maxBuyShow > 0 && minBuyShow > 1">({{ minBuyShow }}{{ detail.goods.unit }}起售，限购{{ maxBuyShow }}{{ detail.goods.unit }})</span>
									<span v-else-if="maxBuyShow > 0">(限购{{ maxBuyShow }}{{ detail.goods.unit }})</span>
									<span  v-else-if="minBuyShow > 1" >({{ minBuyShow }}{{ detail.goods.unit }}起售)</span>
								</div>
							</div>
						</div>
						<div class="flex items-center ">
							<div class="flex items-center" v-if="detail.goods.status == 1">
								<block v-if="isSell">
									<block v-if="maxBuy > 0 || maxBuy == -1">
										<!-- 立即购买 -->
										<el-button v-if="isShowSingleSku" plain  @click="buyFn('buy_now')" class="w-[150px] !h-[50px] !border-primary !text-primary !text-[16px] !rounded-[100px] !ml-[0]  mr-[10px] oppoSans-M">{{t('buyNow')}}</el-button>
										<!-- 加入购物车 -->
										<el-button v-if="goodsDetail.goods.goods_type == 'real' || (goodsDetail.goods.goods_type == 'virtual' && goodsDetail.goods.virtual_receive_type != 'verify')"  type="primary" class="!h-[50px] !w-[150px]  !text-[16px] !rounded-[100px] !m-0 oppoSans-M" @click="buyFn('join_cart')">
											<span class="text-[#fff]">{{t('addCart')}}</span>
										</el-button>
										<!-- 已售罄 -->
										<el-button v-if="!isShowSingleSku" class="w-[150px] !h-[50px] !border-none !bg-[#ccc] !ml-[10px] !text-[#fff]  !text-[16px] !rounded-[100px] oppoSans-M">{{ t('soldOut') }}</el-button>
									</block>
									<el-button v-else-if="isShowSingleSku && maxBuy == 0" class="w-[150px] !h-[50px] !border-none !bg-[#ccc] !ml-[10px] !text-[#fff]  !text-[16px] !rounded-[100px] oppoSans-M">超出限购数量</el-button>
								</block>
								<!-- 该商品规格未售卖 -->
								<el-button v-else class="w-[150px] !h-[50px] !border-none !bg-[#ccc] !ml-[10px] !text-[#fff]  !text-[16px] !rounded-[100px] oppoSans-M">该商品规格未售卖</el-button>
							</div>
							<div v-else>
								<!-- 该商品已下架 -->
								<el-button class="w-[150px] !h-[50px] !border-none !bg-[#ccc] !m-0 !text-[16px] !rounded-[100px] oppoSans-M">
									<span class="text-[#fff]">{{ t('goodsDelist') }}</span>
								</el-button>
							</div>
							<div class="ml-[10px] border-[1px] border-solid border-[var(--el-color-primary)] rounded-[4px]">
								<el-popover placement="bottom" :width="weappImage ? 326 : 170" trigger="hover">
									<template #reference>
										<div class="h-[46px] w-[46px] flex items-center justify-center cursor-pointer">
											<span class="iconfont icon-fenxiang !text-[24px] !text-[var(--el-color-primary)] "></span>
										</div>
									</template>
									<div class="text-center flex items-center justify-center mb-[8px]">
										<span class="mx-[10px]">{{ t('codesShare') }}</span>
									</div>
									<div class="flex items-center">
										<div class="promote-img flex justify-center items-center bg-[#f8f8f8] w-[146px] h-[146px] mr-[10px]" v-if="weappImage">
											<el-image :src="img(weappImage)" />
										</div>
										<div class="promote-img flex justify-center items-center bg-[#f8f8f8] w-[146px] h-[146px]">
											<el-image :src="wapImage" />
										</div>
									</div>
								</el-popover>
							</div>
							<div class="mx-[20px] w-[1px] h-[16px] bg-[#dcdfe6]"></div>
							<div>
								<div class="cursor-pointer" v-if="!isCollect" @click="collectFn">
									<span class="iconfont icon-shoucang1 !text-[16px] text-[#333] mr-[6px]"></span>
									<span class="text-[14px] text-[#333] oppoSans-R">{{ t('collect') }}</span>
								</div>
								<div class="cursor-pointer" v-else @click="collectFn">
									<span class="iconfont icon-yishoucang !text-[16px] text-[var(--el-color-primary)] mr-[6px]"></span>
									<span class="text-[14px] text-[var(--el-color-primary)] oppoSans-R ">{{ t('collected') }}</span>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="mt-[70px] flex">    
					<div class="w-[210px] mr-[20px]">
						<div class="flex flex-col w-full p-[20px] bg-[#fff] border-[1px] border-[#eee] border-solid box-border rounded-[var(--rounded-mid)]">
							<el-image class="w-[60px] h-[60px] rounded-[50%] mx-auto" :src="img(detail.shop_info.front_end_logo ? detail.shop_info.front_end_logo : '')" fit="cover">
								<template #error>
									<img src="@/assets/images/shop_default.png" alt="">
								</template>
							</el-image>
							<div class="w-[170px] flex-center mt-[16px] mb-[20px]">
								<span class="w-[32px] h-[18px] leading-[18px] text-center text-[#fff] bg-[var(--el-color-primary)] rounded-[2px] px-[4px] mr-[6px] text-[12px] flex-shrink-0" v-if="detail.shop_info.is_self">{{ t('self') }}</span>
								<span class="text-[14px] truncate">{{ detail.shop_info.site_name }}</span>
							</div>
							<div>
								<div class="border-0 border-b-[1px] border-[#efefef] border-dashed">
									<div class="flex items-center text-[12px] mt-[8px] mb-[20px]">
										<span class="text-[#999] mr-[15px]">{{ t('shopType') }}</span>
										<span class="text-[#333]">{{ detail.shop_info.category_name }}</span>
									</div>
									<div class="flex items-center text-[12px] mb-[20px]">
										<span class="text-[#999] mr-[15px] flex-shrink-0">{{ t('phone') }}</span>
										<span class="text-[#333] max-w-[100px] truncate">{{ detail.shop_info.phone }}</span>
									</div>
								</div>
								<div class="flex items-center justify-between  mt-[20px]">
									<div  class="w-[78px] h-[30px] leading-[28px] mr-[10px] text-[12px] border-solid border-[1px] border-[#ccc] text-center rounded-full cursor-pointer" @click="goShopDetail(detail.shop_info.site_id)">{{ t('shopAround') }}</div>
									<div class="w-[78px] h-[30px] leading-[28px] text-[12px] border-solid border-[1px] border-primary text-primary text-center rounded-full cursor-pointer"
										v-if="!isFollow" @click="collectShop(detail.shop_info.site_id, 1)">{{ t('followShop') }}</div>
									<div
										class="w-[78px] h-[30px] leading-[30px] bg-primary text-[#fff] text-[12px] text-center rounded-full cursor-pointer"
										v-else @click="collectShop(detail.shop_info.site_id, 0)">{{ t('followed') }}</div>
								</div>
							</div>
						</div>
						<div class="w-full box-border mt-[20px] pt-[20px] px-[20px] border-[1px] border-solid border-[#eee] rounded-[var(--rounded-mid)]">
							<div class="flex items-center justify-between mb-[10px]">
								<div class="w-[35px] h-[1px] bg-[#dcdfe6]"></div>
								<div class="text-[#5a5a5a]">{{ t('shopRecommend')}}</div>
								<div class="w-[35px] h-[1px] bg-[#dcdfe6]"></div>
							</div>
							<div>
								<div class="mb-[20px] cursor-pointer" v-for="(item,index) in recommendList" :key="index" @click="toDetail(item.goods_id)">
									<div class="w-[170px] h-[170px] rounded-[var(--rounded-med)] overflow-hidden">
										<el-image class="w-[170px] h-[170px] rounded-[var(--rounded-med)]" :src="img(item.goods_cover_thumb_mid ? item.goods_cover_thumb_mid : '')" fit="cover" />
									</div>
									<div>
										<div class="my-[15px] text-[14px] text-[#333] truncate">{{ item.goods_name }}</div>
										<div class="flex justify-between items-center">
											<div class="text-[var(--el-price)] price-font">
												<span class="text-[12px]">￥</span>
												<span class="text-[20px]">{{ item.goodsSku.price }}</span>
											</div>
											<div class="text-[#999] text-[12px]">{{ t('saled') }}{{ item.sale_num }}{{item.unit || t('unit')}}</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="w-[970px] mb-[62px] pb-5">
						<div class="flex items-center">
							<div class="cursor-pointer leading-[16px] px-[30px] text-[16px]" :class="{'text-primary':detailActiveName == 'detail'}" @click="hangeChange('detail')">{{ t('goodsDetail') }}</div>
							<div class="cursor-pointer leading-[16px] px-[30px] text-[16px]" :class="{'text-primary':detailActiveName == 'attribute'}" v-if="detail.goods && detail.goods.attr_format && Object.keys(detail.goods.attr_format).length" @click="hangeChange('attribute')">{{ t('goodsArguments') }}</div>
							<div class="cursor-pointer leading-[16px] px-[30px] text-[16px]" :class="{'text-primary':detailActiveName == 'evalate'}" @click="hangeChange('evalate')">{{ t('goodsEvaluate') }}</div>
						</div>
						<el-divider />
						<div v-if="detailActiveName == 'detail'" class="goods-detail-wrap">
							<div  v-html="detail.goods.goods_desc" v-if="detail.goods.goods_desc"></div>
						</div>
						<div v-else-if="detailActiveName == 'attribute'">
							<el-row>
								<template v-for="(item,index) in detail.goods.attr_format" :key="index">
									<el-col :span="12">
										<div class="flex items-center mb-[12px] leading-[32px] text-[14px]">
											<div class="w-[150px] text-[#999] mr-[12px] text-right  truncate">{{item.attr_value_name}}</div>
											<div class="input-width">{{ Array.isArray(item.attr_child_value_name) ? item.attr_child_value_name.join(',') : item.attr_child_value_name }}</div>
										</div>
									</el-col>
								</template>
							</el-row>
						</div>
						<div  v-else-if="detailActiveName == 'evalate'">
							<div class="">
								<div class="flex items-center justify-between">
									<div class="flex">
										<span v-for="(item, index) in evaluateSort" :key="index" @click="handelClick(item)"
											class="px-[26px] text-[14px] text-[#282828] leading-[34px] mr-[10px] cursor-pointer bg-[#f7f7f7] rounded-[var(--rounded-xl)]" :class="{ '!bg-[var(--el-color-primary)] !text-[#fff]': item.status == evaluateTableData.searchParam.currentIndex }">{{item.name }}</span>
									</div>
								</div>
								<div v-loading="evaluateTableData.loading" class="min-h-[300px]">
									<div v-if="evaluateTableData.data.length" class="mt-[30px]">
										<div>
											<div v-for="(item, index) in evaluateTableData.data" :key="index" class="box-border pb-[20px]">
												<div class="flex items-center justify-between">
													<div class="flex items-center">
														<img src="@/assets/images/user.png" v-if="item.is_anonymous == 1 || item.member_head == ''" class="rounded-full mr-[10px] w-[40px] h-[40px]">
														<el-image :src="img(item.member_head ? item.member_head : '')" v-else fit="cover" lazy class="rounded-full mr-[10px] w-[40px] h-[40px]">
															<template #error>
																	<img src="@/assets/images/user.png" class="rounded-full w-[40px] h-[40px]">
															</template>
														</el-image>
														<div class="ml-[10px]">
															<div class="text-[16px] text-[#333] leading-[16px] mb-[8px]">{{ item.member_name }}</div>
															<div class="text-[12px] text-[#999]">{{ item.create_time }}</div>
														</div>
													</div>
													<div>
														<el-rate v-model="item.scores" text-color="var(--el-color-primary)" :colors="['var(--el-color-primary)', 'var(--el-color-primary)', 'var(--el-color-primary)']"  disabled />
													</div>
												</div>
												<div class="border-b-[1px] border-dashed border-[#e3e3e3] ml-[52px] text-[#333] text-[14px]">
													<div class="my-[20px]">{{ item.content }}</div>
													<div class="flex flex-wrap" v-if="item.images && item.images.length">
														<div v-for="(subItem, subIndex) in item.images" :key="subIndex" class="mr-[8px] mb-[10px]">
															<el-image style="width: 86px; height: 86px" class="rounded-[var(--rounded-med)]" :src="img(subItem)" :zoom-rate="1.2" :max-scale="7" :min-scale="0.2" :initial-index="subIndex" :preview-src-list="item.imagesList" fit="cover" :hide-on-click-modal="true" />
														</div>
													</div>
													<div class="mt-[10px] mb-[30px] bg-[#f7f7f7] rounded-[var(--rounded-small)]" v-if="item.explain_first">
														<div class="text-[14px] text-[#333] py-[7px] px-[10px] break-all leading-[20px]">
															<span class="text-primary">{{ t('merchantReply') }}</span>
															<span>{{ item.explain_first}}</span>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class="mt-[16px] flex justify-end">
											<el-pagination v-model:current-page="evaluateTableData.page" v-model:page-size="evaluateTableData.limit" :total="evaluateTableData.total" @current-change="getEvaluateListFn" />
										</div>
									</div>
									<div v-else>
										<el-empty :description="t('noEvaluate')" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- 优惠券列表 -->
		<el-drawer v-model="couponListShow" size="380" direction="rtl" custom-class="!bg-[#F6F6F6]">
			<template #header>
				<div>{{t('coupon')}}</div>
			</template>
			<template #default>
				<div>
					<div class="mb-[15px] flex items-center coupon-bg w-[320px] rounded-[12px] " v-for="(item, index) in couponList" :key="index">
						<div class="flex flex-col items-center justify-center w-[96px] h-[94px]">
							<div class="text-[#fff] ">
								<span class="text-[14px] price-font">￥</span>
								<span class="text-[26px] price-font">{{ item.price }}</span>
							</div>
							<span class="text-[12px] mt-[3px] text-[#fff] truncate max-w-[86px] border-box px-[4px]">{{ Number(item.min_condition_money) ? ('满' + item.min_condition_money + '元可以使用') : '无门槛优惠券' }}</span>
						</div>
						<div class="ml-[10px] flex-1 flex flex-col">
							<span class="text-[16px] text-[#333] leading-[24px] truncate max-w-[150px]">{{ item.title }}</span>
							<span class="text-[#FF0004] text-[12px] my-[4px]">{{ item.type_name }}</span>
							<span class="text-[12px] text-[#999]  leading-[18px]">{{ item.valid_type == 1 &&('领取之日起' + item.length + '天内有效') || item.valid_type == 2 && ('领取之日起至' + item.valid_end_time) }}</span>
						</div>
						<span v-if="item.btnType === 'collecting'" class="bg-[#FB3434] w-[54px] text-center rounded-2xl text-[#fff] text-[12px] mr-[10px] py-[7px] cursor-pointer" @click="getCouponFn(item, index)">领取</span>
						<span v-else class="!bg-[#FF8C8C]  w-[54px] text-center rounded-2xl text-[#fff] text-[12px] mr-[10px] py-[7px]">{{item.btnType === 'collected' ? '已领完' : '已领取' }}</span>
					</div>
				</div>
			</template>
		</el-drawer>
	</div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, watch,onMounted } from 'vue'
import storage from '@/utils/storage'
import { getQrcode } from '@/app/api/index'
import { getGoodsDetail, getEvaluateList, collect, cancelCollect, getRecommendGoods, getGoodsQrcode } from '@/addon/mall/api/goods'
import { addBrowse } from '@/addon/mall/api/member'
import { getMallGoodsCoupon, getCoupon } from '@/addon/mall/api/coupon'
import { editShopCollect } from '@/addon/mall/api/shop'
import { useRoute, useRouter } from 'vue-router'
import useCartStore from '@/addon/mall/stores/cart'
import useMemberStore from '@/stores/member'
import useConfigStore from '@/stores/config'
import { img ,getToken} from '@/utils/common'
import QRCode from 'qrcode'


const route = useRoute()
const router = useRouter()
let id = route.query.id
let type = route.query.type
// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)
// 购物车数量
const cartStore = useCartStore();
cartStore.getList()
const cartList = computed(() => cartStore.cartList)

const detail = ref<any>({})
let imgActive = ref(0)
let loading = ref(true);
let imgListLeft = ref(0)
let detailActiveName = ref('detail')
let discountTime = ref(0) //限时折扣倒计时
const switchMedia = ref<any>('img');
const videoRef = ref<any>(null)

const configStore = useConfigStore()

// 商品详情
const getGoodsDetailFn = (data: any) => {
	getGoodsDetail(data).then((res: any) => {
		if (JSON.stringify(res.data) === '[]' || !res.data.goods) {
			ElMessage.error('找不到该商品')
			setTimeout(() => {
				router.push({path:'/'})
			}, 1000)
			return false
		}
		detail.value = res.data
		// 商品属性
		if(detail.value.goods.attr_format){
			detail.value.goods.attr_format = detail.value.goods.attr_format.filter((item: any, index: any) => {
				return Array.isArray(item.attr_child_value_name) ? item.attr_child_value_name.length : item.attr_child_value_name
			})
		}
		// 商品视频
		if (detail.value?.goods.goods_video != '') {
			switchMedia.value = 'video'
			detail.value.goods.goods_image_thumb_big.unshift(detail.value.sku_image_thumb_big)
			detail.value.goods.goods_image_thumb_small.unshift(detail.value.sku_image_thumb_small)
			setTimeout(() => {
				videoRef.value.play()
			}, 1000)
		}
		// 折扣信息
		if(Object.keys(detail.value.goods).length && detail.value.goods.is_discount && Object.keys(detail.value.discount_info).length){
			let now = new Date();
			let timestamp = now.getTime();
			discountTime.value = Date.now() + (detail.value.discount_info.active.end_time * 1000 - timestamp)

		}

		// 商品限购
		goodsMaxBuy(detail.value);

		isFollow.value = detail.value.member_info.is_follow //关注店铺
		isCollect.value = detail.value.goods.is_collect // 收藏商品
		loading.value = false
		evaluateTableData.searchParam.goods_id =  detail.value.goods_id
		page.value =  detail.value.goods_id
		getMallCouponListFn() // 获取优惠劵
		getEvaluateListFn()//评价列表
		getRecommendFn() // 店铺推荐
    	addBrowseFn() // 添加足迹
		loadQrcode()
	})
}

// 商品限购
const maxBuy = ref(-1);
const minBuy = ref(0); // 起售
const maxBuyShow = ref(0); // 限购
const minBuyShow = ref(0); // 起售

const goodsMaxBuy = (data:any = {})=>{
	// 限购 - 是否开启限购
	if(data.goods.is_limit){
		if(data.goods.max_buy){
			let max_buy = 0;
			if(data.goods.limit_type == 1){ //单次限购
				max_buy = data.goods.max_buy;
			}else{ // 单人限购
				let buyVal = data.goods.max_buy - (data.goods.has_buy||0);
				max_buy = buyVal > 0 ? buyVal : 0;
			}

			if(max_buy > data.goods.stock){
				maxBuy.value = data.goods.stock <= 0 ? (data.goods.min_buy || 1) : (data.goods.stock || 1)
			}else if(max_buy <= data.goods.stock){
				maxBuy.value = max_buy;
			}
			// 限购开启且最大购买变为零时，初始值也应该是零
			if(maxBuy.value == 0){
				buyNum.value = 0;
			}
		}
		// 仅用于展示
		maxBuyShow.value = data.goods.max_buy; // 限购
	}else{
		maxBuy.value = data.goods.stock <= 0 ? (data.goods.min_buy || 1) : (data.goods.stock || 1);
	}

	// 起售
	minBuy.value = data.goods.min_buy > 0 ? data.goods.min_buy : 1;

	// 起售大于库存，初始值也应该是零
	if((minBuy.value > data.goods.stock) && (data.goods.stock > 0)){
		buyNum.value = 0;
	}else{
		buyNum.value = minBuy.value;
	}
	// 仅用于展示
	minBuyShow.value = data.goods.min_buy;
}

// 判断单规格库存是否为0
const isShowSingleSku = computed(() => {
	let isSingleSpec = false // 是否为单规格，true：多规格，false：单规格
	detail.value.skuList.forEach((item: any, index: any) => {
		if (item.sku_spec_format) {
			isSingleSpec = true
		}
	})
	// 单规格，库存为0，显示已售罄
	if (!isSingleSpec && detail.value.stock <= 0) {
		return false;
	} else if (!isSingleSpec && detail.value.stock > 0) {
		// 单规格，库存大于0，可以购买
		return true;
	}
	return true;
})
// 数量改变
const inputNum = (val: any) => {
      if (val == undefined || val == null) {
        buyNum.value = 1
      }
    }

// 当前选中值
let currSpec = ref({
	skuId: "",
	name: []
})
let buyNum = ref(1)
const goodsDetail = computed(() => {
	let data = JSON.parse(JSON.stringify(detail.value))
	if (Object.keys(data).length) {
		if (!Object.keys(currSpec.value.name).length) currSpec.value.name = data.sku_spec_format.split(",")
		data.goodsSpec.forEach((item: any, index: number) => {
			let specName = item.spec_values.split(",");
			item.values = [];
			specName.forEach((specItem: any, specIndex: number) => {
				item.values[specIndex] = {};
				item.values[specIndex].name = specItem;
				item.values[specIndex].selected = false;
				item.values[specIndex].disabled = false;

				// 选中规格
				currSpec.value.name.forEach((currSpecItem, currSpecIndex) => {
					if (currSpecIndex == index && currSpecItem == specItem) {
						item.values[specIndex].selected = true;
					}
				})

			})
		})
		
		getSkuId()

		// 当前详情内容
		if (data.skuList && Object.keys(data.skuList).length) {
			data.skuList.forEach((idItem: any, idIndex: number) => {
				if (idItem.sku_id == currSpec.value.skuId) {
					data.detail = idItem;
					data.is_sell = idItem.is_sell;
				}
			})
		}
		if(currSpec.value.name) detectionGoodsIsDisable(data, currSpec.value.name)
	}
	return data;
})
const getSkuId = () => {
	detail.value.skuList.forEach((skuItem: any, skuIndex: any) => {
		if (skuItem.sku_spec_format == currSpec.value.name.toString()) {
			currSpec.value.skuId = skuItem.sku_id
			detail.value.skuList.forEach((item: any, index: any) => {
				if (item.sku_id == skuItem.sku_id) {
					Object.assign(detail.value, item);
				}
			})
		}
	})
}

// 切换规格
const change = (data: any, index:number) => {
	currSpec.value.name[index] = data.name;
	getSkuId();
	detectionGoodsIsDisable(goodsDetail.value, currSpec.value.name)
}

const isSell = ref(true)
const detectionGoodsIsDisable = (data: any, currSkuName: any)=>{
    const skuNameArr = []
    data.skuList.forEach((item, index) => {
        skuNameArr.push(item.sku_name)
    })
    isSell.value = true
    if(currSkuName && currSkuName.length && !skuNameArr.includes(currSkuName.join(' '))){
        isSell.value = false
    }
}

//图片list hover事件
const imgListHover = (index: number) => {
	if(detail.value?.goods.goods_video != '' && index == 0){
		switchMedia.value = 'video';
		videoRef.value.play();
	} else {
		videoRef.value.pause();
		switchMedia.value = 'img';
	}
	imgActive.value = index;
}
//图片list 上一页下一页点击事件
const imgListClick = (val: any) => {
	let maxLeft = (-100 * detail.value.goods.goods_image_thumb_small.length) + 490 > 0 ? 0 : (-100 * detail.value.goods.goods_image_thumb_small.length) + 490
	if (val == 'right') {
		imgListLeft.value = imgListLeft.value - 490 < maxLeft ? maxLeft : imgListLeft.value - 490
	} else {
		imgListLeft.value = imgListLeft.value + 490 > 0 ? 0 : imgListLeft.value + 490
	}
}
// 去店铺详情
const goShopDetail = (id: number) => {
	router.push({
		path: '/shop/index',
		query: {
			site_id: id
		}
	})
}

// 加入购物车
const buyFn = (type: string) => {
	if(buyNum.value < 1) return;

	// 检测是否登录
	if (!userInfo.value) {
		memberStore.logOpen()
		return false
	}
	// 加入购物车
	if (type == 'join_cart') {
		let num = 0;
		let limitNum = 0;
		let cartId = ""
		if (cartList.value['goods_' + goodsDetail.value.goods_id] && cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + goodsDetail.value.sku_id]) {
			num = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + goodsDetail.value.sku_id].num);
			cartId = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + goodsDetail.value.sku_id].id)
		}
		if (cartList.value['goods_' + goodsDetail.value.goods_id] && cartList.value['goods_' + goodsDetail.value.goods_id]) {
			limitNum = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id].totalNum);
		}
		num += buyNum.value;
		limitNum += Number(buyNum.value);
		/************** 限购-start **************/
		if(goodsDetail.value.goods.is_limit){
			let tips = `该商品单次限购${goodsDetail.value.goods.max_buy}件`;
			if(goodsDetail.value.goods.limit_type != 1){ //单次限购
				tips = `该商品每人限购${goodsDetail.value.goods.max_buy}件`;
				if(goodsDetail.value.goods.max_buy - maxBuy.value){
					tips += `,已购${goodsDetail.value.goods.max_buy - maxBuy.value}件`;
				}
			}
			if(limitNum > maxBuy.value){
				ElMessage.error(tips);
				return false;
			}
		}
		/************** 限购-end **************/
		cartStore.increase({
			id: cartId || '',
			goods_id: goodsDetail.value.goods_id,
			sku_id: goodsDetail.value.sku_id,
			stock: goodsDetail.value.stock,
			sale_price: goodsDetail.value.sale_price,
			site_id: goodsDetail.value.site_id,
			num: num
		}, 0, () => {
			ElMessage({
				message: '加入购物车成功',
				type: 'success',
			})
		})

	} else if (type == 'buy_now') {
		var data = {
			sku_id: goodsDetail.value.sku_id,
			num: buyNum.value
		}
		storage.set({ key: 'orderCreateData', data: { body: { [goodsDetail.value.shop_info.site_id]: { sku_data: [data], order_type: 'mall' } } } })
		router.push('/order/payment')
	}
}
// 收藏店铺
let isFollow = ref(0);
const collectShop = (id: any, is_follow: any) => {
	// 检测是否登录
	if (!userInfo.value) {
		memberStore.logOpen()
		return false
	}
	editShopCollect({
		site_id: id,
		is_follow: is_follow
	}).then(res => {
		isFollow.value = !isFollow.value
		if (isFollow.value) {
			ElMessage({
				message: '收藏成功',
				type: 'success',
			})
		} else {
			ElMessage({
				message: '取消收藏',
				type: 'success',
			})
		}
	})
}

// 收藏商品
let isCollect = ref(0)
const collectFn = () => {
	// 检测是否登录
	if (!userInfo.value) {
		memberStore.logOpen()
	}
	let api = isCollect.value ? cancelCollect({ goods_ids: [detail.value.goods_id] }) : collect(detail.value.goods_id);
	api.then(res => {
		isCollect.value = !isCollect.value;
		if (isCollect.value) {
			ElMessage({
				message: '收藏成功',
				type: 'success',
			})
		} else {
			ElMessage({
				message: '取消收藏成功',
				type: 'success',
			})
		}
	})
}

// 价格类型 //''=>原价，discount_price=>折扣价，member_price=>会员价
let priceType = computed(() => {
	let type = "";
	type = goodsDetail.value.show_type
	return type;
}) 

// 商品价格
let goodsPrice = computed(() =>{
	let price = "0.00";
	price = goodsDetail.value.show_price
	return price;
})


// 领取优惠劵
let couponListShow = ref<boolean>(false) //优惠券
let couponList = ref([]);
const getMallCouponListFn = () => {
	getMallGoodsCoupon({
        site_id: detail.value.shop_info.site_id,
        category_id: detail.value.goods.goods_category || '',
		mall_category_id: goodsDetail.value.goods.goods_mall_category || '',
        goods_id: detail.value.goods_id || '',
		brand_id: goodsDetail.value.goods.brand_id || ''
    }).then(res => {
		couponList.value = res.data.data.map((el: any) => {
			if (!userInfo.value) {
				if (el.sum_count != -1 && el.receive_count === el.sum_count) {
					el.btnType = 'collected'//已领完
				} else {
					el.btnType = 'collecting'//领用
				}
			} else {
				if (el.is_receive) {
					if (el.member_receive_count < el.limit_count) {
						if (el.need_receive) {
							el.btnType = 'collecting'//领用
						} else {
							el.btnType = 'using'//去使用
						}
					} else {
						if (el.need_receive) {
							el.btnType = 'used'//已使用
						} else {
							el.btnType = 'using'//去使用
						}
					}
				} else {
					if (el.sum_count != -1 && el.receive_count === el.sum_count) {
						el.btnType = 'collected'//已领完
					} else {
						el.btnType = 'collecting'//领用
					}
				}
			}
			return el
		});
	})
}

// 领取优惠券
const getCouponFn = (data:any) => {
	// 检测是否登录
	if (!userInfo.value) {
		memberStore.logOpen()
		couponListShow.value = false
		return false
	}
	getCoupon({
		coupon_id: data.id || '',
		number: 1,
	}).then(res => {
		getMallCouponListFn();
		couponListShow.value = false
	})
}

let page = ref('')
const wapImage = ref('')
const wapPreview = ref('')
const loadQrcode = () => {
    wapPreview.value = `${location.origin}/wap/addon/mall/pages/goods/detail?goods_id=${page.value}`
    // errorCorrectionLevel：密度容错率L（低）H(高)
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}
const weappImage = ref('')
const loadWeapp = (id:any) => {
	
	getQrcode({
        scene: 'goods_detail',
        type: 'image',
        channel: 'weapp',
        param: {
            goods_id: route.query.id
        }
    }).then((res:any) =>{
		weappImage.value = res.data
	})
}
loadWeapp(route.query.id)

const  hangeChange = (data:any)=>{
	detailActiveName.value = data
}

// 评论 start
let evaluateSort = ref<Array<any>>([{
	name: '全部',
	status: '',
	scores: []
},
{
	name: '好评',
	status: 'good_evaluate',
	scores: [4, 5]
},
{
	name: '中评',
	status: 'center_evaluate',
	scores: [2, 3]
},
{
	name: '差评',
	status: 'bad_evaluate',
	scores: [1]
}])

let evaluateTableData = reactive<any>({
	data: [],
	page: 1,
	limit: 10,
	total: 0,
	loading: false,
	searchParam:{
		goods_id: 0,
		currentIndex: '',
		scores: [],
	}
})


// 切换tab
const handelClick = (data: any) => {
	evaluateTableData.data = []
	evaluateTableData.searchParam.currentIndex = data.status
	evaluateTableData.searchParam.scores = []
	evaluateTableData.searchParam.scores = data.scores
	evaluateTableData.page = 1
	getEvaluateListFn()
}

// 评价列表
const getEvaluateListFn = (page:number = 1) => {
	evaluateTableData.page = page
	evaluateTableData.loading = true
	getEvaluateList({
		page: evaluateTableData.page,
		limit: evaluateTableData.limit,
		...evaluateTableData.searchParam
	}).then((res: any) => {
		evaluateTableData.total = res.data.total
		evaluateTableData.data = res.data.data.map((item: any) => {
			if(item.images && item.images.length){
				item.imagesList = item.images.map((el: any) => {
					return img(el)
				})
			}
			return item
		});
		evaluateTableData.loading = false
	}).catch(() => {
		evaluateTableData.loading = false
	})
}

// 店铺推荐
let recommendList = ref<Array<any>>([])
const getRecommendFn = () => {
	getRecommendGoods({ site_id: detail.value.site_id, limit: 4 }).then(res => {
		recommendList.value = res.data
	})
}

//添加足迹
const addBrowseFn = () => {
  addBrowse({ goods_id:detail.value.goods_id,sku_id:detail.value.sku_id }).then(res => {})
}

//路由跳转不刷新页面,重新加载数据
watch(() => route.query.id, (newId) => {
	if (newId && newId !== id.value) {
		id.value = newId
	}
})

// 组件初始化时加载数据
onMounted(() => {
	getGoodsDetailFn({goods_id:route.query.id})
})

const toDetail = (goods_id: number) => {
	router.push(`/goods/detail?id=${goods_id}`)
}

</script>

<style lang="scss" scoped>
.discount-bg{
	background-image: url(@/assets/images/addon/discount-bg.png);
	background-position: center center;
	background-size: 100% 100%;
	background-repeat: no-repeat;
}

.square{
	position: relative;
	::before{
		content: '';
		width: 6px;
		height: 6px;
		background-color: #f3f3f3;
		position: absolute;
		top: 50%;
		left: -3px;
		border-radius:3px;
		transform: translateY(-50%);
	}
	::after{
		content: '';
		width: 6px;
		height: 6px;
		background-color: #f3f3f3;
		position: absolute;
		top: 50%;
		right: -3px;
		border-radius:3px;
		transform: translateY(-50%);
	}
}
.coupon-bg{
	background-image: url(@/assets/images/addon/coupon-bg.png);
	background-position: center center;
	background-size: 100% 100%;
	background-repeat: no-repeat;
}
:deep(.page-form .el-form-item__label){
	color: #999;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}
/* 多行超出隐藏 */
.multi-hidden {
	word-break: break-all;
	text-overflow: ellipsis;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}


:deep(.el-input-number__decrease){
	background: #FAFCFF;
}
:deep(.el-input-number__increase){
	background: #FAFCFF;
}
:deep(.goods-detail-wrap img){
	width: 100% !important;
	height: auto!important;
}
</style>