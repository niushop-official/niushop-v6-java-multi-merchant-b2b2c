<template>
    <view class="bg-[var(--page-bg-color)] min-h-screen overflow-hidden record-list" :style="themeColor()">
        <view class="fixed left-0 top-0 right-0 z-10" v-if="statusLoading">
			<scroll-view :scroll-x="true" class="tab-style-2">
				<view class="tab-content">
					<view class="tab-items" :class="{ 'class-select': recordState === item.value }" @click="recordStateFn(item.value)" v-for="(item, index) in recordStateList">{{ item.label }}</view>
				</view>
			</scroll-view>
		</view>

        <mescroll-body ref="mescrollRef" top="88rpx" @init="mescrollInit" :down="{ use: false }" @up="getRecordListFn">
			<view class="sidebar-margin pt-[var(--top-m)]" v-if="list.length">
				<template v-for="(item, index) in list" :key="index">
                    <view class="mb-[var(--top-m)] card-template" @click="toLink(item)">
                        <view class="pb-[10rpx] flex items-center justify-between border-0 border-b-[1rpx] border-solid border-[#F6F6F6] leading-[1]">
                            <view class="flex-shrink-0 mr-[40rpx]">
                                <text class="nc-iconfont nc-icon-cuohaoV6xx text-[32rpx] text-primary" v-if="item.status == -1"></text>
                                <text class="nc-iconfont nc-icon-duihaoV6xx text-[32rpx] text-primary" v-else-if="item.status == 2"></text>
                                <text class="nc-iconfont nc-icon-tubiaoV6- text-[32rpx] text-primary" v-else-if="item.status == 1"></text>
                                <text class="text-[#333] font-500 text-[28rpx] leading-[40rpx] ml-[10rpx]">{{ item.status_name }}</text>
                            </view>
                            <view class="text-[#333] text-[28rpx] leading-[40rpx] truncate">{{ item.site_name }}</view>
                        </view>
                        <view class="flex items-center mt-[16rpx]">
                            <view class="flex-1">
                                <view class="text-[22rpx] text-[#999] leading-[30rpx]">{{ t('shopInfo') }}</view>
                                <view class="mt-[6rpx] text-[#333] text-[24rpx] leading-[34rpx]">{{ item.user_name }} {{ item.user_mobile }}</view>
                            </view>
                            <view class="flex-1">
                                <view class="text-[22rpx] text-[#999] leading-[30rpx]">{{ t('applyTime') }}</view>
                                <view class="mt-[6rpx] text-[#333] text-[24rpx] leading-[34rpx]">{{ item.create_time }}</view>
                            </view>
                        </view>
                        <view class="flex items-center mt-[20rpx]">
                            <view class="flex-1">
                                <view class="text-[22rpx] text-[#999] leading-[30rpx]">{{ t('categoryName') }}</view>
                                <view class="mt-[6rpx] text-[#333] text-[24rpx] leading-[34rpx]">{{ item.category_name }}</view>
                            </view>
                            <view class="flex-1" v-if="item.child_category_name">
                                <view class="text-[22rpx] text-[#999] leading-[30rpx]">{{ t('childCategoryName') }}</view>
                                <view class="mt-[6rpx] text-[#333] text-[24rpx] leading-[34rpx]">{{ item.child_category_name }}</view>
                            </view>
                        </view>
                        <view class="flex items-center mt-[20rpx]" v-if="item.account">
                            <view class="flex-1">
                                <view class="text-[22rpx] text-[#999] leading-[30rpx]">商户账号</view>
                                <view class="mt-[6rpx] text-[#333] text-[24rpx] leading-[34rpx]">{{ item.account }}</view>
                            </view>
                        </view>
                        <view class="mt-[20rpx]">
                            <view class="text-[22rpx] text-[#999] leading-[30rpx]">{{ t('businessLicense') }}</view>
                            <view class="flex items-center mt-[10rpx]">
                                <block v-for="(subItem,subIndex) in item.business_license.split(',')">
                                    <view class="w-[90rpx] h-[90rpx] mr-[20rpx]" @click.stop="imgListPreview(item.business_license,subIndex)">
                                        <up-image class="rounded-[10rpx] overflow-hidden " width="90rpx" height="90rpx" :src="img(subItem ? subItem : '')" model="aspectFill" >
                                            <template #error>
                                                <u-icon name="photo" color="#999" size="30"></u-icon>
                                            </template>
                                        </up-image>
                                    </view>
                                </block>
                            </view>
                        </view>
                        <view class="pt-[20rpx] mt-[20rpx]  flex items-center border-0 border-t-[1rpx] border-solid border-[#F6F6F6]" v-if="item.refuse_reason">
                            <view class="text-[24rpx] text-[#999] leading-[30rpx]">{{ t('refuseReason') }}</view>
                            <view class="mt-[6rpx] text-primary text-[24rpx] leading-[34rpx]">{{ item.refuse_reason }}</view>
                        </view>
                    </view>
				</template>
			</view>
			<mescroll-empty v-if="!list.length && loading" :option="{tip : '暂无入驻记录'}"></mescroll-empty>
		</mescroll-body>
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { t } from '@/locale'
import { img, redirect } from '@/utils/common'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue';
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue';
import useMescroll from '@/components/mescroll/hooks/useMescroll.js';
import { getShopApplyList, getShopApplyStatus} from '@/addon/mall/api/merchant'
import { onLoad,onUnload, onPageScroll, onReachBottom } from '@dcloudio/uni-app';
const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);
import useSystemStore from '@/stores/system'

// 审核
const  systemStore = useSystemStore()

const statusLoading = ref<boolean>(false);
const recordState = ref('')
const recordStateList = ref([]);

const list = ref<Array<Object>>([]);
const loading = ref<boolean>(false);

onLoad((option) => {
	getRecordStatusFn();
});
const getRecordListFn = (mescroll) => {
	loading.value = false;
	let data: object = {
		page: mescroll.num,
		limit: mescroll.size,
		status: recordState.value
	};

	getShopApplyList(data).then((res) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (mescroll.num == 1) {
			list.value = []; //如果是第一页需手动制空列表
		}
		list.value = list.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}
const getRecordStatusFn = () => {
	statusLoading.value = false;
	recordStateList.value = [];
	let obj = { label: '全部', value: '' };
	recordStateList.value.push(obj);

	getShopApplyStatus().then((res) => {
		recordStateList.value = recordStateList.value.concat(res.data);
		statusLoading.value = true;
	}).catch(() => {
		statusLoading.value = true;
	})
}

const recordStateFn = (status:any) =>{
    recordState.value = status;
	list.value = [];
	getMescroll().resetUpScroll();
}

const toLink = (data:any) =>{
    if(data.status == -1){
        redirect({ url: '/addon/mall/pages/member/merchant_settled', param: { apply_id : data.apply_id } })
    } else{
        redirect({ url: '/addon/mall/pages/member/merchant_detail', param: { apply_id : data.apply_id } })
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

<style scoped>
.record-list .mescroll-body {
	padding-bottom: constant(safe-area-inset-bottom) !important;
	padding-bottom: env(safe-area-inset-bottom) !important;
}
</style>