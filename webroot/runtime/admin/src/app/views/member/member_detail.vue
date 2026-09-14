<template>
	<div class="main-container" v-loading="loading">
		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/member/member' })" />
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
                            <div class="flex gap-4 mt-[12px]">
                                <div class="bg-[#F0F2FF] p-[5px] flex items-center cursor-pointer" @click="editMemberInfo('headimg')" >
                                    <el-icon class="cursor-pointer">
                                        <EditPen color="#273DE3" />
                                    </el-icon>
                                    <span class="text-[#273DE3] text-[12px] ml-1">编辑头像</span>
                                </div>
                                <div class="bg-[#E7FBED] p-[5px] flex items-center cursor-pointer" @click="resetPasswordEvent(formData)">
                                    <el-icon class="cursor-pointer">
                                        <EditPen color="#00B737" />
                                    </el-icon>
                                    <span class="text-[#00B737] text-[12px] ml-1">修改密码</span>
                                </div>
                                 <!-- <div class="bg-[#FFE6E6] p-[5px] flex items-center cursor-pointer">
                                    <el-icon @click="editMemberInfo('headimg')" class="cursor-pointer">
                                        <EditPen color="#FB1818" />
                                    </el-icon>
                                    <span class="text-[#FB1818] text-[14px] ml-1">删除</span>
                                </div> -->
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
                                        {{ formData.nickname || t('notAvailable') }}<el-icon @click="editMemberInfo('nickname')" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
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
                                        {{ formData.birthday || t('notAvailable') }}<el-icon @click="editMemberInfo('birthday')" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('sex') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.sex == 1 && t('manSex') || formData.sex == 2 && t('girlSex') || t('secrecySex') }}<el-icon @click="editMemberInfo('sex')" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('wxUnionid') }}</span>
                                    <span class="text-[14px] word-break break-all">
                                        {{ formData.wx_unionid || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('weappOpenid') }}</span>
                                    <span class="text-[14px] word-break break-all">
                                        {{ formData.weapp_openid || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('wxOpenid') }}</span>
                                    <span class="text-[14px] word-break break-all">
                                        {{ formData.wx_openid || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('registeredSource') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.register_channel_name || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('注册时间') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.create_time || t('notAvailable') }}
                                    </span>
                                </div>
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] flex-shrink-0 text-[#666666] mr-[20px]">{{ t('lastVisitTime') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.last_visit_time || t('notAvailable') }}
                                    </span>
                                </div>
                            </el-tab-pane>
                            <el-tab-pane name="formInfo" label="表单信息" v-if="formData.form_info && Object.values(formData.form_info).length &&formData.form_info?.recordsFieldList"> 
                                <div class="flex flex-wrap mb-[20px]" v-if="formData.form_info && Object.values(formData.form_info).length &&formData.form_info?.recordsFieldList">
                                    <div class="flex items-center mb-[15px]" v-for="(field, fieldKey) in formData.form_info?.recordsFieldList || []" :key="fieldKey">
                                        <span class="text-[14px] w-[130px] text-[#666666]  mr-[20px]">{{ field.field_name }}</span>
                                        <span class="text-[14px]">
                                            <component :is="formData.form_info?.recordsFieldList[fieldKey].detailComponent" :data="formData.form_info?.recordsFieldList[fieldKey]" />
                                        </span>
                                    </div>
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
                            会员资产
                        </div>
                    </template>
					<div class="flex justify-between gap-4">
						<div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('积分') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.point }}
									</div>
                                </div>
                                <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/point.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>
							<div class="flex justify-between items-center">
								<div class="text-[14px] text-[#333333]">
									<span>{{ t('累计积分') }}</span>
									<span class="ml-1 text-primary">
										{{ formData.point_get }}
									</span>
								</div>
                                <div>
                                    <span class="text-primary text-[12px] cursor-pointer"  @click="adjustPoint(formData)" >编辑</span>
                                </div>
							</div>
						</div>
                        <div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('余额') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.balance }}
									</div>
                                </div>
                                 <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/balance.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>
							<div class="flex justify-between items-center">
								<div class="text-[14px] text-[#333333]">
									<span>{{ t('累计余额') }}</span>
									<span class="ml-1 text-primary">
										{{ formData.balance_get }}
									</span>
								</div>
                                <div>
                                    <span class="text-primary text-[12px] cursor-pointer" @click="adjustBalance(formData)">编辑</span>
                                </div>
							</div>
						</div>
                        <div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('可提现余额') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.money }}
									</div>
                                </div>
                                <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/money.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>
							<div class="flex justify-between items-center">
								<div class="text-[14px] text-[#333333]">
									<span>{{ t('累计提现') }}</span>
									<span class="ml-1 text-primary">
										{{ formData.money_get }}
									</span>
								</div>
                                <div>
                                    <!-- <span class="text-primary text-[12px] cursor-pointer">编辑</span> -->
                                </div>
							</div>
						</div>
						<div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('佣金') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.commission }}
									</div>
                                </div>
                                 <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/commission.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>
							<div class="flex justify-between items-center">
								<div class="text-[14px] text-[#333333]">
									<span>{{ t('累计佣金') }}</span>
									<span class="ml-1 text-primary">
										{{ formData.commission_get }}
									</span>
								</div>
                                <div>
                                    <!-- <span class="text-primary text-[12px] cursor-pointer">编辑</span> -->
                                </div>
							</div>
						</div>
                        <div class="statistic-card">
                            <div class="flex justify-between">
                                <div>
                                    <div class="text-[14px]">
										{{ t('成长值') }}
									</div>
                                    <div class="text-[22px] font-bold mt-1">
										{{ formData.growth }}
									</div>
                                </div>
                                 <div class="w-[46px] h-[46px] bg-[#EFF4FF] rounded-[8px] flex justify-center items-center">
                                    <img :src="img('static/resource/images/member/detail/growth.png')" class="w-[24px] h-[24px]" alt="">
                                </div>
                            </div>
							<div class="flex justify-between items-center">
								<div class="text-[14px] text-[#333333]">		
									<span class="text-[12px] text-primary cursor-pointer" @click="infoGrowth()">
										查看
									</span>
								</div>
                                <div>
                                    <!-- <span class="text-primary text-[12px] cursor-pointer">编辑</span> -->
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
                                        {{ formData.member_label_name.toString() || t('notAvailable') }}<el-icon @click="editMemberInfo('member_label')" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
                                    </span>
                                </div>
                            </el-col>
                            <el-col :span="8">
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] text-[#666666] text-right  mr-[20px]">{{ t('memberLevel') }}</span>
                                    <span class="text-[14px] ">
                                        {{ formData.member_level_name || t('notAvailable') }}<el-icon @click="editMemberInfo('member_level')" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
                                    </span>
                                </div>
                            </el-col>
                            <el-col :span="8">
                                  <div class="flex items-center mt-[15px]">
                                     <span class="text-[14px] w-[130px] text-[#666666] text-right  mr-[20px]">{{ t('上级推荐人') }}</span>
                                    <span class="text-[14px]">
                                        {{ formData.parent_member?.nickname ? formData.parent_member.nickname : t('notAvailable') }}<el-icon @click="modifyRecommenderEvent(formData)" class="-bottom-[2px] -right-[4px] cursor-pointer">
                                            <EditPen color="#273CE2" />
                                        </el-icon>
                                    </span>
                                </div>
                            </el-col>
                            <el-col :span="8" v-if="formData.vip_card_info">
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] text-[#666666] text-right  mr-[20px]">{{ t('卡名称') }}</span>
                                    <span class="text-[14px]"> {{ formData.vip_card_info?.card_name }}</span>
                                </div>
                            </el-col>
                            <el-col :span="8" v-if="formData.vip_card_info">
                                <div class="flex items-center mt-[15px]">
                                    <span class="text-[14px] w-[130px] text-[#666666] text-right  mr-[20px]">{{ t('到期时间') }}</span>
                                    <span class="text-[14px]"> {{ formData.vip_card_info?.expire_time }}</span>
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
                        <component v-if="dynamicComponentName" :is="dynamicComponentName" :member_id="id" ref="dynamicComponentRefs" />
                    </div>
                </el-card>  
            </div>
        </div>


		<point-edit ref="pointDialog" @complete="getMemberInfoFn" />
		<balance-edit ref="balanceDialog" @complete="getMemberInfoFn" />
		<edit-member ref="editMemberDialog" @complete="getMemberInfoFn()" />
        <reset-password ref="resetPasswordDialog" @complete="getMemberInfoFn()"></reset-password>
        <modify-recommender ref="modifyRecommenderDialog" @complete="getMemberInfoFn()" />


	</div>
</template>

<script lang="ts" setup>
import { reactive, ref ,defineAsyncComponent} from 'vue'
import { t } from '@/lang'
import { getMemberInfo ,getMemberContentType } from '@/app/api/member'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { img } from '@/utils/common'
import PointEdit from '@/app/views/member/components/member-point-edit.vue'
import BalanceEdit from '@/app/views/member/components/member-balance-edit.vue'
import EditMember from '@/app/views/member/components/edit-member.vue'
import useAppStore from '@/stores/modules/app'
import resetPassword from '@/app/views/member/components/reset-password.vue'
import modifyRecommender from '@/app/views/member/components/modify-recommender.vue'


const route = useRoute()
const pageName = route.meta.title
const appStore = useAppStore()

const loading = ref(true)

// 获取会员信息
const id: number = parseInt(route.query.id || 0)
const formData: any = reactive({ member_label_name: '' })
const getMemberContentTypeFn = async () => {
    const res = await getMemberContentType()
    typeList.value = Object.values(res.data)
    activeName.value = typeList.value[0].key
    handleMenuClick(activeName.value)
}
const getMemberInfoFn = async () => {
    loading.value = true
    getMemberContentTypeFn()
    if (id) {
        const data = await (await getMemberInfo(id)).data
        if (!data || Object.keys(data).length == 0) {
            ElMessage.error(t('memberNull'))
            setTimeout(() => {
                router.go(-1)
            }, 2000)
            return false
        }

        Object.keys(data).forEach((item) => {
            formData[item] = data[item]
        })

        if (formData?.member_label_array && Object.keys(formData.member_label_array)?.length) {
            formData.member_label = Object.values(formData.member_label_array).map((item: any, index) => {
                return item.label_id
            })

            formData.member_label_name = Object.values(formData.member_label_array).map((item: any, index) => {
                return item.label_name
            })
        }
         // 获取会员的表单详情
        if (formData.form_info && formData.form_info.recordsFieldList) {
            const recordsFieldList = formData.form_info.recordsFieldList;
            for (const key in recordsFieldList) {
                if (modules[recordsFieldList[key].detailComponent]) {
                    recordsFieldList[key].detailComponent = defineAsyncComponent(modules[recordsFieldList[key].detailComponent]);
                } else {
                    delete recordsFieldList[key];
                }
            }
        }
        loading.value = false
    } else {
        loading.value = false
    }
}
getMemberInfoFn()

const tabBaseType = ref('basicInfo')
const activeName = ref('point')
const typeList = ref([])
const modules: any = import.meta.glob('@/**/*.vue')
const dynamicComponentName: any = ref('') // 动态组件名称

const dynamicComponentRefs: any = ref(null) // 动态组件ref

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

const pointDialog: Record<string, any> | null = ref(null)
const balanceDialog: Record<string, any> | null = ref(null)
const editMemberDialog: Record<string, any> | null = ref(null)

/**
 *  修改会员信息
 */
const editMemberInfo = (type: any) => {
    const data = ref({
        type,
        id,
        data: formData
    })
    editMemberDialog.value.setDialogType(data.value)
    editMemberDialog.value.showDialog = true
}

/**
 * 调整积分
 */
const adjustPoint = (data: any) => {
    pointDialog.value.setFormData(data)
    pointDialog.value.showDialog = true
}

/**
 * 调整余额
 */
const adjustBalance = (data: any) => {
    balanceDialog.value.setFormData(data)
    balanceDialog.value.showDialog = true
}

// 重置密码
const resetPasswordDialog: Record<string, any> | null = ref(null)
const resetPasswordEvent = (data: any) => {
    resetPasswordDialog.value.setFormData(data)
}

// 修改推荐人
const modifyRecommenderDialog: Record<string, any> | null = ref(null)
const modifyRecommenderEvent = (data: any) => { 
    modifyRecommenderDialog.value.setFormData(data)
}

const router = useRouter()

/**
 * 积分详情
 */
const infoPoint = () => {
    router.push(`/member/point?id=${id}`)
}

/**
 * 余额详情
 */
const infoBalance = () => {
    router.push(`/member/balance?id=${id}`)
}

/**
 * 成长值明细
 */
 const infoGrowth = () => {
    router.push(`/member/growth?id=${id}`)
}

/**
 * 佣金详情
 */
const infoCommission = () => {
    router.push(`/member/commission?id=${id}`)
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
    width: 100%;
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
