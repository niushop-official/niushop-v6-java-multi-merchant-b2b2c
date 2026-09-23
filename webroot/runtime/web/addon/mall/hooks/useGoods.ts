export function useGoods(params: any = {}) {
    // 价格类型
    const priceType = (data: any) => {
        return data.goodsSku.show_type
    }

    // 商品价格
    const goodsPrice = (data: any) => {
        let price = data.goodsSku.show_price
        return parseFloat(price);
    }

    // 错误图片展示
    const errorImgFn = (data: any, type: any) => {
        data[type] = '';
    }

    return {
        goodsPrice: goodsPrice,
        priceType: priceType,
        error: errorImgFn
    }
}
