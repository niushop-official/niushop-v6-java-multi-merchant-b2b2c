<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <div class="flex justify-between items-center mb-[20px]">
                <span class="text-page-title">{{ pageName }}</span>
                <el-button type="primary" @click="addAccount">
                    {{ t('addAccount') }}
                </el-button>
            </div>

            <div class="accounts-container flex flex-wrap gap-3.5 mt-[10px]">
                <!-- 账户卡片 -->
                <div 
                    v-for="(account, index) in settAccountList" 
                    :key="index" 
                    class="w-[312px] h-[184px] flex-shrink-0 account-card relative transition-transform duration-300 hover:-translate-y-1 rounded-[5px] text-white bg-cover bg-no-repeat flex flex-col justify-center px-[16px] py-[22px]"
                    :class="[
                        account.type === 1 ? 'bg-[url(@/app/assets/images/sett_account/bank_bg.png)]' : '',
                        account.type === 2 ? 'bg-[url(@/app/assets/images/sett_account/alipay_bg.png)]' : '',
                        account.type === 3 ? 'bg-[url(@/app/assets/images/sett_account/wechat_bg.png)]' : '',
                        account.type === 4 ? 'bg-[url(@/app/assets/images/sett_account/wechat_change_bg.png)]' : ''
                    ]"
                >
                    <!-- 账户头部：类型 + 图标 -->
                    <div class="flex justify-between items-center mb-4 w-full">
                        <span class="text-[18px] font-bold">{{ account.type_name }}</span>
                        <div class="account-icon w-8 h-8">
                            <img v-if="account.type == 1" src="@/app/assets/images/sett_account/bank.png" alt="银行" class="w-full h-full object-contain" />
                            <img v-if="account.type == 2" src="@/app/assets/images/sett_account/alipay.png" alt="支付宝" class="w-full h-full object-contain" />
                            <img v-if="account.type == 3" src="@/app/assets/images/sett_account/wechat.png" alt="微信" class="w-full h-full object-contain" />
                            <img v-if="account.type == 4" src="@/app/assets/images/sett_account/wechat_change.png" alt="微信零钱" class="w-full h-full object-contain" />
                        </div>
                    </div>

                    <!-- 账户信息 -->
                    <div class="mb-[26px] w-full">
                        <!-- <div class="text-[14px] text-base opacity-80 mb-[2px]">账号昵称</div> -->
                        <div class="text-[16px] font-medium">{{ account.account_no }}</div>
                    </div>

                    <!-- 账户底部：持卡人 + 操作按钮 -->
                    <div class="flex justify-between items-center mt-auto w-full">
                        <div class="text-[16px] text-base opacity-80">
                            {{ account.name }} <span class="text-[10px] ml-1" v-if="account.type !== 4">持卡人</span>
                        </div>
                        <div class="account-actions flex gap-2">
                            <div
                                @click="deleteAccount(account)"
                                class="text-[14px] !bg-white !text-gray-800 !rounded-full !px-3 !py-1 !text-sm !border-0 hover:!bg-gray-100 rounded-[15px] flex items-center justify-center"
                            >
                                {{ t('delete') }}
                            </div>
                            <div
                                @click="editAccount(account)"
                                class="text-[14px] !bg-white !text-gray-800 !rounded-full !px-3 !py-1 !text-sm !border-0 hover:!bg-gray-100 rounded-[15px] flex items-center justify-center"
                            >
                                {{ t('edit') }}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="flex justify-center items-center mt-[50px]" v-if="!settAccountList.length && !loading">
                <el-empty  :image="img('static/resource/images/empty.png')" />
            </div>
        </el-card>
        
        <!-- 编辑弹框 -->
        <edit-sett-account ref="editAccountDialog" @complete="loadAccounts" />
    </div>
</template>

<script lang="ts" setup>
import { reactive, ref, onMounted } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { ElMessageBox } from 'element-plus'
import editSettAccount from '@/app/views/site/sett_account/components/edit.vue'
import { getSettAccountList, delSettAccount } from '@/app/api/site'
import { useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const loading = ref(true)
// 账户数据
const settAccountList = ref([])

// 加载账户列表
const loadAccounts = () => {
    getSettAccountList().then(res => {
        loading.value = false
        settAccountList.value = res.data
    }).catch(() => {
        loading.value = false
    })
}
loadAccounts()

// 编辑账户
const editAccountDialog: any = ref(null)
const editAccount = (account: any) => {
    editAccountDialog.value.setFormData(account)
    editAccountDialog.value.showDialog = true
}

// 添加账户
const addAccount = () => {
    editAccountDialog.value.setFormData()
    editAccountDialog.value.showDialog = true
}

// 删除账户
const deleteAccount = (account: any) => {
    ElMessageBox.confirm(t('deleteConfirm'), t('warning'), {
        confirmButtonText: t('confirm'),
        cancelButtonText: t('cancel'),
        type: 'warning'
    }).then(() => {
        delSettAccount(account.id).then(() => {
            loadAccounts()
        })
    })
}

onMounted(() => {
    loadAccounts()
})
</script>

<style lang="scss" scoped>
/* 修复Element Plus按钮样式穿透（Tailwind优先级） */
:deep(.account-actions .el-button) {
    border: none !important;
    box-shadow: none !important;
}
</style>