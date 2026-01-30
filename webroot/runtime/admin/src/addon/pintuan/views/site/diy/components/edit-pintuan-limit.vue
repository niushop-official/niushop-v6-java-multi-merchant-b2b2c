<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('头部风格') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('selectStyle')" class="flex">
                    <span class="text-primary flex-1 cursor-pointer" @click="showHeadStyle">{{ diyStore.editComponent.headStyle.title }}</span>
                    <el-icon @click="showHeadStyle" class="cursor-pointer">
                        <ArrowRight />
                    </el-icon>
                </el-form-item>
            </el-form>

            <el-dialog v-model="showHeadStyleDialog" :title="t('selectStyle')" width="460px">

                <div class="flex flex-wrap">
                    <template v-for="(item,index) in headStyleList" :key="index">
                        <div class="head-style-wrap" :class="{ 'border-primary': selectHeadStyle.value == item.value }" @click="changeHeadStyle(item)">
                            <img :src="img(item.url)" />
                        </div>
                    </template>
                </div>

                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="showHeadStyleDialog = false">{{ t('cancel') }}</el-button>
                        <el-button type="primary" @click="confirmHeadStyle">{{ t('confirm') }}</el-button>
                    </span>
                </template>

            </el-dialog>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('标题内容') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
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
            <h3 class="mb-[10px]">{{ t('副标题内容') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('副标题')">
                    <el-input v-model.trim="diyStore.editComponent.subTitleStyle.text" :placeholder="t('请输入副标题')" clearable maxlength="10" show-word-limit />
                </el-form-item>
            </el-form>
        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('“更多”内容') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('名称')">
                    <el-input v-model.trim="diyStore.editComponent.moreStyle.text" :placeholder="t('请输入名称')" clearable maxlength="4" show-word-limit />
                </el-form-item>
                <el-form-item :label="t('link')">
                    <diy-link v-model="diyStore.editComponent.moreStyle.link" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('商品风格') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('selectStyle')" class="flex">
                    <span class="text-primary flex-1 cursor-pointer" @click="showGoodsStyle">{{ diyStore.editComponent.goodsStyle.title }}</span>
                    <el-icon @click="showGoodsStyle" class="cursor-pointer">
                        <ArrowRight />
                    </el-icon>
                </el-form-item>
            </el-form>

            <el-dialog v-model="showGoodsStyleDialog" :title="t('selectStyle')" width="460px">

                <div class="flex flex-wrap">
                    <template v-for="(item,index) in goodsStyleList" :key="index">
                        <div class="head-style-wrap" :class="{ 'border-primary': selectGoodsStyle.value == item.value }" @click="changeGoodsStyle(item)">
                            <img :src="img(item.url)" />
                        </div>
                    </template>
                </div>

                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="showGoodsStyleDialog = false">{{ t('cancel') }}</el-button>
                        <el-button type="primary" @click="confirmGoodsStyle">{{ t('confirm') }}</el-button>
                    </span>
                </template>

            </el-dialog>

        </div>
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t("selectSource") }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('选择商品')">
                    <el-radio-group v-model="diyStore.editComponent.goodsStyle.source" :title="t('选择商品')">
                        <el-radio label="all">{{ t('全部') }}</el-radio>
                        <el-radio label="custom">{{ t('手动选择') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('goodsNum')" v-if="diyStore.editComponent.goodsStyle.source == 'all'">
                    <el-slider class="goods-list-slider" show-input v-model="diyStore.editComponent.goodsStyle.num" :min="1" max="20" size="small" />
                </el-form-item>
                <el-form-item :label="t('customGoods')" v-if="diyStore.editComponent.goodsStyle.source == 'custom'">
                    <diy-goods-select-popup ref="goodsSelectPopupRef" v-model="diyStore.editComponent.goodsStyle.pintuanIds" :min="1" :max="99" />
                </el-form-item>
            </el-form>
        </div>

    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap" v-show="diyStore.editComponent.titleStyle.way == 'text'">
            <h3 class="mb-[10px]">{{ t('标题样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('textFontSize')">
                    <el-slider v-model="diyStore.editComponent.titleStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="12" :max="20" />
                </el-form-item>
                <el-form-item :label="t('textFontWeight')">
                    <el-radio-group v-model="diyStore.editComponent.titleStyle.fontWeight">
                        <el-radio :value="'normal'">{{ t('fontWeightNormal') }}</el-radio>
                        <el-radio :value="'bold'">{{ t('fontWeightBold') }}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('文本样式')">
                    <el-radio-group v-model="diyStore.editComponent.titleStyle.fontStyle">
                        <el-radio :label="'normal'">{{ t('常规') }}</el-radio>
                        <el-radio :label="'italic'">{{ t('斜体') }}</el-radio>
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
                    <el-slider v-model="diyStore.editComponent.subTitleStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="12" :max="15" />
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
                    <el-slider v-model="diyStore.editComponent.moreStyle.fontSize" show-input size="small" class="ml-[10px] diy-nav-slider" :min="12" :max="15" />
                </el-form-item>
                <el-form-item :label="t('textColor')">
                    <el-color-picker v-model="diyStore.editComponent.moreStyle.textColor" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('商品样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]" v-if="diyStore.editComponent.goodsStyle.value == 'style-1'">
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.goodsStyle.bgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('商品圆角')" v-if="diyStore.editComponent.goodsStyle.value == 'style-1'">
                    <el-slider v-model="diyStore.editComponent.goodsStyle.rounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
                <el-form-item :label="t('图片圆角')">
                    <el-slider v-model="diyStore.editComponent.goodsStyle.imgRounded" show-input size="small" class="ml-[10px] diy-nav-slider" :max="50" />
                </el-form-item>
            </el-form>
        </div>

        <div class="edit-attr-item-wrap" v-if="diyStore.editComponent.goodsStyle.value == 'style-1'">
            <h3 class="mb-[10px]">{{ t('按钮样式') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('文字颜色')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.textColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item :label="t('背景色')">
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.startBgColor" show-alpha :predefine="diyStore.predefineColors" />
                    <icon name="iconfont iconmap-connect" size="20px" class="block !text-gray-400 mx-[5px]" />
                    <el-color-picker v-model="diyStore.editComponent.btnStyle.endBgColor" show-alpha :predefine="diyStore.predefineColors" />
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
import diyGoodsSelectPopup from '@/addon/pintuan/views/site/active/components/diy-goods-select-popup.vue'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = [] // 忽略公共属性

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    if (diyStore.value[index].goodsStyle.source == 'custom' && !diyStore.value[index].goodsStyle.pintuanIds.length) {
        res.code = false
        res.message = t('请选择拼团商品')
    }

    return res
}


const selectHeadStyle = reactive({
    title: diyStore.editComponent.headStyle.title,
    value: diyStore.editComponent.headStyle.value
})

// 标题风格样式
const showHeadStyleDialog = ref(false)

const showHeadStyle = () => {
    selectHeadStyle.title = diyStore.editComponent.headStyle.title
    selectHeadStyle.value = diyStore.editComponent.headStyle.value
    showHeadStyleDialog.value = true
}

const changeHeadStyle = (item: any) => {
    selectHeadStyle.title = item.title
    selectHeadStyle.value = item.value
}

const confirmHeadStyle = () => {
    diyStore.editComponent.headStyle.title = selectHeadStyle.title
    diyStore.editComponent.headStyle.value = selectHeadStyle.value
    initTitleStyle()
    showHeadStyleDialog.value = false
}
const initTitleStyle = () => {
    if (diyStore.editComponent.headStyle.value == 'style-1') {
        diyStore.editComponent.titleStyle.fontStyle = 'normal'
        diyStore.editComponent.titleStyle.fontWeight = 'bold'
        diyStore.editComponent.titleStyle.text = '限量团购'
        diyStore.editComponent.subTitleStyle.text = '最高5折限量购'
    } else if (diyStore.editComponent.headStyle.value == 'style-2') {
        diyStore.editComponent.titleStyle.fontStyle = 'normal'
        diyStore.editComponent.titleStyle.fontWeight = 'bold'
        diyStore.editComponent.titleStyle.text = '特价团购 限时拼'
        diyStore.editComponent.subTitleStyle.text = ''
    }
}

const headStyleList = reactive([
    {
        url: 'addon/pintuan/diy/pintuan_limit/head-style1.jpg',
        title: '风格1',
        value: 'style-1'
    },
    {
        url: 'addon/pintuan/diy/pintuan_limit/head-style2.jpg',
        title: '风格2',
        value: 'style-2'
    }
])

const selectGoodsStyle = reactive({
    title: diyStore.editComponent.goodsStyle.title,
    value: diyStore.editComponent.goodsStyle.value
})

// 标题风格样式
const showGoodsStyleDialog = ref(false)

const showGoodsStyle = () => {
    selectGoodsStyle.title = diyStore.editComponent.goodsStyle.title
    selectGoodsStyle.value = diyStore.editComponent.goodsStyle.value
    showGoodsStyleDialog.value = true
}

const changeGoodsStyle = (item: any) => {
    selectGoodsStyle.title = item.title
    selectGoodsStyle.value = item.value
}

const confirmGoodsStyle = () => {
    diyStore.editComponent.goodsStyle.title = selectGoodsStyle.title
    diyStore.editComponent.goodsStyle.value = selectGoodsStyle.value
    showGoodsStyleDialog.value = false
}

const goodsStyleList = reactive([
    {
        url: 'addon/pintuan/diy/pintuan_limit/goods-style1.jpg',
        title: '风格1',
        value: 'style-1'
    },
    {
        url: 'addon/pintuan/diy/pintuan_limit/goods-style2.jpg',
        title: '风格2',
        value: 'style-2'
    }
])
defineExpose({})

</script>

<style lang="scss">
.head-style-wrap{
    display: flex;
    overflow: hidden;
    justify-content: center;
    cursor: pointer;
    align-items: center;
    width:200px;
    height:100px;
    margin-right: 12px;
    margin-bottom: 12px;
    border-width:1px;
    background: #eee;
}
.edit-list-wrap {

    .item-wrap {
        padding:10px 10px 0;
        position: relative;
        border:1px dashed;
        margin-bottom: 16px;
        .del {
            display: none;
        }

        &:hover {
            .del {
                display: block;
            }
        }
    }
}
.goods-list-slider {
    .el-slider__input {
        width: 100px;
    }
}
</style>
