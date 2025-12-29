<template>
    <view class="bg-[#fff] min-h-screen overflow-hidden" :style="themeColor()" v-if="Object.keys(detail).length">
        <view class="sidebar-margin">
            <view class="text-[24rpx] my-[30rpx]">订单号：{{ detail.order_no }}</view>
            <view class="mb-[50rpx]">
                <view class="flex" v-for="(item,index) in  goodsDataArr" :key="index">
                    <text v-if="item.disabled" class="self-center iconfont text-color text-[34rpx] mr-[32rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0" :class="{ 'iconxuanze1 ':item.checked,'bg-[#F5F5F5]':!item.checked}"></text>
                    <text v-else class="self-center iconfont text-color text-[34rpx] mr-[32rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0 box-border border-solid border-[2rpx]" :class="{ 'iconxuanze1 text-primary':item.checked,'border-[#ddd]':!item.checked}" @click="changeItem(item)"></text>
                    <view class="flex-1 flex">
                        <up-image class="rounded-[var(--goods-rounded-big)] overflow-hidden" width="100rpx" height="100rpx" :src="img(item.goods_image)" mode="aspectFill">
                            <template #error>
                                <image class="w-[100rpx] h-[100rpx] rounded-[var(--goods-rounded-big)] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
                            </template>
                        </up-image>
                        <view class="flex flex-1 flex-col justify-between ml-[20rpx]">
                            <view class="flex justify-between items-baseline">
                                <view class="text-[28rpx] truncate max-w-[400rpx] leading-[40rpx] text-[#333]">{{ item.goods_name }}</view>
                                <view class="text-right leading-[42rpx] ml-[10rpx] price-font">
                                    <text class="text-[22rpx]">￥</text>
                                    <text class="text-[32rpx] font-500">{{parseFloat(item.price).toFixed(2).split('.')[0] }}</text>
                                    <text class="text-[22rpx] font-500">.{{parseFloat(item.price).toFixed(2).split('.')[1] }}</text>
                                </view>
                            </view>
                             <view class="flex items-center justify-between mt-[14rpx]">
                                <view>
                                    <text v-if="item.sku_name" class="text-[22rpx]  text-[var(--text-color-light9)] truncate max-w-[320rpx] leading-[28rpx]">{{ item.sku_name }}</text>
                                </view>
                                <view class="text-right text-[26rpx]">x{{ item.num }}</view>
                            </view>
                        </view>
                    </view>
                </view>
            </view>
            <view class="border-0 border-t-[2rpx] border-solid border-[#eee] p-[20rpx] mb-[20rpx]">
                <view class="text-[24rpx] leading-[34rpx]">{{ detail.taker_name }}，{{ detail.taker_mobile }}，{{ detail.taker_full_address }}</view>
               
            </view>
            <u-form labelPosition="left" :model="formData" errorType='toast' :rules="rules" ref="formRef">
                <view class="delivery-wrap">
                    <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                        <u-form-item label="配送方式" prop="delivery_type" labelWidth="140rpx">
                            <view class="flex items-center flex-1" @click="showDeliveryTypePopup = true">
                                <view class="flex-1" :class="{'text-[var(--text-color-light9)]': !formData.delivery_type }" >{{ formData.delivery_type_name ? formData.delivery_type_name : '请选择发货方式' }}</view>
                                <text class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                            </view>
                        </u-form-item>
                    </view>
                    <template v-if="formData.delivery_type == 'express'">
                        <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                            <u-form-item label="物流公司" prop="express_company_id" labelWidth="140rpx">
                                <view class="flex items-center flex-1" @click="showPopup = true">
                                    <view class="flex-1" :class="{'text-[var(--text-color-light9)]': !formData.express_company_id }" >{{ formData.express_company_id_name ? formData.express_company_id_name : '请选择物流公司' }}</view>
                                    <text class="nc-iconfont nc-icon-youV6xx text-[30rpx] text-[var(--text-color-light9)]"></text>
                                </view>
                            </u-form-item>
                        </view>
                        <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                            <u-form-item label="物流单号" prop="express_number" labelWidth="140rpx">
                                <u-input fontSize="28rpx" v-model.trim="formData.express_number" border="none" clearable maxlength="25" placeholderStyle="color: #888" placeholder="请输入物流单号" />
                            </u-form-item>
                        </view>
                    </template>
                </view>
            </u-form>
        </view>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] footer w-full fixed bottom-0 left-0 right-0 box-border">
                <button hover-class="none" class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx] text-[26rpx] font-500"  @click="save"  :loading="operateLoading">确定</button>
            </view>
        </view>
        <!-- 发货方式 -->
        <u-popup :show="showDeliveryTypePopup" @close="showDeliveryTypePopup = false">
            <view class="popup-common" @touchmove.prevent.stop>
                <view class="title">发货方式</view>
                <scroll-view scroll-y="true" class="h-[400rpx] px-[30rpx] box-border">
                    <u-radio-group v-model="formData.delivery_type" placement="column" iconPlacement="right">
                       <u-radio activeColor="var(--primary-color)" :labelSize="'30rpx'" labelColor="#333"  :customStyle="{marginBottom: '30rpx'}" v-for="(item, index) in deliveryType" :key="index" :label="item" :name="index"></u-radio>
                    </u-radio-group>
                </scroll-view>
                <view class="btn-wrap">
                    <button class="primary-btn-bg btn" @click="changeDeliveryType">确定</button>
                </view>
            </view>
        </u-popup>
        <!-- 物流公司 -->
        <u-popup :show="showPopup" @close="showPopup = false">
            <view class="popup-common" @touchmove.prevent.stop>
                <view class="title">物流公司</view>
                <scroll-view scroll-y="true" class="h-[400rpx] px-[30rpx] box-border">
                    <u-radio-group v-model="formData.express_company_id" placement="column" iconPlacement="right">
                       <u-radio activeColor="var(--primary-color)" :labelSize="'30rpx'" labelColor="#333"  :customStyle="{marginBottom: '30rpx'}" v-for="(item, index) in companyData" :key="index" :label="item.company_name" :name="item.company_id"></u-radio>
                    </u-radio-group>
                </scroll-view>
                <view class="btn-wrap">
                    <button class="primary-btn-bg btn" @click="changeExpressCompany">确定</button>
                </view>
            </view>
        </u-popup>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getOrderDetail, getOrderDeliveryType, orderDelivery } from '@/addon/mall/api/order';
