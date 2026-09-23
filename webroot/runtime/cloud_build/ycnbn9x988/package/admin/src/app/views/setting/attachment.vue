<template>
	<div class="main-container bg-[#fff]">
		<div class="flex ml-[18px] justify-between items-center pt-[20px]">
			<span class="text-page-title">{{ pageName }}</span>
		</div>
		<el-card class="box-card !border-none min-h-[420px]" shadow="never" v-loading="loading">
			<el-form :model="formData" label-width="120px" ref="formRef" :rules="rules" class="page-form" v-if="!loading">
				<el-form-item :label="t('图片类型')" prop="image_ext" required="true">
					<el-checkbox-group v-model="formData.image_ext">
						<el-checkbox :label="item" v-for="(item, index) in imageExtArr" :key="'a' + index">{{ item }}</el-checkbox>
					</el-checkbox-group>
				</el-form-item>
				<el-form-item :label="t('图片大小')" prop="min">
					<el-input v-model="formData.image_size" @input="validateNumber($event, 'image_size')" class="input-mid-width">
						<template #append>KB</template>
					</el-input>
					<div class="form-tip">默认为 1024 KB</div>
				</el-form-item>
				<el-form-item :label="t('大图')" prop="thumb.thumb_type.big" v-if="imageTypeArr.indexOf('big') != -1">
					<el-input
						v-model="formData.thumb.thumb_type.big.width"
						@input="validateNumber($event, 'thumb.thumb_type.big.width')"
						class="input-mid-width mr-[20px]"
					>
						<template #prepend>宽</template>
						<template #append>px</template>
					</el-input>
					<el-input v-model="formData.thumb.thumb_type.big.height" @input="validateNumber($event, 'thumb.thumb_type.big.height')" class="input-mid-width">
						<template #prepend>高</template>
						<template #append>px</template>
					</el-input>
				</el-form-item>
				<el-form-item :label="t('中图')" prop="thumb.thumb_type.mid" v-if="imageTypeArr.indexOf('mid') != -1">
					<el-input
						v-model="formData.thumb.thumb_type.mid.width"
						@input="validateNumber($event, 'thumb.thumb_type.mid.width')"
						class="input-mid-width mr-[20px]"
					>
						<template #prepend>宽</template>
						<template #append>px</template>
					</el-input>
					<el-input v-model="formData.thumb.thumb_type.mid.height" @input="validateNumber($event, 'thumb.thumb_type.mid.height')" class="input-mid-width">
						<template #prepend>高</template>
						<template #append>px</template>
					</el-input>
				</el-form-item>
				<el-form-item :label="t('小图')" prop="thumb.thumb_type.small" v-if="imageTypeArr.indexOf('small') != -1">
					<el-input
						v-model="formData.thumb.thumb_type.small.width"
						@input="validateNumber($event, 'thumb.thumb_type.small.width')"
						class="input-mid-width mr-[20px]"
					>
						<template #prepend>宽</template>
						<template #append>px</template>
					</el-input>
					<el-input v-model="formData.thumb.thumb_type.small.height" @input="validateNumber($event, 'thumb.thumb_type.small.height')" class="input-mid-width">
						<template #prepend>高</template>
						<template #append>px</template>
					</el-input>
				</el-form-item>
				<!-- <el-form-item :label="t('文件类型')" prop="document_ext" required="true">
                    <el-checkbox-group v-model="formData.document_ext">
                        <el-checkbox :label="item" v-for="(item, index) in documentExtArr" :key="'a' + index">{{ item }}</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>

                <el-form-item :label="t('文件大小')">
                    <el-input v-model="formData.document_size" @input="validateNumber($event,'document_size')" class="input-mid-width">
                        <template #append>KB</template>
                    </el-input>
                    <div class="form-tip">默认为 2048 KB</div>
                </el-form-item> -->
				<el-form-item :label="t('视频类型')" prop="video_ext" required="true">
					<el-checkbox-group v-model="formData.video_ext">
						<el-checkbox :label="item" v-for="(item, index) in videoExtArr" :key="'a' + index">{{ item }}</el-checkbox>
					</el-checkbox-group>
				</el-form-item>
				<el-form-item :label="t('视频大小')">
					<el-input v-model="formData.video_size" @input="validateNumber($event, 'video_size')" class="input-mid-width">
						<template #append>KB</template>
					</el-input>
					<div class="form-tip">默认为 10240 KB</div>
				</el-form-item>
			</el-form>
		</el-card>
		<div class="fixed-footer-wrap">
			<div class="fixed-footer">
				<el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { FormInstance, FormRules } from 'element-plus'
