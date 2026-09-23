<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="h-full" v-loading="invoiceTableData.loading">
			<div class="flex items-center justify-between mb-[23px] leading-[20px]">
                <div class="flex flex-wrap">
                    <div class="text-[16px] mr-[50px] text-[#666] cursor-pointer" :class="{'!text-primary font-500': invoiceTableData.params.is_invoice == item.value}" v-for="(item,index) in invoiceType" :key="index" @click="handleClick(item.value)">{{item.name}}</div>
                </div>
            </div>
			<div v-if="invoiceTableData.data.length && !invoiceTableData.loading">
                    <div>
                        <div class="rounded-[12px] overflow-hidden mb-[20px] border-[1px] border-solid border-[#eee]" v-for="(item,index) in invoiceTableData.data" :key="index">
                            <div class="h-[54px] leading-[52px] text-[14px] px-[20px] bg-[#fafafa] flex items-center justify-between">
                                <div class="flex items-center">
                                    <div class="bg-[#bbb] text-[#fff] mr-[20px] rounded-[2px] text-[12px] leading-[1] py-[2px] px-[4px]">{{ item.is_invoice_name }}</div>
                                    <div class="text-[#999] mr-[20px] oppoSans-R">{{item.orderData.create_time}}</div>
                                    <div class="text-[#999] mr-[50px] oppoSans-R">
                                        <span>发票类型：</span>
                                        <span class="text-[#333] font-400 oppoSans-R">{{item.type_name}}</span>
                                    </div>
                                </div>
                            </div>
                            <div class="flex justify-between items-center pl-[20px] ">
                                <div class="w-[786px] my-[20px] border-r-[1px] border-dashed border-[rgb(238, 238, 238)]" >
                                    <div class="flex items-center mb-[20px]" v-for="(subItem,subIndex) in item.orderData.orderGoods" :key="subIndex">
                                        <div class="flex items-center cursor-pointer">
                                            <el-image class="rounded-[var(--rounded-mid)]" style="width: 100px; height: 100px" v-if="subItem.goods_image" :src="img(subItem.goods_image)" fit="cover">
                                                <template #error>
                                                    <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                                </template>
                                            </el-image>
                                            <div class="ml-[20px] flex flex-col justify-center flex-1">
                                                <div class="text-[14px] text-[#333] truncate w-[635px] mb-[14px] font-500 oppoSans-M">{{subItem.goods_name}}</div>
                                                <div class="text-[14px] text-[#333] truncate w-[635px] mb-[15px] font-400 oppoSans-R" v-if="subItem.sku_name">规格：{{subItem.sku_name}}</div>
                                                <div class="text-[14px] text-[#333] oppoSans-R mr-[20px]">￥{{ subItem.goods_money }}×{{ subItem.num }}</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="w-[180px] pt-[20px] pb-[6px]">
                                    <div class="w-[86px] h-[30px] leading-[28px] text-[12px]  text-[#333] rounded-[15px] text-center border-[1px] border-solid border-[#ccc] cursor-pointer mx-auto mb-[14px] " @click="handleDetail(item.id)">{{t('detail')}}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mt-[16px] flex justify-end">
                        <el-pagination v-model:current-page="invoiceTableData.page" v-model:page-size="invoiceTableData.limit" layout="prev, pager, next" background :total="invoiceTableData.total"
                            @size-change="loadInvoiceList()" @current-change="loadInvoiceList" />
                    </div>
                </div>
                <div v-if="!invoiceTableData.data.length && !invoiceTableData.loading">
                    <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                </div>
        </div>
		<!-- 发票详情 -->
		<el-dialog v-model="dialogInvoiceVisible" :title="t('invoiceInfo')" width="700px" :destroy-on-close="true">
			<el-scrollbar height="250px" v-loading="loading">
				<el-form :model="invoiceData" label-width="100px" ref="formRef" class="px-[10px]" label-position="left">
					<el-row>
						<el-col :span="12" v-if="invoiceData.is_invoice_name">
							<el-form-item :label="t('invoiceStatus')">
									<div class="input-width">{{ invoiceData.is_invoice_name }}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.header_name">
							<el-form-item :label="t('headerName')">
								<div class="input-width break-all leading-[1.5]">{{ invoiceData.header_name }}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.header_type_name">
							<el-form-item :label="t('headTypeName')">
									<div class="input-width">{{ invoiceData.header_type_name }}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.name">
							<el-form-item :label="t('name')">
									<div class="input-width">{{ invoiceData.name  }}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.invoice_number">
							<el-form-item :label="t('invoiceNumber')">
									<div class="input-width">{{ invoiceData.invoice_number}}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.type_name">
							<el-form-item :label="t('typeName')">
									<div class="input-width">{{ invoiceData.type_name}}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12">
							<el-form-item :label="t('email')">
									<div class="input-width">{{ invoiceData.email }}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.money">
							<el-form-item :label="t('money')">
									<div class="input-width">{{ invoiceData.money}}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.invoice_time">
							<el-form-item :label="t('invoiceTime')">
									<div class="input-width">{{ invoiceData.invoice_time === 0 ? '' : invoiceData.invoice_time }}</div>
							</el-form-item>
						</el-col>
						<template v-if="invoiceData.header_type == 2">
							<el-col :span="12" v-if="invoiceData.tax_number">
								<el-form-item :label="t('taxNumber')">
										<div class="input-width">{{ invoiceData.tax_number}}</div>
								</el-form-item>
							</el-col>
							<el-col :span="12" v-if="invoiceData.bank_name">
								<el-form-item :label="t('bankTame')">
										<div class="input-width">{{ invoiceData.bank_name }}</div>
								</el-form-item>
							</el-col>
							<el-col :span="12" v-if="invoiceData.bank_card_number">
								<el-form-item :label="t('bankCardNumber')">
										<div class="input-width">{{ invoiceData.bank_card_number }}</div>
								</el-form-item>
							</el-col>
							<el-col :span="12" v-if="invoiceData.address">
								<el-form-item :label="t('address')">
										<div class="input-width">{{ invoiceData.address }}</div>
								</el-form-item>
							</el-col>
							<el-col :span="12" v-if="invoiceData.telephone">
								<el-form-item :label="t('telephone')">
										<div class="input-width">{{ invoiceData.telephone }}</div>
								</el-form-item>
							</el-col>
						</template>
						<el-col :span="12">
							<el-form-item :label="t('applyTime')">
									<div class="input-width">{{ invoiceData.create_time }}</div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.invoice_voucher">
							<el-form-item :label="t('invoiceVoucher')">
									<div><img class="w-[50px] max-h-[50px] inline-block"  :src="img(invoiceData.invoice_voucher)" alt="" @click="previewImage" ></div>
							</el-form-item>
						</el-col>
						<el-col :span="12" v-if="invoiceData.remark">
							<el-form-item :label="t('remark')">
									<div class="input-width break-all">{{ invoiceData.remark }}</div>
							</el-form-item>
						</el-col>
					</el-row>
				</el-form>
				
			</el-scrollbar>
			<template #footer>
				<div class="dialog-footer">
					<el-button type="primary" class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]" @click="dialogInvoiceVisible = false" dark>{{ t('confirm') }}</el-button>
				</div>
			</template>
		</el-dialog>
		<el-image-viewer :url-list="previewImageList" v-if="imageViewer.show" @close="imageViewer.show = false" :initial-index="imageViewer.index" :zoom-rate="1" />
    </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { getMallInvoice , getMallInvoiceDetail} from '@/addon/mall/api/invoice'
