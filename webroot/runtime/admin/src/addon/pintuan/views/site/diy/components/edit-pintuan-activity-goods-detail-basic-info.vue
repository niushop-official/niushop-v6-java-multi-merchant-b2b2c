<template>
    <!-- 内容 -->
    <div class="content-wrap goods-detail-basic-info" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('顶部导航') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('菜单内容')">
                    <el-checkbox-group v-model="diyStore.editComponent.menu.content">
                        <el-checkbox label="浏览模块" value="visitorsModule" />
                        <el-checkbox label="拼团模块" value="pintuanModule"/>
                    </el-checkbox-group>
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('侧边栏导航') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('菜单内容')">
                    <el-checkbox-group v-model="diyStore.editComponent.sideMenu.content" :min="2">
                        <el-checkbox label="首页" value="index" />
                        <!-- <el-checkbox label="我的" value="member"/> -->
                        <el-checkbox label="拼单" value="mySpell"/>
                        <el-checkbox label="海报" value="poster"/>
                        <el-checkbox label="客服" value="service"/>
                        <!-- <el-checkbox label="订单" value="order"/> -->
                    </el-checkbox-group>
                    <div class="text-sm text-gray-400">{{ t('菜单内容最少选择2个') }}</div>
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('商品媒体') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('弹幕')">
                    <el-switch v-model="diyStore.editComponent.barrage.isShow" :active-value="true" :inactive-value="false" />
                </el-form-item>
                <el-form-item :label="t('弹幕数量')">
                    <el-slider class="goods-list-slider" show-input v-model="diyStore.editComponent.barrage.num" :min="1" max="30" size="small" />
                </el-form-item>
                <el-form-item :label="t('背景音乐')">
                    <el-switch v-model="diyStore.editComponent.music.isShow" :active-value="true" :inactive-value="false" />
                </el-form-item>
                <el-form-item :label="t('背景特效')">
                    <el-switch v-model="diyStore.editComponent.specialEffects.isShow" :active-value="true" :inactive-value="false" />
                </el-form-item>
                <el-form-item :label="t('特效播放次数')" v-if="diyStore.editComponent.specialEffects.isShow">
                    <el-slider class="goods-list-slider" show-input v-model="diyStore.editComponent.specialEffects.num" :min="1" max="20" size="small" />
                </el-form-item>

            </el-form>
        </div>
    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <!-- 顶部导航 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('顶部导航') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.menu.textColor" />
                </el-form-item>
                <el-form-item :label="t('数字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.menu.numTextColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.menu.bgColor" />
                </el-form-item>
                <el-form-item :label="t('背景透明度')">
                    <el-slider v-model="diyStore.editComponent.menu.opacity" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="10" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.menu.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
                </el-form-item>
            </el-form>
        </div>
        <!-- 侧边栏导航 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('侧边栏导航') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.sideMenu.textColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.sideMenu.bgColor" />
                </el-form-item>
                <el-form-item :label="t('背景透明度')">
                    <el-slider v-model="diyStore.editComponent.sideMenu.opacity" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="10" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.sideMenu.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
                    <div class="text-sm text-gray-400">{{ t('控制左侧圆角') }}</div>
                </el-form-item>
            </el-form>
        </div>

        <!-- 弹幕样式 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('弹幕样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.barrage.textColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.barrage.bgColor" />
                </el-form-item>
                <el-form-item :label="t('背景透明度')">
                    <el-slider v-model="diyStore.editComponent.barrage.opacity" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="10" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.barrage.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
                </el-form-item>
            </el-form>
        </div>
        <!-- 背景音乐 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('背景音乐样式') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.music.textColor" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.music.bgColor" />
                </el-form-item>
                <el-form-item :label="t('背景透明度')">
                    <el-slider v-model="diyStore.editComponent.music.opacity" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="10" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.music.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :min="0" :max="50" />
                    <div class="text-sm text-gray-400">{{ t('控制左侧圆角') }}</div>
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
import { ref, reactive, onMounted } from 'vue'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = ['pageBgColor', 'componentBgUrl', 'marginTop', 'marginBottom', 'marginBoth', 'topRounded', 'bottomRounded'] // 忽略公共属性

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    return res
}

onMounted(() => {

})

defineExpose({})

</script>

<style lang="scss" scoped></style>
