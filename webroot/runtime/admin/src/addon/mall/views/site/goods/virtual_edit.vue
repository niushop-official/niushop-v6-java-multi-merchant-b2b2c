<template>
    <div class="main-container">
        <el-card class="box-card !border-none" shadow="never">
            <el-page-header :content="goodsEdit.formData.goods_id ? t('updateGoods') : t('addGoods')" :icon="ArrowLeft" @back="goodsEdit.back()" />
        </el-card>

        <el-card class="box-card mt-[15px] !border-none" shadow="never">

            <el-tabs v-model="goodsEdit.activeName" @tab-click="goodsEdit.tabHandleClick">
                <el-tab-pane :label="t('basicInfoTab')" name="basic">

                    <el-form :model="goodsEdit.formData" label-width="120px" ref="basicFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('goodsType')" v-if="goodsEdit.formData.goods_id">
                            <template v-for="(item) in goodsEdit.goodsType" :key="item.type">
                                <div class="goods-type-wrap" :class="[goodsEdit.formData.goods_type == item.type ? 'selected' : 'disabled']">
                                    <div class="goods-type-name">{{ item.name }}</div>
                                    <div class="goods-type-desc">({{ item.desc }})</div>
                                    <template v-if="goodsEdit.formData.goods_type == item.type">
                                        <div class="triangle"></div>
                                        <div class="selected">✓</div>
                                    </template>
                                </div>
                            </template>
                        </el-form-item>
                        <el-form-item :label="t('goodsType')" v-else>
                            <div class="goods-type-wrap" :class="{ 'selected': goodsEdit.formData.goods_type == item.type }" v-for="(item) in goodsEdit.goodsType" :key="item.type" @click="goodsEdit.changeGoodsType(item)">
                                <div class="goods-type-name">{{ item.name }}</div>
                                <div class="goods-type-desc">({{ item.desc }})</div>
                                <template v-if="goodsEdit.formData.goods_type == item.type">
                                    <div class="triangle"></div>
                                    <div class="selected">✓</div>
                                </template>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('同步供货商设置')" prop="supply_sync" v-if="goodsEdit.formData.supply_sync_show">
                            <el-switch v-model="goodsEdit.formData.supply_sync" :active-value="1" :inactive-value="0" @change="goodsEdit.handleSupply" />
                        </el-form-item>
                        <el-form-item :label="t('goodsName')" prop="goods_name">
                            <el-input v-model.trim="goodsEdit.formData.goods_name" clearable :placeholder="t('goodsNamePlaceholder')" class="input-width" maxlength="60" show-word-limit  :disabled="goodsEdit.isSupplyDisabled('goods_name')"/>
                        </el-form-item>
                        <el-form-item :label="t('subTitle')" prop="sub_title">
                            <el-input v-model.trim="goodsEdit.formData.sub_title" clearable :placeholder="t('subTitlePlaceholder')" class="input-width" maxlength="80" show-word-limit  :disabled="goodsEdit.isSupplyDisabled('sub_title')"/>
                        </el-form-item>
                        <el-form-item :label="t('goodsImage')" prop="goods_image">
                            <upload-image v-model="goodsEdit.formData.goods_image" :limit="10" :disabled="goodsEdit.isSupplyDisabled('goods_image')" />
                        </el-form-item>
                        <el-form-item :label="t('goodsVideo')">
                            <div class="flex flex-col">
                                <upload-video v-model="goodsEdit.formData.goods_video" :limit="1" :disabled="goodsEdit.isSupplyDisabled('goods_video')"/>
                                <el-input v-model="goodsEdit.formData.goods_video" clearable :placeholder="t('在此输入外链视频地址')" class="input-width mt-[10px]" :disabled="goodsEdit.isSupplyDisabled('goods_video')"/>
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
                        <el-form-item :label="t('goodsMallCategory')" prop="goods_mall_category">
                            <el-cascader v-model="goodsEdit.formData.goods_mall_category" :options="goodsEdit.goodsMallCategoryOptions" :placeholder="t('goodsMallCategoryPlaceholder')" clearable :props="{ value: 'value', label: 'label', emitPath:false }" :disabled="goodsEdit.isSupplyDisabled('goods_mall_category')"/>
                        </el-form-item>
                        <el-form-item :label="t('goodsCategory')" prop="goods_category">
                            <el-cascader v-model="goodsEdit.formData.goods_category" :options="goodsEdit.goodsCategoryOptions" :props="goodsEdit.goodsCategoryProps" clearable filterable @change="goodsEdit.categoryHandleChange" :disabled="goodsEdit.isSupplyDisabled('goods_category')" />
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="goodsEdit.refreshGoodsCategory(true)">{{ t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="goodsEdit.toGoodsCategoryEvent">{{ t('addGoodsCategory') }}</span>
                            </div>
                        </el-form-item>

                        <el-form-item :label="t('brand')">
                            <el-select v-model="goodsEdit.formData.brand_id" :placeholder="t('brandPlaceholder')" clearable :disabled="goodsEdit.isSupplyDisabled('brand_id')" filterable remote reserve-keyword :remote-method="goodsEdit.refreshGoodsBrand">
                                <el-option v-for="item in goodsEdit.brandOptions" :key="item.brand_id" :label="item.brand_name" :value="item.brand_id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="t('label')">
                            <div v-if="goodsEdit.labelOptions.length == 0" class="text-[12px] text-[#999] leading-[20px]">{{ t('labelEmpty') }}</div>
                            <el-checkbox-group v-else v-model="goodsEdit.formData.label_ids" :disabled="goodsEdit.isSupplyDisabled('label_ids')">
                                <el-checkbox :label="item.label_id" v-for="(item, index) in goodsEdit.labelOptions" :key="index">{{ item.label_name }}</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                        <el-form-item :label="t('goodsService')">
                            <el-checkbox-group v-model="goodsEdit.formData.service_ids" :disabled="goodsEdit.isSupplyDisabled('service_ids')">
                                <el-checkbox :label="item.service_id" v-for="(item, index) in goodsEdit.serviceOptions" :key="index" >{{ item.service_name }}</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>

                        <el-form-item :label="t('supplier')" v-if="goodsEdit.formData.addon_shop_supplier && goodsEdit.formData.addon_shop_supplier.status == 1">
                            <el-select v-model="goodsEdit.formData.supplier_id" :placeholder="t('supplierPlaceholder')" clearable :disabled="goodsEdit.isSupplyDisabled('supplier_id')">
                                <el-option v-for="item in goodsEdit.supplierOptions" :key="item.supplier_id" :label="item.supplier_name" :value="item.supplier_id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="goodsEdit.refreshSupplier">{{ t('refresh') }}</span>
                                <span class="cursor-pointer text-primary" @click="goodsEdit.toSupplierEvent">{{ t('addSupplier') }}</span>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('status')">
                            <el-radio-group v-model="goodsEdit.formData.status" :disabled="goodsEdit.isSupplyDisabled('status')">
                                <el-radio label="1">{{ t('statusOn') }}</el-radio>
                                <el-radio label="0">{{ t('statusOff') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('unit')" prop="unit">
                            <el-input v-model.trim="goodsEdit.formData.unit" clearable :placeholder="t('unitPlaceholder')" class="input-width" show-word-limit maxlength="6" :disabled="goodsEdit.isSupplyDisabled('unit')" />
                        </el-form-item>
                        <el-form-item :label="t('virtualSaleNum')" prop="virtual_sale_num">
                            <div>
                                <el-input v-model.trim="goodsEdit.formData.virtual_sale_num" clearable :placeholder="t('virtualSaleNumPlaceholder')" class="input-width" show-word-limit maxlength="8" @keyup="filterNumber($event)" @blur="goodsEdit.formData.virtual_sale_num = $event.target.value" :disabled="goodsEdit.isSupplyDisabled('virtual_sale_num')">
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : '件' }}</template>
                                </el-input>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('virtualSaleNumDesc') }}</div>
                            </div>
                        </el-form-item>

                        <el-form-item :label="t('setDeliverGoods')">
                            <el-radio-group v-model="goodsEdit.formData.virtual_auto_delivery" :disabled="goodsEdit.isSupplyDisabled('virtual_auto_delivery')">
                                <el-radio label="1" :disabled="isDisabledVirtual">{{ t('autoDeliverGoods') }}</el-radio>
                                <el-radio label="0" :disabled="isDisabledVirtual">{{ t('handDeliverGoods') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item :label="t('setTakeGoods')">
                            <div>
                                <el-radio-group v-model="goodsEdit.formData.virtual_receive_type" :disabled="goodsEdit.isSupplyDisabled('virtual_receive_type')">
                                    <el-radio label="auto" :disabled="isDisabledVirtual">{{ t('autoTakeGoods') }}</el-radio>
                                    <el-radio label="artificial" :disabled="isDisabledVirtual">{{ t('handTakeGoods') }}</el-radio>
                                    <el-radio label="verify" :disabled="isDisabledVirtual">{{ t('verifyTakeGoods') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]" v-show="goodsEdit.formData.virtual_receive_type == 'verify'">{{ t('virtualSetTips') }}</div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('verifyVirtualIndate')" v-if="goodsEdit.formData.virtual_receive_type == 'verify'">
                            <el-radio-group v-model="goodsEdit.formData.virtual_verify_type" :disabled="goodsEdit.isSupplyDisabled('virtual_verify_type')">
                                <el-radio label="0">{{ t('verifyVirtualIndateOne') }}</el-radio>
                                <el-radio label="1">{{ t('verifyVirtualIndateTwo') }}</el-radio>
                                <el-radio label="2">{{ t('verifyVirtualIndateThree') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>

                        <el-form-item :label="t('virtualIndate')" v-if="goodsEdit.formData.virtual_receive_type == 'verify' && goodsEdit.formData.virtual_verify_type == 1" prop="virtual_indate_day">
                            <el-input v-model.trim="goodsEdit.formData.virtual_indate_day" clearable class="input-width" show-word-limit maxlength="5" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('virtual_indate_day')">
                                <template #append>{{t('sky')}}</template>
                            </el-input>
                        </el-form-item>
                        <el-form-item :label="t('virtualIndate')" v-if="goodsEdit.formData.virtual_receive_type == 'verify' && goodsEdit.formData.virtual_verify_type == 2" prop="virtual_indate">
                             <div>
                                 <el-date-picker
                                    class="input-width"
                                    value-format="YYYY-MM-DD HH:mm:ss"
                                    v-model="goodsEdit.formData.virtual_indate"
                                    type="datetime"
                                    :disabled-date="disabledPastDates"
                                    :disabled="goodsEdit.isSupplyDisabled('virtual_indate')"
                                />
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{t('verifyHint')}}</div>
                             </div>
                        </el-form-item>

                        <el-form-item :label="t('verifyVerifyMobile')">
                            <el-radio-group v-model="goodsEdit.formData.virtual_verify_mobile" :disabled="goodsEdit.isSupplyDisabled('virtual_verify_mobile')">
                                <el-radio label="1">{{ t('open') }}</el-radio>
                                <el-radio label="0">{{ t('close') }}</el-radio>
                            </el-radio-group>
                        </el-form-item>

                        <el-form-item :label="t('sort')" prop="shop_sort">
                            <el-input v-model.trim="goodsEdit.formData.shop_sort" clearable :placeholder="t('sortPlaceholder')" class="input-width" show-word-limit maxlength="8" @keyup="filterNumber($event)" @blur="goodsEdit.formData.shop_sort = $event.target.value" :disabled="goodsEdit.isSupplyDisabled('shop_sort')"/>
                        </el-form-item>

                    </el-form>

                </el-tab-pane>
                <el-tab-pane :label="t('priceStockTab')" name="price_stock">
                    <el-form :model="goodsEdit.formData" label-width="120px" ref="priceStockFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('specType')" prop="spec_type">
                            <div>
                                <el-radio-group v-model="goodsEdit.formData.spec_type" :disabled="goodsEdit.isSupplyDisabled('spec_type')">
                                    <el-radio label="single" :disabled="goodsEdit.isDisabledPrice()">{{ t('singleSpec') }}</el-radio>
                                    <el-radio label="multi" :disabled="goodsEdit.isDisabledPrice()">{{ t('multiSpec') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]" v-if="goodsEdit.isDisabledPrice()">{{ t('participateInActiveDisableTips') }}</div>
                            </div>
                        </el-form-item>

                        <template v-if="goodsEdit.formData.spec_type == 'single'">
                            <el-form-item :label="t('price')" prop="price">
                                <el-input v-model.trim="goodsEdit.formData.price" clearable placeholder="0.00" class="input-width" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('price')" v-if="goodsEdit.formData.supply_goods_id">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                                <el-input v-model.trim="goodsEdit.formData.price" clearable placeholder="0.00" class="input-width" maxlength="8" :disabled="goodsEdit.isDisabledPrice()" v-else>
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>

                            <el-form-item :label="t('marketPrice')" prop="market_price">
                                <el-input v-model.trim="goodsEdit.formData.market_price" clearable placeholder="0.00" class="input-width" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('market_price')" >
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>

                            <el-form-item :label="goodsEdit.formData.supply_goods_id ? t('supplyPrice') : t('costPrice')" prop="cost_price">
                                <el-input v-model.trim="goodsEdit.formData.cost_price" clearable placeholder="0.00" class="input-width" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('cost_price')">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('minPrice')" prop="min_price" v-if="goodsEdit.formData.supply_goods_id">
                                <el-input v-model.trim="goodsEdit.formData.min_price" clearable placeholder="0.00" class="input-width" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('min_price')">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('maxPrice')" prop="max_price" v-if="goodsEdit.formData.supply_goods_id">
                                <el-input v-model.trim="goodsEdit.formData.max_price" clearable placeholder="0.00" class="input-width" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('max_price')">
                                    <template #append>{{ t('yuan') }}</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('goodsStock')" prop="stock">
                                <el-input v-model.trim="goodsEdit.formData.stock" clearable :placeholder="t('goodsStockPlaceholder')" class="input-width" maxlength="8" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('stock')" v-if="goodsEdit.formData.supply_goods_id">
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                                <el-input v-model.trim="goodsEdit.formData.stock" clearable :placeholder="t('goodsStockPlaceholder')" class="input-width" maxlength="8" @keyup="filterNumber($event)" :disabled="goodsEdit.isDisabledPrice()" v-else>
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                            </el-form-item>
                            <el-form-item :label="t('skuNo')">
                                <el-input v-model.trim="goodsEdit.formData.sku_no" clearable :placeholder="t('skuNoPlaceholder')" @keyup="goodsEdit.filterSpecial($event)" class="input-width" maxlength="50" :disabled="goodsEdit.isSupplyDisabled('sku_no')" />
                            </el-form-item>
                        </template>

                    </el-form>

                    <!-- 多规格 -->
                    <el-form :model="goodsEdit.goodsSkuData" label-width="120px" ref="skuFormRef" class="page-form">

                        <div class="el-form-item asterisk-left" v-show="goodsEdit.formData.spec_type == 'multi'">
                            <div class="el-form-item__label w-[120px]">{{ t('goodsSku') }}</div>
                            <div class="spec-wrap">
                                <!--规格项/规格值-->
                                <div class="spec-edit-list relative">
                                    <div class="absolute top-0 left-0 right-0 bottom-0 z-[100] bg-[rgba(221,221,221,0.3)] cursor-not-allowed" v-if="curDisabledArea.includes('goods_spec_format')"></div>
                                    <div class="spec-item" v-for="(item, index) in goodsEdit.goodsSpecFormat" :key="item.id">
                                        <div class="spec-name-wrap">
                                            <el-input v-model.trim="item.spec_name" clearable :placeholder="t('specNamePlaceholder')" class="input-width" maxlength="20"  @input="goodsEdit.specValueNameInputListener" />
                                        </div>
                                        <div class="spec-value-wrap">
                                            <ul ref="specValueRef">
                                                <li :class="'draggable-element' + index" v-for="(specValue, specIndex) in item.values" :key="specValue.id">

                                                    <el-input v-model.trim="specValue.spec_value_name" clearable
                                                        :placeholder="t('specValueNamePlaceholder')" class="input-width"
                                                        :suffix-icon="Rank" maxlength="20"
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
                                    <el-button type="primary" :disabled="curDisabledArea.includes('goods_spec_format')" @click="goodsEdit.addSpec">{{ t('addSpec') }}</el-button>
                                </div>

                                <!-- 批量设置 -->
                                <div class="batch-operation-sku" v-show="Object.keys(goodsEdit.goodsSkuData).length">
                                    <label>{{ t('batchOperationSku') }}</label>

                                    <!-- <el-select v-model="goodsEdit.batchOperation.spec" class="set-spec-select">
                                        <el-option :label="t('all')" value="" />
                                        <template v-for="(item, key) in goodsEdit.goodsSkuData" :key="key">
                                            <el-option v-if="item.spec_name" :label="item.spec_name" :value="key" />
                                        </template>
                                    </el-select> -->

                                    <el-input v-if="!goodsEdit.isDisabledPrice()" v-model.trim="goodsEdit.batchOperation.price" clearable :placeholder="t('price')" class="set-input" maxlength="8"  :disabled="goodsEdit.isSupplyDisabled('price')"/>
                                    <el-input v-model.trim="goodsEdit.batchOperation.market_price" clearable :placeholder="t('marketPrice')" class="set-input" maxlength="8"  :disabled="goodsEdit.isSupplyDisabled('market_price')" />
                                    <el-input v-model.trim="goodsEdit.batchOperation.cost_price" clearable :placeholder="goodsEdit.formData.supply_goods_id ? t('supplyPrice') : t('costPrice')" class="set-input" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('cost_price')"  />
                                    <el-input v-model.trim="goodsEdit.batchOperation.min_price" clearable :placeholder="t('minPrice')" class="set-input" maxlength="8" v-if="goodsEdit.formData.supply_goods_id" :disabled="goodsEdit.isSupplyDisabled('min_price')"/>
                                    <el-input v-model.trim="goodsEdit.batchOperation.max_price" clearable :placeholder="t('maxPrice')" class="set-input" maxlength="8" v-if="goodsEdit.formData.supply_goods_id" :disabled="goodsEdit.isSupplyDisabled('max_price')"/>
                                    <el-input  v-if="!goodsEdit.isDisabledPrice()" v-model.trim="goodsEdit.batchOperation.stock" clearable :placeholder="t('stock')" class="set-input" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('stock')"  />
                                    <el-input v-model.trim="goodsEdit.batchOperation.sku_no" clearable maxlength="50" :placeholder="t('skuNo')" class="set-input" :disabled="goodsEdit.isSupplyDisabled('sku_no')" />
                                    <el-button type="primary" @click="goodsEdit.saveBatch">{{ t('confirm') }}</el-button>
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
                                                                <div class="cell">{{ t('isSell') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('image') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('price') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('marketPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ goodsEdit.formData.supply_goods_id ? t('supplyPrice') : t('costPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell" v-if="goodsEdit.formData.supply_goods_id">
                                                                <div class="cell">{{ t('minPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell" v-if="goodsEdit.formData.supply_goods_id">
                                                                <div class="cell">{{ t('maxPrice') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('stock') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('skuNo') }}</div>
                                                            </th>
                                                            <th class="el-table__cell">
                                                                <div class="cell">{{ t('defaultSku') }}</div>
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
                                                                                    <el-switch v-model="item.is_sell" :active-value="1" :inactive-value="0" :disabled="goodsEdit.isSupplyDisabled('is_sell')"  />
                                                                                </div>
                                                                            </td>

                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <upload-image v-model="item.sku_image" :limit="1" width="50px" height="50px" :disabled="goodsEdit.isSupplyDisabled('sku_image')" />
                                                                                </div>
                                                                            </td>

                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <el-form-item :prop="key + '.price'" :rules="goodsEdit.skuPriceRules(item)" class="sku-form-item-wrap">
                                                                                        <el-input v-model.trim="item.price" clearable placeholder="0.00" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('price')" v-if="goodsEdit.formData.supply_goods_id" />
                                                                                        <el-input v-model.trim="item.price" clearable placeholder="0.00" maxlength="8" :disabled="goodsEdit.isDisabledPrice()" v-else/>
                                                                                    </el-form-item>
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <el-form-item :prop="key + '.market_price'" :rules="goodsEdit.skuMarketPriceRules()" class="sku-form-item-wrap">
                                                                                        <el-input v-model.trim="item.market_price" clearable placeholder="0.00" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('market_price')" />
                                                                                    </el-form-item>
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <el-form-item :prop="key + '.cost_price'" :rules="goodsEdit.skuCostPriceRules()" class="sku-form-item-wrap">
                                                                                        <el-input v-model.trim="item.cost_price" clearable placeholder="0.00" maxlength="8"  :disabled="goodsEdit.isSupplyDisabled('cost_price')"/>
                                                                                    </el-form-item>
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell" v-if="goodsEdit.formData.supply_goods_id">
                                                                                <div class="cell">
                                                                                    <el-form-item :prop="key + '.min_price'" class="sku-form-item-wrap">
                                                                                        <el-input v-model.trim="item.min_price" clearable placeholder="0.00" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('min_price')"/>
                                                                                    </el-form-item>
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell" v-if="goodsEdit.formData.supply_goods_id">
                                                                                <div class="cell">
                                                                                    <el-form-item :prop="key + '.max_price'" class="sku-form-item-wrap">
                                                                                        <el-input v-model.trim="item.max_price" clearable placeholder="0.00" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('max_price')"/>
                                                                                    </el-form-item>
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <el-form-item :prop="key + '.stock'" :rules="goodsEdit.skuStockRules()" class="sku-form-item-wrap">
                                                                                        <el-input v-model.trim="item.stock" clearable placeholder="0" @input="goodsEdit.specStockSum" maxlength="8" :disabled="goodsEdit.isSupplyDisabled('stock')" v-if="goodsEdit.formData.supply_goods_id" />
                                                                                        <el-input v-model.trim="item.stock" clearable placeholder="0" @input="goodsEdit.specStockSum" maxlength="8" :disabled="goodsEdit.isDisabledPrice()" v-else />
                                                                                    </el-form-item>
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <el-input v-model.trim="item.sku_no" clearable maxlength="50" :disabled="goodsEdit.isSupplyDisabled('sku_no')" />
                                                                                </div>
                                                                            </td>
                                                                            <td class="el-table__cell">
                                                                                <div class="cell">
                                                                                    <el-switch v-model="item.is_default" :active-value="1" :inactive-value="0" @change="goodsEdit.specValueIsDefaultChangeListener($event, key)" :disabled="goodsEdit.isSupplyDisabled('is_default')"  />
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
                        <el-form-item :label="t('memberDiscount')">
                            <div>
                                <el-radio-group v-model="goodsEdit.formData.member_discount" :disabled="goodsEdit.isSupplyDisabled('member_discount')" >
                                    <el-radio label="">{{ t('nonparticipation') }}</el-radio>
                                    <el-radio label="discount">{{ t('discount') }}</el-radio>
                                    <el-radio label="fixed_price">{{ t('fixedPrice') }}</el-radio>
                                </el-radio-group>
                                <div class="text-[12px] text-[#999] leading-[20px]" v-if="goodsEdit.formData.member_discount == 'discount'">{{t('discountHint')}}</div>
                                <div class="text-[12px] text-[#999] leading-[20px]" v-if="goodsEdit.formData.member_discount == 'fixed_price'">{{t('fixedPriceHint')}}</div>
                            </div>
                        </el-form-item>
                    </el-form>
                    <el-form :model="goodsEdit.formData" label-width="120px" ref="priceStockCommonFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('isLimit')">
                            <div>
                                <el-switch v-model="goodsEdit.formData.is_limit" :active-value="1" :inactive-value="0"  :disabled="goodsEdit.isSupplyDisabled('is_limit')" />
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('isLimitTips') }}</div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('limitType')" prop="limit_type" v-if="goodsEdit.formData.is_limit == '1'">
                            <div>
                                <el-radio-group v-model="goodsEdit.formData.limit_type" :disabled="goodsEdit.isSupplyDisabled('limit_type')">
                                    <el-radio :label="1">{{ t('singleTime') }}</el-radio>
                                    <el-radio :label="2">{{ t('singlePerson') }}</el-radio>
                                </el-radio-group>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('limitTypeTips') }}</div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('maxBuy')" prop="max_buy" v-if="goodsEdit.formData.is_limit == '1'">
                            <div>
                                <el-input v-model.trim="goodsEdit.formData.max_buy" clearable :placeholder="t('maxBuyPlaceholder')" class="input-width" maxlength="8" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('max_buy')">
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('maxBuyWarnTips') }}</div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('minBuy')" prop="min_buy">
                            <div>
                                <el-input v-model.trim="goodsEdit.formData.min_buy" clearable class="input-width" maxlength="8" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('min_buy')">
                                    <template #append>{{ goodsEdit.formData.unit ? goodsEdit.formData.unit : t('defaultUnit') }}</template>
                                </el-input>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('minBuyTips') }}</div>
                            </div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('goodsArguments')" name="goods_arguments">
                    <el-form :model="goodsEdit.formData" label-width="130px" ref="goodsArgumentsFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('mallGoodsArgumentsTemp')">
                            <el-select class="mr-[10px]" v-model="goodsEdit.formData.attr_ids" :placeholder="t('goodsArgumentsTempPlaceholder')" clearable multiple @change="goodsEdit.mallAttrChange" @clear="goodsEdit.mallAttrChange()" :disabled="goodsEdit.isSupplyDisabled('attr_ids')">
                                <el-option v-for="item in goodsEdit.mallAttrOptions" :key="item.attr_id" :label="item.attr_name" :value="item.attr_id" />
                            </el-select>
                        </el-form-item>
                        <el-form-item :label="t('shopGoodsArgumentsTemp')">
                            <div>
                                <div class="flex items-center" v-if="goodsEdit.isSupplyDisabled('shop_attr_ids')">
                                    <el-input v-model.trim="goodsEdit.formData.shop_attr_name" clearable :placeholder="t('goodsArgumentsTempPlaceholder')" class="!w-[215px]" show-word-limit disabled="true" />
                                </div>
                                <div class="flex items-center" v-else>
                                    <el-select v-model="goodsEdit.formData.shop_attr_ids" :placeholder="t('goodsArgumentsTempPlaceholder')" clearable multiple @change="goodsEdit.shopAttrChange" @clear="goodsEdit.shopAttrChange()">
                                        <el-option v-for="item in goodsEdit.shopAttrOptions" :key="item.attr_id" :label="item.attr_name" :value="item.attr_id" />
                                    </el-select>
                                </div>
                                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{t('goodsArgumentsTempHint')}}</div>
                                <div class="relative">
                                    <div class="absolute top-0 left-0 right-0 bottom-0 z-[100] bg-[rgba(221,221,221,0.3)] cursor-not-allowed" v-if="goodsEdit.isSupplyDisabled('shop_attr_ids')"></div>
                                    <table class="attr-table mt-[10px]">
                                        <colgroup>
                                            <col width="30%">
                                            <col width="40%">
                                            <col width="20%">
                                            <col width="10%">
                                        </colgroup>
                                        <thead class="bg-[#f7f7f7]">
                                            <tr>
                                                <th>{{t('argumentsName')}}</th>
                                                <th>{{t('argumentsValue')}}</th>
                                                <th class="prompt-block">
                                                    <div class="flex items-center">
                                                        <span>{{t('sort')}}</span>
                                                        <el-tooltip class="box-item" effect="dark" :content="t('argumentsSortHint')" placement="right">
                                                            <span class="iconfont iconwenhao cursor-pointer ml-[3px] mt-[3px] !text-[14px]"></span>
                                                        </el-tooltip>
                                                    </div>
                                                </th>
                                                <th>{{t('operation')}}</th>
                                            </tr>
                                        </thead>
                                        <tbody class="attr-new">
                                            <template v-if="goodsEdit.attrTableData.length">
                                                <tr class="goods-attr-tr goods-new-attr-tr" v-for="(item,index) in goodsEdit.attrTableData" :key="index">
                                                    <td v-if="item.attr_value_id > 0">{{item.attr_value_name}}</td>
                                                    <td v-else>
                                                        <el-input v-model.trim="item.attr_value_name" maxlength="20" show-word-limit clearable/>
                                                    </td>
                                                    <td>
                                                        <el-input v-if="item.type == 'text'" maxlength="20" show-word-limit v-model.trim="item.select_child_val" clearable/>
                                                        <div v-else-if="item.type == 'radio'">
                                                            <el-radio-group v-model="item.select_child_name" @change="goodsEdit.attrRadioChange(index,$event)">
                                                                <el-radio v-for="(childItem,childIndex) in item.child" :key="childIndex" :label="childItem.id">{{ childItem.name }}</el-radio>
                                                            </el-radio-group>
                                                        </div>
                                                        <div v-else-if="item.type == 'checkbox'">
                                                            <el-checkbox-group v-model="item.select_child_name" @change="goodsEdit.attrCheckboxChange(index,$event)">
                                                                <el-checkbox v-for="(childItem,childIndex) in item.child" :key="childIndex" :label="childItem.id">{{ childItem.name }}</el-checkbox>
                                                            </el-checkbox-group>
                                                        </div>
                                                    </td>
                                                    <td>
                                                        <el-input v-model.trim="item.sort" maxlength="6" show-word-limit clearable @keyup="filterNumber($event)"/>
                                                    </td>
                                                    <td>
                                                        <span class="cursor-pointer text-[var(--el-color-primary)]" @click="goodsEdit.delAttr(index)">{{t('delAttr')}}</span>
                                                    </td>
                                                </tr>
                                            </template>
                                            <tr v-else>
                                                <td colspan="4" class="text-center">{{t('noData')}}</td>
                                            </tr>
                                        </tbody>
                                    </table>
                                </div>
                                <el-button class="mt-[15px]" :disabled="goodsEdit.isSupplyDisabled('shop_attr_ids')" @click="goodsEdit.addAttr">{{t('addGoodsArguments')}}</el-button>
                            </div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('goodsDesc')" name="detail">
                    <el-form :model="goodsEdit.formData" label-width="120px" ref="detailFormRef" :rules="goodsEdit.formRules" class="page-form">
                        <el-form-item :label="t('goodsDesc')" prop="goods_desc">
                            <div class="absolute top-0 left-0 right-0 bottom-0 z-[100] bg-[rgba(221,221,221,0.3)] cursor-not-allowed" v-if="goodsEdit.isSupplyDisabled('goods_desc')"></div>
                            <div class="flex-1">
                                <editor v-model="goodsEdit.formData.goods_desc" :height="600" @handleBlur="goodsEdit.handleBlur"  class="editor-width" />
                            </div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('advancedSetup')" name="diy">
                    <el-form :model="goodsEdit.formData" label-width="120px" class="page-form">
                        <el-form-item :label="t('diyDetailTemplate')">
                            <el-select v-model="goodsEdit.formData.diy_detail_id" :placeholder="t('diyDetailTemplatePlaceholder')" clearable>
                                <el-option v-for="item in goodsEdit.detailTemplateOptions" :key="item.id" :label="item.page_title" :value="item.id" />
                            </el-select>
                            <div class="ml-[10px]">
                                <span class="cursor-pointer text-primary mr-[10px]" @click="goodsEdit.refreshDetailTemplate(true)">{{ t('refresh') }}</span>
                            </div>
                        </el-form-item>

                        <el-form-item :label="t('diyForm')">
                            <div>
                                <div class="flex items-center" v-if="goodsEdit.isSupplyDisabled('form_id')">
                                    <el-input v-model.trim="goodsEdit.formData.page_title" clearable :placeholder="t('diyFormPlaceholder')" class="!w-[215px]" show-word-limit disabled="true" />
                                </div>
                                <div class="flex items-center" v-else>
                                    <el-select v-model="goodsEdit.formData.form_id" :placeholder="t('diyFormPlaceholder')" clearable>
                                        <el-option v-for="item in goodsEdit.diyFormOptions" :key="item.form_id" :label="item.page_title" :value="item.form_id" />
                                    </el-select>
                                    <div class="ml-[10px]">
                                        <span class="cursor-pointer text-primary mr-[10px]" @click="goodsEdit.refreshDiyForm(true)">{{ t('refresh') }}</span>
                                        <span class="cursor-pointer text-primary" @click="goodsEdit.toDiyFormEvent">{{ t('addDiyForm') }}</span>
                                    </div>
                                </div>
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

<script lang="ts" setup>
import { reactive, ref, computed } from 'vue'
import { t } from '@/lang'
import { FormInstance } from 'element-plus'
import { Rank, ArrowLeft } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { deepClone, filterNumber, timeStampTurnTime } from '@/utils/common'
import { addVirtualGoods, editVirtualGoods, getVirtualGoodsInit } from '@/addon/mall/api/site/goods'
import { useGoodsEdit } from './public/js/useGoodsEdit'

const route = useRoute()
const router = useRouter()

const basicFormRef = ref<FormInstance>()
const priceStockFormRef = ref<FormInstance>()
const detailFormRef = ref<FormInstance>()
const skuFormRef = ref<FormInstance>()
const priceStockCommonFormRef = ref<FormInstance>()

// 拖拽规格值
const specValueRef = ref()

const goodsEdit = useGoodsEdit({
    getFormRef () {
        return {
            basicFormRef: basicFormRef.value,
            priceStockFormRef: priceStockFormRef.value,
            detailFormRef: detailFormRef.value,
            skuFormRef: skuFormRef.value,
            specValueRef: specValueRef.value,
            priceStockCommonFormRef: priceStockCommonFormRef.value
        }
    },
    addApi: addVirtualGoods,
    editApi: editVirtualGoods,
    formData: {
        goods_type: 'virtual'
    },
    // 追加表单数据
    appendFormData: {
        virtual_auto_delivery: '0',
        virtual_receive_type: 'auto',
        virtual_verify_type: '0',
        virtual_indate: '0',
        virtual_indate_day: '0', // 几日有效的有效期
        virtual_verify_mobile: '0'
    },
    // 表单验证规则
    getFormRules (formData: any, regExp: any) {
        return {
            virtual_indate_day: [
                { required: true, message: t('virtualIndatePlaceholder'), trigger: 'blur' },
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.virtual_receive_type == 'verify' && formData.virtual_verify_type == 1 && value < 1) {
                            callback(new Error(t('virtualIndateErrorHint')))
                        }
                        callback()
                    }
                }
            ],
            virtual_indate: [
                { required: true, message: t('virtualIndatePlaceholder'), trigger: 'blur' },
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.virtual_receive_type == 'verify' && formData.virtual_verify_type == 2) {
                            const date = new Date(value)
                            const time = date.getTime()
                            const date1 = new Date()
                            const time1 = date1.getTime()
                            if (time <= time1) { callback(new Error(t('virtualIndateErrorOneHint'))) }
                        }
                        callback()
                    }
                }
            ]
        }
    }
})

