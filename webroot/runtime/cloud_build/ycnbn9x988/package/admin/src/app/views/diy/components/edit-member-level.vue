
<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
            <h3 class="mb-[10px]">{{ t('selectStyle') }}</h3>
            <el-form label-width="80px" class="px-[10px]">
                <el-form-item :label="t('selectStyle')" class="flex">
                    <span class="text-primary flex-1 cursor-pointer" @click="showStyle">{{ diyStore.editComponent.styleName }}</span>
                    <el-icon @click="showStyle" class="cursor-pointer">
                        <ArrowRight />
                    </el-icon>
                </el-form-item>
                <el-form-item label="标题" v-if="diyStore.editComponent.style == 'style-5'">
                    <el-input v-model.trim="diyStore.editComponent.headerText" placeholder="请输入标题" clearable maxlength="20" show-word-limit />
                </el-form-item>
            </el-form>

            <el-dialog v-model="showDialog" :title="t('selectStyle')" width="660px">

                <div class="flex flex-wrap">
                    <template v-for="(item,index) in styleList" :key="index">
                        <div :class="{ 'border-primary': selectStyle.value == item.value, '!mr-[0]': [(index+1)%3] == 0 }" @click="changeStyle(item)" class="flex my-[5px] items-center justify-center overflow-hidden w-[200px] h-[100px] mr-[12px] cursor-pointer border bg-gray-50">
                            <img :src="img(item.url)" />
                        </div>
                    </template>
                </div>

                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                        <el-button type="primary" @click="confirmStyle">{{ t('confirm') }}</el-button>
                    </span>
                </template>

            </el-dialog>
        </div>
        <div class="edit-attr-item-wrap" v-if="diyStore.editComponent.style == 'style-5'">
            <h3 class="mb-[10px]">权益项</h3>
            <el-form label-width="120px" class="px-[10px]">
                <div v-for="(item, index) in diyStore.editComponent.list" :key="item.type || index" class="item-wrap p-[10px] pb-0 relative border border-dashed border-gray-300 mb-[16px]">
                    <el-form-item :label="t('image')">
                        <upload-image v-model="item.icon" :limit="1" />
                    </el-form-item>
                    <el-form-item :label="t('graphicNavTitle')">
                        <el-input v-model.trim="item.name" placeholder="请输入权益名称"  disabled clearable maxlength="8" show-word-limit />
                    </el-form-item>
                </div>
            </el-form>
        </div>
    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <div class="edit-attr-item-wrap" v-if="diyStore.editComponent.style == 'style-5'">
            <h3 class="mb-[10px]">样式</h3>
            <el-form label-width="120px" class="px-[10px]">
                <el-form-item label="顶部背景色">
                    <el-color-picker v-model="diyStore.editComponent.headerStartBgColor" show-alpha :predefine="diyStore.predefineColors" />
                    <icon name="iconfont iconmap-connect" size="20px" class="block !text-gray-400 mx-[5px]" />
                    <el-color-picker v-model="diyStore.editComponent.headerEndBgColor" show-alpha :predefine="diyStore.predefineColors" />
                </el-form-item>
                <el-form-item label="按钮文字颜色">
                    <el-color-picker v-model="diyStore.editComponent.buttonTextColor" />
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
import { img } from '@/utils/common'
import { ref, reactive } from 'vue'
import { getBenefits } from '@/app/api/vip_card'

const diyStore = useDiyStore()
diyStore.editComponent.ignore = ['componentBgColor', 'componentBgUrl'] // 忽略公共属性

const selectStyle = reactive({
    title: diyStore.editComponent.styleName,
    value: diyStore.editComponent.style
})

// 风格样式
const showDialog = ref(false)

const showStyle = () => {
    showDialog.value = true
    selectStyle.title = diyStore.editComponent.styleName
    selectStyle.value = diyStore.editComponent.style
}
const benefitsList = ref<any[]>([])
const getBenefitsFn = () => {
    getBenefits().then((res: any) => {
        benefitsList.value = res.data
        diyStore.editComponent.list = Object.values( benefitsList.value).map((item: any) => {
            const matched = diyStore.editComponent.list.find((s: any) => s.type === item.type)
            return {
                type: item.type,
                name: item.name,
                icon: matched ? matched.icon : (item.icon || '')
            }
        })
    })
}
getBenefitsFn()

const styleList = reactive([
    {
        url: 'static/resource/images/diy/member/member_level_style1.png',
        title: '风格1',
        value: 'style-1'
    },
    {
        url: 'static/resource/images/diy/member/member_level_style2.png',
        title: '风格2',
        value: 'style-2'
    },
    {
        url: 'static/resource/images/diy/member/member_level_style3.png',
        title: '风格3',
        value: 'style-3'
    },
    {
        url: 'static/resource/images/diy/member/member_level_style4.png',
        title: '风格4',
        value: 'style-4'
    },
    {
        url: 'static/resource/images/diy/member/member_level_style5.png',
        title: '风格5',
        value: 'style-5'
    }
])

const changeStyle = (item: any) => {
    selectStyle.title = item.title
    selectStyle.value = item.value
}

const confirmStyle = () => {
    diyStore.editComponent.styleName = selectStyle.title
    diyStore.editComponent.style = selectStyle.value
    initStyle(diyStore.editComponent.style)
    showDialog.value = false
}


const initStyle = (style: any) => {
    if (style == 'style-1') {
        diyStore.editComponent.bottomRounded = 0
        diyStore.editComponent.topRounded = 12
    } else if (style == 'style-2') {
        diyStore.editComponent.bottomRounded = 0
        diyStore.editComponent.topRounded = 12
    } else if (style == 'style-3') {
        diyStore.editComponent.bottomRounded = 12
        diyStore.editComponent.topRounded = 12
    } else if (style == 'style-4') {
        diyStore.editComponent.bottomRounded = 12
        diyStore.editComponent.topRounded = 12
    } else if (style == 'style-5') {
        diyStore.editComponent.bottomRounded = 12
        diyStore.editComponent.topRounded = 12
    }

}

defineExpose({})

</script>

<style lang="scss" scoped></style>
