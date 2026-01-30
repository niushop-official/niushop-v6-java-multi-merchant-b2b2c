<template>
    <div  class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <el-page-header :content="goodsEdit.formData.goods_id ? t('updateGoods') : t('addGoods')" :icon="ArrowLeft" @back="goodsEdit.back()" />
        </el-card>
        <el-card class="box-card mt-[15px] !border-none" shadow="never">
            <el-tabs v-model="goodsEdit.activeName" @tab-click="goodsEdit.tabHandleClick">
                <el-tab-pane :label="t('basicInfoTab')" name="basic">

                    <el-form :model="goodsEdit.formData" label-width="120px" ref="basicFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('goodsName')" prop="goods_name">
                            <el-input v-model.trim="goodsEdit.formData.goods_name" clearable :placeholder="t('goodsNamePlaceholder')" class="input-width" maxlength="60" show-word-limit />
                        </el-form-item>
                        <el-form-item :label="t('subTitle')" prop="sub_title">
                            <el-input v-model.trim="goodsEdit.formData.sub_title" clearable :placeholder="t('subTitlePlaceholder')" class="input-width" maxlength="100" show-word-limit />
                        </el-form-item>
                        <el-form-item :label="t('goodsImage')" prop="goods_image">
                            <upload-image v-model="goodsEdit.formData.goods_image" :limit="10" />
                        </el-form-item>
                        <el-form-item :label="t('goodsVideo')">
                                <div class="flex flex-col">
                                    <upload-video v-model="goodsEdit.formData.goods_video" :limit="1" />
                                    <el-input v-model="goodsEdit.formData.goods_video" clearable :placeholder="t('在此输入外链视频地址')" class="input-width mt-[10px]" />
                                    <div class="flex flex-col mt-[10px]">
                                        <div class="text-[12px] text-[#999] leading-[20px]">{{ t('goodsVideoTipTile') }}</div>
                                        <div class="mt-[5px] text-[12px] text-[var(--el-color-primary)] leading-[20px]">{{ t('goodsVideoTipOne') }}</div>
                                        <div class="mt-[5px] text-[12px] text-[var(--el-color-primary)] leading-[20px]">{{ t('goodsVideoTipTwo') }}</div>
                                        <div class="mt-[5px] text-[12px] text-[var(--el-color-primary)] leading-[20px]">{{ t('goodsVideoTipThree') }}</div>
                                        <div class="mt-[5px] text-[12px] text-[var(--el-color-primary)] leading-[20px]">{{ t('goodsVideoTipFour') }}</div>
                                        <div class="mt-[5px] text-[12px] text-[var(--el-color-primary)] leading-[20px]">{{ t('goodsVideoTipFive') }}</div>
                                    </div>
                                </div>
                        </el-form-item>
                        <el-form-item :label="t('goodsCategory')" prop="goods_category">
                            <el-cascader v-model="goodsEdit.formData.goods_category" :options="goodsEdit.goodsMallCategoryOptions" :placeholder="t('goodsCategoryPlaceholder')" clearable :props="{ value: 'value', label: 'label', emitPath: false }" />
                        </el-form-item>
                        <el-form-item :label="t('pointGoodsCategory')" >
                            <el-select v-model="goodsEdit.formData.category" :placeholder="t('pointGoodsCategoryPlaceholder')" clearable >
                                <el-option v-for="(item,index) in goodsEdit.goodsGroupOptions" :key="index" :label="item.category_name" :value="item.category_id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="t('status')">
                            <el-radio-group v-model="goodsEdit.formData.status">
                                <el-radio :label="1">{{ t('statusOn') }}</el-radio>
                                <el-radio :label="0">{{ t('statusOff') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('sort')" prop="sort">
                            <el-input v-model.trim="goodsEdit.formData.sort" clearable :placeholder="t('sortPlaceholder')" class="input-width" show-word-limit maxlength="8"  @keyup="filterNumber($event)" @blur="goodsEdit.formData.sort = $event.target.value" />
                        </el-form-item>
                         <el-form-item :label="t('virtualSale')" prop="virtual_sale_num">
                            <el-input v-model.trim="goodsEdit.formData.virtual_sale_num" clearable :placeholder="t('virtualSalePlaceholder')" class="input-width" show-word-limit maxlength="8" @keyup="filterNumber($event)" @blur="goodsEdit.formData.virtual_sale_num = $event.target.value">
                                <template #append>
                                <span class="mr-[10px]">
                                    {{ goodsEdit.formData.unit ? goodsEdit.formData.unit : '件' }}
                                </span>
                                </template>
                            </el-input>
                        </el-form-item>
                        <el-form-item :label="t('memberLimit')" prop="member_limit_buy">
                           <div>
                                <el-input v-model.trim="goodsEdit.formData.member_limit_buy" clearable placeholder="0" class="input-width" maxlength="8" :disabled="goodsEdit.isDisabledPrice()">
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                                <div class="text-[#999] text-[12rpx]">{{ t('memberLimitTips') }}</div>
                           </div>
                        </el-form-item>
                         <!-- 适用人群 -->
                        <el-form-item :label="t('joinMemberType')" prop="join_member_type">
                            <el-radio-group v-model="goodsEdit.formData.join_member_type">
                                <el-radio v-for="(value, key) in goodsEdit.memberRuleOptions" :label="key" :key="key">{{ value }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <!-- 会员标签 -->
                        <el-form-item :label="t('memberLabel')" prop="member_type_config" v-if="goodsEdit.formData.join_member_type == 'label'">
                            <el-select  v-model="goodsEdit.formData.member_type_config" clearable multiple :placeholder="t('memberLabelPlaceholder')" class="input-width">
                                <el-option :label="item.label_name" :value="item.label_id" v-for="(item, index) in goodsEdit.labelSelectData" :key="index" />
                            </el-select>
                        </el-form-item>
                        <!-- 会员等级 -->
                        <el-form-item :label="t('memberLevel')" prop="member_type_config" v-if="goodsEdit.formData.join_member_type == 'level'">
                            <el-select v-model="goodsEdit.formData.member_type_config" clearable multiple :placeholder="t('memberLevelPlaceholder')" class="input-width">
                                <el-option :label="item.level_name" :value="item.level_id" v-for="(item, index) in goodsEdit.levelSelectData" :key="index" />
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="t('isRefund')">
                            <div>
                                <el-radio-group v-model="goodsEdit.formData.is_allow_refund">
                                    <el-radio :label= "1">{{ t('yes') }}</el-radio>
                                    <el-radio :label= "0">{{ t('no') }}</el-radio>
                                </el-radio-group>
                            </div>
                        </el-form-item>
                    </el-form>

                </el-tab-pane>
                <el-tab-pane :label="t('specInfo')" name="price_stock">
                    <el-form :model="goodsEdit.formData" label-width="120px" ref="priceStockFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('specType')" prop="spec_type">
                            <div>
                                <el-radio-group v-model="goodsEdit.formData.spec_type">
                                    <el-radio label="single" :disabled="goodsEdit.isDisabledPrice()">{{ t('singleSpec') }}</el-radio>
                                    <el-radio label="multi" :disabled="goodsEdit.isDisabledPrice()">{{ t('multiSpec') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]" v-if="goodsEdit.isDisabledPrice()">{{ t('participateInActiveDisableTips') }}</div>
                            </div>
                        </el-form-item>

                        <template v-if="goodsEdit.formData.spec_type == 'single'">
                            <el-form-item :label="t('pointPrcie')" prop="point">
                                <div>
                                    <el-input v-model.trim="goodsEdit.formData.point" clearable placeholder="0" class="!w-[200px]" maxlength="6" @keyup="filterNumber($event)" @blur="goodsEdit.formData.point = $event.target.value">
                                        <template #append>{{ t('point') }}</template>
                                    </el-input>
                                    <span class="mx-[10px]">+</span>
                                    <el-input v-model.trim="goodsEdit.formData.price" clearable placeholder="0.00" class="!w-[200px]" maxlength="6">
                                        <template #append>{{ t('yuan') }}</template>
                                    </el-input>
                                </div>
                            </el-form-item>
                            <el-form-item :label="t('marketPrice')" prop="market_price">
                                <el-input v-model.trim="goodsEdit.formData.market_price" clearable placeholder="0.00" class="input-width" maxlength="6">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>
                             <el-form-item :label="t('unit')" prop="unit">
                                <el-input v-model.trim="goodsEdit.formData.unit" clearable :placeholder="t('unitPlaceholder')" class="input-width" show-word-limit maxlength="6" />
                            </el-form-item>
                            <el-form-item :label="t('goodsStock')" prop="stock">
                                <el-input v-model.trim="goodsEdit.formData.stock" clearable :placeholder="t('goodsStockPlaceholder')" class="input-width" maxlength="8" @keyup="filterNumber($event)">
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                            </el-form-item>
                        </template>
                    </el-form>

                    <!-- 多规格 -->
                    <el-form :model="goodsEdit.goodsSkuData" label-width="120px" ref="skuFormRef" class="page-form">
                        <div class="el-form-item asterisk-left" v-show="goodsEdit.formData.spec_type == 'multi'">
                            <div class="el-form-item__label w-[120px] justify-end">{{ t('goodsSku') }}</div>
                            <div class="spec-wrap">
                                <!--规格项/规格值-->
                                <div class="spec-edit-list">
                                    <div class="spec-item" v-for="(item, index) in goodsEdit.goodsSpecFormat" :key="item.id">
                                        <div class="spec-name-wrap">
                                            <el-input v-model.trim="item.spec_name" clearable :placeholder="t('specNamePlaceholder')" class="input-width" maxlength="30" />
                                        </div>
                                        <div class="spec-value-wrap">
                                            <ul ref="specValueRef">
                                                <li :class="'draggable-element' + index" v-for="(specValue, specIndex) in item.values" :key="specValue.id">

                                                    <el-input v-model.trim="specValue.spec_value_name" clearable
                                                        :placeholder="t('specValueNamePlaceholder')" class="input-width"
                                                        :suffix-icon="Rank" maxlength="30"
                                                        @input="goodsEdit.specValueNameInputListener">
                                                    </el-input>
                                                    <el-icon class="icon" :size="20" color="#7b7b7b" @click="goodsEdit.deleteSpecValue(index, specIndex)">
                                                        <CircleCloseFilled />
                                                    </el-icon>
                                                </li>
                                            </ul>
                                            <span class="text-primary text-[14px] add-spec-value" @click="goodsEdit.addSpecValue(index)">{{ t('addSpecValue') }}</span>
                                            <div class="box"></div>
                                        </div>

                                        <el-icon class="del-spec" :size="20" color="#7b7b7b" @click="goodsEdit.deleteSpec(index)">
                                            <CircleCloseFilled />
                                        </el-icon>
                                    </div>

                                </div>

                                <div class="add-spec">
                                    <el-button type="primary"  @click="goodsEdit.addSpec">{{ t('addSpec') }}</el-button>
                                </div>

                                <!-- 批量设置 -->
                                <div class="batch-operation-sku" v-show="Object.keys(goodsEdit.goodsSkuData).length">
                                    <div class="flex flex-wrap">
                                        <label class="mt-[5px]">{{ t('batchOperationSku') }}</label>
                                        <div>
                                            <el-input v-model.trim="goodsEdit.batchOperation.point" clearable :placeholder="t('point')" class="!max-w-[180px] set-input mb-[10px]" maxlength="8" @keyup="filterNumber($event)" @blur="goodsEdit.batchOperation.point = $event.target.value">
                                                <template #append>{{ t('point') }}</template>
                                            </el-input>
                                            <span class="mb-[10px] mr-[10px]">+</span>
                                            <el-input v-model.trim="goodsEdit.batchOperation.price" clearable :placeholder="t('money')" class="!max-w-[180px] set-input mb-[10px]" maxlength="8">
                                                <template #append>{{ t('yuan') }}</template>
                                            </el-input>
                                        </div>
                                        <el-input v-if="!goodsEdit.isDisabledPrice()" v-model.trim="goodsEdit.batchOperation.market_price" clearable :placeholder="t('marketPrice')" class="set-input mb-[10px]" maxlength="8" />
                                        <el-input v-model.trim="goodsEdit.batchOperation.stock" clearable :placeholder="t('stock')" class="set-input mb-[10px]" maxlength="8"/>
                                        <el-button type="primary" @click="goodsEdit.saveBatch">{{ t('confirm') }}</el-button>
                                    </div>
                                </div>

                                <!--sku列表-->
                                <div class="sku-table" v-show="Object.keys(goodsEdit.goodsSkuData).length">

                                    <div class="el-table--fit el-table--default el-table" style="width: 100%;">

                                        <div class="el-table__inner-wrapper">
                                            <div class="el-table__header-wrapper">
                                                <table class="el-table__header" border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                                                    <thead>
                                                        <tr>
                                                            <th class="el-table__cell w-[50px]">
                                                                <div class="cell">
                                                                    <el-checkbox
                                                                        v-model="goodsEdit.formData.skuCheckAll"
                                                                        :indeterminate="goodsEdit.formData.skuIsIndeterminate"
                                                                        @change="goodsEdit.skuHandleCheckAllChange"
                                                                    ></el-checkbox>
                                                                </div>
                                                            </th>
                                                            <template v-for="(item, index) in goodsEdit.goodsSpecFormat" :key="index">
                                                                <th class="el-table__cell" v-if="item.spec_name">
                                                                    <div class="cell">{{ item.spec_name }}</div>
                                                                </th>
                                                            </template>

                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('image') }}</div>
                                                            </th>
                                                            <th class="el-table__cell w-[400px]">
                                                                <div class="cell">{{ t('pointPrcie') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('marketPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('stock') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('defaultSku') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('isJoin') }}</div>
                                                            </th>
                                                        </tr>
                                                    </thead>
                                                </table>
                                            </div>
                                            <el-checkbox-group v-model="goodsEdit.formData.skuCheckedCities" @change="goodsEdit.handleCheckedCitiesChange">
                                                <div class="el-table__body-wrapper text-[14px]">
                                                    <div class="el-scrollbar">
                                                        <div class="el-scrollbar__wrap el-scrollbar__wrap--hidden-default">
                                                            <div class="el-scrollbar__view" style="display: inline-block; vertical-align: middle;">

                                                                <table class="el-table__body" cellspacing="0" cellpadding="0" border="0" style="table-layout: fixed; width: 100%;">

                                                                        <tbody tabindex="-1">
                                                                            <tr class="el-table__row" v-for="(item, key, index) in goodsEdit.goodsSkuData" :key="key">
                                                                                <td class="el-table__cell w-[50px]">
                                                                                    <div class="cell">
                                                                                        <el-checkbox :label="key" :key="key">{{ '' }}</el-checkbox>
                                                                                    </div>
                                                                                </td>
                                                                                <template v-for="(c, cIndex) in goodsEdit.specData" :key="cIndex">
                                                                                    <td class="el-table__cell" :rowspan="c.rowSpan" v-if="c.index == index">
                                                                                        <div class="cell">{{ c.spec_value_name }}</div>
                                                                                    </td>
                                                                                </template>

                                                                                <td class="el-table__cell">
                                                                                    <div class="cell">
                                                                                        <upload-image v-model="item.sku_image" :limit="1" width="50px" height="50px" />
                                                                                    </div>
                                                                                </td>
                                                                                <td class="el-table__cell w-[400px]">
                                                                                    <div class="cell">
                                                                                        <el-form-item :prop="key + '.point'" :rules="goodsEdit.skuPriceRules(item.is_join)" class="sku-form-item-wrap">
                                                                                            <div class="flex items-center">
                                                                                                <el-input v-model.trim="item.point" clearable :placeholder="t('point')" class="set-input" maxlength="8"  @keyup="filterNumber($event)" @blur="item.point = $event.target.value">
                                                                                                    <template #append>{{ t('point') }}</template>
                                                                                                </el-input>
                                                                                                <span class="mx-[10px]">+</span>
                                                                                                <el-input v-model.trim="item.price" clearable :placeholder="t('money')" class="set-input" maxlength="8">
                                                                                                    <template #append>{{ t('yuan') }}</template>
                                                                                                </el-input>
                                                                                            </div>
                                                                                        </el-form-item>
                                                                                    </div>
                                                                                </td>
                                                                                <td class="el-table__cell">
                                                                                    <div class="cell">
                                                                                        <el-form-item :prop="key + '.market_price'" :rules="goodsEdit.skuMarketPriceRules(item.is_join)" class="sku-form-item-wrap">
                                                                                            <el-input v-model.trim="item.market_price" clearable placeholder="0.00" maxlength="8" />
                                                                                        </el-form-item>
                                                                                    </div>
                                                                                </td>

                                                                                <td class="el-table__cell">
                                                                                    <div class="cell">
                                                                                        <el-form-item :prop="key + '.stock'" :rules="goodsEdit.skuStockRules(item.is_join)" class="sku-form-item-wrap" >
                                                                                            <el-input v-model.trim="item.stock" clearable placeholder="0" @input="goodsEdit.specStockSum" maxlength="8"/>
                                                                                        </el-form-item>
                                                                                    </div>
                                                                                </td>
                                                                                <td class="el-table__cell">
                                                                                    <div class="cell">
                                                                                        <el-switch v-model="item.is_default" :active-value="1" :inactive-value="0" @change="goodsEdit.specValueIsDefaultChangeListener($event, key)" />
                                                                                    </div>
                                                                                </td>
                                                                                <td class="el-table__cell">
                                                                                    <div class="cell">
                                                                                        <el-switch v-model="item.is_join" :disabled="item.is_default == 1"  :active-value="1" :inactive-value="0" />
                                                                                    </div>
                                                                                </td>
                                                                            </tr>
                                                                        </tbody>
                                                                </table>

                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </el-checkbox-group>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </el-form>
                </el-tab-pane>
                <!-- <el-tab-pane :label="t('deliveryTab')" name="delivery">
                    <el-form :model="goodsEdit.formData" label-width="120px" ref="deliveryFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('deliveryType')" prop="delivery_type">
                            <div>
                                <el-checkbox-group v-model="goodsEdit.formData.delivery_type">
                                    <el-checkbox v-for="(item, index) in deliveryTypeCheckBox" :key="index" :label="item.key" >{{ item.name }}</el-checkbox>
                                </el-checkbox-group>
                            </div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane> -->
                <el-tab-pane :label="t('goodsDesc')" name="detail">
                    <el-form :model="goodsEdit.formData" label-width="120px" ref="detailFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('goodsDesc')" prop="goods_desc">
                            <div class="flex-1">
                                <editor v-model="goodsEdit.formData.goods_desc" :height="600" class="editor-width" @handleBlur="goodsEdit.handleBlur" />
                            </div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
        </el-card>
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="save()">{{ t('save') }}</el-button>
                <el-button @click="goodsEdit.back()">{{ t('back') }}</el-button>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { Rank, ArrowLeft } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { deepClone, filterNumber } from '@/utils/common'
import { getGoodsInit, getGoodsFastInit } from '@/addon/points_mall/api/admin/goods'
import { useGoodsEdit } from '@/addon/points_mall/views/admin/goods/public/js/useGoodsEdit'

const route = useRoute()
const router = useRouter()
const basicFormRef = ref<FormInstance>()
const priceStockFormRef = ref<FormInstance>()
const deliveryFormRef = ref<FormInstance>()
const detailFormRef = ref<FormInstance>()
const skuFormRef = ref<FormInstance>()
// 拖拽规格值
const specValueRef = ref()

const goodsEdit = useGoodsEdit({
    getFormRef () {
        return {
            basicFormRef: basicFormRef.value,
            priceStockFormRef: priceStockFormRef.value,
            detailFormRef: detailFormRef.value,
            skuFormRef: skuFormRef.value,
            specValueRef: specValueRef.value
        }
    },
    // 表单验证规则
    getFormRules (formData: any, regExp: any) {
        return {
            delivery_type: [
                { required: true, message: t('deliveryTypePlaceholder'), trigger: 'blur' }
            ]
        }
    },
    // 表单验证
    getVerify () {
        return [
        ]
    },
    formData: {}
})

// 配送方式复选框
const deliveryTypeCheckBox = reactive([{
    key: 'express',
    name: '物流配送'
}])

const curDisabledArea = ref<any>([]) // 禁用区域
const source = ref(route.query.source) || ''
if (source.value == 'mall') {
    getGoodsFastInit({
        goods_id: goodsEdit.formData.goods_id
    }).then((res) => {
        const data = res.data
        if (data) {
            goodsEdit.handleGoodsInit(data)
            if (goodsEdit.formData.goods_id && data.goods_info) {
                // 配送设置
                goodsEdit.formData.delivery_type = data.goods_info.delivery_type || ['express']
            }
        }
    })
} else {
    // 加载初始化数据
    getGoodsInit({
        goods_id: goodsEdit.formData.goods_id
    }).then((res) => {
        const data = res.data
        if (data) {
            curDisabledArea.value = deepClone(data.disable_edit_area)

            goodsEdit.handleGoodsInit(data)
            if (goodsEdit.formData.goods_id && data.goods_info) {
                // 配送设置
                goodsEdit.formData.delivery_type = data.goods_info.delivery_type || ['express']
            }
        }
    })
}


const save = () => {
    goodsEdit.save()
}
</script>

<style lang="scss" scoped>
@import './public/css/goods_edit.scss';
</style>
<style lang="scss">
.edui-default .edui-editor{
    z-index: 9 !important;
}
.el-cascader__tags.is-validate{
    right: 30px !important;
}
</style>