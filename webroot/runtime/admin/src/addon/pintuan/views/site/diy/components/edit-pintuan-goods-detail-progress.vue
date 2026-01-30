<template>
    <!-- 内容 -->
    <div class="content-wrap goods-detail-basic-info" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('是否显示')">
                    <el-radio-group v-model="diyStore.editComponent.isShow">
                        <el-radio :label="true">{{ t('显示') }}</el-radio>
                        <el-radio :label="false">{{ t('隐藏') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </div>
    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('标题样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.titleStyle.textColor" />
                </el-form-item>
                <el-form-item :label="t('字体大小')">
                    <el-slider v-model="diyStore.editComponent.titleStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="12" :max="20" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('‘更多’样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.moreStyle.textColor" />
                </el-form-item>
                <el-form-item :label="t('拼团人数颜色')">
                    <el-color-picker v-model="diyStore.editComponent.moreStyle.numberColor" />
                </el-form-item>
                <el-form-item :label="t('字体大小')">
                    <el-slider v-model="diyStore.editComponent.moreStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="10" :max="15" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('按钮样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.textColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.bgColor" />
                </el-form-item>
                <el-form-item :label="t('字体大小')">
                    <el-slider v-model="diyStore.editComponent.btnStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="10" :max="15" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.btnStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
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

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = [] // 忽略公共属性

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }

    return res
}

defineExpose({})

</script>

<style lang="scss" scoped></style>
