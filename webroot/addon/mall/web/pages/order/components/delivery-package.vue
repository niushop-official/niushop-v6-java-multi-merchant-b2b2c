<template>
    <el-dialog v-model="showDialog" :title="t('packageInfo')" width="700px"
        class="diy-dialog-wrap" :destroy-on-close="true">
		<el-tabs v-model="formData.id" class="demo-tabs" @tab-change="handleClick">
			<el-tab-pane :label="`包裹${index+1}`" :name="item.id" v-for="(item,index) in orderDelivery"></el-tab-pane>
		</el-tabs>
        <div v-loading="loading" class="max-h-[500px]  delivery-info overflow-y-auto pr-[10px]">
			<el-form :model="packageData" label-width="80px"  label-position="left">
				<h3 class="panel-title mb-[10px]">{{ t('deliveryInfo') }}</h3>
				<el-row>
					<el-col :span="12">
						<el-form-item :label="t('devliveryTime')">
							<div class="input-width">{{ packageData.create_time}}</div>
						</el-form-item>
					</el-col>
					<el-col :span="12" v-if="packageData.company">
						<el-form-item :label="t('companyName')">
							<div class="input-width">{{ packageData.company.company_name}}</div>
						</el-form-item>
					</el-col>
					<el-col :span="12">
						<el-form-item :label="t('logisticNo')">
							<div class="input-width">{{ packageData.express_number  }}</div>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form>
			<h3 class="panel-title mb-[10px]">{{ t('logisticInfo') }}</h3>
			<div v-if="packageData.sub_delivery_type == 'express'">
				<div v-if="packageData.traces.success == false">{{ t('notLogistics') }}</div>
				<div v-else>
					<el-timeline style="max-width: 640px">
						<el-timeline-item  v-for="(item, index) in packageData.traces.list" :key="index" :timestamp="item.datetime" :color="item.color">
							{{ item.remark }}
						</el-timeline-item>
					</el-timeline>
				</div>
			</div>
			<div v-else-if="packageData.sub_delivery_type == 'none_express'">
				<div>{{ t('withoutLogistics') }}</div>
			</div>
		</div>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="showDialog = false">{{ t('cancel') }}</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { getMaterialflowList } from '@/addon/mall/api/order'

const showDialog = ref(false)
const loading = ref(false)
const packageData = ref('')
const formData = ref({
	id:'',
	mobile:''
})
const orderDelivery = ref([])

const setFormData = async (row:any) => {
    loading.value = true
    if (row) {
		orderDelivery.value = row.order_delivery.filter(item => item.sub_delivery_type == 'express')
		formData.value.id = row.order_delivery[0].id
		formData.value.mobile = row.taker_mobile
        getMaterialflowList(formData.value).then((res:any) => {
            packageData.value = res.data
			if(packageData.value.traces.list){
				packageData.value.traces.list[0].color = '#EF000C'
			}
            loading.value = false
        })
    }
}
// 切换物流信息
const handleClick = (event: any) => {
	formData.value.id = event
    getMaterialflowFn(formData.value)
}

const getMaterialflowFn = (data:any)=>{
	loading.value = true
	getMaterialflowList(data).then((res:any)=>{
		packageData.value = res.data
        loading.value = false
	})
}
defineExpose({
    showDialog,
    setFormData
})
</script>

<style lang="scss" scoped></style>
<style lang="scss">
.diy-dialog-wrap .el-form-item__label {
    height: auto !important;
}
.notes-form .el-dialog__body .el-form-item{
	margin-bottom: 0px !important;
}
/* 多行超出隐藏 */
.multi-hidden {
	word-break: break-all;
	text-overflow: ellipsis;
	overflow: hidden;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}
.delivery-info::-webkit-scrollbar{
	width:4px;
	border-radius:2px;
	background-color:#f1f1f1;
}
.delivery-info::-webkit-scrollbar-thumb{
	background-color:#c1c1c1;
	border-radius:2px;
}
</style>
