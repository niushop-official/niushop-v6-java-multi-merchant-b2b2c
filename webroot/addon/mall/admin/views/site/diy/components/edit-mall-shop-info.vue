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
			<el-collapse-item :title="t('carouselSearchTabSet')" name="tab">
				<div class="edit-attr-item-wrap">
					<el-form label-width="100px" class="px-[10px]" @submit.prevent>
						<el-form-item :label="t('carouselSearchTabControl')">
							<el-switch v-model="diyStore.editComponent.tab.control" />
						</el-form-item>

						<template v-if="diyStore.editComponent.tab.control">
							<p class="text-sm text-gray-400 mb-[10px]">{{ t('dragMouseAdjustOrder') }}</p>

							<div ref="tabBoxRef">
								<div v-for="(item,index) in diyStore.editComponent.tab.list" :key="item.id" class="item-wrap p-[10px] pb-0 relative border border-dashed border-gray-300 mb-[16px]">

									<el-form-item :label="t('carouselSearchTabCategoryText')">
										<el-input v-model.trim="item.text" :placeholder="t('carouselSearchTabCategoryTextPlaceholder')" clearable maxlength="4" show-word-limit/>
									</el-form-item>

									<el-form-item :label="t('dataSources')">
										<el-input v-model.trim="item.diy_title" :placeholder="t('selectDiyPagePlaceholder')" readonly class="select-diy-page-input" @click="diyPageShowDialogOpen(index)">
											<template #suffix>
												<div @click.stop="tabClear(index)">
													<el-icon v-if="item.diy_title">
														<Close />
													</el-icon>
													<el-icon v-else>
														<ArrowRight />
													</el-icon>
												</div>
											</template>
										</el-input>
									</el-form-item>

									<div class="del absolute cursor-pointer z-[2] top-[-8px] right-[-8px]" v-show="diyStore.editComponent.tab.list.length > 1" @click="diyStore.editComponent.tab.list.splice(index,1)">
										<icon name="element CircleCloseFilled" color="#bbb" size="20px"/>
									</div>

								</div>
								<el-button v-show="diyStore.editComponent.tab.list.length < 50" class="w-full" @click="addTabItem">{{ t('carouselSearchAddTabItem') }}</el-button>
							</div>
						</template>

						<!-- 选择微页面弹出框 -->
						<el-dialog v-model="diyPageShowDialog" :title="t('selectSourcesDiyPage')" width="1000px" :close-on-press-escape="true" :destroy-on-close="true" :close-on-click-modal="false">
							<el-table :data="diyPageTable.data" ref="diyPageTableRef" size="large" v-loading="diyPageTable.loading" height="490px" @current-change="handleCurrentDiyPageChange" row-key="id" highlight-current-row>
								<template #empty>
									<span>{{ !diyPageTable.loading ? t('emptyData') : '' }}</span>
								</template>
								<el-table-column prop="page_title" :label="t('diyPageTitle')" min-width="120" />
								<el-table-column prop="addon_name" :label="t('diyPageTypeName')" min-width="80" />
								<el-table-column prop="type_name" :label="t('diyPageForAddon')" min-width="80" />
							</el-table>
							<div class="mt-[16px] flex justify-end">
								<el-pagination v-model:current-page="diyPageTable.page" v-model:page-size="diyPageTable.limit"
												layout="total, sizes, prev, pager, next, jumper" :total="diyPageTable.total"
												@size-change="loadDiyPageList" @current-change="loadDiyPageList" />
							</div>
							<div class="flex items-center justify-end mt-[15px]">
								<el-button type="primary" @click="saveDiyPageId">{{ t('confirm') }}</el-button>
								<el-button @click="diyPageShowDialog = false">{{ t('cancel') }}</el-button>
							</div>
						</el-dialog>

					</el-form>
				</div>
			</el-collapse-item>
		</el-collapse>
	</div>

	<!-- 样式 -->
	<div class="style-wrap" v-show="diyStore.editTab == 'style'">
		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('shopInfoBgStyle') }}</h3>
			<el-form label-width="100px" class="px-[10px]">
				<el-form-item :label="t('shopInfoBgImage')">
					<upload-image v-model="diyStore.editComponent.bgImg" :limit="1" />
				</el-form-item>
				<el-form-item :label="t('shopInfoBgOpacity')">
					<el-slider v-model="diyStore.editComponent.bgOpacity" show-input size="small" class="ml-[10px] horz-blank-slider" :min="1" :max="100"/>
				</el-form-item>
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('carouselSearchStyle') }}</h3>
			<el-form label-width="100px" class="px-[10px]">
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
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('carouselSearchTabStyle') }}</h3>
			<el-form label-width="100px" class="px-[10px]">
				<el-form-item :label="t('noColor')">
					<el-color-picker v-model="diyStore.editComponent.tab.noColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('selectColor')">
					<el-color-picker v-model="diyStore.editComponent.tab.selectColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('noBgColor')">
					<el-color-picker v-model="diyStore.editComponent.tab.noBgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
				<el-form-item :label="t('selectBgColor')">
					<el-color-picker v-model="diyStore.editComponent.tab.selectBgColor" show-alpha :predefine="diyStore.predefineColors"/>
				</el-form-item>
			</el-form>
		</div>

		<div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('textSet') }}</h3>
			<el-form label-width="80px" class="px-[10px]">

				<el-form-item :label="t('textColor')">
					<el-color-picker v-model="diyStore.editComponent.shop.textColor" />
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
import { getDiyPageListByCarouselSearch } from '@/app/api/diy'

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
    return res
}

