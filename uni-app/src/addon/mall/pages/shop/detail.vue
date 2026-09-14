<template>
    <view  class="bg-[var(--page-bg-color)] min-h-[100vh] overflow-hidden" :style="themeColor()" v-if="Object.keys(shopDetail).length">
        <view class="px-[var(--sidebar-m)] top-mar">
            <view class="card-template mb-[var(--top-m)]">
                <view class="flex items-center justify-between mb-[20rpx]">
					<view class="flex items-center">
						<view class="w-[100rpx] h-[100rpx] overflow-hidden">
                            <image v-if="shopDetail.front_end_logo" :src="img(shopDetail.front_end_logo)" class="w-[100rpx] h-[100rpx] rounded-full"></image>
                            <image v-else :src="img('addon/mall/shop/shop_default.png')" class="w-[100rpx] h-[100rpx] rounded-full"></image>
						</view>
						<view class="ml-[20rpx]">
							<view class="flex items-center mb-[12rpx]">
								<text class="tag w-[60rpx] h-[32rpx] flex-center mr-[10rpx] text-[22rpx] text-[#802502] rounded-[6rpx]" v-if="shopDetail.is_self">自营</text>
								<text class="text-[30rpx] font-500">{{ shopDetail.site_name }}</text>
							</view>
							<view class="text-[24rpx] text-[var(--text-color-light9)]">
								{{shopDetail.follow_number}}人关注
							</view>
						</view>
					</view>
                    <view>
                        <view class="w-[120rpx] h-[50rpx] flex-center primary-btn-bg rounded-full" v-if="!isFollow" @click="collectShop(shopDetail.site_id,1)">
                            <text class="nc-iconfont nc-icon-guanzhuV6xx text-[#fff] text-[22rpx]"></text>
                            <text class="text-[22rpx] font-500 text-[#fff] ml-[6rpx]">关注</text>
                        </view>
                        <view v-else class="w-[120rpx] h-[50rpx] flex-center border-solid border-[2rpx] border-[#999] rounded-full">
                            <text class="text-[22rpx] font-500 text-[#999]" @click="collectShop(shopDetail.site_id,0)">已关注</text>
                        </view>
                    </view>
				</view>
                <view class="flex items-center justify-between">
                    <view class="flex items-center">
                        <text class="text-[24rpx] text-[var(--text-color-light9)] mr-[6rpx]">商品质量</text>
                        <text class="text-[26rpx] text-[#FD3B1F]">{{ shopDetail.quality_score }}</text>
                    </view>
                    <view class="flex items-center">
                        <text class="text-[24rpx] text-[var(--text-color-light9)] mr-[6rpx]">商家服务</text>
                        <text class="text-[26rpx] text-[#FD3B1F]">{{ shopDetail.service_score }}</text>
                    </view>
                    <view class="flex items-center">
                        <text class="text-[24rpx] text-[var(--text-color-light9)] mr-[6rpx]">发货物流</text>
                        <text class="text-[26rpx] text-[#FD3B1F]">{{ shopDetail.logistics_score }}</text>
                    </view>
                </view>
            </view>
            <view class="card-template mb-[var(--top-m)]">
                <view class="card-template-item justify-between" @click="storePic = true">
                    <view class="text-[28rpx]">店铺资质</view>
                    <view class="nc-iconfont nc-icon-gerenziliaoV6xx text-[40rpx]"></view>
                </view>
                <view class="card-template-item justify-between" @click="qrcode = true">
                    <view class="text-[28rpx]">店铺二维码</view>
                    <view class="nc-iconfont nc-icon-erweimaV6xx-1 text-[40rpx]"></view>
                </view>
                <view class="card-template-item justify-between" @click="call(shopDetail.phone)">
                    <view class="text-[28rpx]">联系客服</view>
                    <view class="nc-iconfont nc-icon-kefuV6xx-4 text-[40rpx]"></view>
                </view>
            </view>
            <view class="card-template mb-[var(--top-m)]">
                <view class="card-template-item !items-start">
                    <view class="text-[28rpx] flex-shrink-0 mr-[20rpx]">店铺简介</view>
                    <view class="text-[28rpx] text-[var(--text-color-light6)] leading-[30rpx]">{{shopDetail.desc }}</view>
                </view>
                <view class="card-template-item !items-start">
                    <view class="text-[28rpx] flex-shrink-0 mr-[20rpx]">店铺地址</view>
                    <view class="flex-1 flex items-center" @click="getAddress" v-if="shopDetail.full_address">
                        <view class="text-[28rpx] multi-hidden leading-[30rpx] text-[var(--text-color-light6)]">{{shopDetail.full_address }}</view>
                        <text class="nc-iconfont nc-icon-dizhiguanliV6xx text-[30rpx] text-primary ml-[10rpx]"></text>
                    </view>
                   <view class="flex-1 flex items-center text-[var(--text-color-light6)]" v-else>商家暂未设置地址</view>
                </view>
                <view class="card-template-item" v-if="shopDetail.business_hours">
                    <view class="text-[28rpx] flex-shrink-0 mr-[20rpx]">营业时间</view>
                    <view class="text-[28rpx] text-[var(--text-color-light6)]">{{ shopDetail.business_hours }}</view>
                </view>
            </view>
            <view class="card-template text-center text-[30rpx] text-primary font-500" @click="redirect({url:'/addon/mall/pages/goods/list',param:{site_id:shopDetail.site_id}, mode: 'navigateTo'})">
                查看全部商品
            </view>
        </view>
        <view @touchmove.prevent.stop>
            <u-popup class="popup-type" :show="qrcode" @close="qrcode = false" mode="center" round="var(--rounded-big)">
                <view class="w-[540rpx] rounded-[24rpx] min-h-[300rpx] popup-common" @touchmove.prevent.stop>
                    <view class="title">店铺二维码</view>
                    <!-- #ifdef H5 -->
                    <view class="pb-[40rpx]">
                        <view class="flex-center">
                            <image :src="wapImage" class="w-[220rpx] h-[220rpx]" mode="aspectFill"></image>
                        </view>
                    </view>
                    <!-- #endif -->
                    <!-- #ifdef MP-WEIXIN -->
                    <view class="pb-[40rpx]">
                        <view class="flex-center mb-[20rpx]">
                            <image :src="img(qrcodeImg)" class="w-[220rpx] h-[220rpx]" mode="aspectFill"></image>
                        </view>
                        <view class="text-center text-[26rpx] text-primary" @click="saveQrcode()">保存二维码</view>
                    </view>
                    <!-- #endif -->
                </view>
            </u-popup>
            <u-popup :show="show" mode="center" :round="10" :closeable="true" @close="show = false" :safe-area-inset-bottom="false">
                <view class="dialog-popup">
                    <view class="title">提示</view>
                    <view class="message">您拒绝了保存图片到相册的授权请求，无法保存图片到相册，如需正常使用，请授权之后再进行操作。</view>
                    <view class="action-wrap">
                        <view @click="closeDialog">取消</view>
                        <view>
                            <button type="default" class="authorization-btn" open-type="openSetting" @opensetting="closeDialog" hover-class="none">立即授权</button>
                        </view>
                    </view>
                </view>
            </u-popup>
        </view>

        <view @touchmove.prevent.stop>
            <u-popup class="popup-type" :show="storePic" @close="storePic = false" mode="center" zIndex="995" :overlayStyle="{zIndex: 994}" round="var(--rounded-big)">
                <view class="w-[540rpx] rounded-[24rpx] min-h-[300rpx] popup-common" @touchmove.prevent.stop>
                    <view class="title">店铺资质</view>
                    <view class="flex-center pb-[56rpx]">
                        <view class="w-[400rpx]">
                            <template v-if="shopDetail.shop.business_image.length">
                                <u-swiper :list="shopDetail.shop.business_image" :indicator="shopDetail.shop.business_image.length" :indicatorStyle="{'bottom': '50rpx'}" indicatorMode="dot" :autoplay="true"  height="400rpx" radius="0" @click="swiperClick"></u-swiper>
                            </template>
                            <view v-else class="text-center">暂无店铺资质</view>
                        </view>
                    </view>
                </view>
            </u-popup>
        </view>
        <loading-page :loading="loading"></loading-page>
    </view>
