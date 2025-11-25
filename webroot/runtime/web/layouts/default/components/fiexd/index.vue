<template>
	<div class="fixed bottom-[25%] right-[3%] w-[66px] z-20 cursor-pointer box-shadow">
		<div class="p-[6px] bg-[#fff] rounded-[12px]">
			<div class="mt-[8px] pb-[10px] border-b-[1px] border-solid-[#F6F6F6]">
				<el-popover placement="left" width="130px" trigger="hover" popper-class="!min-w-[130px]">
					<template #reference>
						<div>
							<div class="flex justify-center">
								<img src="@/assets/images/fiexd/mobile.png" class="w-[16px] h-[20px]" />
							</div>
							<div class="text-[12px] text-[#333] text-center mt-[10px]">手机购买</div>
						</div>
					</template>
					<div class="flex flex-wrap items-center justify-center">
						<div class="mb-[10px]" v-if="configStore.weappOpen">
							<div class="promote-img flex justify-center items-center w-[100px] h-[100px] rounded-[4px] mb-[10px] border-solid border-[1px] border-[#eee]">
								<el-image :src="img(weappImage)" class=" w-[92px] h-[92px]" />
							</div>
							<div class="text-center  flex items-center justify-center">
								<span class="text-[12px]">小程序商城</span>
							</div>
						</div>
						<div>
							<div class="promote-img flex justify-center items-center w-[100px] h-[100px] rounded-[4px] mb-[10px] border-solid border-[1px] border-[#eee]">
								<el-image :src="wapImage" class=" w-[92px] h-[92px]" />
							</div>
							<div class="text-center  flex items-center justify-center">
								<span class="text-[12px]">H5商城</span>
							</div>
						</div>
					</div>
				</el-popover>
			</div>
			<NuxtLink to="/goods/cart">
				<div class="mt-[14px] pb-[10px] border-b-[1px] border-solid-[#F6F6F6]">
					<div class="flex justify-center">
						<img src="@/assets/images/fiexd/cart.png" class="w-[20px] h-[20px]" />
					</div>
					<div class="text-[12px] text-[#333] text-center mt-[10px]">购物车</div>
				</div>
			</NuxtLink>
			<NuxtLink to="/order/list" v-if="getToken()">
				<div class="mt-[14px] pb-[10px] border-b-[1px] border-solid-[#F6F6F6]">
					<div class="flex justify-center ">
						<img src="@/assets/images/fiexd/member.png" class="w-[20px] h-[20px]" />
					</div>
					<div class="text-[12px] text-[#333] text-center mt-[10px]">我的订单</div>
				</div>
			</NuxtLink>
			<div class="mt-[14px] pb-[10px] border-b-[1px] border-solid-[#F6F6F6] cursor-pointer" v-else @click="toLogin">
				<div class="flex justify-center ">
					<img src="@/assets/images/fiexd/member.png" class="w-[20px] h-[20px]" />
				</div>
				<div class="text-[12px] text-[#333] text-center mt-[10px]">我的订单</div>
			</div>
			<div class="mt-[14px]" @click="scrollTop">
				<div class="flex justify-center"><img src="@/assets/images/fiexd/top.png" class="w-[18px] h-[16px]" />
				</div>
				<div class="text-[12px] text-[#333] text-center mt-[10px]">回到顶部</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
import QRCode from 'qrcode'
import { ref } from 'vue'
import { getToken } from '@/utils/common'
import useMemberStore from '@/stores/member'
import useConfigStore from '@/stores/config'
import { getQrcode } from '@/app/api/index'
const memberStore = useMemberStore()
const configStore = useConfigStore()
const scrollTop = () => {
	let main = document.getElementById("main");
	main.scrollTo({
		top: 0,
		behavior: "smooth"
	});
}
const wapPreview = ref('')
const wapImage = ref('')
const loadQrcode = () => {
	wapPreview.value = `${ location.origin }/wap/`
	// errorCorrectionLevel：密度容错率L（低）H(高)
	QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
		wapImage.value = url
	})
}
loadQrcode()

const weappImage = ref('')
const loadWeapp = () => {
	getQrcode().then((res:any) =>{
		weappImage.value = res.data
	})
}

watch(() => configStore.weappOpen, (nval, oval) => {
    if(nval){
        loadWeapp()
    }
})

if(configStore.weappOpen){
    loadWeapp()
}
// // 未登录
const toLogin = () =>{
	if(!getToken() && !configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile){
		ElMessage.error('商家未开启普通账号登录注册')
		return false
	}
	memberStore.logOpen()
}
</script>

<style lang="scss" scoped>
.box-shadow {
	box-shadow: 0 3px 20px rgba(0, 0, 0, .06)
}
</style>