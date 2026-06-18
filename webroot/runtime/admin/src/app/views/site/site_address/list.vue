<template>
	<div class="main-container">
		<el-card class="box-card !border-none" shadow="never">

			<div class="flex justify-between items-center">
				<span class="right">{{ pageName }}</span>
				<el-button type="primary" @click="addEvent">
					{{ t('addShopAddress') }}
				</el-button>
			</div>

			<el-card class="box-card !border-none my-[10px] table-search-wrap" shadow="never">
				<el-form :inline="true" :model="ShopAddressTable.searchParam" ref="searchFormRef">
					<el-form-item :label="t('mobile')" prop="mobile">
						<el-input v-model="ShopAddressTable.searchParam.mobile" :placeholder="t('mobilePlaceholder')"/>
					</el-form-item>
					<el-form-item :label="t('fullAddress')" prop="full_address">
						<el-input v-model="ShopAddressTable.searchParam.full_address"  :placeholder="t('fullAddressPlaceholder')"/>
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="loadShopAddressList()">{{ t('search') }}</el-button>
						<el-button @click="resetForm(searchFormRef)">{{ t('reset') }}</el-button>
					</el-form-item>
				</el-form>
			</el-card>

			<div class="mt-[10px]">
				<el-table :data="ShopAddressTable.data" size="large" v-loading="ShopAddressTable.loading">
					<template #empty>
						<span>{{ !ShopAddressTable.loading ? t('emptyData') : '' }}</span>
					</template>
					<el-table-column prop="contact_name" :label="t('contactName')" min-width="100"/>
					<el-table-column prop="mobile" :label="t('mobile')" min-width="100"/>
					<el-table-column prop="full_address" :label="t('fullAddress')" min-width="120" :show-overflow-tooltip="true"/>
					<el-table-column prop="is_delivery_address" :label="t('addressType')" min-width="120" align="left">
						<template #default="{ row }">
							<div v-if="row.is_delivery_address">
								{{ t('deliveryAddress') }}
								<el-tag size="small" v-if="row.is_default_delivery">{{ t('default') }}</el-tag>
							</div>
							<div v-if="row.is_refund_address">
								{{ t('refundAddress') }}
								<el-tag size="small" v-if="row.is_default_refund">{{ t('default') }}</el-tag>
							</div>
						</template>
					</el-table-column>

					<el-table-column :label="t('operation')" fixed="right" min-width="120" align="right">
						<template #default="{ row }">
							<el-button type="primary" link @click="editEvent(row)">{{ t('edit') }}</el-button>
							<el-button type="primary" link @click="deleteEvent(row.id)">{{ t('delete') }}</el-button>
						</template>
					</el-table-column>

				</el-table>
				<div class="mt-[16px] flex justify-end">
					<el-pagination v-model:current-page="ShopAddressTable.page" v-model:page-size="ShopAddressTable.limit" layout="total, sizes, prev, pager, next, jumper" :total="ShopAddressTable.total"  @size-change="loadShopAddressList()" @current-change="loadShopAddressList"/>
				</div>
			</div>

		</el-card>
	</div>
</template>

<script lang="ts" setup>
import { reactive, ref } from 'vue'
import { t } from '@/lang'
import { getShopAddressList, deleteShopAddress } from '@/app/api/delivery_site'
import { ElMessageBox, FormInstance } from 'element-plus'
import { useRouter, useRoute } from 'vue-router'

const route = useRoute()
const pageName = route.meta.title

const ShopAddressTable = reactive({
    page: 1,
    limit: 10,
    total: 0,
    loading: true,
    data: [],
    searchParam: {
        mobile: '',
        full_address: ''
    }
})

const searchFormRef = ref<FormInstance>()

/**
 * 获取商家地址库列表
 */
const loadShopAddressList = (page: number = 1) => {
    ShopAddressTable.loading = true
    ShopAddressTable.page = page

    getShopAddressList({
        page: ShopAddressTable.page,
        limit: ShopAddressTable.limit,
        ...ShopAddressTable.searchParam
    }).then(res => {
        ShopAddressTable.loading = false
        ShopAddressTable.data = res.data.data
        ShopAddressTable.total = res.data.total
    }).catch(() => {
        ShopAddressTable.loading = false
    })
}
loadShopAddressList()

const router = useRouter()

/**
 * 添加商家地址库
 */
const addEvent = () => {
    router.push('/site_address/edit')
}

/**
* 编辑商家地址库
 * @param data
 */
const editEvent = (data: any) => {
    router.push('/site_address/edit?id=' + data.id)
}

/**
 * 删除商家地址库
 */
const deleteEvent = (id: number) => {
    ElMessageBox.confirm(t('ShopAddressDeleteTips'), t('warning'),
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            type: 'warning'
        }
    ).then(() => {
        deleteShopAddress(id).then(() => {
            loadShopAddressList()
        }).catch(() => {
        })
    })
}

const resetForm = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    formEl.resetFields()
    loadShopAddressList()
}

</script>

<style lang="scss" scoped>
	/* 多行超出隐藏 */
	.multi-hidden {
		word-break: break-all;
		text-overflow: ellipsis;
		overflow: hidden;
		display: -webkit-box;
		-webkit-line-clamp: 2;
		-webkit-box-orient: vertical;
	}
</style>
