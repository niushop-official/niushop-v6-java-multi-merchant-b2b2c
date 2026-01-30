<template>
	<div class="box-border pb-[71px] px-[30px] bg-[#fff]">
		<div class="pt-[20px] pb-[18px] flex items-center">
			<span class="iconfont icon31shijian !text-[18px] text-[#9195A3]"></span>
			<span class="text-[14px] text-[#9195A3] leading-[30px] ml-[6px]">{{ time }}</span>
		</div>
		<div class="flex" v-loading="loading">
			<div class="box-border flex-1">
				<div class="flex items-center justify-between">
					<div class="order-num w-[352px] h-[190px] box-border pt-[22px] pr-[20px] pb-[17px] pl-[30px] cursor-pointer" @click="toLink('/mall/order/index')">
						<div class="flex justify-between items-center mb-[15px]">
							<span class="text-[18px] text-[#4D5C6E] font-500">今日订单数</span>
							<el-tooltip effect="light" :content="'订单数:' + statInfo.order_stat.yesterday_order_count"
								placement="bottom">
								<div class="px-[5px] py-[3px] leading-[10px] text-[var(--el-color-primary)] border-solid border-[var(--el-color-primary)] border-[1px]  cursor-pointer text-[12px]">昨日</div>
							</el-tooltip>
						</div>
						<div class="flex items-center leading-[18px] mb-[20px]">
							<span class="text-[12px] text-[#666] font-400">昨日订单数</span>
							<span class="ml-[6px] text-[18px] font-500">{{ statInfo.order_stat.yesterday_order_count }}</span>
							<span class="text-[12px] text-[#666] font-400 ml-[9px]">较昨日</span>
							<span class="ml-[4px] text-[14px] text-[#FA5050]" v-if="calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count) >= 0">{{ Math.abs(calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count)) }}%</span>
							<span class="ml-[4px] text-[14px] text-[#05C08B]" v-if="calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count) < 0">{{ Math.abs(calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count)) }}%</span>
							<span class="iconfont icontransferout text-[#FA5050] !text-[13px]" v-if="calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count) >= 0"></span>
							<span class="iconfont icontransferout-copy text-[#05C08B] !text-[13px]" v-if="calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count) < 0"></span>
						</div>
						<div class="text-[34px] font-bold text-[#111] mb-[16px] leading-[34px]">{{ statInfo.order_stat.today_order_count }}</div>
						<div class="flex items-center justify-between text-[14px] leading-[18px]">
							<span class="text-[#666]">订单总数</span>
							<span class="text-[20px] text-[#333]">{{ statInfo.order_stat.total_order_count }}</span>
						</div>
					</div>
					<div class="sell-num w-[352px] h-[190px] box-border pt-[22px] pr-[20px] pb-[17px] pl-[30px] cursor-pointer" @click="toLink('/mall/order/index')">
						<div class="flex justify-between items-center mb-[15px]">
							<span class="text-[18px] text-[#4D5C6E] font-500">今日销售额</span>
							<el-tooltip effect="light" :content="'销售额:' + statInfo.sale_stat.yesterday_sale_money" placement="bottom">
								<div class="px-[5px] py-[3px] leading-[10px] text-[var(--el-color-primary)] border-solid border-[var(--el-color-primary)] border-[1px]  cursor-pointer text-[12px]">昨日</div>
							</el-tooltip>
						</div>
						<div class="flex items-center leading-[18px] mb-[20px]">
							<span class="text-[12px] text-[#666] font-400">昨日销售额(元)</span>
							<span class="ml-[6px] text-[18px] font-500">{{ statInfo.sale_stat.yesterday_sale_money }}</span>
							<span class="text-[12px] text-[#666] font-400 ml-[9px]">较昨日</span>
							<span class="ml-[4px] text-[14px] text-[#FA5050]" v-if="calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money) >= 0">{{ Math.abs(calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money)) }}%</span>
							<span class="ml-[4px] text-[14px] text-[#05C08B]" v-if="calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money) < 0">{{ Math.abs(calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money)) }}%</span>
							<span class="iconfont icontransferout text-[#FA5050] !text-[13px]" v-if="calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money) >= 0"></span>
							<span class="iconfont icontransferout-copy text-[#05C08B] !text-[13px]" v-if="calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money) < 0"></span>
						</div>
						<div class="text-[34px] font-bold text-[#111] mb-[16px] leading-[34px]">￥{{ statInfo.sale_stat.today_sale_money }}</div>
						<div class="flex items-center justify-between text-[14px] leading-[18px]">
							<span class="text-[#666]">销售总额(元)</span>
							<span class="text-[20px] text-[#333]">{{ statInfo.sale_stat.total_sale_money }}</span>
						</div>
					</div>
					<div class="member-num w-[352px] h-[190px] box-border pt-[22px] pr-[20px] pb-[17px] pl-[30px] cursor-pointer" @click="toLink('/member/member')">
						<div class="flex justify-between items-center mb-[15px]">
							<span class="text-[18px] text-[#4D5C6E] font-500">今日新增会员数</span>
							<el-tooltip effect="light" :content="'会员数:' + statInfo.member_stat.yesterday_member_count" placement="bottom">
								<div class="px-[5px] py-[3px] leading-[10px] text-[var(--el-color-primary)] border-solid border-[var(--el-color-primary)] border-[1px]  cursor-pointer text-[12px]">昨日</div>
							</el-tooltip>
						</div>
						<div class="flex items-center leading-[18px] mb-[20px]">
							<span class="text-[12px] text-[#666] font-400">昨日新增会员数</span>
							<span class="ml-[6px] text-[18px] font-500">{{ statInfo.member_stat.yesterday_member_count }}</span>
							<span class="text-[12px] text-[#666] font-400 ml-[9px]">较昨日</span>
							<span class="ml-[4px] text-[14px] text-[#FA5050]" v-if="calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count) >= 0">{{ Math.abs(calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count)) }}%</span>
							<span class="ml-[4px] text-[14px] text-[#05C08B]" v-if="calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count) < 0">{{ Math.abs(calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count)) }}%</span>
							<span class="iconfont icontransferout text-[#FA5050] !text-[13px]" v-if="calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count) >= 0"></span>
							<span class="iconfont icontransferout-copy text-[#05C08B] !text-[13px]" v-if="calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count) < 0"></span>
						</div>
						<div class="text-[34px] font-bold text-[#111] mb-[16px] leading-[34px]">{{ statInfo.member_stat.today_member_count }}</div>
						<div class="flex items-center justify-between text-[14px] leading-[18px]">
							<span class="text-[#666]">会员总数</span>
							<span class="text-[20px] text-[#333]">{{ statInfo.member_stat.total_member_count }}</span>
						</div>
					</div>
					<div class="store-num w-[352px] h-[190px] box-border pt-[22px] pr-[20px] pb-[17px] pl-[30px] cursor-pointer" @click="toLink('/shop/list')">
						<div class="flex justify-between items-center mb-[15px]">
							<span class="text-[18px] text-[#4D5C6E] font-500">今日申请店铺数</span>
							<el-tooltip effect="light" :content="'申请店铺数:' + statInfo.site_stat.yesterday_site_count" placement="bottom">
								<div class="px-[5px] py-[3px] leading-[10px] text-[var(--el-color-primary)] border-solid border-[var(--el-color-primary)] border-[1px]  cursor-pointer text-[12px]">昨日</div>
							</el-tooltip>
						</div>
						<div class="flex items-center leading-[18px] mb-[20px]">
							<span class="text-[12px] text-[#666] font-400">昨日申请店铺数</span>
							<span class="ml-[6px] text-[18px] font-500">{{ statInfo.site_stat.yesterday_site_count }}</span>
							<span class="text-[12px] text-[#666] font-400 ml-[9px]">较昨日</span>
							<span class="ml-[4px] text-[14px] text-[#FA5050]" v-if="calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count) >= 0">{{ Math.abs(calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count)) }}%</span>
							<span class="ml-[4px] text-[14px] text-[#05C08B]" v-if="calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count) < 0">{{ Math.abs(calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count)) }}%</span>
							<span class="iconfont icontransferout text-[#FA5050] !text-[13px]" v-if="calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count) >= 0"></span>
							<span class="iconfont icontransferout-copy text-[#05C08B] !text-[13px]" v-if="calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count) < 0"></span>
						</div>
						<div class="text-[34px] font-bold text-[#111] mb-[16px] leading-[34px]">{{ statInfo.site_stat.today_site_count }}</div>
						<div class="flex items-center justify-between text-[14px] leading-[18px]">
							<span class="text-[#666]">店铺总数</span>
							<span class="text-[20px] text-[#333]">{{ statInfo.site_stat.total_site_count }}</span>
						</div>
					</div>
				</div>
				<div class="flex items-center justify-between mt-[20px]">
					<div class="w-[724px] pt-[24px] pb-[49px] px-[20px] bg-[#F6F8FF] rounded-[8px] border-[1px] border-[rgba(61,135,227,0.06)] border-solid">
						<div class="flex justify-between items-center mb-[37px] leading-[10px]">
							<span class="text-[#4D5C6E] text-[18px] font-500">商城订单</span>
							<div class="text-[#999] text-[12px] font-500 cursor-pointer" @click="toLink('/mall/order/index')">
								<span>全部</span>
								<span class=" iconfont iconxiangyoujiantou !text-[12px]"></span>
							</div>
						</div>
						<div class="flex justify-between items-center">
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toHref('/mall/order/index', 1)">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.my_order_stat.wait_pay_order }}</div>
									<div class="text-[14px] text-[#666] font-500">待付款</div>
								</div>
							</div>
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toHref('/mall/order/index', 2)">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.my_order_stat.wait_delivery_order }}</div>
									<div class="text-[14px] text-[#666] font-500">待发货</div>
								</div>
							</div>
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toHref('/mall/order/index', 3)">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.my_order_stat.wait_take_order }}</div>
									<div class="text-[14px] text-[#666] font-500">已发货</div>
								</div>
							</div>
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toLink('/mall/order/refund')">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.my_order_stat.refund_order }}</div>
									<div class="text-[14px] text-[#666] font-500">退款中</div>
								</div>
							</div>
						</div>
					</div>
					<div class="ml-[20px] w-[352px]  pt-[24px] pb-[49px] px-[20px] box-border bg-[#F6F8FF] rounded-[8px] border-[1px] border-[rgba(61,135,227,0.06)] border-solid">
						<div class="flex justify-between items-center mb-[37px]  leading-[10px]">
							<span class="text-[#4D5C6E] text-[18px] font-500">提现</span>
							<div class="text-[#999] text-[12px] font-500 cursor-pointer" @click="toLink('/finance/cash_out')">
								<span>全部</span>
								<span class=" iconfont iconxiangyoujiantou !text-[12px]"></span>
							</div>
						</div>
						<div class="flex justify-between items-center">
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toLink('/finance/cash_out')">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.cash_out_stat.member_count }}
									</div>
									<div class="text-[14px] text-[#666] font-500">会员提现</div>
								</div>
							</div>
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toLink('/finance/shop_cash_out')">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.cash_out_stat.site_count }}
									</div>
									<div class="text-[14px] text-[#666] font-500">店铺提现</div>
								</div>
							</div>
						</div>
					</div>
					<div class="ml-[20px] w-[352px]  pt-[24px] pb-[49px] px-[20px] box-border bg-[#F6F8FF] rounded-[8px] border-[1px] border-[rgba(61,135,227,0.06)] border-solid">
						<div class="flex justify-between items-center mb-[37px]  leading-[10px]">
							<span class="text-[#4D5C6E] text-[18px] font-500">商品</span>
							<div class="text-[#999] text-[12px] font-500 cursor-pointer" @click="toHref('/mall/goods/list', '')">
								<span>全部</span>
								<span class=" iconfont iconxiangyoujiantou !text-[12px]"></span>
							</div>
						</div>
						<div class="flex justify-between items-center ">
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toHref('/mall/goods/list', 1)">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.goods_stat.sale_goods_num }}</div>
									<div class="text-[14px] text-[#666] font-500">销售中</div>
								</div>
							</div>
							<div class="flex-1 flex justify-center items-center cursor-pointer" @click="toHref('/mall/goods/list', -1)">
								<div>
									<div class="text-[20px] font-bold text-[#111] mb-[11px] leading-[18px] text-center">{{ statInfo.goods_stat.audit_goods_num }}</div>
									<div class="text-[14px] text-[#666] font-500">待审核</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="flex items-center justify-between mt-[20px]">
					<div class="flex-1 pt-[24px] box-border bg-[#F6F8FF] rounded-[8px] border-[1px] border-[rgba(61,135,227,0.06)] border-solid mr-[20px]">
						<div class="text-[18px] text-[#4D5C6E] font-500  px-[30px] mb-[32px]">近7天订单数</div>
						<div ref="orderStat" :style="{ width: '100%', height: '390px' }"></div>
					</div>
					<div class="flex-1 pt-[24px] box-border  bg-[#F6F8FF] rounded-[8px] border-[1px] border-[rgba(61,135,227,0.06)] border-solid">
						<div class="text-[18px] text-[#4D5C6E] font-500  px-[30px] mb-[32px]">销售额(元)</div>
						<div ref="saleStat" :style="{ width: '100%', height: '390px' }"></div>
					</div>
				</div>
			</div>
			<div class="w-[352px] ml-[20px]">
				<div class="client-service w-[352px] h-[382px] px-[30px] pt-[23px]">
					<div class="font-500 text-[#4D5C6E] text-[18px] mb-[23px] leading-[10px]">客户服务</div>
					<div class="mb-[55px]">
						<div class="mb-[16px] leading-[18px]">
							<i class="iconfont icon31dianhua !text-[18px] text-[#9195A3]"></i>
							<span class="ml-[8px] text-[#666] text-[14px]">客服电话：</span>
						</div>
						<div class="text-[20px] font-500 text-[#4D5C6E] ml-[26px] leading-[13px] cursor-pointer" @click="copyEvent('400-886-7993')">400-886-7993</div>
					</div>
					<div class="mb-[55px]">
						<div class="mb-[16px] leading-[18px]">
							<i class="iconfont iconguanwang !text-[18px] text-[#9195A3]"></i>
							<span class="ml-[8px] text-[#666] text-[14px]">官网：</span>
						</div>
						<div class="text-[20px] font-500 text-[#4D5C6E] ml-[26px] leading-[13px] cursor-pointer" @click="toApplication()">https://www.niucloud.com</div>
					</div>
					<div>
						<div class="mb-[16px] leading-[18px]">
							<i class="iconfont iconshuiqijiaoliu !text-[18px] text-[#9195A3]"></i>
							<span class="ml-[8px] text-[#666] text-[14px]">交流群：</span>
						</div>
						<div class="text-[20px] font-500 text-[#4D5C6E] ml-[26px] leading-[13px] cursor-pointer" @click="copyEvent('29507902')">29507902</div>
					</div>
				</div>
				<div class="mt-[20px] box-border bg-[#F6F8FF] rounded-[8px] border-[1px] border-[rgba(61,135,227,0.06)] border-solid pl-[30px] pr-[22px] pt-[24px] pb-[12px]">
					<div class="font-500 leading-[10px] text-[#4D5C6E] text-[18px] mb-[26px]">最新动态</div>
					<div>
						<template v-for="(  item, index  ) in statNews  " :key="index">
							<div class="flex items-center justify-between mb-[22px] leading-[18px] cursor-pointer" @click="toDetail('/web/article/detail', item.id)">
								<div>
									<i class="iconfont icon31huidaodingbu !text-[12px] text-[#FA5050]" v-if="!index"></i>
									<span class="text-[14px] text-[#9195A3]" v-else>{{ index }}</span>
								</div>
								<span class="truncate flex-1 ml-[10px] text-[#666] text-[14px]">{{ item.title }}</span>
								<span class="text-[12px]  text-[#999] ml-[54px]">{{ item.create_time }}</span>
							</div>
						</template>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { t } from '@/lang'
