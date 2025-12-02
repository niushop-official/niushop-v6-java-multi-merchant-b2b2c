<template>
	<view>
		<!-- tab页切换 -->
		<view class="tab-list-wrap relative z-10" v-if="prop.tab.control && isShow">
			<scroll-view scroll-x="true" class="scroll-wrap" :scroll-into-view="'a' + currTabIndex">
				<view @click="changeData({ source : 'home' },-1)" class="scroll-item" :class="[{ active: currTabIndex == -1 }]">
					<view class="name" :style="getTabColor(currTabIndex == -1)">精选</view>
				</view>
				<view v-for="(item, index) in prop.tab.list" class="scroll-item" :class="[{ active: index == currTabIndex }]" @click="changeData(item,index)" :id="'a' + index" :key="index">
					<view class="name" :style="getTabColor(index == currTabIndex)">{{ item.text }}</view>
				</view>
			</scroll-view>
		</view>
	</view>
</template>

<script lang="ts" setup>
import { computed, ref, watch,onMounted, nextTick,getCurrentInstance } from 'vue'
import { t } from '@/locale'
import useDiyStore from '@/app/stores/diy'
const diyStore = useDiyStore();
const prop = defineProps({
    tab: {
        type: Object,
        default: (() => {
            return {}
        })
    },
	selectIndex: {
        type: Number || String,
        default: -1
    },
    isShow: {
        type: Boolean,
        default: true
    }
})

const currTabIndex = ref(-1)

watch(() => prop.selectIndex, (val) => {
    currTabIndex.value = prop.selectIndex;
},{immediate:true})


const getTabColor = (flag: any) => {
    let style = '';
    if (flag) {
        style += `color:${prop.tab.selectColor};`
		style += `background-color:${prop.tab.selectBgColor};`
    } else {
        style += `color:${prop.tab.noColor};`
		style += `background-color:${prop.tab.noBgColor};`
    }
    return style;
}

const emits = defineEmits(['confirm'])
const changeData = (item: any, index: any) => {
    if (diyStore.mode == 'decorate') return false;
    currTabIndex.value = index;
	emits('confirm', item,index)
}

</script>

<style lang="scss" scoped>
	// tab
	.tab-list-wrap {
		.scroll-wrap {
			left: 0;
			right: 0;
			z-index: 5;
			width: 100%;
			white-space: nowrap;
			box-sizing: border-box;
			padding: 20rpx;
			line-height: 1;
		}

		.scroll-item {
			display: inline-block;
			text-align: center;
			vertical-align: top;
			width: auto;
			position: relative;
			margin: 0 40rpx;
			&:first-of-type{
				margin-left: 0;
			}

			.name {
				display: inline-block;
				font-size: 24rpx;
				color: #333;
				line-height: 32rpx;
				padding: 6rpx 30rpx;
				border-radius: 100rpx;
			}

			&.active {
				
			}
		}


	}
</style>
