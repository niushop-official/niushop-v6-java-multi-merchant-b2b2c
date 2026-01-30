<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addBrand') }}
                </el-button>
            </div>

            <el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
                <el-form :inline="true" :model="brandTable.searchParam" ref="searchFormRef">
                    <el-form-item :label="t('brandName')" prop="brand_name">
                        <el-input v-model.trim="brandTable.searchParam.brand_name" :placeholder="t('brandNamePlaceholder')" />
                    </el-form-item>
                    <el-form-item :label="t('显示状态')" prop="status">
                        <el-select v-model="brandTable.searchParam.status" :placeholder="t('请选择显示状态')" clearable>
                            <el-option v-for="(item,index) in initData.status_list" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('是否热门')" prop="is_hot">
                        <el-select v-model="brandTable.searchParam.is_hot" :placeholder="t('请选择是否热门')" clearable>
                            <el-option v-for="(item,index) in initData.is_hot_list" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item :label="t('审核状态')" prop="audit_status" v-if="activeName == 'audit'">
                        <el-select v-model="brandTable.searchParam.audit_status" :placeholder="t('请选择审核状态')" clearable>
                            <el-option v-for="(item,index) in initData.audit_status_list" :key="index" :label="item" :value="index" />
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadBrandList()">{{ t('search') }}</el-button>
                        <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                    </el-form-item>
                </el-form>
            </el-card>
            <el-tabs v-model="activeName" class="goods-tabs" @tab-click="tabHandleClick">
                <el-tab-pane :label="t('商品品牌')" name="pass"></el-tab-pane>
                <el-tab-pane :label="t('品牌审核')" name="audit"></el-tab-pane>
            </el-tabs>

            <div class="mt-[10px]">
                <el-table :data="brandTable.data" size="large" v-loading="brandTable.loading" @sort-change="sortChange">
                    <template #empty>
                        <span>{{ !brandTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="brand_name" :label="t('brandName')" min-width="120">
                        <template #default="{ row }">
                            <div v-if="row.color_json"  class="inline-block px-[10px] text-[12px] rounded-[4px] box-border whitespace-nowrap h-[28px] leading-[28px]" :style="{ color : row.color_json.text_color, backgroundColor : row.color_json.bg_color, border : row.color_json.border_color ? '1px solid ' + row.color_json.border_color : 'none' }">
                                <span>{{ row.brand_name }}</span>
                            </div>
                            <div v-else>{{ row.brand_name }}</div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('logo')" min-width="120">
                        <template #default="{ row }">
                            <div class="h-[30px]">
                                <el-image class="w-[30px] h-[30px] " :src="img(row.logo)" fit="contain">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[30px] h-[30px]" src="@/app/assets/images/brand_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="site_name" :label="t('siteName')" min-width="120">
                        <template #default="{ row }">
                            <div v-if="row.site">
                                <span>{{ row.site.site_name }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="sort" :label="t('sort')" min-width="120" sortable="custom">
                        <template #default="{ row }">
                            <el-input v-model.trim="row.sort" class="!w-[100px]" maxlength="8" @blur="sortInputListener(row.sort, row)" />
                        </template>
                    </el-table-column>
                    <el-table-column prop="status_name" :label="t('显示状态')" min-width="120">
                        <template #default="{ row }">
                            <el-tag class="cursor-pointer" :type="row.status == 1 ? 'success' : 'danger'" @click="updateStatus(row)">{{ row.status == 1 ? '显示': '隐藏' }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="is_hot_name" :label="t('是否热门')" min-width="120">
                        <template #default="{ row }">
                            <el-tag class="cursor-pointer" :type="row.is_hot == 1 ? 'success' : 'danger'" @click="updateHot(row)">{{ row.is_hot == 1 ? '是': '否' }}</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('审核状态')" min-width="120" v-if="activeName=='audit'">
                        <template #default="{ row }">
                            <div>
                                <div>{{ row.audit_status_name }}</div>
                                <div v-if="row.audit_status == -1">拒绝原因：{{ row.audit_reason }}</div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="120">
                        <template #default="{ row }">
                            <div v-if="row.brand_type == 'admin' && activeName=='pass'">
                                <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                                <el-button type="primary" link @click="deleteEvent(row.brand_id)">{{ t('delete') }}</el-button>
                            </div>
                            <div v-if="row.brand_type != 'admin' && activeName=='pass'">
                                <el-button type="primary" link @click="moveEvent(row.brand_id)">{{ t('move') }}</el-button>
                            </div>
                            <div v-if="row.audit_status == 1 && activeName=='audit'">
                                <el-button type="primary" link @click="auditEvent(row)">审核</el-button>
                            </div>
                        </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="brandTable.page" v-model:page-size="brandTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="brandTable.total"
                        @size-change="loadBrandList()" @current-change="loadBrandList" />
                </div>
            </div>

            <brand-edit ref="editBrandDialog" @complete="loadBrandList" />
        </el-card>
        <!-- 审核弹框 -->
        <el-dialog v-model="auditShowDialog" :title="t('审核')" width="460px" class="diy-dialog-wrap" :destroy-on-close="true">
            <el-form :model="auditFormData" label-width="90px" ref="auditFormRef" :rules="auditFormRules" class="page-form">
                <el-form-item :label="t('审核状态')" prop="status">
                        <el-radio-group v-model="auditFormData.audit_status">
                            <el-radio :label="2">通过</el-radio>
                            <el-radio :label="-1">拒绝</el-radio>
                        </el-radio-group>
                    </el-form-item>
                <el-form-item :label="t('拒绝原因')" prop="audit_reason" v-if="auditFormData.audit_status == -1">
                    <el-input v-model.tirm="auditFormData.audit_reason" type="textarea" rows="4" clearable class="input-width"  :maxlength="100"  :show-word-limit="true" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="auditCancel">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="loading" @click="auditConfirm(auditFormRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { computed, reactive, ref } from 'vue'
import { t } from '@/lang'
import { getBrandPageList, getBrandPageAuditList, editBrandAudit, deleteBrand, modifyBrandSort, moveBrand, getBrandInit, updateBrandStatus, updateBrandHot } from '@/app/api/basic_admin'
import { debounce, img } from '@/utils/common'
import { ElMessage, ElMessageBox, FormInstance } from 'element-plus'
import BrandEdit from '@/app/views/admin/goods/components/brand-edit.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const brandTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        brand_name: '',
        order: '',
        sort: '',
        audit_status: '',
        is_hot: '',
        status: ''
    }
})
const activeName = ref('pass')

const searchFormRef = ref<FormInstance>()

const initData = ref<any>({})
const getBrandInitFn = () => {
    getBrandInit().then(res => {
        initData.value = res.data
    })
}
getBrandInitFn()
// 监听排序
const sortChange = (event: any) => {
    let sort = ''
    if (event.order == 'ascending') {
        sort = 'asc'
    } else if (event.order == 'descending') {
        sort = 'desc'
    }
    if (sort) {
        brandTable.searchParam.order = event.prop
        brandTable.searchParam.sort = sort
    }
    loadBrandList()
}

/**
 * 获取商品品牌列表
 */
const loadBrandList = (page: number = 1) => {
    brandTable.loading = true
    brandTable.page = page

    const api = activeName.value == 'audit' ? getBrandPageAuditList : getBrandPageList

    api({
        page: brandTable.page,
        limit: brandTable.limit,
        ...brandTable.searchParam
    }).then(res => {
        brandTable.loading = false
        brandTable.data = res.data.data
        brandTable.total = res.data.total
    }).catch(() => {
        brandTable.loading = false
    })
}
loadBrandList()

const tabHandleClick = (tab: any) => {
    activeName.value = tab.props.name
    loadBrandList()
}

const editBrandDialog: Record<string, any> | null = ref(null)

/**
 * 添加商品品牌
 */
const addEvent = () => {
    editBrandDialog.value.setFormData()
    editBrandDialog.value.showDialog = true
}

/**
 * 编辑商品品牌
 * @param data
 */
const editEvent = (data: any) => {
    editBrandDialog.value.setFormData(data)
    editBrandDialog.value.showDialog = true
}

/**
 * 删除商品品牌
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('brandDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteBrand(id).then(() => {
            loadBrandList()
        }).catch(() => {
        })
    })
}

/**
 * 转入商品品牌
 */
const moveEvent = (id: number) => {
    ElMessageBox.confirm(t('brandMoveTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        moveBrand({
            brand_id: id
        }).then(() => {
            loadBrandList()
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
    modifyBrandSort({
        brand_id: row.brand_id,
        sort
    }).then((res) => {
    })
})

// 更新状态
const updateStatus = (row: any) => {
    row.status = row.status == 1 ? 2 : 1
    updateBrandStatus({
        brand_id: row.brand_id,
        status: row.status
    }).then(() => {

    })
}

// 更新热门
const updateHot = (row: any) => {
    row.is_hot = row.is_hot == 1 ? 2 : 1
    updateBrandHot({
        brand_id: row.brand_id,
        is_hot: row.is_hot
    }).then(() => {

    })
}

// 审核
const auditShowDialog = ref(false)
const auditFormData = reactive({
    brand_id: '',
    audit_status: 2,
    audit_reason: ''
})
const auditFormRef = ref<FormInstance>()
const loading = ref(false)

// 表单验证规则
const auditFormRules = computed(() => {
    return {
        audit_reason: [
            { required: true, message: t('请输入拒绝原因'), trigger: 'blur' }
        ]
    }
})

const auditEvent = (data: any) => {
    auditFormData.brand_id = data.brand_id
    auditFormData.audit_status = 2
    auditFormData.audit_reason = ''
    auditShowDialog.value = true
}

const auditConfirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            editBrandAudit(auditFormData).then(() => {
                loading.value = false
                auditShowDialog.value = false
                loadBrandList()
            }).catch(() => {
                loading.value = false
                auditShowDialog.value = false
            })
        }
    })
}

const auditCancel = () => {
    auditShowDialog.value = false
    auditFormData.brand_id = ''
    auditFormData.audit_status = 2
    auditFormData.audit_reason = ''
}
const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadBrandList()
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
