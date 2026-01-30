<template>
    <view class="bg-[#fff] min-h-screen overflow-hidden order-list" :style="themeColor()">
        <view class="sidebar-margin py-[20rpx]">
            <u-form labelPosition="left" :model="formData" errorType='toast' :rules="rules" ref="formRef">
                <view class="address-wrap">
                    <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                        <u-form-item label="联系人" prop="contact_name" labelWidth="140rpx">
                            <u-input fontSize="28rpx" v-model.trim="formData.contact_name" border="none" clearable maxlength="25" placeholderStyle="color: #888" placeholder="请输入联系人" />
                        </u-form-item>
                    </view>
                    <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                        <u-form-item label="联系方式" prop="mobile" labelWidth="140rpx">
                            <u-input fontSize="28rpx" v-model.trim="formData.mobile" border="none" clearable maxlength="25" placeholderStyle="color: #888" placeholder="请输入联系方式" />
                        </u-form-item>
                    </view>
                    <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                        <u-form-item label="地址" prop="area" labelWidth="140rpx">
                            <view class="flex justify-between items-center flex-1 h-[52rpx]" @click="chooseLocation">
                                <view class="text-[28rpx] text-[#303133] leading-[1.4]" v-if="formData.area">{{ formData.area }}</view>
                                <view class="text-[#888] text-[28rpx]" v-else>请选择地址</view>
                                <view class="flex items-center">
                                    <text class="nc-iconfont nc-icon-dizhiguanliV6xx text-[32rpx] mr-[4rpx] text-[var(--primary-color)]"></text>
                                    <text class="text-[24rpx] whitespace-nowrap text-[var(--primary-color)]">定位</text>
                                </view>
                            </view>
                        </u-form-item>
                    </view>
                    <view class="h-[88rpx] w-full px-[20rpx] rounded-[16rpx] box-border bg-[#F6F6F6] mb-[30rpx]">
                        <u-form-item label="详细地址" prop="address" labelWidth="140rpx">
                            <u-input fontSize="28rpx" v-model.trim="formData.address" border="none" clearable maxlength="120" placeholder="请输入详细地址" placeholderStyle="color: #888" />
                        </u-form-item>
                    </view>
                    <view class="flex items-center px-[20rpx] mb-[30rpx]">
                        <view class="flex items-center  mr-[80rpx]" @click="formData.is_delivery_address = !formData.is_delivery_address">
                            <text class="iconfont text-[30rpx] mr-[10rpx] w-[30rpx] h-[30rpx] rounded-[16rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formData.is_delivery_address,'border-solid border-[2rpx] border-[#ddd]':!formData.is_delivery_address}"></text>
                            <text>发货地址</text>
                        </view>
                        <view class="flex items-center" @click="formData.is_default_delivery = !formData.is_default_delivery" v-if="formData.is_delivery_address">
                            <text class="iconfont text-[30rpx] mr-[10rpx] w-[30rpx] h-[30rpx] rounded-[16rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formData.is_default_delivery,'border-solid border-[2rpx] border-[#ddd]':!formData.is_default_delivery}"></text>
                            <text>默认发货地址</text>
                        </view>
                    </view>
                    <view class="flex items-center px-[20rpx]">
                        <view class="flex items-center  mr-[80rpx]" @click="formData.is_refund_address = !formData.is_refund_address">
                            <text class="iconfont text-[30rpx] mr-[10rpx] w-[30rpx] h-[30rpx] rounded-[16rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formData.is_refund_address,'border-solid border-[2rpx] border-[#ddd]':!formData.is_refund_address}"></text>
                            <text>收货地址</text>
                        </view>
                        <view class="flex items-center" @click="formData.is_default_refund = !formData.is_default_refund" v-if="formData.is_refund_address">
                            <text class="iconfont text-[30rpx] mr-[10rpx] w-[30rpx] h-[30rpx] rounded-[16rpx] overflow-hidden flex-shrink-0 box-border" :class="{ 'iconxuanze1 text-primary': formData.is_default_refund,'border-solid border-[2rpx] border-[#ddd]':!formData.is_default_refund}"></text>
                            <text>默认收货地址</text>
                        </view>
                    </view>
                </view>
            </u-form>
        </view>
        <view class="w-full footer">
            <view class="py-[var(--top-m)] px-[var(--sidebar-m)] footer w-full fixed bottom-0 left-0 right-0 box-border">
                <button hover-class="none" class="primary-btn-bg !text-[#fff] h-[80rpx] leading-[80rpx]  text-[26rpx] font-500" :loading="operateLoading"  @click="save">确定</button>
            </view>
        </view>
        <area-select ref="areaRef" @complete="areaSelectComplete" :area-id="formData.district_id || formData.city_id" />
        <!-- #ifdef MP-WEIXIN -->
        <!-- 小程序隐私协议 -->
        <wx-privacy-popup ref="wxPrivacyPopupRef"></wx-privacy-popup>
        <!-- #endif -->
    </view>
