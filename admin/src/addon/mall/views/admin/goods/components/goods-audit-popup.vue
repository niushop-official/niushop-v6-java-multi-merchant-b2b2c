<template>
    <div>
        <el-dialog v-model="showDialog" :title="t('商品审核')" width="1000px" :destroy-on-close="true" :close-on-press-escape="false"  :close-on-click-modal="false">
            <el-tabs v-model="activeName">
                <el-tab-pane :label="t('商品信息')" name="basic">
                    <el-form :model="godosDetail" label-width="120px"  class="page-form" label-position="left">
                        <el-form-item :label="t('商品名称')">
                            <div class="input-width">{{ godosDetail.goods_name }}</div>
                        </el-form-item>
                        <el-form-item :label="t('商品图片')">
                            <div v-for="(imageItem, imageIndex) in godosDetail.goods_image.split(',')" :key="imageIndex" class="mr-4">
                                <el-image  class="w-[60px] h-[60px]" :src="img(imageItem)" fit="contain" :preview-src-list="godosDetail.previewList" :zoom-rate="1.2" :hide-on-click-modal="true" :max-scale="7" :min-scale="0.2" :initial-index="imageIndex">
                                    <template #error>
                                        <div class="image-slot">
                                            <img class="w-[60px] h-[60px]" src="@/addon/mall/assets/goods_default.png" />
                                        </div>
                                    </template>
                                </el-image>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('商品类目')">
                            <div class="input-width">{{ godosDetail.goods_mall_category_name }}</div>
                        </el-form-item>
                        <el-form-item :label="t('商品分类')">
                            <div class="flex">
                                <div v-for="(item, index) in godosDetail.goods_category_name" :key="index">
                                    <span>{{ item.category_name }}</span>
                                    <span v-if="(index + 1)!= godosDetail.goods_category_name.length">,</span>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('商品品牌')" v-if="godosDetail.brand_id">
                            <div class="input-width">{{ godosDetail.goods_brand.brand_name }}</div>
                        </el-form-item>
                        <el-form-item :label="t('商品标签')" v-if="godosDetail.label_ids.length">
                            <div class="flex">
                                <div v-for="(item, index) in godosDetail.goods_label_name" :key="index">
                                    <span>{{ item.label_name }}</span>
                                    <span v-if="(index + 1)!= godosDetail.goods_label_name.length">,</span>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('商品服务')" v-if="godosDetail.service_ids.length">
                            <div class="flex">
                                 <div v-for="(item, index) in godosDetail.goods_service_name" :key="index">
                                    <span>{{ item.service_name }}</span>
                                    <span v-if="(index + 1)!= godosDetail.goods_service_name.length">,</span>
                                </div>
                            </div>
                        </el-form-item>
                        <el-form-item :label="t('商品单位')">
                            <div class="input-width">{{ godosDetail.unit }}</div>
                        </el-form-item>
                        <el-form-item :label="t('商品排序')">
                            <div class="input-width">{{ godosDetail.sort }}</div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('商品规格')" name="price_stock">
                    <el-form :model="godosDetail" label-width="120px"  class="page-form" label-position="left">
                        <el-form-item :label="t('商品规格')">
                            <div class="input-width">{{ godosDetail.spec_type == 'multi' ? '多规格': '单规格' }}</div>
                        </el-form-item>
                        <el-form-item :label="t('商品规格')">
                            <el-table :data="godosDetail.sku_list" border>
                                <el-table-column prop="sku_name" label="规格名称" min-width="120" v-if="godosDetail.spec_type == 'multi'" />
                                <el-table-column label="图片" min-width="120">
                                     <template #default="{ row }">
                                        <el-image v-if="row.sku_image" class="w-[50px] h-[50px]" :src="img(row.sku_image)" fit="contain">
                                            <template #error>
                                                <div class="image-slot">
                                                    <img class="w-[50px] h-[50px]" src="@/addon/mall/assets/goods_default.png" />
                                                </div>
                                            </template>
                                        </el-image>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="price" label="销售价" min-width="120" />
                                <el-table-column prop="market_price" label="划线价" min-width="120" />
                                <el-table-column prop="cost_price" label="成本价" min-width="120" />
                                <el-table-column prop="stock" label="库存" min-width="120" />
                                <el-table-column prop="weight" label="重量(kg)" min-width="120"  v-if="godosDetail.goods_type == 'real'"/>
                                <el-table-column prop="volume" label="体积(m³)" min-width="120"  v-if="godosDetail.goods_type == 'real'" />
                            </el-table>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
                <el-tab-pane :label="t('商品详情')" name="detail">
                    <el-form :model="godosDetail" label-width="120px" class="page-form" label-position="left">
                        <el-form-item :label="t('商品详情')">
                           <div class="max-h-[500px] flex-1 overflow-y-auto">
                                <div v-html="godosDetail.goods_desc"></div>
                           </div>
                        </el-form-item>
                    </el-form>
                </el-tab-pane>
            </el-tabs>
            <el-form :model="formData" label-width="120px" class="page-form"  ref="formRef" :rules="formRules" label-position="left">
                <el-form-item :label="t('审核状态')" prop="status">
                    <el-radio-group v-model="formData.status" >
                        <el-radio label="pass">通过</el-radio>
                        <el-radio label="refuse">拒绝</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item :label="t('auditReason')" prop="audit_reason" v-if="formData.status == 'refuse'">
                    <el-input v-model.tirm="formData.audit_reason" type="textarea" rows="4" clearable  :maxlength="100"  :show-word-limit="true" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
                    <el-button type="primary" :loading="loading" @click="confirmEvent(formRef)">{{ t('confirm') }}</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script lang="ts" setup>
import { t } from '@/lang'
import { ref, reactive, computed } from 'vue'
import { cloneDeep } from 'lodash-es'
import { img } from '@/utils/common'
import { getGoodsInfo, audit, refuse } from '@/app/lang/zh-cn/api/admin/goods'
import { FormInstance } from 'element-plus'

const showDialog = ref(false)
const activeName: any = ref('basic')
const godosDetail = reactive<any>({})
const loading = ref(true)
const formRef = ref()
const formData = reactive({
    goods_ids: '',
    status: 'pass',
    audit_reason: ''
})

// 表单验证规则
const formRules = computed(() => {
    return {
        status: [
            { required: true, message: '请选择审核状态', trigger: ['change', 'blur'] }
        ],
        audit_reason: [
            { required: true, message: '请输入拒绝原因', trigger: 'blur' }
        ]
    }
})
const emit = defineEmits(['confirm'])

// 拒绝审核,强制下架
const confirmEvent = async (formEl: FormInstance | undefined) => {
    if (loading.value || !formEl) return
    await formEl.validate(async (valid) => {
        if (valid) {
            loading.value = true
            const api = formData.status == 'pass' ? audit : refuse
            const data = {
                goods_ids: formData.goods_ids,
                audit_reason: formData.audit_reason
            }
            api(data).then(res => {
                loading.value = false
                showDialog.value = false
                emit('confirm')
            }).catch(() => {
                loading.value = false
                showDialog.value = false
            })
        }
    })
}
const show = (data: any) => {
    loading.value = true
    getGoodsInfo(data.goods_id).then((res: any) => {
        Object.assign(godosDetail, res.data)
        godosDetail.previewList = res.data.goods_image?.split(',').map((item: any) => img(item))
        formData.goods_ids = res.data.goods_id
        loading.value = false
        showDialog.value = true
    }).catch(() => {
        loading.value = false
    })
}
defineExpose({
    show
})
</script>

<style lang="scss" scoped></style>