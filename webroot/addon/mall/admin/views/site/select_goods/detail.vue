<template>
    <div class="main-container supply">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push(`/supply/select/list`)" />
        </el-card>
        <el-card class="box-card !border-none min-h-[50vh]" shadow="never" v-loading="loading">
            <div v-if="Object.keys(detail).length">
                <div class="flex mb-[20px]">
                    <div class="w-[500px] flex flex-col mr-[30px]">
                        <el-image class="w-[500px] h-[500px] rounded-[8px]"  :src="img(detail.goods.goods_image_thumb_big.length ? detail.goods.goods_image_thumb_big[imgActive] : '')" fit="cover" hide-on-click-modal>
                            <template #error>
                                <img src="@/addon/mall/assets/goods_default.png" class="w-[500px] h-[500px] rounded-[8px] align-middle">
                            </template>
                        </el-image>
                        <div class="flex justify-between mt-[10px]">
                            <div class="h-[82px] w-[20px] flex items-center justify-center cursor-pointer" @click="imgListClick('left')">
                                <el-icon color="#ccc" size="14">
                                    <ArrowLeftBold />
                                </el-icon>
                            </div>
                            <div class="relative flex-1 overflow-hidden h-[82px]">
                                <div class="absolute flex items-center  h-[100%] transition-all ease-in-out duration-500" :style="{ 'left': imgListLeft + 'px' }">
                                    <template v-if="detail.goods.goods_image_thumb_small">
                                        <el-image class="w-[80px] h-[80px] mx-[6px]  box-border border-[1px] border-solid cursor-pointer rounded-[4px]" :class="[imgActive == index ? 'border-[var(--price-color)]' : 'border-transparent']" v-for="(item, index) in detail.goods.goods_image_thumb_small" :key="index" :src="img(item)" alt="" @mouseover="imgListHover(index)" fit="cover" lazy>
                                            <template #error>
                                                <img class="w-[80px] h-[80px] rounded-[4px]" src="@/addon/mall/assets/goods_default.png"  alt="" />
                                            </template>
                                        </el-image>
                                    </template>
                                </div>
                            </div>
                            <div class="h-[82px] w-[20px] flex items-center justify-center cursor-pointer" @click="imgListClick('right')">
                                <el-icon  color="#ccc" size="14">
                                    <ArrowRightBold />
                                </el-icon>
                            </div>
                        </div>
                    </div>
                    <div class="flex-1 detail-content-right">
                        <div class="text-[18px] leading-[24px] mb-[16px] good-name multi-hidden">{{ detail.goods.goods_name }}</div>
                        <div class="text-[16px] text-[#999] leading-[22px] mb-[20px]">{{ detail.goods.sub_title }}</div>
                        <div class="detail-bg  border-solid border-[1px] border-[#eee] p-[20px] relative">
                            <div class="flex items-center mb-[12px]">
                                <span class="w-[42px] text-[13px] text-[#999] mr-[30px]">供货价</span>
                                <span class="text-[22px] text-[var(--price-color)] leading-[30px] price-font">￥{{ detail.cost_price }}</span>
                            </div>
                            <div class="flex items-center mb-[12px]">
                                <span class="w-[42px] text-[13px] text-[#999] mr-[30px]">零售价</span>
                                <span class="text-[22px] leading-[30px] price-font">￥{{ detail.price }}</span>
                            </div>
                            <div class="flex items-center">
                                <span class="w-[42px] text-[13px] text-[#999] mr-[30px]">利润率</span>
                                <div class="flex items-center">
                                    <span class="text-[22px] text-[var(--price-color)] leading-[30px] price-font">{{ handleProfitRate(detail.profit_rate) }}</span>
                                    <span class="price-icon bg-[var(--price-color)] text-[13px] text-[#fff] h-[24px] leading-[24px] flex-center px-[10px] ml-[10px] rounded-r-[4px] rounded-bl-[4px]">可赚 <span class="price-font">{{ detail.earn_price }}</span>元</span>
                                </div>
                            </div>
                            <div class="absolute bottom-[10px] right-[10px] leading-[1]" v-if="detail.is_select == 1">
                                <span class="nc-iconfont nc-icon-yitiaohuo !text-[100px] text-[var(--price-color)]"></span>
                            </div>
                        </div>
                        <div class="border-solid border-0 border-[#eee] border-b-[1px] p-[20px]">
                            <div class="flex items-center leading-[18px] mb-[22px]">
                                <span class="w-[42px] text-[13px] text-[#999] mr-[30px] flex-shrink-0">总销量</span>
                                <div class="flex items-center text-[14px]">
                                    <span class="price-font">{{ detail.goods.sale_num }}{{ detail.goods.unit }}</span>
                                    <template v-if="detail.online_shop_num">
                                        <span class="w-[1px] h-[10px] bg-[#eee] mx-[10px]"></span>
                                        <span class="text-[#999]">已有</span>
                                        <span class="mx-[4px] price-font text-[#333]">{{ detail.online_shop_num }}</span>
                                        <span class="text-[#999]">个店铺上架</span>
                                    </template>
                                </div>
                            </div>
                            <div class="flex items-center leading-[18px]">
                                <span class="w-[42px] text-[13px] text-[#999] mr-[30px] flex-shrink-0">总库存</span>
                                <span class="text-[14px] price-font">{{ detail.goods.stock }}{{ detail.goods.unit  }}</span>
                            </div>
                        </div>
                        <div class="mt-[50px] pl-[120px]">
                            <el-button type="primary" class="!w-[140px] !bg-[var(--price-color)] !border-[var(--price-color)] !h-[40px] text-[14px]" @click="handlePickGood"  v-if="detail.is_select !== 1">立即挑货</el-button>
                            <el-button type="primary" class="!w-[140px] !bg-[var(--price-color)] !border-[var(--price-color)] !h-[40px] text-[14px]" @click="toDoods" v-else>查看</el-button>
                        </div>
                    </div>
                </div>
                <div class="p-[20px] bg-[#F5FAFF] rounded-[2px] flex mb-[20px]">
                    <el-image class="w-[150px] h-[150px] mr-[20px] rounded-[6px]" :src="img(detail.shop_info.front_end_logo)" fit="cover">
                        <template #error>
                            <img class="w-[150px] h-[150px] mr-[20px] rounded-[6px]" src="@/app/assets/images/supply_default.png" />
                        </template>
                    </el-image>
                    <div>
                        <div class="text-[16px] leading-[22px] mb-[16px] good-name cursor-pointer" @click="router.push({path: '/supply/select/supply', query:{ site_id:detail.site_id }})">{{detail.shop_info.site_name}}</div>
                        <div class="flex items-center text-[12px] mb-[14px]" v-if="detail.shop_info.desc">
                            <span class="w-[48px] text-[#999] mr-[20px] flex-shrink-0">店铺介绍</span>
                            <span class="oppoSans-M">{{ detail.shop_info.desc }}</span>
                        </div>
                        <div class="flex items-center text-[12px] mb-[14px]">
                            <span class="w-[48px] text-[#999] mr-[20px] flex-shrink-0">上架商品</span>
                            <span class="oppoSans-M">{{ detail.shop_info.online_goods_count}}种</span>
                        </div>
                        <div class="flex items-center text-[12px] mb-[14px]" v-if="detail.shop_info.phone">
                            <span class="w-[48px] text-[#999] mr-[20px] flex-shrink-0">联系方式</span>
                            <span class="oppoSans-M">{{ detail.shop_info.phone }}</span>
                        </div>
                        <div class="flex items-center text-[12px]" v-if="detail.shop_info.full_address">
                            <span class="w-[48px] text-[#999] mr-[20px] flex-shrink-0">地址</span>
                            <span class="oppoSans-M">{{ detail.shop_info.full_address }}</span>
                        </div>
                    </div>
                </div>
                <div>
                    <div class="p-[20px] border-solid border-[1px] border-[#eee] flex">
                        <span class="text-[16px] mr-[20px] cursor-pointer" :class="{'tab-item': active == 'detail'}" @click="active = 'detail'">商品详情</span>
                        <span class="text-[16px] mr-[20px] cursor-pointer" :class="{'tab-item': active == 'attr'}" @click="active = 'attr'" v-if="detail.goods && detail.goods.attr_format && Object.keys(detail.goods.attr_format).length">商品属性</span>
                    </div>
                    <div class="p-[20px] border-solid border-[1px] border-[#eee] border-t-0">
                        <div v-if="active == 'detail'">
                            <div v-html="detail.goods.goods_desc" v-if="detail.goods.goods_desc"></div>
                        </div>
                        <div v-else-if="active == 'attr'">
							<el-row>
								<template v-for="(item,index) in detail.goods.attr_format" :key="index">
									<el-col :span="12">
										<div class="flex items-center mb-[12px] leading-[32px] text-[14px]">
											<div class="w-[150px] text-[#999] mr-[12px] text-right  truncate">{{item.attr_value_name}}</div>
											<div class="input-width">{{ Array.isArray(item.attr_child_value_name) ? item.attr_child_value_name.join(',') : item.attr_child_value_name }}</div>
										</div>
									</el-col>
								</template>
							</el-row>
						</div>
                    </div>
                </div>
            </div>
        </el-card>
        <goods-add-popup ref="goodsAddRef" @confirm="getDetailFn" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { img } from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getGoodsDetail } from '@/app/lang/zh-cn/api/site/select_goods.js'
