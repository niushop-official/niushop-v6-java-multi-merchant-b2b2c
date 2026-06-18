<template>
   <el-dialog v-model="showDialog" :title="t('重置密码')" width="550px" :destroy-on-close="true">
        <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules" class="page-form" v-loading="loading" @submit.enter.prevent>
           
           <el-form-item :label="t('会员信息')"> 
                <div class="flex items-center">
                    <img class="w-[50px] h-[50px] mr-[10px]" v-if="memberInfo.headimg" :src="img(memberInfo.headimg)" alt="">
                    <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                    <div class="flex flex flex-col">
                        <span>{{ memberInfo.nickname || '' }}</span>
                    </div>
                </div>
           </el-form-item>
            <el-form-item :label="t('新密码')" prop="password">
                <el-input v-model="formData.password" clearable :placeholder="t('请输入新密码')" class="input-width" :show-password="true" type="password" :readonly="password_input" @click="password_input = false" @blur="password_input = true" />
            </el-form-item>
            <el-form-item :label="t('确认新密码')" prop="confirm_password">
                <el-input v-model="formData.confirm_password" clearable :placeholder="t('请确认新密码')" class="input-width" :show-password="true" type="password" />
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
import { updateResetpwd } from '@/app/api/member'
import { img } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)
const formData = reactive({
    member_id: 0,
    password: '',
    confirm_password: ''
})
const formRef = ref<FormInstance>()
const password_input = ref(true)

const formRules = computed(() => {
    return {
        password: [
            { required: true, message: t('请输入新密码'), trigger: 'blur' }
        ],
        confirm_password: [
            { required: true, message: t('请输入新密码'), trigger: 'blur' },
            { validator: doubleCipher, trigger: 'blur' }
        ]
    }
})
const doubleCipher = (rule: any, value: string, callback: any) => {
    if (value !== formData.password) {
        callback(new Error(t('两次输入的密码不一致')))
    } else {
        callback()
    }
}
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
            updateResetpwd(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('complete')
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

const memberInfo = ref({
    nickname: '',
    headimg: ''
})
const setFormData = async (row: any = null) => {
    formData.member_id = row.member_id
    formData.password = ''
    formData.confirm_password = ''
    memberInfo.value.nickname = row.nickname || ''
    memberInfo.value.headimg = row.headimg || ''
    showDialog.value = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped>

</style>