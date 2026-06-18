<template>
    <el-dialog v-model="showDialog" :title="t('发货内容')" width="700px" :destroy-on-close="true">
        <div class="delivery-content-container" v-loading="loading">
            <div class="delivery-content" v-if="formData && formData.order_delivery_content">
                {{ formData.order_delivery_content }}
            </div>
            <div class="no-content" v-else>
                {{ t('暂无发货内容') }}
            </div>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="showDialog = false">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'

const showDialog = ref(false)
const loading = ref(false)

const initialFormData = {
    order_delivery_content: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const setFormData = (data: any = null) => {
    loading.value = true
    Object.assign(formData, initialFormData)

    if (data) {
        Object.keys(formData).forEach((key: string) => {
            if (data[key] != undefined) formData[key] = data[key]
        })
    }

    loading.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped>
/* 发货内容弹框样式 */
.delivery-content-container {
    max-height: 400px;
    overflow-y: auto;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
    line-height: 1.6;
}

.delivery-content {
    white-space: pre-wrap;
    word-break: break-all;
}

.no-content {
    text-align: center;
    color: #999;
    padding: 40px 0;
}

/* 滚动条样式 */
.delivery-content-container::-webkit-scrollbar {
    width: 8px;
}

.delivery-content-container::-webkit-scrollbar-track {
    background: #f1f1f1;
    border-radius: 4px;
}

.delivery-content-container::-webkit-scrollbar-thumb {
    background: #c1c1c1;
    border-radius: 4px;
}

.delivery-content-container::-webkit-scrollbar-thumb:hover {
    background: #a8a8a8;
}
</style>