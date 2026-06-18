<template>
    <el-dialog v-model="showDialog" :title="t('修改推荐人')" width="550px" :destroy-on-close="true">
        <el-form :model="formData" label-width="110px" ref="formRef" :rules="formRules" class="page-form"
            v-loading="loading" @submit.enter.prevent>

            <el-form-item :label="t('会员信息')">
                <div class="flex items-center">
                    <img class="w-[50px] h-[50px] mr-[10px]" v-if="formData.headimg" :src="img(formData.headimg)" alt="">
                    <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                    <div class="flex flex flex-col">
                        <span>{{ formData.nickname || '' }}</span>
                    </div>
                </div>
            </el-form-item>
            <el-form-item :label="t('上级推荐人')">
                <div class="flex items-center" v-if="formData.parent_member.member_id">
                    <div class="parent-member-container relative">
                        <div class="flex items-center">
                            <img class="w-[50px] h-[50px] mr-[10px]" v-if="formData.parent_member?.headimg" :src="img(formData.parent_member?.headimg)" alt="">
                            <img class="w-[50px] h-[50px] mr-[10px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                            <div class="flex flex-col">
                                <span>{{ formData.parent_member?.nickname || '' }}</span>
                            </div>
                        </div>

                        <!-- 悬浮遮罩层 -->
                        <div class="parent-member-mask">
                            <text class="iconfont iconshanchu-fanggaiV6xx !text-[16px] text-[#fff]" @click="clear"></text>
                            <text class="iconfont iconbianji !text-[16px] text-[#fff] ml-4" @click="selectMemberFn"></text>
                        </div>
                    </div>
                </div>
                <el-button v-else class="ml-[10px]" type="primary" @click="selectMemberFn">
                    {{ t('选择推荐人') }}
                </el-button>
            </el-form-item>
        </el-form>

        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                <el-button type="primary" :loading="loading" @click="confirm(formRef)">{{ t('confirm') }}</el-button>
            </span>
        </template>
        <select-member ref="selectMemberRef" @confirm="selectMemberConfirm"></select-member>

    </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { updateRecommender } from '@/app/api/member'
import { img } from '@/utils/common'
import selectMember from './select-member.vue'

const showDialog = ref(false)
const loading = ref(false)
const initialFormData = {
    member_id: '',
    nickname: '',
    headimg: '',
    parent_member: {
        member_id: '',
        nickname: '',
        headimg: ''
    }
}

const formData: Record<string, any> = reactive({ ...initialFormData })

const formRef = ref<FormInstance>()

const formRules = computed(() => {
    return {

    }
})

const emit = defineEmits(['complete'])
// 选择用户
const selectMemberRef = ref<any>(null)
const selectMemberFn = () => {
    selectMemberRef.value.open(formData.member_id)
}
// 选择会员回调
const selectMemberConfirm = (data: any) => {
    formData.parent_member.member_id = data.member_id
    formData.parent_member.nickname = data.nickname
    formData.parent_member.headimg = data.headimg
}

// 清空
const clear = () => {
    formData.parent_member.member_id = ''
    formData.parent_member.nickname = ''
    formData.parent_member.headimg = ''
}
/**
 * 确认
 * @param formEl
 */
const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const data = {
                member_id: formData.member_id,
                pid: formData.parent_member?.member_id || ''
            }

            updateRecommender(data).then(res => {
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
    Object.assign(formData, {
        ...initialFormData,
        parent_member: { ...initialFormData.parent_member }
    });
    if (row) {
        Object.keys(formData).forEach((key: string) => {
            if (row[key] != undefined) formData[key] = row[key]
        })
    }
    showDialog.value = true
}

defineExpose({
    showDialog,
    setFormData
})
</script>

<style scoped lang="scss">
.parent-member-container {
    position: relative;
    display: inline-block; 
    padding: 8px 12px;
    border-radius: 6px;
    border: 1px solid #e6e6e6;
    cursor: pointer;

    .parent-member-mask {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.6); // 半透明黑色遮罩
        border-radius: 6px;
        display: flex;
        justify-content: center;
        align-items: center;
        opacity: 0; // 默认隐藏
        transition: opacity 0.2s ease; // 过渡动画
    }

    // 鼠标悬浮容器时显示遮罩
    &:hover .parent-member-mask {
        opacity: 1;
    }
}
</style>