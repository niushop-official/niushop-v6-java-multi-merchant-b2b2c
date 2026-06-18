<template>
    <div class="main-container">
        <el-card class="card !border-none" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="back()" />
        </el-card>

        <!-- 表单 -->
        <el-card class="box-card mt-[15px] !border-none" shadow="never" v-loading="loading">
            <el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" scroll-to-error class="page-form">
                <!-- 条件类型 -->
                 <el-form-item :label="t('conditionType')">
                    <div class="goods-type-wrap" :class="{ 'selected': formData.type == key }" v-for="(value,key) in typeList" @click="changeType(key)">
                        <div class="goods-type-name">{{ value }}</div>
                        <div v-if="key == 'amount'" class="goods-type-desc">例:满100元减10元/满100元打8折</div>
                        <div v-else class="goods-type-desc">例:满3件减10元/满3件打8折</div>
                        <template v-if="formData.type == key">
                            <div class="triangle"></div>
                            <div class="selected">✓</div>
                        </template>
                    </div>
                </el-form-item>
                <div class="ml-[120px] mb-[10px] mt-[-10px] text-[12px] text-[#999] leading-[20px]">{{ t('平台优惠金额不计入实付金额内') }}</div>
                <!-- 活动名称： -->
                <el-form-item :label="t('activeName')" prop="title">
                    <div>
                        <el-input v-model.trim="formData.title" clearable :placeholder="t('namePlaceholder')" class="input-width" :maxlength="20" show-word-limit />
                    </div>
                </el-form-item>
                <!-- 活动时间 -->
                <el-form-item :label="t('activityTime')" prop="manjian_time">
                    <div class="w-[180px]">
                        <el-date-picker v-model="formData.manjian_time" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" @change="timeChange" />
                    </div>
                </el-form-item>
                <!-- 优惠规格 -->
                <el-form-item :label="t('ruleType')" prop="rule_type">
                    <el-radio-group v-model="formData.rule_type" :disabled="disableSubmit" @change="ruleTypeChange">
                        <el-radio label="ladder">{{t('阶梯优惠(例:满1件减10元)')}}</el-radio>
                        <el-radio label="cycle">{{t('循环优惠(例:每满1件减10元)')}}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <div v-if="formData.rule_type === 'ladder'" class="ml-[120px] mb-[10px] mt-[-10px] text-[12px] text-[#999] leading-[20px]">{{ t('ruleTypeTips') }}</div>
                <div v-if="formData.rule_type === 'cycle'" class="ml-[120px] mb-[10px] mt-[-10px] text-[12px] text-[#999] leading-[20px]">{{ t('ruleTypeTipsTwo') }}</div>

                <!-- 优惠设置 -->
                <el-form-item :label="t('ruleJson')" prop="rule_json" required>
                <!-- 活动层级 -->
                <div class="sku_list flex flex-col items-start">
                      <!-- 添加活动层级 -->
                    <el-button :disabled="disableSubmit" v-if="formData.rule_type=='ladder'" class="my-[5px]"  type="primary" link @click="addSetting">
                        <text class="text-[30rpx] nc-iconfont nc-icon-jiahaoV6xx"></text>
                        {{ formData.rule_json.length>=5? t('最多添加5级优惠') : t('添加下一级优惠')  }}</el-button>
                    <el-table :data="formData.rule_json" size="large" max-height="420" class="mt-[10px] !w-[950px]">
                        <el-table-column v-if="formData.rule_type=='ladder'" type="index" min-width="100" label="层级"></el-table-column>
                        <el-table-column label="门槛" min-width="200">
                            <template #default="{ row, $index }">
                                <el-form-item class="limit-input" :prop="`rule_json.${$index}.amount`" :rules="[{
                                required: true,
                                trigger: 'blur',
                                validator: (rule: any, value: any, callback: any) => {
                                            if (value === null || value === '') {
                                                callback(t('limitPlaceholder'))
                                            }else if (isNaN(value) || !regExp.digit.test(value) && formData.type=='amount') {
                                                callback(t('limitTips'))
                                            }else if (isNaN(value) || !regExp.number.test(value) && formData.type=='num') {
                                                callback(t('limitTips'))
                                            }else if (value <= 0) {
                                                callback(t('valueMustBeGreaterThanZero'))
                                            } else {
                                                const previousLimit = formData.rule_json[$index - 1]?.amount;
                                                if (previousLimit !== undefined && value <=Number(previousLimit)) {
                                                    callback(t('limitTipsThree'));
                                                } else {
                                                    const limits = formData.rule_json.map((level, idx) => idx !== $index && level.amount);
                                                    if (limits.includes(value)) {
                                                        callback(t('limitTipsTwo'));
                                                    } else {
                                                        callback();
                                                    }
                                                }
                                            }
                                            }
                            }]">
                            <div class="flex items-center ml-[10px]">
                                <span class="mr-[10px]">满</span>
                                    <el-input  clearable class="input-width-short" maxlength="8" v-model="row.amount"  :placeholder="formData.type === 'amount' ? '0.00' : '0'" :disabled="disableSubmit" />
                                <span class="ml-[10px]">{{ formData.type === 'amount' ? '元' : '件' }}</span>
                            </div>
                        </el-form-item>
                            </template>
                        </el-table-column>
                        <el-table-column label="优惠" min-width="200" align="center"> 
                            <template #header>
                                  <el-popover placement="bottom" :width="200" trigger="click">
                                    <template #reference>
                                        <div class="cursor-pointer">减价/折扣 已选:{{ formData.promotion_type == 'reduction' ? '减价' : '折扣' }}
                                            <text class="iconfont iconjiantouxia !text-[12px]"></text>
                                        </div>
                                    </template>
                                    <!-- <el-form-item prop="type" required> -->
                                        <el-radio-group  v-model="formData.promotion_type" :disabled="disableSubmit">
                                            <el-radio label="reduction">{{ t('reduce') }}</el-radio>
                                            <el-radio label="discount" v-if="formData.rule_type=='ladder'">{{ t('discountRate') }}</el-radio>
                                        </el-radio-group>
                                    <!-- </el-form-item> -->
                                </el-popover>
                            </template>
                            <template #default="{ row, $index }"> 
                                <div class="flex items-center justify-center">
                                    <el-form-item :prop="`rule_json.${$index}.reduce`" :rules="[{
                                        required: true,
                                        trigger: 'blur',
                                        validator: (rule: any, value: any, callback: any) => {
                                                    if (value === null || value === '') {
                                                        callback(t('discountsPlaceholder'))
                                                    }else if (isNaN(value) || !regExp.digit.test(value)) {
                                                        callback(t('limitTips'))
                                                    }else if (value <= 0) {
                                                        callback(t('discountMustBeGreaterThanZero'))
                                                    }else if (formData.promotion_type ==='reduction' && formData.type=='amount' && value > Number(row.amount)) {
                                                        callback(t('discountLimit'))
                                                    }else if (formData.promotion_type === 'discount') {
                                                        if(value >9.9 || value < 0.1 ){
                                                            callback(t('discountMustBeGreaterThanNine'))
                                                        }else {
                                                            callback()
                                                        }
                                                    }  else {
                                                        callback();
                                                    }
                                                }
                                    }]" >
                                        <div class="flex items-center">
                                            <span class="mr-[10px]">{{ formData.promotion_type === 'reduction' ? '减' : '打' }}</span>
                                                <el-input :disabled="disableSubmit" v-if="formData.promotion_type === 'reduction'"   clearable class="input-width-short" maxlength="8" v-model="row.reduce"  placeholder="0.00" />
                                                <el-input :disabled="disableSubmit" v-else  v-model="row.reduce"   clearable class="input-width-short" maxlength="8" :max="9.9"  placeholder="0.00" />
                                            <span class="ml-[10px]">{{ formData.promotion_type === 'reduction' ? '元' : '折' }}</span>
                                        </div>
                                    </el-form-item>
                                </div>
                            </template>
                        </el-table-column>
                        <el-table-column label="操作" min-width="180" align="right" v-if="formData.rule_type=='ladder'">
                            <template #default="{ row, $index }">
                                <el-button type="primary" link @click="removeSetting($index)" :disabled="disableSubmit" v-if="$index>0">删除</el-button>
                            </template>
                        </el-table-column>
                    </el-table>  
                </div>
                </el-form-item> 
                
                <!-- 参与商品 -->
                <el-form-item :label="t('goodsType')" prop="goods_type" required>
                    <template #label>
                        <div class="flex items-center">
                            {{ t('goodsType') }}
                            <el-tooltip class="box-item" effect="light" placement="top">
                                <template #content>
                                    {{t('同一时间区间内，【全部商品】的活动仅能存在一个。')}}<br />
                                    {{t('同一时间区间内，【指定商品参与】的活动可存在多个，若已有【指定商品不参与】的活动存在，则创建【指定商品参与】的新活动时，其主商品只能是上述已存在的【指定商品不参与】的活动中的主商品。')}}<br />
                                    {{t('同一时间区间内，【指定商品不参与】的活动仅能存在一个，若已有【指定商品参与】的活动存在，则创建【指定商品不参与】的新活动时，其主商品必须是上述已存在的【指定商品参与】的活动中的全部主商品。')}}
                                </template>
                                <el-icon color="#666">
                                    <QuestionFilled />
                                </el-icon>
                            </el-tooltip>
                        </div>  
                    </template>
                    <el-radio-group v-model="formData.goods_type" :disabled="disableSubmit" @change="goodsTypeChange">
                        <el-radio v-for="(value,key) in goodsTypeList" :label="key" :key="key">{{ value}}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <!-- 选择商品 -->
                <el-form-item :label="t('selectGoods')" prop="goods" v-if="formData.goods_type != 'all' ">
                    <goods-select-popup ref="goodsSelectPopupRef" v-model="formData.goods_data" mode="sku" @goodsSelect="goodsSelect" :min="1" :max="99" />
                </el-form-item>
                <el-form-item class="mt-[15px]" v-if="formData.goods && formData.goods.length && formData.goods_type != 'all'">
                    <el-table :data="formData.goods" size="large" max-height="400" class="!w-[950px]">
                        <el-table-column prop="goods_id" :label="t('goodsSelectPopupGoodsInfo')" min-width="300">
                        <template #default="{ row }">
                            <div class="flex items-center cursor-pointer">
                                <div class="min-w-[60px] h-[60px] flex items-center justify-center">
                                    <el-image v-if="row.sku_image" class="w-[60px] h-[60px]" :src="img(row.sku_image)" fit="contain">
                                        <template #error>
                                            <div class="image-slot">
                                                <img class="w-[60px] h-[60px]" src="@/addon/mall/assets/goods_default.png" />
                                            </div>
                                        </template>
                                    </el-image>
                                    <img v-else class="w-[70px] h-[60px]" src="@/addon/mall/assets/goods_default.png" fit="contain" />
                                </div>
                                <div class="ml-2">
                                    <span :title="`${row.goods_name} ${row.sku_name}`"  class="multi-hidden">{{row.sku_name ? row.goods_name + " " + row.sku_name: row.goods_name}}</span>
                                    <span class="text-primary text-[12px]">{{row.goods_type_name}}</span>
                                    <!-- 错误信息提示 -->
                                    <div v-if="row.error_msg" class="text-red-500 text-sm">{{ row.error_msg }}</div>
                                    <!-- 下架信息提示 -->
                                    <div v-if="row.status==0" class="text-red-500 text-sm ml-[10px] mt-5">{{ t('goodsOffTips') }}</div>
                                </div>
                                 <!-- 错误信息提示 -->
                                <!-- <div v-if="row.error_msg" class="text-red-500 text-sm ml-[10px] mt-5">{{ row.error_msg }}</div> -->
                                  <!-- 下架信息提示 -->
                                <!-- <div v-if="row.status==0" class="text-red-500 text-sm ml-[10px] mt-5">{{ t('goodsOffTips') }}</div> -->

                            </div>
                        </template>
                        </el-table-column>

                        <el-table-column prop="price" :label="t('goodsSelectPopupPrice')" min-width="120">
                        <template #default="{ row }">
                            <div>￥{{ row.price }}</div>
                        </template>
                        </el-table-column>
                        <el-table-column :label="t('operation')" align="right" min-width="160">
                            <template #default="{ row, $index }">
                                <el-button type="primary" link @click="deleteGoodsEvent(row, $index)">{{ t("delete") }}</el-button>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-form-item>
                <!-- 活动对象 -->
                <el-form-item :label="t('joinMemberType')" prop="member_type" required>
                    <el-radio-group v-model="formData.member_type">
                        <el-radio v-for="(value,key) in memberTypeList" :label="key" :key="key">{{ value}}</el-radio>
                    </el-radio-group>
                </el-form-item>
                <!-- 会员标签 -->
                <el-form-item :label="t('memberLabel')" prop="member_label_id" v-if="formData.member_type=='label'" :rules="[{ required: true, message: t('labelTips'), trigger: 'blur' }]">
                    <el-select v-model="formData.member_label_id" clearable multiple :placeholder="t('memberLabelPlaceholder')" class="input-width">
                        <el-option :label="item['label_name']" :value="item['label_id']" v-for="(item, index) in labelSelectData" :key="index" />
                    </el-select>
                </el-form-item>
                <!-- 会员等级 -->
                <el-form-item :label="t('memberLevel')" prop="member_level_id" v-if="formData.member_type=='level'" :rules="[{ required: true, message: t('levelTips'), trigger: 'blur' }]">
                    <el-select v-model="formData.member_level_id" clearable multiple :placeholder="t('memberLevelPlaceholder')" class="input-width">
                        <el-option :label="item['level_name']" :value="item['level_id']" v-for="(item, index) in levelSelectData" :key="index" />
                    </el-select>
                </el-form-item>
                <!-- 备注 -->
                <el-form-item :label="t('remark')" prop="remark">
                    <el-input v-model="formData.remark" :placeholder="t('rankRemarkPlaceholder')" type="textarea" maxlength="500" show-word-limit rows="5" class="!w-[400px]" clearable />
                </el-form-item>
            </el-form>
        </el-card>

        <!-- 提交按钮 -->
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="onSave()">{{ t('save') }}</el-button>
                <el-button @click="back()">{{ t('cancel') }}</el-button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import {ref, computed, reactive} from 'vue'
