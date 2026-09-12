<template>
    <view class="diy-group" id="componentList">
        <top-tabbar
            v-if="data.global && Object.keys(data.global).length && data.global.topStatusBar && data.global.topStatusBar.isShow"
            :scrollBool="diyGroup.componentsScrollBool.TopTabbar" ref="topTabbarRef" :data="data.global" />
        <pop-ads
            v-if="data.global && Object.keys(data.global).length && data.global.popWindow && data.global.popWindow.show"
            ref="popAbsRef" :data="data.global" />
        <template v-for="(component, index) in data.value" :key="component.id">
            <view v-show="component.componentIsShow" @click="diyStore.changeCurrentIndex(index, component)"
                :class="diyGroup.getComponentClass(index,component)" :style="component.pageStyle">
                <view class="relative"
                    :style="{ marginTop : component.margin.top < 0 ? (component.margin.top * 2) + 'rpx' : '0', marginBottom : component.margin.bottom < 0 ? (component.margin.bottom * 2) + 'rpx' : '0' }">
                    <!-- 装修模式下，设置负上边距后超出的内容，禁止选中设置 -->
                    <view v-if="diyGroup.isShowPlaceHolder(index,component)" class="absolute w-full z-1"
                        :style="{ height : (component.margin.top * 2 * -1) + 'rpx' }"
                        @click.stop="diyGroup.placeholderEvent"></view>

                    <template v-if="component.componentName == 'GraphicNav'">
                        <diy-graphic-nav :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'HorzBlank'">
                        <diy-horz-blank :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'HorzLine'">
                        <diy-horz-line :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'HotArea'">
                        <diy-hot-area :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'ImageAds'">
                        <diy-image-ads :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'MemberInfo'">
                        <diy-member-info :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'MemberLevel'">
                        <diy-member-level :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'Notice'">
                        <diy-notice :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'RubikCube'">
                        <diy-rubik-cube :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'Text'">
                        <diy-text :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'RichText'">
                        <diy-rich-text :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'ActiveCube'">
                        <diy-active-cube :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FloatBtn'">
                        <diy-float-btn :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'CarouselSearch'">
                        <diy-carousel-search :component="component" :global="data.global" :index="index"
                            :scrollBool="diyGroup.componentsScrollBool.CarouselSearch" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'PictureShow'">
                        <diy-picture-show :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'Article'">
                        <diy-article :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormSubmit'">
                        <diy-form-submit :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormInput'">
                        <diy-form-input ref="diyFormInputRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormTextarea'">
                        <diy-form-textarea ref="diyFormTextareaRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormIdentity'">
                        <diy-form-identity ref="diyFormIdentityRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormEmail'">
                        <diy-form-email ref="diyFormEmailRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormMobile'">
                        <diy-form-mobile ref="diyFormMobileRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormWechatName'">
                        <diy-form-wechat-name ref="diyFormWechatNameRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormNumber'">
                        <diy-form-number ref="diyFormNumberRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormRadio'">
                        <diy-form-radio ref="diyFormRadioRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormCheckbox'">
                        <diy-form-checkbox ref="diyFormCheckboxRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormTable'">
                        <diy-form-table ref="diyFormTableRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormDate'">
                        <diy-form-date ref="diyFormDateRef" :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormDateScope'">
                        <diy-form-date-scope ref="diyFormDateScopeRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormTime'">
                        <diy-form-time ref="diyFormTimeRef" :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormTimeScope'">
                        <diy-form-time-scope ref="diyFormTimeScopeRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormLocation'">
                        <diy-form-location ref="diyFormLocationRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormAddress'">
                        <diy-form-address ref="diyFormAddressRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormImage'">
                        <diy-form-image ref="diyFormImageRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormVideo'">
                        <diy-form-video ref="diyFormVideoRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormFile'">
                        <diy-form-file ref="diyFormFileRef" :component="component" :global="data.global" :index="index"
                            :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormIdentityPrivacy'">
                        <diy-form-identity-privacy ref="diyFormIdentityPrivacyRef" :component="component"
                            :global="data.global" :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormPrivacy'">
                        <diy-form-privacy ref="diyFormPrivacyRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                    <template v-if="component.componentName == 'FormPrivacyPop'">
                        <diy-form-privacy-pop ref="diyFormPrivacyPopRef" :component="component" :global="data.global"
                            :index="index" :overallData="data.value" />
                    </template>
                                <template v-if="component.componentName == 'MallEmpty'">
                <diy-mall-empty :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallEmpty" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsCoupon'">
                <diy-mall-goods-coupon :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsCoupon" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailAttr'">
                <diy-mall-goods-detail-attr :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailAttr" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailBasicInfo'">
                <diy-mall-goods-detail-basic-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailBasicInfo" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailBottom'">
                <diy-mall-goods-detail-bottom :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailBottom" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailDesc'">
                <diy-mall-goods-detail-desc :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailDesc" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailEvaluate'">
                <diy-mall-goods-detail-evaluate :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailEvaluate" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailPurchaseService'">
                <diy-mall-goods-detail-purchase-service :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailPurchaseService" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailRank'">
                <diy-mall-goods-detail-rank :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailRank" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailShopInfo'">
                <diy-mall-goods-detail-shop-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailShopInfo" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsDetailSow'">
                <diy-mall-goods-detail-sow :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsDetailSow" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsList'">
                <diy-mall-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsList" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsRanking'">
                <diy-mall-goods-ranking :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsRanking" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallGoodsRecommend'">
                <diy-mall-goods-recommend :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallGoodsRecommend" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallManyGoodsList'">
                <diy-mall-many-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallManyGoodsList" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallManyShopList'">
                <diy-mall-many-shop-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallManyShopList" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallMemberInfo'">
                <diy-mall-member-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallMemberInfo" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallOrderInfo'">
                <diy-mall-order-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallOrderInfo" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallSearch'">
                <diy-mall-search :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallSearch" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallShopCouponList'">
                <diy-mall-shop-coupon-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallShopCouponList" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallShopInfo'">
                <diy-mall-shop-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallShopInfo" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
            <template v-if="component.componentName == 'MallShopList'">
                <diy-mall-shop-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallShopList" @update:componentIsShow="component.componentIsShow = $event" />
            </template>

                </view>
            </view>
        </template>
        <template
            v-if="diyStore.mode == '' && data.global && data.global.bottomTabBar && data.global.bottomTabBar.isShow">
            <view class="pt-[20rpx]"></view>
            <site-tabbar :site-id="diyStore.siteId" v-if="diyStore.siteId" />
            <tabbar v-else />
        </template>
    </view>
