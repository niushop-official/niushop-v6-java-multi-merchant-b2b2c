import { isWeixinBrowser } from '@/utils/common'
import wechat from '@/utils/wechat'

export default {
    type: 'wechatpay',

    check: () => true,

    pay: (order:any, payInfo: any,loading: any) => new Promise<void>((resolve, reject) => {
        //#ifdef H5
        if (isWeixinBrowser()) wechat.init();
        //#endif

        // #ifndef H5
        uni.requestPayment({
            provider: 'wxpay',
            ...order,
            success: (res: any) => {
                resolve()
            },
            fail: (res: any) => {
                loading.value = false
                reject('支付取消')
            }
        })
        // #endif
        // #ifdef H5
        if (isWeixinBrowser()) {
            order.timestamp = order.timeStamp
            delete order.timeStamp
            wechat.pay({
                ...order,
                success: () => {
                    resolve()
                },
                cancel: () => {
                    loading.value = false
                    reject('支付取消')
                }
            })
        } else {
            uni.setStorageSync('paymenting', {
                trade_type: payInfo.value?.trade_type,
                trade_id: payInfo.value?.trade_id
            })
            location.href = order.h5_url
        }
        // #endif
    })
}