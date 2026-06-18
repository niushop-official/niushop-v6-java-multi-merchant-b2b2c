<template>
    <view class="min-h-[100vh] bg-[#fff] overflow-hidden" :style="themeColor()"  v-if="cashOutInfo">
        <view class="px-[24rpx] pt-[80rpx]" :style="{backgroundImage: 'url(' + img('static/resource/images/site_uniapp/cash_out/cash_out_bg.png') + ')',backgroundSize: '100%', backgroundPosition: 'center -35%', backgroundRepeat: 'no-repeat'}">
            <view class="flex justify-center mb-[20rpx]">
                <image v-if="cashOutInfo.status == -1" :src="img(`static/resource/images/site_uniapp/cash_out/cash_out_5.png`)" class="w-[70rpx] h-[70rpx]" />
                <image v-else-if="cashOutInfo.status == -2" :src="img(`static/resource/images/site_uniapp/cash_out/cash_out_6.png`)" class="w-[70rpx] h-[70rpx]" />
                <image v-else :src="img(`static/resource/images/site_uniapp/cash_out/cash_out_${cashOutInfo.status}.png`)" class="w-[70rpx] h-[70rpx]" />
            </view>
            <view class="text-center text-[30rpx] font-600 mb-[40rpx]">{{ cashOutInfo.status_name }}</view>
            <view class="text-center price-font mb-[60rpx]">
                <text class="text-[42rpx]">￥</text>
                <text class="text-[76rpx]">{{ cashOutInfo.apply_money }}</text>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[26rpx] text-[#969696]">店铺名称</view>
                <view class="text-[26rpx]">{{ cashOutInfo.siteInfo.site_name }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[26rpx] text-[#969696]">联系电话</view>
                <view class="text-[26rpx]">{{ cashOutInfo.siteInfo.phone }}</view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[26rpx] text-[#969696]">提现方式</view>
                <view class="text-[26rpx]">{{ cashOutInfo.transfer_type_name }}</view>
            </view>
            <template  v-if="cashOutInfo.transfer_type == '1'">
                <view class="justify-between card-template-item">
                    <view class="text-[26rpx] text-[#969696]">开户人姓名</view>
                    <view class="text-[26rpx]">{{ cashOutInfo.transfer_realname }}</view>
                </view>
                <view class="justify-between card-template-item">
                    <view class="text-[28rpx] text-[#969696]">银行名称</view>
                    <view class="text-[28rpx]">{{ cashOutInfo.transfer_bank }}</view>
                </view>
                <view class="justify-between card-template-item">
                    <view class="text-[26rpx] text-[#969696]">银行卡号</view>
                    <view class="text-[26rpx]">{{ cashOutInfo.transfer_account }}</view>
                </view>
            </template>
            <template  v-if="cashOutInfo.transfer_type == '2'">
                <view class="justify-between card-template-item">
                    <view class="text-[26rpx] text-[#969696]">支付宝姓名</view>
                    <view class="text-[26rpx]">{{ cashOutInfo.transfer_realname }}</view>
                </view>
                <view class="justify-between card-template-item">
                    <view class="text-[26rpx] text-[#969696]">支付宝账号</view>
                    <view class="text-[26rpx]">{{ cashOutInfo.transfer_account }}</view>
                </view>
            </template>
            <view class="justify-between card-template-item" v-if="cashOutInfo.status == 3 && cashOutInfo.transfer_voucher">
                <view class="text-[26rpx] text-[#969696]">转账凭证</view>
                <view class="flex items-center" @click="previewImg()">
                    <image :src="img(cashOutInfo.transfer_voucher)" class="w-[140rpx] h-[140rpx] rounded-[var(--rounded-small)]" />
                </view>
            </view>
            <view class="justify-between card-template-item">
                <view class="text-[26rpx] text-[#969696]">申请时间</view>
                <view class="text-[26rpx]">{{ cashOutInfo.create_time }}</view>
            </view>
            <view class="justify-between card-template-item" v-if="cashOutInfo.refuse_reason">
                <view class="text-[26rpx] text-[#969696]">拒绝原因</view>
                <view class="text-[28rpx]">{{ cashOutInfo.refuse_reason }}</view>
            </view>
        </view>
        <view class="flex flex-col items-center mt-[200rpx] mb-[80rpx]">
            <text v-if="(cashOutInfo.status == 2 || cashOutInfo.status == 4) && cashOutInfo.transfer_type == 4" class="w-[320rpx] h-[80rpx] bg-primary rounded-[16rpx] text-center leading-[80rpx] text-[30rpx] text-[#fff] mb-[40rpx]" @click="cashOut">提现</text>
            <text v-if="cashOutInfo.status == 1 || cashOutInfo.status == 2" class="w-[320rpx] h-[80rpx] rounded-[16rpx] text-center leading-[80rpx] text-[30rpx] text-[333] bg-[#eee]" @click="transferCancel">取消提现</text>
        </view>
        <view @touchmove.prevent.stop>
            <u-popup class="popup-type" :show="qrcodePopup" @close="qrcodePopup = false" mode="center" round="var(--rounded-big)">
                <view class="w-[540rpx] rounded-[24rpx] min-h-[300rpx] popup-common" @touchmove.prevent.stop>
                    <swiper class="h-[550rpx]" circular indicator-dots="true">
                        <swiper-item>
                            <view class="title">公众号提现</view>
                            <view class="pb-[40rpx]">
                                <view class="flex-center mb-[30rpx]">
                                    <image :src="wapImage" class="w-[220rpx] h-[220rpx]" mode="aspectFill"></image>
                                </view>
                                <view class="text-center text-[24rpx] text-[#999] mb-[30rpx]">长按识别二维码</view>
                                <view class="flex justify-center">
                                    <button hover-class="none" class="list-grey-solid-btn bg-primary text-[#fff] m-0" @click="toLink">提现</button>
                                </view>
                            </view>
                        </swiper-item>
                        <swiper-item>
                            <view class="title">小程序提现</view>
                            <view class="pb-[40rpx]">
                                <view class="flex-center mb-[30rpx]">
                                    <image :src="img(transferUrl.qrcode)" class="w-[220rpx] h-[220rpx]" mode="aspectFill"></image>
                                </view>
                                <view class="text-center text-[24rpx] text-primary mb-[30rpx]" @click="saveQrcode()">保存二维码</view>
                                <view class="flex justify-center">
                                    <button hover-class="none" class="list-grey-solid-btn bg-primary text-[#fff] m-0" @click="toWeapp">提现</button>
                                </view>
                            </view>
                        </swiper-item>
                    </swiper>
                </view>
            </u-popup>
            <u-popup :show="show" mode="center" :round="10" :closeable="true" @close="show = false" :safe-area-inset-bottom="false">
                <view class="dialog-popup">
                    <view class="title">提示</view>
                    <view class="message">您拒绝了保存图片到相册的授权请求，无法保存图片到相册，如需正常使用，请授权之后再进行操作。</view>
                    <view class="action-wrap">
                        <view @click="closeDialog">取消</view>
                        <view>
                            <button class="authorization-btn" open-type="openSetting" @opensetting="closeDialog" hover-class="none">立即授权</button>
                        </view>
                    </view>
                </view>
            </u-popup>
        </view>
        <!-- 提示 -->
        <tips-popup ref="tipsRef" />
    </view>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { img, diyRedirect } from '@/utils/common'
import { onLoad, onUnload } from '@dcloudio/uni-app';
import { getShopCashOutInfo, getShopCashOutTransfer, transferShopCashCancel } from '@/app/api/account'
import QRCode from 'qrcode'

const cashOutInfo = ref<any>();
const id = ref('');
const tipsRef = ref()

onLoad((option: any) => {
    id.value = option.id
    getShopCashOutInfoFn(id.value)
})

const  getShopCashOutInfoFn  = (id: any) => {
    getShopCashOutInfo(id).then((res: any) => {
        cashOutInfo.value = res.data
    })
}

// 提现
const transferUrl = ref<any>('')
const cashOut = () => {
    getShopCashOutTransfer(cashOutInfo.value.id).then((res: any) => {
        transferUrl.value = res.data
        loadQrcode()
        qrcodePopup.value = true
    })
}

const wapPreview = ref<any>('')
const qrcodePopup = ref(false)
const wapImage = ref('') //公众号二维码

// #ifdef H5 
const loadQrcode = () => {
    wapPreview.value = `${ import.meta.env.VITE_IMG_DOMAIN || location.origin }/wap/${transferUrl.value.page}`
    // errorCorrectionLevel：密度容错率L（低）H(高)
    QRCode.toDataURL(wapPreview.value, { errorCorrectionLevel: 'L', margin: 0, width: 120 }).then(url => {
        wapImage.value = url
    })
}
// #endif

const toWeapp = () => {
    diyRedirect(wapPreview.value)
}

const toLink = () => {
   window.location.href = transferUrl.value.url_link;
}
// 取消提现  
const transferCancel = () => {
    tipsRef.value.open('是否确认取消提现？', () => {
        transferShopCashCancel(cashOutInfo.value.id).then(() => {
            getShopCashOutInfoFn(id.value)
        })
    })

}
// 预览图片
const previewImg = () => {
    uni.previewImage({
        urls: [img(cashOutInfo.value.transfer_voucher)],
        indicator: "number",
        loop: true
    })
}

// 关闭预览图片
onUnload(() => {
    // #ifdef  H5 || APP
    try {
        uni.closePreviewImage()
    } catch (e) {

    }
    // #endif
})

const show = ref(false)
const closeDialog = () => {
    show.value = false;
}
// #ifdef MP || APP-PLUS
//小程序中保存海报
const saveQrcode = () => {
    let url = img(transferUrl.value.qrcode);
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
.card-template-item{
   display: flex;
    align-items: center;
    min-height: 40rpx;
    margin-bottom: 34rpx;
    &:last-of-type{
        margin-bottom: 0;
    } 
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