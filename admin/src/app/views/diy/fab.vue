<template>
    <div class="main-container">
        <el-card class="box-card !border-none min-h-[600px] bg-[#fff]" shadow="never" v-loading="loading">
            <div class="flex">
                <div class="w-[360px] h-[500px] absolute mr-[30px] border-[1px] border-gray-300">
                    <div class="absolute right-[16px] bottom-[140px] w-[35px] h-[35px]">
                        <div class="absolute top-0 right-0 w-[35px] h-[35px] flex items-center justify-center rounded-full fab-button">
                            <div class="fab-icon"></div>
                        </div>
                        <div class="w-[100px] h-[200px] absolute left-[50%] top-[50%] translate-x-[-50%] translate-y-[-50%]">
                            <div class="menu-item flex items-center justify-center" :style="newItemStyle[index]" v-for="(item, index) in fabNavsData.navs" :key="'b' + index">
                                <el-image class="w-[22px] h-[22px] leading-1" :src="img(item.icon)" fit="contain">
                                    <template #error>
                                        <div class="image-slot flex justify-center items-center mt-1">
                                            <el-icon><Picture class="text-3xl text-gray-500" /></el-icon>
                                        </div>
                                    </template>
                                </el-image>
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
                                    </el-radio-group>
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
const fabNavsData = reactive({
    is_show: true,
    navs: [],
    style: {
        type: 1,
        bottom: 300,
        position: 'right'
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
                    item.url.title = item.text
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

const getItemStyle = (index: any) => {
    const config: any = {
        0: {
            x: 30,
            y: 25
        },
        1: {
            x: -15,
            y: 60
        },
        2: {
            x: -12,
            y: 110
        },
        3: {
            x: 30,
            y: 140
        }
    }

    const { x, y } = config[index] || config[0]

    return {
        "--x": `${x}px`,
        "--y": `${y}px`
    }
}

const newItemStyle = ref({
    0: getItemStyle(0),
    1: getItemStyle(1),
    2: getItemStyle(2),
    3: getItemStyle(3)
})
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
.fab-button{
    background: rgba(51, 51, 51, .1);
    box-shadow: 0px 0px 6px 0px rgba(0, 0, 0, .1);
}
.fab-icon {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    box-shadow: 0 0 8px 0 rgba(0, 0, 0, .1);
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #fff;
}
.menu-item {
    position: absolute;
    left: 0;
    top: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 35px;
    height: 35px;
    background-color: #ffffff;
    border-radius: 50%;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
    opacity: 1;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    transform: scale(1) translate(var(--x, 0), var(--y, 0));
    transform-origin: center center;
}
</style>