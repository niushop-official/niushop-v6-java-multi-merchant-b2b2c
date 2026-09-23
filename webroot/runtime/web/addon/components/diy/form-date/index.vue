<template>
    <div v-if="diyComponent.viewFormDetail" class="form-item-frame">
        <div class="base-layout-two detail">
            <div class="detail-two-content">
                <span class="detail-two-content-label">{{ diyComponent.field.name }}</span>
                <div class="detail-two-content-value w-[80%]">
                    <span>{{ diyComponent.field.value.date || t('notHave') }}</span>
                </div>
            </div>
        </div>
    </div>
    <div v-else :style="warpCss" class="form-item-frame form-date">
        <div class="base-layout-two">
            <div class="layout-two-wrap" :class="{'no-border': !diyGlobal.borderControl}">
                <div class="layout-two-label">
                    <span class="required" v-if="diyComponent.field.required">{{ diyComponent.field.required ? '*' : '' }}</span>
                    <span class="name">{{ diyComponent.field.name }}</span>
                </div>
                <div class="layout-two-content">
                    <el-date-picker v-if="diyComponent.dateFormat != 'YYYY-MM-DD HH:mm'" v-model="diyComponent.field.value.timestamp" type="date" :placeholder="diyComponent.placeholder" :format="diyComponent.dateFormat" @change="confirm" />
                    <el-date-picker v-else v-model="diyComponent.field.value.timestamp" type="datetime" :placeholder="diyComponent.placeholder" :format="diyComponent.dateFormat" @change="confirm" />
                </div>
            </div>
            <div class="layout-two-error-message" v-if="errorInfo && !errorInfo.code">{{ errorInfo.message }}</div>
            <div v-if="diyComponent.field.remark.text" class="layout-two-remark"
                  :style="{ color: diyComponent.field.remark.color, fontSize: diyComponent.field.remark.fontSize + 'px' }">
                {{ diyComponent.field.remark.text }}
            </div>
            <div class="layout-two-attribute-wrap" v-if="inputAttribute().length">
                <div v-for="(item,index) in inputAttribute()" :key="index" @click="eventFn(item.type)" class="layout-two-attribute-item">{{ item.title }}</div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
// 表单 日期组件
import { ref, computed, watch, onMounted, nextTick} from 'vue';
import { img, timeTurnTimeStamp } from '@/utils/common';


let currentDate = new Date();
currentDate.setFullYear(currentDate.getFullYear() + 1);
const props = defineProps(['component', 'index', 'global']);

const errorInfo: any = ref(null);

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

const eventFn = (type: any) => {
}

const getDateFn = (data: any = '', type: any = diyComponent.value.dateFormat) => {
    let date = data ? new Date(data * 1000) : new Date();
    let year = date.getFullYear();
    let month = String(date.getMonth() + 1).padStart(2, '0');
    let day = String(date.getDate()).padStart(2, '0');

    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    let str = '';
    if (type == 'YYYY年M月D日') {
        str = `${ year }年${ month }月${ day }日`;
    } else if (type == 'YYYY-MM-DD') {
        str = `${ year }-${ month }-${ day }`;
    } else if (type == 'YYYY/MM/DD') {
        str = `${ year }/${ month }/${ day }`;
    } else if (type == 'YYYY-MM-DD HH:mm') {
        str = `${ year }-${ month }-${ day } ${ hours }:${ minutes }`;
    }
    return str;
}


const initFn = () =>{
    nextTick(() => {
        let date = ''
        if (diyComponent.value.field.value.date) {
            let timestamp = diyComponent.value.field.value.timestamp;
            date = getDateFn(timestamp/1000);

            diyComponent.value.field.value.date = date;
            diyComponent.value.field.value.timestamp = timeTurnTimeStamp(date);
        } else {
            if (diyComponent.value.defaultControl) {
                if (diyComponent.value.dateWay == 'current') {
                    date = getDateFn();
                } else if (diyComponent.value.dateWay == 'diy') {
                    let timestamp = diyComponent.value.field.default.timestamp || '';
                    date = getDateFn(timestamp/1000);
                }

                diyComponent.value.field.value.date = date;
                diyComponent.value.field.value.timestamp = timeTurnTimeStamp(date);
            }
        } 
    })
}
initFn()

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
    // todo 开始、结束时间给予默认值
    // {{ diyComponent.defaultControl ? diyComponent.value.field.value.date : diyComponent.placeholder }}
}

// 表单组件验证
const verify = () => {
    const res = { code: true, message: '' }
    if (diyComponent.value.field.required && (!diyComponent.value.field.value || !diyComponent.value.field.value.timestamp)) {
        res.code = false
        res.message = `请选择${ diyComponent.value.placeholder }`;
    }
    errorInfo.value = res;
    return res;
}

// 重置表单组件数据
const reset = () => {
    diyComponent.value.field.value.date = '';
    diyComponent.value.field.value.timestamp = '';
}

const confirm = (e) => {
    diyComponent.value.field.value.timestamp = timeTurnTimeStamp(e); // todo 需要赋值
    diyComponent.value.field.value.date = getDateFn(diyComponent.value.field.value.timestamp/1000);
}
defineExpose({
    verify,
    reset
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';
</style>

<style lang="scss">
.form-item-frame.form-date .el-input__wrapper {
   width: 450px !important;
}
</style>
