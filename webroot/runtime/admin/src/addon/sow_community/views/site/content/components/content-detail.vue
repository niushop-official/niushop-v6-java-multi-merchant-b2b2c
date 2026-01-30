<template>
    <el-drawer v-model="showDialog" :title="popTitle" direction="rtl" :before-close="handleClose" class="content-detail-drawer">
        <div class="main-container" v-loading="loading">
            <el-tabs v-model="activeName" class="pb-[10px]">
                <el-tab-pane :label="t('baseInfo')" name="basics" />
                <el-tab-pane :label="t('cowry')" name="cowry" />
                <el-tab-pane :label="t('comment')" name="comment" />
            </el-tabs>
            <div v-if="activeName == 'basics'">
                <el-form :model="formData" label-width="100px" ref="formRef" class="page-form" v-if="!loading" label-position="left">
                    <el-row class="row-bg px-[30px] mb-[20px]" :gutter="20">
                        <el-col :span="12">
                            <el-form-item :label="t('contentCover')">
                                <div class="flex items-center min-w-[50px] mr-[10px]">
                                    <el-image   class="w-[50px] h-[50px]" :src="img(formData.content_cover)" fit="cover"  :zoom-rate="1.2" :max-scale="7" :min-scale="0.2" :preview-src-list="[img(formData.content_cover)]" :hide-on-click-modal="true">
                                        <template #error>
                                            <img class="w-[50px] h-[50px]"  src="@/addon/sow_community/assets/default_img.png" it="contain">
                                        </template>
                                    </el-image>
                                </div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item :label="t('contentTitle')">
                                <div class="input-width">{{ formData.content_title }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12" v-if="formData.member">
                            <el-form-item :label="t('contentAuthor')">
                                <div class="input-width">{{ formData.member.nickname }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item :label="t('topicName')">
                                <div class="input-width">{{ formData.topic_name }}</div>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item :label="t('content')">
                                <div>{{ formData.content }}</div>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <div v-if="activeName == 'cowry'">
                <el-table :data="formData.treasure_list" size="large"  ref="cwryTableRef" max-height="700">
                    <template #empty>
                        <span>{{ !formData?.treasure_list?.length ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column :label="t('cwryInfo')" min-width="300">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer">
                                <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                                    <el-image v-if="row.treasure_image" class="w-[70px] h-[70px]" :src="img(row.treasure_image)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" fit="contain" />
                                </div>
                                <div class="ml-2  flex flex-col items-start">
                                    <span :title="row.goods_name" class="multi-hidden">{{ row.treasure_name }}</span>
                                    <span class="text-primary text-[12px]">{{ row.treasure_sub_name }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="treasure_price" :label="t('price')" min-width="120" />
                </el-table>
            </div>
            <div v-if="activeName == 'comment'">
                <el-table :data="comment.data" size="large"  ref="cwryTableRef"  v-loading="comment.loading">
                    <template #empty>
                        <span>{{ !comment.loading ? t('emptyData') : '' }}</span>
                    </template>
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
                            {{ row.content.content_title }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="comment_content" :label="t('commentContent')" min-width="180" />
                    <el-table-column prop="reply_num" :label="t('replyNum')" min-width="120" />
                    <el-table-column prop="like_num" :label="t('likeNum')" min-width="120" />
                    <el-table-column prop="create_time" :label="t('createTime')" min-width="120" />
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="comment.page" v-model:page-size="comment.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="comment.total"
                        @size-change="getCommentListFn()" @current-change="getCommentListFn" />
                </div>
            </div>
        </div>
    </el-drawer>
</template>

<script lang="ts" setup>
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { getContentInfo } from '@/addon/sow_community/api/site/content'
import { getCommentList } from '@/addon/sow_community/api/site/comment'

const popTitle: string = '内容详情'
const activeName = ref('basics')
const loading = ref(false)
const showDialog = ref(false)
const contentId = ref<any>('')
const formData: Record<string, any> | null = ref(null)
// 评论
const comment = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
    searchParam: {
        content_id: contentId
    }
})

const handleClose = (done: () => void) => {
    showDialog.value = false
}
const setFormData = async (row: any = null) => {
    contentId.value = row.id
    formData.value = null
    activeName.value = 'basics'
    getContentInfoFn()
    getCommentListFn()
}

const getContentInfoFn = async () => {
    loading.value = true
    if (contentId.value) {
        await getContentInfo(contentId.value).then(({ data }) => {
            formData.value = data
            formData.value.topic_name = data.topic_list && data.topic_list.map((item: any) => { return item.topic_name }).join(',')
            loading.value = false
        }).catch(() => {
            loading.value = false
        })
    } else {
        loading.value = false
    }
}

const getCommentListFn = (page: number = 1) => {
    comment.loading = true
    comment.page = page
    getCommentList({
        page: comment.page,
        limit: comment.limit,
        ...comment.searchParam
    }).then((res:any) => {
        comment.loading = false
        comment.data = res.data.data
        comment.total = res.data.total
    }).catch(() => {
        comment.loading = false
    })
}

defineExpose({
    showDialog,
    setFormData
})

</script>

<style lang="scss">
.content-detail-drawer{
    width: 1000px !important;
}

</style>
