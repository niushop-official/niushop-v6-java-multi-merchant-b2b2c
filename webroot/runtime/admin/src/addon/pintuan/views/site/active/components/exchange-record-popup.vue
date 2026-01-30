<template>
    <el-drawer v-model="showDialog" :title="t('兑换列表')" direction="rtl" :before-close="handleClose" size="1300px">
        <div class="main-container">
            <div class="flex justify-between items-center mb-[10px]">
                <div class="mb-[10px] flex items-center">
                    <el-checkbox v-model="toggleCheckbox" size="large" class="px-[14px]" @change="toggleChange" :indeterminate="isIndeterminate" />
                    <el-button @click="batchDeleteFn" size="small">{{t("批量删除")}}</el-button>
                </div>
                <div>
                    <el-button type="primary" @click="showDialogImport=true">{{ t("导入兑换码") }}</el-button>
                    <el-button type="primary" @click="clearFn">{{ t("清除兑换码") }}</el-button>
                </div>

            </div>
         
            <el-table :data="tableData.data" size="large" v-loading="tableData.loading" ref="tableRef" @selection-change="handleSelectionChange">
                <template #empty>
                    <span>{{ !tableData.loading ? t("emptyData") : "" }}</span>
                </template>
                <el-table-column type="selection" width="55"  :selectable="isSelectable"  />
                    <el-table-column prop="code" :label="t('兑换码')" min-width="180">
                        <template #default="{ row }">
                            {{row.code}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="code" :label="t('状态')" min-width="180">
                        <template #default="{ row }">
                            {{row.status_name}}
                        </template>
                    </el-table-column>
                
                    <el-table-column prop="code" :label="t('领取人')" min-width="180">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer" @click="detailEvent(row.member?.member_id)" v-if="row.member">
                                <div class="min-w-[50px] h-[50px] flex items-center justify-center">
                                    <el-image v-if="row.member?.headimg" class="w-[50px] h-[50px]" :src="img(row.member?.headimg)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[50px] h-[50px] rounded-full" src="@/app/assets/images/member_head.png" alt="">
                                            </div>
                                        </template>
                                    </el-image>
                                    <img class="w-[50px] h-[50px] rounded-full" v-else src="@/app/assets/images/member_head.png" alt="">
                                </div>
                                <div class="ml-2">
                                    <span :title="row.member?.nickname " class="multi-hidden">{{row.member?.nickname}}</span>
                                </div>
                            </div>
                            <div v-else>
                                --
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="code" :label="t('领取时间')" min-width="180">
                        <template #default="{ row }">
                            {{row.status=='received' ? row.received_time : '--'}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="code" :label="t('领取单号')" min-width="180">
                        <template #default="{ row }">
                            {{row.status=='received' ? row.order_info?.order_no : '--'}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="code" :label="t('过期时间')" min-width="180">
                        <template #default="{ row }">
                            {{row.expire_time == 0 ? '永久有效' : row.expire_time}}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" fixed="right" align="right" width="120">
                        <template #default="{ row }">
                            <el-button v-if="row.status!='received'" type="primary" link @click="deleteEvent(row.id)">{{ t("delete") }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="tableData.page" v-model:page-size="tableData.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="tableData.total"
                        @size-change="loadList()" @current-change="loadList" />
                </div>
        </div>
        <el-dialog v-model="showDialogImport" :title="t('导入兑换码')" width="600px" :destroy-on-close="true" :close-on-click-modal="false">
            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" scroll-to-error class="page-form" v-loading="loading">
                <el-form-item :label="t('兑换码文件地址')" prop="exchange_import_path">
                    <div>
                        <upload-file v-model="formData.exchange_import_path" api="sys/document/excel" />
                        <div @click="examineTemplate" class="form-tip mt-[5px] !text-[var(--el-color-primary)] cursor-pointer">
                            {{ t('下载模板文件') }}
                        </div>
                        <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('兑换码需上传模版文件，注意不要重复,最多上传2000条') }}</div>
                    </div>
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="onSave()">{{ t('save') }}</el-button>
                    <el-button @click="showDialogImport=false">{{ t('cancel') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </el-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { getPintuanReceiveList, pintuanCodeImport, pintuanCodeDelete, pintuanCodeClear } from '@/addon/pintuan/api/site/active'
import { img } from '@/utils/common'
import { useRoute, useRouter } from 'vue-router'
import { FormInstance, ElMessageBox, ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

let pintuan_id = ''
const showDialog = ref(false)
const showDialogImport = ref(false)
const loading = ref(false)
const formData = reactive({
    exchange_import_path: ''
})
const formRef = ref<FormInstance>()
const formRules = computed(() => {
    return {
        exchange_import_path: [
            { required: true, message: t('请输入兑换码文件地址'), trigger: 'blur' }
        ],
    }
})
// 表单内容
const tableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: false,
    data: [],
})
const loadList = (page: number = 1) => {
    tableData.loading = true
    tableData.page = page

    getPintuanReceiveList({
        page: tableData.page,
        limit: tableData.limit,
        pintuan_id: pintuan_id,
    }).then((res) => {
        tableData.loading = false
        tableData.data = res.data.data
        tableData.total = res.data.total
    }).catch(() => {
        tableData.loading = false
    })
}
const handleClose = (done: () => void) => {
    showDialog.value = false
}
// 查看会员详情
const detailEvent = (member_id:number) => {
    const routeData = router.resolve(`/member/detail?id=${member_id}`)
    window.open(routeData.href, ' blank')
}

const examineTemplate = () => {
    window.open(`${import.meta.env.VITE_IMG_DOMAIN || location.origin}/addon/pintuan/import/exchange_code.xlsx`)
}

const isSelectable = (row: any) => {
    return row.status != 'received'
}
// 批量复选框
const toggleCheckbox = ref()

// 复选框中间状态
const isIndeterminate = ref(false)

// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    tableRef.value.toggleAllSelection()
}

const tableRef = ref()

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = val

    toggleCheckbox.value = false
    if (
        multipleSelection.value.length > 0 &&
        multipleSelection.value.length < tableData.data.length
    ) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == tableData.data.length) {
        toggleCheckbox.value = true
    }
}

