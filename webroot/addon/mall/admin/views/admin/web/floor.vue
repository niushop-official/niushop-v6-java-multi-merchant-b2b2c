<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">

            <div class="flex justify-between items-center">
                <span class="text-lg">{{pageName}}</span>
                <el-button type="primary" @click="addEvent">
                    {{ t('addFloor') }}
                </el-button>
            </div>
            <div class="mt-[10px]">
                <el-table :data="floorTable.data" size="large" v-loading="floorTable.loading">
                    <template #empty>
                        <span>{{ !floorTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column prop="floor_name" :label="t('name')" min-width="120" />
                    <el-table-column :label="t('title')" min-width="180" align="center" >
                        <template #default="{ row }">
                            {{ row.config.title }}
                        </template>
                    </el-table-column>
                    <el-table-column  prop="sort" :label="t('sort')" min-width="120" />
					<el-table-column :label="t('statusTitle')" min-width="180" align="center" >
					    <template #default="{ row }">
					        {{ row.status ? '展示' : '隐藏' }}
					    </template>
					</el-table-column>
                    <el-table-column :label="t('createTime')" min-width="180" align="center" >
                        <template #default="{ row }">
                            {{ row.create_time || '' }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('operation')" fixed="right" min-width="120" align="right">
                       <template #default="{ row }">
                           <el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
                           <el-button type="primary" link @click="deleteEvent(row.id)">{{ t('delete') }}</el-button>
                       </template>
                    </el-table-column>

                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="floorTable.page" v-model:page-size="floorTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="floorTable.total"
                        @size-change="loadFloorList()" @current-change="loadFloorList" />
                </div>
            </div>

            <edit ref="editFloorDialog" @complete="loadFloorList" />
        </el-card>
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getFloorList, deleteFloor } from '@/addon/mall/api/admin/web'
import { ElMessageBox } from 'element-plus'
import Edit from '@/addon/mall/views/admin/web/components/edit-floor.vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const floorTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: []
})

/**
 * 获取楼层列表
 */
const loadFloorList = (page: number = 1) => {
    floorTable.loading = true
    floorTable.page = page

    getFloorList({
        page: floorTable.page,
        limit: floorTable.limit
    }).then(res => {
        floorTable.loading = false
        floorTable.data = res.data.data
        floorTable.total = res.data.total
    }).catch(() => {
        floorTable.loading = false
    })
}
loadFloorList()

const editFloorDialog: Record<string, any> | null = ref(null)

/**
 * 添加楼层
 */
const addEvent = () => {
    editFloorDialog.value.setFormData()
    editFloorDialog.value.showDialog = true
}

/**
 * 编辑楼层
 * @param data
 */
const editEvent = (data: any) => {
    editFloorDialog.value.setFormData(data)
    editFloorDialog.value.showDialog = true
}

/**
 * 删除PC导航管理
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('floorDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteFloor(id).then(() => {
            loadFloorList()
        }).catch(() => {
        })
    })
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
.input-width-sort{
    width:70px;
}
</style>
