package com.niu.core.service.core.member;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class MemberImportResult {
    private Integer totalNum = 0;
    private Integer successNum = 0;
    private Integer failNum = 0;
    private Integer repeatNum = 0;
    private List<List<String>> totalList = new ArrayList<>();
    private List<List<String>> failList = new ArrayList<>();
    private String failRemark;
}
