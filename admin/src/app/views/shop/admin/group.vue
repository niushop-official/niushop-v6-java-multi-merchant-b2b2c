<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" class="w-[100px]" @click="addEvent">
                    {{ t('addSiteGroup') }}
                </el-button>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="shopGroupTableData.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('groupName')" prop="keywords">
                        <el-input v-model="shopGroupTableData.searchParam.keywords" :placeholder="t('groupNamePlaceholder')" />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadShopGroupList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div>
                <el-table :data="shopGroupTableData.data" size="large" v-loading="shopGroupTableData.loading">

                    <template #empty>
                        <span>{{ !shopGroupTableData.loading ? t('emptyData') : '' }}</span>
                    </template>

                    <el-table-column :label="t('groupName')" >
                        <template #default="{ row }">
                            <el-tag size="small" v-if="row.create_by != 'user'">{{ row.create_by_name }}</el-tag>
                            <span class="ml-[8px]">{{row.group_name}}</span>
                        </template>
                    </el-table-column>

                   <el-table-column prop="group_name" :label="t('appName')" :show-overflow-tooltip="true">
                       <template #default="{ row }">
                           <el-tag class="mr-1" size="small" v-for="name in row.app_name">{{ name }}</el-tag>
                       </template>
                   </el-table-column>

                    <el-table-column prop="group_name" :label="t('addonName')" :show-overflow-tooltip="true">
                        <template #default="{ row }">
                            <el-tag class="mr-1" size="small" v-for="(name,index) in row.addon_name" :key="index" >{{ name }}</el-tag>
                        </template>
                    </el-table-column>

                    <el-table-column prop="create_time" :label="t('createTime')"></el-table-column>
                    <el-table-column prop="group_roles" :label="t('operation')" align="right" fixed="right" width="130">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)" v-if="row.create_by == 'user'">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.group_id)" v-if="row.create_by == 'user'">{{ t('delete') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="shopGroupTableData.page"
                        v-model:page-size="shopGroupTableData.limit" layout="total, sizes, prev, pager, next, jumper"
                        :total="shopGroupTableData.total" @size-change="loadShopGroupList()"
                        @current-change="loadShopGroupList" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { getShopGroupList, deleteShopGroup } from '@/app/api/shop_admin'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const searchFormRef = ref<FormInstance>()

const shopGroupTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keywords: ''
    }
})

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadShopGroupList()
}

/**
 * 获取分组列表
 */
const loadShopGroupList = (page: number = 1) => {
    shopGroupTableData.loading = true
    shopGroupTableData.page = page

    getShopGroupList({
        page: shopGroupTableData.page,
        limit: shopGroupTableData.limit,
        ...shopGroupTableData.searchParam
    }).then(res => {
        shopGroupTableData.loading = false
        shopGroupTableData.data = res.data.data
        shopGroupTableData.total = res.data.total
    }).catch(() => {
        shopGroupTableData.loading = false
    })
}
loadShopGroupList()

/**
 * 添加分组
 */
const addEvent = () => {
    router.push('/shop/group_edit')
}

/**
 * 编辑分组
 * @param data
 */
const editEvent = (data: any) => {
    router.push({ path: '/shop/group_edit', query: { id: data.group_id } })
}

/**
 * 删除分组
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('groupDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteShopGroup(id).then(() => {
            loadShopGroupList()
        }).catch(() => {
        })
    })
}
</script>

<style lang="scss" scoped></style>
