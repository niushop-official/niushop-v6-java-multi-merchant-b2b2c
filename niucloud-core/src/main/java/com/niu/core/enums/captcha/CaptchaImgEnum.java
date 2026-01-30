package com.niu.core.enums.captcha;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum CaptchaImgEnum {
    BLOCK_PUZZLE("blockPuzzle", "滑动拼图", "static/resource/images/captcha/block_puzzle.gif"),
    CLICK_WORD("clickWord", "文字点选", "static/resource/images/captcha/click_word.gif");

    private final String type;
    private final String name;
    private final String img;


    public static Map<String, Map<String, Object>> getMap() {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        for (CaptchaImgEnum item : CaptchaImgEnum.values()) {
            Map<String, Object> mapItem = new HashMap<>();
            mapItem.put("name", item.getName());
            mapItem.put("example_img", item.getImg());
            map.put(item.getType(), mapItem);
        }
        return map;
    }
}
