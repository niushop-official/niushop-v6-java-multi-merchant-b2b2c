<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addContent') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="contentTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('contentTitle')" prop="content_title">
                        <el-input v-model.trim="contentTable.searchParam.content_title" :placeholder="t('contentTitlePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('contentType')" prop="content_type">
                        <el-select v-model="contentTable.searchParam.content_type" :placeholder="t('contentTypePlaceholder')" clearable>
                            <el-option  :label="t('imageText')" :value="1" />
                            <el-option  :label="t('shortVideo')" :value="2" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadContentList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
                <el-tab-pane :label="t('all')" name=""></el-tab-pane>
                <el-tab-pane :label="t('auditAdopt')" name="2"></el-tab-pane>
                <el-tab-pane :label="t('awaitAudit')" name="1"></el-tab-pane>
                <el-tab-pane :label="t('auditRefuse')" name="-1"></el-tab-pane>
                <el-tab-pane :label="t('off')" name="-2"></el-tab-pane>
            </el-tabs>
            <div class="mb-[10px] flex items-center">
                <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                <el-button @click="batchContentStatus()" size="small" >{{ t('batchOffContent') }}</el-button>
                <el-button @click="batchDeleteContentFn" size="small">{{ t('batchDeleteContent') }}</el-button>
            </div>

            <div class="mt-[10px]">
                <el-table :data="contentTable.data" size="large" ref="contentTableRef" v-loading="contentTable.loading"  @selection-change="handleSelectionChange">
                    <template #empty>
                        <span>{{ !contentTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" />
                    <el-table-column  :label="t('contentCover')" min-width="80">
                        <template #default="{ row }">
                            <div class="min-w-[70px] h-[70px]">
                                <el-image v-if="row.content_cover" class="w-[70px] h-[70px]" :src="img(row.content_cover)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" />
                                        </div>
                                    </template>
                                </el-image>
                                <img v-else class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" fit="contain" />
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="content_title" :label="t('contentTitle')" min-width="200" />
                    <el-table-column  :label="t('contentType')" min-width="100">
                        <template #default="{ row }">
                           <div>{{ row.content_type == 1 ? t('imageText') : t('shortVideo') }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('author')" min-width="120">
                        <template #default="{ row }">
                            <div v-if="row.member">{{ row.member.nickname }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="view_num" :label="t('viewNum')" min-width="120" />
                    <el-table-column prop="like_num" :label="t('likeNum')" min-width="120" />
                    <!-- <el-table-column prop="share_num" :label="t('shareNum')" min-width="120" /> -->
                    <el-table-column prop="comment_num" :label="t('commentNum')" min-width="120" />
                    <el-table-column prop="status_name"  :label="t('status')" min-width="120" />
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="120" />
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                            <el-button type="primary" link @click="deleteEvent(row.content_id)">{{ t('delete') }}</el-button>
                            <el-button type="primary" link @click="detailEvent(row)">{{ t('detail') }}</el-button>
                            <el-button type="primary" link @click="spreadEvent(row)" v-if="row.status == 0 || row.status == 2">{{ t('spread') }}</el-button>
                            <el-button type="primary" link @click="adoptEvent(row.content_id)" v-if="row.status == 1">{{ t('adopt') }}</el-button>
                            <el-button type="primary" link @click="refuseEvent(row.content_id)" v-if="row.status == 1">{{ t('refuse') }}</el-button>
                            <el-button type="primary" link @click="offEvent(row.content_id)" v-if="row.status != -2">{{ t('off') }}</el-button>
                            <el-button type="primary" link @click="addCommentEvent(row.content_id)" v-if="row.status != -2">{{ t('add') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="contentTable.page" v-model:page-size="contentTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="contentTable.total"
                        @size-change="loadContentList()" @current-change="loadContentList" />
                </div>
            </div>
        </el-card>
        <!-- 审核拒绝 -->
        <el-dialog v-model="refuseShowDialog" :title="t('refuseReason')" width="460px" class="diy-dialog-wrap" :destroy-on-close="true">
            <el-form :model="formData" label-width="90px" ref="formRef" :rules="formRules" class="page-form">
                <el-form-item :label="t('refuseReason')" prop="refuse_reason">
                    <el-input v-model.trim="formData.refuse_reason" type="textarea" rows="4" clearable
                        :placeholder="t('refuseReasonPlaceholder')" class="input-width" maxlength="200" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="refuseShowDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
        <!-- 添加评论 -->
        <el-dialog v-model="addShowDialog" :title="t('commentContent')" width="460px"  :destroy-on-close="true">
            <el-form :model="formAddData" label-width="90px" ref="formAddRef" :rules="formAddRules" class="page-form">
                <el-form-item :label="t('commentContent')" prop="comment_content">
                    <el-input v-model.trim="formAddData.comment_content" type="textarea" rows="4" clearable :placeholder="t('commentContentPlaceholder')" class="input-width" maxlength="200" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addShowDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="save(formAddRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
        <content-detail ref="contentDetailRef"  />
        <!-- 推广弹出框 -->
        <spread-popup ref="spreadPopupRef" />

    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { getContentList, getContentStatus, deleteContent, auditContent, offContent } from '@/addon/sow_community/api/admin/content'
import { addComment } from '@/addon/sow_community/api/admin/comment'
import { img, setTablePageStorage, getTablePageStorage } from '@/utils/common'
import { ElMessageBox, FormInstance, ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import ContentDetail from './components/content-detail.vue'
import { cloneDeep } from 'lodash-es'
import spreadPopup from '@/components/spread-popup/index.vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const repeat = ref(false)
const activeName = ref('')
const contentTable = reactive<any>({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        topic_id: '',
        content_title: '',
        content_type: '',
        status: ''
    }
})
contentTable.searchParam.topic_id = route.query.topic_id

const handleClick = (event: string) => {
    contentTable.searchParam.status = event
    loadContentList()
}

const searchFormRef = ref<FormInstance>()

// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    contentTableRef.value.toggleAllSelection()
}
const contentTableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (multipleSelection.value.length > 0 && multipleSelection.value.length < contentTable.data.length) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == contentTable.data.length && contentTable.data.length && multipleSelection.value.length) {
        toggleCheckbox.value = true
    }
}

/**
 * 获取社区分类列表
 */
const loadContentList = (page: number = 1) => {
    contentTable.loading = true
    contentTable.page = page

    const searchData = cloneDeep(contentTable.searchParam)
    getContentList({
        page: contentTable.page,
        limit: contentTable.limit,
        ...contentTable.searchParam
    }).then((res: any) => {
        contentTable.loading = false
        contentTable.data = res.data.data
        contentTable.total = res.data.total
        setTablePageStorage(contentTable.page, contentTable.limit, searchData)
    }).catch(() => {
        contentTable.loading = false
    })
}
loadContentList(getTablePageStorage(contentTable.searchParam).page)

const statusList = ref<any>([])
const getContentStatusFn = () => {
    getContentStatus().then((res: any) => {
        statusList.value = []
        for (const key in res.data) {
            const obj = {
                name: res.data[key],
                value: key
            }
            statusList.value.push(obj)
        }
    })
}

getContentStatusFn()
/**
 * 添加社区分类
 */
const addEvent = () => {
    router.push('/sow_community/content/add')
}

/**
 * 编辑社区分类
 * @param data
 */
const editEvent = (data: any) => {
    router.push('/sow_community/content/edit?content_id=' + data.content_id)
}

/**
 * 删除社区分类
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('contentDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteContent({ content_ids: [id] }).then(() => {
            loadContentList()
        }).catch(() => {
        })
    })
}

// 批量设置下架
const batchContentStatus = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedContentTips')}`
        })
        return
    }

    const contentIds: any = []
    multipleSelection.value.forEach((item: any) => {
        contentIds.push(item.content_id)
    })

    offContent({
        content_ids: contentIds
    }).then((res: any) => {
        loadContentList()
    })
}
// 批量删除
const batchDeleteContentFn = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('batchEmptySelectedContentTips')}`
        })
        return
    }

    ElMessageBox.confirm(t('batchContentDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (repeat.value) return
        repeat.value = true

        const contentIds: any = []
        multipleSelection.value.forEach((item: any) => {
            contentIds.push(item.content_id)
        })

        deleteContent({
            content_ids: contentIds
        }).then(() => {
            loadContentList()
            repeat.value = false
        }).catch(() => {
            repeat.value = false
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadContentList()
}

// 审核通过
const adoptEvent = (id: number) => {
    ElMessageBox.confirm(t('auditAdoptTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        auditContent({
            content_id: id,
            status: 2
        }).then(() => {
            loadContentList()
        }).catch(() => {
        })
    })
}

// 审核拒绝
const refuseShowDialog = ref(false)
const initialFormData = {
    content_id: 0,
    refuse_reason: '',
    status: -1
}
const formData: Record<string, any> = reactive({ ...initialFormData })
const formRef = ref<FormInstance>()
const refuseEvent = (id: number) => {
    formData.content_id = id
    formData.refuse_reason = ''
    refuseShowDialog.value = true
}
const formRules = computed(() => {
    return {
        refuse_reason: [
            { required: true, message: t('refuseReasonPlaceholder'), trigger: 'blur' }
        ]
    }
})
const confirm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            const data = formData
            auditContent(data).then(res => {
                loadContentList()
                refuseShowDialog.value = false
            }).catch(() => {
                refuseShowDialog.value = false
            })
        }
    })
}

// 订单详情
const contentDetailRef = ref()
const detailEvent = (data: any) => {
    const parameter = { id: data.content_id }
    contentDetailRef.value.setFormData(parameter)
    contentDetailRef.value.showDialog = true
}

// 强制下架
const offEvent = (id: number) => {
    ElMessageBox.confirm(t('contentOffTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        offContent({ content_ids: [id] }).then(() => {
            loadContentList()
        }).catch(() => {
        })
    })
}

// 添加评论
const addShowDialog = ref(false)
const init = {
    content_id: 0,
    comment_content: ''

}
const formAddData: Record<string, any> = reactive({ ...init })
const formAddRef = ref<FormInstance>()

const addCommentEvent = (id: number) => {
    formAddData.content_id = id
    formAddData.comment_content = ''
    addShowDialog.value = true
}

const formAddRules = computed(() => {
    return {
        comment_content: [
            { required: true, message: t('commentContentPlaceholder'), trigger: 'blur' }
        ]
    }
})
const save = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            const data = formAddData
            addComment(data).then(res => {
                loadContentList()
                addShowDialog.value = false
            }).catch(() => {
                addShowDialog.value = false
            })
        }
    })
}

// 推广
const spreadPopupRef = ref(null)

const spreadEvent = (data: any) => {
    let pagePath = ''
    if (data.content_type == 1) {
        pagePath = '/addon/sow_community/pages/image/detail' 
    } else {
        pagePath = '/addon/sow_community/pages/video/detail'
    }
    
    const columnName = "content_id"
    const columnValue = data.content_id
    const title = "内容推广"
    const folder = "sow_community"
    spreadPopupRef.value?.show(pagePath, columnName, columnValue, title,folder)
}

</script>

<style lang="scss" scoped>
</style>
