<template>
    <!-- 内容 -->
    <div class="content-wrap" v-show="diyStore.editTab == 'content'">
        <div class="edit-attr-item-wrap">
			<h3 class="mb-[10px]">{{ t('selectStyle') }}</h3>
			<el-form label-width="80px" class="px-[10px]">
				<el-form-item :label="t('selectStyle')" class="flex">
					<span class="text-primary flex-1 cursor-pointer" @click="showStyle">{{ diyStore.editComponent.styleName }}</span>
					<el-icon>
						<ArrowRight />
					</el-icon>
				</el-form-item>
			</el-form>

			<el-dialog v-model="showDialog" :title="t('selectStyle')" width="700px">

				<div class="flex flex-wrap">
					<template v-for="(item,index) in cashBuyStyleList" :key="index">
						<div :class="{ 'border-primary': selectStyle.value == item.value }" @click="changeCouponStyle(item)" class="flex items-center justify-center overflow-hidden w-[200px] h-[100px] m-[6px] cursor-pointer border bg-gray-50">
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

    </div>

    <!-- 样式 -->
    <div class="style-wrap" v-show="diyStore.editTab == 'style'">
        <!-- 组件样式 -->
        <slot name="style"></slot>
    </div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import useDiyStore from '@/stores/modules/diy'
import { img } from '@/utils/common'
import { reactive, ref } from 'vue'

const diyStore: any = useDiyStore()
diyStore.editComponent.ignore = ['pageBgColor'] // 忽略公共属性

// 组件验证
diyStore.editComponent.verify = (index: number) => {
    const res = { code: true, message: '' }
    return res
}

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

const changeCouponStyle = (item:any) => {
    selectStyle.title = item.title
    selectStyle.value = item.value
}

const confirmStyle = () => {
    diyStore.editComponent.styleName = selectStyle.title
    diyStore.editComponent.style = selectStyle.value
    showDialog.value = false
}

const cashBuyStyleList = reactive([
    {
        url: 'static/resource/images/diy/cashbuy1.png',
        title: '风格1',
        value: 'style-1'
    },
    {
        url: 'static/resource/images/diy/cashbuy2.png',
        title: '风格2',
        value: 'style-2'
    }
])

defineExpose({})

</script>

<style lang="scss" scoped></style>
