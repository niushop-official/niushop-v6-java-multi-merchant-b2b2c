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
<!--                  <div class="mt-[5px] text-[12px] text-[var(&#45;&#45;el-color-primary)] leading-[20px]">{{ t('goodsVideoTipFour') }}</div>-->
<!--                  <div class="mt-[5px] text-[12px] text-[var(&#45;&#45;el-color-primary)] leading-[20px]">{{ t('goodsVideoTipFive') }}</div>-->
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

            <!-- <el-form-item :label="goodsEdit.formData.status == 1 ? t('timerOff') : t('timerOn')">
              <div>
                <el-radio-group v-model="goodsEdit.formData[goodsEdit.formData.status == 1 ? 'timer_off_status' : 'timer_on_status']">
                  <el-radio label="1">{{ t('open') }}</el-radio>
                  <el-radio label="0">{{ t('close') }}</el-radio>
                </el-radio-group>
                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ goodsEdit.formData.status == 1 ? t('timerOffTip') : t('timerOnTip') }}</div>
                <div v-if="goodsEdit.formData[goodsEdit.formData.status == 1 ? 'timer_off_status' : 'timer_on_status'] == 1">
                  <el-date-picker v-model="goodsEdit.formData[goodsEdit.formData.status == 1 ? 'timer_off_time' : 'timer_on_time']" type="datetime" :placeholder="t('pleaseSelectTime')" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" :picker-options="timerPickerOptions" :disabled="goodsEdit.isSupplyDisabled('status')" class="timer-picker" />
                </div>
              </div>
            </el-form-item>

            <el-form-item :label="t('supportPayType')">
              <el-checkbox-group v-model="goodsEdit.formData.support_pay_type">
                <el-checkbox :label="item.key" v-for="(item, index) in goodsEdit.payTypeOptions" :key="index">{{ item.name }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item :label="t('isEnableRefund')">
              <el-radio-group v-model="goodsEdit.formData.is_enable_refund">
                <el-radio label="1">{{ t('open') }}</el-radio>
                <el-radio label="0">{{ t('close') }}</el-radio>
              </el-radio-group>
            </el-form-item> -->

            <el-form-item :label="t('PointDeductType')">
              <div>
                <el-radio-group v-model="goodsEdit.formData.point_deduct_type">
                  <el-radio label="custom">{{ t('PointDeductTypeCustom') }}</el-radio>
                  <el-radio label="site">{{ t('PointDeductTypeSite') }}
                    <!-- 只在选中默认设置时显示提示文案 -->
                    <span v-if="goodsEdit.formData.point_deduct_type === 'site'" class="ml-2 text-[12px] text-[#999]">
          （{{ t('pointDeductRatioTip')+pointRatioCache}}%）
        </span>
                  </el-radio>
                </el-radio-group>
              </div>
            </el-form-item>

            <!-- 新增：积分抵扣比例输入框 -->
            <el-form-item :label="t('pointDeductRatio')" prop="point_deduct_rate" v-if="goodsEdit.formData.point_deduct_type === 'custom'">
              <div class="flex items-center">
                <el-input v-model.trim="goodsEdit.formData.point_deduct_rate" clearable :placeholder="t('pointDeductRatioPlaceholder')" class="input-width" maxlength="5" show-word-limit :disabled="goodsEdit.isSupplyDisabled('point_deduct_rate')" @keyup="filterNumberWithPercent($event)" step="0.01" min="0" max="100">
                  <template #append>%</template>
                </el-input>
              </div>
            </el-form-item>
            <!-- 关联推荐 -->
            <el-form-item :label="t('关联推荐')">
              <div>
                <goods-select-popup ref="goodsSelectPopupRef" v-model="goodsEdit.formData.recommend_goods_ids" :exclude_goods_id="goodsEdit.formData.goods_id"  :min="1" :max="6" />
                <div class="text-[12px] text-[#999]">设置后，该商品详情页【店铺推荐】会展示选中的商品</div>
              </div>
            </el-form-item>

            <el-form-item :label="t('unit')" prop="unit">
              <el-input v-model.trim="goodsEdit.formData.unit" clearable :placeholder="t('unitPlaceholder')" class="input-width" show-word-limit maxlength="6" :disabled="goodsEdit.isSupplyDisabled('unit')" />
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
                <el-input v-model.trim="goodsEdit.formData.price" clearable placeholder="0.00" class="input-width" maxlength="8" v-else>
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
                                        <el-form-item :prop="key + '.stock'" :rules="goodsEdit.skuStockRules(item)" class="sku-form-item-wrap">
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
            <el-form-item :label="t('付费会员等级折扣')">
              <el-radio-group v-model="goodsEdit.formData.vip_card_discount" :disabled="goodsEdit.isSupplyDisabled('vip_card_discount')">
                <el-radio label="">{{ t('nonparticipation') }}</el-radio>
                <el-radio label="discount">{{ t('discount') }}</el-radio>
                <el-radio label="fixed_price">{{ t('fixedPrice') }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-form>
          <el-form :model="goodsEdit.formData" label-width="120px" ref="priceStockCommonFormRef" :rules="goodsEdit.formRules" class="page-form">
            <el-form-item :label="t('isLimit')">
              <div>
                <el-switch v-model="goodsEdit.formData.is_limit" :active-value="1" :inactive-value="0"  :disabled="goodsEdit.isSupplyDisabled('is_limit') || isLimitDisabled" />
                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('isLimitTips') }}</div>
              </div>
            </el-form-item>
            <el-form-item :label="t('limitType')" prop="limit_type" v-if="goodsEdit.formData.is_limit == '1'">
              <div>
                <el-radio-group v-model="goodsEdit.formData.limit_type" :disabled="goodsEdit.isSupplyDisabled('limit_type') || isLimitDisabled">
                  <el-radio :label="1">{{ t('singleTime') }}</el-radio>
                  <el-radio :label="2">{{ t('singlePerson') }}</el-radio>
                </el-radio-group>
                <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{ t('limitTypeTips') }}</div>
              </div>
            </el-form-item>
            <el-form-item :label="t('maxBuy')" prop="max_buy" v-if="goodsEdit.formData.is_limit == '1'">
              <div>
                <el-input v-model.trim="goodsEdit.formData.max_buy" clearable :placeholder="t('maxBuyPlaceholder')" class="input-width" maxlength="8" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('max_buy') || isLimitDisabled">
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
        <el-tab-pane :label="t('deliverySetup')" name="delivery">
          <el-form :model="goodsEdit.formData" label-width="120px" ref="deliveryFormRef" :rules="goodsEdit.formRules" class="page-form">
            <el-form-item :label="t('virtualDeliveryType')">
              <el-radio-group v-model="goodsEdit.formData.virtual_delivery_type" :disabled="goodsEdit.isSupplyDisabled('virtual_delivery_type')">
                <el-radio label="auto" :disabled="isDisabledVirtual">{{ t('autoDeliverGoods') }}</el-radio>
                <el-radio label="manual" :disabled="isDisabledVirtual">{{ t('handDeliverGoods') }}</el-radio>
                <el-radio label="verify" :disabled="isDisabledVirtual">{{ t('verifyTakeGoods') }}</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item :label="t('receiveType')" v-if="goodsEdit.formData.virtual_delivery_type == 'auto' || goodsEdit.formData.virtual_delivery_type == 'manual'">
              <div>
                <el-radio-group v-model="goodsEdit.formData.virtual_receive_type" :disabled="goodsEdit.isSupplyDisabled('virtual_receive_type')">
                  <el-radio label="auto" :disabled="isDisabledVirtual">{{ t('autoTakeGoods') }}</el-radio>
                  <el-radio label="artificial" :disabled="isDisabledVirtual">{{ t('handTakeGoods') }}</el-radio>
                </el-radio-group>
              </div>
            </el-form-item>
            <el-form-item :label="goodsEdit.formData.virtual_delivery_type == 'auto' ? t('autoDeliveryContent') : t('manualDeliveryContent')" v-if="goodsEdit.formData.virtual_delivery_type == 'auto' || goodsEdit.formData.virtual_delivery_type == 'manual'">
              <el-input type="textarea" maxlength="5000" show-word-limit rows="5" class="!w-[400px]" v-model.trim="goodsEdit.formData.virtual_delivery_content" clearable :placeholder="goodsEdit.formData.virtual_delivery_type == 'auto' ? t('autoDeliveryContentPlaceholder') : t('manualDeliveryContentPlaceholder')" :disabled="goodsEdit.isSupplyDisabled('virtual_delivery_content')"/>
            </el-form-item>
            <template v-if="goodsEdit.formData.virtual_delivery_type == 'verify'">
              <el-form-item :label="t('verifyVirtualIndate')">
                <el-radio-group v-model="goodsEdit.formData.virtual_verify_type" :disabled="goodsEdit.isSupplyDisabled('virtual_verify_type')">
                  <el-radio label="0">{{ t('verifyVirtualIndateOne') }}</el-radio>
                  <el-radio label="1">{{ t('verifyVirtualIndateTwo') }}</el-radio>
                  <el-radio label="2">{{ t('verifyVirtualIndateThree') }}</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item :label="t('virtualIndate')" v-if="goodsEdit.formData.virtual_verify_type == 1" prop="virtual_indate_day">
                <el-input v-model.trim="goodsEdit.formData.virtual_indate_day" clearable class="input-width" show-word-limit maxlength="5" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('virtual_indate_day')">
                  <template #append>{{t('sky')}}</template>
                </el-input>
              </el-form-item>
              <el-form-item :label="t('virtualIndate')" v-if="goodsEdit.formData.virtual_verify_type == 2" prop="virtual_indate">
                <div>
                  <el-date-picker  class="input-width" value-format="YYYY-MM-DD HH:mm:ss" v-model="goodsEdit.formData.virtual_indate" type="datetime" :disabled-date="disabledPastDates" :disabled="goodsEdit.isSupplyDisabled('virtual_indate')"/>
                  <div class="mt-[10px] text-[12px] text-[#999] leading-[20px]">{{t('verifyHint')}}</div>
                </div>
              </el-form-item>

              <el-form-item :label="t('refundSetup')">
                <el-radio-group v-model="goodsEdit.formData.virtual_refund" :disabled="goodsEdit.isSupplyDisabled('virtual_refund')">
                  <el-radio label="0">{{ t('notAllowRefund') }}</el-radio>
                  <el-radio label="1">{{ t('allowRefundWithinDays') }}</el-radio>
                  <el-radio label="2" v-if="goodsEdit.formData.virtual_verify_type == 1 || goodsEdit.formData.virtual_verify_type == 2">{{ t('核销过期自动退款') }}</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item :label="t('refundDay')" v-if="goodsEdit.formData.virtual_refund == 1" prop="virtual_refund_day">
                <el-input v-model.trim="goodsEdit.formData.virtual_refund_day" clearable class="input-width" show-word-limit maxlength="5" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('virtual_refund_day')" :placeholder="t('refundDayPlaceholder')">
                  <template #append>{{t('sky')}}</template>
                </el-input>
              </el-form-item>
              <div v-if="goodsEdit.formData.virtual_refund == 1" class="text-[12px] text-[#999] leading-[20px] ml-[120px] mb-[22px]">{{t('refundHint')}}</div>

              <!-- <el-form-item :label="t('refundServiceSetup')">
                <el-radio-group v-model="goodsEdit.formData.virtual_refund_service" :disabled="goodsEdit.isSupplyDisabled('virtual_refund_service')">
                  <el-radio label="0">{{ t('noServiceFee') }}</el-radio>
                  <el-radio label="1">{{ t('chargeServiceFee') }}</el-radio>
                  <el-radio label="2">{{ t('chargeServiceFeeAfterDays') }}</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item :label="t('serviceFeeDay')" v-if="goodsEdit.formData.virtual_refund_service == 2" prop="virtual_refund_service_day">
                <el-input v-model.trim="goodsEdit.formData.virtual_refund_service_day" clearable class="input-width" show-word-limit maxlength="5" @keyup="filterNumber($event)" :disabled="goodsEdit.isSupplyDisabled('virtual_refund_service_day')" :placeholder="t('serviceFeeDayPlaceholder')">
                  <template #append>{{t('sky')}}</template>
                </el-input>
              </el-form-item>
              <el-form-item :label="t('serviceFeeRate')" v-if="goodsEdit.formData.virtual_refund_service == 1 || goodsEdit.formData.virtual_refund_service == 2" prop="virtual_refund_service_rate">
                <el-input v-model.trim="goodsEdit.formData.virtual_refund_service_rate" clearable class="input-width" show-word-limit maxlength="8" @keyup="filterNumberWithPercent($event)" :disabled="goodsEdit.isSupplyDisabled('virtual_refund_service_rate')" :placeholder="t('serviceFeeRatePlaceholder')">
                  <template #append>%</template>
                </el-input>
              </el-form-item> -->
            </template>
            <el-form-item :label="t('verifyVerifyMobile')">
              <el-radio-group v-model="goodsEdit.formData.virtual_verify_mobile" :disabled="goodsEdit.isSupplyDisabled('virtual_verify_mobile')">
                <el-radio label="1">{{ t('open') }}</el-radio>
                <el-radio label="0">{{ t('close') }}</el-radio>
              </el-radio-group>
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
import { ref, computed, onMounted, watch } from 'vue'
import { t } from '@/lang'
import { ElMessage, FormInstance } from 'element-plus'
import { ArrowLeft, CircleCloseFilled, Rank } from '@element-plus/icons-vue'
import { useRoute, useRouter } from 'vue-router'
import { deepClone, filterNumber, timeStampTurnTime } from '@/utils/common'
import { addVirtualGoods, editVirtualGoods, getVirtualGoodsInit } from '@/app/lang/zh-cn/api/site/goods'
import { useGoodsEdit } from './public/js/useGoodsEdit'
import { getShopPointConfig } from '@/app/api/basic_site'
import goodsSelectPopup from '@/addon/mall/views/site/goods/components/goods-select-popup.vue'