import { useClipboard } from '@vueuse/core'
import { ElMessage, ElNotification } from 'element-plus'
import { getStatInfo, getStatChart, getStatNewsList } from '@/app/api/stat'
import { checkJobStatus } from '@/app/api/sys'
import * as echarts from 'echarts'
import useStyleStore from '@/stores/modules/style'
import { getFrameworkNewVersion } from '@/app/api/module'
import { useRoute, useRouter } from 'vue-router'
import { AnyObject } from '@/types/global'

const loading = ref(true)
const orderStat = ref<any>(null)
const saleStat = ref<any>(null)

const styleStore = useStyleStore()

interface NewVersion {
	last_version: string
}
interface StatNews {
	id: number,
	title: string,
	create_time: string
}
interface StatInfo {
	order_stat: AnyObject,
	sale_stat: AnyObject,
	member_stat: AnyObject,
	site_stat: AnyObject,
	my_order_stat: AnyObject,
	cash_out_stat: AnyObject,
	goods_stat: AnyObject
}
interface StatChart {
	access_sum: AnyObject,
	order_num: AnyObject,
	refund_money: AnyObject,
	sale_money: AnyObject,
	time: AnyObject
}

const newVersion = ref<NewVersion>({
    last_version: ''
})

getFrameworkNewVersion().then(({ data }) => {
    newVersion.value = data
}).catch()


