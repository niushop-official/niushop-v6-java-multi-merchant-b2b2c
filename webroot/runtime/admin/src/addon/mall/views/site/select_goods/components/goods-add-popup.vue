<template>
    <el-dialog v-model="showDialog" title="添加到店铺" width="700px" class="dialog-wrap" :destroy-on-close="true">
        <div class="supply-popup">
            <div class="flex">
                <el-image class="w-[64px] h-[64px] mr-[10px]" :src="img(detail.goods.goods_cover_thumb_small)" fit="cover">
                    <template #error>
                        <img src="@/addon/mall/assets/goods_default.png" class="w-[64px] h-[64px]">
                    </template>
                </el-image>
                <div>
                    <div class="text-[14px] mb-[4px] font-[600]  using-hidden">{{ detail.goods.goods_name }}</div>
                    <div class="text-[14px]">
                        <span>供货价：</span>
                        <span class="text-[#FF0000]">￥{{ detail.cost_price }}</span>
                        <span class="text-[12px] text-[#999] ml-[22px]">销量 {{ detail.goods.sale_num }}{{ detail.goods.unit }}</span>
                    </div>
                    <div class="text-[14px]">
                        <span>建议零售价：</span>
                        <span class="text-[#FF0000]">￥{{ detail.price }}</span>
                    </div>
                </div>
            </div>
            <div class="text-[12px] mt-[8px] mb-[16px] text-[#999]">我的售价默认使用供货商建议售价，可修改</div>
            <el-form :model="formData" label-width="125px" ref="formGoodsRef" :rules="formRules" class="page-form mb-[20px]">
                <template v-if="detail.spec_type == 'multi'" >
                    <el-form-item label="批量修改售价：" >
                        <el-radio-group v-model="formData.type" class="ml-2">
                            <el-radio :label="1">直接修改</el-radio>
                            <el-radio :label="2">根据供货价计算</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="我的统一售价：" prop="price" v-if="formData.type == 1">
                        <div class="flex items-center">
                            <el-input class="!w-[120px]" size="mini" v-model.trim="formData.price"  clearable placeholder="请输入"  @input="handlePrice" />
                            <span class="ml-[4px]">元</span>
                        </div>
                    </el-form-item>
                    <el-form-item label="在供货价基础上：" prop="addPrice" v-if="formData.type == 2">
                        <div class="flex items-center">
                            <el-select class="!w-[80px] mr-[10px]" size="mini" v-model="formData.calculate" placeholder="请选择" @change="handleTakePrice">
                                <el-option label="加" :value="1" />
                                <el-option label="乘" :value="2" />
                            </el-select>
                            <el-input class="!w-[120px]" size="mini" v-model.trim="formData.addPrice" clearable  placeholder="请输入"  @input="handleAddPrice" />
                            <span class="ml-[4px]">元</span>
                        </div>
                    </el-form-item>
                </template>
                <template v-if="detail.spec_type == 'single'">
                    <el-form-item label="控价区间：">
                        <div v-if="Number(detail.min_price) || Number(detail.max_price)">{{ Number(detail.min_price) ? detail.min_price : '无' }} ～ {{ Number(detail.max_price) ? detail.max_price : '无' }}</div>
                        <div v-else>无</div>
                    </el-form-item>
                    <el-form-item label="我的售价：" prop="singlePrice">
                        <div class="flex items-center">
                            <el-input class="!w-[120px]" size="mini" v-model.trim="formData.singlePrice" clearable placeholder="请输入" @input="handleSinglePrice" />
                            <span class="ml-[4px]">元</span>
                        </div>
                    </el-form-item>
                </template>
            </el-form>
            <el-form :model="detail" label-width="0px" ref="formTableRef"  class="page-form">
                <div class="skuList" v-if="detail.spec_type == 'multi'">
                    <el-table :data="detail.skuList" height="350">
                        <el-table-column prop="sku_name" label="型号" />
                        <el-table-column prop="cost_price" label="供货价">
                            <template #default="{ row }">
                                <div>￥{{ moneyFormat(row.cost_price) }}</div>
                            </template>
                        </el-table-column>
                        <el-table-column  label="控价区间">
                            <template #default="{ row }">
                                <div v-if="Number(row.min_price) || Number(row.max_price)">￥{{ Number(row.min_price) ? moneyFormat(row.min_price) : '无' }} ～ {{ Number(row.max_price) ? moneyFormat(row.max_price) : '无'  }}</div>
                                <div v-else>无</div>
                            </template>
                        </el-table-column>
                        <el-table-column prop="address" label="我的售价">
                            <template #default="scope">
                                <el-form-item :prop="`skuList[${scope.$index}].price`" :rules="[{
                                                            trigger: 'blur',
                                                            validator: (rule: any, value: any, callback: any) => {
                                                                if (Number(scope.row.price) && Number(scope.row.min_price) && Number(scope.row.max_price) && (Number(scope.row.price) < Number(scope.row.min_price) || Number(scope.row.price) > Number(scope.row.max_price))) {
                                                                    callback('价格超出控价区间')
                                                                } else if(Number(scope.row.price) && Number(scope.row.min_price) && (Number(scope.row.price) < Number(scope.row.min_price))){
                                                                    callback('价格不能低于控价区间最低价')
                                                                } else if(Number(scope.row.price) && Number(scope.row.max_price) && (Number(scope.row.price) > Number(scope.row.max_price))){
                                                                    callback('价格不能高于控价区间最高价')
                                                                }
                                                                else if ( Number(scope.row.price) < Number(scope.row.cost_price) ) {
                                                                    callback('价格不能低于供货价')
                                                                }
                                                                else {
                                                                    callback()
                                                                }
                                                            }
                                                        }]">
                                    <div class="flex items-center">
                                        <el-input size="mini" v-model="scope.row.price"  placeholder="请输入" />
                                        <span class="ml-[4px]">元</span>
                                    </div>
                                </el-form-item>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-form>
        </div>
        <template #footer>
            <span class="dialog-footer">
                <el-button  @click="showDialog = false">取消</el-button>
                <el-button type="primary" class="!bg-[var(--price-color)] !border-[var(--price-color)]" :loading="loading"  @click="onSave()">仅添加暂不上架</el-button>
                <el-button type="primary" class="!bg-[var(--price-color)] !border-[var(--price-color)]"  :loading="repeatLoading"  @click="comfrim()">立即上架</el-button>
            </span>
        </template>
    </el-dialog>