import {t} from '@/lang'
import {useRoute, useRouter} from 'vue-router'
import { getFullReductionInfo,editFullReduction,addFullReduction,checkFullReduction } from "@/addon/mall/api/site/marketing";
import {FormInstance, ElMessage} from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getMemberLabelAll, getMemberLevelAll } from '@/app/api/member'
import goodsSelectPopup from '@/addon/mall/views/site/marketing/full_reduction/components/goods-select-popup.vue'
import {deepClone, img} from '@/utils/common'

const router = useRouter()
const route = useRoute()
const pageName = route.meta.title
const id = route.query.id;
const start_time = new Date()
const end_time = new Date(new Date().setMinutes(new Date().getMinutes() + 10));
const loading = ref(false)
const goodsSelectPopupRef: any = ref(null)
const initialFormData = reactive({
    title: '', // 名称
    start_time: '',
    goods_type: 'all',
    type: 'amount',
    rule_type: 'ladder',
    member_type: 'all',
    member_level_id:[],
    member_label_id:[],
    member_type_json:[],
    promotion_type: 'reduction' ,
    end_time: '',
    goods: [],
    goods_data: [],
    rule_json: [
        {
            level: 1,
            amount: "", // 优惠门槛
            reduce: '', // 优惠金额
        },
    ],
    manjian_time: [start_time, end_time]
})

