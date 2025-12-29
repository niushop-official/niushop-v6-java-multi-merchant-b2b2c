<template>
  <div class="box-border pb-[30px]">
    <div v-loading="loading">
      <div class="bg-[#fff] p-[20px]">
        <div class="mb-[16px] flex items-center">
          <span class="text-[18px] font-500 mr-[10px]">基本数据</span>
          <span class="nc-iconfont nc-icon-shijianV6xx text-[14px] text-[#666666] mr-[6px]"></span>
          <span class="text-[14px] text-[#666666] leading-[30px]">{{ time }}</span>
        </div>
        <div class="flex items-center">
          <div class="flex-1 box-border mr-[20px]">
            <div class="bg-[#F4F7FC] h-[170px] box-border py-[20px] px-[16px]">
              <div class="flex justify-between items-center mb-[14px]">
                <span class="text-[14px] text-[#333]">今日订单数</span>
                <el-tooltip effect="light" :content="'订单数:' + statInfo.order_stat.yesterday_order_count"
                            placement="bottom">
                  <div class="w-[36px] h-[20px] leading-[20px] text-center text-[#666] border-solid border-[#86909C] border-[1px]  cursor-pointer text-[12px] box-border">昨日</div>
                </el-tooltip>
              </div>
              <div class="text-[24px] font-500 text-[#111] mb-[12px] leading-[30px]">{{ statInfo.order_stat.today_order_count }}</div>
              <div class="flex items-center leading-[20px] mb-[16px] text-[#999]">
                <span class="text-[14px] mr-[10px]">环比</span>
                <span class="nc-iconfont nc-icon-shang !text-[14px] text-[#F53F3F] mr-[10px]" v-if="calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count) >= 0"></span>
                <span class="nc-iconfont nc-icon-xia !text-[14px] text-[#00b42a] mr-[10px]" v-if="calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count) < 0"></span>
                <span class="text-[14px] mr-[20px]">{{ Math.abs(calculate(statInfo.order_stat.today_order_count, statInfo.order_stat.yesterday_order_count)) }}%</span>
                <span class="text-[14px]">昨日：</span>
                <span class="text-[16px]">{{ statInfo.order_stat.yesterday_order_count }}</span>
              </div>
              <div class="flex items-center justify-between leading-[20px]">
                <span class="text-[14px] text-[#666]">订单总数</span>
                <span class="text-[16px] font-500 text-[#111]">{{ statInfo.order_stat.total_order_count }}</span>
              </div>
            </div>
          </div>
          <div class="flex-1 box-border mr-[20px]">
            <div class="bg-[#F4F7FC] h-[170px] box-border py-[20px] px-[16px]">
              <div class="flex justify-between items-center mb-[14px]">
                <span class="text-[14px] text-[#333]">今日销售额</span>
                <el-tooltip effect="light" :content="'销售额:' + statInfo.sale_stat.yesterday_sale_money" placement="bottom">
                  <div class="w-[36px] h-[20px] leading-[20px] text-center text-[#666] border-solid border-[#86909C] border-[1px]  cursor-pointer text-[12px] box-border">昨日</div>
                </el-tooltip>
              </div>
              <div class="text-[24px] font-500 text-[#111] mb-[12px] leading-[30px]">￥{{ statInfo.sale_stat.today_sale_money }}</div>
              <div class="flex items-center leading-[20px] mb-[16px] text-[#999]">
                <span class="text-[14px] mr-[10px]">环比</span>
                <span class="nc-iconfont nc-icon-shang !text-[14px] text-[#F53F3F] mr-[10px]" v-if="calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money) >= 0"></span>
                <span class="nc-iconfont nc-icon-xia !text-[14px] text-[#00b42a] mr-[10px]" v-if="calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money) < 0"></span>
                <span class="text-[14px] mr-[20px]">{{ Math.abs(calculate(statInfo.sale_stat.today_sale_money, statInfo.sale_stat.yesterday_sale_money)) }}%</span>
                <span class="text-[14px]">昨日：</span>
                <span class="text-[16px]">{{ statInfo.sale_stat.yesterday_sale_money }}</span>
              </div>
              <div class="flex items-center justify-between leading-[20px]">
                <span class="text-[14px] text-[#666]">销售总额(元)</span>
                <span class="text-[16px] font-500 text-[#111]">{{ statInfo.sale_stat.total_sale_money }}</span>
              </div>
            </div>
          </div>
          <div class="flex-1 box-border mr-[20px]">
            <div class="bg-[#F4F7FC] h-[170px] box-border py-[20px] px-[16px]" @click="toLink('/member/member')">
              <div class="flex justify-between items-center mb-[14px]">
                <span class="text-[14px] text-[#333]">今日新增会员数</span>
                <el-tooltip effect="light" :content="'会员数:' + statInfo.member_stat.yesterday_member_count" placement="bottom">
                  <div class="w-[36px] h-[20px] leading-[20px] text-center text-[#666] border-solid border-[#86909C] border-[1px]  cursor-pointer text-[12px] box-border">昨日</div>
                </el-tooltip>
              </div>
              <div class="text-[24px] font-500 text-[#111] mb-[12px] leading-[30px]">{{ statInfo.member_stat.today_member_count }}</div>
              <div class="flex items-center leading-[20px] mb-[16px] text-[#999]">
                <span class="text-[14px] mr-[10px]">环比</span>
                <span class="nc-iconfont nc-icon-shang !text-[14px] text-[#F53F3F] mr-[10px]" v-if="calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count) >= 0"></span>
                <span class="nc-iconfont nc-icon-xia !text-[14px] text-[#00b42a] mr-[10px]" v-if="calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count) < 0"></span>
                <span class="text-[14px] mr-[20px]">{{ Math.abs(calculate(statInfo.member_stat.today_member_count, statInfo.member_stat.yesterday_member_count)) }}%</span>
                <span class="text-[14px]">昨日：</span>
                <span class="text-[16px]">{{ statInfo.member_stat.yesterday_member_count }}</span>
              </div>

              <div class="flex items-center justify-between leading-[20px]">
                <span class="text-[14px] text-[#666]">会员总数</span>
                <span class="text-[16px] font-500 text-[#111]">{{ statInfo.member_stat.total_member_count }}</span>
              </div>
            </div>
          </div>
          <div class="flex-1 box-border mr-[20px]">
            <div class="bg-[#F4F7FC] h-[170px] box-border py-[20px] px-[16px] cursor-pointer" @click="toLink('/shop/list')">
              <div class="flex justify-between items-center mb-[14px]">
                <span class="text-[14px] text-[#333]">今日申请店铺数</span>
                <el-tooltip effect="light" :content="'申请店铺数:' + statInfo.site_stat.yesterday_site_count" placement="bottom">
                  <div class="w-[36px] h-[20px] leading-[20px] text-center text-[#666] border-solid border-[#86909C] border-[1px]  cursor-pointer text-[12px] box-border">昨日</div>
                </el-tooltip>
              </div>
              <div class="text-[24px] font-500 text-[#111] mb-[12px] leading-[30px]">{{ statInfo.site_stat.today_site_count }}</div>
              <div class="flex items-center leading-[20px] mb-[16px] text-[#999]">
                <span class="text-[14px] mr-[10px]">环比</span>
                <span class="nc-iconfont nc-icon-shang !text-[14px] text-[#F53F3F] mr-[10px]" v-if="calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count) >= 0"></span>
                <span class="nc-iconfont nc-icon-xia !text-[14px] text-[#00b42a] mr-[10px]" v-if="calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count) < 0"></span>
                <span class="text-[14px] mr-[20px]">{{ Math.abs(calculate(statInfo.site_stat.today_site_count, statInfo.site_stat.yesterday_site_count)) }}%</span>
                <span class="text-[14px]">昨日：</span>
                <span class="text-[16px]">{{ statInfo.site_stat.yesterday_site_count }}</span>
              </div>

              <div class="flex items-center justify-between leading-[20px]">
                <span class="text-[14px] text-[#666]">店铺总数</span>
                <span class="text-[16px] font-500 text-[#111]">{{ statInfo.site_stat.total_site_count }}</span>
              </div>
            </div>
          </div>
          <div class="flex-1 box-border">
            <div class="bg-[#F4F7FC] h-[170px] box-border py-[20px] px-[16px]">
              <div class="flex justify-between items-center mb-[14px]">
                <span class="text-[14px] text-[#333]">今日退款额</span>
                <el-tooltip effect="light" :content="'今日退款额:' + statInfo.refund_stat.yesterday_refund_money" placement="bottom">
                  <div class="w-[36px] h-[20px] leading-[20px] text-center text-[#666] border-solid border-[#86909C] border-[1px]  cursor-pointer text-[12px] box-border">昨日</div>
                </el-tooltip>
              </div>
              <div class="text-[24px] font-500 text-[#111] mb-[12px] leading-[30px]">{{ statInfo.refund_stat.today_refund_money }}</div>
              <div class="flex items-center leading-[20px] mb-[16px] text-[#999]">
                <span class="text-[14px] mr-[10px]">环比</span>
                <span class="nc-iconfont nc-icon-shang !text-[14px] text-[#F53F3F] mr-[10px]" v-if="calculate(statInfo.refund_stat.today_refund_money, statInfo.refund_stat.yesterday_refund_money) >= 0"></span>
                <span class="nc-iconfont nc-icon-xia !text-[14px] text-[#00b42a] mr-[10px]" v-if="calculate(statInfo.refund_stat.today_refund_money, statInfo.refund_stat.yesterday_refund_money) < 0"></span>
                <span class="text-[14px] mr-[20px]">{{ Math.abs(calculate(statInfo.refund_stat.today_refund_money, statInfo.refund_stat.yesterday_refund_money)) }}%</span>
                <span class="text-[14px]">昨日：</span>
                <span class="text-[16px]">{{ statInfo.refund_stat.yesterday_refund_money }}</span>
              </div>
              <div class="flex items-center justify-between leading-[20px]">
                <span class="text-[14px] text-[#666]">订单总数</span>
                <span class="text-[16px] font-500 text-[#111]">{{ statInfo.refund_stat.total_refund_money }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <el-row :gutter="20" class="mt-[20px]">
        <el-col :span="12">
          <div class="p-[20px] bg-[#fff]">
            <div class="flex justify-between items-center mb-[16px]">
              <span class="text-[#333] text-[18px] font-500">商城订单</span>
              <div class="text-[#999] cursor-pointer flex items-center">
                <span  class="text-[14px] mr-[4px]">全部</span>
                <span class=" iconfont iconxiangyoujiantou !text-[14px]"></span>
              </div>
            </div>
            <div class="h-[96px] flex justify-between items-center">
              <div class="flex-1 flex justify-center items-center ">
                <div class="nc-iconfont nc-icon-yinhangka2 !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#F53F3F] mb-[4px] leading-[26px] text-center">{{ statInfo.my_order_stat.wait_pay_order }}</div>
                  <div class="text-[14px] text-[#333]">待付款</div>
                </div>
              </div>
              <div class="flex-1 flex justify-center items-center">
                <div class="nc-iconfont nc-icon-a-Diskcipan !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#165DFF] mb-[4px] leading-[26px] text-center">{{ statInfo.my_order_stat.wait_delivery_order }}</div>
                  <div class="text-[14px] text-[#333]">待发货</div>
                </div>
              </div>
              <div class="flex-1 flex justify-center items-center">
                <div class="nc-iconfont nc-icon-a-Deliveryjiaohuo !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#165DFF] mb-[4px] leading-[26px] text-center">{{ statInfo.my_order_stat.wait_take_order }}</div>
                  <div class="text-[14px] text-[#333]">已发货</div>
                </div>
              </div>
              <div class="flex-1 flex justify-center items-center">
                <div class="nc-iconfont nc-icon-a-Historylishijilu !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#F53F3F] mb-[4px] leading-[26px] text-center">{{ statInfo.my_order_stat.refund_order }}</div>
                  <div class="text-[14px] text-[#333]">退款中</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="p-[20px] bg-[#fff]">
            <div class="flex justify-between items-center mb-[16px]">
              <span class="text-[#333] text-[18px] font-500">提现</span>
              <div class="text-[#999] cursor-pointer flex items-center" @click="toLink('/finance/cash_out')">
                <span class="text-[14px] mr-[4px]">全部</span>
                <span class=" iconfont iconxiangyoujiantou !text-[14px]"></span>
              </div>
            </div>
            <div class="h-[96px] flex justify-between items-center">
              <div class="flex-1 flex justify-center items-center cursor-pointer" @click="toLink('/finance/cash_out')">
                <div class="nc-iconfont nc-icon-huiyuantixian !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#165DFF] mb-[11px] leading-[26px] text-center">{{ statInfo.cash_out_stat.member_count }}
                  </div>
                  <div class="text-[14px] text-[#333]">会员提现</div>
                </div>
              </div>
              <div class="flex-1 flex justify-center items-center cursor-pointer" @click="toLink('/finance/shop_cash_out')">
                <div class="nc-iconfont nc-icon-a-Financing-tworongzi2 !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#165DFF] mb-[4px] leading-[26px] text-center">{{ statInfo.cash_out_stat.site_count }}
                  </div>
                  <div class="text-[14px] text-[#333]">店铺提现</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="p-[20px] bg-[#fff]">
            <div class="flex justify-between items-center mb-[16px]">
              <span class="text-[#333] text-[18px] font-500">商品</span>
              <div class="text-[#999] cursor-pointer flex items-center">
                <span class="text-[14px] mr-[4px]">全部</span>
                <span class=" iconfont iconxiangyoujiantou !text-[14px]"></span>
              </div>
            </div>
            <div class="h-[96px] flex justify-between items-center ">
              <div class="flex-1 flex justify-center items-center">
                <div class="nc-iconfont nc-icon-a-Weixin-marketweixindianshang !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#165DFF] mb-[4px] leading-[26px] text-center">{{ statInfo.goods_stat.sale_goods_num }}</div>
                  <div class="text-[14px] text-[#333]">销售中</div>
                </div>
              </div>
              <div class="flex-1 flex justify-center items-center">
                <div class="nc-iconfont nc-icon-a-Repairbianji !text-[36px] text-[#111]"></div>
                <div class="ml-[14px]">
                  <div class="text-[20px] font-500 text-[#F53F3F] mb-[4px] leading-[26px] text-center">{{ statInfo.goods_stat.audit_goods_num }}</div>
                  <div class="text-[14px] text-[#333]">待审核</div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mt-[20px]">
        <el-col :span="10">
          <div class="p-[20px] box-border bg-[var(--el-fill-color-blank)]">
            <div class="text-[18px] text-[#333] font-500  mb-[20px]">近7天订单数</div>
            <div ref="orderStat" :style="{ width: '100%', height: '310px' }"></div>
          </div>
        </el-col>
        <el-col :span="10">
          <div class="p-[20px] box-border bg-[var(--el-fill-color-blank)]">
            <div class="text-[18px] text-[#333] font-500  mb-[20px]">销售额(元)</div>
            <div ref="saleStat" :style="{ width: '100%', height: '310px' }"></div>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="p-[20px] box-border bg-[var(--el-fill-color-blank)] h-full">
            <div class="font-500 leading-[26px] text-[#333] text-[18px] mb-[16px]">最新动态</div>
            <div v-if="statNews.length">
              <template v-for="(  item, index  ) in statNews  " :key="index">
                <div class="flex items-center justify-between py-[15px] border-solid border-b-[1px] border-[#F9F9F9] leading-[20px] cursor-pointer" @click="toDetail('/web/article/detail', item.id)">
                  <div>
                    <i class="iconfont icon31huidaodingbu !text-[12px] text-[#E11111]" v-if="!index"></i>
                    <span class="text-[14px] text-[#666]" v-else :class="{'!text-[#FF1010]': index == 1, '!text-[#FF9415]': index == 2, '!text-[#EB6464]': index == 3}">{{ index }}</span>
                  </div>
                  <span class="truncate flex-1 ml-[8px] text-[#333] text-[14px]">{{ item.title }}</span>
                  <span class="text-[14px]  text-[#999] ml-[20px]">{{ item.create_time.split(' ')[0] }}</span>
                </div>
              </template>
            </div>
            <div v-else class="flex flex-col items-center justify-center pb-[15px]">
              <el-image class="w-[100px] h-[100px]" :src="img('static/resource/images/empty.png')" fit="contain"></el-image>
              <div class="text-[14px] text-[#999]">暂无动态</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common';
import { useClipboard } from '@vueuse/core'
import { getStatInfo, getStatChart, getStatNewsList } from '@/app/api/stat'
import { getService } from '@/app/api/sys'
import * as echarts from 'echarts'
import useStyleStore from '@/stores/modules/style'
import { getFrameworkNewVersion } from '@/app/api/module'
import { useRoute, useRouter } from 'vue-router'
import { AnyObject } from '@/types/global'
import { ElMessage } from 'element-plus'

const loading = ref(true)
const orderStat = ref<any>(null)
const saleStat = ref<any>(null)

const styleStore = useStyleStore()

interface NewVersion {
    last_version: string
}
interface StatInfo {
    order_stat: AnyObject,
    sale_stat: AnyObject,
    member_stat: AnyObject,
	cash_out_stat: AnyObject,
    site_stat: AnyObject,
    refund_stat: AnyObject,
    my_order_stat: AnyObject,
    approval_stat: AnyObject,
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
    order_stat: {},
    sale_stat: {},
    member_stat: {},
    cash_out_stat: {},
    site_stat: {},
    refund_stat: {},
    my_order_stat: {},
    approval_stat: {},
    goods_stat: {}
})

const phone = ref('400-886-7993')
const getStatInfoFn = async (id: number = 0) => {
    statInfo.value = await (await getStatInfo()).data
    const serviceData = await (await getService()).data
    phone.value = serviceData.tel || '400-886-7993'
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

const statNews = ref({
    title: '',
    create_time: ''
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
        //     text: '销售额（元）'
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

const toDetail = (url: any, id: any) => {
    window.open(location.origin + url + '?id=' + id)
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
.refund-num {
	background-image: url('@/app/assets/images/index/index05.png');
	background-size: 100% 100%;
}
.customer-service{
	background-image: url('@/app/assets/images/index/index06.png');
	background-size: 100% 100%;
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
