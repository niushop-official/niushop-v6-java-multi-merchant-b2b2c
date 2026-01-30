<template>
	<!-- 内容 -->
	<div class="content-wrap" v-show="diyStore.editTab == 'content'">
		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('shopInfoShowPosition') }}</h3>
			<el-form label-width="100px" class="px-[10px]">
				<el-form-item :label="t('shopInfoShowWay')">
					<el-radio-group v-model="diyStore.editComponent.positionWay">
						<el-radio label="static">{{ t('shopInfoShowWayStatic') }}</el-radio>
						<el-radio label="fixed">{{ t('shopInfoShowWayFixed') }}</el-radio>
					</el-radio-group>
				</el-form-item>
				<el-form-item :label="t('shopInfoFixedBgColor')" v-show="diyStore.editComponent.positionWay == 'fixed'">
					<el-color-picker v-model="diyStore.editComponent.fixedBgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
                <el-form-item :label="t('shopInfoBgGradient')">
                    <el-radio-group v-model="diyStore.editComponent.bgGradient">
						<el-radio :label="true">{{ t('shopInfoOpen') }}</el-radio>
						<el-radio :label="false">{{ t('shopInfoClose') }}</el-radio>
					</el-radio-group>
                </el-form-item>
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('shopInfoSet') }}</h3>
			<el-form label-width="100px" class="px-[10px]">
				<el-form-item :label="t('shopInfoText')">
					<div>
						<el-input v-model.trim="diyStore.editComponent.search.text" :placeholder="t('shopInfoPlaceholder')" clearable maxlength="20" show-word-limit />
						<p class="text-sm text-gray-400 mt-[10px] leading-[1.5]">{{t('shopInfoTextTips')}}</p>
					</div>
				</el-form-item>
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('shopInfoHotWordSet') }}</h3>
			<el-form label-width="100px" class="px-[10px]">

				<el-form-item :label="t('shopInfoHotWordInterval')">
					<el-slider v-model="diyStore.editComponent.search.hotWord.interval" show-input size="small" class="ml-[10px] horz-blank-slider" :min="1" :max="10"/>
				</el-form-item>

				<p class="text-sm text-gray-400 mb-[10px]">{{ t('dragMouseAdjustOrder') }}</p>

				<div ref="searchHotWordTabBoxRef">
					<div v-for="(item,index) in diyStore.editComponent.search.hotWord.list" :key="item.id" class="item-wrap p-[10px] relative border border-dashed border-gray-300 mb-[16px]">

						<el-form-item :label="t('shopInfoHotWordText')" class="!mb-0">
							<el-input v-model.trim="item.text" :placeholder="t('shopInfoHotWordTextPlaceholder')" clearable maxlength="4" show-word-limit/>
						</el-form-item>

						<div class="del absolute cursor-pointer z-[2] top-[-8px] right-[-8px]" @click="diyStore.editComponent.search.hotWord.list.splice(index,1)">
							<icon name="element-CircleCloseFilled" color="#bbb" size="20px"/>
						</div>

					</div>
					<el-button v-show="diyStore.editComponent.search.hotWord.list.length < 50" class="w-full" @click="addHotWordItem">{{ t('shopInfoAddHotWordItem') }}</el-button>
				</div>

			</el-form>
		</div>

		<el-collapse v-model="activeNames" @change="handleChange" class="collapse-wrap">
			<el-collapse-item :title="t('carouselSearchSwiperSet')" name="swiper">
                <el-form label-width="100px" class="px-[10px]">
                    <el-form-item :label="t('carouselSearchSwiperControl')">
                        <el-switch v-model="diyStore.editComponent.swiper.control" />
                    </el-form-item>
                    <el-form-item :label="t('carouselSearchSwiperInterval')">
                        <el-slider v-model="diyStore.editComponent.swiper.interval" show-input size="small" class="ml-[10px] diy-nav-slider" :min="1" :max="10" />
                    </el-form-item>

                    <div class="text-sm text-gray-400 mb-[10px]">{{ t('carouselSearchSwiperTips') }}</div>

                    <div ref="imageBoxRef">
                        <div v-for="(item,index) in diyStore.editComponent.swiper.list" :key="item.id" class="item-wrap p-[10px] pb-0 relative border border-dashed border-gray-300 mb-[16px]">
                            <el-form-item :label="t('image')">
                                <upload-image v-model="item.imageUrl" :limit="1" @change="selectImg" />
                            </el-form-item>

                            <div class="del absolute cursor-pointer z-[2] top-[-8px] right-[-8px]" v-show="diyStore.editComponent.swiper.list.length > 1" @click="diyStore.editComponent.swiper.list.splice(index,1)">
                                <icon name="element CircleCloseFilled" color="#bbb" size="20px" />
                            </div>

                            <el-form-item :label="t('link')">
                                <diy-link v-model="item.link" />
                            </el-form-item>
                        </div>
                    </div>

                    <el-button v-show="diyStore.editComponent.swiper.list.length < 10" class="w-full" @click="addImageAd">{{ t('addImageAd') }}</el-button>
                </el-form>
            </el-collapse-item>
		</el-collapse>
	</div>

	<!-- 样式 -->
	<div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('textSet') }}</h3>
			<el-form label-width="80px" class="px-[10px]">
				<el-form-item :label="t('textColor')">
					<el-color-picker v-model="diyStore.editComponent.shop.textColor" />
				</el-form-item>
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('carouselSearchStyle') }}</h3>
			<el-form label-width="120px" class="px-[10px]">
				<el-form-item :label="t('carouselSearchTextColor')">
					<el-color-picker v-model="diyStore.editComponent.search.color" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('carouselSearchBgColor')">
					<el-color-picker v-model="diyStore.editComponent.search.bgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('carouselSearchBtnColor')">
					<el-color-picker v-model="diyStore.editComponent.search.btnColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('carouselSearchBtnBgColor')">
					<el-color-picker v-model="diyStore.editComponent.search.btnBgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
                <el-form-item :label="t('置顶文字颜色')">
					<el-color-picker v-model="diyStore.editComponent.fixedSearch.color" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('置顶背景颜色')">
					<el-color-picker v-model="diyStore.editComponent.fixedSearch.bgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('置顶按钮颜色')">
					<el-color-picker v-model="diyStore.editComponent.fixedSearch.btnColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('置顶按钮背景色')">
					<el-color-picker v-model="diyStore.editComponent.fixedSearch.btnBgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('carouselSearchSwiperSet') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('carouselSearchSwiperStyle')" @change="changeSwiperStyle">
                    <el-radio-group v-model="diyStore.editComponent.swiper.swiperStyle">
                        <el-radio label="style-1">{{ t('carouselSearchSwiperIndicatorStyle1') }}</el-radio>
                        <el-radio label="style-2">{{ t('carouselSearchSwiperIndicatorStyle2') }}</el-radio>
                        <el-radio label="style-3">{{ t('carouselSearchSwiperIndicatorStyle3') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('topRounded')">
                    <el-slider v-model="diyStore.editComponent.swiper.topRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
                <el-form-item :label="t('bottomRounded')">
                    <el-slider v-model="diyStore.editComponent.swiper.bottomRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('carouselSearchSwiperIndicatorSet') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('carouselSearchSwiperIndicatorStyle')">
                    <el-radio-group v-model="diyStore.editComponent.swiper.indicatorStyle">
                        <el-radio label="style-1">{{ t('carouselSearchSwiperIndicatorStyle1') }}</el-radio>
                        <el-radio label="style-2">{{ t('carouselSearchSwiperIndicatorStyle2') }}</el-radio>
                        <el-radio label="style-3">{{ t('carouselSearchSwiperIndicatorStyle3') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('carouselSearchSwiperIndicatorAlign')">
                    <el-radio-group v-model="diyStore.editComponent.swiper.indicatorAlign">
                        <el-radio label="left">{{ t('alignLeft') }}</el-radio>
                        <el-radio label="center">{{ t('alignCenter') }}</el-radio>
                        <el-radio label="right">{{ t('alignRight') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('noColor')">
                    <el-color-picker v-model="diyStore.editComponent.swiper.indicatorColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('selectColor')">
                    <el-color-picker v-model="diyStore.editComponent.swiper.indicatorActiveColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
            </el-form>
        </div>

		


		<!-- 组件样式 -->
		<!-- <slot name="style"></slot> -->
	</div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { img } from '@/utils/common'
import useDiyStore from '@/stores/modules/diy'
import { ref, watch, onMounted, nextTick, reactive } from 'vue'
import Sortable from 'sortablejs'
import { cloneDeep, range } from 'lodash-es'


const diyStore = useDiyStore()
diyStore.editComponent.ignore = ['componentBgColor', 'componentBgUrl', 'marginTop', 'marginBottom', 'topRounded', 'bottomRounded', 'pageBgColor', 'marginBoth'] // 忽略公共属性

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }

    diyStore.value[index].search.hotWord.list.forEach((item: any) => {
        if (item.text == '') {
            res.code = false
            res.message = t('shopInfoHotWordTextPlaceholder')
            return res
        }
    })

    if (diyStore.value[index].swiper.control) {
        diyStore.value[index].swiper.list.forEach((item: any) => {
            if (item.imageUrl == '') {
                res.code = false
                res.message = t('shopInfoImageUrlTip')
                return res
            }
        })
    }
    return res
}

diyStore.editComponent.search.hotWord.list.forEach((item: any) => {
    if (!item.id) item.id = diyStore.generateRandom()
})

diyStore.editComponent.swiper.list.forEach((item: any) => {
    if (!item.id) item.id = diyStore.generateRandom()
})

const activeNames = ref(['swiper'])

const handleChange = (val: string[]) => {}

const addHotWordItem = () => {
    diyStore.editComponent.search.hotWord.list.push({
        id: diyStore.generateRandom(),
        text: '关键词'
    })
}

const searchHotWordTabBoxRef = ref()

const imageBoxRef = ref()

onMounted(() => {
    nextTick(() => {
        const hotWordSortable = Sortable.create(searchHotWordTabBoxRef.value, {
            group: 'item-wrap',
            animation: 200,
            onEnd: event => {
                const temp = diyStore.editComponent.search.hotWord.list[event.oldIndex!]
                diyStore.editComponent.search.hotWord.list.splice(event.oldIndex!, 1)
                diyStore.editComponent.search.hotWord.list.splice(event.newIndex!, 0, temp)
                hotWordSortable.sort(
                    range(diyStore.editComponent.search.hotWord.list.length).map(value => {
                        return value.toString()
                    })
                )
            }
        })
        const imageSortable = Sortable.create(imageBoxRef.value, {
            group: 'item-wrap',
            animation: 200,
            onEnd: event => {
                const temp = diyStore.editComponent.swiper.list[event.oldIndex!]
                diyStore.editComponent.swiper.list.splice(event.oldIndex!, 1)
                diyStore.editComponent.swiper.list.splice(event.newIndex!, 0, temp)
                imageSortable.sort(
                    range(diyStore.editComponent.swiper.list.length).map(value => {
                        return value.toString()
                    })
                )
                handleHeight(true)
            }
        })
    })
})

watch(
    () => diyStore.editComponent.swiper.list,
    (newValue, oldValue) => {
        // 设置图片宽高
        handleHeight()
    },
    { deep: true }
)

const addImageAd = () => {
    diyStore.editComponent.swiper.list.push({
        id: diyStore.generateRandom(),
        imageUrl: '',
        imgWidth: 0,
        imgHeight: 0,
        link: { name: '' }
    })
}

const selectImg = (url: string) => {
    handleHeight(true)
}

const changeSwiperStyle = (value: any) => {
    handleHeight(true)
}

// 处理高度
const handleHeight = (isCalcHeight: boolean = false) => {
    diyStore.editComponent.swiper.list.forEach((item: any, index: number) => {
        const image = new Image()
        image.src = img(item.imageUrl)
        image.onload = async () => {
            item.imgWidth = image.width
            item.imgHeight = image.height
            // 计算第一张图片高度
            if (isCalcHeight && index == 0) {
                const ratio = item.imgHeight / item.imgWidth
                if (diyStore.editComponent.swiper.swiperStyle == 'style-1') {
                    item.width = 375 * 0.92 // 0.92：前端缩放比例
                } else {
                    item.width = 355
                }
                item.height = item.width * ratio
                diyStore.editComponent.swiper.imageHeight = parseInt(item.height)
            }
        }
    })
}
defineExpose({})

</script>

<style lang="scss" scoped></style>
<style lang="scss">
	.select-diy-page-input .el-input__inner{
		cursor: pointer;
	}
	.collapse-wrap{
		.el-collapse-item__header{
			font-size: 16px;
		}
	}
</style>