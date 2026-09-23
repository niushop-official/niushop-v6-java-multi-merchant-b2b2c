<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <!-- 选择数据源 -->
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t("selectSource") }}</h3>
			<el-form label-width="90px" class="px-[10px]">
				<el-form-item label="店铺选择">
                    <el-select v-model="diyStore.editComponent.source" title="店铺选择" @change="handleChange" clearable>
                        <el-option label="全部店铺" value="all" />
                        <el-option label="店铺分类" value="category_id" />
                        <el-option label="是否直营" value="is_self" />
                    </el-select>
				</el-form-item>
                <el-form-item label="所属行业" v-if="diyStore.editComponent.source == 'category_id'">
					<el-select v-model="diyStore.editComponent.category_id" title="所属行业" clearable>
                        <el-option :label="item.name" :value="item.category_id" v-for="(item,index) in categoryList" :key="index" />
                    </el-select>
				</el-form-item>
                <el-form-item label="是否直营" v-if="diyStore.editComponent.source == 'is_self'">
					<el-select v-model="diyStore.editComponent.is_self" title="是否直营" clearable>
                        <el-option :label="t('自营')" value="1" />
                        <el-option :label="t('直营')" value="0" />
                    </el-select>
				</el-form-item>
                <el-form-item label="附近店铺">
                    <div>
                        <el-switch v-model="diyStore.editComponent.position.control" />
                        <div class="text-[12px] text-[#999] leading-[20px]">开启后将仅展示用户当前定位所在城市的商铺</div>
                    </div>
                </el-form-item>
                <el-form-item label="同城配送">
                    <div>
                        <el-switch v-model="diyStore.editComponent.localDelivery.control" />
                        <div class="text-[12px] text-[#999] leading-[20px]">开启后将仅展示启用同城配送的商铺</div>
                    </div>
                </el-form-item>
                <el-form-item :label="t('展示距离')">
                    <el-switch v-model="diyStore.editComponent.distance.isShow" />
                </el-form-item>
                <el-form-item :label="t('排序方式')">
                    <el-radio-group v-model="diyStore.editComponent.distance.sort" title="排序方式">
						<el-radio label="mall_sale_num">销量</el-radio>
						<el-radio label="create_time">开店时间</el-radio>
                        <el-radio label="distance" v-if="diyStore.editComponent.distance.isShow">距离</el-radio>
					</el-radio-group>
                </el-form-item>
			</el-form>
		</div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">展示样式</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item label="样式选择">
					<el-radio-group v-model="diyStore.editComponent.type" title="样式选择" @change="initTypeFn">
						<el-radio :label="1">样式1</el-radio>
						<el-radio :label="2">样式2</el-radio>
                        <el-radio :label="3">样式3</el-radio>
					</el-radio-group>
				</el-form-item>
            </el-form>
        </div>
    </div>
    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">店铺样式</h3>
			<el-form label-width="90px" class="px-[10px]">
                <template v-if="diyStore.editComponent.type != 3">
                    <el-form-item label="店铺背景">
                        <el-color-picker v-model="diyStore.editComponent.siteStyle.startBgColor" show-alpha :predefine="diyStore.predefineColors" />
                        <icon name="iconfont iconmap-connect" size="20px" class="block !text-gray-400 mx-[5px]"/>
                        <el-color-picker v-model="diyStore.editComponent.siteStyle.endBgColor" show-alpha :predefine="diyStore.predefineColors"/>
                    </el-form-item>
                    <el-form-item label="渐变角度">
                        <el-radio-group v-model="diyStore.editComponent.siteStyle.gradientAngle">
                            <el-radio label="to bottom">{{ t('topToBottom') }}</el-radio>
                            <el-radio label="to right">{{ t('leftToRight') }}</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="店铺背景图">
                        <upload-image v-model="diyStore.editComponent.siteStyle.bgUrl" :limit="1"/>
                    </el-form-item>
                </template>
				<el-form-item label="店铺名称">
					<el-color-picker v-model="diyStore.editComponent.siteNameStyle.color" show-alpha :predefine="diyStore.predefineColors" />
					<div class="mr-[20px]"></div>
					<el-radio-group v-model="diyStore.editComponent.siteNameStyle.fontWeight">
						<el-radio :label="'normal'">{{ t('fontWeightNormal') }}</el-radio>
						<el-radio :label="'bold'">{{ t('fontWeightBold') }}</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item :label="t('topRounded')">
					<el-slider v-model="diyStore.editComponent.topElementRounded" show-input size="small" class="ml-[10px] graphic-nav-slider" :max="50" />
				</el-form-item>
				<el-form-item :label="t('bottomRounded')">
					<el-slider v-model="diyStore.editComponent.bottomElementRounded" show-input size="small" class="ml-[10px] graphic-nav-slider" :max="50" />
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
import { ref } from 'vue'
import { getSiteCategoryAll } from '@/app/api/site_category'

const diyStore:any = useDiyStore()
diyStore.editComponent.ignore = ['componentBgUrl'] // 忽略公共属性

const categoryList = ref<any>([])

const getShopCategoryAllFn = () => {
    getSiteCategoryAll({addon: 'mall'}).then(res => {
        categoryList.value = res.data
    })
}
getShopCategoryAllFn()

const handleChange = (val: any) => {
    diyStore.editComponent.category_id = ''
    diyStore.editComponent.is_self = ''
}

const initTypeFn = () => {
    if (diyStore.editComponent.type == 3) {
        diyStore.editComponent.siteNameStyle.color = '#ffffff'
        diyStore.editComponent.siteStyle.startBgColor = ''
        diyStore.editComponent.siteStyle.endBgColor = ''
    } else {
        diyStore.editComponent.siteNameStyle.color = '#333333'
        diyStore.editComponent.siteStyle.startBgColor = '#FFC3C4'
        diyStore.editComponent.siteStyle.endBgColor = '#FFDABA'
    }
}

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    if (diyStore.value[index].source == 'category_id') {
        if (diyStore.value[index].category_id == '') {
            res.code = false
            res.message = t('请选择店铺分类')
        }
    }
    if (diyStore.value[index].source == 'is_self') {
        if (diyStore.value[index].is_self == '') {
            res.code = false
            res.message = t('请选择是否直营')
        }
    }

    return res
}
</script>

<style lang="scss">
.shop-list-slider {
    .el-slider__input {
        width: 100px;
    }
}
</style>
