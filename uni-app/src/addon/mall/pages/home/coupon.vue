<template>
    <view class="bg-[var(--page-bg-color)] min-h-[100vh] overflow-hidden" :style="themeColor()">
        <view>
            <shop-head></shop-head>
        </view>
        <mescroll-body ref="mescrollRef"  @init="mescrollInit" :down="{ use: false }"  @up="getShopCouponListFn">
			<view class="py-[var(--top-m)] sidebar-margin" v-if="couponList.length">
                <template v-for="(item, index) in couponList">
                    <view class="card-template flex items-center justify-between mb-[20rpx]">
                        <view class="flex-1">
                            <view class="flex items-center mb-[20rpx]">
                                <text class="w-[90rpx] text-center bg-[#FFEAEA] whitespace-nowrap text-[var(--primary-color)] text-[22rpx] h-[36rpx] leading-[36rpx] text-center rounded-[8rpx] mr-[20rpx] flex-shrink-0">{{ item.type_name }}</text>
                                <text class="text-[30rpx] font-500 truncate max-w-[300rpx] ">{{ item.title }}</text>
                            </view>
                            <view class="text-[22rpx] text-[var(--text-color-light9)] mb-[20rpx]">
                                <text v-if="item.min_condition_money === '0.00'">无门槛</text>
                                <text v-else>满{{ item.coupon_min_price }}元可用</text>
                            </view>
                            <view class="text-[24rpx] text-[var(--text-color-light9)]">
                                <text v-if="item.valid_type == 1">领取之日起{{ item.length || '' }}天内有效</text>
                                <text v-else> 有效期至{{ item.valid_end_time ? item.valid_end_time.slice(0, 10) : '' }}</text>
                            </view>
                        </view>
                        <view class="flex-shrink-0 ml-[20rpx]">
                            <view class="flex items-baseline justify-center text-[var(--price-text-color)] mb-[20rpx]">
                                <text class="text-[24rpx] leading-[34rpx] text-center font-400 price-font mr-[4rpx]">￥</text>
                                <text class="text-[44rpx] font-500 text-left leading-[62rpx] max-w-[136rpx] truncate price-font">{{ item.coupon_price }}</text>
                            </view>
                            <button v-if="item.btnType === 'collected'" class="flex-center" :style="{width:'136rpx',height:'46rpx',color:'#fff', fontSize:'24rpx', padding:'0',backgroundColor:'#FFB4B1', border:'none' ,opacity :'1',borderRadius:'30rpx'}"  disabled>已领完</button>
                            <button v-if="item.btnType === 'collecting'" @click.stop="collecting(item.id, index)"  class="flex-center" :style="{width:'136rpx',height:'46rpx',color:'#fff', fontSize:'24rpx', padding:'0', backgroundColor:'var(--primary-color)',border:'none',borderRadius:'30rpx'}" >立即领取</button>
                            <button v-if="item.btnType === 'using'" @click.stop="toLink(item.id)" class="flex-center" :style="{width:'136rpx',height:'46rpx',color:'var(--primary-color)', fontSize:'24rpx', padding:'0',backgroundColor:'transparent',border:'2rpx solid var(--primary-color)',borderRadius:'30rpx'}" >去使用</button>
                             <button v-if="item.btnType === 'used'" class="flex-center" :style="{width:'136rpx',height:'46rpx',color:'#fff', fontSize:'24rpx', padding:'0',backgroundColor:'#FFB4B1', border:'none' ,opacity :'1',borderRadius:'30rpx'}"  disabled>已使用</button>
                        </view>
                    </view>
                </template>
            </view>
            <mescroll-empty v-if="!couponList.length && loading" :option="{tip : '暂无优惠券'}"></mescroll-empty>
	    </mescroll-body>
        <site-tabbar :site-id="siteId"  v-if="siteId" />
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { img, redirect } from '@/utils/common';
import { useLogin } from '@/hooks/useLogin';
import useMemberStore from '@/stores/member'
import { getMallCouponList, getCoupon } from '@/app/api/marketing'
import shopHead from '@/addon/mall/pages/home/components/shop-head/shop-head.vue'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app';

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)
const couponList: any = ref([])
const loading = ref(false);
const siteId = ref(0);
onLoad((option: any) => {
    siteId.value = option.site_id;
})
const getShopCouponListFn = (mescroll: any) => {
    loading.value = false;
    let data: object = {
        page: mescroll.num,
        limit: mescroll.size,
        site_id: siteId.value,
    }

    getMallCouponList(data).then((res:any) => {
        let newArr = Object.values(res.data.data).map((el: any) => {
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
    //设置列表数据
    if (mescroll.num == 1) {
        couponList.value = []; //如果是第一页需手动制空列表
    }
    couponList.value = couponList.value.concat(newArr);
    mescroll.endSuccess(newArr.length);
    loading.value = true;
    }).catch(() => {
        loading.value = true;
        mescroll.endErr(); // 请求失败, 结束加载
    })
}

const collecting = (coupon_id: any, index: number) => {
    if (!userInfo.value) {
        useLogin().setLoginBack({ url: '/addon/mall/pages/site/index', param: { site_id: siteId.value } })
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
	redirect({ url: '/addon/mall/pages/shop/goods_list', param: { site_id: siteId.value, coupon_id: id } })
}
</script>

<style lang="scss" scoped>
.background-size{
    background-repeat: no-repeat;
    background-position: right top;
    background-size: 27%;
}	
</style>