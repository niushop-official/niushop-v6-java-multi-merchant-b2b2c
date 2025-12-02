<template>
    <x-skeleton :type="skeleton.type" :loading="skeleton.loading" :config="skeleton.config" v-if="couponList && Object.keys(couponList).length > 0">
        <view :style="warpCss" class="overflow-hidden">
            <view class="py-[var(--top-m)] sidebar-margin">
                <template v-for="(item, index) in couponList">
					<view v-if="item.btnType === 'collected'" class="flex items-center relative w-[100%] rounded-[var(--rounded-big)] overflow-hidden bg-[#fff] py-[20rpx] background-size" :class="{'mt-[var(--top-m)]':index}" :style="{ backgroundImage: 'url(' + img('addon/mall/coupon/coupn_loot.png') + ')'}" >
						<view class="box-border flex-1 border-0 border-r-[1px] border-[#FFDCDC] border-dashed flex items-center">
							<view class="w-[164rpx] box-border flex justify-center">
								<view class="flex items-baseline text-[var(--price-text-color)]">
									<text class="text-[28rpx] leading-[34rpx] text-center font-400 price-font mr-[4rpx]">￥</text>
									<text class="text-[54rpx] font-500 text-left leading-[70rpx] max-w-[136rpx] truncate price-font">{{ item.coupon_price }}</text>
								</view>
							</view>
							<view class="flex-1 box-border ml-[10rpx]">
								<view class="text-[26rpx] leading-[42rpx] text-left font-500">
									<text v-if="item.min_condition_money === '0.00'">无门槛</text>
									<text v-else>满{{ item.coupon_min_price }}元可用</text>
								</view>
								<view class="mt-[10rpx] text-left flex items-center">
									<text class="w-[80rpx] text-center bg-[#FFEFF0] whitespace-nowrap text-[var(--primary-color)] text-[18rpx] h-[30rpx] leading-[30rpx] text-center rounded-[16rpx] mr-[10rpx] flex-shrink-0">{{ item.type_name }}</text>
									<text class="text-[24rpx] truncate max-w-[190rpx] leading-[30rpx] text-[var(--text-color-light6)]">{{ item.title }}</text>
								</view>
								<view class="w-[100%] mt-[6rpx] text-[20rpx] leading-[30rpx] text-[var(--text-color-light6)]">
									<text v-if="item.valid_type == 1">领取之日起{{ item.length || '' }}天内有效</text>
									<text v-else> 有效期至{{ item.valid_end_time ? item.valid_end_time.slice(0, 10) : '' }}</text>
								</view>
							</view>
						</view>
						<view class="pr-[20rpx] pl-[34rpx]">
							<button class="flex-center" :style="{width:'150rpx',height:'60rpx',color:'#fff', fontSize:'24rpx', padding:'0',backgroundColor:'#FFB4B1', border:'none' ,opacity :'1',borderRadius:'30rpx'}"  disabled>已领完</button>
						</view>
						<view class="absolute top-0 right-[194rpx]  h-[10rpx] w-[20rpx] rounded-br-[20rpx] rounded-bl-[20rpx] bg-[var(--page-bg-color)] "></view>
						<view class="absolute bottom-0 right-[194rpx] h-[10rpx] w-[20rpx] rounded-tr-[20rpx] rounded-tl-[20rpx] bg-[var(--page-bg-color)]"></view>
					</view>
					<view v-else class ="flex items-center relative w-[100%] rounded-[var(--rounded-big)] overflow-hidden bg-[#fff] py-[20rpx] background-size" :class="{'mt-[var(--top-m)]':index}">
						<view class="relative box-border flex-1 border-0 border-r-[1px] border-[#FFDCDC] border-dashed flex items-center pl-[10rpx]">
							<view class="w-[164rpx] box-border flex justify-center">
								<view class="flex items-baseline text-[var(--price-text-color)]">
									<text class="text-[28rpx] leading-[34rpx] text-center font-400 mr-[4rpx] price-font">￥</text>
									<text class="text-[54rpx] font-500 text-left leading-[70rpx] max-w-[136rpx] price-font">{{ item.coupon_price }}</text>
								</view>
							</view>
							<view class="flex-1 box-border ml-[10rpx]">
								<view class="text-[26rpx] leading-[42rpx] text-left font-500">
									<text v-if="item.min_condition_money === '0.00'">无门槛</text>
									<text v-else>满{{ item.coupon_min_price }}元可用</text>
								</view>
								<view class="mt-[10rpx] text-left flex items-center">
									<text class="w-[80rpx] bg-[#FFEFF0]  whitespace-nowrap text-[var(--primary-color)] text-[18rpx] h-[30rpx] leading-[30rpx] text-center rounded-[16rpx] mr-[10rpx] flex-shrink-0 box-border">{{ item.type_name }}</text>
									<text class="text-[24rpx] truncate max-w-[190rpx] leading-[30rpx] text-[var(--text-color-light9)]">{{ item.title }}</text>
								</view>
								<view class="w-[100%] mt-[6rpx] text-[20rpx] leading-[30rpx] text-[var(--text-color-light9)]">
									<text v-if="item.valid_type == 1">领取之日起<text>{{ item.length || '' }}</text>天内有效</text>
									<text v-else> 有效期至<text>{{ item.valid_end_time ? item.valid_end_time.slice(0, 10) : '' }}</text></text>
								</view>
							</view>
						</view>
						<view v-if="item.btnType === 'collecting'" @click.stop="collecting(item.id, index)" class="pr-[20rpx] pl-[34rpx]">
							<button class="flex-center" :style="{width:'150rpx',height:'60rpx',color:'#fff', fontSize:'24rpx', padding:'0', backgroundColor:'var(--primary-color)',border:'none',borderRadius:'30rpx'}" >立即领取</button>
						</view>
						<view v-if="item.btnType === 'using'" @click.stop="toLink(item.id)" class="pr-[20rpx] pl-[34rpx]">
							<button class="flex-center" :style="{width:'150rpx',height:'60rpx',color:'var(--primary-color)', fontSize:'24rpx', padding:'0',backgroundColor:'transparent',border:'2rpx solid var(--primary-color)',borderRadius:'30rpx'}" >去使用</button>
						</view>
						<view v-if="item.btnType === 'used'" class="pr-[20rpx] pl-[34rpx]">
							<button class="flex-center" :style="{width:'150rpx',height:'60rpx',color:'#fff', fontSize:'24rpx', padding:'0',backgroundColor:'#FFB4B1', border:'none' ,opacity :'1',borderRadius:'30rpx'}"  disabled>已使用</button>
						</view>

						<view class="absolute top-0 right-[194rpx]  h-[10rpx] w-[20rpx] rounded-br-[20rpx] rounded-bl-[20rpx] bg-[var(--page-bg-color)] "></view>
						<view class="absolute bottom-0 right-[194rpx] h-[10rpx] w-[20rpx] rounded-tr-[20rpx] rounded-tl-[20rpx] bg-[var(--page-bg-color)]"></view>
					</view>
				</template>
			</view>
        </view>
    </x-skeleton>
