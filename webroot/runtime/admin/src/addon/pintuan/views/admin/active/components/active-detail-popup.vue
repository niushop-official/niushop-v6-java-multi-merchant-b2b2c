<template>
    <el-drawer v-model="showDialog" :title="t('活动详情')" direction="rtl" :before-close="handleClose"  size="1300px">
        <div class="main-container" v-loading="loading">
            <el-tabs v-model="activeName" class="pb-[10px]" @tab-change="handleClick">
                <el-tab-pane :label="t('基础信息')" name="basicInfo" />
                <el-tab-pane :label="t('活动商品')" name="activityGoods" />
            </el-tabs>
            <div v-if="activeName == 'basicInfo'">
                <el-form class="mt-[15px]" :model="formData" label-width="120px" ref="formRef" label-position="left" v-if="Object.keys(formData).length">
                    <div class="relative" v-if="formData">
                        <el-row>
                            <el-col :span="8">
                                <el-form-item :label="t('活动名称')">
                                    <div class="input-width">
                                        <span>{{formData.title}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('活动时间')">
                                    <div class="input-width">
                                        <span>{{formData.start_time}} ~ {{formData.end_time}}</span>
                                    </div>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item :label="t('活动对象')">
                                    <div class="input-width">
                                        <span v-if="formData.member_rule == 'all'">{{t('所有会员参与')}}</span>
                                        <div v-if="formData.member_rule == 'level'">
                                            {{ formData.member_level }}
                                        </div>
                                        <div v-if="formData.member_rule == 'label'">
                                            {{ formData.member_label }}
                                        </div>
                                    </div>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </div>
                </el-form>
            </div>
            <div v-if="activeName == 'activityGoods'">
                <el-table :data="goodsTable.list" size="large" ref="goodsInfoTableRef">
                    <el-table-column prop="nickname" :show-overflow-tooltip="true" :label="t('商品信息')" min-width="150">
                        <template #default="{ row }">
                            <div class="flex items-center">
                                <div class="mr-[10px] rounded-full w-[50px] h-[50px] flex items-center justify-center">
                                    <el-image v-if="formData.goods_data.cover || row.sku_image" :src="img(row.sku_image ? row.sku_image : formData.goods_data.cover)" fit="cover" class="w-[50px] h-[50px]" >
                                        <template #error>
                                            <img class="w-[50px] h-[50px]" src="@/addon/pintuan/assets/goods_default.png" alt="">
                                        </template>
                                    </el-image>
                                    <img class="w-[50px] h-[50px]" v-else src="@/addon/pintuan/assets/goods_default.png" alt="">
                                </div>
                                <div class="flex flex-col">
                                    <span>{{row.sku_name  ? formData.goods_data.title + " " + row.sku_name: formData.goods_data.title}}</span>
                                </div>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="original" :label="t('原价')" width="180">
                        <template #default="{ row }">
                            {{row.price_config.original}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="pintuan" :label="t('拼团价')" width="180"  v-if="formData.type != 'ladder'">
                        <template #default="{ row }">
                            {{row.price_config.pintuan}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="leader" :label="t('团长价')" width="180" v-if="formData.leader_discount=='on'">
                        <template #default="{ row }">
                            {{row.price_config.leader}}
                        </template>
                    </el-table-column>
                    <!-- 阶梯1 -->
                    <el-table-column prop="ladder" :label="t('阶梯1')" width="180" v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 1">
                        <template #default="{ row }">
                            {{ row.price_config.ladder[0].price }}
                        </template>
                    </el-table-column>
                    <!-- 阶梯2 -->
                    <el-table-column prop="ladder" :label="t('阶梯2')" width="180"  v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 2">
                        <template #default="{ row }">
                           {{ row.price_config.ladder[1].price }}
                        </template>
                    </el-table-column>
                    <!-- 阶梯3 -->
                    <el-table-column prop="ladder" :label="t('阶梯3')" width="180"  v-if="formData.type === 'ladder' && formData.member_num_config.ladder.length >= 3">
                        <template #default="{ row }">
                            {{ row.price_config.ladder[2].price }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="stock" :label="t('库存')" width="180">
                        <template #default="{ row }">
                            {{row.stock}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="member_limit_buy" :label="t('每单限购')" width="180">
                        <template #default="{ row }">
                            {{row.order_limit_buy}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="member_limit_buy" :label="t('每人限购')" width="180">
                        <template #default="{ row }">
                            {{row.member_limit_buy}}
                        </template>
                    </el-table-column>
                </el-table>
                <div class="mt-[16px] flex justify-end">
                    <el-pagination v-model:current-page="goodsTable.page" v-model:page-size="goodsTable.limit"
                        layout="total, sizes, prev, pager, next, jumper" :total="goodsTable.total"
                        @size-change="setGoodsList()" @current-change="setGoodsList" />
                </div>
            </div>
        </div>
    </el-drawer>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getPintuanAdminDetail } from '@/addon/pintuan/api/admin/active'
import { img } from '@/utils/common'

const showDialog = ref(false)
const loading = ref(false)
let id = ''
const activeName = ref('basicInfo')
const initialFormData = reactive({
})
interface goodsTableInterface {
    page: number,
    limit: number,
    total: number,
    data: any,
    list: Array<any>,
}

const goodsTable = reactive<goodsTableInterface>({
    page: 1,
    limit: 10,
    total: 0,
    data: [],
    list: []
})
// 完整数据转分页数据
const splitArray = (array: [], size: number) => {
    const result = []
    for (let i = 0; i < array.length; i += size) {
        result.push(array.slice(i, i + size))
    }
    return result
}
// 设置展示商品
const setGoodsList = (page = 1) => {
    if (Object.keys(formData.goods_data).length != 0 && formData.goods_data?.sku_list.length) {
        formData.goods_data.sku_list.forEach((item: any, index: number) => {
            item.index = index
        })
        goodsTable.data = splitArray(formData.goods_data.sku_list, goodsTable.limit)
        // 给当前页每一项添加全局索引
        goodsTable.list = goodsTable.data[page - 1]
        goodsTable.total = parseInt(formData.goods_data.sku_list.length.toString())
    } else {
        goodsTable.data = []
        goodsTable.list = []
        goodsTable.total = 0
        goodsTable.page = 1
    }
}

const formData: Record<string, any> = reactive({ ...initialFormData })

const handleClick = (data:string) => {
    activeName.value = data
}

const handleClose = (done: () => void) => {
    activeName.value = 'basicInfo'
    showDialog.value = false
}

const getPintuanAdminDetailFn = (id:number) => {
    loading.value = true
    getPintuanAdminDetail(id).then((res:any) => {
        const data = res.data
        Object.assign(formData, data)
        setGoodsList()
        if (formData.member_level) {
            formData.member_level = data.member_level.map(obj => obj.level_name)
            formData.member_level = formData.member_level.join(',')
        }
        if (formData.member_label) {
            formData.member_label = data.member_label.map(obj => obj.label_name)
            formData.member_label = formData.member_label.join(',')
        }
        loading.value = false
    })
}

const setFormData = async (row: any = null) => {
    id = row.id
    getPintuanAdminDetailFn(Number(id))
}

defineExpose({
    showDialog,
    setFormData
})
</script>
<style lang="scss" scoped></style>
