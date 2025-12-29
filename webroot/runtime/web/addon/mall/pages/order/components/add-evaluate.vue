<template>
    <el-dialog v-model="dialogEvaluateVisible" width="600px">
        <div class="min-h-[300px]">
            <div class="evaluate-form" v-loading="loading">
                <el-form :model="formData" label-width="90px"  ref="evaluateFormRef">
                    <template v-for="(item, index)  in evaluateInfo" :key="index">
                        <div class="flex mb-[20px]" :class="{'mt-[20px]' : index}">
                            <img class="w-[120px] h-[120px]"  :src="img(item.goods_image ? item.goods_image : '')" alt="">
                            <div class="ml-[10px] flex-1 flex flex-col justify-between">
                                <div>
                                    <div class="text-[14px] text-[#333] leading-[20px] h-[40px]">{{ item.goods_name }}</div>
                                    <div class="mt-[8px] text-[12px] text-[#999] leading-[16px] oppoSans-R" v-if="item.sku_name">规格：{{ item.sku_name }}</div>
                                </div>
                                <div class="mt-auto flex  justify-between w-[100%]">
                                    <span class="">￥{{ item.price }}</span>
                                    <span class="text-[12px] text-[#666]">x{{ item.num }}</span>
                                </div>
                            </div>
                        </div>
                        <el-form-item label="商品评分" >
                            <el-rate v-model="formData[index].scores" text-color="#FA6400" :colors="['#FA6400','#FA6400','#FA6400']" size="small"/>
                        </el-form-item>
                        <el-form-item label="评价内容" :prop="`${index}.content`" :rules="[{ required: true, message: '请输入评价内容', trigger: 'blur' }]">
                            <div class="relative w-[100%]">
                                <el-input v-model="formData[index].content" maxlength="500" placeholder="点击输入你对商品的评价吧!"  :autosize="true" resize="none" type="textarea"/>
                                <div class="absolute bottom-[12px] right-[16px] flex text-[12px] text-[#888] leading-[18px]">
                                    <span class="mr-[10px]">{{ formData[index].content ? formData[index].content.length :0 }}/500</span>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item>
                           <div>
                                <upload-file  v-model="formData[index].images" :limit="9"></upload-file>
                                <div class="text-[12px] text-[#999] mt-[8px]">最多可上传9张图片</div>
                            </div>
                        </el-form-item>
                     </template>
                     <el-form-item class="mt-[30px]">
                        <el-checkbox v-model="is_anonymous" :checked="false" label="匿名"  true-label="1" false-label="2" @change="handleChange"/>
                        <el-button class="ml-24px !w-[220px] !h-[48px] rounded-[4px] !bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]" type="primary" :loading="loading" @click="onSave(evaluateFormRef)">提交评价</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive} from 'vue'
import { getMallOrderDetail } from '@/addon/mall/api/order'
import { setEvaluate } from '@/addon/mall/api/goods'
import { cloneDeep } from 'lodash-es'

const loading = ref(false)
const dialogEvaluateVisible = ref(false) // 评价弹框
let formData = ref([])
const is_anonymous = ref('2')
// 是否匿名
const handleChange = (data)=>{
	is_anonymous.value= data
}
const emit = defineEmits(['complete'])
const evaluateFormRef = ref()
const siteId = ref(0)

const onSave = async (formEl) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
    if (valid) {
        formData.value.forEach(el=>{
            el.is_anonymous = is_anonymous.value
        })
        let data = cloneDeep(formData.value)
        data.forEach(el =>{
            el.images = el.images.split(',')
        })
        setEvaluate({ site_id : siteId.value,evaluate_array: data }).then(res => {
            dialogEvaluateVisible.value = false
            emit('complete')
        })
    }
  })
}

let evaluateInfo = ref([])
const setFormData = async (row) => {
    loading.value = true
    if (row) {
        const data = await (await getMallOrderDetail(row)).data
        console.log(data)
        siteId.value = data.stie_id;
        evaluateInfo.value = data.order_goods
        formData.value = []
        data.order_goods.forEach(el => {
            formData.value.push({
                    order_id: el.order_id,
                    order_goods_id: el.order_goods_id,
                    goods_id: el.goods_id,
                    site_id: el.site_id,
                    content: '',
                    images: '',
                    scores: 5
            })
        })
        console.log(formData.value)
    }
    loading.value = false
}
setFormData()
defineExpose({
    dialogEvaluateVisible,
    setFormData
})
</script>

<style lang="scss" scoped>
.evaluate-form :deep(.el-textarea__inner){
    height:120px !important;
}

</style>