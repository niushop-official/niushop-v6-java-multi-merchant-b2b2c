<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addTopic') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="topicTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('topicName')" prop="topic_name">
                        <el-input v-model.trim="topicTable.searchParam.topic_name" :placeholder="t('topicNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('status')" prop="status">
                        <el-select v-model="topicTable.searchParam.status" :placeholder="t('statusPlaceholder')" clearable>
                            <el-option  :label="t('open')" :value="1" />
                            <el-option  :label="t('off')" :value="0" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('isRecommend')" prop="is_recommend">
                        <el-select v-model="topicTable.searchParam.is_recommend" :placeholder="t('isRecommendPlaceholder')" clearable>
                            <el-option  :label="t('recommend')" :value="1" />
                            <el-option  :label="t('notRecommend')" :value="0" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadTopicList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <el-table :data="topicTable.data" size="large" v-loading="topicTable.loading">
                    <template #empty>
                        <span>{{ !topicTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="topic_name" :label="t('topicName')" min-width="120" />
                    <el-table-column prop="content_num" :label="t('contentNum')" min-width="120" />
                    <el-table-column prop="member_num" :label="t('memberNum')" min-width="120" />
                    <el-table-column  :label="t('isRecommend')" min-width="120">
                        <template #default="{ row }">
                            <el-tag class="cursor-pointer" :type="row.status == 0 ?  'info' : row.is_recommend != 0 ? 'success' : 'danger'" @click="handleRecommend(row)">{{ row.is_recommend != 0 ? t('recommend') : t('notRecommend') }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('status')" min-width="120">
                        <template #default="{ row }">
                            <el-tag class="cursor-pointer" :type="row.status != 0 ? 'success' : 'danger'" @click="showClick(row)">{{ row.status != 0 ? t('open') : t('off') }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" :label="t('sort')" min-width="120">
                        <template #default="{ row }">
                            <el-input v-model.trim="row.sort" class="!w-[100px]" maxlength="8" @blur="sortInputListener(row.sort, row)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="120" />
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="toLink(row)">{{ t('show') }}</el-button>
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.topic_id)">{{ t('delete') }}
                            </el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="topicTable.page" v-model:page-size="topicTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="topicTable.total"
                        @size-change="loadTopicList()" @current-change="loadTopicList" />
                </div>
            </div>

            <topic-edit ref="editTopicDialog" @complete="loadTopicList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getTopicList, deleteTopic, modifyTopicSort, modifyTopicStatus, modifyTopicRecommend } from '@/addon/sow_community/api/admin/topic'
import { debounce } from '@/utils/common'
import { ElMessageBox, FormInstance, ElMessage } from 'element-plus'
import TopicEdit from '@/addon/sow_community/views/admin/topic/components/topic-edit.vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const topicTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        topic_name: '',
        status: '',
        is_recommend: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取社区分类列表
 */
const loadTopicList = (page: number = 1) => {
    topicTable.loading = true
    topicTable.page = page

    getTopicList({
        page: topicTable.page,
        limit: topicTable.limit,
        ...topicTable.searchParam
    }).then((res: any) => {
        topicTable.loading = false
        topicTable.data = res.data.data
        topicTable.total = res.data.total
    }).catch(() => {
        topicTable.loading = false
    })
}
loadTopicList()

const editTopicDialog: Record<string, any> | null = ref(null)

/**
 * 添加社区分类
 */
const addEvent = () => {
    editTopicDialog.value.setFormData()
    editTopicDialog.value.showDialog = true
}

/**
 * 编辑社区分类
 * @param data
 */
const editEvent = (data: any) => {
    editTopicDialog.value.setFormData(data)
    editTopicDialog.value.showDialog = true
}

/**
 * 删除社区分类
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('topicDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteTopic(id).then(() => {
            loadTopicList()
        }).catch(() => {
        })
    })
}

const handleRecommend = (row: any) => {
    if (row.status == 0) {
        return false
    }
    row.is_recommend = row.is_recommend === 1 ? 0 : 1
    modifyTopicRecommend({
        topic_id: row.topic_id,
        is_recommend: row.is_recommend
    }).then((res: any) => {
    })
}

const showClick = (row: any) => {
    row.status = row.status === 1 ? 0 : 1
    modifyTopicStatus({
        topic_id: row.topic_id,
        status: row.status
    }).then((res: any) => {
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
    modifyTopicSort({
        topic_id: row.topic_id,
        sort
    }).then((res: any) => {
    })
})

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadTopicList()
}

const toLink = (data: any) => {
    router.push(`/sow_community/content/list?topic_id=${data.topic_id}`)
}
</script>

<style lang="scss" scoped>
</style>
