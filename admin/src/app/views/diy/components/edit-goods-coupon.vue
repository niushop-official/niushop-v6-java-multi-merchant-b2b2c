<template>
	<!-- 内容 -->
	<div class="content-wrap" v-show="diyStore.editTab == 'content'">

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('selectStyle') }}</h3>
			<el-form label-width="80px" class="px-[10px]">
				<el-form-item :label="t('selectStyle')" class="flex">
					<span class="text-primary flex-1 cursor-pointer" @click="showCouponStyle">{{ diyStore.editComponent.styleName }}</span>
					<el-icon>
						<ArrowRight />
					</el-icon>
				</el-form-item>
			</el-form>

			<el-dialog v-model="showCouponDialog" :title="t('selectStyle')" width="700px">

				<div class="flex flex-wrap">
					<template v-for="(item,index) in couponStyleList" :key="index">
						<div :class="{ 'border-primary': selectCouponStyle.value == item.value }" @click="changeCouponStyle(item)" class="flex items-center justify-center overflow-hidden w-[200px] h-[100px] m-[6px] cursor-pointer border bg-gray-50">
							<img :src="img(item.url)" />
						</div>
					</template>
				</div>

				<template #footer>
					<span class="dialog-footer">
						<el-button @click="showCouponDialog = false">{{ t('cancel') }}</el-button>
						<el-button type="primary" @click="confirmCouponStyle">{{ t('confirm') }}</el-button>
					</span>
				</template>

			</el-dialog>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('couponContent') }}</h3>
			<el-form label-width="90px" class="px-[10px]">

				<el-form-item :label="t('couponTitle')">
					<el-input v-model="diyStore.editComponent.couponTitle" clearable :maxlength="diyStore.editComponent.style == 'style-3' ? 4 : 8" show-word-limit/>
				</el-form-item>

				<el-form-item :label="t('couponSubTitle')" v-if="diyStore.editComponent.style != 'style-5' && diyStore.editComponent.style != 'style-6' && diyStore.editComponent.style != 'style-7' && diyStore.editComponent.style != 'style-8'">
					<el-input v-model="diyStore.editComponent.couponSubTitle" clearable :maxlength="diyStore.editComponent.style == 'style-3' ? 7 : 10" show-word-limit/>
				</el-form-item>

                <el-form-item label="背景图" v-if="diyStore.editComponent.style == 'style-3'">
                    <upload-image v-model="diyStore.editComponent.couponBgUrl" :limit="1"/>
                </el-form-item>
			</el-form>

		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('couponData') }}</h3>
			<el-form label-width="90px" class="px-[10px]">
                <el-form-item :label="t('couponAddon')">
					<el-radio-group v-model="diyStore.editComponent.addon">
						<el-radio v-for="item in couponTypeOptions" :key="item.value" :label="item.value">{{ item.label }}</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item :label="t('selectCoupon')">
					<el-radio-group v-model="diyStore.editComponent.source" :title="t('goodsSelectPopupSelectGoodsButton')">
						<el-radio label="all">{{ t('allSources') }}</el-radio>
						<el-radio label="custom">{{ t('manualSelectionSources') }}</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item :label="t('manualSelectionSources')" v-if="diyStore.editComponent.source == 'custom'">
					<shop-coupon-select-popup ref="couponSelectPopupRef" v-if="siteId > 0" v-model="diyStore.editComponent.couponIds" :min="1" :max="20" :addon="diyStore.editComponent.addon" :siteId="siteId"   />
					<admin-coupon-select-popup ref="couponSelectPopupRef" v-else v-model="diyStore.editComponent.couponIds" :min="1" :max="20" :addon="diyStore.editComponent.addon" />
				</el-form-item>
				<el-form-item :label="t('couponNum')" v-if="diyStore.editComponent.source == 'all'">
					<el-slider show-input v-model="diyStore.editComponent.num" :min="1" max="20" size="small"
                           class="goods-coupon-slider" />
				</el-form-item>
				<el-form-item :label="t('couponBtnText')" v-if="diyStore.editComponent.style != 'style-4' && diyStore.editComponent.style != 'style-6' && diyStore.editComponent.style != 'style-7'">
					<el-input v-model="diyStore.editComponent.btnText" clearable maxlength="5" show-word-limit/>
				</el-form-item>

			</el-form>

		</div>

	</div>

	<!-- 样式 -->
	<div class="style-wrap" v-show="diyStore.editTab == 'style'">
		<div class="edit-attr-item-wrap" v-if="['style-3','style-4','style-5','style-6','style-7','style-8'].indexOf(diyStore.editComponent.style) != -1">
            <h3 class="mb-[10px]">{{ t("couponTitleStyle") }}</h3>
            <el-form label-width="90px" class="px-[10px]">
                <el-form-item :label="t('couponTitleColor')">
                    <el-color-picker v-model="diyStore.editComponent.titleColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('couponSubTitleColor')" v-if="diyStore.editComponent.style != 'style-5' && diyStore.editComponent.style != 'style-6' && diyStore.editComponent.style != 'style-7' && diyStore.editComponent.style != 'style-8'">
                    <el-color-picker v-model="diyStore.editComponent.subTitleColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('对齐方式')" v-if="diyStore.editComponent.style == 'style-6' || diyStore.editComponent.style == 'style-7' || diyStore.editComponent.style == 'style-8'">
                    <el-radio-group v-model="diyStore.editComponent.titleAlign">
                        <el-radio label="left">{{ t('左对齐') }}</el-radio>
                        <el-radio label="center">{{ t('居中') }}</el-radio>
                        <el-radio label="right">{{ t('右对齐') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('按钮颜色')" v-if="diyStore.editComponent.style == 'style-3'">
                    <el-color-picker v-model="diyStore.editComponent.btnTextColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('按钮背景色')" v-if="diyStore.editComponent.style == 'style-3'">
                    <el-color-picker v-model="diyStore.editComponent.btnBgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap" v-if="diyStore.editComponent.style == 'style-3'">
            <h3 class="mb-[10px]">优惠券项样式</h3>
            <el-form label-width="90px" class="px-[10px]">
                <el-form-item :label="t('背景颜色')">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('textColor')" v-if="diyStore.editComponent.style  !== 'style-7'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.textColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('价格背景色')">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.moneyBgStartColor" show-alpha :predefine="diyStore.predefineColors" />
                    <icon name="iconfont iconmap-connect" size="20px" class="block !text-gray-400 mx-[5px]" />
                    <el-color-picker v-model="diyStore.editComponent.couponItem.moneyBgEndColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('价格颜色')">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.moneyColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('subTextColor')">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.subTextColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap" v-if="['style-4','style-5','style-6','style-7','style-8'].indexOf(diyStore.editComponent.style) != -1">
            <h3 class="mb-[10px]">{{ t("couponItemStyle") }}</h3>
            <el-form label-width="90px" class="px-[10px]">
                <el-form-item :label="t('couponMoney')" v-if="diyStore.editComponent.style  !== 'style-7'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.moneyColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('couponMoney')" v-if="diyStore.editComponent.style  == 'style-7'">
                    <el-color-picker v-for="(item,index) in diyStore.editComponent.couponItem.itemMoneyColor" :key="index" v-model="diyStore.editComponent.couponItem.itemMoneyColor[index]" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('textColor')" v-if="diyStore.editComponent.style  !== 'style-7'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.textColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('textColor')" v-if="diyStore.editComponent.style  == 'style-7'">
                    <el-color-picker v-for="(item,index) in diyStore.editComponent.couponItem.itemTextColor" :key="index" v-model="diyStore.editComponent.couponItem.itemTextColor[index]" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('subTextColor')" v-if="diyStore.editComponent.style  !== 'style-5' && diyStore.editComponent.style  !== 'style-6' && diyStore.editComponent.style  !== 'style-7' && diyStore.editComponent.style  !== 'style-8'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.subTextColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('背景图')" v-if="diyStore.editComponent.style  == 'style-5'">
                    <upload-image v-model="diyStore.editComponent.couponItem.bgUrl" :limit="1"/>
                </el-form-item>
                <el-form-item :label="t('按钮颜色')" v-if="diyStore.editComponent.style  == 'style-5' || diyStore.editComponent.style  == 'style-8'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.btnColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('listFrameColor')" v-if="diyStore.editComponent.style  !== 'style-5' && diyStore.editComponent.style  !== 'style-6' && diyStore.editComponent.style  !== 'style-7'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('边框颜色')" v-if="diyStore.editComponent.style  === 'style-8'">
                    <el-color-picker v-model="diyStore.editComponent.couponItem.borderColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('listFrameColor')" v-if="diyStore.editComponent.style  == 'style-6' || diyStore.editComponent.style  == 'style-7'">
                    <el-color-picker v-for="(item,index) in diyStore.editComponent.couponItem.itemBgColor" :key="`color-${diyStore.editComponent.style}-${index}`" v-model="diyStore.editComponent.couponItem.itemBgColor[index]" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('goodsRounded')">
                    <el-slider v-model="diyStore.editComponent.couponItem.aroundRadius" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
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
import useUserStore from '@/stores/modules/user'
import { img } from '@/utils/common'
import { ref, reactive, computed } from 'vue'
import Sortable from 'sortablejs'
import storage from '@/utils/storage'
import { range } from 'lodash-es'
import adminCouponSelectPopup from '@/app/views/admin/marketing/coupon/components/common-coupon-select-popup.vue'
import shopCouponSelectPopup from '@/app/views/site/marketing/coupon/components/common-coupon-select-popup.vue'

const siteId = storage.get('siteId')
const diyStore = useDiyStore()
const userStore = useUserStore()
diyStore.editComponent.ignore = ['componentBgColor', 'componentBgUrl'] // 忽略公共属性

// 优惠券类型选项，根据已安装的插件动态生成
const couponTypeOptions = computed(() => {
    const options: { label: string; value: string }[] = []
    if (userStore.addons.includes('mall')) {
        options.push({ label: t('mallCoupon'), value: 'mall' })
    }
    if (userStore.addons.includes('o2o')) {
        options.push({ label: t('o2oCoupon'), value: 'o2o' })
    }
    if (userStore.addons.includes('waimai')) {
        options.push({ label: t('waimaiCoupon'), value: 'waimai' })
    }
    if (userStore.addons.includes('front_warehouse')) {
        options.push({ label: t('前置仓'), value: 'front_warehouse' })
    }
    return options
})

// 设置默认 addon 值
if (!diyStore.editComponent.addon && couponTypeOptions.value.length > 0) {
    diyStore.editComponent.addon = couponTypeOptions.value[0].value
}

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }

    if (diyStore.value[index].source == 'custom') {
        if (diyStore.value[index].couponIds.length == 0) {
            res.code = false
            res.message = t('couponPlaceholder')
            return res
        }
    }

    if (diyStore.value[index].btnText == '') {
        res.code = false
        res.message = t('couponBtnTextPlaceholder')
        return res
    }

    if (diyStore.value[index].couponTitle == '') {
        res.code = false
        res.message = t('couponTitlePlaceholder')
        return res
    }

    if (diyStore.value[index].couponSubTitle == '') {
        res.code = false
        res.message = t('couponSubTitlePlaceholder')
        return res
    }

    return res
}

