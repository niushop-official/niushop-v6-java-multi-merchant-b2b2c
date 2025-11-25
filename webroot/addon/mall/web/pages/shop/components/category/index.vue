<template>
    <div class="w-full mt-[20px] bg-[#fff] p-[30px] rounded-[var(--rounded-mid)]">
        <el-collapse v-model="activeNames">
            <template v-for="(item,index) in categoryList" :key="index">
                <el-collapse-item  :name="item.category_id" class="mb-[24px] leading-[16px]" v-if="item.child_list&&item.child_list.length" >
                    <template #title>
                        <div :class="{'!text-[var(--el-color-primary)]':item.category_id.toString() === goods_category}" @click.stop="categoryClick(item.category_id)">{{item.category_name}}</div>
                    </template>
                    <div class="mt-[24px] text-[12px] leading-[14px] cursor-pointer text-[#666]" v-for="(subItem,subIndex) in item.child_list"  :key="subIndex" :class="{'!text-[var(--el-color-primary)]':subItem.category_id.toString() === goods_category}" @click="categoryClick(subItem.category_id)">{{subItem.category_name}}</div>
                </el-collapse-item>
                <div v-else class="mb-[24px] text-[14px] leading-[16px] cursor-pointer" :class="{'!text-[var(--el-color-primary)]':item.category_id.toString() === goods_category}" @click="categoryClick(item.category_id)">{{item.category_name}}</div>
            </template>
        </el-collapse>
    </div>
</template>
<script lang="ts" setup>
import { ref,reactive } from 'vue'
import { getStoreCategory } from '@/app/api/basic'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const goods_category = ref('')
const emit = defineEmits(['categoryClick'])
if(route.query.goods_category) goods_category.value = route.query.goods_category.toString()
const activeNames = ref([])
//获取分类列表
const categoryList = ref([])
const getCategoryTreeFn = () => {
    getStoreCategory({site_id:route.query.site_id}).then(res => {
        const obj = {category_id:'',category_name:'全部'}
        categoryList.value.push(obj)
        categoryList.value = categoryList.value.concat(res.data);
        //获取分类id是否是一级
        categoryList.value.forEach(item =>{
            if(item.child_list){
                activeNames.value.push(item.category_id)
            }
        })
    })
}
getCategoryTreeFn()

//分类点击
const categoryClick = (id:any)=>{
  goods_category.value = id.toString()
  emit('categoryClick',goods_category.value)
}

</script>
<style lang="scss" scoped>
:deep(.el-collapse){
    border:none !important;
}
:deep(.el-collapse-item__wrap){
    border:none !important;
}
:deep(.el-collapse-item__header){
    --el-collapse-header-height: auto !important;
    --el-collapse-header-font-size: 14px;
    border:none !important;
}
:deep(.el-collapse-item__content){
    padding-bottom:0 !important;
}
</style>