import { useRoute } from 'vue-router'
import { filterDigit } from '@/utils/common'
import { getExtOfType, getUploadConfig, setUploadConfig, getThumbType } from '@/app/api/sys'

const route = useRoute()
const pageName = route.meta.title

const loading = ref(true)
const formRef = ref<FormInstance>()

const formData = reactive<Record<string, any>>({
	image_ext: [],
	image_size: 0,
	video_ext: [],
	video_size: 0,
	thumb: {
		thumb_type: {
			big: {
				width: 0,
				height: 0,
			},
			mid: {
				width: 0,
				height: 0,
			},
			small: {
				width: 0,
				height: 0,
			},
		},
	},
	document_ext: [],
	document_size: 0,
})
const imageExtArr = ref([])
const imageTypeArr = ref([])
const documentExtArr = ref([])
const videoExtArr = ref([])

const rules: FormRules = {
	image_ext: [
		{
			trigger: 'change',
			validator: (rule: any, value: any, callback: any) => {
				if (!Array.isArray(value) || value.length === 0) callback(new Error('请选择图片类型'))
				else callback()
			},
		},
	],
	document_ext: [
		{
			trigger: 'change',
			validator: (rule: any, value: any, callback: any) => {
				if (!Array.isArray(value) || value.length === 0) callback(new Error('请选择文件类型'))
				else callback()
			},
		},
	],
	video_ext: [
		{
			trigger: 'change',
			validator: (rule: any, value: any, callback: any) => {
				if (!Array.isArray(value) || value.length === 0) callback(new Error('请选择视频类型'))
				else callback()
			},
		},
	],
}

getExtOfType().then((res) => {
	if (res?.data) {
		documentExtArr.value = res.data.document
		videoExtArr.value = res.data.video
		imageExtArr.value = res.data.image
	}
})
getThumbType().then((res) => {
	if (res?.data) imageTypeArr.value = res.data
	console.log('getThumbType', imageTypeArr.value)
})

const getFormData = async () => {
	loading.value = true
	try {
		const data = await (await getUploadConfig()).data
		if (data?.image_ext) formData.image_ext = data.image_ext
		if (data?.image_size !== undefined) formData.image_size = Number(data.image_size / 1024).toFixed(2)
		if (data?.video_ext) formData.video_ext = data.video_ext
		if (data?.video_size !== undefined) formData.video_size = Number(data.video_size / 1024).toFixed(2)
		if (data?.document_ext) formData.document_ext = data.document_ext
		if (data?.document_size !== undefined) formData.document_size = Number(data.document_size / 1024).toFixed(2)
		if (data?.thumb) formData.thumb = typeof data.thumb == 'string' ? JSON.parse(data.thumb) : data.thumb
	} finally {
		loading.value = false
	}
}
getFormData()

const onSave = async (formEl: FormInstance | undefined) => {
	if (loading.value || !formEl) return

	await formEl.validate(async (valid) => {
		if (!valid) return
		loading.value = true
		console.log(formData)
		const saveData = {
			image_ext: formData.image_ext,
			image_size: Number(formData.image_size) * 1024,
			document_ext: formData.document_ext,
			document_size: Number(formData.document_size) * 1024,
			video_ext: formData.video_ext,
			video_size: Number(formData.video_size) * 1024,
			thumb: JSON.stringify(formData.thumb),
		}
		setUploadConfig(saveData)
			.then(() => {
				loading.value = false
			})
			.catch(() => {
				loading.value = false
			})
	})
}

const validateNumber = (event: any, data: any) => {
	// 正则：仅保留数字和一个小数点，去掉其他字符
	event = event.replace(/[^\d.]/g, '')
	// 确保只有一个小数点
	event = event.replace(/\.{2,}/g, '.')

	// 小数点后超过2位时先截断（避免输入过多无效字符）
	const dotIndex = event.indexOf('.')
	if (dotIndex > -1) {
		event = event.slice(0, dotIndex + 3)
	}
	setGlobalPathValue(data, event)
}

const setGlobalPathValue = (fullPath, value) => {
	const pathArr = fullPath.split('.')
	let current = formData

	// 定位到父对象
	const lastKey = pathArr.pop()
	pathArr.forEach((key) => {
		if (!current[key] || typeof current[key] !== 'object') {
			current[key] = {}
		}
		current = current[key]
	})

	current[lastKey] = value
}
</script>

<style lang="scss" scoped></style>
