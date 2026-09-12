package com.niu.core.enums.upload;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 上传规范枚举
 */
@Getter
@AllArgsConstructor
public enum UploadExtTypeEnum {
    IMAGE("image", new String[]{"jpg", "png", "gif", "jpeg", "webp"}),
    VIDEO("video", new String[]{"mp4"}),
    DOCUMENT("document", new String[]{"crt", "pem", "zip", "rar", "key", "xls", "xlsx"}),
    ;
    /**
     * 规则名称
     */
    private final String name;

    /**
     * 扩展名
     */
    private final String[] ext;

    public static Map<String, String[]> getMap() {
        return Arrays.stream(UploadExtTypeEnum.values())
                .collect(Collectors.toMap(UploadExtTypeEnum::getName, UploadExtTypeEnum::getExt));
    }
}
