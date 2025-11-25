package com.niu.core.service.admin.sys.impl;

import cn.hutool.json.JSONUtil;
import com.niu.core.common.component.context.cache.Cached;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.exception.CommonException;
import com.niu.core.common.utils.QrcodeUtils;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.service.admin.stat.vo.StatSystemVo;
import com.niu.core.service.admin.stat.vo.StatVersionVo;
import com.niu.core.service.admin.sys.ISystemService;
import com.niu.core.service.admin.sys.vo.SysQrcodeVo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemServiceImpl implements ISystemService {


    @Resource
    private Cached cached;
    /**
     * 获取版权信息
     *
     * @return
     */
    @Override
    public StatSystemVo getSystemInfo() {
        StatSystemVo statSystemVo=new StatSystemVo();
        statSystemVo.setOs(System.getProperty("os.name", "Linux"));
        statSystemVo.setEnvironment(System.getProperty("catalina.home"));
        statSystemVo.setPhpV(System.getProperty("java.version"));

        StatVersionVo statVersionVo=new StatVersionVo();
        statVersionVo.setVersion("202406150001");
        statVersionVo.setCode("0.4.0");

        statSystemVo.setVersion(statVersionVo);
        return statSystemVo;
    }

    @Override
    public void clearCache() {
        cached.getAllKeys().stream().forEach(key -> cached.remove(key));;
    }

    @Override
    public SysQrcodeVo getQrcode(String folder, String page, List<Map<String,Object>> params) {
        SysQrcodeVo result = new SysQrcodeVo();
        result.setH5Path("");
        result.setWeappPath("");
        Integer siteId = RequestUtils.siteId();
        List<Map<String,Object>> paramList = new ArrayList<>();
        if(params != null){
            params.forEach(param -> {
                Map<String,Object> paramMap = new HashMap<>();
                paramMap.put("key",param.get("column_name").toString());
                paramMap.put("value",param.get("column_value").toString());
                paramList.add(paramMap);
            });
        }
        if(!paramList.isEmpty()){
            String dir = "upload/"+siteId+"/"+ folder+"_qrcode";
            try {
                String path = QrcodeUtils.qrcodeToFile(RequestUtils.siteId(),"weapp", "", page, paramList.get(0), dir);
                result.setWeappPath(path);
            } catch (Exception e) {
                log.error("获取推广微信小程序二维码error: {}, 错误文件: {}, 错误行号: {} , params: {}" , e.getMessage() ,e.getStackTrace()[0].getFileName() ,e.getStackTrace()[0].getLineNumber() , JSONUtil.parse(paramList.get(0)).toString());
                throw new AdminException("小程序未配置完善");
            }
        }
        return result;
    }
}
