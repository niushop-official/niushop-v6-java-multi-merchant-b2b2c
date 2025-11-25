import { isWeixinBrowser, redirect } from '@/utils/common'

export default {
    type: 'huifu_alipay',

    check: () => true,

    pay: (order:any, payInfo:any) => new Promise<void>((resolve, reject) => {
        // #ifdef H5
        if (isWeixinBrowser()) {
            redirect({
                url: '/app/pages/pay/browser',
                param: {
                    trade_type: payInfo.value?.trade_type,
                    trade_id: payInfo.value?.trade_id,
                    alipay: encodeURIComponent(order.url)
                },
                mode: 'redirectTo'
            })
        } else {
            uni.setStorageSync('paymenting', {
                trade_type: payInfo.value?.trade_type,
                trade_id: payInfo.value?.trade_id
            })
            location.href = order.url
        }
        // #endif
    })
}