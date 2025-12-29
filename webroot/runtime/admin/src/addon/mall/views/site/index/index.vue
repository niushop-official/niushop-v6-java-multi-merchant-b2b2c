<template>
	<div class="main-container m-[10px] flex" v-loading="loading">
		<div class="flex-1">
			<!-- 实时概况 -->
			<el-row :gutter="20">
				<el-col :span="6" >
					<div class="store h-[100px] box-border px-[25px] py-[25px] flex cursor-pointer" @click="toLink('/shop/setting')">
						<el-image :src="img(info.front_end_logo)" fit="contain" class="w-[50px] h-[50px] rounded-full border-[1px] border-[#fff] border-solid">
							<template #error>
								<img src="@/app/assets/images/icon-addon.png" class="w-[50px] h-[50px]">
							</template>
						</el-image>
						<div class="ml-[12px]">
							<div class="mb-[12px] flex items-center">
								<span class="text-[16px] font-bold text-[#fff] leading-[20px] max-w-[160px] truncate">{{siteInfo.site_name}}</span>
								<span class="ml-[4px] rounded-[2px] bg-[#fff] px-[4px] text-[#2878FF] text-[12px] leading-[18px] h-[18px]">{{siteInfo.is_self ? t('selfShop') : t('notSelfShop') }}</span>
							</div>
							<div class="text-[12px] text-[#fff] leading-[18px]" v-if="siteInfo.category_name && siteInfo.category_name.category_name">{{t('storeCategory')}}{{siteInfo.category_name.category_name}}</div>
						</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="goods h-[100px] px-[20px] py-[17px] cursor-pointer" @click="toLink('/mall/goods/list')">
						<div class="text-[16px] text-[#fff] leading-[20px] mb-[6px]">{{ t('goodsNum') }}</div>
						<div class="text-[28px] font-bold text-[#fff]">{{ shopStat.goods_num }}</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="order h-[100px] px-[20px] py-[17px] cursor-pointer" @click="toLink('/mall/order/index')">
						<div class="text-[16px] text-[#fff] leading-[20px] mb-[6px]">{{ t('orderNum') }}</div>
						<div class="text-[28px] font-bold text-[#fff]">{{ shopStat.order_num }}</div>
					</div>
				</el-col>
				<el-col :span="6">
					<div class="addon h-[100px] px-[20px] py-[17px] cursor-pointer" @click="toLink('/app/index')">
						<div class="text-[16px] text-[#fff] leading-[20px] mb-[6px]">{{ t('addonNum') }}</div>
						<div class="text-[28px] font-bold text-[#fff]">{{ shopStat.addon_num }}</div>
					</div>
				</el-col>
			</el-row>
			<!-- 代办事项 -->
			<div class="mt-[20px]">
				<el-row :gutter="20">
					<el-col :span="8">
						<div class="bg-[#fff] rounded-[12px] px-[20px] pt-[20px] pb-[28px]">
							<div class="text-[18px] mb-[30px] leading-[22px]">{{ t('waitMatter') }}</div>
							<div class="flex flex-wrap">
								<div class="w-[33.3%] flex flex-col justify-center  pl-[30px] cursor-pointer mb-[38px]"  @click="toHref('/mall/order/index', 1)">
									<span class="text-[14px] text-[#333] mb-[10px]">{{ t('waitPay') }}</span>
									<span class="text-[24px] font-bold leading-[32px]">{{ statTodo.wait_pay || 0 }}</span>
								</div>
								<div class="w-[33.3%] flex flex-col justify-center pl-[30px] cursor-pointer mb-[38px]" @click="toHref('/mall/order/index', 2)">
									<span class="text-[14px] text-[#333] mb-[10px]">{{ t('waitDelivery') }}</span>
									<span class="text-[24px] font-bold leading-[32px]">{{ statTodo.wait_delivery || 0 }}</span>
								</div>
								<div class="w-[33.3%] flex flex-col justify-center pl-[30px] cursor-pointer mb-[38px]" @click="toLink('/mall/order/refund')">
									<span class="text-[14px] text-[#333] mb-[10px]">{{ t('waitRefund') }}</span>
									<span class="text-[24px] font-bold leading-[32px]">{{ statTodo.wait_refund || 0 }}</span>
								</div>
								<div class="w-[33.3%] flex flex-col justify-center pl-[30px] cursor-pointer" @click="toHref('/mall/order/index', 3)">
									<span class="text-[14px] text-[#333] mb-[10px]">{{ t('waitVerify') }}</span>
									<span class="text-[24px] font-bold leading-[32px]">{{ statTodo.wait_verify || 0 }}</span>
								</div>
								<div class="w-[33.3%] flex flex-col justify-center pl-[30px] cursor-pointer" @click="toHref('/mall/order/index', 3)">
									<span class="text-[14px] text-[#333] mb-[10px]">{{ t('waitTake') }}</span>
									<span class="text-[24px] font-bold leading-[32px]">{{ statTodo.wait_take || 0 }}</span>
								</div>
								<div class="w-[33.3%] flex flex-col justify-center pl-[30px] cursor-pointer" @click="toLink('/account/cash_out')">
									<span class="text-[14px] text-[#333] mb-[10px]">{{ t('waitTransfer') }}</span>
									<span class="text-[24px] font-bold leading-[32px]">{{ statTodo.wait_transfer || 0 }}</span>
								</div>
							</div>
						</div>
					</el-col>
					<el-col :span="16">
						<div class="bg-[#fff] rounded-[12px] px-[20px] pt-[20px] pb-[28px]">
							<div class="flex justify-between items-center mb-[30px]">
								<div class="text-[18px] leading-[22px]">{{ t('dataStatistics') }}</div>
								<div class="bg-[#F6F6F8] rounded-[16px] flex items-center">
									<span class="w-[54px] h-[26px] leading-[26px] rounded-[16px] text-center text-[12px] cursor-pointer" :class="{'bg-[#2878FF] text-[#fff]': totalActive == item.date}" v-for="(item,index) in dateList" :key="index" @click="handleTotal(item.date)"> {{  item.label }}</span>
								</div>
							</div>
							<div class="flex flex-wrap">
								<div class="w-[25%] flex pl-[9px]  mb-[34px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-xiaoshoue text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('saleMoney') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.sale_money || '0.00' }}</span>
									</div>
								</div>
								<div class="w-[25%] flex pl-[9px]  mb-[34px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-tuikuan text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('refundMoney') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.refund_money || '0.00' }}</span>
									</div>
								</div>
								<div class="w-[25%] flex pl-[9px]  mb-[34px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-dingdanliang text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('orderNumber') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.order_num || 0 }}</span>
									</div>
								</div>
								<div class="w-[25%] flex pl-[9px] mb-[34px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-tuikuanjianshu text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('refundNum') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.refund_num || 0 }}</span>
									</div>
								</div>

								<div class="w-[25%] flex pl-[9px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-jiagoushuliang text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('cartNum') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.cart_num || 0 }}</span>
									</div>
								</div>
								<div class="w-[25%] flex pl-[9px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-xiadanshu text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('saleNum') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.sale_num || 0 }}</span>
									</div>
								</div>
								<div class="w-[25%] flex pl-[9px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-zhifujianshu1 text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('payNum') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.pay_num || 0 }}</span>
									</div>
								</div>
								<div class="w-[25%] flex pl-[9px]">
									<div class="bg-[#F0F8FF] rounded-[12px] w-[30px] h-[30px] flex items-center justify-center mr-[10px]"><i class="nc-iconfont nc-icon-shangpinliulanliang text-[#2878FF]"></i></div>
									<div class="flex flex-col justify-center">
										<span class="text-[14px] text-[#333] mb-[10px]">{{ t('accessNum') }}</span>
										<span class="text-[24px] font-bold leading-[32px]">{{ statTotal.access_num || 0 }}</span>
									</div>
								</div>
							</div>
						</div>
					</el-col>
				</el-row>
			</div>
			<!-- 订单趋势 -->
			<div class="mt-[20px]">
				<el-row :gutter="20">
					<el-col :span="12">
						<div class="bg-[#fff] rounded-[12px]">
							<div class="p-[20px]  flex justify-between items-center">
								<div class="text-[18px] leading-[22px]">{{ t('tradingOverview') }}</div>
								<div class="bg-[#F6F6F8] rounded-[16px] flex items-center">
									<span class="w-[54px] h-[26px] leading-[26px] rounded-[16px] text-center text-[12px] cursor-pointer" :class="{'bg-[#2878FF] text-[#fff]': statActive == '7days' }" @click="handleStat('7days')">{{ t('sevenDays') }}</span>
									<span class="w-[54px] h-[26px] leading-[26px] rounded-[16px] text-center text-[12px] cursor-pointer" :class="{'bg-[#2878FF] text-[#fff]': statActive == '30days' }" @click="handleStat('30days')">{{ t('thirtyDays') }}</span>
								</div>
							</div>
							<div ref="hourStat"  :style="{ width: '100%', height: '395px' }"></div>
						</div>
					</el-col>
					<el-col :span="12">
						<div class="bg-[#fff] rounded-[12px]">
							<div class="p-[20px] flex justify-between items-center">
								<div class="text-[18px] leading-[22px]">{{ t('goodsRank') }}</div>
								<div class="bg-[#F6F6F8] rounded-[16px] flex items-center">
									<span class="w-[54px] h-[26px] leading-[26px] rounded-[16px] text-center text-[12px] cursor-pointer" :class="{'bg-[#2878FF] text-[#fff]': rankActive == item.date }" v-for="(item,index) in dateList" :key="index" @click="handleRank(item.date)"> {{  item.label }}</span>
								</div>
							</div>
							<div class="p-[20px] min-h-[395px]">
								<el-table :data="statRank.data" style="width: 100%" @sort-change="sortChange">
									<el-table-column  :label="t('ranking')" min-width="80">
										<template #default="scope">
											<img src="@/addon/mall/assets/index/one.png" class="w-[18px] h-[20px]" v-if="scope.$index + 1 == 1">
											<img src="@/addon/mall/assets/index/two.png" class="w-[18px] h-[20px]" v-else-if="scope.$index + 1 == 2">
											<img src="@/addon/mall/assets/index/three.png" class="w-[18px] h-[20px]" v-else-if="scope.$index + 1 == 3">
											<div class="w-[20px] h-[20px] rounded-full text-center leading-[20px] bg-[#EAEAEA] text-[12px] text-[#666]" v-else>{{ scope.$index + 1 }}</div>
										</template>
									</el-table-column>
									<el-table-column  :label="t('goodsInfo')" min-width="180">
										<template #default="{ row }">
											<div class="flex items-center cursor-pointer">
												<div class="min-w-[40px] h-[40px] flex items-center">
													<el-image  class="w-[40px] h-[40px]" :src="img(row.goods.goods_cover)" fit="contain">
														<template #error>
															<div class="image-slot">
																<img class="w-[40px] h-[40px]" src="@/addon/mall/assets/goods_default.png" />
															</div>
														</template>
													</el-image>
												</div>
												<div class="ml-2 flex flex-col items-start">
													<span :title="row.goods.goods_name" class="multi-hidden">{{ row.goods.goods_name }}</span>
												</div>
											</div>
										</template>
									</el-table-column>
									<el-table-column prop="sale_num" :label="t('saleNumber')" min-width="100"  sortable="custom"/>
									<el-table-column prop="pay_money" :label="t('saleAllMoney')" min-width="100" align="center" sortable="custom" />
								</el-table>
							</div>
						</div>
					</el-col>
				</el-row>
			</div>
		</div>
		<div class="w-[310px] ml-[16px] index-other-temp" :class="{'slide-fade-enter-active': !showDlog}">
			<div class="other-temp-item" v-if="serviceInfo.tel">
				<div class="head">
					<span class="head-left">联系客服</span>
				</div>
				<div class="content">
					<div class="content-item">
						<span class="nc-iconfont nc-icon-kefuV6xx1 text-[#2878FF]"></span>
						<span class="font-500 text-[16px] ml-[10px]">{{ serviceInfo.tel }}</span>
					</div>
				</div>
			</div>

			<div class="other-temp-item min-h-[264px]" v-if="articleList && articleList.length">
				<div class="head">
					<span class="head-left">公告通知</span>
					<span class="head-right" @click="articleCheckMore">更多</span>
				</div>

				<div class="content">
					<div class="content-item  flex justify-between text-[14px] py-[8px] text-[#666] leading-[1] " v-for="(item, index) in articleList" :key="index" >
						<span class="truncate w-[70%] cursor-pointer hover-item" @click="articleDetail(item.id)">{{ item.title }}</span>
						<span v-if="item.create_time">{{ item.create_time.split(" ")[0] }}</span>
					</div>
				</div>
			</div>
			<div class="other-temp-item !pb-[12]" v-if="suggestionList && suggestionList.length">
				<div class="head">
					<span class="head-left">使用指南</span>
				</div>
				<div class="content">
					<div class="content-item py-[8px] text-[#666] text-[14px] flex items-center leading-[1] justify-between" v-for="(item,index) in suggestionList" :key="index">
						<span class="truncate w-[70%]">{{ item.title }}</span>
						<button class="text-[var(--el-color-primary)] cursor-pointer" :class="{'!text-[#999]': item.is_complete}" @click="toLinkBlank('/'+item.router_path)" >{{ item.button_name }}</button>
					</div>
				</div>
			</div>

			<div class="other-temp-item min-h-[215px]" v-if="addonList && addonList.length">
				<div class="head">
					<span class="head-left">优选应用</span>
					<span class="head-right"  @click="toLinkBlank('/app/index')">更多</span>
				</div>
				<div class="content flex flex-wrap">
					<div class="content-item addon-item" v-for="(item, index) in addonList" :key="index" @click="toLinkBlank('/'+item.router_path)">
						<img class="img" :src="img(item.icon)" fit="contain" />
						<div class="text">{{ item.title }}</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getShopStatSale, getShopInfo, getShopStatTodo, getShopStatTotal, getShopStatRank, getShopStat } from '@/addon/mall/api/site/shop'
