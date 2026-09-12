package com.niu.core.service.admin.auth.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.anji.captcha.model.vo.CaptchaVO;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.EnumUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.consts.sms.SmsConst;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.AdminForgetPasswordParam;
import com.niu.core.service.admin.auth.param.SendMobileParam;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.param.UserMobileLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import com.niu.core.service.admin.auth.vo.LoginUserInfoVo;
import com.niu.core.service.admin.home.IAuthSiteService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.vo.SysUserInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.core.captcha.ICoreCaptchaImgService;
import com.niu.core.service.core.circle.ICoreAuthCircleUserService;
import com.niu.core.service.core.circle.vo.CircleUserAuthVo;
import com.niu.core.service.core.notice.ICoreNoticeService;
import com.niu.core.service.core.sys.ICoreConfigService;
import com.niu.core.service.core.user.ICoreUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class LoginServiceImpl implements ILoginService {

    @Resource
    IConfigService configService;
    @Resource
    ISysUserService sysUserService;
    @Resource
    ISysUserRoleService sysUserRoleService;
    @Resource
    IAuthSiteService authSiteService;
    @Resource
    IAuthService authService;
    @Resource
    ISiteService siteService;
    @Resource
    ICoreCaptchaImgService coreCaptchaImgService;
    @Resource
    private Cached cached;
    @Resource
    private ICoreNoticeService coreNoticeService;
    @Resource
    ICoreUserService coreUserService;
    @Resource
    ICoreConfigService coreConfigService;
    @Autowired(required = false)
    private ICoreAuthCircleUserService coreAuthCircleUserService;

    /**
     * 账号密码登录（admin/site/circle）
     */
    @Override
    public LoginResultVo account(UserLoginParam userLoginParam) {
        String appType = userLoginParam.getAppType();
        String userName = userLoginParam.getUsername();
        String passWord = userLoginParam.getPassword();

        if (!EnumUtils.isInclude(appType, AppTypeEnum.class, "getName")) {
            throw new AuthException("应用类型不存在");
        }

        LoginConfigVo loginConfigVo = configService.getLoginConfig();
        Integer isCaptcha = 0;
        if (appType.equals(AppTypeEnum.ADMIN.getName())) {
            isCaptcha = loginConfigVo.getIsCaptcha();
        } else if (appType.equals(AppTypeEnum.SITE.getName())) {
            isCaptcha = loginConfigVo.getIsSiteCaptcha();
        } else if (appType.equals(AppTypeEnum.CIRCLE.getName())) {
            isCaptcha = loginConfigVo.getIsCircleCaptcha();
        }

        if (isCaptcha == 1) {
            // 验证图形验证码
            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setCaptchaVerification(userLoginParam.getCaptchaCode());
            coreCaptchaImgService.verification(captchaVO);
        }
        SysUserInfoVo userInfo = sysUserService.getUserInfoByUserName(userName);
        if (ObjectUtil.isNull(userInfo)) {
            throw new AuthException("账号或密码错误", 0);
        }

        // 检测密码加密是否正确
        if (!PasswordEncipher.matche(passWord, userInfo.getPassword())) {
            throw new AuthException("账号或密码错误", 0);
        }
        return login(userInfo, appType);
    }

    /**
     * 手机号验证码登录
     */
    @Override
    public LoginResultVo mobile(UserMobileLoginParam param) {
        checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode(), SmsConst.LOGIN, param.getAppType());
        if (!sysUserService.getUserInfoByMobile(param.getMobile())) {
            throw new AuthException("当前手机号未绑定登录账号");
        }
        SysUserInfoVo userInfo = sysUserService.getUserInfoByMobileInfo(param.getMobile());
        return login(userInfo, param.getAppType());
    }

    /**
     * 发送手机验证码
     */
    @Override
    public JSONObject sendMobileCode(SendMobileParam param) {
        // 验证图形验证码
        CaptchaVO captchaVO = new CaptchaVO();
        captchaVO.setCaptchaVerification(param.getCaptchaCode());
        coreCaptchaImgService.verification(captchaVO);

        if (StrUtil.isEmpty(param.getMobile())) {
            throw new AuthException("手机号必须填写");
        }
        // 校验手机号是否已绑定账号
        if (!sysUserService.getUserInfoByMobile(param.getMobile())) {
            throw new AuthException("当前手机号未绑定登录账号");
        }
        // 校验短信场景 type
        if (!SmsConst.SCENE_TYPE.contains(param.getType())) {
            throw new AuthException("无效的短信验证码");
        }

        // 生成4位验证码
        int randomNumber = RandomUtil.randomInt(1, 9999);
        String code = StrUtil.padPre(randomNumber + "", 4, "0");
        Map<String, Object> dataMap = Map.of("code", code, "mobile", param.getMobile());
        // 发送短信
        coreNoticeService.send(RequestUtils.siteId(), "verify_code", dataMap);
        String key = DigestUtil.md5Hex(IdUtil.fastUUID());
        String cacheTagName = "mobile_key" + param.getMobile() + param.getType() + param.getAppType();
        // 清除旧验证码缓存
        clearMobileCode(param.getMobile(), param.getType(), param.getAppType());
        Map<String, Object> value = Map.of("mobile", param.getMobile(), "code", code, "type", param.getType(), "appType", param.getAppType());
        // 写入验证码缓存（600s）
        cached.tag(cacheTagName).put(key, JSONUtil.toJsonStr(value), 600);
        return JSONUtil.createObj().set("key", key);
    }

    /**
     * 忘记密码重置
     */
    @Override
    public void resetPassword(AdminForgetPasswordParam param) {
        // 1. 校验 find_pass 短信验证码（appType = site）
        checkMobileCode(param.getMobile(), param.getMobileKey(), param.getMobileCode(),
                SmsConst.FIND_PASS, AppTypeEnum.SITE.getName());
        // 2. 按手机号查用户
        if (!sysUserService.getUserInfoByMobile(param.getMobile())) {
            throw new AuthException("用户不存在");
        }
        SysUserInfoVo userInfo = sysUserService.getUserInfoByMobileInfo(param.getMobile());
        // 3. 重置密码
        coreUserService.modifyPassword(userInfo.getUid(), param.getPassword());
    }

    /**
     * 用户登录
     */
    public LoginResultVo login(SysUserInfoVo userInfo, String appType) {
        // 设置当前登录用户id
        RequestUtils.setUid(userInfo.getUid());

        if (!EnumUtils.isInclude(appType, AppTypeEnum.class, "getName")) {
            throw new AuthException("应用类型不存在");
        }

        Integer defaultSiteId = 0;
        SysUserRoleInfoVo roleInfoVo = null;
        List<Integer> siteIds = new ArrayList<>();

        if (appType.equals(AppTypeEnum.ADMIN.getName())) {
            // 平台端 admin：校验平台角色与锁定状态
            defaultSiteId = RequestUtils.defaultSiteId();
            roleInfoVo = sysUserRoleService.getUserRole(defaultSiteId, userInfo.getUid());
            if (ObjectUtil.isNotNull(roleInfoVo) && ObjectUtil.isNotNull(roleInfoVo.getUid())) {
                if (roleInfoVo.getStatus() != null && roleInfoVo.getStatus() <= 0) {
                    throw new AuthException("账号被锁定");
                }
            } else {
                // 无平台角色则拒绝登录
                throw new AuthException("您没有访问权限");
            }
        } else if (appType.equals(AppTypeEnum.SITE.getName())) {
            // 店铺端 site：校验店铺用户、可访问站点列表
            // 校验非默认站点下的用户角色是否存在
            if (!authSiteService.checkSiteUserAccount()) {
                throw new AuthException("店铺用户不存在");
            }
            siteIds = authSiteService.getSiteIds();
            if (ObjectUtil.isEmpty(siteIds)) {
                throw new AuthException("账号被锁定");
            }
            // 确定默认进入的 site_id
            int requestSiteId = RequestUtils.siteId();
            if (requestSiteId > 0 && siteIds.contains(requestSiteId)) {
                defaultSiteId = requestSiteId;
            } else {
                defaultSiteId = siteIds.get(0);
            }
        } else if (appType.equals(AppTypeEnum.CIRCLE.getName())) {
            // 商圈端 circle：校验商圈管理员与商圈列表
            defaultSiteId = RequestUtils.defaultSiteId();
            // circle 插件未安装
            if (coreAuthCircleUserService == null) {
                throw new AuthException("插件未安装");
            }
            CircleUserAuthVo circleUser = coreAuthCircleUserService.checkCircleUserAccount();
            if (ObjectUtil.isEmpty(circleUser.getCircleIds())) {
                throw new AuthException("未获取到商圈信息");
            }
        } else {
            throw new AuthException("应用类型不存在");
        }

        // 修改用户登录信息
        sysUserService.editUserLoginInfo(userInfo.getUid());

        SaLoginModel loginModel = SaLoginModel.create();
        loginModel.setDevice(RequestUtils.handler().getHeader("User-Agent"));
        loginModel.setExtra("userName", userInfo.getUsername());
        loginModel.setExtra("headImg", userInfo.getHeadImg());
        loginModel.setExtra("realName", userInfo.getRealName());
        loginModel.setExtra("appType", appType);
        // 执行登录
        StpUtil.login(userInfo.getUid(), loginModel);
        // 获取 token 信息
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        LoginResultVo resultVo = new LoginResultVo();

        LoginUserInfoVo userInfoVo = new LoginUserInfoVo();
        userInfoVo.setUid(userInfo.getUid());
        userInfoVo.setUsername(userInfo.getUsername());
        userInfoVo.setHeadImg(userInfo.getHeadImg());
        userInfoVo.setIsSuperAdmin(authService.isSuperAdmin());

        // 填充站点信息与布局
        if (appType.equals(AppTypeEnum.ADMIN.getName())
                || (appType.equals(AppTypeEnum.SITE.getName()) && defaultSiteId > 0)
                || appType.equals(AppTypeEnum.CIRCLE.getName())) {
            RequestUtils.setSiteId(defaultSiteId);
            SiteInfoVo siteInfoVo = siteService.info(RequestUtils.siteId());
            resultVo.setSiteInfo(siteInfoVo);
        }
        if (appType.equals(AppTypeEnum.ADMIN.getName()) && !userInfoVo.getIsSuperAdmin()) {
            siteIds = authSiteService.getSiteIds();
        }

        resultVo.setToken(saTokenInfo.getTokenValue());
        resultVo.setExpiresTime(DateUtils.currTime() + saTokenInfo.getTokenTimeout());
        resultVo.setUserinfo(userInfoVo);
        resultVo.setSiteId(defaultSiteId);
        resultVo.setUserrole(roleInfoVo != null ? roleInfoVo : new SysUserRoleInfoVo());
        resultVo.setLayout(resolveLayout(defaultSiteId));
        userInfoVo.setSiteIds(siteIds);

        return resultVo;
    }

    /**
     * 用户登出
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 清理token
     *
     * @param uid     用户id
     * @param appType 应用类型
     * @param token   token
     */
    @Override
    public void clearToken(Integer uid, String appType, String token) {
        if (ObjectUtil.isNotNull(token) && ObjectUtil.isNotEmpty(token)) {
            StpUtil.logoutByTokenValue(token);
        } else if (ObjectUtil.isNotNull(appType) && ObjectUtil.isNotEmpty(appType)) {
            StpUtil.logout(uid, appType);
        } else {
            StpUtil.logout(uid);
        }
    }

    /**
     * 获取站点布局风格，优先 SITE_LAYOUT，兼容 LAYOUT_SETTING
     */
    private String resolveLayout(Integer siteId) {
        if (siteId == null || siteId <= 0) {
            return "default";
        }
        JSONObject layoutConfig = coreConfigService.getConfigValue(siteId, "SITE_LAYOUT");
        if (ObjectUtil.isEmpty(layoutConfig)) {
            layoutConfig = coreConfigService.getConfigValue(siteId, "LAYOUT_SETTING");
        }
        if (ObjectUtil.isEmpty(layoutConfig)) {
            return "default";
        }
        if (layoutConfig.containsKey("key")) {
            return layoutConfig.getStr("key", "default");
        }
        JSONObject value = layoutConfig.getJSONObject("value");
        if (value != null && value.containsKey("key")) {
            return value.getStr("key", "default");
        }
        return "default";
    }

    /**
     * 校验手机验证码（按 mobile + type + appType 缓存 tag）
     */
    private void checkMobileCode(String mobile, String mobileKey, String mobileCode, String type, String appType) {
        if (StrUtil.isEmpty(mobile)) {
            throw new AuthException("手机号必须填写");
        }
        if (StringUtils.isEmpty(mobileKey) || StringUtils.isEmpty(mobileCode)) {
            throw new AuthException("手机验证码有误");
        }
        String cacheTagName = "mobile_key" + mobile + type + appType;
        Object cache = cached.tag(cacheTagName).get(mobileKey);
        if (ObjectUtil.isEmpty(cache)) {
            throw new AuthException("手机验证码有误");
        }
        JSONObject userJson = JSONUtil.parseObj(cache);
        if (!appType.equals(userJson.getStr("appType"))
                || !mobile.equals(userJson.getStr("mobile"))
                || !mobileCode.equals(userJson.getStr("code"))) {
            throw new AuthException("手机验证码有误");
        }
        clearMobileCode(mobile, type, appType);
    }

    /**
     * 清除同手机号同场景下的旧验证码缓存
     */
    private void clearMobileCode(String mobile, String type, String appType) {
        String cacheTagName = "mobile_key" + mobile + type + appType;
        cached.tag(cacheTagName).clear();
    }
}
