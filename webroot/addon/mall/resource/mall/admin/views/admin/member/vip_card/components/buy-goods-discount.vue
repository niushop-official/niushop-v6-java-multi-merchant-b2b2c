<template>
    <el-form ref="formRef" :model="formData" label-width="100px" :rules="formRules" class="page-form">
        <el-form-item label="权益图标" prop="icon">
            <upload-image v-model="formData.icon" :limit="1" />
        </el-form-item>
        <el-form-item label="权益名称" prop="name">
            <el-input v-model.tirm="formData.name" clearable maxlength="20" placeholder="请输入权益名称" class="input-width" />
        </el-form-item>
        <el-form-item label="权益简介" prop="desc">
            <el-input v-model.tirm="formData.desc" clearable maxlength="20" placeholder="请输入权益简介" class="input-width" />
        </el-form-item>
        <el-form-item label="是否启用" prop="status">
            <el-radio-group v-model="formData.status">
                <el-radio :label="1">是</el-radio>
                <el-radio :label="0">否</el-radio>
            </el-radio-group>
        </el-form-item>
        <el-form-item label="规则设置" prop="item_list">
            <div class="text-primary">可在店铺端商品中单独设置付费会员专属折扣价。</div>
        </el-form-item>
        <el-form-item label="权益说明" prop="explain">
            <div class="flex-1">
            <editor v-model="formData.explain" :height="300"  class="editor-width" />
            </div>
        </el-form-item>
    </el-form>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'

const props = defineProps({
    modelValue: {
        type: Object,
        default: () => {
            return {}
        }
    }
})
const emits = defineEmits(['update:modelValue'])

const formData = ref<any>({
    icon: '',
    name: '',
    desc: '',
    status: 0,
    item_list: [],
    explain: ''
})

const formRef = ref(null)

const value = computed({
    get () {
        return props.modelValue
    },
    set (value) {
        emits('update:modelValue', value)
    }
})

watch(() => value.value, (nval, oval) => {
    if ((!oval || !Object.keys(oval).length) && Object.keys(nval).length) {
        Object.assign(formData.value, value.value)
    }
}, { immediate: true })

watch(() => formData.value, () => {
    value.value = formData.value
}, { deep: true })

const formRules = computed(() => {
    return {
        icon: [
            {
                required: true,
                message: '请上传图标',
                trigger: 'blur'
            }
        ],
        name: [
            {
                required: true,
                message: '请输入权益名称',
                trigger: 'blur'
            }
        ],
        status: [
            {
                required: true,
                message: '请选择是否启用',
                trigger: 'blur'
            }
        ]
    }
})
const verify = async () => {
    let verify = true
    await formRef.value?.validate((valid) => {
        verify = valid
    })
    return verify
}

defineExpose({
    verify
})
</script>

<style  lang="scss" scoped>

</style>