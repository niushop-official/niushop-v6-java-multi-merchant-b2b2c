package com.niu.mall.service.admin.site_shop.param;

import com.niu.core.enums.shop.ShopApplyEnum;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminShopApplyVerifyParam {
    @NotNull(message = "status不能为空")
    private Integer status;
    private String refuseReason;

    @AssertTrue(message = "请填写拒绝原因")
    public boolean isRefuseReasonValid() {
        if (status == null || !ShopApplyEnum.REFUSE.getValue().equals(status)) {
            return true;
        }
        return refuseReason != null && !refuseReason.isBlank();
    }
}
