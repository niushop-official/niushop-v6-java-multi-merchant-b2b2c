<template>
    <div class="main-container">
        <el-card class="card !border-none" shadow="never">
            <el-page-header :content="cardId ? '编辑会员卡' : '添加会员卡'" :icon="ArrowLeft" @back="back()" />
        </el-card>
        <el-card class="box-card mt-[15px] !border-none" shadow="never" v-loading="loading">
            <el-form :model="formData" label-width="140px"  ref="formRef" :rules="formRules" scroll-to-error class="page-form">
                <el-form-item label="会员卡名称" prop="card_name">
                    <el-input v-model.trim="formData.card_name" clearable placeholder="请输入会员卡名称" class="input-width" :maxlength="20" show-word-limit />
                </el-form-item>
                <el-form-item label="会员卡简介" prop="desc">
                    <el-input v-model="formData.desc" placeholder="请输入会员卡简介" type="textarea" maxlength="500" show-word-limit rows="5" class="input-width" clearable />
                </el-form-item>
                <el-form-item label="领卡权益" prop="benefits_config">
                    <div v-if="formData.benefits_config.length">
                        <span>已选{{formData.benefits_config.length}}项权益</span>
                        <el-button type="primary" link @click="handleChange">修改</el-button>
                    </div>
                    <el-button v-else  type="primary" link @click="handleChange" >选择权益</el-button>
                    <el-button type="primary" link @click="toSet">去配置</el-button>
                </el-form-item>
                <el-form-item label="是否开启试用" prop="is_trial_open">
                    <div>
                        <el-radio-group v-model="formData.is_trial_open" @change="handleTrial">
                            <el-radio :label="1">是</el-radio>
                            <el-radio :label="0">否</el-radio>
                        </el-radio-group>
                        <div>
                            <div class="text-[12px] text-[#999] leading-[20px]">1、试用卡每位用户仅限购买一次。</div>
                            <div class="text-[12px] text-[#999] leading-[20px]">2、试用期仅支持商品优惠福利，购卡返利、阶段 / 周期返利、生日特权均不生效。</div>
                        </div>
                    </div>
                </el-form-item>
                <template v-if="formData.is_trial_open">
                    <el-form-item label="试用天数" prop="trial_day">
                        <el-input v-model.trim="formData.trial_day" clearable placeholder="请输入试用天数" class="input-width" :maxlength="20" />
                    </el-form-item>
                    <el-form-item label="试用价" prop="trial_price">
                        <el-input v-model.trim="formData.trial_price" clearable  class="input-width" placeholder="请输入试用价" maxlength="8">
                            <template #append>
                                <span>元</span>
                            </template>
                        </el-input>
                    </el-form-item>
                </template>
                <el-form-item label="有效期设置" prop="item_list" class="time-wrap">
                    <div class="flex-1">
                        <el-table :data="formData.item_list" class="flex-1">
                            <el-table-column label="规格名称" min-width="120">
                                <template #default="scope">
                                    <el-form-item :prop="`item_list[${scope.$index}].item_name`" :rules="[{ required: true, message: '请输入规格名称', trigger: 'blur' }]" >
                                        <el-input v-model.trim="scope.row.item_name" clearable placeholder="请输入规格名称" class="!w-[200px]" maxlength="60" />
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="有效期" min-width="120">
                                <template #default="scope">
                                    <el-form-item :prop="`item_list[${scope.$index}].time_limit`" :rules="[{ required: true, message: '请输入有效期', trigger: 'blur' }]" >
                                        <el-input v-model.trim="scope.row.time_limit" clearable  maxlength="8">
                                            <template #append>
                                                <el-select v-model="scope.row.time_type" class="!w-[60px]">
                                                    <el-option label="天" value="day" />
                                                    <el-option label="月" value="month" />
                                                    <el-option label="年" value="year" />
                                                </el-select>
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="价格" min-width="100">
                                <template #default="scope">
                                    <el-form-item :prop="`item_list[${scope.$index}].price`"
                                        :rules="[{
                                                    trigger: 'blur',
                                                    validator: (rule: any, value: any, callback: any) => {
                                                        if (value == '') {
                                                            callback('请输入价格')
                                                        } else if (value < 0) {
                                                            callback('价格不能小于0')
                                                        } else {
                                                            callback()
                                                        }
                                                    }
                                                }]">
                                        <el-input v-model.trim="scope.row.price" clearable  maxlength="8">
                                            <template #append>
                                                <span>元</span>
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="划线价" min-width="100">
                                <template #default="scope">
                                    <el-form-item :prop="`item_list[${scope.$index}].cost_price`"
                                        :rules="[{
                                                    trigger: 'blur',
                                                    validator: (rule: any, value: any, callback: any) => {
                                                        if (value < 0) {
                                                            callback('划线价不能小于0')
                                                        } else {
                                                            callback()
                                                        }
                                                    }
                                                }]">
                                        <el-input v-model.trim="scope.row.cost_price" clearable   maxlength="8">
                                            <template #append>
                                                <span>元</span>
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="续费价" min-width="100">
                                <template #default="scope">
                                    <el-form-item :prop="`item_list[${scope.$index}].renew_price`"
                                        :rules="[{
                                                    trigger: 'blur',
                                                    validator: (rule: any, value: any, callback: any) => {
                                                        if (value < 0) {
                                                            callback('价续费价不能小于0')
                                                        } else {
                                                            callback()
                                                        }
                                                    }
                                                }]">
                                        <el-input v-model.trim="scope.row.renew_price" clearable  maxlength="8">
                                            <template #append>
                                                <span>元</span>
                                            </template>
                                        </el-input>
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="是否试用" min-width="80">
                                <template #default="scope">
                                    <el-form-item>
                                        <el-switch v-model="scope.row.is_trial" :active-value="1"  :inactive-value="0" :disabled='formData.is_trial_open == 0'  />
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="是否售卖" min-width="80">
                                <template #default="scope">
                                    <el-form-item>
                                        <el-switch v-model="scope.row.is_sale" :active-value="1"  :inactive-value="0" />
                                    </el-form-item>
                                </template>
                            </el-table-column>
                            <el-table-column label="操作" fixed="right" min-width="100">
                                <template #default="scope">
                                    <el-button link type="primary" @click.prevent="deleteRow(scope.$index)">删除</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                        <el-button type="primary" @click="addItem()" class="mt-[15px]">添加规格</el-button>
                    </div>
                </el-form-item>
                <el-form-item label="分享标题" prop="share_config">
                    <el-input v-model.trim="formData.share_config.share_title" clearable class="input-width" placeholder="请输入分享标题" maxlength="100" show-word-limit />
                </el-form-item>
                <el-form-item label="分享描述" prop="share_config">
                    <el-input v-model.trim="formData.share_config.share_desc" clearable class="input-width" placeholder="请输入分享描述" maxlength="500" show-word-limit />
                </el-form-item>
                <el-form-item label="分享图片（h5）" prop="share_config">
                    <div>
                        <upload-image v-model="formData.share_config.share_image_h5" :limit="1" />
                    </div>
                </el-form-item>
                <el-form-item label="分享图片（小程序）" prop="share_config">
                    <div>
                        <upload-image v-model="formData.share_config.share_image_weapp" :limit="1" />
                    </div>
                </el-form-item>
            </el-form>
        </el-card>
        <!-- 提交按钮 -->
        <div class="fixed-footer-wrap">
            <div class="fixed-footer">
                <el-button type="primary" @click="confirm(formRef)">{{ t("save") }}</el-button>
                <el-button @click="back()">{{ t("back") }}</el-button>
            </div>
        </div>
        <el-dialog v-model="benefitsDialog" :title="'会员权益'" width="800px" :close-on-press-escape="false" :destroy-on-close="true" :close-on-click-modal="false">
            <div class="flex">
                <div class="flex-1 h-[500px] flex box-border border-[1px] border-solid border-[#e3e2e5]">
                    <div class="w-[300px] px-[20px] box-border h-full overflow-y-auto border-r border-[#e3e2e5]">
                        <el-checkbox-group v-model="selectedBenefitsIdList" size="large">
                            <div class="flex items-center justify-between" v-for="(item,index) in benefitsList" :key="index">
                                <el-checkbox :label="item.name" :value="item.benefits_id"   @change="benefitsChange($event, item)" />
                                <span class="nc-iconfont nc-icon-youV6xx text-primary" v-if="currentSelectBenefits.benefits_id == item.benefits_id"></span>
                            </div>
                        </el-checkbox-group>
                    </div>
                    <div class="flex-1 px-[20px] box-border h-full overflow-y-auto">
                        <el-radio-group v-model="benefitsItemMap[currentSelectBenefits.benefits_id]" class="flex-col !items-start">
                            <el-radio :label="subItem.benefits_item_id" v-for="(subItem, index) in currentSelectBenefits.item_list" :key="index"  @change="handleSubChange(currentSelectBenefits)">{{ subItem.name }}</el-radio>
                        </el-radio-group>
                    </div>
                </div>
                <div class="bg-[#f7f8fa] w-[200px] p-[10px] box-border ml-[20px] overflow-y-auto flex-shrink-0">
                    <div class="text-[12px] text-[#666] mb-[12px]">已选{{ selectedBenefitsIdList.length }}项</div>
                    <div v-for="(item, idx) in selectedBenefitsText" :key="idx" class="text-[12px] text-[#333] mb-[12px]">
                        <span>{{ item.mainName }}</span>
                        <span v-if="item.mainType != 'o2o_buy_goods_discount' && item.mainType != 'mall_buy_goods_discount'">：{{ item.subName }}</span>
                    </div>
                </div>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="getBenefitsListFn(true)">刷新</el-button>
                    <el-button @click="benefitsDialog = false">取消</el-button>
                    <el-button type="primary" @click="onSave()">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup lang="ts">
