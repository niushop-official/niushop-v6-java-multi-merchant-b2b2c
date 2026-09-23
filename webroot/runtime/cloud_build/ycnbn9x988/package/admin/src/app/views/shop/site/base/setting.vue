<template>
    <div class="main-container"  v-loading="loading">
        <el-card class="box-card mb-[15px] !border-none" shadow="never">
            <div class="flex items-center">
                <span class="text-page-title">{{ pageName }}</span>
            </div>
        </el-card>
        <el-form :model="formData" label-width="110" ref="formRef" :rules="formRules" class="page-form">
            <el-card class="box-card mb-[15px] !border-none" shadow="never">
                <div class="text-page-title mb-[20px]">订单设置</div>
                <el-form-item label="万能表单" v-if="siteInfo?.app.includes('mall')">
                    <el-select v-model="formData.form_id" placeholder="请选择万能表单" clearable>
                        <el-option v-for="item in diyFormOptions" :key="item.form_id" :label="item.page_title" :value="item.form_id" />
                    </el-select>
                    <div class="ml-[10px]">
                        <span class="cursor-pointer text-primary mr-[10px]" @click="refreshDiyForm(true)">刷新</span>
                        <span class="cursor-pointer text-primary" @click="toDiyFormEvent">添加表单</span>
                    </div>
                </el-form-item>
                <el-form-item label="自动接单" v-if="siteInfo?.app.includes('waimai')">
                    <el-radio-group class="mx-[10px]" v-model="formData.is_auto_accept">
                        <el-radio :label="0">关闭</el-radio>
                        <el-radio :label="1">开启</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="拣货设置" prop="picking_length" v-if="siteInfo?.app.includes('front_warehouse')">
                    <span class="text-[13px] mr-[10px]">订单配送前</span>
                    <el-input v-model.trim="formData.picking_length" placeholder="请输入" class="!w-[150px]" clearable @keyup="filterNumber($event)"  @blur="formData.picking_length = $event.target.value"/>
                    <span class="ml-[10px] text-[13px]">分钟开始下发拣货订单</span>
                </el-form-item>
            </el-card>
            <el-card class="box-card mb-[15px] !border-none" shadow="never">
                <div class="text-page-title mb-[20px]">发票设置</div>
                <el-form-item label="发票开关">
                    <el-radio-group class="mx-[10px]" v-model="formData.is_invoice">
                        <el-radio label="2">关闭</el-radio>
                        <el-radio label="1">开启</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-card>
            <el-card class="box-card mb-[15px] !border-none" shadow="never">
                <div class="text-page-title mb-[20px]">会员设置</div>
                <el-form-item label="老用户判定规则" prop="old_member_length">
                    <span class="text-[13px] mr-[10px]">会员注册</span>
                    <el-input v-model.trim="formData.old_member_length" placeholder="请输入" class="!w-[150px]" clearable @keyup="filterNumber($event)"  @blur="formData.old_member_length = $event.target.value"/>
                    <span class="ml-[10px] text-[13px]">天后自动成为老用户</span>
                </el-form-item>
            </el-card>
        </el-form>

        <div class="fixed-footer-wrap" v-if="!loading">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">保存</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { getDiyFormList } from '@/app/api/diy_form_site'
import { useRoute, useRouter } from 'vue-router'
import { filterNumber } from '@/utils/common'
import { ElMessage } from 'element-plus'
import { getConfig, setConfig } from '@/app/api/shop_site'
import userStore from '@/stores/modules/user'

const siteInfo = computed(() => {
    return userStore().siteInfo
})

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const loading = ref(false)
const formData = ref({
    form_id: '',
    is_invoice: '1',
    old_member_length: 0,
    is_auto_accept: 0,
    picking_length: 0
})

const formRef = ref()

const formRules = {
    picking_length: [
        { required: true, message: '请输入拣货设置', trigger: 'blur' },
        {
            validator: (_rule: any, value: any, callback: any) => {
                if (Number(value) <= 0) {
                    callback(new Error('分钟数必须大于0'))
                } else {
                    callback()
                }
            },
            trigger: 'blur'
        }
    ]
}

const getConfigFn = () => {
    loading.value = true
    getConfig().then((res: any) => {
        formData.value = Object.assign(formData.value, res.data)
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

getConfigFn()
const onSave = async (formEl: any) => {
    await formEl.validate(async (valid:any) => {
        if (valid) {
            loading.value = true
            setConfig(formData.value).then(res => {
                getConfigFn()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

/** ***************** 万能表单-start *************************/
// 万能表单列表下拉框
const diyFormOptions = reactive<any>([])
// 跳转到万能表单列表，添加表单
const toDiyFormEvent = () => {
    const url = router.resolve({
        path: '/site/diy_form/list'
    })
    window.open(url.href)
}

// 刷新万能表单
const refreshDiyForm = (bool = false) => {
    getDiyFormList({
        type: 'DIY_FROM_ORDER_PAYMENT',
        status: 1
    }).then((res) => {
        const data = res.data
        if (data) {
            diyFormOptions.splice(0, diyFormOptions.length, ...data)
            if (bool) {
                ElMessage({
                    message: '刷新成功',
                    type: 'success'
                })
            }
        }
    })
}

refreshDiyForm()
/** *****************万能表单-end *************************/
</script>

<style lang="scss" scoped>

</style>