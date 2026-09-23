<template>
    <el-dialog v-model="selectDialog" title="操作类型" width="600px" :close-on-click-modal="false">
        <div class="operation-container" v-loading="loading">
            <div v-for="(item, index) in operationList"  :key="index" class="operation-item">
                <div>
                    <div class="operation-title flex items-baseline">
                        <span class="mr-[4px]">{{ item.name }}</span>
                        <el-tooltip placement="bottom" effect="light"  v-if="item.type  == 'print_delivery'">
                            <template #content>
                                <div class="w-[300px]">支持商家单次勾选多笔订单（不超过100笔）进行在线下单发货并批量打印电子面单，提高发货效率。</div>
                            </template>
                            <span class="cursor-pointer"><el-icon color="#999"><QuestionFilled /></el-icon></span>
                        </el-tooltip>
                        <el-tooltip placement="bottom" effect="light" v-if="item.type  == 'delivery'">
                            <template #content>
                                <div>批量发货目前仅支持快递订单，暂不支持自提订单和同城配送订单。</div>
                                <div>批量发货按模板要求填写发货信息，运单号不可写成“E+22”的格式。</div>
                                <div>批量发货文件若上传失败，可点击【下载失败记录】，对症修改。</div>
                            </template>
                            <span class="cursor-pointer"><el-icon color="#999"><QuestionFilled /></el-icon></span>
                        </el-tooltip>
                        <el-tooltip placement="bottom" effect="light" v-if="item.type  == 'select_delivery'">
                             <template #content>
                                <div>仅针对无需物流的订单</div>
                                <div>暂不支持选择以下类型的订单：</div>
                                <div>维权订单、虚拟订单......</div>
                            </template>
                            <span class="cursor-pointer"><el-icon color="#999"><QuestionFilled /></el-icon></span>
                        </el-tooltip>
                    </div>
                    <div class="operation-desc">{{ item.desc }}</div>
                </div>
                <el-button v-if="item.type == 'print_delivery'" type="primary" @click="handleSelect(index)">批量打单发货</el-button>
                <el-button v-if="item.type  == 'delivery'" type="primary" @click="handleSelect(index)">批量导入发货</el-button>
                <el-button v-if="item.type  == 'select_delivery'" type="primary" @click="handleSelect(index)">批量选择发货</el-button>
            </div>
            <el-empty v-if="!loading && operationList.length === 0" description="暂无操作类型" />
        </div>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { getOrderBatchDeliveryType } from '@/app/lang/zh-cn/api/site/order'

const selectDialog = ref(false)
const loading = ref(false)

interface OperationType {
    type: string
    name: string
    desc: string
}

const operationList = ref<OperationType[]>([])

// 获取批量操作类型
const loadOperationTypes = async () => {
    loading.value = true
    try {
        const res = await getOrderBatchDeliveryType()
        // console.log('接口返回数据:', res)
        if (res.data && typeof res.data === 'object') {
            // 将对象转换为数组
            operationList.value = Object.keys(res.data).map(key => ({
                type: key,
                name: res.data[key].name,
                desc: res.data[key].desc
            }))
            // console.log('操作列表:', operationList.value)
        } else {
            // console.warn('接口返回数据格式不正确:', res)
        }
    } catch (error) {
        console.error('获取批量操作类型失败:', error)
    } finally {
        loading.value = false
    }
}

const emit = defineEmits(['select'])

const handleSelect = (index: number) => {
    selectDialog.value = false
    const selectedType = operationList.value[index]?.type
    emit('select', selectedType)
}

// 打开弹窗
const open = async () => {
    selectDialog.value = true
    // 打开弹窗时加载数据
    await loadOperationTypes()
}

// 组件挂载时加载数据
onMounted(() => {
    loadOperationTypes()
})

defineExpose({
    open
})
</script>

<style lang="scss" scoped>
.operation-container {
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.operation-item {
    padding: 20px 24px;
    border: 1px solid #e4e7ed;
    border-radius: 8px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.operation-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
}

.operation-desc {
    font-size: 13px;
    color: #909399;
    line-height: 1.5;
}
</style>
