package com.niu.core.service.admin.auth.impl;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.anji.captcha.model.vo.CaptchaVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.AuthException;
import com.niu.core.common.utils.EnumUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.common.utils.crypto.PasswordEncipher;
import com.niu.core.common.utils.date.DateUtils;
import com.niu.core.consts.sms.SmsConst;
import com.niu.core.entity.sys.SysUser;
import com.niu.core.enums.sys.AppTypeEnum;
import com.niu.core.enums.sys.SmsStatusEnum;
import com.niu.core.service.admin.auth.IAuthService;
import com.niu.core.service.admin.auth.IConfigService;
import com.niu.core.service.admin.auth.ILoginService;
import com.niu.core.service.admin.auth.param.SendMobileParam;
import com.niu.core.service.admin.auth.param.UserLoginParam;
import com.niu.core.service.admin.auth.param.UserMobileLoginParam;
import com.niu.core.service.admin.auth.vo.LoginConfigVo;
import com.niu.core.service.admin.auth.vo.LoginResultVo;
import com.niu.core.service.admin.auth.vo.LoginUserInfoVo;
import com.niu.core.service.admin.home.IAuthSiteService;
import com.niu.core.service.admin.site.ISiteService;
import com.niu.core.service.admin.site.vo.SiteInfoVo;
import com.niu.core.service.admin.sys.ISysNoticeService;
import com.niu.core.service.admin.sys.ISysUserRoleService;
import com.niu.core.service.admin.sys.ISysUserService;
import com.niu.core.service.admin.sys.vo.SysUserInfoVo;
import com.niu.core.service.admin.sys.vo.SysUserRoleInfoVo;
import com.niu.core.service.core.captcha.ICoreCaptchaImgService;
import com.niu.core.service.core.notice.ICoreNoticeService;
import org.apache.commons.lang3.StringUtils;
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
    @Override
    public LoginResultVo account(UserLoginParam userLoginParam) {
        String appType=userLoginParam.getAppType();
        String userName=userLoginParam.getUsername();
        String passWord=userLoginParam.getPassword();

        if(!EnumUtils.isInclude(appType, AppTypeEnum.class, "getName")){
            throw new AuthException("APP_TYPE_NOT_EXIST");
        }

        LoginConfigVo loginConfigVo=configService.getLoginConfig();
        Integer isCaptcha=0;
        if(appType.equals(AppTypeEnum.ADMIN.getName())){
            isCaptcha=loginConfigVo.getIsCaptcha();
        }else if(appType.equals(AppTypeEnum.SITE.getName())){
            isCaptcha=loginConfigVo.getIsSiteCaptcha();
        }

        if(isCaptcha==1){
            //验证验证码
            CaptchaVO captchaVO=new CaptchaVO();
            captchaVO.setCaptchaVerification(userLoginParam.getCaptchaCode());
            coreCaptchaImgService.verification(captchaVO);
        }
        SysUserInfoVo userInfo=sysUserService.getUserInfoByUserName(userName);
        if(ObjectUtil.isNull(userInfo)){
            throw new AuthException("账号密码错误");
        }

        //检测密码加密是否正确
        if(!PasswordEncipher.matche(passWord, userInfo.getPassword())){
            throw new AuthException("账号密码错误");
        }
        return login(userInfo,appType);
    }

    @Override
    public LoginResultVo mobile(UserMobileLoginParam param) {
        checkMobileCode(param, param.getAppType());
        SysUserInfoVo userInfo = sysUserService.getUserInfoByUserName(param.getMobile());
        if (ObjectUtil.isNull(userInfo)){
            throw new AuthException("手机号填写错误");
        }
        return login(userInfo, param.getAppType());
    }

    @Override
    public JSONObject sendMobileCode(SendMobileParam param) {
        //校验手机号是否存在
        if (!sysUserService.getUserInfoByMobile(param.getMobile())){
            throw new AuthException("手机号填写错误");
        }
        CaptchaVO captchaVO=new CaptchaVO();
        captchaVO.setCaptchaVerification(param.getCaptchaCode());
        coreCaptchaImgService.verification(captchaVO);

        if (!SmsConst.SCENE_TYPE.contains(param.getType())){
            throw new AuthException("无效的短信验证码");
        }
        //生成验证码
        int randomNumber = RandomUtil.randomInt(1, 9999);
        String code = StrUtil.padPre(randomNumber+"", 4, "0");
        Map<String, Object> dataMap = Map.of("code", code, "mobile", param.getMobile());
        //发送短信
        coreNoticeService.send(RequestUtils.siteId(), "verify_code", dataMap);
        String key = DigestUtil.md5Hex(IdUtil.fastUUID());
        String cacheTagName = "mobile_key"+param.getMobile()+param.getType()+param.getAppType();
        //清除旧数据
        clearMobileCode(param.getMobile(),param.getType(),param.getAppType());
        Map<String, Object> value = Map.of("mobile", param.getMobile(), "code", code, "type", param.getType(), "appType", param.getAppType());
        //添加缓存
        cached.tag(cacheTagName).put(key, JSONUtil.toJsonStr( value), 600);
        return JSONUtil.createObj().set("key", key);
    }

    /**
     * 用户登录
     * @return
     */
    public LoginResultVo login(SysUserInfoVo userInfo,String appType) {
        //设置当前登录用户id
        RequestUtils.setUid(userInfo.getUid());

        Integer defaultSiteId=0;
        SysUserRoleInfoVo roleInfoVo=new SysUserRoleInfoVo();
        List<Integer> siteIds=new ArrayList<>();
        if(appType.equals(AppTypeEnum.ADMIN.getName())){
            defaultSiteId=RequestUtils.defaultSiteId();
            roleInfoVo=sysUserRoleService.getUserRole(defaultSiteId, userInfo.getUid());
            if(ObjectUtil.isNotNull(roleInfoVo)){
                if(userInfo.getStatus()<=0){
                    throw new AuthException("USER_LOCK");
                }
            }else{
                appType=AppTypeEnum.SITE.getName();
            }
        }else if(appType.equals(AppTypeEnum.SITE.getName())){
            siteIds=authSiteService.getSiteIds();
            if(ObjectUtil.isNotNull(siteIds) && siteIds.size()>0){
                defaultSiteId=((siteIds.indexOf(RequestUtils.siteId())>0 || authService.isSuperAdmin()) & RequestUtils.siteId() > 0)?RequestUtils.siteId():siteIds.get(0);
            }
        }else{
            throw new AuthException("APP_TYPE_NOT_EXIST");
        }

        //修改用户登录信息
        sysUserService.editUserLoginInfo(userInfo.getUid());

        SaLoginModel loginModel = SaLoginModel.create();
        loginModel.setDevice(RequestUtils.handler().getHeader("User-Agent"));
        loginModel.setExtra("userName", userInfo.getUsername());
        loginModel.setExtra("headImg", userInfo.getHeadImg());
        loginModel.setExtra("realName", userInfo.getRealName());
        // 执行登录
        StpUtil.login(userInfo.getUid(), loginModel);
        // 获取返回内容
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        LoginResultVo resultVo=new LoginResultVo();

        LoginUserInfoVo userInfoVo=new LoginUserInfoVo();
        userInfoVo.setUid(userInfo.getUid());
        userInfoVo.setUsername(userInfo.getUsername());
        userInfoVo.setHeadImg(userInfo.getHeadImg());
        userInfoVo.setIsSuperAdmin(authService.isSuperAdmin());


        if(appType.equals(AppTypeEnum.ADMIN.getName()) || (appType.equals(AppTypeEnum.SITE.getName()) && defaultSiteId>0)){
            RequestUtils.setSiteId(defaultSiteId);
            SiteInfoVo siteInfoVo=siteService.info(RequestUtils.siteId());
            resultVo.setSiteInfo(siteInfoVo);
        }
        if(appType.equals(AppTypeEnum.ADMIN.getName()) && !userInfoVo.getIsSuperAdmin()){
            siteIds=authSiteService.getSiteIds();
        }

        resultVo.setToken(saTokenInfo.getTokenValue());
        resultVo.setExpiresTime(DateUtils.currTime()+saTokenInfo.getTokenTimeout());
        resultVo.setUserinfo(userInfoVo);
        resultVo.setSiteId(defaultSiteId);
        resultVo.setUserrole(roleInfoVo);
        userInfoVo.setSiteIds(siteIds);

        return resultVo;
    }

    /**
     * 用戶登出
     *
     */
    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 清理token
     *
     * @param uid
     * @param appType
     * @param token
     */
    @Override
    public void clearToken(Integer uid, String appType, String token) {
        if(ObjectUtil.isNotNull(token) && ObjectUtil.isNotEmpty(token)){
            StpUtil.logoutByTokenValue(token);
        }else if(ObjectUtil.isNotNull(appType) && ObjectUtil.isNotEmpty(appType)){
            StpUtil.logout(uid, appType);
        }else{
            StpUtil.logout(uid);
        }
    }

    private Boolean checkMobileCode(UserMobileLoginParam param,String appType){
        if(StringUtils.isEmpty(param.getMobileKey()) || StringUtils.isEmpty(param.getMobileCode())) throw new AuthException("手机验证码有误");
       Object cache =  cached.tag("mobile_key"+param.getMobile()+"login"+param.getAppType()).get(param.getMobileKey());
       if(ObjectUtil.isEmpty( cache)){
           throw new AuthException("手机验证码有误");
       }
       JSONObject userJson = JSONUtil.parseObj(cache);
       String tempMobile = userJson.getStr("mobile");
       String tempCode = userJson.getStr("code");
       String tempType = userJson.getStr("type");
       String tempAppType = userJson.getStr("appType");
       if(!tempAppType.equals(appType)){
           throw new AuthException("手机验证码有误");
       }
       if(!tempMobile.equals(param.getMobile()) || !tempCode.equals(param.getMobileCode())){
           throw new AuthException("手机验证码有误");
       }

       clearMobileCode(tempMobile,tempType,appType);
       return true;
    }

    private void clearMobileCode(String mobile,String type,String appType){
        String cacheTagName = "mobile_key"+mobile+type+appType;
        cached.tag(cacheTagName).clear();
    }
}