import goodsAddPopup from './components/goods-add-popup.vue'

const route = useRoute()
const router = useRouter()
const goodsId: any = route.query.goods_id
const pageName = route.meta.title
const detail = ref<any>({})
const loading = ref(true)
const active = ref('detail')

const getDetailFn = () => {
    loading.value = true
    getGoodsDetail({ goods_id: goodsId }).then((res: any) => {
        detail.value = res.data
        if (detail.value.goods.attr_format) {
            detail.value.goods.attr_format = detail.value.goods.attr_format.filter((item: any, index: any) => {
                return Array.isArray(item.attr_child_value_name) ? item.attr_child_value_name.length : item.attr_child_value_name
            })
        }
        loading.value = false
    })
}
getDetailFn()

const imgListLeft = ref(0)
const imgActive = ref(0)

// 图片list 上一页下一页点击事件
const imgListClick = (val: any) => {
    const maxLeft = (-92 * detail.value.goods.goods_image_thumb_small.length) + 460 > 0 ? 0 : (-92 * detail.value.goods.goods_image_thumb_small.length) + 460
    if (val == 'right') {
        imgListLeft.value = imgListLeft.value - 460 < maxLeft ? maxLeft : imgListLeft.value - 460
    } else {
        imgListLeft.value = imgListLeft.value + 460 > 0 ? 0 : imgListLeft.value + 460
    }
}
// 图片list hover事件
const imgListHover = (index: any) => {
    imgActive.value = index
}

