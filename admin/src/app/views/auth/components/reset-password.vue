<template>
   <el-dialog v-model="showDialog" :title="t('重置密码')" width="550px" :destroy-on-close="true">
        <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading" @submit.enter.prevent>
            <el-form-item :label="t('password')" prop="password">
                <el-input v-model="formData.password" clearable :placeholder="t('passwordPlaceholder')" class="input-width" :show-password="true" type="password" :readonly="password_input" @click="password_input = false" @blur="password_input = true" />
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

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { updateUserResetpwd } from '@/app/api/site'

const showDialog = ref(false)
const loading = ref(false)
const formData = reactive({
    uid: 0,
    password: ''
})
const formRef = ref<FormInstance>()
const password_input = ref(true)

const formRules = computed(() => {
    return {
        password: [
            { required: true, message: t('passwordPlaceholder'), trigger: 'blur' }
        ]
    }
})

const emit = defineEmits(['complete'])
/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = formData
            updateUserResetpwd(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
const setFormData = async (row: any = null) => {
    formData.uid = row.uid
    formData.password = ''
    showDialog.value = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>

</style>