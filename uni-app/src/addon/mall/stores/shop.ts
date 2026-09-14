import { defineStore } from 'pinia'
import { getShopInfo } from '@/addon/mall/api/shop';
import useSystemStore from '@/stores/system'

interface ShopInfo {
    shopInfo: AnyObject
}


export const useShopStore = defineStore('shop', {
    state: (): ShopInfo => {
        return {
            shopInfo: {}
        }
    },
    actions: {
        getShopInfoFn(siteId: any, callback: any){
            getShopInfo(siteId,{ position: useSystemStore().diyAddressInfo }).then((res: any) => {
               const data = {
                    siteId: res.data.site_id,
                    icon: res.data.front_end_logo ? res.data.front_end_logo : res.data.icon,
                    siteName: res.data.site_name,
                    typeName: res.data.is_self == 1 ? '自营店铺' : '直营店铺',
                    follow_number: res.data.follow_number,
                    isFollow: res.data.member_info?.is_follow,
                    distance: res.data.distance,
                    background_h5: res.data.background_h5,
                }
                this.shopInfo[siteId] = data;
                if(callback) callback(data);
            })
        }
    }
})

export default useShopStore