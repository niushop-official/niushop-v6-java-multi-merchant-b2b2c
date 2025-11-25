package com.niu.mall.listener.treasure;

import com.niu.core.common.annotation.EventCallback;
import com.niu.core.event.common.CommonEventDefiner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 宝贝类型查询
 * Class TreasureTypeListener
 * @package com.niu.mall.listener.treasure
 */
@EventCallback("mall")
@Component
public class TreasureTypeListener extends CommonEventDefiner {

    /**
     * 处理宝贝类型查询事件
     * @param event 事件参数
     * @return 宝贝类型结果
     */
    @Override
    public CommonEventDefiner.CommonEventResult handleCallback(CommonEventDefiner.CommonEvent event) {

        if(event.getEventName().equals("TreasureType")){
            // 创建结果对象
            CommonEventDefiner.CommonEventResult result = new CommonEventDefiner.CommonEventResult();
            // 创建类型映射
            Map<String, String> treasureTypes = new HashMap<>();
            treasureTypes.put("mall", "商品");

            // 设置结果
            result.setResult(treasureTypes);
            return result;
        }

        return null;
    }
}
