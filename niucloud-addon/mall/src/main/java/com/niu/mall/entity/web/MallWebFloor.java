package com.niu.mall.entity.web;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;

/**
* 首页楼层实体
*/
@Data
public class MallWebFloor implements Serializable {

private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value="id", type= IdType.AUTO)
    private Integer id;

    /** 配置参数 */
    private String config;

    /** 添加时间 */
    private Long createTime;

    /** 楼层名称 */
    private String floorName;

    /** 排序号 */
    private Integer sort;

    /** 配置模板 */
    private String temp;

    /** 更新时间 */
    private Long updateTime;

    /** 状态（0隐藏 1显示） */
    private Integer status;

}