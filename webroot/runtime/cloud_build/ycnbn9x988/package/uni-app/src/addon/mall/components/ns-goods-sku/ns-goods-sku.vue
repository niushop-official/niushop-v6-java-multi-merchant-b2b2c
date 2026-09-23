<template>
	<view @touchmove.prevent.stop>
		<u-overlay :show="goodsSkuPop" @click="closeFn" zIndex="490">
			<u-popup class="popup-type" :show="goodsSkuPop" @close="closeFn" mode="bottom" :overlay="false" zIndex="500">
				<view class="py-[32rpx] relative" v-if="goodsDetail.detail" @touchmove.prevent.stop>
					<view class="flex px-[32rpx] mb-[40rpx]">
						<view class="rounded-[var(--goods-rounded-big)] overflow-hidden w-[180rpx] h-[180rpx]">
							<up-image
								width="180rpx"
								height="180rpx"
								:radius="'var(--goods-rounded-big)'"
								:src="img(goodsDetail.detail.sku_image)"
								@click="imgListPreview(goodsDetail.detail.sku_image)"
								mode="aspectFill"
							>
								<template #error>
									<image
										class="w-[180rpx] h-[180rpx]"
										:src="img('static/resource/images/diy/shop_default.jpg')"
										mode="aspectFill"
									></image>
								</template>
							</up-image>
						</view>
						<view class="flex flex-1 flex-col justify-between ml-[24rpx] py-[10rpx]">
							<view class="w-[100%]">
								<view class="text-[var(--price-text-color)] flex items-baseline">
									<text class="text-[32rpx] font-bold price-font">￥</text>
									<text class="text-[48rpx] price-font">{{ parseFloat(goodsPrice).toFixed(2).split('.')[0] }}</text>
									<text class="text-[32rpx] mr-[6rpx] price-font">.{{ parseFloat(goodsPrice).toFixed(2).split('.')[1] }}</text>
									<image v-if="priceType() == 'svip_price'" class="max-w-[64rpx] h-[22rpx] ml-[6rpx]" :src="img('addon/mall/SVIP.png')" mode="heightFix" />
									<image
										class="h-[24rpx] ml-[6rpx] max-w-[44rpx]"
										v-if="priceType() == 'member_price'"
										:src="img('addon/mall/VIP.png')"
										mode="heightFix"
									/>
									<image
										class="h-[24rpx] ml-[6rpx] max-w-[80rpx]"
										v-if="priceType() == 'discount_price'"
										:src="img('addon/mall/discount.png')"
										mode="heightFix"
									/>
								</view>
								<view class="text-[26rpx] leading-[32rpx] text-[var(--text-color-light6)] mt-[20rpx]"
									>库存{{ goodsDetail.detail.stock }}{{ goodsDetail.goods.unit }}</view
								>
							</view>
							<view
								class="text-[26rpx] leading-[30rpx] text-[var(--text-color-light6)] w-[100%] max-h-[60rpx] multi-hidden"
								v-if="goodsDetail.goodsSpec && goodsDetail.goodsSpec.length"
								>已选规格：{{ goodsDetail.detail.sku_spec_format }}</view
							>
						</view>
					</view>
					<scroll-view class="h-[500rpx] px-[32rpx] box-border mb-[60rpx]" scroll-y="true">
						<view :class="{ 'mt20': 0 != index }" v-for="(item, index) in goodsDetail.goodsSpec" :key="index">
							<view class="text-[28rpx] leading-[36rpx] mb-[24rpx]">{{ item.spec_name }}</view>
							<view class="flex flex-wrap">
								<view class="relative box-border sku-item-wrap  text-center flex-center"
									:class="{'actvie': subItem.selected,
										'not-exist': (specListStatus[item.spec_id] && specListStatus[item.spec_id][subItem.name] && (specListStatus[item.spec_id][subItem.name]?.status == 'notExist'))
									}"
									v-for="(subItem, subIndex) in item.values"
									:key="subIndex"
									@click="change(subItem, index, item)"
									>{{ subItem.name }}
                                    <text class="stockout absolute " :class="{'actvie': subItem.selected }" v-if="specListStatus[item.spec_id] && specListStatus[item.spec_id][subItem.name] && (specListStatus[item.spec_id][subItem.name]?.status == 'outOfStock')">缺货</text></view>
							</view>
						</view>
						<view class="flex justify-between items-center my-[20rpx]">
							<view class="text-[28rpx]">购买数量</view>
							<text v-if="maxBuyShow > 0 && minBuyShow > 1" class="ml-[20rpx] mr-[auto] text-[24rpx] text-[var(--primary-color)]"
								>({{ minBuyShow }}{{ goodsDetail.goods.unit }}起售，限购{{ maxBuyShow }}{{ goodsDetail.goods.unit }})</text
							>
							<text v-else-if="maxBuyShow > 0" class="ml-[20rpx] mr-[auto] text-[24rpx] text-[var(--primary-color)]"
								>(限购{{ maxBuyShow }}{{ goodsDetail.goods.unit }})</text
							>
							<text v-else-if="minBuyShow > 1" class="ml-[20rpx] mr-[auto] text-[24rpx] text-[var(--primary-color)]"
								>({{ minBuyShow }}{{ goodsDetail.goods.unit }}起售)</text
							>
							<u-number-box :min="minBuy" :max="maxBuy" integer :step="1" input-width="68rpx" v-model="buyNum" input-height="52rpx">
								<template #minus>
									<view class="relative w-[30rpx] h-[30rpx]" @click="reduceNumChange">
										<text
											class="num-box nc-iconfont nc-icon-jianV6xx font-500 absolute flex items-center justify-center"
											:class="{ 'text-color-light9': buyNum <= minBuy }"
										></text>
									</view>
								</template>
								<template #input>
									<input
										class="text-[#303133] text-[28rpx] mx-[10rpx] w-[80rpx] h-[44rpx] bg-[var(--temp-bg)] leading-[44rpx] text-center rounded-[6rpx]"
										type="number"
										@input="goodsSkuInputFn"
										@blur="goodsSkuBlurFn"
										v-model="buyNum"
									/>
								</template>
								<template #plus>
									<view class="relative w-[30rpx] h-[30rpx]" @click="addNumChange">
										<text
											class="num-box nc-iconfont nc-icon-jiahaoV6xx font-500 absolute flex items-center justify-center"
											:class="{ 'text-color-light9': buyNum >= maxBuy }"
										></text>
									</view>
								</template>
							</u-number-box>
						</view>
						<view class="mt-[40rpx]">
							<diy-form
								ref="diyFormRef"
								:form_id="goodsDetail.goods.form_id"
								:form_sign="
									'goodsDetail_' +
									goodsDetail.sku_id +
									'_siteId_' +
									goodsDetail.site_id +
									'_memberId_' +
									userInfo?.member_id +
									'_formId_' +
									goodsDetail.goods.form_id
								"
								:storage_name="
									'diyFormStorageByGoodsDetail_' +
									goodsDetail.sku_id +
									'_siteId_' +
									goodsDetail.site_id +
									'_memberId_' +
									userInfo?.member_id
								"
							/>
						</view>
					</scroll-view>
					<view class="px-[20rpx]">
						<template v-if="isSell">
							<!-- #ifdef H5 -->
							<button
								v-if="goodsDetail.detail.stock > 0"
								hover-class="none"
								class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg"
								type="primary"
								@click="confirm"
							>
								确定
							</button>
							<!-- #endif -->
							<!-- #ifdef MP-WEIXIN -->
							<template v-if="goodsDetail.detail.stock > 0">
								<!-- <button v-if="isBindMobile && userInfo && !userInfo.mobile" hover-class="none" class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg" type="primary" open-type="getPhoneNumber" @getphonenumber="memberStore.bindMobile">确定</button>
                                <button v-else hover-class="none" class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg" type="primary" @click="confirm">确定</button> -->
								<button
									hover-class="none"
									class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 rounded-[50rpx] primary-btn-bg"
									type="primary"
									@click="confirm"
								>
									确定
								</button>
							</template>
							<!-- #endif -->

							<button
								hover-class="none"
								v-else
								class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 text-[#fff] bg-[#ccc] rounded-[50rpx]"
							>
								已售罄
							</button>
						</template>
						<button
							hover-class="none"
							v-else
							class="!h-[80rpx] leading-[80rpx] text-[26rpx] font-500 text-[#fff] bg-[#ccc] rounded-[50rpx]"
						>
							该商品规格未售卖
						</button>
					</view>
				</view>
			</u-popup>
		</u-overlay>
		<!-- 强制绑定手机号 -->
		<bind-mobile ref="bindMobileRef" />
	</view>
</template>

<script setup lang="ts">
import { ref, computed, toRaw } from 'vue'
import { img, redirect, getToken } from '@/utils/common'
import useCartStore from '@/addon/mall/stores/cart'
import { useLogin } from '@/hooks/useLogin'
import useMemberStore from '@/stores/member'
import bindMobile from '@/components/bind-mobile/bind-mobile.vue'
import { cloneDeep } from 'lodash-es'
import { t } from '@/locale'
import diyForm from '@/addon/components/diy-form/index.vue'
import useGoodsDetailStore from '@/addon/mall/stores/goodsDetail'

const props = defineProps(['goodsDetail'])
const goodsSkuPop = ref(false)
const callback: any = ref(null)
const currSpec = ref({
	skuId: '',
	name: [],
})
const openType = ref('')
const buyNum = ref(1)
const diyFormRef: any = ref(null)

const maxBuy = ref(0) // 限购
const minBuy = ref(0) // 起售
const maxBuyShow = ref(0) // 限购
const minBuyShow = ref(0) // 起售

// 商品价格
const goodsPrice = computed(() => {
	let price = '0.00'
	price = goodsDetail.value.detail.show_price
	return price
})

// 价格类型
const priceType = () => {
	let type = ''
	type = goodsDetail.value.show_type
	return type
}

// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

// 购物车数量
const cartStore = useCartStore()
cartStore.getList()
const cartList = computed(() => cartStore.cartList)

const open = (type = '', fn = '') => {
	openType.value = type
	goodsSkuPop.value = true
	callback.value = fn
}

const goodsSkuInputFn = () => {
	setTimeout(() => {
		buyNum.value = parseInt(buyNum.value)
		if (!buyNum.value || buyNum.value <= minBuy.value) {
			buyNum.value = minBuy.value || 1
		}
		if (buyNum.value >= maxBuy.value) {
			buyNum.value = maxBuy.value
		}
		// 起售大于库存，初始值也应该是零
		if (minBuy.value > goodsDetail.value.detail.stock) {
			buyNum.value = 0
		}
	}, 0)
}

const goodsSkuBlurFn = () => {
	setTimeout(() => {
		buyNum.value = parseInt(buyNum.value)
		if (!buyNum.value || buyNum.value <= minBuy.value) {
			buyNum.value = minBuy.value || 1
		}
		if (buyNum.value >= maxBuy.value) {
			buyNum.value = maxBuy.value
		}

		// 起售大于库存，初始值也应该是零
		if (minBuy.value > goodsDetail.value.detail.stock) {
			buyNum.value = 0
			if (goodsDetail.value.detail.stock <= 0) {
				uni.showToast({ title: '库存不足', icon: 'none' })
				return
			} else {
				uni.showToast({
					title: '商品库存小于起购数量',
					icon: 'none',
				})
			}
		}
	}, 0)
}

const closeFn = () => {
	goodsSkuPop.value = false
	useGoodsDetailStore().setGoodsDetail({ isOpenSkuBuy: false })
}

const goodsDetail = computed(() => {
	let data = cloneDeep(props.goodsDetail)
	console.log('goodsDetail', data)
	// 重组数据结构
	if (Object.keys(data).length) {
		if (!Object.keys(currSpec.value.name).length) currSpec.value.name = data.sku_spec_format.split(',')

		data.goodsSpec.forEach((item: any, index: any) => {
			let specName = item.spec_values.split(',')
			item.values = []

			specName.forEach((specItem: any, specIndex: any) => {
				item.values[specIndex] = {}
				item.values[specIndex].name = specItem
				item.values[specIndex].selected = false
				item.values[specIndex].disabled = false

				// 选中规格
				currSpec.value.name.forEach((currSpecItem, currSpecIndex) => {
					if (currSpecIndex == index && currSpecItem == specItem) {
						item.values[specIndex].selected = true
					}
				})
			})
		})
		getSkuId()

		// 当前详情内容
		if (data.skuList && Object.keys(data.skuList).length) {
			data.skuList.forEach((idItem: any, idIndex: any) => {
				if (idItem.sku_id == currSpec.value.skuId) {
					idItem.sku_image = idItem.sku_image ? idItem.sku_image : data.goods.goods_cover
					data.detail = idItem
					data.is_sell = idItem.is_sell
				}
			})
		}
		if (currSpec.value.name) detectionGoodsIsDisable(data, currSpec.value.name)
	}

	// 限购 - 是否开启限购
	if (data.goods?.is_limit) {
		if (data.goods.max_buy) {
			let max_buy = 0
			if (data.goods.limit_type == 1) {
				//单次限购
				max_buy = data.goods.max_buy
			} else {
				// 单人限购
				let buyVal = data.goods.max_buy - (data.goods.has_buy || 0)
				max_buy = buyVal > 0 ? buyVal : 0
			}
			if (max_buy > data.detail.stock) {
				maxBuy.value = data.detail.stock
			} else if (max_buy <= data.detail.stock) {
				maxBuy.value = max_buy
			}

			// 限购开启且最大购买变为零时，初始值也应该是零
			if (maxBuy.value == 0) {
				buyNum.value = 0
			}
		}
		// 仅用于展示
		maxBuyShow.value = data.goods.max_buy // 限购
	} else {
		maxBuy.value = data.detail?.stock
	}

	// 起售
	minBuy.value = data.goods?.min_buy > 0 ? data.goods?.min_buy : 1
	// 起售大于库存，初始值也应该是零
	if (minBuy.value > data.detail.stock) {
		buyNum.value = 0
	} else {
		buyNum.value = minBuy.value
	}
	// 仅用于展示
	minBuyShow.value = data.goods.min_buy
	setTimeout(() => {
		updateSpecListStatus()
	}, 100)
	return data
})

const change = (data: any, index: any, parentData: any) => {
    if(specListStatus.value[parentData.spec_id] && specListStatus.value[parentData.spec_id][data.name] && (specListStatus.value[parentData.spec_id][data.name]?.status == 'notExist')){
        uni.showToast({
            title: '该规格暂不支持销售',
            icon: 'none'
        });
        return false
    }
	currSpec.value.name[index] = data.name
	getSkuId() // 刷新当前规格信息
	detectionGoodsIsDisable(goodsDetail.value, currSpec.value.name)
}

const isSell = ref(true)
const detectionGoodsIsDisable = (data: any, currSkuName: any) => {
	const skuNameArr = []
	data.skuList.forEach((item, index) => {
		skuNameArr.push(item.sku_name)
	})
	isSell.value = true
	if (currSkuName && currSkuName.length && !skuNameArr.includes(currSkuName.join(' '))) {
		isSell.value = false
	}
}

const emits = defineEmits(['change', 'update'])
const getSkuId = () => {
	props.goodsDetail.skuList.forEach((skuItem: any, skuIndex: any) => {
		if (skuItem.sku_spec_format == currSpec.value.name.toString()) {
			currSpec.value.skuId = skuItem.sku_id
			emits('change', skuItem.sku_id)
		}
	})
}

const addNumChange = () => {
	if (goodsDetail.value.detail.stock <= 0) {
		uni.showToast({ title: '库存不足', icon: 'none' })
		return
	}
	if (minBuy.value && minBuy.value > goodsDetail.value.detail.stock) {
		uni.showToast({ title: '商品库存小于起购数量', icon: 'none' })
		return
	}
	if (goodsDetail.value.goods.is_limit) {
		let tips = `该商品单次限购${goodsDetail.value.goods.max_buy}件`
		if (goodsDetail.value.goods.limit_type != 1) {
			//单次限购
			tips = `该商品每人限购${goodsDetail.value.goods.max_buy}件`
			if (goodsDetail.value.goods.max_buy - maxBuy.value) {
				tips += `,已购${goodsDetail.value.goods.max_buy - maxBuy.value}件`
			}
		}
		if (buyNum.value >= maxBuy.value) {
			uni.showToast({ title: tips, icon: 'none' })
		}
	}
}

const reduceNumChange = () => {
	if (minBuy.value > 1) {
		let tips = `该商品起购${minBuy.value}件`
		if (buyNum.value <= minBuy.value) {
			uni.showToast({ title: tips, icon: 'none' })
		}
	}
}

//强制绑定手机号
const bindMobileRef: any = ref(null)
const isBindMobile = ref(uni.getStorageSync('isBindMobile'))
//提交
const confirm = () => {
	if (!diyFormRef.value.verify()) return

	if (buyNum.value < 1) return

	// #ifdef H5
	// 绑定手机号
	if (!userInfo.value && uni.getStorageSync('isBindMobile')) {
		uni.setStorage({
			key: 'loginBack',
			data: {
				url: '/addon/mall/pages/goods/detail',
				param: {
					sku_id: goodsDetail.value.sku_id,
					type: goodsDetail.value.type,
				},
			},
		})
		bindMobileRef.value.open()
		return false
	}
	// #endif

	// 检测是否登录
	if (!userInfo.value) {
		useLogin().setLoginBack({
			url: '/addon/mall/pages/goods/detail',
			param: {
				sku_id: goodsDetail.value.sku_id,
				type: goodsDetail.value.type,
			},
		})
		return false
	}

	// 加入购物车
	if (openType.value == 'join_cart') {
		let num = 0
		let limitNum = 0
		let cartId = ''
		if (
			cartList.value['goods_' + goodsDetail.value.goods_id] &&
			cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + currSpec.value.skuId]
		) {
			num = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + currSpec.value.skuId].num)
			cartId = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id]['sku_' + currSpec.value.skuId].id)
		}
		if (cartList.value['goods_' + goodsDetail.value.goods_id] && cartList.value['goods_' + goodsDetail.value.goods_id]) {
			limitNum = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id].totalNum)
		}

		num += Number(buyNum.value)
		limitNum += Number(buyNum.value)
		/************** 限购-start **************/
		if (goodsDetail.value.goods.is_limit) {
			let tips = `该商品单次限购${goodsDetail.value.goods.max_buy}件`
			if (goodsDetail.value.goods.limit_type != 1) {
				//单次限购
				tips = `该商品每人限购${goodsDetail.value.goods.max_buy}件`
				if (goodsDetail.value.goods.max_buy - maxBuy.value) {
					tips += `,已购${goodsDetail.value.goods.max_buy - maxBuy.value}件`
				}
			}
			if (limitNum > maxBuy.value) {
				uni.showToast({ title: tips, icon: 'none' })
				return false
			}
		}
		/************** 限购-end **************/

		cartStore.increase(
			{
				id: cartId || '',
				goods_id: goodsDetail.value.goods_id,
				sku_id: currSpec.value.skuId,
				stock: goodsDetail.value.stock,
				sale_price: goodsDetail.value.sale_price,
				site_id: goodsDetail.value.site_id,
				num: num,
			},
			0,
			() => {
				uni.showToast({
					title: '加入购物车成功',
					icon: 'none',
				})
			},
		)
	} else if(openType.value == 'update_cart'){
		let num = 0
		let limitNum = 0
		if (cartList.value['goods_' + goodsDetail.value.goods_id] && cartList.value['goods_' + goodsDetail.value.goods_id]) {
			limitNum = toRaw(cartList.value['goods_' + goodsDetail.value.goods_id].totalNum)
		}
		num += Number(buyNum.value)
		limitNum += Number(buyNum.value)
		/************** 限购-start **************/
		if (goodsDetail.value.goods.is_limit) {
			let tips = `该商品单次限购${goodsDetail.value.goods.max_buy}件`
			if (goodsDetail.value.goods.limit_type != 1) {
				//单次限购
				tips = `该商品每人限购${goodsDetail.value.goods.max_buy}件`
				if (goodsDetail.value.goods.max_buy - maxBuy.value) {
					tips += `,已购${goodsDetail.value.goods.max_buy - maxBuy.value}件`
				}
			}
			if (limitNum > maxBuy.value) {
				uni.showToast({ title: tips, icon: 'none' })
				return false
			}
		}
		/************** 限购-end **************/

		cartStore.increase(
			{
				id: goodsDetail.value.id || '',
				goods_id: goodsDetail.value.goods_id,
				sku_id: currSpec.value.skuId,
				stock: goodsDetail.value.stock,
				sale_price: goodsDetail.value.sale_price,
				site_id: goodsDetail.value.site_id,
				num: num,
			},
			0,
			() => {
				uni.showToast({
					title: '加入购物车成功',
					icon: 'none',
				})
				currSpec.value = {
					skuId: '',
					name: [],
				}
				emits('update')
			},
		)
	} else if (openType.value == 'buy_now') {
		// 立即购买
		const data = {
			sku_id: currSpec.value.skuId,
			num: buyNum.value,
		}
		let key = goodsDetail.value.shop_info.site_id
		let extend_data = {}
		extend_data.relate_id = goodsDetail.value.show_type == 'discount_price' ? goodsDetail.value.discount_info.discount_id : ''
		extend_data.activity_type = goodsDetail.value.show_type == 'discount_price' ? 'discount' : goodsDetail.value.detail.type
		uni.setStorage({
			key: 'orderCreateData',
			data: {
				body: {
					[key]: { sku_data: [data], order_type: 'mall', extend_data },
				},
			},
			success: () => {
				redirect({ url: '/addon/mall/pages/order/payment' })
			},
		})
	}

	closeFn()
}

