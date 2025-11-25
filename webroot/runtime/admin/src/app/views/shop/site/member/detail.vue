<template>
	<div class="main-container" v-loading="loading">
		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push({ path: '/shop/member/list' })" />
        </el-card>
		<el-card class="box-card !border-none" shadow="never" v-if="Object.keys(formData).length">
			<el-tabs v-model="activeName" class="demo-tabs" @tab-change="handleClick">
				<el-tab-pane :label="t('essentialInfo')" name=""></el-tab-pane>
				<el-tab-pane :label="t('couponMember')" name="1"></el-tab-pane>
			</el-tabs>
			<div v-if="activeName != 1">
				<el-card class="box-card !border-none" shadow="never">
					<el-row>
						<el-col :span="12">
							<div class="bg-[#fff]">
								<el-statistic :value="formData.comsum_num"></el-statistic>
								<div class="statistic-footer">
									<div class="text-[14px] text-[#666]">
										<span>{{ t('consumeNum') }}</span>
									</div>
								</div>
							</div>
						</el-col>
						<el-col :span="12">
							<div class="bg-[#fff]">
								<el-statistic :value="formData.comsum_money"></el-statistic>
								<div class="statistic-footer">
									<div class="text-[14px] text-[#666]">
										<span>{{ t('comsumMoney') }}</span>
									</div>
								</div>
							</div>
						</el-col>
					</el-row>
				</el-card>
				<el-card class="box-card !border-none" shadow="never">
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('headimg') }}</span>
						<span class="text-[14px] text-[#666666]">
							<img class="w-[50px] h-[50px] inline-block" v-if="formData.headimg" :src="img(formData.headimg)" alt="">
							<img class="w-[50px] h-[50px] inline-block" v-else src="@/app/assets/images/default_headimg.png" alt="">
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] min-w-[110px] text-right mr-[20px]">UID</span>
						<span class="member-info-item text-[14px] text-[#666666] font-bold w-[150px]">
							{{ formData.member_no || t('notAvailable') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('urserName') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.username || t('notAvailable') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('nickname') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.nickname || t('notAvailable') }}
						</span>
					</div>

					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('mobile') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.mobile ? mobileHide(formData.mobile) : t('notAvailable') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('memberLabel') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.member_label_name.toString() || t('notAvailable') }}<el-icon @click="editMemberInfo('shop_member_label')" class="-bottom-[2px] -right-[4px] cursor-pointer">
								<EditPen color="#273CE2" />
							</el-icon>
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('birthday') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.birthday || t('notAvailable') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('sex') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.sex == 1 && t('manSex') || formData.sex == 2 && t('girlSex') || t('secrecySex') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('registeredSource') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.register_channel_name || t('notAvailable') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('firstConsumTime') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.first_consum_time || t('notAvailable') }}
						</span>
					</div>
					<div class="flex items-center mt-[15px]">
						<span class="text-[14px] w-[130px] text-right mr-[20px]">{{ t('lastConsumTime') }}</span>
						<span class="text-[14px] text-[#666666]">
							{{ formData.last_consum_time || t('notAvailable') }}
						</span>
					</div>
				</el-card>
			</div>
			<div v-else>
				<el-table :data="formData.coupon_member" size="large">
					<el-table-column prop="title" :label="t('title')" min-width="50" align="left" />
					<el-table-column prop="price" :label="t('price')" min-width="50" align="left" />
					<el-table-column prop="min_condition_money" :label="t('minConditionMoney')" min-width="50" align="left" />
					<el-table-column prop="create_time" :label="t('createTime')" min-width="50" align="left" />
					<el-table-column prop="expire_time" :label="t('expireTime')" min-width="50" align="left" />
				</el-table>
			</div>
			<edit-member ref="editMemberDialog" @complete="getMemberInfoFn()" />
		</el-card>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getShopMemberInfo, getShopMemberCoupon } from '@/app/api/shop_site'
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

const handleClick = (event: any) => {
    activeName.value = event
}

// 获取会员信息
const member_id: number = parseInt(route.query.member_id || 0)
const formData: any = reactive({ member_label_name: '', coupon_member: [] })

const getMemberInfoFn = async () => {
    loading.value = true
    if (member_id) {
        const data = await (await getShopMemberInfo(member_id)).data
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
.el-col {
	background-color: #FAFAFD;
}

.member-info {
	left: 0px
}

.member-info-item {
	overflow: hidden;
	white-space: nowrap;
	text-overflow: ellipsis;
	-o-text-overflow: ellipsis;
}
</style>
