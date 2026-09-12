package com.niu.core.service.admin.site.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.niu.core.common.exception.AdminException;
import com.niu.core.common.utils.RequestUtils;
import com.niu.core.entity.site.SiteSettAccount;
import com.niu.core.enums.site.SiteSettAccountTypeEnum;
import com.niu.core.mapper.site.SiteSettAccountMapper;
import com.niu.core.service.admin.site.ISiteSettAccountService;
import com.niu.core.service.admin.site.param.SiteSettAccountParam;
import com.niu.core.service.admin.site.param.SiteSettAccountSearchParam;
import com.niu.core.service.admin.site.vo.SiteSettAccountVo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站点结算账户服务实现
 */
@Service
public class SiteSettAccountServiceImpl implements ISiteSettAccountService {

    @Resource
    private SiteSettAccountMapper siteSettAccountMapper;

    /**
     * 获取结算账户列表
     * 
     * @param searchParam 搜索参数对象，包含账户类型等筛选条件
     * @return 结算账户视图对象列表，包含账户基本信息及类型名称
     */
    @Override
    public List<SiteSettAccountVo> getList(SiteSettAccountSearchParam searchParam) {
        // 构建查询条件
        QueryWrapper<SiteSettAccount> queryWrapper = new QueryWrapper<>();
        // 按站点ID筛选
        queryWrapper.eq("main_id", RequestUtils.siteId());
        // 限定为站点类型
        queryWrapper.eq("main_type", "site");

        // 如果传入了账户类型，则按类型筛选
        if (ObjectUtil.isNotNull(searchParam.getType()) && ObjectUtil.isNotEmpty(searchParam.getType())) {
            queryWrapper.eq("type", searchParam.getType());
        }

        // 按创建时间降序排列
        queryWrapper.orderByDesc("create_time");

        // 执行查询获取结算账户列表
        List<SiteSettAccount> list = siteSettAccountMapper.selectList(queryWrapper);
        ArrayList<SiteSettAccountVo> voList = new ArrayList<>();

        // 将实体列表转换为视图对象列表
        for (SiteSettAccount account : list) {
            SiteSettAccountVo vo = new SiteSettAccountVo();
            // 复制属性
            BeanUtil.copyProperties(account, vo);
            // 设置类型名称（通过枚举转换）
            vo.setTypeName(SiteSettAccountTypeEnum.getNameByType(account.getType()));
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 获取结算账户详情
     * 
     * @param id 结算账户ID
     * @return 结算账户视图对象，包含账户详细信息及类型名称；如果账户不存在则返回null
     */
    @Override
    public SiteSettAccountVo getInfo(Integer id) {
        // 根据ID查询结算账户
        SiteSettAccount account = siteSettAccountMapper.selectById(id);
        // 如果账户不存在，返回null
        if (account == null) {
            return null;
        }
        // 将实体转换为视图对象
        SiteSettAccountVo vo = new SiteSettAccountVo();
        BeanUtil.copyProperties(account, vo);
        // 设置类型名称（通过枚举转换）
        vo.setTypeName(SiteSettAccountTypeEnum.getNameByType(account.getType()));
        return vo;
    }

    /**
     * 根据ID获取结算账户实体
     * 
     * @param id 结算账户ID
     * @return 结算账户实体，如果不存在则返回null
     */
    @Override
    public SiteSettAccount getSiteSettAccountById(Integer id) {
        return siteSettAccountMapper.selectById(id);
    }

    /**
     * 根据结算账户获取转账数据格式
     * 
     * @param siteSettAccount 结算账户实体
     * @return 转账数据Map，包含转账银行、类型、真实姓名、账号、收款码等信息
     */
    @Override
    public Map<String, Object> getTransferDataBySettAccount(SiteSettAccount siteSettAccount) {
        Map<String, Object> data = new HashMap<>();
        // 设置转账银行（仅银行卡类型有值）
        data.put("transfer_bank", siteSettAccount.getType() == SiteSettAccountTypeEnum.BANK.getType() 
            ? siteSettAccount.getBankName() : "");
        // 设置转账类型
        data.put("transfer_type", siteSettAccount.getType());
        
        // 根据账户类型设置不同的转账信息
        if (siteSettAccount.getType() == SiteSettAccountTypeEnum.BANK.getType()) {
            data.put("transfer_realname", siteSettAccount.getName());
            data.put("transfer_account", siteSettAccount.getAccountNo());
        } else if (siteSettAccount.getType() == SiteSettAccountTypeEnum.ALIPAY.getType()) {
            data.put("transfer_realname", siteSettAccount.getName());
            data.put("transfer_account", siteSettAccount.getAccountNo());
            data.put("transfer_payment_code", siteSettAccount.getAccountImg()); // 支付宝收款码
        } else if (siteSettAccount.getType() == SiteSettAccountTypeEnum.WECHAT.getType()) {
            data.put("transfer_realname", siteSettAccount.getName());
            data.put("transfer_account", siteSettAccount.getAccountNo());
            data.put("transfer_payment_code", siteSettAccount.getAccountImg()); // 微信收款码
        }
        
        return data;
    }

    /**
     * 添加结算账户
     * 
     * @param param 结算账户参数对象，包含账户类型、名称、账号等信息
     * @return 新增结算账户的ID
     * @throws IllegalArgumentException 当微信零钱结算账户已存在时抛出
     */
    @Transactional
    @Override
    public Integer add(SiteSettAccountParam param) {
        // 微信零钱账户唯一性校验：同一站点只能存在一个微信零钱结算账户
        if (param.getType() == SiteSettAccountTypeEnum.WECHAT_CHANGE.getType()) {
            QueryWrapper<SiteSettAccount> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("main_id", RequestUtils.siteId());
            queryWrapper.eq("main_type", "site");
            queryWrapper.eq("type", SiteSettAccountTypeEnum.WECHAT_CHANGE.getType());

            SiteSettAccount existAccount = siteSettAccountMapper.selectOne(queryWrapper);
            if (existAccount != null) {
                throw new AdminException("微信零钱结算账户已存在");
            }
        }
        // 构建结算账户实体对象
        SiteSettAccount account = new SiteSettAccount();
        account.setMainId(RequestUtils.siteId());           // 设置所属站点ID
        account.setMainType("site");                        // 设置主体类型为站点
        account.setType(param.getType());                    // 设置账户类型
        account.setName(param.getName() != null ? param.getName() : "");           // 设置账户名称
        account.setAccountNo(param.getAccountNo() != null ? param.getAccountNo() : ""); // 设置账号
        account.setAccountImg(param.getAccountImg() != null ? param.getAccountImg() : ""); // 设置账户图片
        account.setBankName(param.getBankName() != null ? param.getBankName() : "");     // 设置银行名称
        account.setBankAddress(param.getBankAddress() != null ? param.getBankAddress() : ""); // 设置开户行地址
        account.setBindMemberId(param.getBindMemberId() != null ? param.getBindMemberId() : 0); // 设置绑定会员ID
        account.setCreateTime(System.currentTimeMillis() / 1000);  // 设置创建时间（秒级时间戳）

        // 插入数据库
        siteSettAccountMapper.insert(account);

        // 返回新增账户的ID
        return account.getId();
    }

    /**
     * 编辑结算账户
     * 
     * @param id 结算账户ID
     * @param param 结算账户参数对象，包含账户类型、名称、账号等信息
     * @return 更新后的结算账户ID
     * @throws AdminException 当结算账户不存在或微信零钱账户已存在时抛出
     */
    @Transactional
    @Override
    public Integer edit(Integer id, SiteSettAccountParam param) {
        // 根据ID查询结算账户
        SiteSettAccount account = siteSettAccountMapper.selectById(id);
        // 如果账户不存在，抛出异常
        if (account == null) {
            throw new AdminException("结算账户不存在");
        }

        // 微信零钱账户唯一性校验：排除当前账户，检查是否存在其他微信零钱账户
        if (param.getType() == SiteSettAccountTypeEnum.WECHAT_CHANGE.getType()) {
            QueryWrapper<SiteSettAccount> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type", SiteSettAccountTypeEnum.WECHAT_CHANGE.getType());
            queryWrapper.ne("id", id); // 排除当前账户

            SiteSettAccount existAccount = siteSettAccountMapper.selectOne(queryWrapper);
            if (existAccount != null) {
                throw new AdminException("微信零钱结算账户已存在");
            }
        }

        // 更新账户信息
        account.setType(param.getType());                    // 更新账户类型
        account.setName(param.getName() != null ? param.getName() : "");           // 更新账户名称
        account.setAccountNo(param.getAccountNo() != null ? param.getAccountNo() : ""); // 更新账号
        account.setAccountImg(param.getAccountImg() != null ? param.getAccountImg() : ""); // 更新账户图片
        account.setBankName(param.getBankName() != null ? param.getBankName() : "");     // 更新银行名称
        account.setBankAddress(param.getBankAddress() != null ? param.getBankAddress() : ""); // 更新开户行地址
        account.setBindMemberId(param.getBindMemberId() != null ? param.getBindMemberId() : 0); // 更新绑定会员ID
        account.setUpdateTime(System.currentTimeMillis() / 1000);  // 设置更新时间（秒级时间戳）

        // 更新数据库，返回受影响行数
        return siteSettAccountMapper.updateById(account);
    }

    /**
     * 删除结算账户
     * 
     * @param id 结算账户ID
     * @return 被删除的结算账户ID
     * @throws AdminException 当结算账户不存在时抛出
     */
    @Transactional
    @Override
    public Integer del(Integer id) {
        SiteSettAccount account = siteSettAccountMapper.selectById(id);
        if (account == null) {
            throw new AdminException("结算账户不存在");
        }
        // 执行删除操作
        siteSettAccountMapper.deleteById(id);

        // 返回被删除的账户ID
        return id;
    }
}