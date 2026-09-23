<template>
    <div v-if="diyComponent.viewFormDetail" class="form-item-frame">
        <div class="base-layout-two detail">
            <div class="detail-two-content" v-if="diyComponent.field.value.start.date || diyComponent.field.value.end.date">
                <span class="detail-two-content-label">{{ diyComponent.field.name }}</span>
                <div class="detail-two-content-value w-[80%]">
                    <span>{{ diyComponent.field.value.start.date }}</span>
                    <span class="mx-[5px]" v-if="diyComponent.field.value.start.date && diyComponent.field.value.end.date">-</span>
                    <span>{{ diyComponent.field.value.end.date }}</span>
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
                <div class="layout-two-content !w-[450px]">
                    <el-date-picker v-model="dateArr" type="datetimerange" range-separator="-"
                    :start-placeholder="diyComponent.start.placeholder" :end-placeholder="diyComponent.end.placeholder" :format="diyComponent.dateFormat" @change="confirm"/>
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
// 表单 日期范围组件
import { ref, computed, watch, onMounted } from 'vue';
import { img, timeTurnTimeStamp } from '@/utils/common';


const props = defineProps(['component', 'index', 'global']);

const errorInfo: any = ref(null);

const diyComponent = computed(() => {
    return props.component;
})

const diyGlobal = computed(() => {
    return props.global;
})
const dateArr = ref([]);

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

const getDateFn = (res: any = '', type: any = diyComponent.value.dateFormat) => {
    let date = res ? new Date(res * 1000) : new Date();
    let year = date.getFullYear();
    let month = String(date.getMonth() + 1).padStart(2, '0');
    let day = String(date.getDate()).padStart(2, '0');

    let str = '';
    if (type == 'YYYY年M月D日') {
        str = `${ year }年${ month }月${ day }日`;
    } else if (type == 'YYYY-MM-DD') {
        str = `${ year }-${ month }-${ day }`;
    } else if (type == 'YYYY/MM/DD') {
        str = `${ year }/${ month }/${ day }`;
    }
    return str;
}

const initFn = () => {
    // 开始日期
    let startDate = '';
    if (diyComponent.value.field.value.start.date) {
        let timestamp = diyComponent.value.field.value.start.timestamp;
        startDate = getDateFn(timestamp);
    } else {
        if (diyComponent.value.start.defaultControl) {
            if (diyComponent.value.start.dateWay == 'current') {
                startDate = getDateFn();
            } else if (diyComponent.value.start.dateWay == 'diy') {
                let timestamp = diyComponent.value.field.default.start.timestamp ? diyComponent.value.field.default.start.timestamp : '';
                startDate = getDateFn(timestamp);
            }
        } else {
            startDate = '';
        }
    }
    diyComponent.value.field.value.start.date = startDate;
    diyComponent.value.field.value.start.timestamp = startDate ? timeTurnTimeStamp(startDate) : 0;

    // 结束日期
    let endDate = '';
    if (diyComponent.value.field.value.end.date) {
        let timestamp = diyComponent.value.field.value.end.timestamp;
        endDate = getDateFn(timestamp);

        diyComponent.value.field.value.end.date = endDate;
        diyComponent.value.field.value.end.timestamp = timeTurnTimeStamp(endDate);
    } else {
        if (diyComponent.value.end.defaultControl) {
            if (diyComponent.value.end.dateWay == 'current') {
                endDate = getDateFn();
            } else if (diyComponent.value.end.dateWay == 'diy') {
                endDate = getDateFn(diyComponent.value.field.default.end.timestamp);
            }
            diyComponent.value.field.value.end.date = endDate;
            diyComponent.value.field.value.end.timestamp = timeTurnTimeStamp(endDate);
        } else {
            endDate = '';
        }
    }
    dateArr.value = [timeTurnTimeStamp(startDate), timeTurnTimeStamp(endDate)]
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
    // 装修模式下刷新
    
});

const refresh = () => {
    // todo 开始、结束时间给予默认值
    // {{ diyComponent.start.defaultControl ? diyComponent.field.value.start.date : diyComponent.start.placeholder }}
}

// 表单组件验证
const verify = () => {
    const res = { code: true, message: '' }
    if (diyComponent.value.field.required) {
        if (diyComponent.value.field.value.start.date == '' || !diyComponent.value.field.value.start.timestamp) {
            res.code = false
            res.message = `请选择${ diyComponent.value.start.placeholder }`;
        } else if (diyComponent.value.field.value.end.date == '' || !diyComponent.value.field.value.end.timestamp) {
            res.code = false
            res.message = `请选择${ diyComponent.value.end.placeholder }`;
        }
    }

    errorInfo.value = res;
    return res;
}

// 重置表单组件数据
const reset = () => {
    diyComponent.value.field.value.start.date = '';
    diyComponent.value.field.value.start.timestamp = 0;
    diyComponent.value.field.value.end.date = '';
    diyComponent.value.field.value.end.timestamp = 0;
    dateArr.value = [];
}

const confirm = (e: any) => {
    diyComponent.value.field.value.start.timestamp = timeTurnTimeStamp(e[0])/1000; // todo 需要赋值
    diyComponent.value.field.value.start.date = getDateFn(diyComponent.value.field.value.start.timestamp);
    diyComponent.value.field.value.end.timestamp = timeTurnTimeStamp(e[e.length - 1])/1000; // todo 需要赋值
    diyComponent.value.field.value.end.date = getDateFn(diyComponent.value.field.value.end.timestamp);
    dateArr.value = [timeTurnTimeStamp(e[0]), timeTurnTimeStamp(e[e.length - 1])];
}

defineExpose({
    verify,
    reset
})
</script>

<style lang="scss" scoped>
@import '@/assets/styles/diy_form.scss';

.text-overflow-ellipsis {
    text-overflow: ellipsis;
    overflow: hidden;
    white-space: nowrap;
    display: inline-block;
}
</style>
<style lang="scss">
.calendar-wrap .el-calendar .el-calendar-header__title, .el-calendar-header__subtitle, .el-calendar-month__title {
    display: block;
}
</style>
