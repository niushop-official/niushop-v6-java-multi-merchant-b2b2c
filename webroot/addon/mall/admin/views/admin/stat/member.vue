<template>
    <div class="main-container">
        <el-card class="box-card !border-none mb-[20px]" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
        </el-card>
        <!-- 顶部卡片 -->
        <el-row :gutter="20" class="mb-[20px]">
            <el-col :span="6">
                <div class="bg-white h-[140px] py-[30px] pr-[28px] pl-[40px] flex justify-between items-center">
                    <div>
                        <div class="text-[16px] text-[#333] mb-[6px] flex items-center">
                            <span>累计会员数</span>
                            <el-tooltip content="累计会员总数" placement="top">
                                <el-icon class="ml-[4px] text-[#999] cursor-pointer pb-[3px]"><QuestionFilled /></el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-[36px] leading-[50px] font-bold text-[#32b7ff]">{{ memberStat.total_member || 0 }}</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="bg-white h-[140px]  py-[30px] pr-[28px] pl-[40px] flex justify-between items-center">
                    <div>
                        <div class="text-[16px] text-[#333] mb-[6px] flex items-center">
                            <span>付费会员数</span>
                            <el-tooltip content="有过消费记录的会员数" placement="top">
                                <el-icon class="ml-[4px] text-[#999] cursor-pointer pb-[3px]"><QuestionFilled /></el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-[36px] leading-[50px] font-bold text-[#7B68EE]">{{ memberStat.pay_member_count || 0 }}</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="bg-white h-[140px] py-[30px] pr-[28px] pl-[40px] flex justify-between items-center">
                    <div>
                        <div class="text-[16px] text-[#333] mb-[6px] flex items-center">
                            <span>活跃会员数</span>
                            <el-tooltip content="在3个月内登陆过的会员数" placement="top">
                                <el-icon class="ml-[4px] text-[#999] cursor-pointer pb-[3px]"><QuestionFilled /></el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-[36px] leading-[50px] font-bold text-[#FF6A00]">{{ memberStat.active_member_count || 0 }}</div>
                    </div>
                </div>
            </el-col>
            <el-col :span="6">
                <div class="bg-white h-[140px]  py-[30px] pr-[28px] pl-[40px] flex justify-between items-center">
                    <div>
                        <div class="text-[16px] text-[#333] mb-[6px] flex items-center">
                            <span class="leading-[1]">沉默会员数</span>
                            <el-tooltip content="超过三个月未登录的会员数" placement="top">
                                <el-icon class="ml-[4px] text-[#999] cursor-pointer pb-[3px]"><QuestionFilled /></el-icon>
                            </el-tooltip>
                        </div>
                        <div class="text-[36px] leading-[50px] font-bold text-[#00C050]">{{ memberStat.silent_member_count || 0 }}</div>
                    </div>
                </div>
            </el-col>
        </el-row>

        <!-- 会员数据概览 -->
        <el-card class="!border-none mb-[20px]" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <div class="text-[16px] font-bold pl-[10px] title-wrap">会员数据</div>
                <div class="flex items-center">
                    <span class="mr-[10px] text-[14px] text-[#666]">筛选日期：</span>
                    <el-date-picker v-model="dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" :shortcuts="shortcuts"  :disabled-date="disabledDate" class="!w-[240px] mr-[10px]" @change="handleDateChange"/>
                    <el-button-group>
                        <el-button :type="dateType === 'yesterday' ? 'primary' : ''" @click="setDateType('yesterday')">昨日</el-button>
                        <el-button :type="dateType === '7days' ? 'primary' : ''" @click="setDateType('7days')">近7天</el-button>
                        <el-button :type="dateType === '30days' ? 'primary' : ''" @click="setDateType('30days')">近30天</el-button>
                    </el-button-group>
                    <el-button type="primary" class="ml-[10px]" @click="loadOverviewData">筛选</el-button>
                </div>
            </div>

            <el-row :gutter="20" class="mb-[20px]">
                <el-col :span="4" v-for="(item, index) in dataOverview" :key="index" class="mb-[20px]">
                    <div class="bg-[#f6f8ff] rounded-[8px] p-[16px] h-[100px] flex flex-col justify-center">
                        <div class="text-[14px] text-[#333] mb-[10px]">{{ item.label }}</div>
                        <div class="text-[24px] font-bold leading-[20px] text-[#333]">{{ item.value }}</div>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <!-- 会员详情图表 -->
        <el-card class="!border-none mb-[20px]" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <div class="text-[16px] font-bold pl-[10px] title-wrap">会员详情</div>
                <div>
                    <el-button type="primary" plain @click="exportChart">导出图表</el-button>
                </div>
            </div>
            <div ref="memberTrendChart" style="width: 100%; height: 400px;"></div>
         </el-card>

        <!-- 来源与时长 -->
        <el-row :gutter="20" class="mb-[20px]">
            <el-col :span="12">
                <el-card class="!border-none" shadow="never">
                    <div class="text-[16px] font-bold pl-[10px] mb-[20px] title-wrap">会员来源</div>
                    <div ref="memberSourceChart" style="width: 100%; height: 350px;"></div>
                </el-card>
            </el-col>
            <el-col :span="12">
                <el-card class="!border-none" shadow="never">
                    <div class="text-[16px] font-bold pl-[10px] mb-[20px] title-wrap">客户时长</div>
                    <div ref="customerDurationChart" style="width: 100%; height: 350px;"></div>
                </el-card>
            </el-col>
        </el-row>

        <!-- 会员消费排行 -->
        <el-card class="!border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <div class="text-[16px] font-bold pl-[10px] title-wrap">会员消费排行</div>
                <div class="flex items-center">
                    <span class="mr-[10px] text-[14px] text-[#666]">会员：</span>
                    <el-input v-model="searchParams.keyword" placeholder="手机号/微信昵称/姓名" class="!w-[200px] mr-[20px]" />
                    <span class="mr-[10px] text-[14px] text-[#666]">排行：</span>
                    <el-select v-model="searchParams.sort_field" class="!w-[150px] mr-[20px]">
                        <el-option label="付款订单数" value="pay_order_count" />
                        <el-option label="付款商品数" value="pay_goods_count" />
                        <el-option label="付款金额" value="pay_money" />
                        <el-option label="退款商品数" value="refund_goods_count" />
                        <el-option label="退款金额" value="refund_money" />
                    </el-select>
                    <span class="mr-[10px] text-[14px] text-[#666]">统计时间：</span>
                    <el-date-picker  v-model="searchParams.date_range" type="daterange" range-separator="-"  start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" :disabled-date="disabledDate" class="!w-[240px] mr-[10px]" @change="handleRankingDateChange" />
                    <el-button type="primary" @click="getRankingDataFn">筛选</el-button>
                </div>
            </div>

            <el-table :data="rankingList" style="width: 100%">
                <el-table-column label="会员" min-width="200">
                    <template #default="{ row }">
                        <div class="flex items-center">
                            <img class="w-[40px] h-[40px]" v-if="row.headimg" :src="img(row.headimg)" alt="">
                            <img class="w-[40px] h-[40px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                            <div class="ml-[10px]">
                                <div>{{ row.nickname }}</div>
                                <div class="text-[12px] text-[#999]">ID: {{ row.member_id }}</div>
                            </div>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="pay_order_count" label="付款订单数" align="center" />
                <el-table-column prop="pay_goods_count" label="付款商品数" align="center" />
                <el-table-column prop="pay_money" label="付款金额" align="center" />
                <el-table-column prop="refund_goods_count" label="退款商品数" align="center" />
                <el-table-column prop="refund_money" label="退款金额" align="center" />
            </el-table>
            <div class="mt-[20px] flex justify-end">
                <el-pagination v-model:current-page="page" v-model:page-size="limit"  layout="total, prev, pager, next, jumper" :total="total"  @current-change="getRankingDataFn" @size-change="getRankingDataFn" />
            </div>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { img } from '@/utils/common'
import * as echarts from 'echarts'
import { QuestionFilled } from '@element-plus/icons-vue'
import { getMemberStatBasic, getMemberData, getTrendData, getSourceData, getDurationData, getRankingData } from '@/app/lang/zh-cn/api/admin/stat'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const dateType = ref('30days')
const dateRange = ref<string[]>([])
const page = ref(1)
const limit = ref(10)
const total = ref(3)

const memberStat = ref({
    total_member: 0,
    pay_member_count: 0,
    active_member_count: 0,
    silent_member_count: 0
})

const dataOverview = ref<Array<{ label: string, value: number | string }>>([])

const getMemberStatBasicFn = async () => {
    const res = await getMemberStatBasic()
    memberStat.value = res.data
}
getMemberStatBasicFn()

const getMemberDataFn = async () => {
    const res = await getMemberData({
        start_time: dateRange.value[0],
        end_time: dateRange.value[1]
    })
    // Map backend keys to frontend labels
    const mapping: Record<string, string> = {
        new_member_count: '新增会员数',
        new_pay_member_count: '新增付费会员数',
        visit_member_count: '访问会员数',
        add_cart_member_count: '加购会员数',
        order_member_count: '下单会员数',
        pay_member_count: '支付会员数',
        lost_member_count: '流失会员数',
        recharge_member_count: '充值会员数',
        member_unit_price: '会员客单价 (元)'
    }
    dataOverview.value = Object.keys(mapping).map(key => ({
        label: mapping[key],
        value: res.data[key] || 0
    }))
}
getMemberDataFn()

const searchParams = reactive({
    keyword: '',
    sort_field: 'pay_order_count',
    date_range: []
})

const rankingList = ref([
    { member_id: 1273, nickname: '陈陈', headimg: '', order_num: 3, goods_num: 4, order_money: '3.00', refund_goods_num: 0, refund_money: '0.00', recharge_money: '0.00' },
    { member_id: 1272, nickname: '陈田', headimg: '', order_num: 1, goods_num: 1, order_money: '0.00', refund_goods_num: 0, refund_money: '0.00', recharge_money: '0.00' },
    { member_id: 1275, nickname: '-', headimg: '', order_num: 1, goods_num: 1, order_money: '0.00', refund_goods_num: 0, refund_money: '0.00', recharge_money: '0.00' }
])

const shortcuts = [
    {
        text: '昨日',
        value: () => {
            const end = new Date()
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

const memberTrendChart = ref(null)
const memberSourceChart = ref(null)
const customerDurationChart = ref(null)

const getTrendDataFn = async () => {
    const res = await getTrendData({
        start_time: dateRange.value[0],
        end_time: dateRange.value[1]
    })
    if (memberTrendChart.value) {
        const myChart = echarts.getInstanceByDom(memberTrendChart.value) || echarts.init(memberTrendChart.value)
        myChart.setOption({
            xAxis: { data: res.data.date_list },
            series: [
                { name: '新增会员数', data: res.data.new_member_count_list },
                { name: '新增付费会员数', data: res.data.new_pay_member_count_list },
                { name: '访问会员数', data: res.data.visit_member_count_list },
                { name: '加购会员数', data: res.data.add_cart_member_list },
                { name: '下单会员数', data: res.data.order_member_list },
                { name: '支付会员数', data: res.data.pay_member_list },
                { name: '流失会员数', data: res.data.lost_member_count_list },
                { name: '充值会员数', data: res.data.recharge_member_count_list },
                { name: '会员客单价', data: res.data.member_unit_price_list }
            ]
        })
    }
}

const getSourceDataFn = async () => {
    const res = await getSourceData()
    if (memberSourceChart.value) {
        const myChart = echarts.getInstanceByDom(memberSourceChart.value) || echarts.init(memberSourceChart.value)
        myChart.setOption({
            yAxis: { data: res.data.map((item: any) => item.register_channel_name).reverse() },
            series: [{ data: res.data.map((item: any) => item.count).reverse() }]
        })
    }
}

const getDurationDataFn = async () => {
    const res = await getDurationData()
    if (customerDurationChart.value) {
        const myChart = echarts.getInstanceByDom(customerDurationChart.value) || echarts.init(customerDurationChart.value)
        myChart.setOption({ series: [{ data: res.data }] })
    }
}

const getRankingDataFn = async () => {
    const res = await getRankingData({
        ...searchParams,
        page: page.value,
        limit: limit.value,
        start_time: searchParams.date_range ? searchParams.date_range[0] : '',
        end_time: searchParams.date_range ? searchParams.date_range[1] : ''
    })
    rankingList.value = res.data.data
    total.value = res.data.total
}

const exportChart = () => {
    if (memberTrendChart.value) {
        const myChart = echarts.getInstanceByDom(memberTrendChart.value)
        if (myChart) {
            const url = myChart.getDataURL({
                type: 'png',
                pixelRatio: 2,
                backgroundColor: '#fff'
            })
            const link = document.createElement('a')
            link.download = '会员详情趋势图.png'
            link.href = url
            link.click()
        }
    }
}

const initCharts = () => {
    // 会员详情趋势图
    if (memberTrendChart.value) {
        const myChart = echarts.init(memberTrendChart.value)
        const option = {
            tooltip: { trigger: 'axis' },
            legend: {
                data: ['新增会员数', '新增付费会员数', '访问会员数', '加购会员数', '下单会员数', '支付会员数', '流失会员数', '充值会员数', '会员客单价'],
            },
            grid: { left: '3%', right: '4%', bottom: '10%', containLabel: true },
            xAxis: { type: 'category', boundaryGap: false, data: [] },
            yAxis: { type: 'value' },
            series: [
                { name: '新增会员数', type: 'line', smooth: true, data: [] },
                { name: '新增付费会员数', type: 'line', smooth: true, data: [] },
                { name: '访问会员数', type: 'line', smooth: true, data: [] },
                { name: '加购会员数', type: 'line', smooth: true, data: [] },
                { name: '下单会员数', type: 'line', smooth: true, data: [] },
                { name: '支付会员数', type: 'line', smooth: true, data: [] },
                { name: '流失会员数', type: 'line', smooth: true, data: [] },
                { name: '充值会员数', type: 'line', smooth: true, data: [] },
                { name: '会员客单价', type: 'line', smooth: true, data: [] }
            ],
            color: ['#FF6A00', '#2878FF', '#00C050', '#7B68EE', '#FFC107', '#00BCD4', '#E91E63', '#9C27B0', '#FF4081']
        }
        myChart.setOption(option)

        const resizeObserver = new ResizeObserver(() => {
            myChart.resize()
        })
        resizeObserver.observe(memberTrendChart.value)
    }

    // 会员来源图
    if (memberSourceChart.value) {
        const myChart = echarts.init(memberSourceChart.value)
        const option = {
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: {
                type: 'value',
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                splitLine: {
                    show: false
                }
            },
            yAxis: {
                axisTick: {
                    show: false
                },
                axisLine: {
                    show: false
                },
                axisLabel: {
                    color: '#999'
                },
                type: 'category',
                data: []
            },
            series: [{
                name: '数量',
                type: 'bar',
                showBackground: true,
                backgroundStyle: {
                    borderRadius: [0, 10, 10, 0]
                },
                data: [],
                barWidth: 20,
                itemStyle: {
                    color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                        { offset: 0, color: '#4577ff' },
                        { offset: 1, color: '#9cb7ff' }
                    ]),
                    borderRadius: [0, 10, 10, 0]
                },
                label: {
                    show: true, // 开启数值显示
                    position: 'right', // 数值显示在柱子右侧
                    color: '#666', // 数值文字颜色
                    fontSize: 12 // 数值文字大小
                }
            }]
        }
        myChart.setOption(option)

        const resizeObserver = new ResizeObserver(() => {
            myChart.resize()
        })
        resizeObserver.observe(memberSourceChart.value)
    }

    // 客户时长图
    if (customerDurationChart.value) {
        const myChart = echarts.init(customerDurationChart.value)
        const option = {
            tooltip: { trigger: 'item' },
            legend: { orient: 'vertical', right: '10%', top: 'center' },
            series: [{
                name: '客户时长',
                type: 'pie',
                radius: ['50%', '70%'],
                center: ['40%', '50%'],
                avoidLabelOverlap: false,
                itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
                label: { show: false, position: 'center' },
                emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
                labelLine: { show: false },
                data: [],
                color: ['#7B68EE', '#FF6A00', '#00C050', '#2878FF', '#FF4081', '#FFC107']
            }]
        }
        myChart.setOption(option)

        const resizeObserver = new ResizeObserver(() => {
            myChart.resize()
        })
        resizeObserver.observe(customerDurationChart.value)
    }
    // Load initial data
    getTrendDataFn()
    getSourceDataFn()
    getDurationDataFn()
    getRankingDataFn()
}

const overviewLoading = ref(false)
const rankingLoading = ref(false)

const disabledDate = (time: Date) => {
    return time.getTime() > Date.now()
}

const loadOverviewData = () => {
    overviewLoading.value = true
    Promise.all([
        getMemberDataFn(),
        getTrendDataFn()
    ]).finally(() => {
        overviewLoading.value = false
    })
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

const handleRankingDateChange = (val: any) => {
    if (val) {
        const start = new Date(val[0]).getTime()
        const end = new Date(val[1]).getTime()
        const day = (end - start) / (1000 * 60 * 60 * 24)
        if (day > 31) {
            ElMessage.error('日期范围最大选择31天')
            searchParams.date_range = []
            return
        }
    }
    getRankingDataFn()
}

onMounted(() => {
    // Initialize date range
    setDateType('30days')

    // Load other independent data
    getMemberStatBasicFn()
    getSourceDataFn()
    getDurationDataFn()
    getRankingDataFn()

    nextTick(() => {
        initCharts()
    })
})
</script>

<style scoped lang="scss">
.el-row {
    margin-bottom: 20px;
}
.el-row:last-child {
    margin-bottom: 0;
}
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
</style>