const selectCouponStyle = reactive({
    title: diyStore.editComponent.styleName,
    value: diyStore.editComponent.style
})

// 风格样式
const showCouponDialog = ref(false)

const showCouponStyle = () => {
    showCouponDialog.value = true
    selectCouponStyle.title = diyStore.editComponent.styleName
    selectCouponStyle.value = diyStore.editComponent.style
}

const couponStyleList = reactive([
    {
        url: 'static/resource/images/diy/goods_coupon/style-1.png',
        title: '风格1',
        value: 'style-1'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-2.png',
        title: '风格2',
        value: 'style-2'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-3.png',
        title: '风格3',
        value: 'style-3'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-4.png',
        title: '风格4',
        value: 'style-4'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-5.png',
        title: '风格5',
        value: 'style-5'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-6.png',
        title: '风格6',
        value: 'style-6'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-7.png',
        title: '风格7',
        value: 'style-7'
    },
    {
        url: 'static/resource/images/diy/goods_coupon/style-8.png',
        title: '风格8',
        value: 'style-8'
    }
])

const changeCouponStyle = (item:any) => {
    selectCouponStyle.title = item.title
    selectCouponStyle.value = item.value
}

const confirmCouponStyle = () => {
    diyStore.editComponent.styleName = selectCouponStyle.title
    diyStore.editComponent.style = selectCouponStyle.value
    if (diyStore.editComponent.style == 'style-3') {
        if (diyStore.editComponent.couponTitle && diyStore.editComponent.couponTitle.length > 4) {
            diyStore.editComponent.couponTitle = diyStore.editComponent.couponTitle.substring(0, 4)
        }
        if (diyStore.editComponent.couponSubTitle && diyStore.editComponent.couponSubTitle.length > 7) {
            diyStore.editComponent.couponSubTitle = diyStore.editComponent.couponSubTitle.substring(0, 7)
        }
    }
    initStyleFn()
    showCouponDialog.value = false
}