const orderGoodsCount = ref(0)

const isDisabledVirtual = computed(() => {
    if (goodsEdit.formData.virtual_receive_type == 'verify' && orderGoodsCount.value > 0) {
        // 虚拟商品，并且设置为店内核销，若存在订单，则禁用，无法编辑
        return true
    }
    return false
})

const curDisabledArea = ref<any>([]) // 禁用区域
// 加载初始化数据
getVirtualGoodsInit({
    goods_id: goodsEdit.formData.goods_id
}).then((res) => {
    const data = res.data
    if (data) {
        curDisabledArea.value = deepClone(data.disable_edit_area)
        goodsEdit.handleGoodsInit(data)

        // 虚拟商品 收发货设置
        if (goodsEdit.formData.goods_id && data.goods_info) {
            orderGoodsCount.value = data.goods_info.order_goods_count

            goodsEdit.formData.virtual_auto_delivery = String(data.goods_info.virtual_auto_delivery)
            goodsEdit.formData.virtual_receive_type = data.goods_info.virtual_receive_type
            goodsEdit.formData.virtual_verify_type = String(data.goods_info.virtual_verify_type)
            goodsEdit.formData.virtual_verify_mobile = String(data.goods_info.virtual_verify_mobile)
            if (data.goods_info.virtual_receive_type == 'verify' && data.goods_info.virtual_verify_type == 2) {
                goodsEdit.formData.virtual_indate = timeStampTurnTime(data.goods_info.virtual_indate)
            } else if (data.goods_info.virtual_receive_type == 'verify' && data.goods_info.virtual_verify_type == 1) {
                goodsEdit.formData.virtual_indate_day = data.goods_info.virtual_indate
            }
        }
    }
})

// 保存数据
const save = () => {
    goodsEdit.save((data: any) => {
        // 收货设置
        if (data.virtual_receive_type == 'verify' && data.virtual_verify_type == 2) {
            const date = new Date(data.virtual_indate)
            data.virtual_indate = Math.floor(date.getTime() / 1000)
        }
        if (data.virtual_receive_type == 'verify' && data.virtual_verify_type == 1) {
            data.virtual_indate = data.virtual_indate_day
        }
        if (data.virtual_receive_type == 'verify' && data.virtual_verify_type == 0) {
            data.virtual_indate = 0
        }
    })
}
const disabledPastDates = (date:any) => {
    return date.valueOf() < Date.now()
}
</script>

<style lang="scss" scoped>
    @import 'public/css/goods_edit.scss';
</style>
