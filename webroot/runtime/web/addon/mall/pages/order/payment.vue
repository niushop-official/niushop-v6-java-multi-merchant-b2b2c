<template>
    <div class="min-h-[500px]" v-loading="createLoading">
        <div class="main-container pt-[30px]" v-if="Object.keys(orderData).length">
            <div v-if="orderData.basic.has_goods_types.includes('real')">
                <div class="bg-[#fff] mb-[20px] px-[30px] pt-[30px] pb-[20px] rounded-[var(--rounded-big)]">
                    <div class="text-[16px] text-[#333] mb-[20px]">{{ t('deliveryAddress') }}</div>
                    <div class="flex flex-wrap  h-[106px] justify-between overflow-hidden" id="address-list">
                        <div class="w-[560px] h-[90px] leading-[90px] text-[#333] text-[14px] border-[1px] border-solid border-[#ccc] text-center cursor-pointer rounded-[var(--rounded-med)]" @click="addAddressFn">
                            <span class="text-[#666]">+</span>
                            <span>{{ t('addDeliveryAddress') }}</span>
                        </div>
                        <template v-for="(item, index) in addressList">
                            <div class="w-[560px] h-[90px] border-[1px] border-solid border-[#ccc] p-[20px] mb-[20px] overflow-hidden  relative cursor-pointer rounded-[var(--rounded-med)]" :class="{'!border-primary': orderData.delivery.take_address.id == item.id}" @click="selectAddressFn(item)">
                                <div class="flex items-center font-500 mb-[10px] text-[14px] text-[#333]">
                                    <span class="truncate max-w-[200px]">{{item.name}}</span>
                                    <span>{{item.mobile}}</span>
                                    <span class="text-[12px] text-[#fff] bg-primary text-center px-[4px] py-[2px] rounded-[2px] ml-[6px]" v-if="item.is_default == 1">{{ t('default') }}</span>
                                </div>
                                <div class="text-[14px] text-[#666] font-400 truncate leading-[18px] oppoSans-R">{{item.full_address}}</div>
                            </div>
                        </template>
                    </div>
                    <div v-if="addressList.length">
                        <div class="pt-[14px] text-center text-[#666] cursor-pointer text-[14px]"  @click="showMoreAddress" v-if="activeAddress">{{ t('allAddress') }}
                            <span class="iconfont icon-xiajiantou !text-[12px] ml-[4px]"></span>
                        </div>
                        <div class="pt-[14px] text-center text-[#666] cursor-pointer text-[14px]"  @click="hiddenMoreAddress" v-else>{{ t('packUp') }}
                            <span class="iconfont icon-xiangshangjiantou !text-[12px] ml-[4px]"></span>
                        </div>
                    </div>
                </div>
            </div>
            <view v-if="orderData.basic.has_goods_types.length == 1 && orderData.basic.has_goods_types.includes('virtual') && orderData.basic.virtual_verify_mobile == 1" class="flex items-center bg-[#fff] mb-[20px] px-[30px] py-[20px] rounded-[var(--rounded-big)]">
                <div class="flex items-center whitespace-nowrap text-[16px] text-[#333] mr-[10px]">
                    <span class="iconfont icon-dianhuaV6xx mr-[6px]"></span>
                    <span>联系方式</span>
                </div>
                <el-input v-model="createData.delivery.taker_mobile" class="h-[40px] !w-[350px]" type="Number" :placeholder="t('请填写手机号')" maxlength="11" />
            </view>
            <div class=""> 
                <div v-for="(item, key, index) in orderData.order_list" :key="index">
                    <div class="bg-[#fff] px-[30px] pt-[30px] pb-[20px] mb-[20px] rounded-[var(--rounded-big)]">
                        <div class="flex items-center mb-[30px]">
                            <span class="iconfont icon-Vector-25 text-[#999] mr-[6px]"></span>
                            <span class="text-[14px] text-[#333]">{{item.site_info.site_name}}</span>
                        </div>
                        <div>
                            <template v-for="(subItem, subKey, subIndex) in item.goods_data" :key="subIndex">
                                <div class="mb-[30px] flex items-center justify-between" >
                                    <div class="flex">
                                        <div class="w-[100px] h-[100px]">
                                            <el-image class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]"  :src="img(subItem.sku_image)" fit="cover">
                                                <template #error>
                                                    <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                                </template>
                                            </el-image>
                                        </div>
                                        <div class="ml-[20px] flex flex-col justify-center text-[14px] ">
                                            <div class="text-[#333] font-500 w-[580px] truncate mb-[14px]"> {{ subItem.goods.goods_name }}</div>
                                            <div class="text-[#919191] oppoSans-R" v-if="subItem.sku_name">规格：{{ subItem.sku_name }}</div>
                                        </div>
                                    </div>
                                    <div class="flex justify-center items-center">
                                        <div class="w-[160px] text-[14px] text-[#333] oppoSans-R">¥{{subItem.price}}</div>
                                        <div class="w-[100px] text-[14px] text-[#333] text-right oppoSans-R">x{{ subItem.num }}</div>
                                        <div class="w-[160px] text-[var(--el-price)]  text-right">
                                            <span class="text-[12px] price-font">¥</span> 
                                            <span class="font-600 text-[18px] price-font">{{ parseFloat(Number(subItem.price) * Number(subItem.num)).toFixed(2) }}</span>
                                        </div>
                                    </div>
                                </div>
                                <diy-form ref="diyFormGoodsRef" :form_id="subItem.goods.form_id" :relate_id="subItem.sku_id"></diy-form>
                            </template>
                            <div class="h-[1px] border-t-[1px] border-dashed border-[#dcdfe6] my-[24px]"></div>
                        </div>
                        <div class="mb-[30px] flex justify-between items-center" v-if="orderData.basic.has_goods_types[0] != 'virtual'">
                            <div class="text-[16px] text-[#666]">{{ t('deliveryMoney') }}</div>
                            <div class="text-[16px] text-[#333] price-font">￥{{parseFloat(item.basic.delivery_money).toFixed(2)}}</div>
                        </div>
                        <!-- 优惠劵 -->
                        <div class="mb-[30px] flex justify-between items-center">
                            <div class="text-[16px] text-[#666]">店铺优惠</div>
                            <div>
                                <div class="text-[16px] text-[333] cursor-pointer" v-if="couponData[key] && couponData[key].couponList.length"  @click="couponRef.open(createData.body[key])">
                                    <span class="price-font">-￥{{parseFloat(item.basic.discount_money).toFixed(2)}}</span>
                                    <span class="iconfont icon-xiaV6xx !text-[20px]"></span>
                                </div>
                                <div v-else class="text-[#666]">暂无可使用优惠</div>
                            </div>
                        </div>
                        <div v-if="item.form_id" class="mb-[30px]">
                            <diy-form ref="diyFormRef" :form_id="item.form_id" :relate_id="item.site_id"></diy-form>
                        </div>
                        <div class="mb-[30px] flex items-center" v-if="item.config && item.config.invoice &&item.config.invoice.is_invoice == 1">
                            <div class="text-[16px] text-[#666] w-[10%]">{{ t('invoiceInfo') }}</div>
                            <div class="flex-1 flex items-center justify-start">
                               <div class="flex items-center">
                                    <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3] flex box-border px-[50px] leading-[32px] text-[14px] rounded-[var(--rounded-med)]" :class="{'!border-primary text-primary': !createData.body[key].invoice  }" @click="notInvoiceFn(item,key)">
                                        <div>{{ t('noInvoice') }}</div>
                                    </div>
                                    <div class="mr-[15px] cursor-pointer relative  border-[1px] border-solid border-[#d3d3d3]  flex box-border px-[50px] leading-[32px] text-[14px] rounded-[var(--rounded-med)]" :class="{'!border-primary text-primary': createData.body[key].invoice && createData.body[key].invoice.header_name }" @click="invoiceFn(item)">
                                        <div>{{ t('electronicInvoice') }}</div>
                                    </div>
                               </div>
                               <div class="ml-auto flex items-center text-primary" v-if="createData.body[key].invoice">
                                    <div class="ml-[20px]">{{ createData.body[key].invoice ? createData.body[key].invoice.header_name : '' }}</div>
                                    <div class="ml-[20px]">{{ createData.body[key].invoice && createData.body[key].invoice.header_type == 1 ? t('person') : t('company') }}</div>
                                    <div class="ml-[20px] cursor-pointer" @click="editInvoice(createData.body[key].invoice)">{{ t('update') }}</div>
                               </div>
                            </div>
                        </div>
                        <div class="mb-[30px] flex">
                            <div class="text-[16px] pt-[5px] text-[#666] w-[10%]">{{ t('remark') }}</div>
                            <div class="flex items-center flex-1">
                                <el-input v-model="createData.body[key].member_remark" class="h-[120px]" type="textarea" :placeholder="t('remarkPlaceholder')" :input-style="{height: '120px',borderRadius:'8px'}" resize="none" maxlength="50" />
                            </div>
                        </div>
                        <div class="flex items-center justify-end text-[14px]">
                            <span>共{{item.basic.total_num}}{{ t('unit') }}</span>
                            <div class="ml-[10px]">
                                <span>{{ t('subTotal') }}</span>
                                <span class="price-font">￥{{ parseFloat(item.basic.order_money + item.basic.mall_discount_money).toFixed(2) }}</span>
                            </div>
                        </div>
                    </div> 
                </div>
            </div>
            <!--平台优惠劵  -->
            <div class="bg-[#fff] px-[30px] py-[30px] mb-[20px] rounded-[var(--rounded-big)]" v-if="platformCouponData && platformCouponData.length">
                <div class="flex items-center justify-between">
                    <div class="text-[16px] text-[#666] w-[64px] flex-shrink-0">{{ t('platformCoupon') }}</div>
                    <div class="text-[16px] text-[333] cursor-pointer" @click="platformCouponRef.open(createData.mall_discount.platform_coupon_id)">
                        <div v-if="orderData.discount && orderData.discount.platform_coupon">
                             <span class="price-font"> -￥{{ orderData.discount.platform_coupon.money }}</span>
                             <span class="iconfont icon-xiaV6xx !text-[20px]"></span>
                        </div>
                        <div class="text-[13px] text-gray-subtitle" v-else>
                            <span>请选择平台优惠券</span>
                            <span class="iconfont icon-xiaV6xx !text-[20px]"></span>
                        </div>
                    </div>
                </div>
            </div>
            <div class="bg-[#fff] px-[30px] pt-[30px] pb-[20px] rounded-[var(--rounded-big)]">
                <div class="bg-[#f7f7f7] p-[20px] rounded-[var(--rounded-big)]">
                    <div class="flex justify-between items-center mb-[15px] text-[14px]">
                        <div class="text-[#666]">{{ t('goodsMoney') }}</div>
                        <div class="text-[#333] price-font">¥{{parseFloat(orderData.basic.goods_money).toFixed(2) }}</div>
                    </div>
                    <div class="flex justify-between items-center mb-[15px] text-[14px]" v-if="orderData.basic.shop_discount_money">
                        <div class="text-[#666]">{{ t('discountMoney') }}</div>
                        <div class="text-[#333] price-font">-¥{{parseFloat(orderData.basic.shop_discount_money).toFixed(2) }}</div>
                    </div>
                    <div class="flex justify-between items-center mb-[15px] text-[14px]" v-if="orderData.basic.mall_discount_money">
                        <div class="text-[#666]">{{ t('mallDiscountMoney') }}</div>
                        <div class="text-[#333] price-font">-¥{{parseFloat(orderData.basic.mall_discount_money).toFixed(2) }}</div>
                    </div>
                    <div class="flex justify-between items-center mb-[15px] text-[14px]" v-if="orderData.basic.delivery_money">
                        <div class="text-[#666]">{{ t('deliveryMoney') }}</div>
                        <div class="text-[#333] price-font">+¥{{ parseFloat(orderData.basic.delivery_money).toFixed(2) }}</div>
                    </div>
                    <div class="flex justify-between items-center">
                        <div class="text-[14px] text-[#666]">{{ t('orderMoney') }}</div>
                        <div class="text-[var(--el-price)] ">
                            <span class="text-[14px] price-font">¥</span>
                            <span class="font-600 text-[22px] price-font">{{ parseFloat(orderData.basic.order_money).toFixed(2) }}</span>
                        </div>
                    </div>
                </div>
                <div class="mt-[30px]  flex justify-end items-center">
                    <el-button type="primary"  class="!w-[120px] !h-[44px] !text-[16px] !rounded-[var(--rounded-xl)] oppoSans-M" :loading="createLoading"  @click="create">{{ t('submitOrder') }}</el-button>
                </div>
            </div>
        </div>
        <!-- 地址 -->
        <add-address ref="addAddressRef" @complete="getAddressListFn" @select="handleSelect" />
        <!-- 发票 -->
        <invoice ref="invoiceRef" @confirm="confirmInvoice" @cancel="cancelInvoice" />
        <!-- 店铺优惠劵 -->
         <select-coupon :order-key="createData.body" ref="couponRef" @confirm="confirmSelectCoupon"  v-if="storeCoupon" />
         <!-- 平台优惠劵 -->
         <platform-coupon :order-key="createData.body" ref="platformCouponRef" @confirm="confirmPlatformCoupon"/>
    </div>
    
