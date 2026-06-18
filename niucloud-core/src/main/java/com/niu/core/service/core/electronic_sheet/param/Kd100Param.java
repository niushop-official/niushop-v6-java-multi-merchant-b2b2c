package com.niu.core.service.core.electronic_sheet.param;

import lombok.Data;

@Data
public class Kd100Param {
    private EsTemplateInfo esTemplateInfo;
    private PersonnelInfo receiver;
    private PersonnelInfo sender;
    private String quantity;
    private String weight;

    @Data
    public static class EsTemplateInfo {
        private KD100Config kd100Config;
        private String expType;
        private String tempId;
        private String childTempId;
        private String backTempId;
        private Company company;
    }

    @Data
    public static class KD100Config {
        private String monthCode;
        private String customerPwd;
        private String customerName;
        private String sendSite;
        private String sendStaff;
        private String payType;
    }

    @Data
    public static class PersonnelInfo {
        private String name;
        private String mobile;
        private String provinceName;
        private String cityName;
        private String expAreaName;
        private String address;

        public String getFullAddr() {
            return (this.provinceName != null ? this.provinceName : "") +
                    (this.cityName != null ? this.cityName : "") +
                    (this.expAreaName != null ? this.expAreaName : "") +
                    (this.address != null ? this.address : "");
        }
    }

    @Data
    public static class Company {
        private Object kd100ExpressNoElectronicSheet;
    }
}
