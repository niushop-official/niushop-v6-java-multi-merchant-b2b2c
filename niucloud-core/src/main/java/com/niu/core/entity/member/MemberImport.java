package com.niu.core.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ns_member_import")
public class MemberImport implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("site_id")
    private Integer siteId;

    @TableField("main_id")
    private Integer mainId;

    @TableField("status")
    private Integer status;

    @TableField("total_num")
    private Integer totalNum;

    @TableField("success_num")
    private Integer successNum;

    @TableField("fail_num")
    private Integer failNum;

    @TableField("repeat_num")
    private Integer repeatNum;

    @TableField("data")
    private String data;

    @TableField("output")
    private String output;

    @TableField("fail_output")
    private String failOutput;

    @TableField("fail_remark")
    private String failRemark;

    @TableField("duration")
    private Integer duration;

    @TableField("create_time")
    private Long createTime;

    @TableField("update_time")
    private Long updateTime;
}