const formData: Record<string, any> = reactive({...initialFormData})
const formRef = ref<FormInstance>()
const formRules = computed(() => {
    const rules = {
        title: [
            { required: true, message: t('namePlaceholder'), trigger: 'blur' },
        ],
        manjian_time: [
            {required: true, validator: receiveTime, trigger: 'change'}
        ],
        goods: [
            {
                required: true,
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.goods_type != 'all') {
                        if (formData.goods.length == 0) {
                            callback(new Error(t('goodsJsonEmpty')))
                        } else {
                            callback()
                        }
                    }
                }
            }
        ],
    };

    return rules;
});

const receiveTime = (rule: any, value: any, callback: any) => {
    if (!formData.manjian_time || (formData.manjian_time && !formData.manjian_time[0] && !formData.manjian_time[1])) {
        callback(new Error(t('selectActivityTime')))
    } else if (!formData.manjian_time[0]) {
        callback(new Error(t('selectActivityStartTime')))
    } else if (!formData.manjian_time[1]) {
        callback(new Error(t('selectActivityEndTime')))
    } else if (formData.manjian_time[1] <= formData.manjian_time[0]) {
        callback(new Error(t('selectActivityTimeTips')))
    }

    callback()
}


const ruleTypeChange = (value: any) => { 
    formData.rule_json = [
        {
            level: 1,
            amount: "", // 优惠门槛
            reduce: '', // 优惠金额
        },
    ]
    if (value == 'cycle') { 
        formData.promotion_type = 'reduction'
    }
}

