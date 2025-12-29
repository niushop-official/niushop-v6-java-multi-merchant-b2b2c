import { getWeappTemplateId } from '@/app/api/system'

/**
 * 小程序订阅消息
 */
export const useSubscribeMessage = () => {
    const request = (keys: string) => {
        // #ifdef MP-WEIXIN
        const method = getWeappTemplateId
        // #endif

        // #ifdef MP
        let env = __wxConfig.envVersion 
        method({
            miniprogram_state: env,
            keys
        }).then(({ data }) => {
            uni.requestSubscribeMessage({
                tmplIds: data,
                success: (res) => {
                    console.log("requestSubscribeMessage:success", res)
                },
                fail: (res) => {
                    console.log('requestSubscribeMessage:fail', res)
                }
            })
        }).catch()
        // #endif
    }

    return {
        request
    }
}
