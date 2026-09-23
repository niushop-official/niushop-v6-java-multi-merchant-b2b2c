<template>
    <el-dialog v-model="showDialog" title="查看地图" width="800px" :close-on-click-modal="true" :destroy-on-close="true" >
        <el-form :model="formData" label-width="0px" ref="formRef" class="page-form mb-[30px]">
            <el-form-item>
                <div>{{ formData.taker_address }}</div>
            </el-form-item>
            <el-form-item >
                <!-- 地图display：none会渲染报错 -->
                <div class="h-auto overflow-hidden">
                    <map-selector
                            ref="mapSelectorRef"
                            :longitude="formData.taker_longitude"
                            :latitude="formData.taker_latitude"
                            :zoom="14"
                            :width="800"
                            :height="500"
                            :disabledClickMarker="true"
                        />
                </div>
                <div v-if="mapMessage" class="text-[#f52222] text-[12px] line-height-[15px]">{{ mapMessage }}</div>

            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button type="primary" @click="showDialog = false">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { t } from '@/lang'
import { cloneDeep } from 'lodash-es'
import MapSelector from '@/components/map-selector/index.vue'

const showDialog = ref(false)
/**
 * 表单数据
 */
let formData: Record<string, any> = reactive({
    taker_latitude: 0,
    taker_longitude: 0,
    taker_address: ''
})

// 初始化
const setFormData = async (data: any) => {
    formData = cloneDeep(data)
    showDialog.value = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>

</style>