</template>
<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { img, redirect } from '@/utils/common';
import useDiyStore from '@/app/stores/diy';
import { useLogin } from '@/hooks/useLogin';
import useMemberStore from '@/stores/member'
import { getMallCouponComponents, getCoupon } from '@/addon/mall/api/coupon'

const props = defineProps(['component', 'index']);
const diyStore = useDiyStore();
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

const skeleton = reactive({
    type: 'banner',
    loading: diyStore.mode == 'decorate' ? false : true,
    config: {
        gridRows: 1,
        gridRowsGap: '0rpx',
        headHeight: '170rpx'
    }
})
const diyComponent = computed(() => {
    if (diyStore.mode == 'decorate') {
        return diyStore.value[props.index];
    } else {
        return props.component;
    }
})

const warpCss = computed(() => {
    var style = '';
    if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    return style;
})

const couponList: any = ref([])
const getShopCouponListFn = () => {
    let data: object = {
        num: diyComponent.value.num ||  10
    };
    if (diyStore.siteId) {
		data.site_id = diyStore.siteId;
		getMallCouponComponents(data).then((res:any) => {
			couponList.value = Object.values(res.data).map((el: any) => {
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
		})
			skeleton.loading = false;
		}).catch(() => {
		})
	}
}

onMounted(() => {
    refresh();
});

const refresh = () => {

    // 装修模式下设置默认图
    if (diyStore.mode == 'decorate') {
        let obj = {
            title: '满减券',
            type_name: '通用券',
            coupon_price: 100,
            coupon_min_price: 0,
            valid_type: 1,
            length: 10,
            btnType: 'collecting',
        };
        for (let i = 0; i < 2; i++) {
            couponList.value.push(obj);
        }
    } else {
        getShopCouponListFn();
    }
}

const collecting = (coupon_id: any, index: number) => {
    if (diyStore.mode == 'decorate') return;
    if (!userInfo.value) {
        useLogin().setLoginBack({ url: '/addon/mall/pages/index', param: { site_id: diyStore.siteId } })
        return false
    }
    getCoupon({ coupon_id, number: 1, type:'receive' }).then((res: any) => {
		if (res.code > 0) {
			couponList.value[index].member_receive_count += 1
			couponList.value[index].receive_count += 1
			if (couponList.value[index].member_receive_count == couponList.value[index].limit_count || (couponList.value[index].sum_count != -1 && couponList.value[index].receive_count === couponList.value[index].sum_count)
			) {
				couponList.value[index].btnType = 'using'
			}
		}
	})
}

const toLink = (id: any) => {
    if (diyStore.mode == 'decorate') return;
	redirect({ url: '/addon/mall/pages/shop/goods_list', param: { site_id: diyStore.siteId, coupon_id: id } })
}

</script>