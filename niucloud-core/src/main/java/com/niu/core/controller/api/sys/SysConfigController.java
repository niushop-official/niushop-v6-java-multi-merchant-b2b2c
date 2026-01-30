package com.niu.core.controller.api.sys;

import cn.hutool.core.util.ObjectUtil;
import com.niu.core.common.domain.Result;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.event.sys.InitWapEvent;
import com.niu.core.service.api.diy.IDiyService;
import com.niu.core.service.api.diy.param.DiyTabbarListParam;
import com.niu.core.service.api.login.ILoginService;
import com.niu.core.service.api.member.IMemberLevelService;
import com.niu.core.service.api.member.IMemberService;
import com.niu.core.service.api.member.param.MemberLevelParam;
import com.niu.core.service.api.sys.ISysConfigService;
import com.niu.core.service.api.sys.param.*;
import com.niu.core.service.api.sys.vo.SysInitVo;
import com.niu.core.service.core.app.helper.EventAndSubscribeOfPublisher;
import com.niu.core.service.core.diy.ICoreDiyConfigService;
import com.niu.core.service.core.site.ICoreSiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api")
@Slf4j
public class SysConfigController {

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private ILoginService loginService;

    @Resource
    private IDiyService diyService;

    @Resource
    private ICoreSiteService coreSiteService;

    @Resource
    private IMemberLevelService memberLevelService;
    @Resource
    private IMemberService memberService;

    /**
     * 版权信息
     *
     * @param param
     * @return
     */
    @GetMapping("/copyright")
    public Result<?> copyright(SysConfigCopyrightParam param) {
        return Result.success(sysConfigService.copyright(param));
    }

    /**
     * 站点信息
     *
     * @param param
     * @return
     */
    @GetMapping("/site")
    public Result<?> site(SysConfigSiteParam param) {
        return Result.success(sysConfigService.site(param));
    }

    /**
     * 场景域名
     *
     * @param param
     * @return
     */
    @GetMapping("/scene_domain")
    public Result<?> sceneDomain(SysConfigSceneDomainParam param) {
        return Result.success(sysConfigService.sceneDomain(param));
    }

    /**
     * 获取地图设置
     *
     * @return
     */
    @GetMapping("/map")
    public Result<?> map(SysConfigMapParam param) {
        return Result.success(sysConfigService.map(param));
    }

    /**
     * 获取初始化数据信息
     *
     * @param url
     * @return
     */
    @GetMapping("/init")
    public Result init(@RequestParam(name = "url", defaultValue = "") String url) {
        var vo = new SysInitVo();

        vo.setTabbarList(diyService.getBottomList());
        vo.setMapConfig(sysConfigService.map(new SysConfigMapParam()));
        vo.setLoginConfig(loginService.getLoginConfig());
        vo.setSiteInfo(coreSiteService.getSiteCache(RequestUtils.siteId()));
        vo.setThemeList(diyService.getDiyTheme());
        vo.setMemberLevel(memberLevelService.list(new MemberLevelParam()));

        if (ObjectUtil.isEmpty(vo.getSiteInfo().getSiteId())) {
            InitWapEvent event = new InitWapEvent();
            event.setSiteId(RequestUtils.siteId());
            EventAndSubscribeOfPublisher.publishAll(event);
        }

        return Result.success(vo);
    }

    /**
     * 用作小程序审核
     * @return
     */
    @GetMapping("/isshow")
    public Result isShow() {
        return Result.success(sysConfigService.getWeappListSetting().getIsOpen() == 0);
    }

    /**
     * 获取公众号用户是否绑定手机
     * @return Response
     */
    @GetMapping("/member_mobile_exist")
    public Result getMemberMobileExist(SysConfigMobileExistParam param) {
        String openid = param.getOpenid();
        String openidField;
        String channel = RequestUtils.channel();

        if ("wechat".equals(channel)) {
            openidField = "wx_openid";
        } else if ("weapp".equals(channel)) {
            openidField = "weapp_openid";
        } else {
            openidField = "";
        }

        // 根据来源查询是否绑定手机号, 如果绑定并且开启强制绑定则快捷登录时不再弹出绑定手机弹窗
        int memberMobileExist = 0;
        if (openid != null && !openid.isEmpty()) {
            if (openidField != null && !openidField.isEmpty()) {
                // 使用getCount方法处理查询逻辑，保持代码整洁
                List<List<String>> conditions = new ArrayList<>();
                conditions.add(Arrays.asList(openidField, "=", openid));
                conditions.add(Arrays.asList("mobile", "<>", ""));

                Integer count = memberService.getCount(conditions);
                memberMobileExist = (count != null && count > 0) ? 1 : 0;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("member_mobile_exist", memberMobileExist);
        return Result.success(result);
    }

    @GetMapping("/service")
    public Result service() {
        return Result.success(sysConfigService.service());
    }

    @GetMapping("/qrcode/index")
    public Result indexQrcode() {
        Object qrcode = sysConfigService.getIndexQrcode();
        return Result.success(qrcode);
    }
}
