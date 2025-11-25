<template>
	<div class="main-container" v-loading="loading">

		<el-card class="card !border-none mb-[15px]" shadow="never">
            <el-page-header :content="pageName" :icon="ArrowLeft" @back="router.push(`/mall/marketing/platform_coupon/list`)" />
        </el-card>

		<!-- 表单 -->
		<el-card class="box-card !border-none page-height" shadow="never" v-if="Object.keys(formData).length">
			<el-form :model="formData" label-width="120px" ref="formRef" :rules="formRules" class="page-form">
				<!-- 优惠券信息： -->
				<!-- 优惠券名称： -->
				<el-form-item :label="t('title')" prop="title">
					<el-input v-model="formData.title" clearable :placeholder="t('titlePlaceholder')" class="input-width" :maxlength="20" />
				</el-form-item>

				<!-- 优惠券面额： -->
				<el-form-item :label="t('price')" prop="price">
					<el-input v-model="formData.price" @keyup="filterDigit($event)" clearable :placeholder="t('pricePlaceholder')" class="input-width" maxlength="5" />
				</el-form-item>

				<!-- 优惠券类型 -->
				<el-form-item :label="t('type')" prop="type">
					<el-radio-group v-model="formData.type" disabled>
						<el-radio :label="1">通用券</el-radio>
						<el-radio :label="5">品类券</el-radio>
						<el-radio :label="4">品牌券</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-if="formData.type == 5" prop="goods_category_ids">
					<div>
						<el-cascader v-model="formData.goods_category_ids" :options="options" :props="props" :placeholder="t('goodsCategoryPlaceholder')" collapse-tags collapse-tags-tooltip clearable />
					</div>
				</el-form-item>

				<el-form-item v-if="formData.type == 4" prop="brand_ids">
					<div>
						<el-select v-model="formData.brand_ids" multiple collapse-tags clearable :placeholder="t('brandPlaceholder')">
							<el-option :label="item['brand_name']" :value="item['brand_id']" v-for="(item,index) in brandOptions"  :key="index"/>
						</el-select>
					</div>
				</el-form-item>

				<!-- 适用店铺 -->
				<el-form-item :label="t('applicableShop')" prop="is_all_site_join">
					<el-radio-group v-model="formData.is_all_site_join">
						<el-radio :label="0">全部店铺</el-radio>
						<el-radio :label="1">指定店铺</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-if="formData.is_all_site_join == 1" prop="join_site_ids">
					<div>
						<el-cascader v-model="formData.join_site_ids" :options="joinSiteOptions" :props="props" placeholder="请选择店铺" collapse-tags collapse-tags-tooltip clearable />
					</div>
				</el-form-item>

				<!-- 使用门槛 -->
				<el-form-item :label="t('threshold')">
					<el-radio-group v-model="formData.threshold">
						<el-radio :label="1">{{ t('reduction') }}</el-radio>
						<el-radio :label="2">{{ t('noThreshold') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-if="formData.threshold == 1" prop="min_condition_money">
					最低满
					<div class="flex items-center" style="padding-left: 5px;">
						<el-input v-model="formData.min_condition_money" @keyup="filterDigit($event)" clearable class="!w-[100px]" />
					</div>
					元可用
				</el-form-item>

				<!-- 使用时间 -->
				<el-form-item :label="t('validType')">
					<el-radio-group v-model="formData.valid_type">
						<el-radio :label="1">{{ t('days') }}</el-radio>
						<el-radio :label="2">{{ t('times') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-show="formData.valid_type == 1">
					领劵后立即生效，有效期
					<div class="flex items-center" style="padding-left: 5px;">
						<el-input v-model="formData.length" @keyup="filterNumber($event)" clearable class="!w-[100px]" />
						天
					</div>
				</el-form-item>

				<el-form-item prop="valid_time" v-if="formData.valid_type == 2">
					领劵后立即生效，使用时间截止至
					<div class="w-[220px]" style="padding-left: 5px;">
						<el-date-picker v-model="formData.valid_time" type="datetime" :placeholder="t('validTimePlaceholder')" />
					</div>
				</el-form-item>

				<!--  活动信息  -->
				<!-- 领取方式： -->
				<el-form-item :label="t('receiveType')">
					<div>
						<el-radio-group v-model="formData.receive_type">
							<el-radio :label="1">{{ t('user') }}</el-radio>
							<el-radio :label="2">{{ t('grant') }}</el-radio>
						</el-radio-group>
					</div>
					<div class="form-tip">开启手动领取后，会员可以直接在优惠券列表以及优惠券推广中直接领取</div>
				</el-form-item>

				<!--  领取时间  -->
				<el-form-item :label="t('receiveTime')" v-show="formData.receive_type == 1">
					<el-radio-group v-model="formData.receive_type_time">
						<el-radio :label="1">{{ t('limitedTime') }}</el-radio>
						<el-radio :label="2">{{ t('unlimitedTime') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item prop="receive_time" v-show="formData.receive_type_time == 1 && formData.receive_type == 1">
					<div class="w-[180px]">
						<el-date-picker v-model="formData.receive_time" type="datetimerange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期"></el-date-picker>
					</div>
				</el-form-item>

				<!--  优惠券数量  -->
				<el-form-item :label="t('receiveNumber')" v-show="formData.receive_type == 1">
					<el-radio-group v-model="formData.limit">
						<el-radio :label="1">{{ t('limit') }}</el-radio>
						<el-radio :label="2">{{ t('unlimited') }}</el-radio>
					</el-radio-group>
				</el-form-item>

				<el-form-item v-show="formData.limit == 1 && formData.receive_type == 1" prop="remain_count">
					<div>
						<el-input onkeypress='return( /[\d]/.test(String.fromCharCode(event.keyCode) ) )'
							v-model="formData.remain_count" clearable :placeholder="t('remainCountPlaceholder')"
							class="input-width" :min="formData.remain_count" :max="100000" :controls="false">
							<template #append>张</template>
						</el-input>
					</div>
				</el-form-item>

				<!-- 用户可领取数量 -->
				<el-form-item :label="t('userLimitCount')" prop="limit_count" v-show="formData.receive_type == 1">
					<el-input onkeypress='return( /[\d]/.test(String.fromCharCode(event.keyCode) ) )'
						v-model="formData.limit_count" clearable :placeholder="t('userLimitCountPlaceholder')"
						class="input-width" :min="1" :max="100000">
						<template #append>张</template>
					</el-input>
					<div class="form-tip">每个会员通过前端直接可领用数量</div>
				</el-form-item>

			</el-form>
		</el-card>

		<!-- 提交按钮 -->
		<div class="fixed-footer-wrap">
			<div class="fixed-footer">
				<el-button type="primary" @click="onSave(formRef)">{{ t('save') }}</el-button>
				<el-button @click="back()">{{ t('cancel') }}</el-button>
			</div>
		</div>
	</div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue'
import { t } from '@/lang'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsCategoryList, getSiteList, editPlatformCoupon, getPlatformCouponInfo } from '@/addon/mall/api/admin/marketing'
import { getBrandList } from '@/app/api/basic_admin'
import type { FormInstance } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { filterNumber, filterDigit } from '@/utils/common'

const route = useRoute()
const router = useRouter()
const pageName = route.meta.title

const couponId: number = parseInt(route.query.id as string)
const loading = ref(false)
const start = new Date()
const end = new Date()

end.setTime(end.getTime() + 3600 * 1000 * 2 * 360) // 设置结束默认时间为当前时间30天后
const initialFormData = {
    title: '', // 名称
    price: '', // 面值
    type: 1, // 优惠券类型 1通用券 5品类券 4品牌券
    is_all_site_join: 0,
    join_site_ids: [],
    limit: 2,
    receive_type: 2, // 领取方式
    remain_count: 1000, // 剩余数量
    threshold: 2, // 门槛
    limit_count: '', // 单个用户领取数量
    // status: 1,  //状态 1 正常 2 未开启 3 已无效
    min_condition_money: '', // 商品最低多少金额可用优惠券
    length: 30, // 有效期时长(天)
    brand_ids: [], // 商品品牌
    goods_category_ids: [], // 关联商品分类id
    receive_type_time: 2,
    valid_type: 1, // 有效方式，1=时长，2=范围
    receive_time: [start, end],
    valid_time: end
}
const formData: Record<string, any> = ref({ ...initialFormData })
const formRef = ref<FormInstance>()

// 表单验证规则
const formRules = computed(() => {
    return {
        title: [
            { required: true, message: t('titlePlaceholder'), trigger: 'blur' }
        ],
        price: [
            { required: true, validator: priceRule, trigger: 'blur' }
        ],
        brand_ids: [
            { required: true, message: t('brandPlaceholder'), trigger: 'blur' }
        ],
        goods_category_ids: [
            { required: true, message: t('goodsCategoryPlaceholder'), trigger: 'blur' }
        ],
        join_site_ids: [
            { required: true, message: t('joinSitePlaceholder'), trigger: 'blur' }      
        ],
        min_condition_money: [
            { required: true, validator: minConditionMoney, trigger: 'blur' }
        ],
        valid_time: [
            { required: true, validator: validTime, trigger: 'blur' }
        ],
        receive_time: [
            { required: true, validator: receiveTime, trigger: 'blur' }
        ],
        remain_count: [
            { required: true, validator: remainCount, trigger: 'blur' }
        ],
        limit_count: [
            { required: true, validator: limitCountRule, trigger: 'blur' }
        ]
    }
})

const receiveTime = (rule: any, value: any, callback: any) => {
    if (formData.value.receive_type_time == 1 && formData.value.receive_type == 1) {
        if (!formData.value.receive_time[0] || timestampFn(formData.value.receive_time[0]) <= Date.now()) {
            callback(new Error(t('领取开始时间不能小于等于当前时间')))
        }
        if (!formData.value.receive_time[1] || timestampFn(formData.value.receive_time[1]) <= timestampFn(formData.value.receive_time[0])) {
            callback(new Error(t('领取结束时间不能小于等于领取开始时间')))
        }
    }
    callback()
}

const validTime = (rule: any, value: any, callback: any) => {
    if (formData.value.valid_type == 2 && formData.value.valid_time <= Date.now()) {
        callback(new Error(t('有效期不能小于等于当前时间')))
    }
    if (formData.value.valid_type == 2 && formData.value.receive_type_time == 1 && formData.value.receive_type == 1) {
        if (timestampFn(formData.value.valid_time) <= timestampFn(formData.value.receive_time[1])) {
            callback(new Error(t('有效期不能小于等于领取结束时间')))
        }
    }
    callback()
}

const minConditionMoney = (rule: any, value: any, callback: any) => {
    if (formData.value.threshold == 1 && formData.value.min_condition_money <= 0) {
        callback(new Error(t('使用门槛最低不能小于0元')))
    }
    callback()
}

const remainCount = (rule: any, value: any, callback: any) => {
    if (formData.value.remain_count != '' && formData.value.remain_count > 100000) {
        callback(new Error(t('remainCountPlaceholder')))
    }
    if (!formData.value.remain_count || formData.value.remain_count != '' && formData.value.remain_count < 1) {
        callback(new Error(t('发放数量不能小于1张')))
    }
    callback()
}
const priceRule = (rule: any, value: any, callback: any) => {
    if (!formData.value.price || formData.value.price == '' || formData.value.price <= 0) {
        callback(new Error(t('pricePlaceholder')))
    }
    callback()
}

const limitCountRule = (rule: any, value: any, callback: any) => {
    if (!formData.value.limit_count || formData.value.limit_count != '' && formData.value.limit_count < 1) {
        callback(new Error(t('userLimitCountPlaceholder')))
    }
    if (formData.value.limit == 1 && formData.value.limit_count != '' && formData.value.remain_count != '' && parseInt(formData.value.limit_count) > parseInt(formData.value.remain_count)) {
        callback(new Error(t('限领张数不能大于发放数量')))
    }
    callback()
}

// 发布数量
// const setBoundsTo = ref(true)
// 优惠券类型
// const type = ref(1)
const props = { multiple: true }
// const categoryData = ref([])
const options = ref([])
const categoryList = () => {
    getGoodsCategoryList().then((res) => {
        options.value = res.data.goods_category_tree
    })
}
categoryList()

// 适用店铺列表
const joinSiteOptions = ref([])
const joinSiteList = () => {
    getSiteList().then((res) => {
        joinSiteOptions.value = res.data.site_tree
    })
}
joinSiteList()

// 品牌列表
const brandOptions = ref([])
const brandList = () => {
    getBrandList({}).then((res) => {
        brandOptions.value = res.data
    })
}
brandList()

const onSave = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const data = JSON.parse(JSON.stringify(formData.value))
            if (data.type == 1) {
                delete data.goods_category_ids
                delete data.brand_ids
            } else if (data.type == 5) {
                delete data.brand_ids
            } else if (data.type == 4) {
                delete data.goods_category_ids
            }

            if (data.is_all_site_join == 0) {
                delete data.join_site_ids
            } else {
                const joinSiteIds: any = []
                data.join_site_ids.forEach((item: any) => {
                    if (typeof item == 'object') {
                        item.forEach((second: any, index: any) => {
                            if (index && joinSiteIds.indexOf(second) == -1) {
                                joinSiteIds.push(second)
                            }
                        })
                    }
                })
                data.join_site_ids = joinSiteIds
            }

            const save = editPlatformCoupon
            save(data).then(res => {
                loading.value = false
                history.back()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
onMounted(async () => {
    getCouponInfoFn(couponId)
})

// 时间格式转换时间戳
const timestampFn = (data) => {
    var dateObject = new Date(data)
    return dateObject.getTime()
}

// 详情查询
const getCouponInfoFn = (id: any) => {
    loading.value = true
    getPlatformCouponInfo(id).then(res => {
        formData.value = Object.assign(formData.value, res.data)
        if (parseInt(formData.value.start_time) != 0 && parseInt(formData.value.end_time) != 0) {
            var start_time = new Date(formData.value.start_time)
            var end_time = new Date(formData.value.end_time)
            formData.value.receive_type_time = 1
            formData.value.receive_time = [start_time, end_time]
        }

        if (res.data.valid_end_time) formData.value.valid_time = res.data.valid_end_time

        if (formData.value.type == 5) {
            formData.value.goods_category_ids.forEach((item: any, index: any) => {
                const val = search(options, item)
                formData.value.goods_category_ids = [val]
            })
        }
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}

// 查找el-cascader显示数据的完整路径
const search = (object, value) => {
    for (var key in object) {
        if (object[key].value == value) return [object[key].value]
        if (object[key].children && Object.keys(object[key].children).length > 0) {
            var temp = search(object[key].children, value)
            if (temp) return [object[key].value, temp].flat()
        }
    }
}

const back = () => {
    history.back()
}
</script>

<style lang="scss">
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
	-webkit-appearance: none !important;
	-moz-appearance: none !important;
	-o-appearance: none !important;
	-ms-appearance: none !important;
	appearance: none !important;
	margin: 0;
}

input[type="number"] {
	-webkit-appearance: textfield;
	-moz-appearance: textfield;
	-o-appearance: textfield;
	-ms-appearance: textfield;
	appearance: textfield;
}

.page-height {
	min-height: calc(100vh - 180px);
}
</style>
