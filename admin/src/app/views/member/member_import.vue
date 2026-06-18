<template>
    <div class="main-container">
        <el-card class="box-card !border-none mb-[10px]" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
        </el-card>
        <el-card class="box-card !border-none mb-[10px]" shadow="never">
            <template #header>
                <div>导入</div>
            </template>
            <el-form :model="formData" label-width="100px" label-position="right" class="page-form">
                <el-form-item label="会员标签">
                    <el-select v-model="formData.memberLabel" placeholder="选择标签" class="input-width" multiple collapse-tags>
                        <el-option v-for="(item, index) in labelSelectData" :key="index" :label="item.label_name" :value="item.label_id" />
                    </el-select>
                    <div class="ml-[10px]">
                        <span class="cursor-pointer text-primary mr-[10px]" @click="handleAddLabel">新建标签</span>
                        <span class="cursor-pointer text-primary" @click="refreshLabels">刷新</span>
                    </div>
                </el-form-item>

                <el-form-item label="手机号重复时">
                    <div class="bg-[#f5f7fa] p-4 rounded w-full">
                        <div class="mb-3 flex items-center">
                            <span class="text-sm text-gray-600 w-[100px] text-right mr-2">基础资料：</span>
                            <el-radio-group v-model="formData.basicInfoAction">
                                <el-radio label="skip">跳过</el-radio>
                                <el-radio label="overwrite">覆盖</el-radio>
                            </el-radio-group>
                        </div>
                        <div class="text-sm text-gray-500 mb-3 ml-[108px]">包含昵称、性别、生日。</div>
                        <div class="mb-2 flex items-center">
                            <span class="text-sm text-gray-600 w-[100px] text-right mr-2">积分：</span>
                            <el-radio-group v-model="formData.pointAction">
                                <el-radio label="skip">跳过</el-radio>
                                <el-radio label="overwrite">覆盖</el-radio>
                                <el-radio label="add">增加</el-radio>
                            </el-radio-group>
                        </div>
                        <div class="mb-2 flex items-center">
                            <span class="text-sm text-gray-600 w-[100px] text-right mr-2">不可提现余额：</span>
                            <el-radio-group v-model="formData.nonWithdrawBalanceAction">
                                <el-radio label="skip">跳过</el-radio>
                                <el-radio label="overwrite">覆盖</el-radio>
                                <el-radio label="add">增加</el-radio>
                            </el-radio-group>
                        </div>
                    </div>
                </el-form-item>

                <el-form-item :label="t('文件上传')" prop="path">
                    <div class="flex items-center">
                        <div class="input-width">
                            <upload-file v-model="formData.filePath" api="sys/document/excel" />
                        </div>

                        <div @click="downloadTemplate" class="ml-[10px] text-[var(--el-color-primary)] cursor-pointer"> {{ t('下载模版') }}</div>
                    </div>
                </el-form-item>

                <el-form-item label="">
                    <el-button type="primary" @click="handleImport" :loading="importLoading" class="w-[100px]">导入</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card class="box-card !border-none" shadow="never">
            <template #header>
                <div>导入记录</div>
            </template>
            <!-- 导入记录 -->
            <div>
                <el-table :data="importTableData.data" size="large" v-loading="importTableData.loading">
                    <template #empty>
                        <span>{{ !importTableData.loading ? '暂无数据' : '' }}</span>
                    </template>
                    <el-table-column prop="create_time" label="导入时间" min-width="150" align="center" />
                    <el-table-column prop="total_num" label="导入总数" min-width="100" align="center" />
                    <el-table-column prop="success_num" label="导入成功总数" min-width="120" align="center" />
                    <el-table-column prop="fail_num" label="导入失败总数" min-width="120" align="center" />
                    <el-table-column prop="repeat_num" label="手机号重复数" min-width="120" align="center" />
                    <el-table-column prop="status_name" label="状态" min-width="100" align="center" />
                    <el-table-column label="耗时" min-width="100" align="center">
                        <template #default="{ row }">
                            <span>{{ formatDuration(row.duration) }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" min-width="150" align="center" fixed="right">
                        <template #default="{ row }">
                            <div class="flex flex-col items-center">
                                <el-button v-if="row.output" type="primary" link @click="downloadFile(row.output)">下载导入记录</el-button>
                                <el-button v-if="row.fail_output" type="primary" link @click="downloadFile(row.fail_output)" class="!ml-0">下载失败记录</el-button>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination  v-model:current-page="importTableData.page"  v-model:page-size="importTableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="importTableData.total" @size-change="loadImportList()" @current-change="loadImportList"  />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getMemberLabelAll, importMember, getMemberImportList } from '@/app/api/member'
import uploadFile from '@/components/upload-file/index.vue'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title || '会员导入'

// 表单数据
const formData = reactive({
    memberLabel: [],
    basicInfoAction: 'skip',
    pointAction: 'skip',
    nonWithdrawBalanceAction: 'skip',
    filePath: ''
})

// 导入loading状态
const importLoading = ref(false)

// 获取全部标签
const labelSelectData = ref([])
const getMemberLabelAllFn = async () => {
    try {
        labelSelectData.value = await (await getMemberLabelAll()).data
    } catch (error) {
        console.error('获取标签失败', error)
    }
}
getMemberLabelAllFn()

// 刷新标签
const refreshLabels = () => {
    getMemberLabelAllFn()
    ElMessage.success('已刷新')
}

// 新建标签
const handleAddLabel = () => {
    const url = router.resolve({
        path: '/admin/member/label'
    })
    window.open(url.href)
}

// 下载模板
const downloadTemplate = () => {
    const url = `${import.meta.env.VITE_IMG_DOMAIN || location.origin}/static/resource/member_import/member_import_template.xlsx`
    window.open(url)
}

// 导入记录数据
const importTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: []
})

// 导入处理
const handleImport = () => {
    if (!formData.filePath) {
        ElMessage.warning('请先选择要导入的文件')
        return
    }
    importLoading.value = true

    const params = {
        member_label: formData.memberLabel,
        mobile_repeat_rule: {
            basic: formData.basicInfoAction,
            point: formData.pointAction,
            balance: formData.nonWithdrawBalanceAction
        },
        path: formData.filePath
    }

    importMember({ data: params }).then(res => {
        importLoading.value = false
        loadImportList()
    }).catch(() => {
        importLoading.value = false
    })
}

// 加载导入记录列表
const loadImportList = (page: number = 1) => {
    importTableData.loading = true
    importTableData.page = page

    getMemberImportList({
        page: importTableData.page,
        limit: importTableData.limit
    }).then(res => {
        importTableData.loading = false
        importTableData.data = res.data.data
        importTableData.total = res.data.total
    }).catch(() => {
        importTableData.loading = false
    })
}

// 下载记录文件
const downloadFile = (url: string) => {
    window.open(`${import.meta.env.VITE_IMG_DOMAIN || location.origin}/${url}`)
}

// 格式化耗时
const formatDuration = (seconds: number) => {
    if (!seconds) return '0秒'
    if (seconds < 60) return seconds + '秒'
    const minutes = Math.floor(seconds / 60)
    const remainingSeconds = seconds % 60
    return `${minutes}分${remainingSeconds}秒`
}

loadImportList()

</script>

<style lang="scss" scoped>
:deep(.el-form-item) {
    margin-bottom: 18px;
}
</style>
