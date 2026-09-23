<template>
    <div>
        <div v-show="!loading" class="diy-template-wrap">
            <diy-group ref="diyGroupRef" :data="diyFormData" />
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue';
import diyGroup from '@/addon/components/diy/group/index.vue'
import { getFormRecord } from '@/app/api/diy_form';
import { deepClone } from '@/utils/common';

const props = defineProps(['record_id', 'completeLayout']);
const emits = defineEmits(['callback'])
const loading = ref(true);
const diyFormData: any = reactive({
    global: {},
    value: []
})

onMounted(() => {
    getFormRecord({
        record_id: props.record_id
    }).then((res: any) => {
        diyFormData.global.completeLayout = props.completeLayout || 'style-1';
        let formData = {}
        if (res.data.recordsFieldList) {
            res.data.recordsFieldList.forEach((item: any) => {
                formData[item.field_key] = item
            })
        }
        if (res.data.diy_component_list) {
            let diyComponentList = deepClone(res.data.diy_component_list)
            diyComponentList = diyComponentList.filter((item: any, index: any, arr: any) => {
                if(formData[item.id]){
                    item.field.value = formData[item.id].handle_field_value
                    item.field.privacyProtection = formData[item.id].privacy_protection
                    item.field.unique = formData[item.id].field_unique
                    item.viewFormDetail = true // 查看表单详情标识
                }
                if (item.componentType == 'diy_form' && item.componentName != 'FormSubmit' && item.field.value && Object.keys(item.field.value).length) {
                    return item
                }
            })
            diyFormData.value = deepClone(diyComponentList)
        }
        emits('callback', res.data.recordsFieldList)
        loading.value = false;
    }).catch(() => {
        loading.value = false;
        emits('callback', [])
    })
})
</script>
