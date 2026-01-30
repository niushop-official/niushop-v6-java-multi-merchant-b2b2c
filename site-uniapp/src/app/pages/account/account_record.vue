<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()">
        <view class="fixed left-0 right-0 top-0 z-10 bg-[#fff] py-[14rpx] px-[20rpx] flex items-center">
            <view class="search-input flex-1">
                <input class="input" maxlength="50" type="text" v-model="keyword"  placeholder="请输入账单编号" placeholderClass="text-[var(--text-color-light9)] text-[24rpx]" confirm-type="search" @confirm="searchTypeFn">
                <text v-if="keyword" class="nc-iconfont nc-icon-cuohaoV6xx1 clear mr-[10rpx]" @click="keyword=''"></text>
                <text @click.stop="searchTypeFn" class="nc-iconfont nc-icon-sousuo-duanV6xx1 btn !mr-0"></text>
            </view>
            <view class="nc-iconfont nc-icon-shaixuanV6xx1 text-[30rpx] ml-[20rpx] px-[10rpx]" @click="showPopup = true"></view>
        </view>
        
        <u-popup :show="showPopup" mode="top" @close="showPopup = false" zIndex="9" class="account-record">
            <view class="popup-common relative pt-[30rpx]">
                <scroll-view :scroll-y="true" class="h-[30vh]">
                    <view class="px-[24rpx]">
                        <view class="flex items-baseline justify-between" @click="handleSelect">
                            <text class="text-[28rpx]">账单日期</text>
                            <text class="flex-1 text-[26rpx] text-right mr-[10rpx]" v-if="create_time.length">{{ create_time[0] }}～{{  create_time[1] }}</text>
                            <text class="nc-iconfont nc-icon-youV6xx text-[26rpx]"></text>
                        </view>
                        <view class="h-[1rpx] bg-[#eee] my-[30rpx]"></view>
                        <view class="text-[28rpx] mb-[30rpx]">账单类型</view>
                        <view class="flex flex-wrap">
                            <text @click="fromType = ''" :class="{ 'label-select': fromType == ''}" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent min-w-[120rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[8rpx] text-[22rpx]">全部</text>
                            <text @click="changeFromType(key)" v-for="(item, key) in accountType" :key="key" :class="{ 'label-select': fromType == key}" class="truncate text-[#333] px-[10rpx] border-[2rpx] border-solid border-transparent min-w-[120rpx] h-[56rpx] flex items-center justify-center mr-[30rpx] mb-[30rpx] box-border bg-[var(--temp-bg)] rounded-[8rpx] text-[22rpx]">{{ item.name }}</text>
                        </view>
                    </view>
                </scroll-view>
                <view class="flex items-center justify-between px-[30rpx] py-[30rpx]">
                    <text class="flex-1 h-[68rpx] rounded-[16rpx] text-center leading-[68rpx] text-[26rpx] mr-[30rpx] text-primary border-primary border-solid border-[2rpx]" @click="reset">重置</text>
                    <text class="flex-1 h-[68rpx] bg-primary rounded-[16rpx] text-center leading-[68rpx] text-[26rpx] text-[#fff]" @click="confirm">确认</text>
                </view>
            </view>
        </u-popup>
        <mescroll-body ref="mescrollRef" @init="mescrollInit" top="88rpx" @down="downCallback" @up="getAccountListFn">
            <view class="sidebar-margin pt-[var(--top-m)]" v-if="accountList.length">
                <view v-for="(item,index) in accountList" :key="index" class="mb-[var(--top-m)] card-template flex items-center justify-between" @click="toLink(item)">
                    <view class="flex-1">
                        <view class="text-[28rpx] font-500 text-[#333] leading-[40rpx] mb-[20rpx]">{{ item.from_type_name }}</view>
                        <view class="text-[24rpx] text-[var(--text-color-light6)]">{{ item.create_time }}</view>
                    </view>
                    <view class="text-[34rpx] font-500" :class="{'text-[var(--price-text-color)]': Number(item.money) > 0, 'text-[#03B521]': Number(item.money) < 0 }">{{ item.money }}</view>
                </view>
            </view>
            <mescroll-empty v-if="!accountList.length && loading"></mescroll-empty>
        </mescroll-body>
        <!-- 时间选择 -->
		<select-date ref="selectDateRef" @confirm="confirmFn" />
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { redirect } from '@/utils/common'
import { getShopAccountTypeList, getShopAccountLog } from '@/app/api/account'
import MescrollBody from '@/components/mescroll/mescroll-body/mescroll-body.vue'
import MescrollEmpty from '@/components/mescroll/mescroll-empty/mescroll-empty.vue'
import useMescroll from '@/components/mescroll/hooks/useMescroll.js'
import { onLoad, onPageScroll, onReachBottom } from '@dcloudio/uni-app'


const { mescrollInit, downCallback, getMescroll } = useMescroll(onPageScroll, onReachBottom);


const loading = ref(false)
const accountList = ref<any>([])
const accountType = ref([])
const keyword = ref('')
const fromType = ref('')
const create_time = ref([])
const showPopup = ref(false)
const getAccountListFn = (mescroll : any) => {
	loading.value = false;
	let data : object = {
		page: mescroll.num,
		limit: mescroll.size,
		related_id: keyword.value,
        from_type: fromType.value,
        create_time: create_time.value
	};

	getShopAccountLog(data).then((res : any) => {
		let newArr = (res.data.data as Array<Object>);
		//设置列表数据
		if (mescroll.num == 1) {
			accountList.value = []; //如果是第一页需手动制空列表
		}
		accountList.value = accountList.value.concat(newArr);
		mescroll.endSuccess(newArr.length);
		loading.value = true;
	}).catch(() => {
		loading.value = true;
		mescroll.endErr(); // 请求失败, 结束加载
	})
}

// 获取账单类型
const getShopAccountTypeListFn = () => {
    getShopAccountTypeList().then((res: any) => {
        accountType.value = res.data
    })
}
getShopAccountTypeListFn()

const changeFromType = (type: string) => {
    fromType.value = type
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

const  reset = () => {
    keyword.value = '' 
    fromType.value = ''
    create_time.value = []
    showPopup.value = false
    getMescroll().resetUpScroll()
}

const toLink = (data: any) => {
    if(data.from_type == 'cash_out'){
        redirect({ url: '/app/pages/account/account_detail', param: { related_id: data.related_id } })
    } else if (data.from_type == 'site_separate' || data.from_type == 'order') {
        redirect({ url: '/addon/mall/pages/order/detail', param: { order_id: data.related_id } })
    }
    
}
</script>

<style lang="scss" scoped>
.label-select {
	color: var(--primary-color);
	border-color: var(--primary-color);
	background-color: var(--primary-color-light);
}
:deep(.account-record.u-popup .u-transition) {
	top: 88rpx !important;
}
</style>