<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="full" v-loading="shopTableData.loading">
            <div>
                <div class="mb-[27px] text-[18px] text-[#333] font-500">全部店铺({{ shopTableData.total }})</div>
                <div v-if="shopTableData.data.length && !shopTableData.loading">
                    <div class="products">
                        <template v-for="(item,index) in shopTableData.data" :key="index">
                            <div class="w-[228px] flex flex-col items-center box-border border-[1px] border-solid border-[#eee] pb-[20px] bg-[#fff] rounded-[var(--rounded-big)] cursor-pointer" @click="toShopDetail(item.site.site_id)">
                                <el-image class="w-[228px] h-[228px] rounded-t-[var(--rounded-big)]" :src="img(item.site.logo ? item.site.logo : '')" fit="cover">
                                    <template #error>
                                        <img :src="img(`static/resource/images/site/back0${ Math.floor(Math.random() * 4) + 1}.jpg`)" class="w-[228px] h-[228px] rounded-t-[var(--rounded-big)]">
                                    </template>
                                </el-image>
                                <div class="w-[80px] h-[80px] mt-[-36px] mb-[18px] box-border border-[1px] border-solid border-[#eee] rounded-full">
                                    <el-image class="w-[80px] h-[80px] rounded-full" :src="img(item.site.front_end_logo ? item.site.front_end_logo : '')" fit="cover">
                                        <template #error>
                                            <img src="@/assets/images/shop_default.png" class="w-[80px] h-[80px] rounded-full">
                                        </template>
                                    </el-image>
                                </div>
                                <div class="text-[#333] text-[16px] truncate flex-center w-[212px] mb-[14px]">{{item.site.site_name}}</div>
                                <div class="flex justify-center">
                                    <el-button plain round class="!text-primary !border-primary oppoSans-M !text-[12px]"  @click.stop="cancelCollectShop(item.site.site_id,0)">取消关注</el-button>
                                </div>
                            </div>
                        </template>
                    </div>
                    <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="shopTableData.page" v-model:page-size="shopTableData.limit" layout="prev, pager, next" background :total="shopTableData.total" @size-change="loadShopList()" @current-change="loadShopList" />
                    </div>
                </div>
                <div class="min-h-[300px]" v-if="!shopTableData.data.length && !shopTableData.loading">
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { getShopFollowList , editShopCollect } from '@/app/api/basic'
import { useRouter } from 'vue-router'

const router = useRouter()

// 获取店铺列表
const shopTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})
const loadShopList = (page: number = 1) => {
    shopTableData.loading = true
    shopTableData.page = page

    getShopFollowList({
        page: shopTableData.page,
        limit: shopTableData.limit,
        addon: 'mall'
    }).then(res => {
        shopTableData.loading = false
        shopTableData.data = res.data.data
        shopTableData.total = res.data.total
    }).catch(() => {
        shopTableData.loading = false
    })
}
loadShopList()
// 店铺详情
const toShopDetail = (id:any) => {
	router.push(`/shop/detail?site_id=${id}`)
}
// 店铺取消关注
const cancelCollectShop = (id:any,is_follow:any) =>{
    editShopCollect({
		site_id: id,
		is_follow: is_follow,
		addon: 'mall'
	}).then(res => {
        loadShopList()
    })
}

</script>

<style lang="scss" scoped>
.products{
    grid-row-gap: 15px;
    grid-column-gap: 15px;
    display: grid;
    grid-template-columns: repeat(4,1fr);
    grid-template-rows: auto;
    position: relative;
}
</style>
