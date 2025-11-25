package com.niu.core.event.separ;

import com.niu.core.common.component.context.event.Event;
import com.niu.core.entity.separ_account.SeparAccount;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分账
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AfterSeparEvent extends Event {

    private SeparAccount account;
}
