<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('头部背景图')">
                    <upload-image v-model="diyStore.editComponent.headImgUrl" :limit="1" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('标题内容') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('标题风格')">
                    <el-radio-group v-model="diyStore.editComponent.titleStyle.way">
                        <el-radio :value="'text'">{{ t('文字') }}</el-radio>
                        <el-radio :value="'img'">{{ t('图片') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('标题')" v-show="diyStore.editComponent.titleStyle.way == 'text'">
                    <el-input v-model.trim="diyStore.editComponent.titleStyle.text" :placeholder="t('请输入标题')" clearable maxlength="10" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('image')" v-show="diyStore.editComponent.titleStyle.way == 'img'">
                    <upload-image v-model="diyStore.editComponent.titleStyle.imgUrl" :limit="1" />
                </el-form-item>
                <el-form-item :label="t('link')">
                    <diy-link v-model="diyStore.editComponent.titleStyle.link" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('“更多”内容') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('名称')">
                    <el-input v-model.trim="diyStore.editComponent.moreStyle.text" :placeholder="t('请输入名称')" clearable maxlength="4" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('link')">
                    <diy-link v-model="diyStore.editComponent.moreStyle.link" />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t("manyGoodsListCategorySet") }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <div>

                    <p class="text-sm text-gray-400 mb-[10px]">{{ t('dragMouseAdjustOrder') }}</p>

                    <div ref="goodsBoxRef">
                        <div v-for="(item,index) in diyStore.editComponent.list" :key="item.id"
                             class="item-wrap p-[10px] pb-0 relative border border-dashed border-gray-300 mb-[16px]">

                            <el-form-item :label="t('manyGoodsListCategoryName')">
                                <el-input v-model.trim="item.title" clearable maxlength="4" show-word-limit />
                            </el-form-item>
                            <el-form-item :label="t('goodsSelectPopupSelectGoodsButton')">
                                <el-radio-group v-model="item.source">
                                    <el-radio label="all">{{ t('goodsSelectPopupAllGoods') }}</el-radio>
                                    <el-radio label="custom">{{ t('manualSelectionSources') }}</el-radio>
                                </el-radio-group>
                            </el-form-item>
                            <el-form-item :label="t('goodsNum')" v-if="item.source == 'all'">
                                <el-slider show-input class="diy-nav-slider" v-model="item.num" :min="1" max="20" size="small" />
                            </el-form-item>
                            <el-form-item :label="t('customGoods')" v-if="item.source == 'custom'">
                                <diy-goods-select-popup ref="goodsSelectPopupRef" v-model="item.pintuan_ids" :min="1" :max="99" />
                            </el-form-item>

                            <div class="del absolute cursor-pointer z-[2] top-[-8px] right-[-8px]"
                                 v-show="diyStore.editComponent.list.length > 1"
                                 @click="diyStore.editComponent.list.splice(index,1)">
                                <icon name="element CircleCloseFilled" color="#bbb" size="20px" />
                            </div>

                        </div>
                    </div>

                    <el-button class="w-full" @click="addItem">{{ t('manyGoodsLisAddItem') }}</el-button>
                </div>

            </el-form>
        </div>

        <div class="edit-attr-item-wrap mt-[20px]">
            <h3 class="mb-[10px]">{{ t("goodsBuyBtn") }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('按钮名称')">
                    <el-input v-model.trim="diyStore.editComponent.btnStyle.text"
                              :placeholder="t('goodsBtnTextPlaceholder')" clearable maxlength="4" show-word-limit />
                </el-form-item>
            </el-form>
        </div>
    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
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
            <h3 class="mb-[10px]">{{ t('副标题样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('textFontSize')">
                    <el-slider v-model="diyStore.editComponent.subTitleStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="10" :max="15" />
                </el-form-item>
                <el-form-item :label="t('textColor')">
                    <el-color-picker v-model="diyStore.editComponent.subTitleStyle.textColor" />
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
            <h3 class="mb-[10px]">{{ t('goodsStyle') }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('goodsBgColor')">
                    <el-color-picker v-model="diyStore.editComponent.goodsStyle.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('图片圆角')">
                    <el-slider v-model="diyStore.editComponent.goodsStyle.imgRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
                <el-form-item :label="t('商品圆角')">
                    <el-slider v-model="diyStore.editComponent.goodsStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t("goodsBuyBtn") }}</h3>
            <el-form label-width="100px" class="px-[10px]">
                <el-form-item :label="t('goodsTextColor')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.textColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('listFrameColor')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.startBgColor" show-alpha :predefine="diyStore.predefineColors" />
                    <icon name="iconfont iconmap-connect" size="20px" class="block !text-gray-400 mx-[5px]" />
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.endBgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('圆角')">
                    <el-slider v-model="diyStore.editComponent.btnStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
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
import { ref, onMounted, nextTick } from 'vue'
import { range } from 'lodash-es'
import Sortable from 'sortablejs'
import diyGoodsSelectPopup from '@/addon/pintuan/views/site/active/components/diy-goods-select-popup.vue'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = ['componentBgUrl'] // 忽略公共属性
const goodsBoxRef = ref(null)

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }

    if (diyStore.value[index].list?.length) {
        diyStore.value[index].list.forEach((item: any) => {
            if (item.source == 'custom') {
                if (item.pintuan_ids.length == 0) {
                    res.code = false
                    res.message = t('请选择拼团商品')
                }
            }
        })
    }

    return res
}

diyStore.editComponent.list.forEach((item: any) => {
    if (!item.id) item.id = diyStore.generateRandom()
})

onMounted(() => {
    nextTick(() => {
        const sortable = Sortable.create(goodsBoxRef.value, {
            group: 'item-wrap',
            animation: 200,
            onEnd: event => {
                const temp = diyStore.editComponent.list[event.oldIndex!]
                diyStore.editComponent.list.splice(event.oldIndex!, 1)
                diyStore.editComponent.list.splice(event.newIndex!, 0, temp)
                sortable.sort(
                    range(diyStore.editComponent.list.length).map(value => {
                        return value.toString()
                    })
                )
            }
        })
    })
    window.addEventListener("resize", getScrollBarWidth);
})

const addItem = () => {
    diyStore.editComponent.list.push({
        id: diyStore.generateRandom(),
        title: "分类",
        source: "all",
        pintuan_ids: [],
        num: 10
    })
}

const scrollBarWidth = ref(0)
const tableBodyRef = ref(null)

const getScrollBarWidth = () => {
    nextTick(() => {
        if (tableBodyRef.value) {
            scrollBarWidth.value = tableBodyRef.value.offsetWidth - tableBodyRef.value.clientWidth
        }
    })
}
defineExpose({})

</script>

<style lang="scss" scoped>
.arrow-show {
  transform: rotate(90deg) !important; /* 提高优先级 */
}
</style>
