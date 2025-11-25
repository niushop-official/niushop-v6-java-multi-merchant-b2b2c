<template>
	<view class="w-screen h-screen" :style="themeColor()">
		<view v-if="isWeixinBrowser()">
			<view class="flex flex-col items-center justify-center h-[50vh]">
				<view class="">
					<image class="h-[90rpx] w-[300rpx]  rounded-[30rpx]" mode="aspectFit" :src="img(systemStore.site?.front_end_logo)"
						@error="systemStore.site.logo='static/resource/images/logo.png'"></image>
				</view>
				<view class="flex flex-col items-center my-[50rpx]">
					<view class="font-bold text-[32rpx]">
						即将登录微信账号
					</view>
					<view class="text-[24rpx] text-[#666] my-[20rpx]">
						请确认是否为本人操作
					</view>
				</view>
			</view>
			<view class="fixed bottom-0 w-full ">
				<view class="flex items-center flex-col justify-center footer">
					<button
						class="w-[75%] h-[80rpx] !bg-[var(--primary-color)] text-[26rpx] rounded-[40rpx] leading-[80rpx] font-500 !text-[#fff] !mx-[0]"
						loadingText="登录中" @click="wechatUserPcFn">微信授权登录</button>
					<view v-if="configStore.login.agreement_show" class="mt-[20rpx] flex items-center mb-[20rpx] py-[14rpx]"
						@click.stop="agreeChange">
						<u-checkbox-group @change="agreeChange">
							<u-checkbox activeColor="var(--primary-color)" :checked="isAgree" shape="circle" size="30rpx" />
						</u-checkbox-group>
						<view class="text-[26rpx] text-[var(--text-color-light6)] flex items-center flex-wrap">
							<text>{{ t('agreeTips') }}</text>
							<text @click.stop="redirect({ url: '/app/pages/auth/agreement?key=privacy' })"
								class="text-primary">《{{ t('privacyAgreement') }}》</text>
							<text>{{ t('and') }}</text>
							<text @click.stop="redirect({ url: '/app/pages/auth/agreement?key=service' })"
								class="text-primary">《{{ t('userAgreement') }}》</text>
						</view>
					</view>
				</view>
			</view>
			<u-popup :show="popupShow" mode="center" round="8" :safeAreaInsetBottom="false">
				<view
					class="bg-[#fff] flex flex-col justify-between w-[600rpx] min-h-[280rpx] rounded-[var(--rounded-big)] box-border px-[35rpx] pt-[35rpx] pb-[8rpx] relative">
					<view class="flex justify-center">
						<text class="text-[33rpx] font-700"> 用户协议及隐私保护</text>
					</view>
					<view class="flex items-center mb-[20rpx] mt-[20rpx] py-[20rpx]" @click.stop="agreeChange">
						<view class="text-[26rpx] text-[var(--text-color-light6)] flex items-center flex-wrap">
							<text>{{ t('agreeTips') }}</text>
							<text @click.stop="redirect({ url: '/app/pages/auth/agreement?key=privacy' })"
								class="text-primary">《{{ t('privacyAgreement') }}》</text>
							<text>{{ t('and') }}</text>
							<text @click.stop="redirect({ url: '/app/pages/auth/agreement?key=service' })"
								class="text-primary">《{{ t('userAgreement') }}》</text>
						</view>
					</view>
					<view>
						<view
							class="w-[100%] flex justify-center bg-[var(--primary-color)] h-[70rpx] leading-[70rpx] text-[#fff] text-[26rpx] border-[0] font-500 rounded-[50rpx]"
							@click="dialogConfirm">同意并登录</view>
						<view
							class="w-[100%] flex justify-center h-[70rpx] leading-[70rpx] text-[#999] text-[24rpx] border-[0] font-500 rounded-[50rpx]"
							@click="dialogClose">不同意</view>
					</view>
				</view>
			</u-popup>
		</view>
		<view class="flex flex-col items-center justify-center h-[50vh]" v-else>请使用微信扫码登录</view>
	</view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { img, isWeixinBrowser, redirect,} from '@/utils/common'
