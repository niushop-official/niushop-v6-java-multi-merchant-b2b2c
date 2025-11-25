package com.niu.mall.enums.goods;
import cn.hutool.json.JSONObject;
import com.niu.core.common.utils.json.JsonModuleLoader;
public class GoodsSourceEnum {
    public  static  JSONObject getSourceType(){
        JsonModuleLoader jsonModuleLoader = new JsonModuleLoader();
        return  jsonModuleLoader.mergeResultElement("goods/goods_source.json");
    }

}