const changeType = (value: any) => { 
    formData.type = value
}

// 添加优惠设置
const addSetting = () => {
    if (formData.rule_json.length >= 5) {
        ElMessage.error(t("addLevelLimit"));
        return;
    }
    formData.rule_json.push({
        level:formData.rule_json.length + 1,
        amount: "", // 优惠门槛
        reduce: '', // 优惠金额
    });
}

// 正则表达式
const regExp: any = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}

// 删除优惠设置
const removeSetting = (index: number) => {
    formData.rule_json.splice(index, 1);
};
const levelSelectData = ref([])
const labelSelectData = ref([])
const disableSubmit = ref(false)
const goodsTypeList = ref([])
const memberTypeList = ref([])
const promotionTypeList = ref([])
const typeList = ref([])
const getInfo = async() => {

    // 获取全部标签
    getMemberLabelAll().then(({ data }) => {
        labelSelectData.value = data
    })

    getMemberLevelAll().then(({ data }) => {
        levelSelectData.value = data
    })
    loading.value = true
    const res = await getFullReductionInfo({id})
    goodsTypeList.value = res.data.goods_type_list
    memberTypeList.value = res.data.member_type_list
    promotionTypeList.value = res.data.promotion_type_list
    typeList.value = res.data.type_list
    if (id) {
        // disableSubmit.value = true
        const data = res.data.info;
        Object.assign(formData, data);
        if (formData.member_type == 'level') {
            formData.member_level_id = formData.member_type_json
        } else if (formData.member_type == 'label') {
            formData.member_label_id = formData.member_type_json
        }
        formData.manjian_time = [data.start_time, data.end_time]
      
    }
    loading.value = false;  
}

