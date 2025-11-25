package com.niu.core.service.core.separ_account;

import com.niu.core.entity.separ_account.SeparAccount;
import com.niu.core.entity.separ_account.SeparAccountDetail;
import com.niu.core.service.core.separ_account.param.SeparNotifyParam;

import java.util.List;

public interface ICoreSeparAccountService {

    /**
     * 添加分账记录
     * @param account
     * @param detail
     */
    void add(SeparAccount account, List<SeparAccountDetail> detail);

    /**
     * 分账通知处理
     */
    void notify(SeparNotifyParam param);

    /**
     * 去分账
     * @param account
     */
    void toSpear(SeparAccount account);
}
