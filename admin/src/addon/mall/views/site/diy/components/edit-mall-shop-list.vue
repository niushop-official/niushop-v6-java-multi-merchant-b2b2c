<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <!-- 标题内容 -->
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('titleContent') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('selectStyle')" class="flex">
					<span class="text-primary flex-1 cursor-pointer" @click="showTitleStyle">{{ diyStore.editComponent.titleStyle.title }}</span>
					<el-icon>
						<ArrowRight />
					</el-icon>
				</el-form-item>
                <el-form-item :label="t('image')">
                    <upload-image v-model="diyStore.editComponent.textImg" :limit="1"/>
                </el-form-item>
                <el-form-item :label="t('subTitle')">
                    <el-input v-model.trim="diyStore.editComponent.subTitle.text" :placeholder="t('subTitlePlaceholder')" clearable maxlength="8" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('link')">
                    <diy-link v-model="diyStore.editComponent.subTitle.link"/>
                </el-form-item>
            </el-form>

            <el-dialog v-model="showTitleDialog" :title="t('selectStyle')" width="460px">

                <div class="flex flex-wrap">
                    <template v-for="(item,index) in titleStyleList" :key="index">
                        <div :class="{ 'border-primary': selectTitleStyle.value == item.value }" @click="changeTitleStyle(item)" class="flex items-center justify-center overflow-hidden w-[200px] h-[100px] mr-[12px] mb-[12px] cursor-pointer border bg-[#eee]">
                            <img :src="img(item.url)" />
                        </div>
                    </template>
                </div>

                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="showTitleDialog = false">{{ t('cancel') }}</el-button>
                        <el-button type="primary" @click="confirmTitleStyle">{{ t('confirm') }}</el-button>
                    </span>
                </template>

            </el-dialog>
        </div>
        <!-- 店铺风格选择 -->
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">展示样式</h3>
			<div class="flex items-center mb-[18px] rounded overflow-hidden">
				<span
					class="iconfont iconzuoyoutuwenpc border-[1px] border-solid border-[#eee] cursor-pointer flex-1 flex items-center justify-center py-[5px]"
					:class="{ '!border-[var(--el-color-primary)] text-[var(--el-color-primary)]': diyStore.editComponent.style == 'style-1' }"
					@click="styleChangeFn('style-1')"></span>
				<span
					class="iconfont iconshangxiatuwenpc border-[1px] border-solid border-[#eee] cursor-pointer flex-1 flex items-center justify-center py-[5px]"
					:class="{ '!border-[var(--el-color-primary)] text-[var(--el-color-primary)]': diyStore.editComponent.style == 'style-2' }"
					@click="styleChangeFn('style-2')"></span>
			</div>
		</div>
        <!-- 选择数据源 -->
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t("selectSource") }}</h3>
			<el-form label-width="90px" class="px-[10px]">
				<el-form-item label="店铺选择">
					<el-radio-group v-model="diyStore.editComponent.source" title="店铺选择">
						<el-radio label="all">全部店铺</el-radio>
						<el-radio label="custom">手动选择</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item :label="t('customGoods')" v-if="diyStore.editComponent.source == 'custom'">
                    <shop-select-popup v-model="diyStore.editComponent.site_ids" :min="1" :max="99" />
				</el-form-item>
                <el-form-item label="店铺数量" v-if="diyStore.editComponent.source == 'all'">
					<div class="flex items-center w-full ml-[5px]">
                        <el-slider class="shop-list-slider" show-input v-model="diyStore.editComponent.num" :min="1" max="20" size="small" />
					</div>
				</el-form-item>
                <el-form-item label="附近店铺">
                    <div>
                        <el-switch v-model="diyStore.editComponent.position.control" />
                        <div class="text-[12px] text-[#999] leading-[20px]">开启后将仅展示用户当前定位所在城市的商铺</div>
                    </div>
                </el-form-item>
                <el-form-item label="同城配送" v-if="diyStore.editComponent.position.control">
                    <div>
                        <el-switch v-model="diyStore.editComponent.localDelivery.control" />
                        <div class="text-[12px] text-[#999] leading-[20px]">开启后将仅展示定位处于商家配送范围之内的商铺</div>
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
    </div>
    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('titleStyle') }}</h3>
			<el-form label-width="120px" class="px-[10px]">
				<el-form-item :label="t('textColor')">
					<el-color-picker v-model="diyStore.editComponent.subTitle.textColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
			</el-form>
		</div>
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">店铺样式</h3>
			<el-form label-width="90px" class="px-[10px]">
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
import { img } from '@/utils/common'
import useDiyStore from '@/stores/modules/diy'
import { ref, reactive } from 'vue'
import shopSelectPopup from '@/app/views/web/components/shop-select-popup.vue'

const diyStore:any = useDiyStore()
diyStore.editComponent.ignore = ['componentBgUrl'] // 忽略公共属性

// 标题风格样式
const selectTitleStyle = reactive({
    title: diyStore.editComponent.titleStyle.title,
    value: diyStore.editComponent.titleStyle.value
})

const showTitleDialog = ref(false)

const showTitleStyle = () => {
    selectTitleStyle.title = diyStore.editComponent.titleStyle.title
    selectTitleStyle.value = diyStore.editComponent.titleStyle.value
    showTitleDialog.value = true
}

const changeTitleStyle = (item:any) => {
    selectTitleStyle.title = item.title
    selectTitleStyle.value = item.value
}

const confirmTitleStyle = () => {
    diyStore.editComponent.titleStyle.title = selectTitleStyle.title
    diyStore.editComponent.titleStyle.value = selectTitleStyle.value
    showTitleDialog.value = false
}

const titleStyleList = reactive([
    {
        url: 'addon/mall/diy/mall_shop_list/title_style_01.png',
        title: '风格1',
        value: 'style-1'
    }
])

// 店铺样式选择
const styleChangeFn = (style: any) => {
    diyStore.editComponent.style = style
}

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    if (diyStore.value[index].source == 'custom') {
        if (diyStore.value[index].site_ids.length == 0) {
            res.code = false
            res.message = t('请选择店铺')
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
