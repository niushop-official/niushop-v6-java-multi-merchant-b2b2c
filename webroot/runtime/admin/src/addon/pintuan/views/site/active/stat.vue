<template>
    <div class="main-container">
        <el-card class="card !border-none" shadow="never">
            <el-page-header :content="t('活动统计')" :icon="ArrowLeft" @back="router.push('/pintuan/active/list')" />
        </el-card>
        <div v-loading="loading">
            <el-card class="box-card mt-[10px] !border-none compact-card" shadow="never">
                <el-form class="mt-[10px]" :model="formData" label-width="120px" ref="formRef" label-position="left"
                    v-if="Object.keys(formData).length">
                    <div class="relative" v-if="formData">
                        <el-row>
                            <el-col :span="8">
                                <el-form-item :label="t('活动名称')">
                                    <div class="input-width">
                                        <span>{{formData.title}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('活动商品')">
                                    <div class="flex items-center">
                                        <div class="mr-[10px] rounded-full w-[50px] h-[50px] flex items-center justify-center">
                                            <el-image v-if="formData.goods_data?.cover " :src="img(formData.goods_data?.cover)" fit="cover" class="w-[50px] h-[50px]" >
                                                <template #error>
                                                    <img class="w-[50px] h-[50px]" src="@/addon/pintuan/assets/goods_default.png" alt="">
                                                </template>
                                            </el-image>
                                            <img class="w-[50px] h-[50px]" v-else src="@/addon/pintuan/assets/goods_default.png" alt="">
                                        </div>
                                        <div class="flex flex-col">
                                            <span>{{formData.goods_data?.title}}</span>
                                        </div>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('活动时间')">
                                    <div class="input-width">
                                        <span>{{formData.start_time}} ~ {{formData.end_time}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('活动对象')">
                                    <div class="input-width">
                                        <span v-if="formData.member_rule == 'all'">{{t('所有会员参与')}}</span>
                                        <div v-if="formData.member_rule == 'level'">
                                            {{ formData.member_level }}
                                        </div>
                                        <div v-if="formData.member_rule == 'label'">
                                            {{ formData.member_label }}
                                        </div>
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </div>
                </el-form>
            </el-card>
            <!-- 商品概况 -->
            <el-card shadow="never" class="!border-none mt-[10px] goods-stat compact-card">
                <template #header>
                    <div class="flex justify-between items-start">
                        <span class="text-lg mr-[10px]">{{ t('统计') }}</span>
                    </div>
                </template>
                <div class="flex flex-wrap -mx-[10px] compact-stats">
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.pv" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('浏览量') }}</span>
                                    <el-tooltip class="box-item" effect="light"
                                        :content="t('统计时间内浏览该活动的次数,一个人在统计时间内访问多次记为多次')" placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.order_count" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('下单数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内,成功下单的订单总数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.pay_order_count" class="ml-[32px]" > 
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('支付订单数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内，成功支付的订单总数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.refund_order_count" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('退款订单数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内，成功退款订单总数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.open_group_num" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('开团数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内，成功发起的拼团数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.uv" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('访客数') }}</span>
                                    <el-tooltip class="box-item" effect="light"
                                        :content="t('统计时间内，访问商品详情页的用户数,一个人在统计时间范围内访问多次只记为一个')" placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.order_amount" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('下单金额') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内,成功下单的商品总金额')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.pay_order_amount" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('支付订单金额') }}</span>
                                    <el-tooltip class="box-item" effect="light"
                                        :content="t('统计时间内，所有参与秒杀活动的商品支付订单金额的总和')" placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.refund_order_amount" class="ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('退款订单金额') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内，成功退款订单的总金额')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.join_group_num" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('参团数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内，参与拼团的总人数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.view_avg" class="flex-1 ml-[32px]" precision="2">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('人均访问次数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('浏览量/访客数')" placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.order_rate" suffix="%" class="flex-1 ml-[32px]" precision="2">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('下单率') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('下单数/访客数')" placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.pay_rate" suffix="%" class="flex-1 ml-[32px]" precision="2">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('支付率') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('支付单数/下单数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.refund_rate" suffix="%" class="flex-1 ml-[32px]" precision="2">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('退款率') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('退款单数/支付单数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.success_group_num" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('成团数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间范围内，所有成团的数量')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <!-- <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.open_group_num" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('开团数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内,所有开团的数量')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.group_open_rate" suffix="%" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('开团率') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('统计时间内,')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.success_group_num" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('成团数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('关闭订单数/下单数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.group_success_rate" suffix="%" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('成团率') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('关闭订单数/下单数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.join_group_member_num" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('参团次数') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('关闭订单数/下单数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div>
                    <div class="w-[20%] flex flex-shrink-0 px-[10px] mb-[10px]">
                        <el-statistic :value="goodsTotal.group_join_rate" suffix="%" class="flex-1 ml-[32px]">
                            <template #title>
                                <div class="flex items-center">
                                    <span class="mr-[5px] text-[14px]">{{ t('参团率') }}</span>
                                    <el-tooltip class="box-item" effect="light" :content="t('关闭订单数/下单数')"
                                        placement="top">
                                        <el-icon>
                                            <QuestionFilled />
                                        </el-icon>
                                    </el-tooltip>
                                </div>
                            </template>
                        </el-statistic>
                    </div> -->
                </div>
            </el-card>
            <el-card shadow="never" class="!border-none goods-stat mt-[10px] compact-card">
                <h3 class="text-lg mb-[15px]">拼单统计</h3>
                <div class="flex items-start">
                    <div class="flex items-center">
                        <el-form :inline="true">
                            <el-form-item :label="t('日期')" prop="date">
                                <el-date-picker v-model="splicingOrdersDate" type="daterange" value-format="YYYY/MM/DD"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    :shortcuts="shortcuts"  />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="getSplicingordersStatFn()" >{{ t('search')
                                    }}</el-button>
                                <el-button @click="resetSplicingorders()" >{{ t('重置') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="bg-[white] mt-[15px]">
                    <div ref="splicingordersChartRef" class="h-[300px] w-full pt-[20px]"></div>
                </div>
            </el-card>
            <el-card shadow="never" class="!border-none goods-stat mt-[10px] compact-card">
                <h3 class="text-lg mb-[15px]">商品统计</h3>
                <div class="flex items-start">
                    <div class="flex items-center">
                        <el-form :inline="true">
                            <el-form-item :label="t('日期')" prop="date">
                                <el-date-picker v-model="goodsDate" type="daterange" value-format="YYYY/MM/DD"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    :shortcuts="shortcuts"  />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="getGoodsStatFn()" >{{ t('search')
                                    }}</el-button>
                                <el-button @click="resetGoods()" >{{ t('重置') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="bg-[white] mt-[15px]">
                    <div ref="goodsChartRef" class="h-[300px] w-full pt-[20px]"></div>
                </div>
            </el-card>
            <el-card shadow="never" class="!border-none goods-stat mt-[10px] compact-card">
                <h3 class="text-lg mb-[15px]">订单统计</h3>
                <div class="flex items-start">
                    <div class="flex items-center">
                        <el-form :inline="true">
                            <el-form-item :label="t('日期')" prop="date">
                                <el-date-picker v-model="orderDate" type="daterange" value-format="YYYY/MM/DD"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    :shortcuts="shortcuts"  />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="getOrderStatFn()" >{{ t('search')
                                    }}</el-button>
                                <el-button @click="resetOrder()" >{{ t('重置') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="bg-[white] mt-[15px]">
                    <div ref="orderChartRef" class="h-[300px] w-full pt-[20px]"></div>
                </div>
            </el-card>
            <el-card shadow="never" class="!border-none goods-stat mt-[10px] compact-card">
                <h3 class="text-lg mb-[15px]">访客分析</h3>
                <div class="flex items-start">
                    <div class="flex items-center">
                        <el-form :inline="true">
                            <el-form-item :label="t('日期')" prop="date">
                                <el-date-picker v-model="visitorDate" type="daterange" value-format="YYYY/MM/DD"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    :shortcuts="shortcuts"  />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="getVisitorStatFn()" >{{ t('search')
                                    }}</el-button>
                                <el-button @click="resetVisitor()" >{{ t('重置') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="bg-[white] mt-[15px]">
                    <div ref="visitorChartRef" class="h-[300px] w-full pt-[20px]"></div>
                </div>
            </el-card>

            <!-- 渠道数据统计 -->
            <el-card shadow="never" class="!border-none goods-stat mt-[10px] compact-card">
                <h3 class="text-lg mb-[15px]">渠道数据统计</h3>

                <div class="flex items-start">
                    <div class="flex items-center">
                        <el-form :inline="true">
                            <el-form-item :label="t('日期')" prop="date">
                                <el-date-picker v-model="channelDate" type="daterange" value-format="YYYY/MM/DD"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    :shortcuts="shortcuts" />
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="getChannelStatFn()" >{{ t('search')
                                    }}</el-button>
                                <el-button @click="resetChannel()">{{ t('重置') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <!-- channelDate -->
                <div class="bg-[white] mt-[15px]">
                    <div class="flex">
                        <!-- 左侧饼图 -->
                        <div class="w-1/2">
                            <div ref="channelChartRef" class="h-[300px] w-full"></div>
                        </div>
                        <!-- 右侧表格 -->
                        <div class="w-1/2 pl-4">
                            <el-table :data="channelTableData" style="width: 100%" height="300">
                                <el-table-column prop="channel_name" label="渠道名称" align="center" />
                                <el-table-column prop="uv" label="访客数" align="center" />
                                <el-table-column prop="order_count" label="订单数" align="center" />
                                <el-table-column prop="order_rate" label="转化率" align="center">
                                    <template #default="scope">
                                        <span>{{ scope.row.order_rate }}%</span>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </div>
                    </div>
                </div>
            </el-card>
            <el-card shadow="never" class="!border-none goods-stat mt-[10px] compact-card" v-if="false">
                <h3 class="text-lg mb-[15px]">活动小时维度</h3>
                <div class="flex items-start">
                    <div class="flex items-center">
                        <el-form :inline="true">
                            <el-form-item :label="t('日期')" prop="date">
                                <el-date-picker v-model="date2" type="daterange" value-format="YYYY/MM/DD"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    :shortcuts="shortcuts" size="small" />
                            </el-form-item>
                            <el-form-item :label="t('场次')" prop="date">
                                <el-select v-model="time2" clearable :placeholder="t('请选择场次')" class="w-[200px]" size="small">
                                    <el-option v-for="item in timeList" :key="item.id" :label="item.name"
                                        :value="item.id" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="t('商品')" prop="goods">
                                <el-select v-model="goods2" clearable :placeholder="t('请选择商品')" class="w-[200px]" size="small">
                                    <el-option v-for="item in goodsList" :key="item.id" :label="item.name"
                                        :value="item.goods_id" />
                                </el-select>
                            </el-form-item>
                            <el-form-item :label="t('渠道')" clearable prop="channel">
                                <el-select v-model="channel2" :placeholder="t('请选择渠道')" class="w-[200px]" size="small">
                                    <el-option :label="item" :value="key" v-for="(item, key) in channelList"
                                        :key="key" />
                                </el-select>
                            </el-form-item>
                             <el-form-item :label="t('时间段范围')" prop="hour_range">
                                 <el-time-picker
                                    v-model="hour_range"
                                    is-range
                                    value-format="HH"
                                    :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')"
                                    size="small"
                                />
                            </el-form-item>
                            <el-form-item :label="t('指定小时')" prop="specific_hours">
                                <el-select v-model="specific_hours" multiple :placeholder="t('请选择小时')"
                                    class="w-[200px]" size="small">
                                    <el-option v-for="h in 24" :key="h - 1" :label="`${h - 1} 时`" :value="String(h - 1)" />
                                </el-select>
                            </el-form-item>

                            <el-form-item>
                                <el-button type="primary" @click="getPintuanStatHourFn()" size="small">{{ t('search')
                                    }}</el-button>
                                <el-button @click="reset2()" size="small">{{ t('重置') }}</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </div>
                <div class="bg-[white] mt-[15px]">
                    <div ref="incomeChartRef2" class="h-[300px] w-full pt-[20px]"></div>
                </div>
            </el-card>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { img } from '@/utils/common'
import { t } from '@/lang'
import * as echarts from 'echarts'
import { useRouter, useRoute } from 'vue-router'
import { getChannelType } from '@/app/api/sys'
import { ArrowLeft } from '@element-plus/icons-vue'
import { FormInstance } from 'element-plus'
import { getPintuanStatTotal, getPintuanStatDay, getPintuanDetail, getPintuanStatHour, getPintuanStatChannel } from "@/addon/pintuan/api/site/active"

const router = useRouter()
const route = useRoute()
const pintuanId = route.query.id
const loading = ref(false)
// 获取默认7天日期范围（今天以及前6天）
const getDefault7DaysRange = () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 6) // 前6天
    const format = (date: any) => {
        const year = date.getFullYear()
        const month = date.getMonth() + 1
        const day = date.getDate()
        return year + '/' + (month < 10 ? '0' + month : month) + '/' + (day < 10 ? '0' + day : day)
    }
    return [format(start), format(end)]
}

const curDateRange = () => {
    const end = new Date()
    const start = new Date()
    start.setTime(start.getTime() - 3600 * 1000 * 24 * 29)
    const format = (date: any) => {
        const year = date.getFullYear()
        const month = date.getMonth() + 1
        const day = date.getDate()

        return year + '/' + month + '/' + day
    }
    return [format(start), format(end)]
}
const date = ref<any>([])
date.value = []
const date2 = ref<any>([])
date2.value = []
const hour_range = ref([])
const hour_range_start = ref('')
const hour_range_end = ref('')
const specific_hours = ref([])


const time = ref<any>('')
const time2 = ref<any>('')
const timeList = ref<any>([])
const goodsList = ref<any>([])
const goods = ref<any>('')
const goods2 = ref<any>('')

const channelList = ref({})
const channel = ref<any>('')
const channel2 = ref<any>('')
const setChannelList = async () => {
  const res = await getChannelType()
    channelList.value = res.data
};
const reset = () => {
    date.value = []
    time.value = ''
    goods.value = ''
    channel.value = ''
    getPintuanStatDayFn()
}
const reset2 = () => {
    date2.value = []
    time2.value = ''
    goods2.value = ''
    channel2.value = ''
    hour_range_end.value = ''
    specific_hours.value = []
    hour_range_start.value = ''
    getPintuanStatHourFn()
}
const shortcuts = [
    {
        text: '今天',
        value: new Date()
    }, {
        text: '昨天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24)
            return [start, end]
        }
    }, {
        text: '最近7天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            return [start, end]
        }
    }, {
        text: '最近30天',
        value: () => {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 29)
            return [start, end]
        }
    }, {
        text: '上月',
        value: () => {
            const date = new Date()
            const year = date.getFullYear()
            const month = date.getMonth()
            const start = new Date(year, month - 1, 1)
            const end = new Date(year, month, 0)
            return [start, end]
        }
    }, {
        text: '本月',
        value: () => {
            const date = new Date()
            const year = date.getFullYear()
            const month = date.getMonth()
            const start = new Date(year, month, 1)
            const end = new Date()
            return [start, end]
        }
    }, {
        text: '本年',
        value: () => {
            const date = new Date()
            const year = date.getFullYear()
            const start = new Date(year, 0, 1)
            const end = new Date()
            return [start, end]
        }
    }
]

const goodsTotal = ref<any>([]) // 基本信息

// 商品维度变量
const goodsDate = ref<any>([])

// 订单维度变量
const orderDate = ref<any>([])

// 访客分析变量
const visitorDate = ref<any>([])

// 拼单统计变量
const splicingOrdersDate = ref<any>([])

// 渠道数据变量
const channelTableData = ref<any>([])
const channelDate = ref<any>([])
// 图表引用
const goodsChartRef = ref(null)
const orderChartRef = ref(null)
const visitorChartRef = ref(null)
const splicingordersChartRef = ref(null)
const channelChartRef = ref(null)
const incomeChartRef = ref(null)
const incomeChartRef2 = ref(null)
const initIncomeChart = (data:any,chart:any) => {
    const goodsData = reactive<any>([])
    const goodsLegend = reactive<any>([])

    data.data.forEach((item: any) => {
        if (item.name == 'pv') {
            goodsData.push({
                name: '浏览量',
                type: 'line',
                smooth: true,
                data: item.data
            })
            goodsLegend.push('浏览量')
        } else if (item.name == 'uv') {
            goodsData.push({
                name: '访客数',
                type: 'line',
                smooth: true,
                data: item.data
            })
            goodsLegend.push('访客数')
        } else if (item.name == 'order_count') {
            goodsData.push({
                name: '下单数',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('下单数')
        } else if (item.name == 'order_amount') {
            goodsData.push({
                name: '下单金额',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('下单金额')
        } else if (item.name == 'pay_order_count') {
            goodsData.push({
                name: '支付订单数',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('支付订单数')
        } else if (item.name == 'pay_order_amount') {
            goodsData.push({
                name: '支付订单金额',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('支付订单金额')
        } else if (item.name == 'refund_order_count') {
            goodsData.push({
                name: '退款订单数',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('退款订单数')
        } else if (item.name == 'refund_order_amount') {
            goodsData.push({
                name: '退款订单金额',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('退款订单金额')
        }else if (item.name == 'close_order_count') {
            goodsData.push({
                name: '关闭订单数',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('关闭订单数')
        } else if (item.name == 'close_order_amount') {
            goodsData.push({
                name: '关闭订单金额',
                type: 'bar',
                data: item.data
            })
            goodsLegend.push('关闭订单金额')
        }else if (item.name == 'refund_rate') {
            goodsData.push({
                name: '退款率',
                type: 'line',
                data: item.data
            })
            goodsLegend.push('退款率')
        }else if (item.name == 'order_rate') {
            goodsData.push({
                name: '下单率',
                type: 'line',
                data: item.data
            })
            goodsLegend.push('下单率')
        }else if (item.name == 'pay_rate') {
            goodsData.push({
                name: '支付率',
                type: 'line',
                data: item.data
            })
            goodsLegend.push('支付率')
        }else if (item.name == 'close_rate') {
            goodsData.push({
                name: '订单关闭率',
                type: 'line',
                data: item.data
            })
            goodsLegend.push('订单关闭率')
        }else if (item.name == 'view_avg') {
            goodsData.push({
                name: '人均访问次数',
                type: 'line',
                data: item.data
            })
            goodsLegend.push('人均访问次数')
        }
    })
    if (chart !== null) {
        const incomeChart = echarts.init(chart)
        // 配置项
        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function (params: any) {
                    let result = params[0].axisValue + '<br/>'
                    params.forEach((item: any) => {
                        const isPercentSeries = ['退款率', '下单率', '支付率', '订单关闭率'].includes(item.seriesName)
                        const value = item.data
                        const displayValue = isPercentSeries ? (value != null ? value + '%' : '-') : value
                        result += `${item.marker}${item.seriesName}：${displayValue}<br/>`
                    })
                    return result
                }
            },
            color: ['#1890ff', '#00c050', '#F76A6C', '#43C5FF', '#ffab2b', '#b37feb', '#627DFE', '#11D0EA'],
            legend: {
                data: [],
                selected: {
                    '关闭订单数': false,
                    '订单关闭率': false,
                    '关闭订单金额': false,
                    '退款率': false,
                    '退款订单数': false,
                    '退款订单金额': false,
                }
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    interval: 0,
                    rotate: 45
                },
                data: data.time
            },
            yAxis: {
                type: 'value'
            },
            grid: [{
                left: '3%',
                right: '3%',
                bottom: '40',
                containLabel: true
            }],
            series: []
        }
        // 使用配置项初始化图表
        option.series = goodsData
        option.legend.data = goodsLegend
        incomeChart.setOption(option)
        incomeChart.resize({
            width: 'auto'
        })
        window.addEventListener('resize', () => {
            // 页面大小变化后Echarts也更改大小
            incomeChart.resize()
        })
    }
}
const initialFormData = reactive({
    title: '', // 活动标题

})

const formData: Record<string, any> = reactive({ ...initialFormData })
const showTimeTitle = (data: any) => {
  return data.start_time_format + ' - ' + data.end_time_format
}

const getPintuanDetailFn = async() => {
  loading.value = true
  getPintuanDetail(pintuanId).then((res: any) => {
    const data = res.data
    Object.assign(formData, data)
    if (formData.member_level) {
      formData.member_level = data.member_level.map(obj => obj.level_name)
      formData.member_level = formData.member_level.join(',')
    }
    if (formData.member_label) {
      formData.member_label = data.member_label.map(obj => obj.label_name)
      formData.member_label = formData.member_label.join(',')
    }
  })
}



const searchFormRef = ref<FormInstance>()
const getPintuanStatTotalFn = ()=>{
    getPintuanStatTotal({pintuan_id:pintuanId}).then((res:any)=>{
        goodsTotal.value = res.data
    })
}
const getPintuanStatDayFn = ()=>{
    const data = date.value? date.value.join('-') : ''
    getPintuanStatDay({pintuan_id:pintuanId,time_range:data,time_id:time.value,goods_id:goods.value,channel_code:channel.value}).then((res:any)=>{
        initIncomeChart(res.data,incomeChartRef.value) 
    })
}
const getPintuanStatHourFn = ()=>{
    const data = date2.value? date2.value.join('-') : ''
    const params: any = {
        pintuan_id:pintuanId,
        time_range: data,
        time_id: time2.value,
        goods_id: goods2.value,
        channel_code: channel2.value,
    }
    if (specific_hours.value.length > 0) {
        params.specific_hours = specific_hours.value
    } 
    if (hour_range_start.value && !hour_range_end.value) {
        ElMessage.error('请选择结束时间')
        return
    } else if (!hour_range_start.value && hour_range_end.value) {
        ElMessage.error('请选择开始时间')
        return
    }else if((hour_range_start.value && hour_range_end.value) && Number(hour_range_end.value)< Number(hour_range_start.value)){
        ElMessage.error('结束时间不能小于开始时间')
        return
    }else{
        params.hour_range =  hour_range.value
        // [hour_range_start.value, hour_range_end.value]
    }
    getPintuanStatHour(params).then((res:any)=>{
        initIncomeChart(res.data,incomeChartRef2.value)
    })
}

// 商品维度重置
const resetGoods = () => {
    const defaultDateRange = getDefault7DaysRange()
    goodsDate.value = defaultDateRange
    orderDate.value = defaultDateRange
    visitorDate.value = defaultDateRange
    splicingOrdersDate.value = defaultDateRange
    channelDate.value = defaultDateRange

    getGoodsStatFn()
}

// 商品维度统计
const getGoodsStatFn = ()=>{
    
    const data = goodsDate.value? goodsDate.value.join('-') : ''
    getPintuanStatDay({pintuan_id:pintuanId,time_range:data}).then((res:any)=>{
        initGoodsChart(res.data)
    })
}

// 商品维度图表初始化（只显示访客数和订单数）
const initGoodsChart = (data:any) => {
    const goodsData = reactive<any>([])
    const goodsLegend = reactive<any>([])

    data.data.forEach((item: any) => {
        if (item.name == 'uv') {
            goodsData.push({
                name: '访客数',
                type: 'line',
                smooth: true,
                data: item.data
            })
            goodsLegend.push('访客数')
        } else if (item.name == 'order_count') {
            goodsData.push({
                name: '订单数',
                type: 'line',
                data: item.data
            })
            goodsLegend.push('订单数')
        }
    })
    if (goodsChartRef.value !== null) {
        const goodsChart = echarts.init(goodsChartRef.value)
        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function (params: any) {
                    let result = params[0].axisValue + '<br/>'
                    params.forEach((item: any) => {
                        result += `${item.marker}${item.seriesName}：${item.data}<br/>`
                    })
                    return result
                }
            },
            color: ['#1890ff', '#00c050'],
            legend: {
                data: goodsLegend
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    interval: 0,
                    rotate: 45
                },
                data: data.time
            },
            yAxis: {
                type: 'value'
            },
            grid: [{
                left: '3%',
                right: '3%',
                bottom: '40',
                containLabel: true
            }],
            series: goodsData
        }
        goodsChart.setOption(option)
        goodsChart.resize({
            width: 'auto'
        })
        window.addEventListener('resize', () => {
            goodsChart.resize()
        })
    }
}

// 订单维度重置
const resetOrder = () => {
    const defaultDateRange = getDefault7DaysRange()
    orderDate.value = defaultDateRange
    getOrderStatFn()
}

// 订单维度统计
const getOrderStatFn = ()=>{
    const data = orderDate.value? orderDate.value.join('-') : ''
    getPintuanStatDay({pintuan_id:pintuanId,time_range:data}).then((res:any)=>{
        initOrderChart(res.data)
    })
}

// 订单维度图表初始化（只显示订单金额和订单数）
const initOrderChart = (data:any) => {
    const orderData = reactive<any>([])
    const orderLegend = reactive<any>([])

    data.data.forEach((item: any) => {
        if (item.name == 'order_amount') {
            orderData.push({
                name: '订单金额',
                type: 'line',
                data: item.data
            })
            orderLegend.push('订单金额')
        } else if (item.name == 'order_count') {
            orderData.push({
                name: '订单数',
                type: 'line',
                data: item.data
            })
            orderLegend.push('订单数')
        }
    })
    if (orderChartRef.value !== null) {
        const orderChart = echarts.init(orderChartRef.value)
        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function (params: any) {
                    let result = params[0].axisValue + '<br/>'
                    params.forEach((item: any) => {
                        result += `${item.marker}${item.seriesName}：${item.data}<br/>`
                    })
                    return result
                }
            },
            color: ['#1890ff', '#00c050',],
            legend: {
                data: orderLegend
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    interval: 0,
                    rotate: 45
                },
                data: data.time
            },
            yAxis: {
                type: 'value'
            },
            grid: [{
                left: '3%',
                right: '3%',
                bottom: '40',
                containLabel: true
            }],
            series: orderData
        }
        orderChart.setOption(option)
        orderChart.resize({
            width: 'auto'
        })
        window.addEventListener('resize', () => {
            orderChart.resize()
        })
    }
}

// 访客分析重置
const resetVisitor = () => {
    const defaultDateRange = getDefault7DaysRange()
    visitorDate.value = defaultDateRange
    getVisitorStatFn()
}

// 访客分析统计
const getVisitorStatFn = ()=>{
    const data = visitorDate.value? visitorDate.value.join('-') : ''
    getPintuanStatDay({pintuan_id:pintuanId,time_range:data}).then((res:any)=>{
        initVisitorChart(res.data)
    })
}

// 拼单统计重置
const resetSplicingorders = () => {
    const defaultDateRange = getDefault7DaysRange()
    splicingOrdersDate.value = defaultDateRange
    getSplicingordersStatFn()
}

// 拼单统计统计
const getSplicingordersStatFn = ()=>{
    const data = splicingOrdersDate.value? splicingOrdersDate.value.join('-') : ''
    getPintuanStatDay({pintuan_id:pintuanId,time_range:data}).then((res:any)=>{
        initSplicingordersChart(res.data)
    })
}

const getChannelStatFn = ()=>{
    const data = channelDate.value? channelDate.value.join('-') : ''
    getPintuanStatChannel({pintuan_id:pintuanId,time_range:data}).then((res:any)=>{
        channelTableData.value = res.data
        initChannelChart(res.data)
    })
}
const resetChannel = () => {
    const defaultDateRange = getDefault7DaysRange()
    channelDate.value = defaultDateRange
    getChannelStatFn()
}
// 访客分析图表初始化（只显示浏览量和访客数）
const initVisitorChart = (data:any) => {
    const visitorData = reactive<any>([])
    const visitorLegend = reactive<any>([])

    data.data.forEach((item: any) => {
        if (item.name == 'pv') {
            visitorData.push({
                name: '浏览量',
                type: 'line',
                smooth: true,
                data: item.data
            })
            visitorLegend.push('浏览量')
        } else if (item.name == 'uv') {
            visitorData.push({
                name: '访客数',
                type: 'line',
                smooth: true,
                data: item.data
            })
            visitorLegend.push('访客数')
        }
    })
    if (visitorChartRef.value !== null) {
        const visitorChart = echarts.init(visitorChartRef.value)
        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function (params: any) {
                    let result = params[0].axisValue + '<br/>'
                    params.forEach((item: any) => {
                        result += `${item.marker}${item.seriesName}：${item.data}<br/>`
                    })
                    return result
                }
            },
            color: ['#1890ff', '#00c050'],
            legend: {
                data: visitorLegend
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    interval: 0,
                    rotate: 45
                },
                data: data.time
            },
            yAxis: {
                type: 'value'
            },
            grid: [{
                left: '3%',
                right: '3%',
                bottom: '40',
                containLabel: true
            }],
            series: visitorData
        }
        visitorChart.setOption(option)
        visitorChart.resize({
            width: 'auto'
        })
        window.addEventListener('resize', () => {
            visitorChart.resize()
        })
    }
}

const initSplicingordersChart = (data:any) => {
    const splicingordersData = reactive<any>([])
    const splicingordersLegend = reactive<any>([])
    data.data.forEach((item: any) => {
        if (item.name == 'open_group_num') {
            splicingordersData.push({
                name: '开团数',
                type: 'line',
                smooth: true,
                data: item.data
            })
            splicingordersLegend.push('开团数')
        }else if (item.name == 'join_group_num') {
            splicingordersData.push({
                name: '参团数',
                type: 'line',
                smooth: true,
                data: item.data
            })
            splicingordersLegend.push('参团数')
        }else if (item.name == 'success_group_num') {
            splicingordersData.push({
                name: '成团数',
                type: 'line',
                smooth: true,
                data: item.data
            })
            splicingordersLegend.push('成团数')
        }
    })
    if (splicingordersChartRef.value!== null) {
        const splicingordersChart = echarts.init(splicingordersChartRef.value)
        const option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'cross'
                },
                formatter: function (params: any) {
                    let result = params[0].axisValue + '<br/>'
                    params.forEach((item: any) => {
                        result += `${item.marker}${item.seriesName}：${item.data}<br/>`
                    })
                    return result
                }
            },
            color: ['#1890ff', '#00c050', '#F76A6C'],
            legend: {
                data:splicingordersLegend
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    interval: 0,
                    rotate: 45
                },
                data: data.time
            },
            yAxis: {
                type: 'value'
            },
            grid: [{
                left: '3%',
                right: '3%',
                bottom: '40',
                containLabel: true
            }],
            series: splicingordersData
        }
        splicingordersChart.setOption(option)
        splicingordersChart.resize({
            width: 'auto'
        })
        window.addEventListener('resize', () => {
            splicingordersChart.resize()
        })
    }
}

//获取渠道数据统计
const getPintuanStatChannelFn = ()=>{
    getPintuanStatChannel({pintuan_id:pintuanId}).then((res:any)=>{
        channelTableData.value = res.data
        initChannelChart(res.data)
    })
}

// 渠道分析饼图初始化
const initChannelChart = (data: any) => {
    if (channelChartRef.value !== null) {
        const channelChart = echarts.init(channelChartRef.value)

        // 转换数据格式为饼图需要的格式
        const pieData = data.map((item: any) => ({
            name: item.channel_name,
            value: item.uv,
            channel_code: item.channel_code,
            order_count: item.order_count,
            order_rate: item.order_rate
        }))

        const option = {
            // title: {
            //     text: '渠道访客数分布',
            //     left: 'center',
            //     top: 20,
            //     textStyle: {
            //         fontSize: 16
            //     }
            // },
            tooltip: {
                trigger: 'item',
                formatter: function (params: any) {
                    const data = params.data
                    return `${params.name}<br/>
                            访客数: ${data.value}<br/>
                            订单数: ${data.order_count}<br/>
                            转化率: ${data.order_rate}%`
                }
            },
            // legend: {
            //     orient: 'vertical',
            //     left: 'left',
            //     top: 'middle'
            // },
            series: [
                {
                    name: '渠道分析',
                    type: 'pie',
                    radius: '70%',
                    center: ['50%', '50%'],
                    data: pieData,
                    emphasis: {
                        itemStyle: {
                            shadowBlur: 10,
                            shadowOffsetX: 0,
                            shadowColor: 'rgba(0, 0, 0, 0.5)'
                        }
                    },
                    label: {
                        show: true,
                        formatter: '{b}: {c} ({d}%)'
                    }
                }
            ]
        }

        channelChart.setOption(option)
        channelChart.resize({
            width: 'auto'
        })
        window.addEventListener('resize', () => {
            channelChart.resize()
        })
    }
}

onMounted(() => {
  // 定义异步初始化函数
  const initData = async () => {
    loading.value = true;
    try {
      await setChannelList();
      await getPintuanDetailFn()


      // 设置默认7天日期范围
      const defaultDateRange = getDefault7DaysRange()
      goodsDate.value = defaultDateRange
      orderDate.value = defaultDateRange
      visitorDate.value = defaultDateRange
      splicingOrdersDate.value = defaultDateRange
      channelDate.value = defaultDateRange
      await Promise.all([
        getPintuanStatTotalFn(),
        getPintuanStatDayFn(),
        getPintuanStatHourFn(),
        getGoodsStatFn(),
        getOrderStatFn(),
        getVisitorStatFn(),
        getSplicingordersStatFn(),
        getPintuanStatChannelFn()
      ]);
      loading.value = false;
    } catch (error) {
    }
  };
  // 执行初始化函数
  initData();
});
</script>
<style scoped>
:deep(.el-col-6,.el-col-24){
    height: 40px;
}
</style>