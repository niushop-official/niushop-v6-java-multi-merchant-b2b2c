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
            <template v-if="component.componentName == 'SingleRecommend'">
                <diy-single-recommend :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SingleRecommend" @update:componentIsShow="component.componentIsShow = $event" />
            </template>
<!--            <template v-if="component.componentName == 'SeckillGoods'">-->
<!--                <diy-seckill-goods :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillGoods" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillGoodsDetailBasicInfo'">-->
<!--                <diy-seckill-goods-detail-basic-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillGoodsDetailBasicInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillGoodsDetailBottom'">-->
<!--                <diy-seckill-goods-detail-bottom :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillGoodsDetailBottom" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillGoodsDetailDesc'">-->
<!--                <diy-seckill-goods-detail-desc :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillGoodsDetailDesc" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillGoodsDetailPurchaseService'">-->
<!--                <diy-seckill-goods-detail-purchase-service :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillGoodsDetailPurchaseService" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillHot'">-->
<!--                <diy-seckill-hot :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillHot" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillRubikCube'">-->
<!--                <diy-seckill-rubik-cube :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillRubikCube" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'SeckillSpecialOffer'">-->
<!--                <diy-seckill-special-offer :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.SeckillSpecialOffer" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PointsMallGoodsList'">-->
<!--                <diy-points-mall-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PointsMallGoodsList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PointsMallInfo'">-->
<!--                <diy-points-mall-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PointsMallInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PointsMallManyGoodsList'">-->
<!--                <diy-points-mall-many-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PointsMallManyGoodsList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailBasicInfo'">-->
<!--                <diy-pintuan-activity-goods-detail-basic-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailBasicInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailBottom'">-->
<!--                <diy-pintuan-activity-goods-detail-bottom :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailBottom" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailDesc'">-->
<!--                <diy-pintuan-activity-goods-detail-desc :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailDesc" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailGameplay'">-->
<!--                <diy-pintuan-activity-goods-detail-gameplay :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailGameplay" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailGoodsInfo'">-->
<!--                <diy-pintuan-activity-goods-detail-goods-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailGoodsInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailPintuanInfo'">-->
<!--                <diy-pintuan-activity-goods-detail-pintuan-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailPintuanInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanActivityGoodsDetailProgress'">-->
<!--                <diy-pintuan-activity-goods-detail-progress :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanActivityGoodsDetailProgress" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanGoodsDetailBasicInfo'">-->
<!--                <diy-pintuan-goods-detail-basic-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanGoodsDetailBasicInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanGoodsDetailBottom'">-->
<!--                <diy-pintuan-goods-detail-bottom :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanGoodsDetailBottom" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanGoodsDetailDesc'">-->
<!--                <diy-pintuan-goods-detail-desc :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanGoodsDetailDesc" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanGoodsDetailGameplay'">-->
<!--                <diy-pintuan-goods-detail-gameplay :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanGoodsDetailGameplay" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanGoodsDetailProgress'">-->
<!--                <diy-pintuan-goods-detail-progress :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanGoodsDetailProgress" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanGoodsDetailPurchaseService'">-->
<!--                <diy-pintuan-goods-detail-purchase-service :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanGoodsDetailPurchaseService" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanHot'">-->
<!--                <diy-pintuan-hot :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanHot" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanImmediately'">-->
<!--                <diy-pintuan-immediately :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanImmediately" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanLimit'">-->
<!--                <diy-pintuan-limit :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanLimit" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanList'">-->
<!--                <diy-pintuan-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanMemberInfo'">-->
<!--                <diy-pintuan-member-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanMemberInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'PintuanOrderInfo'">-->
<!--                <diy-pintuan-order-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.PintuanOrderInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FrontWarehouseCategoryGoodsList'">-->
<!--                <diy-front-warehouse-category-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FrontWarehouseCategoryGoodsList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FrontWarehouseManyGoodsList'">-->
<!--                <diy-front-warehouse-many-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FrontWarehouseManyGoodsList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FrontWarehouseMemberInfo'">-->
<!--                <diy-front-warehouse-member-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FrontWarehouseMemberInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FrontWarehouseOrderInfo'">-->
<!--                <diy-front-warehouse-order-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FrontWarehouseOrderInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oGoodsList'">-->
<!--                <diy-o2o-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oGoodsList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oGoodsRecommend'">-->
<!--                <diy-o2o-goods-recommend :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oGoodsRecommend" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oMemberInfo'">-->
<!--                <diy-o2o-member-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oMemberInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oNotice'">-->
<!--                <diy-o2o-notice :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oNotice" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oOrderInfo'">-->
<!--                <diy-o2o-order-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oOrderInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oSearch'">-->
<!--                <diy-o2o-search :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oSearch" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oStoreBottom'">-->
<!--                <diy-o2o-store-bottom :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oStoreBottom" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oStoreEvaluate'">-->
<!--                <diy-o2o-store-evaluate :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oStoreEvaluate" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oStoreGroup'">-->
<!--                <diy-o2o-store-group :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oStoreGroup" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oStoreInfo'">-->
<!--                <diy-o2o-store-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oStoreInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oStoreTab'">-->
<!--                <diy-o2o-store-tab :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oStoreTab" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'O2oVicinityLowPrice'">-->
<!--                <diy-o2o-vicinity-low-price :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.O2oVicinityLowPrice" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'WaimaiMemberAsset'">-->
<!--                <diy-waimai-member-asset :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.WaimaiMemberAsset" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'WaimaiMemberBanner'">-->
<!--                <diy-waimai-member-banner :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.WaimaiMemberBanner" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'WaimaiMemberCollect'">-->
<!--                <diy-waimai-member-collect :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.WaimaiMemberCollect" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'WaimaiMemberInfo'">-->
<!--                <diy-waimai-member-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.WaimaiMemberInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'WaimaiOrderInfo'">-->
<!--                <diy-waimai-order-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.WaimaiOrderInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'WaimaiShopList'">-->
<!--                <diy-waimai-shop-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.WaimaiShopList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'MallFenxiaoGoodsList'">-->
<!--                <diy-mall-fenxiao-goods-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.MallFenxiaoGoodsList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpCarousel'">-->
<!--                <diy-friend-help-carousel :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpCarousel" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpGoodsDetailBasicInfo'">-->
<!--                <diy-friend-help-goods-detail-basic-info :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpGoodsDetailBasicInfo" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpGoodsDetailBottom'">-->
<!--                <diy-friend-help-goods-detail-bottom :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpGoodsDetailBottom" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpGoodsDetailDesc'">-->
<!--                <diy-friend-help-goods-detail-desc :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpGoodsDetailDesc" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpGoodsDetailGameplay'">-->
<!--                <diy-friend-help-goods-detail-gameplay :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpGoodsDetailGameplay" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpGoodsDetailProgress'">-->
<!--                <diy-friend-help-goods-detail-progress :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpGoodsDetailProgress" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpGoodsDetailPurchaseService'">-->
<!--                <diy-friend-help-goods-detail-purchase-service :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpGoodsDetailPurchaseService" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpHot'">-->
<!--                <diy-friend-help-hot :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpHot" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
<!--            <template v-if="component.componentName == 'FriendHelpList'">-->
<!--                <diy-friend-help-list :component="component" :global="data.global" :index="index" :scrollBool="diyGroup.componentsScrollBool.FriendHelpList" @update:componentIsShow="component.componentIsShow = $event" />-->
<!--            </template>-->
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
   import diySingleRecommend from '@/addon/mall/components/diy/single-recommend/index.vue';
   // import diySeckillGoods from '@/addon/seckill/components/diy/seckill-goods/index.vue';
   // import diySeckillGoodsDetailBasicInfo from '@/addon/seckill/components/diy/seckill-goods-detail-basic-info/index.vue';
   // import diySeckillGoodsDetailBottom from '@/addon/seckill/components/diy/seckill-goods-detail-bottom/index.vue';
   // import diySeckillGoodsDetailDesc from '@/addon/seckill/components/diy/seckill-goods-detail-desc/index.vue';
   // import diySeckillGoodsDetailPurchaseService from '@/addon/seckill/components/diy/seckill-goods-detail-purchase-service/index.vue';
   // import diySeckillHot from '@/addon/seckill/components/diy/seckill-hot/index.vue';
   // import diySeckillRubikCube from '@/addon/seckill/components/diy/seckill-rubik-cube/index.vue';
   // import diySeckillSpecialOffer from '@/addon/seckill/components/diy/seckill-special-offer/index.vue';
   // import diyPointsMallGoodsList from '@/addon/points_mall/components/diy/points-mall-goods-list/index.vue';
   // import diyPointsMallInfo from '@/addon/points_mall/components/diy/points-mall-info/index.vue';
   // import diyPointsMallManyGoodsList from '@/addon/points_mall/components/diy/points-mall-many-goods-list/index.vue';
   // import diyPintuanActivityGoodsDetailBasicInfo from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-basic-info/index.vue';
   // import diyPintuanActivityGoodsDetailBottom from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-bottom/index.vue';
   // import diyPintuanActivityGoodsDetailDesc from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-desc/index.vue';
   // import diyPintuanActivityGoodsDetailGameplay from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-gameplay/index.vue';
   // import diyPintuanActivityGoodsDetailGoodsInfo from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-goods-info/index.vue';
   // import diyPintuanActivityGoodsDetailPintuanInfo from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-pintuan-info/index.vue';
   // import diyPintuanActivityGoodsDetailProgress from '@/addon/pintuan/components/diy/pintuan-activity-goods-detail-progress/index.vue';
   // import diyPintuanGoodsDetailBasicInfo from '@/addon/pintuan/components/diy/pintuan-goods-detail-basic-info/index.vue';
   // import diyPintuanGoodsDetailBottom from '@/addon/pintuan/components/diy/pintuan-goods-detail-bottom/index.vue';
   // import diyPintuanGoodsDetailDesc from '@/addon/pintuan/components/diy/pintuan-goods-detail-desc/index.vue';
   // import diyPintuanGoodsDetailGameplay from '@/addon/pintuan/components/diy/pintuan-goods-detail-gameplay/index.vue';
   // import diyPintuanGoodsDetailProgress from '@/addon/pintuan/components/diy/pintuan-goods-detail-progress/index.vue';
   // import diyPintuanGoodsDetailPurchaseService from '@/addon/pintuan/components/diy/pintuan-goods-detail-purchase-service/index.vue';
   // import diyPintuanHot from '@/addon/pintuan/components/diy/pintuan-hot/index.vue';
   // import diyPintuanImmediately from '@/addon/pintuan/components/diy/pintuan-immediately/index.vue';
   // import diyPintuanLimit from '@/addon/pintuan/components/diy/pintuan-limit/index.vue';
   // import diyPintuanList from '@/addon/pintuan/components/diy/pintuan-list/index.vue';
   // import diyPintuanMemberInfo from '@/addon/pintuan/components/diy/pintuan-member-info/index.vue';
   // import diyPintuanOrderInfo from '@/addon/pintuan/components/diy/pintuan-order-info/index.vue';
   // import diyFrontWarehouseCategoryGoodsList from '@/addon/front_warehouse/components/diy/front-warehouse-category-goods-list/index.vue';
   // import diyFrontWarehouseManyGoodsList from '@/addon/front_warehouse/components/diy/front-warehouse-many-goods-list/index.vue';
   // import diyFrontWarehouseMemberInfo from '@/addon/front_warehouse/components/diy/front-warehouse-member-info/index.vue';
   // import diyFrontWarehouseOrderInfo from '@/addon/front_warehouse/components/diy/front-warehouse-order-info/index.vue';
   // import diyO2oGoodsList from '@/addon/o2o/components/diy/o2o-goods-list/index.vue';
   // import diyO2oGoodsRecommend from '@/addon/o2o/components/diy/o2o-goods-recommend/index.vue';
   // import diyO2oMemberInfo from '@/addon/o2o/components/diy/o2o-member-info/index.vue';
   // import diyO2oNotice from '@/addon/o2o/components/diy/o2o-notice/index.vue';
   // import diyO2oOrderInfo from '@/addon/o2o/components/diy/o2o-order-info/index.vue';
   // import diyO2oSearch from '@/addon/o2o/components/diy/o2o-search/index.vue';
   // import diyO2oStoreBottom from '@/addon/o2o/components/diy/o2o-store-bottom/index.vue';
   // import diyO2oStoreEvaluate from '@/addon/o2o/components/diy/o2o-store-evaluate/index.vue';
   // import diyO2oStoreGroup from '@/addon/o2o/components/diy/o2o-store-group/index.vue';
   // import diyO2oStoreInfo from '@/addon/o2o/components/diy/o2o-store-info/index.vue';
   // import diyO2oStoreTab from '@/addon/o2o/components/diy/o2o-store-tab/index.vue';
   // import diyO2oVicinityLowPrice from '@/addon/o2o/components/diy/o2o-vicinity-low-price/index.vue';
   // import diyWaimaiMemberAsset from '@/addon/waimai/components/diy/waimai-member-asset/index.vue';
   // import diyWaimaiMemberBanner from '@/addon/waimai/components/diy/waimai-member-banner/index.vue';
   // import diyWaimaiMemberCollect from '@/addon/waimai/components/diy/waimai-member-collect/index.vue';
   // import diyWaimaiMemberInfo from '@/addon/waimai/components/diy/waimai-member-info/index.vue';
   // import diyWaimaiOrderInfo from '@/addon/waimai/components/diy/waimai-order-info/index.vue';
   // import diyWaimaiShopList from '@/addon/waimai/components/diy/waimai-shop-list/index.vue';
   // import diyMallFenxiaoGoodsList from '@/addon/mall_fenxiao/components/diy/mall-fenxiao-goods-list/index.vue';
   // import diyFriendHelpCarousel from '@/addon/friend_help/components/diy/friend-help-carousel/index.vue';
   // import diyFriendHelpGoodsDetailBasicInfo from '@/addon/friend_help/components/diy/friend-help-goods-detail-basic-info/index.vue';
   // import diyFriendHelpGoodsDetailBottom from '@/addon/friend_help/components/diy/friend-help-goods-detail-bottom/index.vue';
   // import diyFriendHelpGoodsDetailDesc from '@/addon/friend_help/components/diy/friend-help-goods-detail-desc/index.vue';
   // import diyFriendHelpGoodsDetailGameplay from '@/addon/friend_help/components/diy/friend-help-goods-detail-gameplay/index.vue';
   // import diyFriendHelpGoodsDetailProgress from '@/addon/friend_help/components/diy/friend-help-goods-detail-progress/index.vue';
   // import diyFriendHelpGoodsDetailPurchaseService from '@/addon/friend_help/components/diy/friend-help-goods-detail-purchase-service/index.vue';
   // import diyFriendHelpHot from '@/addon/friend_help/components/diy/friend-help-hot/index.vue';
   // import diyFriendHelpList from '@/addon/friend_help/components/diy/friend-help-list/index.vue';
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