package com.niu.core.upgrade.v004;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.upgrade.IUpgradeProvider;
import com.niu.core.common.component.upgrade.annotation.UpgradeProvider;
import com.niu.core.entity.member.MemberAccountLog;
import com.niu.core.entity.shop.SiteShopMember;
import com.niu.core.entity.sys.SysConfig;
import com.niu.core.enums.member.MemberAccountTypeEnum;
import com.niu.core.mapper.member.MemberAccountLogMapper;
import com.niu.core.mapper.shop.SiteShopMemberMapper;
import com.niu.core.mapper.sys.SysConfigMapper;

@UpgradeProvider(addon = "core", version = "0.0.4")
public class Upgrade extends IUpgradeProvider {

    @Override
    public void handle() {
        handleMemberAccountLogData();
        handleShopMember();
        handleConfigData();
    }

    // 处理会员账单成长值来源
    public void handleMemberAccountLogData() {
        MemberAccountLogMapper memberAccountLogMapper = SpringContext.bean(MemberAccountLogMapper.class);
        memberAccountLogMapper.update(new MemberAccountLog(), new LambdaUpdateWrapper<MemberAccountLog>()
                .eq(MemberAccountLog::getAccountType, MemberAccountTypeEnum.GROWTH.getType())
                .like(MemberAccountLog::getMemo, "订单交易成功后按订单交易金额发放成长值")
                .set(MemberAccountLog::getFromType, "consume_reward")
        );
        memberAccountLogMapper.update(new MemberAccountLog(), new LambdaUpdateWrapper<MemberAccountLog>()
                .eq(MemberAccountLog::getAccountType, MemberAccountTypeEnum.GROWTH.getType())
                .like(MemberAccountLog::getMemo, "订单交易成功后发放成长值")
                .set(MemberAccountLog::getFromType, "order_reward")
        );
    }

    // 处理店铺会员创建时间
    public void handleShopMember() {
        SiteShopMemberMapper siteShopMemberMapper = SpringContext.bean(SiteShopMemberMapper.class);
        siteShopMemberMapper.update(new SiteShopMember(), new LambdaUpdateWrapper<SiteShopMember>()
                .eq(SiteShopMember::getCreateTime, 0L)
                .set(SiteShopMember::getCreateTime, System.currentTimeMillis() / 1000)
        );
    }

    // 处理配置
    public void handleConfigData() {
        SysConfigMapper sysConfigMapper = SpringContext.bean(SysConfigMapper.class);
        sysConfigMapper.delete(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, "UPLOAD")
        );
    }

}