</template>

<script setup lang="ts">
import { ref, reactive, nextTick, computed } from 'vue'
import manifestJson from '@/manifest.json';
import { onLoad } from '@dcloudio/uni-app'
import { getAddressByLatlng } from '@/app/api/system';
import { redirect } from '@/utils/common'
import { getShopAddressInfo, addShopAddress, editShopAddress } from '@/app/api/delivery_site';


const initialFormData = {
    id: 0,
    contact_name: '',
    mobile: '',
    province_id: 0,
    city_id: 0,
    district_id: 0,
    address: '',
    full_address: '',
    area: '',
    lat: '',
    lng: '',
    is_delivery_address: 0,
    is_refund_address: 0,
    is_default_delivery: 0,
    is_default_refund: 0
}

const formData: Record<string, any> = reactive({ ...initialFormData })
const id = ref('')
const formRef: any = ref(null)

const wxPrivacyPopupRef: any = ref(null)

onLoad((data: any) => {
    id.value = data.id || 0;
    if (data.id && !data.name) {
        getShopAddressInfoFn(id.value);
    } else if (data.name) {
        if (uni.getStorageSync('siteAddressEditInfo')) {
            Object.assign(formData, uni.getStorageSync('siteAddressEditInfo'))
        }
        getAddress(data.latng);
        const tempArr = getQueryVariable('latng').split(',');
        formData.lat = tempArr[0];
        formData.lng = tempArr[1];
    }
    // #ifdef MP
    nextTick(() => {
        if (wxPrivacyPopupRef.value) wxPrivacyPopupRef.value.proactive();
    })
    // #endif
})


const  getShopAddressInfoFn = (id: any) => {
    getShopAddressInfo(id).then((res: any) => {
		Object.keys(formData).forEach((key: string) => {
            if (res.data[key] != undefined) formData[key] = res.data[key]
        })
	}).catch(() => {

	})
}

const areaSelectComplete = (event: any) => {
    formData.province_id = event.province?.id || 0
    formData.city_id = event.city?.id || 0
    formData.district_id = event.district?.id || 0
    formData.area = `${ event.province?.name || '' }${ event.city?.name || '' }${ event.district?.name || '' }`
}

const rules = computed(() => {
    return {
        'contact_name': {
            type: 'string',
            required: true,
            message: '请输入联系人名称',
            trigger: ['blur', 'change'],
        },
        'mobile': [
            {
                type: 'string',
                required: true,
                message: '请输入联系方式',
                trigger: ['blur', 'change'],
            },
            {
                validator(rule: any, value: any, callback: any) {
                    let mobile = /^1[3-9]\d{9}$/;
                    if (!mobile.test(value)) {
                        callback(new Error('联系方式格式错误'))
                    } else {
                        callback()
                    }
                }
            }
        ],
        'area': {
            validator() {
                let bool = true;
                if (uni.$u.test.isEmpty(formData.area)) {
                    bool = false;
                }
                return bool
            },
            message: '请选择地址'
        },
        'address': {
            type: 'string',
            required: true,
            message: '请输入详细地址',
            trigger: ['blur', 'change']
        }
    }
})

