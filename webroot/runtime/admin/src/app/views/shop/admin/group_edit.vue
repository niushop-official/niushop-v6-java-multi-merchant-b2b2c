<template>
    <div class="main-container" v-loading="loading">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="back()" />
        </el-card>
        <el-card class="box-card !border-none" shadow="never">
            <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules" class="page-form">
                <el-form-item :label="t('groupName')" prop="group_name">
                    <el-input v-model.trim="formData.group_name" :placeholder="t('groupNamePlaceholder')" clearable
                        :disabled="formData.uid || formData.create_by != 'user'" class="input-width" maxlength="20" :show-word-limit="true" />
                </el-form-item>
                <el-form-item :label="t('groupDesc')" prop="group_desc">
                    <el-input v-model.trim="formData.group_desc" type="textarea" rows="4" clearable
                        :placeholder="t('groupDescPlaceholder')" class="input-width" maxlength="100" :disabled="formData.create_by != 'user'" />
                </el-form-item>

                <el-form-item :label="t('mainApp')" prop="app">
                    <div class="text-gray-400" v-if="!appList.length">{{ t('appListEmpty') }}</div>
                    <el-checkbox-group v-model="formData.app" class="flex flex-wrap w-full" v-else>
                        <template #default>
                            <div class="flex w-[300px]" v-for="(item, index) in appList" :key="index">
                                <el-checkbox :label="item.key" class="w-full !h-auto border border-[var(--el-color-info-light-7)] border-solid p-[10px] !mr-[10px] !mb-[10px] rounded-md" :disabled="formData.create_by != 'user'">
                                    <template #default>
                                        <div class="w-full">
                                            <div class="flex">
                                                <div class="w-[60px] h-[60px] mr-[10px] rounded-md overflow-hidden">
                                                    <el-image :src="img(item.icon)" v-if="item.icon" class="w-full h-full" />
                                                    <el-image v-else class="w-full h-full">
                                                        <template #error>
                                                            <div class="image-error">
                                                                <el-icon>
                                                                    <icon-picture />
                                                                </el-icon>
                                                            </div>
                                                        </template>
                                                    </el-image>
                                                </div>
                                                <div class="flex-1 w-0 flex flex-col justify-center">
                                                    <div class="font-bold truncate">{{ item.title }}</div>
                                                    <div class="text-gray-400 mt-[10px] truncate" :title="item.desc">{{ item.desc }}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </template>
                                </el-checkbox>
                            </div>
                        </template>
                    </el-checkbox-group>
                </el-form-item>

                <el-form-item :label="t('containAddon')">
                    <div class="text-gray-400" v-if="!addonList.length">{{ t('appListEmpty') }}</div>
                    <el-checkbox-group v-model="formData.addon" class="flex flex-wrap w-full" v-else>
                        <template #default>
                            <div class="flex w-[300px]" v-for="(item, index) in addonList" :key="index">
                                <el-checkbox :label="item.key" name="" class="w-full !h-auto border border-solid p-[10px] !mr-[10px] !mb-[10px] rounded-md">
                                    <template #default>
                                        <div class="w-full">
                                            <div class="flex">
                                                <div class="w-[60px] h-[60px] mr-[10px] rounded-md overflow-hidden">
                                                    <el-image :src="img(item.icon)" v-if="item.icon" class="w-full h-full" />
                                                    <el-image v-else class="w-full h-full">
                                                        <template #error>
                                                            <div class="image-error">
                                                                <el-icon><icon-picture /></el-icon>
                                                            </div>
                                                        </template>
                                                    </el-image>
                                                </div>
                                                <div class="flex-1 w-0 flex flex-col justify-center">
                                                    <div class="font-bold truncate">{{ item.title }}</div>
                                                    <div class="text-gray-400 mt-[10px] truncate" :title="item.desc">{{ item.desc }}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </template>
                                </el-checkbox>
                            </div>
                        </template>
                    </el-checkbox-group>
                </el-form-item>
            </el-form>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" :loading="saveLoding" @click="confirm(formRef)">{{ t('save') }}</el-button>
                <el-button @click="back()">{{ t('cancel') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup async>
import { ref, computed } from 'vue'
import { t } from '@/lang'
import { ElMessageBox, FormInstance } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { addShopGroup, editShopGroup, getShopGroupInfo } from '@/app/api/shop_admin'
import { getInstalledAddonList } from '@/app/api/addon'
import { img } from '@/utils/common'
import { useRouter, useRoute } from 'vue-router'

const loading = ref(true)
const addonList = ref<any>([])
const appList = ref<any>([])
const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const saveLoding = ref(false)

/**
 * 表单数据
 */
const formData: Record<string, any> = ref({
    group_id: 0,
    group_name: '',
    group_desc: '',
    app: [],
    addon: [],
    create_by: 'user',
    sync_site: 0
})

let installAddon = []
const getInstalledAddonListFn = async () => {
    await getInstalledAddonList().then(({ data }) => {
        const apps: any[] = []
        const addons: any[] = []

        Object.keys(data).forEach(key => {
            installAddon.push(key)
            const item = data[key]
            item.type == 'addon' ? addons.push(item) : apps.push(item)
        })

        appList.value = apps
        addonList.value = addons
    }).catch()
}
getInstalledAddonListFn()

if (route.query.id) {
    getShopGroupInfo(route.query.id).then(({ data }) => {
        data.addon = data.addon.filter((key: string) => installAddon.includes(key))
        formData.value = data
        loading.value = false
    }).catch()
} else {
    loading.value = false
}

const back = () => {
    router.push('/shop/group')
}

const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        group_name: [
            { required: true, message: t('groupNamePlaceholder'), trigger: 'blur' }
        ],
        app: [
            { required: true, message: t('mainAppPlaceholder'), trigger: 'blur' }
        ]
    }
})

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (saveLoding.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            if (formData.value.group_id) {
                ElMessageBox.confirm(t('syncUpdateSiteApp'), t('warning'),
                {
                    confirmButtonText: t('yes'),
                    cancelButtonText: t('no'),
                    type: 'warning',
                    showClose: false,
                    closeOnClickModal: false
                }
                ).then(() => {
                    formData.value.sync_site = 1
                    saveFun()
                }).catch(() => {
                    saveFun()
                })
            } else {
                saveFun()
            }
        }
    })
}

const saveFun = () => {
    saveLoding.value = true
    const save = formData.value.group_id ? editShopGroup : addShopGroup
    save(formData.value).then(res => {
        setTimeout(() => {
            back()
        }, 1000)
        saveLoding.value = false
    }).catch(() => {
        saveLoding.value = false
    })
}
</script>

<style lang="scss" scoped>
.image-error {
    background: var(--el-border-color-extra-light);
    width: 100%;
    height: 100%;
}

:deep(.el-checkbox__label) {
    width: 100%;
}
</style>
