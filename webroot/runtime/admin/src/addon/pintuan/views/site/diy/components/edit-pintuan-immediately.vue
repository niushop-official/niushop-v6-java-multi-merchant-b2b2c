<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('标题内容') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('标题')">
                    <el-input v-model.trim="diyStore.editComponent.titleStyle.text" :placeholder="t('请输入标题')" clearable maxlength="10" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('标题图片')">
                    <upload-image v-model="diyStore.editComponent.titleImgUrl" :limit="1" />
                </el-form-item>
                <el-form-item :label="t('link')">
                    <diy-link v-model="diyStore.editComponent.titleStyle.link" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('“更多”内容') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('名称')">
                    <el-input v-model.trim="diyStore.editComponent.moreStyle.text" :placeholder="t('请输入名称')" clearable maxlength="4" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('link')">
                    <diy-link v-model="diyStore.editComponent.moreStyle.link" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t("selectSource") }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('选择商品')">
                    <el-radio-group v-model="diyStore.editComponent.goodsStyle.source" :title="t('选择商品')">
                        <el-radio label="all">{{ t('全部') }}</el-radio>
                        <el-radio label="custom">{{ t('手动选择') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('goodsNum')" v-if="diyStore.editComponent.goodsStyle.source == 'all'">
                    <el-slider class="goods-list-slider" show-input v-model="diyStore.editComponent.goodsStyle.num" :min="1" max="20" size="small" />
                </el-form-item>
                <el-form-item :label="t('customGoods')" v-if="diyStore.editComponent.goodsStyle.source == 'custom'">
                    <diy-goods-select-popup ref="goodsSelectPopupRef" v-model="diyStore.editComponent.goodsStyle.pintuanIds" :min="1" :max="99" />
                </el-form-item>
                <el-form-item :label="t('按钮名称')">
                    <el-input v-model.trim="diyStore.editComponent.btnStyle.text" :placeholder="t('请输入名称')" clearable maxlength="4" show-word-limit />
                </el-form-item>
            </el-form>

        </div>
    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('模块样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.moduleStyle.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.moduleStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('标题样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('textFontSize')">
                    <el-slider v-model="diyStore.editComponent.titleStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="12" :max="30" />
                </el-form-item>
                <el-form-item :label="t('textFontWeight')">
                    <el-radio-group v-model="diyStore.editComponent.titleStyle.fontWeight">
                        <el-radio :value="'normal'">{{ t('fontWeightNormal') }}</el-radio>
                        <el-radio :value="'bold'">{{ t('fontWeightBold') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('textColor')">
                    <el-color-picker v-model="diyStore.editComponent.titleStyle.textColor" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('“更多”样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('textFontSize')">
                    <el-slider v-model="diyStore.editComponent.moreStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="12" :max="30" />
                </el-form-item>
                <el-form-item :label="t('textColor')">
                    <el-color-picker v-model="diyStore.editComponent.moreStyle.textColor" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('商品样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.goodsStyle.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('商品圆角')">
                    <el-slider v-model="diyStore.editComponent.goodsStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
                <el-form-item :label="t('图片圆角')">
                    <el-slider v-model="diyStore.editComponent.goodsStyle.imgRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('按钮样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.textColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.btnStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="30" />
                </el-form-item>
            </el-form>
        </div>

        <!-- 组件样式 -->
        <slot name="style"></slot>
    </div>

</template>

<script lang="ts" setup>
import { t } from '@/lang'
import useDiyStore from '@/stores/modules/diy'
import diyGoodsSelectPopup from '@/addon/pintuan/views/site/active/components/diy-goods-select-popup.vue'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = [] // 忽略公共属性

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    if (diyStore.value[index].goodsStyle.source == 'custom' && !diyStore.value[index].goodsStyle.pintuanIds.length) {
        res.code = false
        res.message = t('请选择拼团商品')
    }
    return res
}
defineExpose({})

</script>

<style lang="scss">
.goods-list-slider {
    .el-slider__input {
        width: 100px;
    }
}
</style>
