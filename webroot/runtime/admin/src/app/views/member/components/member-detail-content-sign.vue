<template>
    <div class="main-container">
      <el-table :data="memberSignListTableData.data" size="large" v-loading="memberSignListTableData.loading">
                    <template #empty>
                        <span>{{ !memberSignListTableData.loading ? t('emptyData') : '' }}</span>
                    </template> 
                    <el-table-column :label="t('days')" min-width="110">
                        <template #default="{ row }">
                            {{ row.days }}{{ t('day') }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('dayAward')" min-width="100">
                        <template #default="{ row }">
                            <div v-if="row.day_award">
                                <template v-for="item in row.day_award">
                                    <div v-if="item.content">{{ item.content }}</div>
                                </template>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('continueAward')" min-width="150">
                        <template #default="{ row }">
                            <div v-if="row.continue_award">
                                <template v-for="item in row.continue_award">
                                    <div v-if="item.content">{{ item.content }}</div>
                                </template>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="create_time" :show-overflow-tooltip="true" :label="t('createTime')" min-width="150" />
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="memberSignListTableData.page"
                        v-model:page-size="memberSignListTableData.limit" layout="total, sizes, prev, pager, next, jumper"
                        :total="memberSignListTableData.total" @size-change="loadMemberAccountLogList()"
                        @current-change="loadMemberAccountLogList" />
                </div>
    </div>
</template>
<script lang="ts" setup> 
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getMemberSignList} from '@/app/api/member'
import { img } from '@/utils/common'

const props = defineProps({
    member_id: {
        type: Number,
        default: 0
    }
})

const memberSignListTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keywords: '',
        from_type: '',
        create_time: '',
        mobile: '',
        member_id: props.member_id,
        balance_type: ''
    }
})


/**
 * 获取会员账单表列表
 */
const loadMemberAccountLogList = (page: number = 1) => {
    memberSignListTableData.loading = true
    memberSignListTableData.page = page
    getMemberSignList({
        page: memberSignListTableData.page,
        limit: memberSignListTableData.limit,
        ...memberSignListTableData.searchParam
    }).then(res => {
        memberSignListTableData.loading = false
        memberSignListTableData.data = res.data.data
        memberSignListTableData.total = res.data.total
    }).catch(() => {
        memberSignListTableData.loading = false
    })
}
loadMemberAccountLogList()
</script>
<style lang="scss" scoped>
</style>