<template>
    <view :style="warpCss">
        <view class="p30 box-border" :class="{'pb120':diyComponent.isShowAccount}">
            <!-- #ifdef MP-WEIXIN -->
            <view :style="navbarInnerStyle"></view>
            <!-- #endif -->
            <view v-if="info" class="flex items-center">
                <!-- 唤起获取微信 -->
                <view class="relative w-[140rpx] flex-shrink-0 overflow-visible"  :class="{'border-[2rpx] border-solid border-[#eee] rounded-full': info.vip_card_buy.is_open}">
                    <u-avatar :src="img(info.headimg)" :size="'140rpx'" leftIcon="none" :default-url="img('static/resource/images/default_headimg.png')" @click="clickAvatar" />
                    <view v-if="info.vip_card_buy.is_open" class="absolute bottom-[-8rpx] left-0 right-0 flex-center z-100">
                        <view  class="flex items-center flex-shrink-0 whitespace-nowrap h-[32rpx] bg-[#eee] px-[12rpx] rounded-[20rpx] text-[18rpx] text-[#333] box-border">
                            <image :src="img('static/resource/images/diy/member/VIP_06.png')" mode="aspectFit" class="w-[20rpx] h-[16rpx] align-middle flex-shrink-0 mr-[4rpx]" />
                            <text class="flex-shrink-0 mt-[2rpx] max-w-[120rpx] truncate">{{ info.vip_card_buy.card_name }}</text>
                        </view>
                    </view>
                </view>
                
                <view class="ml-[20rpx] flex-1">
                    <view class="text-white flex items-baseline flex-wrap" :class="{'baseline':info.mobile, 'center': !info.mobile}" :style="{ color : diyComponent.textColor }">
                        <view class="text-[32rpx] truncate max-w-[320rpx] font-500 leading-[38rpx]">{{ info.nickname }}</view>
                        <view class="text-[26rpx] font-400 leading-[28rpx] ml-[10rpx]" v-if="info.mobile">{{ info.mobile.replace(info.mobile.substring(3, 7), "****") }}</view>
                        <!-- #ifdef H5 -->
                        <view v-if="!info.mobile" @click="bindMobileFn"
                              class="text-[22rpx] ml-[10rpx] px-[6rpx] border-[1rpx] border-solid border-[#E3E4E9] rounded-[8rpx] h-[34rpx] flex-center" :style="diyComponent.textColor ? { boxShadow: '0 0 0 1rpx ' + diyComponent.textColor, border: 'none' } : {}">
                            {{ t('bindMobile') }}
                        </view>
                        <!-- #endif -->

                        <!-- #ifdef MP-WEIXIN -->
                        <button v-if="!info.mobile" class="text-[22rpx] ml-[10rpx] bg-[#fff] px-[6rpx] border-[1rpx] border-solid border-[#E3E4E9] rounded-[8rpx] h-[37rpx] flex-center mr-0" :style="diyComponent.textColor ? { boxShadow: '0 0 0 1rpx ' + diyComponent.textColor, border: 'none' } : {}" open-type="getPhoneNumber" @getphonenumber="memberStore.bindMobile">{{ t('bindMobile') }}</button>
                        <!-- #endif -->

                    </view>
                    <view class="text-[#666] text-[24rpx] font-400 leading-[28rpx] mt-[14rpx]" :style="{ color : diyComponent.uidTextColor }">UID：{{ info.member_no }}</view>
                    <view class="flex items-center mt-[14rpx]">
                        <text class="bg-[#FFEBCD] rounded-[4rpx] rounded-br-[14rpx] px-[8rpx] leading-[32rpx] text-[22rpx] font-500 text-[#111]" @click="toLink('/app/pages/member/level')" v-if="info.member_level_name">{{ info.member_level_name }}</text>
                    </view>
                </view>
                <text @click="redirect({ url: '/app/pages/setting/index' })" class="nc-iconfont nc-icon-shezhiV6xx1 text-[38rpx] ml-[10rpx]" :style="{ color : diyComponent.textColor }"></text>
            </view>
            <view v-else class="flex items-center">
                <u-avatar :src="img('static/resource/images/default_headimg.png')" :size="'100rpx'" @click="toLogin" />
                <view class="ml-[20rpx] flex-1" @click="toLogin">
                    <view class="text-[32rpx] font-500 leading-[38rpx]" :style="{ color : diyComponent.textColor }">{{ t('login') }}/{{ t('register') }}</view>
                </view>
                <view @click="redirect({ url: '/app/pages/setting/index' })">
                    <text class="nc-iconfont nc-icon-shezhiV6xx1 text-[38rpx] ml-[10rpx]" :style="{ color : diyComponent.textColor }"></text>
                </view>
            </view>
            <view class="flex mt-[40rpx] items-center" v-if="diyComponent.isShowAccount">
                <view class="text-center w-[33.333%] flex-shrink-0">
                    <view class="text-[36rpx] mb-[20rpx] font-500 price-font">
                        <view @click="redirect({url: '/app/pages/member/balance'})" :style="{ color : diyComponent.textColor }">{{ money || 0 }}</view>
                    </view>
                    <view class="text-[22rpx] font-400">
                        <view @click="redirect({url: '/app/pages/member/balance'})" :style="{ color : diyComponent.accountTextColor }">{{ t('balance') }}</view>
                    </view>
                </view>
                <view class="text-center w-[33.333%] flex-shrink-0">
                    <view class="text-[36rpx] mb-[20rpx] font-500 price-font">
                        <view @click="redirect({url: '/app/pages/member/point'})" :style="{ color : diyComponent.textColor }">{{ parseInt(info?.point) || 0 }}</view>
                    </view>
                    <view class="text-[22rpx] font-400">
                        <view @click="redirect({url: '/app/pages/member/point'})" :style="{ color : diyComponent.accountTextColor }">{{ t('point') }}</view>
                    </view>
                </view>
                <view class="text-center w-[33.333%] flex-shrink-0" @click="redirect({ url: '/addon/mall/pages/member/my_coupon' })">
                    <view class="text-[36rpx] mb-[20rpx] font-500 price-font">
                        <view :style="{ color : diyComponent.textColor }">{{ couponCount || 0 }}</view>
                    </view>
                    <view class="text-[22rpx] font-400">
                        <view :style="{ color : diyComponent.accountTextColor }">{{ t('coupon') }}</view>
                    </view>
                </view>
            </view>
        </view>

        <!-- #ifdef MP-WEIXIN -->
        <information-filling ref="infoFillRef"></information-filling>
        <!-- #endif -->

        <!-- 强制绑定手机号 -->
        <bind-mobile ref="bindMobileRef" />

    </view>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'
