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
    <div v-else :style="warpCss" class="form-item-frame">
        <div class="base-layout-two">
            <div class="layout-two-wrap" :class="{'no-border': !diyGlobal.borderControl}">
                <div class="layout-two-label">
                    <span class="required" v-if="diyComponent.field.required">{{ diyComponent.field.required ? '*' : '' }}</span>
                    <span class="name">{{ diyComponent.field.name }}</span>
                </div>
                <div class="layout-two-content">
                    <el-input type="number" v-model="diyComponent.field.value" class="!w-[450px]" :placeholder="inputPlaceholder"/>
                    <span class="text-[#999] ml-[5px] pt-[1px] text-[14px]" v-if="diyComponent.unit">{{ diyComponent.unit }}</span>
                </div>
            </div>
            <div class="layout-two-error-message" v-if="errorInfo && !errorInfo.code">{{ errorInfo.message }}</div>
            <div v-if="diyComponent.field.remark.text" class="layout-two-remark" :style="{ color: diyComponent.field.remark.color, fontSize: diyComponent.field.remark.fontSize + 'px' }">{{ diyComponent.field.remark.text }}</div>
            <div class="layout-two-attribute-wrap" v-if="inputAttribute().length">
                <div v-for="(item,index) in inputAttribute()" :key="index" @click="eventFn(item.type)" class="layout-two-attribute-item">{{ item.title }}</div>
            </div>
        </div>
        <form-privacy-pop ref="formPrivacyRef" :data="formPrivacyData" />
    </div>
</template>

<script setup lang="ts">
// 表单 数字组件
import { ref, computed, watch, onMounted } from 'vue';
import { img } from '@/utils/common';

import formPrivacyPop from './../form-privacy-pop/index.vue';

const props = defineProps(['component', 'index', 'global']);
const errorInfo: any = ref(null);
const formPrivacyRef: any = ref(null);

const diyComponent = computed(() => {
    return props.component;
})

const diyGlobal = computed(() => {
    return props.global;
})

const inputPlaceholder = computed(() => {
    let str = '';
    str += diyComponent.value.placeholder
    return str;
})

const formPrivacyData = computed(() => {
    let str = `${ diyComponent.value.field.name }已开启隐私保护，提交后会部分打码，只有你自己和管理员才能查看完整信息`;
    return str;
})

const inputAttribute = () => {
    let arr = [];
    if (diyComponent.value.autofill) {
        let obj = {
            title: '已自动填充'
        };
        arr.push(obj);
    }
    if (diyComponent.value.field.privacyProtection) {
        let obj = {
            title: '已开启隐私保护',
            type: 'privacy'
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
    if (type == 'privacy') {
        // 查看隐私
        formPrivacyRef.value.open();
    }
}

const warpCss = computed(() => {
    var style = '';
    style += 'position:relative;';
    if (diyComponent.value.componentStartBgColor && diyComponent.value.componentEndBgColor) style += `background:linear-gradient(${ diyComponent.value.componentGradientAngle },${ diyComponent.value.componentStartBgColor },${ diyComponent.value.componentEndBgColor });`;
    else if (diyComponent.value.componentStartBgColor) style += 'background-color:' + diyComponent.value.componentStartBgColor + ';';
    else if (diyComponent.value.componentEndBgColor) style += 'background-color:' + diyComponent.value.componentEndBgColor + ';';

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

const refresh = () => {
    // 实际展示，优先缓存，其次默认值
    if (diyComponent.value.field.value == '' && diyComponent.value.field.default) {
        diyComponent.value.field.value = diyComponent.value.field.default;
    }
}

// 表单组件验证
const verify = () => {
    const res = { code: true, message: '' }
    if (diyComponent.value.field.required && diyComponent.value.field.value == '') {
        res.code = false
        res.message = `${ inputPlaceholder.value }`;
    }
    errorInfo.value = res;
    return res;
}

// 重置表单组件数据
const reset = () => {
    diyComponent.value.field.value = '';
}



defineExpose({
    verify,
    reset
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';
</style>
