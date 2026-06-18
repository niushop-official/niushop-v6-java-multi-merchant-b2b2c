<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="accounTable.searchParam" ref="searchFormRef">
<!--                    <el-form-item :label="t('店铺名称')" prop="site_name">
                        <el-input v-model="accounTable.searchParam.site_name"  :placeholder="t('请输入店铺名称')" />
                    </el-form-item>-->
                    <el-form-item :label="t('入驻平台')" prop="type">
                        <el-select v-model="accounTable.searchParam.type" clearable :placeholder="t('请选择入驻平台')" class="input-width">
                            <el-option :label="t('全部')" value="" />
                            <el-option :label="item" :value="index" v-for="(item, index) in typeList" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('业务入账状态')" prop="is_settle">
                        <el-select v-model="accounTable.searchParam.is_settle" clearable :placeholder="t('请选择业务入驻状态')" class="input-width">
                            <el-option :label="t('全部')" value="" />
                            <el-option :label="item.name" :value="item.id" v-for="(item, index) in settleList" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('开启状态')" prop="status">
                        <el-select v-model="accounTable.searchParam.status" clearable :placeholder="t('请选择状态')" class="input-width">
                            <el-option :label="t('全部')" value="" />
                            <el-option :label="item" :value="index" v-for="(item, index) in statusList" :key="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('createTime')" prop="create_time">
                        <el-date-picker v-model="accounTable.searchParam.create_time" type="datetimerange" value-format="YYYY-MM-DD HH:mm:ss" :start-placeholder="t('开始时间')" :end-placeholder="t('结束时间')" />
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadAccountList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                        <el-button type="primary" @click="bindUserEvent">{{ t('绑定商户') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <div>
                <el-table :data="accounTable.data" size="large" v-loading="accounTable.loading">
                    <template #empty>
                        <span>{{ !accounTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="site_name" :label="t('店铺名称')" min-width="120" />
                    <el-table-column prop="type_name" :label="t('入驻平台')" min-width="120" />
                    <el-table-column prop="user_type" :label="t('入驻类型 ')" min-width="120" />
                    <el-table-column prop="extend_no" :label="t('分账编号')" min-width="120" />
                    <el-table-column prop="status_name" :label="t('开启状态')" min-width="120" />
                    <el-table-column prop="settle_desc" :label="t('业务入账状态')" min-width="120" />
                    <el-table-column prop="create_time" :label="t('创建时间')" min-width="120" />
                    <el-table-column :label="t('operation')" align="right" fixed="right" width="190">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row.id)" v-if="Object.keys(row.infos).length > 0">{{ t('详情') }}</el-button>
                            <el-button type="primary" link @click="editEvent(row)">{{ t('编辑') }}</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="accounTable.page" v-model:page-size="accounTable.limit" layout="total, sizes, prev, pager, next, jumper"  :total="accounTable.total" @size-change="loadAccountList()"  @current-change="loadAccountList" />
                </div>
            </div>
        </el-card>

        <el-dialog v-model="bindUserDialogVisible" :title="t('绑定商户')" width="500px">
            <el-form :model="bindUserForm" label-width="100px" ref="bindUserFormRef" :rules="bindUserRules" class="page-form">
                <el-form-item :label="t('入驻平台')" prop="site_id">
                    <el-input v-model="bindUserForm.type_name" disabled class="input-width" />
                </el-form-item>
                <el-form-item :label="t('店铺名称')" prop="site_id">
                    <el-select v-model="bindUserForm.site_id" :placeholder="t('请输入店铺名称')"  class="input-width">
                        <el-option v-for="item in shopList" :key="item.site_id" :label="item.site_name" :value="item.site_id" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('商户编号')" prop="extend_no">
                    <el-input v-model="bindUserForm.extend_no" :placeholder="t('请输入商户编号')" class="input-width" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="bindUserDialogVisible = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="confirmBindUser(bindUserFormRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>

        <el-dialog v-model="editDialogVisible" :title="t('编辑商户')" width="500px">
            <el-form :model="editForm" label-width="100px" ref="editFormRef" :rules="editRules" class="page-form">
                <el-form-item :label="t('商户编号')" prop="extend_no">
                    <el-input v-model="editForm.extend_no" :placeholder="t('请输入商户编号')" class="input-width" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editDialogVisible = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="confirmEdit(editFormRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, FormInstance } from 'element-plus'
import { getAccount, getAccountStatus, getAccountType, getAccountSettle, addByHandle, getShopList, editAccount } from '@/app/api/shop_admin'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title

const typeList = ref<any>([])
const statusList = ref<any>([])
const settleList = ref<any>([])
const accounTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        site_name: '',
        type: '',
        status: '',
        is_settle: '',
        create_time: []
    }
})
const searchFormRef = ref<FormInstance>()

const initData = () => {
    getAccountType().then((res: any) => {
        typeList.value = res.data
    })
    getAccountStatus().then((res: any) => {
        statusList.value = res.data
    })

    getAccountSettle().then((res: any) => {
        settleList.value = res.data
    })
}
initData()
/**
 * 获取申请列表
 */
const loadAccountList = (page: number = 1) => {
    accounTable.loading = true
    accounTable.page = page

    getAccount({
        page: accounTable.page,
        limit: accounTable.limit,
        ...accounTable.searchParam

    }).then((res: any) => {
        accounTable.loading = false
        accounTable.data = res.data.data
        accounTable.total = res.data.total
    }).catch(() => {
        accounTable.loading = false
    })
}
loadAccountList()

/**
 * 查看详情
 * @param id
 */
const detailEvent = (id: number) => {
    router.push({ path: '/shop/account_info', query: { id } })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return

    formEl.resetFields()
    loadAccountList()
}

// 绑定商户
const bindUserDialogVisible = ref(false)
const bindUserForm = reactive({
    type_name: '汇付分账',
    site_id: '',
    extend_no: ''
})
const bindUserFormRef = ref<FormInstance>()
const bindUserRules = reactive({
    site_id: [
        { required: true, message: t('请输入店铺名称'), trigger: 'blur' }
    ],
    extend_no: [
        { required: true, message: t('请输入商户编号'), trigger: 'blur' }
    ]
})
const shopList = ref<any>([])

const getShopListFn = () => {
    getShopList({}).then((res: any) => {
        shopList.value = res.data
    })
}
getShopListFn()
const bindUserEvent = () => {
    bindUserDialogVisible.value = true
    bindUserForm.site_id = ''
    bindUserForm.extend_no = ''
}

const confirmBindUser = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
        if (valid) {
            addByHandle(bindUserForm).then((res) => {
                bindUserDialogVisible.value = false
                if (res.data.is_sure_cover) {
                    ElMessageBox.confirm('当前站点已配置相关的分账配置,您确定要覆盖吗(覆盖后原有的配置将被删除)?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        (bindUserForm as any).is_cover = true
                        addByHandle(bindUserForm).then(() => {
                            loadAccountList()
                        })
                    }).catch(() => {
                        // 取消操作
                    })
                } else {
                    loadAccountList()
                }
            }).catch(() => {
            })
        }
    })
}

// 编辑
const editDialogVisible = ref(false)
const editForm = reactive({
    id: 0,
    extend_no: ''
})
const editFormRef = ref<FormInstance>()
const editRules = reactive({
    extend_no: [
        { required: true, message: t('请输入商户编号'), trigger: 'blur' }
    ]
})

const editEvent = (row: any) => {
    editDialogVisible.value = true
    editForm.id = row.id
    editForm.extend_no = row.extend_no
}

const confirmEdit = async (formEl: FormInstance | undefined) => {
    if (!formEl) return
    await formEl.validate((valid, fields) => {
        if (valid) {
            editAccount(editForm).then((res) => {
                editDialogVisible.value = false
                loadAccountList()
            }).catch(() => {
            })
        }
    })
}
</script>

<style scoped>

</style>