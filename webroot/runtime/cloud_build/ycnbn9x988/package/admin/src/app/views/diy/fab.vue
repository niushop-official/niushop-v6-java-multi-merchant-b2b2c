<template>
    <div class="main-container">
        <el-card class="box-card !border-none min-h-[600px] bg-[#fff]" shadow="never" v-loading="loading">
            <div class="flex">
                <div class="w-[360px] h-[500px] absolute mr-[30px] border-[1px] border-gray-300 bg-[#333] opacity-50 overflow-hidden">
                    <div class="absolute right-[16px] bottom-[140px] p-[5px] rounded-[23px] fab-wrapper" v-if="fabNavsData.style.type == 1">
                        <div class="w-[35px] h-[35px] rounded-full bg-[#fff]" :class="{'mt-[8px]': index}" v-for="(item, index) in fabNavsData.navs" :key="index">
                            <el-image class="w-[35px] h-[35px] rounded-full" :src="img(item.icon)" fit="contain">
                                <template #error>
                                    <div class="w-[35px] h-[35px] flex justify-center items-center">
                                        <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                    </div>
                                </template>
                            </el-image>
                        </div>
                        <div class="w-[35px] h-[35px] rounded-full bg-[#fff] mt-[8px]">
                            <el-image v-if="fabNavsData.style.icon" class="w-[35px] h-[35px] rounded-full" :src="img(fabNavsData.style.icon)" fit="contain">
                                <template #error>
                                    <div class="w-[35px] h-[35px] flex justify-center items-center">
                                        <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                    </div>
                                </template>
                            </el-image>
                        </div>
                    </div>
                    <div class="absolute right-0 bottom-[140px] p-[5px] flex items-center rounded-l-[23px] fab-wrapper" v-if="fabNavsData.style.type == 2">
                        <div class="w-[35px] h-[35px] rounded-full bg-[#fff] mr-[8px]" v-for="(item, index) in fabNavsData.navs" :key="index">
                            <el-image class="w-[35px] h-[35px] rounded-full" :src="img(item.icon)" fit="contain">
                                <template #error>
                                    <div class="w-[35px] h-[35px] flex justify-center items-center">
                                        <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                    </div>
                                </template>
                            </el-image>
                        </div>
                        <span class="nc-iconfont nc-icon-youV6xx font-500 text-[#333]"></span>
                    </div>
                    <div class="absolute right-[10px] bottom-[140px] h-[149px]" v-if="fabNavsData.style.type == 3">
                        <div class="absolute right-0 top-[50%] translate-y-[-50%]">
                            <div>
                                <div class="absolute w-[35px] h-[35px] rounded-full bg-[#fff] fab-btn" v-for="(item, index) in fabNavsData.navs" :key="index">
                                    <el-image class="w-[35px] h-[35px] rounded-full" :src="img(item.icon)" fit="contain">
                                        <template #error>
                                            <div class="w-[35px] h-[35px] image-slot flex justify-center items-center">
                                                <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                            </div>
                                        </template>
                                    </el-image>
                                </div>
                            </div>
                            <div class="w-[35px] h-[35px] rounded-full bg-[#fff] shadow-[0_0_8px_0_rgba(0,0,0,0.1)]">
                                <el-image v-if="fabNavsData.style.icon" class="w-[35px] h-[35px] rounded-full" :src="img(fabNavsData.style.icon)" fit="contain">
                                    <template #error>
                                        <div class="w-[35px] h-[35px] flex justify-center items-center">
                                            <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                        </div>
                                    </template>
                                </el-image>
                            </div>
                        </div>
                    </div>
                    <div class="w-[175px] h-[175px] rounded-full fab-wrapper shadow-[0_0_7px_0_rgba(0,0,0,0.1)] absolute right-[-62px] bottom-[140px] p-[5px]" v-if="fabNavsData.style.type == 4">
                        <div class="absolute right-[70px] top-[50%] translate-y-[-50%]">
                            <div>
                                <div class="absolute w-[35px] h-[35px] rounded-full bg-[#fff] fab-style-btn shadow-[0_0_7px_0_rgba(0,0,0,0.1)]" v-for="(item, index) in fabNavsData.navs" :key="index">
                                    <el-image class="w-[35px] h-[35px] rounded-full" :src="img(item.icon)" fit="contain">
                                        <template #error>
                                            <div class="w-[35px] h-[35px] image-slot flex justify-center items-center">
                                                <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                            </div>
                                        </template>
                                    </el-image>
                                </div>
                            </div>
                            <div class="w-[35px] h-[35px] flex items-center justify-center rounded-full bg-[#fff] shadow-[0_0_8px_0_rgba(0,0,0,0.1)]">
                                <span class="nc-iconfont nc-icon-guanbiV6xx2 text-[#333]"></span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="flex-1 ml-[430px]">
                    <el-form :model="fabNavsData" label-width="100px" ref="formRef">
                        <el-tabs v-model="activeName" class="demo-tabs mt-[15px]">
                            <el-tab-pane :label="t('navImage')" name="navPicture">
                                <div ref="navItemRef">
                                    <div v-for="(item,index) in fabNavsData.navs" :key="'a'+index" :data-id="index"  class="item-wrap border-2 border-dashed pt-[18px] m-[10px] mb-[15px] relative list-item" :class="{ 'not-sort': useDrag }">
                                        <el-form-item :label="t('navIconOne')">
                                            <div class="flex align-center">
                                                <div class="flex flex-col justify-center items-center">
                                                    <upload-image v-model="item.icon" width="60px" height="60px" :limit="1" />
                                                    <span class="mr-[10px] text-sm">{{t('uploadImgUnselected')}}</span>
                                                </div>
                                            </div>
                                        </el-form-item>
                                        <el-form-item :label="t('navTitleOne')">
                                            <el-input class="!w-[215px]" v-model="item.text" :placeholder="t('titleContent')" maxlength="5" show-word-limit />
                                        </el-form-item>
                                        <el-form-item :label="t('navLinkOne')">
                                            <diy-link v-model="item.url" @confirm="diyLinkFn"/>
                                        </el-form-item>
                                        <el-icon class="close-icon cursor-pointer -top-[11px] -right-[8px]" @click="deleteNav(index)">
                                            <CircleCloseFilled />
                                        </el-icon>
                                    </div>
                                </div>

                                <el-button type="primary" class="mt-[15px]" v-show="fabNavsData.navs.length < 4" @click="addNav">{{ t('addnav') }}</el-button>
                            </el-tab-pane>
                            <el-tab-pane :label="t('styleSet')" name="setStyle">
                                <el-form-item :label="t('是否显示')">
                                    <el-radio-group v-model="fabNavsData.is_show" class="ml-4">
                                        <el-radio :label="true">是</el-radio>
                                        <el-radio :label="false">否</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item :label="t('导航类型')">
                                    <el-radio-group v-model="fabNavsData.style.type" class="ml-4">
                                        <el-radio :label="1">样式1</el-radio>
                                        <el-radio :label="2">样式2</el-radio>
                                        <el-radio :label="3">样式3</el-radio>
                                        <el-radio :label="4">样式4</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                                <el-form-item :label="t('主按钮')" v-if="fabNavsData.style.type != 4">
                                    <div>
                                        <upload-image v-model="fabNavsData.style.icon" width="70px" height="70px" :limit="1" />
                                        <div class="text-[12px] text-[#999]">上传图片建议70px*70px</div>
                                    </div>
                                </el-form-item>
                            </el-tab-pane>
                        </el-tabs>
                    </el-form>
                </div>
            </div>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { t } from '@/lang'