const statInfo = ref<StatInfo>({
    order_stat: {
		today_order_count: 0,
		yesterday_order_count: 0,
		total_order_count: 0
	},
    sale_stat: {
		today_sale_money: 0,
		yesterday_sale_money: 0,
		total_sale_money: 0
	},
    member_stat: {
		today_member_count: 0,
		yesterday_member_count: 0,
		total_member_count: 0
	},
    site_stat: {
		today_site_count: 0,
		yesterday_site_count: 0,
		total_site_count: 0
	},
    my_order_stat: {
		wait_pay_order: 0,
		wait_delivery_order: 0,
		wait_take_order: 0,
		refund_order: 0
	},
    cash_out_stat: {
		member_count: 0,
		site_count: 0
	},
    goods_stat: {
		sale_goods_num: 0,
		audit_goods_num: 0
	}
})
const getStatInfoFn = async (id: number = 0) => {
    statInfo.value = await (await getStatInfo()).data
    loading.value = false
}
getStatInfoFn()

const statChart = ref<StatChart>({
    access_sum: [],
    order_num: [],
    refund_money: [],
    sale_money: [],
    time: []
})
const getStatChartFn = async (id: number = 0) => {
    statChart.value = await (await getStatChart()).data
    loading.value = false
    setTimeout(() => {
        drawChart('')
        drawChartTo('')
    }, 20)
}
getStatChartFn()

