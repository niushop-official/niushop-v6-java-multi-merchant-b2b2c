<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="full"  v-loading="browseData.loading">
            <div>
                <div class="text-[18px] text-[#333] font-500 leading-[18px] ml-[10px] mb-[27px]">{{ t('title') }}({{ browseData.total }})</div>
                <!-- <div class="bg-[#FFF4F4] text-primary text-[12px] h-[32px] leading-[32px] pl-[10px] mb-[20px] rounded-[6px]">共{{ browseData.total }}条记录,{{t('remark')}}</div> -->
                 <div v-if="browseData.total && !browseData.loading">
                    <div>
                        <div v-for="(item, index) in browseData.data" class="mb-30px" :key="index">
                            <div class="flex items-center ml-[10px] mb-[18px]">
                                <span class="iconfont icon-riliV6xx text-[16px] mr-[6px]"></span>
                                <span class="text-[#333] text-[14px]">{{ item.date }}</span>
                            </div>
                            <div class="goods-list">
                                <div class="w-[228px] h-[342px] cursor-pointer  border-[1px] border-solid	border-[#eee] rounded-[var(--rounded-big)]" v-for="(goodsItem, goodsIndex) in item.list" :key="goodsIndex" @click="toDetail(goodsItem)">
                                    <div class="flex items-center justify-center">
                                        <el-image class="w-[227px] h-[227px] rounded-t-[var(--rounded-big)]" :class="{'opacity-25':goodsItem.goods.status != 1 || goodsItem.goods.delete_time > 0}" :src="img(goodsItem.goods_cover_thumb_mid ? goodsItem.goods_cover_thumb_mid : '')" fit="cover">
                                            <template #error>
                                                <img src="@/assets/images/goods_default.png" class="w-[227px] h-[227px] rounded-t-[var(--rounded-big)]">
                                            </template>
                                        </el-image>
                                    </div>
                                    <div  class="pt-[15px] px-[20px] pb-[20px] h-[112px] flex flex-col justify-between">
                                        <div class="text-[14px] text-[#333]  multi-hidden leading-[20px]" :class="{'text-[#999]':goodsItem.goods.status != 1 || goodsItem.goods.delete_time > 0}">{{ goodsItem.goods_name }}</div>
                                        <div class="flex justify-between">
                                            <div class="text-[#333] text-[14px] oppoSans-R" v-if="goodsItem.goods.delete_time > 0">
                                                {{t('delete')}}
                                            </div>
                                            <div class="text-[#333] text-[14px] oppoSans-R" v-else-if="goodsItem.goods.status != 1">
                                                {{t('invalid')}}
                                            </div>
                                            <div class="text-primary flex items-baseline" v-else>
                                                <span class="price-font font-600 text-[14px]">¥</span>
                                                <span class="price-font text-[20px] font-600">{{parseFloat(goodsItem.price).toFixed(2)}}</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="browseData.page" v-model:page-size="browseData.limit" layout="total, sizes, prev, pager, next, jumper" :total="browseData.total"
                            @size-change="loadBrowseList()" @current-change="loadBrowseList" />
                    </div>
                </div>
                <div class="min-h-[300px]" v-if="!browseData.total && !browseData.loading">
                    <el-empty :description="t('empty')" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { getBrowseList } from '@/addon/mall/api/member'
import { useRouter } from 'vue-router'
const router = useRouter()
const browseData = reactive({
    total: 0,
    page: 1,
    limit: 10,
    loading: true,
    data: []
})
const loadBrowseList = (page: number = 1) => {
    browseData.page = page
    browseData.loading = true
    getBrowseList({
        page: browseData.page,
        limit: browseData.limit,
    }).then((res: any) => {
        browseData.loading = false
        browseData.total = res.data.total
        let newArr = res.data.data
         // 按日期分组
         const groupedData = newArr.reduce((acc: any, item: any) => {
            const date = item.browse_time.split(' ')[0]; // 提取日期部分
            if (!acc[date]) {
                acc[date] = [];
            }
            acc[date].push(item);
            return acc;
        }, {});
        // 转换为所需格式
        browseData.data = Object.keys(groupedData).map(date => ({
            date: date,
            list: groupedData[date]
        }));
        
    }).catch(() => {
        browseData.loading = false
    })
}
loadBrowseList()

const toDetail = (item:any) => {
  if(item.goods.delete_time > 0 || item.goods.status != 1) return
  router.push(`/goods/detail?id=${item.goods_id}`)
}
</script>

<style lang="scss" scoped>
.goods-list{
  grid-row-gap: 16px;
  grid-column-gap: 16px;
  display: grid;
  grid-template-columns: repeat(4,1fr);
  grid-template-rows: auto;
  position: relative;
}
</style>