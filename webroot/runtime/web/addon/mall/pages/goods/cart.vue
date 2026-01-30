<template>
    <div class="main-container min-h-[500px]"  v-loading="loading">
        <div v-if="!loading">
            <div class="min-h-[500px] flex flex-col items-center justify-center bg-[#fff] mt-[20px]" v-if="!userInfo">
                <el-empty :description="t('notLogin')" :image-size="200" :image="img('static/resource/images/system/login.png')"/>
                <el-button class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)] !text-[#fff]" @click="toLogin">{{ t('goLogin') }}</el-button>
            </div>
            <div class="min-h-[500px] flex flex-col items-center justify-center bg-[#fff] mt-[20px]" v-else-if="!cartList.length">
                <el-empty :description="t('emptyCart')" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                <el-button class="!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)] !text-[#fff]" @click="router.push('/')">{{ t('goStroll') }}</el-button>
            </div>
            <div v-else>
                <div class="mt-[20px] cart">
                    <div class="pl-[30px] py-[16px] bg-[#fff]  h-[50px] mb-[20px] flex items-center rounded-[var(--rounded-mid)]">
                        <div class="w-[285px]">
                            <div class="flex items-center">
                                <el-checkbox v-model="isSelectAll"  @change="allChange" />
                                <span class="ml-[10px] text-[14px] text-[#666]">{{t('isAll')}}</span>
                            </div>
                        </div>
                        <div class="w-[217px] text-[14px] text-[#666]">{{ t('shopInfo') }}</div>
                        <div class="w-[178px] text-[14px] text-[#666] text-center">{{ t('price') }}</div>
                        <div class="w-[150px] text-[14px] text-[#666] text-center">{{ t('num') }}</div>
                        <div class="w-[230px] text-[14px] text-[#666] text-center">{{ t('amount') }}</div>
                        <div class="w-[76px] text-[14px] text-[#666] text-center">{{ t('operation') }}</div>
                    </div>
                    <div>
                        <div class="p-[30px] bg-[#fff] overflow-hidden  mb-[20px] box-border rounded-[var(--rounded-big)]" v-for="(item, index) in cartList" :key="index">
                            <div class="flex items-center mb-[30px]">
                                <el-checkbox v-model="item.checked" :disabled="item.disabled" size="large"  @change="isClickSite(item)" />
                                <span class="text-[14px] ml-[24px] text-[#333]">{{ item.site_name}}</span>
                            </div>
                            <div>
                                <div class="flex items-center" :class="{'mt-[30px]': subIndex}" v-for="(subItem,subIndex) in item.goods_list" :key="subIndex">
                                    <div>
                                        <el-checkbox v-model="subItem.checked"  :disabled="subItem.disabled"  @change="changeItem(item)" />
                                    </div>
                                    <div class="flex ml-[20px] cursor-pointer" @click="router.push(`/goods/detail?id=${subItem.goods.goods_id}`)">
                                        <div class="flex items-center w-[100px] mr-[20px] flex-shrink-0 relative">
                                            <el-image style="width: 100px; height: 100px" class="rounded-[var(--rounded-mid)]"  :src="img(subItem.goodsSku.sku_image_thumb_mid)" fit="cover">
                                                <template #error>
                                                    <img src="@/assets/images/goods_default.png" class="w-[100px] h-[100px] rounded-[var(--rounded-mid)]">
                                                </template>
                                            </el-image>
                                            <div class="absolute top-0 left-0 right-0 bottom-0 bg-[rgba(0,0,0,0.5)] text-[#fff] text-[14px] flex items-center justify-center" v-if="subItem.disabled">
                                               {{t('lostEfficacy')}}
                                            </div>
                                        </div>
                                        <div class="w-[349px]  flex flex-col justify-between">
                                            <div>
                                                <div class="multi-hidden text-[14px] leading-[20px] h-[40px]">{{subItem.goods.goods_name}}</div>
                                                <div class="text-[12px] mt-[10px] text-[#999]" v-if="subItem.goodsSku && subItem.goodsSku.sku_spec_format">{{subItem.goodsSku.sku_spec_format}}</div>
                                            </div>
                                            <img  v-if="diyGoods.priceType(subItem) == 'member_price'" class="h-[14px] mt-auto w-[24px]" src="@/assets/images/addon/VIP.png" />
                                            <img   v-if="diyGoods.priceType(subItem) == 'discount_price'" class="h-[12px] mt-auto w-[36px]" src="@/assets/images/addon/discount.png" />
                                        </div>
                                    </div>
                                    <div class="w-[180px] flex items-center justify-center text-[14px]">
                                        <div class="price-font">￥{{ diyGoods.goodsPrice(subItem).toFixed(2) }}</div>
                                    </div>
                                    <div class="w-[150px] flex items-center justify-center">
                                        <el-input-number v-model="subItem.num" :step="1" :min="numLimit(subItem).min" step-strictly :max="numLimit(subItem).max"   :disabled="subItem.disabled" @change="numChange(subItem)"  />	
                                    </div>
                                    <div class="w-[230px] flex items-center justify-center text-[var(--el-price)]">
                                        <div class="flex items-baseline">
                                            <span class="text-[12px] price-font">￥</span>
                                            <span class="text-[18px] price-font">{{parseFloat(Number(diyGoods.goodsPrice(subItem)) * Number(subItem.num)).toFixed(2)}}</span>
                                        </div>
                                    </div>
                                    <div class="w-[76px] text-center">
                                        <span class="iconfont icon-shanchu-yuangaizhiV6xx cursor-pointer !text-[20px]" @click="deleteCart(subItem.id,index)"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="h-[66px] mt-[20px] mb-[30px] bg-[#fff] flex justify-between items-center px-[30px] box-shadow rounded-[var(--rounded-mid)]">
                        <div class="flex items-center text-[#282828]">
                            <div class="flex items-center">
                                <el-checkbox v-model="isSelectAll"   @change="allChange" />
                                <span class="text-[14px] ml-[10px]">{{t('isAll')}}</span>
                            </div>
                            <div class="text-[14px] ml-[20px] cursor-pointer"  @click="deleteCartFn">{{ t('deleteCheck') }}</div>
                        </div>
                        <div class="flex items-center">
                            <div class="flex items-baseline">
                                <span class="text-[14px]">{{t('total')}}</span>
                                <div class="text-[var(--el-color-primary)]">
                                    <span class="text-[14px] price-font">￥</span>
                                    <span class="text-[22px] font-600 price-font">{{total}}</span>
                                </div>
                            </div>
                            <div class="w-[136px] h-[44px] bg-primary text-center leading-[44px] text-[#fff] text-[16px] ml-[30px] rounded-[var(--rounded-xl)] cursor-pointer" @click="settlement">{{ t('toPay') }} ({{checkedNum}})</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive,watch,computed } from  'vue'
