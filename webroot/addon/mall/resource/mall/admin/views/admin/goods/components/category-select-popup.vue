<template>
    <div>
        <div class="leading-[1]" @click="show">
            <slot>
                <el-button>{{ t('selectCategoryOne') }}</el-button>
                <div class="inline-block ml-[10px] text-[14px]" v-show="categoryId">
                    <span>{{ t('goodsSelectPopupSelect') }}</span>
                    <span class="text-primary mx-[2px]">1</span>
                    <span>{{ t('goodsSelectPopupPiece') }}</span>
                </div>
            </slot>
        </div>
        <el-dialog v-model="showDialog" :title="t('goodsCategoryTitle')" width="1000px" :destroy-on-close="true"
            :close-on-click-modal="false">
            <div class="table w-[100%] mt-[15px]" v-loading="categoryTable.loading">
                <div class="table-head flex items-center bg-[#f5f7f9] py-[10px] text-[14px]"
                    :style="{ paddingRight: scrollBarWidth + 'px' }">
                    <div class="w-[6%]"></div>
                    <div class="w-[8%]">
                        <!-- <el-checkbox v-model="staircheckAll" :indeterminate="isStairIndeterminate" @change="handleCheckAllChange" /> -->
                    </div>
                    <div class="w-[52%]">{{ t('categoryName') }}</div>
                    <div class="w-[34%]">{{ t('categoryImage') }}</div>
                </div>
                <div class="table-body max-h-[500px] overflow-y-auto" ref="tableBodyRef">
                    <!-- 遍历一级分类 -->
                    <div v-for="(row, rowIndex) in categoryTable.data" :key="rowIndex" class="flex flex-col">
                        <div class="flex items-center border-solid border-[#e5e7eb] py-[10px] border-b-[1px]">
                            <!-- 图标：展开/收起子级 -->
                            <div v-if="row.child_list && row.child_list.length"
                                class="w-[6%] cursor-pointer text-center !text-[10px]"
                                @click="secondLevelArrowChange(row)"
                                :class="{ 'iconfont iconxiangyoujiantou': row.child_list.length, 'arrow-show': row.isShow }">
                            </div>
                            <div v-else class="w-[6%]"></div>
                            <!-- 一级分类复选框 -->
                            <div class="w-[8%]">
                                <el-checkbox v-model="row.secondLevelCheckAll"
                                    :indeterminate="row.isSecondLevelIndeterminate"
                                    @change="handleCheckboxChange($event, row)" />
                            </div>
                            <!-- 一级分类名称 -->
                            <div class="ml-2 flex flex-col items-start w-[52%]">
                                <span :title="row.category_name"
                                    class="multi-hidden leading-[1.4] mr-5 text-[14px] text-[#666]">
                                    {{ row.category_name }}
                                </span>
                            </div>
                            <!-- 一级分类图片 -->
                            <div class="flex items-center cursor-pointer w-[34%]">
                                <div class="min-w-[30px] h-[30px] flex items-center justify-center">
                                    <el-image v-if="row.img" class="w-[30px] h-[30px]" :src="img(row.img)"
                                        fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[30px] h-[30px]"
                                                    src="@/addon/mall/assets/category_default.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[30px] h-[30px]" src="@/addon/mall/assets/category_default.png"
                                        fit="contain" />
                                </div>
                            </div>
                        </div>
                        <!-- 子级分类 -->
                        <div v-show="row.child_list && row.isShow">
                            <div v-for="(item, index) in row.child_list" :key="index">
                                <div class="flex items-center py-[10px] border-solid border-b-[1px]"
                                    :class="{ 'hidden': !row.isShow, 'border-[#e5e7eb]': index == (row.child_list.length - 1) }">
                                    <!-- 图标：展开/收起子级 -->
                                    <div v-if="item.child_list && item.child_list.length"
                                        class="w-[9%] cursor-pointer text-center !text-[10px]"
                                        @click="secondLevelArrowChange(item)"
                                        :class="{ 'iconfont iconxiangyoujiantou': item.child_list.length, 'arrow-show': item.isShow }">
                                    </div>
                                    <div v-else class="w-[9%]"></div>
                                    <!-- 子级分类复选框 -->
                                    <div class="w-[5%]">
                                        <el-checkbox v-model="item.threeLevelCheckAll" :indeterminate="item.isThreeLevelIndeterminate"
                                            @change="handleCheckboxChange($event, item, row, 'twoLevel')" />
                                    </div>
                                    <!-- 子级分类名称 -->
                                    <div class="ml-2 flex flex-col items-start w-[52%]">
                                        <span :title="item.category_name"
                                            class="multi-hidden leading-[1.4] mr-5 text-[14px] text-[#666]">
                                            {{ item.category_name }}
                                        </span>
                                    </div>
                                    <!-- 子级分类图片 -->
                                    <div class="flex items-center cursor-pointer w-[34%]">
                                        <div class="min-w-[30px] h-[30px] flex items-center justify-center">
                                            <el-image v-if="row.img" class="w-[30px] h-[30px]" :src="img(row.img)"
                                                fit="contain">
                                                <template #error>
                                                    <div class="image-slot">
                                                        <img class="w-[30px] h-[30px]"
                                                            src="@/addon/mall/assets/category_default.png" />
                                                    </div>
                                                </template>
                                            </el-image>
                                            <img v-else class="w-[30px] h-[30px]"
                                                src="@/addon/mall/assets/category_default.png" fit="contain" />
                                        </div>
                                    </div>
                                </div>
                                <!-- 三级分类 -->
                                <div v-show="item.child_list && item.isShow">
                                    <div v-for="(threeItem, threeIndex) in item.child_list" :key="threeIndex"
                                        class="flex items-center py-[10px] border-solid border-b-[1px]"
                                        :class="{ 'hidden': !item.isShow, 'border-[#e5e7eb]': index == (item.child_list.length - 1) }">
                                        <div class="w-[12%]"></div>
                                        <!-- 三级分类复选框 -->
                                        <div class="w-[5%]">
                                            <el-checkbox v-model="threeItem.fourLevelCheckAll"
                                                @change="handleCheckboxChange($event, threeItem, item, 'threeLevel')" />
                                        </div>
                                        <!-- 三级分类名称 -->
                                        <div class="ml-2 flex flex-col items-start w-[49%]">
                                            <span :title="threeItem.category_name"
                                                class="multi-hidden leading-[1.4] mr-5 text-[14px] text-[#666]">
                                                {{ threeItem.category_name }}
                                            </span>
                                        </div>
                                        <!-- 三级分类图片 -->
                                        <div class="flex items-center cursor-pointer w-[34%]">
                                            <div class="min-w-[30px] h-[30px] flex items-center justify-center">
                                                <el-image v-if="item.img" class="w-[30px] h-[30px]" :src="img(item.img)"
                                                    fit="contain">
                                                    <template #error>
                                                        <div class="image-slot">
                                                            <img class="w-[30px] h-[30px]"
                                                                src="@/addon/mall/assets/category_default.png" />
                                                        </div>
                                                    </template>
                                                </el-image>
                                                <img v-else class="w-[30px] h-[30px]"
                                                    src="@/addon/mall/assets/category_default.png" fit="contain" />
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div v-if="!categoryTable.data.length && !categoryTable.loading"
                        class="h-[60px] flex items-center justify-center border-solid border-[#e5e7eb] py-[12px] border-b-[1px]">
                        {{
            t('emptyData') }}</div>
                </div>
            </div>
            <div class="flex items-center justify-end mt-[15px]">
                <el-button type="primary" @click="save">{{ t('confirm') }}</el-button>
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
            </div>
        </el-dialog>
    </div>

