<template>
    <el-drawer v-model="showDialog" :title="t('满赠活动详情')" direction="rtl" :before-close="handleClose" size="1300px">
        <div class="main-container" v-loading="loading">
            <div>
                <el-card class="box-card !border-none mb-[15px]" shadow="never">
                    <h3 class="panel-title">{{ t('basicInfo') }}</h3>
                    <el-form class="mt-[15px]" :model="formData" label-width="120px" ref="formRef" label-position="left" v-if="Object.keys(formData).length">
                    <div class="relative" v-if="formData">
                        <el-row>
                            <el-col :span="8">
                                <el-form-item :label="t('name')">
                                    <div class="input-width">
                                        <span>{{formData.title}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('status')">
                                    <div class="input-width">
                                        <span>{{formData.status_name}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <!-- <el-col :span="8">
                                <el-form-item :label="t('活动类型')">
                                    <div class="input-width">
                                        <span>{{formData.type_name}}</span>
                                    </div>
                                </el-form-item>
                            </el-col> -->
                            <el-col :span="8">
                                <el-form-item :label="t('优惠规则')">
                                    <div class="input-width">
                                        <span>{{formData.rule_type=='ladder'?'阶梯优惠':'循环优惠'}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('参与商品')">
                                    <div class="input-width">
                                        <span>{{formData.goods_type_name}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>    
                            <el-col :span="8">
                                <el-form-item :label="t('startTime')">
                                    <div class="input-width">
                                        <span>{{formData.start_time}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('endTime')">
                                    <div class="input-width">
                                        <span>{{formData.end_time}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('createTime')">
                                    <div class="input-width">
                                        <span>{{formData.create_time}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>

                    </div>
                </el-form>
                </el-card>
                <el-card class="box-card !border-none mt-4" shadow="never">
                    <h3 class="panel-title">{{ t('activeDetail') }}</h3>
                    <el-table :data="formData.rule_json" size="large">
                        <el-table-column :label="t('discountThreshold')" prop="limit">
                            <template #default="{ row }">
                                <span v-if="row.amount">满{{ row.amount }}{{ formData.type == 'amount' ? '元' : '件' }}</span>
                                <span v-else>-</span>
                            </template>
                        </el-table-column>
                        <el-table-column :label="t('赠品')" prop="discount_money">
                            <template #default="{ row }">
                                <div v-if="row.gift_sku_data && row.gift_sku_data.length">
                                    <div v-for="(goods, index) in row.gift_sku_data" :key="index" class="flex items-center">
                                        <div class="goods-name">{{ goods.goods_name }} </div>
                                        <div>* {{ goods.num }}</div>
                                    </div>
                                </div>
                                <div v-else>-</div>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
                <el-card class="box-card !border-none mt-4" shadow="never" v-if="formData.goods_type !='all'">
                    <h3 class="panel-title">{{ formData.goods_type_name }}</h3>
                   <el-table :data="formData.goods" size="large" max-height="400">
                        <el-table-column prop="goods_id" :label="t('商品信息')" min-width="300">
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
                                    <span :title="row.sku_name" class="multi-hidden">{{row.sku_name ? row.goods_name + " " + row.sku_name: row.goods_name}}</span>
                                    <span class="text-primary text-[12px]">{{row.goods_type_name}}</span>
                                </div>

                            </div>
                        </template>
                        </el-table-column>

                        <el-table-column prop="price" :label="t('goodsSelectPopupPrice')" min-width="120">
                        <template #default="{ row }">
                            <div>￥{{ row.price }}</div>
                        </template>
                        </el-table-column>
                    </el-table>
                </el-card>

            </div>
        </div>
    </el-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getFullGiftInfo} from "@/app/lang/zh-cn/api/admin/marketing";
import { useRouter } from 'vue-router'
import { img } from '@/utils/common'

const router = useRouter()
const showDialog = ref(false)
const loading = ref(false)
let id = ''
const formData:Record<string, any> = ref({})



const handleClose = (done: () => void) => {
    showDialog.value = false
}

const getManjianInfoFn = (id:number) => {
    loading.value = true
    getFullGiftInfo(id).then((res:any) => {
        formData.value = Object.assign(formData.value, res.data)
        loading.value = false
    })
}

const setFormData = async (row: any = null) => {
    id = row.id
    getManjianInfoFn(Number(id))
}

defineExpose({
    showDialog,
    setFormData
})
</script>
<style lang="scss" scoped>
.goods-name {
  max-width: 150px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>