</template>
<script setup lang="ts">
import { reactive, ref, computed } from 'vue';
import { onLoad, onShow, onUnload } from '@dcloudio/uni-app';
import { img, redirect, isWeixinBrowser } from '@/utils/common'
import { getShopInfo, editShopCollect } from '@/addon/mall/api/shop';
import { getShopQrcode } from '@/app/api/shop';
import { useLogin } from '@/hooks/useLogin'
import useMemberStore from '@/stores/member'
import QRCode from 'qrcode'
import wechat from '@/utils/wechat'
import { useShare } from '@/hooks/useShare'
const { setShare } = useShare()

// #ifdef H5
if (isWeixinBrowser()) {
    wechat.init();
}
// #endif


// 会员信息
const memberStore = useMemberStore()
const userInfo = computed(() => memberStore.info)

const siteId = ref('')
const shopDetail = ref<Object>({});
const loading = ref<boolean>(true);
onLoad((option:any) => {
	siteId.value = option.site_id;
	shopInfoFn(siteId.value);
    // #ifdef MP-WEIXIN
    loadWeapp()
    // #endif
    // #ifdef H5
    loadQrcode()
    // #endif
});
onShow(() => {
    setTimeout(() => {
        let share = {
            title: '店铺详情',
            desc: '',
        }

        setShare({
            wechat: {
                ...share
            },
            weapp: {
                ...share
            }
        });
    }, 600);
})

