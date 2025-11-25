<template>
    <el-drawer v-model="showDialog" title="分账明细" direction="rtl" :before-close="handleClose" class="!w-[1000px]">
        <div class="main-container" v-loading="loading">
            <el-table :data="detail" size="large">
                <template #empty>
                    <span>{{ !detail.length ? t('emptyData') : '' }}</span>
                </template>
                <el-table-column prop="main_name" :label="t('分账方')" min-width="120">
                    <template #default="{ row }">
                        {{ row.main_name }}({{ row.main_type_name }})
                    </template>
                </el-table-column>
                <el-table-column prop="value_source_name" :label="t('分账类型')" min-width="120" />
                <el-table-column prop="value" :label="t('分账金额')" min-width="120" />
                <el-table-column prop="value_type_name" :label="t('金额类型')" min-width="120" />
                <el-table-column prop="memo" :label="t('备注')" min-width="200" />
            </el-table>
        </div>

    </el-drawer>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { getSiteSpearDetail } from '@/app/api/separ'

const showDialog = ref(false)
const loading = ref(false)
const detail = ref<any>([])
const open = async (id: any = null) => {
    loading.value = true
    detail.value = await (await getSiteSpearDetail(id)).data
    showDialog.value = true
    loading.value = false
}

const handleClose = () => {
    showDialog.value = false
}

defineExpose({
    showDialog,
    open
})
</script>

<style lang="scss" scoped>

</style>