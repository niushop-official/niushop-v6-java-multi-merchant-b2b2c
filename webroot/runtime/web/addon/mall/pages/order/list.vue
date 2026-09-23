<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="h-full" v-loading="tableData.loading">
            <div>
                <div class="flex items-center justify-between mb-[23px] leading-[20px]">
                    <div class="flex flex-wrap">
                        <div class="text-[16px] mr-[50px] text-[#666] cursor-pointer" :class="{'!text-primary font-500': tableData.params.status == item.status}" v-for="(item,index) in tableData.orderStatusOption" :key="index" @click="handleClick(item.status)">{{item.name}}</div>
                    </div>
                </div>
                <div v-if="tableData.data.length && !tableData.loading">
                    <div>
                        <div class="rounded-[12px] overflow-hidden mb-[20px] border-[1px] border-solid border-[#eee]" v-for="(item,index) in tableData.data" :key="index">
                            <div class="h-[54px]  text-[14px] px-[20px] bg-[#fafafa] flex items-center justify-between">
                                <div class="flex items-center">
                                    <div class="text-[14px] text-primary mr-[10px]" v-if="item.order_type_name">【{{ item.order_type_name }}】</div>
                                    <div class="bg-[#bbb] text-[#fff] mr-[20px] rounded-[2px] text-[12px] leading-[1] py-[2px] px-[4px]" :class="{'!bg-primary': item.status == 1 || item.status == 2}">{{ item.status_name.name }}</div>
                                    <div class="text-[#999] mr-[20px] oppoSans-R">{{item.create_time}}</div>
                                    <div class="text-[#999] mr-[50px] oppoSans-R">
                                        <span>订单号：</span>
                                        <span class="text-[#333] font-400 oppoSans-R">{{item.order_no}}</span>
                                    </div>
                                    <div class="cursor-pointer truncate flex items-center text-[#333] hover:text-primary" @click="toShop(item.site)" v-if="item.site">
                                        <span class="iconfont icon-Vector-25 !text-[14px] mr-[5px]"></span>
                                        <span>{{ item.site.site_name }}</span>
                                    </div>
                                </div>
                                <div class="text-[#333]">
                                    <span class="text-[12px] oppoSans-M">￥</span>
                                    <span class="text-[20px] font-600 price-font">{{item.order_money}}</span>
                                </div>
                            </div>
                            <div class="flex justify-between items-center pl-[20px] ">
                                <div class="w-[760px] my-[20px] border-r-[1px] border-dashed border-[rgb(238, 238, 238)]" >
                                    <div class="flex items-center" :class="{'mb-[20px]': subIndex }" v-for="(subItem,subIndex) in item.order_goods" :key="subIndex">
                                        <div class="flex items-center cursor-pointer" @click="toLink(item)">
                                            <el-image class="rounded-[var(--rounded-mid)]" style="width: 100px; height: 100px" v-if="subItem.goods_image" :src="img(subItem.goods_image)" fit="cover">
                                                <template #error>
                                                    <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                                </template>
                                            </el-image>
                                            <div class="ml-[20px] flex flex-col justify-between flex-1 h-[100px]">
                                                <div class="text-[14px] text-[#333] truncate w-[635px] mb-[14px] font-500 oppoSans-M leading-[25px] flex-1">{{subItem.goods_name}}</div>
                                                <div class="text-[14px] text-[#333] truncate w-[635px] mb-[15px] font-400 oppoSans-R" v-if="subItem.sku_name">规格：{{subItem.sku_name}}</div>
                                                <div class="text-[14px] text-[#333] oppoSans-R mr-[20px]">￥{{ subItem.price }}×{{ subItem.num }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="w-[180px] pt-[20px] pb-[6px]">
                                    <div class="w-[86px] h-[30px] leading-[28px] text-[12px]  text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mx-auto mb-[14px] " @click="toLink(item)">{{t('detail')}}</div>
                                    <div v-if="(item.status == 1) || (item.status == 3) || (item.status == 5 && evaluateConfig.is_evaluate == 1)">
                                        <div v-if="item.status == 1" class="w-[86px] h-[30px] leading-[28px] text-[12px]  text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mb-[14px] mx-auto" @click="orderBtnFn(item, 'close')">{{t('orderClose')}}</div>
                                        <div  v-if="item.status == 1" class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[var(--el-color-primary)] rounded-[15px] text-center border-[1px] border-solid border-[var(--el-color-primary)] cursor-pointer mb-[14px] mx-auto" @click="orderBtnFn(item, 'pay')">{{t('topay')}}</div>
                                        <div v-if="item.status == 3" class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mb-[14px] mx-auto" @click="orderBtnFn(item, 'finish')">{{t('orderFinish')}}</div>
                                        <div  v-if="item.status == 5 && evaluateConfig.is_evaluate == 1 && !item.is_evaluate" class="w-[86px] h-[30px] leading-[28px] text-[12px] text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mx-auto" @click="orderBtnFn(item, 'evaluate')">{{t('evaluate')}}</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
                            @size-change="getOrderListFn()" @current-change="getOrderListFn" />
                    </div>
                </div>
                <div v-if="!tableData.data.length && !tableData.loading">
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
        <add-evaluate   ref="addEvaluateRef"  @complete="getOrderListFn" ></add-evaluate>
        <group-pay ref="groupPayRef"></group-pay>
        <group-close ref="groupCloseRef" @confirm="closeGroupOrder"></group-close>
    </div>
</template>

<script lang="ts" setup>
import { computed, ref, watch ,reactive} from 'vue'
import { getMallOrder, getMallOrderStatus, orderClose, orderFinish, getMallOrderPay, getMallOrderClose} from '@/addon/mall/api/order'
import { getEvaluateConfig } from '@/addon/mall/api/goods'
import { useRouter, useRoute } from 'vue-router'
import { Picture as IconPicture } from '@element-plus/icons-vue'
import AddEvaluate from '@/addon/mall/pages/order/components/add-evaluate.vue'
import GroupPay from '@/addon/mall/pages/order/components/group-pay.vue'
import GroupClose from '@/addon/mall/pages/order/components/group-close.vue'
definePageMeta({
    layout: "manage",
    middleware: "auth"
});

const route = useRoute()
const router = useRouter()
const evaluateConfig = ref('')
// 获取订单列表
const tableData = reactive<any>({
    total: 0,
    page: 1,
    limit: 10,
    loading: true,
    data: [],
    orderStatusOption: [],
    params: {
        order_no: '',
        status: '',
        create_time: '',
    },
})

onMounted(() => {
    tableData.params.status = route.query.status || '';
    getOrderListFn()
})

//获取订单状态
const  getMallOrderStatusFn = () => {
    let obj = { name: '全部', status: '' }
    tableData.orderStatusOption.push(obj)
    getMallOrderStatus().then((res: any) => {
        Object.values(res.data).forEach((item, index) => {
			tableData.orderStatusOption.push(item);
		})
    })
}
getMallOrderStatusFn()
// 获取评论设置
const evaluateEvent = () => {
	getEvaluateConfig().then((res) => {
		evaluateConfig.value = res.data
	})
}
evaluateEvent()

const getOrderListFn = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getMallOrder({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.params
    }).then((res: any) => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
    }).catch(() => {
        tableData.loading = false
    })
}

