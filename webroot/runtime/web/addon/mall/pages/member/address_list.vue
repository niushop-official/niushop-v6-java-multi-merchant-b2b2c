<template>
    <div class="ml-[20px] min-h-[70vh] px-[20px] py-[30px] w-[1000px] bg-[#fff] rounded-[var(--rounded-big)]">
        <div class="h-full" v-loading="loading">
            <div>
                <div class="mb-[37px] flex items-center justify-between"> 
                    <div class="text-[18px] text-[#333]">{{ t('addressList') }}</div>
                    <div class="w-[86px] h-[30px] leading-[30px] text-[14px] bg-primary text-[#fff] rounded-[15px] text-center cursor-pointer" @click="addAddressFn">{{t('add')}}</div>
                </div>
                <div class="h-[82%]" >
                    <div v-if="addressList.length && !loading">
                        <div class="p-[20px] bg-[#fafafa] rounded-[16px] w-[960px] h-[106px] mb-[20px] flex items-center justify-between"  v-for="(item,index) in addressList" :key="index">
                            <div class="text-[14px] text-[#666]">
                                <div class="mb-[10px]">
                                    <span class="w-[70px] inline-block mr-[6px]">收货人：</span>
                                    <span>{{item.name}}</span>
                                </div>
                                <div class="mb-[10px]">
                                    <span class="w-[70px] inline-block mr-[6px]">联系电话：</span>
                                    <span>{{item.mobile}}</span>
                                </div>
                                <div class="w-[815px] truncate leading-[20px]">
                                    <span class="w-[70px] inline-block mr-[6px]">收货地址：</span>
                                    <span>{{item.full_address}}</span>
                                </div>
                            </div>
                            <div>
                                <div class="mb-[34px] text-right">
                                    <span class="iconfont icon-xiugaiV6xx mr-[14px] cursor-pointer" @click="editAddressFn(item.id)"></span>
                                    <span class="iconfont icon-shanchu-yuangaizhiV6xx cursor-pointer" @click="deleteAddressFn(item.id)"></span>
                                </div>
                                <div class="text-[14px] text-[#666]" v-if="item.is_default == 1">默认地址</div>
                            </div>
                        </div>
                    </div>
                    <div v-if="!addressList.length && !loading">
                        <el-empty description="暂无数据" :image-size="200" :image="img('static/resource/images/system/empty.png')"/>
                    </div>
                </div>
            </div>
        </div>
        <add-address ref="addAddressRef" @complete="getAddressListFn"/>
    </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { getAddressList,deleteAddress, editAddress } from '@/addon/mall/api/address'
import AddAddress from '@/addon/mall/pages/order/components/edit-address.vue'

const addressList = ref([])
const loading = ref(false)
const getAddressListFn = ()=>{
    loading.value = true
    getAddressList({}).then(res=>{
        addressList.value = res.data
        loading.value = false
    }).catch(()=>{
        loading.value = false
    })
}
getAddressListFn()

// 添加地址
const addAddressRef = ref(null)
const addAddressFn = ()=>{
    addAddressRef.value.setFormData()
    addAddressRef.value.dialogAddressVisible = true
}
// 编辑地址
const editAddressFn = (id:number) =>{
    addAddressRef.value.setFormData(id)
    addAddressRef.value.dialogAddressVisible = true
}
// 删除地址
const deleteAddressFn = (id:number) =>{
    ElMessageBox.confirm('确定要删除该数据吗？', '提示',
        {
            confirmButtonText: t('confirm'),
            cancelButtonText: t('cancel'),
            confirmButtonClass:'!bg-[var(--el-color-primary)] !border-[var(--el-color-primary)]',
            type: 'warning'
        }
    ).then(() => {
        deleteAddress(id).then(res => {
            getAddressListFn()
        })
    })
}


</script>

<style lang="scss" scoped>
:deep(.el-radio){
    height: 0px !important;
}
:deep(.el-radio.el-radio--large .el-radio__inner){
    width: 18px;
    height: 18px;
}
</style>