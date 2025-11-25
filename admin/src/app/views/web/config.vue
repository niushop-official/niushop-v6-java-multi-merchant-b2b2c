<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading">
                <el-form-item label="甄选好店模块" prop="is_show_shop">
                    <el-switch v-model="formData.is_show_shop" :active-value="1" :inactive-value="0" />
                </el-form-item>
                <el-form-item label="店铺广告图" prop="shop_ad_image">
                    <div>
                        <upload-image v-model="formData.shop_ad_image" :limit="1" />
                        <div class="text-[12px] text-[#999]">建议上传288*422的图片</div>
                    </div>
                </el-form-item>
                <el-form-item label="店铺" prop="site_ids">
                    <shop-select-popup v-model="formData.site_ids" :min="1" :max="6" />
                </el-form-item>
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
import { ref, computed } from 'vue'
import { t } from '@/lang'
import { useRoute } from 'vue-router'
import shopSelectPopup from '@/app/views/web/components/shop-select-popup.vue'
import { getWebConfig, setWebConfig } from '@/app/api/web'
import { FormInstance } from 'element-plus'

const route = useRoute()
const pageName = route.meta.title

const loading = ref(false)
const formRef = ref<FormInstance>()
const formData = ref<any>({
    is_show_shop: 1,
    shop_ad_image: '',
    site_ids: []
})

const getWebConfigFn = () => {
    loading.value = true
    getWebConfig().then((res: any) => {
        formData.value = res.data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getWebConfigFn()

const formRules = computed(() => {
    return {
        is_show_shop: [
            { required: true, message: '请选择是否显示甄选好店模块', trigger: 'blur' }
        ],
        shop_ad_image: [
            { required: true, message: '请上传店铺广告图', trigger: 'blur' }
        ],
        site_ids: [
            { required: true, message: '请选择店铺', trigger: 'blur' }
        ]
    }
})

/**
 * 确认
 * @param formEl
 */
const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            setWebConfig(formData.value).then(res => {
                getWebConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
</script>
<style lang="scss" scoped></style>