import { img } from '@/utils/common'
import { FormInstance, ElNotification } from 'element-plus'
import { getFabNavs, setFabNavs } from '@/app/api/sys'
import Sortable from 'sortablejs'
import { range } from 'lodash-es'

const activeName = ref<string>('navPicture')
const loading = ref<boolean>(false)

// 悬浮按钮数据
const fabNavsData = reactive<any>({
    is_show: true,
    navs: [],
    style: {
        type: 1,
        bottom: 300,
        position: 'right',
        icon: ''
    }
})

// 悬浮按钮项数据
const fabNavsItemData = reactive({
    icon: '',
    text: '',
    url: {
        title: '',
        value: ''
    }
})

// 添加悬浮按钮
const addNav = (): void => {
    fabNavsData.navs.push({ ...fabNavsItemData })
}

addNav()

// 删除悬浮按钮
const deleteNav = (index:any): void => {
    const data = fabNavsData.navs
    data.splice(index, 1)
}

const formRef = ref<FormInstance>()
/**
 * 获取悬浮按钮数据
 */
const getFabNavsFn = () => {
    loading.value = true

    getFabNavs().then(res => {
        loading.value = false
        if (res.data && Object.keys(res.data).length) {
            Object.keys(fabNavsData).forEach((item, index) => {
                fabNavsData[item] = res.data[item]
                fabNavsData.style.type = res.data.style.type || 1
            })
            if (fabNavsData.navs.length > 0) {
                fabNavsData.navs.forEach((item: any, index) => {
                    item.url.title = item.url.title || item.text
                    item.url.url = item.url.value
                })
            }
        }
    }).catch(() => {
        loading.value = false
    })
}
getFabNavsFn()

