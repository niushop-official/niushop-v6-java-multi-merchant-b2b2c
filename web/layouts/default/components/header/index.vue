<template>
    <div class="min-w-[1200px] bg-[#fff]">
        <div class="bg-[#f0f0f0] h-[40px]">
            <div class="flex items-center w-[1200px]  mx-auto justify-between h-[40px]">
                <div class="flex items-center">
                    <NuxtLink to="/">
                        <div class="text-[#666] flex items-center hover:text-[var(--el-color-primary)]">
                            <i class="iconfont icon-shouye mr-[5px]  !text-[16px]"></i>
                            <span class="text-[12px] leading-[18px]">首页</span>
                        </div>
                    </NuxtLink>
                    <div class="ml-[30px]">
                        <el-popover placement="bottom" :width="weappImage ? '250px' : '130px'" trigger="hover" :popper-class="weappImage ? '!min-w-[250px]': '!min-w-[130px]' ">
                            <template #reference>
                                <div class="text-[#666] flex items-center cursor-pointer hover:text-[var(--el-color-primary)]">
                                    <i class="iconfont icon-shoujiV6xx mr-[5px] !text-[14px]"></i>
                                    <span class="text-[12px] leading-[18px]">手机商城</span>
                                </div>
                            </template>
                            <div class="flex">
                                <div class="mr-[20px]" v-if="weappImage">
                                    <div class="promote-img flex justify-center items-center w-[100px] h-[100px] rounded-[4px] mb-[10px] border-solid border-[1px] border-[#eee]">
                                        <el-image :src="img(weappImage)" class=" w-[92px] h-[92px]" />
                                    </div>
                                    <div class="text-center  flex items-center justify-center">
                                        <span class="text-[12px]">小程序商城</span>
                                    </div>
                                </div>
                                <div>
                                    <div class="promote-img flex justify-center items-center w-[100px] h-[100px] rounded-[4px] mb-[10px] border-solid border-[1px] border-[#eee]">
                                        <el-image :src="wapImage" class=" w-[92px] h-[92px]" />
                                    </div>
                                    <div class="text-center  flex items-center justify-center">
                                        <span class="text-[12px]">H5商城</span>
                                    </div>
                                </div>
                            </div>
                        </el-popover>
                    </div>
                </div>
                <div class="flex items-center  whitespace-pre text-[#666] text-[12px] ">
                    <div class="mr-[8px] pl-[8px]">
                        <div class="flex items-center" v-if="info">
                            <NuxtLink to="/member/center">
                                <div class="flex items-center">
                                    <img v-if="!info.headimg" class="w-[22px] h-[22px] rounded-full" src="@/assets/images/default_headimg.png" alt="">
                                    <img v-else :src="img(info.headimg)" class="w-[22px] h-[22px] rounded-full" alt="">
                                    <span class="cursor-pointer ml-[8px] text-[var(--el-color-primary)]">{{ info.nickname }}</span>
                                </div>
                            </NuxtLink>
                            <span class="mx-2"></span>
                            <span class="cursor-pointer hover:text-[var(--el-color-primary)]" @click="logoutFn">退出</span>
                        </div>
                        <div v-else class="mr-[8px] pl-[8px] cursor-pointer" @click="toLogin">{{ t('login') }}/{{ t('register') }}</div>
                    </div>
                    <NuxtLink v-if="info" to="/order/list">
                            <div class="mr-[12px] pl-[12px] user-item hover:text-[var(--el-color-primary)]">我的订单</div>
                    </NuxtLink>
                    <div v-else class="mr-[12px] pl-[12px] user-item  cursor-pointer hover:text-[var(--el-color-primary)]"  @click="toLogin">我的订单</div>

                    <NuxtLink to="/article/list">
                            <div class="mr-[12px] pl-[12px] user-item hover:text-[var(--el-color-primary)]">资讯信息</div>
                    </NuxtLink>
                    <div class="pl-[12px]">
                        <el-dropdown trigger="hover" @command="handleCommand">
                            <div class="text-[#666] text-[12px] mr-[8px] cursor-pointer hover:text-[var(--el-color-primary)]">
                                <span>商户入驻</span>
                                <span class="iconfont icon-xiaV6xx !text-[12px] ml-[5px]"></span>
                            </div>
                            <template #dropdown>
                                <el-dropdown-menu >
                                    <el-dropdown-item command="enter">
                                        <div class="text-[12px] py-[5px] hover:text-[var(--el-color-primary)]">商户入驻</div>
                                    </el-dropdown-item>
                                    <el-dropdown-item command="record">
                                        <div class="text-[12px] py-[5px] hover:text-[var(--el-color-primary)]">申请记录</div>
                                    </el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                    </div>
                </div>
            </div>
        </div>
        <div class="h-[100px] bg-[#fff]">
            <div class="w-[1200px] h-[100px] mx-auto flex items-center justify-between">
                <NuxtLink to="/">
                    <div class="max-w-[158px] mr-[10px]">
                         <el-image class="max-w-[158px] max-h-[28px]" :src="img((siteInfo && siteInfo.front_end_logo) ? siteInfo.front_end_logo : '' )" fit="contain">
                            <template #error>
                                <img class="max-w-[158px] max-h-[28px]" :src="img('/static/resource/images/web/logo.png')" />
                            </template>
                        </el-image>
                    </div>
                </NuxtLink>
                <div class="search w-[610px] h-[40px] border-primary rounded-[100px] border-solid border-[1px] flex items-center border-box">
                    <el-input v-model="search" style="width: 530px" class="oppoSans-M" size="large" placeholder="搜索商品或店铺">
                        <template #prepend>
                            <el-select v-model="type" style="width:74px;font-size: 14px;margin: 0;">
                                <el-option label="商品" value="goods" />
                                <el-option label="店铺" value="shop" />
                            </el-select>
                        </template>
                    </el-input>
                    <div class="w-[80px] h-full flex justify-center items-center leading-[38px] text-[#fff] bg-primary  cursor-pointer rounded-[100px]" @click="searchFn">
                        <span class="iconfont icon-sousuo !text-[16px]"></span>
                    </div>
                </div>
                <div class="h-[40px] w-[140px] flex-center border-[#ddd] border-solid border-[1px]  text-primary cursor-pointer rounded-[100px]" @click="toLink">
                    <span class="iconfont icon-gouwucheV6xx-2 text-[14px] mr-[10px]"></span>
                    <span class="text-[14px]">购物车({{cartTotalNum}})</span>
                </div>
            </div>
        </div>
        <LoadingDialog/>
    </div>