</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { img } from '@/utils/common'
import { ElMessage } from 'element-plus'
import { getCategoryTree } from '@/app/api/basic_site'

const prop = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    categoryId: {
        type: Number || String,
        default: ''
    }
})

const emit = defineEmits(['update:modelValue', 'categorySelect'])
const showDialog = ref(false)

const categoryId: any = computed(() => {
    return prop.categoryId
})

// 已选商品分类
let currCategoryData: any = null
const categoryTable = reactive({
    loading: true,
    data: []
})

const scrollBarWidth = ref(0)
const tableBodyRef = ref(null)

onMounted(() => {
    window.addEventListener('resize', getScrollBarWidth)
})

const getScrollBarWidth = () => {
    nextTick(() => {
        if (tableBodyRef.value) {
            scrollBarWidth.value = tableBodyRef.value.offsetWidth - tableBodyRef.value.clientWidth
        }
    })
}

// 选择商品分类
// 方法：切换子级展开/收起
const secondLevelArrowChange = (row: any) => {
    row.isShow = !row.isShow
    nextTick(() => getScrollBarWidth())
}

const show = () => {
    categoryTable.loading = true
    currCategoryData = null
    getCategoryTree({
        addon: 'mall'
    }).then(res => {
        categoryTable.loading = false
        categoryTable.data = res.data
        categoryTable.data.forEach((item: any) => {
            // 初始化一级分类的字段
            item.isShow = false // 控制子级是否展开
            item.isSecondLevelIndeterminate = false // 一级分类不确定状态
            item.secondLevelCheckAll = false // 一级分类复选框状态

            // 如果有子分类（child_list），初始化子分类的字段
            if (item.child_list && item.child_list.length) {
                item.child_list.forEach((childItem: any) => {
                    childItem.isShow = false // 控制子级是否展开
                    item.isThreeLevelIndeterminate = false // 一级分类不确定状态
                    childItem.threeLevelCheckAll = false // 子分类复选框状态
                    // 三级分类
                    if (childItem.child_list && childItem.child_list.length) {
                        childItem.child_list.forEach((threeItem: any) => {
                            threeItem.fourLevelCheckAll = false // 三级分类复选框状态
                        })
                    }
                })
            }
        })
        if (categoryId.value) {
            categoryTable.data.forEach((row: any) => {
                if (row.category_id == categoryId.value) {
                    row.secondLevelCheckAll = true
                    row.isShow = true // 展开选中的一级分类
                    currCategoryData = row
                }
                if (row.child_list) {
                    row.child_list.forEach((child: any) => {
                        if (child.category_id == categoryId.value) {
                            child.threeLevelCheckAll = true
                            row.isShow = true
                            currCategoryData = row
                        }
                        if (child.child_list) {
                            child.child_list.forEach((threeItem: any) => {
                                if (threeItem.category_id == categoryId.value) {
                                    row.isShow = true
                                    child.isShow = true
                                    threeItem.fourLevelCheckAll = true
                                    currCategoryData = child
                                }
                            })
                        }
                    })
                }
            })
        }
        showDialog.value = true
    }).catch(() => {
        categoryTable.loading = false
    })
}