import { t } from '@/lang'
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, FormInstance } from 'element-plus'
import { addVipcard, editVipcard, getVipcardInfo, getBenefitsList } from '@/app/api/vip_card'
import { cloneDeep } from 'lodash-es'

const router = useRouter()
const route = useRoute()
const cardId: any = Number(route.query.card_id) || ''
const loading = ref(true)

const formData = reactive<any>({
    card_id: '',
    card_name: '',
    desc: '',
    benefits_config: [],
    is_trial_open: 0,
    trial_day: '',
    trial_price: '',
    item_list: [],
    share_config: {
        share_title: '',
        share_desc: '',
        share_image_h5: '',
        share_image_weapp: ''
    }
})

const formRef = ref<FormInstance>()

const getVipcardInfoFn = () => {
    getVipcardInfo(cardId).then((res: any) => {
        Object.assign(formData, res.data)
        formData.item_list = cloneDeep(res.data.item)
        selectedBenefitsIdList.value = res.data.benefits_config.map((item: any) => item.benefits_id)
        benefitsItemMap.value = {}
        res.data.benefits_config.forEach((item: any) => {
            if (item.benefits_item?.benefits_item_id) {
                benefitsItemMap.value[item.benefits_id] = item.benefits_item.benefits_item_id
            }
        })
        loading.value = false
    }).catch(() => {
        loading.value = false
    })
}
if (cardId) {
    getVipcardInfoFn()
} else {
    loading.value = false
}