const route = useRoute()
const router = useRouter()

const basicFormRef = ref<FormInstance>()
const priceStockFormRef = ref<FormInstance>()
const detailFormRef = ref<FormInstance>()
const skuFormRef = ref<FormInstance>()
const priceStockCommonFormRef = ref<FormInstance>()
const deliveryFormRef = ref<FormInstance>()

// 拖拽规格值
const specValueRef = ref()
// 修复：改为响应式变量，初始值为空
const pointRatioCache = ref('')

// 修复：在 onMounted 中异步获取数据（符合 Vue 规范）
onMounted(async () => {
    try {
        const SitePointDeductConfig = await getShopPointConfig()
        // 将获取到的 reduce_rate 赋值到响应式变量中
        pointRatioCache.value = SitePointDeductConfig.data.deduct_rate || ''
    } catch (error) {
        console.error('获取积分抵扣配置失败：', error)
        pointRatioCache.value = '' // 异常时赋空值，避免页面报错
    }
})

const goodsEdit = useGoodsEdit({
    getFormRef () {
        return {
            basicFormRef: basicFormRef.value,
            priceStockFormRef: priceStockFormRef.value,
            detailFormRef: detailFormRef.value,
            skuFormRef: skuFormRef.value,
            specValueRef: specValueRef.value,
            priceStockCommonFormRef: priceStockCommonFormRef.value,
            deliveryFormRef: deliveryFormRef.value
        }
    },
    getVerify () {
        return [
            {
                key: 'delivery',
                verify: false,
                ref: deliveryFormRef.value
            }
        ]
    },
    addApi: addVirtualGoods,
    editApi: editVirtualGoods,
    formData: {
        goods_type: 'virtual',
        // 新增：积分抵扣比例默认值
        point_deduct_rate: '',
        point_deduct_type: 'site'
    },
    // 追加表单数据
    appendFormData: {
        virtual_delivery_type: 'auto',
        virtual_receive_type: 'auto',
        virtual_verify_type: '0',
        virtual_indate: '0',
        virtual_indate_day: '0', // 几日有效的有效期
        virtual_verify_mobile: '0',
        virtual_delivery_content: '',
        virtual_refund: '0',
        virtual_refund_day: '0',
    // virtual_refund_service: '0',
    // virtual_refund_service_day: '0',
    // virtual_refund_service_rate: ''
    },
    // 表单验证规则
    getFormRules (formData: any, regExp: any) {
        return {
            // 新增：积分抵扣比例验证规则
            point_deduct_rate: [
                {
                    required: true,
                    message: t('pointDeductRatioRequired') || '请输入积分抵扣比例',
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.point_deduct_type === 'custom') {
                            if (!value) {
                                callback(new Error(t('pointDeductRatioRequired') || '请输入积分抵扣比例'))
                            } else {
                                const numValue = Number(value)
                                if (isNaN(numValue)) {
                                    callback(new Error(t('pointDeductRatioNumber') || '请输入有效的数字'))
                                } else if (numValue < 0 || numValue > 100) {
                                    callback(new Error(t('pointDeductRatioRange') || '积分抵扣比例必须在0-100之间'))
                                } else {
                                    callback()
                                }
                            }
                        } else {
                            callback()
                        }
                    }
                }
            ],
            virtual_indate_day: [
                { required: true, message: t('virtualIndatePlaceholder'), trigger: 'blur' },
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.virtual_delivery_type == 'verify' && formData.virtual_verify_type == 1 && Number(value) < 1) {
                            callback(new Error(t('virtualIndateErrorHint')))
                            return
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
                        if (formData.virtual_delivery_type == 'verify' && formData.virtual_verify_type == 2) {
                            const date = new Date(value)
                            const time = date.getTime()
                            const date1 = new Date()
                            const time1 = date1.getTime()
                            if (time <= time1) { callback(new Error(t('virtualIndateErrorOneHint'))) }
                        }
                        callback()
                    }
                }
            ],
            virtual_refund_day: [
                {
                    trigger: 'blur',
                    validator: (rule: any, value: any, callback: any) => {
                        if (formData.virtual_refund == 1) {
                            if (value === '' || value === null || value === undefined) {
                                callback(new Error(t('refundDayPlaceholder')))
                            } else if (Number(value) < 0) {
                                callback(new Error('退款有效天数不能小于0'))
                            }
                        }
                        callback()
                    }
                }
            ]
            // virtual_refund_service_day: [
            //   {
            //     trigger: 'blur',
            //     validator: (rule: any, value: any, callback: any) => {
            //       if (formData.virtual_refund_service == 2) {
            //         if (value === '' || value === null || value === undefined) {
            //           callback(new Error(t('serviceFeeDayPlaceholder')))
            //         } else if (Number(value) <= 0) {
            //           callback(new Error('指定天数必须大于0'))
            //         }
            //       }
            //       callback()
            //     }
            //   }
            // ],
            // virtual_refund_service_rate: [
            //   {
            //     trigger: 'blur',
            //     validator: (rule: any, value: any, callback: any) => {
            //       if (formData.virtual_refund_service == 1 || formData.virtual_refund_service == 2) {
            //         if (value === '' || value === null || value === undefined) {
            //           callback(new Error(t('serviceFeeRatePlaceholder')))
            //         } else {
            //           const numValue = Number(value)
            //           if (isNaN(numValue)) {
            //             callback(new Error('退款服务费比率必须为数字'))
            //           } else if (numValue < 0 || numValue > 100) {
            //             callback(new Error('退款服务费比率必须在0-100之间'))
            //           }
            //         }
            //       }
            //       callback()
            //     }
            //   }
            // ]
        }
    }
})

