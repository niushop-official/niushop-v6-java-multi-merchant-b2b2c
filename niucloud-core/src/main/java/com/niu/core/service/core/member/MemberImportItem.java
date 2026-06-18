package com.niu.core.service.core.member;

import lombok.Data;

@Data
public class MemberImportItem {
    private String mobile;
    private String username;
    private String nickname;
    private String levelName;
    private String wxOpenid;
    private String weappOpenid;
    private String wxUnionid;
    private String sex;
    private String birthday;
    private String point;
    private String balance;
}
