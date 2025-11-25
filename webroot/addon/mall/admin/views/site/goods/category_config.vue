<template>
    <div class="main-container" >
        <el-card class="box-card !border-none" shadow="never" >
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="170" ref="formRef" :rules="rules" class="page-form min-h-[300px]" v-loading="loading">
                <template v-if="Object.keys(formData).length">
                    <el-card class="box-card !border-none" shadow="never">
                        <h3 class="!text-[14px] pl-[15px]">{{ t('categoryTemplate') }}</h3>
                        <el-form-item :label="t('categoryTemplate')">
                            <template v-for="(item,index) in config" :key="index">
                                <div
                                    :class="['w-[150px] border-[1px] border-[#ededed] border-solid overflow-hidden text-[#ededed] rounded-[4px] mr-[20px] relative', formData.template ===item.template ? 'border-color text-color' : '']"
                                    @click="levelChange(item.template)">
                                    <img class="w-[100%]" :src="img(item.preview)" fit-object="contain" />
                                    <span class="iconfont iconicon-selected absolute right-0 bottom-[-8px]"></span>
                                </div>
                            </template>
                        </el-form-item>
                    </el-card>
                </template>
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
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { getCategoryConfig, setCategoryConfig } from '@/addon/mall/api/site/goods'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
interface formDataType {
    template: string
}

const formData = ref<formDataType|any>({})
const loading = ref(false)

const config = reactive<any>([{
    template: 'style-1',
    preview: 'addon/mall/shop_category_style1.jpg'
}])
const getCategoryConfigFn = () => {
    loading.value = true
    getCategoryConfig().then(res => {
        formData.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
getCategoryConfigFn()
const levelChange = (value: any) => {
    formData.value.template = value
}

const formRef = ref()
const onSave = async (formEl: any) => {
    await formEl.validate(async (valid:any) => {
        if (valid) {
            loading.value = true
            setCategoryConfig(formData.value).then(res => {
                getCategoryConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
.border-color {
    border-color: var(--el-color-primary);
}

.text-color {
    color: var(--el-color-primary);
}

.bg-color {
    background-color: var(--el-color-primary);
}

.carStyle {
    :deep(.el-form-item__label) {
        height: 50px;
        line-height: 50px;
    }
}
:deep(.el-tabs__item:hover) {
    border-bottom: 2px solid var(--el-color-primary);
}

:deep(.el-tabs__item) {
    padding: 0;
}

:deep(.el-tabs__item+.el-tabs__item) {
    margin-right: 20px;
    margin-left: 20px;
    // border-bottom: 2px solid var(--el-color-primary);
}

:deep(.el-tabs--top) {
    .el-tabs__active-bar {
        display: none;
    }

    .el-tabs__item.is-active {
        border-bottom: 2px solid var(--el-color-primary);
    }

    .el-tabs__item.is-top:nth-child(2) {
        margin-right: 20px;
    }

}

:deep(.el-step.is-vertical .el-step__icon.is-icon) {
    padding: 8px 0;
    height: 40px;
    background-color: #fff;
}

:deep(.el-step__title) {
    height: 40px;
    line-height: 40px !important;
}
</style>
