<template>
    <view class="min-h-[100vh] bg-[var(--page-bg-color)]  overflow-hidden" :style="themeColor()">
        <view class="card-template sidebar-margin my-[20rpx]">
            <u-form labelPosition="left" :model="formData" :label-style="{'font-size':'28rpx'}"  class="pl-[15rpx]" labelWidth="140rpx" errorType='toast' :rules="rules" ref="formRef">
                <view>
                    <u-form-item label="启用裁剪" prop="is_cropper">
                         <u-radio-group v-model="formData.is_cropper" placement="row"  iconPlacement="left">
                            <u-radio activeColor="var(--primary-color)" size="32rpx"  :labelSize="'28rpx'" labelColor="#333" label="是" :name="1"></u-radio>
                            <u-radio activeColor="var(--primary-color)" size="32rpx" :labelSize="'28rpx'" labelColor="#333" label="否" :name="0"></u-radio>
                        </u-radio-group>
                    </u-form-item>
                </view>
            </u-form>
        </view>
        <view class="py-[var(--top-m)] px-[var(--sidebar-m)]  box-border mt-[40rpx]">
            <button hover-class="none" class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx] rounded-[16rpx] text-[26rpx] font-500"  @click="save" >确定</button>
        </view>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, computed, nextTick } from 'vue'
import { getAttachmentConfig, setAttachmentConfig } from '@/app/api/system'
import { redirect } from '@/utils/common'

const formRef = ref<any>()
const formData = reactive<any>({
    is_cropper: 0,

})
const loading = ref(true)
const getAttachmentConfigFn = () => {
    loading.value = true
    getAttachmentConfig().then((res: any) => {
        formData.is_cropper = Number(res.data.is_cropper)
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
getAttachmentConfigFn()

const rules = computed(() => {
    return {
        is_cropper: [
            { 
                required: true,
                validator: (rule: any, value: any, callback: any) => {
                    if ([0, 1].includes(value)) {
                        callback() 
                    } else {
                        callback(new Error('请选择是否裁剪'))
                    }
                },
                trigger: ['change', 'blur'] }
        ]
    }
})

const  operateLoading = ref(false)
const save = () => {
    formRef.value.validate().then(() => {
        
        if (operateLoading.value) return
        operateLoading.value = true

        setAttachmentConfig(formData).then((res: any) => {
            operateLoading.value = false
            redirect({url: '/app/pages/site/index'})
        }).catch((err: any) => {
            operateLoading.value = false
        })
    })
    
}
</script>

<style lang="scss" scoped>
:deep(.u-form-item__body__right__content){
    height: 100% !important;
}
:deep(.u-form-item__body){
    padding: 0 !important;
}
</style>