// 删除
let deleteRepeat = false
const deleteEvent = (data:number) => {
    ElMessageBox.confirm(t('确定要删除该兑换码吗？'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        if (deleteRepeat) return
        deleteRepeat = true
        const pintuanId = Number(pintuan_id)
        pintuanCodeDelete({pintuan_id: pintuanId, ids: [data]}).then(() => {
            deleteRepeat = false
            loadList()
        }).catch(() => {
            deleteRepeat = false
        })
    })
}

// 批量删除
const repeat = ref(false)
const batchDeleteFn = () => {
    if (multipleSelection.value.length == 0) {
        ElMessage({
            type: 'warning',
            message: `${t('请先选择要删除的兑换码')}`
        })
        return
    }

    ElMessageBox.confirm(t('确定删除选中的兑换码吗？'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }).then(() => {
        if (repeat.value) return
        repeat.value = true

        const ids: any = []
        multipleSelection.value.forEach((item: any) => {
            ids.push(item.id)
        })
        const pintuanId = Number(pintuan_id)
        pintuanCodeDelete({pintuan_id: pintuanId, ids: ids}).then(() => {
            loadList()
            repeat.value = false
        }).catch(() => {
            repeat.value = false
        })
    })
}

const clearFn = () => {
    ElMessageBox.confirm(t('确定要清除该活动的兑换码吗？请谨慎操作兑换码删除功能。删除后，若用户下单时无可用兑换码，系统将自动拦截订单，禁止完成下单流程。'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }).then(() => {
        if (repeat.value) return
        repeat.value = true

        const pintuanId = Number(pintuan_id)
        pintuanCodeClear({ pintuan_id: pintuanId }).then(() => {
            loadList()
            repeat.value = false
        }).catch(() => {
            repeat.value = false
        })
    })
}
const setFormData = async (row: any = null) => {
    pintuan_id = row.goods_data.pintuan_id
    loadList()
}

const onSave = async () => {
    if (loading.value) return
    await formRef.value?.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const pintuanId = Number(pintuan_id)
            let obj ={
                pintuan_id:pintuanId,
                path: formData.exchange_import_path,
            }
            pintuanCodeImport(obj).then(() => {
                showDialogImport.value = false
                loadList()
                loading.value = false
                formData.exchange_import_path = ''
            }).catch(() => {
                loading.value = false
                formData.exchange_import_path = ''
            })
        }
    })
}

defineExpose({
    showDialog,
    setFormData
})
</script>
<style lang="scss" scoped></style>
