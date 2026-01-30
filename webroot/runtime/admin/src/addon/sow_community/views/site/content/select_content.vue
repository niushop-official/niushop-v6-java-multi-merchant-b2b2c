<template>
    <div>
        <el-form :inline="true" :model="tableData.searchParam" ref="searchFormRef">
            <el-form-item :label="t('contentTitle')" prop="content_title" class="form-item-wrap">
                <el-input v-model.trim="tableData.searchParam.content_title" :placeholder="t('contentTitlePlaceholder')" />
            </el-form-item>
            <el-form-item :label="t('contentType')" prop="content_type">
                <el-select v-model="tableData.searchParam.content_type" :placeholder="t('contentTypePlaceholder')" clearable>
                    <el-option  :label="t('imageText')" :value="1" />
                    <el-option  :label="t('shortVideo')" :value="2" />
                </el-select>
            </el-form-item>
            <el-form-item class="form-item-wrap">
                <el-button type="primary" @click="loadList()">{{ t('search') }}</el-button>
                <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
            </el-form-item>
        </el-form>
        <el-table :data="tableData.data" size="large" ref="tableRef" v-loading="tableData.loading">
            <template #empty>
                <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
            </template>
            <el-table-column min-width="7%">
                <template #default="{ row }">
                    <el-checkbox v-model="row.checked" @change="handleCheckChange($event,row)" />
                </template>
            </el-table-column>
            <el-table-column  :label="t('contentCover')" width="140px">
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
            <el-table-column prop="content_title" :label="t('contentTitle')" min-width="20%" />
            <el-table-column  :label="t('contentType')" min-width="10%">
                <template #default="{ row }">
                   <div>{{ row.content_type == 1 ? t('imageText') : t('shortVideo') }}</div>
                </template>
            </el-table-column>
            <el-table-column  :label="t('author')" min-width="10%">
                <template #default="{ row }">
                    <div v-if="row.member">{{ row.member.nickname }}</div>
                </template>
            </el-table-column>
            <!-- <el-table-column prop="view_num" :label="t('viewNum')" min-width="10%" /> -->
            <!-- <el-table-column prop="like_num" :label="t('likeNum')" min-width="10%"  />
            <el-table-column prop="share_num" :label="t('shareNum')" min-width="10%"  />
            <el-table-column prop="comment_num" :label="t('commentNum')" min-width="10%"  /> -->
        </el-table>
        <div class="mt-[16px] flex justify-end">
            <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit"
                           layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
                           @size-change="loadList()" @current-change="loadList" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, nextTick } from 'vue'
import { t } from '@/lang'
import { getContentSelectList} from '@/addon/sow_community/api/site/content'
import { FormInstance, ElMessage } from "element-plus";
import { img } from '@/utils/common'

const prop = defineProps({
    content_id: {
        type: [Number, String],
        default: 0
    }
})

const searchFormRef = ref<FormInstance>()
const tableRef = ref();

const tableData: any = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        content_title: '',
        content_type: '',
        verify_content_ids: []
    }
})

// 已选数据
const selectData: any = reactive({
    content_id: prop.content_id
})

// 获取优惠券列表
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    if (selectData.content_id) {
        tableData.searchParam.verify_content_ids = [selectData.content_id]
    }

    getContentSelectList({
        page: tableData.page,
        limit: tableData.limit,
        ...tableData.searchParam
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data.data

        tableData.data.forEach((item: any) => {
            item.checked = item.content_id == selectData.content_id
        })
        tableData.total = res.data.total
        setGoodsSelected()
    }).catch(() => {
        tableData.loading = false
    })
}

loadList()

const handleCheckChange = (isSelect: any, row: any) => {
    if (isSelect) {
        selectData.content_id = row.content_id
    } else {
        selectData.content_id = 0 // 未选中，移除当前
    }
    setGoodsSelected()
}

// 表格设置选中状态
const setGoodsSelected = () => {
    nextTick(() => {
        for (let i = 0; i < tableData.data.length; i++) {
            tableData.data[i].checked = false
            if (selectData.content_id == tableData.data[i].content_id) {
                tableData.data[i].checked = true
                Object.assign(selectData, tableData.data[i])
            }
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadList()
}

const getData = () => {
    if (selectData.content_id == 0) {
        ElMessage({
            type: 'warning',
            message: `${ t('couponSelectContentTips') }`
        });
        return;
    }

    let url = '';
    if (selectData.content_type == 1) {
        url = `/addon/sow_community/pages/image/detail?content_id=${ selectData.content_id }`;
    } else if (selectData.content_type == 2) {
        url = `/addon/sow_community/pages/video/detail?content_id=${ selectData.content_id }`;
    }

    return {
        name: 'SOW_COMMUNITY_CONTENT',
        title: selectData.content_title,
        url,
        action: '',
        content_id: selectData.content_id
    };
};

defineExpose({
    getData
})
</script>

<style lang="scss" scoped>
.form-item-wrap {
    margin-right: 10px !important;
    margin-bottom: 10px !important;

    &.last-child {
        margin-right: 0 !important;
    }
}
</style>