import { getSuggestionList, getAddonList, getArticleList } from '@/addon/mall/api/site/stat'
import * as echarts from 'echarts'
import userStore from '@/stores/modules/user'
import { getService } from '@/app/api/sys'
import { useRouter } from 'vue-router'
const info = userStore().siteInfo
const siteInfo = ref<any>({})
const loading = ref(true)
const showDlog = ref(true)
const hourStat = ref<any>(null)
const router = useRouter()

const shopStat = ref<any>({})
const statTodo = ref<any>({}) // 代办事项
const statTotal = ref<any>({}) // 数据统计
const statCount = ref<any>([]) // 统计图
const statRank = reactive<any>({
    data: [],
    searchParam: {
        order: '',
        sort: ''
    }
}) // 排行榜
const totalActive = ref('all')
const rankActive = ref('all')
const statActive = ref('7days')

const getShopInfoFn = () => {
    loading.value = true
    getShopInfo().then(res => {
        siteInfo.value = res.data
        loading.value = false
    })
}
getShopInfoFn()
const getStatInfoFn = async () => {
    shopStat.value = await (await getShopStat()).data
    statTodo.value = await (await getShopStatTodo()).data
    statTotal.value = await (await getShopStatTotal({ date: totalActive.value })).data
    statCount.value = await (await getShopStatSale({ date: statActive.value })).data
    statRank.data = await (await getShopStatRank({ date: rankActive.value, ...statRank.searchParam })).data
    setTimeout(() => {
        drawChartTo()
    }, 20)
}
getStatInfoFn()

