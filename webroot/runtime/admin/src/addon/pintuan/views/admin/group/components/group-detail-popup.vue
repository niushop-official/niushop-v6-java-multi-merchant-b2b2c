<template>
    <el-drawer v-model="showDialog" :title="t('拼团详情')" direction="rtl" :before-close="handleClose" size="1300px">
        <div class="main-container" v-loading="loading">
            <el-tabs v-model="activeName" class="pb-[10px]" @tab-change="handleClick">
                <el-tab-pane :label="t('基础信息')" name="basicInfo" />
                <el-tab-pane :label="t('拼团订单')" name="activityGoods" />
            </el-tabs>
            <div v-if="activeName == 'basicInfo'">
                <el-form class="mt-[15px]" :model="formData" label-width="120px" ref="formRef" label-position="left" v-if="Object.keys(formData).length">
                    <div class="relative" v-if="formData">
                        <el-row>
                            <el-col :span="8">
                                <el-form-item :label="t('活动名称')">
                                    <div class="input-width">
                                        <span>{{formData.pintuan.title}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('进度')">
                                    <div class="input-width">
                                        <span>{{formData.current_num}}/{{ formData.target_num }}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('状态')">
                                    <div class="input-width">
                                        <span>{{formData.status_name}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('团长')">
                                    <div class="flex items-center cursor-pointer" @click="memberEvent(formData.leader?.member_id)">
                                        <el-image v-if="formData.leader?.headimg" class="w-[50px] h-[50px]" :src="img(formData.leader?.headimg)" fit="contain">
                                            <template #error>
                                                <div class="image-slot">
                                                    <img class="w-[50px] h-[50px]" src="@/app/assets/images/member_head.png" />
                                                </div>
                                            </template>
                                        </el-image>
                                        <img v-else class="w-[50px] h-[50px]" src="@/app/assets/images/member_head.png" fit="contain" />
                                        <span class="ml-[10px]">{{formData.leader?.nickname}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('过期时间')">
                                    <div class="input-width">
                                        <span>{{formData.expire_time}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </div>
                </el-form>
            </div>
            <div v-if="activeName == 'activityGoods'">
                <el-table :data="formData.member_list" size="large" ref="goodsInfoTableRef" max-height="685">
                    <el-table-column prop="goods_id" :label="t('用户信息')" min-width="150">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer" @click="memberEvent(row.member.member_id)">
                                <div class="min-w-[60px] h-[60px] flex items-center justify-center" >
                                    <el-image v-if="row.member.headimg" class="w-[50px] h-[50px]" :src="img(row.member.headimg)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[50px] h-[50px]" src="@/app/assets/images/member_head.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[50px] h-[50px]" src="@/app/assets/images/member_head.png" fit="contain" />
                                </div>
                                <div class="ml-2">
                                    <span :title="row.member.nickname" class="multi-hidden">{{row.member.nickname}}</span>
                                </div>

                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('订单编号')" prop="role" min-width="140">
                        <template #default="{row}">
                            {{ row.orderInfo.order_no  }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('订单金额')" prop="status" min-width="120">
                        <template #default="{row}">
                            {{ row.orderInfo.order_money  }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('订单状态')" prop="status" min-width="120">
                        <template #default="{row}">
                            {{ row.orderInfo.status_name.name  }}
                        </template>
                    </el-table-column>
                    <el-table-column :label="t('创建时间')" prop="join_time" min-width="120">
                        <template #default="{row}">
                            {{ row.orderInfo.create_time  }}
                        </template>
                    </el-table-column>

                    <el-table-column :label="t('operation')" fixed="right" align="right" min-width="100">
                        <template #default="{ row }">
                            <el-button type="primary" link @click="detailEvent(row)">{{t('查看订单')}}</el-button>
                        </template>
                    </el-table-column>

                </el-table>

            </div>
        </div>
    </el-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getPintuanAdminGroupDetail } from '@/addon/pintuan/api/admin/group'
import { img } from '@/utils/common'
import { useRouter } from 'vue-router'

const router = useRouter()

const showDialog = ref(false)
const loading = ref(false)
let id = ''
const activeName = ref('basicInfo')
const initialFormData = reactive({
    pintuan: {
        title: ''
    }
})

const formData: Record<string, any> = reactive({ ...initialFormData })

const handleClick = (data:string) => {
    activeName.value = data
}

const handleClose = (done: () => void) => {
    activeName.value = 'basicInfo'
    showDialog.value = false
}

const getPintuanGroupDetailFn = (id:number) => {
    loading.value = true
    getPintuanAdminGroupDetail(id).then((res:any) => {
        const data = res.data
        Object.assign(formData, data)
        console.log(formData, 'formData')
        loading.value = false
    })
}

const setFormData = async (row: any = null) => {
    id = row.id
    getPintuanGroupDetailFn(Number(id))
}

const memberEvent = (id: number) => {
    const routeUrl = router.resolve({
        path: '/member/detail',
        query: { id }
    })
    window.open(routeUrl.href, '_blank')
}

const detailEvent = (data: any) => {
    const routeUrl = router.resolve({
        path: '/admin/mall/order/detail',
        query: { order_id: data.orderInfo.order_id }
    })
    window.open(routeUrl.href, '_blank')
}

defineExpose({
    showDialog,
    setFormData
})
</script>
<style lang="scss" scoped></style>
