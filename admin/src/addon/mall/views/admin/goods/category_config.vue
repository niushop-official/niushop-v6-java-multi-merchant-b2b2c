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
                        <el-form-item :label="t('categoryType')">
                            <el-radio-group class="mx-[10px]" v-model="formData.level" @change="formData.template='style-1'">
                                <el-radio :label="1">{{ t('categorystyleOne') }}</el-radio>
                                <el-radio :label="2">{{ t('categorystyleTwo') }}</el-radio>
                                <el-radio :label="3">{{ t('categorystyleThree') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('categoryTemplate')">
                            <template v-for="(item,index) in config['level_'+formData.level]" :key="index">
                                <div
                                    :class="['w-[150px] border-[1px] border-[#ededed] border-solid overflow-hidden text-[#ededed] rounded-[4px] mr-[20px] relative', formData.template ===item.template ? 'border-color text-color' : '']"
                                    @click="levelChange(item.template)">
                                    <img class="w-[100%]" :src="img(item.preview)" fit-object="contain" />
                                    <span class="iconfont iconicon-selected absolute right-0 bottom-[-8px]"></span>
                                </div>
                            </template>
                        </el-form-item>
                    </el-card>
                    <el-card class="box-card !border-none" shadow="never">
                        <h3 class="!text-[14px] pl-[15px]">{{ t('pageSettings') }}</h3>
                        <el-form-item :label="t('pageTitle')" prop="page_title">
                            <el-input v-model="formData.page_title" clearable :placeholder="t('pageTitlePlaceholder')" class="input-width" maxlength="10" show-word-limit />
                        </el-form-item>
                        <el-form-item :label="t('searchControl')">
                            <el-radio-group class="mx-[10px]" v-model="formData.search.control">
                                <el-radio :label="1">{{ t('open') }}</el-radio>
                                <el-radio :label="0">{{ t('close') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item v-if="formData.search.control" :label="t('searchTitle')" prop="search.title">
                            <el-input v-model="formData.search.title" clearable :placeholder="t('searchTitlePlaceholder')" class="input-width" maxlength="12" show-word-limit />
                        </el-form-item>
                        <template v-if="!((formData.level===2&&formData.template == 'style-1')||(formData.level===3&&formData.template == 'style-1'))">
                            <el-form-item :label="t('cartControl')">
                                <el-radio-group class="mx-[10px]" v-model="formData.cart.control">
                                    <el-radio :label="1">{{ t('show') }}</el-radio>
                                    <el-radio :label="0">{{ t('hidden') }}</el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <template v-if="formData.cart.control">
                                <el-form-item :label="t('cartStyle')" class="carStyle">
                                    <div class="flex items-center">
                                        <div :class="['flex items-center justify-center w-[65px] h-[65px] border-0 border-color rounded-[4px] border-solid box-border cursor-pointer mr-[15px]', formData.cart.style === 'style-1' ? '!border-[1px]' : '']" @click="carStyleClick(1)">
                                            <div class="text-[#fff] bg-color h-[20px] text-[12px] px-[10px] leading-[20px] rounded-[10px]">
                                                {{ formData.cart.text }}
                                            </div>
                                        </div>
                                        <div :class="['flex items-center justify-center w-[65px] h-[65px] border-0 border-color rounded-[4px] border-solid box-border cursor-pointer mr-[15px]', formData.cart.style === 'style-3' ? '!border-[1px]' : '']" @click="carStyleClick(3)">
                                            <span class="text-color iconfont icongouwuche !text-[30px]"></span>
                                        </div>
                                        <div :class="['flex items-center justify-center w-[65px] h-[65px] border-0 border-color rounded-[4px] border-solid box-border cursor-pointer mr-[15px]', formData.cart.style === 'style-4' ? '!border-[1px]' : '']" @click="carStyleClick(4)">
                                            <div class="text-[#fff] bg-color h-[30px] w-[30px] leading-[30px] rounded-[30px] text-center">
                                                <span class=" iconfont icongouwuche !text-[20px]"></span>
                                            </div>

                                        </div>
                                    </div>
                                </el-form-item>
                                <el-form-item v-if="formData.cart.style === 'style-1'" prop="cart.text">
                                    <el-input v-model.trim="formData.cart.text" clearable :placeholder="t('cartTextPlaceholder')" class="input-width" maxlength="3" show-word-limit />
                                </el-form-item>
                                <el-form-item :label="t('cartEvent')">
                                    <el-radio-group class="mx-[10px]" v-model="formData.cart.event">
                                        <el-radio label="detail">{{ t('detail') }}</el-radio>
                                        <el-radio label="cart">{{ t('cart') }}</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                            </template>
                        </template>
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
import { getCategoryConfig, setCategoryConfig } from '@/app/lang/zh-cn/api/admin/goods'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
interface formDataType {
    level: string|number
    template: string
    page_title: string
    search: {
        title: string
        control: number|string
    }
    sort: string
    cart: {
        control: number
        style: string
        text: string
        event: string
    }
}

const formData = ref<formDataType|any>({})
const loading = ref(false)
const rules = computed(() => {
    return {
        page_title: [
            { required: true, message: t('pageTitlePlaceholder'), trigger: 'blur' }
        ],
        'search.title': [
            { required: true, message: t('searchTitlePlaceholder'), trigger: 'blur' }
        ],
        sort: [
            { required: true, message: t('sortPlaceholder'), trigger: 'change' }
        ],
        'cart.text': [
            { required: true, message: t('cartTextPlaceholder'), trigger: 'blur' }
        ]
    }
})

interface configType {
    level_1: {
        template: string
        preview: string
    }[]
    level_2: {
        template: string
        preview: string
    }[]
}
const config = reactive<configType|any>({
    level_1: [
        {
            template: 'style-1',
            preview: 'addon/mall/category_style1_1.png'
        }
    ],
    level_2: [
        {
            template: 'style-1',
            preview: 'addon/mall/category_style2_1.png'
        },
        {
            template: 'style-2',
            preview: 'addon/mall/category_style2_2.png'
        }
    ],
    level_3: [
        {
            template: 'style-1',
            preview: 'addon/mall/category_style3_1.png'
        }
    ]
})
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
const carStyleClick = (value: any) => {
    formData.value.cart.style = 'style-' + value
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
