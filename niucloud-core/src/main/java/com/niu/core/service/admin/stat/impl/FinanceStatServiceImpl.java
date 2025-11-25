package com.niu.core.service.admin.stat.impl;

import com.niu.core.service.admin.shop.ISiteShopService;
import com.niu.core.service.admin.stat.IFinanceStatService;
import com.niu.core.service.admin.site.ISiteAccountLogService;
import com.niu.core.service.admin.stat.IStatService;
import com.niu.core.service.core.member.ICoreMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class FinanceStatServiceImpl implements IFinanceStatService {

    @Resource
    private ISiteAccountLogService siteAccountLogService;

    @Resource
    private ISiteShopService shopService;

    @Resource
    private ICoreMemberService coreMemberService;

    @Resource
    private IStatService coreStatService;

    /**
     * 获取收款统计数据
     * @return 统计数据
     */
    @Override
    public Map<String, BigDecimal> getPayStat() {
        Map<String, Object> accountStat = siteAccountLogService.stat();
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("pay", new BigDecimal(accountStat.getOrDefault("pay", 0).toString()));
        data.put("refund", new BigDecimal(accountStat.getOrDefault("refund", 0).toString()));
        data.put("transfer", new BigDecimal(accountStat.getOrDefault("transfer", 0).toString()));
        return data;
    }

    /**
     * 获取结算统计数据
     * @return 统计数据
     */
    @Override
    public Map<String, BigDecimal> getSettlementStat() {
        Map<String, BigDecimal> shopStat = shopService.getShopStatMoney();
        Map<String, BigDecimal> adminStat = shopService.getAdminStatMoney();
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("admin_settlement", adminStat.get("admin_settlement"));
        data.put("shop_settlement", shopStat.get("shop_settlement"));
        data.put("shop_cash_out", shopStat.get("shop_cash_out"));
        data.put("shop_balance", shopStat.get("shop_balance"));
        return data;
    }

    /**
     * 获取会员统计数据
     * @return 统计数据
     */
    @Override
    public Map<String, BigDecimal> getMemberStat() {
/*        Map<String, BigDecimal> memberStat = coreMemberService.getMemberStatMoney();
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("member_balance", memberStat.get("member_balance"));
        data.put("member_balance_get", memberStat.get("member_balance_get"));
        data.put("member_balance_use", memberStat.get("member_balance_use"));
        return data;*/
        return null;
    }

    /**
     * 账单总数统计
     * @param where 查询条件
     * @return 统计数据
     */
    @Override
    public Map<String, BigDecimal> getAccountsTotal(Map<String, Object> where) {
        String start_date = (String) (where.get("date") != null ? ((Object[]) where.get("date"))[0] : "");
        String end_date = (String) (where.get("date") != null ? ((Object[]) where.get("date"))[1] : "");
/*        Map<String, BigDecimal> statList = coreStatService.(site_id, start_date, end_date);
        Shop shop_info = new Shop(); // 这里可能需要根据实际情况查询数据库
        statList.put("money", BigDecimal.ZERO);
        statList.put("money_get", BigDecimal.ZERO);
        if (shop_info != null) {
            statList.put("money", shop_info.getMoney());
            statList.put("money_get", shop_info.getMoneyGet());
        }
        return stat_list;*/
        return null;
    }

    /**
     * 账单列表统计
     * @param where 查询条件
     * @return 统计数据
     */
    @Override
    public Map<String, BigDecimal> getAccountsList(Map<String, Object> where) {
        String start_date = (String) (where.get("date") != null ? ((Object[]) where.get("date"))[0] : "");
        String end_date = (String) (where.get("date") != null ? ((Object[]) where.get("date"))[1] : "");
        String type = (String) where.get("type");
        return null;
        //return coreStatService.getStatList(site_id, start_date, end_date, type);
    }
}