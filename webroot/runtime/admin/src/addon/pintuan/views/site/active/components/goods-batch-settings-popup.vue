<template>
    <el-dialog v-model="showDialog" title="批量设置" width="700px" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" class="mt-[10px] page-form" v-loading="loading">
            <!-- 商品详情模板 -->
            <el-form-item label="商品详情模板">
                <el-select v-model="formData.diy_detail_id" placeholder="请选择商品详情模板" clearable>
                    <el-option v-for="item in detailTemplateOptions" :key="item.id" :label="item.page_title" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { ElMessage, FormInstance } from 'element-plus'
import { useRouter } from 'vue-router'
import { getGoodsInfoTemplate, goodsBatchSet } from '@/addon/pintuan/api/site/active'

const emit = defineEmits(['load'])
const showDialog = ref(false)
const loading = ref(false)
const router = useRouter()

/**
 * 表单数据
 */
const initialFormData = {
    diy_detail_id: ''
}

const formData: Record<string, any> = reactive({ ...initialFormData })
const formRef = ref<FormInstance>()

const pintuan_ids = ref([])
const show = (info: any) => {
    pintuan_ids.value = info.ids
    showDialog.value = true
}

const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = {
                pintuan_ids: pintuan_ids.value,
                field: 'diy_detail_id',
                value: formData.diy_detail_id
            }
            goodsBatchSet(data).then((res) => {
                emit('load')
                loading.value = false
                showDialog.value = false
            }).catch(err => {
                loading.value = false
            })
        }
    })
}

/** ***************** 商品详情模板-start *************************/
// 商品详情模板列表下拉框
const detailTemplateOptions = reactive([])

// 刷新微页面
const refreshDetailTemplate = (bool = false) => {
    getGoodsInfoTemplate({
        type: ['DIY_PINTUAN_GOODS_DETAIL', 'DIY_PINTUAN_ACTIVITY_GOODS_DETAIL']
    }).then((res) => {
        const data = res.data
        if (data) {
            detailTemplateOptions.splice(0, detailTemplateOptions.length, ...data)
            if (bool) {
                ElMessage({
                    message: t('refreshSuccess'),
                    type: 'success'
                })
            }
        }
    })
}

refreshDetailTemplate()
/** *****************商品详情模板-end *************************/

defineExpose({
    showDialog,
    show
})
</script>

<style lang="scss" scoped>
.el-menu-item {
    height:40px;
}
.input-width-short{
    width: 200px;
}
</style>
