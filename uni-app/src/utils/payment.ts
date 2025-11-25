import { redirect } from '@/utils/common'
class PaymentService {
    methods: any[]
    constructor() {
        this.methods = []
    }
    // 动态加载支付模块
    async init() {
        
        try {

            //加载系统支付模块
            const modules = import.meta.glob('../payment/*.ts', { eager: true })
            // console.log(modules);
            for (const path in modules) {
                // console.log('*******************', path,modules, '*******************');

                const module = await modules[path]
                this.methods.push(module.default)
                // console.log(modules);
            }

            //加载插件支付模块
            const addon_modules = import.meta.glob('../addon/**/payment/*.ts', { eager: true })
            for (const addon_path in addon_modules) {
                // console.log('*******************', addon_path,addon_modules, '*******************');
    
                const module = await addon_modules[addon_path]
                this.methods.push(module.default)
                // console.log(modules);
            }

        } catch (error) {
            // console.log(error);
        } 
    }

    // 自动选择支付方式 order, pay_type, payInfo,loading
    async autoPay(params: any = {}) {
        try {
            // 非微信/支付宝环境，尝试所有可用方式
            let is_exist = false;
                for (const method of this.methods) {
                    if(method.type == params.pay_type){
                        if (method.check()) {
                            is_exist = true;
                            await method.pay(params.order, params.payInfo,params.loading)
                            break
                        }
                    }
                }
                if(!is_exist){
                    if (params.order.url) {
                        redirect({
                            url: params.order.url,
                            param: params.order.param || {},
                            mode: 'redirectTo'
                        })
                        return
                    }
                }
        } catch (error) {
            this.handleError(error)
            throw error
        }
    }

    handleError(error: any) {
        console.error('Payment Error:', error)
        // 可扩展：发送错误日志、显示错误提示等
    }
}

export const paymentService = new PaymentService()
