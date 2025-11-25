import { defineStore } from 'pinia'
import { getConfig, weappCheck } from '@/app/api/auth'

interface loginConfig {
    is_username: number | boolean,
    is_mobile: number | boolean,
    is_auth_register: number | boolean,
    is_bind_mobile: number | boolean,
    agreement_show: number | boolean
}


interface Config {
    login: loginConfig,
    weappOpen: any
}


const useConfigStore = defineStore('config', {
    state: (): Config => {
        return {
            login: {
                is_username: 0,
                is_mobile: 0,
                is_auth_register: 0,
                is_bind_mobile: 0,
                agreement_show: 0
            },
            weappOpen: null,
        }
    },
    actions: {
        async getLoginConfig() {
            await getConfig().then(({ data }) => {
                this.login.is_username = data.is_username
                this.login.is_mobile = parseInt(data.is_mobile)
                this.login.is_auth_register = parseInt(data.is_auth_register)
                this.login.is_bind_mobile = parseInt(data.is_bind_mobile)
                this.login.agreement_show = parseInt(data.agreement_show)
            }).catch(() => {

            })
        },
        async getWeappConfig() {
            await weappCheck().then((res: any) => {
                this.weappOpen = res.data
            }).catch(() => {
                
            })
        }
    }
})

export default useConfigStore
