package com.niu.core.service.core.upload.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UploadConfigParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "图片大小限制")
    private Long imageSize;
    @Schema(description = "图片后缀名限制")
    private String[] imageExt;
    @Schema(description = "视频大小限制")
    private Long videoSize;
    @Schema(description = "视频后缀名限制")
    private String[] videoExt;
    @Schema(description = "文档大小限制")
    private Long documentSize;
    @Schema(description = "文档后缀名限制")
    private String[] documentExt;
    @Schema(description = "压缩")
    private String thumb;
}
