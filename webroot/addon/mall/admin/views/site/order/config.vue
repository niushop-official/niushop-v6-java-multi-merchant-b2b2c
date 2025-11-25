<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="95" ref="formRef" :rules="rules" class="page-form" v-loading="loading">
                <!-- 万能表单 -->
                <el-card class="box-card !border-none" shadow="never">
                    <el-form-item :label="t('diyForm')">
                        <el-select v-model="formData.form_id" :placeholder="t('diyFormPlaceholder')" clearable>
                            <el-option v-for="item in diyFormOptions" :key="item.form_id" :label="item.page_title" :value="item.form_id" />
                        </el-select>
                        <div class="ml-[10px]">
                            <span class="cursor-pointer text-primary mr-[10px]" @click="refreshDiyForm(true)">{{ t('refresh') }}</span>
                            <span class="cursor-pointer text-primary" @click="toDiyFormEvent">{{ t('addDiyForm') }}</span>
                        </div>
                    </el-form-item>
                </el-card>
            </el-form>

        </el-card>
        <div class="fixed-footer-wrap" v-if="!loading">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { getConfig, setConfig } from '@/addon/mall/api/site/order'
import { getDiyFormList } from '@/app/api/diy_form_site'
import { useRoute, useRouter} from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const formData = ref({
    form_id: ''
})
const loading = ref(false)
const getConfigFn = () => {
    loading.value = true
    getConfig().then(res => {
        formData.value = Object.assign(formData.value, res.data)
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getConfigFn()
const formRef = ref()

/** ***************** 万能表单-start *************************/
// 万能表单列表下拉框
const diyFormOptions = reactive([])
// 跳转到万能表单列表，添加表单
const toDiyFormEvent = () => {
    const url = router.resolve({
        path: '/site/diy_form/list'
    })
    window.open(url.href)
}

// 刷新万能表单
const refreshDiyForm = (bool = false) => {
    getDiyFormList({
        type: 'DIY_FROM_ORDER_PAYMENT',
        status: 1
    }).then((res) => {
        const data = res.data
        if (data) {
            diyFormOptions.splice(0, diyFormOptions.length, ...data)
            if (bool) {
                ElMessage({
                    message: t('refreshSuccess'),
                    type: 'success'
                })
            }
        }
    })
}

refreshDiyForm()
/** *****************万能表单-end *************************/

const onSave = async (formEl: any) => {
    await formEl.validate(async (valid: any) => {
        if (valid) {
            loading.value = true
            setConfig(formData.value).then(res => {
                getConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>
<style lang="scss" scoped></style>
