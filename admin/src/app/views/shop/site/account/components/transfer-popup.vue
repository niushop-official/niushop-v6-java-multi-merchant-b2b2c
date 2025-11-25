<template>
    <div>
        <el-dialog v-model="showDialog" :title="t('transferTitle')" width="500px" :destroy-on-close="true" @close="closeDialogFn">
            <el-tabs v-model="active" class="goods-tabs">
                <el-tab-pane :label="t('wechat')" name="wechat">
                    <div class="promote-flex flex">
                        <div class="promote-img flex justify-center items-center bg-[#f8f8f8] w-[150px] h-[150px]">
                            <el-image :src="wapImage" />
                        </div>

                        <div class="px-[20px] flex-1">
                            <div class="mb-[10px]">{{ t('transferLink') }}</div>
                            <el-input class="mb-[10px]" readonly :value="wapPreview">
                                <template #append>
                                    <el-button @click="copyEvent(wapPreview)">{{ t('copy') }}</el-button>
                                </template>
                            </el-input>
                            <a class="text-primary" :href="wapImage" download>{{ t('downloadQrcode') }}</a>
                        </div>
                    </div>
                </el-tab-pane>
                <el-tab-pane :label="t('weapp')" name="weapp">
                    <div class="promote-flex flex flex-col justify-center items-center">
                        <div class="promote-img flex justify-center items-center bg-[#f8f8f8] w-[150px] h-[150px]">
                            <el-image :src="img(weappUrl)" />
                        </div>
                        <a class="text-primary mt-[10px]" :href="img(weappUrl)" download>{{ t('downloadQrcode') }}</a>
                    </div>
                </el-tab-pane>
            </el-tabs>

        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { ref, watch } from 'vue'
import { img } from '@/utils/common'
import QRCode from 'qrcode'
import storage from '@/utils/storage'
import { getUrl } from '@/app/api/sys'
import { ElMessage } from 'element-plus'
import { useClipboard } from '@vueuse/core'

const showDialog = ref(false)

const wapUrl = ref('')
const wapDomain = ref('')
const wapImage = ref('')
const wapPreview = ref('')
const page = ref('')
const weappUrl = ref('')
const active = ref('wechat')

getUrl().then((res: any) => {
    wapUrl.value = res.data.wap_url

    // 生产模式禁止
    if (import.meta.env.MODE == 'production') return

    wapDomain.value = res.data.wap_domain

    // env文件配置过wap域名
    if (wapDomain.value) {
        wapUrl.value = wapDomain.value + '/wap'
    }

    const wapDomainStorage = storage.get('wap_domain')
    if (wapDomainStorage) {
        wapUrl.value = wapDomainStorage
    }
})

const loadQrcode = () => {
    wapPreview.value = `${wapUrl.value}/${page.value}`
    // errorCorrectionLevel：密度容错率L（低）H(高)
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}

const show = (data: any) => {
    page.value = data.page
    weappUrl.value = data.qrcode
    loadQrcode()
    showDialog.value = true
}

// 复制
const { copy, isSupported, copied } = useClipboard()
const copyEvent = (text: string) => {
    if (!isSupported.value) {
        ElMessage({
            message: t('notSupportCopy'),
            type: 'warning'
        })
    }
    copy(text)
}

watch(copied, () => {
    if (copied.value) {
        ElMessage({
            message: t('copySuccess'),
            type: 'success'
        })
    }
})

const emit = defineEmits(['closeCallback'])
const closeDialogFn = () => {
    emit('closeCallback')
    showDialog.value = false
}

defineExpose({
    showDialog,
    show
})
</script>

<style lang="scss" scoped></style>
