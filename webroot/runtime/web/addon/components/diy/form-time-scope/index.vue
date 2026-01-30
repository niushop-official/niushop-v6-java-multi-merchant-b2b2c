<template>
	<div v-if="diyComponent.viewFormDetail" class="form-item-frame">
		<div class="base-layout-two detail">
			<div class="detail-two-content"
				v-if="diyComponent.field.value.start.date || diyComponent.field.value.end.date">
				<span class="detail-two-content-label">{{ diyComponent.field.name }}</span>
				<div class="detail-two-content-value w-[80%]">
					<span>{{ diyComponent.field.value.start.date }}</span>
					<span class="mx-[5px]"
						v-if="diyComponent.field.value.start.date && diyComponent.field.value.end.date">-</span>
					<span>{{ diyComponent.field.value.end.date }}</span>
				</div>
			</div>
		</div>
	</div>
	<div v-else class="form-item-frame">
		<div class="base-layout-two">
			<div class="layout-two-wrap" :class="{'no-border': !diyGlobal.borderControl}">
				<div class="layout-two-label">
					<span class="required"
						v-if="diyComponent.field.required">{{ diyComponent.field.required ? '*' : '' }}</span>
					<span class="name">{{ diyComponent.field.name }}</span>
				</div>
				<div class="layout-two-content !w-[450px]">
					<el-time-picker v-model="timeArr" is-range range-separator="-"
						:start-placeholder="diyComponent.start.placeholder"
						:end-placeholder="diyComponent.end.placeholder" @change="timeConfirm" format="HH:mm"
						value-format="HH:mm" />
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
// 表单 时间范围组件
import { ref, computed, watch, onMounted } from 'vue';
import { img, timeTurnTimeStamp } from '@/utils/common';

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

// 只获取当前时间的时分部分 (HH:mm)
const getTimeOnly = () => {
	const now = new Date();
	const hours = String(now.getHours()).padStart(2, '0');
	const minutes = String(now.getMinutes()).padStart(2, '0');
	return `${hours}:${minutes}`;
}

// 计算比给定时间晚10分钟的时间
const addTenMinutes = (timeStr : string) => {
	if (!timeStr) return '';
	const [hours, minutes] = timeStr.split(':').map(Number);
	const totalMinutes = hours * 60 + minutes + 10;
	const newHours = Math.floor(totalMinutes / 60) % 24;
	const newMinutes = totalMinutes % 60;
	return `${String(newHours).padStart(2, '0')}:${String(newMinutes).padStart(2, '0')}`;
}

// 转换时间字符串为时间戳(仅基于时间部分)
const timeStrToTimestamp = (timeStr : string) => {
	if (!timeStr) return 0;
	const [hours, minutes] = timeStr.split(':').map(Number);
	// 使用当天日期加上时间部分生成时间戳
	const date = new Date();
	date.setHours(hours, minutes, 0, 0);
	return date.getTime();
}

let timeArr = ref<string[]>([]);

const initFn = () => {
	let startTime = '';

	// 设置开始时间
	if (diyComponent.value.field.value.start.date) {
		startTime = diyComponent.value.field.value.start.date;
	} else {
		if (diyComponent.value.start.defaultControl) {
			if (diyComponent.value.start.timeWay === 'current') {
				startTime = getTimeOnly();
			} else if (diyComponent.value.start.timeWay === 'diy') {
				startTime = diyComponent.value.field.default.start.date || getTimeOnly();
			}
		}
	}

	// 设置结束时间(比开始时间晚10分钟)
	let endTime = '';
	if (diyComponent.value.field.value.end.date) {
		endTime = diyComponent.value.field.value.end.date;
	} else {
		if (diyComponent.value.end.defaultControl) {
			if (diyComponent.value.end.timeWay === 'current') {
				// 如果是当前时间，仍确保比开始时间晚10分钟
				endTime = addTenMinutes(startTime);
			} else if (diyComponent.value.end.timeWay === 'diy' && diyComponent.value.field.default.end.date) {
				endTime = diyComponent.value.field.default.end.date;
				// 确保结束时间大于开始时间
				if (timeStrToTimestamp(endTime) <= timeStrToTimestamp(startTime)) {
					endTime = addTenMinutes(startTime);
				}
			} else {
				// 默认比开始时间晚10分钟
				endTime = addTenMinutes(startTime);
			}
		}
	}

	// 更新组件数据
	diyComponent.value.field.value.start.date = startTime;
	diyComponent.value.field.value.start.timestamp = timeStrToTimestamp(startTime);

	diyComponent.value.field.value.end.date = endTime;
	diyComponent.value.field.value.end.timestamp = timeStrToTimestamp(endTime);

	timeArr.value = [startTime, endTime];
}

onMounted(() => {
	initFn();
});

// 表单验证
const verify = () => {
	const res = { code: true, message: '' }
	if (diyComponent.value.field.required && !diyComponent.value.field.value.start.date) {
		res.code = false;
		res.message = `请选择${diyComponent.value.start.placeholder}`;
	} else if (diyComponent.value.field.required && !diyComponent.value.field.value.end.date) {
		res.code = false;
		res.message = `请选择${diyComponent.value.end.placeholder}`;
	} else if (
		diyComponent.value.field.value.start.timestamp &&
		diyComponent.value.field.value.end.timestamp &&
		diyComponent.value.field.value.start.timestamp >= diyComponent.value.field.value.end.timestamp
	) {
		res.code = false;
		res.message = `开始时间不能大于等于结束时间`;
	}
	errorInfo.value = res;
	return res;
}

// 重置表单
const reset = () => {
	diyComponent.value.field.value.start.date = '';
	diyComponent.value.field.value.start.timestamp = 0;
	diyComponent.value.field.value.end.date = '';
	diyComponent.value.field.value.end.timestamp = 0;
	timeArr.value = [];
}

const timeConfirm = (values : string[]) => {
	if (!values || values.length < 2) return;

	const [startTime, endTime] = values;

	// 确保结束时间大于开始时间
	if (timeStrToTimestamp(endTime) <= timeStrToTimestamp(startTime)) {
		// 自动调整为开始时间+10分钟
		const adjustedEndTime = addTenMinutes(startTime);
		timeArr.value = [startTime, adjustedEndTime];
		diyComponent.value.field.value.end.date = adjustedEndTime;
		diyComponent.value.field.value.end.timestamp = timeStrToTimestamp(adjustedEndTime);
	} else {
		diyComponent.value.field.value.end.date = endTime;
		diyComponent.value.field.value.end.timestamp = timeStrToTimestamp(endTime);
	}

	// 更新开始时间
	diyComponent.value.field.value.start.date = startTime;
	diyComponent.value.field.value.start.timestamp = timeStrToTimestamp(startTime);
}

defineExpose({
	verify,
	reset
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';

.text-overflow-ellipsis {
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.form-item-frame :deep(.u-picker .u-toolbar__wrapper__cancel),
.form-item-frame :deep(.u-picker .u-toolbar__wrapper__confirm) {
	font-size: 14px;
}
</style>