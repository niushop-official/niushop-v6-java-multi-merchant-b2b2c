package com.niu.core.event.shop;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.site.SiteApply;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 商户入驻申请审核后事件
 * 用于在申请审核后触发后续业务处理，如记录操作日志等
 * v120迁移：使用 SiteApply 实体
 */
@Getter
public class SiteShopApplyVerifyAfterEvent extends Event {

    /**
     * 操作类型
     */
    private final String type;

    /**
     * 主类型
     */
    private final String mainType;

    /**
     * 操作用户ID
     */
    private final Integer uid;

    /**
     * 审核后的申请数据
     */
    private final SiteApply siteApply;

    /**
     * 审核状态
     */
    private final Integer status;

    /**
     * 拒绝原因
     */
    private final String refuseReason;

    public SiteShopApplyVerifyAfterEvent(Object source, String type, String mainType, Integer uid, SiteApply siteApply, Integer status, String refuseReason) {
        super(source);
        this.type = type;
        this.mainType = mainType;
        this.uid = uid;
        this.siteApply = siteApply;
        this.status = status;
        this.refuseReason = refuseReason;
    }
}
