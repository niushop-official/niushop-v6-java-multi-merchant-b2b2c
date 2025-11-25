<template>
    <el-dialog v-model="showDialog" title="添加到店铺" width="700px" class="diy-dialog-wrap" :destroy-on-close="true">
        <div>
            <div class="flex">
                <el-image class="w-[64px] h-[64px] mr-[8px]" :src="img('')" fit="cover">
                    <template #error>
                        <img src="@/addon/mall/assets/goods_default.png" class="w-[64px] h-[64px]">
                    </template>
                </el-image>
                <div>
                    <div class="text-[14px] mb-[4px] font-bold using-hidden">REREJOY2022新款飞织镂空王妃鞋平跟+粗跟组合【5-7天发货】</div>
                    <div class="text-[14px]">
                        <span>品牌货源价：</span>
                        <span class="text-[#FF0000]">￥60-68</span>
                        <span class="text-[12px] text-[#999] ml-[22px]">销量 3979</span>
                    </div>
                    <div class="text-[14px]">
                        <span>建议零售价：</span>
                        <span class="text-[#FF0000]">￥89-99</span>
                    </div>
                </div>
            </div>
            <div class="text-[12px] mt-[4px] mb-[16px] text-[#999]">我的售价默认使用供应商建议售价，可修改</div>
            <div class="flex items-center mb-[12px]">
                <el-tooltip class="box-item"  effect="light"  placement="top">
                    <template #content>
                        <div class="max-w-[300px]">如选择同步展示全网销量评价，则商品挑货上架后，商品详情页将展示该货源商品在平台全网的销量评价信息；如选择展示本店销量评价，则商品挑货上架后，商品详情页仅展示该货源商品在本店的销量评价信息；如需调整店铺设置，可前往「品牌货源-我的-销量评价」进行设置。</div>
                    </template>
                    <div class="w-[120px] text-right">销量评价展示<span class="nc-iconfont nc-icon-tanhaoV6xx-1 !text-[14px] ml-[2px]"></span> ：</div>
                </el-tooltip>
                <el-radio-group v-model="formData.type" class="ml-4">
                    <el-radio :label="1">与店铺设置保持一致</el-radio>
                    <el-radio :label="2">全网销量评价</el-radio>
                    <el-radio :label="3">全店销量评价</el-radio>
                </el-radio-group>
            </div>
            <div class="flex items-center mb-[12px]">
                <span class="w-[120px] text-right">批量修改售价：</span>
                <el-radio-group v-model="formData.type" class="ml-4">
                    <el-radio :label="1">直接修改</el-radio>
                    <el-radio :label="2">根据货源价计算</el-radio>
                </el-radio-group>
            </div>
            <div class="flex items-center mb-[16px]">
                <span class="ml-[136px]">我的统一售价：</span>
                <el-input class="!w-[120px]" size="mini" v-model="formData.price"  placeholder="请输入" />
                <span class="ml-[4px]">元</span>
            </div>
        </div>
        <el-table :data="tableData" height="350" >
            <el-table-column prop="date" label="型号" />
            <el-table-column prop="name" label="货源价"  />
            <el-table-column prop="address" label="控价区间" />
            <el-table-column prop="address" label="我的售价">
                <template #default="{ row }">
                    <div class="flex items-center">
                        <el-input size="mini" v-model="row.price"  placeholder="请输入" />
                        <span class="ml-[4px]">元</span>
                    </div>
                </template>
            </el-table-column>
        </el-table>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">取消</el-button>
                <el-button type="primary" :loading="loading" @click="">仅添加暂不上架</el-button>
                <el-button type="primary" :loading="loading" @click="">立即上架</el-button>
            </span>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { img } from '@/utils/common'
const showDialog = ref(false)
const loading = ref(false)

const formData = reactive({
    type: 1,
    price: ''

})
const tableData = reactive([
    
])
</script>
<style lang="scss" scoped></style>