</template>
<script lang="ts" setup>
   import diyMallEmpty from '@/addon/mall/components/diy/mall-empty/index.vue';
   import diyMallGoodsCoupon from '@/addon/mall/components/diy/mall-goods-coupon/index.vue';
   import diyMallGoodsDetailAttr from '@/addon/mall/components/diy/mall-goods-detail-attr/index.vue';
   import diyMallGoodsDetailBasicInfo from '@/addon/mall/components/diy/mall-goods-detail-basic-info/index.vue';
   import diyMallGoodsDetailBottom from '@/addon/mall/components/diy/mall-goods-detail-bottom/index.vue';
   import diyMallGoodsDetailDesc from '@/addon/mall/components/diy/mall-goods-detail-desc/index.vue';
   import diyMallGoodsDetailEvaluate from '@/addon/mall/components/diy/mall-goods-detail-evaluate/index.vue';
   import diyMallGoodsDetailPurchaseService from '@/addon/mall/components/diy/mall-goods-detail-purchase-service/index.vue';
   import diyMallGoodsDetailRank from '@/addon/mall/components/diy/mall-goods-detail-rank/index.vue';
   import diyMallGoodsDetailShopInfo from '@/addon/mall/components/diy/mall-goods-detail-shop-info/index.vue';
   import diyMallGoodsDetailSow from '@/addon/mall/components/diy/mall-goods-detail-sow/index.vue';
   import diyMallGoodsList from '@/addon/mall/components/diy/mall-goods-list/index.vue';
   import diyMallGoodsRanking from '@/addon/mall/components/diy/mall-goods-ranking/index.vue';
   import diyMallGoodsRecommend from '@/addon/mall/components/diy/mall-goods-recommend/index.vue';
   import diyMallManyGoodsList from '@/addon/mall/components/diy/mall-many-goods-list/index.vue';
   import diyMallManyShopList from '@/addon/mall/components/diy/mall-many-shop-list/index.vue';
   import diyMallMemberInfo from '@/addon/mall/components/diy/mall-member-info/index.vue';
   import diyMallOrderInfo from '@/addon/mall/components/diy/mall-order-info/index.vue';
   import diyMallSearch from '@/addon/mall/components/diy/mall-search/index.vue';
   import diyMallShopCouponList from '@/addon/mall/components/diy/mall-shop-coupon-list/index.vue';
   import diyMallShopInfo from '@/addon/mall/components/diy/mall-shop-info/index.vue';
   import diyMallShopList from '@/addon/mall/components/diy/mall-shop-list/index.vue';

    import topTabbar from '@/components/top-tabbar/top-tabbar.vue'
    import popAds from '@/components/pop-ads/pop-ads.vue'
    import { useDiyGroup } from './useDiyGroup'
    import useDiyStore from '@/app/stores/diy';
    import { ref, getCurrentInstance } from 'vue';

    const props = defineProps(['data']);
    const topTabbarRef = ref(null);
    const diyStore = useDiyStore();
    const instance : any = getCurrentInstance();
    const getFormRef = () => {
        return {
            componentRefs: instance.refs
        }
    }
    const diyGroup = useDiyGroup({
        ...props,
        getFormRef
    });

    const data = ref(diyGroup.data)

    // 监听页面加载完成
    diyGroup.onMounted()

    // 监听滚动事件
    diyGroup.onPageScroll()

    defineExpose({
        refresh: diyGroup.refresh,
        getFormRef
    })
</script>
<style lang="scss" scoped>
    @import './index.scss';
</style>