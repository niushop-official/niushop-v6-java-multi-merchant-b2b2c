<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="commentTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('contentTitle')" prop="content_title">
                        <el-input v-model.trim="commentTable.searchParam.content_title" :placeholder="t('contentTitlePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('commentContent')" prop="comment_content">
                        <el-input v-model.trim="commentTable.searchParam.comment_content" :placeholder="t('commentContentPlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('memberInfo')" prop='keyword'>
                        <el-input class="w-[200px]" v-model.trim="commentTable.searchParam.keyword"  :placeholder="t('memberInfoPlaceholder')"/>
                    </el-form-item>
                    <el-form-item :label="t('status')" prop="status">
                        <el-select v-model="commentTable.searchParam.status" :placeholder="t('statusPlaceholder')" clearable>
                            <el-option  v-for="(item,index) in statusList" :key="index" :label="item.name" :value="item.value" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="commentTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('startDate')" :end-placeholder="t('endDate')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadCommentList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <div class="mt-[10px]">
                <div class="mb-[10px] flex items-center">
                    <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                    <el-button @click="batchDeleteEvent" size="small" >{{t("batchDelete")}}</el-button>
                    <el-button @click="batchAuditEvent(2)" size="small"  >{{t("batchAdopt")}}</el-button>
                    <el-button @click="batchAuditEvent(-1)" size="small"  >{{t("batchRefuse")}}</el-button>
                </div>
                <el-table :data="commentTable.data" size="large" v-loading="commentTable.loading" ref="commentsListTableRef" @selection-change="handleSelectionChange">
                    <template #empty>
                        <span>{{ !commentTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" />
                    <el-table-column prop="nickname" :show-overflow-tooltip="true" :label="t('memberInfo')" min-width="170">
                        <template #default="{ row }">
                            <div class="flex items-center" v-if="row.member">
                                <div class="mr-[10px] rounded-full w-[50px] h-[50px] flex items-center justify-center">
                                    <img class="max-w-[50px] max-h-[50px]" v-if="row.member.headimg" :src="img(row.member.headimg)" alt="">
                                    <img class="max-w-[50px] max-h-[50px]" v-else src="@/app/assets/images/member_head.png" alt="">
                                </div>
                                <div class="flex flex-col">
                                    <span>{{ row.member.nickname || '' }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column  :label="t('contentTitle')" min-width="120">
                        <template #default="{ row }">
                           <div v-if="row.content"> {{ row.content.content_title }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="comment_content" :label="t('commentContent')" min-width="120" />
                    <el-table-column prop="reply_num" :label="t('replyNum')" min-width="120" />
                    <el-table-column prop="like_num" :label="t('likeNum')" min-width="120" />
                    <el-table-column prop="status_name"  :label="t('status')" min-width="120" />
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="120" />
                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="deleteEvent(row.comment_id)">{{ t('delete') }}</el-button>
                            <el-button type="primary" link @click="adoptEvent(row.comment_id)" v-if="row.status == 1">{{ t('adopt') }}</el-button>
                            <el-button type="primary" link @click="refuseEvent(row.comment_id)" v-if="row.status == 1">{{ t('refuse') }}</el-button>
                            <el-button type="primary" link @click="replyEvent(row)" v-if="row.status == 0 || row.status == 2">{{ t('reply') }}</el-button>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="commentTable.page" v-model:page-size="commentTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="commentTable.total"
                        @size-change="loadCommentList()" @current-change="loadCommentList" />
                </div>
            </div>
        </el-card>
        <el-dialog v-model="replyShowDialog" :title="t('commentContentTitle')" width="460px" class="diy-dialog-wrap" :destroy-on-close="true">
            <el-form :model="formData" label-width="90px" ref="formRef" :rules="formRules" class="page-form">
                <el-form-item :label="t('commentContent')" prop="comment_content">
                    <el-input v-model.trim="formData.comment_content" type="textarea" rows="4" clearable
                        :placeholder="t('commentContentPlaceholder')" class="input-width" maxlength="200" show-word-limit />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="replyShowDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { getCommentList, getCommentStatus, deleteComment, auditComment, addComment ,batchDelteComment,batchAuditComment} from '@/addon/sow_community/api/admin/comment'
import { img } from '@/utils/common'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRoute } from 'vue-router'

const route = useRoute()

const pageName = route.meta.title
const commentTable = reactive<any>({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        content_title: '',
        comment_content: '',
        keyword: '',
        status: '',
        create_time: []
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取社区分类列表
 */
const loadCommentList = (page: number = 1) => {
    commentTable.loading = true
    commentTable.page = page

    getCommentList({
        page: commentTable.page,
        limit: commentTable.limit,
        ...commentTable.searchParam
    }).then((res: any) => {
        commentTable.loading = false
        commentTable.data = res.data.data
        commentTable.total = res.data.total
    }).catch(() => {
        commentTable.loading = false
    })
}
loadCommentList()

const statusList = ref<any>([])
const getCommentStatusFn = () => {
    getCommentStatus().then((res: any) => {
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

getCommentStatusFn()


// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    commentsListTableRef.value.toggleAllSelection()
}

const commentsListTableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (multipleSelection.value.length > 0 && multipleSelection.value.length < commentTable.data.length) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == commentTable.data.length && commentTable.data.length && multipleSelection.value.length) {
        toggleCheckbox.value = true
    }
}

// 批量删除
const batchDeleteEvent = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: "warning",
            message: `${ t("batchEmptySelectedGoodsTips") }`,
        })
        return
    }

    ElMessageBox.confirm(t("batchDeleteTips"), t("warning"), {
        confirmButtonText: t("confirm"),
        cancelButtonText: t("cancel"),
        type: "warning"
    }).then(() => {
        const comment_ids: any = []
        multipleSelection.value.forEach((item: any) => {
            comment_ids.push(item.comment_id)
        })

        batchDelteComment({
            ids: comment_ids
        }).then(() => {
            loadCommentList()
        }).catch(() => {
        })
    })
}

// 批量审核
const batchAuditEvent = (status: number) => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: "warning",
            message: t("batchEmptySelectedGoodsTips"),
        });
        return;
    }

    const confirmMessage = status === 2 ? t("auditAdoptTips") : t("auditRefuseTips");

    ElMessageBox.confirm(confirmMessage, t("warning"), {
        confirmButtonText: t("confirm"),
        cancelButtonText: t("cancel"),
        type: "warning",
    }).then(() => {
        const comment_ids: any = []
        multipleSelection.value.forEach((item: any) => {
            comment_ids.push(item.comment_id)
        })

        batchAuditComment({
            ids: comment_ids,
            status: status,
        }).then(() => {
            loadCommentList();
        }).catch(() => {

        });
    });
};