const statNews = ref<StatNews>({
    id: 0,
    title: '',
    create_time: '',
})
const getStatNewsFn = async (id: number = 0) => {
    statNews.value = await (await getStatNewsList({ num: 6 })).data
    loading.value = false
}
getStatNewsFn()

// 绘制折线图
const drawChart = (item: any) => {
    let value = statChart.value.order_num
    if (item) value = item
    if (!orderStat.value) return
    // 新增站点
    const newSiteStatChart = echarts.init(orderStat.value)
    const newSiteStatOption = ref({
        legend: {},
        xAxis: {
            data: []
        },
        yAxis: {},
        tooltip: {
            trigger: 'axis'
        },
        series: [
            {
                type: 'line',
                data: []
            }
        ]
    })
    newSiteStatOption.value.xAxis.data = statChart.value.time
    newSiteStatOption.value.series[0].data = value
    newSiteStatChart.setOption(newSiteStatOption.value)
}

const drawChartTo = (item: any) => {
    let valueTo = statChart.value.sale_money
    if (item) valueTo = item
    if (!saleStat.value) return
    const saleStatChart = echarts.init(saleStat.value)
    const saleStatOption = ref({
        // title: {
        //     text: '销售额(元)'
        // },
        legend: {},
        xAxis: {
            data: []
        },
        yAxis: {},
        tooltip: {
            trigger: 'axis'
        },
        series: [
            {
                type: 'line',
                data: []
            }
        ]
    })
    saleStatOption.value.xAxis.data = statChart.value.time
    saleStatOption.value.series[0].data = valueTo
    saleStatChart.setOption(saleStatOption.value)
}

