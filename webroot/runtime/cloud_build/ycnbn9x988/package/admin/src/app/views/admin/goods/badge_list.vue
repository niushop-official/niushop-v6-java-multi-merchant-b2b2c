<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addBadge') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form label-width="70px" :inline="true" :model="badgeTable.searchParam" ref="searchFormRef" class="page-form">
                    <el-form-item :label="t('badgeName')" prop="badge_name">
                        <el-input v-model.trim="badgeTable.searchParam.badge_name" :placeholder="t('badgeNamePlaceholder')" class="search-input" clearable />
                    </el-form-item>

                    <el-form-item>
                        <el-button type="primary" @click="loadBadgeList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="badgeTable.data" size="large" v-loading="badgeTable.loading" @sort-change="sortChange">
                    <template #empty>
                        <span>{{ !badgeTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="badge_name" :label="t('badgeName')" min-width="120">
                        <template #default="{ row }">
                            <div v-if="row.style_type == 'diy'" class="inline-block px-[10px] text-[12px] rounded-[4px] box-border whitespace-nowrap h-[28px] leading-[28px]"
                                 :style="{ color : row.color_json.text_color, backgroundColor : row.color_json.bg_color, border : row.color_json.border_color ? '1px solid ' + row.color_json.border_color : 'none' }">
                                <span>{{ row.badge_name }}</span>
                            </div>
                            <img v-else-if="row.style_type == 'icon'" class="block h-[28px] rounded-[4px] object-cover" :src="img(row.icon)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="status" :label="t('status')" min-width="80" :show-overflow-tooltip="true" >
                        <template #default="{ row }">
                            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" inline-prompt :active-text="t('statusOn')" :inactive-text="t('statusOff')" @change="(value) => modifyBadgeStatusEvent(row, value)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="memo" :label="t('memo')" min-width="200" />
                    <el-table-column prop="sort" :label="t('sort')" min-width="120" sortable="custom">
                        <template #default="{ row }">
                            <el-input v-model.trim="row.sort" class="!w-[100px]" maxlength="8" @blur="sortInputListener(row.sort, row)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="100" sortable="custom">
                        <template #default="{ row }">
                            <div>{{ row.create_time }}</div>
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.badge_id)">{{ t('delete') }}</el-button>
                            <el-button type="primary" link @click="copyEvent(row)">{{ t('copyBadge') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="badgeTable.page" v-model:page-size="badgeTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="badgeTable.total"
                        @size-change="loadBadgeList()" @current-change="loadBadgeList" />
                </div>
            </div>

            <badge-edit ref="editBadgeDialog" @complete="loadBadgeList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getBadgePageList, deleteBadge, modifyBadgeSort, modifyBadgeStatus, copyBadge } from '@/app/api/basic_admin'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import BadgeEdit from '@/app/views/admin/goods/components/badge-edit.vue'
import { useRoute } from 'vue-router'
import { debounce } from 'lodash-es'

const route = useRoute()
const pageName = route.meta.title

const badgeTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        badge_name: '',
        order: '',
        sort: ''
    }
})

const searchFormRef = ref<FormInstance>()

// 监听排序
const sortChange = (event: any) => {
    let sort = ''
    if (event.order == 'ascending') {
        sort = 'asc'
    } else if (event.order == 'descending') {
        sort = 'desc'
    }
    if (sort) {
        badgeTable.searchParam.order = event.prop
        badgeTable.searchParam.sort = sort
    }
    loadBadgeList()
}

/**
 * 获取商品角标列表
 */
const loadBadgeList = (page: number = 1) => {
    badgeTable.loading = true
    badgeTable.page = page

    getBadgePageList({
        page: badgeTable.page,
        limit: badgeTable.limit,
        ...badgeTable.searchParam
    }).then(res => {
        badgeTable.loading = false
        badgeTable.data = res.data.data
        badgeTable.total = res.data.total
    }).catch(() => {
        badgeTable.loading = false
    })
}
loadBadgeList()

const editBadgeDialog: Record<string, any> | null = ref(null)

/**
 * 添加商品角标
 */
const addEvent = () => {
    editBadgeDialog.value.setFormData()
    editBadgeDialog.value.showDialog = true
}

/**
 * 编辑商品角标
 * @param data
 */
const editEvent = (data: any) => {
    editBadgeDialog.value.setFormData(data)
    editBadgeDialog.value.showDialog = true
}

/**
 * 删除商品角标
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('badgeDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteBadge(id).then(() => {
            loadBadgeList()
        }).catch(() => {
        })
    })
}

// 修改排序号
const sortInputListener = debounce((sort, row) => {
    if (isNaN(sort) || !/^\d{0,8}$/.test(sort)) {
        ElMessage({
            type: 'warning',
            message: `${t('sortTips')}`
        })
        return
    }
    if (sort > 99999999) {
        row.sort = 99999999
    }
    modifyBadgeSort({
        badge_id: row.badge_id,
        sort
    }).then((res) => {
    })
})

const isRepeat = ref(false)

// 修改状态
const modifyBadgeStatusEvent = (row: any, status: any) => {
    if (isRepeat.value) return
    isRepeat.value = true

    modifyBadgeStatus({
        badge_id: row.badge_id,
        status
    }).then((res) => {
        loadBadgeList()
        isRepeat.value = false
    }).catch(() => {
        row.status = status == 1 ? 0 : 1
        isRepeat.value = false
    })
}
// 复制商品角标
const copyEvent = (data: any) => {
    ElMessageBox.confirm(t('badgeCopyTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (isRepeat.value) return
        isRepeat.value = true

        copyBadge({
            badge_id: data.badge_id
        }).then((res: any) => {
            if (res.code == 1) {
                loadBadgeList()
            }
            isRepeat.value = false
        }).catch(() => {
            isRepeat.value = false
        })
    })
}
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadBadgeList()
}
</script>

<style lang="scss" scoped>
/* 多行超出隐藏 */
.multi-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
</style>