const benefitsList = ref<any>([])
const benefitsDialog = ref(false)
const selectedBenefitsIdList = ref<any>([]) // 选中的权益id
const benefitsItemMap = ref<Record<string | number, string | number>>({})
const currentSelectBenefits = ref<any>(null)

const getBenefitsListFn = (bool = false) => {
    getBenefitsList().then((res: any) => {
        benefitsList.value = res.data
        if (!bool) currentSelectBenefits.value = benefitsList.value[0]
        if (bool) {
            ElMessage({
                message: '刷新成功',
                type: 'success'
            })
        }
    })
}

getBenefitsListFn()

const handleChange = () => {
    if (benefitsList.value == 0) {
        ElMessage({
            message: '请先开启会员权益',
            type: 'warning'
        })
        return false
    }
    benefitsDialog.value = true
}

const selectedBenefitsText = computed(() => {
    return selectedBenefitsIdList.value.map((benefitsId: any) => {
        // 找到一级权益
        const mainItem = benefitsList.value.find((item: any) => item.benefits_id === benefitsId)
        // 找到选中的子项ID
        const itemId = benefitsItemMap.value[benefitsId]
        // 找到子项名称
        const subItem = mainItem?.item_list?.find((s: any) => s.benefits_item_id === itemId)

        return {
            mainType: mainItem?.type || '',
            mainName: mainItem?.name || '',
            subName: subItem?.name || ''
        }
    })
})