getInfo()

// 删除商品
const deleteGoodsEvent = (row: any, index: any) => {
    formData.goods.splice(index, 1);

    // formData.goods_data（本身是 [{ goods_id: x, sku_ids: [] }] 格式）
    const targetGoodsId = row.goods_id;
    const targetSkuId = row.sku_id;
    if (!targetGoodsId || !targetSkuId) return;

    // 在 goods_data 中通过 goods_id 找到对应商品对象
    const targetGoodsItemInData = formData.goods_data.find(
        item => item.goods_id === targetGoodsId
    );

    // 精准删除该商品对象 sku_ids 中的 targetSkuId（替换你原来的单行删除逻辑）
    if (targetGoodsItemInData && Array.isArray(targetGoodsItemInData.sku_ids)) {
        const skuIndex = targetGoodsItemInData.sku_ids.findIndex(skuId => skuId === targetSkuId);
        if (skuIndex > -1) {
            targetGoodsItemInData.sku_ids.splice(skuIndex, 1); // 只删对应 sku_id，不删整个商品项
        }

        // 如果该商品的 sku_ids 删空了，也可以删除整个 goods_data 中的商品项（按需选择）
        if (targetGoodsItemInData.sku_ids.length === 0) {
            const goodsDataIndex = formData.goods_data.findIndex(item => item.goods_id === targetGoodsId);
            if (goodsDataIndex > -1) formData.goods_data.splice(goodsDataIndex, 1);
        }
    }
};

//选择商品
const goodsSelect = (value: any) => {
    let arr = [];
    for (let key in value) {
        let goods_sku: any = value[key];
        let sku: any = {
            goods_id: goods_sku.goods_id,
            sku_id: goods_sku.sku_id,
            goods_type_name: goods_sku.goods_type_name,
            price: goods_sku.price,
            sku_image: goods_sku.sku_image,
            goods_name: goods_sku.goods_name,
            sku_name: goods_sku.sku_name,
            stock: goods_sku.stock,
        };

        if (formData.goods.length) {
            formData.goods.forEach((el: any) => {
                if (el.sku_id == sku.sku_id) {
                    sku = Object.assign(sku, el)
                }
            })

        }
        arr.push(deepClone(sku))
    }

    formData.goods = arr;
    if (formRef.value) {
        formRef.value.validateField(`goods`)
    }

    checkFullReductionFn();
};

