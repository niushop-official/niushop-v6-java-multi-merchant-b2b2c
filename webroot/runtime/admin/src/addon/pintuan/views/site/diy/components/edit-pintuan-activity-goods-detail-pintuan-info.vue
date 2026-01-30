<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('标题')">
                    <el-input v-model.trim="diyStore.editComponent.pintuanTitle.text" :placeholder="t('titlePlaceholder')" clearable maxlength="12" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('拼团信息')">
                    <el-checkbox-group v-model="diyStore.editComponent.pintuanInfo.content" :min="2">
                        <el-checkbox label="已浏览" value="views" />
                        <el-checkbox label="已参与" value="memberNum" />
                        <el-checkbox label="成交量" value="orderNum"/>
                    </el-checkbox-group>
                    <div class="text-sm text-gray-400">{{ t('至少选择2个') }}</div>
                </el-form-item>
            </el-form>
        </div>
    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <!-- 标题样式 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('标题样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.pintuanTitle.textColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.pintuanTitle.bgColor" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.pintuanTitle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
                </el-form-item>
            </el-form>
        </div>

        <!-- 倒计时样式 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('倒计时样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('标题颜色')">
                    <el-color-picker v-model="diyStore.editComponent.countdown.titleColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.countdown.bgColor" />
                </el-form-item>
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.countdown.textColor" />
                </el-form-item>
                <el-form-item :label="t('分隔符颜色')">
                    <el-color-picker v-model="diyStore.editComponent.countdown.dropColor" />
                </el-form-item>
            </el-form>
        </div>

        <!-- 信息样式 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('拼团信息样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.pintuanInfo.bgColor" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.pintuanInfo.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
                </el-form-item>
                <el-form-item :label="t('数字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.pintuanInfo.dataNumColor" />
                </el-form-item>
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.pintuanInfo.dataTextColor" />
                </el-form-item>
                <!-- 分割线样式 -->
                <el-form-item :label="t('分割线颜色')">
                    <el-color-picker v-model="diyStore.editComponent.dividerLine.color" />
                </el-form-item>
                <el-form-item :label="t('头像圆角')">
                    <el-slider v-model="diyStore.editComponent.pintuanInfo.imgRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
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
import { ref, reactive } from 'vue'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = ['componentBgColor', 'componentBgUrl', 'topRounded', 'bottomRounded'] // 忽略公共属性

const serviceConfig = ref([])
if (diyStore.editComponent.serviceConfig) {
    serviceConfig.value = (typeof diyStore.editComponent.serviceConfig == 'object') ? diyStore.editComponent.serviceConfig : diyStore.editComponent.serviceConfig.split(',')
}

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    return res
}

defineExpose({})

</script>

<style lang="scss" scoped></style>
