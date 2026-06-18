package com.niu.core.enums.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上传压缩枚举
 */

@Getter
@AllArgsConstructor
public enum UploadThumbTypeEnum {

    BIG("big", 800, 800),
    MID("mid", 400, 400),
    SMALL("small", 200, 200),
    ;

    private final String type;

    private final Integer width;

    private final Integer height;

    public static List<String> getTypeList() {
        return Arrays.stream(UploadThumbTypeEnum.values())
                .map(UploadThumbTypeEnum::getType)
                .toList();
    }

    public static Map<String, Map<String, Object>> getMap() {
        return Arrays.stream(UploadThumbTypeEnum.values())
                .collect(Collectors.toMap(
                        UploadThumbTypeEnum::getType,
                        e -> Map.of("type", e.getType(), "width", e.getWidth(), "height", e.getHeight())
                ));
    }
}
