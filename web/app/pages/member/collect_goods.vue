<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="full" v-loading="goodsTableData.loading">
            <div>
                <div class="mb-[27px] text-[18px] text-[#333] font-500">全部商品 ({{ goodsTableData.total }})</div>
                <div v-if="goodsTableData.data.length && !goodsTableData.loading">
                    <div class="products">
                        <template v-for="(item,index) in goodsTableData.data" :key="index">
                            <div class="goods-item cursor-pointer border-[1px] border-solid border-[#eee] rounded-[var(--rounded-big)] relative"  @click="toGoodsDetail(item.goods.goods_id)">
                                <div class="w-[227px] h-[227px]">
                                     <el-image class="w-[227px] h-[227px] rounded-t-[var(--rounded-big)]" :src="img(item.goods_cover_thumb_mid ? item.goods_cover_thumb_mid : '')" fit="cover">
                                        <template #error>
                                            <img src="@/assets/images/goods_default.png" class="w-[227px] h-[227px] rounded-t-[var(--rounded-big)]">
                                        </template>
                                     </el-image>
                                </div>
                                <div class="px-[20px] pt-[15px] pb-[20px] flex flex-col justify-between h-[112px] mt-[-4px]">
                                    <div class="multi-hidden text-[14px] leading-[18px] text-[#333] h-[36px]">{{item.goods_name}}</div>
                                    <div class="text-[#333] price-font">
                                        <span class="text-[12px]">¥</span>
                                        <span class="text-[20px]" v-if="item.goodsSku">{{item.goodsSku.price}}</span>
                                    </div>
                                </div>
                                <div class="collect absolute top-[10px] left-[10px] z-9 bg-[#000] rounded-[4px] text-[#fff] cursor-pointer h-[26px] leading-[26px] opacity-50 w-[70px] text-center text-[12px] hidden"  @click.stop="cancelCollectGoods(item.goods.goods_id)">
                                    取消收藏
                                </div>
                            </div>
                        </template>
                    </div>
                    <div class="mt-[16px] flex justify-center">
                        <el-pagination v-model:current-page="goodsTableData.page" v-model:page-size="goodsTableData.limit" layout="prev, pager, next" background :total="goodsTableData.total" @size-change="loadGoodsList()" @current-change="loadGoodsList" />
                    </div>
                </div>
                <div class="min-h-[300px]" v-if="!goodsTableData.data.length && !goodsTableData.loading">
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { getCollectList, cancelCollect} from '@/addon/mall/api/goods'
import { useRouter } from 'vue-router'

const router = useRouter()
// 获取会员列表
const goodsTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})
const loadGoodsList = (page: number = 1) => {
    goodsTableData.loading = true
    goodsTableData.page = page

    getCollectList({
        page: goodsTableData.page,
        limit: goodsTableData.limit,
    }).then(res => {
        goodsTableData.loading = false
        goodsTableData.data = res.data.data
        goodsTableData.total = res.data.total
    }).catch(() => {
        goodsTableData.loading = false
    })
}
loadGoodsList()
// 商品详情
const toGoodsDetail = (goods_id:number) => {
	router.push(`/goods/detail?id=${goods_id}`)
}
// 商品取消收藏
const cancelCollectGoods= (goods_id:number)=>{
    cancelCollect({goods_ids: [goods_id]}).then(res => {
        loadGoodsList()
    })
}

</script>

<style lang="scss" scoped>
.products{
    grid-row-gap: 16px;
    grid-column-gap: 16px;
    display: grid;
    grid-template-columns: repeat(4,1fr);
    grid-template-rows: auto;
    position: relative;
}
.goods-item:hover{
    .collect{
        display: block;
    }
}
</style>