// 新增：积分抵扣比例输入过滤方法
const filterNumberWithPercent = (e: KeyboardEvent) => {
    const input = e.target as HTMLInputElement
    let value = input.value

    // 记录最后一个字符是否是小数点
    const lastChar = value.slice(-1)
    const isLastCharDot = lastChar === '.'

    // 1. 过滤非法字符：仅保留数字和单个小数点
    value = value.replace(/[^0-9.]/g, '')
    if (value.indexOf('.') !== value.lastIndexOf('.')) {
        const firstDotIndex = value.indexOf('.')
        value = value.substring(0, firstDotIndex + 1) + value.substring(firstDotIndex + 1).replace(/\./g, '')
    }

    // 2. 保留末尾小数点（解决输入50.后小数点消失问题）
    if (isLastCharDot && !value.endsWith('.')) {
        value += '.'
    }

    // 3. 限制小数位数（最多两位）
    if (value.includes('.') && !value.endsWith('.')) {
        const [intPart, decPart] = value.split('.')
        value = `${intPart}.${decPart.substring(0, 2)}`
    }

    // 4. 范围限制（0-100）
    const numValue = Number(value.replace(/\.$/, ''))
    if (!isNaN(numValue)) {
        if (numValue < 0) {
            value = isLastCharDot ? '0.' : '0'
        } else if (numValue > 100) {
            value = isLastCharDot ? '100.' : '100'
        }
    }

    // 5. 同步值
    input.value = value
    goodsEdit.formData.point_deduct_rate = value
}

