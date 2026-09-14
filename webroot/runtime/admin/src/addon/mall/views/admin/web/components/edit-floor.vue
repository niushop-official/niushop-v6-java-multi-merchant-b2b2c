<template>
    <el-dialog v-model="showDialog" :title="formData.id ? t('updateFloor') : t('addFloor')" width="30%" class="diy-dialog-wrap" :destroy-on-close="true">
        <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading">
                <el-form-item :label="t('floorName')" prop="floor_name">
                    <el-input v-model="formData.floor_name" :placeholder="t('floorNamePlaceholder')" class="input-width" maxlength="20" clearable />
                </el-form-item>
                <el-form-item :label="t('floorTitle')" prop="title">
                    <el-input v-model="formData.title" :placeholder="t('floorTitlePlaceholder')" class="input-width" maxlength="20" clearable />
                </el-form-item>
                <el-form-item :label="t('floorSubTitle')" prop="sub_title">
                    <el-input v-model="formData.sub_title" :placeholder="t('floorSubTitlePlaceholder')" class="input-width" maxlength="20" clearable />
                </el-form-item>
                <el-form-item :label="t('goodsSelect')" prop="goods_ids">
                   <div>
                        <goods-select-popup ref="goodsSelectPopupRef" v-model="formData.goods_ids" :min="1" :max="99" />
                        <div class="text-[12px] text-[#999] leading-[20px] mt-[4px]">若选择侧边广告图,建议选择8个商品。</div>
                        <div class="text-[12px] text-[#999] leading-[20px]">不选择侧边广告图建议选择10个商品。</div>
                   </div>
                </el-form-item>
                <el-form-item :label="t('floorImg1')">
                    <div>
                        <upload-image v-model="formData.adv_img1" />
                        <div class="text-[12px] text-[#999]">侧边广告图大小为240*628</div>
                    </div>
                </el-form-item>
                <el-form-item :label="t('floorImg2')">
                    <div>
                        <upload-image v-model="formData.adv_img2" />
                        <div class="text-[12px] text-[#999]">底部广告图大小为1200*120</div>
                    </div>
                </el-form-item>
                <el-form-item :label="t('floorUrl')">
                    <nav-link v-model="formData.url"/>
                </el-form-item>
				<el-form-item :label="t('statusTitle')">
				    <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
				</el-form-item>
				
                <el-form-item :label="t('sort')" >
                    <el-input v-model="formData.sort" :placeholder="t('sortPlaceholder')"  class="input-width" maxlength="6" clearable/>
                </el-form-item>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{t('confirm')}}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { addFloor, editFloor, getFloorInfo } from '@/app/lang/zh-cn/api/admin/web'
import { cloneDeep } from 'lodash-es'
import navLink from '@/app/views/web/components/nav-link.vue'
import goodsSelectPopup from '@/addon/mall/views/admin/goods/components/goods-select-popup.vue'

const showDialog = ref(false)
const loading = ref(false)

/**
 * 表单数据
 */
const initialFormData = {
    id: '',
    floor_name: '',
    title: '',
    sub_title: '',
    goods_ids: [],
    adv_img1: '',
    adv_img2: '',
    url: {
        name: ''
    },
	status:1,
    sort: ''
}
const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        floor_name: [
            { required: true, message: t('floorNamePlaceholder'), trigger: 'blur' }
        ],
        title: [
            { required: true, message: t('floorTitlePlaceholder'), trigger: 'blur' }
        ],
        goods_ids: [
            { required: true, message: t('goodsIdsPlaceholder'), trigger: ['blur'] }
        ]
    }
})

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    const save = formData.id ? editFloor : addFloor

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = formData

            save(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
const setFormData = async (row: any = null) => {
    Object.assign(formData, cloneDeep(initialFormData))
    loading.value = true
    if (row) {
        const data = await (await getFloorInfo(row.id)).data
        if (data) {
            Object.keys(formData).forEach((key: string) => {
                if (data[key] != undefined) formData[key] = data[key]
            })
            formData.title = data.config.title
            formData.sub_title = data.config.sub_title
            formData.adv_img1 = data.config.adv_img1
            formData.adv_img2 = data.config.adv_img2
            formData.goods_ids = data.config.goods_ids
            formData.url = data.config.url
        }
    }
    loading.value = false
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
<style lang="scss">
.diy-dialog-wrap .el-form-item__label{
    height: auto  !important;
}
</style>
