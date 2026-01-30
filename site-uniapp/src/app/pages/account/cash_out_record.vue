<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()">
        <view class="fixed left-0 right-0 top-0 z-10 bg-[#fff] py-[14rpx] px-[20rpx] flex items-center">
            <view class="search-input flex-1">
                <input class="input" maxlength="50" type="text" v-model="keyword"  placeholder="请输入提现单号" placeholderClass="text-[var(--text-color-light9)] text-[24rpx]" confirm-type="search" @confirm="searchTypeFn">
                <text v-if="keyword" class="nc-iconfont nc-icon-cuohaoV6xx1 clear mr-[10rpx]" @click="keyword=''"></text>
                <text @click.stop="searchTypeFn" class="nc-iconfont nc-icon-sousuo-duanV6xx1 btn !mr-0"></text>
            </view>
            <view class="nc-iconfont nc-icon-shaixuanV6xx1 text-[30rpx] ml-[20rpx] px-[10rpx]" @click="showPopup = true"></view>
        </view>
        <u-popup :show="showPopup" mode="top" @close="showPopup = false" zIndex="9" class="cash-out-record">
            <view class="popup-common relative pt-[30rpx]">
                <scroll-view :scroll-y="true" class="h-[40vh]">
                    <view class="px-[24rpx]">
                        <view class="flex items-baseline justify-between" @click="handleSelect">
                            <text class="text-[28rpx]">申请日期</text>
                            <text class="flex-1 text-[26rpx] text-right mr-[10rpx]" v-if="create_time.length">{{ create_time[0] }}～{{  create_time[1] }}</text>
                            <text class="nc-iconfont nc-icon-youV6xx text-[26rpx]"></text>
                        </view>
                        <view class="h-[1rpx] bg-[#eee] my-[30rpx]"></view>
                        <view class="text-[28rpx] mb-[30rpx]">提现状态</view>
                        <view class="flex flex-wrap">
                            <text @click="status = ''" :class="{ 'label-select': status == ''}" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent min-w-[120rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[8rpx] text-[22rpx]">全部</text>
                            <text @click="handleStatus(key)" v-for="(item, key) in cashOutStatusList" :key="key" :class="{ 'label-select': status == key}" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent min-w-[120rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[8rpx] text-[22rpx]">{{ item }}</text>
                        </view>
                        <view class="h-[1rpx] bg-[#eee] my-[30rpx]"></view>
                        <view class="text-[28rpx] mb-[30rpx]">提现方式</view>
                        <view class="flex flex-wrap">
                            <text @click="transfer_type = ''" :class="{ 'label-select': transfer_type == ''}" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent min-w-[120rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[8rpx] text-[22rpx]">全部</text>
                            <text @click="handleTransferType(key)" v-for="(item, key) in transfertype" :key="key" :class="{ 'label-select': transfer_type == key}" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent min-w-[120rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[8rpx] text-[22rpx]">{{ item.name }}</text>
                        </view>
                    </view>
                </scroll-view>
                <view class="flex items-center justify-between px-[30rpx] py-[30rpx]">
                    <text class="flex-1 h-[68rpx] rounded-[16rpx] text-center leading-[68rpx] text-[26rpx] mr-[30rpx] text-primary border-primary border-solid border-[2rpx]" @click="reset">重置</text>
                    <text class="flex-1 h-[68rpx] bg-primary rounded-[16rpx] text-center leading-[68rpx] text-[26rpx] text-[#fff]" @click="confirm">确认</text>
                </view>
            </view>
        </u-popup>
        <mescroll-body ref="mescrollRef" @init="mescrollInit" top="88rpx" @down="downCallback" @up="getCashOutListFn">
            <view class="sidebar-margin pt-[var(--top-m)]" v-if="cashOutList.length">
                <view v-for="(item,index) in cashOutList" :key="item.id" class="mb-[var(--top-m)] card-template" @click="toDetailFn(item)">
                    <view class="flex items-center justify-between mb-[20rpx]">
                        <text class="text-[28rpx] font-500 text-[#333]">{{ item.transfer_type_name  }}</text>
                        <text class="text-[34rpx] text-[var(--price-text-color)]" >{{ item.money }}</text>
                    </view>
                    <view class="flex items-center justify-between">
                        <text class="text-[24rpx] text-[var(--text-color-light6)]">{{ item.create_time }}</text>
                        <text class="text-[24rpx] text-[#999] ">{{ item.status_name }}</text>
                    </view>
                </view>
            </view>
            <mescroll-empty v-if="!cashOutList.length && loading"></mescroll-empty>
        </mescroll-body>
        <!-- 时间选择 -->
		<select-date ref="selectDateRef" @confirm="confirmFn" />
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { redirect } from '@/utils/common'
import { getCashOutList, getShopCashOutStatus, getTransfertype } from '@/app/api/account'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue'
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue'
import useMescroll from '@/components/mescroll/hooks/useMescroll.js'
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app'

const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);

const cashOutList = ref<any>([])
const cashOutStatusList = ref([]) // 提现状态列表
const transfertype = ref<any>([]) // 获取转账方式
const keyword = ref('')
const status = ref('')
const transfer_type = ref('')
const create_time = ref([])
const loading = ref(false)
const showPopup = ref(false)

onLoad((option: any) => {
	status.value = option.status || ''
})

const checkStatusList = () => {
    getShopCashOutStatus().then((res : any) => {
        cashOutStatusList.value = res.data
    })
}
checkStatusList()
const getTransfertypeFn = async () => {
    getTransfertype().then((res : any) => {
        transfertype.value = res.data
    })
}
getTransfertypeFn()
const getCashOutListFn = (mescroll : any) => {
	loading.value = false;
	let data : object = {
		page: mescroll.num,
		limit: mescroll.size,
        cash_out_no: keyword.value,
        status: status.value,
        transfer_type: transfer_type.value,
        create_time: create_time.value
	};

	getCashOutList(data).then((res : any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (mescroll.num == 1) {
			cashOutList.value = []; //如果是第一页需手动制空列表
		}
		cashOutList.value = cashOutList.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}
const handleStatus = (key : string) => {
    status.value = key
}
const handleTransferType = (key : string) => {
    transfer_type.value = key
}
const searchTypeFn = () => {
	getMescroll().resetUpScroll();
}

//日期筛选
const selectDateRef = ref()
const handleSelect = () =>{
	selectDateRef.value.show = true
}
// 确定时间筛选
const confirmFn = (data: any) =>{
	create_time.value = data;
}

const confirm = () => {
    showPopup.value = false
    getMescroll().resetUpScroll()
}
const reset = () => {
    keyword.value = ''
    status.value = ''
    transfer_type.value = ''
    create_time.value = []
    showPopup.value = false
    getMescroll().resetUpScroll()
}

const toDetailFn = (item : any) => {
	redirect({ url: '/app/pages/account/cash_out_detail', param: { id: item.id } });
}

</script>

<style lang="scss" scoped>
.label-select {
	color: var(--primary-color);
	border-color: var(--primary-color);
	background-color: var(--primary-color-light);
}
:deep(.cash-out-record.u-popup .u-transition) {
	top: 88rpx !important;
}
</style>