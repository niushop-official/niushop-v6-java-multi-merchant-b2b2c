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
                <el-form-item label="商品类型">
                    <el-radio-group v-model="formData.goodsType">
                        <el-radio :label="index" v-for="(item, index) in goodsTypeSelectData" :key="index">{{ item }}</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item :label="t('文件上传')" prop="path">
                    <div class="flex items-center">
                        <div class="input-width" >
                            <upload-file v-model="formData.filePath" api="sys/document/excel" />
                        </div>
                        <div class="ml-[10px]">
                            <span @click="downloadRealGoodsTemplate" class="cursor-pointer text-primary mr-[10px]">{{ t('downloadRealGoodsTemplate') }}</span>
                            <span @click="downloadVirtualGoodsTemplate" class="cursor-pointer text-primary">{{ t('downloadVirtualGoodsTemplate') }}</span>
                        </div>
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
                    <el-table-column prop="goods_type_name" label="商品类型" min-width="120" align="center" />
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
                    <el-pagination v-model:current-page="importTableData.page" v-model:page-size="importTableData.limit" layout="total, sizes, prev, pager, next, jumper" :total="importTableData.total" @size-change="loadImportList()" @current-change="loadImportList" />
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsTypeAll, getImportRecordPage, addRecord, importExec } from '@/addon/mall/views/site/goods/public/js/goodsImport'
import uploadFile from '@/components/upload-file/index.vue'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title || '商品导入'

// 表单数据
const formData = reactive({
    goodsType: 'real',
    filePath: ''
})

// 导入loading状态
const importLoading = ref(false)
// 获取类型
const goodsTypeSelectData = ref([])
const getGoodsTypeAllFn = async () => {
    try {
        goodsTypeSelectData.value = await (await getGoodsTypeAll()).data
    } catch (error) {
        console.error('获取商品类型失败', error)
    }
}
getGoodsTypeAllFn()

// 下载实物商品模版
const downloadRealGoodsTemplate = () => {
    const url = `${import.meta.env.VITE_IMG_DOMAIN || location.origin}/addon/mall/import/mall_real_goods_import_template.xlsx`
    const link = document.createElement('a')
    link.href = url
    link.download = '实物商品模板.xlsx'
    link.click()
}
// 下载虚拟商品模版
const downloadVirtualGoodsTemplate = () => {
    const url = `${import.meta.env.VITE_IMG_DOMAIN || location.origin}/addon/mall/import/mall_virtual_goods_import_template.xlsx`
    const link = document.createElement('a')
    link.href = url
    link.download = '虚拟商品模板.xlsx'
    link.click()
}

// 导入处理
const handleImport = () => {
    if (!formData.goodsType) {
        ElMessage.warning('请先选择商品类型')
        return
    }
    if (!formData.filePath) {
        ElMessage.warning('请先选择要导入的文件')
        return
    }

    importLoading.value = true

    const params = {
        goods_type: formData.goodsType,
        file_path: formData.filePath
    }

    addRecord(params).then(res => {
        const data = {
            record_id: res.data.record_id
        }
        importExec(data).then(res => {
            importLoading.value = false
            formData.filePath = ''
            loadImportList()
        }).catch(() => {
            importLoading.value = false
        })
    }).catch(() => {
        importLoading.value = false
    })
}

// 导入记录数据
const importTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: []
})
// 加载导入记录列表
const loadImportList = (page: number = 1) => {
    importTableData.loading = true
    importTableData.page = page

    getImportRecordPage({
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
loadImportList()

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

</script>
<style lang="scss" scoped>

</style>