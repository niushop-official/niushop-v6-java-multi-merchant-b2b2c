<template>
    <div v-if="diyComponent.viewFormDetail" class="form-item-frame">
        <div class="base-layout-two detail">
            <div class="detail-two-content">
                <div class="detail-two-content-label">{{ diyComponent.field.name }}</div>
                <div class="detail-two-content-value flex w-[80%] justify-end">
                    <div v-for="(item,index) in diyComponent.field.value" :key="index">
                        <span>{{ item.text }}</span>
                        <span v-if="index !== diyComponent.field.value.length - 1">、</span>
                    </div>
                    <span v-if="!diyComponent.field.value || !diyComponent.field.value.length">{{ t('notHave') }}</span>
                </div>
            </div>
        </div>
    </div>
    <div v-else :style="warpCss" class="form-item-frame">
        <div class="base-layout-two">
            <div class="layout-two-wrap" :class="{'!pb-[10px]': ((diyComponent.style == 'style-2' || diyComponent.style == 'style-3') && diyGlobal.borderControl),'no-border': !diyGlobal.borderControl}">
                <div class="layout-two-label">
                    <span class="required" v-if="diyComponent.field.required">{{ diyComponent.field.required ? '*' : '' }}</span>
                    <span class="name">{{ diyComponent.field.name }}</span>
                </div>
                <div class="layout-two-content" v-if="diyComponent.style == 'style-1'">
                    <el-checkbox-group v-model="selectValue" @change="checkboxChange" class="flex">
                        <div v-for="(item, index) in diyComponent.options" :key="index" :class="{'ml-[15px]': index}">
                            <el-checkbox el-checkbox:key="index" :label="item.id">{{ item.text }}</el-checkbox>
                        </div>
                    </el-checkbox-group>
                </div>

                <div class="layout-two-content" v-if="diyComponent.style == 'style-2'">
                    <div class="justify-end w-full">
                        <el-checkbox-group v-model="selectValue" placement="column" @change="checkboxChange" iconPlacement="left">
                            <div v-for="(item, index) in diyComponent.options" :key="index" @click="selectRadio(item)" class="border-solid border-[1px] border-[#e6e6e6] rounded-[5px] flex items-center h-[40px] mb-[8px] px-[8px] box-border !w-[200px] cursor-pointer"  :class="{'!mb-[0]': diyComponent.options.length == (index + 1)}">
                                <el-checkbox class="!m-[0]" :label="item.id">{{ item.text }}</el-checkbox>
                            </div>
                        </el-checkbox-group>
                    </div>
                </div>

                <div class="layout-two-content" v-if="diyComponent.style == 'style-3'">
                    <el-select v-model="selectValue" :placeholder="checkboxPlaceholder" class="w-[250px]"  :style="{'font-size': diyComponent.fontSize + 'px'}" multiple @change="pullDownConfirmFn">
                        <el-option v-for="item in diyComponent.options" :key="item.value" :style="{'font-size': diyComponent.fontSize + 'px'}" :label="item.text" :value="item.id" />
                    </el-select>
                </div>
            </div>
            <div class="layout-two-error-message" v-if="errorInfo && !errorInfo.code">{{ errorInfo.message }}</div>
            <div v-if="diyComponent.field.remark.text" class="layout-two-remark" :style="{ color: diyComponent.field.remark.color, fontSize: diyComponent.field.remark.fontSize + 'px' }">{{ diyComponent.field.remark.text }}</div>
            <div class="layout-two-attribute-wrap" v-if="inputAttribute().length">
                <div v-for="(item,index) in inputAttribute()" :key="index" @click="eventFn(item.type)" class="layout-two-attribute-item">{{ item.title }}</div>
            </div>
        </div>
        
        <!-- 隐私弹窗 -->
        <form-privacy-pop ref="formPrivacyRef" :data="formPrivacyData" />
    </div>
</template>

<script setup lang="ts">
// 表单 多选项组件
import { ref, computed, watch, onMounted } from 'vue';
import { img } from '@/utils/common';

import formPrivacyPop from './../form-privacy-pop/index.vue';

const props = defineProps(['component', 'index', 'global']);
const selectValue = ref([])

const errorInfo: any = ref(null);
const formPrivacyRef: any = ref(null);

// diyComponent.isShowArrow 只有个人资料进行使用
const diyComponent = computed(() => {
    return props.component;
})

const diyGlobal = computed(() => {
    return props.global;
})

const formPrivacyData = computed(() => {
    let str = `${ diyComponent.value.field.name }已开启隐私保护，提交后会部分打码，只有你自己和管理员才能查看完整信息`;
    return str;
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
    if (diyComponent.value.field.value?.length) {
        selectValue.value = diyComponent.value.field.value.map(item => item.id);
    }
});

const refresh = () => {
    // console.log('diyComponent.value.field.value',diyComponent.value.field.value)
}

const checkboxPlaceholder = computed(() => {
    let str = '';
    str += `请选择${ diyComponent.value.field.name }`
    return str;
})

// 表单组件验证
const verify = () => {
    const res = { code: true, message: '' }
    if (diyComponent.value.field.required && diyComponent.value.field.value.length == '') {
        res.code = false
        res.message = checkboxPlaceholder.value;
    }
    errorInfo.value = res;
    return res;
}
const backupValue = ref([]);

// 重置表单组件数据
const reset = () => {
    selectValue.value = [];
    diyComponent.value.field.value = [];
}

const pullDownConfirmFn = () => {
    const selectedItems = diyComponent.value.options.filter(item => selectValue.value.includes(item.id));
    diyComponent.value.field.value = selectedItems.map(item => ({ id: item.id, text: item.text }));
}


// 保存数据到 store
const checkboxChange = (value: any) => {
    const selectedItems = diyComponent.value.options.filter(item => value.includes(item.id));
    diyComponent.value.field.value = selectedItems.map(item => ({ id: item.id, text: item.text }));
};
const selectRadio = (item) => {
    const itemId = item.id;
    const index = selectValue.value.indexOf(itemId);
    if (index > -1) {
        selectValue.value.splice(index, 1);
    } else {
        selectValue.value.push(itemId);
    }
    const selectedItems = diyComponent.value.options.filter(item => selectValue.value.includes(item.id));

    diyComponent.value.field.value = selectedItems.map(item => ({ id: item.id, text: item.text }));
};

defineExpose({
    verify,
    reset
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';

.pull-down-arrow {
    transition: all .3s;
    transform: rotate(0);

    &.selected {
        transform: rotate(180deg);
    }
}

.form-item-frame :deep(.el-checkbox .el-checkbox__icon-wrap) {
    width: 15px !important;
    height: 15px !important;
}
</style>