const goodsAddRef = ref()
const handlePickGood = () => {
    goodsAddRef.value.open(detail.value.goods_id)
}

// 去查看
const toDoods = () => {
    const query: any = {
        goods_id: detail.value.mallGoodsSku.goods_id
    }
    if (detail.value.googs_type == 'real') {
        const url = router.resolve({
            path: '/mall/goods/real_edit',
            query
        })
        window.open(url.href)
    } else {
        const url = router.resolve({
            path: '/mall/goods/virtual_edit',
            query
        })
        window.open(url.href)
    }
}

const handleProfitRate = (profitRate: string) => {
  if (profitRate.indexOf('～') == -1) {
    return parseFloat(profitRate) * 100 + '%'
  } else {
    return (parseFloat(profitRate.split('～')[0]) * 100) + '% ～ ' + (parseFloat(profitRate.split('～')[1])) * 100 + '%';
  }
}
</script>

<style lang="scss" scoped>
.detail-bg{
    background-image: url('@/addon/mall/assets/supply_detail.png');
    background-repeat: no-repeat;
    background-size: 100% 100%;
}
.price-icon{
    position: relative;
    &::before{
        content: '';
        position: absolute;
        top: 0;
        left: -6px;
        border: 6px solid transparent;
        border-top-color: var(--price-color);
    }
}
.tab-item{
    position: relative;
    color: var(--price-color);
    &::after{
        content: '';
        position: absolute;
        bottom: -20px;
        left: 50%;
        width: 50px;
        height: 2px;
        background-color: var(--price-color);
        transform: translateX(-50%);
    }
}
.el-button:not(.is-round){
    border-radius: 4px !important;
}
.good-name{
    font-family: 'oppoSans-M' !important;
}
.supply{
    --el-color-primary: #e53a40 !important;
    --el-color-primary-light-9: #fff3f4 !important;
    --el-color-primary-light-8: #ffe7e9 !important;
    --el-color-primary-light-7: #ffdadd !important;
    --el-color-primary-light-5:  #ffbcc1 !important;
    --el-color-primary-light-3: #ff9e9a !important;
    --el-color-primary-dark-2: #a42a2e !important;
}
</style>