package com.niu.core.entity.member;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 会员访问日志表
 */
@Data
public class MemberVisit implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    /** 主键ID */
    private Integer id;
    /** 会员ID */
    private Integer memberId;
    /** 访问时间 */
    private Long visitTime;
    /** 站点ID */
    private Integer siteId;
}
