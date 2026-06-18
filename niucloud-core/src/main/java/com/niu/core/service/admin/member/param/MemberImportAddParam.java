package com.niu.core.service.admin.member.param;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class MemberImportAddParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private MemberImportData data;

    @Data
    public static class MemberImportData {
        private String path;
        private MobileRepeatRule mobileRepeatRule;
        private Map<String, Object> myMemberLabel = new HashMap<>();
    }

    @Data
    public static class MobileRepeatRule {
        private String basic;
        private String point;
        private String balance;
    }
}