import { useRouter } from 'vue-router'

const router = useRouter()
const invoiceType = [
	{
		name: '全部',
		value: 'all'
	},{
		name: '已开票',
		value: 1
	},{
		name: '未开票',
		value: 0
	}]

// 获取会员列表
const invoiceTableData = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
	params: {
		is_invoice: 'all'
	}
})

const loadInvoiceList = (page = 1) =>{
    invoiceTableData.page = page
    invoiceTableData.loading = true
    getMallInvoice({
        page: invoiceTableData.page,
        limit: invoiceTableData.limit,
        ... invoiceTableData.params
    }).then((res: any) =>{
        invoiceTableData.loading = false
        invoiceTableData.total = res.data.total
        invoiceTableData.data = res.data.data.map((el: any) => {
            el.orderData.orderGoods.forEach((v: any) => {
                v.rowNum = el.orderData.orderGoods.length
            })
            return el
        })
    })
}
loadInvoiceList()

// 切换状态
const handleClick = (event:any) =>{
    invoiceTableData.params.is_invoice = event
    loadInvoiceList()
}

// 查看发票详情
const loading = ref(false)
const dialogInvoiceVisible = ref(false)
const invoiceData = ref<any>({})
const handleDetail = (id: any) =>{
	loading.value = true
    getMallInvoiceDetailFn(id)
    dialogInvoiceVisible.value = true
}
const getMallInvoiceDetailFn = async (id:any) => {
    invoiceData.value = await (await getMallInvoiceDetail(id)).data
	if(invoiceData.value.invoice_voucher) {
        previewImageList.push(img(invoiceData.value.invoice_voucher))
    }
    loading.value = false
}

let previewImageList: string[] = reactive([])

/**
 * 查看图片
 */
const imageViewer = reactive({
    show: false,
    index: 0
})
const previewImage = () => {
    imageViewer.show = true
}
</script>

<style lang="scss" scoped>
</style>