const checkFullReductionFn = () => { 
    formData.goods.forEach((item: any) => {
        item.error_msg = ''
    })
    let data = {
        id: id ? Number(id) : "",
        goods_data: formData.goods_data,
        goods_type: formData.goods_type,
        start_time: formData.manjian_time[0] ? formData.manjian_time[0] : start_time,
        end_time: formData.manjian_time[1] ? formData.manjian_time[1] : end_time
    }
    checkFullReduction(data).then((res: any) => {
        if (res.data.code == -1) {
            ElMessage({
                message: t("goodOnlyOne"),
                type: "error",
            });
        }
        const errorList = res.data.data;
        formData.goods.forEach((item: any) => {
            const error = errorList.find((err) => err.goods_id === item.goods_id);
            if (error) {
                item.error_msg = error.error_msg;
            }
        })

    })
};

const goodsTypeChange = (value: any) => {
    checkFullReductionFn();
};

const timeChange = (value: any) => {
    checkFullReductionFn();
}

const onSave = async () => {
    // 防止失败丢失
    const originalRuleJson = JSON.parse(JSON.stringify(formData.rule_json));
    const originalGoodsData = JSON.parse(JSON.stringify(formData.goods));

    await formRef.value?.validate(async (valid) => {
        if (valid) {
            loading.value = true
            formData.start_time = formData.manjian_time[0];
            formData.end_time = formData.manjian_time[1];
            if (formData.member_type == 'level') {
                formData.member_type_json = formData.member_level_id
            } else if (formData.member_type == 'label') {
                formData.member_type_json = formData.member_label_id
            } else {
                formData.member_type_json = []
            }
            const api = id ? editFullReduction : addFullReduction;
            formData.id = id;
            api(formData).then((res) => { 
                loading.value = false;
                back()
            }).catch(() => {
                formData.rule_json = originalRuleJson;
                formData.goods = originalGoodsData;
                loading.value = false;
            });
        } else {
            const iserror = document.getElementsByClassName('is-error')[0];
            iserror.scrollIntoView()
        }
    });
}

const back = () => {
    router.push('/mall/marketing/full_reduction/list')
}

</script>

<style lang="scss" scoped>
.activity-level{
    width: 100%;
}
.input-width-short{
  width: 120px;
}
.level-header{
    background-color: #f5f5f5;
    display: flex;
    padding-left: 10px;
    justify-content: space-between;
}
.level-section{
    margin-bottom: 10px;
    margin-top: 10px;
}
.vertical-checkbox-group {
    margin-left: 10px;
}
.condition-item:first-child{
    margin-top: 3px;
}
.condition-item{
    margin-top: 8px;
}
.dynamic-content{
    margin: 5px 20px;
}
.sku_list :deep(.cell) {
    // min-height: 35px !important;
    overflow: initial !important;
}
.sku_list :deep(.el-form-item__error ) {
    padding-top: 6px;
    left: 2px;
}
.sku_list :deep(.el-table--enable-row-transition .el-table__body td.el-table__cell) {
    padding: 20px 0;
}
.goods-type-wrap {
  width: 150px;
  height: 80px;
  background: #fff;
  border-radius: 3px;
  float: left;
  text-align: center;
  padding-top: 8px;
  position: relative;
  cursor: pointer;
  line-height: 23px;
  border: 1px solid #e7e7e7;

  .goods-type-name {
    font-size: 14px;
    font-weight: 600;
    color: rgba(0, 0, 0, .85);
  }

  .goods-type-desc {
    font-size: 12px;
    font-weight: 400;
    color: #999;
  }

  .triangle {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 0;
    height: 0;
    border-bottom: 26px solid var(--el-color-primary);
    border-left: 26px solid transparent;
  }

  .selected {
    position: absolute;
    bottom: -2px;
    right: 2px;
    color: #fff;
  }

  &.selected {
    border: 1px solid var(--el-color-primary);
  }

  &.disabled {
    cursor: not-allowed;

    .goods-type-name {
      color: #999;
    }
  }

  &:nth-child(2n) {
    margin: 0 12px;
  }

}
</style>
