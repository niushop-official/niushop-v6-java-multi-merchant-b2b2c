<template>
    <div>
        <el-dialog v-model="showDialog" :title="t('treasureList')" width="1000px" :destroy-on-close="true" :close-on-click-modal="false">
            <el-form :inline="true" :model="treasureTable.searchParam" ref="searchFormRef">
                <el-form-item prop="select_type" class="form-item-wrap">
                    <el-select v-model="treasureTable.searchParam.select_type" @change="handleSelectTypeChange">
                        <el-option :label="t('alltreasures')" value="all" />
                        <el-option :label="t('selectedtreasure')" value="selected" />
                    </el-select>
                </el-form-item>
                <el-form-item :label="t('treasuresName')" prop="keyword" class="form-item-wrap">
                    <el-input v-model.trim="treasureTable.searchParam.keyword" :placeholder="t('treasuresNamePlaceholder')" maxlength="60" />
                </el-form-item>
                <el-form-item :label="t('relateTypeName')">
                    <el-select v-model="treasureTable.searchParam.relate_type" :placeholder="t('relateTypeNamePlaceholder')">
                        <el-option v-for="(item,index) in treasureType" :key="index" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
                <el-form-item class="form-item-wrap">
                    <el-button type="primary" @click="loadTreasureList()">{{ t('search') }}</el-button>
                    <el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
                </el-form-item>
            </el-form>

            <el-table :data="treasureTable.data" size="large" v-loading="treasureTable.loading" ref="treasureListTableRef"  max-height="400" @select="handleSelectChange" @select-all="handleSelectAllChange">
                    <template #empty>
                        <span>{{ !treasureTable.loading ? t('emptyData') : '' }}</span>
                    </template>
                    <el-table-column type="selection" width="55" />

                    <el-table-column  :label="t('treasureInfo')" min-width="300">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer" >
                                <div class="min-w-[70px] h-[70px] flex items-center justify-center">
                                    <el-image v-if="row.treasure_image" class="w-[70px] h-[70px]" :src="img(row.treasure_image)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[70px] h-[70px]" src="@/addon/sow_community/assets/default_img.png" fit="contain" />
                                </div>
                                <div class="ml-2  flex flex-col items-start">
                                    <span :title="row.treasure_name" class="multi-hidden">{{ row.treasure_name }}</span>
									<span :title="row.site" class="text-[12px] text-[#999]"><span class="iconfont icondianpuliebiao1 pr-[2px] !text-[13px]"></span>{{ row.site.site_name }}</span>
                                    <span class="text-primary text-[12px]">{{ row.treasure_sub_name }}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>

                    <el-table-column prop="relate_type_name" :label="t('relateTypeName')" min-width="120" />
                    <el-table-column prop="treasure_price" :label="t('treasurePrice')" min-width="120" />
                </el-table>

            <div class="mt-[16px] flex">
                <div class="flex items-center flex-1">
                    <div class="mr-[10px]" v-show="selectTreasureNum">
                        <span>{{ t('treasureBeforeTip') }}</span>
                        <span class="text-primary mx-[2px]">{{ selectTreasureNum }}</span>
                        <span>{{ t('treasureAfterTip') }}</span>
                    </div>
                    <el-button type="primary" link @click="clear" v-show="selectTreasureNum">{{ t('goodsSelectPopupClearGoods') }}</el-button>
                </div>
                <el-pagination v-model:current-page="treasureTable.page" v-model:page-size="treasureTable.limit"
                    layout="total, sizes, prev, pager, next, jumper" :total="treasureTable.total"
                    @size-change="loadTreasureList()" @current-change="loadTreasureList" />
            </div>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="save">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive, computed, nextTick } from 'vue'
import { cloneDeep } from 'lodash-es'
import { img } from '@/utils/common'
import { ElMessage, FormInstance } from 'element-plus'
import { getTreasureSelect, getTreasureType } from '@/addon/sow_community/api/site/treasure'

const prop = defineProps({
    modelValue: {
        type: String,
        default: ''
    },
    max: {
        type: Number,
        default: 0
    },
    min: {
        type: Number,
        default: 0
    }

})

const emit = defineEmits(['update:modelValue', 'treasurSelect'])

const treasureIds: any = computed({
    get () {
        return prop.modelValue
    },
    set (value) {
        emit('update:modelValue', value)
    }
})

const showDialog = ref(false)

// 已选宝贝列表
const selectTreasure: any = reactive({})

// 已选宝贝数量
const selectTreasureNum: any = computed(() => {
    return Object.keys(selectTreasure).length
})

const treasureTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        keyword: '',
        relate_type: '',
        select_type: 'all',
        treasure_ids: ''
    }
})

const searchFormRef = ref()

// 查询全部/已选商品
const handleSelectTypeChange = () => {
    loadTreasureList()
}

// 商品类型
const treasureType: any = ref([])

