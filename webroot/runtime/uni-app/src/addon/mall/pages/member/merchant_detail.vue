<template>
    <view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden record-list">
        <view class="card-template sidebar-margin my-[20rpx]">
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">店铺名称</view>
                <view class="text-[28rpx]">{{ detail.site_name }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">审核状态</view>
                <view class="text-[28rpx]">{{ detail.status_name }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">所属行业</view>
                <view class="text-[28rpx]">{{ detail.category_name }}</view>
            </view>
            <view class="justify-between card-template-item" v-if="detail.child_category_name">
                <view class="text-[28rpx]">子级行业</view>
                <view class="text-[28rpx]">{{ detail.child_category_name }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">商户信息</view>
                <view class="text-[28rpx]">{{ detail.user_name }} {{ detail.user_mobile }}</view>
            </view>
            <view class="justify-between card-template-item" v-if="detail.account">
                <view class="text-[28rpx]">商户账号</view>
                <view class="text-[28rpx]">{{ detail.account }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[28rpx]">申请时间</view>
                <view class="text-[28rpx]">{{ detail.create_time }}</view>
            </view>
            <view class="justify-between card-template-item !items-start">
                <view class="text-[28rpx] flex-shrink-0">营业执照</view>
                <view class="flex flex-wrap items-center">
                    <block v-for="(item,index) in detail.business_license?.split(',')">
                        <view class="w-[90rpx] h-[90rpx] ml-[20rpx] mb-[20rpx]" @click.stop="imgListPreview(detail.business_license,index)">
                            <up-image class="rounded-[10rpx] overflow-hidden " width="90rpx" height="90rpx" :src="img(item ? item : '')" model="aspectFill" >
                                <template #error>
                                    <u-icon name="photo" color="#999" size="30"></u-icon>
                                </template>
                            </up-image>
                        </view>
                    </block>
                </view>
            </view>
            <diy-form-detail  v-if="detail.form_record_id" :record_id="detail.form_record_id" completeLayout="style-2" @callback="getOrderDiyFormDetailCallback" />
        </view>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { img, } from '@/utils/common'
import { onLoad, onUnload } from '@dcloudio/uni-app';
import { getShopApply } from '@/addon/mall/api/merchant'
import diyFormDetail from '@/addon/components/diy-form-detail/index.vue'

const applyId = ref<any>('')
const detail = ref<any>({})
const loading = ref<boolean>(true); 
onLoad((options: any) => {
    applyId.value = options.apply_id ||  0;
    getShopApplyFn()
})

const getShopApplyFn = () => {
    loading.value = true;
    getShopApply(applyId.value).then((res: any) => {
        detail.value = res.data;
        loading.value = false;
    }).catch(() => {
        loading.value = false;
    });
}
// 订单表单信息
const diyFormData = ref([]);
const getOrderDiyFormDetailCallback = (data: any) => {
    if (data) {
        diyFormData.value = data;
    }
}
//预览图片
const imgListPreview = (item:any,index:number) => {
    let data = item.split(',').map((el:any) =>{
        return img(el)
    })
    if (!data.length) return false
    var urlList = []
    urlList = data 
    uni.previewImage({
		indicator: "number",
		current:index,
		loop: true,
		urls: urlList
	})
}
// 关闭预览图片
onUnload(()=>{
    // #ifdef  H5 || APP
    uni.closePreviewImage()
    // #endif
})
</script>

<style lang="scss">
@import '@/styles/diy_form.scss';
</style>