const save = () => {
    if (!currCategoryData) {
        ElMessage({
            type: 'warning',
            message: `${t('goodsCategorySelectContentPlaceholder')}`
        })
        return
    }
    emit('categorySelect', currCategoryData)
    emit('update:modelValue', [currCategoryData.category_id])
    showDialog.value = false
}

const clearAllSelections = () => {
    categoryTable.data.forEach((row: any) => {
        row.secondLevelCheckAll = false
        if (row.child_list) {
            row.child_list.forEach((child: any) => {
                child.threeLevelCheckAll = false
                if (child.child_list) {
                    child.child_list.forEach((threeItem: any) => {
                        threeItem.fourLevelCheckAll = false
                    })
                }
            })
        }
    })
}

// 处理复选框变化
const handleCheckboxChange = (checked: any, target: any, parentRow: any, series:any = '') => {
    clearAllSelections() // 清空所有复选框的选中状态
    if (checked) {
        // 设置当前选中的分类
        if (parentRow) {
            currCategoryData = target
            if (series == 'twoLevel') { // 二级分类
                target.threeLevelCheckAll = true
            } else if (series == 'threeLevel') { // 三级分类
                target.fourLevelCheckAll = true
            }
        } else {
            // 如果是一级分类
            target.secondLevelCheckAll = true
            currCategoryData = target
        }
    } else {
        // 取消勾选时，清空选中的分类 ID
        currCategoryData = null
    }
}

defineExpose({
    showDialog,
    currCategoryData
})
</script>

<style lang="scss" scoped>
.arrow-show {
    transform: rotate(90deg) !important;
    /* 提高优先级 */
}
</style>