import { getCompanyList } from '@/app/api/delivery_site'
import { deepClone, img, redirect } from '@/utils/common';

const orderId = ref('')
const showType = ref('')
const loading = ref(false)
const detail = reactive<any>({})
const isHasVirtual = ref(false)
const deliveryType = ref([]) //配送方式
const goodsDataArr = ref<any>([]) //商品数据
const companyData = ref<any>([]) //物流公司
const formRef = ref<any>(null)
const formData = reactive<any>({
    order_id: 0,
    delivery_type: '',
    delivery_type_name: '',
    delivery_way: 'manual_write',
    express_company_id: '',
    express_company_id_name: '',
    express_number: '',
    order_goods_ids: [],
})

onLoad((options: any) => {
    orderId.value = options.order_id
    showType.value = options.type
    orderDetailFn(orderId.value)
})
// 物流公司
getCompanyList({}).then((res: any) => {
    companyData.value = res.data
})
const orderDetailFn = (id: any) => {
	loading.value = true;
	getOrderDetail(id).then((res: any) => {
        Object.assign(detail, res.data)
        formData.order_id = detail.order_id
        formData.delivery_type = ''
        isHasVirtual.value = false
		loading.value = false;
        getOrderDeliveryType({
            delivery_type: detail.delivery_type
        }).then((res: any) => {
            deliveryType.value = res.data
            for (const v in res.data) {
                formData.delivery_type = v
                formData.delivery_type_name = res.data[v]
                break
            }
            deliveryChange()
        })
        for (let i = 0; i < detail.order_goods.length; i++) {
            if (detail.order_goods[i].goods_type == 'virtual') {
                isHasVirtual.value = true
                break
            }
        }
        if (isHasVirtual.value) {
            Object.assign(deliveryType.value, { virtual: '虚拟发货' })
        }
	}).catch(() => {
		loading.value = false;
	})
}

