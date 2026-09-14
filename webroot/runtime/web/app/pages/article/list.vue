<template>
    <div class="w-full pt-6">
        <div class="main-container flex justify-between items-start">
            <div class="w-[860px] bg-[#fff] rounded-[16px] flex flex-col self-stretch overflow-hidden">
                <div class="flex justify-between items-start px-[20px] pt-[20px]">
                   <div class="flex-1 flex flex-wrap" :class="{'h-[34px] overflow-hidden':!isActive}">
                        <div class="text-[14px] cursor-pointer text-[#666] mb-[20px]" :class="{'!text-[var(--el-color-primary)]': articleTableData.searchParam.category_id == ''}" @click="handleClick('')">全部</div>
                        <template v-for="(item,index) in categoryList" :key="index">
                            <div class="text-[14px] cursor-pointer text-[#666] ml-[40px] mb-[20px]" :class="{'!text-[var(--el-color-primary)]': articleTableData.searchParam.category_id == item.category_id}" @click="handleClick(item.category_id)">{{ item.name }}</div>
                        </template>
                   </div>
                   <div v-if="categoryList.length>8" class="flex items-center ml-[20px] !text-[14px] text-[#666] cursor-pointer" @click="handleActive">
                        <span class="mr-[10px]" >{{ isActive? '收起':'更多'}}</span>
                        <el-icon v-if="isActive" color="#666" :size="14"><ArrowUp /></el-icon>
                        <el-icon v-else color="#666" :size="14"><ArrowDown /></el-icon>
                   </div>
                </div>
                <div class="flex-1 p-[20px] pt-[10px] border-0 border-t-[1px] border-solid border-[#eee]">
                    <div v-if="articleTableData.data.length">
                        <template v-for="(item,index) in articleTableData.data" :key="index">
                            <div class="py-[20px] flex cursor-pointer" @click="toDetail(item.id)">
                                <div class="w-[200px] h-[132px] rounded-[12px] overflow-hidden">
                                    <el-image class="w-[200px] h-[132px]" :src="img(item.image ? item.image : '')" fit="cover" />
                                </div>
                                <div class="flex-1 flex flex-col justify-between ml-[20px]">
                                    <div>
                                        <h1 class="text-[16px] text-[#333] mb-[20px] hover:text-[var(--el-color-primary)]">{{ item.title }}</h1>
                                        <p class="text-[14px] text-[#666] leading-[22px] multi-hidden max-h-[44px] overflow-hidden">{{ item.intro }}</p>
                                    </div>
                                    <div class="text-[#999] !text-[14px] flex items-center">
                                        <span class="iconfont icon-shijian_o !text-[20px]"></span>
                                        <span class="ml-[5px]">{{ item.update_time }}</span>
                                        <span class="iconfont icon-yanjing_kai !text-[20px] ml-[20px]"></span>
                                        <span class="ml-[5px]">{{ parseInt(item.visit + item.visit_virtual) }}</span>
                                    </div>
                                </div>
                            </div>
                        </template>
                        <div class="flex justify-center">
                            <el-pagination v-model:current-page="articleTableData.page" v-model:page-size="articleTableData.limit" layout=" prev, pager, next" :total="articleTableData.total" background  hide-on-single-page small @size-change="loadArticleList()" @current-change="loadArticleList" />
                        </div>
                    </div>
                    <div v-else-if="!articleTableData.loading" class="h-full w-full flex items-center justify-center">
                        <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')" />
                    </div>
                </div>
            </div>
            <div class="w-[320px] ml-[20px] p-[20px] rounded-[16px] box-border bg-[#fff]">
                <div class="flex items-center justify-between mb-[20px]">
                    <div class="flex items-center">
                        <img src="@/assets/images/new.png" class="w-[18px] h-[18px] mr-[6px]" />
                        <h1 class="text-[16px] text-[#333] font-500 ">热门资讯</h1>
                    </div>
                    <div class="text-[14px] text-[#999]">
                        <span class="iconfont icon-shuaxinV6xx !text-[14px] mr-[4px]"></span>
                        <span >换一换</span>
                    </div>
                </div>
                <template v-for="(item, index) in articleHotList" :key="index">
                    <div class="cursor-pointer flex items-center justify-between" :class="{'mb-[14px]': (index + 1) != articleHotList.length }"  @click="toDetail(item.id)">
                        <h3 class="text-[14px] leading-[22px] flex items-center hover:text-[var(--el-color-primary)] whitespace-nowrap overflow-hidden text-ellipsis">
                            <span class="w-[6px] h-[6px] bg-[#D9D9D9] flex-shrink-0 mr-[6px]"></span>
                            <span class="text-[#333] max-h-[44px] using-hidden hover:text-[var(--el-color-primary)] flex-1">{{ item.title }}</span>
                        </h3>
                        <span class="text-[#999] text-[14px] flex-shrink-0 ml-[6px]">{{ item.create_time.split(" ")[0] }}</span>
                    </div>
                </template>
                <el-empty v-if="!articleHotList.length&&!articleHotLoading" description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')" />
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from  'vue'
import { useRouter } from 'vue-router'
import { getArticleCategory,getArticleList,getArticleHot } from '@/app/api/article'

let router = useRouter()
// 文章栏目
let categoryList = ref([])
const getArticleCategoryFn = () =>{
    getArticleCategory().then(res =>{
        categoryList.value = res.data
    })
}
getArticleCategoryFn()

const articleTableData = reactive({
    page: 1,
	limit: 10,
	total: 0,
	loading: true,
	data: [],
	searchParam: {
		title:'',
        category_id:''
	}
})
// 文章列表
const loadArticleList = (page = 1) => {
	articleTableData.page = page
	articleTableData.loading = true
	getArticleList({
		page: articleTableData.page,
		limit: articleTableData.limit,
        ...articleTableData.searchParam
	}).then(res =>{
        articleTableData.total = res.data.total
		articleTableData.data = res.data.data
        articleTableData.loading = false
	})
}
loadArticleList()
//点击切换更多/收起
const isActive= ref(false)
const handleActive = () =>{
    isActive.value = !isActive.value
}
// 切换文章栏目
const handleClick = (val) =>{
    articleTableData.searchParam.category_id = val
    loadArticleList()
}
// 文章详情
const toDetail = (id) =>{
    router.push({path:'/article/detail',query:{id}})
}
//获取热门推荐
const articleHotList = ref([])
const articleHotLoading = ref(true)
const getArticleHotFn = () =>{
    getArticleHot({limit:3}).then(res =>{
        articleHotList.value = res.data
        articleHotLoading.value = false
    }).catch(() =>{
        articleHotLoading.value = false
    })
}
getArticleHotFn()
</script>

<style lang="scss" scoped>
/* 多行超出隐藏 */
.multi-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
</style>