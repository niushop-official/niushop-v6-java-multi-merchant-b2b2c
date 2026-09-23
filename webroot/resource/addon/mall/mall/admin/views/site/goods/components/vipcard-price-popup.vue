<template>
    <div>
        <el-dialog v-model="showDialog" :title="t('编辑付费会员价')" width="1100px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">

            <el-form :model="formData" label-width="120px" class="page-form">
                <el-form-item :label="t('memberDiscount')">
                    <div>
                        <el-radio-group v-model="formData.vip_card_discount">
                            <el-radio label="">{{ t('nonparticipation') }}</el-radio>
                            <el-radio label="discount">{{ t('discount') }}</el-radio>
                            <el-radio label="fixed_price">{{ t('fixedPrice') }}</el-radio>
                        </el-radio-group>
                        <div class="text-[12px] text-[#999] leading-[20px]" v-if="formData.vip_card_discount == 'discount'">会员折扣说明：按照付费会员等级折扣优惠</div>
                        <div class="text-[12px] text-[#999] leading-[20px]" v-if="formData.vip_card_discount == 'fixed_price'">指定优惠价格，商品未参与活动时，按照付费会员价优惠，若商品参与活动，则以活动价为准</div>
                    </div>
                </el-form-item>
                <el-form-item v-if="formData.vip_card_discount == 'discount' || formData.vip_card_discount == 'fixed_price'">
                    <div class="mb-[10px] flex items-center">
                        <el-checkbox v-model="toggleCheckbox"  @change="toggleChange" size="large" class="px-[14px]" :indeterminate="isIndeterminate" />
                        <el-button v-for="(item,index) in tableData.vipcardList" :key="index" size="small" @click="batchGoodsBtn(item.card_id)">
                            {{item.card_name}}
                        </el-button>
                    </div>
                    <el-table :data="tableData.data" size="large" v-loading="tableData.loading" max-height="450" @selection-change="handleSelectionChange" ref="goodsListTableRef">
                        <template #empty>
                            <span>{{ !tableData.loading ? t('emptyData') : '' }}</span>
                        </template>
                        <el-table-column type="selection" width="55" />
                        <el-table-column fixed prop="sku_name" :label="t('goodsSku')" min-width="150" />
                        <el-table-column fixed prop="price" :label="t('skuPrice')" min-width="120" />
                        <el-table-column v-for="(item,index) in tableData.vipcardList" :key="index" :label="item.card_name" min-width="190">
                            <template #default="{ row }">
                                <el-input v-if="formData.vip_card_discount == 'discount'" v-model.trim="row.svip_discount[`card_${item.card_id}`]" maxlength="8" clearable class="w-full"  @keyup="filterDigit($event)">
                                    <template #append>折</template>
                                </el-input>
                                <el-input v-if="formData.vip_card_discount == 'fixed_price'" v-model.trim="row.svip_price[`card_${item.card_id}`]" maxlength="8" clearable class="w-full"  @keyup="filterDigit($event)">
                                    <template #append>{{t('yuanUnit')}}</template>
                                </el-input>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-form-item>
            </el-form>

            <el-dialog v-model="vipcardPriceDialog" :title="t('editMemberPrice')" width="600px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
                <el-form :model="formData" label-width="120px" class="page-form">
                    <el-form-item :label="t('付费会员价')"  prop="vipcard_price" v-if="formData.vip_card_discount == 'fixed_price'">
                        <el-input v-model.trim="vipcardPrice" :placeholder="t('请输入付费会员价')" maxlength="8" clearable  @keyup="filterDigit($event)" />
                    </el-form-item>
                    <el-form-item :label="t('付费会员折扣')"  prop="vipcard_discount" v-if="formData.vip_card_discount == 'discount'">
                        <el-input v-model.trim="vipcardDiscount" :placeholder="t('请输入付费会员折扣')" maxlength="8" clearable  @keyup="filterDigit($event)" />
                    </el-form-item>
                </el-form>

                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="vipcardPriceDialog = false">{{ t('cancel') }}</el-button>
                        <el-button type="primary" @click="vipcardPriceSave">{{ t('confirm') }}</el-button>
                    </span>
                </template>
            </el-dialog>

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="cancel">{{ t('cancel') }}</el-button>
                    <el-button type="primary" @click="save">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive } from 'vue'