const deliveryChange = () => {
    let arr: any = []
    if (formData.delivery_type && formData.delivery_type == 'virtual') {
        detail.order_goods.forEach((item: any) => {
            if (item.goods_type == 'virtual') {
                arr.push(item)
            }
        })
    } else if (formData.delivery_type && formData.delivery_type != 'virtual') {
        detail.order_goods.forEach((item: any) => {
            if (item.goods_type != 'virtual') {
                arr.push(item)
            }
        })
    }
    goodsDataArr.value = deepClone(arr)
    goodsDataArr.value.forEach((item: any) => {
    	if((item.status == 2 || item.delivery_status != 'wait_delivery' || item.status == 3) && (showType.value == 'add' || !showType.value)){
    		item.disabled = true
    	}else{
    		item.disabled = false
    	}
    })
    console.log(goodsDataArr.value)
}

// 发货方式
const showDeliveryTypePopup = ref(false)
const changeDeliveryType = () => {
    if (!formData.delivery_type) {
        uni.showToast({
            title: '请选择发货方式',
            icon: 'none'
        })
        return
    }
    formData.delivery_type_name = deliveryType.value[formData.delivery_type]
    showDeliveryTypePopup.value = false
    deliveryChange()
}


// 物流公司
const showPopup = ref(false)
const changeExpressCompany = () => {
    if (!formData.express_company_id) {
        uni.showToast({
            title: '请选择物流公司',
            icon: 'none'
        })
        return
    }
    const company = companyData.value.find((item: any) => item.company_id == formData.express_company_id)
    formData.express_company_id_name = company.company_name
    showPopup.value = false
}


// 选择商品
const changeItem = (item: any) => {
	item.checked = !item.checked
    formData.order_goods_ids = goodsDataArr.value.filter((v: any) => v.checked).map((v: any) => v.order_goods_id)
    if (showType.value == 'edit') {
        formData.delivery_ids = goodsDataArr.value.filter((v: any) => v.checked).map((v: any) => v.delivery_id)
    }
}

const rules = computed(() => {
    return {
        'delivery_type': {
            type: 'string',
            required: true,
            message: '请选择配送方式',
            trigger: ['blur', 'change'],
        },
        'express_company_id':{
            validator(rule: any, value: any, callback: any) {
                if (formData.delivery_type == 'express' && value === '') {
                    callback(new Error('请选择物流公司'))
                } else {
                    callback()
                }
            }
        },
        'express_number': {
        validator(rule: any, value: any, callback: any) {
                if (formData.delivery_type == 'express' && formData.delivery_way == 'manual_write' && value === '') {
                    callback(new Error('请输入物流单号'))
                } else {
                    callback()
                }
            }
        }
    }
})

const operateLoading = ref(false)
const save = () => {
    if (formData.order_goods_ids.length <= 0) {
        uni.showToast({
            title: '请选择要发货的商品',
            icon: 'none'
        })
        return
    }
    formRef.value.validate().then(() => {
        
        if (operateLoading.value) return
        operateLoading.value = true

        orderDelivery(formData).then(res => {
            operateLoading.value = false
            initFormData()
            if (getCurrentPages().length > 1) {
                uni.navigateBack({
                    delta: 1
                });
            } else {
                redirect({url: '/addon/mall/pages/order/list'});
            }
        }).catch(() => {
            operateLoading.value = false
        })
    })
}

const  toAddress = () => {
	redirect({url: '/addon/mall/pages/order/edit_address', param: {order_id: detail.order_id}})
}

const initFormData = () => {
    formData.order_id = 0
    formData.delivery_type = ''
    formData.express_company_id = ''
    formData.express_company_id_name = ''
    formData.express_number = ''
    formData.order_goods_ids = []
}
</script>

<style lang="scss" scoped>
.delivery-wrap :deep(.u-form-item__body__left__content__label) {
    font-size: 28rpx !important;
}
.footer {
    height: calc(100rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(100rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>