import { useRouter } from 'vue-router'
import { getCartGoodsList } from '@/addon/mall/api/cart'
import { debounce, getToken } from '@/utils/common'
import useMemberStore from '@/stores/member'
import useCartStore from '@/addon/mall/stores/cart'
import { useGoods } from '@/addon/mall/hooks/useGoods'

const router = useRouter()
const loading = ref(true)

const diyGoods = useGoods();

const optionLoading = ref(false)
// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

const cartStore = useCartStore()

// 未登录
const toLogin = () =>{
    memberStore.logOpen()
}

// 购物车列表
const cartList = ref<object[]>([])
const getCartGoodsListFn = () => {
    getCartGoodsList({}).then((res: any) => {
        cartList.value  =  Object.values(res.data).map((item: any) =>{
            item.checked = false
            item.goods_list.map((subItem: any) =>{
                if (subItem.goods.status == 1 && subItem.goods.delete_time == 0) {
                    if (subItem.goodsSku.stock) {
                        if (subItem.num > subItem.goodsSku.stock) subItem.num = subItem.goodsSku.stock;
                        subItem.checked = false
                    }else{
                        subItem.disabled = true
                    }   
                    
                }else{
                    subItem.disabled = true
                }
                return subItem
            })
            item.disabled = item.goods_list.every((subItem: any) => subItem.disabled)
            return item
        })
        isSelectAll.value = true
        allChange()
        loading.value = false
    }).catch(()=>{
        loading.value = false
    })
}
getCartGoodsListFn()  
// 删除购物车
const deleteCart = (id: any) => {
    if (optionLoading.value) return
	optionLoading.value = true
    cartStore.delete(id,()=>{
        getCartGoodsListFn()
        optionLoading.value = false
    })
}

// 选择数量
const checkedNum = computed(() => {
	let num = 0
	cartList.value.forEach((item: any) => {
		item.goods_list.forEach((subItem: any) =>{
           if(subItem.checked){
             num += subItem.num
           }
        })
	})
	return num
})

