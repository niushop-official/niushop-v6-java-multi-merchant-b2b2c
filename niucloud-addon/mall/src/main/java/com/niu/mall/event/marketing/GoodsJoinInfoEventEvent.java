
package com.niu.mall.event.marketing;

import com.niu.core.common.component.context.event.Event;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 获取商品参与的营销活动
 */
@Data
public abstract class GoodsJoinInfoEventEvent extends Event  {
    //商品ID列表
    private List<Integer> goodsIds;
    //是否获取数量
    private Integer isGetCount =0;
    //是否全选
    private  Integer isAll =0;
    //查询条件
    private Map<String, Object> where;

}
