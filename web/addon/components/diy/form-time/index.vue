<template>
	<div v-if="diyComponent.viewFormDetail" class="form-item-frame">
		<div class="base-layout-two detail">
			<div class="detail-two-content">
				<span class="detail-two-content-label">{{ diyComponent.field.name }}</span>
				<div class="detail-two-content-value w-[80%]">
					<span>{{ diyComponent.field.value || t('notHave') }}</span>
				</div>
			</div>
		</div>
	</div>
	<div v-else :style="warpCss" class="form-item-frame form-time">
		<div class="base-layout-two">
			<div class="layout-two-wrap" :class="{'no-border': !diyGlobal.borderControl}">
				<div class="layout-two-label">
					<span class="required"
						v-if="diyComponent.field.required">{{ diyComponent.field.required ? '*' : '' }}</span>
					<span class="name">{{ diyComponent.field.name }}</span>
				</div>
				<div class="layout-two-content">
					<el-time-picker v-model="timeValue" format="HH:mm" value-format="HH:mm"
						:placeholder="diyComponent.placeholder" @change="timeConfirm" />
				</div>
			</div>
			<div class="layout-two-error-message" v-if="errorInfo && !errorInfo.code">{{ errorInfo.message }}</div>
			<div v-if="diyComponent.field.remark.text" class="layout-two-remark"
				:style="{ color: diyComponent.field.remark.color, fontSize: diyComponent.field.remark.fontSize + 'px' }">
				{{ diyComponent.field.remark.text }}</div>
			<div class="layout-two-attribute-wrap" v-if="inputAttribute().length">
				<div v-for="(item,index) in inputAttribute()" :key="index" @click="eventFn(item.type)"
					class="layout-two-attribute-item">{{ item.title }}</div>
			</div>
		</div>
	</div>
</template>

<script setup lang="ts">
	// 表单 时间组件
	import { ref, computed, watch, onMounted } from 'vue';
	import { img } from '@/utils/common'; // 移除了timeTurnTimeStamp，不再需要

	const props = defineProps(['component', 'index', 'global']);
	const errorInfo : any = ref(null);

	const diyComponent = computed(() => {
		return props.component;
	})

	const diyGlobal = computed(() => {
		return props.global;
	})

	// input属性
	const inputAttribute = () => {
		let arr = [];
		if (diyComponent.value.autofill) {
			let obj = {
				title: '已自动填充'
			};
			arr.push(obj);
		}

		arr.forEach((item, index, arr) => {
			if (index != (arr.length - 1)) {
				let obj = {
					title: '|'
				};
				arr.push(obj);
			}
		})
		return arr;
	}

	const eventFn = (type : any) => {
	}

	// 获取当前时间的HH:mm格式
	const getCurrentTime = () => {
		const now = new Date();
		const hours = String(now.getHours()).padStart(2, '0');
		const minutes = String(now.getMinutes()).padStart(2, '0');
		return `${hours}:${minutes}`;
	}

	// 双向绑定的时间值
	const timeValue = ref<string>('');

	const initFn = () => {
		let time = '';

		// 如果已有值，则直接使用
		if (diyComponent.value.field.value) {
			time = diyComponent.value.field.value;
		}
		// 否则根据默认设置生成时间
		else if (diyComponent.value.defaultControl) {
			if (diyComponent.value.timeWay === 'current') {
				time = getCurrentTime();
			} else if (diyComponent.value.timeWay === 'diy') {
				// 假设diyComponent.value.field.default是HH:mm格式的字符串
				time = diyComponent.value.field.default || getCurrentTime();
			}
		}

		// 更新组件值
		diyComponent.value.field.value = time;
		timeValue.value = time;
	}

	initFn();

	const warpCss = computed(() => {
		var style = '';
		style += 'position:relative;';
		if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${diyComponent.value.componentGradientAngle},${diyComponent.value.componentStartBgColor},${diyComponent.value.componentEndBgColor});`;
		else if (diyComponent.value.componentStartBgColor) style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
		else if (diyComponent.value.componentEndBgColor) style += 'background-color:' + diyComponent.value.componentEndBgColor + ';';

		if (diyComponent.value.componentBgUrl) {
			style += `background-image:url('${img(diyComponent.value.componentBgUrl)}');`;
			style += 'background-size: cover;background-repeat: no-repeat;';
		}

		if (diyComponent.value.topRounded) style += 'border-top-left-radius:' + diyComponent.value.topRounded + 'px;';
		if (diyComponent.value.topRounded) style += 'border-top-right-radius:' + diyComponent.value.topRounded + 'px;';
		if (diyComponent.value.bottomRounded) style += 'border-bottom-left-radius:' + diyComponent.value.bottomRounded + 'px;';
		if (diyComponent.value.bottomRounded) style += 'border-bottom-right-radius:' + diyComponent.value.bottomRounded + 'px;';
		return style;
	})

	onMounted(() => {
		refresh();
	});

	const refresh = () => {
		// 初始化逻辑已移至initFn
	}

	// 表单组件验证
	const verify = () => {
		const res = { code: true, message: '' }
		if (diyComponent.value.field.required && !diyComponent.value.field.value) {
			res.code = false;
			res.message = `请选择${diyComponent.value.placeholder}`;
		}
		errorInfo.value = res;
		return res;
	}

	// 重置表单组件数据
	const reset = () => {
		diyComponent.value.field.value = '';
		timeValue.value = '';
	}

	// 时间选择确认
	const timeConfirm = (value : string) => {
		// value已经是HH:mm格式，直接赋值
		diyComponent.value.field.value = value;
		timeValue.value = value;
	}

	defineExpose({
		verify,
		reset
	})
</script>

<style lang="scss">
	@import '@/assets/styles/diy_form.scss';

	.text-overflow-ellipsis {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}

	.form-item-frame.form-time .el-input__wrapper {
		width: 450px !important;
	}
</style>