import { deepClone, filterDigit } from '@/utils/common'
import { ElMessage } from 'element-plus'
import { getGoodsSkuList, editGoodsListVipcardPrice } from '@/app/lang/zh-cn/api/site/goods'

const goodsListTableRef = ref()
const formData: any = reactive({
    vip_card_discount: ''
})
const goods: any = reactive({})
const showDialog = ref(false)
const vipcardPrice = ref('') // 会员价
const vipcardDiscount = ref('')

const emit = defineEmits(['load'])

const tableData = reactive<any>({
    loading: true,
    data: [],
    vipcardList: []
})
/**
 * 获取商品列表
 */
const loadGoodsList = () => {
    tableData.loading = true

    getGoodsSkuList({
        goods_id: goods.goods_id
    }).then(res => {
        tableData.loading = false
        tableData.data = res.data
        tableData.data.forEach((item: any, index: any, Array: any) => {
            // 针对于单规格是，sku_name为空就展示商品名称
            if (!item.sku_name) {
                Array[index].sku_name = goods.goods_name
            }
            // 先解析一次，避免循环多张卡时重复 JSON.parse 已转成的对象
            const parseJsonField = (val: any) => {
                if (!val) return {}
                if (typeof val === 'object') return val
                try {
                    return JSON.parse(val)
                } catch {
                    return {}
                }
            }
            const svipPriceMap = parseJsonField(item.svip_price)
            const svipDiscountMap = parseJsonField(item.svip_discount)
            Array[index].svip_price = {}
            Array[index].svip_discount = {}
            tableData.vipcardList.forEach((cardItem: any) => {
                const cardKey = `card_${cardItem.card_id}`
                const savedPrice = svipPriceMap[cardKey]
                if (savedPrice !== undefined && savedPrice !== null && savedPrice !== '') {
                    Array[index].svip_price[cardKey] = parseFloat(savedPrice).toFixed(2)
                } else {
                    Array[index].svip_price[cardKey] = parseFloat(item.price).toFixed(2)
                }
                const savedDiscount = svipDiscountMap[cardKey]
                if (savedDiscount !== undefined && savedDiscount !== null && savedDiscount !== '') {
                    Array[index].svip_discount[cardKey] = parseFloat(savedDiscount).toFixed(2)
                } else {
                    Array[index].svip_discount[cardKey] = ''
                }
            })
        })
    }).catch(() => {
        tableData.loading = false
    })
}

const show = (data: any, vipcardLevel: any) => {
    Object.assign(goods, data)
    tableData.vipcardList = []
    Object.assign(tableData.vipcardList, vipcardLevel)
    formData.vip_card_discount = data.vip_card_discount
    loadGoodsList()
    showDialog.value = true
}

/** ******* 批量复选框-start *************/
const toggleCheckbox = ref()
// 复选框中间状态
const isIndeterminate = ref(false)
// 监听批量复选框事件
const toggleChange = (value: any) => {
    isIndeterminate.value = false
    goodsListTableRef.value.toggleAllSelection()
}

// 选中数据
const multipleSelection: any = ref([])

// 监听表格单行选中
const handleSelectionChange = (val: []) => {
    multipleSelection.value = deepClone(val)

    toggleCheckbox.value = false
    if (multipleSelection.value.length > 0 && multipleSelection.value.length < tableData.data.length) {
        isIndeterminate.value = true
    } else {
        isIndeterminate.value = false
    }

    if (multipleSelection.value.length == tableData.data.length) {
        toggleCheckbox.value = true
    }
}

