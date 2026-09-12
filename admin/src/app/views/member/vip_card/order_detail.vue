<template>
    <div class="main-container">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/admin/member/vip_card/order' })" />
        </el-card>
        <el-form :model="detail" label-width="140px" ref="formRef" class="page-form" v-loading="loading" label-position="left">
            <el-card class="box-card !border-none relative" shadow="never" v-if="detail">
                <h3 class="panel-title">订单信息</h3>
                <el-row class="px-[30px] mb-[20px]">
                    <el-col :span="8">
                        <el-form-item label="订单编号">
                            <div class="input-width">
                                <span>{{ detail.order_no }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="会员信息">
                            <div class="input-width flex items-center">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-if="detail.member.headimg" :src="img(detail.member.headimg)" alt="">
                                <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                                <div class="flex flex flex-col">
                                    <span>{{ detail.member.nickname || '' }}</span>
                                </div>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="订单来源">
                            <div class="input-width">
                                <span>{{ detail.order_from_name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="交易流水号">
                            <div class="input-width">
                                <span>{{ detail.out_trade_no }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="支付方式">
                            <div class="input-width">
                                <span>{{ detail.pay_type_name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="订单状态">
                            <div class="input-width">
                                <span>{{ detail.status_name }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="下单时间">
                            <div class="input-width">
                                <span>{{ detail.create_time }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" v-if="detail.pay_time">
                        <el-form-item label="支付时间">
                            <div class="input-width">
                                <span>{{ detail.pay_time }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <h3 class="panel-title">会员卡信息</h3>
                <el-row class="px-[30px]">
                    <el-col :span="8">
                        <el-form-item label="会员卡名称">
                            <div class="input-width">
                                <span>{{ detail.body }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="会员卡规格">
                            <div class="input-width">
                                <span>{{ detail.card_buy.item_config.item_name }}</span>
                                <span class="ml-[8px]">{{ detail.card_buy.item_config.time_limit }}{{ detail.card_buy.item_config.time_type == 'day' ? '天' : detail.card_buy.item_config.time_type == 'month' ? '月' : '年' }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="价格">
                            <div class="input-width">
                                <span>{{ detail.order_money }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="续费价">
                            <div class="input-width">
                                <span>{{ detail.card_buy.item_config.renew_price }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="是否试用">
                            <div class="input-width">
                                <span>{{ detail.card_buy.is_trial ? '是' : '否' }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="试用天数">
                            <div class="input-width">
                                <span>{{ detail.card_buy.trial_day }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="到期时间">
                            <div class="input-width">
                                <span>{{ detail.card_buy.expire_time }}</span>
                            </div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="会员卡权益">
                            <div class="input-width">
                                <div v-for="(item,index) in detail.card_buy.benefits_config_content" :key="index">{{ item.content }}</div>
                            </div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <h3 class="mt-[50px] mb-[20px]" v-if="detail.order_log.length > 0">订单日志</h3>
				<div class="mb-[100px]" style="min-height: 100px" v-if="detail.order_log.length > 0">
					<div class="flex" v-for="(items, index) in detail.order_log" :key="index">
						<div class="mr-[20px] min-w-[71px]">
							<div class="leading-[1] w-full text-[14px] w-[100px] flex justify-end" v-if="items.create_time">
								{{ items.create_time.split(' ')[0] }}
							</div>
							<div class="leading-[1] w-full text-[14px]  w-[100px] flex justify-end mt-[15px]" v-if="items.create_time">
								{{ items.create_time.split(' ')[1] }}
							</div>
						</div>
						<div>
							<div class="w-[16px] h-[16px] flex items-center bg-[#D1EBFF] border-[1px] border-[#0091FF] rounded-[999px]">
								<div class="w-[8px] h-[8px] mx-auto bg-[#0091FF] rounded-[999px]"></div>
							</div>
							<div v-if="index + 1 != detail.order_log.length" class="w-[2px] h-[50px] bg-[#D1EBFF] mx-auto">
							</div>
						</div>
						<div>
							<div class="leading-[1] ml-[20px] text-[14px]">
								{{ items.main_type_name }}{{ items.main_name }}
							</div>
							<div class="leading-[1] ml-[20px] text-[14px] mt-[15px]">
								<span>{{ items.type_name }}</span>
								<span class="ml-[10px]">{{items.content}}</span>
							</div>
						</div>
					</div>
				</div>
            </el-card>
        </el-form>
    </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { img } from '@/utils/common'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getOrderDetail } from '@/app/api/vip_card'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const orderId: number = parseInt(route.query.order_id as string)
const loading = ref(true)

const detail = ref<any>(null)

const getOrderDetailFn = () => {
    getOrderDetail(orderId).then(res => {
        detail.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
getOrderDetailFn()
</script>

<style scoped>

</style>