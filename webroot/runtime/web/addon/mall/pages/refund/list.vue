<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="h-full"  v-loading="refundTableData.loading">
            <div>
                <div v-if="refundTableData.data.length && !refundTableData.loading">
                    <div>
                        <div class="rounded-[12px] overflow-hidden mb-[20px] border-[1px] border-solid border-[#eee]" v-for="(item,index) in refundTableData.data" :key="index">
                            <div class="h-[54px] leading-[52px] text-[14px] px-[20px] bg-[#fafafa] flex items-center justify-between">
                                <div class="flex items-center">
                                    <div class="bg-primary text-[#fff] mr-[20px] rounded-[2px] text-[12px] leading-[1] py-[2px] px-[4px]">{{ item.status_name }}</div>
                                    <div class="text-[#999] mr-[50px] oppoSans-R">
                                        <span>{{ t('refundNo') }}：</span>
                                        <span class="text-[#333] font-400 oppoSans-R">{{item.order_refund_no}}</span>
                                    </div>
                                    <div class="cursor-pointer truncate  text-[#333] hover:text-primary" @click="toShop(item.site)" v-if="item.site">
                                        <span class="iconfont icon-Vector-25 !text-[14px] mr-[5px]"></span>
                                        <span>{{ item.site.site_name }}</span>
                                    </div>
                                </div>
                                <div class="text-[#333]">
                                    <span class="text-[12px] oppoSans-M">退款金额：</span>
                                    <span class="text-[12px] oppoSans-M">￥</span>
                                    <span class="text-[20px] font-600 price-font">{{item.apply_money}}</span>
                                </div>
                            </div>
                            <div class="flex justify-between items-center pl-[20px] ">
                                <div class="w-[786px] my-[20px] border-r-[1px] border-dashed border-[rgb(238, 238, 238)]" >
                                    <div class="flex items-center mb-[20px]" v-for="(subItem,subIndex) in [item.order_goods]" :key="subIndex">
                                        <div class="flex items-center">
                                            <el-image class="rounded-[var(--rounded-mid)]" style="width: 100px; height: 100px" v-if="subItem.goods_image" :src="img(subItem.goods_image)" fit="cover">
                                                <template #error>
                                                    <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                                </template>
                                            </el-image>
                                            <div class="ml-[20px] flex flex-col justify-center flex-1">
                                                <div class="text-[14px] text-[#333] truncate w-[635px] mb-[14px] font-500 oppoSans-M">{{subItem.goods_name}}</div>
                                                <div class="text-[14px] text-[#333] truncate w-[635px] mb-[15px] font-400 oppoSans-R" v-if="subItem.sku_name"> 规格：{{subItem.sku_name}}</div>
                                                <div class="flex items-center">
                                                    <div class="text-[14px] text-[#333] oppoSans-R mr-[20px]">申请数量：{{ subItem.num }}</div>
                                                    <div class="text-[14px] text-primary oppoSans-R mr-[20px]">{{item.refund_type_name}}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="w-[180px] pt-[20px] pb-[6px]">
                                    <div class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mx-auto mb-[14px]" @click="detailEvent(item)">{{ t('info')}}</div>
                                    <div v-if="['6','7','8','-1'].indexOf(item.status) == -1" class="w-[86px] h-[30px] leading-[28px] text-[12px] text-primary rounded-[15px] text-center border-[1px] border-solid border-primary cursor-pointer mb-[14px] mx-auto"  @click="refundConfirm(item)">{{ t('refundApply')}}</div>
                                    <div v-if="['3'].indexOf(item.status) != -1" class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mb-[14px] mx-auto"  @click="editRefund(item)">{{ t('edit')}}</div>
                                    <div v-if="['2'].indexOf(item.status) != -1"  class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mb-[14px] mx-auto" @click="trackingFn(item,false)">{{ t('writeLogistic') }}</div>
                                    <div v-if="['5'].indexOf(item.status) != -1"   class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mb-[14px] mx-auto" @click="trackingFn(item,true)">{{ t('editLogistic') }}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="refundTableData.page" v-model:page-size="refundTableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="refundTableData.total"
                            @size-change="loadRefundList()" @current-change="loadRefundList" />
                    </div>
                </div>
                <div v-if="!refundTableData.data.length && !refundTableData.loading">
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
        <logistics-tracking ref="trackingRef" @complete="loadRefundList"></logistics-tracking>
    </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch ,reactive} from 'vue'
import { getRefundList,closeRefund } from '@/addon/mall/api/refund'
import { useRouter, useRoute } from 'vue-router'
import logisticsTracking from '@/addon/mall/pages/refund/components/logistics-tracking.vue'

definePageMeta({
    layout: "manage",
    middleware: "auth"
})
const route = useRoute()
const router = useRouter()
// 获取会员列表
const refundTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})
const loadRefundList = (page: number = 1) => {
    refundTableData.loading = true
    refundTableData.page = page

    getRefundList({
        page: refundTableData.page,
        limit: refundTableData.limit
    }).then(res => {
        refundTableData.loading = false
        refundTableData.data = res.data.data
        refundTableData.total = res.data.total
    }).catch(() => {
        refundTableData.loading = false
    })
}
loadRefundList()

// 订单详情
const detailEvent = (data: any) => {
    router.push('/refund/detail?order_refund_no=' + data.order_refund_no)
}
// 申请退款
const refundConfirm = (data:any)=>{
    ElMessageBox.confirm('撤销之后本次申请将会关闭,如后续仍有问题可再次发起申请。', '提示',
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        closeRefund(data.order_refund_no).then((res) => {
			loadRefundList()
		}).catch(() => {
			
		})
    })
}
// 编辑退款
const editRefund = (data:any)=>{
    router.push({path:'/refund/apply',query:{order_refund_no:data.order_refund_no}})
}
// 物流弹框
const trackingRef = ref(null)
const trackingFn = (data:any,status:any) =>{
    trackingRef.value.setFormData(data.order_refund_no,status)
    trackingRef.value.showDialog = true
}

// 去店铺首页
const toShop = (data:any) =>{
    router.push(`/shop/index?site_id=${data.site_id}`)
}
</script>

<style lang="scss" scoped>
.box-card {
    border: none !important;
}

.text-color {
    color: var(--jjext-color-brand);
}
.table-top :deep(.el-table__body-wrapper) {
	display: none;
}

:deep(.el-table) {
	--el-table-row-hover-bg-color: var(--el-transfer-border-color);
}
</style>