const benefitsChange = (checked: any, data: any) => {
    currentSelectBenefits.value = data
    if (checked) {
        if (!benefitsItemMap.value[data.benefits_id] && data.item_list?.length) {
            benefitsItemMap.value[data.benefits_id] = data.item_list[0].benefits_item_id
        }
    } else {
        delete benefitsItemMap.value[data.benefits_id]
    }
}

const handleSubChange = (mainItem: any) => {
    if (!mainItem) return
    const id = mainItem.benefits_id
    // 如果还没勾选，则自动勾选
    if (!selectedBenefitsIdList.value.includes(id)) {
        selectedBenefitsIdList.value.push(id)
    }
}
const onSave = () => {
    if (selectedBenefitsIdList.value.length == 0) {
        ElMessage.warning('请至少选择一项会员权益')
        return false
    }
    const result = selectedBenefitsIdList.value.map((benefitsId: any) => {
        const mainItem = benefitsList.value.find((item: any) => item.benefits_id === benefitsId)
        const itemId = benefitsItemMap.value[benefitsId]
        if (itemId && mainItem?.item_list?.length) {
            return {
                benefits_id: benefitsId,
                benefits_item: {
                    benefits_item_id: itemId
                }
            }
        } else {
            return {
                benefits_id: benefitsId
            }
        }
    })
    formData.benefits_config = result
    benefitsDialog.value = false
}
const addItem = () => {
    formData.item_list.push({
        item_name: '',
        time_limit: '',
        time_type: 'day',
        price: '',
        cost_price: '',
        renew_price: '',
        is_trial: 0,
        is_sale: 1
    })
}
const deleteRow = (index: number) => {
    formData.item_list.splice(index, 1)
}

// 正则表达式
const regExp: any = {
    required: /[\S]+/,
    number: /^\d{0,10}$/,
    digit: /^\d{0,10}(.?\d{0,2})$/,
    special: /^\d{0,10}(.?\d{0,3})$/
}
const formRules = computed(() => {
    return {
        card_name: [
            { required: true, message: '请输入会员卡名称', trigger: 'blur' }
        ],
        benefits_config: [
            {
                validator: (rule: any, value: any, callback: any) => {
                    if (formData.benefits_config.length == 0) {
                        return callback(new Error('请选择会员权益'))
                    } else {
                        return callback()
                    }
                }
            }
        ],
        trial_day: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (value === '') {
                        callback('请输入会员卡有效期')
                    } else if (isNaN(value) || !regExp.number.test(value)) {
                        callback(new Error(t('会员卡有效期格式不正确')))
                    } else if (value <= 0) {
                        callback('会员卡有效期不能小于等于0')
                    } else {
                        callback()
                    }
                }
            }
        ],
        trial_price: [
            {
                trigger: 'blur',
                validator: (rule: any, value: any, callback: any) => {
                    if (value == '') {
                        callback('请输入价格')
                    } else if (value < 0) {
                        callback('价格不能小于0')
                    } else {
                        callback()
                    }
                }
            }
        ],
        item_list: [
            { required: true, message: '请添加规格', trigger: 'blur' }
        ]
    }
})

const handleTrial = (val: any) => {
    if (val == 0 && formData.item_list.length) {
        formData.item_list.forEach((item: any) => {
            item.is_trial = 0
        })
    }
}

const confirm = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return

    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true

            const api = formData.card_id ? editVipcard : addVipcard
            api(formData).then((res: any) => {
                loading.value = false
                back()
            }).catch(() => {
                loading.value = false
            })
        }
    })
}
const back = () => {
    router.push('/member/vip_card/list')
}

const toSet = () => {
    const url = router.resolve({
        path: '/member/vip_card/benefits'
    })
    window.open(url.href)
}

</script>

<style  lang="scss" scoped>
.time-wrap :deep(.el-form-item__content) {
    display: block;
}
.time-wrap :deep(.el-table__body .el-table__cell) {
    height: 68px !important;
}
.time-wrap :deep(.el-table__cell .cell) {
    overflow: initial !important;
    padding: 0 12px;
}
</style>
