<template>
    <div class="main-container min-h-[300px]">
        <div class="">
            <el-card class="box-card mt-[10px]!border-none table-search-wrap" shadow="never">
                <div class="flex p-[10px] items-end">
                    <div class="flex items-center w-[500px] h-[45px] border-[2px] border-primary rounded-l-lg">
                        <span class="h-[15px] ml-[10px] pr-[10px] border-r-[1px] border-[#DEE1E7] leading-[1]">
                            <img class="w-[15px]" :src="img('static/resource/images/verify/write.png')" />

                        </span>
                        <input class="w-[400px] h-[40px] outline-none pl-[10px] text-[18px] bg-transparent"
                            v-model="verifycode" />
                        <span class="nc-iconfont nc-icon-cuohaoV6xx text-[24rpx] text-[#999] ml-auto mr-[10px] cursor-pointer" v-if="verifycode" @click="verifycode = ''"></span>
                    </div>
                    <div class="bg-primary h-[45px] flex items-center px-[20px] rounded-[10px] ml-[10px] text-[#fff] cursor-pointer"
                        @click="handelVerify">
                        <span>{{ t("核销") }}</span>
                    </div>
                    <div class="bg-primary h-[45px] flex items-center px-[20px] rounded-[10px] ml-[10px] text-[#fff] cursor-pointer"
                        @click="router.push('/marketing/verify')">
                        <span>{{ t("核销记录") }}</span>
                    </div>
                </div>

            </el-card>
        </div>
        <el-dialog v-model="showDialog" :title="t('核销')" width="500px" :destroy-on-close="true">
            <div>
                <h3 class="panel-title !text-sm">{{ t('核销信息') }}</h3>
                <div class="flex items-center mt-[15px]">
                    <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('核销码') }}</span>
                    <span class="text-[14px] text-[#666666]">
                        {{verifycode}}
                    </span>
                </div>
                <div class="flex items-center mt-[15px]">
                    <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('核销类型') }}</span>
                    <span class="text-[14px] text-[#666666]">
                        {{ verifyInfo.type_name }}
                    </span>
                </div>
                <div class="flex items-center mt-[15px]" v-for="(item,index) in verifyInfo.fixed" :key="index">
                    <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ item.title }}</span>
                    <span class="text-[14px] text-[#666666]">
                        {{ item.value }}
                    </span>
                </div>
                <div class="box-card mt-[15px] !border-none" shadow="never" v-for="(item,index) in verifyContentData.diy" :key="index">
                    <h3 class="panel-title !text-sm">{{ item.title }}</h3>

                    <div class="flex items-center mt-[15px]" v-for="(subItem,subIndex) in item.list" :key="subIndex">
                        <span class="text-[14px] w-[130px] text-right mr-[20px]">{{ subItem.title }}</span>
                        <span class="text-[14px] text-[#666666]">
                            {{ subItem.value }}
                        </span>
                    </div>
                </div>
                <div class="mt-[15px]">
                    <h3 class="panel-title !text-sm">{{ t('商品信息') }}</h3>
                    <el-table :data="verifyGoodsList" size="large">
                        <el-table-column :label="t('商品名称')" align="left" min-width="250">
                            <template #default="{ row }">
                                <div class="flex">
                                    <div class="flex items-center flex-shrink-0 mr-[10px]">
                                        <el-image v-if="row.cover" class="w-[50px] h-[50px] mr-[10rpx]" :src="img(row.cover)" fit="contain">
                                            <template #error>
                                                <div class="image-slot">
                                                    <img class="w-[50px] h-[50px] mr-[10rpx]" src="@/app/assets/images/goods_default.png" />
                                                </div>
                                            </template>
                                        </el-image>
                                        <img v-else  class="w-[50px] h-[50px] mr-[10rpx]" src="@/app/assets/images/goods_default.png" fit="contain" />
                                    </div>
                                    <div class="flex flex-col">
                                        <p class="multi-hidden text-[14px]">{{ row.name }}</p>
                                        <div class="text-primary">{{ row.un_use_msg }}</div>
                                    </div>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="num" :label="t('数量')" min-width="80" align="right" />
                    </el-table>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="verifyCode">{{ t("confirm") }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang='ts' setup>
import { ref } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { useRoute,useRouter } from 'vue-router'
import { getVerifyDetailInfo, verify } from '@/app/api/verify'

const route = useRoute()
const router = useRouter()
const code = route.query.code as string
const loading = ref(false)
const verifycode = ref('')
const verifyInfo = ref<any>([])
const verifyContentData: any = ref({})
const verifyGoodsList: any = ref([])
const showDialog = ref(false)
const handelVerify = () => {
    if (verifycode.value == '') return
    getVerifyDetailInfo(verifycode.value).then((res) => {
        showDialog.value = true
        verifyInfo.value = res.data
        verifyContentData.value = res.data.value.content || {}
        verifyGoodsList.value = res.data.value.list || []
        loading.value = false
    })
}
if (code) {
    verifycode.value = code
    handelVerify()
}

const isSubmit = ref(false)
const verifyCode = () => {
    if (verifycode.value == '') return
    if (isSubmit.value) return
    isSubmit.value = true
    verify(verifycode.value).then(() => {
        showDialog.value = false
        verifycode.value = ''
        isSubmit.value = false
    }).catch(() => {
        isSubmit.value = false
    })
}
</script>

<style></style>