// 计算总价
const total = ref('0.00')
watch(() => cartList.value, () => {
	let value = 0
	cartList.value.forEach((item: any) => {
        item.goods_list.forEach((subItem: any) => {
            console.log(subItem)
            if (subItem.checked && subItem.goodsSku){
                let price: any = 0;
                price = subItem.goodsSku.show_price
                value += parseFloat(price) * subItem.num
            }
        })
	})
	total.value = value.toFixed(2)
}, { deep: true })

// 限购
const numLimit = (data: any) => {
    let obj = {
		min: 1,
		max: data.goodsSku.stock || data.num
	};

    // 限购 - 是否开启限购
	if(data.goods.is_limit){
		if(data.goods.max_buy){
			let max_buy = 0;
			max_buy = data.goods.max_buy;

			if(max_buy > data.goods.stock){
				obj.max = data.goods.stock < 0 ? data.goods.min_buy : data.goods.stock;
			}else if(max_buy <= data.goods.stock){
				obj.max = max_buy;
			}
		}
	}

    // 起售
	if(data.goods.min_buy > 0){
		obj.min = data.goods.min_buy;
	}
	return obj;
}

// 数量增加
const numChange = debounce((data)=>{
    cartStore.increase({
		id: data.id,
		goods_id: data.goods_id,
		sku_id: data.sku_id,
		stock: data.goodsSku.stock,
		sale_price: data.goodsSku.sale_price,
		num: data.num,
        site_id: data.site_id
	}, 0);
})

 //判断是否全选
const isSelectAll = ref(false)
const isallclick = ()=>{
    const isActive = cartList.value.every((item) => {
        return item.checked
    })
    if (isActive) { isSelectAll.value = true } else { isSelectAll.value= false }
}
// 选择单个商品
const changeItem = (data: any) =>{
    let arr =[];
    data.goods_list.forEach((item: any) => {
        if(item.checked != undefined){
            arr.push(item)
        }
    })
    const isActive = arr.every((item) => {
        return item.checked
    })
    if (isActive) { data.checked = true } else { data.checked = false }
    isallclick()
}
// 选择店铺
const isClickSite = (data) =>{
    if (data.checked) {
        data.goods_list.forEach(item => {
            if(!item.disabled){
                item.checked = true
            }
        })
    } else {
        data.goods_list.forEach(item => {
            if(!item.disabled){
                item.checked = false
            }
        });
    }
    isallclick()
}
// 全选
 const allChange = () =>{
    if (isSelectAll.value) {
        cartList.value.forEach(item => {
            item.checked = item.goods_list.some((item) => {
                return !item.disabled
            })
            item.goods_list.forEach(subItem => {
                if(!subItem.disabled){
                    subItem.checked = true
                }
            })
        })
    } else {           
        cartList.value.forEach(item => {
            item.checked = false
            item.goods_list.forEach(subItem => {
                if(!subItem.disabled){
                    subItem.checked = false
                }
            })
        })
    }

}

// 结算
const settlement = () =>{
    if (!checkedNum.value) {
		ElMessage.error('还没有选择商品')
		return
	}
    let body = {}
	cartList.value.forEach((item: any)=> {
        body[item.site_id] = {
            order_type: 'mall'
        }
	})
    for(let key in body){
        cartList.value.forEach((item: any) => {
            if (item.site_id == key) {
                body[key].cart_ids = []
                item.goods_list.forEach((subItem: any)=> {
                    if (subItem.checked) {
                        body[key].cart_ids.push(subItem.id)
                    }
                })
            }
        })
        if (!body[key].cart_ids.length){
            delete body[key]
        }
    }
    storage.set({ key: 'orderCreateData', data: {body: body}})
    router.push('/order/payment')
}
// 全选删除
const deleteCartFn = () => {
	if (!checkedNum.value) {
		ElMessage.error('还没有选择商品')
		return
	}
	if (optionLoading.value) return
	optionLoading.value = true

	const ids = []
    if(isSelectAll.value){
        cartList.value.forEach(item => {
            item.goods_list.forEach(subItem => {
                ids.push(subItem.id)
            })
        })
    }else{
        cartList.value.forEach(item => {
            item.goods_list.forEach(subItem => {
                if (subItem.checked) {
                    ids.push(subItem.id)
                }
            })
        })
    }

	cartStore.delete(ids, () => {
		getCartGoodsListFn()
		optionLoading.value = false
	})
}

</script>

<style lang="scss" scoped>
:deep(.cart .el-checkbox){
    height: 14px;
}
:deep(.cart .el-checkbox__inner){
    border-radius:7px;
}
.box-shadow{
    box-shadow: 0 -2px 6px #0000000d;
}
</style>