// 按钮
const currLevelId = ref('')
const batchGoodsBtn = (card_id: any) => {
    if (!multipleSelection.value.length) {
        ElMessage({
            message: '请选择要操作的商品',
            type: 'warning'
        })
        return false
    }
    currLevelId.value = card_id
    vipcardPrice.value = ''
    vipcardDiscount.value = ''
    vipcardPriceDialog.value = true
}

/** ******* 批量复选框-end *************/
const vipcardPriceDialog = ref(false)
const vipcardPriceSave = () => {
    if (!vipcardPrice.value && formData.vip_card_discount == 'fixed_price') {
        ElMessage.error('请输入付费会员价')
        return false
    }
    if (!vipcardDiscount.value && formData.vip_card_discount == 'discount') {
        ElMessage.error('请输入付费会员折扣')
        return false
    }

    const idArr = multipleSelection.value.map((obj: any) => obj.sku_id)
    tableData.data.forEach((item: any, index, Array: any) => {
        if (formData.vip_card_discount == 'fixed_price') {
            if (idArr.indexOf(item.sku_id) > -1) { Array[index].svip_price[`card_${currLevelId.value}`] = parseFloat(vipcardPrice.value).toFixed(2) }
        } else if (formData.vip_card_discount == 'discount') {
            if (idArr.indexOf(item.sku_id) > -1) { Array[index].svip_discount[`card_${currLevelId.value}`] = parseFloat(vipcardDiscount.value).toFixed(2) }
        }
    })
    vipcardPrice.value = ''
    vipcardDiscount.value = ''
    vipcardPriceDialog.value = false
}

let saveLoad = false
const save = () => {
    const sku_list: any = []
    let verify = true // 用于校验价格是否小于等于零或大于商品原价
    if (formData.vip_card_discount == 'fixed_price' || formData.vip_card_discount == 'discount') {
        tableData.data.forEach((item: any, index, Array) => {
            const obj: any = {}
            obj.sku_id = item.sku_id
            obj.svip_price = {}
            obj.svip_discount = {}
            tableData.vipcardList.forEach((cardItem: any) => {
                if (verify) {
                    obj.svip_price[`card_${cardItem.card_id}`] = item.svip_price[`card_${cardItem.card_id}`]
                    obj.svip_discount[`card_${cardItem.card_id}`] = item.svip_discount[`card_${cardItem.card_id}`]
                    if (formData.vip_card_discount == 'fixed_price') {
                        if (parseFloat(item.svip_price[`card_${cardItem.card_id}`]) <= 0) {
                            verify = false
                            ElMessage.error(`[${item.sku_name}][${cardItem.card_name}]的指定价格不能小于等于零`)
                        }
                        if (parseFloat(item.svip_price[`card_${cardItem.card_id}`]) > parseFloat(item.price)) {
                            verify = false
                            ElMessage.error(`[${item.sku_name}][${cardItem.card_name}]的指定价格不能大于商品原价`)
                        }
                        if (goods.supply_id && (parseFloat(item.svip_price[`card_${cardItem.card_id}`]) < parseFloat(item.cost_price))) {
                            verify = false
                            ElMessage.error(`[${item.sku_name}][${cardItem.card_name}]的指定价格不能小于供货价`)
                        }
                    }
                }
            })
            sku_list.push(obj)
        })

        if (!verify) return false
    }

    if (saveLoad) return false
    saveLoad = true

    editGoodsListVipcardPrice({
        goods_id: goods.goods_id,
        vip_card_discount: formData.vip_card_discount,
        sku_list
    }).then(res => {
        saveLoad = false
        emit('load')
        showDialog.value = false
        toggleCheckbox.value = false
    })
}

// 取消
const cancel = () => {
    showDialog.value = false
    toggleCheckbox.value = false
}
defineExpose({
    showDialog,
    show
})
</script>

<style lang="scss" scoped>
.form-item-wrap {
    margin-right: 10px !important;

    &.last-child {
        margin-right: 0 !important;
    }
}
</style>