</template>

<script setup lang="ts">
import { ref, reactive,computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import storage from '@/utils/storage'
import { getAddressList } from '@/addon/mall/api/address'
import { orderCreateCalculate, orderCoupon, orderCreate, orderPlatformCoupon } from '@/addon/mall/api/order'
import AddAddress from '@/addon/mall/pages/order/components/edit-address.vue'
import invoice from '@/addon/mall/pages/order/components/invoice.vue'
import selectCoupon from '@/addon/mall/pages/order/components/select-coupon.vue'
import platformCoupon from '@/addon/mall/pages/order/components/platform-coupon.vue'
import useCartStore from '@/addon/mall/stores/cart'
import diyForm from '@/addon/components/diy-form/index.vue'
const router = useRouter()
const createData = ref({
    body:{},
    order_key:'',
    delivery:{
		take_address_id:'',
        taker_mobile: ''
	},
    mall_discount: {
        platform_coupon_id:''
    },
    form_data: {}
})
const createLoading = ref(false)
const couponRef = ref()
const platformCouponRef = ref()
const orderData = ref({})
const diyFormRef: any = ref(null)
const diyFormGoodsRef: any = ref(null)
storage.get('orderCreateData') && Object.assign(createData.value, storage.get('orderCreateData'))


// 获取全部地址
const addressList = ref([])
const getAddressListFn = async () =>{
    addressList.value = await( await getAddressList({})).data
}
getAddressListFn()
/**
 * 订单计算
 */
// 定义一个变量请求店铺优惠劵
let storeCoupon = ref(false)
 const calculate = (callback: any = null) => {
    createLoading.value = true
    orderCreateCalculate(createData.value).then((res) => {
        orderData.value = res.data
        createData.value.order_key = res.data.order_key
        Object.values(orderData.value.order_list).forEach(item =>{
            if(createData.value.body[item.site_id] != undefined){
                createData.value.body[item.site_id].order_key = item.order_key
                createData.value.body[item.site_id].member_remark = ''
                createData.value.body[item.site_id].site_id= item.site_id 
            } 
        })
        storeCoupon.value = true
        if(addressList.value.length){
            const data = addressList.value.filter(item =>{
                return item.id == orderData.value.delivery.take_address.id
            })
 
            const curIndex = addressList.value.findIndex(item =>{
                return item.id == orderData.value.delivery.take_address.id
            })
            if(data.length && curIndex != -1){
                addressList.value.splice(curIndex,1)
                addressList.value.unshift(data[0])
            }
            
        }
        callback && callback()
        createLoading.value = false
    }).catch(()=>{
        createLoading.value = false
    })
}
calculate()

// 选择优惠劵
const couponData = computed(() => {
    return couponRef.value?.couponData || {}
})

const confirmSelectCoupon = async () =>{
    for(let i in couponData.value){
        if(couponData.value[i].coupon){
            createData.value.body[i].discount = {}
            createData.value.body[i].discount.coupon_id = couponData.value[i].coupon.id
        }else{
            createData.value.body[i].discount = {}
            createData.value.body[i].discount.coupon_id = ''
        }
    }
    createData.value.mall_discount.platform_coupon_id = 0
    await calculate((data:any) =>{
        // 平台优惠劵
        platformCouponRef.value?.orderPlatformCouponFn()
    })
}
// 选择平台优惠劵
const platformCouponData = computed(() => {
    return platformCouponRef.value?.couponList || []
})

const confirmPlatformCoupon = (data:any) =>{
    createData.value.mall_discount.platform_coupon_id = data ? data.id : 0
    calculate()
}


// 新增地址后选中新增地址
const handleSelect = (data) =>{
    selectAddressFn(data)
}

// 选择地址
const selectAddressFn = (data)=>{
    createData.value.delivery.take_address_id = data.id
    createData.value.order_key = ''
    for(let i in createData.value.body){
        createData.value.body[i].order_key = ''
    }
    calculate()
}



// 添加地址
const addAddressRef = ref(null)
const addAddressFn = ()=>{
    addAddressRef.value.setFormData()
    addAddressRef.value.dialogAddressVisible = true
}
// 展示更多地址
const activeAddress = ref(true)
const showMoreAddress = () =>{
    nextTick(()=>{
        activeAddress.value = !activeAddress.value
        const addressListDom = document.querySelector('#address-list')
        addressListDom.style.height = 'auto'
    })
}
// 隐藏更多地址
const hiddenMoreAddress = () =>{
    nextTick(()=>{
        activeAddress.value = !activeAddress.value
        const addressListDom = document.querySelector('#address-list')
        addressListDom.style.height = '106px'
    })
}
/**
 * 校验选择地址
 */
 const verify = () => {
    if (!Object.values(orderData.value.delivery.take_address).length && orderData.value.basic.has_goods_types[0] != 'virtual') {
        ElMessage.error('请选择收货地址')
        return false
    }
    if (orderData.value?.basic?.has_goods_types?.length == 1 && orderData.value?.basic?.has_goods_types.includes('virtual') && orderData.value?.basic?.virtual_verify_mobile == 1){
        if (createData.value.delivery.taker_mobile == '') {
            ElMessage.error('请填写手机号')
            return false
        } else if (!/^1[3-9]\d{9}$/.test(createData.value.delivery.taker_mobile)) {
            ElMessage.error('手机号格式不正确，请重新输入')
            return false
        }
    }
    return true
}


/**
 * 订单创建
 */
const create = () => {
    if (!verify() || createLoading.value) return
    if (diyFormGoodsRef.value) {
        let pass = true;
        for (let i = 0; i < diyFormGoodsRef.value.length; i++) {
            if (!diyFormGoodsRef.value[i].verify()) {
                pass = false;
                break;
            }
        }
        if (!pass) return;
    }
    if (diyFormRef.value) {
        let pass = true;
        for (let i = 0; i < diyFormRef.value.length; i++) {
            if (!diyFormRef.value[i].verify()) {
                pass = false;
                break;
            }
        }
        if (!pass) return;
    }
    
    createLoading.value = true

    createData.value.form_data = {}

    let diyFormGoodsData = {}
    if(diyFormGoodsRef.value){
        diyFormGoodsRef.value.forEach((formGoodsItem: any, formGoodsIndex: any) => {
            diyFormGoodsData[formGoodsItem.getData().relate_id] = formGoodsItem.getData()
        })
    }

    let diyFormOrderData = {}
    if(diyFormRef.value){
        diyFormRef.value.forEach((formItem: any, formIndex: any) => {
            diyFormOrderData[formItem.getData().relate_id] = formItem.getData()
        })
    }

    Object.values(orderData.value.order_list).forEach((item: any, index:any) => {
        createData.value.form_data[item.site_id] = {}
        if(diyFormOrderData[item.site_id]) createData.value.form_data[item.site_id].order = diyFormOrderData[item.site_id]
        createData.value.form_data[item.site_id].goods = {}
        Object.values(item.goods_data).forEach((subItem: any, subIndex: any) => {
            if(diyFormGoodsData[subItem.sku_id]) createData.value.form_data[item.site_id].goods[subItem.sku_id] = diyFormGoodsData[subItem.sku_id]
        })
    })

    orderCreate(createData.value)
    .then(({ data }) => {
        if (orderData.value.basic.order_money == 0) {
            router.push({ path: '/order/list' })
        } else {
            // 购物车数量
            setTimeout(() => {
                const cartStore = useCartStore();
                cartStore.getList()
                router.replace({ path: '/pay/pay', query: { trade_type: data.trade_type, trade_id: data.trade_id } })
            },1000)
        }
    }).catch(() => {
        createLoading.value = false
    })
}

// 填写发票
const invoiceRef = ref(null)
// 开具发票
const invoiceFn = (data) =>{
    invoiceRef.value.setFormData(data)
    invoiceRef.value.dialogInvoiceVisible = true
}
// 不开具发票
const notInvoiceFn = (data,key) =>{
   delete createData.value.body[key].invoice 
}
// 修改发票
const editInvoice = (data) =>{
    invoiceRef.value.open(data)
    invoiceRef.value.dialogInvoiceVisible = true
}
const confirmInvoice = (invoice) => {
    createData.value.body[invoice.site_id].invoice = {}
    Object.assign(createData.value.body[invoice.site_id].invoice, invoice.invoice)
}
const cancelInvoice = (site_id) =>{
    if(!createData.value.body[site_id].invoice){
        delete createData.value.body[site_id].invoice
    }
}
</script>

<style lang="scss" scoped>
/* 多行超出隐藏 */
.multi-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 4;
    -webkit-box-orient: vertical;
}
.line2-hidden {
    word-break: break-all;
    text-overflow: ellipsis;
    overflow: hidden;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
}
:deep(.coupon){
    background-color:#f5f5f5!important;
}
:deep(.coupon  .el-dialog__header){
    padding: 30px 0 36px!important;
}
:deep(.coupon  .el-dialog__body){
    padding: 0 25px 30px!important;
    max-height: 60vh!important;
    overflow-y: auto;
}
:deep(.coupon  .el-dialog__body::-webkit-scrollbar){
    display: none;
}
</style>