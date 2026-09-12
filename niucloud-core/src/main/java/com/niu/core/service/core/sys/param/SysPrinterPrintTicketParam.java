package com.niu.core.service.core.sys.param;

import lombok.Data;

@Data
public class SysPrinterPrintTicketParam {
    private Integer siteId;
    /** Optional printer selected for a manual print. Null means all matching printers. */
    private Integer printerId;
    private String type;
    private String trigger;
    private Business business;

    @Data
    public static class Business {
        private Integer orderId;
    }
}
