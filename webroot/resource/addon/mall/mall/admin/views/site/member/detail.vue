<template>
	<div class="main-container" v-loading="loading">
		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/mall/member/list' })" />
        </el-card>
		<div class="flex justify-between h-full">
            <div class="w-[360px]">
                <div class="bg-[#fff] h-full"> 
                    <div class="flex flex-col justify-center p-[20px]">
                        <div class="flex flex-col items-center justify-center">
                            <img class="w-[50px] h-[50px]" v-if="formData.headimg" :src="img(formData.headimg)" alt="">
                            <img class="w-[50px] h-[50px]" v-else src="@/app/assets/images/default_headimg.png" alt="">
                            <div class="mt-[12px] text-[20px]"> 
                                {{ formData.username || t('notAvailable') }}
                            </div>
                            <div class="flex items-center mt-[12px] text-[#666]">
                                <span class="text-[14px] text-right mr-[5px]">UID：</span>
                                <span class="member-info-item text-[14px] text-[#666666]">
                                    {{ formData.member_no || t('notAvailable') }}
                                </span>
                            </div>
                        </div>    
                    </div>
                    <div class="h-[1px] border-solid border-[#e5e7eb] border-b-[1px] my-[10px]"></div>
                    
                    <div class="p-[20px]">
                        <el-tabs v-model="tabBaseType">
                            <el-tab-pane name="basicInfo" label="基本信息">
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('urserName') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.username || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('nickname') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.nickname || t('notAvailable') }}
                                    </span>
                                </div>

                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('mobile') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.mobile || t('notAvailable') }}
                                    </span>
                                </div>
                            
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('birthday') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.birthday || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('sex') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.sex == 1 && t('manSex') || formData.sex == 2 && t('girlSex') || t('secrecySex') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('是否关注') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.is_follow ? t('已关注') : t('未关注') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('registeredSource') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.register_channel_name || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('firstConsumTime') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.first_consum_time || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('lastConsumTime') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.last_consum_time || t('notAvailable') }}
                                    </span>
                                </div>
                            </el-tab-pane>
                        </el-tabs>
                        
                    </div>
                </div>
            </div>
            <div class="flex-1 ml-[10px]">
                <el-card class="box-card !border-none" shadow="never"> 
                    <template #header>
                        <div class="card-header font-bold">
                        消费统计
                        </div>
                    </template>
					<div class="flex gap-4">
						<div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('消费次数') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.comsum_num }}
									</div>
                                </div>
                                <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/comsum_num.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>
						</div>
                        <div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('累计消费金额') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.comsum_money }}
									</div>
                                </div>
                                 <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/comsum_money.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>		
						</div>		
                    </div>
                </el-card>

                <el-card class="box-card !border-none mt-[10px]" shadow="never"> 
                    <template #header>
                        <div class="card-header font-bold">
                            会员属性
                        </div>
                    </template>
                    <div>
                        <el-row>
                            <el-col :span="8">
                                 <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] text-[#666666] text-right  mr-[20px]">{{ t('memberLabel') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.member_label_name.toString() || t('notAvailable') }}<el-icon @click="editMemberInfo('shop_member_label')" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
                                    </span>
                                </div>
                            </el-col>       
                        </el-row>
                    </div>
                </el-card>  
                <el-card class="box-card !border-none mt-[10px]" shadow="never"> 
                        <el-tabs v-model="activeName" class="demo-tabs ml-[15px]" @tab-change="handleMenuClick">
                            <el-tab-pane v-for="(item,index) in typeList" :key="index" :name="item.key">
                                <template #label>
                                    <div class="flex items-center">
                                        <span>{{ item.name }}</span>
                                    </div>
                                </template>
                            </el-tab-pane>
                        </el-tabs>
                    <div>
                        <component v-if="dynamicComponentName" :is="dynamicComponentName" :member_id="member_id" ref="dynamicComponentRefs" />
                    </div>
                </el-card>  
            </div>
        </div>
	</div>
    <edit-member ref="editMemberDialog" @complete="getMemberInfoFn()" />
</template>

<script lang="ts" setup>
import { reactive, ref ,defineAsyncComponent} from 'vue'
import { t } from '@/lang'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getShopMemberInfo, getMemberContentType } from '@/app/api/shop_site'
import { getShopMemberCoupon } from '@/app/api/marketing_site'
import { ElMessage } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'
import { img, mobileHide } from '@/utils/common'
import EditMember from '@/app/views/shop/site/member/components/edit-member.vue'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title
const activeName = ref('')
const id: number = parseInt(route.query.id || 0)
const loading = ref(true)
const tabBaseType = ref('basicInfo')
const handleClick = (event: any) => {
    activeName.value = event
}

// 获取会员信息
const member_id: number = parseInt(route.query.member_id || 0)
const formData: any = reactive({ member_label_name: '', coupon_member: [] })

const getMemberInfoFn = async () => {
    loading.value = true
    if (member_id) {
        const data = await (await getShopMemberInfo(member_id, 'mall')).data
        if (!data || Object.keys(data).length == 0) {
            ElMessage.error(t('memberNull'))
            setTimeout(() => {
                router.go(-1)
            }, 2000)
            return false
        }
        const memberCouponData = await (await getShopMemberCoupon(member_id)).data
        if (!memberCouponData || Object.keys(memberCouponData).length == 0) {
            ElMessage.error(t('memberNull'))
            setTimeout(() => {
                router.go(-1)
            }, 2000)
            return false
        }

        formData.coupon_member = memberCouponData.data

        Object.keys(data).forEach((item) => {
            formData[item] = data[item]
        })

        if (formData?.shop_member_label_array && Object.keys(formData.shop_member_label_array)?.length) {
            formData.member_label = Object.values(formData.shop_member_label_array).map((item: any, index) => {
                return item.label_id
            })

            formData.member_label_name = Object.values(formData.shop_member_label_array).map((item: any, index) => {
                return item.label_name
            })
        }
        loading.value = false
    } else {
        loading.value = false
    }
}
getMemberInfoFn()

const typeList = ref([])
const modules: any = import.meta.glob('@/**/*.vue')
const dynamicComponentName: any = ref('') // 动态组件名称

const dynamicComponentRefs: any = ref(null) // 动态组件ref
const getMemberContentTypeFn = async () => {
    const res = await getMemberContentType()
    typeList.value = Object.values(res.data)
    activeName.value = typeList.value[0].key
    handleMenuClick(activeName.value)
}
getMemberContentTypeFn()
const handleMenuClick = (value: any) => {
    const item = typeList.value.find((item: any) => item.key == value)
    const component = item?.component
    if (component) {
        dynamicComponentName.value = component
        dynamicComponentName.value = defineAsyncComponent(modules[dynamicComponentName.value])
    } else {
        dynamicComponentName.value = ''
    }
}

const editMemberDialog: Record<string, any> | null = ref(null)

/**
 *  修改会员信息
 */
const editMemberInfo = (type: any) => {
    const data = ref({
        type,
        id: member_id,
        data: formData
    })
    editMemberDialog.value.setDialogType(data.value)
    editMemberDialog.value.showDialog = true
}

</script>

<style lang="scss" scoped>

.member-info {
	left: 0px
}

.member-info-item {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
	-o-text-overflow: ellipsis;
}
.statistic-card{
    width: 20%;
    background-color: #fff;
    border:1px solid #e5e7eb;
    border-radius: 8px;
    padding: 16px;
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
}
:deep(.el-card__header){
    padding: 15px;
}
:deep(.el-tabs__item:hover){
    color: inherit !important;
}
</style>
