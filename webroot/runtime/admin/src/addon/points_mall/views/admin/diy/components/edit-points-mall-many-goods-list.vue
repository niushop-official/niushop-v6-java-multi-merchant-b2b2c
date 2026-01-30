<template>
    <div>
        <!-- 内容 -->
        <div class="content-wrap" v-show="diyStore.editTab == 'content'">
            <div class="edit-attr-item-wrap">
                <h3 class="mb-[10px]">{{ t('goodsSet') }}</h3>
                <el-form label-width="80px" class="px-[10px]">
                    <el-form-item :label="t('sortWay')">
                        <el-radio-group v-model="diyStore.editComponent.sortWay">
                            <el-radio label="default">{{ t('default') }}</el-radio>
                            <el-radio label="sale_num">{{ t('sales') }}</el-radio>
                            <el-radio label="point">{{ t('积分') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="t('goodsNum')">
                        <el-slider show-input class="diy-nav-slider" v-model="diyStore.editComponent.num" :min="1" max="20" size="small" />
                    </el-form-item>
                </el-form>
            </div>
            <div class="edit-attr-item-wrap">
                <h3 class="mb-[10px]">{{ t("manyGoodsListCategorySet") }}</h3>
                <el-form label-width="80px" class="px-[10px]">
                    <el-form-item :label="t('dataSources')">
                        <el-radio-group v-model="diyStore.editComponent.source">
                            <el-radio label="goods_category">{{ t('商品分组') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>

            </div>
        </div>
        <!-- 样式 -->
        <div class="style-wrap" v-if="diyStore.editTab == 'style'">
            <div class="edit-attr-item-wrap">
                <h3 class="mb-[10px]">{{ t('goodsStyle') }}</h3>
                <el-form label-width="80px" class="px-[10px]">
                    <el-form-item :label="t('goodsNameColor')">
                        <el-color-picker v-model="diyStore.editComponent.goodsNameStyle.color" show-alpha :predefine="diyStore.predefineColors" />
                        <div class="mr-[20px]"></div>
                        <el-radio-group v-model="diyStore.editComponent.goodsNameStyle.fontWeight">
                            <el-radio :label="'normal'">{{ t('fontWeightNormal') }}</el-radio>
                            <el-radio :label="'bold'">{{ t('fontWeightBold') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item :label="t('imageRounded')">
                        <el-slider v-model="diyStore.editComponent.imgElementRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                    </el-form-item>
                    <el-form-item :label="t('goodsNumColor')">
                        <el-color-picker v-model="diyStore.editComponent.saleStyle.color" show-alpha :predefine="diyStore.predefineColors" />
                    </el-form-item>
                    <el-form-item :label="t('goodsPriceColor')">
                        <el-color-picker v-model="diyStore.editComponent.priceStyle.mainColor" show-alpha :predefine="diyStore.predefineColors" />
                    </el-form-item>
                    <el-form-item :label="t('topRounded')">
                        <el-slider v-model="diyStore.editComponent.topElementRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                    </el-form-item>
                    <el-form-item :label="t('bottomRounded')">
                        <el-slider v-model="diyStore.editComponent.bottomElementRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                    </el-form-item>
                </el-form>
            </div>

            <!-- 组件样式 -->
            <slot name="style"></slot>
        </div>
    </div>

</template>

<script setup lang="ts">
import { t } from '@/lang'
import useDiyStore from '@/stores/modules/diy'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = [] // 忽略公共属性

diyStore.editComponent.ignore = ['componentBgUrl'] // 忽略公共属性

defineExpose({})
</script>

<style scoped>

</style>