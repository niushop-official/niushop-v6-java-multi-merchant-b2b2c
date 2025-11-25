<template>
    <div class="h-[370px]">
        <div class="flex flex-col items-center justify-center px-[25px] h-[370px] bg-[#fff] text-[14px] rounded-[var(--rounded-mid)]" v-if="Object.values(detail).length">
            <div class="flex flex-col items-center justify-center border-b-[1px] border-dashed border-[#ececec] h-[130px]">
                <div class="w-[61px] h-[61px] mb-[15px]">
                    <el-image class="w-[61px] h-[61px] rounded-full" :src="img(detail.front_end_logo)" fit="cover">
                            <template #error>
                                <img src="@/assets/images/shop_default.png" alt=""  class="w-[61px] h-[61px] rounded-full">
                            </template>
                    </el-image>
                </div>
                <div class="truncate flex items-center">
                    <span class="w-[32px] h-[18px] leading-[18px] text-center text-[#fff] bg-[var(--el-color-primary)] rounded-[2px] mr-[3px] text-[12px]" v-if="detail.is_self">{{ t('self') }}</span>
                    <span class="text-[14px] max-w-[135px] truncate">{{detail.site_name}}</span>
                </div>
            </div>
            <div class="pt-[20px] border-b-[1px] border-dashed border-[#ececec]">
                <div class="text-[#7e7e7e] text-[12px] mb-[15px] flex items-center">
                    <span class="text-[12px] text-[#999] mr-[15px]">{{ t('followNumber') }}</span>
                    <span class="text-[#333]">{{ detail.follow_number }}{{ t('people') }}</span>
                </div>
                <div class="text-[#7e7e7e] text-[12px] mb-[15px] flex items-center">
                    <span class="text-[12px] text-[#999] mr-[15px]">{{ t('shopCategory') }}</span>
                    <span class="text-[#333]">{{detail.category_name}}</span>
                </div>
                <div class="text-[#7e7e7e] text-[12px] mb-[15px] flex items-center cursor-pointer" v-if="detail.phone" @click="handlePhoneShow">
                    <span class="text-[12px] text-[#999] mr-[15px]">{{ t('service') }}</span>
                    <span class="iconfont icon-kefu text-[var(--el-color-primary)]"></span>
                </div>
                <div class="text-[#7e7e7e] text-[12px] mb-[15px] flex items-center cursor-pointer" @click="handleWeappShow" v-if="configStore.weappOpen">
                    <span class="text-[12px] text-[#999] mr-[15px]">店铺二维码</span>
                    <span class="iconfont icon-xiaochengxushezhi text-[var(--el-color-primary)]"></span>
                </div>
            </div>
            <div class="mt-[14px]">
                <div class="w-[78px] h-[30px] leading-[28px] text-[12px] border-solid border-[1px] border-primary text-primary text-center rounded-full cursor-pointer" v-if="!isFollow" @click="collectShop(detail.site_id,1)">{{ t('followShop') }}</div>
                <div class="w-[78px] h-[30px] leading-[30px] bg-primary text-[#fff] text-[12px] text-center rounded-full cursor-pointer" v-else  @click="collectShop(detail.site_id,0)">{{ t('followed') }}</div>
            </div>
        </div>
        <el-dialog v-model="dialogVisible" align-center width="530" custom-class="weapp !rounded-[var(--rounded-big)]" :show-close="false">
            <template #header>
                <div class="text-center">店铺小程序二维码</div>
            </template>
            <div>
                <span class="iconfont icon-tubiaoV6-3 absolute top-[-33px] right-[-33px] text-[#fff] !text-[24px] cursor-pointer" @click="handleClose"></span>
                <div class="flex-center">
                    <el-image class="w-[340px] h-[340px] rounded-[12px]" :src="img(qrcodeImg)" fit="cover">
                        <template #error>
                            <div class="w-[340px] h-[340px] rounded-[12px] bg-[#f6f6f6]"></div>
                        </template>
                    </el-image>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { ref,reactive,computed, watch } from 'vue'
import { getShopInfo, editShopCollect, getShopQrcode} from '@/addon/mall/api/shop'
import useMemberStore from '@/stores/member'
import useConfigStore from '@/stores/config'
import { useRouter, useRoute } from 'vue-router'

const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

const router = useRouter()
const route = useRoute()
const detail = ref({})
const loading = ref(true)
const phoneShow = ref(false)


const qrcodeImg = ref('')
const loadWeapp = (id: any) => {
	getShopQrcode(id).then((res:any) =>{
		qrcodeImg.value = res.data
	})
}

const configStore = useConfigStore()

watch(() => configStore.weappOpen, (nval, oval) => {
    if(nval){
        loadWeapp(route.query.site_id)
    }
})
if(configStore.weappOpen){
    loadWeapp(route.query.site_id)
}

const getShopDetailFn = (id: any) => {
    loading.value = true
	getShopInfo(id).then((res: any) => {
        if(res.data.business_status == 0){
            navigateTo('/site/store_close', { replace: true })
        }
		detail.value = res.data
        isFollow.value = detail.value.member_info.is_follow
        loading.value = false
    }).catch(()=>{
        loading.value = false
    })
}
getShopDetailFn(route.query.site_id)



// 收藏店铺
let isFollow = ref(0);
const collectShop = (id:any,is_follow:any) =>{
	// 检测是否登录
	if (!userInfo.value) {
		memberStore.logOpen()
		return false
	}
	editShopCollect({
		site_id: id,
		is_follow: is_follow
	}).then(res => {
		isFollow.value = !isFollow.value
		if(isFollow.value){
			ElMessage({
				message: '关注成功',
				type: 'success',
			})
		}else{
			ElMessage({
				message: '取消关注成功',
				type: 'success',
			})
		}
	})
}
const handlePhoneShow = () => {
	ElMessageBox.confirm(
        `联系电话：${detail.value.phone}`,'温馨提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {})
}

const dialogVisible = ref(false)
const  handleWeappShow = () => {
    dialogVisible.value = true
}
const handleClose = () => {
    dialogVisible.value = false
}
</script>
<style lang="scss" scoped>
.weapp{
    position: relative;
}
:deep(.el-dialog__header){
    padding: 30px 0 !important;
}

:deep(.el-dialog__body){
    padding-top: 0 !important;
}
:deep(.weapp .el-dialog__headerbtn){
    z-index: 99;
}
</style>