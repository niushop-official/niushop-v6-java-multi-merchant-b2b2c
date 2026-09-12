package com.niu.mall.service.core.goods.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品导入/采集演示数据
 */
public class CoreGoodsPartyDemoService {

  private CoreGoodsPartyDemoService() {
  }

  public static List<Map<String, Object>> importExample() {
    List<Map<String, Object>> list = new ArrayList<>();
    list.add(buildSku(38, "白色 XL", "白色,XL", "1"));
    list.add(buildSku(39, "黑色 XL", "黑色,XL", "10"));
    return list;
  }

  private static Map<String, Object> buildSku(int skuId, String skuName, String skuSpecFormat, String stock) {
    Map<String, Object> item = new HashMap<>();
    item.put("goods_id", 33);
    item.put("sku_id", skuId);
    item.put("goods_name", "演示商品");
    item.put("sku_name", skuName);
    item.put("sku_no", "");
    item.put("sku_spec_format", skuSpecFormat);
    item.put("goods_spec_format", "颜色:白色,黑色;尺码:XL");
    item.put("goods_type", "real");
    item.put("goods_image", "https://oss.aliyun.cn/upload/attachment/image/152/202503/24/1742789772e95f8fe95273d20f25a08f001a3c6f91_local.jpg,https://oss.aliyun.cn/upload/attachment/image/142/202403/06/17097200053b642ccf48c521b340739f75cf96cf2a_local.jpg");
    item.put("sku_image", "https://oss.aliyun.cn/upload/attachment/image/152/202405/23/1716427284e22c099933682bc235befd750893471b_local.jpg");
    item.put("sub_title", "哈哈哈");
    item.put("goods_mall_category", "服装配饰");
    item.put("goods_category", "电子数码/电子手表;电子数码/电脑办公");
    item.put("goods_desc", "<p>哈哈哈哈哈哈或或或或或或<img src=\"http://niucloudb2b2c/upload/attachment/image/151/202406/15/171843769475ad96bffa0d5eabc09395e199942fe9_local.png\"/></p>");
    item.put("brand_name", "");
    item.put("price", "9.9");
    item.put("stock", stock);
    item.put("unit", "件");
    item.put("status", 0);
    item.put("delivery_type", "express;local_delivery;store");
    item.put("label_names", "标签1;标签2");
    item.put("service_names", skuId == 38 ? "正品行货 精致服务;天天低价 畅选无忧" : "正品行货;精致服务");
    item.put("attr_format", "接口类型:type-c;颜色:中国红;材质:铁");
    item.put("virtual_auto_delivery", 0);
    item.put("virtual_receive_type", "");
    item.put("virtual_verify_type", 0);
    item.put("virtual_indate", 0);
    item.put("virtual_verify_mobile", 0);
    item.put("supplier_name", "供货商名称");
    item.put("spec_type", "multi");
    return item;
  }
}