// 获取资讯列表
const articleList = ref([])
const getArticleListFn = () => {
    getArticleList({ limit: 6 }).then(res => {
        articleList.value = res.data
    })
}
getArticleListFn()

// 获取应用列表
const addonList = ref([])
const getAddonListFn = () => {
    getAddonList({ limit: 6 }).then(res => {
        addonList.value = res.data
    })
}
getAddonListFn()

// 获取优化建议列表
const suggestionList = ref([])
const getSuggestionListFn = () => {
    getSuggestionList({ limit: 6 }).then(res => {
        suggestionList.value = res.data
    })
}
getSuggestionListFn()

const serviceInfo = ref<any>({})
const getServiceFn = () => {
    getService().then(res => {
        serviceInfo.value = res.data
    })
}
getServiceFn()

const drawChartTo = () => {
    const goodsData = reactive<any>([]) // 商品数据
    const goodsLegend = reactive<any>([]) // 商品图例

    for (const key in statCount.value) {
        if (key == 'order_num') {
            goodsData.push({
                name: '订单数量',
                type: 'line',
                smooth: true,
                data: statCount.value[key]
            })
            goodsLegend.push('订单数量')
        } else if (key == 'refund_money') {
            goodsData.push({
                name: '退款金额',
                type: 'line',
                smooth: true,
                data: statCount.value[key]
            })
            goodsLegend.push('退款金额')
        } else if (key == 'sale_money') {
            goodsData.push({
                name: '交易金额',
                type: 'line',
                smooth: true,
                data: statCount.value[key]
            })
            goodsLegend.push('交易金额')
        }
    }
    if (!hourStat.value) return
    const hourStatChart = echarts.init(hourStat.value)
    const hourStatOption = ref({
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        color: ['#1890ff', '#00c050', '#F76A6C'],
        legend: {
            data: []
        },
        xAxis: {
            type: 'category',
            axisLabel: {
                interval: function (index: any) {
                    if (statCount.value.time.length > 10) {
                        return index % 3 === 0
                    } else {
                        return true
                    }
                }
            },
            data: statCount.value.time
        },
        yAxis: {
            type: 'value'
        },

        series: []
    })
    hourStatOption.value.series = goodsData
    hourStatOption.value.legend.data = goodsLegend
    hourStatChart.setOption(hourStatOption.value)
}