const initStyleFn = () => {
    const index = diyStore.editComponent.ignore.indexOf('componentBgColor')
    if (index !== -1) {
        diyStore.editComponent.ignore.splice(index, 1)
    } else {
        diyStore.editComponent.ignore.push('componentBgColor')
    }

    switch (diyStore.editComponent.style) {
        case 'style-4':
            diyStore.editComponent.titleColor = '#ffffff'
            diyStore.editComponent.subTitleColor = '#ffffff'
            diyStore.editComponent.couponItem.moneyColor = '#fa191d'
            diyStore.editComponent.couponItem.textColor = '#333333'
            diyStore.editComponent.couponItem.subTextColor = '#999999'
            diyStore.editComponent.couponItem.bgColor = '#ffffff'
            diyStore.editComponent.couponItem.aroundRadius = 10
            diyStore.editComponent.componentStartBgColor = '#fa191d'
            break
        case 'style-5':
            diyStore.editComponent.titleColor = '#333'
            diyStore.editComponent.couponItem.moneyColor = '#fff'
            diyStore.editComponent.couponItem.textColor = '#fff'
            diyStore.editComponent.couponItem.bgColor = '#FFF7F6'
            diyStore.editComponent.couponItem.btnColor = '#fff'
            diyStore.editComponent.couponItem.aroundRadius = 6
            diyStore.editComponent.componentStartBgColor = '#fff'
            break
        case 'style-6':
            diyStore.editComponent.couponTitle = '畅享礼券'
            diyStore.editComponent.titleColor = '#333'
            diyStore.editComponent.couponItem.moneyColor = '#fff'
            diyStore.editComponent.couponItem.textColor = '#fff'
            // 使用数组整体替换，保证响应式
            diyStore.editComponent.couponItem.itemBgColor = ['#0E1647', '#352504', '#082B18', '#661414', '#0C3740', '#430E4B']
            diyStore.editComponent.couponItem.aroundRadius = 0
            diyStore.editComponent.componentStartBgColor = '#fff'
            diyStore.editComponent.topRounded = 12
            diyStore.editComponent.bottomRounded = 12
            break
        case 'style-7':
            diyStore.editComponent.couponTitle = '畅享礼券'
            diyStore.editComponent.titleColor = '#333'
            diyStore.editComponent.couponItem.itemMoneyColor = ['#00179C', '#684700', '#005627', '#C40000', '#004E5E', '#61036D']
            diyStore.editComponent.couponItem.itemTextColor = ['#00179C', '#684700', '#005627', '#C40000', '#004E5E', '#61036D']
            // 使用数组整体替换，保证响应式
            diyStore.editComponent.couponItem.itemBgColor = ['#D7DDFF', '#FBE1A9', '#C8F6DD', '#FFDCD8', '#BFEAF3', '#F4CFF9']
            diyStore.editComponent.couponItem.aroundRadius = 0
            diyStore.editComponent.componentStartBgColor = '#fff'
            diyStore.editComponent.topRounded = 12
            diyStore.editComponent.bottomRounded = 12
            break
    }
}
initStyleFn()

defineExpose({})

</script>
<style lang="scss">
.goods-coupon-slider {
    .el-slider__input {
        width: 100px;
    }
}
</style>
