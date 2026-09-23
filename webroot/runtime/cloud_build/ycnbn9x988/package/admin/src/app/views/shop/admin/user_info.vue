<template>
    <div class="main-container" v-loading="loading">
        <el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/shop/user' })" />
        </el-card>
        <el-card class="box-card !border-none" shadow="never">
            <el-card class="box-card !border-none" shadow="never">
                <h3 class="panel-title !text-[16px] pl-[15px] mb-[20px]">{{ t('userInfo') }}</h3>
                <el-row :gutter="20" class="pl-[15px] mb-[20px]">
                    <el-col :span="6">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('uid') }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ detail.uid }}
                        </span>
                    </el-col>
                    <el-col :span="6" :offset="6">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('username') }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ detail.username }}
                        </span>
                    </el-col>
                </el-row>
                <el-row :gutter="20" class="pl-[15px] mb-[20px]">
                    <el-col :span="6">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('realname') }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ detail.real_name || '--' }}
                        </span>
                    </el-col>
                    <el-col :span="6" :offset="6">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('addTime') }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ detail.create_time }}
                        </span>
                    </el-col>
                </el-row>
                <el-row :gutter="20" class="pl-[15px] mb-[20px]">
                    <el-col :span="6">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('lastLoginTime') }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ detail.last_time || '' }}
                        </span>
                    </el-col>
                    <el-col :span="6" :offset="6">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('lastLoginIP') }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ detail.last_ip || '' }}
                        </span>
                    </el-col>
                </el-row>
            </el-card>
            <el-card class="box-card !border-none" shadow="never">
                <h3 class="panel-title !text-[16px] pl-[15px]">{{ t('siteInfo') }}</h3>

                <el-table :data="detail.roles" size="large">
                    <el-table-column prop="site_id" :label="t('siteId')" width="100px" />
                    <el-table-column prop="site_name" :label="t('siteName')" />
                    <el-table-column prop="create_time" :label="t('addTime')" min-width="160" />
                    <el-table-column prop="is_admin" :label="t('isAdmin')" min-width="180" align="center">
                        <template #default="{ row }">
                            {{ row.is_admin ? t('yes') : t('no') }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('status')" min-width="80" align="center">
                        <template #default="{ row }">
                            <el-tag class="ml-2" type="success" v-if="row.status == 1">{{ row.site_status_name }}</el-tag>
                            <el-tag class="ml-2" type="error" v-else-if="row.status == 3">
                                {{ row.site_status_name }}
                            </el-tag>
                            <el-tag class="ml-2" type="error" v-else>
                                {{ row.site_status_name }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" align="right" fixed="right">
                            <template #default="{ row }">
                                <el-button v-if="getSiteInfoPath(row)" type="primary" link @click="siteInfo(row)">{{ t('info') }}</el-button>
                                <span v-else>--</span>
                            </template>
                        </el-table-column>
                </el-table>
            </el-card>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { getUserInfo } from '@/app/api/site'
import { ArrowLeft } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const uid: number = parseInt(route.query.uid || 0)
const loading = ref(true)
const detail = ref({})
const siteInfoPathMap: Record<string, string> = {
    mall: '/mall/shop/info',
    o2o: '/o2o/shop/info',
    waimai: '/waimai/shop/info',
    supply: '/supply/shop/info',
    front_warehouse: '/front_warehouse/shop/info'
}

getUserInfo(uid).then(({ data }) => {
    detail.value = data
    loading.value = false
}).catch()

const normalizeAppList = (app: any): any[] => {
    if (!app) return []
    if (Array.isArray(app)) return app
    if (typeof app === 'string') {
        try {
            return normalizeAppList(JSON.parse(app))
        } catch (e) {
            return app.split(',').map((item: string) => item.trim()).filter(Boolean)
        }
    }
    if (typeof app === 'object') {
        return [...Object.keys(app), ...Object.values(app)]
    }
    return []
}

const getAppKey = (item: any) => {
    if (typeof item === 'string') return item
    return item?.key || item?.addon_key || item?.app || ''
}

const getSiteInfoPath = (data: any) => {
    const appKey = normalizeAppList(data?.app).map(getAppKey).find((key: string) => siteInfoPathMap[key])
    return appKey ? siteInfoPathMap[appKey] : ''
}

/**
 * 站点详情
 * @param data
 */
const siteInfo = (data: any) => {
    const path = getSiteInfoPath(data)
    if (path) {
        router.push({ path, query: { id: data.site_id } })
    }
}

</script>

<style lang="scss" scoped></style>