const dateList = ref([
    {
        label: '全部',
        date: 'all'
    },
    {
        label: '今日',
        date: 'today'
    },
    {
        label: '7天',
        date: '7days'
    },
    {
        label: '30天',
        date: '30days'
    }
])
// 切换时间
const handleTotal = (data: any) => {
    totalActive.value = data
    getShopStatTotal({ date: totalActive.value }).then(res => {
        statTotal.value = res.data
    })
}
const handleRank = (data: any) => {
    rankActive.value = data
    getShopStatRank({ date: rankActive.value, ...statRank.searchParam }).then(res => {
        statRank.data = res.data
    })
}

const handleStat = (data: any) => {
    statActive.value = data
    getShopStatSale({ date: statActive.value }).then(res => {
        statCount.value = res.data
        setTimeout(() => {
            drawChartTo()
        }, 20)
    })
}

// 监听排序
const sortChange = (event: any) => {
    let sort = ''
    if (event.order == 'ascending') {
        sort = 'asc'
    } else if (event.order == 'descending') {
        sort = 'desc'
    }
    if (sort) {
        statRank.searchParam.order = event.prop
        statRank.searchParam.sort = sort
    }
    handleRank(rankActive.value)
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
/**
 * 链接跳转 - 新窗口
 */
const toLinkBlank = (link: any) => {
    const url = router.resolve({
        path: link
    })
    window.open(url.href, '_blank')
}

const articleCheckMore = () => {
    window.open('/web/article/list', '_blank')
}
const articleDetail = (id: any) => {
    window.open(`/web/article/detail?id=${id}`, '_blank')
}
</script>

<style lang="scss" scoped>
.store{
    background-image: url('@/addon/mall/assets/index/store_bg.png');
	background-size: 100% 100%;
}
.goods{
	background-image: url('@/addon/mall/assets/index/goods.png');
	background-size: 100% 100%;
}
.order{
	background-image: url('@/addon/mall/assets/index/order.png');
	background-size: 100% 100%;
}
.addon{
	background-image: url('@/addon/mall/assets/index/addon.png');
	background-size: 100% 100%;
}
/* 右收起动画 */
.slide-fade-enter-active {
	width: 0px !important;
	overflow: hidden;
    transition: all 0.3s ease-out;
}

.index-other-temp{
	--mt-spacing: 16px;
	--hover-bg: #f5f5f5;
}
.index-other-temp{
	.other-temp-item{
		@apply py-[20px] px-[12px] bg-[#fff] rounded-[12px] mb-[18px];
		.head{
			@apply flex items-center justify-between mb-[12px] px-[12px];
			.head-left{
				@apply text-[17px] text-[#333];
			}
			.head-right{
				@apply text-[14px] text-[#999] hover:text-[#333] cursor-pointer;
			}
		}
		.content{
			.content-item{
				@apply px-[12px];
			}
			.hover-item{
				@apply hover:text-[var(--el-color-primary)] cursor-pointer;
				// @apply hover:bg-[#f5f5f5] cursor-pointer;
			}
		}
	}
	.addon-item{
		@apply py-[12px] w-[33.3%] hover:bg-[#f5f5f5] cursor-pointer;
		.img{
			@apply w-[36px] h-[36px] m-[auto] rounded-[3px];
		}
		.text{
			@apply mt-[8px] text-[14px] truncate text-center;
		}
	}
}
</style>