//预览图片
const imgListPreview = (item: any) => {
	if (item === '') return false
	const urlList = []
	urlList.push(img(item)) //push中的参数为 :src="item.img_url" 中的图片地址
	uni.previewImage({
		indicator: 'number',
		loop: true,
		urls: urlList,
	})
}
const specListStatus = ref([])
const updateSpecListStatus = () => {
	const skuList = goodsDetail.value.skuList
	const specList = goodsDetail.value.goodsSpec
	let selectedArr = JSON.parse(JSON.stringify(currSpec.value.name))
	let selected = {}
	selectedArr.forEach((item, index) => {
		selected[specList[index]?.spec_id] = item
	})

	// 1. 获取规格顺序
	const specIds = specList.map((spec) => spec.spec_id)

	// 2. 构建 SKU 映射表，key 为 sku_name（格式如 "红 S"）
	const skuMap = new Map()
	skuList.forEach((sku) => {
		skuMap.set(sku.sku_name, sku)
	})

	// 3. 结果对象
	const result = {}

	// 4. 遍历每个规格，计算状态
	specList.forEach((spec) => {
		const specId = spec.spec_id
		const currentSelected = selected[specId]
		const specStatus = {}

		spec.values.forEach((value) => {
			const valName = value.name

            // 构建完整组合：当前规格用 valName，其他规格用选中的值
            const combination = {}
            specIds.forEach((id) => {
                combination[id] = id === specId ? valName : selected[id]
            })
            const key = specIds.map((id) => combination[id]).join(' ')
            const sku = skuMap.get(key)

            if (!sku) {
                specStatus[valName] = { status: 'notExist' }
            } else if (sku.stock === 0) {
                specStatus[valName] = { status: 'outOfStock' }
            } else {
                specStatus[valName] = { status: 'available' }
            }
		})

		result[specId] = specStatus
	})
	specListStatus.value = result
	return result // 返回更新后的 specList（原对象已被修改）
}