</template>
<script lang="ts" setup>
import { ref, reactive, computed } from 'vue'
import { img, moneyFormat } from '@/utils/common'
import { pickGoods, getGoodsDetail } from '@/app/lang/zh-cn/api/site/select_goods'
import { FormInstance } from 'element-plus'

const showDialog = ref(false)
const detail = ref<any>({})
const loading = ref(false)
const repeatLoading = ref(false)

const formData = reactive({
    singlePrice: '',
    type: 1,
    price: '',
    calculate: 1,
    addPrice: ''
})

// 正则表达式
const regExp: any = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}

const formRules = computed(() => {
    return {
        singlePrice: [
            {
                validator: (rule: any, value: any, callback: any) => {
                    if (isNaN(value) || !regExp.digit.test(value)) {
                        callback('价格格式错误')
                    } else if (Number(value) && Number(detail.value.min_price) && Number(detail.value.max_price) && (Number(value) < Number(detail.value.min_price) || Number(value) > Number(detail.value.max_price))) {
                        callback('价格超出控价区间')
                    } else if (Number(value) && Number(detail.value.min_price) && (Number(value) < Number(detail.value.min_price))) {
                        callback('价格不能低于控价区间最低价') 
                    } else if (Number(value) && Number(detail.value.max_price) && (Number(value) > Number(detail.value.max_price))) {
                        callback('价格不能高于控价区间最高价')
                    } else if (Number(value) && Number(value) < Number(detail.value.cost_price)) {
                        callback('价格不能低于供货价')
                    } else if (value !== '' && Number(value) === 0) {
                        callback('价格不能为0')
                    } else {
                        callback()
                    }
                },
                trigger: 'blur'
            }
        ],
        price: [
            {
                validator: (rule: any, value: any, callback: any) => {
                    if (isNaN(value) || !regExp.digit.test(value)) {
                        callback('价格格式错误')
                    } else if (value !== '' && Number(value) === 0) {
                        callback('价格不能为0')
                    } else {
                        callback()
                    }
                }
            }
        ],
        addPrice: [
            {
                validator: (rule: any, value: any, callback: any) => {
                    if (isNaN(value) || !regExp.digit.test(value)) {
                        callback('价格格式错误')
                    } else if (value !== '' && Number(value) === 0) {
                        callback('价格不能为0')
                    } else {
                        callback()
                    }
                }
            }
        ]
    }
})

