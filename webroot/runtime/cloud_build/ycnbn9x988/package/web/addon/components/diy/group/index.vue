<template>
    <div>
        <div v-for="(component, index) in props.data.value" :key="component.id">
            <template v-if="component.componentName == 'FormSubmit'">
                FormSubmit
                <!-- <diy-form-submit :component="component" :global="data.global" :index="index" /> -->
            </template>
            <template v-if="component.componentName == 'FormInput'">
                <diy-form-input ref="diyFormInputRef" :component="component" :global="props.data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormTextarea'">
                <diy-form-textarea ref="diyFormTextareaRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormIdentity'">
                <diy-form-identity ref="diyFormIdentityRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormEmail'">
                <diy-form-email ref="diyFormEmailRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormMobile'">
                <diy-form-mobile ref="diyFormMobileRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormNumber'">
                <diy-form-number ref="diyFormNumberRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormRadio'">
                <diy-form-radio ref="diyFormRadioRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormCheckbox'">
                <diy-form-checkbox ref="diyFormCheckboxRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormDate'">
                <diy-form-date ref="diyFormDateRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormDateScope'">
                <diy-form-date-scope ref="diyFormDateScopeRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormTime'">
                <diy-form-time ref="diyFormTimeRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormTimeScope'">
                <diy-form-time-scope ref="diyFormTimeScopeRef" :component="component" :global="data.global" :index="index" />
            </template>
            <template v-if="component.componentName == 'FormImage'">
                <diy-form-image ref="diyFormImageRef" :component="component" :global="data.global" :index="index" />
            </template>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { ref, getCurrentInstance } from 'vue';
import { useDiyForm } from '@/addon/components/diy/group/useDiyForm'
import { useRouter, useRoute } from 'vue-router'
import { deepClone, getValidTime } from '@/utils/common'
import storage from '@/utils/storage'
import diyGroup from '@/addon/components/diy/group/index.vue'
import { ElMessage } from 'element-plus'
import diyFormInput from '@/addon/components/diy/form-input/index.vue'
import diyFormTextarea from '@/addon/components/diy/form-textarea/index.vue'
import diyFormNumber from '@/addon/components/diy/form-number/index.vue'
import diyFormRadio from '@/addon/components/diy/form-radio/index.vue'
import diyFormCheckbox from '@/addon/components/diy/form-checkbox/index.vue'
import diyFormMobile from '@/addon/components/diy/form-mobile/index.vue'
import diyFormEmail from '@/addon/components/diy/form-email/index.vue'
import diyFormIdentity from '@/addon/components/diy/form-identity/index.vue'
import diyFormDate from '@/addon/components/diy/form-date/index.vue'
import diyFormDateScope from '@/addon/components/diy/form-date-scope/index.vue'
import diyFormTime from '@/addon/components/diy/form-time/index.vue'
import diyFormTimeScope from '@/addon/components/diy/form-time-scope/index.vue'
import diyFormImage from '@/addon/components/diy/form-image/index.vue'

const props = defineProps(['data','pullDownRefreshCount']);
const instance: any = getCurrentInstance();
    const getFormRef = () => {
        return {
            componentRefs: instance.refs
        }
    }

defineExpose({
       refresh: diyGroup.refresh,
       getFormRef
   })
</script>

<style lang="scss">
</style>