</template>

<script lang="ts" setup>
import {ref, computed,watch} from 'vue'
import useMemberStore from '@/stores/member'
import useSystemStore from '@/stores/system'
import { ElMessageBox } from 'element-plus'
import { useRouter,useRoute } from 'vue-router'
import { img, getToken } from '@/utils/common'
import useCartStore from '@/addon/mall/stores/cart'
import useConfigStore from '@/stores/config'
import LoadingDialog from '@/components/login-dialog/index.vue'
import QRCode from 'qrcode'
import { getQrcode } from '@/app/api/index'

const router = useRouter()
const route = useRoute()
const memberStore = useMemberStore()
const configStore = useConfigStore()
const info = computed(() => memberStore.info)
// 站点信息
useSystemStore().getSiteInfoFn()
const systemStore = useSystemStore()
const siteInfo = computed(() => systemStore.site)
// 购物车数量
const cartStore = useCartStore();
cartStore.getList()
let cartTotalNum = computed(() => cartStore.totalNum)

const logoutFn = () => {
    ElMessageBox.confirm('您确定要退出账号吗？', '提示',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            confirmButtonClass:'!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]',
            cancelButtonClass:'!border-[#dcdfe6]',
            type: 'warning'
        }
    ).then(() => {
        memberStore.logout()
        navigateTo(`/`)
    })
}
const search = ref('')
const type = ref('goods')
const searchFn = () =>{
    if(type.value == 'goods'){
        if(route.query.site_id){
            router.push({path:'/shop/detail',query:{keyword:search.value,site_id:route.query.site_id}})
        }else{
            router.push({path:'/goods/list',query:{keyword:search.value}})
        }
    }else{
        router.push({path:'/shop/list',query:{keyword:search.value}})
    }
    
}
const toLink = ()=>{
    if(!getToken()){
        if(!configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile){
            ElMessage.error('商家未开启普通账号登录注册')
            return false
        }else{
            memberStore.logOpen()
            return false
        }
    }
    router.push(`/goods/cart`)
}
// 未登录
const toLogin = () =>{
    if(!getToken() && !configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile){
        ElMessage.error('商家未开启普通账号登录注册')
        return false
    }
    memberStore.logOpen()
}
watch(()=> route.path ,(newValue,oldValue)=>{
    if(route.query.keyword) search.value = route.query.keyword as string
    else search.value = ''
},{immediate:true,deep: true})

const wapPreview = ref('')
const wapImage = ref('')
const loadQrcode = () => {
    wapPreview.value = `${location.origin}/wap/`
    // errorCorrectionLevel：密度容错率L（低）H(高)
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}
loadQrcode()

const weappImage = ref('')
const loadWeapp = () => {
	getQrcode({
        scene: 'wap_index',
        type: 'image',
        channel: 'weapp'
    }).then((res:any) =>{
		weappImage.value = res.data
	})
}
 loadWeapp()


// 商户入驻
const handleCommand = (type:string) =>{
    if(!getToken()){
        if(!configStore.login.is_username && !configStore.login.is_mobile && !configStore.login.is_bind_mobile){
            ElMessage.error('商家未开启普通账号登录注册')
            return false
        }else{
            memberStore.logOpen()
            return false
        }
    }
    if(type == 'enter'){
        router.push(`/member/merchant_settled`)
    }else{
        router.push(`/member/merchant_record`)
    }
}
</script>

<style lang="scss" scoped>
:deep(.el-input__wrapper){
    background-color: inherit;
    box-shadow: none;
    padding-left: 20px !important;
}
:deep(.el-input .el-input__inner){
    font-family:  'oppoSans-M';
}
:deep(.search .el-input-group__prepend){
    background-color: inherit;
    box-shadow: none;
    padding: 0;
}
:deep(.search .el-input-group__prepend .el-select .el-input .el-input__wrapper){
    background-color: inherit;
    box-shadow: none;
    padding: 0 ;
    padding-left: 20px;
}
:deep(.search .el-input-group--prepend .el-input-group__prepend .el-select:hover .el-input__wrapper){
    box-shadow: none !important;
}
:deep(.search .el-select .el-input.is-focus .el-input__wrapper){
    box-shadow: none !important;
}
:deep(.search .el-select .el-input__wrapper.is-focus){
    box-shadow: none !important;
}
:deep(:focus-visible) {
    outline: none;
}
:deep(.el-dropdown-menu__item) {
	&:focus {
		background-color: transparent !important;
		color: #333 !important;
	}
}
</style>