// 初始化数据
const initData = () => {
    // 宝贝类型
    getTreasureType().then((res: any) => {
        const data = res.data
        if (data) {
            treasureType.value = []
            for (const k in data) {
                const obj = {
                    value: k,
                    label: data[k]
                }
                treasureType.value.push(obj)
            }
            if (treasureType.value.length) {
                treasureTable.searchParam.relate_type = treasureType.value[0].value
            }
            loadTreasureList(1, (treasure_ids: any) => {
                // 第一次打开弹出框时，纠正数据，并且赋值已选宝贝
                if (treasureIds.value) {
                    treasureIds.value.splice(0, treasureIds.value.length, ...treasure_ids)
                    treasureIds.value.forEach((item: any) => {
                        if (!selectTreasure['treasure_' + item]) {
                            selectTreasure['treasure_' + item] = {}
                        }
                    })

                    // 赋值已选择的宝贝
                    for (let i = 0; i < treasureTable.data.length; i++) {
                        if (treasureIds.value.indexOf(treasureTable.data[i].treasure_id) != -1) {
                            selectTreasure['treasure_' + treasureTable.data[i].treasure_id] = treasureTable.data[i]
                        }
                    }
                }
            })
        }
    })
}

const treasureListTableRef = ref()

// 监听表格复选框
const handleSelectChange = (selection: any, row: any) => {
    // 是否选中
    let isSelected = false
    for (let i = 0; i < selection.length; i++) {
        if (selection[i].treasure_id == row.treasure_id) {
            isSelected = true
            break
        }
    }
    if (isSelected) {
        selectTreasure['treasure_' + row.treasure_id] = row
    } else {
        // 未选中，删除当前宝贝
        delete selectTreasure['treasure_' + row.treasure_id]
    }
}
// 监听表格全选
const handleSelectAllChange = (selection: any) => {
    if (selection.length) {
        selection.forEach((item: any) => {
            selectTreasure['treasure_' + item.treasure_id] = item
        })
    } else {
        treasureTable.data.forEach((item: any) => {
            delete selectTreasure['treasure_' + item.treasure_id]
        })
    }
}

/**
 * 获取宝贝列表
 */
const loadTreasureList = (page: number = 1, callback: any = null) => {
    treasureTable.loading = true
    treasureTable.page = page

    const searchData = cloneDeep(treasureTable.searchParam)

    if (searchData.select_type == 'selected') {
        const giftcard_ids = <any>[]
        for (let k in selectTreasure) {
            giftcard_ids.push(parseInt(k.replace('treasure_', '')))
        }
        searchData.treasure_ids = giftcard_ids
    }

    getTreasureSelect({
        page: treasureTable.page,
        limit: treasureTable.limit,
        ...searchData
    }).then(res => {
        treasureTable.data = res.data.data
        treasureTable.total = res.data.total
        treasureTable.loading = false
        if (callback) callback(res.data.treasure_ids)
        setTreasureSelected()
    }).catch(() => {
        treasureTable.loading = false
    })
}

// 表格设置选中状态
const setTreasureSelected = () => {
    nextTick(() => {
        if (!treasureListTableRef.value) return
        for (let i = 0; i < treasureTable.data.length; i++) {
            treasureListTableRef.value.toggleRowSelection(treasureTable.data[i], false)
            if (selectTreasure['treasure_' + treasureTable.data[i].treasure_id]) {
                treasureListTableRef.value.toggleRowSelection(treasureTable.data[i], true)
            }
        }
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadTreasureList()
}

const open = () => {
    for (const k in selectTreasure) {
        delete selectTreasure[k]
    }
    // 检测商品id集合是否存在，移除不存在的商品id，纠正数据准确性
    treasureTable.searchParam.treasure_ids = treasureIds.value
    initData()
    showDialog.value = true
}

// 清空已选宝贝
const clear = () => {
    for (const k in selectTreasure) {
        delete selectTreasure[k]
    }
    treasureTable.searchParam.treasure_ids = ''
    loadTreasureList()
    setTreasureSelected()
}
const save = () => {
    if (prop.min && selectTreasureNum.value < prop.min) {
        ElMessage({
            type: 'warning',
            message: `${t('treasureMinTip')}${prop.min}${t('treasurePiece')}`
        })
        return
    }

    if (prop.max && prop.max > 0 && selectTreasureNum.value && selectTreasureNum.value > prop.max) {
        ElMessage({
            type: 'warning',
            message: `${t('treasureMaxTip')}${prop.max}${t('treasurePiece')}`
        })
        return
    }

    const ids: any = []
    for (const k in selectTreasure) {
        ids.push(parseInt(k.replace('treasure_', '')))
    }

    treasureIds.value.splice(0, treasureIds.value.length, ...ids)

    emit('treasurSelect', Object.values(selectTreasure))
    showDialog.value = false
}

defineExpose({
    open,
    showDialog
})
</script>

<style lang="scss" scoped>
.form-item-wrap {
    margin-right: 10px !important;
    margin-bottom: 10px !important;

    &.last-child {
        margin-right: 0 !important;
    }
}
.arrow-show{
    transform: rotate(90deg);
}
</style>
