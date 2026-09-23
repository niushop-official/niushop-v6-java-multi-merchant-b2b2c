<template>
    <div class="w-[180px] flex-shrink-0">
        <div class="bg-[#fff] pt-[30px] pr-[30px] pb-[21px] pl-[45px] rounded-[var(--rounded-big)]">
            <el-collapse v-model="activeNames">
                <el-collapse-item name="1" class="!mb-[24px]">
                    <template #title>
                        <div class="flex items-center">
                            <span class="text-[16px]">账户设置</span>
                        </div>
                    </template>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/member/center'}"  @click="router.push('/member/center')">个人资料</div>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666]" :class="{'!text-primary': appStore.route == '/member/address_list'}" @click="router.push('/member/address_list')">地址管理</div>
                </el-collapse-item>
                <el-collapse-item name="2" class="!mb-[24px]">
                    <template #title>
                        <div class="flex items-center">
                            <span class="text-[16px]">我的账户</span>
                        </div>
                    </template>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/member/point'}"  @click="router.push('/member/point')">我的积分</div>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/member/balance'}" @click="router.push('/member/balance')">我的余额</div>
                    
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666]" :class="{'!text-primary': appStore.route == '/member/my_coupon'}" @click="router.push('/member/my_coupon')">我的优惠劵</div>
                </el-collapse-item>
                <el-collapse-item name="3" class="!mb-[24px]">
                  <template #title>
                    <div class="flex items-center">
                      <span class="text-[16px]">收藏夹</span>
                    </div>
                  </template>
                  <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/member/collect/goods'}" @click="router.push('/member/collect/goods')">收藏的宝贝</div>
                  <div class="text-[14px] leading-[24px] cursor-pointer text-[#666]" :class="{'!text-primary': appStore.route == '/member/collect/shop'}" @click="router.push('/member/collect/shop')">收藏的店铺</div>
                </el-collapse-item>
                <el-collapse-item name="4" class="!mb-[24px]">
                  <template #title>
                    <div class="flex items-center">
                      <span class="text-[16px]">历史记录</span>
                    </div>
                  </template>
                  <div class="text-[14px] leading-[24px] cursor-pointer text-[#666]" :class="{'!text-primary': appStore.route == '/member/browse'}"  @click="router.push('/member/browse')">我的足迹</div>
                </el-collapse-item>
                <el-collapse-item name="5" class="!mb-[24px]">
                    <template #title>
                        <div class="flex items-center">
                            <span class="text-[16px] oppoSans-M">订单中心</span>
                        </div>
                    </template>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/order/list'}" @click="router.push('/order/list')">我的订单</div>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] " :class="{'!text-primary': appStore.route == '/member/invoice'}" @click="router.push('/member/invoice')">我的发票</div>
                </el-collapse-item>
                <el-collapse-item name="6" class="!mb-[24px]">
                    <template #title>
                        <div class="flex items-center">
                            <span class="text-[16px]">售后服务</span>
                        </div>
                    </template>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666]" :class="{'!text-primary': appStore.route == '/refund/list'}" @click="router.push('/refund/list')">售后/退款</div>
                </el-collapse-item>
                <el-collapse-item name="7" class="!mb-[24px]">
                    <template #title>
                        <div class="flex items-center">
                            <span class="text-[16px] oppoSans-M">平台协议</span>
                        </div>
                    </template>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/auth/agreement' && route.query.key == 'service'}" @click="router.push({path:'/auth/agreement',query:{key:'service'}})">用户协议</div>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] mb-[24px]" :class="{'!text-primary': appStore.route == '/auth/agreement' && route.query.key == 'privacy'}" @click="router.push({path:'/auth/agreement',query:{key:'privacy'}})">隐私协议</div>
                    <div class="text-[14px] leading-[24px] cursor-pointer text-[#666] " :class="{'!text-primary': appStore.route == '/auth/agreement' && route.query.key == 'shop_apply'}" @click="router.push({path:'/auth/agreement',query:{key:'shop_apply'}})">入驻协议</div>
                </el-collapse-item>
            </el-collapse>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import useAppStore from '@/stores/app'
import { useRouter, useRoute} from 'vue-router'

const appStore = useAppStore()
const router = useRouter()
const route = useRoute()
const activeNames = ref(['1','2','3','4','5','6','7'])
</script>

<style lang="scss" scoped>
:deep(.el-collapse){
    border:none !important;
}
:deep(.el-collapse-item__wrap){
    border:none !important;
}
:deep(.el-collapse-item__header){
    --el-collapse-header-height: auto !important;
    --el-collapse-header-font-size: 16px;
    border:none !important;
    margin-bottom: 24px;
}
:deep(.el-collapse-item__content){
    padding-bottom:0 !important;
}

</style>