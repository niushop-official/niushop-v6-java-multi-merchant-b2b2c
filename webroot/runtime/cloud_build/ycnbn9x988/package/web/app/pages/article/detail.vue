<template>
    <div class="w-ful min-h-[300px]">
        <div class="main-container">
            <el-breadcrumb class="my-[20px]" :separator-icon="ArrowRight">
				<el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/')">首页</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item>
                    <span class="cursor-pointer text-[#333]" @click="router.push('/article/list')">资讯信息</span>
                </el-breadcrumb-item>
                <el-breadcrumb-item v-if=" articleInfo.title">
                    <span class="text-[#666]">{{ articleInfo.title}}</span>
                </el-breadcrumb-item>
            </el-breadcrumb>
            <div class="py-[50px] px-[60px] bg-[#fff] rounded-[16px]" v-if="Object.keys(articleInfo).length">
                <div class="text-[24px] text-[#333] text-center mb-[30px]">{{ articleInfo.title}}</div>
                <div class="text-[#999] text-[14px] flex items-center justify-center mb-[40px]">
                    <span class="iconfont icon-geren !text-[20px]" v-if="articleInfo.author"></span>
                    <span class="ml-[5px]">{{ articleInfo.author }}</span>
                    <span class="iconfont icon-shijian_o !text-[20px] ml-[20px]"></span>
                    <span class="ml-[5px]">{{ articleInfo.update_time }}</span>
                    <span class="iconfont icon-yanjing_kai !text-[20px] ml-[20px]" v-if="articleInfo.visit || articleInfo.visit_virtual"></span>
                    <span class="ml-[5px]">{{ parseInt(articleInfo.visit + articleInfo.visit_virtual) }}</span>
                </div>
                <div v-html="articleInfo.content" v-if="articleInfo.content"></div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref } from  'vue'
import { ArrowRight } from '@element-plus/icons-vue'
import { useRouter,useRoute } from 'vue-router'
import { getArticleInfo,incVisit } from '@/app/api/article'

const route = useRoute()
const router = useRouter()

let id = route.query.id
const articleInfo = ref<any>({})
const getArticleInfoFn = (id) =>{
    getArticleInfo(id).then(res=>{
        if(res.data) {
            articleInfo.value = res.data
            // 做居中兼容
            articleInfo.value.content = articleInfo.value.content.split('text-align: center;').join('text-align: center;text-align: -webkit-center;')
            incVisit(id).then(res=>{})
        }
    })
}
getArticleInfoFn(id)
</script>

<style lang="scss" scoped>

</style>