// 时间选择器配置：禁用过去时间、设置时间步长
const timerPickerOptions = computed(() => ({
    // 禁用过去的日期时间（仅允许选择当前时间之后的）
    disabledDate: (time: { getTime: () => number; }) => {
        return time.getTime() < Date.now() - 8.64e7 // 允许选择今天及以后，减去一天毫秒数兼容当天时间
    },
    // 时间选择步长：分钟每5分钟可选，秒每10秒可选（可根据需求调整）
    selectableRange: '00:00:00 - 23:59:59',
    timeStep: {
        hour: 1,
        minute: 5,
        second: 10
    }
}))

const orderGoodsCount = ref(0)

const isDisabledVirtual = computed(() => {
    if (goodsEdit.formData.virtual_delivery_type == 'verify' && orderGoodsCount.value > 0) {
    // 虚拟商品，并且设置为店内核销，若存在订单，则禁用，无法编辑
        return true
    }
    return false
})

// 计算属性：控制限购相关字段的禁用状态
const isLimitDisabled = computed(() => {
    return goodsEdit.formData.virtual_delivery_type === 'auto' || goodsEdit.formData.virtual_delivery_type === 'manual'
})

const curDisabledArea = ref<any>([]) // 禁用区域
const isInitializing = ref(true) // 初始化标志位