const router = useRouter()
const route = useRoute()
if (route.path == '/admin/index') {
    styleStore.changeStyle()
}
watch(() => route.path, (newval, oldval) => {
    if (newval !== '/admin/index') {
        styleStore.changeBlack()
    }
})

// 计算同日比
const calculate = (todayNum: any, yesterdayNum: any) => {
    if (Number(yesterdayNum) == 0) {
        return 0
    } else {
        const num = ((Number(todayNum) - Number(yesterdayNum)) / Number(yesterdayNum)) * 100
        return parseFloat(num).toFixed(2)
    }
}

/**
 * 链接跳转
 */
const toLink = (link: any) => {
    router.push(link)
}
const toHref = (url: any, status: any) => {
    router.push({
        path: url,
        query: { status }
    })
}
const toDetail = (url: any, id: any) => {
    window.open(location.origin + url + '?id=' + id)
}

const toApplication = () => {
    window.open('https://www.niucloud.com/')
}

/**
 * 复制
 */
const { copy, isSupported, copied } = useClipboard()
const copyEvent = (text: string) => {
    if (!isSupported.value) {
        ElMessage({
            message: t('notSupportCopy'),
            type: 'warning'
        })
        return
    }
    copy(text)
}

watch(copied, () => {
    if (copied.value) {
        ElMessage({
            message: t('copySuccess'),
            type: 'success'
        })
    }
})