// 切换订单状态
const handleClick = (event: string) =>{
    tableData.params.status = event
    getOrderListFn()
}



const orderBtnFn = (data: any, type = '') => {
	if (type == 'pay'){
		payFn(data)
	}else if (type == 'close') {
		close(data);
	} else if (type == 'finish') {
		finish(data);
	} else if (type == 'evaluate') {
		evaluateFn(data)
	}
}
//取消订单
const groupCloseRef = ref(null)
const close = (item:any) => {
    ElMessageBox.confirm('您确定要关闭该订单吗？', '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            confirmButtonClass:'!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]',
            cancelButtonClass:'!border-[#dcdfe6]',
            type: 'warning'
        }
    ).then(() => {
        orderClose(item.order_id).then((res: any) => {
            if(Object.keys(res.data).length && !res.data.is_allow_close){
                if(res.data.order_list.length){
                    groupCloseRef.value.open(res.data)
                }else{
                    getMallOrderClose(res.data.group_id).then(res =>{
						getOrderListFn()
					}).catch(() =>{
					})
                }
			}else{
				getOrderListFn()		
			}
        }).catch(() => {
        })
    })
}
const closeGroupOrder = ()=>{
    getOrderListFn()
}

//订单完成
const finish = (item: any) => {
	ElMessageBox.confirm('您确定物品已收到吗', '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            confirmButtonClass:'!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]',
            cancelButtonClass:'!border-[#dcdfe6]',
            type: 'warning'
        }
    ).then(() => {
        orderFinish(item.order_id).then((res: any) => {
            getOrderListFn()
        }).catch(() => {
        })
    })
}
// 支付
let groupPayRef = ref(null)
const payFn = (item:any) => {
	getMallOrderPay(item.order_id).then(res =>{
		if(res.data.is_allow_pay){
            router.push({ path: '/pay/pay', query: {trade_type: 'mall',trade_id: res.data.group_id } })
		}else{
            if(res.data.order_list.length){
			    groupPayRef.value.open(res.data,item)
            }else{
                router.push({ path: '/pay/pay', query: {trade_type: 'mall',trade_id: res.data.group_id } })
            }
		}
	})
}
// 去详情
const toLink = (data:any) =>{
    router.push(`/order/detail?order_id=${data.order_id}`)
}
// 去店铺首页
const toShop = (data:any) =>{
    router.push(`/shop/index?site_id=${data.site_id}`)
}

//添加评论
const addEvaluateRef = ref(null)
const evaluateFn = (item: any) =>{
    addEvaluateRef.value.setFormData(item.order_id)
    addEvaluateRef.value.dialogEvaluateVisible = true
}
</script>

<style lang="scss" scoped>
.box-card {
    border: none !important;
}

.table-top :deep(.el-table__body-wrapper) {
    display: none;
}

:deep(.el-table) {
    --el-table-row-hover-bg-color: var(--el-transfer-border-color);
}

</style>