const shopInfoFn = (siteId:any) =>{
    loading.value = true;
    getShopInfo(siteId).then((res:any) =>{
        if (res.data.business_status == 0) {
            redirect({ url: '/app/pages/site/close' })
            return false
        }
        shopDetail.value = res.data;
        shopDetail.value.rate = 5
        shopDetail.value.shop.business_image = []
        if(shopDetail.value.shop.business_license){
            shopDetail.value.shop.business_license.split(',').forEach((item:any, index:any) => {
                shopDetail.value.shop.business_image.push(img(item))
            })
        }
        isFollow.value = shopDetail.value.member_info.is_follow
        loading.value = false;
    }).catch(() =>{
        loading.value = false;
    })
}

// 关注
let isFollow = ref<any>(0);
const collectShop = (site_id:any,is_follow:any) =>{
    // 检测是否登录
	if (!userInfo.value) {
		useLogin().setLoginBack({ url: '/addon/mall/pages/shop/detail', param: { site_id: shopDetail.value.site_id } })
		return false
	}
    editShopCollect({
        site_id: site_id,
		is_follow: is_follow
    }).then(res =>{
        isFollow.value = !isFollow.value;
        if (isFollow.value) {
			uni.showToast({
				title: '关注成功',
				icon: 'none'
			});
            shopDetail.value.follow_number ++
		} else {
			uni.showToast({
				title: '取消关注',
				icon: 'none'
			});
            shopDetail.value.follow_number --
		}
    })
}
const storePic = ref(false)


const qrcode = ref(false)
const wapPreview = ref<any>('')
const wapImage = ref('') //公众号二维码
const qrcodeImg = ref('') //小程序二维码
// #ifdef H5 
const loadQrcode = () => {
    wapPreview.value = `${ import.meta.env.VITE_IMG_DOMAIN || location.origin }/wap/addon/mall/pages/index?site_id=${ siteId.value }`
    // errorCorrectionLevel：密度容错率L（低）H(高)
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}
// #endif

const loadWeapp = () => {
	getShopQrcode(siteId.value).then((res:any) =>{
		qrcodeImg.value = res.data
	})
}


const call = (data: any) => {
    if (!data) {
        uni.showToast({
            title: '商家暂未设置联系电话',
            icon: 'none'
        });
        return false
    }
    uni.makePhoneCall({
        phoneNumber: data,
        success: (res: any) => {
        },
        fail: (res: any) => {
        }
    })
}

