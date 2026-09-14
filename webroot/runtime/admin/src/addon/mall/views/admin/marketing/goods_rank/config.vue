<template>
    <div>
        <!-- 榜单设置 -->
        <el-card class="box-card !border-none main-container" shadow="never">
            <div class="flex justify-between items-center mb-[10px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form">
                <el-card class="box-card !border-none" shadow="never">
                    <el-form-item :label="t('rankImages')" prop="rank_images" :rules="[{
                        required: true,
                        trigger: 'change',
                        validator: (rule: any, value: any, callback: any) => {
                        if (!value) {
                            callback(t('imagePlaceholder'))
                        }
                        callback()
                        }
                    }]">
                        <upload-image v-model="formData.rank_images" :limit="1" />
                    </el-form-item>
                    <el-form-item :label="t('noColor')">
                        <el-color-picker v-model="formData.no_color" show-alpha  />
                    </el-form-item>
                    <el-form-item :label="t('selectedColor')">
                        <el-color-picker v-model="formData.select_color" show-alpha  />
                    </el-form-item>
                    <el-form-item :label="t('selectedBgColor')">
                        <el-color-picker v-model="formData.select_bg_color_start" show-alpha />
                        <icon name="iconfont iconmap-connect" size="20px" class="block !text-gray-400 mx-[5px]" />
                        <el-color-picker v-model="formData.select_bg_color_end" show-alpha />
                    </el-form-item>
                    <el-form-item :label="t('搜索页榜单样式')">
                        <template v-for="(item,index) in config" :key="index">
                                <div :class="['w-[150px] border-[1px] border-[#ededed] border-solid overflow-hidden text-[#ededed] rounded-[4px] mr-[20px] relative', formData.search_style_type == item.value ? 'border-color text-color' : '']" @click="rankChange(item.value)">
                                    <img class="w-[100%] h-full" :src="img(item.preview)" fit-object="contain" />
                                    <span class="iconfont iconicon-selected absolute right-0 bottom-[-8px]"></span>
                                </div>
                            </template>
                    </el-form-item>
                </el-card>
            </el-form>
        </el-card>

        <!-- 提交按钮 -->
        <div class="fixed-footer-wrap">
            <div class="fixed-footer h-[48px]">
                <el-button type="primary" @click="onSave">{{ t("save") }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { useRoute } from 'vue-router'
import { FormInstance } from 'element-plus'
import { img } from '@/utils/common'
import { getRankConfig, setRankConfig } from '@/app/lang/zh-cn/api/admin/marketing'

const route = useRoute()
const pageName = route.meta.title

const formData = ref({
    no_color: '#FFFCF5',
    select_color: '#FF4142',
    select_bg_color_start: '#FFFFFF',
    select_bg_color_end: '#FFEBD7',
    rank_images: '',
    search_style_type: 1
})

const config = reactive<any>([
    {
        value: 1,
        preview: 'addon/mall/rank/rank_1.png'
    },
    {
        value: 2,
        preview: 'addon/mall/rank/rank_2.png'
    },
    {
        value: 3,
        preview: 'addon/mall/rank/rank_3.png'
    }
])

const rankChange = (value: any) => {
    formData.value.search_style_type = value
}

const formRef = ref<FormInstance>()

const formRules = computed(() => {
    return {
        rank_images: [
            {
                required: true,
                message: t('imagePlaceholder'),
                trigger: 'change'
            }
        ]
    }
})

const getRankConfigFn = () => {
    getRankConfig().then((res: any) => {
        Object.keys(formData.value).forEach((key) => {
            if (res.data[key]) formData.value[key] = res.data[key]
        })
    })
}

getRankConfigFn()

/** ** 提交 ****/
const preventDuplication = ref(false)
const onSave = async () => {
    if (preventDuplication.value) return
    await formRef.value?.validate(async (valid) => {
        if (valid) {
            preventDuplication.value = true

            setRankConfig(formData.value).then(() => {
                getRankConfigFn()
                preventDuplication.value = false
            }).catch(() => {
                preventDuplication.value = false
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
</style>
