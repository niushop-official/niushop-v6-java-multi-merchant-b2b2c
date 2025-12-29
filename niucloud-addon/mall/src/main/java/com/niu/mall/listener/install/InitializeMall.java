package com.niu.mall.listener.install;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.SpringContext;
import com.niu.core.common.component.initialize.InitializeProvider;
import com.niu.core.common.component.initialize.annotation.Initializer;
import com.niu.core.common.config.dataization.AddonModuleContext;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.entity.member.Member;
import com.niu.core.service.core.addon.ICoreAddonService;
import com.niu.core.service.core.poster.ICorePosterService;
import com.niu.core.service.core.site.ICoreSiteService;
import com.niu.mall.service.admin.install.IInstallMallService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Set;

@Initializer("mall")
@Slf4j
public class InitializeMall implements InitializeProvider {

    public void init(ApplicationContext applicationContext) throws Exception {
        //判断插件是否已经安装
        ICoreSiteService coreSiteService = SpringContext.bean(ICoreSiteService.class);
        IInstallMallService installMallService = SpringContext.bean(IInstallMallService.class);

        if (coreSiteService.siteAddonIsInit(0, "mall")) return;
        Set<String> addonModules = AddonModuleContext.getAddonModules();
        if(!addonModules.contains("mall")){
            return;
        }
        //初始化 插件数据
        installMallService.install();

        coreSiteService.addSiteAddonInitRecord(0, "mall");

    }
}