diyStore.editComponent.search.hotWord.list.forEach((item: any) => {
    if (!item.id) item.id = diyStore.generateRandom()
})

const activeNames = ref(['tab'])

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
                tabSortable.sort(
                    range(diyStore.editComponent.search.hotWord.list.length).map(value => {
                        return value.toString()
                    })
                )
            }
        })
    })
})

onMounted(() => {
    loadDiyPageList()
})

const tabClear = (index:any) => {
    diyStore.editComponent.tab.list[index].diy_id = 0
    diyStore.editComponent.tab.list[index].diy_title = ''
}

const addTabItem = () => {
    diyStore.editComponent.tab.list.push({
        id: diyStore.generateRandom(),
        text: '分类名称', // 最多4个字
        source: 'diy_page', // 数据源类型，微页面：diy_page
        diy_id: '',
        diy_title: ''
    })
}
const diyPageShowDialog = ref(false)
const diyPageTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
    }
})
const diyPageTableRef = ref<InstanceType<typeof ElTable>>()

/**
 * 获取自定义页面列表
 */
const loadDiyPageList = (page: number = 1) => {
    diyPageTable.loading = true
    diyPageTable.page = page

    getDiyPageListByCarouselSearch({
        page: diyPageTable.page,
        limit: diyPageTable.limit,
        ...diyPageTable.searchParam
    }).then(res => {
        diyPageTable.loading = false
        let data = res.data.data
        let newData: any = []
        let isExistCount = 0

        // 排除当前编辑的微页面以及存在 置顶组件的数据
        if (diyStore.id) {
            for (let i = 0; i < data.length; i++) {
                if (data[i].id == diyStore.id) {
                    isExistCount++
                } else {
                    newData.push(data[i])
                }
            }
        } else {
            newData = cloneDeep(data) // 添加
        }
        if (isExistCount) {
            res.data.total = res.data.total - isExistCount
        }
        diyPageTable.data = newData
        diyPageTable.total = res.data.total
    }).catch(() => {
        diyPageTable.loading = false
    })
}

// 选择微页面
let currDiyPage:any = {}
let currTabIndexForDiyPage = 0
const handleCurrentDiyPageChange = (val: string | any[]) => {
    currDiyPage = val
}

const saveDiyPageId = () => {
    diyStore.editComponent.tab.list[currTabIndexForDiyPage].diy_id = currDiyPage.id
    diyStore.editComponent.tab.list[currTabIndexForDiyPage].diy_title = currDiyPage.title
    diyPageShowDialog.value = false
}
const diyPageShowDialogOpen = (index:any) => {
    diyPageShowDialog.value = true
    currTabIndexForDiyPage = index
    if (currDiyPage) {
        setTimeout(() => {
            diyPageTableRef.value!.setCurrentRow(currDiyPage)
        }, 200)
    }
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