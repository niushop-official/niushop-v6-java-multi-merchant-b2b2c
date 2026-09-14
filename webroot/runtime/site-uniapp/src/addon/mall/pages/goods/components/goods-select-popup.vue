<template>
    <view @touchmove.prevent.stop>
        <u-popup :show="showDialog" @close="showDialog=false" mode="bottom" zIndex="99">
            <view @touchmove.prevent.stop class="popup-common">
               <view class="title">关联推荐</view>
               <scroll-view scroll-y="true" class="h-[700rpx] px-[30rpx] mb-[20rpx] box-border" @scrolltolower="handleLoadMore">
                    <view class="mb-[20rpx]" v-for="(item, index) in goodsTable.data" :key="index">
                        <view class="flex items-center mb-[30rpx]"  @click="handleCheckedChange(item)">
                            <text  class="iconfont text-color text-[34rpx] mr-[32rpx] w-[34rpx] h-[34rpx] rounded-[17rpx] overflow-hidden flex-shrink-0" :class="{ 'iconxuanze1 text-primary': item.checked,'bg-[#F5F5F5]':!item.checked}"></text>
                            <view class="flex flex-1">
                                <up-image class="rounded-[10rpx] overflow-hidden" width="80rpx" height="80rpx" :src="img(item.goods_image)" mode="aspectFill">
                                    <template #error>
                                        <image class="w-[80rpx] h-[80rpx] rounded-[10rpx] overflow-hidden" :src="img('static/resource/images/diy/shop_default.jpg')" mode="aspectFill"></image>
                                    </template>
                                </up-image>
                                <view class="flex-1 ml-[20rpx] flex flex-col justify-between">
                                    <text class="text-[26rpx]">{{ item.goods_name  }}</text>
                                    <view>
                                        <text class="text-[24rpx] text-[#999] mr-[20rpx]">价格：￥{{ item.price }}</text>
                                        <text class="text-[24rpx] text-[#999]">库存：{{ item.stock }}</text>
                                    </view>
                                </view>
                            </view>
                        </view>
                    </view>
               </scroll-view>
               <view class="btn-wrap flex justify-between items-center">
                    <view class="mr-[20rpx] flex items-center">已选{{selectGoodsNum }}个商品</view>
                    <button class="btn border-[0] rounded-[100rpx] primary-btn-bg mr-0  !w-[300rpx] !h-[60rpx] flex-center" shape="circle" hover-class="none" @click="save">确定</button>
                </view>
            </view>
        </u-popup>
    </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue';
import {  deepClone, img} from '@/utils/common'
import { getGoodsSelectPageList } from '@/addon/mall/api/goods'

const prop = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    max: {
        type: Number,
        default: 0
    },
    min: {
        type: Number,
        default: 0
    },
    exclude_goods_id: {
        type: String,
        default: ''
    }
})
const emit = defineEmits(['update:modelValue'])

const goodsIds: any = computed({
    get () {
        return prop.modelValue
    },
    set (value) {
        emit('update:modelValue', value)
    }
})
const selectGoods = ref([]) // 已选商品列表

// 已选商品数量
const selectGoodsNum: any = computed(() => {
    return Object.keys(selectGoods.value).length
})
const showDialog = ref(false);
const goodsTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    last_page: 1,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        select_type: 'all',
        goods_ids: '',
        verify_goods_ids: '',
        goods_type: '',
        exclude_goods_id: prop.exclude_goods_id,
    }
})

/**
 * 获取商品列表
 */
const loadGoodsList = (page: number = 1) => {
    goodsTable.loading = true;
    goodsTable.page = page
    if(goodsTable.page > goodsTable.last_page) return false
    getGoodsSelectPageList({
        page: goodsTable.page,
        limit: goodsTable.limit,
        ...goodsTable.searchParam
    }).then((res: any) => {
        if (Number(page) === 1) {
            goodsTable.data = []; //如果是第一页需手动制空列表
		}
        if(res.data.last_page){
            goodsTable.last_page = res.data.last_page
        }else{
            goodsTable.last_page = 1
        }
        goodsTable.loading = false;
        goodsTable.data = goodsTable.data.concat(res.data.data); 
        goodsTable.total = res.data.total
        goodsTable.data.forEach((item: any) => {
            if(goodsIds.value.includes(item.goods_id)) {
                item.checked = true
            }
        })
        selectGoods.value = goodsTable.data.filter((item: any) => item.checked)
    }).catch(() => {
        
    })
}

// 分页请求
const handleLoadMore = () => {
    goodsTable.page++
    loadGoodsList(goodsTable.page)
}

// 单选
const  handleCheckedChange = (data: any) => {
    data.checked = !data.checked
    selectGoods.value = goodsTable.data.filter((item: any) => item.checked)
}

const save = () => {
    if (prop.min && selectGoodsNum.value < prop.min) {
        uni.showToast({
            title: `请至少选择${prop.min}个商品`,
            icon: 'none'
        })
        return;
    }
    if (prop.max && prop.max > 0 && selectGoodsNum.value && selectGoodsNum.value > prop.max) {
        uni.showToast({
            title: `最多只能选择${prop.max}个商品`,
            icon: 'none'
        })
        return;
    }
    let ids = selectGoods.value.map((item: any) => item.goods_id)
    goodsIds.value.splice(0, goodsIds.value.length, ...ids)
    showDialog.value = false
}

const open = () => {
    loadGoodsList()
    showDialog.value = true
}

defineExpose({
    showDialog,
    open
})
</script>

<style scoped>

</style>