// 保存导航数据
const onSave = async (formEl: FormInstance | undefined) => {
    if (verifyFn()) return false

    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            setFabNavs(fabNavsData).then(res => {
                loading.value = false
            }).catch(() => {
                loading.value = false
            })
        }
    })
}

// 验证
const verifyFn = (): boolean => {
    try {
        fabNavsData.navs.forEach((item: any, index) => {
            item.url.value = item.url.url
        })
        const msg = ref<string>('')
        fabNavsData.navs.forEach((item: any, index) => {
            if (!item.icon) msg.value = `${t('pleaseUpload')}${index + 1}${t('navIcon')}`
            if (!item.text) msg.value = `${t('pleaseEnter')}[${index + 1}${t('navTitle')}`
            if (!item.url.value && !item.url.mobile && (!item.url.appid || !item.url.page)) msg.value = `${t('pleaseChoose')}${index + 1}${t('navLink')}`
            if (msg.value) {
                ElNotification({
                    type: 'error',
                    message: msg.value
                })
                throw Error()
            }
        })
        if (fabNavsData.style.type != 4 && (fabNavsData.style.icon == '' || fabNavsData.style.icon == undefined)) {
            ElNotification({
                type: 'error',
                message: '请上传悬浮按钮图片'
            })
            throw Error()
        }
    } catch (e) {
        return true
    }
    return false
}

const navItemRef = ref()

onMounted(() => {
    const sortable = Sortable.create(navItemRef.value, {
        group: 'item-wrap',
        animation: 200,
        filter: '.not-sort', // 过滤.not-sort的元素
        onEnd: event => {
            const temp = fabNavsData.navs[event.oldIndex!]
            fabNavsData.navs.splice(event.oldIndex!, 1)
            fabNavsData.navs.splice(event.newIndex!, 0, temp)
            nextTick(() => {
                sortable.sort(
                    range(fabNavsData.navs.length).map(value => {
                        return value.toString()
                    })
                )
            })
        }
    })
})

const useDrag = ref(false)
const diyLinkFn = (val: any) => {
    useDrag.value = val
}

</script>
<style lang="scss" scoped>
.close-icon {
    display: none;
    position: absolute !important;
    font-size: 20px !important;
    color: #7d7b7b !important;
}

.list-item:hover .close-icon {
    display: block;
}

.fab-wrapper{
    background: hsla(0, 0%, 100%, .5)
}
.fab-btn{
    &:first-child{
        top: -56px;
        right: 6px;
    }
    &:nth-child(2){
        top: -27px;
        right: 51px;
    }
    &:nth-child(3){
        top: 27px;
        right: 51px;
    }
    &:nth-child(4){
        top: 56px;
        right: 6px;
    }
}
.fab-style-btn{
    &:first-child{
        top: -56px;
        right: 0;
    }
    &:nth-child(2){
        top: -27px;
        right: 48px;
    }
    &:nth-child(3){
        top: 27px;
        right: 48px;
    }
    &:nth-child(4){
        top: 56px;
        right: 0;
    }
}
</style>