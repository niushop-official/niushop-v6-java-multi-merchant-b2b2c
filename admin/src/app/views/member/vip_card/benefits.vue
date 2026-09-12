<template>
    <div class="main-container" v-loading="loading">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <div class="mt-[20px]">
                <div class="flex items-center bg">
                    <span class="p-[15px] w-[30%] text-[14px]">权益名称</span>
                    <span class="p-[15px] w-[35%] text-[14px]">权益简介</span>
                    <span class="p-[15px] w-[15%] text-[14px]">排序</span>
                    <span class="p-[15px] w-[15%] text-[14px]">是否启用</span>
                    <span class="p-[15px] w-[15%] text-[14px]">操作</span>
                </div>
                <div v-for="(item, key) in benefits" :key="key"  class="flex items-center">
                    <div class="p-[15px] w-[30%] text-[14px] flex items-center">
                        <img :src="img(item.icon)" class="w-[40px] h-[40px] mr-[10px]">
                        <span>{{ item.name }}</span>
                    </div>
                    <span class="p-[15px] w-[35%] text-[14px] text-[#666]">{{ item.desc || '--' }}</span>
                    <span class="p-[15px] w-[15%] text-[14px] text-[#666]">
                        <el-input v-model.trim="item.sort" class="!w-[100px]" maxlength="8" @blur="sortInputListener(item)" />
                    </span>
                    <span class="p-[15px] w-[15%] text-[14px] text-[#666]">
                        <el-switch v-model="item.status" :active-value="1" :inactive-value="0" inline-prompt active-text="开启" inactive-text="关闭" @change="(value) => updateStatus(item, value)" />
                    </span>
                    <span class="p-[15px] w-[15%] text-[14px] text-[#666] text-[var(--el-color-primary)] cursor-pointer" @click="examineFn(key)">配置</span>
                </div>
            </div>
        </el-card>
        <el-dialog v-model="benefitsDialog" :title="'会员权益'" width="1200px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
            <div class="max-h-[68vh] overflow-y-auto">
                <div v-for="(item, key) in benefits" :key="key">
                    <component :is="item.component" v-model="formData[item.type]" ref="benefitsRefs" v-if="item.component && curBenefits == key"/>
                </div>
            </div>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="benefitsDialog = false">取消</el-button>
                    <el-button type="primary" @click="onSave()">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'
import { debounce, img } from '@/utils/common'
import { useRoute } from 'vue-router'
import { getBenefits, getBenefitsConfig, setBenefitsConfig, updateBenefitsStatus, modifyBenefitsSort } from '@/app/api/vip_card'
import { ElMessage } from 'element-plus'

const route = useRoute()
const pageName = route.meta.title

const loading = ref(true)
const benefits = ref<any>({})
const formData = ref<any>({})
const benefitsRefs = ref<any>([])

const modules: any = import.meta.glob('@/**/*.vue')
const getBenefitsFn = () => {
    getBenefits().then(({ data }) => {
        Object.keys(data).forEach((key: string) => {
            if (data[key].component) {
                if (modules[data[key].component]) {
                    data[key].component = defineAsyncComponent(modules[data[key].component])
                }
            }
        })
        benefits.value = data
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getBenefitsFn()

const updateStatus = (row: any, value: number) => {
    if (row.benefits_id == '') {
        row.status = value == 1 ? 0 : 1
        ElMessage.warning('请先进行配置')
        return false
    }
    updateBenefitsStatus({
        benefits_id: row.benefits_id,
        status: value
    }).catch(() => {
        row.status = value == 1 ? 0 : 1
    })
}

// 修改排序号
const sortInputListener = debounce((data) => {
    if (isNaN(data.sort) || !/^\d{0,8}$/.test(data.sort)) {
        ElMessage({
            type: 'warning',
            message: '排序号必须是数字'
        })
        return
    }
    if (data.sort > 99999999) {
        data.sort = 99999999
    }
    modifyBenefitsSort({
        benefits_id: data.benefits_id,
        sort: data.sort
    }).then((res) => {
    })
})

const saveLoading = ref(false)
const onSave = async () => {
    if (await benefitsRefs.value[0].verify()) {
        if (saveLoading.value) return
        saveLoading.value = true

        setBenefitsConfig(formData.value[curBenefits.value]).then(() => {
            benefitsDialog.value = false
            saveLoading.value = false
            getBenefitsFn()
        }).catch(() => {
            saveLoading.value = false
        })
    }
}

const benefitsDialog = ref(false)
// 查看操作
const curBenefits = ref('')
const examineFn = (key:string) => {
    curBenefits.value = key
    getBenefitsConfig(key).then(({ data }) => {
        formData.value[key] = {}
        formData.value[key] = data
        benefitsDialog.value = true
    })
}
</script>

<style scoped>

</style>