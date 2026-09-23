<template>
    <div v-if="diyComponent.viewFormDetail" class="form-item-frame">
        <div class="base-layout-two detail">
            <div class="detail-two-content">
                <span class="detail-two-content-label">{{ diyComponent.field.name }}</span>
                <div class="flex items-center w-[80%] justify-end">
                    <div class="flex flex-wrap justify-end gap-[5px]">
                        <div class="relative image-item w-[90px] !h-[90px] gap-[8px]" v-for="(item,index) in diyComponent.field.value" :key="index">
                            <el-image class="w-[100%] h-[100%]" :src="img(item)" :preview-src-list="[img(item)]" fit="fill" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div v-else :style="warpCss" class="form-item-frame">
        <div class="base-layout-two">
            <div class="layout-two-wrap" :class="{'no-border': !diyGlobal.borderControl}">
                <div class="layout-two-label">
                    <span class="required" v-if="diyComponent.field.required">{{ diyComponent.field.required ? '*' : '' }}</span>
                    <span class="name">{{ diyComponent.field.name }}</span>
                </div>
                <div class="layout-two-content flex-wrap gap-[8px]">
                    <div class="relative border-box w-[90px] !h-[90px] border-box border-[1px] border-solid border-[#e6e6e6] rounded-[5px] flex items-center"
                        v-for="(item,index) in diyComponent.field.value" :key="index">
                        <el-image class="w-[100%] h-[100%]" :src="img(item)" :preview-src-list="[img(item)]" fit="fill" />
                        <div class="absolute top-0 right-[0] bg-[#373737] flex justify-end h-[14px] w-[14px] rounded-bl-[20px] cursor-pointer" @click="deleteImage(index)">
                            <span class="iconfont icon-a-guanbi34 !text-[10px] mt-[1px] mr-[1px] text-[#fff]"></span>
                        </div>
                    </div>
                    <div class="items-start border-box border-[1px] border-solid border-[#e6e6e6] rounded-[5px]">
                        <el-upload class="avatar-uploader" :show-file-list="false" v-bind="upload" ref="uploadRef" v-if="diyComponent.field.value.length < Number(diyComponent.limit)" :limit="Number(diyComponent.limit)" multiple>
                            <div class="flex flex-col items-center justify-center min-w-[90px] min-h-[90px]">
                                <span class="iconfont !text-[20px] mb-[8px] icon-jiahao"></span>
                                <span class="text-[14px] ml-[5px] text-[12px]">上传</span>
                            </div>
                        </el-upload>
                    </div>
                </div>
            </div>
            <div class="layout-two-error-message" v-if="errorInfo && !errorInfo.code">{{ errorInfo.message }}</div>
            <div v-if="diyComponent.field.remark.text" class="layout-two-remark" :style="{ color: diyComponent.field.remark.color, fontSize: diyComponent.field.remark.fontSize + 'px' }">{{ diyComponent.field.remark.text }}</div>
            <div class="layout-two-attribute-wrap" v-if="inputAttribute().length">
                <div v-for="(item,index) in inputAttribute()" :key="index" @click="eventFn(item.type)" class="layout-two-attribute-item">{{ item.title }}</div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
// 表单 图片组件
import { ref, computed, watch, onMounted } from 'vue';
import { img } from '@/utils/common';
import { fetchImage } from '@/app/api/system'
import { ElMessage, ElMessageBox, UploadFile, UploadFiles } from 'element-plus'
import request from '@/utils/request'
import storage from '@/utils/storage'
import { getToken } from '@/utils/common'
// uploadImage

const props = defineProps(['component', 'index', 'global']);
const errorInfo: any = ref(null);

const diyComponent = computed(() => {
    return props.component;
})

const diyGlobal = computed(() => {
    return props.global;
})

const uploadRef = ref(null)
const upload = computed(() => {
    const headers: Record<string, any> = {}
    headers.token = getToken()
    headers['site-id'] = storage.get('siteId') || 1
    return {
        action: `${request.options.baseURL}/file/image`,
        limit: 1,
        headers,
        onSuccess: (response: any, uploadFile: UploadFile, uploadFiles: UploadFiles) => {
            let img = uploadFile?.response?.data?.url;
            console.log('onSuccess - 上传', img, response)
            if (response.code == 200 || response.code == 1) {
                diyComponent.value.field.value.push(img)
            } else {
                uploadFile.status = 'fail'
                ElMessage({ message: response.msg, type: 'error' })
            }
        }
    }
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

const eventFn = (type: any) => {

}

const warpCss = computed(() => {
    var style = '';
    style += 'position:relative;';
    if (diyComponent.value.componentStartBgColor) {
        if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${ diyComponent.value.componentGradientAngle },${ diyComponent.value.componentStartBgColor },${ diyComponent.value.componentEndBgColor });`;
        else style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
    }

    if (diyComponent.value.componentBgUrl) {
        style += `background-image:url('${ img(diyComponent.value.componentBgUrl) }');`;
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


const deleteImage = (event) => {
    diyComponent.value.field.value.splice(event.index, 1)
}

const refresh = () => {
}

// 表单组件验证
const verify = () => {
    const res = { code: true, message: '' }
    // todo 验证组件，diyComponent.value.field.value
    if (diyComponent.value.field.required && (!diyComponent.value.field.value || diyComponent.value.field.value && !diyComponent.value.field.value.length)) {
        res.code = false
        res.message = `请上传图片`;
    } else if (diyComponent.value.field.value && diyComponent.value.field.value.length > Number(diyComponent.value.limit)) {
        res.code = false
        res.message = `图片上传数量已超出限制数量`;
    }
    errorInfo.value = res;
    return res;
}

// 重置表单组件数据
const reset = () => {
    // todo 清空组件数据，diyComponent.value.field.value = '';
    diyComponent.value.field.value = [];
}

defineExpose({
    verify,
    reset
})
</script>

<style lang="scss">
.layout-one-content .u-upload .u-upload__wrap > uni-view, .layout-one-content .u-upload .u-upload__wrap > view, .layout-one-content .u-upload .u-upload__wrap > div {
    width: 100%;
}
</style>
<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';
</style>
