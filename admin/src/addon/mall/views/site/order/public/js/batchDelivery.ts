import { addBatchOrderDelivery } from '@/addon/mall/api/site/order'
export function useBatchDelivery() {
    const handleBatchDelivery = (data: any, callback: any) => { //data中有goods,formData,type
        const orderMap = new Map()
        data.goods.forEach((item: any) => {
            if (!orderMap.has(item.order_id)) {
                orderMap.set(item.order_id, {
                    order_id: item.order_id,
                    order_goods_ids: []
                })
            }
            orderMap.get(item.order_id).order_goods_ids.push(item.order_goods_id)
        })
        const list = Array.from(orderMap.values())

        const params = {
            type: data.type,
            data: {
                list,
                ...data.formData
            }
        }

        addBatchOrderDelivery(params).then(() => {
            if (callback) {
                callback()
            }
        })
    }

    return {
        handleBatchDelivery
    }

}