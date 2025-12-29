<template>
    <u-popup :show="show" @close="show = false" mode="bottom" :round="10"  :safeAreaInsetBottom="true">
        <view @touchmove.prevent.stop class="popup-common">
			<view class="title">请填写发票信息</view>
			<scroll-view :scroll-y="true"  class="h-[50vh]">
				<view class="px-[var(--popup-sidebar-m)] pb-[60rpx] pt-0 text-sm">
					<u-form labelPosition="left" :model="formData" labelWidth="200rpx" :labelStyle="{'font-size': '28rpx'}" errorType='toast' :rules="rules" ref="formRef">
						<view class="mt-[10rpx]">
							<u-form-item label="需要发票" leftIconStyle="text-[28rpx]">
								<view class="flex">
									<view
										class="h-[60rpx] box-border rounded px-[30rpx] leading-[56rpx] mr-[20rpx] border-[2rpx] border-[var(--temp-bg)] bg-[var(--temp-bg)] border-solid text-[24rpx]"
										:class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': !need}"
										@click="need = false">不需要</view>
									<view class="h-[60rpx] box-border rounded px-[30rpx] leading-[56rpx] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)]"
										:class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': need}"
										@click="need = true">需要</view>
								</view>
							</u-form-item>
						</view>
						<view v-show="need">
							<view class="mt-[10rpx]">
								<u-form-item label="发票类型">
									<view class="flex" v-if="formData.header_type == 1">
										<view class="h-[60rpx] box-border leading-[56rpx] text-[24rpx]">增值税电子普通发票</view>
									</view>
									<view class=" flex-1 flex justify-between items-center" v-if="formData.header_type == 2" @click="invoiceTypePopup = true">
										<view class="h-[60rpx] box-border leading-[56rpx] text-[24rpx]">{{  formData.type == 1 ? '增值税电子普通发票' : '增值税专用发票'}}</view>
										<text class="nc-iconfont nc-icon-youV6xx text-[26rpx] text-[var(--text-color-light6)]"></text>
									</view>
								</u-form-item>
								<u-popup :show="invoiceTypePopup">
									<view class="px-[30rpx] pb-[30rpx] bg-[#f5f5f5]" @touchmove.prevent.stop>
										<view class="flex items-center h-[90rpx] justify-between">
											<text>发票类型选择</text>
											<text class="iconfont iconguanbi" @click="invoiceTypePopup = false"></text>
										</view>
										<view class="h-[450rpx] mt-[20rpx]">
											<u-radio-group v-model="invoiceType" placement="column">
												<view class="flex items-center bg-[#fff] rounded-[16rpx] px-[30rpx] py-[34rpx] mb-[16rpx]">
													<u-radio activeColor="var(--primary-color)" :name="1"></u-radio>
													<view class="ml-[20rpx]">
														<view class="text-[28rpx]">增值税电子普通发票</view>
														<view class="text-[22rpx] mt-[10rpx] text-[#909090]">默认发送至所提供的电子邮件</view>
													</view>
												</view>
												<view class="flex items-center bg-[#fff] rounded-[16rpx] px-[30rpx] py-[34rpx] mb-[16rpx]">
													<u-radio activeColor="var(--primary-color)"  :name="2"></u-radio>
													<view class="ml-[20rpx]">
														<view class="text-[28rpx]">增值税专用发票</view>
														<view class="text-[22rpx] mt-[10rpx] text-[#909090]">纸质发票开出后将以邮寄形式交付</view>
													</view>
												</view>
											</u-radio-group>
										</view>
										<u-button type="primary" class="mt-[40rpx]" shape="circle" @click="invoiceTypePopupFn">确定</u-button>
									</view>
								</u-popup>
							</view>
							<view class="mt-[10rpx]">
								<u-form-item label="抬头类型">
									<view class="flex">
										<view class="h-[60rpx] box-border rounded px-[30rpx] mr-[20rpx] leading-[56rpx] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)]"
											:class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': formData.header_type == 1}"
											@click="formData.header_type = 1,formData.type = 1,invoiceType = 1">个人</view>
										<view class="h-[60rpx] box-border rounded px-[30rpx] leading-[56rpx] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)]"
											:class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': formData.header_type == 2}"
											@click="formData.header_type = 2">企业</view>
									</view>
								</u-form-item>
							</view>
							<view class="mt-[10rpx]" v-if="currentConfig && currentConfig.config && currentConfig.config.invoice && currentConfig.config.invoice.invoice_content">
								<u-form-item label="发票内容" prop="name">
									<view class="flex flex-wrap" >
										<block v-for="(item,index) in currentConfig.config.invoice.invoice_content">
											<view class="h-[60rpx] box-border rounded px-[30rpx] leading-[56rpx] border-[2rpx] border-[var(--temp-bg)] border-solid text-[24rpx] bg-[var(--temp-bg)] mb-[20rpx]" 
												:class="{'!bg-[var(--primary-color-light)] !text-[var(--primary-color)] !border-primary': formData.name == item,'mr-[20rpx]': (currentConfig.config.invoice.invoice_content - 1) != index}"
												@click="formData.name = item">{{ item  }}</view>
										</block>
									</view>
								</u-form-item>
							</view>
							<view class="mt-[10rpx]">
								<u-form-item label="邮箱" prop="email">
									<u-input fontSize="28rpx" v-model.trim="formData.email" border="none" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" clearable placeholder="请输入邮箱号" ></u-input>
								</u-form-item>
							</view>
							<view class="mt-[10rpx]">
								<u-form-item label="发票抬头" prop="header_name">
									<u-input fontSize="28rpx"  v-model.trim="formData.header_name" border="none" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" clearable placeholder="请输入发票抬头" ></u-input>
								</u-form-item>
							</view>
							<view v-if="formData.header_type == 2">
								<view class="mt-[10rpx]">
									<u-form-item label="纳税人识别号" prop="tax_number">
										<u-input fontSize="28rpx" v-model.trim="formData.tax_number" border="none" clearable placeholder="请输入纳税人识别号" maxlength="50" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" @change="inputChange"></u-input>
									</u-form-item>
								</view>
								<view class="mt-[10rpx]">
									<u-form-item label="注册地址">
										<u-input fontSize="28rpx"  v-model="formData.address" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable placeholder="(选填)请输入企业注册地址"></u-input>
									</u-form-item>
								</view>
								<view class="mt-[10rpx]">
									<u-form-item label="注册电话" >
										<u-input fontSize="28rpx" v-model="formData.telephone" border="none" clearable 
										placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" placeholder="(选填)请输入企业注册电话"></u-input>
									</u-form-item>
								</view>
								<view class="mt-[10rpx]">
									<u-form-item label="开户银行">
											<u-input  fontSize="28rpx"  v-model="formData.bank_name" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable placeholder="(选填)请输入企业开户银行"></u-input>
									</u-form-item>
								</view>
								<view class="mt-[10rpx]">
									<u-form-item label="银行账号">
										<u-input fontSize="28rpx" v-model="formData.bank_card_number" placeholder-class="!text-[var(--text-color-light9)] text-[28rpx]" border="none" clearable  placeholder="(选填)请输入企业开户银行账号"></u-input>
									</u-form-item>
								</view>
							</view>
						</view>
					</u-form>
				</view>
			</scroll-view>
			<view class="btn-wrap">
				<button class="primary-btn-bg btn" @click="confirm">确认</button>
			</view>
		</view>
    </u-popup>