import useMemberStore from '@/stores/member'
import { useLogin } from '@/hooks/useLogin'
import { getMyCouponCount } from '@/app/api/marketing'
import { img, isWeixinBrowser, redirect, urlDeconstruction, moneyFormat } from '@/utils/common'
import { t } from '@/locale'
import { wechatSync } from '@/app/api/system'
import useDiyStore from '@/app/stores/diy'
import useConfigStore from '@/stores/config'
import bindMobile from '@/components/bind-mobile/bind-mobile.vue';
import useSystemStore from "@/stores/system";

const props = defineProps(['component', 'index', 'global']);
const systemStore = useSystemStore()
const configStore = useConfigStore()
const diyStore = useDiyStore();

const diyComponent = computed(() => {
    if (diyStore.mode == 'decorate') {
        return diyStore.value[props.index];
    } else {
        return props.component;
    }
})

const warpCss = computed(() => {
    let style = '';
    if (diyComponent.value.componentStartBgColor) {
        if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${ diyComponent.value.componentGradientAngle },${ diyComponent.value.componentStartBgColor },${ diyComponent.value.componentEndBgColor });`;
        else style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
    }
    if (diyComponent.value.bgUrl) {
        style += 'background-image:url(' + img(diyComponent.value.bgUrl) + ');';
        style += 'background-size: 100%;';
        style += 'background-repeat: no-repeat;';
    }
    if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';
    if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded * 2 + 'rpx;';

    return style;
})

const memberStore = useMemberStore()

// #ifdef H5
const { query } = urlDeconstruction(location.href)
if (query.code && isWeixinBrowser()) {
    setTimeout(() => {
        wechatSync({ code: query.code }).then(res => {
            memberStore.getMemberInfo()
        })
    }, 1500)
}
// #endif

const info = computed(() => {
    // 装修模式
    if (diyStore.mode == 'decorate') {
        return {
            headimg: '',
            nickname: '昵称',
            member_level_name: '普通会员',
            balance: 0,
            point: 0,
            money: 0,
            mobile: '155****0549',
            member_no: 'NIU0000021',
            vip_card_buy: {
                is_open: 0,
                card_name: '付费会员',
            }
        }
    } else {
        getMyCouponCountFn()
		const data = memberStore ? memberStore.info : null
        return data;
    }
})

const money = computed(() => {
    if (info.value && (info.value.balance || info.value.money)) {
        let m = parseFloat(info.value.balance) + parseFloat(info.value.money)
        return moneyFormat(m.toString());
    } else {
        return 0;
    }
})

const toLogin = () => {
    let normalLogin = !configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile; // 未开启普通登录
    let authRegisterLogin = !configStore.login.is_auth_register; // 自动注册登录

    // #ifdef H5
    if (isWeixinBrowser()) {
        // 微信浏览器
        if (normalLogin && authRegisterLogin) {
            uni.showToast({ title: '商家未开启登录注册', icon: 'none' })
        } else if (configStore.login.is_username || configStore.login.is_mobile || configStore.login.is_bind_mobile) {
            useLogin().setLoginBack({ url: '/app/pages/member/index' })
        } else if (normalLogin && configStore.login.is_auth_register && configStore.login.is_force_access_user_info) {
            // 判断是否开启第三方自动注册登录，并且开启强制获取用户信息
            useLogin().getAuthCode({ scopes: 'snsapi_userinfo' })
        } else if (normalLogin && configStore.login.is_auth_register && !configStore.login.is_force_access_user_info) {
            // 判断是否开启第三方自动注册登录，并且关闭强制获取用户信息
            useLogin().getAuthCode({ scopes: 'snsapi_base' })
        }
    } else {
        // 普通浏览器
        if (normalLogin) {
            uni.showToast({ title: '商家未开启登录注册', icon: 'none' })
        } else if (configStore.login.is_username || configStore.login.is_mobile || configStore.login.is_bind_mobile) {
            useLogin().setLoginBack({ url: '/app/pages/member/index' })
        }
    }
    // #endif

    // #ifdef MP
    if (normalLogin && authRegisterLogin) {
        uni.showToast({ title: '商家未开启登录注册', icon: 'none' })
    } else if (configStore.login.is_username || configStore.login.is_mobile || configStore.login.is_bind_mobile) {
        useLogin().setLoginBack({ url: '/app/pages/member/index' })
    } else if (normalLogin && configStore.login.is_auth_register && !configStore.login.is_force_access_user_info) {
        // 判断是否开启第三方自动注册登录
        useLogin().getAuthCode()
    } else if (configStore.login.is_auth_register && configStore.login.is_force_access_user_info) {
        // 开启了第三方自动注册登录，但是需要强制获取昵称
        useLogin().setLoginBack({ url: '/app/pages/member/index' })
    } else if (configStore.login.is_auth_register && configStore.login.is_bind_mobile) {
        // 开启了第三方自动注册登录，但是需要强制获取手机号
        useLogin().setLoginBack({ url: '/app/pages/member/index' })
    }
    // #endif

}

const infoFillRef: any = ref(false)
const clickAvatar = () => {
    // #ifdef MP-WEIXIN
    infoFillRef.value.show = true
    // #endif

    // #ifdef H5
    if (isWeixinBrowser()) {
        useLogin().getAuthCode({ scopes: 'snsapi_userinfo' })
    } else {
        redirect({ url: '/app/pages/member/personal' })
    }
    // #endif
}

const couponCount = ref(0)
const getMyCouponCountFn = async() => {
    try {
        const res = await getMyCouponCount({ status: 1 })
        couponCount.value = res.data
    } catch (e) {
        couponCount.value = 0
    }

}

// 跳转链接
const toLink = (link: string) => {
    if (diyStore.mode == 'decorate') return false;
    redirect({ url: link })
}

//强制绑定手机号
const bindMobileRef: any = ref(null)
const bindMobileFn = () => {
    bindMobileRef.value.open()
}

// 导航栏内部盒子的样式
const navbarInnerStyle = computed(() => {
    let style = '';
    // 导航栏宽度，如果在小程序下，导航栏宽度为胶囊的左边到屏幕左边的距离
    // #ifdef MP
    if (props.global.topStatusBar.isShow == false) {
        style += 'height:' + systemStore.menuButtonInfo.height + 'px;';
        style += 'padding-top:' + systemStore.menuButtonInfo.top + 'px;';
    }
    // #endif
    return style;
})
</script>

<style lang="scss" scoped>
.p30{
    padding: 30rpx;
}
.pb120 {
    padding-bottom: 120rpx !important;
}
.baseline{
    align-items: baseline !important;
}
.center {
    align-items: center !important;
}
</style>