const getAddress = () => {
    // #ifdef H5
    if (isWeixinBrowser()) {
        wechat.openLocation({
            latitude: Number(shopDetail.value.latitude),
            longitude: Number(shopDetail.value.longitude),
            name: shopDetail.value.full_address,
            address: shopDetail.value.full_address
        })
    }
    // #endif
    // #ifdef MP
	uni.openLocation({
		latitude: Number(shopDetail.value.latitude),
		longitude: Number(shopDetail.value.longitude),
        name: shopDetail.value.full_address,
        address: shopDetail.value.full_address,
		success: function () { }
	});
    // #endif
}
const swiperClick = (index:any)=>{
	if(typeof index == 'number')
		imgListPreview(shopDetail.value.shop.business_image,index)
}
//预览图片
const imgListPreview = (item:any,index:any) => {
	if(Array.isArray(item)){
		if (!item.length) return false
		var urlList =item;
		uni.previewImage({
			indicator: "number",
			current:index,
			loop: true,
			urls: urlList
		})
	}else{
		if (item === '') return false
		var urlList = []
		urlList.push(img(item))  //push中的参数为 :src="item.img_url" 中的图片地址
		uni.previewImage({
			indicator: "number",
			loop: true,
			urls: urlList
		})
	}
}	
// 关闭预览图片
onUnload(()=>{
    // #ifdef  H5 || APP
    uni.closePreviewImage()
    // #endif
})

const show = ref(false)
const closeDialog = () => {
    show.value = false;
}
// #ifdef MP || APP-PLUS
//小程序中保存海报
const saveQrcode = () => {
    let url = img(qrcodeImg.value);
    uni.downloadFile({
        url: url,
        success: (res) => {
            if (res.errMsg == "downloadFile:ok") {
                uni.saveImageToPhotosAlbum({
                    filePath: res.tempFilePath,
                    success: () => {
                        uni.showToast({
                            title: '保存成功',
                            icon: 'none'
                        });
                    },
                    fail: (err) => {
                        if (err.errno == 104) {
                            let msg = '用户未授权隐私权限，将图像保存到相册失败';
                            uni.showToast({ title: msg, icon: 'none' })
                        } else if (err.errMsg == "saveImageToPhotosAlbum:fail auth deny" ||
                            err.errMsg == "saveImageToPhotosAlbum:fail:auth denied") {
                            show.value = true;
                        } else if (err.errMsg == "saveImageToPhotosAlbum:fail cancel") {
                            let msg = '用户取消将图片保存到相册';
                            uni.showToast({ title: msg, icon: 'none' })
                        } else {
                            uni.showToast({ title: err.errMsg, icon: 'none' })
                        }
                    }
                });
            }
        },
        fail: (err) => {
            uni.showToast({
                title: err.errMsg,
                icon: 'none'
            });
        }
    });
}
// #endif
</script>
<style lang="scss" scoped>
.tag{
	background: linear-gradient( 90deg, #FFD6B5 0%, #FFBA8C 100%)
}
.dialog-popup {
    width: 580rpx;
    background: #fff;
    box-sizing: border-box;
    border-radius: 10rpx;
    overflow: hidden;
    height: initial;

    .title {
        padding: 30rpx 30rpx 0 30rpx;
        text-align: center;
        font-size: 32rpx;
        font-weight: bold;
    }

    .message {
        padding: 0 30rpx;
        color: #666;
        text-align: center;
        line-height: 1.3;
        margin-top: 30rpx;
    }

    .action-wrap {
        margin-top: 50rpx;
        height: 80rpx;
        display: flex;
        border-top: 2rpx solid #eee;

        & > view {
            flex: 1;
            text-align: center;
            line-height: 80rpx;

            &:first-child {
                border-right: 2rpx solid #eee;
                color: #999;
            }

            button {
                border: none;
                line-height: 80rpx;
                padding: 0;
                margin: 0;
                width: 100%;
                height: 100%;
            }
        }
    }

    .authorization-btn {
        background-color: #07c160;
        color: #fff;
        font-size: 28rpx;
        border-radius: 0;
    }
}
</style>