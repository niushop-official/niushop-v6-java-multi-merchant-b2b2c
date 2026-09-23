<template>
    <el-dialog v-model="showDialog" :title="title || t('updateMember')" width="500px" :destroy-on-close="true">

        <el-form :model="saveData" label-width="90px" :rules="formRules" class="page-form" v-loading="loading">
            <el-form-item :label="t('memberLabel')" v-if="type == 'shop_member_label'">
                <el-select v-model="saveData.shop_member_label" multiple collapse-tags :placeholder="t('memberLabelPlaceholder')" class="input-width">
                    <el-option :label="item['label_name']" :value="item['label_id']" v-for="(item,index) in labelSelectData"  :key="index"/>
                </el-select>
            </el-form-item>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{t('confirm')}}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref, reactive, computed} from 'vue'
import { t } from '@/lang'
import type { FormInstance } from 'element-plus'
import { editShopMemberDetail, getShopMemberLabelAll ,batchModifyMemberDetail } from '@/app/api/shop_site'
import userStore from '@/stores/modules/user'

const siteInfo = computed(() => {
    return userStore().siteInfo
})

// 修改类型
const type = ref('')
const title = ref('')
// 会员id
const memberId = ref('')
const showDialog = ref(false)
const loading = ref(false)

const labelSelectData = ref(null)
// 获取全部标签
const getShopMemberLabelAllFn = async () => {
    labelSelectData.value = await (await getShopMemberLabelAll()).data
}
getShopMemberLabelAllFn()

/**
 * 表单数据
 */
const initialFormData = {
    headimg: '',
    nickname: '',
    shop_member_label: '',
    sex: '',
    birthday: ''
}
const saveData: Record<string, any> = reactive({ ...initialFormData })

const emit = defineEmits(['complete'])

/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    const addon = siteInfo.value?.app?.[0]
    loading.value = true
    if(isBatch.value){
        const data = ref({
            member_ids: batchMember.value,
            field: type.value,
            value: saveData[type.value]
        })

        batchModifyMemberDetail(data.value, addon).then(res => {
            loading.value = false
            showDialog.value = false
            emit('complete')
        }).catch(() => {
            loading.value = false
            // showDialog.value = false
        })
    }else{
        const data = ref({
            member_id: memberId.value,
            field: type.value,
            value: saveData[type.value]
        })

        editShopMemberDetail(data.value, addon).then(res => {
            loading.value = false
            showDialog.value = false
            emit('complete')
        }).catch(() => {
            loading.value = false
            // showDialog.value = false
        })
    }
   
}

// 批量修改
const isBatch = ref(false)
const batchMember = ref(null)
const batchSetDialogType = (data: any) => {
    loading.value = true
    type.value = data.type
    isBatch.value = true
    batchMember.value = data.memberIds
    title.value = data.title
    saveData[type.value] = null
    loading.value = false
}
const setDialogType = async (row: any = null) => {
    loading.value = true
    type.value = row.type
    isBatch.value = false
    title.value = row.title
    memberId.value = row.id
    saveData[type.value] = row.data[type.value]
    if (type.value == 'shop_member_label' && saveData[type.value]) {
        saveData[type.value].forEach((item:any, index:any) => {
            saveData[type.value][index] = Number.parseFloat(item)
        })
    }
    loading.value = false
}

defineExpose({
    showDialog,
    setDialogType,
    batchSetDialogType
})
</script>

<style lang="scss" scoped></style>
