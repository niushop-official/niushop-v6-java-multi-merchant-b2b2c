package com.niu.core.service.admin.shop.param;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 店铺扩展表参数
*/
@Data
public class SiteShopParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站点名称 */
    private String siteName;

    /** 站点ID（编辑时使用，新增时可不传） */
    private Integer siteId;

    /** 用户ID */
    private Integer uid;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 密码 */
    private String password;
    
    /** 确认密码 */
    private String confirmPassword;

    /** 分组ID */
    private Integer groupId;

    /** 应用列表 - 可以是字符串或数组 */
    private Object app;

    /** 插件列表 - 可以是字符串或数组 */
    private Object addons;

    /** 业务类型，group选择套餐 diy自定义 */
    private String selectType = "group";
    
    /**
     * 获取应用列表
     */
    public List<String> getAppList() {
        if (app == null || (app instanceof String && ((String) app).isEmpty())) {
            return new java.util.ArrayList<>();
        }
        if (app instanceof List) {
            return (List<String>) app;
        }
        return new java.util.ArrayList<>();
    }
    
    /**
     * 获取插件列表
     */
    public List<String> getAddonsList() {
        if (addons == null || (addons instanceof String && ((String) addons).isEmpty())) {
            return new java.util.ArrayList<>();
        }
        if (addons instanceof List) {
            return (List<String>) addons;
        }
        return new java.util.ArrayList<>();
    }

    /** 店铺申请ID */
    private Integer applyId;

    /** 过期时间 */
    private String expireTime ;

    /** 站点域名 */
    private String siteDomain = "";

    /** 营业执照 */
    private String businessLicense = "";

    /** 商家分类id */
    private Integer categoryId;

    /** 子分类id */
    private Integer childCategoryId;

    /** 商圈id */
    private Integer circleId;

    /** 是否自营 */
    private Integer isSelf = 0;

    /** 店铺联系电话 */
    private String phone;

    /** 供应链服务费率 */
    private BigDecimal supplyServiceRate = BigDecimal.ZERO;

}