const operateLoading = ref(false)
const save = () => {
    const save = formData.id ? editShopAddress : addShopAddress
    formRef.value.validate().then(() => {
        if (operateLoading.value) return
        operateLoading.value = true

        formData.full_address = formData.area + formData.address

        save(formData).then((res: any) => {
            operateLoading.value = false
            setTimeout(() => {
                redirect({ url: '/app/pages/site_address/list', mode: 'redirectTo' })
            }, 1000)
        }).catch(() => {
            operateLoading.value = false
        })
    })
}

// 选择地址
const chooseLocation = () => {
    // #ifdef MP
    uni.chooseLocation({
        success: (res) => {
            res.latitude && (formData.lat = res.latitude)
            res.longitude && (formData.lng = res.longitude)
            res.address && (formData.area = res.address)
            res.name && (formData.full_address = res.name)
            if (res.latitude && res.longitude) {
                let latng = res.latitude + ',' + res.longitude;
                getAddress(latng);
            }
        },
        fail: (res) => {
            // 在隐私协议中没有声明chooseLocation:fail api作用域
            if (res.errMsg && res.errno) {
                if (res.errno == 104) {
                    let msg = '用户未授权隐私权限，选择位置失败';
                    uni.showToast({ title: msg, icon: 'none' })
                } else if (res.errno == 112) {
                    let msg = '隐私协议中未声明，打开地图选择位置失败';
                    uni.showToast({ title: msg, icon: 'none' })
                } else {
                    uni.showToast({ title: res.errMsg, icon: 'none' })
                }
            }
        }
    });
    // #endif

    // #ifdef H5
    const urlencode = formData;
    uni.setStorageSync('siteAddressEditInfo', urlencode);
    let backurl = location.origin + location.pathname + '?order_id=' + formData.order_id;
    window.location.href = 'https://apis.map.qq.com/tools/locpicker?search=1&type=0&backurl=' + encodeURIComponent(backurl) + '&key=' + manifestJson.h5.sdkConfigs.maps.qqmap.key + '&referer=myapp';
    // #endif
}


//获取详细地址
const getAddress = (latlng: any) => {
    getAddressByLatlng({ latlng }).then((res: any) => {
        if (res.data) {
            formData.full_address = '';
            formData.full_address += res.data.province != undefined ? res.data.province : '';
            formData.full_address += res.data.city != undefined ? '' + res.data.city : '';
            formData.full_address += res.data.district != undefined ? '' + res.data.district : '';

            formData.area = (res.data.province + res.data.city + res.data.district) || res.data.full_address;

            formData.province_id = res.data.province_id != undefined ? res.data.province_id : 0;
            formData.city_id = res.data.city_id != undefined ? res.data.city_id : 0;
            formData.district_id = res.data.district_id != undefined ? res.data.district_id : 0;
            formData.address = res.data.community!= undefined ? res.data.community : '';
        } else {
            uni.showToast({ title: res.msg, icon: 'none' })
        }
    })

}

const getQueryVariable = (variable: any) => {
    const query = window.location.search.substring(1);
    const vars = query.split('&');
    for (let i = 0; i < vars.length; i++) {
        const pair = vars[i].split('=');
        if (pair[0] == variable) {
            return pair[1];
        }
    }
    return false;
}
</script>

<style lang="scss" scoped>
.address-wrap :deep(.u-form-item__body__left__content__label) {
    font-size: 28rpx !important;
}
.footer {
    height: calc(80rpx + var(--top-m) + var(--top-m) + constant(safe-area-inset-bottom)) !important;
    height: calc(80rpx + var(--top-m) + var(--top-m) + env(safe-area-inset-bottom)) !important;
}
</style>