import { t } from '@/locale'
import useConfigStore from '@/stores/config'
import { webScanLogin, wechatUserPc, changeScanStatus } from '@/app/api/auth'
import { onLoad } from '@dcloudio/uni-app'
import useSystemStore from '@/stores/system'
import { useLogin } from '@/hooks/useLogin'

const login = useLogin()
const systemStore = useSystemStore()
const configStore = useConfigStore()
const loginConfig = computed(() => {
    return configStore.login
})

const wxchatCode = ref('')
const popupShow = ref(false)
const isAgree = ref(false)
const scanParams = ref({
	openid: '',
	unionid: '',
	nickname: '',
	avatar: '',
	key: uni.getStorageSync('pcLosinKey')
})
const agreeChange = () => {
	isAgree.value = !isAgree.value
}

const dialogClose = () => {
	popupShow.value = false
}

const dialogConfirm = () => {
	isAgree.value = true
	popupShow.value = false
	wechatUserPcFn()
}

const loginEr = ref('')
const changeScanStatusFn = () => {
	changeScanStatus({ key: scanParams.value.key }).then((successData) => {
		
	}).catch((err) => {
		uni.showToast({
			title: err.msg
		})
	})
}
onLoad(async (options : any) => {
	await systemStore.getSiteInfoFn()
	if (options.key) {
		uni.setStorageSync('pcLosinKey', options.key)
	}
	if (isWeixinBrowser() && !options.code) {
		oneClickLogin()
	}
	if (options.code) {
		wxchatCode.value = options.code
		changeScanStatusFn()
	}
})
const loginLoading = ref(false)
// 一键登录
const oneClickLogin = (callback : any = null, data : any = null) => {
	if (loginLoading.value) return
	loginLoading.value = true

	if (!callback) {
		callback = () => {
			loginLoading.value = false
		}
	}
	if (loginConfig.is_auth_register) {
		if (loginConfig.is_force_access_user_info) {
			// 开启强制获取用户信息
            login.getAuthCode({ scopes: 'snsapi_userinfo' }) // 强制获取用户信息
		} else if (!loginConfig.is_force_access_user_info) {
			// 关闭强制获取用户信息，昵称随机生成
            login.getAuthCode({ scopes: 'snsapi_base' }) // 静默获取
		}
	} else {
		login.getAuthCode({ scopes: 'snsapi_base' }) // 静默获取
	}
}
const wechatUserPcFn = () => {
	if (configStore.login.agreement_show && !isAgree.value) {
		popupShow.value = true
		return false;
	}
	if (!scanParams.value.openid) {
		wechatUserPc({ code: wxchatCode.value }).then((user_res) => {
			if (user_res.data) {
				let arr = JSON.parse(user_res.data.data)
				const [avatarUrl, nickname, openid, unionid] = arr;
				scanParams.value.openid = openid
				scanParams.value.unionid = unionid
				scanParams.value.nickname = nickname
				scanParams.value.avatar = avatarUrl
				webScanLoginFn()
			}
		}).catch((err) => {
			uni.showToast({
				title: err.msg,
				icon: 'none'
			})
		})
	} else {
		webScanLoginFn()
	}
}
const webScanLoginFn = () => {
	webScanLogin(scanParams.value).then((res) => {
		redirect({ url: '/app/pages/auth/pc_login_result' })
	}).catch((err) => {
		uni.showToast({
			title: err.msg,
			icon: 'none'
		})
	})
}
</script>

<style lang="scss" scoped>
.footer {
	padding-bottom: calc(60rpx + constant(safe-area-inset-bottom));
	padding-bottom: calc(60rpx + env(safe-area-inset-bottom));
}

:deep(.u-checkbox) {
	margin: 0 !important;
}
</style>