// 更新时间
const time = ref('')
const nowTime = () => {
    const date = new Date()
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hh = checkTime(date.getHours())
    const mm = checkTime(date.getMinutes())
    const ss = checkTime(date.getSeconds())
    function checkTime(i: any) {
        if (i < 10) {
            return '0' + i
        }
        return i
    }
    time.value = year + '-' + month + '-' + day + ' ' + hh + ':' + mm + ':' + ss
}
nowTime()

const toUpgrade = () => {
    router.push({ path: '/tools/authorize' })
}

let notification = null

/**
 * 校验消息队列
 */
const checkJob = () => {
    checkJobStatus().then(res => {
        if (!res.data) {
            notification = ElNotification({
                title: '温馨提示',
                dangerouslyUseHTMLString: true,
                duration: 0,
                position: 'bottom-right',
                message: '您的【队列】运行异常，会导致商城订单相关任务无法正常使用，请尽快启动队列<br><a class="text-[var(--el-color-primary)]" href="https://doc.niucloud.com/saas.html?keywords=/ji-hua-ren-wu-pei-zhi" target="_blank">>>> 点击查看处理方法 <<<</a>',
                type: 'warning'
            })
        }
    }).catch(() => {
    })
}
checkJob()
// if (notification) {
// 	notification.close()
// }
</script>

<style lang="scss" scoped>
.order-num {
	background-image: url('@/app/assets/images/index/index01.png');
	background-size: 100% 100%;
}

.sell-num {
	background-image: url('@/app/assets/images/index/index02.png');
	background-size: 100% 100%;
}

.member-num {
	background-image: url('@/app/assets/images/index/index03.png');
	background-size: 100% 100%;
}

.store-num {
	background-image: url('@/app/assets/images/index/index04.png');
	background-size: 100% 100%;
}

.client-service {
	background-image: url('@/app/assets/images/index/index05.png');
	background-size: 100% 100%;
}

:deep(.site .el-card__header) {
	padding-left: 0 !important;
}

.text-color {
	color: var(--el-menu-active-color);
}

:deep(.el-descriptions__body) {
	background-color: #fbfbfb !important;
	padding: 16px 34px 2px;
	border-bottom: solid 1px #e9ebf0;
}

:deep(.el-descriptions__label) {
	font-weight: 600;
	color: #575657;
	line-height: 20px;
}

:deep(.el-descriptions__content) {
	color: #666;
}
</style>
