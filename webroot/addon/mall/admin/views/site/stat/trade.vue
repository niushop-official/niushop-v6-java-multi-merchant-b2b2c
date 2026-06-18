<template>
    <div class="main-container">
        <el-card class="box-card !border-none mb-[20px]" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
        </el-card>
        <!-- 头部筛选 -->
        <el-card class="!border-none mb-[20px]" shadow="never">
            <div class="flex justify-between items-center mb-[30px]">
                <div class="text-[16px] font-bold pl-[10px] title-wrap">交易概览</div>
                <div class="flex items-center">
                    <span class="mr-[10px] text-[14px] text-[#666]">筛选日期：</span>
                    <el-date-picker v-model="dateRange" type="daterange"  range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" :shortcuts="shortcuts" :disabled-date="disabledDate" class="!w-[240px] mr-[10px]"  @change="handleDateChange" />
                    <el-button-group>
                        <el-button :type="dateType === 'yesterday' ? 'primary' : ''" @click="setDateType('yesterday')">昨日</el-button>
                        <el-button :type="dateType === '7days' ? 'primary' : ''" @click="setDateType('7days')">近7天</el-button>
                        <el-button :type="dateType === '30days' ? 'primary' : ''" @click="setDateType('30days')">近30天</el-button>
                    </el-button-group>
                    <el-button type="primary" class="ml-[10px]" @click="loadOverviewData">筛选</el-button>
                </div>
            </div>
            <!-- 交易数据卡片 -->
            <el-row :gutter="15">
                <el-col :span="4" v-for="(item, index) in tradeStat" :key="index" class="mb-[15px]">
                    <div class="bg-[#f6f8ff] rounded-[10px] p-[16px] h-[88px] box-border flex flex-col justify-center">
                        <div class="text-[14px] text-[#666] mb-[10px]">{{ item.label }}</div>
                        <div class="text-[24px] font-bold text-[#333] leading-[24px]">{{ item.value }}</div>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <!-- 交易详情趋势图 -->
        <el-card class="!border-none mb-[20px]" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <div class="text-[16px] font-bold pl-[10px] title-wrap">交易详情</div>
                <div>
                    <el-button type="primary" plain @click="exportChart('trend')">导出图表</el-button>
                </div>
            </div>
            <div ref="tradeTrendChart" style="width: 100%; height: 320px;"></div>
        </el-card>

        <!-- 交易转化 -->
        <el-card class="!border-none mb-[20px]" shadow="never">
            <div class="text-[15px] font-bold pl-[10px] mb-[20px] title-wrap">交易转化</div>
            <div class="flex items-center">
                <!-- Data Section (Metrics + Funnel) -->
                <div class="w-[67%] flex flex-col">
                    <!-- Visitor Row -->
                    <div class="flex bg-[#f6f9fe]">
                        <div class="flex-1 flex items-center">
                            <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                <div class="text-[14px]">访客数</div>
                                <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.visit_num || 0 }}</div>
                                <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.visit_num_loop_rate)">{{ formatRate(conversionData.visit_num_loop_rate) }} {{ getRateArrow(conversionData.visit_num_loop_rate) }}</span></div>
                            </div>
                        </div>
                        <div class="flex-shrink-0 w-[230px] bg-[#4071ff] flex items-center justify-end pr-[64px] box-border text-white text-[28px] font-bold funnel-stage-v">访客</div>
                    </div>

                    <!-- Order Row -->
                    <div class="flex bg-[#ebf2ff]">
                        <div class="flex-1 overflow-x-auto">
                            <div class="flex items-center flex-nowrap">
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">下单人数</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.order_member_count || 0 }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.order_member_count_loop_rate)">{{ formatRate(conversionData.order_member_count_loop_rate) }} {{ getRateArrow(conversionData.order_member_count_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">下单笔数</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.order_count || 0 }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.order_count_loop_rate)">{{ formatRate(conversionData.order_count_loop_rate) }} {{ getRateArrow(conversionData.order_count_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">下单件数</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.order_num || 0 }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.order_num_loop_rate)">{{ formatRate(conversionData.order_num_loop_rate) }} {{ getRateArrow(conversionData.order_num_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">下单金额</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.crete_order_money || '0.00' }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.crete_order_money_loop_rate)">{{ formatRate(conversionData.crete_order_money_loop_rate) }} {{ getRateArrow(conversionData.crete_order_money_loop_rate) }}</span></div>
                                </div>
                            </div>
                        </div>
                        <div class="flex-shrink-0 w-[230px] bg-[#4fdca7] flex items-center justify-end pr-[64px] box-border text-white text-[28px] font-bold funnel-stage-o">下单 </div>
                    </div>

                    <!-- Pay Row -->
                    <div class="flex bg-[#f6f9fe]">
                       <div class="flex-1 overflow-x-auto">
                            <div class="flex items-center flex-nowrap">
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">支付人数</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.pay_member_count || 0 }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.pay_member_count_loop_rate)">{{ formatRate(conversionData.pay_member_count_loop_rate) }} {{ getRateArrow(conversionData.pay_member_count_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">支付订单数</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.pay_count || 0 }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.pay_count_loop_rate)">{{ formatRate(conversionData.pay_count_loop_rate) }} {{ getRateArrow(conversionData.pay_count_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">支付件数</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.pay_num || 0 }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.pay_num_loop_rate)">{{ formatRate(conversionData.pay_num_loop_rate) }} {{ getRateArrow(conversionData.pay_num_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">支付金额</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.order_money || '0.00' }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.order_money_loop_rate)">{{ formatRate(conversionData.order_money_loop_rate) }} {{ getRateArrow(conversionData.order_money_loop_rate) }}</span></div>
                                </div>
                                <div class="min-w-[165px] p-[15px] text-[#666] flex flex-col box-border">
                                    <div class="text-[14px]">客单价</div>
                                    <div class="text-[24px] font-bold leading-[20px] my-[10px]">{{ conversionData.member_unit_price || '0.00' }}</div>
                                    <div class="text-[12px]">环比 <span :class="getRateClass(conversionData.member_unit_price_loop_rate)">{{ formatRate(conversionData.member_unit_price_loop_rate) }} {{ getRateArrow(conversionData.member_unit_price_loop_rate) }}</span></div>
                                </div>
                            </div>
                        </div>
                        <div class="flex-shrink-0 w-[230px] bg-[#ffec41] flex items-center justify-end pr-[64px] box-border text-white text-[28px] font-bold funnel-stage-p">支付</div>
                    </div>
                </div>

                <!-- Rate Section (Conversion Lines) -->
                <div class="w-[33%] flex items-center ml-[15px]">
                    <div>
                        <div class="flex items-center">
                            <div class="h-[118px] w-[17px] border-wrap"></div>
                            <div class="mx-[18px] text-[14px] text-center text-[#333]">访客-下单<br>转化率 {{ conversionData.visit_order_rate || '0.00' }}%</div>
                        </div>
                        <div class="flex items-center mt-[15px]">
                            <div class="h-[118px] w-[17px] border-wrap"></div>
                            <div class="mx-[18px] text-[14px] text-center text-[#333]">下单-支付<br>转化率 {{ conversionData.order_pay_rate || '0.00' }}%</div>
                        </div>
                    </div>
                    <div>
                        <div class="flex items-center">
                            <div class="h-[275px] w-[17px] border-wrap"></div>
                            <div class="mx-[18px] text-[14px] text-center text-[#333]">访客-支付<br>转化率 {{ conversionData.visit_pay_rate || '0.00' }}%</div>
                        </div>
                    </div>
                </div>
            </div>
        </el-card>

        <el-row :gutter="20">
            <!-- 付款金额渠道分析 -->
            <el-col :span="12">
                <el-card class="!border-none !h-full" shadow="never">
                    <div class="text-[16px] font-bold pl-[10px] mb-[20px] title-wrap">付款金额渠道分析</div>
                    <div ref="channelChart" style="width: 100%; height: 350px;"></div>
                </el-card>
            </el-col>
            <!-- 成交客户构成 -->
            <el-col :span="12">
                <el-card class="!border-none" shadow="never">
                    <div class="flex items-center mb-[20px]">
                        <div class="text-[16px] font-bold pl-[10px] title-wrap">成交客户构成</div>
                        <el-tooltip content="统计时间内，交易完成的各种等级客户构成" placement="top">
                            <span class="iconfont iconwenhao ml-[5px] text-[#999] cursor-pointer"></span>
                        </el-tooltip>
                    </div>
                    <div ref="customerChart" style="width: 100%; height: 280px;"></div>
                    <div class="mt-[20px]">
                        <el-table :data="customerStatTable">
                            <el-table-column prop="label" label="" min-width="100" />
                            <el-table-column prop="pay_money" label="付款金额" align="center" />
                            <el-table-column prop="sale_money" label="销售额" align="center" />
                            <el-table-column prop="pay_member_count" label="支付人数" align="center" />
                        </el-table>
                    </div>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getTradeStatBasic, getTradeTrend, getTradeConversion, getTradeChannel, getTradeDealMember } from '@/addon/mall/api/site/stat'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const overviewLoading = ref(false)
const dateType = ref('30days')
const dateRange = ref<string[]>([])

// 交易概况
const tradeStat = ref([
    { label: '实际销售额(元)', value: '0.00' },
    { label: '付款金额(元)', value: '0.00' },
    { label: '余额支付金额(元)', value: '0.00' },
    { label: '下单订单数', value: '0' },
    { label: '付款订单数', value: '0' },
    { label: '下单商品件数', value: '0' },
    { label: '付款商品件数', value: '0' },
    { label: '下单人数', value: '0' },
    { label: '付款人数', value: '0' },
    { label: '售后商品件数', value: '0' },
    { label: '售后金额', value: '0.00' },
    { label: '售后会员数', value: '0' }
])

const conversionData = ref<any>({})

const customerStatTable = ref([
    { label: '老客户', pay_money: '0.00', sale_money: '0.00', pay_member_count: 0 },
    { label: '新客户', pay_money: '0.00', sale_money: '0.00', pay_member_count: 0 }
])

const getRateClass = (rate: number | string) => {
    const val = parseFloat(rate as string)
    if (val > 0) return 'text-[#E63044]'
    if (val < 0) return 'text-[#2fba63]'
    return 'text-[#909399]'
}

const getRateArrow = (rate: number | string) => {
    const val = parseFloat(rate as string)
    if (val > 0) return '▲'
    if (val < 0) return '▼'
    return ''
}

const formatRate = (rate: number | string) => {
    const val = parseFloat(rate as string)
    if (isNaN(val)) return '0%'
    return (val > 0 ? '+' : '') + val + '%'
}

const tradeTrendChart = ref(null)
const channelChart = ref(null)
const customerChart = ref(null)

const shortcuts = [
    {
        text: '昨日',
        value: () => {
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24)
            return [start, start]
        }
    },
    {
        text: '近7天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        }
    },
    {
        text: '近30天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            return [start, end]
        }
    }
]

const disabledDate = (time: Date) => {
    return time.getTime() > Date.now()
}

const setDateType = (type: string) => {
    dateType.value = type
    const end = new Date()
    const start = new Date()
    if (type === 'yesterday') {
        start.setTime(start.getTime() - 3600 * 1000 * 24)
        end.setTime(end.getTime() - 3600 * 1000 * 24)
    } else if (type === '7days') {
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
    } else if (type === '30days') {
        start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
    }
    dateRange.value = [
        start.toISOString().split('T')[0],
        end.toISOString().split('T')[0]
    ]
    loadOverviewData()
}

const handleDateChange = (val: any) => {
    if (val) {
        const start = new Date(val[0]).getTime()
        const end = new Date(val[1]).getTime()
        const day = (end - start) / (1000 * 60 * 60 * 24)
        if (day > 31) {
            ElMessage.error('日期范围最大选择31天')
            dateRange.value = []
            return
        }
    }
    dateType.value = ''
    loadOverviewData()
}

const loadOverviewData = () => {
    overviewLoading.value = true
    Promise.all([
        getTradeStatBasic({
            start_time: dateRange.value[0],
            end_time: dateRange.value[1]
        }).then((res: any) => {
            const data = res.data
            tradeStat.value = [
                { label: '实际销售额(元)', value: data.sale_money },
                { label: '付款金额(元)', value: data.order_money },
                { label: '余额支付金额(元)', value: data.balance_pay_money },
                { label: '下单订单数', value: data.order_count },
                { label: '付款订单数', value: data.pay_count },
                { label: '下单商品件数', value: data.order_num },
                { label: '付款商品件数', value: data.pay_num },
                { label: '下单人数', value: data.order_member_count },
                { label: '付款人数', value: data.pay_member_count },
                { label: '售后商品件数', value: data.refund_num },
                { label: '售后金额', value: data.refund_money },
                { label: '售后会员数', value: data.refund_member_count }
            ]
        }),
        getTradeTrendFn(),
        getTradeConversionFn(),
        getTradeChannelFn(),
        getTradeDealMemberFn()
    ]).finally(() => {
        overviewLoading.value = false
    })
}

const getTradeConversionFn = () => {
    return getTradeConversion({
        start_time: dateRange.value[0],
        end_time: dateRange.value[1]
    }).then((res: any) => {
        conversionData.value = res.data
    })
}

const getTradeChannelFn = () => {
    return getTradeChannel({
        start_time: dateRange.value[0],
        end_time: dateRange.value[1]
    }).then((res: any) => {
        const data = res.data
        if (channelChart.value) {
            const myChart = echarts.getInstanceByDom(channelChart.value) || echarts.init(channelChart.value)
            const option = {
                tooltip: { trigger: 'item' },
                legend: { orient: 'vertical', right: '10%', top: 'center' },
                series: [
                    {
                        name: '渠道分析',
                        type: 'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
                        label: { show: false, position: 'center' },
                        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
                        labelLine: { show: false },
                        data: data.map((item: any) => ({
                            value: item.count,
                            name: item.channel_name
                        })),
                        color: ['#7B68EE', '#FF6A00', '#00C050', '#2878FF', '#FF4081']
                    }
                ]
            }
            myChart.setOption(option)
            const resizeObserver = new ResizeObserver(() => {
                myChart.resize()
            })
            resizeObserver.observe(channelChart.value)
        }
    })
}

const getTradeDealMemberFn = () => {
    return getTradeDealMember({
        start_time: dateRange.value[0],
        end_time: dateRange.value[1]
    }).then((res: any) => {
        const data = res.data
        customerStatTable.value = data.map((item: any) => ({
            label: item.level_name,
            pay_money: item.order_money,
            sale_money: item.goods_money,
            pay_member_count: item.pay_member_count
        }))

        if (customerChart.value) {
            const myChart = echarts.getInstanceByDom(customerChart.value) || echarts.init(customerChart.value)
            const option = {
                tooltip: { trigger: 'item' },
                legend: { orient: 'vertical', right: '10%', top: 'center' },
                series: [
                    {
                        name: '客户构成',
                        type: 'pie',
                        radius: ['50%', '70%'],
                        avoidLabelOverlap: false,
                        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
                        label: { show: false, position: 'center' },
                        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
                        labelLine: { show: false },
                        data: data.map((item: any) => ({
                            value: item.pay_member_count,
                            name: item.level_name
                        })),
                        color: ['#2878FF', '#FF4081', '#00C050', '#7B68EE', '#FF6A00']
                    }
                ]
            }
            myChart.setOption(option)
            const resizeObserver = new ResizeObserver(() => {
                myChart.resize()
            })
            resizeObserver.observe(customerChart.value)
        }
    })
}

const getTradeTrendFn = () => {
    return getTradeTrend({
        start_time: dateRange.value[0],
        end_time: dateRange.value[1]
    }).then((res: any) => {
        const data = res.data
        if (tradeTrendChart.value) {
            const myChart = echarts.getInstanceByDom(tradeTrendChart.value) || echarts.init(tradeTrendChart.value)
            const option = {
                tooltip: {
                    trigger: 'axis',
                    formatter: (params: any) => {
                        let res = params[0].name + '<br/>'
                        params.forEach((item: any) => {
                            let value = item.value
                            if (item.seriesName.includes('转化率')) {
                                value = value + '%'
                            }
                            res += `<div style="display: flex; align-items: center; justify-content: space-between;">
                                <div style="display: flex; align-items: center;">
                                    ${item.marker}
                                    <span style="margin-right: 10px;">${item.seriesName}</span>
                                </div>
                                <span style="font-weight: bold;">${value}</span>
                            </div>`
                        })
                        return res
                    }
                },
                legend: {
                    data: ['访客数', '支付金额', '支付人数', '支付件数', '访客-下单转化率', '下单-支付转化率', '访客-支付转化率']
                },
                grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: data.date_list
                },
                yAxis: { type: 'value' },
                series: [
                    { name: '访客数', type: 'line', smooth: true, data: data.visit_num },
                    { name: '支付金额', type: 'line', smooth: true, data: data.order_money },
                    { name: '支付人数', type: 'line', smooth: true, data: data.pay_member_count },
                    { name: '支付件数', type: 'line', smooth: true, data: data.pay_num },
                    { name: '访客-下单转化率', type: 'line', smooth: true, data: data.visit_order_rate },
                    { name: '下单-支付转化率', type: 'line', smooth: true, data: data.order_pay_rate },
                    { name: '访客-支付转化率', type: 'line', smooth: true, data: data.visit_pay_rate }
                ],
                color: ['#FF6A00', '#2878FF', '#00C050', '#7B68EE', '#FFC107', '#00BCD4', '#E91E63']
            }
            myChart.setOption(option)

            const resizeObserver = new ResizeObserver(() => {
                myChart.resize()
            })
            resizeObserver.observe(tradeTrendChart.value)
        }
    })
}

const initCharts = () => {
}

const exportChart = (type: string) => {
    let chartDom = null
    let name = ''
    if (type === 'trend') {
        chartDom = tradeTrendChart.value
        name = '交易详情趋势图'
    }

    if (chartDom) {
        const myChart = echarts.getInstanceByDom(chartDom)
        if (myChart) {
            const url = myChart.getDataURL({
                type: 'png',
                pixelRatio: 2,
                backgroundColor: '#fff'
            })
            const link = document.createElement('a')
            link.download = name + '.png'
            link.href = url
            link.click()
        }
    }
}

onMounted(() => {
    setDateType('30days')
    nextTick(() => {
        initCharts()
    })
})
</script>

<style scoped lang="scss">
.title-wrap{
    position: relative;
    &::after{
        content: '';
        width: 3px;
        height: 13px;
        background-color: var(--el-color-primary);
        position: absolute;
        top: 50%;
        left: 0px;
        transform: translateY(-50%);
        border-radius: 10px;
    }
}
::-webkit-scrollbar {
    width: 6px;
    height: 6px;
    background-color: rgba(0, 0, 0, .1);
}
::-webkit-scrollbar-thumb {
    background-color: #b0b3bf;
}
::-webkit-scrollbar-track {
    background-color: #d7d7d7;
}
.el-row {
    margin-bottom: 20px;
}
.el-row:last-child {
    margin-bottom: 0;
}
.funnel-stage-v {
    clip-path: polygon(0 0, 100% 0, 100% 100%, 10% 100%);
}
.funnel-stage-o {
    clip-path: polygon(10% 0, 100% 0, 100% 100%, 20% 100%);
}
.funnel-stage-p {
    clip-path: polygon(20% 0, 100% 0, 100% 100%, 30% 100%);
}

.funnel-label {
    color: white;
    font-size: 16px;
    font-weight: bold;
}

.border-wrap{
    border: 1px solid #e5e5e5;
    border-left-color: transparent;
}
</style>
