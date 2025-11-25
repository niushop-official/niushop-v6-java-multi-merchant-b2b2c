<template>
    <div  class="w-full">
        <div class="main-container">
            <el-breadcrumb class="my-[20px] !leading-[18px]" :separator-icon="ArrowRight">
				<el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/')">首页</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]"  @click="router.push('/member/merchant_settled')">商户入驻</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="text-[#666]">申请记录</span>
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div class="min-h-[300px]" v-loading="recordList.loading">
                <div v-if="recordList.data.length">
                    <div>
                        <div class="px-[30px] pb-[20px] mb-[20px] bg-[#fff] rounded-[var(--rounded-big)]" v-for="(item,index) in recordList.data">
                            <div class="h-[78px] border-b-[1px] border-dashed border-[#ddd] mb-[30px] flex items-center justify-between">
                                <div>
                                    <span class="iconfont icon-shenhezhong !text-[#0095ff] mr-[6px]"></span>
                                    <span class="text-[16px] text-[#333] font-600 mr-[20px]">{{item.status_name}}</span>
                                    <span class="text-primary text-[14px] " v-if="item.status == -1">{{item.refuse_reason}}</span>
                                </div>
                                <div class="cursor-pointer text-[14px] text-primary" v-if="item.status == -1" @click="router.push({path:'/member/merchant_settled',query:{apply_id:item.apply_id}})">去修改</div>
                            </div>
                            <div class="flex flex-wrap">
                                <div class="text-[14px] leading-[18px] text-[#333] w-[560px]">
                                    <div class="mb-[20px]">商户名称：{{item.site_name}}</div>
                                     <div class="mb-[20px]" v-if="item.account">商户账号：{{item.account}}</div>
                                    <div class="mb-[20px]">商户分类：{{item.category_name}}</div>
                                    <div class="mb-[20px]">商户类型：{{item.group_name}}</div>
                                    <div class="mb-[20px]">申请时间：{{item.create_time}}</div>
                                </div>
                                <div class="w-[1px] h-[124px] bg-[#eee]"></div>
                                <div class="text-[14px] leading-[18px] text-[#333] w-[549px] ml-[30px]">
                                    <div class="mb-[20px]">商户姓名：{{item.user_name}}</div>
                                    <div class="mb-[20px]">联系电话：{{item.user_mobile }}</div>
                                    <div class="flex flex-wrap">
                                        <div>资质证照：</div>
                                        <div class="flex flex-wrap w-[470px]">
                                            <el-image style="width: 66px; height: 66px" class="mr-[10px] mb-[10px]" :src="img(subItem)" :preview-src-list="[img(subItem)]" :initial-index="4" fit="cover" v-for="(subItem,subIndex) in item.business_license.split(',')" />
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mt-[20px] flex justify-center">
                        <el-pagination background  small hide-on-single-page v-model:current-page="recordList.page" v-model:page-size="recordList.limit" layout="prev, pager, next" :total="recordList.total" @size-change="getShopApplyListFn()" @current-change="getShopApplyListFn" />
                    </div>
                </div>
                <div class="bg-[#fff]" v-else>
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { getShopApplyList } from '@/addon/mall/api/merchant'
import { ArrowRight } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const recordList = reactive({
    page: 1,
	limit: 10,
	total: 0,
	loading: true,
	data: []
})
const getShopApplyListFn = (page = 1) => {
	recordList.page = page
	recordList.loading = true
	getShopApplyList({
		page: recordList.page,
        limit: recordList.limit
	}).then((res:any) =>{
		recordList.data = res.data.data
		recordList.total = res.data.total
		recordList.loading = false
	})
}
getShopApplyListFn()
</script>

<style scoped>

</style>