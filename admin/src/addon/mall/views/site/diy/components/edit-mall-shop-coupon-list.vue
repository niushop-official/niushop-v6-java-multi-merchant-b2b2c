<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('couponData') }}</h3>
            <el-form label-width="90px" class="px-[10px]">
                <el-form-item :label="t('couponNum')" v-if="diyStore.editComponent.source == 'all'">
					<el-slider show-input v-model="diyStore.editComponent.num" :min="1" max="20" size="small" class="goods-coupon-slider" />
				</el-form-item>
            </el-form>
        </div>
    </div>
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <!-- 组件样式 -->
       <slot name="style"></slot>
    </div>

</template>
<script lang="ts" setup>
// 店铺优惠劵列表
import { t } from '@/lang'
import useDiyStore from '@/stores/modules/diy'
const diyStore:any = useDiyStore()
diyStore.editComponent.ignore = ['componentBgColor', 'componentBgUrl'] // 忽略公共属性
</script>
<style lang="scss">
.goods-coupon-slider {
    .el-slider__input {
        width: 100px;
    }
}
</style>