</template>

<script setup lang="ts">
import { ref, computed,nextTick } from 'vue'


const show = ref(false)
const currentConfig = ref({}) // 当前发票配置
const need = ref(false)
const optionalShow = ref(false)
const formData = ref<any>({
	email: '',
	header_type: 1,
	header_name: '',
	type: 1,
	name: '',
	tax_number: '',
	telephone: '',
	address: '',
	bank_name: '',
	bank_card_number: ''
})
const inputChange = (e: any) =>{
	nextTick(()=>{
		formData.value.tax_number = e.replace(/[^0-9a-zA-Z]/g,'')
	})
}

const formRef :any = ref(null)

const rules = computed(() => {
	return {
		'email': [{
			type: 'string',
			required: need.value,
			message: '请输入邮箱号',
			trigger: ['blur', 'change'],
		},
			{
				validator(rule:any, value:any, callback:any) {
					const limit = /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/;
					if (!limit.test(value) && need.value) {
						callback(new Error('请输入正确的邮箱号'))
					} else {
						callback()
					}
				}
			}],
		'header_name': {
			type: 'string',
			required: need.value,
			message: '请输入发票抬头',
			trigger: ['blur', 'change'],
		},
		'tax_number': [{
			type: 'string',
			required: need.value && formData.value.header_type == 2,
			message: '请输入纳税人识别号',
			trigger: ['blur', 'change'],
		},
			{
				validator(rule:any, value:any, callback:any) {
					const limit = /^[0-9a-zA-Z]+$/;
					if (!limit.test(value) && formData.header_type == 2) {
						callback(new Error('请输入正确的纳税人识别号'))
					} else {
						callback()
					}
				}
			}]
		
	}
})

const open = (data:any) => {
	currentConfig.value = uni.$u.deepClone(data)
	data.config.invoice.invoice_content.length && (formData.value.name = data.config.invoice.invoice_content[0])
	show.value = true
}

const emits = defineEmits(['confirm'])

const confirm = () => {
	formRef.value.validate().then(() => {
		const invoice = need.value ? formData.value : {}
		const invoiceData = {
			site_id: currentConfig.value.site_id,
			invoice: invoice
		}
		emits('confirm', invoiceData)
		show.value = false
	})
}

// 发票类型
const invoiceTypePopup = ref(false)
const invoiceType = ref(1) 
const invoiceTypePopupFn = () =>{
	formData.value.type = invoiceType.value
	invoiceTypePopup.value = false
}

defineExpose({
	open
})
</script>

<style lang="scss" scoped>

</style>