/**
 * 删除社区分类
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('commentDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteComment(id).then(() => {
            loadCommentList()
        }).catch(() => {
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadCommentList()
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
        auditComment({
            comment_id: id,
            status: 2
        }).then(() => {
            loadCommentList()
        }).catch(() => {
        })
    })
}

// 审核拒绝
const refuseEvent = (id: number) =>{
    ElMessageBox.confirm(t('auditAdoptTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        auditComment({
            comment_id: id,
            status: -1
        }).then(() => {
            loadCommentList()
        }).catch(() => {
        })
    })
}

// 评论回复
const replyShowDialog = ref(false)
const initialFormData = {
    content_id: 0,
    parent_comment_id: '',
    reply_member_id: '',
    reply_site_id: '',
    comment_content: '',
    level: 0
}
const formData: Record<string, any> = reactive({ ...initialFormData })
const formRef = ref<FormInstance>()

const replyEvent = (data: any) => {
    formData.content_id = data.content_id
    formData.parent_comment_id = data.parent_comment_id == 0 ? data.comment_id : data.parent_comment_id
    formData.reply_member_id = data.parent_comment_id == 0 ? 0 : data.member_id
    formData.reply_site_id = data.site_id
    formData.comment_content = ''
    formData.level = data.level
    replyShowDialog.value = true
}
const formRules = computed(() => {
    return {
        refuse_reason: [
            { required: true, message: t('commentContentPlaceholder'), trigger: 'blur' }
        ]
    }
})

const confirm = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            const data = formData
            addComment(data).then(res => {
                loadCommentList()
                replyShowDialog.value = false
            }).catch(() => {
                replyShowDialog.value = false
            })
        }
    })
}
</script>

<style lang="scss" scoped>
</style>
