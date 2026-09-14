<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-table :data="qrcodeTableData.data" size="large" v-loading="qrcodeTableData.loading">

                <template #empty>
                    <span>{{ !qrcodeTableData.loading ? t('emptyData') : '' }}</span>
                </template>

                <el-table-column prop="name" label="名称" min-width="120" />

                <el-table-column :label="t('operation')" fixed="right" align="right" min-width="160">
                    <template #default="{ row }">
                        <el-button type="primary" link @click="previewPoster(row)">预览</el-button>
                        <el-button type="primary" link @click="download(row)">下载</el-button>
                        <el-button type="primary" link @click="setQrcode(row)">配置</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>
        <!--预览海报-->
        <el-dialog v-model="previewDialogVisible" title="预览" width="400px" height="640px">
            <div v-if="previewPosterUrl">
                <view class="w-[360px] h-[640px]">
                    <img :src="img(previewPosterUrl)"  object-fit="cover" class="w-[360px] h-auto" />
                </view>
            </div>
        </el-dialog>
        <el-dialog v-model="dialogVisible" title="配置" width="25%">

            <el-form :model="formData" label-width="140px" ref="formRef">
                <el-form-item label="是否隐藏平台入口" :rules="[{ required: true, message: '请选择是否隐藏平台入口', trigger: ['blur','change'] }]" v-if="formData.type == 'cash'">
                    <el-radio-group v-model="formData.value.is_hidden_plat_index">
                        <el-radio :label="1">显示</el-radio>
                        <el-radio :label="0">隐藏</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="是否显示" :rules="[{ required: true, message: '请选择是否显示', trigger: ['blur','change'] }]"  v-if="formData.type == 'mall_shop' || formData.type == 'o2o_shop'">
                    <el-radio-group v-model="formData.value.is_show">
                        <el-radio :label="1">显示</el-radio>
                        <el-radio :label="0">隐藏</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="loading" @click="addEvent(formRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>

    </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { t } from '@/lang'
import { getSiteQrcodeList } from '@/app/api/diy'
import { getPreviewGenerate, updateQrcodeConfig, getQrcodeConfig } from '@/app/api/poster'
import { useRoute, useRouter } from 'vue-router'
import { img } from '@/utils/common'
import { FormInstance } from 'element-plus'
import storage from '@/utils/storage'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title
const siteId = storage.get('siteId')

const qrcodeTableData: any = reactive({
    loading: true,
    data: []
})

// 获取自定义页面列表
const loadQrcodeList = () => {
    qrcodeTableData.loading = true

    getSiteQrcodeList().then(res => {
        qrcodeTableData.loading = false
        qrcodeTableData.data = res.data
    }).catch(() => {
        qrcodeTableData.loading = false
    })
}

loadQrcodeList()

const previewDialogVisible = ref(false)
const previewPosterUrl = ref('')
const isRepeat = ref(false)
// 预览海报
const previewPoster = (data: any) => {
    if (isRepeat.value) return
    isRepeat.value = true

    getPreviewGenerate({
        id: data.id,
        type: data.type,
        param: {
            site_id: siteId
        }
    }).then((res:any) => {
        if (res.data) {
            previewPosterUrl.value = res.data
            previewDialogVisible.value = true
        }
        isRepeat.value = false
    })
}

// 下载
const download = (data: any) => {
    if (isRepeat.value) return
    isRepeat.value = true

    getPreviewGenerate({
        id: data.id,
        type: data.type,
        param: {
            site_id: siteId
        }
    }).then(async (res:any) => {
        const url = img(res.data)
        const link = document.createElement('a')
        link.href = url
        link.download = data.name + '.png'
        link.target = '_blank'
        document.body.appendChild(link)
        link.click()
        link.remove()
        isRepeat.value = false
    }).catch(() => {
        isRepeat.value = false
    })
}

// 配置
const setQrcode = (data: any) => {
    formData.type = data.type
    getQrcodeConfig({
        type: data.type
    }).then(res => {
        formData.value.is_hidden_plat_index = res.data.is_hidden_plat_index
        formData.value.is_show = res.data.is_show
        dialogVisible.value = true
    })
}
const formRef = ref<FormInstance>()
const dialogVisible = ref(false)
const loading = ref(false)
const formData = reactive<any>({
    type: '',
    value: {
        is_hidden_plat_index: 0,
        is_show: 1
    }
})

const addEvent = async (formEl: FormInstance | undefined) => {
    if (!formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            if (loading.value) return
            loading.value = true
            updateQrcodeConfig(formData).then(res => {
                loading.value = false
                dialogVisible.value = false
                loadQrcodeList()
            })
        }
    })
}
</script>

<style lang="scss" scoped>
</style>