const handleSinglePrice = (event: any) => {
    if (formData.singlePrice) {
        detail.value.skuList = detail.value.skuList.map((item: any) => {
            item.price = formData.singlePrice
            return item
        })
    }
}
const handlePrice = (event: any) => {
    if (formData.price) {
        detail.value.skuList = detail.value.skuList.map((item: any) => {
            item.price = formData.price
            return item
        })
    }
}
// 计算加乘
const handleTakePrice = (event: any) => {
    if (formData.addPrice) {
        if (formData.calculate == 1) {
            detail.value.skuList = detail.value.skuList.map((item: any) => {
                item.price = Number(item.cost_price) + Number(formData.addPrice)
                return item
            })
        } else {
            detail.value.skuList = detail.value.skuList.map((item: any) => {
                item.price = Number(item.cost_price) * Number(formData.addPrice)
                return item
            })
        }
    }
}
const handleAddPrice = (event: any) => {
    if (formData.addPrice) {
        if (formData.calculate == 1) {
            detail.value.skuList = detail.value.skuList.map((item: any) => {
                item.price = Number(item.cost_price) + Number(formData.addPrice)
                return item
            })
        } else {
            detail.value.skuList = detail.value.skuList.map((item: any) => {
                item.price = Number(item.cost_price) * Number(formData.addPrice)
                return item
            })
        }
    }
}
const formTableRef = ref<FormInstance | null>(null)
const formGoodsRef = ref<FormInstance | null>(null)
const verify = (callback: any) => {
    const formRef = [
        {
            verify: false,
            ref: formTableRef.value
        },
        {
            verify: false,
            ref: formGoodsRef.value
        }
    ]
    formRef.forEach((el: any, index) => {
        el.ref.validate((valid: any) => {
            el.verify = valid
        })
    })

    setTimeout(() => {
        let verify = true
        // 检测验证，并且定位tab页面
        for (let i = 0; i < formRef.length; i++) {
            if (formRef[i].verify == false) {
                verify = false
                break
            }
        }
        if (verify && callback) callback()
    }, 10)
}

const emit = defineEmits(['confirm'])

const onSave = () => {
    verify(() => {
        loading.value = true
        const obj: any = {
            goods_id: detail.value.goods.goods_id,
            sku_ids: detail.value.skuList.map((item: any) => {
                return {
                    sku_id: item.sku_id,
                    price: item.price
                }
            }),
            status: 0
        }
        pickGoods(obj).then((res: any) => {
            loading.value = false
            showDialog.value = false
            emit('confirm')
        }).catch(() => {
            loading.value = false
            showDialog.value = false
        })
    })
}
const comfrim = () => {
    verify(() => {
        repeatLoading.value = true
        const obj: any = {
            goods_id: detail.value.goods.goods_id,
            sku_ids: detail.value.skuList.map((item: any) => {
                return {
                    sku_id: item.sku_id,
                    price: item.price
                }
            }),
            status: 1
        }
        pickGoods(obj).then((res: any) => {
            repeatLoading.value = false
            showDialog.value = false
            emit('confirm')
        }).catch(() => {
            repeatLoading.value = false
            showDialog.value = false
        })
    })
}
const open = async (id: number) => {
    detail.value = await (await (getGoodsDetail({ goods_id: id }))).data
    if (detail.value.spec_type == 'single') {
        formData.singlePrice = detail.value.price
    }
    showDialog.value = true
}
defineExpose({
    showDialog,
    open
})
</script>
<style lang="scss" scoped>
.skuList :deep(.el-form-item) {
    margin-bottom: 0px !important;
}
.skuList :deep(.el-table__body .el-table__cell) {
    height: 68px !important;
}
.skuList :deep(.el-table__cell .cell) {
    overflow: initial !important;
    padding: 0 12px;
}

</style>