defineExpose({
	open,
})
</script>

<style lang="scss" scoped>
::v-deep .u-number-box .u-number-box__slot {
	display: flex;
	align-items: center;
}
::v-deep .uni-scroll-view {
	position: inherit;
}
.mt20 {
	margin-top: 20px;
}
.sku-item-wrap{
	background-color: var(--temp-bg);
	font-size: 24rpx;
	padding: 0 44rpx;
	height: 56rpx;
	margin-right: 20rpx;
	margin-bottom: 20rpx;
	border-radius: 50rpx;
	border: 1rpx solid var(--temp-bg);
	&.actvie {
		border-color: var(--primary-color) !important;
		color: var(--primary-color);
		background-color: var(--primary-color-light) !important;
	}
	&.not-exist { 
		color: #a8a8a8;
	}
}
.stockout{
	font-size: 16rpx;
	color: #fff;
	background-color: #CACACA;
	padding: 4rpx;
	top: -14rpx;
	right: 0; 
	border-radius: 5rpx;
	&.actvie{
		background-color: var(--primary-color) !important;
	}
}
.num-box{
	font-size: 30rpx;
	left: -8rpx;
	top: -8rpx;
	right: -8rpx;
	bottom: -8rpx;
}
.text-color-light9{
	color: var(--text-color-light9) !important;
}
</style>