// 监听发货方式变化
watch(() => goodsEdit.formData.virtual_delivery_type, (newType) => {
    // 初始化期间不执行，避免覆盖已设置的限购值
    if (isInitializing.value) return
    
    if (newType === 'auto' || newType === 'manual') {
        // 选择自动发货或手动发货时，设置限购相关值
        goodsEdit.formData.is_limit = 1
        goodsEdit.formData.limit_type = 1
        goodsEdit.formData.max_buy = 1
    }
    // 注释掉else分支，不再自动清空限购值，保留用户之前设置的值
    // else {
    //     // 选择其他发货方式时，清空限购相关值
    //     goodsEdit.formData.is_limit = 0
    //     goodsEdit.formData.limit_type = 0
    //     goodsEdit.formData.max_buy = 0
    // }
}, { immediate: true })
// 加载初始化数据
getVirtualGoodsInit({
    goods_id: goodsEdit.formData.goods_id
}).then((res) => {
    const data = res.data
    if (data) {
        curDisabledArea.value = deepClone(data.disable_edit_area)

        // 先设置虚拟发货类型，再调用 handleGoodsInit，这样 watch 就不会覆盖已设置的限购值
        if (goodsEdit.formData.goods_id && data.goods_info) {
            goodsEdit.formData.virtual_delivery_type = data.goods_info.virtual_delivery_type
        }

        goodsEdit.handleGoodsInit(data)

        // 虚拟商品 收发货设置
        if (goodsEdit.formData.goods_id && data.goods_info) {
            orderGoodsCount.value = data.goods_info.order_goods_count

            // goodsEdit.formData.virtual_delivery_type = data.goods_info.virtual_delivery_type
            goodsEdit.formData.virtual_receive_type = data.goods_info.virtual_receive_type
            goodsEdit.formData.virtual_verify_type = String(data.goods_info.virtual_verify_type)
            goodsEdit.formData.virtual_verify_mobile = String(data.goods_info.virtual_verify_mobile)
            goodsEdit.formData.virtual_delivery_content = data.goods_info.virtual_delivery_content || ''
            goodsEdit.formData.virtual_refund = data.goods_info.virtual_refund !== undefined ? String(data.goods_info.virtual_refund) : '0'
            goodsEdit.formData.virtual_refund_day = data.goods_info.virtual_refund_day || '0'
            // goodsEdit.formData.virtual_refund_service = data.goods_info.virtual_refund_service !== undefined ? String(data.goods_info.virtual_refund_service) : '0'
            // goodsEdit.formData.virtual_refund_service_day = data.goods_info.virtual_refund_service_day || '0'
            // goodsEdit.formData.virtual_refund_service_rate = data.goods_info.virtual_refund_service_rate || '0.00'
            if (data.goods_info.virtual_delivery_type == 'verify' && data.goods_info.virtual_verify_type == 2) {
                goodsEdit.formData.virtual_indate = timeStampTurnTime(data.goods_info.virtual_indate)
            } else if (data.goods_info.virtual_delivery_type == 'verify' && data.goods_info.virtual_verify_type == 1) {
                goodsEdit.formData.virtual_indate_day = data.goods_info.virtual_indate
            }
            goodsEdit.formData.point_deduct_rate = data.goods_info.point_deduct_rate || ''
            goodsEdit.formData.point_deduct_type = data.goods_info.point_deduct_type || 'site'
        }

        // 初始化完成，允许 watch 监听用户操作（在所有数据设置完成后设置）
        isInitializing.value = false
    }
})

// 保存数据
const save = () => {
    goodsEdit.save((data: any) => {
    // 收货设置
        if (data.virtual_delivery_type == 'verify' && data.virtual_verify_type == 2) {
            const date = new Date(data.virtual_indate)
            data.virtual_indate = Math.floor(date.getTime() / 1000)
        } else if (data.virtual_delivery_type == 'verify' && data.virtual_verify_type == 1) {
            data.virtual_indate = data.virtual_indate_day
        } else if (data.virtual_delivery_type == 'verify' && data.virtual_verify_type == 0) {
            data.virtual_indate = 0
        }

        // 处理积分抵扣比例数据
        if (data.point_deduct_type === 'custom' && data.point_deduct_rate) {
            // 确保保存为数字格式（保留两位小数）
            data.point_deduct_rate = Number(data.point_deduct_rate).toFixed(2)
        } else {
            // 未开启积分抵扣时清空比例
            data.point_deduct_rate = ''
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
